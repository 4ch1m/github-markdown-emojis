package de.achimonline.github_markdown_emojis.helper

import de.achimonline.github_markdown_emojis.helper.GitHubMarkdownEmojisHelper.Companion.createFindRegex
import de.achimonline.github_markdown_emojis.helper.GitHubMarkdownEmojisHelper.Companion.createReplaceRegexes
import de.achimonline.github_markdown_emojis.helper.GitHubMarkdownEmojisHelper.Companion.getEmojis
import de.achimonline.github_markdown_emojis.helper.GitHubMarkdownEmojisHelper.Companion.parseEmojis
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URLEncoder
import kotlin.properties.Delegates

internal class GitHubMarkdownEmojisHelperTest {
    companion object {
        private var emojisJsonString: String by Delegates.notNull()
        private var markdownPanelHtmlString: String by Delegates.notNull()

        private fun getFile(name: String): File = File(GitHubMarkdownEmojisHelperTest::class.java.getResource("/${name}")!!.file)

        @JvmStatic
        @BeforeAll
        fun setUp() {
            emojisJsonString = getFile("emojis.json").readText()
            markdownPanelHtmlString = getFile("markdown-panel.html").readText()
        }
    }

    @Test
    fun `get all available emojis`() {
        val emojiFilePath = getFile("emojis.json").absolutePath

        var emojis: String? = getEmojis("file:///${URLEncoder.encode(emojiFilePath, "utf-8")}")
        assertNotNull(emojis)
        assertTrue(emojis!!.contains("smile"))

        emojis = getEmojis("file:///does-not-exist")
        assertNull(emojis)
    }

    @Test
    fun `parse json`() {
        val emojis = parseEmojis(emojisJsonString)

        assertNotNull(emojis["smile"])
    }

    @Test
    fun `create and test findRegex`() {
        val emojis = parseEmojis(emojisJsonString)
        val regex = createFindRegex(emojis)

        assertEquals(15, regex.findAll(markdownPanelHtmlString).count())
    }

    @Test
    fun `create and test replaceRegexes`() {
        val emojis = parseEmojis(emojisJsonString)
        val findRegex = createFindRegex(emojis)
        val replaceRegexes = createReplaceRegexes(emojis)

        var html = markdownPanelHtmlString

        val findAllResults = findRegex.findAll(html)
        val foundEmojis = findAllResults.map { it.groupValues[2] }.distinct().toList()

        assertEquals(15, findAllResults.count())
        assertEquals(listOf("smile", "dog", "cat"), foundEmojis)

        for (foundEmoji in foundEmojis) {
            for ((regex, replacement) in replaceRegexes[foundEmoji]!!) {
                html = regex.replace(html, replacement)
            }
        }

        assertFalse(findRegex.containsMatchIn(html))
    }
}
