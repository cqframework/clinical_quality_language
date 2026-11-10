package org.hl7.cql.ast

import org.antlr.v4.kotlinruntime.BaseErrorListener
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonToken
import org.antlr.v4.kotlinruntime.Lexer
import org.antlr.v4.kotlinruntime.LexerNoViableAltException
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer
import org.antlr.v4.kotlinruntime.Token
import org.cqframework.cql.gen.cqlLexer

/**
 * Finds the top-level declarations of a library that has syntax errors (the library header,
 * `using`, `include`, terminology and parameter definitions, `context` and `define` statements), so
 * that each can be parsed on its own and an error in one cannot consume the others.
 *
 * The grammar alone cannot find these boundaries during error recovery: `define`, `using`,
 * `context` and the other declaration keywords are also valid identifiers, so after an incomplete
 * expression such as `1 +` the parser reads the next line's `define` as the missing operand. A
 * declaration therefore starts at a declaration keyword (or an access modifier before one) that is
 * the first token on its line and is followed by something that can begin a name. That excludes
 * uses of the keywords as identifiers, such as the tuple element `code: X`, the property access
 * `concept.display` and the choice type `Choice<code, ...>` split across lines.
 */
internal object StatementBoundaries {

    private val declarationKeywords =
        setOf(
            "library",
            "using",
            "include",
            "codesystem",
            "valueset",
            "code",
            "concept",
            "parameter",
            "context",
            "define",
        )

    private val accessModifiers = setOf("private", "public")

    private val declarationLine =
        Regex(
            "^[ \\t]*(?:(?:private|public)[ \\t]+)?(?:" +
                declarationKeywords.joinToString("|") +
                ")[ \\t]+[\"`A-Za-z_]"
        )

    /** Literal tokens that may span lines, and so can swallow declarations when unterminated. */
    private val literalTypes =
        setOf(
            cqlLexer.Tokens.STRING,
            cqlLexer.Tokens.QUOTEDIDENTIFIER,
            cqlLexer.Tokens.DELIMITEDIDENTIFIER,
        )

    /** A lexer error, positioned in the whole text. */
    class LexerError(
        val message: String,
        val startIndex: Int,
        val stopIndex: Int,
        val line: Int,
        val column: Int,
    )

    class Lexed(val tokens: List<Token>, val errors: List<LexerError>)

    /**
     * Lexes [text] for recovery. An unterminated string or quoted identifier either pairs with the
     * next quote character, which can be any number of declarations later, or fails to match at the
     * end of the text, and the lexer discards everything it scanned. A literal or a failed match
     * that runs across a line starting a declaration is therefore treated as unterminated: lexing
     * stops there and starts again at that line. Block comments are not cut, since commented-out
     * declarations are common.
     */
    fun lex(text: String): Lexed {
        val tokens = mutableListOf<Token>()
        val errors = mutableListOf<LexerError>()
        var start = 0
        var line = 1
        while (true) {
            val cut = lexFrom(text, start, line, tokens, errors) ?: return Lexed(tokens, errors)
            line += text.substring(start, cut).count { it == '\n' }
            start = cut
        }
    }

    /**
     * Lexes [text] from [start], the beginning of line [line], adding tokens and errors with their
     * positions in [text]. Returns where to start again if a literal or failed match runs across a
     * declaration line, which is always after [start].
     */
    private fun lexFrom(
        text: String,
        start: Int,
        line: Int,
        tokens: MutableList<Token>,
        errors: MutableList<LexerError>,
    ): Int? {
        val lexer = cqlLexer(CharStreams.fromString(text.substring(start)))
        val recorder = ErrorRecorder(start, line - 1)
        lexer.removeErrorListeners()
        lexer.addErrorListener(recorder)
        var cut: Int?
        var token: Token
        do {
            val reported = recorder.errors.size
            token = lexer.nextToken().moveBy(start, line - 1)
            val newErrors = recorder.errors.drop(reported)
            val spans =
                newErrors.map { it.startIndex..it.stopIndex } +
                    listOfNotNull(
                        (token.startIndex..token.stopIndex).takeIf { token.type in literalTypes }
                    )
            val found = spans.firstNotNullOfOrNull { declarationLineWithin(text, it) }
            errors += newErrors.filter { found == null || it.startIndex < found }
            if (found == null) tokens += token
            cut = found
        } while (cut == null && token.type != Token.EOF)
        return cut
    }

