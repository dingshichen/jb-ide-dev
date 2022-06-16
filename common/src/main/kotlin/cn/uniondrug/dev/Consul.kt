/** @author dingshichen */
package cn.uniondrug.dev

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.Charset
import java.time.Duration
import java.util.*

// Consul 测试环境
const val TEST_CONSUL = "http://192.168.3.209:8500"

/**
 * Consul KV 返回值结构体
 */
data class ConsulKVResult (
    val Key: String,
    val Value: String,
    val Session: String,
)

/**
 * 利用测试环境的 Consul
 */
interface ConsulService {

    /**
     * 获取配置
     */
    fun getStringValue(key: String): String?

    /**
     * 获取公共配置
     */
    fun getApplicationData(): Properties

}

/**
 * Consul Java 服务
 */
class ConsulJavaService : ConsulService {

    override fun getStringValue(key: String) = getApplicationData().run {
        getProperty(key)?.replace("\${api_host}", "uniondrug.cn")
    }

    override fun getApplicationData(): Properties {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("$TEST_CONSUL/v1/kv/config/application/data"))
            .GET()
            .timeout(Duration.ofSeconds(2L))
            .build()
        val client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(2L))
            .build()
        val gson = GsonBuilder().create()
        return client.send(request, HttpResponse.BodyHandlers.ofString()).run {
            val result = gson.fromJson<List<ConsulKVResult>>(body(), object : TypeToken<List<ConsulKVResult>>() {}.type)
            val text = String(Base64.getDecoder().decode(result[0].Value))
            Properties().apply {
                load(text.byteInputStream(Charset.defaultCharset()))
            }
        }
    }

}