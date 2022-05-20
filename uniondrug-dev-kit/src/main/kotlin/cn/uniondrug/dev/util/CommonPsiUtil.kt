/** @author dingshichen */
package cn.uniondrug.dev.util

import cn.uniondrug.dev.*
import com.intellij.codeInsight.AnnotationUtil
import com.intellij.psi.*
import com.intellij.psi.javadoc.PsiDocComment
import com.intellij.psi.util.PsiUtil

/**
 * 是否是基本类型
 */
fun isBaseType(type: PsiType) = type.canonicalText in BASE

fun isBaseCollection(type: PsiType): Boolean {
    if (type is PsiClassType) {
        if (type.canonicalText in FULL_BASE_LIST) {
            return true
        }
    }
    return false
}

/**
 * 元素是否是注释内容
 */
fun PsiElement.isCommentData() = toString() == "PsiDocToken:DOC_COMMENT_DATA"

/**
 * 获取元素注释内容
 */
fun PsiElement.commentText() = text.replace("*", "").trim()

/**
 * 获取 API 名称
 */
fun getApiName(psiMethod: PsiMethod) = psiMethod.docComment?.let { docComment ->
    docComment.children.find { it.isCommentData() }?.commentText()
} ?: throw ApiBuildException("获取 API 名称失败，请检查方法注释是否存在")

/**
 * 获取 API 描述
 */
fun getApiDescription(psiMethod: PsiMethod) = psiMethod.docComment?.let {
    val comments =  it.children.filter { e -> e.isCommentData() }
    if (comments.size > 1) {
        return comments[1].commentText()
    } else {
        return ""
    }
} ?: ""

/**
 * 获取 API 作者
 */
fun getApiAuthor(psiClass: PsiClass, psiMethod: PsiMethod): String {
    fun getClassAuthor() = psiClass.docComment?.let {
        getApiAuthor(it)
    } ?: ""

    val methodComment = psiMethod.docComment
    return if (methodComment == null) {
        getClassAuthor()
    } else {
        getApiAuthor(methodComment) ?: getClassAuthor()
    }
}

/**
 * 获取注释里的作者
 */
fun getApiAuthor(docComment: PsiDocComment) = docComment.findTagByName("author")?.valueElement?.commentText()

/**
 * 获取字段描述
 */
fun getFieldDescription(psiField: PsiField): String? {
    return psiField.docComment?.children?.filter { it.isCommentData() }?.joinToString("<br>") { it.commentText() }
}

/**
 * 是否弃用
 */
fun isDeprecated(psiClass: PsiClass, psiMethod: PsiMethod) =
    AnnotationUtil.isAnnotated(psiClass, DEPRECATED, 0)
            || AnnotationUtil.isAnnotated(psiMethod, DEPRECATED, 0)

/**
 * 从参数中获取类型
 */
fun getRequestBody(parameter: PsiParameter) = getBody(null, parameter.type as PsiClassType)

/**
 * 从返回值解析获取返回结构体
 */
fun getResponseBody(returnTypeElement: PsiTypeElement) = getBody(null, returnTypeElement.type as PsiClassType)

/**
 * 解析 body 参数
 * @param parentField 父节点属性名
 * @param psiType 当前属性类型
 * @param childrenFields 指定子节点名
 */
private fun getBody(
    parentField: PsiField?,
    psiType: PsiClassType,
    childrenFields: Array<PsiField>? = null
): ArrayList<ApiParam> {
    val params = ArrayList<ApiParam>()
    // 递归获取父类字段
    psiType.superTypes.forEach {
        if (it.canonicalText == "java.lang.Object"
            || it.canonicalText == "java.io.Serializable"
            || it.canonicalText.startsWith("java.util.Collection")
            || it.canonicalText.startsWith("java.lang.Iterable")
        ) {
            return@forEach
        }
        params += getBody(parentField, it as PsiClassType)
    }
    val psiClass = PsiUtil.resolveClassInClassTypeOnly(psiType) ?: throw RuntimeException("获取请求体参数失败")
    // 泛型对应真实类型关系 K: 泛型 V: 真实类型的 PsiType
    val generics = getGenericsType(psiClass, psiType)
    // 没有给出属性字段，则解析类型里的属性
    val field = childrenFields ?: psiClass.fields
    field?.forEach {
        if (it.name == "serialVersionUID") {
            // 序列化 ID 字段，跳过
            return@forEach
        }
        var fieldType = it.type
        // 如果能获取到对应的真实类型，说明是范型
        val paramType = generics[fieldType.presentableText]
        paramType?.let {
            if ("?" == paramType.presentableText) {
                // 如果是通配符，说明没有此范型所在字段是没有定义的，跳过
                return@forEach
            }
            // 替换成真实类型
            fieldType = paramType
        }
        val param = ApiParam(
            name = it.name,
            type = fieldType.presentableText,
            required = if (AnnotationUtil.isAnnotated(it, REQUIRED, 0)) 1 else 0,
            maxLength = getMaxLength(it),
            description = getFieldDescription(it) ?: getUniondrugFieldDescription(it, psiType),
            parentId = parentField?.name ?: "",
            children = getChildren(it, fieldType, generics),
        )
        params += param
    }
    return params
}

