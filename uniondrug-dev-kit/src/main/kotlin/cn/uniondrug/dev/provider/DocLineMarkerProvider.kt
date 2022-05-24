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
                when (element.getParent()) {
                    is PsiMethod -> {
                        val psiMethod = element.parent as PsiMethod
                        PsiTreeUtil.getParentOfType(psiMethod, PsiClass::class.java)?.let {
                            if (it.isAnnotationType
                                || it.isEnum
                                || it.isInterface
                                || isNotSpringMVCMethod(psiMethod)) {
                                return null
                            }
                            LineMarkerInfo(
                                element,
                                element.textRange,
                                DevIcons.DOC_VIEW,
                                { "查看文档" },
                                { _, _ ->
                                    val docService = DocService.getInstance()
                                    try {
                                        val docItem = docService.buildApi(element.project, it, psiMethod)
                                        PreviewForm.getInstance(element.project, element.containingFile, docItem).popup()
                                    } catch (e: ApiBuildException) {
                                        notifyError(element.project, e.localizedMessage)
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