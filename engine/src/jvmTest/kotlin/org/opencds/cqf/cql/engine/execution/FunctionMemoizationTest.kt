package org.opencds.cqf.cql.engine.execution

import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import org.hl7.elm.r1.FunctionDef
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.Value

/**
 * Function bodies invoked from a query join are counted through the profile, whose per-node
 * `misses` are the invocations that actually evaluated the function body.
 */
class FunctionMemoizationTest : CqlTestBase() {
    private fun newEngine(memoize: Boolean): CqlEngine {
        // Expression caching is left off: it would serve whole query results across evaluations
        val options = mutableSetOf(CqlEngine.Options.EnableProfiling)
        if (memoize) {
            options.add(CqlEngine.Options.EnableFunctionMemoization)
        }
        return CqlEngine(Environment(libraryManager), options)
    }

    private fun CqlEngine.evaluate(
        expression: String,
        parameters: Map<String, Value?>? = null,
        evaluationDateTime: ZonedDateTime = EVALUATION_DATE_TIME,
    ): Value? {
        return evaluate {
                library("FunctionMemoizationTest") { expressions(expression) }
                this.parameters = parameters
                this.evaluationDateTime = evaluationDateTime
            }
            .onlyResultOrThrow[expression]!!
            .value
    }

    /** Number of times the body of the named function was evaluated, across all call paths. */
    private fun CqlEngine.bodyEvaluations(functionName: String): Long {
        fun Profile.Node.sum(): Long =
            children.values.sumOf { byElement ->
                byElement!!.entries.sumOf { (element, node) ->
                    val own =
                        if (element is FunctionDef && element.name == functionName) node.misses
                        else 0L
                    own + node.sum()
                }
            }
        return state.debugResult!!.profile!!.tree.sum()
    }

    @ParameterizedTest
    @ValueSource(strings = ["CrossJoin", "WithoutClause", "FreshInterval"])
    fun joinEvaluatesFunctionOncePerDistinctArgument(expression: String) {
        val function = if (expression == "FreshInterval") "SlowInterval" else "Slow"

        val baseline = newEngine(memoize = false)
        val expected = baseline.evaluate(expression)
        assertEquals(X_COUNT * Y_COUNT, baseline.bodyEvaluations(function))

        val memoized = newEngine(memoize = true)
        assertEquals(expected, memoized.evaluate(expression))
        assertEquals(X_COUNT, memoized.bodyEvaluations(function))
    }

    @Test
    fun parametersArePartOfTheKey() {
        val engine = newEngine(memoize = true)
        val first = engine.evaluate("CrossJoin", mapOf("Offset" to Integer(0)))
        assertEquals(X_COUNT, engine.bodyEvaluations("Slow"))

        // Same inputs: results are reused across evaluations
        assertEquals(first, engine.evaluate("CrossJoin", mapOf("Offset" to Integer(0))))
        assertEquals(X_COUNT, engine.bodyEvaluations("Slow"))

        // Different inputs: the function is evaluated again
        val shifted = engine.evaluate("CrossJoin", mapOf("Offset" to Integer(15)))
        assertEquals(2 * X_COUNT, engine.bodyEvaluations("Slow"))
        assertEquals(
            newEngine(memoize = false).evaluate("CrossJoin", mapOf("Offset" to Integer(15))),
            shifted,
        )
    }

    @Test
    fun evaluationDateTimeIsPartOfTheKey() {
        val engine = newEngine(memoize = true)
        val later = EVALUATION_DATE_TIME.plusDays(1)
        val first = engine.evaluate("Timestamps")
        val second = engine.evaluate("Timestamps", evaluationDateTime = later)

        assertEquals(2 * X_COUNT, engine.bodyEvaluations("Shifted"))
        assertEquals(newEngine(memoize = false).evaluate("Timestamps"), first)
        assertEquals(
            newEngine(memoize = false).evaluate("Timestamps", evaluationDateTime = later),
            second,
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["Messages", "TransitiveMessages"])
    fun functionsWithMessagesAreNotMemoized(expression: String) {
        val engine = newEngine(memoize = true)
        engine.evaluate(expression)
        assertEquals(X_COUNT * Y_COUNT, engine.bodyEvaluations("Noisy"))
    }

    @Test
    fun expressionCacheIsInvalidatedWhenInputsChange() {
        val engine =
            CqlEngine(
                Environment(libraryManager),
                mutableSetOf(CqlEngine.Options.EnableExpressionCaching),
            )
        assertEquals(Integer(0), engine.evaluate("OffsetValue", mapOf("Offset" to Integer(0))))
        assertEquals(Integer(15), engine.evaluate("OffsetValue", mapOf("Offset" to Integer(15))))

        // The default of Start is computed from Now()
        val later = EVALUATION_DATE_TIME.plusDays(1)
        assertNotEquals(engine.evaluate("StartValue"), engine.evaluate("StartValue", null, later))
    }

    companion object {
        private const val X_COUNT = 10L
        private const val Y_COUNT = 20L
        private val EVALUATION_DATE_TIME = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
    }
}
