/** @author dingshichen */
package cn.uniondrug.dev.dto

import com.intellij.psi.PsiComment

/**
 * GO API 注释结构
 */
data class GoApiStruct(
    var nameComment: DocNameComment? = null,
    var authorComment: DocAuthorComment? = null,
    var deprecatedComment: DocDeprecatedComment? = null,
    var getComment: DocGetComment? = null,
    var postComment: DocPostComment? = null,
    var requestComment: DocRequestComment? = null,
    var responseComment: DocResponseComment? = null,
    val errorComment: MutableList<DocErrorComment> = mutableListOf(),
    var ignoreComment: PsiComment? = null,
    val descComment: MutableList<DocDescComment> = mutableListOf(),
) {

    /**
     * 接口有效判断
     */
    fun isValid() = requestComment != null
            && nameComment != null
            && (getComment != null
            || postComment != null
            || nameComment!!.isRestfullPrefix())

    /**
     * 接口是否可忽略
     */
    fun isIgnore() = ignoreComment != null

}

/**
 * GO MBS 注释结构
 */
data class GoMbsStruct(
    var nameComment: DocNameComment? = null,
    var authorComment: DocAuthorComment? = null,
    var mbsComment: DocMbsComment? = null,
    var topicComment: DocMbsTopicComment? = null,
    var tagComment: DocMbsTagComment? = null,
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