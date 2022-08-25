/** @author dingshichen */
package cn.uniondrug.dev.psi

import com.intellij.psi.PsiElement

/**
 * 元素是否是注释内容
 */
fun PsiElement.isCommentData() = toString() == "PsiDocToken:DOC_COMMENT_DATA"

/**
 * 获取元素注释内容
 */
fun PsiElement.commentText() = text.replace("*", "").trim()