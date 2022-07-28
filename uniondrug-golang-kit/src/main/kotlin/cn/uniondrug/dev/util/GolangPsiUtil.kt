package cn.uniondrug.dev.util

import cn.uniondrug.dev.dto.*
import com.goide.psi.*
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * @author dingshichen
 * @date 2022/4/7
 */
object GolangPsiUtil {

    /**
     * 获取属性 json 字段名
     */
    fun getFieldJsonName(field: GoFieldDeclaration) =
        field.tag?.getValue("json")?.split(",")?.get(0) ?: field.fieldDefinitionList[0].name

    /**
     * 标签是否有必填
     */
    fun isRequired(tag: GoTag) = tag.getValue("validate")?.let { it -> "required" in it } ?: false

    /**
     * 获取标签中描述的最大长度
     */
    fun getMaxLength(tag: GoTag) = tag.getValue("validate")?.split(",")?.find { max ->
        max.startsWith("max=")
    }?.substring(4)

    /**
     * 获取 mock 值
     */
    fun getMockValue(tag: GoTag) = tag.getValue("mock")

    /**
     * 获取属性的描述
     */
    fun getFieldDescription(tag: GoTag) = tag.getValue("label")

    /**
     * 获取真实子集
     */
    fun getRealChildren(psiElement: PsiElement): List<PsiElement> {
        val children = mutableListOf<PsiElement>()
        addNextSibling(psiElement.firstChild, children)
        return children
    }

    /**
     * 获取 GoType 类型或者从指针类型中获取
     */
    fun getRealTypeOrSelf(goType: GoType): GoType = when (goType) {
        is GoPointerType -> goType.type!!
        else -> goType
    }

    private fun addNextSibling(psiElement: PsiElement?, list: MutableList<PsiElement>) {
        if (psiElement != null) {
            list += psiElement
            addNextSibling(psiElement.nextSibling, list)
        }
    }

    /**
     * 解析方法，解析注释
     */
    fun resulveFuncComment(method: GoMethodDeclaration, psiElements: Array<PsiElement>): GoApiStruct? {
        val methodName = method.name ?: return null
        val firstIndex = psiElements.indexOf(method)
        val commentStruct = GoApiStruct()
        tailrec fun resolve(
            commentStruct: GoApiStruct,
            index: Int,
        ) {
            val before = index - 2
            if (before < 0) {
                return
            }
            when (val comment = psiElements[before]) {
                is PsiComment -> {
                    when {
                        comment.text.startsWith("// $methodName ") -> {
                            commentStruct.nameComment = DocNameComment(methodName, comment)
                        }
                        comment.text.startsWith("// Deprecated") -> {
                            commentStruct.deprecatedComment = DocDeprecatedComment(comment)
                            resolve(commentStruct, before)
                        }
                        docGetPattern.matcher(comment.text).find() -> {
                            commentStruct.getComment = DocGetComment(comment)
                            resolve(commentStruct, before)
                        }
                        docPostPattern.matcher(comment.text).find() -> {
                            commentStruct.postComment = DocPostComment(comment)
                            resolve(commentStruct, before)
                        }
                        docAuthorPattern.matcher(comment.text).find() -> {
                            commentStruct.authorComment = DocAuthorComment(comment)
                            resolve(commentStruct, before)
                        }
                        docRequestPattern.matcher(comment.text).find() -> {
                            commentStruct.requestComment = DocRequestComment(comment)
                            resolve(commentStruct, before)
                        }
                        docResponsePattern.matcher(comment.text).find() -> {
                            commentStruct.responseComment = DocResponseComment(comment)
                            resolve(commentStruct, before)
                        }
                        docResponseListPattern.matcher(comment.text).find() -> {
                            commentStruct.responseComment = DocResponseListComment(comment)
                            resolve(commentStruct, before)
                        }
                        docResponsePagingPattern.matcher(comment.text).find() -> {
                            commentStruct.responseComment = DocResponsePagingComment(comment)
                            resolve(commentStruct, before)
                        }
                        docErrorPattern.matcher(comment.text).find() -> {
                            commentStruct.errorComment += DocErrorComment(comment)
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// @Ignore") -> {
                            commentStruct.ignoreComment = DocIgnoreComment(comment)
                            resolve(commentStruct, before)
                        }
                        else -> {
                            commentStruct.descComment += DocDescComment(comment)
                            resolve(commentStruct, before)
                        }
                    }
                }
            }
        }
        resolve(commentStruct, firstIndex)
        return if (commentStruct.isValid()) commentStruct else null
    }

