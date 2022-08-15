/** @author dingshichen */
package cn.uniondrug.dev.util

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
 * 获取注释值
 */
fun String.getAnnotationValue(annotationName: String) =
    substring(indexOf(annotationName) + annotationName.length + 1, lastIndexOf(")"))

/**
 * 获取注释的多个值（"," 分割）
 */
fun String.getAnnotationMultiValues(annotationName: String) =
    getAnnotationValue(annotationName).run {
        var begin = 0
        var isBegin = false
        val pairs = mutableListOf<Pair<Int, Int>>()
        val values = mutableListOf<String>()
        forEachIndexed { index, char ->
            if (char.toString() == "\"") {
                isBegin = !isBegin
            } else if (char.toString() == ",") {
                if (!isBegin) {
                    pairs += Pair(begin, index)
                    begin = index + 1
                }
            }
        }
        pairs += Pair(begin, this.length)
        pairs.forEach { (begin, end) ->
            substring(begin, end).replace("\"", "").trim().let {
                if (it.isNotBlank()) {
                    values += it
                }
            }
        }
        values
    }

/**
 * 驼峰转路径
 */
fun String.humpToPath() = buildString {
    for (char in this@humpToPath) {
        if (char.isUpperCase()) {
            append("/").append(char.lowercaseChar())
        } else {
            append(char)
        }
    }
}

/**
 * 舍弃英文 . 后缀
 */
//fun String.removePointSuffix(): String = if (endsWith(".")) substring(0, length - 1) else this