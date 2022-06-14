package cn.uniondrug.dev.service

import cn.uniondrug.dev.Api
import cn.uniondrug.dev.MbsEvent
import cn.uniondrug.dev.notifier.notifyError
import cn.uniondrug.dev.notifier.notifyInfo
import cn.uniondrug.dev.util.*
import com.intellij.openapi.application.ApplicationManager
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
        fun getInstance(): DocService {
            return ApplicationManager.getApplication().getService(DocService::class.java)
        }
    }

    /**
     * 构建 Torna API
     */
    fun buildApi(project: Project, psiClass: PsiClass, psiMethod: PsiMethod) = Api(
        folder = psiClass.name!!,
        name = getApiName(psiMethod),
        description = getApiDescription(psiMethod),
        author = getApiAuthor(psiClass, psiMethod),
        url = getUrl(project, psiClass, psiMethod),
        deprecated = if (isDeprecated(psiClass, psiMethod)) "已废弃（仍然可用，不建议使用）" else null,
        httpMethod = getHttpMethod(psiMethod),
        contentType = getContentType(psiMethod),
        requestParams = getRequestBody(project, psiMethod),
        responseParams = getResponseBody(project, psiMethod),
    )

    /**
     * 构建 MBS 文档
     */
    fun buildMbs(project: Project, psiClass: PsiClass, mbs: String, topic: String, tag: String, author: String?) = MbsEvent (
        name = getMbsName(psiClass),
        mbs = mbs,
        topic = topic,
        tag = tag,
        author = author,
        messageParams = getBody(project, psiType = PsiElementFactory.getInstance(project).createType(psiClass))
    )

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