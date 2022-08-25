/** @author dingshichen */
package cn.uniondrug.dev.psi

import cn.uniondrug.dev.AUTHOR
import com.intellij.psi.javadoc.PsiDocComment

/**
 * 获取注释里的作者
 */
fun PsiDocComment.getAuthor() = findTagByName(AUTHOR)?.valueElement?.commentText()