package cn.uniondrug.dev

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * 项目
 */
data class TornaProjectDTO(
    val id: String,
    val name: String,
    val spaceId: String,
) {
    override fun toString() = name
}

class TornaProjectService {

    /**
     * 获取空间里的项目
     */
    fun listProjectBySpace(token: String, spaceId: String, loginFailBack: (() -> String)? = null): List<TornaProjectDTO> {
        val body = doGetTorna("/space/project/list?spaceId=$spaceId", token)
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val tornaResult: TornaResult<List<TornaProjectDTO>> = gson.fromJson(body, object : TypeToken<TornaResult<List<TornaProjectDTO>>>() {}.type)
        if (tornaResult.isLoginError()) {
            loginFailBack?.let {
                return listProjectBySpace(it(), spaceId)
            }
            throw LoginException(tornaResult.msg)
        }
        if (tornaResult.isError()) {
            throw ProjectException("查询项目失败：${tornaResult.msg}")
        }
        return tornaResult.data!!
    }
}