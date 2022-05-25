package cn.uniondrug.dev

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

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
                        requestAppend("", this, it)
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
                    append("|参数名|类型|最大长度|描述|\n|:-----|:-----|:-----|:-----|\n")
                    api.responseParams?.forEach {
                        responseAppend("", this, it)
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

        fun putParamExample(param: ApiParam, example: JSONObject) {
            when (param.type) {
                CommonType.STRING -> example[param.name] = "xxxxxxx"
                CommonType.BOOL -> example[param.name] = true
                CommonType.BYTE -> example[param.name] = 0
                CommonType.INT -> example[param.name] = 1
                CommonType.LONG -> example[param.name] = 2
                CommonType.FLOAT -> example[param.name] = 125.90
                CommonType.ARRAY -> example[param.name] = JSONArray()
                CommonType.OBJECT -> {
                    val children = JSONObject(true)
                    param.children?.forEach { putParamExample(it, children) }
                    example[param.name] = children
                }
                CommonType.ARRAY_STRING -> example[param.name] = arrayJsonOf("yyyyyyy", "zzzzzzz")
                CommonType.ARRAY_BOOL -> example[param.name] = arrayJsonOf(false, true)
                CommonType.ARRAY_BYTE -> example[param.name] = arrayJsonOf(3, 1)
                CommonType.ARRAY_INT -> example[param.name] = arrayJsonOf(2, 5)
                CommonType.ARRAY_LONG -> example[param.name] = arrayJsonOf(15680, 432771)
                CommonType.ARRAY_FLOAT -> example[param.name] = arrayJsonOf(251.25, 180.07)
                CommonType.ARRAY_OBJECT -> {
                    val children1 = JSONObject(true)
                    param.children?.forEach { putParamExample(it, children1) }
                    val children2 = JSONObject(true)
                    param.children?.forEach { putParamExample(it, children2) }
                    example[param.name] = arrayJsonOf(children1, children2)
                }
            }
        }

        fun requestAppend(prefix: String, builder: StringBuilder, param: ApiParam) {
            builder.append("| $prefix${param.name} | ${param.type.value} | ${param.required} | ${param.maxLength ?: ""} | ${param.description ?: ""} | \n")
            param.children?.forEach {
                requestAppend(
                    if (prefix == "") "└─" else "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$prefix",
                    builder,
                    it
                )
            }
        }

        fun responseAppend(prefix: String, builder: StringBuilder, param: ApiParam) {
            builder.append("| $prefix${param.name} | ${param.type.value} | ${param.maxLength ?: ""} | ${param.description ?: ""} | \n")
            param.children?.forEach {
                responseAppend(
                    if (prefix == "") "└─" else "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$prefix",
                    builder,
                    it
                )
            }
        }

    }


}