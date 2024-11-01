import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser

@ExperimentalJsExport
@JsExport
fun parseToTree(logic: String): String {
    val input: CharStream = CharStreams.fromString(logic)
    val tokens = CommonTokenStream(cqlLexer(input))
    val parser = cqlParser(tokens)
    parser.buildParseTree = true
    val library = parser.library()

    // Inspect the library object in the console.
    // Methods like toStringTree are mangled because they have multiple overloads.
    // TODO: Remove in the future.
    console.log("Library:", library)

    return library.toStringTree(parser)
}

// This method is not exported so it won't be available in the JS library.
// TODO: Remove in the future.
fun someMethod() {
    console.log("I am not exported")
}

// This `main` method always just runs.
// TODO: Remove in the future.
fun main() {
    val logic = "define inIPP : AgeAt(start of MeasurementPeriod) < 18"
    val input: CharStream = CharStreams.fromString(logic)
    val tokens = CommonTokenStream(cqlLexer(input))
    val parser = cqlParser(tokens)
    parser.buildParseTree = true
    val library = parser.library()

    val def = library.statement(0)?.expressionDefinition()
    console.log(def?.identifier()?.IDENTIFIER().toString())
}