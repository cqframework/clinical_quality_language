package org.hl7.cql.ast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CommentStyle {
    /** `// comment` */
    @SerialName("line") LINE,

    /** `/* comment */` */
    @SerialName("block") BLOCK,
}

/** A comment as written, including its `//` or `/* */` delimiters. */
@Serializable
data class Comment(
    val text: String,
    val style: CommentStyle,
    override val locator: Locator = Locator.UNKNOWN,
) : AstNode

/** An `@name: value` tag from the comments before a declaration, such as `@author: Jane`. */
@Serializable data class Tag(val name: String, val value: String? = null)

/**
 * A top-level declaration (the library header, a [Definition] or a [Statement]), which carries the
 * comments around it and the tags they declare.
 */
interface Declaration : AstNode {
    /**
     * The comments that belong to this declaration, in source order: those on the lines before it
     * (after the previous declaration's last line), those within it, and those after it on its last
     * line. Comments after the last declaration of a library belong to it too.
     */
    val comments: List<Comment>

    /**
     * The tags in the comments between the previous declaration and this one, as the translator
     * reads them into ELM annotations.
     */
    val tags: List<Tag>
}

/**
 * Reads tags from the text between two declarations, as the translator always has: the text of each
 * comment is searched for `@name`, optionally followed by `:` and a value that runs to the next
 * tag. A value can be a date (`@2020-01-01`) or begin with a quoted parameter name.
 */
object TagParser {

    /** The ` * ` that begins each continued line of a `/** ... */` comment. */
    private val continuationLinePrefix = Regex("\n[ \t]*\\*[ \t\\*]*")

    fun parse(header: String): List<Tag> = parseTags(commentText(header))

    /** The text of the comments in [header], without their delimiters, one line per line. */
    private fun commentText(header: String): String {
        val result = ArrayList<String>()
        var inBlock = false
        for (line in header.replace("\r\n", "\n").split("\n")) {
            val blockStart = line.indexOf("/*")
            val lineCommentStart = line.indexOf("//")
            when {
                inBlock -> {
                    val end = line.indexOf("*/")
                    inBlock = end < 0
                    if (inBlock) result.add(line)
                    else if (end > 0) result.add(line.substring(0, end))
                }
                blockStart >= 0 -> {
                    val content = line.substring(blockStart + 2)
                    result.add(if (line.endsWith("*/")) content.dropLast(2) else content)
                    // As the translator always has, the block is taken to continue on the next
                    // line even when it ends on this one.
                    inBlock = true
                }
                lineCommentStart >= 0 -> result.add(line.substring(lineCommentStart + 2))
            }
        }
        return result.joinToString("\n")
    }

    private fun parseTags(text: String): List<Tag> {
        val header = text.trim { it <= ' ' }.split(continuationLinePrefix).joinToString("\n")
        val tags = ArrayList<Tag>()
        var startFrom = 0
        while (startFrom < header.length) {
            val (name, afterName) = lookForTagName(header, startFrom) ?: break
            startFrom = afterName
            if (name.isNotEmpty() && isValidIdentifier(name)) {
                val value = lookForTagValue(header, startFrom)?.takeIf { it.first.isNotEmpty() }
                if (value != null) {
                    startFrom = value.second
                }
                tags.add(Tag(name, value?.first))
            }
        }
        return tags
    }

    /** The next tag name at or after [startFrom], and where to look for its value. */
    private fun lookForTagName(header: String, startFrom: Int): Pair<String, Int>? {
        val start = if (startFrom < header.length) header.indexOf("@", startFrom) else -1
        if (start < 0) return null
        val nextTag = header.indexOf("@", start + 1)
        val nextColon = header.indexOf(":", start + 1)
        val (end, resume) =
            when {
                nextTag < 0 && nextColon < 0 -> header.length to header.length
                // There is a next tag, and no colon or only the next tag's
                nextTag >= 0 && (nextColon < 0 || nextColon > nextTag) -> nextTag to nextTag
                else -> nextColon to nextColon + 1
            }
        return header.substring(start + 1, end).trim { it <= ' ' } to resume
    }

    /**
     * The tag value at [startFrom], and where to look for the next tag name. `@1980-12-01` is a
     * date value rather than a tag, and a value starting with a quote, such as `"Measurement
     * Interval" [@2019,@2020]`, runs to the end of its line.
     */
    @Suppress("CyclomaticComplexMethod", "NestedBlockDepth", "ReturnCount")
    fun lookForTagValue(header: String, startFrom: Int): Pair<String, Int>? {
        if (startFrom >= header.length) {
            return null
        }
        val nextTag = header.indexOf('@', startFrom)
        val nextStartDoubleQuote = header.indexOf("\"", startFrom)
        val quoteBeforeNextTag = nextTag < 0 || nextTag > nextStartDoubleQuote
        if (
            quoteBeforeNextTag &&
                nextStartDoubleQuote > 0 &&
                header.length > nextStartDoubleQuote + 1
        ) {
            val nextEndDoubleQuote = header.indexOf("\"", nextStartDoubleQuote + 1)
            return if (nextEndDoubleQuote > 0) {
                val parameterEnd = header.indexOf("\n", nextStartDoubleQuote + 1)
                if (parameterEnd < 0) {
                    header.substring(nextStartDoubleQuote) to header.length
                } else {
                    header.substring(nextStartDoubleQuote, parameterEnd) to parameterEnd
                }
            } else {
                // The closing quote is missing
                header.substring(nextStartDoubleQuote) to header.length
            }
        }
        if (nextTag == startFrom && !isStartingWithDigit(header, nextTag + 1)) {
            // The next tag starts here, so this one has no value
            return "" to startFrom
        } else if (nextTag > 0) {
            val interimText = header.substring(startFrom, nextTag).trim { it <= ' ' }
            return if (isStartingWithDigit(header, nextTag + 1)) {
                // The next `@` begins a date value
                if (interimText.isNotEmpty() && interimText != ":") {
                    interimText to nextTag
                } else {
                    val nextSpace = header.indexOf(' ', nextTag)
                    val nextLine = header.indexOf("\n", nextTag)
                    val mul = nextSpace * nextLine
                    var nextDelimiterIndex = header.length
                    if (mul < 0) {
                        nextDelimiterIndex = nextLine.coerceAtLeast(nextSpace)
                    } else if (mul > 1) {
                        nextDelimiterIndex = nextLine.coerceAtMost(nextSpace)
                    }
                    header.substring(nextTag, nextDelimiterIndex) to nextDelimiterIndex
                }
            } else {
                interimText to nextTag
            }
        }
        return header.substring(startFrom).trim { it <= ' ' } to header.length
    }

    /** Whether [tagName] is a letter followed by letters, digits and underscores (ASCII only). */
    fun isValidIdentifier(tagName: String): Boolean =
        tagName.withIndex().all { (i, c) ->
            c == '_' || (if (i == 0) c.isAsciiLetter() else c.isAsciiLetter() || c.isAsciiDigit())
        }

    private fun isStartingWithDigit(header: String, index: Int): Boolean =
        index < header.length && header[index].isAsciiDigit()

    private fun Char.isAsciiLetter(): Boolean = this in 'a'..'z' || this in 'A'..'Z'

    private fun Char.isAsciiDigit(): Boolean = this in '0'..'9'
}
