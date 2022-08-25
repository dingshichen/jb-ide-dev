/** @author dingshichen */
package cn.uniondrug.dev.psi

import cn.uniondrug.dev.DEPRECATED
import cn.uniondrug.dev.IGNORE
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod

/**
 * 获取 Method 标题
 */
fun PsiMethod.getTitle(): String? {
    return childrenDocComment()
        ?.find { it.isCommentData() && it.commentText().isNotBlank() }
        ?.commentText()
}

/**
 * 是否忽略
 */
fun PsiMethod.isIgnore() = findTagByName(IGNORE) != null

/**
 * 是否不忽略
 */
fun PsiMethod.isNotIgnore() = !isIgnore()

/**
 * 获取 Method 注释详情
 */
fun PsiMethod.getCommentDescription(): String {
    return childrenDocComment()
        ?.filter { it.isCommentData() }
        ?.takeIf { it.size > 1 }
        ?.let { it[1].commentText() }
        ?: ""
}

/**
 * 获取作者
 */
fun PsiMethod.getAuthor(psiClass: PsiClass) = docComment?.getAuthor() ?: psiClass.getAuthor()

/**
 * 是否弃用
 */
fun PsiMethod.isDeprecated(psiClass: PsiClass) = psiClass.isAnnotated(DEPRECATED) || this.isAnnotated(DEPRECATED)
