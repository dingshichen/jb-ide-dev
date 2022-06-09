package cn.uniondrug.dev

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * 项目
 */
data class ProjectDTO(
    val id: String,
    val name: String,
    val spaceId: String,
)

class ProjectService {

    /**
     * 获取空间里的项目
     */
    fun listProjectBySpace(host: String, token: String, spaceId: String): List<ProjectDTO> {
        val body = doGet("$host/space/project/list?spaceId=$spaceId", token)
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val result: Result<List<ProjectDTO>> = gson.fromJson(body, object : TypeToken<Result<List<ProjectDTO>>>() {}.type!!)
        if (result.isError()) {
            throw ProjectException("查询项目失败：${result.msg}")
        }
        return result.data
    }
}