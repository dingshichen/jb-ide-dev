package cn.uniondrug.dev

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * 文档
 */
data class DocumentDTO(
    val id: String,
    val name: String,
    val isFolder: Int,
    val parentId: String,
    val moduleId: String,
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
            throw ProjectException("查询文档失败：${result.msg}")
        }
        return result.data
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
            throw ProjectException("查询文档失败：${result.msg}")
        }
        return result.data
    }

}