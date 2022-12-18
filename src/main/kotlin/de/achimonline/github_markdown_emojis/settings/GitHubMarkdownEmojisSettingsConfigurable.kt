package de.achimonline.github_markdown_emojis.settings

import com.intellij.openapi.options.Configurable
import de.achimonline.github_markdown_emojis.bundle.GitHubMarkdownEmojisBundle
import javax.swing.JComponent

class GitHubMarkdownEmojisSettingsConfigurable : Configurable {
    private var settingsComponent: GitHubMarkdownEmojisSettingsComponent? = null

    override fun getDisplayName(): String {
        return GitHubMarkdownEmojisBundle.message("settings.display.name")
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return settingsComponent!!.textField
    }

    override fun createComponent(): JComponent? {
        settingsComponent = GitHubMarkdownEmojisSettingsComponent()
        return settingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        return settingsComponent!!.url != GitHubMarkdownEmojisSettingsState.instance.url
    }

    override fun apply() {
        val settings: GitHubMarkdownEmojisSettingsState = GitHubMarkdownEmojisSettingsState.instance
        settings.url = settingsComponent!!.url
    }

    override fun reset() {
        val settings: GitHubMarkdownEmojisSettingsState = GitHubMarkdownEmojisSettingsState.instance
        settingsComponent!!.url = settings.url
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}
