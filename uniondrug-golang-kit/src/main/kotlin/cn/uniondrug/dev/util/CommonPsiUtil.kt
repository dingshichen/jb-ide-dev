package cn.uniondrug.dev.util

import cn.uniondrug.dev.ApiParam
import cn.uniondrug.dev.CommonType
import cn.uniondrug.dev.CommonTypeConvertor
import cn.uniondrug.dev.DocBuildFailException
import com.goide.psi.*
import com.goide.psi.impl.GoArrayOrSliceTypeImpl
import com.goide.psi.impl.GoTypeUtil
import com.goide.stubs.index.GoTypesIndex
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement

/**
 * @author dingshichen
 * @date 2022/4/5
 */
object CommonPsiUtil {

    fun getResultData() = arrayListOf(
        ApiParam(name = "errno", type = CommonType.INT, required = true, description = "状态码：0-成功；其他-失败"),
        ApiParam(name = "error", type = CommonType.STRING, required = true, description = "状态描述"),
        ApiParam(name = "dataType", type = CommonType.STRING, required = true, description = "数据类型：OBJECT/LIST/ERROR"),
        ApiParam(name = "data", type = CommonType.OBJECT, required = true, description = "数据"),
    )

    fun getResultList() = arrayListOf(
        ApiParam(name = "errno", type = CommonType.INT, required = true, description = "状态码：0-成功；其他-失败"),
        ApiParam(name = "error", type = CommonType.STRING, required = true, description = "状态描述"),
        ApiParam(name = "dataType", type = CommonType.STRING, required = true, description = "数据类型：OBJECT/LIST/ERROR"),
        ApiParam(name = "data", type = CommonType.ARRAY_OBJECT, required = true, description = "数据"),
    )

    fun getResultPagingBody() = arrayListOf(
        ApiParam(name = "errno", type = CommonType.INT, required = true, description = "状态码：0-成功；其他-失败"),
        ApiParam(name = "error", type = CommonType.STRING, required = true, description = "状态描述"),
        ApiParam(name = "dataType", type = CommonType.STRING, required = true, description = "数据类型：OBJECT/LIST/ERROR"),
        ApiParam(name = "data", type = CommonType.OBJECT, required = true, description = "数据", children = arrayListOf(
            ApiParam(name = "body", type = CommonType.ARRAY_OBJECT, required = true, description = "列表信息", parentId = "data"),
            ApiParam(name = "paging", type = CommonType.OBJECT, required = true, description = "分页信息", parentId = "data",
                children = arrayListOf(
                    ApiParam(name = "first", type = CommonType.INT, required = true, description = "第一页", parentId = "paging"),
                    ApiParam(name = "before", type = CommonType.INT, required = true, description = "前一页", parentId = "paging"),
                    ApiParam(name = "current", type = CommonType.INT, required = true, description = "当前页", parentId = "paging"),
                    ApiParam(name = "last", type = CommonType.INT, required = true, description = "最后一页", parentId = "paging"),
                    ApiParam(name = "next", type = CommonType.INT, required = true, description = "下一页", parentId = "paging"),
                    ApiParam(name = "limit", type = CommonType.INT, required = true, description = "每页条数", parentId = "paging"),
                    ApiParam(name = "totalPages", type = CommonType.INT, required = true, description = "总页数", parentId = "paging"),
                    ApiParam(name = "totalItems", type = CommonType.INT, required = true, description = "总数据量", parentId = "paging"),
            )),
        )),
    )

    /**
     * 获取 RequestBoy
     */
    fun getRequestBody(project: Project, psiComment: PsiComment): ArrayList<ApiParam> {
        return getBody(project, psiComment, getPackageName(psiComment))
    }

