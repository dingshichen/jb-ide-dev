package cn.uniondrug.dev.util

import com.goide.psi.GoFieldDeclaration
import com.goide.psi.GoTag
import com.goide.psi.GoType
import com.goide.psi.impl.GoTypeUtil

/**
 * @author dingshichen
 * @date 2022/4/7
 */
object GolangPsiUtil {

    private const val STRING_TYPE = "string"

    fun isBaseType(goType: GoType) = GoTypeUtil.isBasicType(goType, goType.context) || STRING_TYPE == goType.text

    fun isNotBaseType(goType: GoType) = !isBaseType(goType)

    /**
     * 获取属性 json 字段名
     */
    fun getFieldJsonName(field: GoFieldDeclaration) = field.tag?.getValue("json") ?: field.fieldDefinitionList[0].name

    /**
     * 标签是否有必填
     */
    fun isRequired(tag: GoTag) = tag.getValue("validate")?.let { validate -> "required" in validate } ?: false
}