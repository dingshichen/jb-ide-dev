package cn.uniondrug.dev.service

import cn.uniondrug.dev.Api
import cn.uniondrug.dev.DocConvertor
import cn.uniondrug.dev.TEMPLATE
import cn.uniondrug.dev.dto.GoApiStruct
import cn.uniondrug.dev.notifier.notifyError
import cn.uniondrug.dev.notifier.notifyInfo
import cn.uniondrug.dev.util.*
import com.goide.psi.GoMethodDeclaration
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import java.io.File
import java.io.IOException

/**
 * DOC
 * @author dingshichen
 * @date 2022/4/5
 */
class DocService {

    companion object {

        fun getInstance(): DocService = ApplicationManager.getApplication().getService(DocService::class.java)
    }

    /**
     * 构建 API DTO
     */
    fun buildApi(method: GoMethodDeclaration, goApiStruct: GoApiStruct): Api {
        var url = ""
        var httpMethod = ""
        var contentType = ""
        goApiStruct.getComment?.let {
            url = it.text.getCommentValue("Get")
            httpMethod = "GET"
            contentType = "application/x-www-form-urlencoded"
        } ?: goApiStruct.postComment?.let {
            url = it.text.getCommentValue("Post")
            httpMethod = "POST"
            contentType = "application/json"
        } ?: goApiStruct.nameComment?.let {
            if (it.text.startsWith("// Get")) {
                url = it.text.substring(6).humpToPath()
                httpMethod = "Get"
                contentType = "application/x-www-form-urlencoded"
            } else if (it.text.startsWith("// Post")) {
                url = it.text.substring(7).humpToPath()
                httpMethod = "POST"
                contentType = "application/json"
            }
        }
        return Api(
            name = goApiStruct.nameComment?.text?.getCommentValue(method.name!!)!!,
            description = goApiStruct.descComment?.text?.getCommentValue("Desc") ?: "",
            author = goApiStruct.authorComment?.text?.getCommentValue("Author") ?: "",
            deprecated = goApiStruct.deprecatedComment?.text?.getCommentValue("Deprecated"),
            url = url,
            httpMethod = httpMethod,
            contentType = contentType,
            requestParams = CommonPsiUtil.getRequestBody(method.project, goApiStruct.requestComment!!),
            responseParams = CommonPsiUtil.getResponseBody(method.project, goApiStruct.responseComment),
        )
    }

    /**
     * 解析成字符串
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