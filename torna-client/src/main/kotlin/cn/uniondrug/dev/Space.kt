package cn.uniondrug.dev

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * 空间
 */
data class SpaceDTO(
    val id: String,
    val name: String,
) {
    override fun toString() = name
}

class SpaceService {

    /**
     * 查询我所在的空间
     */
    fun listMySpace(token: String): List<SpaceDTO> {
        val body = doGet("/space/list", token)
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val result: Result<List<SpaceDTO>> = gson.fromJson(body, object : TypeToken<Result<List<SpaceDTO>>>() {}.type!!)
        if (result.isError()) {
            throw SpaceException("查询空间失败：${result.msg}")
        }
        return result.data!!
    }

}