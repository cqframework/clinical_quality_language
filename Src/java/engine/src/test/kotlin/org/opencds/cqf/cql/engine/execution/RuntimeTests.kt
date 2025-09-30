package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.debug.Location
import org.opencds.cqf.cql.engine.debug.SourceLocator
import org.opencds.cqf.cql.engine.exception.InvalidInterval
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Tuple

internal class RuntimeTests {
    @Test
    fun quantityToString() {
        var q = Quantity().withValue(null).withUnit(null)
        MatcherAssert.assertThat(q.toString(), Matchers.`is`("null 'null'"))

        q = Quantity()
        MatcherAssert.assertThat(q.toString(), Matchers.`is`("0.0 '1'"))

        q = Quantity().withValue(BigDecimal("1.0")).withUnit("g")
        MatcherAssert.assertThat(q.toString(), Matchers.`is`("1.0 'g'"))

        q = Quantity().withValue(BigDecimal("0.05")).withUnit("mg")
        MatcherAssert.assertThat(q.toString(), Matchers.`is`("0.05 'mg'"))
    }

    @Test
    fun intervalOfQuantityWithDifferentUOM() {
        val s = Quantity().withValue(BigDecimal(10)).withUnit("mg/mL")
        val e = Quantity().withValue(BigDecimal(10)).withUnit("kg/m3")

        Assertions.assertThrows(InvalidInterval::class.java) { Interval(s, true, e, true) }
    }

    @Test
    fun tupleToString() {
        var t = Tuple()
        MatcherAssert.assertThat(t.toString(), Matchers.`is`("Tuple {}"))

        t = Tuple()
        t.getElements()["id"] = 1
        t.getElements()["value"] = Quantity().withValue(BigDecimal("1.0")).withUnit("g")
        MatcherAssert.assertThat(
            t.toString(),
            Matchers.`is`("Tuple {\n  id: 1\n  value: 1.0 'g'\n}"),
        )
    }

    @Test
    fun sourceLocation() {
        var sourceLocator =
            SourceLocator(
                "http://cql.hl7.org/Library/Example",
                "Example",
                "1.0.0",
                "1",
                "RetrieveEvaluator",
                Location.fromLocator("1:1-89:80"),
            )

        MatcherAssert.assertThat(sourceLocator.toString(), Matchers.`is`("Example.1:1-89:80(1)"))

        sourceLocator = SourceLocator(null, null, null, null, null, null)

        MatcherAssert.assertThat(sourceLocator.toString(), Matchers.`is`("?.?(?)"))
    }
}
