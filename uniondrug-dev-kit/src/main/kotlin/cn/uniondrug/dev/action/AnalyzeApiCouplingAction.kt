package cn.uniondrug.dev.action

import cn.uniondrug.dev.mss.*
import cn.uniondrug.dev.notifier.notifyError
import cn.uniondrug.dev.notifier.notifyInfo
import cn.uniondrug.dev.util.*
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.psi.search.searches.OverridingMethodsSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil
import java.io.File

/**
 * 分析接口的耦合
 */
class AnalyzeApiCouplingAction: AnAction() {

    private var listenerInterface: PsiClass? = null

    private var eventMap: MutableMap</* 事件 */ PsiClass, /* 监听 */PsiClass> = mutableMapOf()

    override fun actionPerformed(e: AnActionEvent) {
        e.getData(CommonDataKeys.PROJECT)?.let { project ->
            e.getData(CommonDataKeys.VIRTUAL_FILE)?.let { virtualFile ->
                // 先调起路径选择器控件
                val fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                fileChooserDescriptor.isForcedToUseIdeaFileChooser = true
                FileChooser.chooseFile(fileChooserDescriptor, project, null) { out ->
                    try {
                        mutableMapOf<UniondrugResource, Set<UniondrugResource>>().run {
                            // 开始分析
                            analyzeApiCoupling(project, virtualFile, this)
                            clearEventListener();
                            // 输出结果
                            val fileAll = File("${out.path}/AnalyzeApiCoupling-all.txt")
                            FileUtil.writeToFile(fileAll, concatAll(this))
                            notifyInfo(project, "分析接口耦合结果完成")
                        }
                    } catch (e: Exception) {
                        notifyError(project, "分析接口耦合结果完成失败")
                    }
                }
            }
        }
    }

    /**
     * 回收调临时资源
     */
    private fun clearEventListener() {
        listenerInterface = null
        eventMap.clear()
    }

    /**
     * 拼接所有
     */
    private fun concatAll(analyMap: Map<UniondrugResource, Set<UniondrugResource>>) = buildString {
        analyMap.forEach {(api, trace) ->
            appendLine().append("分析开始 -> $api")
            trace.forEach {
                appendLine().append("调用 -> $it")
            }
            appendLine().append("分析结束")
        }
    }

    private fun analyzeApiCoupling(project: Project, virtualFile: VirtualFile, result: MutableMap<UniondrugResource, Set<UniondrugResource>>) {
        if (virtualFile.isDirectory) {
            // 如果是目录就递归子集
            virtualFile.children.forEach { child ->
                analyzeApiCoupling(project, child, result)
            }
        }
        // 从虚拟文件中获得语法结构文件
        PsiManager.getInstance(project).findFile(virtualFile)?.let {
            PsiTreeUtil.findChildrenOfType(it, PsiClass::class.java).forEach { psiClass ->
                psiClass.allMethods.forEach { method ->
                    if (isSpringMVCMethod(method)) {
                        mutableSetOf<UniondrugResource>().apply {
                            val ownResource = ownResource {
                                name {
                                    method.name
                                }
                                path {
                                    getUrl(project, psiClass, method).substringAfter("turboradio.cn")
                                }
                            }
                            result[ownResource] = this
                            analyRestMethod(project, method, this)
                        }
                    }
                }
            }
        }
    }

    /**
     * 分析 REST 接口方法
     */
    private fun analyRestMethod(project: Project, method: PsiMethod, traceNames: MutableSet<UniondrugResource>) {
        PsiTreeUtil.findChildrenOfType(method, PsiCodeBlock::class.java).forEach { codeBlock ->
            // 从方法解读代码块，只有 PsiStatement 才是程序正确需要运行的元素
            codeBlock.statements.forEach { psiStatement ->
                mutableListOf<PsiMethodCallExpression>().run {
                    addAllMethodCalls(psiStatement, this)
                    forEach {
                        analyMethodExpression(project, it, traceNames)
                    }
                }
            }
        }
    }

