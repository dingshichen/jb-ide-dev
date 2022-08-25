/** @author dingshichen */
package cn.uniondrug.dev.psi

import com.intellij.codeInsight.AnnotationUtil
import com.intellij.psi.PsiModifierListOwner


fun PsiModifierListOwner.isAnnotated(annotation: String) = AnnotationUtil.isAnnotated(this, annotation, 0)

fun PsiModifierListOwner.isAnnotated(annotations: List<String>) = AnnotationUtil.isAnnotated(this, annotations, 0)

fun PsiModifierListOwner.findAnnotation(annotation: String) = AnnotationUtil.findAnnotation(this, annotation)

fun PsiModifierListOwner.findAnnotation(annotations: List<String>) = AnnotationUtil.findAnnotation(this, annotations)