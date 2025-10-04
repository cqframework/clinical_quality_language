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
        val results = engine.evaluate(toElmIdentifier("CqlNullologicalOperatorsTest"))
        var value = results.forExpression("CoalesceANull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("a"))

        value = results.forExpression("CoalesceNullA")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("a"))

        value = results.forExpression("CoalesceEmptyList")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("CoalesceListFirstA")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("a"))

        value = results.forExpression("CoalesceListLastA")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`("a"))

        value = results.forExpression("CoalesceFirstList")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<String?>("a")))

        value = results.forExpression("CoalesceLastList")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<String?>("a")))

        value = results.forExpression("DateTimeCoalesce")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 5, 18)) ==
                true
        )

        value = results.forExpression("DateTimeListCoalesce")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 5, 18)) ==
                true
        )

        value = results.forExpression("TimeCoalesce")!!.value()
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(5, 15, 33, 556)) == true)

        value = results.forExpression("TimeListCoalesce")!!.value()
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(5, 15, 33, 556)) == true)

        value = results.forExpression("IsNullTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IsNullFalseEmptyString")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IsNullAlsoFalseAbcString")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IsNullAlsoFalseNumber1")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IsNullAlsoFalseNumberZero")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IsFalseFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IsFalseTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IsFalseNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IsTrueTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IsTrueFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IsTrueNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))
    }
}
