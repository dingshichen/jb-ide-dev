package cn.uniondrug.dev.dialog

import cn.uniondrug.dev.ui.TornaIndexForm
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.Action
import javax.swing.JComponent

/**
 * Torna 首页对话框
 * @author dingshichen
 * @date 2022/6/10
 */
class TornaIndexDialog(
    private val project: Project,
    private val targetFolder: String,
) : DialogWrapper(true) {

    lateinit var tornaIndexForm: TornaIndexForm

    init {
        title = "Torna"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        tornaIndexForm = TornaIndexForm(project, targetFolder)
        return tornaIndexForm.rootPanel
    }

    override fun createActions(): Array<Action> {
        setOKButtonText("上传")
        setCancelButtonText("取消")
        return arrayOf(okAction, cancelAction)
    }

    fun getSpaceId(): String = tornaIndexForm.spaceId

    fun getProjectId(): String = tornaIndexForm.projectId

    fun getModuleId(): String = tornaIndexForm.moduleId

    fun getFolderId(): String? = tornaIndexForm.folderId

    fun refreshFolderId() {
        tornaIndexForm.refreshFolderId()
    }

}

