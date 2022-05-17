/** @author dingshichen */
package cn.uniondrug.dev.util

/**
 * 截取字符串
 */
fun String.substring(startString: String, endString: String): String {
    val firstIndex = this.indexOf(startString)
    val startIndex = firstIndex + startString.length
    val endIndex = this.indexOf(endString)
    if (firstIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
        return ""
    }
    return this.substring(startIndex, endIndex)
}

/**
 * 获取注释前缀
 */
fun String.getCommentKey() = this.split(" ")[1]

/**
 * 根据注释前缀获取后面的值
 */
fun String.getCommentValue(commentKey: String): String {
    val index = this.indexOf(commentKey)
    if (index == -1) {
        return ""
    }
    return this.substring(index + commentKey.length).trim()
}

/**
 * 驼峰转路径
 */
fun String.humpToPath() = buildString {
    for (char in this) {
        if (char.isUpperCase()) {
            append("/").append(char.lowercaseChar())
        } else {
            append(char)
        }
    }
}