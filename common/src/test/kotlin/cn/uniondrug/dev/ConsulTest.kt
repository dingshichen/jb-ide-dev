/** @author dingshichen */
package cn.uniondrug.dev

import kotlin.test.Test

class ConsulJavaServiceTest {

    @Test
    fun getApplicationData() {
        val service = ConsulJavaService()
//        val value = service.getApplicationData()["api.member.url"]
//        println(value)

        val value2 = service.getStringValue("api.member.url")
        println(value2)
    }
}