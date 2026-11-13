package org.hl7.cql.ast

import org.antlr.v4.kotlinruntime.Token
import org.cqframework.cql.gen.cqlLexer

/** Gives each comment of a library to a declaration, as [Declaration.comments] describes. */
internal object CommentAttacher {

    class Attached(val comments: List<Comment>, val tags: List<Tag>)

    /**
     * The comments and tags of each declaration, given the [extents] of the declarations in source
     * order. With no declarations there is nothing to attach to, and [comments] has them all.
     */
    fun attach(
        text: String,
        tokens: List<Token>,
        extents: List<Locator>,
        sourceId: String?,
    ): List<Attached> {
        val lines = LineIndex(text)
        val owned = List(extents.size) { mutableListOf<Comment>() }
        for (token in tokens) {
            val comment = token.toComment(sourceId) ?: continue
            if (extents.isNotEmpty()) owned[owner(token.startIndex, extents, lines)] += comment
        }
        return extents.mapIndexed { i, extent ->
            val headerStart = if (i == 0) 0 else extents[i - 1].stopIndex + 1
            val header = text.substring(headerStart, maxOf(headerStart, extent.startIndex))
            Attached(owned[i], TagParser.parse(header))
        }
    }

    fun comments(tokens: List<Token>, sourceId: String?): List<Comment> =
        tokens.mapNotNull { it.toComment(sourceId) }

    /**
     * The declaration a comment starting at [offset] belongs to: the one it is within or ends the
     * line of, or else the next one, or the last one if it is after them all.
     */
    private fun owner(offset: Int, extents: List<Locator>, lines: LineIndex): Int {
        val before = extents.indexOfLast { it.startIndex <= offset }
        return when {
            before < 0 -> 0
            offset <= extents[before].stopIndex -> before
            lines.lineOf(offset) == lines.lineOf(extents[before].stopIndex) -> before
            else -> minOf(before + 1, extents.lastIndex)
        }
    }

    private fun Token.toComment(sourceId: String?): Comment? {
        val style =
            when (type) {
                cqlLexer.Tokens.COMMENT -> CommentStyle.BLOCK
                cqlLexer.Tokens.LINE_COMMENT -> CommentStyle.LINE
                else -> return null
            }
        return Comment(
            text = text.orEmpty(),
            style = style,
            locator = Locator(sourceId, startIndex, stopIndex, line, charPositionInLine),
        )
    }

    /** The line of each offset in a text. */
    private class LineIndex(text: String) {
        private val lineStarts = listOf(0) + text.indices.filter { text[it] == '\n' }.map { it + 1 }

        fun lineOf(offset: Int): Int {
            val found = lineStarts.binarySearch(offset)
            return if (found >= 0) found else -found - 2
        }
    }
}
