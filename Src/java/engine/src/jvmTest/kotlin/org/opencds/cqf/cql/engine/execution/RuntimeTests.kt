package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.debug.Location
import org.opencds.cqf.cql.engine.debug.SourceLocator
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator
import org.opencds.cqf.cql.engine.elm.executing.WidthEvaluator
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
        val modelManager = ModelManager()
        val libraryManager = LibraryManager(modelManager)
        val environment = Environment(libraryManager)
        val state = State(environment)

        // To spellings of the mass per volume quantity so we can assert that the width of the
        // interval is 0.
        val s = Quantity().withValue(BigDecimal(10)).withUnit("mg/mL")
        val e = Quantity().withValue(BigDecimal(10)).withUnit("kg/m3")
        val interval = Interval(s, true, e, true, state)
        Assertions.assertEquals(s, interval.start)
        Assertions.assertEquals(e, interval.end)
        Assertions.assertEquals(
            true,
            EqualEvaluator.equal(
                Quantity().withValue(BigDecimal(0)).withUnit("kg/m3"),
                WidthEvaluator.width(interval, state),
                state,
            ),
        )
    }

    @Test
    fun tupleToString() {
        var t = Tuple()
        MatcherAssert.assertThat(t.toString(), Matchers.`is`("Tuple {}"))

        t = Tuple()
        t.elements["id"] = 1
        t.elements["value"] = Quantity().withValue(BigDecimal("1.0")).withUnit("g")
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
