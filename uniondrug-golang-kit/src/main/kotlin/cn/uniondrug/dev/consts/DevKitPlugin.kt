package cn.uniondrug.dev.consts

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader

/**
 * @author dingshichen
 * @date 2022/4/5
 */
object DevKitPlugin {

    const val NAME = "Uniondrug Golang Kit"

    const val ID = "cn.uniondrug.uniondrug-golang-kit"

}

/**
 * 图标
 */
object DevIcons {

    @JvmField
    val DOC_VIEW = AllIcons.CodeWithMe.CwmPermissionView

    @JvmField
    val UNIONDRUG_LIGHT = IconLoader.getIcon("META-INF/pluginIcon.svg", DevIcons::class.java)

    @JvmField
    val UNIONDRUG_DARK = IconLoader.getIcon("META-INF/pluginIcon_dark.svg", DevIcons::class.java)
}