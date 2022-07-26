package cn.uniondrug.dev

import cn.uniondrug.dev.util.DigestUtils
import cn.uniondrug.dev.util.IdUtil
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.nio.charset.Charset

/**
 * 文档
 */
data class TornaDocDTO(
    val id: String,
    val name: String,
    /**
     * 是否是分类，0：不是，1：是, 数据库字段：is_folder
     */
    val isFolder: Int,
    val description: String,
    val author: String,
    val url: String,
    val httpMethod: String,
    val contentType: String,
    val deprecated: String,
    val parentId: String,
    val moduleId: String,
    val projectId: String,
    val creatorName: String,
    val modifierName: String,
    val requestParams: MutableList<DocParamSaveCmd>,
    val responseParams: MutableList<DocParamSaveCmd>,
    val errorCodeParams: MutableList<DocParamSaveCmd>,
) {

    /**
     * 0:http,1:dubbo, 数据库字段：protocol
     */
    val type = 0

    /**
     * 是否显示, 数据库字段：is_show
     */
    val isShow = 1
    val remark = ""

    override fun toString() = name
}

/**
 * 添加目录
 */
data class FolderAddCmd(
    val moduleId: String,
    val name: String,
    var parentId: String? = null,
)

/**
 * 字段保存入参数
 */
data class DocParamSaveCmd(
    /** 字段名称, 数据库字段：name  */
    val name: String,
    /** 字段类型, 数据库字段：type  */
    val type: String,
    /** 示例值, 数据库字段：example  */
    val example: String,
    /** 是否必须，1：是，0：否, 数据库字段：required  */
    val required: Byte,
    /** 最大长度, 数据库字段：max_length  */
    val maxLength: String? = null,
    /** 描述, 数据库字段：description  */
    val description: String? = null,
    /** doc_info.id, 数据库字段：doc_id  */
    val docId: String,
    /** 父节点, 数据库字段：parent_id  */
    val parentId: String? = null,
    /** 0：header, 1：请求参数，2：返回参数，3：错误码, 数据库字段：style  */
    val style: Byte,
    var children: MutableList<DocParamSaveCmd>? = null,
) {
    var id: String? = null
    val enumId: String = ""

    /** 新增操作方式，0：人工操作，1：开放平台推送, 数据库字段：create_mode  */
    val createMode: Byte = 1

    /** 修改操作方式，0：人工操作，1：开放平台推送, 数据库字段：modify_mode  */
    val modifyMode: Byte = 1

    /** 创建人  */
    val creatorName: String? = null

    /** 修改人  */
    val modifierName: String? = null

    /** 排序  */
    var orderIndex: Int? = null
    var isDeleted: Byte = 0

    /**
     * 认为是同一个
     */
    infix fun same(docParamSaveCmd: DocParamSaveCmd) = this.name == docParamSaveCmd.name

    /**
     * 重写 equals 便于比较
     */
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is DocParamSaveCmd) {
            return false
        }
        return this same other
    }

    override fun hashCode(): Int {
        return this.name.hashCode()
    }
}

/**
 * 新增文档入参
 */
data class DocInfoSaveCmd(
    val name: String,
    /** 文档概述, 数据库字段：description  */
    val description: String,
    /** 维护人, 数据库字段：author  */
    val author: String,
    /** 访问URL, 数据库字段：url  */
    val url: String,
    /** http方法, 数据库字段：http_method  */
    val httpMethod: String,
    /** contentType, 数据库字段：content_type  */
    val contentType: String,
    /** 父节点, 数据库字段：parent_id  */
    val parentId: String,
    /** 模块id，module.id, 数据库字段：module_id  */
    val moduleId: String,
    /** 项目id  */
    val projectId: String,
    val requestParams: MutableList<DocParamSaveCmd>,
    val responseParams: MutableList<DocParamSaveCmd>,
    val errorCodeParams: MutableList<DocParamSaveCmd>,
) {
    var id: String? = null
    val type = 0

    /** 是否是分类，0：不是，1：是, 数据库字段：is_folder  */
    val isFolder = 0
    val isShow = 1
    val remark = ""

    /** 是否使用全局请求参数, 数据库字段：is_use_global_headers  */
    val isUseGlobalHeaders = 0

    /** 是否使用全局请求参数, 数据库字段：is_use_global_params  */
    val isUseGlobalParams = 0

    /** 是否使用全局返回参数, 数据库字段：is_use_global_returns  */
    val isUseGlobalReturns = 0

    /** 是否请求数组, 数据库字段：is_request_array  */
    val isRequestArray = 0

    /** 是否返回数组, 数据库字段：is_response_array  */
    val isResponseArray = 0

    /** 请求数组时元素类型, 数据库字段：request_array_type  */
    val requestArrayType = 0

    /** 返回数组时元素类型, 数据库字段：response_array_type  */
    val responseArrayType = 0

    /** 创建人  */
    val creatorName = ""

    /** 修改人  */
    val modifierName = ""
    val pathParams: List<DocParamSaveCmd>? = null
    val headerParams: List<DocParamSaveCmd>? = null
    val queryParams: List<DocParamSaveCmd>? = null
    val globalHeaders: List<DocParamSaveCmd>? = null
    val globalParams: List<DocParamSaveCmd>? = null
    val globalReturns: List<DocParamSaveCmd>? = null
}

