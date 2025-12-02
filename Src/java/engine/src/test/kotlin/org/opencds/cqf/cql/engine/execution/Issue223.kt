package org.opencds.cqf.cql.engine.execution

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

internal class Issue223 : CqlTestBase() {
    @Test
    fun interval() {
        val results = engine.evaluate { library("Issue223") }.onlyResultOrThrow
        var value = results["Access Flattened List of List Items"]!!.value
        var list = value as MutableList<*>
        MatcherAssert.assertThat(list.size, Matchers.`is`(1))
        MatcherAssert.assertThat(list[0], Matchers.`is`<Any?>(true))

        value = results["Access Flattened List of List Items in a Single Query"]!!.value
        list = (value as MutableList<*>?)!!
        MatcherAssert.assertThat(list.size, Matchers.`is`(1))
        MatcherAssert.assertThat(list[0], Matchers.`is`<Any?>(true))
    }
}
