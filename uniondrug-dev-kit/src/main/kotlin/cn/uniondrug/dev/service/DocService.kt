package cn.uniondrug.dev.service

import cn.uniondrug.dev.Api
import cn.uniondrug.dev.TEMPLATE
import cn.uniondrug.dev.DocConvertor
import cn.uniondrug.dev.notifier.notifyError
import cn.uniondrug.dev.notifier.notifyInfo
import cn.uniondrug.dev.util.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiClass
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
     * @param psiClass
     * @param psiMethod
     * @return
     */
    fun buildApi(project: Project, psiClass: PsiClass, psiMethod: PsiMethod) = Api(
        name = getApiName(psiMethod),
        description = getApiDescription(psiMethod),
        author = getApiAuthor(psiClass, psiMethod),
        url = getUrl(project, psiClass, psiMethod),
        deprecated = if (isDeprecated(psiClass, psiMethod)) "已废弃（仍然可用，不建议使用）" else null,
        httpMethod = getHttpMethod(psiMethod),
        contentType = getContentType(psiMethod),
        requestParams = getRequestBody(psiMethod),
        responseParams = getResponseBody(psiMethod),
    )

//    /**
//     * 上传文档
//     * @param project
//     * @param api
//     */
//    fun upload(project: Project, api: Api) {
//        val docSetting = getInstance(project)
//        val client = OpenClient(docSetting.state.url)
//        val request = DocPushRequest(docSetting.state.token)
//        request.debugEnvs = listOf(DebugEnv("Testing", docSetting.state.domain))
//        request.apis = listOf(api)
//        request.author = docSetting.state.author
//        // 发送请求
//        val response = client.execute(request)
//        if (response.isSuccess) {
//            // 返回结果
//            val data = response.data
//            println(JSON.toJSONString(data, SerializerFeature.PrettyFormat))
//        } else {
//            println("errorCode:" + response.code + ",errorMsg:" + response.msg)
//        }
//    }

    /**
     * 转成 Markdown 文本
     */
    fun parse(api: Api): String {
        val detail = DocConvertor.convert(api)
        return VelocityUtil.convert(TEMPLATE, detail)
    }

    /**
     * 导出
     */
    fun export(project: Project, fileName: String, text: String) {
        val fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
        fileChooserDescriptor.isForcedToUseIdeaFileChooser = true
        FileChooser.chooseFile(fileChooserDescriptor, project, null)?.let {
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