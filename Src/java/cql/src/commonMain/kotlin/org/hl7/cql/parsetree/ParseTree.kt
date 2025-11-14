package org.hl7.cql.parsetree

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser

/** Creates an ANTLR parser for the given CQL. */
fun createParser(text: String): cqlParser {
    val input = CharStreams.fromString(text)
    val lexer = cqlLexer(input)
    val tokens = CommonTokenStream(lexer)
    return cqlParser(tokens)
}

/** Generates the parse tree for the given CQL. */
@OptIn(ExperimentalJsExport::class)
@JsExport
fun inspectCqlParseTree(text: String): String {
    val parser = createParser(text)
    parser.buildParseTree = true
    val tree: ParseTree = parser.library()
    return tree.toStringTree(parser)
}
