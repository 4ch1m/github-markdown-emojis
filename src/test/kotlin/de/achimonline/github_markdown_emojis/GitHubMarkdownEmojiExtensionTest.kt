package de.achimonline.github_markdown_emojis

import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.net.URLEncoder
import kotlin.properties.Delegates

internal class GitHubMarkdownEmojiExtensionTest {
    private fun getFile(name: String): File = File(javaClass.getResource("/${name}")!!.file)

    var emojisJsonString: String by Delegates.notNull()
    var markdownPanelHtmlString: String by Delegates.notNull()

    @Before
    fun setUp() {
        emojisJsonString = getFile("emojis.json").readText()
        markdownPanelHtmlString = getFile("markdown-panel.html").readText()
    }

    @Test
    fun `get all available emojis`() {
        val emojiFilePath = getFile("emojis.json").absolutePath
        var emojis: String?

        emojis = GitHubMarkdownEmojis.getEmojis("file:///${URLEncoder.encode(emojiFilePath, "utf-8")}")
        assertNotNull(emojis)
        assertTrue(emojis!!.contains("smile"))

        emojis = GitHubMarkdownEmojis.getEmojis("file:///does-not-exist")
        assertNull(emojis)
    }

    @Test
    fun `parse json`() {
        val emojis = GitHubMarkdownEmojis.parseEmojis(emojisJsonString)

        assertNotNull(emojis["smile"])
    }

    @Test
    fun `create and test findRegex`() {
        val emojis = GitHubMarkdownEmojis.parseEmojis(emojisJsonString)
        val regex = GitHubMarkdownEmojis.createFindRegex(emojis)

        assertEquals(15, regex.findAll(markdownPanelHtmlString).count())
    }

    @Test
    fun `create and test replaceRegexes`() {
        val emojis = GitHubMarkdownEmojis.parseEmojis(emojisJsonString)
        val findRegex = GitHubMarkdownEmojis.createFindRegex(emojis)
        val replaceRegexes = GitHubMarkdownEmojis.createReplaceRegexes(emojis)

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
