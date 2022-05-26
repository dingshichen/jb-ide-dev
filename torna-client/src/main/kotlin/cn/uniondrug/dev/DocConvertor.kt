package cn.uniondrug.dev

import cn.uniondrug.dev.util.BaseDataTypeMockUtil
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
                        requestAppend("", it)
                    }
                }
            }
            initRequestExample {
                buildJsonString {
                    api.requestParams?.forEach {
                        putParamExample(it)
                    }
                }
            }
            initResponseBody {
                buildString {
                    append("|参数名|类型|最大长度|描述|\n|:-----|:-----|:-----|:-----|\n")
                    api.responseParams?.forEach {
                        responseAppend("", it)
                    }
                }
            }
            initResponseExample {
                buildJsonString {
                    api.responseParams?.forEach {
                        putParamExample(it)
                    }
                }
            }
        }

        private fun JSONObject.putParamExample(param: ApiParam) {
            when (param.type) {
                CommonType.STRING,
                CommonType.BYTE,
                CommonType.INT,
                CommonType.LONG,
                CommonType.FLOAT ->
                    this[param.name] = BaseDataTypeMockUtil.getValByTypeAndFieldName(param.type.value, param.name)
                CommonType.ARRAY -> this[param.name] = JSONArray()
                CommonType.BOOL -> this[param.name] = true
                CommonType.OBJECT -> this[param.name] = jsonObject {
                    param.children?.forEach {
                        putParamExample(it)
                    }
                }
                CommonType.ARRAY_STRING,
                CommonType.ARRAY_BOOL,
                CommonType.ARRAY_BYTE,
                CommonType.ARRAY_INT,
                CommonType.ARRAY_LONG,
                CommonType.ARRAY_FLOAT -> this[param.name] = arrayJsonOf(
                        BaseDataTypeMockUtil.getValByTypeAndFieldName(param.type.value, param.name),
                        BaseDataTypeMockUtil.getValByTypeAndFieldName(param.type.value, param.name)
                )
                CommonType.ARRAY_OBJECT -> this[param.name] = arrayJsonOf(
                    jsonObject {
                        param.children?.forEach {
                            putParamExample(it)
                        }
                    },
                    jsonObject {
                        param.children?.forEach {
                            putParamExample(it)
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

    }


}