    private fun analyMethodExpression(project: Project, call: PsiMethodCallExpression, traceNames: MutableSet<UniondrugResource>) {
        val express = call.methodExpression.resolve() ?: return
        if (express !is PsiMethod) {
            return
        }
        val file = express.containingFile
        if (file !is PsiJavaFile) {
            return
        }
        if (express.containingClass?.qualifiedName == "org.springframework.web.client.RestTemplate"
            || file.packageName.startsWith("cn.uniondrug")) {
            val query = OverridingMethodsSearch.search(
                express,
                ProjectScope.getProjectScope(project),
                false
            )
            query.findAll().run {
                if (isEmpty()) {
                    PsiTreeUtil.getParentOfType(express, PsiClass::class.java)?.let { psiClass ->
                        if (isMbsService(psiClass)) {
                            call.argumentList.expressions.run args@{
                                if (this.size > 2) {
                                    traceNames += mbsResource {
                                        channel {
                                            ofMbsChannel(psiClass.qualifiedName!!)!!
                                        }
                                        topic {
                                            getLiteralValue(this@args[0])!!
                                        }
                                        tag {
                                            getLiteralValue(this@args[1])!!
                                        }
                                    }
                                }
                            }
                        } else if (isFeignClient(psiClass)) {
                            traceNames += rpcResource {
                                serverUrlExpress {
                                    getFeignClientUrl(psiClass)!!
                                }
                                path {
                                    getFeignClientMethodPath(express)!!
                                }
                                thirdFlag {
                                    "0"
                                }
                            }
                        }
                    }
                } else {
                    forEach {
                        PsiTreeUtil.getParentOfType(it, PsiClass::class.java)?.let { _ ->
                            analyRestMethod(project, it, traceNames);
                        }
                    }
                }
            }
        } else if (isEventPublisher(express)) {
            // spring 事件监听需要特殊处理
            val args = call.argumentList.expressions
            if (args.size != 1) {
                return
            }
            args[0].reference?.resolve().let { variable ->
                if (variable is PsiLocalVariable) {
                    PsiUtil.resolveClassInClassTypeOnly(variable.type)?.let {
                        it.supers.find { psiClass ->
                            psiClass.qualifiedName == EVENT
                        }?.let { _ ->
                            if (eventMap.isEmpty()) {
                                initEventListener(project)
                            }
                            eventMap[it]?.let { listener ->
                                // 找到对应事件的话，就可以处理了
                                listener.findMethodsByName("onApplicationEvent", false).first()?.let { onMethod ->
                                    analyRestMethod(project, onMethod, traceNames)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 初始化 spring 事件监听关系
     */
    private fun initEventListener(project: Project) {
        if (listenerInterface == null) {
            listenerInterface = JavaPsiFacade.getInstance(project)
                .findClass(EVENT_LISTENER, GlobalSearchScope.allScope(project))
        }
        listenerInterface?.let { l ->
            ClassInheritorsSearch.search(l).filter { listener ->
                val qualifiedName = listener.qualifiedName ?: return@filter false
                return@filter qualifiedName.startsWith("cn.uniondrug")
            }.forEach { listener ->
                if (listener.implementsListTypes.size == 1) {
                    val parameters = listener.implementsListTypes[0].parameters
                    if (parameters.size == 1) {
                        PsiUtil.resolveClassInClassTypeOnly(parameters[0])?.let { classOfEvent ->
                            eventMap[classOfEvent] = listener
                        }
                    }
                }
            }
        }
    }

    /**
     * 如果是事件发布者
     */
    private fun isEventPublisher(psiMethod: PsiMethod): Boolean {
        val psiClass = psiMethod.containingClass
        psiClass ?: return false
        if (psiClass.qualifiedName == EVENT_PUBLISHER) {
            return true
        }
        return psiClass.supers.find { it.qualifiedName == EVENT_PUBLISHER } != null
    }

    /**
     * 递归寻找所有 PsiMethodCallExpression
     */
    private fun addAllMethodCalls(psiElement: PsiElement, list: MutableList<PsiMethodCallExpression>) {
        psiElement.acceptChildren(object : PsiElementVisitor() {

            override fun visitElement(element: PsiElement) {
                if (element is PsiMethodCallExpression) {
                    list += element
                } else {
                    addAllMethodCalls(element, list)
                }
            }

        })
    }

}