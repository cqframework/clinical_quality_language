package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean
import org.opencds.cqf.cql.engine.runtime.toCqlString

class MatchesEvaluatorTest {
    @Test
    fun matches() {
        assertEquals(
            true.toCqlBoolean(),
            MatchesEvaluator.matches("abc".toCqlString(), "abc".toCqlString()),
        )
        assertEquals(
            true.toCqlBoolean(),
            MatchesEvaluator.matches(
                "Not all who wander are lost - circa 2017".toCqlString(),
                ".*\\d+".toCqlString(),
            ),
        )
        assertEquals(
            false.toCqlBoolean(),
            MatchesEvaluator.matches("abc".toCqlString(), "'ab".toCqlString()),
        )
    }

    @Test
    fun partialMatching() {
        // The spec's examples (Matches): regular expressions use partial matching, and ^ and $
        // match the entire string.
        assertEquals(
            true.toCqlBoolean(),
            MatchesEvaluator.matches("1,2three".toCqlString(), "\\d,\\d\\w+".toCqlString()),
        )
        assertEquals(
            false.toCqlBoolean(),
            MatchesEvaluator.matches("1,2three".toCqlString(), "^\\w+$".toCqlString()),
        )
        assertEquals(
            true.toCqlBoolean(),
            MatchesEvaluator.matches(
                "http://fhir.org/guides/cqf/common/Library/FHIR-ModelInfo|4.0.1".toCqlString(),
                "Library".toCqlString(),
            ),
        )
        assertEquals(
            true.toCqlBoolean(),
            MatchesEvaluator.matches("1,2three".toCqlString(), "\\w+".toCqlString()),
        )
        assertEquals(
            true.toCqlBoolean(),
            MatchesEvaluator.matches("abc".toCqlString(), "b".toCqlString()),
        )
        assertEquals(
            false.toCqlBoolean(),
            MatchesEvaluator.matches("abc".toCqlString(), "^b$".toCqlString()),
        )
        assertEquals(null, MatchesEvaluator.matches("12three".toCqlString(), null))
    }
}