    /**
     * Moves a token lexed from a part of the text that starts a line to its position in the whole
     * text. Its text is fixed first, since a token reads it from the part by position.
     */
    private fun Token.moveBy(offset: Int, lines: Int): Token =
        (this as CommonToken).apply {
            text = text
            startIndex += offset
            stopIndex += offset
            line += lines
        }

    /** Records lexer errors, including the text a failed match scanned and discarded. */
    private class ErrorRecorder(private val offset: Int, private val lines: Int) :
        BaseErrorListener() {
        val errors = mutableListOf<LexerError>()

        override fun syntaxError(
            recognizer: Recognizer<*, *>,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String,
            e: RecognitionException?,
        ) {
            val failedFrom = (e as? LexerNoViableAltException)?.startIndex ?: return
            val failedTo = (recognizer as Lexer).inputStream.index()
            errors +=
                LexerError(
                    message = msg,
                    startIndex = failedFrom + offset,
                    stopIndex = maxOf(failedFrom, failedTo - 1) + offset,
                    line = line + lines,
                    column = charPositionInLine,
                )
        }
    }

    /** The start of the first line within [span], after its first, that starts a declaration. */
    private fun declarationLineWithin(text: String, span: IntRange): Int? {
        var lineStart = text.indexOf('\n', span.first) + 1
        while (lineStart in 1..span.last) {
            val lineEnd = text.indexOf('\n', lineStart).let { if (it < 0) text.length else it }
            if (declarationLine.containsMatchIn(text.substring(lineStart, lineEnd)))
                return lineStart
            lineStart = lineEnd + 1
        }
        return null
    }

    /**
     * Returns contiguous runs of [tokens] that together contain every token in order. Every run but
     * the first begins at a declaration; the first holds anything before the first declaration.
     */
    fun split(tokens: List<Token>): List<List<Token>> {
        val visible = tokens.indices.filter { tokens[it].channel == Token.DEFAULT_CHANNEL }
        val starts =
            visible.indices
                .filter { k ->
                    val token = tokens[visible[k]]
                    val previous = visible.getOrNull(k - 1)?.let { tokens[it] }
                    val next = visible.getOrNull(k + 1)?.let { tokens[it] }
                    val firstOnLine = previous == null || previous.endLine() < token.line
                    firstOnLine && startsDeclaration(token, next)
                }
                .map { visible[it] }
        val bounds = listOf(0) + starts.filter { it > 0 } + tokens.size
        return bounds
            .zipWithNext { from, to -> tokens.subList(from, to) }
            .filter { it.isNotEmpty() }
    }

    /** Indices of the tokens in [tokens], after the first, that are the first on their line. */
    fun lineStarts(tokens: List<Token>): List<Int> {
        val visible = tokens.indices.filter { tokens[it].channel == Token.DEFAULT_CHANNEL }
        return visible
            .zipWithNext()
            .filter { (previous, token) -> tokens[previous].endLine() < tokens[token].line }
            .map { it.second }
    }

    private fun startsDeclaration(token: Token, next: Token?): Boolean =
        when (token.text) {
            in declarationKeywords -> next.beginsName()
            in accessModifiers -> next?.text in declarationKeywords
            else -> false
        }

    private fun Token?.beginsName(): Boolean =
        this?.text?.firstOrNull()?.let { it.isLetter() || it == '_' || it == '"' || it == '`' } ==
            true

    private fun Token.endLine(): Int = line + text.orEmpty().count { it == '\n' }
}
