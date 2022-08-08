package cn.uniondrug.dev

import java.net.URI
import java.net.http.*
import java.time.Duration

/**
 * 固定 torna Web 域名
 */
const val TORNA_WEB = "https://torna.uniondrug.cn"

/**
 * 固定 torna 服务端域名
 */
const val TORNA_SERVER = "https://ud-torna.uniondrug.cn"

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
        .uri(URI.create("$TORNA_SERVER$path"))
        .GET()
        .header("Authorization", "Bearer $token")
        .timeout(Duration.ofSeconds(5L))
        .build()
    val client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(5L))
        .build()
    val response = try {
        client.send(request, HttpResponse.BodyHandlers.ofString())
    } catch (e: HttpTimeoutException) {
        throw HttpTimeoutException("请求 Torna 超时，请检查网络或稍后重试，${e.localizedMessage}")
    } catch (e: HttpConnectTimeoutException) {
        throw HttpConnectTimeoutException("请求 Torna 超时，请检查网络或稍后重试，${e.localizedMessage}")
    }
    return response.body()
}

/**
 * POST
 */
fun doPostTorna(path: String, body: String, token: String? = null): String {
    val builder = HttpRequest.newBuilder()
        .uri(URI.create("$TORNA_SERVER$path"))
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
    val response = try {
        client.send(builder.build(), HttpResponse.BodyHandlers.ofString())
    } catch (e: HttpTimeoutException) {
        throw HttpTimeoutException("操作 Torna 超时，请检查网络或稍后重试，${e.localizedMessage}")
    } catch (e: HttpConnectTimeoutException) {
        throw HttpConnectTimeoutException("操作 Torna 超时，请检查网络或稍后重试，${e.localizedMessage}")
    }
    return response.body()
}