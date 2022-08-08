package cn.uniondrug.dev.action

import cn.uniondrug.dev.ConsulService
import cn.uniondrug.dev.UNIONDRUG_PACKAGE
import cn.uniondrug.dev.config.DocSetting
import cn.uniondrug.dev.dialog.MssAnalyzeDialog
import cn.uniondrug.dev.mss.*
import cn.uniondrug.dev.notifier.notifyError
import cn.uniondrug.dev.notifier.notifyInfo
import cn.uniondrug.dev.util.*
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.psi.search.searches.OverridingMethodsSearch
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil

/**
 * 分析接口的耦合
 */
class AnalyzeApiCouplingAction : AnAction() {

    private var listenerInterface: PsiClass? = null

    private var eventMap: MutableMap</* 事件 */ PsiClass, /* 监听 */PsiClass> = mutableMapOf()

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val consulService = project.getService(ConsulService::class.java)
        val consul = try {
            consulService.getApplicationData()
        } catch (ex: Exception) {
            notifyError(project, "请确认网络环境是否正确，无法连接测试环境 consul：${ex.message}")
            return
        }
        val docSetting = DocSetting.instance(project)
        MssAnalyzeDialog(project).apply {
            if (showAndGet()) {
                // 记住我的选择
                docSetting.saveMssState(getWorker(), getProjectCode(), getToken())
                mutableMapOf<OwnResource, Set<UniondrugResource>>().run {
                    // 开始分析
                    try {
                        analyzeApiCoupling(project, virtualFile, this)
                    } catch (ex: Exception) {
                        notifyError(project, "分析接口失败：${ex.message}")
                        return@apply
                    } finally {
                        clearEventListener()
                    }
                    notifyInfo(project, "已完成接口耦合分析，正在进行 MSS 数据上报...")
                    val mssProjectService = project.getService(MssProjectService::class.java)
                    val mssApiService = project.getService(MssApiService::class.java)
                    val mssService = project.getService(MssService::class.java)
                    // 上传
                    ApplicationManager.getApplication().executeOnPooledThread {
                        try {
                            mssService.upload(
                                mssProjectService,
                                mssApiService,
                                consul,
                                getWorker(),
                                getProjectCode(),
                                getToken(),
                                this
                            )
                            notifyInfo(project, "MSS 数据上报完成")
                        } catch (ex: Exception) {
                            notifyError(project, "MSS 数据上报失败：${ex.message}")
                        }
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

    private fun analyzeApiCoupling(
        project: Project,
        virtualFile: VirtualFile,
        result: MutableMap<OwnResource, Set<UniondrugResource>>
    ) {
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
                            analyRestMethod(project, method, this, mutableSetOf())
                        }
                    }
                }
            }
        }
    }

    /**
     * 分析 REST 接口方法
     */
    private fun analyRestMethod(
        project: Project,
        method: PsiMethod,
        traceNames: MutableSet<UniondrugResource>,
        traceMethods: MutableSet<TraceMethod>
    ) {
        PsiTreeUtil.findChildrenOfType(method, PsiCodeBlock::class.java).forEach { codeBlock ->
            // 从方法解读代码块，只有 PsiStatement 才是程序正确需要运行的元素
            codeBlock.statements.forEach { psiStatement ->
                mutableListOf<PsiMethodCallExpression>().run {
                    addAllMethodCalls(psiStatement, this, traceMethods)
                    forEach {
                        analyMethodExpression(project, it, traceNames, traceMethods)
                    }
                }
            }
        }
    }

    private fun analyMethodExpression(
        project: Project,
        call: PsiMethodCallExpression,
        traceNames: MutableSet<UniondrugResource>,
        traceMethods: MutableSet<TraceMethod>
    ) {
        val express = call.methodExpression.resolve() ?: return
        if (express !is PsiMethod) {
            return
        }
        val file = express.containingFile
        if (file !is PsiJavaFile) {
            return
        }
        if (express.containingClass?.qualifiedName == SPRING_REST_TEMPLATE
            || file.packageName.startsWith(UNIONDRUG_PACKAGE)
        ) {
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
                                            getLiteralValue(this@args[0]) ?: "unknown"
                                        }
                                        tag {
                                            getLiteralValue(this@args[1]) ?: "unknown"
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
                                    "0" // TODO V1 全部解读为内部接口
                                }
                            }
                        } else if (!psiClass.isInterface) {
                            analyRestMethod(project, express, traceNames, traceMethods)
                        }
                    }
                } else {
                    forEach {
                        PsiTreeUtil.getParentOfType(it, PsiClass::class.java)?.let { _ ->
                            analyRestMethod(project, it, traceNames, traceMethods)
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
                                    analyRestMethod(project, onMethod, traceNames, traceMethods)
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
                return@filter qualifiedName.startsWith(UNIONDRUG_PACKAGE)
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
    private fun addAllMethodCalls(
        psiElement: PsiElement,
        list: MutableList<PsiMethodCallExpression>,
        traceMethods: MutableSet<TraceMethod>
    ) {
        psiElement.acceptChildren(object : PsiElementVisitor() {

            override fun visitElement(element: PsiElement) {
                if (element is PsiMethodCallExpression) {
                    val psiMethod = element.methodExpression.resolve() ?: return
                    if (psiMethod !is PsiMethod) {
                        return
                    }
                    val psiClass = psiMethod.containingClass ?: return
                    val traceMethod = TraceMethod(psiClass, psiMethod)
                    if (traceMethod !in traceMethods) {
                        list += element
                        traceMethods += traceMethod
                    }
                } else {
                    addAllMethodCalls(element, list, traceMethods)
                }
            }

        })
    }

    /**
     * 控制无限递归的标识
     */
    data class TraceMethod(
        val psiClass: PsiClass,
        val psiMethod: PsiMethod,
    ) {
        override fun hashCode() = toString().hashCode()

        override fun equals(other: Any?): Boolean {
            if (other == null || other !is TraceMethod) {
                return false
            }
            return toString() == other.toString()
        }

        override fun toString() = "${psiClass.qualifiedName}#${psiMethod.name}"
    }

}