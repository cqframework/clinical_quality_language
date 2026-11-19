package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.execution.CqlTestBase
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

/**
 * Tuple equality is "a conjunction of equality comparisons" (Equal), so it doesn't depend on the
 * order of the elements. These are the spec's four examples, each also with the elements reversed.
 */
internal class TupleEqualityTest : CqlTestBase() {
    private fun tuple(vararg elements: Pair<String, Int?>): Tuple =
        Tuple(
            linkedMapOf<String, Value?>(
                *elements.map { it.first to it.second?.toCqlInteger() }.toTypedArray()
            )
        )

    private fun equal(left: Tuple, right: Tuple) =
        EqualEvaluator.equal(left, right, State(environment!!))

    private fun reversed(t: Tuple) =
        Tuple(LinkedHashMap(t.elements.entries.reversed().associate { it.key to it.value }))

    private fun assertBothOrders(expected: Boolean?, left: Tuple, right: Tuple) {
        assertEquals(expected, equal(left, right))
        assertEquals(expected, equal(reversed(left), reversed(right)), "with the elements reversed")
    }

    @Test
    fun tupleEqualBothNullTrue() {
        assertBothOrders(Boolean.TRUE, tuple("x" to 1, "y" to null), tuple("x" to 1, "y" to null))
    }

    @Test
    fun tupleEqualBothNullFalse() {
        assertBothOrders(Boolean.FALSE, tuple("x" to 1, "y" to null), tuple("x" to 2, "y" to null))
    }

    @Test
    fun tupleEqualMixedNullNull() {
        val left = tuple("x" to 1, "y" to 1)
        val right = tuple("x" to null, "y" to 1)
        assertNull(equal(left, right))
        assertNull(equal(reversed(left), reversed(right)))
    }

    @Test
    fun tupleEqualMixedNullFalse() {
        // null and false is false, whichever element comes first.
        assertBothOrders(Boolean.FALSE, tuple("x" to 1, "y" to 1), tuple("x" to null, "y" to 2))
    }
}
