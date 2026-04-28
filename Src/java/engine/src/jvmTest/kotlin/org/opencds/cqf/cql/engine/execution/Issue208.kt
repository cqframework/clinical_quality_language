package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertTrue
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

internal class Issue208 : CqlTestBase() {
    @Test
    fun interval() {
        val results = engine.evaluate { library("Issue208") }.onlyResultOrThrow
        var value = results["Let Test 1"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent(
                    ((value as List).elementAt(0) as List).elementAt(0),
                    1.toCqlInteger(),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    (value.elementAt(0) as List).elementAt(1),
                    2.toCqlInteger(),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    (value.elementAt(0) as List).elementAt(2),
                    3.toCqlInteger(),
                )
                .value == true
        )

        assertTrue(
            EquivalentEvaluator.equivalent(
                    (value.elementAt(1) as List).elementAt(0),
                    4.toCqlInteger(),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    (value.elementAt(1) as List).elementAt(1),
                    5.toCqlInteger(),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    (value.elementAt(1) as List).elementAt(2),
                    6.toCqlInteger(),
                )
                .value == true
        )

        value = results["Let Test 2"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent(
                    ((value as List).elementAt(0) as List).elementAt(0),
                    1.toCqlInteger(),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    (value.elementAt(0) as List).elementAt(1),
                    2.toCqlInteger(),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    (value.elementAt(0) as List).elementAt(2),
                    3.toCqlInteger(),
                )
                .value == true
        )

        assertTrue(
            EquivalentEvaluator.equivalent(
                    (value.elementAt(1) as List).elementAt(0),
                    4.toCqlInteger(),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    (value.elementAt(1) as List).elementAt(1),
                    5.toCqlInteger(),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    (value.elementAt(1) as List).elementAt(2),
                    6.toCqlInteger(),
                )
                .value == true
        )
    }
}
