/**
 * 通知
 */
package cn.uniondrug.dev.notifier

import com.intellij.notification.BrowseNotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import javax.swing.Icon

private val NOTIFIER = NotificationGroupManager.getInstance()
    .getNotificationGroup("Uniondrug.Notification.Group")

fun notifyInfo(project: Project, content: String, action: BrowseNotificationAction? = null) {
    NOTIFIER.createNotification(content, NotificationType.INFORMATION).run {
        action?.let {
            addAction(it)
        }
        notify(project)
    }
}

fun notifyWarn(project: Project, content: String) =
    NOTIFIER.createNotification(content, NotificationType.WARNING).notify(project)

fun notifyError(project: Project, content: String) =
    NOTIFIER.createNotification(content, NotificationType.ERROR).notify(project)

fun notifyStartup(project: Project, icon: Icon) {
    NOTIFIER.createNotification("使用方法在 Wiki <br> 如果遇到麻烦、或者有什么需求和建议，可以在群里联系开发者～～", NotificationType.INFORMATION)
        .setTitle("药联 Java 开发者工具")
        .setIcon(icon)
        .addAction(BrowseNotificationAction("Wiki", "http://wiki.turboradio.cn/pages/viewpage.action?pageId=32968250"))
        .notify(project)
}