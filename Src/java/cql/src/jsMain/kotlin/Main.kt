import org.cqframework.cql.gen.cqlParser.*
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser

fun parseToTree(logic: String): ParseTree {
    val input: CharStream = CharStreams.fromString(logic)
    val tokens = CommonTokenStream(cqlLexer(input))
    val parser = cqlParser(tokens)
    parser.buildParseTree = true
    return parser.library()
}

fun main() {
    console.log("Hello, Kotlin/JS!")
    val tree: ParseTree = parseToTree("define inIPP : AgeAt(start of MeasurementPeriod) < 18")
    console.log(tree)
    val logic: LibraryContext = tree.payload as LibraryContext

    val def = logic.statement(0)?.expressionDefinition()
    console.log(def?.identifier()?.IDENTIFIER().toString())
}