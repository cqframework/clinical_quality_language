package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.opencds.cqf.cql.engine.debug.Location
import org.opencds.cqf.cql.engine.debug.SourceLocator
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator
import org.opencds.cqf.cql.engine.elm.executing.WidthEvaluator
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

internal class RuntimeTests {
    @Test
    fun quantityToString() {
        var q = Quantity().withValue(null).withUnit(null)
        assertEquals("null 'null'", q.toString())

        q = Quantity()
        assertEquals("0.0 '1'", q.toString())

        q = Quantity().withValue(BigDecimal("1.0")).withUnit("g")
        assertEquals("1.0 'g'", q.toString())

        q = Quantity().withValue(BigDecimal("0.05")).withUnit("mg")
        assertEquals("0.05 'mg'", q.toString())
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
        assertEquals(s, interval.start)
        assertEquals(e, interval.end)
        assertEquals(
            true,
            EqualEvaluator.equal(
                    Quantity().withValue(BigDecimal(0)).withUnit("kg/m3"),
                    WidthEvaluator.width(interval, state),
                    state,
                )
                ?.value,
        )
    }

    @Test
    fun tupleToString() {
        var t = Tuple()
        assertEquals("Tuple {}", t.toString())

        t = Tuple()
        t.elements["id"] = 1.toCqlInteger()
        t.elements["value"] = Quantity().withValue(BigDecimal("1.0")).withUnit("g")
        assertEquals("Tuple {\n  id: 1\n  value: 1.0 'g'\n}", t.toString())
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

        assertEquals("Example.1:1-89:80(1)", sourceLocator.toString())

        sourceLocator = SourceLocator(null, null, null, null, null, null)

        assertEquals("?.?(?)", sourceLocator.toString())
    }
}
