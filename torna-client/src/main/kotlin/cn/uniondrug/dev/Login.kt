package cn.uniondrug.dev

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * 登录接口入参
 */
data class TornaUserLogin(
    val username: String,
    val password: String
) {
    // 固定来源
    val source = "ldap"
}

/**
 * 登录接口出参
 */
data class LoginResult(
    val token: String,
    val status: Int,
)

class TornaUserService {

    /**
     * 登录
     */
    fun login(username: String, password: String): String {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val tornaUserLogin = TornaUserLogin(username, password)
        val body = doPostTorna("/system/login", gson.toJson(tornaUserLogin))
        val tornaResult: TornaResult<LoginResult> = gson.fromJson(body, object : TypeToken<TornaResult<LoginResult>>() {}.type!!)
        if (tornaResult.isError()) {
            throw LoginException("登录失败：${tornaResult.msg}")
        }
        return tornaResult.data!!.token
    }

}