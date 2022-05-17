/**
 * 通知
 */
package cn.uniondrug.dev.notifier

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

private val NOTIFIER = NotificationGroupManager.getInstance()
    .getNotificationGroup("Uniondrug.Notification.Group")

fun notifyInfo(project: Project, content: String) =
    NOTIFIER.createNotification(content, NotificationType.INFORMATION).notify(project)

fun notifyWarn(project: Project, content: String) =
    NOTIFIER.createNotification(content, NotificationType.WARNING).notify(project)

fun notifyError(project: Project, content: String) =
    NOTIFIER.createNotification(content, NotificationType.ERROR).notify(project)
