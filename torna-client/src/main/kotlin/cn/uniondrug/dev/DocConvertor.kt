package cn.uniondrug.dev

import cn.uniondrug.dev.util.BaseDataTypeMockUtil
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.serializer.SerializerFeature

/**
 * @author dingshichen
 * @date 2022/4/14
 */
class DocConvertor {

    companion object {

        fun convert(api: Api): ApiDetail {
            return apiDetail(api) {
//                initRequestHeader {
//
//                }
                initRequestBody {
                    val builder = StringBuilder("|参数名|类型|是否必填|最大长度|描述|\n|:-----|:-----|:-----|:-----|:-----|\n")
                    api.requestParams?.forEach { append("", builder, it) }
                    builder.toString()
                }
                initRequestExample {
                    val example = JSONObject(true)
                    api.requestParams?.forEach { putParamExample(it, example) }
                    example.jsonToString()
                }
                initResponseBody {
                    val builder = StringBuilder("|参数名|类型|是否必填|最大长度|描述|\n|:-----|:-----|:-----|:-----|:-----|\n")
                    api.responseParams?.forEach { append("", builder, it) }
                    builder.toString()
                }
                initResponseExample {
                    val example = JSONObject(true)
                    api.responseParams?.forEach { putParamExample(it, example) }
                    example.jsonToString()
                }
            }
        }

        private fun apiDetail(api: Api, init: ApiDetail.() -> Unit): ApiDetail {
            val apiDetail = ApiDetail(api)
            apiDetail.init()
            return apiDetail
        }

        private fun putParamExample(responseParam: ApiParam, example: JSONObject) {
            if (responseParam.type == "List") {
                example[responseParam.name] = JSONArray()
            } else if (responseParam.type in BASE_LIST) {
                val firtsIndex = responseParam.type.indexOf("<")
                val lastIndex = responseParam.type.indexOf(">")
                val baseType = responseParam.type.substring(firtsIndex + 1, lastIndex)
                example[responseParam.name] = "[${BaseDataTypeMockUtil.jsonValueByType(baseType)}]"
            } else if (responseParam.children == null) {
                val valus = BaseDataTypeMockUtil.getValByTypeAndFieldName(responseParam.type, responseParam.name);
                example[responseParam.name] = valus
            } else if (responseParam.type.endsWith("]") || responseParam.type.startsWith("List<")) {
                val array = JSONArray()
                responseParam.children?.let { childrenParam ->
                    if (childrenParam.isNotEmpty()) {
                        val children = JSONObject(true)
                        childrenParam.forEach { child -> putParamExample(child, children) }
                        array.add(children)
                    }
                }
                example[responseParam.name] = array
            } else {
                val children = JSONObject(true)
                responseParam.children?.forEach { child -> putParamExample(child, children) }
                example[responseParam.name] = children
            }
        }

        private fun append(prefix: String, builder: StringBuilder, param: ApiParam) {
            builder.append("| $prefix${param.name} | ${param.type} | ${param.required} | ${param.maxLength ?: ""} | ${param.description ?: ""} | \n")
            param.children?.forEach {
                append(
                    if (prefix == "") "└─" else "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$prefix",
                    builder,
                    it
                )
            }
        }

        private fun JSONObject.jsonToString() = toString(
            SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteNullStringAsEmpty,
            SerializerFeature.WriteNullNumberAsZero,
            SerializerFeature.WriteNullBooleanAsFalse,
            SerializerFeature.PrettyFormat
        )

    }


}