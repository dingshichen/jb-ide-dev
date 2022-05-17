package cn.uniondrug.dev.provider

import cn.uniondrug.dev.consts.DevIcons
import cn.uniondrug.dev.consts.DevKitPlugin
import cn.uniondrug.dev.dto.GoApiStruct
import cn.uniondrug.dev.service.DocService
import cn.uniondrug.dev.ui.PreviewForm
import com.goide.psi.GoFile
import com.goide.psi.GoMethodDeclaration
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

/**
 * Api Method Marker
 * @author dingshichen
 * @date 2022/4/5
 */
class DocLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        return when (element) {
            is GoMethodDeclaration -> {
                PsiTreeUtil.getParentOfType(element, GoFile::class.java)?.let { goFile ->
                    resulveComment(element, goFile.children)?.let {
                        LineMarkerInfo(
                            element,
                            element.textRange,
                            DevIcons.DOC_VIEW,
                            { "查看文档" },
                            { _, _ ->
                                resulveComment(element, goFile.children)?.let {
                                    val docService = DocService.getInstance()
                                    val docItem = docService.buildApi(element, it)
                                    PreviewForm.getInstance(element.project, element.containingFile, docItem)
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

    /**
     * 解析方法注释
     */
    private fun resulveComment(menthod: GoMethodDeclaration, psiElements: Array<PsiElement>): GoApiStruct? {
        val firstIndex = psiElements.indexOf(menthod)
        val commentStruct = GoApiStruct()
        tailrec fun resolve(
            commentStruct: GoApiStruct,
            index: Int,
        ) {
            val before = index - 2
            when (val comment = psiElements[before]) {
                is PsiComment -> {
                    when {
                        comment.text.startsWith("// ${menthod.name}") -> {
                            commentStruct.nameComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Get") -> {
                            commentStruct.getComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Post") -> {
                            commentStruct.postComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Desc") -> {
                            commentStruct.descComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Author") -> {
                            commentStruct.authorComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Deprecated") -> {
                            commentStruct.deprecatedComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Request") -> {
                            commentStruct.requestComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Response") -> {
                            commentStruct.responseComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Error") -> {
                            commentStruct.errorComment += comment
                            resolve(commentStruct,  before)
                        }
                    }
                }
            }
        }
        resolve(commentStruct, firstIndex)
        return if (commentStruct.isValid()) commentStruct else null
    }

}