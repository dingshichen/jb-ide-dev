package cn.uniondrug.dev.util

import cn.uniondrug.dev.ApiParam
import com.goide.psi.GoFieldDeclaration
import com.goide.psi.GoFile
import com.goide.psi.GoStructType
import com.goide.psi.impl.GoTypeUtil
import com.goide.stubs.index.GoTypesIndex
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment

/**
 * @author dingshichen
 * @date 2022/4/5
 */
object CommonPsiUtil {

    fun getResultData() = arrayListOf(
        ApiParam(name = "errno", type = "int", required = true, description = "状态码：0-成功；其他-失败"),
        ApiParam(name = "error", type = "string", required = true, description = "状态描述"),
        ApiParam(name = "dataType", type = "string", required = true, description = "数据类型：OBJECT/LIST/ERROR"),
        ApiParam(name = "data", type = "object", required = true, description = "数据"),
    )

    fun getResultList() = arrayListOf(
        ApiParam(name = "errno", type = "int", required = true, description = "状态码：0-成功；其他-失败"),
        ApiParam(name = "error", type = "string", required = true, description = "状态描述"),
        ApiParam(name = "dataType", type = "string", required = true, description = "数据类型：OBJECT/LIST/ERROR"),
        ApiParam(name = "data", type = "arrary[]", required = true, description = "数据"),
    )

    fun getResultPagingBody() = arrayListOf(
        ApiParam(name = "errno", type = "int", required = true, description = "状态码：0-成功；其他-失败"),
        ApiParam(name = "error", type = "string", required = true, description = "状态描述"),
        ApiParam(name = "dataType", type = "string", required = true, description = "数据类型：OBJECT/LIST/ERROR"),
        ApiParam(name = "data", type = "pagingBody", required = true, description = "数据", children = arrayListOf(
            ApiParam(name = "body", type = "arrary[]", required = true, description = "列表信息", parentId = "data"),
            ApiParam(name = "paging", type = "paging", required = true, description = "分页信息", parentId = "data",
                children = arrayListOf(
                    ApiParam(name = "first", type = "int", required = true, description = "第一页", parentId = "paging"),
                    ApiParam(name = "before", type = "int", required = true, description = "前一页", parentId = "paging"),
                    ApiParam(name = "current", type = "int", required = true, description = "当前页", parentId = "paging"),
                    ApiParam(name = "last", type = "int", required = true, description = "最后一页", parentId = "paging"),
                    ApiParam(name = "next", type = "int", required = true, description = "下一页", parentId = "paging"),
                    ApiParam(name = "limit", type = "int", required = true, description = "每页条数", parentId = "paging"),
                    ApiParam(name = "totalPages", type = "int", required = true, description = "总页数", parentId = "paging"),
                    ApiParam(name = "totalItems", type = "int", required = true, description = "总数据量", parentId = "paging"),
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
    private fun buildDocParam(parent: String?, field: GoFieldDeclaration, params: ArrayList<ApiParam>) {
        field.type?.let {
            field.tag?.let { tag ->
                val param = ApiParam(
                    name = field.text.substring(0, field.text.indexOf(" ")),
                    type = it.text,
                    required = tag.getValue("validate")?.let { validate -> "required" in validate } ?: false,
//                    maxLength =     TODO
//                    example =
                    parentId = parent,
                    description = tag.getValue("label")
                )
                if (GolangPsiUtil.isNotBaseType(it)) {
                    val children = ArrayList<ApiParam>()
                    val typeSpec = GoTypeUtil.findTypeSpec(it, it.context)
                    when (val struct = typeSpec.specType.type) {
                        is GoStructType -> {
                            struct.fieldDeclarationList.forEach { buildDocParam(param.name, it, children) }
                            param.children = children
                        }
                    }
                }
                params += param
            }
        }
    }
}