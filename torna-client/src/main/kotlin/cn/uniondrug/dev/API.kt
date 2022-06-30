/** @author dingshichen */
package cn.uniondrug.dev

import cn.uniondrug.dev.util.BaseDataTypeMockUtil
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

/**
 * API 接口
 */
data class Api(
    /** 文件夹，如果 Controller 有注释，就用注释第一行，没有就使用 Controller Class Name */
    val folder: String,
    /** 文档名称  */
    val name: String,
    /** 文档概述  */
    var description: String?,
    /** 维护人  */
    var author: String?,
    /** 访问URL  */
    val url: String,
    /** 废弃信息  */
    val deprecated: String?,
    /** HTTP 请求方式  */
    val httpMethod: String,
    /** HTTP ContentType  */
    val contentType: String,
    /** 请求入参 */
    var requestParams: List<ApiParam>?,
    /** 响应返回值入参 */
    var responseParams: List<ApiParam>?,
) {

    val fileName: String by lazy {
        url.splitToSmallHump("/")
    }

    val requestBody: String by lazy {
        buildString {
            append("|参数名|类型|是否必填|最大长度|描述|\n|:-----|:-----|:-----|:-----|:-----|\n")
            requestParams?.forEach {
                requestAppend("", it)
            }
        }
    }

    val requestExample: String by lazy {
        buildJsonString {
            requestParams?.forEach {
                putParamExample(it)
            }
        }
    }

    val responseBody: String by lazy {
        buildString {
            append("|参数名|类型|最大长度|描述|\n|:-----|:-----|:-----|:-----|\n")
            responseParams?.forEach {
                responseAppend("", it)
            }
        }
    }

    val responseExample: String by lazy {
        buildJsonString {
            responseParams?.forEach {
                putParamExample(it)
            }
        }
    }

    val markdownText: String by lazy {
        "**$name**\n\n" +
                "**URL:** `$url`\n\n" +
                "**Type:** `$httpMethod`\n\n" +
                "**Author:** $author\n\n" +
                "**Content-Type:** `$contentType`\n\n" +
                "**Description:** $description\n\n" +
                "**Body-parameters:**\n\n$requestBody\n\n" +
                "**Request-example:**\n```json\n$requestExample\n```\n\n" +
                "**Response-fields:**\n\n$responseBody\n\n" +
                "**Response-example:**\n```json\n$responseExample\n```\n\n"
    }

}

/**
 * API 接口参数
 */
data class ApiParam(
    /** 字段名称  */
    val name: String,
    /** 字段类型   */
    val type: CommonType,
    /** 是否必须，1：是，0：否  */
    val required: Boolean,
    /** 最大长度  */
    var maxLength: String? = null,
    /** 描述  */
    var description: String? = null,
    /** 父节点  */
    var parentId: String? = null,
    /** 子节点  */
    var children: List<ApiParam>? = null,
) {
    val example: String
        get() {
            return if (type.isBaseType) getExample(this).toString() else ""
        }
}

/**
 * MBS 事件结构
 */
data class MbsEvent(
    /** 事件名称 */
    val name: String,
    /** MBS 编号（1 或 2） */
    val mbs: String,
    /** 主题 */
    val topic: String,
    /** 标签 */
    val tag: String,
    /** 作者 */
    val author: String?,
    /** 消息体字段 */
    var messageParams: List<ApiParam>? = null
) {

    val fileName: String by lazy {
        tag.splitToSmallHump("_")
    }

    val messageBody: String by lazy {
        buildString {
            append("|参数名|类型|是否必填|最大长度|描述|\n|:-----|:-----|:-----|:-----|:-----|\n")
            messageParams?.forEach {
                requestAppend("", it)
            }
        }
    }

    val messageExample: String by lazy {
        buildJsonString {
            messageParams?.forEach {
                putParamExample(it)
            }
        }
    }

    val markdownText: String by lazy {
        "**$name**\n\n" +
                "**MBS:** `$mbs`\n\n" +
                "**Topic:** `$topic`\n\n" +
                "**Tag:** `$tag`\n\n" +
                "**Author:** $author\n\n" +
                "**Message-parameters:**\n\n$messageBody\n\n" +
                "**Message-example:**\n```json\n$messageExample\n```"
    }
}

fun JSONObject.putParamExample(param: ApiParam) {
    this[param.name] = getExample(param)
}

fun getExample(param: ApiParam): Any {
    return when (param.type) {
        CommonType.STRING,
        CommonType.BYTE,
        CommonType.INT,
        CommonType.LONG,
        CommonType.FLOAT -> BaseDataTypeMockUtil.getValByTypeAndFieldName(param.type.value, param.name)
        CommonType.ARRAY -> JSONArray()
        CommonType.BOOL -> true
        CommonType.OBJECT -> jsonObject {
            param.children?.forEach {
                this[it.name] = getExample(it)
            }
        }
        CommonType.ARRAY_STRING,
        CommonType.ARRAY_BOOL,
        CommonType.ARRAY_BYTE,
        CommonType.ARRAY_INT,
        CommonType.ARRAY_LONG,
        CommonType.ARRAY_FLOAT -> {
            val begin = param.type.value.indexOf("[")
            val end = param.type.value.indexOf("]")
            val sampleType = param.type.value.substring(begin + 1, end)
            arrayJsonOf(
                BaseDataTypeMockUtil.getValByTypeAndFieldName(sampleType, param.name)
            )
        }
        CommonType.ARRAY_OBJECT -> arrayJsonOf(
            jsonObject {
                param.children?.forEach {
                    this[it.name] = getExample(it)
                }
            }
        )
    }
}

private fun StringBuilder.requestAppend(prefix: String, param: ApiParam) {
    append("| $prefix${param.name} | ${param.type.value} | ${param.required} | ${param.maxLength ?: ""} | ${param.description ?: ""} | \n")
    param.children?.forEach {
        requestAppend(if (prefix == "") "└─" else "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$prefix",
            it
        )
    }
}

private fun StringBuilder.responseAppend(prefix: String, param: ApiParam) {
    append("| $prefix${param.name} | ${param.type.value} | ${param.maxLength ?: ""} | ${param.description ?: ""} | \n")
    param.children?.forEach {
        responseAppend(
            if (prefix == "") "└─" else "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$prefix",
            it
        )
    }
}