package org.opencds.cqf.cql.engine.execution

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class EmptyStringsTest : CqlTestBase() {
    @Test
    fun all_empty_string() {
        val results = engine.evaluate { library("EmptyStringsTest") }.onlyResultOrThrow
        var value = results["Null"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["Space"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(" "))

        value = results["Empty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(""))
    }
}
