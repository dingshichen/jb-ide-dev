package cn.uniondrug.dev.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

/**
 * @author dingshichen
 * @date 2022/6/16
 */
@State(name = "UniondrugDevKitDocSettingService", storages = [Storage("UniondrugDevKitDocSetting.xml")])
class DocSetting : PersistentStateComponent<DocSetting.TornaState> {

    companion object {
        fun getInstance(project: Project): DocSetting = project.getService(DocSetting::class.java)
    }

    private var state = TornaState()

    override fun getState(): TornaState {
        return state
    }

    override fun loadState(state: TornaState) {
        this.state = state
    }

    /**
     * 记住 MSS 功能的输入
     */
    fun saveMssState(mssWorker: String, mssProjectCode: String, mssToken: String) {
        this.state.mssWorker = mssWorker
        this.state.mssProjectCode = mssProjectCode
        this.state.mssToken = mssToken
    }

    data class TornaState(
        var username: String? = null,
        var rememberSpaceBoxId: String? = null,
        var rememberProjectBoxId: String? = null,
        var rememberModuleBoxId: String? = null,
        var mssWorker: String? = null,
        var mssProjectCode: String? = null,
        var mssToken: String? = null,
    )

}