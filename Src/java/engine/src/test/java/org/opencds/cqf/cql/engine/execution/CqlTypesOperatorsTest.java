package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.*;

class CqlTypesOperatorsTest extends CqlTestBase {

    @ParameterizedTest
    @MethodSource("timezones")
    void all_types_operators(String timezone) {
        final String oldTz = System.getProperty("user.timezone");
        // This is the ONLY thing that will work.  System.setProperty() and -Duser.timezone do NOT work
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));
        engine.getState().setEvaluationDateTime(ZonedDateTime.now());

        try {
            final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

            var results = engine.evaluate(toElmIdentifier("CqlTypeOperatorsTest"));
            var value = results.forExpression("AsQuantity").value();
            assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

            value = results.forExpression("CastAsQuantity").value();
            assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

            value = results.forExpression("AsDateTime").value();
            assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2014, 1, 1)));

            value = results.forExpression("IntegerToDecimal").value();
            assertEquals(value, new BigDecimal(5));

            value = results.forExpression("IntegerToString").value();
            assertEquals("5", value);

            value = results.forExpression("StringToDateTime").value();
            assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2014, 1, 1)));

            value = results.forExpression("StringToTime").value();
            assertTrue(EquivalentEvaluator.equivalent(value, new Time(14, 30, 0, 0)));

            value = results.forExpression("ConvertQuantity").value();
            assertTrue(EquivalentEvaluator.equivalent(
                    value, new Quantity().withValue(new BigDecimal("0.005")).withUnit("g")));

            value = results.forExpression("ConvertSyntax").value();
            assertTrue(EquivalentEvaluator.equivalent(
                    value, new Quantity().withValue(new BigDecimal("0.005")).withUnit("g")));

            value = results.forExpression("ConvertsToBooleanTrue").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToBooleanFalse").value();
            assertFalse((Boolean) value);

            value = results.forExpression("ConvertsToBooleanNull").value();
            assertNull(value);

            value = results.forExpression("ConvertsToDateTrue").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToDateFalse").value();
            assertFalse((Boolean) value);

            value = results.forExpression("ConvertsToDateNull").value();
            assertNull(value);

            value = results.forExpression("ConvertsToDateTimeStringTrue").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToDateTimeDateTrue").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToDateTimeFalse").value();
            assertFalse((Boolean) value);

            value = results.forExpression("ConvertsToDateTimeNull").value();
            assertNull(value);

            value = results.forExpression("ConvertsToDecimalTrue").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToDecimalFalse").value();
            assertFalse((Boolean) value);

            value = results.forExpression("ConvertsToDecimalNull").value();
            assertNull(value);

            value = results.forExpression("ConvertsToIntegerTrue").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToIntegerLong").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToIntegerFalse").value();
            assertFalse((Boolean) value);

            value = results.forExpression("ConvertsToIntegerNull").value();
            assertNull(value);

            value = results.forExpression("ConvertsToLongTrue").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToLongFalse").value();
            assertFalse((Boolean) value);

            value = results.forExpression("ConvertsToLongNull").value();
            assertNull(value);

            value = results.forExpression("ConvertsToQuantityStringTrue").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToQuantityStringFalse").value();
            assertFalse((Boolean) value);

            value = results.forExpression("ConvertsToQuantityIntegerTrue").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToQuantityDecimalTrue").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToQuantityRatioTrue").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToQuantityNull").value();
            assertNull(value);

            value = results.forExpression("ConvertsToStringBoolean").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringInteger").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringLong").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringDecimal").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringQuantity").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringRatio").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringDate").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringDateTime").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringTime").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringNull").value();
            assertNull(value);

            value = results.forExpression("ConvertsToTimeTrue").value();
            assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToTimeFalse").value();
            assertFalse((Boolean) value);

            value = results.forExpression("ConvertsToTimeNull").value();
            assertNull(value);

            value = results.forExpression("IntegerIsInteger").value();
            assertTrue((Boolean) value);

            value = results.forExpression("StringIsInteger").value();
            assertFalse((Boolean) value);

            value = results.forExpression("StringNoToBoolean").value();
            assertFalse((Boolean) value);

            value = results.forExpression("CodeToConcept1").value();
            assertTrue(((Concept) value).equivalent(new Concept().withCode(new Code().withCode("8480-6"))));

            value = results.forExpression("ToDateTime0").value();
            assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2014, 1)));

            value = results.forExpression("ToDateTime1").value();
            assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2014, 1, 1)));

            value = results.forExpression("ToDateTime2").value();
            assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2014, 1, 1, 12, 5)));

            value = results.forExpression("ToDateTime3").value();
            assertTrue(EquivalentEvaluator.equivalent(
                    value, new DateTime(bigDecimalZoneOffset, 2014, 1, 1, 12, 5, 5, 955)));

            value = results.forExpression("ToDateTime4").value();
            assertTrue(
                    EquivalentEvaluator.equivalent(
                            value, new DateTime(new BigDecimal("1.5"), 2014, 1, 1, 12, 5, 5, 955)),
                    "ToDateTime4 vs. new DateTime(-1.5)");

            value = results.forExpression("ToDateTime5").value();
            assertTrue(
                    EquivalentEvaluator.equivalent(
                            value, new DateTime(new BigDecimal("-1.25"), 2014, 1, 1, 12, 5, 5, 955)),
                    "ToDateTime5 vs. new DateTime(-1.25)");

            value = results.forExpression("ToDateTime6").value();
            final BigDecimal bigDecimalOffsetForUtc = getBigDecimalZoneOffset(ZoneId.of("UTC"));
            assertTrue(EquivalentEvaluator.equivalent(
                    value, new DateTime(bigDecimalOffsetForUtc, 2014, 1, 1, 12, 5, 5, 955)));

            value = results.forExpression("ToDateTimeMalformed").value();
            assertNull(value);

            value = results.forExpression("String25D5ToDecimal").value();
            assertEquals(value, new BigDecimal("25.5"));

            value = results.forExpression("StringNeg25ToInteger").value();
            assertEquals(-25, value);

            value = results.forExpression("String123ToLong").value();
            assertEquals(123L, value);

            value = results.forExpression("String5D5CMToQuantity").value();
            assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("5.5")).withUnit("cm")));

            value = results.forExpression("StringInvalidToQuantityNull").value();
            assertNull(value);

            value = results.forExpression("String100PerMinPerSqMeterToQuantity").value();
            assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("100")).withUnit("daL/min/m2")));

            value = results.forExpression("String100UnitPer10BillionToQuantity").value();
            assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("100")).withUnit("U/10*10{cells}")));

            value = results.forExpression("String60DayPer7DayToQuantity").value();
            assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("60")).withUnit("d/(7.d)")));

            value = results.forExpression("String60EhrlichPer100gmToQuantity").value();
            assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("60")).withUnit("{EhrlichU}/100.g")));

            value = results.forExpression("StringPercentToQuantity").value();
            assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("60")).withUnit("%")));

            value = results.forExpression("StringPercentWithoutQuoteToQuantity").value();
            assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("70")).withUnit("%")));

            value = results.forExpression("StringPercentWithTabToQuantity").value();
            assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("80")).withUnit("%")));

            value = results.forExpression("StringPercentWithMultiSpacesToQuantity")
                    .value();
            assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("90")).withUnit("%")));

            value = results.forExpression("StringPercentWithSpacesUnitToQuantity")
                    .value();
            assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("10")).withUnit("ml")));

            value = results.forExpression("StringPercentWithQuoteUnitToQuantity")
                    .value();
            assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("20")).withUnit("ml")));

            value = results.forExpression("ToRatioIsValid").value();
            assertTrue(((Ratio) value)
                    .getNumerator()
                    .equal(new Quantity().withValue(new BigDecimal("1.0")).withUnit("mg")));
            assertTrue(((Ratio) value)
                    .getDenominator()
                    .equal(new Quantity().withValue(new BigDecimal("2.0")).withUnit("mg")));

            value = results.forExpression("ToRatioIsNull").value();
            assertNull(value);

            value = results.forExpression("IntegerNeg5ToString").value();
            assertEquals("-5", value);

            value = results.forExpression("LongNeg5ToString").value();
            assertEquals("-5", value);

            value = results.forExpression("Decimal18D55ToString").value();
            assertEquals("18.55", value);

            value = results.forExpression("Quantity5D5CMToString").value();
            assertEquals("5.5 'cm'", value);

            value = results.forExpression("BooleanTrueToString").value();
            assertEquals("true", value);

            value = results.forExpression("ToTime1").value();
            assertTrue(EquivalentEvaluator.equivalent(value, new Time(14, 30, 0, 0)));

            value = results.forExpression("ToTimeMalformed").value();
            assertNull(value);
        } finally {
            TimeZone.setDefault(TimeZone.getTimeZone(oldTz));
        }
    }
}
