package org.opencds.cqf.cql.engine.elm.executing

import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

class SplitOnMatchesEvaluatorTest {
    @Test
    fun splitOnMatches() {
        assertEquals(
            listOf("a".toCqlString(), "b".toCqlString()).toCqlList(),
            SplitOnMatchesEvaluator.splitOnMatches("a,b".toCqlString(), ",".toCqlString()),
        )
    }
}
