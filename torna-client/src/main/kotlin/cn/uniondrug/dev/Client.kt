package cn.uniondrug.dev

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

/**
 * 接口值包装结构
 */
data class Result<T>(
    val code: String,
    val msg: String,
    var data: T? = null,
) {

    fun isError() = code != "0"
}

/**
 * GET
 */
fun doGet(url: String, token: String): String {
    val request = HttpRequest.newBuilder()
        .uri(URI.create(url))
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
fun doPost(url: String, body: String, token: String? = null): String {
    val builder = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .timeout(Duration.ofSeconds(2L))
    token?.let {
        builder.header("Authorization", "Bearer $token")
    }
    val client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(2L))
        .build()
    val response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString())
    return response.body()
}