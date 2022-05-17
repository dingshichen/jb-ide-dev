package cn.uniondrug.dev.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

/**
 * @author dingshichen
 * @date 2022/4/11
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

    data class TornaState(
        var domain: String? = null,
        var url: String? = null,
        var token: String? = null,
        var author: String? = null,
    )

}