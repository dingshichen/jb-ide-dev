package cn.uniondrug.dev.dialog

import cn.uniondrug.dev.LoginException
import cn.uniondrug.dev.notifier.notifyError
import cn.uniondrug.dev.ui.PushAllDocForm
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.Action
import javax.swing.JComponent

/**
 * 一键上传所有的弹窗
 */
class PushAllDocDialog(
    val project: Project
) : DialogWrapper(true) {

    lateinit var pushAllDocForm: PushAllDocForm

    init {
        title = "上传所有"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        pushAllDocForm = PushAllDocForm(project)
        return pushAllDocForm.rootPanel
    }

    override fun createActions(): Array<Action> {
        setOKButtonText("一把梭")
        setCancelButtonText("怂一波")
        return arrayOf(okAction, cancelAction)
    }

    fun spaceId(): String = pushAllDocForm.spaceId

    fun projectId(): String = pushAllDocForm.projectId

    fun moduleId(): String = pushAllDocForm.moduleId

}

fun showAndGetPushAllDocDialog(project: Project, ok: PushAllDocDialog.() -> Unit) {
    try {
        PushAllDocDialog(project).run { if (showAndGet()) this.ok() }
    } catch (e: LoginException) {
        notifyError(project, "Torna 登陆失败，请确认 AD 账号密码是否正确！")
    }
}