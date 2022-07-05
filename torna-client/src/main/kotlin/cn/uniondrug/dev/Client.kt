package cn.uniondrug.dev

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

/**
 * 固定 torna 域名
 */
const val UNIONDRUG_TORNA_URL = "https://ud-torna.uniondrug.cn"

/**
 * 接口值包装结构
 */
data class TornaResult<T>(
    val code: String,
    val msg: String,
    var data: T? = null,
) {

    fun isError() = code != "0"

    fun isLoginError() = code == "1000"
}

/**
 * GET
 */
fun doGetTorna(path: String, token: String): String {
    val request = HttpRequest.newBuilder()
        .uri(URI.create("$UNIONDRUG_TORNA_URL$path"))
        .GET()
        .header("Authorization", "Bearer $token")
        .timeout(Duration.ofSeconds(2L))
        .build()
    val client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(2L))
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofString())
    return response.body()
}

/**
 * POST
 */
fun doPostTorna(path: String, body: String, token: String? = null): String {
    val builder = HttpRequest.newBuilder()
        .uri(URI.create("$UNIONDRUG_TORNA_URL$path"))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .timeout(Duration.ofSeconds(8L))
    token?.let {
        builder.header("Authorization", "Bearer $token")
    }
    val client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(8L))
        .build()
    val response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString())
    return response.body()
}