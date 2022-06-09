/** @author dingshichen */
package cn.uniondrug.dev

import com.alibaba.fastjson.JSONObject
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

fun doPost(url: String, body: String): String {
    val request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .timeout(Duration.ofSeconds(2L))
        .build()

    val client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(2L))
        .build()

    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    return response.body()
}

class TestRequest {

//    @Test
    fun doPost() {
        val json = JSONObject(mapOf(
            "ipackageNo" to "DIC2022060200073"
        ))
        val responseBody = doPost("http://jm-insure.turboradio.cn/ipackage/direct/get", json.toString())
        println(responseBody)
    }
}