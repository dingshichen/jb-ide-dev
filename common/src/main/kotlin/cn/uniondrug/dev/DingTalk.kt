/** @author dingshichen */
package cn.uniondrug.dev

data class MarkdownBody(
    val title: String,
    val text: String
)

data class RobotMarkdownMessage(
    private val markdown: MarkdownBody
) {
    private val msgtype = "markdown"
}

data class ResponseData(
    val errcode: Int,
    val errmsg: String,
) {

    fun isFail() = errcode != 0
}