    /**
     * 获取 ResponseBody
     */
    fun getResponseBody(project: Project, psiComment: PsiComment?): ArrayList<ApiParam> {
        if (psiComment == null) {
            return getResultData()
        }
        return when (psiComment.text.getCommentKey()) {
            "ResponseList" -> {
                val result = getResultList()
                val body = getBody(project, psiComment, getPackageName(psiComment), result[3])
                result[3].children = body
                result
            }
            "ResponsePaging" -> {
                val result = getResultPagingBody()
                val body = getBody(project, psiComment, getPackageName(psiComment), result[3].children!![0])
                result[3].children!![0].children = body
                result
            }
            else -> {
                val result = getResultData()
                val body = getBody(project, psiComment, getPackageName(psiComment), result[3])
                result[3].children = body
                result
            }
        }
    }

    /**
     * 获取消息体参数
     */
    fun getMessageBody(goTypeSpec: GoTypeSpec): List<ApiParam> {
        val params = ArrayList<ApiParam>()
        when (val struct = goTypeSpec.specType.type) {
            is GoStructType -> {
                struct.fieldDeclarationList.forEach { buildDocParam(field = it, params = params) }
            }
            else -> throw DocBuildFailException("解析消息体异常，如需帮助请联系开发者")
        }
        return params
    }

    private fun getBody(
        project: Project,
        psiComment: PsiComment,
        packageName: String,
        parent: ApiParam? = null,
    ): ArrayList<ApiParam> {
        val structName = getStructName(psiComment)
        val goTypeSpecs = GoTypesIndex.find(structName, project, null, null)
        val params = ArrayList<ApiParam>()
        for (goTypeSpec in goTypeSpecs) {
            when (val struct = goTypeSpec.specType.type) {
                is GoStructType -> {
                    val file = struct.containingFile
                    if (file is GoFile) {
                        if (packageName != file.packageName) {
                            continue
                        }
                    }
                    struct.fieldDeclarationList.forEach { buildDocParam(parent?.name, it, params) }
                }
            }
        }
        return params
    }

    /**
     * 获取包名
     */
    private fun getPackageName(psiComment: PsiComment): String {
        val allName = psiComment.text.split(" ")[2]
        return allName.substring(0, allName.lastIndexOf("."))
    }

    /**
     * 获取结构体名称
     */
    private fun getStructName(psiComment: PsiComment) =
        psiComment.text.substring(psiComment.text.lastIndexOf(".") + 1)

    /**
     * 构建参数
     */
    private fun buildDocParam(parent: String? = null, field: GoFieldDeclaration, params: ArrayList<ApiParam>) {
        val commonTypeConvertor = field.project.getService(CommonTypeConvertor::class.java)
        field.type?.let {
            field.tag?.let { tag ->
                val param = ApiParam(
                    name = GolangPsiUtil.getFieldJsonName(field) ?: throw DocBuildFailException("获取参数属性 json 名称失败"),
                    // 从背后真实的类型转换
                    type = commonTypeConvertor.convert(GolangPsiUtil.getRealTypeOrSelf(it).contextlessUnderlyingType.presentationText),
                    required = GolangPsiUtil.isRequired(tag),
                    maxLength = GolangPsiUtil.getMaxLength(tag),
                    parentId = parent,
                    description = GolangPsiUtil.getFieldDescription(field, tag)
                )
                when (param.type) {
                    CommonType.OBJECT -> {
                        findChildrenFieldDeclaration(param, it, it.context)
                    }
                    CommonType.ARRAY_OBJECT -> {
                        if (it is GoArrayOrSliceTypeImpl) {
                            findChildrenFieldDeclaration(param, it.type, it.type.context)
                        }
                    }
                    else -> {}
                }
                params += param
            }
        }
    }

    private fun findChildrenFieldDeclaration(param: ApiParam, goType: GoType, context: PsiElement?) {
        val children = arrayListOf<ApiParam>()
        val typeSpec = GoTypeUtil.findTypeSpec(GolangPsiUtil.getRealTypeOrSelf(goType), context)
        when (val struct = typeSpec.specType.type) {
            is GoStructType -> {
                struct.fieldDeclarationList.forEach { buildDocParam(param.name, it, children) }
                param.children = children
            }
        }
    }
}