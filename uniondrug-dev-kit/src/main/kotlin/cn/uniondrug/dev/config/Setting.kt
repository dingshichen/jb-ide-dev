/** @author dingshichen */
package cn.uniondrug.dev.config

import cn.uniondrug.dev.ui.DocSettingForm
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import java.awt.event.ItemEvent
import javax.swing.JComponent
import javax.swing.JFrame


@State(name = "UniondrugDevKitDocSettingService", storages = [Storage("UniondrugDevKitDocSetting.xml")])
class DocSetting : PersistentStateComponent<DocSetting.TornaState> {

    companion object {
        fun getInstance(project: Project): DocSetting = project.getService(DocSetting::class.java)
    }

    private var state = TornaState()

    override fun getState(): TornaState {
        return state
    }

    override fun loadState(state: TornaState) {
        this.state = state
    }

    data class TornaState(
        var domain: String? = null,
        var url: String? = null,
        var username: String? = null,
    )

}

class TornaPasswordService {

    companion object {
        const val KEY = "Torna"
        fun getInstance(project: Project): TornaPasswordService = project.getService(TornaPasswordService::class.java)
    }

    /**
     * 存储凭证
     */
    fun setPassword(password: String) {
        createCredentialAttributes().apply {
            Credentials(KEY, password).let {
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
     * 创建凭证
     */
    private fun createCredentialAttributes() = CredentialAttributes(generateServiceName("Uniondrug Torna Passphrase", KEY), KEY)

}

class DocSettingConfigurable(
    private val project: Project
) : SearchableConfigurable {

    private lateinit var docSettingForm: DocSettingForm

    override fun getId(): @NonNls String {
        return "uniondrug.dev.kit.DocSettingConfigurable"
    }

    override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return "Doc Settings"
    }

    override fun createComponent(): JComponent? {
        docSettingForm = DocSettingForm(project)
        return docSettingForm.rootPanel
    }

    override fun isModified(): Boolean {
        return docSettingForm.isModified
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        docSettingForm.apply()
    }

    override fun reset() {
        docSettingForm.reset()
    }
}

class Open: JFrame() {

    var box = ComboBox(arrayOf(null, "南京", "上海", "合肥"))

    init {
        add(box)
        // 添加此事件，当发生选择变更时，会出发两次，一个是原选中值触发 DESELECTED ，另一个是新选中的值触发 SELECTED (null 元素不触发)
        box.addItemListener {
            if (it.stateChange == ItemEvent.SELECTED) {
                println("${it.item} 被选中")
            }
        }
    }

    fun clear() {
        box.removeAllItems()
    }

    fun addNewItems() {
        box.addItem("广州")
        box.addItem("北京")
        box.addItem("重庆")
    }
}