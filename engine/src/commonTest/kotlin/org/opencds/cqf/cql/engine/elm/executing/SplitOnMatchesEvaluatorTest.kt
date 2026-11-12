package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

class SplitOnMatchesEvaluatorTest {
    private fun splitOnMatches(string: String, pattern: String) =
        SplitOnMatchesEvaluator.splitOnMatches(string.toCqlString(), pattern.toCqlString())

    private fun listOf(vararg values: String) = values.map { it.toCqlString() }.toCqlList()

    @Test
    fun splitOnMatches() {
        assertNull(SplitOnMatchesEvaluator.splitOnMatches(null, ",".toCqlString()))
        assertEquals(listOf("A", "B"), splitOnMatches("A,B", ","))
        assertEquals(listOf("A", "B", "C"), splitOnMatches("A1B22C", "[0-9]+"))
    }

    /**
     * [#1919](https://github.com/cqframework/clinical_quality_language/issues/1919): With no match,
     * the single element is the argument's value, without CQL quote marks
     */
    @Test
    fun splitOnMatchesNotFound() {
        assertEquals(listOf("ABC"), splitOnMatches("ABC", ","))
        assertEquals(listOf("it's"), splitOnMatches("it's", ","))
    }
}
