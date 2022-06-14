package cn.uniondrug.dev.action

import cn.uniondrug.dev.DocumentService
import cn.uniondrug.dev.config.DocSetting
import cn.uniondrug.dev.config.TornaKeyService
import cn.uniondrug.dev.dialog.PushAllDocDialog
import cn.uniondrug.dev.notifier.notifyError
import cn.uniondrug.dev.notifier.notifyInfo
import cn.uniondrug.dev.notifier.notifyWarn
import cn.uniondrug.dev.service.DocService
import cn.uniondrug.dev.util.isSpringMVCMethod
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiMethod
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
                                try {
                                    this@apply.forEach { childFile ->
                                        PsiManager.getInstance(project).findFile(childFile)?.let {
                                            PsiTreeUtil.findChildrenOfType(it, PsiClass::class.java).forEach { psiClass ->
                                                psiClass.methods
                                                    .filter { it -> isSpringMVCMethod(it) }
                                                    .forEach { it -> pushByMethod(project, psiClass, it, this) }
                                            }
                                        }
                                    }
                                    notifyInfo(project, "批量上传任务执行完毕")
                                } catch (e: Exception) {
                                    //
                                }
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
     * 根据 method 分析
     */
    private fun pushByMethod(
        project: Project,
        psiClass: PsiClass,
        psiMethod: PsiMethod,
        pushAllDocDialog: PushAllDocDialog
    ) {
        val apiSettings = DocSetting.getInstance(project)
        val api = try {
            DocService.getInstance().buildApi(project, psiClass, psiMethod)
        } catch (ex: Exception) {
            notifyWarn(project, "有文档解析异常，跳过此接口 ${psiClass.name}#${psiMethod.name} ，错误信息：${ex.message}")
            return
        }
        val documentService = project.getService(DocumentService::class.java)
        val tornaKeyService = TornaKeyService.getInstance(project)
        try {
            val token = tornaKeyService.getToken(project, apiSettings)
            val projectId = pushAllDocDialog.projectId()
            val moduleId = pushAllDocDialog.moduleId()
            val folders = documentService.listFolderByModule(token, moduleId)
            if (folders.none { f -> f.name == api.folder }) {
                documentService.saveFolder(token, moduleId, api.folder)
            }
            documentService.listFolderByModule(token, moduleId)
                .find { f -> f.name == api.folder }
                ?.let { f -> documentService.saveDocument(token, projectId, moduleId, f.id, api) }
            notifyInfo(project, "文档 ${api.name} 上传成功")
        } catch (ex: Exception) {
            notifyError(project, "有文档上传失败，批量上传异常终止：${ex.message}")
            throw ex
        }
    }
}