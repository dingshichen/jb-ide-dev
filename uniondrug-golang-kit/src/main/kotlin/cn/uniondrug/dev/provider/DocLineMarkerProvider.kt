package cn.uniondrug.dev.provider

import cn.uniondrug.dev.DocBuildFailException
import cn.uniondrug.dev.consts.DevIcons
import cn.uniondrug.dev.consts.DevKitPlugin
import cn.uniondrug.dev.notifier.notifyError
import cn.uniondrug.dev.service.DocService
import cn.uniondrug.dev.ui.PreviewForm
import cn.uniondrug.dev.util.GolangPsiUtil
import com.goide.psi.GoMethodDeclaration
import com.goide.psi.GoTypeDeclaration
import com.goide.psi.GoTypeSpec
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement

/**
 * Api Method Marker
 * @author dingshichen
 * @date 2022/4/5
 */
class DocLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement) = when (element) {
        is GoMethodDeclaration -> {
            element.containingFile.run {
                GolangPsiUtil.resulveFuncComment(element, children)?.let {
                    val project = element.project
                    LineMarkerInfo(
                        element,
                        element.textRange,
                        DevIcons.DOC_VIEW,
                        { "查看文档" },
                        { _, method ->
                            GolangPsiUtil.resulveFuncComment(method, children)?.let {
                                val docService = DocService.getInstance()
                                try {
                                    val api = docService.buildApiDoc(project, method, it)
                                    PreviewForm.getInstance(project, this, api)
                                        .popup()
                                } catch (e: DocBuildFailException) {
                                    notifyError(project, e.localizedMessage)
                                }
                            }
                        },
                        GutterIconRenderer.Alignment.CENTER,
                        { DevKitPlugin.NAME }
                    )
                }
            }
        }
        is GoTypeDeclaration,
        is GoTypeSpec -> {
            element.containingFile.run {
                GolangPsiUtil.resulveStructComment(element, this)?.let {
                    val project = element.project
                    LineMarkerInfo(
                        element,
                        element.textRange,
                        DevIcons.DOC_VIEW,
                        { "查看文档" },
                        { _, typeSpec ->
                            GolangPsiUtil.resulveStructComment(typeSpec, this)?.let {
                                val docService = DocService.getInstance()
                                try {
                                    val mbsEvent = docService.buildMbsDoc(typeSpec, it)
                                    PreviewForm.getInstance(project, this, mbsEvent)
                                        .popup()
                                } catch (e: DocBuildFailException) {
                                    notifyError(project, e.localizedMessage)
                                }
                            }
                        },
                        GutterIconRenderer.Alignment.CENTER,
                        { DevKitPlugin.NAME }
                    )
                }
            }
        }
        else -> null
    }
}