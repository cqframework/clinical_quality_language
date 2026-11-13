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
}
