package cn.uniondrug.dev.service

import cn.uniondrug.dev.Api
import cn.uniondrug.dev.DocBuildFailException
import cn.uniondrug.dev.MbsEvent
import cn.uniondrug.dev.dto.ApiBaseAccess
import cn.uniondrug.dev.dto.GoApiStruct
import cn.uniondrug.dev.dto.GoMbsStruct
import cn.uniondrug.dev.notifier.notifyError
import cn.uniondrug.dev.notifier.notifyInfo
import cn.uniondrug.dev.util.CommonPsiUtil
import cn.uniondrug.dev.util.GolangPsiUtil
import cn.uniondrug.dev.util.getCommentValue
import cn.uniondrug.dev.util.humpToPath
import com.goide.psi.GoMethodDeclaration
import com.goide.psi.GoTypeDeclaration
import com.goide.psi.GoTypeSpec
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiElement
import java.io.File
import java.io.IOException

/**
 * DOC
 * @author dingshichen
 * @date 2022/4/5
 */
class DocService {

    /**
     * 构建 API DTO  # (method.receiverType as GoPointerTypeImpl).type.contextlessResolve().containingFile.children
     */
    fun buildApiDoc(project: Project, method: GoMethodDeclaration, goApiStruct: GoApiStruct): Api {
        val receiverType = method.receiverType
        val urlPrefix: String = if (receiverType != null) {
            GolangPsiUtil.getRealTypeOrSelf(receiverType).contextlessResolve()?.let {
                GolangPsiUtil.findRoutePrefix(it, it.containingFile)?.getPath()
            } ?: ""
        } else ""
        val (url, httpMethod, contentType) = goApiStruct.getComment?.let {
            ApiBaseAccess(
                url = it.getPath(),
                httpMethod = "GET",
                contentType = "application/x-www-form-urlencoded",
            )
        } ?: goApiStruct.postComment?.let {
            ApiBaseAccess(
                url = it.getPath(),
                httpMethod = "POST",
                contentType = "application/json",
            )
        } ?: goApiStruct.nameComment?.let {
            if (it.isGetPrefix()) {
                ApiBaseAccess(
                    url = it.text.substring(6).split(" ")[0].humpToPath(),
                    httpMethod = "Get",
                    contentType = "application/x-www-form-urlencoded",
                )
            } else if (it.isPostPrefix()) {
                ApiBaseAccess(
                    url = it.text.substring(7).split(" ")[0].humpToPath(),
                    httpMethod = "POST",
                    contentType = "application/json",
                )
            } else null
        } ?: throw DocBuildFailException("分析接口基本协议错误，请检查接口定义")
        return Api(
            folder = receiverType?.presentationText?.replace("*", "") ?: "",
            name = goApiStruct.nameComment!!.getName(),
            description = goApiStruct.descComment.reversed().joinToString("") { it.text.replace("//", "") },
            author = goApiStruct.authorComment?.getAuthor() ?: "",
            deprecated = goApiStruct.deprecatedComment?.getDeprecated() ?: "",
            url = "/$urlPrefix/$url".replace("///", "/").replace("//", "/"),
            httpMethod = httpMethod,
            contentType = contentType,
            requestParams = CommonPsiUtil.getRequestBody(project, goApiStruct.requestComment ?: throw DocBuildFailException("分析入参失败，请检查接口定义")),
            responseParams = CommonPsiUtil.getResponseBody(project, goApiStruct.responseComment),
            errorParams = CommonPsiUtil.getErrnos(goApiStruct.errorComment.reversed())
        )
    }

    /**
     * 构建 MBS 文档
     */
    fun buildMbsDoc(type: PsiElement, goMbsStruct: GoMbsStruct): MbsEvent {
        val typeSpec = when(type) {
            is GoTypeDeclaration -> type.typeSpecList[0]
            is GoTypeSpec -> type
            else -> throw DocBuildFailException("构建 MBS 文档失败，解析不到对应元素的类型")
        }
        return MbsEvent(
            name = goMbsStruct.nameComment!!.getName(),
            mbs = goMbsStruct.mbsComment!!.getMbs(),
            topic = goMbsStruct.topicComment!!.getTopic(),
            tag = goMbsStruct.tagComment!!.getTag(),
            author = goMbsStruct.authorComment?.text?.getCommentValue("Author") ?: "",
            messageParams =  CommonPsiUtil.getMessageBody(typeSpec)
        )
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

    companion object {

        fun getInstance(): DocService = ApplicationManager.getApplication().getService(DocService::class.java)
    }

}
