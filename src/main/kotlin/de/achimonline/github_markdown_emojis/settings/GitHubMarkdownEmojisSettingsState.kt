package de.achimonline.github_markdown_emojis.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "de.achimonline.github_markdown_emojis.settings.GitHubMarkdownEmojisSettingsState",
    storages = [Storage("GitHubMarkdownEmojis.xml")]
)
class GitHubMarkdownEmojisSettingsState : PersistentStateComponent<GitHubMarkdownEmojisSettingsState?> {
    var settings = GitHubMarkdownEmojisSettings()

    override fun getState(): GitHubMarkdownEmojisSettingsState {
        return this
    }

    override fun loadState(state: GitHubMarkdownEmojisSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: GitHubMarkdownEmojisSettingsState
            get() = ApplicationManager.getApplication().getService(GitHubMarkdownEmojisSettingsState::class.java)
    }
}
