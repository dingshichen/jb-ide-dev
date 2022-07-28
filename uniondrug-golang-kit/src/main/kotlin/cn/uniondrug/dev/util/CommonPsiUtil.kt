package cn.uniondrug.dev.util

import cn.uniondrug.dev.*
import cn.uniondrug.dev.dto.*
import cn.uniondrug.dev.mock.generateBaseTypeMockData
import com.goide.psi.*
import com.goide.psi.impl.GoArrayOrSliceTypeImpl
import com.goide.psi.impl.GoTypeUtil
import com.goide.stubs.index.GoTypesIndex
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

/**
 * @author dingshichen
 * @date 2022/4/5
 */
object CommonPsiUtil {

    fun getResultData() = arrayListOf(
        ApiParam(name = "errno", type = CommonType.INT, required = true, description = "状态码：0-成功；其他-失败", example = 0),
        ApiParam(name = "error", type = CommonType.STRING, required = true, description = "状态描述", example = "success"),
        ApiParam(name = "dataType", type = CommonType.STRING, required = true, description = "数据类型：OBJECT/LIST/ERROR", example = "OBJECT"),
        ApiParam(name = "data", type = CommonType.OBJECT, required = true, description = "数据", example = ""),
    )

    fun getResultList() = arrayListOf(
        ApiParam(name = "errno", type = CommonType.INT, required = true, description = "状态码：0-成功；其他-失败", example = 0),
        ApiParam(name = "error", type = CommonType.STRING, required = true, description = "状态描述", example = "success"),
        ApiParam(name = "dataType", type = CommonType.STRING, required = true, description = "数据类型：OBJECT/LIST/ERROR", example = "OBJECT"),
        ApiParam(name = "data", type = CommonType.ARRAY_OBJECT, required = true, description = "数据", example = ""),
    )

    fun getResultPagingBody() = arrayListOf(
        ApiParam(name = "errno", type = CommonType.INT, required = true, description = "状态码：0-成功；其他-失败", example = 0),
        ApiParam(name = "error", type = CommonType.STRING, required = true, description = "状态描述", example = "success"),
        ApiParam(name = "dataType", type = CommonType.STRING, required = true, description = "数据类型：OBJECT/LIST/ERROR", example = "OBJECT"),
        ApiParam(name = "data", type = CommonType.OBJECT, required = true, description = "数据", children = arrayListOf(
            ApiParam(name = "body", type = CommonType.ARRAY_OBJECT, required = true, description = "列表信息", parentId = "data", example = ""),
            ApiParam(name = "paging", type = CommonType.OBJECT, required = true, description = "分页信息", parentId = "data",
                children = arrayListOf(
                    ApiParam(name = "first", type = CommonType.INT, required = true, description = "第一页", parentId = "paging", example = 1),
                    ApiParam(name = "before", type = CommonType.INT, required = true, description = "前一页", parentId = "paging", example = 1),
                    ApiParam(name = "current", type = CommonType.INT, required = true, description = "当前页", parentId = "paging", example = 1),
                    ApiParam(name = "last", type = CommonType.INT, required = true, description = "最后一页", parentId = "paging", example = 9),
                    ApiParam(name = "next", type = CommonType.INT, required = true, description = "下一页", parentId = "paging", example = 2),
                    ApiParam(name = "limit", type = CommonType.INT, required = true, description = "每页条数", parentId = "paging", example = 10),
                    ApiParam(name = "totalPages", type = CommonType.INT, required = true, description = "总页数", parentId = "paging", example = 10),
                    ApiParam(name = "totalItems", type = CommonType.INT, required = true, description = "总数据量", parentId = "paging", example = 99),
            ), example = ""),
        ), example = ""),
    )

    /**
     * 获取 RequestBoy
     */
    fun getRequestBody(project: Project, psiComment: DocRequestComment): ArrayList<ApiParam> {
        return getBody(project, psiComment, getPackageName(psiComment))
    }

