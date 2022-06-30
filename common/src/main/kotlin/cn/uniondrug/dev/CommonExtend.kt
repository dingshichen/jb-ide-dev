/** @author dingshichen */
package cn.uniondrug.dev

/**
 * 截取
 */
fun String.substring(startString: String, endString: String): String {
    val a = indexOf(startString)
    val b = lastIndexOf(endString)
    if (a == -1 || b == -1) {
        return ""
    }
    return substring(a + startString.length, b)
}

/**
 * 是否是带泛型的集合
 */
fun String.isJavaGenericArray() = this.startsWith("List<")
        || this.startsWith("ArrayList<")
        || this.startsWith("LinkedList<")
        || this.startsWith("Set<")
        || this.startsWith("HashSet<")

/**
 * 截出 JAVA 泛型
 */
fun String.subJavaGeneric() = substring("<", ">")

/**
 * 是否是 GO 集合
 */
fun String.isGoArray() = this.startsWith("[]")

/**
 * 截出 GO 集合里的类型
 */
fun String.subGoTypeInArray() = this.substring(2)

/**
 * 分割符转小驼峰
 */
fun String.splitToSmallHump(split: String): String {
    val builder = StringBuilder()
    for (s in this.split(split)) {
        if (builder.toString() == "") {
            builder.append(s)
        } else {
            builder.append(s[0].uppercase())
            builder.append(s.substring(1))
        }
    }
    return builder.toString()
}