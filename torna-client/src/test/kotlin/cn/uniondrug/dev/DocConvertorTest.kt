package cn.uniondrug.dev

import com.alibaba.fastjson.JSONObject
import kotlin.test.Test

/**
 * @author dingshichen
 * @date 2022/5/20
 */
class DocConvertorTest {

    @Test fun putParamExample() {
        val example = JSONObject(true)
        val param = ApiParam(
            "orderNo",
            "String",
            1,
            "16",
            "订单号"
        )
        DocConvertor.putParamExample(param, example)
        println(example)
    }

    @Test fun buildJsonString() {
        val jsonString = DocConvertor.buildJsonString {
            val child = JSONObject(true)
            child["orderNo"] = "123456"
            this["order"] = child
        }
        println(jsonString)
    }
}