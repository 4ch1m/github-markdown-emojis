package de.achimonline.github_markdown_emojis.toolwindow

import com.beust.klaxon.JsonObject
import de.achimonline.github_markdown_emojis.helper.GitHubMarkdownEmojisHelper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.properties.Delegates

internal class GitHubMarkdownEmojisToolWindowFactoryTest {
    companion object {
        private var toolWindowFactory: GitHubMarkdownEmojisToolWindowFactory by Delegates.notNull()
        private var emojis: JsonObject by Delegates.notNull()
        private var imgRegex: Regex by Delegates.notNull()

        @JvmStatic
        @BeforeAll
        fun setUp() {
            toolWindowFactory = GitHubMarkdownEmojisToolWindowFactory()

            emojis = GitHubMarkdownEmojisHelper.parseEmojis("""{
                "emoji1": "url1",
                "emoji2": "url2",
                "emoji3": "url3"
            }""")

            imgRegex = Regex("<img ")
        }
    }

    @Test
    fun `build HTML with given Emojis - no filter`() {
        val html = toolWindowFactory.buildHtml(emojis)

        listOf("<html", "<body", "<script").forEach {
            assertTrue(html.contains(it))
        }

        assertEquals(3, imgRegex.findAll(html).count())
    }

    @Test
    fun `build HTML with given Emojis - filtered`() {
        val html = toolWindowFactory.buildHtml(emojis, "emoji2")

        assertEquals(1, imgRegex.findAll(html).count())
    }
}
