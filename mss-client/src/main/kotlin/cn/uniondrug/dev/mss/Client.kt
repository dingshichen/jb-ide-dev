package cn.uniondrug.dev.mss

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

/**
 * 固定 MSS 域名
 */
const val UNIONDRUG_MSS_URL = "http://pm-dev-manage.uniondrug.cn"

/**
 * POST
 */
fun doPostMSS(path: String, body: String, token: String): String {
    val builder = HttpRequest.newBuilder()
        .uri(URI.create("$UNIONDRUG_MSS_URL$path"))
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer $token")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .timeout(Duration.ofSeconds(2L))
    val client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(2L))
        .build()
    val response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString())
    return response.body()
}
