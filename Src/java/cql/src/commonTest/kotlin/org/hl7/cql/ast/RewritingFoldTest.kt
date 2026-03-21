package org.hl7.cql.ast

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class RewritingFoldTest {

    private val builder = Builder()

    @Test
    fun rewritesExpressionsByTransformingLiterals() {
        val expression = builder.parseExpression("1 + 2").expression
        val incrementingFold =
            object : RewritingFold() {
                override fun onLiteral(
                    expr: LiteralExpression,
                    children: LiteralChildren<Expression>,
                ): Expression {
                    val literal = expr.literal
                    if (literal is IntLiteral) {
                        return expr.copy(literal = literal.copy(value = literal.value + 1))
                    }
                    return super.onLiteral(expr, children)
                }
            }

        val transformed = incrementingFold.fold(expression) as OperatorBinaryExpression

        val left = ((transformed.left as LiteralExpression).literal as IntLiteral).value
        val right = ((transformed.right as LiteralExpression).literal as IntLiteral).value

        assertEquals(2, left)
        assertEquals(3, right)
    }

    @Test
    fun rewritesLibrariesByUpdatingNestedExpressions() {
        val result =
            builder.parseLibrary(
                """
                library Test version '1.0'

                define "Greeting": 'hello'
                """
            )
        val library = result.library

        val upperCaseFold =
            object : RewritingFold() {
                override fun onLiteral(
                    expr: LiteralExpression,
                    children: LiteralChildren<Expression>,
                ): Expression {
                    val literal = expr.literal
                    if (literal is StringLiteral) {
                        return expr.copy(literal = literal.copy(value = literal.value.uppercase()))
                    }
                    return super.onLiteral(expr, children)
                }
            }

        val transformed = rewriteLibrary(upperCaseFold, library)
        val expressionDefinition =
            transformed.statements.filterIsInstance<ExpressionDefinition>().single()
        val literal =
            ((expressionDefinition.expression as LiteralExpression).literal as StringLiteral).value

        assertEquals("HELLO", literal)
    }

    @Test
    fun returnsOriginalNodeWhenNothingChanges() {
        val expression = builder.parseExpression("1 + 2").expression
        val identityFold = object : RewritingFold() {}

        val result = identityFold.fold(expression)

        assertSame(expression, result, "Identity fold should return the same object reference")
    }
}
