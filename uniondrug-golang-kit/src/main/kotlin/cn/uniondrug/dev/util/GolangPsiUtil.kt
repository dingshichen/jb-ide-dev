package cn.uniondrug.dev.util

import com.goide.psi.GoFieldDeclaration
import com.goide.psi.GoTag
import com.intellij.psi.PsiElement

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

    private fun addNextSibling(psiElement: PsiElement?, list: MutableList<PsiElement>) {
        if (psiElement != null) {
            list += psiElement
            addNextSibling(psiElement.nextSibling, list)
        }
    }
}