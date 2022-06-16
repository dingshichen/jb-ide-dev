/** @author dingshichen */
package cn.uniondrug.dev.mss

import java.util.*

const val MBS_SERVICE_1 = "cn.uniondrug.mbs.service.MsgService"
const val MBS_SERVICE_2 = "cn.uniondrug.mbs.service.Msg2Service"

/**
 * 药联资源标识
 */
interface UniondrugResource

/**
 * 自己的接口资源
 */
data class OwnResource(
    /* 接口名称 */
    var name: String? = null,
    /* 路径 */
    var path: String? = null,
) : UniondrugResource {

    fun name(init: OwnResource.() -> String) {
        this.name = init()
    }

    fun path(init: OwnResource.() -> String) {
        this.path = init()
    }

}

/**
 * 涉及接口资源
 */
data class RPCResource(
    /* 服务地址 spring value 表达式 */
    var serverUrlExpress: String? = null,
    /* 接口路径 */
    var path: String? = null,
    /* 三方资源标识：0 内部、1 三方 */
    var thirdFlag: String? = null,
) : UniondrugResource {

    fun serverUrlExpress(init: RPCResource.() -> String) {
        this.serverUrlExpress = init()
    }

    fun path(init: RPCResource.() -> String) {
        this.path = init()
    }

    fun thirdFlag(init: RPCResource.() -> String) {
        this.thirdFlag = init()
    }
    
    fun replaceValue(properties: Properties) {
        var realServerUrl = ""
        val a = serverUrlExpress!!.indexOf("\${")
        val b = serverUrlExpress!!.lastIndexOf("}")
        realServerUrl = if (a != -1 && b != -1) {
            val value = properties.getProperty(serverUrlExpress!!.substring(a + 2, b)).replaceHost()
            if (a == 0 && b == serverUrlExpress!!.length - 1) {
                value
            } else if (a == 0 && b < serverUrlExpress!!.length - 1) {
                value + serverUrlExpress!!.substring(b + 1)
            } else if (a > 0 && b < serverUrlExpress!!.length - 1) {
                serverUrlExpress!!.substring(0, a) + value + serverUrlExpress!!.substring(b + 1)
            } else {
                serverUrlExpress!!.substring(0, a) + value
            }
        } else {
            serverUrlExpress!!.replaceHost()
        }
        if (!realServerUrl.endsWith("uniondrug.cn") && !realServerUrl.endsWith("uniondrug.cn/")) {
            val c = realServerUrl.indexOf("uniondrug.cn")
            serverUrlExpress = realServerUrl.substring(0, c + 12)
            path = "/${realServerUrl.substring(c + 13)}/$path"
                .replace("///", "/")
                .replace("//", "/")
                .removeHttp()
        } else {
            serverUrlExpress = realServerUrl.replace("uniondrug.cn/", "uniondrug.cn")
            path = "/$path"
                .replace("///", "/")
                .replace("//", "/")
                .removeHttp()
        }
    }

    private fun String.replaceHost() = replace("\${api_host}", "uniondrug.cn").replace("turboradio.cn", "uniondrug.cn")

    private fun String.removeHttp() = replace("http://", "").replace("https://", "")
}

/**
 * 涉及 MBS 资源
 */
data class MbsResource(
    /* 通道 */
    var channel: MbsChannel? = null,
    /* 主题 */
    var topic: String? = null,
    /* 标签 */
    var tag: String? = null,
) : UniondrugResource {

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

fun ownResource(init: OwnResource.() -> Unit): OwnResource {
    val ownResource = OwnResource()
    ownResource.init()
    return ownResource
}

fun rpcResource(init: RPCResource.() -> Unit): RPCResource {
    val rpcResource = RPCResource()
    rpcResource.init()
    return rpcResource
}

fun mbsResource(init: MbsResource.() -> Unit): MbsResource {
    val mbsResource = MbsResource()
    mbsResource.init()
    return mbsResource
}

/**
 * 获取 MBS 通道
 */
fun ofMbsChannel(className: String) = when (className) {
    MBS_SERVICE_1 -> MbsChannel.JAVA_MBS_1
    MBS_SERVICE_2 -> MbsChannel.JAVA_MBS_2
    else -> null
}