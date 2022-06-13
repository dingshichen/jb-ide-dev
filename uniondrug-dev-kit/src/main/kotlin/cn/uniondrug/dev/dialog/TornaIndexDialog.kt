package cn.uniondrug.dev.dialog

import cn.uniondrug.dev.ui.TornaIndexForm
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.Action
import javax.swing.JComponent

/**
 * @author dingshichen
 * @date 2022/6/10
 */
class TornaIndexDialog(
    val project: Project
) : DialogWrapper(true) {

    init {
        title = "Torna"
        init()
    }
    override fun createCenterPanel(): JComponent? {
        return TornaIndexForm(project).rootPanel
    }

    override fun createActions(): Array<Action> {
        setOKButtonText("上传")
        setCancelButtonText("取消")
        return arrayOf(okAction, cancelAction)
    }
}