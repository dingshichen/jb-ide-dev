package cn.uniondrug.dev

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * 模块
 */
data class TornaModuleDTO(
    val id: String,
    val name: String,
    val projectId: String,
) {
    override fun toString() = name
}

class TornaModuleService {

    /**
     * 获取项目里的模块
     */
    fun listModuleByProject(token: String, projectId: String, loginFailBack: (() -> String)? = null): List<TornaModuleDTO> {
        val body = doGetTorna("/module/list?projectId=$projectId", token)
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val tornaResult: TornaResult<List<TornaModuleDTO>> = gson.fromJson(body, object : TypeToken<TornaResult<List<TornaModuleDTO>>>() {}.type)
        if (tornaResult.isLoginError()) {
            loginFailBack?.let {
                return listModuleByProject(it(), projectId)
            }
            throw LoginException(tornaResult.msg)
        }
        if (tornaResult.isError()) {
            throw ProjectException("查询项目失败：${tornaResult.msg}")
        }
        return tornaResult.data!!
    }
}