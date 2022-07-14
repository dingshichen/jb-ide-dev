/** @author dingshichen */
package cn.uniondrug.dev.util

import cn.uniondrug.dev.*
import cn.uniondrug.dev.mss.MBS_SERVICE_1
import cn.uniondrug.dev.mss.MBS_SERVICE_2
import com.intellij.codeInsight.AnnotationUtil
import com.intellij.openapi.project.Project
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
 * 获取目录的名称，先获取注释内容，没有描述的注释就获取 className
 */
fun getFolder(psiClass: PsiClass) = psiClass.childrenDocComment()?.find {
    it.isCommentData() && it.commentText().isNotBlank()
}?.commentText() ?: psiClass.name!!

/**
 * 获取 API 名称
 */
fun getApiName(psiMethod: PsiMethod) = psiMethod.childrenDocComment()?.find {
    it.isCommentData() && it.commentText().isNotBlank()
}?.commentText() ?: throw DocBuildFailException("获取 API 名称失败，请检查方法注释是否存在")

/**
 * 获取 MBS 事件名称
 */
fun getMbsName(psiClass: PsiClass) = psiClass.childrenDocComment()?.find {
    it.isCommentData()
}?.commentText() ?: throw DocBuildFailException("获取 MBS 事件名称失败，请检查类注释是否有效")

/**
 * 获取 API 描述
 */
fun getApiDescription(psiMethod: PsiMethod) = psiMethod.childrenDocComment()?.let {
    val comments = it.filter { e -> e.isCommentData() }
    if (comments.size > 1) {
        return comments[1].commentText()
    } else {
        return ""
    }
} ?: ""

/**
 * 获取 API 作者
 */
fun getApiAuthor(psiClass: PsiClass, psiMethod: PsiMethod) = psiMethod.docComment?.let {
    getApiAuthor(it) ?: getClassAuthor(psiClass)
} ?: getClassAuthor(psiClass)

/**
 * 获取类注释里的作者
 */
fun getClassAuthor(psiClass: PsiClass) = psiClass.docComment?.let { getApiAuthor(it) } ?: ""

/**
 * 获取注释里的作者
 */
fun getApiAuthor(docComment: PsiDocComment) = docComment.findTagByName("author")?.valueElement?.commentText()

/**
 * 是否弃用
 */
fun isDeprecated(psiClass: PsiClass, psiMethod: PsiMethod) =
    AnnotationUtil.isAnnotated(psiClass, DEPRECATED, 0)
            || AnnotationUtil.isAnnotated(psiMethod, DEPRECATED, 0)

/**
 * 是否忽略
 */
fun isIgnore(psiMethod: PsiMethod) = psiMethod.docComment?.findTagByName("ignore")?.let { true } ?: false

/**
 * 是否不忽略
 */
fun isNotIgnore(psiMethod: PsiMethod) = !isIgnore(psiMethod)

/**
 * 获取属性名
 */
fun getFieldName(psiField: PsiField): String {
    return AnnotationUtil.findAnnotation(psiField, JSON_ALIAS)?.let {
        AnnotationUtil.getStringAttributeValue(it, "value")
    } ?: psiField.name
}

/**
 * 从参数中获取类型
 */
fun getRequestBody(project: Project, parameter: PsiParameter) = parameter.type.run {
    getBody(
        project = project,
        psiType = this as PsiClassType,
        fieldNode = newFiledNode(this),
    )
}

/**
 * 从返回值解析获取返回结构体
 */
fun getResponseBody(project: Project, returnElement: PsiTypeElement) = returnElement.type.run {
    getBody(
        project = project,
        psiType = this as PsiClassType,
        fieldNode = newFiledNode(this),
    )
}

/**
 * 解析 body 参数
 * @param parentField 父节点属性名
 * @param psiType 当前属性类型
 * @param childrenFields 指定子节点名
 * @param fieldNode 父级节点
 * @param ignoreFieldNames 忽略的字段名集合
 */
fun getBody(
    project: Project,
    parentField: PsiField? = null,
    psiType: PsiClassType,
    childrenFields: Array<PsiField>? = null,
    fieldNode: FieldNode,
    ignoreFieldNames: MutableList<String> = mutableListOf(),
): MutableList<ApiParam> {
    // 获取到类上的忽略序列化注解
    psiType.resolve()?.getAnnotationValues(ANNOTATION_JSON_IGNORE_PROPERTIES)?.let {
        ignoreFieldNames += it
    }
    val params = mutableListOf<ApiParam>()
    // 递归获取父类字段
    psiType.superTypes.forEach {
        if (it is PsiClassType) {
            if (it.canonicalText == "java.lang.Object"
                || it.canonicalText == "java.io.Serializable"
                || it.canonicalText.startsWith("java.util.Collection")
                || it.canonicalText.startsWith("java.lang.Iterable")
            ) {
                return@forEach
            }
            // 将父类的忽略字段一起加入
            params += getBody(project, parentField, it, fieldNode = fieldNode, ignoreFieldNames = ignoreFieldNames)
        }
    }
    val psiClass = PsiUtil.resolveClassInClassTypeOnly(psiType) ?: throw DocBuildFailException("获取请求体参数类型失败")
    // 泛型对应真实类型关系 K: 泛型 V: 真实类型的 PsiType
    val generics = getGenericsType(psiClass, psiType)
    // 没有给出属性字段，则解析类型里的属性
    val fields = childrenFields ?: psiClass.fields
    fields.forEach {
        if (it.name == "serialVersionUID" || it.name in ignoreFieldNames) {
            // 序列化 ID || 忽略序列化
            return@forEach
        }
        var fieldType = it.type
        // 如果能获取到对应的真实类型，说明是范型
        generics[fieldType.presentableText]?.let { pt ->
            if ("?" == pt.presentableText) {
                // 如果是通配符，说明没有此范型所在字段是没有定义的，跳过
                return@forEach
            }
            // 替换成真实类型
            fieldType = pt
        }
        val commonTypeConvertor = project.getService(CommonTypeConvertor::class.java)
        val chirldNode = newFiledNode(fieldType)
        if (chirldNode.existFromDownToUp(fieldNode)) {
            // 防止无限递归
            return@forEach
        }
        chirldNode.parentNode = fieldNode
        fieldNode += chirldNode
        params += ApiParam(
            name = getFieldName(it),
            type = commonTypeConvertor.convert(fieldType.presentableText),
            required = AnnotationUtil.isAnnotated(it, REQUIRED, 0),
            maxLength = getMaxLength(it),
            description = it.getFieldDescription() ?: getUniondrugFieldDescription(it, psiType),
            parentId = parentField?.name ?: "",
            children = getChildren(project, it, fieldType, generics, chirldNode),
        )
    }
    return params
}

