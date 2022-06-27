package cn.uniondrug.dev.util

import cn.uniondrug.dev.dto.GoApiStruct
import cn.uniondrug.dev.dto.GoMbsStruct
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
    fun getFieldJsonName(field: GoFieldDeclaration) = field.tag?.getValue("json") ?: field.fieldDefinitionList[0].name

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
     * 获取属性的描述
     */
    fun getFieldDescription(field: GoFieldDeclaration, tag: GoTag): String? {
        return tag.getValue("label")
    }

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
                        comment.text.startsWith("// ${method.name}") -> {
                            commentStruct.nameComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Get") -> {
                            commentStruct.getComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Post") -> {
                            commentStruct.postComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Desc") -> {
                            commentStruct.descComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Author") -> {
                            commentStruct.authorComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Deprecated") -> {
                            commentStruct.deprecatedComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Request") -> {
                            commentStruct.requestComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Response") -> {
                            commentStruct.responseComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Error") -> {
                            commentStruct.errorComment += comment
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
            is GoTypeSpec -> GolangPsiUtil.getRealChildren(element.parent)
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
                        comment.text.startsWith("// $structName") -> {
                            commentStruct.nameComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Mbs") -> {
                            commentStruct.mbsComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Topic") -> {
                            commentStruct.topicComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Tag") -> {
                            commentStruct.tagComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Author") -> {
                            commentStruct.authorComment = comment
                            resolve(commentStruct, before)
                        }
                    }
                }
            }
        }
        resolve(commentStruct, firstIndex)
        return if (commentStruct.isValid()) commentStruct else null
    }
}