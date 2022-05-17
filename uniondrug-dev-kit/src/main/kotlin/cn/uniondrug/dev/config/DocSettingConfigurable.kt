package cn.uniondrug.dev.config

import cn.uniondrug.dev.ui.DocSettingForm
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import javax.swing.JComponent

/**
 * @author dingshichen
 * @date 2022/4/11
 */
class DocSettingConfigurable(
    private val project: Project
) : SearchableConfigurable {

    private var docSettingForm: DocSettingForm? = null

    override fun getId(): @NonNls String {
        return "uniondrug.dev.kit.DocSettingConfigurable"
    }

    override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return "Doc Settings"
    }

    override fun createComponent(): JComponent? {
        docSettingForm = DocSettingForm(project)
        return docSettingForm?.rootPanel
    }

    override fun isModified(): Boolean {
        return docSettingForm!!.isModified
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        docSettingForm?.apply()
    }

    override fun reset() {
        docSettingForm?.reset()
    }
}