/**
 * 如果类型是 List<T> Set<T>
 */
private fun tryGetCollectionGenericsType(psiType: PsiType): Array<PsiField>? {
    if (psiType.presentableText.startsWith("List<")
        || psiType.presentableText.startsWith("Set<")
    ) {
        if (psiType is PsiClassType) {
            psiType.parameters.first {
                return PsiUtil.resolveClassInClassTypeOnly(it)?.fields
            }
        }
    }
    return null
}

/**
 * 获取药联字段
 */
private fun getUniondrugFieldDescription(psiField: PsiField, psiType: PsiClassType): String {
    return when (getClassNameWithoutGenerics(psiType)) {
        RESULT ->
            when (psiField.name) {
                RESULT_ERRNO -> "状态码：0-成功；其他-失败"
                RESULT_ERROR -> "状态描述"
                RESULT_DATATYPE -> "数据类型：OBJECT/LIST/ERROR"
                RESULT_DATA -> "数据"
                else -> ""
            }
        PAGING_BODY ->
            when (psiField.name) {
                PAGING_BODY_BODY -> "列表信息"
                PAGING_BODY_PAGING -> "分页信息"
                else -> ""
            }
        PAGING ->
            when (psiField.name) {
                PAGING_FIRST -> "第一页"
                PAGING_BEFORE -> "前一页"
                PAGING_CURRENT -> "当前页"
                PAGING_LAST -> "最后一页"
                PAGING_NEXT -> "下一页"
                PAGING_LIMIT -> "每页条数"
                PAGING_TOTALPAGES -> "总页数"
                PAGING_TOTALITEMS -> "总数据量"
                else -> ""
            }
        PAGE_QUERY_COMMAND ->
            when (psiField.name) {
                PAGE_QUERY_COMMAND_PAGE_NO -> "当前页码"
                PAGE_QUERY_COMMAND_PAGE_SIZE -> "每页条数"
                PAGE_QUERY_COMMAND_ORDER_DESCES -> "查询排序"
                else -> ""
            }
        ORDER_DESC ->
            when (psiField.name) {
                ORDER_DESC_FIELD -> "排序属性"
                ORDER_DESC_ASC -> "排序规则"
                else -> ""
            }
        PAGING_QUERY_COMMAND ->
            when (psiField.name) {
                PAGING_QUERY_COMMAND_page -> "页码，从 1 开始"
                PAGING_QUERY_COMMAND_limit -> "页容，最大 1000，最小 1"
                else -> ""
            }
        OPERATOR ->
            when (psiField.name) {
                MEMBER_ID -> "操作人 memberId"
                MEMBER_NAME -> "操作人名称"
                else -> ""
            }
        OPERATOR_DTO ->
            when (psiField.name) {
                WORKER_ID -> "操作人 workerId"
                WORKER_NAME -> "操作人名称"
                else -> ""
            }
        else -> ""
    }
}

/**
 * 获取不带泛型的类型
 */
fun getClassNameWithoutGenerics(psiType: PsiClassType): String {
    val className = psiType.canonicalText
    val index = className.indexOf("<")
    return if (index == -1) className else className.substring(0, index)
}

/**
 * 获取泛型对应关系
 */
private fun getGenericsType(psiClass: PsiClass, psiType: PsiClassType): Map<String, PsiType> {
    val generics: MutableMap<String, PsiType> = mutableMapOf()
    val typeParameters = psiClass.typeParameters
    val parameters = psiType.parameters
    for (i in typeParameters.indices) {
        generics[typeParameters[i].name!!] = parameters[i]
    }
    return generics
}

/**
 * 获取子节点
 */
private fun getChildren(psiField: PsiField, fieldType: PsiType, generics: Map<String, PsiType>): List<ApiParam>? {
    return if (isBaseType(fieldType) || isBaseCollection(fieldType)) {
        null
    } else {
        if (fieldType is PsiClassType) {
            if (fieldType.hasParameters()) {
                generics[fieldType.parameters[0].presentableText]?.let {
                    PsiUtil.resolveClassInClassTypeOnly(it)?.fields.let { fields ->
                        return getBody(psiField, fieldType, fields)
                    }
                }
            }
        }
        getBody(psiField, fieldType as PsiClassType, tryGetCollectionGenericsType(fieldType))
    }
}