data class DocIdDTO(
    val id: String
)

class TornaDocService {

    /**
     * 查询模块里的文件夹
     */
    fun listFolderByModule(token: String, moduleId: String, loginFailBack: (() -> String)? = null): List<TornaDocDTO> {
        val body = doGetTorna("/doc/folder/list?moduleId=$moduleId", token)
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val tornaResult: TornaResult<List<TornaDocDTO>> =
            gson.fromJson(body, object : TypeToken<TornaResult<List<TornaDocDTO>>>() {}.type)
        if (tornaResult.isLoginError()) {
            loginFailBack?.let {
                return listFolderByModule(it(), moduleId)
            }
            throw LoginException(tornaResult.msg)
        }
        if (tornaResult.isError()) {
            throw DocumentException("查询文档失败：${tornaResult.msg}")
        }
        return tornaResult.data!!
    }

    /**
     * 查询模块里的文档
     */
    fun listDocumentByModule(
        token: String,
        moduleId: String,
        loginFailBack: (() -> String)? = null
    ): List<TornaDocDTO> {
        val body = doGetTorna("/doc/list?moduleId=$moduleId", token)
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val tornaResult: TornaResult<List<TornaDocDTO>> =
            gson.fromJson(body, object : TypeToken<TornaResult<List<TornaDocDTO>>>() {}.type)
        if (tornaResult.isLoginError()) {
            loginFailBack?.let {
                listDocumentByModule(it(), moduleId)
            }
            throw LoginException(tornaResult.msg)
        }
        if (tornaResult.isError()) {
            throw DocumentException("查询文档失败：${tornaResult.msg}")
        }
        return tornaResult.data!!
    }

    /**
     * 创建目录
     */
    fun saveFolder(token: String, moduleId: String, folder: String, loginFailBack: (() -> String)? = null) {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val folderAddCmd = FolderAddCmd(moduleId, folder)
        val body = doPostTorna("/doc/folder/add", gson.toJson(folderAddCmd), token)
        val tornaResult: TornaResult<*> = gson.fromJson(body, object : TypeToken<TornaResult<*>>() {}.type)
        if (tornaResult.isLoginError()) {
            loginFailBack?.let {
                saveFolder(it(), moduleId, folder)
            }
            throw LoginException(tornaResult.msg)
        }
        if (tornaResult.isError()) {
            throw DocumentException("创建目录失败：${tornaResult.msg}")
        }
    }

    /**
     * 文档详情查询
     */
    fun getDocDetail(token: String, docId: String, loginFailBack: (() -> String)? = null): TornaDocDTO? {
        val body = doGetTorna("/doc/detail?id=$docId", token)
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val tornaResult: TornaResult<TornaDocDTO> =
            gson.fromJson(body, object : TypeToken<TornaResult<TornaDocDTO>>() {}.type)
        if (tornaResult.isLoginError()) {
            loginFailBack?.let {
                getDocDetail(it(), docId)
            }
            throw LoginException(tornaResult.msg)
        }
        if (tornaResult.isError()) {
            throw DocumentException("查询文档失败：${tornaResult.msg}")
        }
        return tornaResult.data
    }

    /**
     * 保存文档
     */
    fun saveDoc(
        token: String,
        projectId: String,
        moduleId: String,
        folderId: String,
        api: Api,
        loginFailBack: (() -> String)? = null
    ): String {
        // 初步组装参数
        val docSaveCmd = apiToCmd(projectId, moduleId, folderId, api)
        // 查询模块下所有文档
        val docs = listDocumentByModule(token, moduleId)
        // 计算 docDataId
        val docId = getDocDataId(folderId, moduleId, api.url, api.httpMethod)
        // 获取到 docDataId 一致的就更新、否则直接新增即可
        docs.find {
            getDocDataId(it.parentId, moduleId, it.url, it.httpMethod) == docId
        }?.let { tornaDoc ->
            // 将匹配到的文档 ID 赋值于自己才能在 Torna 那边去通过 ID 更新
            docSaveCmd.id = tornaDoc.id
            // 查询详情，获取到其所有的字段
            getDocDetail(token, tornaDoc.id, loginFailBack)?.let {
                // 递归填充参数
                recursiveFillParamCmd(
                    docSaveCmd.requestParams,
                    it.requestParams.filter { param -> param.parentId.isNullOrBlank() },    // parentId 为空的，就是第一级参数
                    it.requestParams
                )
                recursiveFillParamCmd(
                    docSaveCmd.responseParams,
                    it.responseParams.filter { param -> param.parentId.isNullOrBlank() },
                    it.responseParams
                )
                recursiveFillParamCmd(
                    docSaveCmd.errorCodeParams,
                    it.errorCodeParams,
                    it.errorCodeParams
                )
            }
        }
        // 保存
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val body = doPostTorna("/doc/save", gson.toJson(docSaveCmd), token)
        val tornaResult: TornaResult<DocIdDTO> = gson.fromJson(body, object : TypeToken<TornaResult<DocIdDTO>>() {}.type)
        if (tornaResult.isLoginError()) {
            return loginFailBack?.let { saveDoc(it(), projectId, moduleId, folderId, api) } ?: throw LoginException(tornaResult.msg)
        }
        if (tornaResult.isError()) {
            throw DocumentException("保存文档失败：${tornaResult.msg}")
        }
        return tornaResult.data?.id ?: ""
    }

