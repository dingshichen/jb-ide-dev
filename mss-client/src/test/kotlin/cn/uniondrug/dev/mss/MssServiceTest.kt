package cn.uniondrug.dev.mss

import cn.uniondrug.dev.ConsulJavaService

class MssServiceTest {

//    @Test
    fun printProperties() {
        val serverUrlExpress = "\${key}/test/"
        val path = "/get/url"


    }

//    @Test
    fun replaceValue() {
        val r1 = RPCResource("\${api.take.module.center.url}", "/test/get", "0")
        val r2 = RPCResource("\${api.take.module.center.url}/", "/test/get", "0")
        val r3 = RPCResource("\${api.take.module.center.url}/test", "/get", "0")
        val r4 = RPCResource("\${api.take.module.center.url}/test", "get", "0")
        val r5 = RPCResource("\${api.take.module.center.url}/test/", "get", "0")
        val r6 = RPCResource("\${api.take.module.center.url}", "test/get", "0")
        val r7 = RPCResource("http://take-module.\${api_host}", "test/get", "0")
        val consulService = ConsulJavaService()
        consulService.getApplicationData().run {
            r1.replaceValue(this)
            r2.replaceValue(this)
            r3.replaceValue(this)
            r4.replaceValue(this)
            r5.replaceValue(this)
            r6.replaceValue(this)
            r7.replaceValue(this)
            println("r1 服务地址为： ${r1.serverUrlExpress}  路径为： ${r1.path}")
            println("r2 服务地址为： ${r2.serverUrlExpress}  路径为： ${r2.path}")
            println("r3 服务地址为： ${r3.serverUrlExpress}  路径为： ${r3.path}")
            println("r4 服务地址为： ${r4.serverUrlExpress}  路径为： ${r4.path}")
            println("r5 服务地址为： ${r5.serverUrlExpress}  路径为： ${r5.path}")
            println("r6 服务地址为： ${r6.serverUrlExpress}  路径为： ${r6.path}")
            println("r7 服务地址为： ${r7.serverUrlExpress}  路径为： ${r7.path}")
        }
    }
}