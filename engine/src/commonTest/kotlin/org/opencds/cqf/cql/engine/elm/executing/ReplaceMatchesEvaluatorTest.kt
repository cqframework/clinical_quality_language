package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.runtime.toCqlString

class ReplaceMatchesEvaluatorTest {
    @Test
    fun replaceMatches() {
        assertEquals(
            "aXc".toCqlString(),
            ReplaceMatchesEvaluator.replaceMatches(
                "abc".toCqlString(),
                "b".toCqlString(),
                "X".toCqlString(),
            ),
        )
    }
}
