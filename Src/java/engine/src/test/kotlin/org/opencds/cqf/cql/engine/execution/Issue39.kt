package org.opencds.cqf.cql.engine.execution

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class Issue39 : CqlTestBase() {
    @Test
    fun interval() {
        val results = engine.evaluate(toElmIdentifier("Issue39"))
        val value = results.forExpression("EquivalentIntervals").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))
    }
}
