package cn.uniondrug.dev.dialog

import cn.uniondrug.dev.Issue
import cn.uniondrug.dev.IssueService
import cn.uniondrug.dev.config.DocSetting
import cn.uniondrug.dev.notifier.notifyError
import cn.uniondrug.dev.notifier.notifyInfo
import cn.uniondrug.dev.notifier.notifyWarn
import cn.uniondrug.dev.ui.IssueForm
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import javax.swing.Action
import javax.swing.JComponent

/**
 * @author dingshichen
 */
class IssueDialog : DialogWrapper(true) {

    init {
        title = "意见箱"
        init()
    }

    lateinit var issueForm: IssueForm

    override fun createCenterPanel(): JComponent {
        issueForm = IssueForm()
        return issueForm.rootPanel
    }

    override fun createActions(): Array<Action> {
        setOKButtonText("发送")
        setCancelButtonText("取消")
        return arrayOf(okAction, cancelAction)
    }

    override fun doValidateAll(): MutableList<ValidationInfo> {
        val list = mutableListOf<ValidationInfo>()
        if (issueForm.content.isNullOrBlank()) {
            list += ValidationInfo("请输入意见内容")
        }
        return list
    }

    fun issue(author: String) = Issue(author, issueForm.content)

    companion object {

        @JvmStatic
        fun showIssueDialog(project: Project) {
            val docSetting = project.service<DocSetting>()
            val username = docSetting.state.username
            if (username.isNullOrBlank()) {
                notifyWarn(project, "请先完成 AD 账号和密码的配置")
                return
            }
            IssueDialog().apply {
                if (showAndGet()) {
                    val issue = issue(username)
                    try {
                        project.service<IssueService>().postIssue(issue)
                        notifyInfo(project, "意见已投递", null)
                    } catch (ex: Exception) {
                        notifyError(project, "抱歉，投递意见失败了，请检查网络或稍后重试")
                    }
                }
            }
        }
    }
}

