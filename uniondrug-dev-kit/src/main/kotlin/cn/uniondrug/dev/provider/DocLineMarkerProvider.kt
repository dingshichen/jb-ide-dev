package cn.uniondrug.dev.provider

import cn.uniondrug.dev.ApiBuildException
import cn.uniondrug.dev.consts.DevIcons
import cn.uniondrug.dev.consts.DevKitPlugin
import cn.uniondrug.dev.notifier.notifyError
import cn.uniondrug.dev.service.DocService
import cn.uniondrug.dev.ui.PreviewForm
import cn.uniondrug.dev.util.isNotSpringMVCMethod
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil

/**
 * @author dingshichen
 * @date 2022/4/12
 */
class DocLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        when (element) {
            is PsiIdentifier -> {
                when (val parent = element.parent) {
                    is PsiMethod -> {
                        PsiTreeUtil.getParentOfType(parent, PsiClass::class.java)?.let {
                            if (it.isAnnotationType
                                || it.isEnum
                                || it.isInterface
                                || isNotSpringMVCMethod(parent)) {
                                return null
                            }
                            val project = element.project
                            val containingFile = element.containingFile
                            return LineMarkerInfo(
                                element,
                                element.textRange,
                                DevIcons.DOC_VIEW,
                                { "查看文档" },
                                { _, _ ->
                                    val docService = DocService.getInstance()
                                    try {
                                        val docItem = docService.buildApi(project, it, parent)
                                        PreviewForm.getInstance(project, containingFile, docItem).popup()
                                    } catch (e: ApiBuildException) {
                                        notifyError(project, e.localizedMessage)
                                    }
                                },
                                GutterIconRenderer.Alignment.CENTER,
                                { DevKitPlugin.NAME }
                            )
                        }
                    }
                }
            }
        }
        return null
    }

}