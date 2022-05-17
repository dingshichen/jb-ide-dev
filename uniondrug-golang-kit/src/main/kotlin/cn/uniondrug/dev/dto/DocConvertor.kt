package cn.uniondrug.dev.dto

import cn.uniondrug.dev.Api
import cn.uniondrug.dev.ApiParam
import cn.uniondrug.dev.ApiDetail
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.serializer.SerializerFeature

/**
 * @author dingshichen
 * @date 2022/4/10
 */
object DocConvertor {

    fun convert(api: Api): ApiDetail {
        val detail = ApiDetail(api)
        //        initRequestHeader();  TODO
        initRequestExample(detail)
        initResponseExample(detail)
        initRequestBody(detail)
        initResponseBody(detail)
        return detail
    }

    private fun initRequestExample(detail: ApiDetail) {
        val example = JSONObject(true)
        detail.api.requestParams?.forEach { putParamExample(it, example) }
        detail.requestExample = jsonToString(example)
    }

    private fun initResponseExample(detail: ApiDetail) {
        val example = JSONObject(true)
        detail.api.responseParams?.forEach { putParamExample(it, example) }
        detail.responseExample = jsonToString(example)
    }

    private fun putParamExample(responseParam: ApiParam, example: JSONObject) {
        if (responseParam.children == null) {
            example[responseParam.name] = ""
        } else if (responseParam.type.endsWith("]")) {
            val array = JSONArray()
            responseParam.children?.let {
                val children = JSONObject(true)
                it.forEach { child -> putParamExample(child, children) }
                array.add(children)
            }
            example[responseParam.name] = array
        } else {
            val children = JSONObject(true)
            responseParam.children?.forEach { putParamExample(it, children) }
            example[responseParam.name] = children
        }
    }

    private fun jsonToString(json: JSONObject) = json.toString(
        SerializerFeature.WriteMapNullValue,
        SerializerFeature.WriteNullListAsEmpty,
        SerializerFeature.WriteNullStringAsEmpty,
        SerializerFeature.WriteNullNumberAsZero,
        SerializerFeature.WriteNullBooleanAsFalse,
        SerializerFeature.PrettyFormat
    )

    private fun initRequestBody(detail: ApiDetail) {
        val builder = StringBuilder("|参数名|类型|是否必填|最大长度|描述|\n|:-----|:-----|:-----|:-----|:-----|\n")
        detail.api.requestParams?.forEach { append("", builder, it) }
        detail.requestBody = builder.toString()
    }

    private fun initResponseBody(detail: ApiDetail) {
        val builder = StringBuilder("|参数名|类型|是否必填|最大长度|描述|\n|:-----|:-----|:-----|:-----|:-----|\n")
        detail.api.responseParams?.forEach { append("", builder, it) }
        detail.responseBody = builder.toString()
    }

    private fun append(prefix: String, builder: StringBuilder, param: ApiParam) {
        builder.append("| ")
            .append(prefix)
            .append(this nullDefault param.name)
            .append(" | ")
            .append(this nullDefault param.type)
            .append(" | ")
            .append(this nullDefault param.required)
            .append(" | ")
            .append(this nullDefault param.maxLength)
            .append(" | ")
            .append(this nullDefault param.description)
            .append(" | \n")
        param.children?.forEach {
            append(
                if (prefix == "") "└─" else "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$prefix",
                builder,
                it
            )
        }
    }

    private infix fun nullDefault(value: String?) = value ?: ""

    private infix fun nullDefault(value: Byte?): String {
        value?.let {
            if (it.toInt() == 1) {
                return "是"
            }
        }
        return " "
    }
}