package de.achimonline.github_markdown_emojis

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.intellij.openapi.application.ApplicationManager
import de.achimonline.github_markdown_emojis.bundle.GitHubMarkdownEmojisBundle
import de.achimonline.github_markdown_emojis.notification.GitHubMarkdownEmojiNotification
import de.achimonline.github_markdown_emojis.settings.GitHubMarkdownEmojisSettingsState
import org.intellij.plugins.markdown.extensions.MarkdownBrowserPreviewExtension
import org.intellij.plugins.markdown.ui.preview.MarkdownHtmlPanel
import org.intellij.plugins.markdown.ui.preview.ResourceProvider
import org.intellij.plugins.markdown.ui.preview.jcef.MarkdownJCEFHtmlPanel
import java.net.URL
import java.util.*
import kotlin.collections.HashMap

class GitHubMarkdownEmojis(
    panel: MarkdownHtmlPanel,
    startDelay: Long = 1000,
    updateInterval: Long = 1000
) : MarkdownBrowserPreviewExtension {
    init {
        val settingsState = ApplicationManager.getApplication().getService(GitHubMarkdownEmojisSettingsState::class.java)
        val emojis = getEmojis(settingsState.settings.url)

        if (emojis == null) {
            GitHubMarkdownEmojiNotification().notifyError(panel.project, GitHubMarkdownEmojisBundle.message("notifications.error.request"))
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

    companion object {
        fun getEmojis(apiUrl: String): String? {
            return try {
                URL(apiUrl).readText()
            } catch (ex: Exception) {
                null
            }
        }

        fun parseEmojis(jsonString: String): JsonObject {
            return Parser.default().parse(StringBuilder(jsonString)) as JsonObject
        }

        fun createFindRegex(emojis: JsonObject): Regex {
            val joinedKeys = emojis.keys.joinToString(separator = "|") { Regex.escape(it) }

            return ":(<span md-src-pos=\"[0-9]+..[0-9]+\">)?(${joinedKeys})(</span>)?:".toRegex()
        }

        fun createReplaceRegexes(emojis: JsonObject): HashMap<String, List<Pair<Regex, String>>> {
            val map = hashMapOf<String, List<Pair<Regex, String>>>()

            for ((id, url) in emojis) {
                map[id] = listOf(
                    Pair(
                        ":${Regex.escape(id)}:".toRegex(),
                        "<img src=\"${url}\" style=\"height: 1em;\" alt=\"${id}\">"
                    ),
                    Pair(
                        "(:)(<span md-src-pos=\"[0-9]+..[0-9]+\">)(${Regex.escape(id)})(</span>)(:)".toRegex(),
                        "$2<img src=\"${url}\" style=\"height: 1em;\" alt=\"${id}\">$4"
                    )
                )
            }

            return map
        }
    }
}
