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
    var requestParams: List<ApiParam>?,
    /** 响应返回值入参 */
    var responseParams: List<ApiParam>?,
) {

    val fileName: String by lazy {
        url.substring(8).run {
            "${substring(indexOf("/") + 1).splitToSmallHump("/")}.md"
        }
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

    inline fun initRequestHeader(init: ApiDetail.() -> String) = apply { requestHeader = init() }

    inline fun initRequestBody(init: ApiDetail.() -> String) = apply { requestBody = init() }

    inline fun initRequestExample(init: ApiDetail.() -> String) = apply { requestExample = init() }

    inline fun initResponseBody(init: ApiDetail.() -> String) = apply { responseBody = init() }

    inline fun initResponseExample(init: ApiDetail.() -> String) = apply { responseExample = init() }

}

/**
 * MBS 事件结构
 */
data class MbsEvent(
    /** 事件名称 */
    val name: String,
    /** 事件概述  */
    var description: String?,
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
)

/**
 * MBS 事件结构扩展，用于填充模版
 */
data class MbsEventDetail(
    val mbsEvent: MbsEvent,
    var messageBody: String? = null,
    var messageExample: String? = null,
) {

    inline fun initMessageBody(init: MbsEventDetail.() -> String) = apply { messageBody = init() }

    inline fun initMessageExample(init: MbsEventDetail.() -> String) = apply { messageExample = init() }

}

inline fun apiDetail(api: Api, init: ApiDetail.() -> Unit) = ApiDetail(api).apply { init() }

inline fun mbsEventDetail(mbsEvent: MbsEvent, init: MbsEventDetail.() -> Unit) = MbsEventDetail(mbsEvent).apply { init() }