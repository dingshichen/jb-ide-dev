/** @author dingshichen */
package cn.uniondrug.dev.util

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiJavaDocumentedElement

/**
 * 元素是否是注释内容
 */
fun PsiElement.isCommentData() = toString() == "PsiDocToken:DOC_COMMENT_DATA"

/**
 * 获取元素注释内容
 */
fun PsiElement.commentText() = text.replace("*", "").trim()

/**
 * 直接获取 Java 注释子元素
 */
fun PsiJavaDocumentedElement.childrenDocComment(): Array<PsiElement>? = docComment?.children

/**
 * 获取属性的描述
 */
fun PsiField.getFieldDescription() = childrenDocComment()?.filter { it.isCommentData() }?.joinToString("<br>") { it.commentText() }