package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator.equal
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator.equivalent
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Tuple

// import static org.hamcrest.MatcherAssert.assertThat;
// import static org.hamcrest.Matchers.is;
// import static org.hamcrest.Matchers.nullValue;
internal class CqlTypesTest : CqlTestBase() {
    @Test
    fun all_types() {
        val bigDecimalZoneOffset = bigDecimalZoneOffset

        val results = engine.evaluate { library("CqlTypesTest") }.onlyResultOrThrow
        var value = results["AnyInteger"]!!.value
        Assertions.assertEquals(5, value, "AnyInteger")

        value = results["AnyLong"]!!.value
        Assertions.assertEquals(value, "12".toLong(), "AnyLong")

        value = results["AnyDecimal"]!!.value
        Assertions.assertEquals(value, BigDecimal("5.0"), "AnyDecimal")

        value = results["AnyQuantity"]!!.value
        Assertions.assertTrue(
            equal(value, Quantity().withValue(BigDecimal("5.0")).withUnit("g")) == true,
            "AnyQuantity",
        )

        value = results["AnyDateTime"]!!.value
        Assertions.assertTrue(
            equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 4, 4)) == true,
            "AnyDateTime",
        )

        value = results["AnyTime"]!!.value
        Assertions.assertTrue(equivalent(value, Time(9, 0, 0, 0)) == true, "AnyTime")

        value = results["AnyInterval"]!!.value
        Assertions.assertEquals(true, equivalent(value, Interval(2, true, 7, true)), "AnyInterval")

        value = results["AnyList"]!!.value
        Assertions.assertEquals(value, listOf(1, 2, 3), "AnyList")

        value = results["AnyTuple"]!!.value
        Assertions.assertEquals(
            (value as Tuple).elements,
            object : HashMap<String?, Any?>() {
                init {
                    put("id", 5)
                    put("name", "Chris")
                }
            },
            "AnyTuple",
        )

        value = results["BooleanTestTrue"]!!.value
        Assertions.assertEquals("Boolean", value!!.javaClass.getSimpleName(), "BooleanTestTrue")
        Assertions.assertTrue(value as Boolean, "BooleanTestTrue")

        value = results["BooleanTestFalse"]!!.value
        Assertions.assertEquals("Boolean", value!!.javaClass.getSimpleName(), "BooleanTestFalse")
        Assertions.assertFalse(value as Boolean, "BooleanTestFalse")

        value = results["CodeLiteral"]!!.value
        Assertions.assertTrue(
            equal(
                value,
                Code()
                    .withCode("8480-6")
                    .withSystem("http://loinc.org")
                    .withVersion("1.0")
                    .withDisplay("Systolic blood pressure"),
            ) == true,
            "CodeLiteral",
        )

        value = results["CodeLiteral2"]!!.value
        Assertions.assertTrue(
            equal(
                value,
                Code()
                    .withCode("1234-5")
                    .withSystem("http://example.org")
                    .withVersion("1.05")
                    .withDisplay("Test Code"),
            ) == true,
            "CodeLiteral2",
        )

        value = results["ConceptTest"]!!.value
        Assertions.assertTrue(
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
            ) == true,
            "ConceptTest",
        )

        value = results["DateTimeNull"]!!.value
        Assertions.assertNull(value, "DateTimeNull")

        value = results["DateTimeProper"]!!.value
        Assertions.assertTrue(
            equivalent(value, DateTime(bigDecimalZoneOffset, 2016, 7, 7, 6, 25, 33, 910)) == true,
            "DateTimeProper",
        )

        value = results["DateTimeIncomplete"]!!.value
        Assertions.assertTrue(
            equivalent(value, DateTime(bigDecimalZoneOffset, 2015, 2, 10)) == true,
            "DateTimeIncomplete",
        )

        value = results["DateTimeUncertain"]!!.value
        Assertions.assertEquals(19, (value as Interval).start, "DateTimeUncertain")
        Assertions.assertEquals(49, value.end, "DateTimeUncertain")

        value = results["DateTimeMin"]!!.value
        Assertions.assertTrue(
            equivalent(
                value,
                DateTime(OffsetDateTime.of(1, 1, 1, 0, 0, 0, 0, OffsetDateTime.now().offset)),
            ) == true
        )

        value = results["DateTimeMax"]!!.value
        Assertions.assertTrue(
            equivalent(value, DateTime(bigDecimalZoneOffset, 9999, 12, 31, 23, 59, 59, 999)) == true
        )

        value = results["DecimalUpperBoundExcept"]!!.value
        Assertions.assertEquals(
            value,
            BigDecimal("10000000000000000000000000000000000.00000000"),
            "DecimalUpperBoundExcept",
        )

        value = results["DecimalLowerBoundExcept"]!!.value
        Assertions.assertEquals(
            value,
            BigDecimal("-10000000000000000000000000000000000.00000000"),
            "DecimalLowerBoundExcept",
        )

        // NOTE: This should also return an error as the fractional precision is greater than 8
        value = results["DecimalFractionalTooBig"]!!.value
        Assertions.assertEquals(value, BigDecimal("5.999999999"), "DecimalFractionalTooBig")

        value = results["DecimalPi"]!!.value
        Assertions.assertEquals(value, BigDecimal("3.14159265"), "DecimalPi")

        value = results["IntegerProper"]!!.value
        Assertions.assertEquals(5000, value, "IntegerProper")

        value = results["QuantityTest"]!!.value
        Assertions.assertTrue(
            equal(value, Quantity().withValue(BigDecimal("150.2")).withUnit("[lb_av]")) == true
        )

        value = results["QuantityTest2"]!!.value
        Assertions.assertTrue(
            equal(value, Quantity().withValue(BigDecimal("2.5589")).withUnit("{eskimo kisses}")) ==
                true
        )

        // NOTE: This should also return an error as the fractional precision is greater than 8
        value = results["QuantityFractionalTooBig"]!!.value
        Assertions.assertTrue(
            equal(value, Quantity().withValue(BigDecimal("5.99999999")).withUnit("g")) == true
        )

        value = results["RatioTest"]!!.value
        Assertions.assertTrue(
            equal(
                (value as Ratio).numerator,
                Quantity().withValue(BigDecimal("150.2")).withUnit("[lb_av]"),
            ) == true
        )
        Assertions.assertTrue(
            equal(
                value.denominator,
                Quantity().withValue(BigDecimal("2.5589")).withUnit("{eskimo kisses}"),
            ) == true
        )

        value = results["StringTestEscapeQuotes"]!!.value
        Assertions.assertEquals(
            "\'I start with a single quote and end with a double quote\"",
            value,
            "StringTestEscapeQuotes",
        )

        value = results["TimeProper"]!!.value
        Assertions.assertTrue(equivalent(value, Time(10, 25, 12, 863)) == true, "TimeProper")

        value = results["TimeAllMax"]!!.value
        Assertions.assertTrue(equivalent(value, Time(23, 59, 59, 999)) == true, "TimeAllMax")

        value = results["TimeAllMin"]!!.value
        Assertions.assertTrue(equivalent(value, Time(0, 0, 0, 0)) == true, "TimeAllMin")
    }
}
