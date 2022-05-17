package cn.uniondrug.dev.consts

import com.intellij.openapi.util.IconLoader

/**
 * @author dingshichen
 * @date 2022/4/5
 */
object DevKitPlugin {

    const val NAME = "Uniondrug Golang Kit"
    const val DOC_ICON = "/icons/star.svg"

}

/**
 * 图标
 */
object DevIcons {

    val DOC_VIEW = IconLoader.getIcon(DevKitPlugin.DOC_ICON, DevIcons::class.java)

}