package cn.uniondrug.dev.mss

import cn.uniondrug.dev.PagingBody
import cn.uniondrug.dev.Result
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

data class PageApiCmd(
    val projectId: Int,
) {
    val page = 1
    val limit = 1000
}

/**
 * 接口的字段有很多，先留这几个
 */
data class MssApiDTO(
    val id: Int,
    val projectId: Int,
    val apiName: String,
    val apiUrl: String,
)

data class PageByApiCmd(
    val projectId: Int,
    val projectApiId: Int,
) {
    val page = 1
    val limit = 1000
}

data class ApiCallDTO(
    val id: Int,
    val domain: String,
    val url: String,
)

data class ApiMbsDTO(
    val id: Int,
    val topic: String,
    val tag: String,
)

data class AddMbsCmd(
    val projectId: Int,
    val projectApiId: Int,
    val topic: String,
    val tag: String,
    val mType: String,
)

data class AddCallCmd(
    val projectId: Int,
    val projectApiId: Int,
    val domain: String,
    val url: String,
    val thirdFlag: String,
)

class MssApiService {

    /**
     * 根据项目查询所有 API
     */
    fun listApiByProject(token: String, projectId: Int): List<MssApiDTO> {
        val gson = GsonBuilder().create()
        val cmd = PageApiCmd(projectId)
        val body = doPostMSS("/api/page", gson.toJson(cmd), token)
        val result: Result<PagingBody<MssApiDTO>> = gson.fromJson(body, object : TypeToken<Result<PagingBody<MssApiDTO>>>() {}.type)
        if (result.isFail()) throw MssException("查询项目 API 失败：${result.error}")
        return result.data.body
    }

    /**
     * 查询被 API 的调用接口
     */
    fun listCallByApi(token: String, projectId: Int, projectApiId: Int): List<ApiCallDTO> {
        val gson = GsonBuilder().create()
        val cmd = PageByApiCmd(projectId, projectApiId)
        val body = doPostMSS("/projectCallApi/paging", gson.toJson(cmd), token)
        val result: Result<PagingBody<ApiCallDTO>> = gson.fromJson(body, object : TypeToken<Result<PagingBody<ApiCallDTO>>>() {}.type)
        if (result.isFail()) throw MssException("查询被 API 调用的接口列表失败：${result.error}")
        return result.data.body
    }

    /**
     * 查询发送的 MBS
     */
    fun listMbs(token: String, projectId: Int, projectApiId: Int): List<ApiMbsDTO> {
        val gson = GsonBuilder().create()
        val cmd = PageByApiCmd(projectId, projectApiId)
        val body = doPostMSS("/projectApiMessage/paging", gson.toJson(cmd), token)
        val result: Result<PagingBody<ApiMbsDTO>> = gson.fromJson(body, object : TypeToken<Result<PagingBody<ApiMbsDTO>>>() {}.type)
        if (result.isFail()) throw MssException("查询 API 发送的 MBS 失败：${result.error}")
        return result.data.body
    }

    /**
     * 新增被此 API 调用的接口
     */
    fun addCall(token: String, projectId: Int, projectApiId: Int, domain: String, url: String, thirdFlag: String) {
        val gson = GsonBuilder().create()
        val cmd = AddCallCmd(projectId, projectApiId, domain, url, thirdFlag)
        val body = doPostMSS("/projectCallApi/create", gson.toJson(cmd), token)
        val result: Result<*> = gson.fromJson(body, object : TypeToken<Result<*>>() {}.type)
        if (result.isFail()) throw MssException("新增被此 API 调用的接口失败：${result.error}")
    }

    /**
     * 新增此 API 发送的MBS
     */
    fun addMbs(token: String, projectId: Int, projectApiId: Int, topic: String, tag: String, mType: String) {
        val gson = GsonBuilder().create()
        val cmd = AddMbsCmd(projectId, projectApiId, topic, tag, mType)
        val body = doPostMSS("/projectApiMessage/create", gson.toJson(cmd), token)
        val result: Result<*> = gson.fromJson(body, object : TypeToken<Result<*>>() {}.type)
        if (result.isFail()) throw MssException("新增此 API 发送的 MBS 失败：${result.error}")
    }
}