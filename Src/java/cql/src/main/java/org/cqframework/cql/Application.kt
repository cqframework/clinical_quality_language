package org.cqframework.cql

import java.io.FileInputStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser

object Application {
    fun main(args: Array<String>) {
        val inputFile = args.getOrNull(0)
        val stream = inputFile?.let { FileInputStream(it) } ?: System.`in`

        val chars = CharStreams.fromStream(stream)
        val lexer = cqlLexer(chars)
        val tokens = CommonTokenStream(lexer)
        val parser = cqlParser(tokens)

        parser.buildParseTree = true
        val tree = parser.library()

        // show tree in text form
        println(tree.toStringTree(parser))
    }
}
