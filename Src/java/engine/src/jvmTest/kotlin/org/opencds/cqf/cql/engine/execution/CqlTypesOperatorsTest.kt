package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator.equal
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator.equivalent
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio
import org.opencds.cqf.cql.engine.runtime.Time

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
            Assertions.assertTrue(
                equal(value, Quantity().withValue(BigDecimal("45.5")).withUnit("g")) == true
            )

            value = results["CastAsQuantity"]!!.value
            Assertions.assertTrue(
                equal(value, Quantity().withValue(BigDecimal("45.5")).withUnit("g")) == true
            )

            value = results["AsDateTime"]!!.value
            Assertions.assertTrue(
                equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 1, 1)) == true
            )

            value = results["IntegerToDecimal"]!!.value
            Assertions.assertEquals(value, BigDecimal(5))

            value = results["IntegerToString"]!!.value
            Assertions.assertEquals("5", value)

            value = results["StringToDateTime"]!!.value
            Assertions.assertTrue(
                equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 1, 1)) == true
            )

            value = results["StringToTime"]!!.value
            Assertions.assertTrue(equivalent(value, Time(14, 30, 0, 0)) == true)

            value = results["ConvertQuantity"]!!.value
            Assertions.assertTrue(
                equivalent(value, Quantity().withValue(BigDecimal("0.005")).withUnit("g")) == true
            )

            value = results["ConvertSyntax"]!!.value
            Assertions.assertTrue(
                equivalent(value, Quantity().withValue(BigDecimal("0.005")).withUnit("g")) == true
            )

            value = results["ConvertsToBooleanTrue"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToBooleanFalse"]!!.value
            Assertions.assertFalse((value as Boolean?)!!)

            value = results["ConvertsToBooleanNull"]!!.value
            Assertions.assertNull(value)

            value = results["ConvertsToDateTrue"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToDateFalse"]!!.value
            Assertions.assertFalse((value as Boolean?)!!)

            value = results["ConvertsToDateNull"]!!.value
            Assertions.assertNull(value)

            value = results["ConvertsToDateTimeStringTrue"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToDateTimeDateTrue"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToDateTimeFalse"]!!.value
            Assertions.assertFalse((value as Boolean?)!!)

            value = results["ConvertsToDateTimeNull"]!!.value
            Assertions.assertNull(value)

            value = results["ConvertsToDecimalTrue"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToDecimalFalse"]!!.value
            Assertions.assertFalse((value as Boolean?)!!)

            value = results["ConvertsToDecimalNull"]!!.value
            Assertions.assertNull(value)

            value = results["ConvertsToIntegerTrue"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToIntegerLong"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToIntegerFalse"]!!.value
            Assertions.assertFalse((value as Boolean?)!!)

            value = results["ConvertsToIntegerNull"]!!.value
            Assertions.assertNull(value)

            value = results["ConvertsToLongTrue"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToLongFalse"]!!.value
            Assertions.assertFalse((value as Boolean?)!!)

            value = results["ConvertsToLongNull"]!!.value
            Assertions.assertNull(value)

            value = results["ConvertsToQuantityStringTrue"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToQuantityStringFalse"]!!.value
            Assertions.assertFalse((value as Boolean?)!!)

            value = results["ConvertsToQuantityIntegerTrue"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToQuantityDecimalTrue"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToQuantityRatioTrue"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToQuantityNull"]!!.value
            Assertions.assertNull(value)

            value = results["ConvertsToStringBoolean"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToStringInteger"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToStringLong"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToStringDecimal"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToStringQuantity"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToStringRatio"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToStringDate"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToStringDateTime"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToStringTime"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToStringNull"]!!.value
            Assertions.assertNull(value)

            value = results["ConvertsToTimeTrue"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["ConvertsToTimeFalse"]!!.value
            Assertions.assertFalse((value as Boolean?)!!)

            value = results["ConvertsToTimeNull"]!!.value
            Assertions.assertNull(value)

            value = results["IntegerIsInteger"]!!.value
            Assertions.assertTrue((value as Boolean?)!!)

            value = results["StringIsInteger"]!!.value
            Assertions.assertFalse((value as Boolean?)!!)

            value = results["StringNoToBoolean"]!!.value
            Assertions.assertFalse((value as Boolean?)!!)

            value = results["CodeToConcept1"]!!.value
            Assertions.assertTrue(
                equivalent(value, Concept().withCode(Code().withCode("8480-6"))) == true
            )

            value = results["ToDateTime0"]!!.value
            Assertions.assertTrue(
                equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 1)) == true
            )

            value = results["ToDateTime1"]!!.value
            Assertions.assertTrue(
                equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 1, 1)) == true
            )

            value = results["ToDateTime2"]!!.value
            Assertions.assertTrue(
                equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 1, 1, 12, 5)) == true
            )

            value = results["ToDateTime3"]!!.value
            Assertions.assertTrue(
                equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 1, 1, 12, 5, 5, 955)) == true
            )

            value = results["ToDateTime4"]!!.value
            Assertions.assertTrue(
                equivalent(value, DateTime(BigDecimal("1.5"), 2014, 1, 1, 12, 5, 5, 955)) == true,
                "ToDateTime4 vs. new DateTime(-1.5)",
            )

            value = results["ToDateTime5"]!!.value
            Assertions.assertTrue(
                equivalent(value, DateTime(BigDecimal("-1.25"), 2014, 1, 1, 12, 5, 5, 955)) == true,
                "ToDateTime5 vs. new DateTime(-1.25)",
            )

            value = results["ToDateTime6"]!!.value
            val bigDecimalOffsetForUtc = getBigDecimalZoneOffset(ZoneId.of("UTC"))
            Assertions.assertTrue(
                equivalent(value, DateTime(bigDecimalOffsetForUtc, 2014, 1, 1, 12, 5, 5, 955)) ==
                    true
            )

            value = results["ToDateTimeMalformed"]!!.value
            Assertions.assertNull(value)

            value = results["String25D5ToDecimal"]!!.value
            Assertions.assertEquals(value, BigDecimal("25.5"))

            value = results["StringNeg25ToInteger"]!!.value
            Assertions.assertEquals(-25, value)

            value = results["String123ToLong"]!!.value
            Assertions.assertEquals(123L, value)

            value = results["String5D5CMToQuantity"]!!.value
            Assertions.assertTrue(
                equal(value, Quantity().withValue(BigDecimal("5.5")).withUnit("cm")) == true
            )

            value = results["StringInvalidToQuantityNull"]!!.value
            Assertions.assertNull(value)

            value = results["String100PerMinPerSqMeterToQuantity"]!!.value
            Assertions.assertTrue(
                equal(value, Quantity().withValue(BigDecimal("100")).withUnit("daL/min/m2")) == true
            )

            value = results["String100UnitPer10BillionToQuantity"]!!.value
            Assertions.assertTrue(
                equal(value, Quantity().withValue(BigDecimal("100")).withUnit("U/10*10{cells}")) ==
                    true
            )

            value = results["String60DayPer7DayToQuantity"]!!.value
            Assertions.assertTrue(
                equal(value, Quantity().withValue(BigDecimal("60")).withUnit("d/(7.d)")) == true
            )

            value = results["String60EhrlichPer100gmToQuantity"]!!.value
            Assertions.assertTrue(
                equal(value, Quantity().withValue(BigDecimal("60")).withUnit("{EhrlichU}/100.g")) ==
                    true
            )

            value = results["StringPercentToQuantity"]!!.value
            Assertions.assertTrue(
                equal(value, Quantity().withValue(BigDecimal("60")).withUnit("%")) == true
            )

            value = results["StringPercentWithoutQuoteToQuantity"]!!.value
            Assertions.assertTrue(
                equal(value, Quantity().withValue(BigDecimal("70")).withUnit("%")) == true
            )

            value = results["StringPercentWithTabToQuantity"]!!.value
            Assertions.assertTrue(
                equal(value, Quantity().withValue(BigDecimal("80")).withUnit("%")) == true
            )

            value = results["StringPercentWithMultiSpacesToQuantity"]!!.value
            Assertions.assertTrue(
                equal(value, Quantity().withValue(BigDecimal("90")).withUnit("%")) == true
            )

            value = results["StringPercentWithSpacesUnitToQuantity"]!!.value
            Assertions.assertTrue(
                equal(value, Quantity().withValue(BigDecimal("10")).withUnit("ml")) == true
            )

            value = results["StringPercentWithQuoteUnitToQuantity"]!!.value
            Assertions.assertTrue(
                equal(value, Quantity().withValue(BigDecimal("20")).withUnit("ml")) == true
            )

            value = results["ToRatioIsValid"]!!.value
            Assertions.assertTrue(
                equal(
                    (value as Ratio).numerator,
                    Quantity().withValue(BigDecimal("1.0")).withUnit("mg"),
                ) == true
            )
            Assertions.assertTrue(
                equal(value.denominator, Quantity().withValue(BigDecimal("2.0")).withUnit("mg")) ==
                    true
            )

            value = results["ToRatioIsNull"]!!.value
            Assertions.assertNull(value)

            value = results["IntegerNeg5ToString"]!!.value
            Assertions.assertEquals("-5", value)

            value = results["LongNeg5ToString"]!!.value
            Assertions.assertEquals("-5", value)

            value = results["Decimal18D55ToString"]!!.value
            Assertions.assertEquals("18.55", value)

            value = results["Quantity5D5CMToString"]!!.value
            Assertions.assertEquals("5.5 'cm'", value)

            value = results["BooleanTrueToString"]!!.value
            Assertions.assertEquals("true", value)

            value = results["ToTime1"]!!.value
            Assertions.assertTrue(equivalent(value, Time(14, 30, 0, 0)) == true)

            value = results["ToTimeMalformed"]!!.value
            Assertions.assertNull(value)
        } finally {
            TimeZone.setDefault(TimeZone.getTimeZone(oldTz))
        }
    }
}
