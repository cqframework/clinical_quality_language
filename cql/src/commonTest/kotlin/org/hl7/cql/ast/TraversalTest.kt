package org.hl7.cql.ast

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * [AstWalker] and [Transformer] reach every expression of a tree. The expressions are found
 * independently, in the tree's JSON, and told apart by their kind and position.
 */
class TraversalTest {

    private val trees: List<Pair<String, JsonElement>> =
        listOf(CqlSamples.LIBRARY to library(CqlSamples.LIBRARY).toJson()) +
            CqlSamples.EXPRESSIONS.map { it to Builder().parseExpression(it).expression.toJson() }

    @Test
    fun theWalkerVisitsEveryExpression() {
        for ((text, json) in trees) {
            val visited = mutableSetOf<String>()
            val walker =
                object : AstWalker() {
                    override fun visitExpression(expression: Expression) {
                        visited += expression.toJson().key()
                        super.visitExpression(expression)
                    }

                    override fun visitRetrieveExpression(expression: RetrieveExpression) {
                        // A query source is a retrieve without being visited as an expression
                        visited += expression.toJson().key()
                        super.visitRetrieveExpression(expression)
                    }
                }
            when {
                text == CqlSamples.LIBRARY -> walker.visitLibrary(library(text))
                else -> walker.visitExpression(Builder().parseExpression(text).expression)
            }
            assertEquals(json.expressions().map { it.key() }.toSet(), visited, text)
        }
    }

    @Test
    fun theTransformerReplacesEveryExpression() {
        // Wrapping each expression it visits in parentheses shows both that it visited the
        // expression and that it kept what it was given back.
        val wrapping =
            object : Transformer() {
                override fun visitExpression(expression: Expression): Expression =
                    ParenthesizedExpression(super.visitExpression(expression))
            }
        for ((text, _) in trees) {
            val transformed =
                when {
                    text == CqlSamples.LIBRARY -> library(text).transform(wrapping).toJson()
                    else -> Builder().parseExpression(text).expression.transform(wrapping).toJson()
                }
            val unwrapped = transformed.unwrappedExpressions(parent = null, key = null)
            assertTrue(unwrapped.isEmpty(), "$text: not transformed: $unwrapped")
        }
    }

    @Test
    fun theTransformerChangesNothingByDefault() {
        val library = library(CqlSamples.LIBRARY)
        assertEquals(library, library.transform(object : Transformer() {}))
        for (text in CqlSamples.EXPRESSIONS) {
            val expression = Builder().parseExpression(text).expression
            assertEquals(expression, expression.transform(object : Transformer() {}), text)
        }
    }

    @Test
    fun traversesTheNodesLeftBySyntaxErrors() {
        val damaged = listOf("using", "define A", "define A: 2147483648").map { library(it) }
        for (library in damaged) {
            object : AstWalker() {}.visitLibrary(library)
            assertEquals(library, library.transform(object : Transformer() {}))
        }
    }

    @Test
    fun noOtherNodeSharesTheKindOfAnExpression() {
        // Expressions are found in the JSON by kind, so no other node may use one.
        val others =
            listOf(
                    Literal.serializer(),
                    TypeSpecifier.serializer(),
                    Definition.serializer(),
                    Statement.serializer(),
                    QuerySource.serializer(),
                    QueryInclusionClause.serializer(),
                    IntervalOperatorPhrase.serializer(),
                    PathSegment.serializer(),
                    FunctionBody.serializer(),
                )
                .flatMap { it.descriptor.getElementDescriptor(1).elementNames }
        // A retrieve is both an expression and a query source
        assertEquals(setOf("retrieve"), expressionKinds.intersect(others.toSet()))
    }

    private fun library(text: String): Library = Builder().parseLibrary(text).library

    private fun Library.toJson(): JsonElement = Json.encodeToJsonElement(Library.serializer(), this)

    private fun Expression.toJson(): JsonElement =
        Json.encodeToJsonElement(Expression.serializer(), this)

    /** Every expression in the tree, by its kind, as the serializer names it. */
    private fun JsonElement.expressions(): List<JsonObject> =
        when (this) {
            is JsonObject ->
                listOfNotNull(this.takeIf { isExpression() }) + values.flatMap { it.expressions() }
            is JsonArray -> flatMap { it.expressions() }
            else -> emptyList()
        }

    /**
     * Expressions in [this] that are not the direct child of a parenthesized expression, except for
     * the retrieve of a query source, which cannot be wrapped.
     */
    private fun JsonElement.unwrappedExpressions(parent: JsonObject?, key: String?): List<String> =
        when (this) {
            is JsonObject -> {
                val wrapped =
                    parent?.kind() == PARENTHESIZED ||
                        (key == "source" && kind() == "retrieve") ||
                        kind() == PARENTHESIZED
                listOfNotNull(key().takeIf { isExpression() && !wrapped }) +
                    entries.flatMap { (childKey, child) ->
                        child.unwrappedExpressions(parent = this, key = childKey)
                    }
            }
            is JsonArray -> flatMap { it.unwrappedExpressions(parent, key) }
            else -> emptyList()
        }

    private fun JsonObject.isExpression(): Boolean = kind() in expressionKinds

    private fun JsonElement.kind(): String? =
        ((this as? JsonObject)?.get("kind") as? JsonPrimitive)?.content

    /** The kind and position of an expression, which no other expression in a tree shares. */
    private fun JsonElement.key(): String {
        val locator = jsonObject["locator"]?.jsonObject
        return "${kind()}@${locator?.get("startIndex")?.jsonPrimitive?.content}-" +
            "${locator?.get("stopIndex")?.jsonPrimitive?.content}"
    }

    companion object {
        private const val PARENTHESIZED = "parenthesized"

        /** The serial names of the kinds of [Expression]. */
        private val expressionKinds: Set<String> =
            Expression.serializer().descriptor.getElementDescriptor(1).elementNames.toSet()
    }
}
