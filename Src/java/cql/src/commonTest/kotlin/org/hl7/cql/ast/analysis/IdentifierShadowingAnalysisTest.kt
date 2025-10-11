package org.hl7.cql.ast.analysis

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.hl7.cql.ast.AggregateClause
import org.hl7.cql.ast.AliasedQuerySource
import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.ast.Identifier
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.ast.IntLiteral
import org.hl7.cql.ast.Library
import org.hl7.cql.ast.LiteralExpression
import org.hl7.cql.ast.NamedTypeSpecifier
import org.hl7.cql.ast.OperatorBinaryExpression
import org.hl7.cql.ast.BinaryOperator
import org.hl7.cql.ast.OperandDefinition
import org.hl7.cql.ast.ExpressionDefinition
import org.hl7.cql.ast.ExpressionFunctionBody
import org.hl7.cql.ast.LetClauseItem
import org.hl7.cql.ast.QueryExpression
import org.hl7.cql.ast.QualifiedIdentifier
import org.hl7.cql.ast.RetrieveExpression
import org.hl7.cql.ast.ReturnClause

class IdentifierShadowingAnalysisTest {

    private val analyzer = AnalysisPipeline(listOf(IdentifierShadowingAnalysis()))

    @Test
    fun reportsLetShadowingFunctionOperand() {
        val function =
            FunctionDefinition(
                name = Identifier("Foo"),
                operands =
                    listOf(
                        OperandDefinition(
                            name = Identifier("value"),
                            type = integerType(),
                        )
                    ),
                body =
                    ExpressionFunctionBody(
                        expression =
                            QueryExpression(
                                sources =
                                    listOf(
                                        AliasedQuerySource(
                                            source = retrieve("Observation"),
                                            alias = Identifier("Obs"),
                                        )
                                    ),
                                lets =
                                    listOf(
                                        LetClauseItem(
                                            identifier = Identifier("value"),
                                            expression = LiteralExpression(IntLiteral(1)),
                                        )
                                    ),
                                inclusions = emptyList(),
                                where = null,
                                aggregate = null,
                                result = ReturnClause(expression = IdentifierExpression(qid("value"))),
                                sort = null,
                            )
                    ),
            )

        val library = Library(statements = listOf(function))
        val warnings = analyzer.analyze(library)

        assertEquals(1, warnings.size)
        assertTrue(warnings.first().message.contains("value"))
    }

    @Test
    fun reportsQueryAliasShadowingOperand() {
        val function =
            FunctionDefinition(
                name = Identifier("Foo"),
                operands =
                    listOf(
                        OperandDefinition(
                            name = Identifier("Obs"),
                            type = integerType(),
                        )
                    ),
                body =
                    ExpressionFunctionBody(
                        expression =
                            QueryExpression(
                                sources =
                                    listOf(
                                        AliasedQuerySource(
                                            source = retrieve("Observation"),
                                            alias = Identifier("Obs"),
                                        )
                                    ),
                                lets = emptyList(),
                                inclusions = emptyList(),
                                where = null,
                                aggregate = null,
                                result = ReturnClause(expression = IdentifierExpression(qid("Obs"))),
                                sort = null,
                            )
                    ),
            )

        val library = Library(statements = listOf(function))
        val warnings = analyzer.analyze(library)

        assertEquals(1, warnings.size)
        assertTrue(warnings.first().message.contains("Obs"))
    }

    @Test
    fun reportsAggregateIdentifierShadowingLet() {
        val query =
            QueryExpression(
                sources =
                    listOf(
                        AliasedQuerySource(
                            source = retrieve("Observation"),
                            alias = Identifier("Obs"),
                        )
                    ),
                lets =
                    listOf(
                        LetClauseItem(
                            identifier = Identifier("Value"),
                            expression = LiteralExpression(IntLiteral(1)),
                        )
                    ),
                inclusions = emptyList(),
                where = null,
                aggregate =
                    AggregateClause(
                        identifier = Identifier("Value"),
                        starting = LiteralExpression(IntLiteral(0)),
                        expression =
                            OperatorBinaryExpression(
                                operator = BinaryOperator.ADD,
                                left = IdentifierExpression(qid("Value")),
                                right = LiteralExpression(IntLiteral(1)),
                            ),
                    ),
                result = null,
                sort = null,
            )

        val exprDef =
            ExpressionDefinition(
                name = Identifier("Result"),
                expression = query,
            )

        val library = Library(statements = listOf(exprDef))
        val warnings = analyzer.analyze(library)

        assertEquals(1, warnings.size)
        assertTrue(warnings.first().message.contains("Value"))
    }

    private fun integerType(): NamedTypeSpecifier =
        NamedTypeSpecifier(QualifiedIdentifier(listOf("Integer")))

    private fun retrieve(typeName: String): RetrieveExpression =
        RetrieveExpression(typeSpecifier = NamedTypeSpecifier(QualifiedIdentifier(listOf(typeName))))

    private fun qid(name: String): QualifiedIdentifier = QualifiedIdentifier(listOf(name))
}
