package cn.uniondrug.dev

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * 登录接口入参
 */
data class UserLogin(
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

class UserService {

    /**
     * 登录
     */
    fun login(username: String, password: String): String {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val userLogin = UserLogin(username, password)
        val body = doPost("/system/login", gson.toJson(userLogin))
        val result: Result<LoginResult> = gson.fromJson(body, object : TypeToken<Result<LoginResult>>() {}.type!!)
        if (result.isError()) {
            throw LoginException("登录失败：${result.msg}")
        }
        return result.data!!.token
    }

}