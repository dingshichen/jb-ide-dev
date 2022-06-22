package cn.uniondrug.dev.dialog

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

    var pushAllDocForm: PushAllDocForm? = null

    init {
        title = "上传所有"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        pushAllDocForm = PushAllDocForm(project)
        return pushAllDocForm!!.rootPanel
    }

    override fun createActions(): Array<Action> {
        setOKButtonText("一把梭")
        setCancelButtonText("怂一波")
        return arrayOf(okAction, cancelAction)
    }

    fun projectId() = pushAllDocForm!!.projectId

    fun moduleId() = pushAllDocForm!!.moduleId

}