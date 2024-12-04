package org.cqframework.cql.grammar

import org.cqframework.cql.gen.cqlParser.*
import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.cqframework.cql.gen.cqlLexer
import org.cqframework.cql.gen.cqlParser
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * GrammarTest ensures that the grammar (and generated parsers) work as expected.  If non-compatible changes are made
 * to the grammar, these tests should fail.  If the change is intentional, modify the tests to pass-- otherwise, fix
 * the grammar.
 */
internal class GrammarTest {
    @Test
    fun ageAt() {
        val tree: ParseTree = parseToTree("define inIPP : AgeAt(start of MeasurementPeriod) < 18")
        val logic: LibraryContext = tree.payload as LibraryContext

        val def = logic.statement(0)?.expressionDefinition()
        assertEquals("inIPP", def?.identifier()?.IDENTIFIER().toString())

        val cmpExpr: InequalityExpressionContext = def?.expression() as InequalityExpressionContext
        assertEquals("<", cmpExpr.getChild(1)?.text)

        val termExpression: TermExpressionContext = cmpExpr.expression(0) as TermExpressionContext
        val termExpressionTerm: TermExpressionTermContext = termExpression.expressionTerm() as TermExpressionTermContext
        val invocationTerm: InvocationTermContext = termExpressionTerm.term() as InvocationTermContext
        val functionInvocation: FunctionInvocationContext = invocationTerm.invocation() as FunctionInvocationContext
        assertEquals(
            "AgeAt", functionInvocation.function().referentialIdentifier().text
        )

        val argExpression: TermExpressionContext =
            functionInvocation.function().paramList()?.expression(0) as TermExpressionContext
        val argExpressionTerm: TimeBoundaryExpressionTermContext =
            argExpression.expressionTerm() as TimeBoundaryExpressionTermContext
        assertEquals("start", argExpressionTerm.getChild(0)?.text)
        assertEquals("MeasurementPeriod", argExpressionTerm.expressionTerm().text)

        val termExpr = cmpExpr.expression(1)
        assertEquals("18", termExpr?.text)
    }

    private fun parseToTree(logic: String): ParseTree {
        val input: CharStream = CharStreams.fromString(logic)
        val tokens = CommonTokenStream(cqlLexer(input))
        val parser = cqlParser(tokens)
        parser.buildParseTree = true
        return parser.library()
    }
}
