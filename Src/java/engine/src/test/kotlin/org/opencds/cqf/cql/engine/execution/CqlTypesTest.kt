package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
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

        val results = engine.evaluate(toElmIdentifier("CqlTypesTest"))
        var value = results.forExpression("AnyInteger").value()
        Assertions.assertEquals(5, value, "AnyInteger")

        value = results.forExpression("AnyLong").value()
        Assertions.assertEquals(value, "12".toLong(), "AnyLong")

        value = results.forExpression("AnyDecimal").value()
        Assertions.assertEquals(value, BigDecimal("5.0"), "AnyDecimal")

        value = results.forExpression("AnyQuantity").value()
        Assertions.assertTrue(
            (value as Quantity).equal(Quantity().withValue(BigDecimal("5.0")).withUnit("g")),
            "AnyQuantity",
        )

        value = results.forExpression("AnyDateTime").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 4, 4)),
            "AnyDateTime",
        )

        value = results.forExpression("AnyTime").value()
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(9, 0, 0, 0)), "AnyTime")

        value = results.forExpression("AnyInterval").value()
        Assertions.assertEquals((value as Interval?), Interval(2, true, 7, true), "AnyInterval")

        value = results.forExpression("AnyList").value()
        Assertions.assertEquals(value, mutableListOf<Int?>(1, 2, 3), "AnyList")

        value = results.forExpression("AnyTuple").value()
        Assertions.assertEquals(
            (value as Tuple).getElements(),
            object : HashMap<String?, Any?>() {
                init {
                    put("id", 5)
                    put("name", "Chris")
                }
            },
            "AnyTuple",
        )

        value = results.forExpression("BooleanTestTrue").value()
        Assertions.assertEquals("Boolean", value.javaClass.getSimpleName(), "BooleanTestTrue")
        Assertions.assertTrue(value as Boolean, "BooleanTestTrue")

        value = results.forExpression("BooleanTestFalse").value()
        Assertions.assertEquals("Boolean", value.javaClass.getSimpleName(), "BooleanTestFalse")
        Assertions.assertFalse(value as Boolean, "BooleanTestFalse")

        value = results.forExpression("CodeLiteral").value()
        Assertions.assertTrue(
            (value as Code).equal(
                Code()
                    .withCode("8480-6")
                    .withSystem("http://loinc.org")
                    .withVersion("1.0")
                    .withDisplay("Systolic blood pressure")
            ),
            "CodeLiteral",
        )

        value = results.forExpression("CodeLiteral2").value()
        Assertions.assertTrue(
            (value as Code).equal(
                Code()
                    .withCode("1234-5")
                    .withSystem("http://example.org")
                    .withVersion("1.05")
                    .withDisplay("Test Code")
            ),
            "CodeLiteral2",
        )

        value = results.forExpression("ConceptTest").value()
        Assertions.assertTrue(
            (value as Concept).equal(
                Concept()
                    .withCodes(
                        listOf<Code?>(
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
                    .withDisplay("Type B viral hepatitis")
            ),
            "ConceptTest",
        )

        value = results.forExpression("DateTimeNull").value()
        Assertions.assertNull(value, "DateTimeNull")

        value = results.forExpression("DateTimeProper").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                value,
                DateTime(bigDecimalZoneOffset, 2016, 7, 7, 6, 25, 33, 910),
            ),
            "DateTimeProper",
        )

        value = results.forExpression("DateTimeIncomplete").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2015, 2, 10)),
            "DateTimeIncomplete",
        )

        value = results.forExpression("DateTimeUncertain").value()
        Assertions.assertEquals(19, (value as Interval).start, "DateTimeUncertain")
        Assertions.assertEquals(49, value.end, "DateTimeUncertain")

        value = results.forExpression("DateTimeMin").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                value,
                DateTime(OffsetDateTime.of(1, 1, 1, 0, 0, 0, 0, OffsetDateTime.now().offset)),
            )
        )

        value = results.forExpression("DateTimeMax").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                value,
                DateTime(bigDecimalZoneOffset, 9999, 12, 31, 23, 59, 59, 999),
            )
        )

        value = results.forExpression("DecimalUpperBoundExcept").value()
        Assertions.assertEquals(
            value,
            BigDecimal("10000000000000000000000000000000000.00000000"),
            "DecimalUpperBoundExcept",
        )

        value = results.forExpression("DecimalLowerBoundExcept").value()
        Assertions.assertEquals(
            value,
            BigDecimal("-10000000000000000000000000000000000.00000000"),
            "DecimalLowerBoundExcept",
        )

        // NOTE: This should also return an error as the fractional precision is greater than 8
        value = results.forExpression("DecimalFractionalTooBig").value()
        Assertions.assertEquals(value, BigDecimal("5.999999999"), "DecimalFractionalTooBig")

        value = results.forExpression("DecimalPi").value()
        Assertions.assertEquals(value, BigDecimal("3.14159265"), "DecimalPi")

        value = results.forExpression("IntegerProper").value()
        Assertions.assertEquals(5000, value, "IntegerProper")

        value = results.forExpression("QuantityTest").value()
        Assertions.assertTrue(
            (value as Quantity).equal(Quantity().withValue(BigDecimal("150.2")).withUnit("[lb_av]"))
        )

        value = results.forExpression("QuantityTest2").value()
        Assertions.assertTrue(
            (value as Quantity).equal(
                Quantity().withValue(BigDecimal("2.5589")).withUnit("{eskimo kisses}")
            )
        )

        // NOTE: This should also return an error as the fractional precision is greater than 8
        value = results.forExpression("QuantityFractionalTooBig").value()
        Assertions.assertTrue(
            (value as Quantity).equal(Quantity().withValue(BigDecimal("5.99999999")).withUnit("g"))
        )

        value = results.forExpression("RatioTest").value()
        Assertions.assertTrue(
            (value as Ratio)
                .numerator
                .equal(Quantity().withValue(BigDecimal("150.2")).withUnit("[lb_av]"))
        )
        Assertions.assertTrue(
            value.denominator.equal(
                Quantity().withValue(BigDecimal("2.5589")).withUnit("{eskimo kisses}")
            )
        )

        value = results.forExpression("StringTestEscapeQuotes").value()
        Assertions.assertEquals(
            "\'I start with a single quote and end with a double quote\"",
            value,
            "StringTestEscapeQuotes",
        )

        value = results.forExpression("TimeProper").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value, Time(10, 25, 12, 863)),
            "TimeProper",
        )

        value = results.forExpression("TimeAllMax").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value, Time(23, 59, 59, 999)),
            "TimeAllMax",
        )

        value = results.forExpression("TimeAllMin").value()
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(0, 0, 0, 0)), "TimeAllMin")
    }
}