    /**
     * 递归填充参数
     * @param saveParams 保存的入参
     * @param tornaParams 从 Torna 查询到的文档里匹配到对应同级的参数
     * @param tornaAllParams 从 Torna 查询到的所有字段，Torna 文档接口里的字段格式是平级的，只是靠 parentId 建立关系
     */
    private fun recursiveFillParamCmd(
        saveParams: MutableList<DocParamSaveCmd>,
        tornaParams: List<DocParamSaveCmd>?,
        tornaAllParams: List<DocParamSaveCmd>?
    ) {
        saveParams.forEachIndexed { idx, paramCmd ->
            tornaParams?.find { it == paramCmd }?.let {
                paramCmd.id = it.id
                paramCmd.children?.let { children ->
                    recursiveFillParamCmd(
                        children,
                        tornaAllParams?.filter { tornaParam -> tornaParam.parentId == it.id },
                        tornaAllParams
                    )
                }
            }
            // 以解析到的字段顺序为 Torna 的字段顺序
            paramCmd.orderIndex = idx
        }
        // 如果 Torna 上有我们这里没有的字段，那么设置其为 deleted
        tornaParams?.forEach { tornaParam ->
            if (tornaParam !in saveParams) {
                tornaParam.isDeleted = 1
                saveParams += tornaParam
            }
        }
    }

    /**
     * 删除文档
     */
    fun deleteDoc(token: String, docId: String, loginFailBack: (() -> String)? = null) {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val body = doPostTorna("/doc/delete", gson.toJson(DocIdDTO(docId)), token)
        val tornaResult: TornaResult<*> = gson.fromJson(body, object : TypeToken<TornaResult<*>>() {}.type)
        if (tornaResult.isLoginError()) {
            loginFailBack?.let {
                deleteDoc(it(), docId)
            }
            throw LoginException(tornaResult.msg)
        }
        if (tornaResult.isError()) {
            throw DocumentException("删除文档失败：${tornaResult.msg}")
        }
    }

    private fun apiToCmd(projectId: String, moduleId: String, folderId: String, api: Api): DocInfoSaveCmd {
        val docId = getDocDataId(folderId, moduleId, api.url, api.httpMethod)
        return DocInfoSaveCmd(
            name = api.name,
            description = api.description ?: "",
            author = api.author ?: "",
            url = api.url,
            httpMethod = api.httpMethod,
            contentType = api.contentType,
            parentId = folderId,
            moduleId = moduleId,
            projectId = projectId,
            requestParams = apiParamToDocParamSaveCmd(docId, api.requestParams, style = 1),
            responseParams = apiParamToDocParamSaveCmd(docId, api.responseParams, style = 2),
            errorCodeParams = apiErrorsToDocParamSaveCmd(docId, api.errorParams, style = 3),
        )
    }

    private fun getDocDataId(parentId: String? = null, moduleId: String, url: String, httpMethod: String): String {
        val parent: Long = parentId?.let { IdUtil.decode(it) } ?: 0L
        val content = "${IdUtil.decode(moduleId)}:$parent:$url:$httpMethod"
        return DigestUtils.md5DigestAsHex(content.toByteArray(Charset.defaultCharset()))
    }

    private fun apiParamToDocParamSaveCmd(
        docId: String,
        params: List<ApiParam>? = null,
        parentId: String? = null,
        style: Byte
    ): MutableList<DocParamSaveCmd> {
        if (params == null) {
            return mutableListOf()
        }
        val saveParamCmds = mutableListOf<DocParamSaveCmd>()
        params.forEach {
            val saveCmd = DocParamSaveCmd(
                name = it.name,
                type = it.type.simpleValue,
                example = it.getExampleText(),
                required = if (it.required) 1 else 0,
                maxLength = it.maxLength,
                description = it.description,
                docId = docId,
                parentId = parentId,
                style = style,
            )
            it.children?.let { children ->
                saveCmd.children = apiParamToDocParamSaveCmd(docId, children, style = style)
            }
            saveParamCmds += saveCmd
        }
        return saveParamCmds
    }

    private fun apiErrorsToDocParamSaveCmd(
        docId: String,
        errorParams: List<ApiErrno>?,
        style: Byte
    ): MutableList<DocParamSaveCmd> {
        if (errorParams == null) {
            return mutableListOf()
        }
        return errorParams.map {
            DocParamSaveCmd(
                name = it.errno,
                type = "string",
                example = it.remark,
                required = 1,
                description = it.error,
                docId = docId,
                style = style,
            )
        }.toMutableList()
    }

    /**
     * 获取文档视图 URL
     */
    fun getDocViewUrl(docId: String) = "$TORNA_WEB/#/view/$docId"

}