package cn.uniondrug.dev.action

import cn.uniondrug.dev.Api
import cn.uniondrug.dev.TornaDocService
import cn.uniondrug.dev.config.DocSetting
import cn.uniondrug.dev.config.DocSettingConfigurable
import cn.uniondrug.dev.config.TornaKeyService
import cn.uniondrug.dev.dialog.PushAllDocDialog
import cn.uniondrug.dev.dialog.showAndGetPushAllDocDialog
import cn.uniondrug.dev.notifier.notifyError
import cn.uniondrug.dev.notifier.notifyInfo
import cn.uniondrug.dev.notifier.notifyWarn
import cn.uniondrug.dev.psi.isIgnore
import cn.uniondrug.dev.service.DocService
import cn.uniondrug.dev.util.isSpringMVCMethod
import com.intellij.notification.BrowseNotificationAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil

/**
 * 一键推送所有文档到 Torna
 */
class PushAllDocAnAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val docSetting = DocSetting.instance(project)
        val tornaKeyService = TornaKeyService.instance(project)
        if (docSetting.state.username.isNullOrBlank() || tornaKeyService.getPassword().isNullOrBlank()) {
            // 说明没有配置 Torna, 跳转到配置页面
            notifyError(project, "请先完成 Torna 账号配置")
            ShowSettingsUtil.getInstance().showSettingsDialog(e.project, DocSettingConfigurable::class.java)
            return
        }
        mutableListOf<VirtualFile>()
            .apply { findAllFiles(virtualFile, this) }
            .takeIf { it.isNotEmpty() }
            ?.let { files ->
                showAndGetPushAllDocDialog(project) {
                    // 记住我的选择
                    with(docSetting.state) {
                        rememberSpaceBoxId = spaceId()
                        rememberModuleBoxId = moduleId()
                        rememberProjectBoxId = projectId()
                    }
                    val apis = mutableListOf<Api>()
                    files.forEach { childFile ->
                        PsiManager.getInstance(project).findFile(childFile)?.let {
                            PsiTreeUtil.findChildrenOfType(it, PsiClass::class.java).forEach { psiClass ->
                                psiClass.methods
                                    .filter { it -> isSpringMVCMethod(it) }
                                    .forEach apiForEach@{ psiMethod ->
                                        if (psiMethod.isIgnore()) {
                                            notifyInfo(project, "接口 ${psiClass.name}#${psiMethod.name} 有标识 @ignore 忽略上传")
                                            return@apiForEach
                                        }
                                        apis += try {
                                            DocService.instance().buildApi(project, psiClass, psiMethod)
                                        } catch (ex: Throwable) {
                                            notifyWarn(project, "有文档解析异常，跳过此接口 ${psiClass.name}#${psiMethod.name} ，错误信息：${ex.message}")
                                            return@apiForEach
                                        }
                                    }
                            }
                        }
                    }
                    notifyInfo(project, "文档解析完成，开始上传......")
                    // 批量上传
                    ApplicationManager.getApplication().executeOnPooledThread {
                        batchUpload(project, apis, this)
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
        val apiSettings = DocSetting.instance(project)
        val tornaDocService = project.getService(TornaDocService::class.java)
        val tornaKeyService = TornaKeyService.instance(project)
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
                    ?.let { f ->
                        val docId = tornaDocService.saveDoc(token, projectId, moduleId, f.id, api)
                        val url = tornaDocService.getDocViewUrl(docId)
                        notifyInfo(project, "文档 ${api.name} 上传成功", BrowseNotificationAction("-> Torna", url))
                    }
            }
            notifyInfo(project, "批量上传任务执行完毕")
        } catch (ex: Exception) {
            notifyError(project, "有文档上传失败，批量上传异常终止：${ex.message}")
            throw ex
        }
    }

}

