package org.opencds.cqf.cql.engine.execution

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval

internal class Issue33 : CqlTestBase() {
    @Test
    fun interval() {
        val bigDecimalZoneOffset = bigDecimalZoneOffset

        val results = engine.evaluate { library("Issue33") }.onlyResultOrThrow
        val value = results["Issue33"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as Interval).start,
                DateTime(bigDecimalZoneOffset, 2017, 12, 20, 11, 0, 0),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                value.end,
                DateTime(bigDecimalZoneOffset, 2017, 12, 20, 23, 59, 59, 999),
            ) == true
        )
    }
}
