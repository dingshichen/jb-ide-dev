package cn.uniondrug.dev.mss

import java.util.*

/**
 * @author dingshichen
 * @date 2022/6/16
 */
class MssService {

    /**
     * 上传资源
     */
    fun upload(
        mssProjectService: MssProjectService,
        mssApiService: MssApiService,
        properties: Properties,
        worker: String,
        projectCode: String,
        token: String,
        resourceSetMutableMap: MutableMap<OwnResource, Set<UniondrugResource>>
    ) {
        val projectId = mssProjectService.getProjectId(token, projectCode, worker) ?: throw MssException("根据配置获取不到项目ID")
        val apiList = mssApiService.listApiByProject(token, projectId)
        if (apiList.isEmpty()) throw MssException("此项目没有需要维护的接口")
        val apiMap = apiList.associateBy { it.apiUrl }
        resourceSetMutableMap.forEach { (own, set) ->
            apiMap[own.path]?.let { api ->
                val calls = mssApiService.listCallByApi(token, projectId, api.id)
                val mbs = mssApiService.listMbs(token, projectId, api.id)
                val callMap = calls.associateBy { it.url }
                val mbsMap = mbs.associateBy { it.topic + it.tag }
                set.forEach {
                    if (it is RPCResource) {
                        if (it.path !in callMap) {
                            it.replaceValue(properties)
                            mssApiService.addCall(token, projectId, api.id, it.serverUrlExpress!!, it.path!!, it.thirdFlag!!)
                        }
                    } else if (it is MbsResource) {
                        if (it.topic + it.tag !in mbsMap) {
                            mssApiService.addMbs(token, projectId, api.id, it.topic!!, it.tag!!, it.channel!!.value)
                        }
                    }
                }
            }
        }
    }

}