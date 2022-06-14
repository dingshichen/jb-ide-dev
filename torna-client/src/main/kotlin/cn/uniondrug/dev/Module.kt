package cn.uniondrug.dev

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * 模块
 */
data class ModuleDTO(
    val id: String,
    val name: String,
    val projectId: String,
) {
    override fun toString() = name
}

class ModuleService {

    /**
     * 获取项目里的模块
     */
    fun listModuleByProject(token: String, projectId: String): List<ModuleDTO> {
        val body = doGet("/module/list?projectId=$projectId", token)
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val result: Result<List<ModuleDTO>> = gson.fromJson(body, object : TypeToken<Result<List<ModuleDTO>>>() {}.type!!)
        if (result.isError()) {
            throw ProjectException("查询项目失败：${result.msg}")
        }
        return result.data!!
    }
}