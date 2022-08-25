/** @author dingshichen */
package cn.uniondrug.dev.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJavaDocumentedElement
import com.intellij.psi.javadoc.PsiDocTag

/**
 * 直接获取 Java 注释里的标签
 */
fun PsiJavaDocumentedElement.findTagByName(tag: String) = docComment?.findTagByName(tag)

fun PsiJavaDocumentedElement.findTagsByName(tag: String): Array<PsiDocTag>? = docComment?.findTagsByName(tag)

/**
 * 直接获取 Java 注释子元素
 */
fun PsiJavaDocumentedElement.childrenDocComment(): Array<PsiElement>? = docComment?.children