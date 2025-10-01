package org.cqframework.cql.tools.parsetree

import java.io.FileInputStream
import java.io.IOException
import org.antlr.v4.kotlinruntime.CharStreams.fromStream
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser

/** A simple wrapper around the ANTLR4 testrig. */
object Main {
    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        var inputFile: String? = null
        if (args.isNotEmpty()) {
            inputFile = args[0]
        }
        var `is` = System.`in`
        if (inputFile != null) {
            `is` = FileInputStream(inputFile)
        }
        val input = fromStream(`is`)
        val lexer = cqlLexer(input)
        val tokens = CommonTokenStream(lexer)
        tokens.fill()
        val parser = cqlParser(tokens)
        parser.buildParseTree = true
        val tree: ParserRuleContext = parser.library()
        // Trees.inspect(tree, parser);
    }
}
