package org.opencds.cqf.cql.engine.execution

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class CqlLogicalOperatorsTest : CqlTestBase() {
    @Test
    fun all_logical_operators() {
        val results = engine.evaluate(toElmIdentifier("CqlLogicalOperatorsTest"))
        var value = results.forExpression("TrueAndTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TrueAndFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TrueAndNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("FalseAndTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("FalseAndFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("FalseAndNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("NullAndTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("NullAndFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("NullAndNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("NotTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("NotFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("NotNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("TrueOrTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TrueOrFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TrueOrNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("FalseOrTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("FalseOrFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("FalseOrNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("NullOrTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("NullOrFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("NullOrNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("TrueXorTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TrueXorFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TrueXorNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("FalseXorTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("FalseXorFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("FalseXorNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("NullXorTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("NullXorFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("NullXorNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))
    }
}