    /**
     * 获取 ResponseBody
     */
    fun getResponseBody(project: Project, psiComment: DocResponseComment?): ArrayList<ApiParam> {
        if (psiComment == null) {
            return getResultData()
        }
        return when (psiComment) {
            is DocResponseListComment -> {
                val result = getResultList()
                val body = getBody(project, psiComment, getPackageName(psiComment), result[3])
                result[3].children = body
                result
            }
            is DocResponsePagingComment -> {
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

    /**
     * 获取错误码
     */
    fun getErrnos(errorComment: List<DocErrorComment>): List<ApiErrno> = errorComment.map {
        it.getValues().run {
            if (size < 2 || size > 3) throw DocBuildFailException("解析错误码错误，请核对错误码是否定义如下：// @Error(1001, 系统错误, 需发起重试)")
            ApiErrno(this[0], this[1], if (this.size >= 3) this[2] else "")
        }
    }

    private fun getBody(
        project: Project,
        psiComment: DocRequestComment,
        packageName: String,
        parent: ApiParam? = null,
    ): ArrayList<ApiParam> {
        val structName = getStructName(psiComment)
        val goTypeSpecs = GoTypesIndex.find(structName, project, null, null)
        if (goTypeSpecs.isEmpty()) throw DocBuildFailException("查找不到 $packageName.$structName 结构体，请确认结构体名称是否正确")
        val params = ArrayList<ApiParam>()
        var find = false
        for (goTypeSpec in goTypeSpecs) {
            when (val struct = goTypeSpec.specType.type) {
                is GoStructType -> {
                    val file = struct.containingFile
                    if (file is GoFile && packageName == file.packageName) {
                        find = true
                        struct.fieldDeclarationList.forEach { buildDocParam(parent?.name, it, params) }
                        break
                    }
                }
            }
        }
        if (!find) throw DocBuildFailException("查找不到 $packageName.$structName 结构体，请确认包目录是否正确")
        return params
    }

    /**
     * 获取包名
     */
    private fun getPackageName(psiComment: DocRequestComment): String {
        return psiComment.getParam().run {
            substring(0, lastIndexOf("."))
        }
    }

    /**
     * 获取结构体名称
     */
    private fun getStructName(psiComment: DocRequestComment): String {
        return psiComment.getParam().run {
            substring(lastIndexOf(".") + 1)
        }
    }

    /**
     * 构建参数
     */
    private fun buildDocParam(parent: String? = null, field: GoFieldDeclaration, params: ArrayList<ApiParam>) {
        val commonTypeConvertor = field.project.getService(CommonTypeConvertor::class.java)
        val fieldType = field.type ?: return
        val tag = field.tag ?: return
        val name = GolangPsiUtil.getFieldJsonName(field) ?: throw DocBuildFailException("获取参数属性 json 名称失败")
        val type = GolangPsiUtil.getRealTypeOrSelf(fieldType).run {
            commonTypeConvertor.convert(this.contextlessUnderlyingType.presentationText)
        }
        val example = try {
            generateExample(tag, type, name)
        } catch (e: NumberFormatException) {
            throw DocBuildFailException("参数 $name 的 mock 示例值 ${GolangPsiUtil.getMockValue(tag)} 与自身类型不匹配")
        }
        val param = ApiParam(
            name = name,
            // 从背后真实的类型转换
            type = type,
            required = GolangPsiUtil.isRequired(tag),
            maxLength = GolangPsiUtil.getMaxLength(tag),
            example = example,
            parentId = parent,
            description = GolangPsiUtil.getFieldDescription(tag)
        )
        when (param.type) {
            CommonType.OBJECT -> {
                findChildrenFieldDeclaration(param, fieldType, fieldType.context)
            }
            CommonType.ARRAY_OBJECT -> {
                if (fieldType is GoArrayOrSliceTypeImpl) {
                    findChildrenFieldDeclaration(param, fieldType.type, fieldType.type.context)
                }
            }
            else -> {}
        }
        params += param
    }

    /**
     * 数组的示例值可以是用 "," 分割的数组形式，但不要有 [] 符号
     */
    private fun generateExample(tag: GoTag, type: CommonType, fieldName: String): Any {
        return GolangPsiUtil.getMockValue(tag).let {
            when (type) {
                CommonType.STRING -> it ?: generateBaseTypeMockData(type.value, fieldName)
                CommonType.BOOL -> true
                CommonType.BYTE -> it?.toInt() ?: generateBaseTypeMockData(type.value, fieldName)
                CommonType.INT -> it?.toInt() ?: generateBaseTypeMockData(type.value, fieldName)
                CommonType.LONG -> it?.toLong() ?: generateBaseTypeMockData(type.value, fieldName)
                CommonType.FLOAT -> it?.toFloat() ?: generateBaseTypeMockData(type.value, fieldName)
                CommonType.ARRAY -> it?.splitToTypeList(type) ?: emptyList<String>()
                CommonType.OBJECT -> it ?: ""
                CommonType.ARRAY_STRING -> it?.splitToTypeList(type) ?: listOf(generateBaseTypeMockData(CommonType.STRING.value, fieldName), generateBaseTypeMockData(CommonType.STRING.value, fieldName))
                CommonType.ARRAY_BOOL -> listOf(false, true)
                CommonType.ARRAY_BYTE -> it?.splitToTypeList(type) ?: listOf(generateBaseTypeMockData(CommonType.BYTE.value, fieldName),generateBaseTypeMockData(CommonType.BYTE.value, fieldName))
                CommonType.ARRAY_INT -> it?.splitToTypeList(type) ?: listOf(generateBaseTypeMockData(CommonType.INT.value, fieldName), generateBaseTypeMockData(CommonType.INT.value, fieldName))
                CommonType.ARRAY_LONG -> it?.splitToTypeList(type) ?: listOf(generateBaseTypeMockData(CommonType.LONG.value, fieldName), generateBaseTypeMockData(CommonType.LONG.value, fieldName))
                CommonType.ARRAY_FLOAT -> it?.splitToTypeList(type) ?: listOf(generateBaseTypeMockData(CommonType.FLOAT.value, fieldName), generateBaseTypeMockData(CommonType.FLOAT.value, fieldName))
                CommonType.ARRAY_OBJECT -> emptyList<String>()
            }
        }
    }

    private fun findChildrenFieldDeclaration(param: ApiParam, goType: GoType, context: PsiElement?) {
        val children = arrayListOf<ApiParam>()
        val struct: GoStructType? = if (goType is GoStructType) goType else {
            GoTypeUtil.findTypeSpec(GolangPsiUtil.getRealTypeOrSelf(goType), context)?.let {
                when (val value = it.specType.type) {
                    is GoStructType -> value
                    else -> null
                }
            }
        }
        struct?.let { type ->
            type.fieldDeclarationList.forEach { buildDocParam(param.name, it, children) }
            param.children = children
        }
    }
}