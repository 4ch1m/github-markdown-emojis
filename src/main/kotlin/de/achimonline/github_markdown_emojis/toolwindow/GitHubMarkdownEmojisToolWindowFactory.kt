package de.achimonline.github_markdown_emojis.toolwindow

import com.beust.klaxon.JsonObject
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.ColorUtil.toHtmlColor
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.jcef.JCEFHtmlPanel
import com.intellij.util.ui.JBUI.CurrentTheme
import de.achimonline.github_markdown_emojis.bundle.GitHubMarkdownEmojisBundle.message
import de.achimonline.github_markdown_emojis.helper.GitHubMarkdownEmojisHelper.Companion.getEmojis
import de.achimonline.github_markdown_emojis.helper.GitHubMarkdownEmojisHelper.Companion.parseEmojis
import de.achimonline.github_markdown_emojis.settings.GitHubMarkdownEmojisSettingsState

class GitHubMarkdownEmojisToolWindowFactory: ToolWindowFactory, DumbAware {
    private var emojis: JsonObject? = null

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val settingsState =
            ApplicationManager.getApplication().getService(GitHubMarkdownEmojisSettingsState::class.java)

        if (emojis == null) {
            getEmojis(settingsState.settings.url)?.let {
                emojis = parseEmojis(it)
            }
        }

        val toolWindowPanel: DialogPanel

        if (emojis != null) {
            val html = buildHtml(emojis!!)
            val htmlPanel = JCEFHtmlPanel("about:blank")
            htmlPanel.setHtml(html)

            toolWindowPanel = panel {
                row {
                    textField()
                        .label(message("toolwindow.filter"))
                        .onChanged { filter ->
                            htmlPanel.setHtml(buildHtml(emojis!!, filter.text))
                        }
                        .align(Align.FILL)
                }.topGap(TopGap.SMALL)
                row {
                    cell(htmlPanel.component)
                        .align(Align.FILL)
                        .comment(message("toolwindow.comment"))
                }
            }
        } else {
            toolWindowPanel = panel {
                row {
                    text(message("toolwindow.unable-to-retrieve"))
                }
            }
        }

        toolWindow.contentManager.addContent(
            ContentFactory
                .getInstance()
                .createContent(toolWindowPanel, null, false)
        )
    }

    fun buildHtml(emojis: JsonObject, filter: String? = null): String {
        var html = """
            <html>
                <body style='background-color: ${toHtmlColor(CurrentTheme.ToolWindow.background())};'>
                    <script>
                        function copyToClipboard(id) {
                            let range = document.createRange();
                            range.selectNode(document.getElementById(id));
                            window.getSelection().removeAllRanges();
                            window.getSelection().addRange(range);
                            document.execCommand('copy');
                            window.getSelection().removeAllRanges();
                        }
                    </script>
        """

        for ((id, url) in emojis) {
            if (filter == null || id.contains(filter.trim(), true)) {
                html += """
                    <figure title='${id}' style='text-align: center; float: left;' onclick='copyToClipboard("$id");'>
                        <img src='${url}' alt='${id}' style='height: 2em;'>
                        <figcaption style='color: ${toHtmlColor(CurrentTheme.Label.foreground())};'>
                            <small id='${id}'>
                                <span style='color: ${toHtmlColor(CurrentTheme.Label.disabledForeground())}'>:</span>${id}<span style='color: ${toHtmlColor(CurrentTheme.Label.disabledForeground())}'>:</span>
                            </small>
                        </figcaption>
                    </figure>
                """
            }
        }

        return "${html}</body></html>"
    }
}
