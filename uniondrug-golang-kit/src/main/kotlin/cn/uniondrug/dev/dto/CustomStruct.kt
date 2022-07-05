/** @author dingshichen */
package cn.uniondrug.dev.dto

import com.intellij.psi.PsiComment

/**
 * GO API 注释结构
 */
data class GoApiStruct(
    var nameComment: PsiComment? = null,
    var authorComment: PsiComment? = null,
    var descComment: PsiComment? = null,
    var deprecatedComment: PsiComment? = null,
    var getComment: PsiComment? = null,
    var postComment: PsiComment? = null,
    var requestComment: PsiComment? = null,
    var responseComment: PsiComment? = null,
    val errorComment: ArrayList<PsiComment> = ArrayList(),
) {

    /**
     * 接口有效判断
     */
    fun isValid() = nameComment != null
            && requestComment != null
            && (getComment != null
            || postComment != null
            || nameComment!!.text.startsWith("// Post")
            || nameComment!!.text.startsWith("// Get"))

}

/**
 * GO MBS 注释结构
 */
data class GoMbsStruct(
    var nameComment: PsiComment? = null,
    var authorComment: PsiComment? = null,
    var mbsComment: PsiComment? = null,
    var topicComment: PsiComment? = null,
    var tagComment: PsiComment? = null,
) {

    /**
     * 接口有效判断
     */
    fun isValid() = nameComment != null
            && mbsComment != null
            && topicComment != null
            && tagComment != null

}

/**
 * 接口基本协议
 */
data class ApiBaseAccess(
    val url: String,
    val httpMethod: String,
    val contentType: String,
)