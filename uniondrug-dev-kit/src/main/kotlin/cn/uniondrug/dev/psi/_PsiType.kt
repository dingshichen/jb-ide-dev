/** @author dingshichen */
package cn.uniondrug.dev.psi

import cn.uniondrug.dev.BASE
import cn.uniondrug.dev.FULL_BASE_LIST
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType

/**
 * 是否是基本类型
 */
fun PsiType.isBaseType() = canonicalText in BASE

/**
 * 是否是基本类型集合
 */
fun PsiType.isBaseCollection(): Boolean {
    if (this is PsiClassType) {
        if (canonicalText in FULL_BASE_LIST) {
            return true
        }
    }
    return false
}


