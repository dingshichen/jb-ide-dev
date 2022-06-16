package cn.uniondrug.dev.config

import cn.uniondrug.dev.LoginException
import cn.uniondrug.dev.TornaUserService
import cn.uniondrug.dev.util.StringUtil
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project

/**
 * @author dingshichen
 * @date 2022/6/16
 */
class TornaKeyService {

    companion object {
        const val CREDENTIA_KEY = "Torna"
        const val TOKEN_KEY = "cn.uniondrug.dev.torna.token"
        fun getInstance(project: Project): TornaKeyService = project.getService(TornaKeyService::class.java)
    }

    /**
     * 存储凭证
     */
    fun setPassword(password: String) {
        createCredentialAttributes().apply {
            Credentials(CREDENTIA_KEY, password).let {
                PasswordSafe.instance.set(this, it)
            }
        }
    }

    /**
     * 获取凭证里的密码
     */
    fun getPassword() = createCredentialAttributes().run {
        PasswordSafe.instance.get(this)?.getPasswordAsString() ?: PasswordSafe.instance.getPassword(this)
    }

    /**
     * 获取 token
     */
    fun getToken(project: Project, docSetting: DocSetting): String {
        val username = docSetting.state.username
        val password = getPassword()
        if (StringUtil.isAnyEmpty(username, password)) {
            throw LoginException("获取不到正确的项目配置")
        }
        val properties = PropertiesComponent.getInstance()
        var token = properties.getValue(TOKEN_KEY)
        if (token.isNullOrBlank()) {
            val loginService = project.getService(TornaUserService::class.java)
            token = try {
                loginService.login(username!!, password!!)
            } catch (e: Exception) {
                throw LoginException("登陆失败：${e.message}")
            }
            properties.setValue(TOKEN_KEY, token)
        }
        return token!!
    }

    /**
     * 创建凭证
     */
    private fun createCredentialAttributes() = CredentialAttributes(generateServiceName("Uniondrug Torna Passphrase", CREDENTIA_KEY), CREDENTIA_KEY)

}