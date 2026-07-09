package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator.equal
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator.equivalent
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlLong
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class CqlTypesOperatorsTest : CqlTestBase() {
    @ParameterizedTest
    @MethodSource("timezones")
    fun all_types_operators(timezone: String?) {
        val oldTz = System.getProperty("user.timezone")
        // This is the ONLY thing that will work.  System.setProperty() and -Duser.timezone do NOT
        // work
        TimeZone.setDefault(TimeZone.getTimeZone(timezone))
        engine.state.setEvaluationDateTime(ZonedDateTime.now())

        try {
            val bigDecimalZoneOffset = bigDecimalZoneOffset

            val results = engine.evaluate { library("CqlTypeOperatorsTest") }.onlyResultOrThrow
            var value = results["AsQuantity"]!!.value
            assertTrue(
                equal(value, Quantity().withValue(BigDecimal("45.5")).withUnit("g"))?.value == true
            )

            value = results["CastAsQuantity"]!!.value
            assertTrue(
                equal(value, Quantity().withValue(BigDecimal("45.5")).withUnit("g"))?.value == true
            )

            value = results["AsDateTime"]!!.value
            assertTrue(equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 1, 1)).value == true)

            value = results["IntegerToDecimal"]!!.value
            assertEquals(BigDecimal(5).toCqlDecimal(), value)

            value = results["IntegerToString"]!!.value
            assertEquals("5".toCqlString(), value)

            value = results["StringToDateTime"]!!.value
            assertTrue(equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 1, 1)).value == true)

            value = results["StringToTime"]!!.value
            assertTrue(equivalent(value, Time(14, 30, 0, 0)).value == true)

            value = results["ConvertQuantity"]!!.value
            assertTrue(
                equivalent(value, Quantity().withValue(BigDecimal("0.005")).withUnit("g")).value ==
                    true
            )

            value = results["ConvertSyntax"]!!.value
            assertTrue(
                equivalent(value, Quantity().withValue(BigDecimal("0.005")).withUnit("g")).value ==
                    true
            )

            value = results["ConvertsToBooleanTrue"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToBooleanFalse"]!!.value
            assertFalse((value as Boolean).value)

            value = results["ConvertsToBooleanNull"]!!.value
            Assertions.assertNull(value)

            value = results["ConvertsToDateTrue"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToDateFalse"]!!.value
            assertFalse((value as Boolean).value)

            value = results["ConvertsToDateNull"]!!.value
            Assertions.assertNull(value)

            value = results["ConvertsToDateTimeStringTrue"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToDateTimeDateTrue"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToDateTimeFalse"]!!.value
            assertFalse((value as Boolean).value)

            value = results["ConvertsToDateTimeNull"]!!.value
            Assertions.assertNull(value)

            value = results["ConvertsToDecimalTrue"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToDecimalFalse"]!!.value
            assertFalse((value as Boolean).value)

            value = results["ConvertsToDecimalNull"]!!.value
            Assertions.assertNull(value)

            value = results["ConvertsToIntegerTrue"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToIntegerLong"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToIntegerFalse"]!!.value
            assertFalse((value as Boolean).value)

            value = results["ConvertsToIntegerNull"]!!.value
            Assertions.assertNull(value)

            value = results["ConvertsToLongTrue"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToLongFalse"]!!.value
            assertFalse((value as Boolean).value)

            value = results["ConvertsToLongNull"]!!.value
            Assertions.assertNull(value)

            value = results["ConvertsToQuantityStringTrue"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToQuantityStringFalse"]!!.value
            assertFalse((value as Boolean).value)

            value = results["ConvertsToQuantityIntegerTrue"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToQuantityDecimalTrue"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToQuantityRatioTrue"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToQuantityNull"]!!.value
            Assertions.assertNull(value)

            value = results["ConvertsToStringBoolean"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToStringInteger"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToStringLong"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToStringDecimal"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToStringQuantity"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToStringRatio"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToStringDate"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToStringDateTime"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToStringTime"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToStringNull"]!!.value
            Assertions.assertNull(value)

            value = results["ConvertsToTimeTrue"]!!.value
            assertTrue((value as Boolean).value)

            value = results["ConvertsToTimeFalse"]!!.value
            assertFalse((value as Boolean).value)

            value = results["ConvertsToTimeNull"]!!.value
            Assertions.assertNull(value)

            value = results["IntegerIsInteger"]!!.value
            assertTrue((value as Boolean).value)

            value = results["StringIsInteger"]!!.value
            assertFalse((value as Boolean).value)

            value = results["StringNoToBoolean"]!!.value
            assertFalse((value as Boolean).value)

            value = results["CodeToConcept1"]!!.value
            assertTrue(
                equivalent(value, Concept().withCode(Code().withCode("8480-6"))).value == true
            )

            value = results["ToDateTime0"]!!.value
            assertTrue(equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 1)).value == true)

            value = results["ToDateTime1"]!!.value
            assertTrue(equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 1, 1)).value == true)

            value = results["ToDateTime2"]!!.value
            assertTrue(
                equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 1, 1, 12, 5)).value == true
            )

            value = results["ToDateTime3"]!!.value
            assertTrue(
                equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 1, 1, 12, 5, 5, 955))
                    .value == true
            )

            value = results["ToDateTime4"]!!.value
            assertTrue(
                equivalent(value, DateTime(BigDecimal("1.5"), 2014, 1, 1, 12, 5, 5, 955)).value ==
                    true,
                "ToDateTime4 vs. new DateTime(-1.5)",
            )

            value = results["ToDateTime5"]!!.value
            assertTrue(
                equivalent(value, DateTime(BigDecimal("-1.25"), 2014, 1, 1, 12, 5, 5, 955)).value ==
                    true,
                "ToDateTime5 vs. new DateTime(-1.25)",
            )

            value = results["ToDateTime6"]!!.value
            val bigDecimalOffsetForUtc = getBigDecimalZoneOffset(ZoneId.of("UTC"))
            assertTrue(
                equivalent(value, DateTime(bigDecimalOffsetForUtc, 2014, 1, 1, 12, 5, 5, 955))
                    .value == true
            )

            value = results["ToDateTimeMalformed"]!!.value
            Assertions.assertNull(value)

            value = results["String25D5ToDecimal"]!!.value
            assertEquals(BigDecimal("25.5").toCqlDecimal(), value)

            value = results["StringNeg25ToInteger"]!!.value
            assertEquals((-25).toCqlInteger(), value)

            value = results["String123ToLong"]!!.value
            assertEquals(123L.toCqlLong(), value)

            value = results["String5D5CMToQuantity"]!!.value
            assertTrue(
                equal(value, Quantity().withValue(BigDecimal("5.5")).withUnit("cm"))?.value == true
            )

            value = results["StringInvalidToQuantityNull"]!!.value
            Assertions.assertNull(value)

            value = results["String100PerMinPerSqMeterToQuantity"]!!.value
            assertTrue(
                equal(value, Quantity().withValue(BigDecimal("100")).withUnit("daL/min/m2"))
                    ?.value == true
            )

            value = results["String100UnitPer10BillionToQuantity"]!!.value
            assertTrue(
                equal(value, Quantity().withValue(BigDecimal("100")).withUnit("U/10*10{cells}"))
                    ?.value == true
            )

            value = results["String60DayPer7DayToQuantity"]!!.value
            assertTrue(
                equal(value, Quantity().withValue(BigDecimal("60")).withUnit("d/(7.d)"))?.value ==
                    true
            )

            value = results["String60EhrlichPer100gmToQuantity"]!!.value
            assertTrue(
                equal(value, Quantity().withValue(BigDecimal("60")).withUnit("{EhrlichU}/100.g"))
                    ?.value == true
            )

            value = results["StringPercentToQuantity"]!!.value
            assertTrue(
                equal(value, Quantity().withValue(BigDecimal("60")).withUnit("%"))?.value == true
            )

            value = results["StringPercentWithoutQuoteToQuantity"]!!.value
            assertTrue(
                equal(value, Quantity().withValue(BigDecimal("70")).withUnit("%"))?.value == true
            )

            value = results["StringPercentWithTabToQuantity"]!!.value
            assertTrue(
                equal(value, Quantity().withValue(BigDecimal("80")).withUnit("%"))?.value == true
            )

            value = results["StringPercentWithMultiSpacesToQuantity"]!!.value
            assertTrue(
                equal(value, Quantity().withValue(BigDecimal("90")).withUnit("%"))?.value == true
            )

            value = results["StringPercentWithSpacesUnitToQuantity"]!!.value
            assertTrue(
                equal(value, Quantity().withValue(BigDecimal("10")).withUnit("ml"))?.value == true
            )

            value = results["StringPercentWithQuoteUnitToQuantity"]!!.value
            assertTrue(
                equal(value, Quantity().withValue(BigDecimal("20")).withUnit("ml"))?.value == true
            )

            value = results["ToRatioIsValid"]!!.value
            assertTrue(
                equal(
                        (value as Ratio).numerator,
                        Quantity().withValue(BigDecimal("1.0")).withUnit("mg"),
                    )
                    ?.value == true
            )
            assertTrue(
                equal(value.denominator, Quantity().withValue(BigDecimal("2.0")).withUnit("mg"))
                    ?.value == true
            )

            value = results["ToRatioIsNull"]!!.value
            Assertions.assertNull(value)

            value = results["IntegerNeg5ToString"]!!.value
            assertEquals("-5".toCqlString(), value)

            value = results["LongNeg5ToString"]!!.value
            assertEquals("-5".toCqlString(), value)

            value = results["Decimal18D55ToString"]!!.value
            assertEquals("18.55".toCqlString(), value)

            value = results["Quantity5D5CMToString"]!!.value
            assertEquals("5.5 'cm'".toCqlString(), value)

            value = results["BooleanTrueToString"]!!.value
            assertEquals("true".toCqlString(), value)

            value = results["ToTime1"]!!.value
            assertTrue(equivalent(value, Time(14, 30, 0, 0)).value == true)

            value = results["ToTimeMalformed"]!!.value
            Assertions.assertNull(value)
        } finally {
            TimeZone.setDefault(TimeZone.getTimeZone(oldTz))
        }
    }
}
