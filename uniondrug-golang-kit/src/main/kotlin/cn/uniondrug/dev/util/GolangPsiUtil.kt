package cn.uniondrug.dev.util

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

}