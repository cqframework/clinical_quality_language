package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import java.time.OffsetDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator.equal
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator.equivalent
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Tuple
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlLong
import org.opencds.cqf.cql.engine.runtime.toCqlString

// import static org.hamcrest.MatcherAssert.assertThat;
// import static org.hamcrest.Matchers.is;
// import static org.hamcrest.Matchers.nullValue;
internal class CqlTypesTest : CqlTestBase() {
    @Test
    fun all_types() {
        val bigDecimalZoneOffset = bigDecimalZoneOffset

        val results = engine.evaluate { library("CqlTypesTest") }.onlyResultOrThrow
        var value = results["AnyInteger"]!!.value
        assertEquals(5.toCqlInteger(), value, "AnyInteger")

        value = results["AnyLong"]!!.value
        assertEquals(12L.toCqlLong(), value, "AnyLong")

        value = results["AnyDecimal"]!!.value
        assertEquals(BigDecimal("5.0").toCqlDecimal(), value, "AnyDecimal")

        value = results["AnyQuantity"]!!.value
        assertTrue(
            equal(value, Quantity().withValue(BigDecimal("5.0")).withUnit("g"))?.value == true,
            "AnyQuantity",
        )

        value = results["AnyDateTime"]!!.value
        assertTrue(
            equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 4, 4)).value == true,
            "AnyDateTime",
        )

        value = results["AnyTime"]!!.value
        assertTrue(equivalent(value, Time(9, 0, 0, 0)).value == true, "AnyTime")

        value = results["AnyInterval"]!!.value
        assertEquals(
            true,
            equivalent(value, Interval(2.toCqlInteger(), true, 7.toCqlInteger(), true)).value,
            "AnyInterval",
        )

        value = results["AnyList"]!!.value
        assertEquals(
            listOf(1.toCqlInteger(), 2.toCqlInteger(), 3.toCqlInteger()).toCqlList(),
            value,
            "AnyList",
        )

        value = results["AnyTuple"]!!.value
        assertEquals(
            mutableMapOf<kotlin.String, Value?>(
                "id" to 5.toCqlInteger(),
                "name" to "Chris".toCqlString(),
            ),
            (value as Tuple).elements,
            "AnyTuple",
        )

        value = results["BooleanTestTrue"]!!.value
        assertIs<Boolean>(value, "BooleanTestTrue")
        assertTrue(value.value, "BooleanTestTrue")

        value = results["BooleanTestFalse"]!!.value
        assertIs<Boolean>(value, "BooleanTestFalse")
        assertFalse(value.value, "BooleanTestFalse")

        value = results["CodeLiteral"]!!.value
        assertTrue(
            equal(
                    value,
                    Code()
                        .withCode("8480-6")
                        .withSystem("http://loinc.org")
                        .withVersion("1.0")
                        .withDisplay("Systolic blood pressure"),
                )
                ?.value == true,
            "CodeLiteral",
        )

        value = results["CodeLiteral2"]!!.value
        assertTrue(
            equal(
                    value,
                    Code()
                        .withCode("1234-5")
                        .withSystem("http://example.org")
                        .withVersion("1.05")
                        .withDisplay("Test Code"),
                )
                ?.value == true,
            "CodeLiteral2",
        )

        value = results["ConceptTest"]!!.value
        assertTrue(
            equal(
                    value,
                    Concept()
                        .withCodes(
                            listOf(
                                Code()
                                    .withCode("8480-6")
                                    .withSystem("http://loinc.org")
                                    .withVersion("1.0")
                                    .withDisplay("Systolic blood pressure"),
                                Code()
                                    .withCode("1234-5")
                                    .withSystem("http://example.org")
                                    .withVersion("1.05")
                                    .withDisplay("Test Code"),
                            )
                        )
                        .withDisplay("Type B viral hepatitis"),
                )
                ?.value == true,
            "ConceptTest",
        )

        value = results["DateTimeNull"]!!.value
        assertNull(value, "DateTimeNull")

        value = results["DateTimeProper"]!!.value
        assertTrue(
            equivalent(value, DateTime(bigDecimalZoneOffset, 2016, 7, 7, 6, 25, 33, 910)).value ==
                true,
            "DateTimeProper",
        )

        value = results["DateTimeIncomplete"]!!.value
        assertTrue(
            equivalent(value, DateTime(bigDecimalZoneOffset, 2015, 2, 10)).value == true,
            "DateTimeIncomplete",
        )

        value = results["DateTimeUncertain"]!!.value
        assertEquals(19.toCqlInteger(), (value as Interval).start, "DateTimeUncertain")
        assertEquals(49.toCqlInteger(), value.end, "DateTimeUncertain")

        value = results["DateTimeMin"]!!.value
        assertTrue(
            equivalent(
                    value,
                    DateTime(OffsetDateTime.of(1, 1, 1, 0, 0, 0, 0, OffsetDateTime.now().offset)),
                )
                .value == true
        )

        value = results["DateTimeMax"]!!.value
        assertTrue(
            equivalent(value, DateTime(bigDecimalZoneOffset, 9999, 12, 31, 23, 59, 59, 999))
                .value == true
        )

        value = results["DecimalUpperBoundExcept"]!!.value
        assertEquals(
            BigDecimal("10000000000000000000000000000000000.00000000").toCqlDecimal(),
            value,
            "DecimalUpperBoundExcept",
        )

        value = results["DecimalLowerBoundExcept"]!!.value
        assertEquals(
            BigDecimal("-10000000000000000000000000000000000.00000000").toCqlDecimal(),
            value,
            "DecimalLowerBoundExcept",
        )

        // NOTE: This should also return an error as the fractional precision is greater than 8
        value = results["DecimalFractionalTooBig"]!!.value
        assertEquals(BigDecimal("5.999999999").toCqlDecimal(), value, "DecimalFractionalTooBig")

        value = results["DecimalPi"]!!.value
        assertEquals(BigDecimal("3.14159265").toCqlDecimal(), value, "DecimalPi")

        value = results["IntegerProper"]!!.value
        assertEquals(5000.toCqlInteger(), value, "IntegerProper")

        value = results["QuantityTest"]!!.value
        assertTrue(
            equal(value, Quantity().withValue(BigDecimal("150.2")).withUnit("[lb_av]"))?.value ==
                true
        )

        value = results["QuantityTest2"]!!.value
        assertTrue(
            equal(value, Quantity().withValue(BigDecimal("2.5589")).withUnit("{eskimo kisses}"))
                ?.value == true
        )

        // NOTE: This should also return an error as the fractional precision is greater than 8
        value = results["QuantityFractionalTooBig"]!!.value
        assertTrue(
            equal(value, Quantity().withValue(BigDecimal("5.99999999")).withUnit("g"))?.value ==
                true
        )

        value = results["RatioTest"]!!.value
        assertTrue(
            equal(
                    (value as Ratio).numerator,
                    Quantity().withValue(BigDecimal("150.2")).withUnit("[lb_av]"),
                )
                ?.value == true
        )
        assertTrue(
            equal(
                    value.denominator,
                    Quantity().withValue(BigDecimal("2.5589")).withUnit("{eskimo kisses}"),
                )
                ?.value == true
        )

        value = results["StringTestEscapeQuotes"]!!.value
        assertEquals(
            "\'I start with a single quote and end with a double quote\"".toCqlString(),
            value,
            "StringTestEscapeQuotes",
        )

        value = results["TimeProper"]!!.value
        assertTrue(equivalent(value, Time(10, 25, 12, 863)).value == true, "TimeProper")

        value = results["TimeAllMax"]!!.value
        assertTrue(equivalent(value, Time(23, 59, 59, 999)).value == true, "TimeAllMax")

        value = results["TimeAllMin"]!!.value
        assertTrue(equivalent(value, Time(0, 0, 0, 0)).value == true, "TimeAllMin")
    }
}
