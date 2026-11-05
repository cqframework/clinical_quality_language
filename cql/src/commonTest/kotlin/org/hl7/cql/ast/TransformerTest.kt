package org.hl7.cql.ast

import kotlin.test.Test
import kotlin.test.assertEquals

class TransformerTest {

    private val builder = Builder()

    @Test
    fun transformsExpressionsByRewritingLiterals() {
        val expression = builder.parseExpression("1 + 2").expression
        val incrementingTransformer =
            object : Transformer() {
                override fun visitIntLiteral(literal: IntLiteral): Literal {
                    val value = literal.value
                    return literal.copy(value = (value + 1))
                }
            }

        val transformed = expression.transform(incrementingTransformer) as OperatorBinaryExpression

        val left = ((transformed.left as LiteralExpression).literal as IntLiteral).value
        val right = ((transformed.right as LiteralExpression).literal as IntLiteral).value

        assertEquals(2, left)
        assertEquals(3, right)
    }

    @Test
    fun transformsLibrariesByUpdatingNestedExpressions() {
        val result =
            builder.parseLibrary(
                """
                library Test version '1.0'

                define "Greeting": 'hello'
                """
            )
        val library = result.library

        val upperCaseTransformer =
            object : Transformer() {
                override fun visitStringLiteral(literal: StringLiteral): Literal =
                    literal.copy(value = literal.value.uppercase())
            }

        val transformed = library.transform(upperCaseTransformer)
        val expressionDefinition =
            transformed.statements.filterIsInstance<ExpressionDefinition>().single()
        val literal =
            ((expressionDefinition.expression as LiteralExpression).literal as StringLiteral).value

        assertEquals("HELLO", literal)
    }
}
