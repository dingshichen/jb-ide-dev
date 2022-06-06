/** @author dingshichen */
package cn.uniondrug.dev

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.serializer.SerializerFeature

/**
 * 构建一个 JSONArray
 */
fun <T> arrayJsonOf(vararg elements: T): JSONArray {
    return JSONArray().apply {
        if (elements.isNotEmpty()) {
            elements.forEach { this += it }
        }
    }
}

/**
 * 构建 json
 */
inline fun jsonObject(builder: JSONObject.() -> Unit) = JSONObject(true).apply { builder() }

/**
 * 输出 json
 */
inline fun buildJsonString(builder: JSONObject.() -> Unit): String {
    return JSONObject(true).run {
        builder()
        toString(
            SerializerFeature.WriteMapNullValue,        // 输出 null 值的字段
            SerializerFeature.WriteNullListAsEmpty,     // List 字段如果是 null，输出 []
            SerializerFeature.WriteNullStringAsEmpty,   // 字符串如果为 null，输出 ""
            SerializerFeature.WriteNullNumberAsZero,    // 数字字段如果为 null，输出 0
            SerializerFeature.WriteNullBooleanAsFalse,  // boolean 字段如果为 null，输出 false
            SerializerFeature.PrettyFormat              // 格式化
        )
    }
}

