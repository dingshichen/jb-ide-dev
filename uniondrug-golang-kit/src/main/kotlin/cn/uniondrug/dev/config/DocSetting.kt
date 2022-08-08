package cn.uniondrug.dev.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

/**
 * @author dingshichen
 * @date 2022/6/16
 */
@State(name = "UniondrugDevKitDocSettingService", storages = [Storage("UniondrugDevKitDocSetting.xml")])
class DocSetting : PersistentStateComponent<DocSetting.TornaState> {

    companion object {

        @JvmStatic
        fun instance(project: Project): DocSetting = project.service()
    }

    private var state = TornaState()

    override fun getState(): TornaState {
        return state
    }

    override fun loadState(state: TornaState) {
        this.state = state
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