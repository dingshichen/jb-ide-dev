/** @author dingshichen */
package cn.uniondrug.dev

class ConsulJavaServiceTest {

//    @Test
    fun getApplicationData() {
        val service = ConsulJavaService()
        val value2 = service.getStringValue("api.member.url")
        println(value2)
    }
}