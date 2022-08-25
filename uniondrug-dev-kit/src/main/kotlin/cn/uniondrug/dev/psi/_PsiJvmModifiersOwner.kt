/** @author dingshichen */
package cn.uniondrug.dev.psi

import com.intellij.psi.PsiJvmModifiersOwner

/**
 * 获取注释值
 */
fun PsiJvmModifiersOwner.getAnnotationValues(annotation: String): List<String> {
    return getAnnotation(annotation)?.findAttributeValue("value")?.run {
        text.replace("\n", "")
            .replace("\t", "")
            .replace("{", "")
            .replace("}", "")
            .replace("\"", "")
            .replace(" ", "")
            .split(",")
    } ?: emptyList()
}