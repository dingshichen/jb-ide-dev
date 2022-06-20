package cn.uniondrug.dev.provider

import cn.uniondrug.dev.consts.DevIcons
import cn.uniondrug.dev.consts.DevKitPlugin
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
                        { _, _ ->
                            GolangPsiUtil.resulveFuncComment(element, children)?.let {
                                val docService = DocService.getInstance()
                                val api = docService.buildApiDoc(project, element, it)
                                PreviewForm.getInstance(project, this, api)
                                    .popup()
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
                        { _, _ ->
                            GolangPsiUtil.resulveStructComment(element, this)?.let {
                                val docService = DocService.getInstance()
                                val mbsEvent = docService.buildMbsDoc(element, it)
                                PreviewForm.getInstance(project, this, mbsEvent)
                                    .popup()
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