/** @author dingshichen */
package cn.uniondrug.dev.psi

import com.intellij.psi.PsiClass

/**
 * 获取 Class 标题
 */
fun PsiClass.getTitle() = getSimpleTitle() ?: name

/**
 * 获取 Class 简单标题
 */
fun PsiClass.getSimpleTitle(): String? {
    return childrenDocComment()
        ?.find { it.isCommentData() && it.commentText().isNotBlank() }
        ?.commentText()
}

/**
 * 获取 Class 作者
 */
fun PsiClass.getAuthor() = docComment?.getAuthor()
