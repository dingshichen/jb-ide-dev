package cn.uniondrug.dev.dialog

import cn.uniondrug.dev.Api
import cn.uniondrug.dev.ui.CreateFolderForm
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
    val project: Project,
    val api: Api,
) : DialogWrapper(true) {

    var tornaIndexForm: TornaIndexForm? = null

    init {
        title = "Torna"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        tornaIndexForm = TornaIndexForm(project, api)
        return tornaIndexForm!!.rootPanel
    }

    override fun createActions(): Array<Action> {
        setOKButtonText("上传")
        setCancelButtonText("取消")
        return arrayOf(okAction, cancelAction)
    }

    fun getSpaceId() = tornaIndexForm!!.spaceId

    fun getProjectId() = tornaIndexForm!!.projectId

    fun getModuleId() = tornaIndexForm!!.moduleId

    fun getFolderId() = tornaIndexForm!!.folderId
}

/**
 * 创建目录对话框
 */
class CreateFolderDialog(
    val project: Project
) : DialogWrapper(true) {

    init {
        title = "新建目录"
        init()
    }

    private var createFolderForm: CreateFolderForm? = null

    override fun createCenterPanel(): JComponent? {
        createFolderForm = CreateFolderForm(project)
        return createFolderForm!!.rootPanel
    }

    /**
     * 获取目录名
     */
    fun getFolder() = createFolderForm!!.folderText

    override fun createActions(): Array<Action> {
        setOKButtonText("确定")
        setCancelButtonText("取消")
        return arrayOf(okAction, cancelAction)
    }
}