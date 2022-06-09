package cn.uniondrug.dev.provider

import cn.uniondrug.dev.consts.DevIcons
import cn.uniondrug.dev.consts.DevKitPlugin
import cn.uniondrug.dev.dto.GoApiStruct
import cn.uniondrug.dev.dto.GoMbsStruct
import cn.uniondrug.dev.service.DocService
import cn.uniondrug.dev.ui.PreviewForm
import cn.uniondrug.dev.util.GolangPsiUtil
import com.goide.psi.GoMethodDeclaration
import com.goide.psi.GoTypeDeclaration
import com.goide.psi.GoTypeSpec
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * Api Method Marker
 * @author dingshichen
 * @date 2022/4/5
 */
class DocLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement) = when (element) {
        is GoMethodDeclaration -> {
            element.containingFile.run {
                resulveFuncComment(element, children)?.let {
                    val project = element.project
                    LineMarkerInfo(
                        element,
                        element.textRange,
                        DevIcons.DOC_VIEW,
                        { "查看文档" },
                        { _, _ ->
                            resulveFuncComment(element, children)?.let {
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
                resulveStructComment(element, this)?.let {
                    val project = element.project
                    LineMarkerInfo(
                        element,
                        element.textRange,
                        DevIcons.DOC_VIEW,
                        { "查看文档" },
                        { _, _ ->
                            resulveStructComment(element, this)?.let {
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

    /**
     * 解析方法注释
     */
    private fun resulveFuncComment(menthod: GoMethodDeclaration, psiElements: Array<PsiElement>): GoApiStruct? {
        val firstIndex = psiElements.indexOf(menthod)
        val commentStruct = GoApiStruct()
        tailrec fun resolve(
            commentStruct: GoApiStruct,
            index: Int,
        ) {
            val before = index - 2
            if (before < 0) {
                return
            }
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
                            resolve(commentStruct, before)
                        }
                    }
                }
            }
        }
        resolve(commentStruct, firstIndex)
        return if (commentStruct.isValid()) commentStruct else null
    }

    /**
     * 解析结构体注释
     */
    private fun resulveStructComment(element: PsiElement, psiFile: PsiFile): GoMbsStruct? {
        // 需要遍历的上级集合
        val psiElements: List<PsiElement> = when (element) {
            is GoTypeDeclaration -> psiFile.children.toList()
            is GoTypeSpec -> GolangPsiUtil.getRealChildren(element.parent)
            else -> listOf()
        }
        // 结构体名称
        val structName = when (element) {
            is GoTypeDeclaration -> element.typeSpecList[0].name
            is GoTypeSpec -> element.name
            else -> null
        } ?: return null
        // 步长
        val step = when (element) {
            is GoTypeDeclaration -> 2
            is GoTypeSpec -> 3
            else -> 2
        }
        val firstIndex = psiElements.indexOf(element)
        val commentStruct = GoMbsStruct()
        tailrec fun resolve(
            commentStruct: GoMbsStruct,
            index: Int,
        ) {
            val before = index - step
            if (before < 0) {
                return
            }
            when (val comment = psiElements[before]) {
                is PsiComment -> {
                    when {
                        comment.text.startsWith("// $structName") -> {
                            commentStruct.nameComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Mbs") -> {
                            commentStruct.mbsComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Topic") -> {
                            commentStruct.topicComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Tag") -> {
                            commentStruct.tagComment = comment
                            resolve(commentStruct, before)
                        }
                        comment.text.startsWith("// Author") -> {
                            commentStruct.authorComment = comment
                            resolve(commentStruct, before)
                        }
                    }
                }
            }
        }
        resolve(commentStruct, firstIndex)
        return if (commentStruct.isValid()) commentStruct else null
    }

}