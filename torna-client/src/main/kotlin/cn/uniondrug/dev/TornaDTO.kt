/** @author dingshichen */
package cn.uniondrug.dev

/**
 * API 接口
 */
data class Api(
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
    var requestParams: ArrayList<ApiParam>?,
    /** 响应返回值入参 */
    var responseParams: ArrayList<ApiParam>?,
)

/**
 * API 接口参数
 */
data class ApiParam(
    /** 字段名称  */
    val name: String,
    /** 字段类型   */
    val type: String,
    /** 是否必须，1：是，0：否  */
    val required: Byte,
    /** 最大长度  */
    var maxLength: String? = null,
    /** 描述  */
    var description: String? = null,
    /** 父节点  */
    var parentId: String? = null,
    /** 子节点  */
    var children: List<ApiParam>? = null,
)

/**
 * 扩展，用于填充模版
 */
data class ApiDetail(
    val api: Api,
    var requestHeader: String? = null,
    var requestBody: String? = null,
    var requestExample: String? = null,
    var responseBody: String? = null,
    var responseExample: String? = null,
) {

    fun initRequestHeader(init: ApiDetail.() -> String) {
        requestHeader = init()
    }

    fun initRequestBody(init: ApiDetail.() -> String) {
        requestBody = init()
    }

    fun initRequestExample(init: ApiDetail.() -> String) {
        requestExample = init()
    }

    fun initResponseBody(init: ApiDetail.() -> String) {
        responseBody = init()
    }

    fun initResponseExample(init: ApiDetail.() -> String) {
        responseExample = init()
    }


}