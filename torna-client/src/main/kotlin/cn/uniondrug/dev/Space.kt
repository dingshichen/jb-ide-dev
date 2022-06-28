package cn.uniondrug.dev

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * 空间
 */
data class TornaSpaceDTO(
    val id: String,
    val name: String,
) {
    override fun toString() = name
}

class TornaSpaceService {

    /**
     * 查询我所在的空间
     */
    fun listMySpace(token: String, loginFailBack: (() -> String)? = null): List<TornaSpaceDTO> {
        val body = doGetTorna("/space/list", token)
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val tornaResult: TornaResult<List<TornaSpaceDTO>> = gson.fromJson(body, object : TypeToken<TornaResult<List<TornaSpaceDTO>>>() {}.type)
        if (tornaResult.isLoginError()) {
            loginFailBack?.let {
                return listMySpace(it())
            }
            throw LoginException(tornaResult.msg)
        }
        if (tornaResult.isError()) {
            throw SpaceException("查询空间失败：${tornaResult.msg}")
        }
        return tornaResult.data!!
    }

}