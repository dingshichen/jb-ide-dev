/** @author dingshichen */
package cn.uniondrug.dev

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

const val DINGTALK_WEBHOOK = "https://oapi.dingtalk.com/robot/send?access_token=620b7de6fd7fe5b81db8329c769d9cfd8433389596742f335bfbc599027455c4"

data class Issue(
    val author: String,
    val content: String,
)

interface IssueService {

    fun postIssue(issue: Issue)
}

class IssueServiceImpl : IssueService {

    override fun postIssue(issue: Issue) {
        val gson = GsonBuilder().create()
        val data = RobotMarkdownMessage(MarkdownBody("意见", "### ${issue.author} 说：\n\n ${issue.content}"))
        val request = HttpRequest.newBuilder()
            .uri(URI.create(DINGTALK_WEBHOOK))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(data)))
            .timeout(Duration.ofSeconds(5L))
            .build()
        val client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5L))
            .build()
        return client.send(request, HttpResponse.BodyHandlers.ofString()).run {
            val result = gson.fromJson<ResponseData>(body(), object : TypeToken<ResponseData>() {}.type)
            if (result.isFail()) throw PostDingTalkFailException(result.errmsg)
        }
    }

}