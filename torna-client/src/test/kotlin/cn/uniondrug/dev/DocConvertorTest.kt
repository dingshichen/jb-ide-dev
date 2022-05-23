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
        val age = ApiParam(
            "age",
            "int",
            true,
            "2",
            "年龄"
        )
        DocConvertor.putParamExample(age, example)
        val orderNo = ApiParam(
            "orderNo",
            "string",
            true,
            "16",
            "订单号"
        )
        DocConvertor.putParamExample(orderNo, example)
        val policyNo = ApiParam(
            "policyNo",
            "string",
            true,
            "20",
            "保单号"
        )
        DocConvertor.putParamExample(policyNo, example)
        println(example)
    }

    @Test fun buildJsonString() {
        val example = JSONObject(true)
        val age = ApiParam(
            "age",
            "int",
            true,
            "2",
            "年龄"
        )
        DocConvertor.putParamExample(age, example)
        val orderNo = ApiParam(
            "orderNo",
            "string",
            true,
            "16",
            "订单号"
        )
        DocConvertor.putParamExample(orderNo, example)
        val policyNo = ApiParam(
            "policyNo",
            "string",
            true,
            "20",
            "保单号"
        )
        DocConvertor.putParamExample(policyNo, example)
        val jsonString = DocConvertor.buildJsonString {
            example
        }
        println(jsonString)
    }
}