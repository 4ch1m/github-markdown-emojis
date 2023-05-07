package de.achimonline.github_markdown_emojis.helper

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.net.URL

class GitHubMarkdownEmojisHelper {
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
