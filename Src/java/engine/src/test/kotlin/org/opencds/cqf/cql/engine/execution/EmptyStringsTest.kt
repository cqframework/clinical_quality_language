package org.opencds.cqf.cql.engine.execution

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class EmptyStringsTest : CqlTestBase() {
    @Test
    fun all_empty_string() {
        val results = engine.evaluate(toElmIdentifier("EmptyStringsTest"))
        var value = results.forExpression("Null")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("Space")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(" "))

        value = results.forExpression("Empty")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(""))
    }
}
