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
                CommonType.STRING, CommonType.BYTE, CommonType.INT, CommonType.LONG, CommonType.FLOAT ->
                    example[param.name] = BaseDataTypeMockUtil.getValByTypeAndFieldName(param.type.value, param.name)
                CommonType.ARRAY -> example[param.name] = JSONArray()
                CommonType.BOOL -> example[param.name] = true
                CommonType.OBJECT -> {
                    val children = JSONObject(true)
                    param.children?.forEach { putParamExample(it, children) }
                    example[param.name] = children
                }
                CommonType.ARRAY_STRING, CommonType.ARRAY_BOOL, CommonType.ARRAY_BYTE, CommonType.ARRAY_INT,
                CommonType.ARRAY_LONG, CommonType.ARRAY_FLOAT -> example[param.name] =
                    arrayJsonOf(
                        BaseDataTypeMockUtil.getValByTypeAndFieldName(param.type.value, param.name),
                        BaseDataTypeMockUtil.getValByTypeAndFieldName(param.type.value, param.name)
                    )
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