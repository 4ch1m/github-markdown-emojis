package de.achimonline.github_markdown_emojis

import com.intellij.openapi.application.ApplicationManager
import de.achimonline.github_markdown_emojis.bundle.GitHubMarkdownEmojisBundle
import de.achimonline.github_markdown_emojis.helper.GitHubMarkdownEmojisHelper.Companion.createFindRegex
import de.achimonline.github_markdown_emojis.helper.GitHubMarkdownEmojisHelper.Companion.createReplaceRegexes
import de.achimonline.github_markdown_emojis.helper.GitHubMarkdownEmojisHelper.Companion.getEmojis
import de.achimonline.github_markdown_emojis.helper.GitHubMarkdownEmojisHelper.Companion.parseEmojis
import de.achimonline.github_markdown_emojis.notification.GitHubMarkdownEmojisNotification
import de.achimonline.github_markdown_emojis.settings.GitHubMarkdownEmojisSettingsState
import org.intellij.plugins.markdown.extensions.MarkdownBrowserPreviewExtension
import org.intellij.plugins.markdown.ui.preview.MarkdownHtmlPanel
import org.intellij.plugins.markdown.ui.preview.ResourceProvider
import org.intellij.plugins.markdown.ui.preview.jcef.MarkdownJCEFHtmlPanel
import java.util.*

class GitHubMarkdownEmojis(
    panel: MarkdownHtmlPanel,
    startDelay: Long = 1000,
    updateInterval: Long = 1000
) : MarkdownBrowserPreviewExtension {
    init {
        val settingsState =
            ApplicationManager.getApplication().getService(GitHubMarkdownEmojisSettingsState::class.java)
        val emojis = getEmojis(settingsState.settings.url)

        if (emojis == null) {
            GitHubMarkdownEmojisNotification().notifyError(
                panel.project,
                GitHubMarkdownEmojisBundle.message("notifications.error.request")
            )
        } else {
            val parsedEmojis = parseEmojis(emojis)

            val findRegex = createFindRegex(parsedEmojis)
            val replaceRegexes = createReplaceRegexes(parsedEmojis)

            Timer().schedule(
                object : TimerTask() {
                    override fun run() {
                        if (panel is MarkdownJCEFHtmlPanel) {
                            panel.cefBrowser.getSource { source ->
                                val findAllResults = findRegex.findAll(source)

                                if (findAllResults.count() > 0) {
                                    var newSource = source

                                    for (result in findAllResults.map { it.groupValues[2] }.distinct().toList()) {
                                        for ((regex, replacement) in replaceRegexes[result]!!) {
                                            newSource = regex.replace(newSource, replacement)
                                        }
                                    }

                                    panel.setHtml(newSource, 0, panel.virtualFile!!.toNioPath())
                                }
                            }
                        }
                    }
                },
                startDelay,
                updateInterval
            )
        }
    }

    class Provider : MarkdownBrowserPreviewExtension.Provider {
        override fun createBrowserExtension(panel: MarkdownHtmlPanel): MarkdownBrowserPreviewExtension {
            return GitHubMarkdownEmojis(panel)
        }
    }

    override fun dispose() = Unit

    override val priority: MarkdownBrowserPreviewExtension.Priority
        get() = MarkdownBrowserPreviewExtension.Priority.AFTER_ALL

    override val resourceProvider: ResourceProvider
        get() = ResourceProvider.DefaultResourceProvider()

    override fun compareTo(other: MarkdownBrowserPreviewExtension) = 0

    override val styles: List<String>
        get() = emptyList()

    override val scripts: List<String>
        get() = emptyList()
}
