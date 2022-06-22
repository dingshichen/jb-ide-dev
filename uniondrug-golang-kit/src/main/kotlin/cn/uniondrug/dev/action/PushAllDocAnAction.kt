package cn.uniondrug.dev.action

import cn.uniondrug.dev.Api
import cn.uniondrug.dev.TornaDocService
import cn.uniondrug.dev.config.DocSetting
import cn.uniondrug.dev.config.TornaKeyService
import cn.uniondrug.dev.dialog.PushAllDocDialog
import cn.uniondrug.dev.notifier.notifyError
import cn.uniondrug.dev.notifier.notifyInfo
import cn.uniondrug.dev.notifier.notifyWarn
import cn.uniondrug.dev.service.DocService
import cn.uniondrug.dev.util.GolangPsiUtil
import com.goide.psi.GoMethodDeclaration
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil

/**
 * 一键推送所有文档到 Torna
 */
class PushAllDocAnAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        e.getData(CommonDataKeys.PROJECT)?.let { project ->
            e.getData(CommonDataKeys.VIRTUAL_FILE)?.let { virtualFile ->
                mutableListOf<VirtualFile>().apply {
                    findAllFiles(virtualFile, this)
                    if (isNotEmpty()) {
                        PushAllDocDialog(project).run {
                            if (showAndGet()) {
                                val apis = mutableListOf<Api>()
                                this@apply.forEach { childFile ->
                                    PsiManager.getInstance(project).findFile(childFile)?.let { goFile ->
                                        PsiTreeUtil.findChildrenOfType(goFile, GoMethodDeclaration::class.java).forEach { method ->
                                            GolangPsiUtil.resulveFuncComment(method, goFile.children)?.let api@{
                                                apis += try {
                                                    DocService.getInstance().buildApiDoc(project, method, it)
                                                } catch (ex: Exception) {
                                                    notifyWarn(project, "有文档解析异常，跳过此接口 ${goFile.name}#${method.name} ，错误信息：${ex.message}")
                                                    return@api
                                                }
                                            }
                                        }
                                    }
                                }
                                if (apis.isEmpty()) {
                                    notifyInfo(project, "解析不到可用的文档，任务结束")
                                    return
                                }
                                notifyInfo(project, "文档解析完成，开始上传......")
                                // 批量上传
                                ApplicationManager.getApplication().executeOnPooledThread {
                                    batchUpload(project, apis, this)
                                }
                                notifyInfo(project, "批量上传任务执行完毕")
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 递归获取所有虚拟文件
     */
    private fun findAllFiles(virtualFile: VirtualFile, list: MutableList<VirtualFile>) {
        if (virtualFile.isDirectory) {
            virtualFile.children.forEach {
                findAllFiles(it, list)
            }
        } else {
            list += virtualFile
        }
    }

    /**
     * 批量上传
     */
    private fun batchUpload(project: Project, apis: MutableList<Api>, pushAllDocDialog: PushAllDocDialog) {
        val apiSettings = DocSetting.getInstance(project)
        val tornaDocService = project.getService(TornaDocService::class.java)
        val tornaKeyService = TornaKeyService.getInstance(project)
        // token
        val token = tornaKeyService.getToken(project, apiSettings)
        // 项目
        val projectId = pushAllDocDialog.projectId()
        // 模块
        val moduleId = pushAllDocDialog.moduleId()
        try {
            apis.forEach { api ->
                val folders = tornaDocService.listFolderByModule(token, moduleId)
                if (folders.none { f -> f.name == api.folder }) {
                    tornaDocService.saveFolder(token, moduleId, api.folder)
                }
                tornaDocService.listFolderByModule(token, moduleId)
                    .find { f -> f.name == api.folder }
                    ?.let { f -> tornaDocService.saveDoc(token, projectId, moduleId, f.id, api) }
                notifyInfo(project, "文档 ${api.name} 上传成功")
            }
        } catch (ex: Exception) {
            notifyError(project, "有文档上传失败，批量上传异常终止：${ex.message}")
            throw ex
        }
    }

}