/**
 * 如果类型是 List<T> Set<T>
 */
private fun tryGetCollectionGenericsType(psiType: PsiType, ignores: MutableList<String>): Array<PsiField>? {
    if (psiType.presentableText.isJavaGenericArray()) {
        if (psiType is PsiClassType) {
            psiType.parameters.first {
                return PsiUtil.resolveClassInClassTypeOnly(it)?.let { psiClass ->
                    ignores += psiClass.getAnnotationValues(ANNOTATION_JSON_IGNORE_PROPERTIES)
                    psiClass.allFields
                }
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
    typeParameters.forEachIndexed { i, psiTypeParameter ->
        if (parameters.size - 1 >= i) {
            psiTypeParameter.name?.let {
                generics[it] = parameters[i]
            }
        }
    }
    return generics
}

/**
 * 获取子节点
 */
private fun getChildren(
    project: Project,
    psiField: PsiField,
    fieldType: PsiType,
    generics: Map<String, PsiType>,
    parentNode: FieldNode,
): List<ApiParam>? {
    return if (isBaseType(fieldType) || isBaseCollection(fieldType)) {
        null
    } else {
        // 属性上可能有忽略注解
        val ignores = mutableListOf<String>()
        ignores += psiField.getAnnotationValues(ANNOTATION_JSON_IGNORE_PROPERTIES)
        if (fieldType is PsiClassType) {
            if (fieldType.hasParameters()) {
                generics[fieldType.parameters[0].presentableText]?.let {
                    PsiUtil.resolveClassInClassTypeOnly(it)?.fields.let { fields ->
                        return getBody(project, psiField, fieldType, fields, parentNode, ignores)
                    }
                }
            }
        }
        val childrenFields = tryGetCollectionGenericsType(fieldType, ignores)
        getBody(project, psiField, fieldType as PsiClassType, childrenFields, parentNode, ignores)
    }
}

/**
 * 获取参数引用里的常量值
 */
fun getLiteralValue(expression: PsiExpression): String? {
    when (expression) {
        is PsiReferenceExpression -> {
            val literal = expression.resolve()?.children?.find { it is PsiLiteralExpression }
            if (literal == null) {
                // 这里特殊处理下使用枚举定义 topic tag 的一种场景
                val child = expression.children[0]
                if (child is PsiReferenceExpression) {
                    val enumConstant = child.resolve()
                    if (enumConstant is PsiEnumConstant) {
                        val constructorMap = mutableMapOf<String, String>()
                        // 构造函数形参
                        enumConstant.resolveConstructor()?.parameterList?.parameters?.let {
                            enumConstant.argumentList?.expressions?.forEachIndexed { idx, psiExpression ->
                                constructorMap[it[idx].name] = psiExpression.text
                            }
                            expression.children.find { it is PsiIdentifier }?.let {
                                return constructorMap[it.text]?.replace("\"", "")
                            }
                        }
                    }
                    return expression.resolve()?.children?.find { it.text == "=" }?.let {
                        // 获取下一个表达式
                        fun findPsiReferenceExpression(psiElement: PsiElement): PsiReferenceExpression? {
                            return when (val next = psiElement.nextSibling) {
                                null -> null
                                is PsiReferenceExpression -> next
                                else -> findPsiReferenceExpression(next)
                            }
                        }
                        findPsiReferenceExpression(it)?.let { reference ->
                            getLiteralValue(reference)
                        }
                    }
                } else {
                    return null
                }
            } else {
                return literal.text?.replace("\"", "")
            }
        }
        else -> return null
    }
}

fun isMbsService(psiClass: PsiClass) =
    psiClass.qualifiedName == MBS_SERVICE_1 || psiClass.qualifiedName == MBS_SERVICE_2

fun newFiledNode(psiType: PsiType) = psiType.presentableText.run {
    val type = if (isJavaGenericArray()) subJavaGeneric() else this
    FieldNode(type)
}

/**
 * 获取错误状态码
 */
fun getErrorParams(psiMethod: PsiMethod): List<ApiErrno> {
    return psiMethod.docComment?.findTagsByName("errno")?.map { tag ->
        val errno = tag.valueElement?.commentText() ?: throw DocBuildFailException("请正确定义错误状态码 errno")
        val error = tag.dataElements[1].commentText()
        ApiErrno(errno, error, "")
    } ?: emptyList()
}
