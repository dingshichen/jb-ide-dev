package cn.uniondrug.dev.provider

import cn.uniondrug.dev.DocBuildFailException
import cn.uniondrug.dev.consts.DevIcons
import cn.uniondrug.dev.consts.DevKitPlugin
import cn.uniondrug.dev.notifier.notifyError
import cn.uniondrug.dev.service.DocService
import cn.uniondrug.dev.ui.PreviewForm
import cn.uniondrug.dev.util.commentText
import cn.uniondrug.dev.util.isNotSpringMVCMethod
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod
import com.intellij.psi.javadoc.PsiDocComment
import com.intellij.psi.util.PsiTreeUtil

/**
 * @author dingshichen
 * @date 2022/4/12
 */
class DocLineMarkerProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        return when (element) {
            is PsiIdentifier -> {
                when (val parent = element.parent) {
                    is PsiMethod -> apiLineMarkerInfo(element, parent)
                    is PsiClass -> mbsLineMarkerInfo(element, parent)
                    else -> null
                }
            }
            else -> null
        }
    }

    /**
     * API 标记
     */
    private fun apiLineMarkerInfo(element: PsiElement, psiMethod: PsiMethod): LineMarkerInfo<*>? {
        return PsiTreeUtil.getParentOfType(psiMethod, PsiClass::class.java)?.let {
            if (it.isAnnotationType
                || it.isEnum
                || it.isInterface
                || isNotSpringMVCMethod(psiMethod)) {
                return null
            }
            val project = element.project
            val containingFile = element.containingFile
            LineMarkerInfo(
                element,
                element.textRange,
                DevIcons.DOC_VIEW,
                { "查看文档" },
                { _, _ ->
                    val docService = DocService.getInstance()
                    try {
                        val docItem = docService.buildApi(project, it, psiMethod)
                        PreviewForm.getInstance(project, containingFile, docItem).popup()
                    } catch (e: DocBuildFailException) {
                        notifyError(project, e.localizedMessage)
                    }
                },
                GutterIconRenderer.Alignment.CENTER,
                { DevKitPlugin.NAME }
            )
        }
    }

    /**
     * MBS 消息体标记
     */
    private fun mbsLineMarkerInfo(element: PsiElement, psiClass: PsiClass): LineMarkerInfo<*>? {
        if (psiClass.isAnnotationType
            || psiClass.isEnum
            || psiClass.isInterface) {
            return null
        }
        return psiClass.docComment?.let {
            val mbsCommentTag = mbsCommentTag(it)
            if (!mbsCommentTag.isValid()) {
                return null
            }
            val project = element.project
            val containingFile = element.containingFile
            LineMarkerInfo(
                element,
                element.textRange,
                DevIcons.DOC_VIEW,
                { "查看文档" },
                { _, _ ->
                    val docService = DocService.getInstance()
                    try {
                        mbsCommentTag(it).apply {
                            val mbsEvent = docService.buildMbs(project, psiClass, mbs!!, topic!!, tag!!, author)
                            PreviewForm.getInstance(project, containingFile, mbsEvent).popup()
                        }
                    } catch (e: DocBuildFailException) {
                        notifyError(project, e.localizedMessage)
                    }
                },
                GutterIconRenderer.Alignment.CENTER,
                { DevKitPlugin.NAME }
            )
        }
    }

    private fun mbsCommentTag(psiDocComment: PsiDocComment) = MbsCommentTag(
        mbs = psiDocComment.findTagByName("mbs")?.valueElement?.commentText(),
        topic = psiDocComment.findTagByName("topic")?.valueElement?.commentText(),
        tag = psiDocComment.findTagByName("tag")?.valueElement?.commentText(),
        author = psiDocComment.findTagByName("author")?.valueElement?.commentText(),
    )

    data class MbsCommentTag(
        val mbs: String?,
        val topic: String?,
        val tag: String?,
        val author: String?
    ) {
        fun isValid() = mbs != null && topic != null && tag != null
    }


}