package org.opencds.cqf.cql.engine.execution

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class CqlLogicalOperatorsTest : CqlTestBase() {
    @Test
    fun all_logical_operators() {
        val results = engine.evaluate { library("CqlLogicalOperatorsTest") }.onlyResultOrThrow
        var value = results["TrueAndTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TrueAndFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TrueAndNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["FalseAndTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["FalseAndFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["FalseAndNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["NullAndTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["NullAndFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["NullAndNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["NotTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["NotFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["NotNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["TrueOrTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TrueOrFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TrueOrNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["FalseOrTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["FalseOrFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["FalseOrNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["NullOrTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["NullOrFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["NullOrNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["TrueXorTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TrueXorFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TrueXorNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["FalseXorTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["FalseXorFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["FalseXorNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["NullXorTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["NullXorFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["NullXorNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))
    }
}
