package org.opencds.cqf.cql.engine.execution

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Time

internal class CqlNulLogicalOperatorsTest : CqlTestBase() {
    @Test
    fun all_null_logical_operators() {
        val bigDecimalZoneOffset = bigDecimalZoneOffset
        val results = engine.evaluate { library("CqlNullologicalOperatorsTest") }.onlyResultOrThrow
        var value = results["CoalesceANull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("a"))

        value = results["CoalesceNullA"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("a"))

        value = results["CoalesceEmptyList"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["CoalesceListFirstA"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("a"))

        value = results["CoalesceListLastA"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`("a"))

        value = results["CoalesceFirstList"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<String?>("a")))

        value = results["CoalesceLastList"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<String?>("a")))

        value = results["DateTimeCoalesce"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 5, 18)) ==
                true
        )

        value = results["DateTimeListCoalesce"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 5, 18)) ==
                true
        )

        value = results["TimeCoalesce"]!!.value
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(5, 15, 33, 556)) == true)

        value = results["TimeListCoalesce"]!!.value
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(5, 15, 33, 556)) == true)

        value = results["IsNullTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IsNullFalseEmptyString"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IsNullAlsoFalseAbcString"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IsNullAlsoFalseNumber1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IsNullAlsoFalseNumberZero"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IsFalseFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IsFalseTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IsFalseNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IsTrueTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IsTrueFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IsTrueNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))
    }
}
