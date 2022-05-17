/** @author dingshichen */
package cn.uniondrug.dev.consts

import com.intellij.openapi.util.IconLoader

object DevKitPlugin {

    const val NAME = "Uniondrug Dev Kit"
    const val DOC_ICON = "/icons/star.svg"

}

/**
 * 图标
 */
object DevIcons {

    val DOC_VIEW = IconLoader.getIcon(DevKitPlugin.DOC_ICON, DevIcons::class.java)

}