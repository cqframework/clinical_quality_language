package org.opencds.cqf.cql.engine.execution

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class DateComparatorTest : CqlTestBase() {
    @Test
    fun date_comparator() {
        val results = engine.evaluate { library("DateComparatorTest") }.onlyResultOrThrow
        val value = results["Date Comparator Test"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))
    }
}
