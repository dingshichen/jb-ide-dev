package cn.uniondrug.dev.mss

import cn.uniondrug.dev.PagingBody
import cn.uniondrug.dev.Result
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

data class GetProjectCmd(
    val projectCode: String,
    val workerName: String,
)

/**
 * 项目，字段很多，就取一个
 */
data class MssProjectDTO(
    val id: Int
)

class MssProjectService {

    /**
     * 查询项目 ID
     */
    fun getProjectId(token: String, projectCode: String, workerName: String) = getProject(token, projectCode, workerName)?.id

    /**
     * 查询项目
     */
    fun getProject(token: String, projectCode: String, workerName: String): MssProjectDTO? {
        val gson = GsonBuilder().create()
        val cmd = GetProjectCmd(projectCode, workerName)
        val body = doPostMSS("/project/my/project", gson.toJson(cmd), token)
        val result: Result<PagingBody<MssProjectDTO>> = gson.fromJson(body, object : TypeToken<Result<PagingBody<MssProjectDTO>>>() {}.type)
        if (result.isFail()) throw MssException("查询项目失败：${result.error}")
        return result.data.body.takeIf { it.isNotEmpty() }?.get(0)
    }

}