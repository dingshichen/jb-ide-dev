package cn.uniondrug.dev.dialog

import cn.uniondrug.dev.ui.MssAnalyzeForm
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.Action
import javax.swing.JComponent

/**
 * @author dingshichen
 * @date 2022/6/16
 */
class MssAnalyzeDialog(
    val project: Project
) : DialogWrapper(true) {

    init {
        title = "导入所有接口依赖"
        init()
    }

    var mssAnalyzeForm: MssAnalyzeForm? = null

    override fun createCenterPanel(): JComponent? {
        mssAnalyzeForm = MssAnalyzeForm(project)
        return mssAnalyzeForm!!.rootPanel
    }

    override fun createActions(): Array<Action> {
        setOKButtonText("一把梭")
        setCancelButtonText("怂一波")
        return arrayOf(okAction, cancelAction)
    }

    fun getWorker() = mssAnalyzeForm!!.worker

    fun getProjectCode() = mssAnalyzeForm!!.projectCode

    fun getToken() = mssAnalyzeForm!!.token

}