import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser
import kotlin.js.JsExport

@JsExport
fun parseToTree(logic: String): String {
    val input: CharStream = CharStreams.fromString(logic)
    val tokens = CommonTokenStream(cqlLexer(input))
    val parser = cqlParser(tokens)
    parser.buildParseTree = true
    val library = parser.library()
    return library.toStringTree(parser)
}
