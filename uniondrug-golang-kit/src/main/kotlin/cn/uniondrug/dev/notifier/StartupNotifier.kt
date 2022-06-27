package cn.uniondrug.dev.notifier

import cn.uniondrug.dev.consts.DevIcons
import cn.uniondrug.dev.consts.DevKitPlugin
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.EditorColorsUtil
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.ui.ColorUtil
import com.intellij.util.text.VersionComparatorUtil

/**
 * @author dingshichen
 * @date 2022/4/5
 */
class StartupNotifier : StartupActivity, DumbAware {

    private val pluginVersion = "${DevKitPlugin.NAME} last version"

    override fun runActivity(project: Project) {
        if (ApplicationManager.getApplication().isUnitTestMode) {
            return
        }
        val properties = PropertiesComponent.getInstance()
        val lastVersion = properties.getValue(pluginVersion)
        PluginManagerCore.getPlugin(PluginId.getId(DevKitPlugin.ID))?.let {
            if (lastVersion == null || VersionComparatorUtil.compare(lastVersion, it.version) < 0) {
                EditorColorsUtil.getGlobalOrDefaultColorScheme().run {
                    notifyStartup(project, if (ColorUtil.isDark(defaultBackground)) DevIcons.UNIONDRUG_DARK else DevIcons.UNIONDRUG_LIGHT)
                }
            }
            properties.setValue(pluginVersion, it.version)
        }
    }

}