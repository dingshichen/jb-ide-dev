/** @author dingshichen */
package cn.uniondrug.dev

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.serializer.SerializerFeature

/**
 * 构建一个 JSONArray
 */
fun <T> arrayJsonOf(vararg elements: T): JSONArray {
    val array = JSONArray()
    if (elements.isNotEmpty()) {
        elements.forEach { array += it }
    }
    return array
}

/**
 * 输出 json
 */
inline fun buildJsonString(builder: JSONObject.() -> Unit): String {
    val json = JSONObject(true)
    json.builder()
    return json.toString(
        SerializerFeature.WriteMapNullValue,        // 输出 null 值的字段
        SerializerFeature.WriteNullListAsEmpty,     // List 字段如果是 null，输出 []
        SerializerFeature.WriteNullStringAsEmpty,   // 字符串如果为 null，输出 ""
        SerializerFeature.WriteNullNumberAsZero,    // 数字字段如果为 null，输出 0
        SerializerFeature.WriteNullBooleanAsFalse,  // boolean 字段如果为 null，输出 false
        SerializerFeature.PrettyFormat              // 格式化
    )
}