    /**
     * 解析结构体的注释
     */
    fun resulveStructComment(element: PsiElement, psiFile: PsiFile): GoMbsStruct? {
        // 需要遍历的上级集合
        val psiElements: List<PsiElement> = when (element) {
            is GoTypeDeclaration -> psiFile.children.toList()
            is GoTypeSpec -> getRealChildren(element.parent)
            else -> listOf()
        }
        // 结构体名称
        val structName = when (element) {
            is GoTypeDeclaration -> element.typeSpecList[0].name
            is GoTypeSpec -> element.name
            else -> null
        } ?: return null
        // 步长
        val step = when (element) {
            is GoTypeDeclaration -> 2
            is GoTypeSpec -> 3
            else -> 2
        }
        val firstIndex = psiElements.indexOf(element)
        val commentStruct = GoMbsStruct()
        tailrec fun resolve(
            commentStruct: GoMbsStruct,
            index: Int,
        ) {
            val before = index - step
            if (before < 0) {
                return
            }
            when (val comment = psiElements[before]) {
                is PsiComment -> {
                    when {
                        comment.text.startsWith("// $structName ") -> {
                            commentStruct.nameComment = DocNameComment(structName, comment)
                        }
                        docMbsPattern.matcher(comment.text).find() -> {
                            commentStruct.mbsComment = DocMbsComment(comment)
                            resolve(commentStruct, before)
                        }
                        docMbsTopicPattern.matcher(comment.text).find() -> {
                            commentStruct.topicComment = DocMbsTopicComment(comment)
                            resolve(commentStruct, before)
                        }
                        docMbsTagPattern.matcher(comment.text).find() -> {
                            commentStruct.tagComment = DocMbsTagComment(comment)
                            resolve(commentStruct, before)
                        }
                        docAuthorPattern.matcher(comment.text).find() -> {
                            commentStruct.authorComment = DocAuthorComment(comment)
                            resolve(commentStruct, before)
                        }
                    }
                }
            }
        }
        resolve(commentStruct, firstIndex)
        return if (commentStruct.isValid()) commentStruct else null
    }

    /**
     * 获取 controller 级路由前缀
     */
    fun findRoutePrefix(struct: PsiElement, psiFile: PsiFile): DocRoutePrefixComment? {
        // 需要遍历的上级集合
        val psiElements = psiFile.children
        val firstIndex = psiElements.indexOf(struct.context)
        var before = firstIndex - 2
        while (before >= 0) {
            when (val comment = psiElements[before]) {
                is PsiComment -> {
                    if (docRoutePrefixPattern.matcher(comment.text).find()) {
                        return DocRoutePrefixComment(comment)
                    } else {
                        before -= 2
                    }
                }
                else -> before = 0
            }
        }
        return null
    }

    /**
     * 获取 controller 标题
     */
    fun findStructTitle(goType: GoType, struct: PsiElement, psiFile: PsiFile): String {
        val structName = goType.presentationText.replace("*", "")
        // 需要遍历的上级集合
        val psiElements = psiFile.children
        val firstIndex = psiElements.indexOf(struct.context)
        var before = firstIndex - 2
        while (before >= 0) {
            when (val comment = psiElements[before]) {
                is PsiComment -> {
                    if (comment.text.startsWith("// $structName")) {
                        return comment.text.getCommentValue(structName).ifBlank { structName }
                    } else {
                        before -= 2
                    }
                }
                else -> before = 0
            }
        }
        return structName
    }
}