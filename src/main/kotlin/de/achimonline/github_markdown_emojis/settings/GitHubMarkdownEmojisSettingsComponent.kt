package de.achimonline.github_markdown_emojis.settings

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import de.achimonline.github_markdown_emojis.bundle.GitHubMarkdownEmojisBundle
import javax.swing.JPanel

class GitHubMarkdownEmojisSettingsComponent {
    val panel: JPanel
    val textField: JBTextField = JBTextField()

    init {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel(GitHubMarkdownEmojisBundle.message("settings.url.label")), textField, 1, true)
            .addComponent(JBLabel(GitHubMarkdownEmojisBundle.message("settings.url.info")))
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    var url: String
        get() = textField.text
        set(newText) {
            textField.text = newText
        }
}
