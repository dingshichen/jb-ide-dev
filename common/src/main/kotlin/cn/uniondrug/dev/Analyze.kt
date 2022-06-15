/** @author dingshichen */
package cn.uniondrug.dev

const val MBS_SERVICE_1 = "cn.uniondrug.mbs.service.MsgService"
const val MBS_SERVICE_2 = "cn.uniondrug.mbs.service.Msg2Service"

/**
 * 药联资源标识
 */
interface UniondrugResource

/**
 * 涉及接口资源
 */
class RPCResource : UniondrugResource {

    /* 服务地址 spring value 表达式 */
    lateinit var serverUrlExpress: String
    /* 接口路径 */
    lateinit var path: String
    /* 三方资源标识：0 内部、1 三方 */
    lateinit var thirdFlag: String

    fun serverUrlExpress(init: RPCResource.() -> String) {
        this.serverUrlExpress = init()
    }

    fun path(init: RPCResource.() -> String) {
        this.path = init()
    }

    fun thirdFlag(init: RPCResource.() -> String) {
        this.thirdFlag = init()
    }
}

fun rpcResource(init: RPCResource.() -> Unit): RPCResource {
    val rpcResource = RPCResource()
    rpcResource.init()
    return rpcResource
}

/**
 * 涉及 MBS 资源
 */
class MbsResource : UniondrugResource {

    /* 通道 */
    lateinit var channel: MbsChannel
    /* 主题 */
    lateinit var topic: String
    /* 标签 */
    lateinit var tag: String

    fun channel(init: MbsResource.() -> MbsChannel) {
        this.channel = init()
    }

    fun topic(init: MbsResource.() -> String) {
        this.topic = init()
    }

    fun tag(init: MbsResource.() -> String) {
        this.tag = init()
    }
}

fun mbsResource(init: MbsResource.() -> Unit): MbsResource {
    val mbsResource = MbsResource()
    mbsResource.init()
    return mbsResource
}

/**
 * MBS 通道
 */
enum class MbsChannel(
    val value: String,
) {
    JAVA_MBS_1("JAVA_MBS1"),
    JAVA_MBS_2("JAVA_MBS2"),
    PHP_MBS("PHP_MBS"),
}

/**
 * 获取 MBS 通道
 */
fun ofMbsChannel(className: String) = when (className) {
    MBS_SERVICE_1 -> MbsChannel.JAVA_MBS_1
    MBS_SERVICE_2 -> MbsChannel.JAVA_MBS_2
    else -> null
}