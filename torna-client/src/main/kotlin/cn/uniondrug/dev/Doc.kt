package cn.uniondrug.dev

import cn.uniondrug.dev.util.DigestUtils
import cn.uniondrug.dev.util.IdUtil
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.nio.charset.Charset

/**
 * 文档
 */
data class DocumentDTO(
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
data class DocParamSaveCmd (
    /** 字段名称, 数据库字段：name  */
    val name: String,
    /** 字段类型, 数据库字段：type  */
    val type: String,
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
    var children: List<DocParamSaveCmd>? = null,
) {
    var id: String? = null
    /** 示例值, 数据库字段：example  */
    val example: String = ""
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
    val orderIndex: Int? = null
    val isDeleted: Byte = 0
}

/**
 * 新增文档入参
 */
data class DocInfoSaveCmd (
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
    val requestParams: List<DocParamSaveCmd>? = null,
    val responseParams: List<DocParamSaveCmd>? = null,
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
    val errorCodeParams: List<DocParamSaveCmd>? = null
    val globalHeaders: List<DocParamSaveCmd>? = null
    val globalParams: List<DocParamSaveCmd>? = null
    val globalReturns: List<DocParamSaveCmd>? = null
}

data class DocIdCmd(
    val id: String
)

class DocumentService {

    /**
     * 查询模块里的文件夹
     */
    fun listFolderByModule(host: String, token: String, moduleId: String): List<DocumentDTO> {
        val body = doGet("$host/doc/folder/list?moduleId=$moduleId", token)
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val result: Result<List<DocumentDTO>> = gson.fromJson(body, object : TypeToken<Result<List<DocumentDTO>>>() {}.type!!)
        if (result.isError()) {
            throw DocumentException("查询文档失败：${result.msg}")
        }
        return result.data!!
    }

    /**
     * 查询模块里的文档
     */
    fun listDocumentByModule(host: String, token: String, moduleId: String): List<DocumentDTO> {
        val body = doGet("$host/doc/list?moduleId=$moduleId", token)
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val result: Result<List<DocumentDTO>> = gson.fromJson(body, object : TypeToken<Result<List<DocumentDTO>>>() {}.type!!)
        if (result.isError()) {
            throw DocumentException("查询文档失败：${result.msg}")
        }
        return result.data!!
    }

    /**
     * 创建目录
     */
    fun saveFolder(host: String, token: String, moduleId: String, folder: String) {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val folderAddCmd = FolderAddCmd(moduleId, folder)
        val body = doPost("$host/doc/folder/add", gson.toJson(folderAddCmd), token)
        val result: Result<*> = gson.fromJson(body, object : TypeToken<Result<*>>() {}.type!!)
        if (result.isError()) {
            throw DocumentException("创建目录失败：${result.msg}")
        }
    }

    /**
     * 文档详情查询
     */
    fun getDocumentDetail(host: String, token: String, docId: String): DocumentDTO? {
        val body = doGet("$host/doc/detail?id=$docId", token)
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val result: Result<DocumentDTO> = gson.fromJson(body, object : TypeToken<Result<DocumentDTO>>() {}.type!!)
        if (result.isError()) {
            throw DocumentException("查询文档失败：${result.msg}")
        }
        return result.data
    }

    /**
     * 查询文档是否存在
     */
    fun existDocument(host: String, token: String, docId: String) = getDocumentDetail(host, token, docId) != null

    /**
     * 保存文档（可覆盖）
     */
    fun saveDocument(host: String, token: String, projectId: String, moduleId: String, folderId: String, api: Api) {
        val docId = getDocDataId(folderId, moduleId, api.url, api.httpMethod)
        val doc = getDocumentDetail(host, token, docId)
        doc?.let {
            // 先删除
            deleteDocument(host, token, it.id)
        }
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val docSaveCmd = apiToCmd(projectId, moduleId, folderId, api)
        val body = doPost("$host/doc/save", gson.toJson(docSaveCmd), token)
        val result: Result<*> = gson.fromJson(body, object : TypeToken<Result<*>>() {}.type!!)
        if (result.isError()) {
            throw DocumentException("保存文档失败：${result.msg}")
        }
    }

    /**
     * 删除文档
     */
    fun deleteDocument(host: String, token: String, docId: String) {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()
        val body = doPost("$host/doc/delete", gson.toJson(DocIdCmd(docId)), token)
        val result: Result<*> = gson.fromJson(body, object : TypeToken<Result<*>>() {}.type!!)
        if (result.isError()) {
            throw DocumentException("删除文档失败：${result.msg}")
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
        )
    }

    private fun getDocDataId(parentId: String? = null, moduleId: String, url: String, httpMethod: String): String {
        val parent: Long = parentId?.let { IdUtil.decode(it) } ?: 0L
        val content = "$moduleId:$parent:$url:$httpMethod"
        return DigestUtils.md5DigestAsHex(content.toByteArray(Charset.defaultCharset()))
    }

    private fun apiParamToDocParamSaveCmd(
        docId: String,
        params: List<ApiParam>? = null,
        parentId: String? = null,
        style: Byte
    ): List<DocParamSaveCmd>? {
        if (params == null) {
            return null
        }
        val saveParamCmds = mutableListOf<DocParamSaveCmd>()
        params.forEach {
            val saveCmd = DocParamSaveCmd(
                name = it.name,
                type = it.type.value,
                required = if (it.required) 1 else 0,
                maxLength = it.maxLength,
                description = it.description,
                docId = docId,
                parentId = parentId,
                style = style,
            )
            it.children?.let { children ->
                saveCmd.children = apiParamToDocParamSaveCmd(docId, children, "getParamId()", style)
            }
            saveParamCmds += saveCmd
        }
        return saveParamCmds
    }
}