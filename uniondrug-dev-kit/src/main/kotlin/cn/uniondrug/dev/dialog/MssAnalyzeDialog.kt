package cn.uniondrug.dev.dialog

import cn.uniondrug.dev.ui.MssAnalyzeForm
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
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

    private lateinit var mssAnalyzeForm: MssAnalyzeForm

    override fun createCenterPanel(): JComponent? {
        mssAnalyzeForm = MssAnalyzeForm(project)
        return mssAnalyzeForm.rootPanel
    }

    override fun createActions(): Array<Action> {
        setOKButtonText("一把梭")
        setCancelButtonText("怂一波")
        return arrayOf(okAction, cancelAction)
    }

    override fun doValidateAll(): MutableList<ValidationInfo> {
        val list = mutableListOf<ValidationInfo>()
        if (getWorker().isEmpty()) {
            list += ValidationInfo("需要正确填写中文姓名项目负责人，如：张三", mssAnalyzeForm.workerField)
        }
        if (getProjectCode().isEmpty()) {
            list += ValidationInfo("需要正确填写项目英文编码，如：js-demo", mssAnalyzeForm.projectField)
        }
        if (getToken().isEmpty()) {
            list += ValidationInfo("需要正确填写正确的认证 token，可从 web 后台抓包获取", mssAnalyzeForm.authField)
        }
        return list
    }

    fun getWorker(): String = mssAnalyzeForm.worker

    fun getProjectCode(): String = mssAnalyzeForm.projectCode

    fun getToken(): String = mssAnalyzeForm.token

}