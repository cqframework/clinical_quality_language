package org.opencds.cqf.cql.engine.execution

import kotlin.test.assertEquals
import org.hl7.elm.r1.ExpressionDef
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class QueryOptimizationTest : CqlTestBase() {
    private fun newEngine(optimize: Boolean): CqlEngine {
        val options = mutableSetOf(CqlEngine.Options.EnableProfiling)
        if (optimize) {
            options.add(CqlEngine.Options.EnableQueryOptimization)
        }
        return CqlEngine(Environment(libraryManager), options)
    }

    private fun CqlEngine.evaluate(expression: String) =
        evaluate { library("QueryOptimizationTest") { expressions(expression) } }
            .onlyResultOrThrow[expression]!!
            .value

    /** Number of times the named definition was evaluated, across all call paths. */
    private fun CqlEngine.bodyEvaluations(name: String): Long {
        fun Profile.Node.sum(): Long =
            children.values.sumOf { byElement ->
                byElement!!.entries.sumOf { (element, node) ->
                    val own =
                        if (element is ExpressionDef && element.name == name) node.misses else 0L
                    own + node.sum()
                }
            }
        return state.debugResult!!.profile!!.tree.sum()
    }

    @ParameterizedTest
    @ValueSource(
        strings =
            [
                "With",
                "Row, related and pair conjuncts",
                "Without",
                "Null conjuncts",
                "With empty",
                "Without empty",
                "Correlated source",
                "Correlated without",
                "Nested query",
                "Conditional",
                "Two relationships",
                "Let",
                "Shadowed alias",
                "Message",
                "Message through expression",
                "Message in cross product",
                "Cross product",
                "Three sources",
                "Invariant false",
                "Invariant true",
                "Cross product with empty",
                "Default return",
                "Distinct return",
                "Sorted",
                "Non-list source",
            ]
    )
    fun optimizedResultEqualsNaiveResult(expression: String) {
        assertEquals(
            newEngine(optimize = false).evaluate(expression),
            newEngine(optimize = true).evaluate(expression),
        )
    }

    @Test
    fun rowSubexpressionIsEvaluatedOncePerRow() {
        // Double(X) = Y first holds at Y = 2X, so the naive loop calls Double 2 + 4 + ... + 20
        // times
        val naive = newEngine(optimize = false)
        naive.evaluate("With")
        assertEquals(110L, naive.bodyEvaluations("Double"))

        val optimized = newEngine(optimize = true)
        optimized.evaluate("With")
        assertEquals(10L, optimized.bodyEvaluations("Double"))
    }

    @Test
    fun crossProductPrunesOnEarlierSources() {
        val naive = newEngine(optimize = false)
        naive.evaluate("Cross product")
        assertEquals(200L, naive.bodyEvaluations("Double"))

        // Double(A) is evaluated once for each A > 2 that has some B < 5
        val optimized = newEngine(optimize = true)
        optimized.evaluate("Cross product")
        assertEquals(8L, optimized.bodyEvaluations("Double"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["Message", "Message through expression", "Message in cross product"])
    fun queriesThatMayLogMessagesAreEvaluatedNaively(expression: String) {
        val definition = if (expression == "Message through expression") "Noisy flag" else "Noisy"
        val naive = newEngine(optimize = false)
        naive.evaluate(expression)
        val optimized = newEngine(optimize = true)
        optimized.evaluate(expression)
        assertEquals(naive.bodyEvaluations(definition), optimized.bodyEvaluations(definition))
    }
}
