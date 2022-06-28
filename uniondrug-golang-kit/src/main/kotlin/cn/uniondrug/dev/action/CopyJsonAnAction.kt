package cn.uniondrug.dev.action

import cn.uniondrug.dev.buildJsonString
import cn.uniondrug.dev.putParamExample
import cn.uniondrug.dev.util.CommonPsiUtil
import com.goide.psi.GoTypeSpec
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.wm.WindowManager
import java.awt.datatransfer.StringSelection

/**
 * @author dingshichen
 * @date 2022/6/9
 */
class CopyJsonAnAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        if (psiElement is GoTypeSpec) {
            CommonPsiUtil.getMessageBody(psiElement).let { params ->
                buildJsonString {
                    params.forEach { param ->
                        putParamExample(param)
                    }
                }.also { copy ->
                    CopyPasteManager.getInstance().setContents(StringSelection(copy))
                    WindowManager.getInstance().getStatusBar(project)?.let { statusBar ->
                        statusBar.info = "Json text has been copied"
                    }
                }
            }
        }
    }
}