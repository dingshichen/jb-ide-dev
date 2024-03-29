package cn.uniondrug.dev.service

import cn.uniondrug.dev.Api
import cn.uniondrug.dev.DocBuildFailException
import cn.uniondrug.dev.MbsEvent
import cn.uniondrug.dev.notifier.notifyError
import cn.uniondrug.dev.notifier.notifyInfo
import cn.uniondrug.dev.psi.*
import cn.uniondrug.dev.util.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiMethod
import java.io.File
import java.io.IOException

/**
 * @author dingshichen
 * @date 2022/4/12
 */
class DocService {

    companion object {

        @JvmStatic
        fun instance(): DocService = ApplicationManager.getApplication().getService(DocService::class.java)
    }

    /**
     * 构建 Torna API
     */
    fun buildApi(project: Project, psiClass: PsiClass, psiMethod: PsiMethod) = Api(
        folder = psiClass.getTitle() ?: throw DocBuildFailException("获取接口目录失败，请检查类注释！"),
        name = psiMethod.getTitle() ?: throw DocBuildFailException("获取 API 名称失败，请检查方法注释是否存在"),
        description = psiMethod.getCommentDescription(),
        author = psiMethod.getAuthor(psiClass),
        url = getUrl(psiClass, psiMethod),
        deprecated = if (psiMethod.isDeprecated(psiClass)) "已废弃（仍然可用，不建议使用）" else null,
        httpMethod = getHttpMethod(psiMethod),
        contentType = getContentType(psiMethod),
        requestParams = getRequestBody(project, psiMethod),
        responseParams = getResponseBody(project, psiMethod),
        errorParams = getErrorParams(psiMethod),
    )

    /**
     * 构建 MBS 文档
     */
    fun buildMbs(project: Project, psiClass: PsiClass, mbs: String, topic: String, tag: String, author: String?): MbsEvent {
        return PsiElementFactory.getInstance(project).createType(psiClass).run {
            MbsEvent (
                name = psiClass.getSimpleTitle() ?: throw DocBuildFailException("获取 MBS 事件名称失败，请检查类注释是否有效"),
                mbs = mbs,
                topic = topic,
                tag = tag,
                author = author,
                messageParams = getBody(project, psiType = this, fieldNode = newFiledNode(this))
            )
        }
    }

    /**
     * 导出
     */
    fun export(project: Project, fileName: String, text: String) {
        val fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
        fileChooserDescriptor.isForcedToUseIdeaFileChooser = true
        FileChooser.chooseFile(fileChooserDescriptor, project, null) {
            try {
                val file = File("${it.path}/$fileName.md")
                FileUtil.writeToFile(file, text)
                notifyInfo(project, "接口文档导出完成")
            } catch (ioException: IOException) {
                notifyError(project, "接口文档导出失败")
            }
        }
    }
}