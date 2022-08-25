/** @author dingshichen */
package cn.uniondrug.dev.psi

import cn.uniondrug.dev.MOCK
import cn.uniondrug.dev.MOCK_AT
import cn.uniondrug.dev.util.JSON_ALIAS
import com.intellij.codeInsight.AnnotationUtil
import com.intellij.psi.PsiField

/**
 * 获取属性的描述
 */
fun PsiField.getFieldDescription(): String? {
    return childrenDocComment()
        ?.filter { it.isCommentData() }
        ?.joinToString("<br>") { it.commentText() }
}

/**
 * 获取属性字段名
 */
fun PsiField.getFieldName(): String {
    return AnnotationUtil.findAnnotation(this, JSON_ALIAS)
        ?.let { AnnotationUtil.getStringAttributeValue(it, "value") }
        ?: name
}

/**
 * 获取示例值
 */
fun PsiField.getMockValue(): String? {
    return docComment?.findTagByName(MOCK)?.text?.replace(MOCK_AT, "")?.trim()?.ifEmpty { null }
}