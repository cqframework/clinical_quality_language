package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
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

            val results = engine.evaluate(toElmIdentifier("CqlTypeOperatorsTest"))
            var value = results.forExpression("AsQuantity").value()
            Assertions.assertTrue(
                (value as Quantity).equal(Quantity().withValue(BigDecimal("45.5")).withUnit("g"))
            )

            value = results.forExpression("CastAsQuantity").value()
            Assertions.assertTrue(
                (value as Quantity).equal(Quantity().withValue(BigDecimal("45.5")).withUnit("g"))
            )

            value = results.forExpression("AsDateTime").value()
            Assertions.assertTrue(
                EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 1, 1))
            )

            value = results.forExpression("IntegerToDecimal").value()
            Assertions.assertEquals(value, BigDecimal(5))

            value = results.forExpression("IntegerToString").value()
            Assertions.assertEquals("5", value)

            value = results.forExpression("StringToDateTime").value()
            Assertions.assertTrue(
                EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 1, 1))
            )

            value = results.forExpression("StringToTime").value()
            Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(14, 30, 0, 0)))

            value = results.forExpression("ConvertQuantity").value()
            Assertions.assertTrue(
                EquivalentEvaluator.equivalent(
                    value,
                    Quantity().withValue(BigDecimal("0.005")).withUnit("g"),
                )
            )

            value = results.forExpression("ConvertSyntax").value()
            Assertions.assertTrue(
                EquivalentEvaluator.equivalent(
                    value,
                    Quantity().withValue(BigDecimal("0.005")).withUnit("g"),
                )
            )

            value = results.forExpression("ConvertsToBooleanTrue").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToBooleanFalse").value()
            Assertions.assertFalse((value as Boolean?)!!)

            value = results.forExpression("ConvertsToBooleanNull").value()
            Assertions.assertNull(value)

            value = results.forExpression("ConvertsToDateTrue").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToDateFalse").value()
            Assertions.assertFalse((value as Boolean?)!!)

            value = results.forExpression("ConvertsToDateNull").value()
            Assertions.assertNull(value)

            value = results.forExpression("ConvertsToDateTimeStringTrue").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToDateTimeDateTrue").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToDateTimeFalse").value()
            Assertions.assertFalse((value as Boolean?)!!)

            value = results.forExpression("ConvertsToDateTimeNull").value()
            Assertions.assertNull(value)

            value = results.forExpression("ConvertsToDecimalTrue").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToDecimalFalse").value()
            Assertions.assertFalse((value as Boolean?)!!)

            value = results.forExpression("ConvertsToDecimalNull").value()
            Assertions.assertNull(value)

            value = results.forExpression("ConvertsToIntegerTrue").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToIntegerLong").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToIntegerFalse").value()
            Assertions.assertFalse((value as Boolean?)!!)

            value = results.forExpression("ConvertsToIntegerNull").value()
            Assertions.assertNull(value)

            value = results.forExpression("ConvertsToLongTrue").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToLongFalse").value()
            Assertions.assertFalse((value as Boolean?)!!)

            value = results.forExpression("ConvertsToLongNull").value()
            Assertions.assertNull(value)

            value = results.forExpression("ConvertsToQuantityStringTrue").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToQuantityStringFalse").value()
            Assertions.assertFalse((value as Boolean?)!!)

            value = results.forExpression("ConvertsToQuantityIntegerTrue").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToQuantityDecimalTrue").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToQuantityRatioTrue").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToQuantityNull").value()
            Assertions.assertNull(value)

            value = results.forExpression("ConvertsToStringBoolean").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToStringInteger").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToStringLong").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToStringDecimal").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToStringQuantity").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToStringRatio").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToStringDate").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToStringDateTime").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToStringTime").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToStringNull").value()
            Assertions.assertNull(value)

            value = results.forExpression("ConvertsToTimeTrue").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("ConvertsToTimeFalse").value()
            Assertions.assertFalse((value as Boolean?)!!)

            value = results.forExpression("ConvertsToTimeNull").value()
            Assertions.assertNull(value)

            value = results.forExpression("IntegerIsInteger").value()
            Assertions.assertTrue((value as Boolean?)!!)

            value = results.forExpression("StringIsInteger").value()
            Assertions.assertFalse((value as Boolean?)!!)

            value = results.forExpression("StringNoToBoolean").value()
            Assertions.assertFalse((value as Boolean?)!!)

            value = results.forExpression("CodeToConcept1").value()
            Assertions.assertTrue(
                (value as Concept).equivalent(Concept().withCode(Code().withCode("8480-6")))
            )

            value = results.forExpression("ToDateTime0").value()
            Assertions.assertTrue(
                EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 1))
            )

            value = results.forExpression("ToDateTime1").value()
            Assertions.assertTrue(
                EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 1, 1))
            )

            value = results.forExpression("ToDateTime2").value()
            Assertions.assertTrue(
                EquivalentEvaluator.equivalent(
                    value,
                    DateTime(bigDecimalZoneOffset, 2014, 1, 1, 12, 5),
                )
            )

            value = results.forExpression("ToDateTime3").value()
            Assertions.assertTrue(
                EquivalentEvaluator.equivalent(
                    value,
                    DateTime(bigDecimalZoneOffset, 2014, 1, 1, 12, 5, 5, 955),
                )
            )

            value = results.forExpression("ToDateTime4").value()
            Assertions.assertTrue(
                EquivalentEvaluator.equivalent(
                    value,
                    DateTime(BigDecimal("1.5"), 2014, 1, 1, 12, 5, 5, 955),
                ),
                "ToDateTime4 vs. new DateTime(-1.5)",
            )

            value = results.forExpression("ToDateTime5").value()
            Assertions.assertTrue(
                EquivalentEvaluator.equivalent(
                    value,
                    DateTime(BigDecimal("-1.25"), 2014, 1, 1, 12, 5, 5, 955),
                ),
                "ToDateTime5 vs. new DateTime(-1.25)",
            )

            value = results.forExpression("ToDateTime6").value()
            val bigDecimalOffsetForUtc = getBigDecimalZoneOffset(ZoneId.of("UTC"))
            Assertions.assertTrue(
                EquivalentEvaluator.equivalent(
                    value,
                    DateTime(bigDecimalOffsetForUtc, 2014, 1, 1, 12, 5, 5, 955),
                )
            )

            value = results.forExpression("ToDateTimeMalformed").value()
            Assertions.assertNull(value)

            value = results.forExpression("String25D5ToDecimal").value()
            Assertions.assertEquals(value, BigDecimal("25.5"))

            value = results.forExpression("StringNeg25ToInteger").value()
            Assertions.assertEquals(-25, value)

            value = results.forExpression("String123ToLong").value()
            Assertions.assertEquals(123L, value)

            value = results.forExpression("String5D5CMToQuantity").value()
            Assertions.assertTrue(
                (value as Quantity).equal(Quantity().withValue(BigDecimal("5.5")).withUnit("cm"))
            )

            value = results.forExpression("StringInvalidToQuantityNull").value()
            Assertions.assertNull(value)

            value = results.forExpression("String100PerMinPerSqMeterToQuantity").value()
            Assertions.assertTrue(
                (value as Quantity).equal(
                    Quantity().withValue(BigDecimal("100")).withUnit("daL/min/m2")
                )
            )

            value = results.forExpression("String100UnitPer10BillionToQuantity").value()
            Assertions.assertTrue(
                (value as Quantity).equal(
                    Quantity().withValue(BigDecimal("100")).withUnit("U/10*10{cells}")
                )
            )

            value = results.forExpression("String60DayPer7DayToQuantity").value()
            Assertions.assertTrue(
                (value as Quantity).equal(
                    Quantity().withValue(BigDecimal("60")).withUnit("d/(7.d)")
                )
            )

            value = results.forExpression("String60EhrlichPer100gmToQuantity").value()
            Assertions.assertTrue(
                (value as Quantity).equal(
                    Quantity().withValue(BigDecimal("60")).withUnit("{EhrlichU}/100.g")
                )
            )

            value = results.forExpression("StringPercentToQuantity").value()
            Assertions.assertTrue(
                (value as Quantity).equal(Quantity().withValue(BigDecimal("60")).withUnit("%"))
            )

            value = results.forExpression("StringPercentWithoutQuoteToQuantity").value()
            Assertions.assertTrue(
                (value as Quantity).equal(Quantity().withValue(BigDecimal("70")).withUnit("%"))
            )

            value = results.forExpression("StringPercentWithTabToQuantity").value()
            Assertions.assertTrue(
                (value as Quantity).equal(Quantity().withValue(BigDecimal("80")).withUnit("%"))
            )

            value = results.forExpression("StringPercentWithMultiSpacesToQuantity").value()
            Assertions.assertTrue(
                (value as Quantity).equal(Quantity().withValue(BigDecimal("90")).withUnit("%"))
            )

            value = results.forExpression("StringPercentWithSpacesUnitToQuantity").value()
            Assertions.assertTrue(
                (value as Quantity).equal(Quantity().withValue(BigDecimal("10")).withUnit("ml"))
            )

            value = results.forExpression("StringPercentWithQuoteUnitToQuantity").value()
            Assertions.assertTrue(
                (value as Quantity).equal(Quantity().withValue(BigDecimal("20")).withUnit("ml"))
            )

            value = results.forExpression("ToRatioIsValid").value()
            Assertions.assertTrue(
                (value as Ratio)
                    .numerator
                    .equal(Quantity().withValue(BigDecimal("1.0")).withUnit("mg"))
            )
            Assertions.assertTrue(
                value.denominator.equal(Quantity().withValue(BigDecimal("2.0")).withUnit("mg"))
            )

            value = results.forExpression("ToRatioIsNull").value()
            Assertions.assertNull(value)

            value = results.forExpression("IntegerNeg5ToString").value()
            Assertions.assertEquals("-5", value)

            value = results.forExpression("LongNeg5ToString").value()
            Assertions.assertEquals("-5", value)

            value = results.forExpression("Decimal18D55ToString").value()
            Assertions.assertEquals("18.55", value)

            value = results.forExpression("Quantity5D5CMToString").value()
            Assertions.assertEquals("5.5 'cm'", value)

            value = results.forExpression("BooleanTrueToString").value()
            Assertions.assertEquals("true", value)

            value = results.forExpression("ToTime1").value()
            Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(14, 30, 0, 0)))

            value = results.forExpression("ToTimeMalformed").value()
            Assertions.assertNull(value)
        } finally {
            TimeZone.setDefault(TimeZone.getTimeZone(oldTz))
        }
    }
}
