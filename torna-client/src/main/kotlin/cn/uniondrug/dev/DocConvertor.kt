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

        fun convert(api: Api) = apiDetail(api) {
            initRequestBody {
                buildString {
                    append("|参数名|类型|是否必填|最大长度|描述|\n|:-----|:-----|:-----|:-----|:-----|\n")
                    api.requestParams?.forEach {
                        treeAppend("", this, it)
                    }
                }
            }
            initRequestExample {
                buildJsonString {
                    api.requestParams?.forEach {
                        putParamExample(it, this)
                    }
                }
            }
            initResponseBody {
                buildString {
                    append("|参数名|类型|是否必填|最大长度|描述|\n|:-----|:-----|:-----|:-----|:-----|\n")
                    api.responseParams?.forEach {
                        treeAppend("", this, it)
                    }
                }
            }
            initResponseExample {
                buildJsonString {
                    api.responseParams?.forEach {
                        putParamExample(it, this)
                    }
                }
            }
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

        private fun treeAppend(prefix: String, builder: StringBuilder, param: ApiParam) {
            builder.append("| $prefix${param.name} | ${param.type} | ${param.required} | ${param.maxLength ?: ""} | ${param.description ?: ""} | \n")
            param.children?.forEach {
                treeAppend(
                    if (prefix == "") "└─" else "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$prefix",
                    builder,
                    it
                )
            }
        }

        private inline fun buildJsonString(builder: JSONObject.() -> Unit): String {
            val json = JSONObject(true)
            json.builder()
            return json.toString(
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.PrettyFormat
            )
        }

    }


}