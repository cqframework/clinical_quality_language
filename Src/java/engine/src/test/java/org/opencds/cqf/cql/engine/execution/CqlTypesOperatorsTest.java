package org.opencds.cqf.cql.engine.execution;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class CqlTypesOperatorsTest extends CqlTestBase {
    @Test(dataProvider = "timezones")
    public void test_all_types_operators(String timezone) {
        final String oldTz = System.getProperty("user.timezone");
        // This is the ONLY thing that will work.  System.setProperty() and -Duser.timezone do NOT work
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));
        engine.getState().setEvaluationDateTime(ZonedDateTime.now());

        try {
            final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

            final SoftAssert softAssert = new SoftAssert();

            var results = engine.evaluate(toElmIdentifier("CqlTypeOperatorsTest"));
            var value = results.forExpression("AsQuantity").value();
            softAssert.assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

            value = results.forExpression("CastAsQuantity").value();
            softAssert.assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

            value = results.forExpression("AsDateTime").value();
            softAssert.assertTrue(
                    EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2014, 1, 1)));

            value = results.forExpression("IntegerToDecimal").value();
            softAssert.assertEquals(value, new BigDecimal(5));

            value = results.forExpression("IntegerToString").value();
            softAssert.assertEquals(value, "5");

            value = results.forExpression("StringToDateTime").value();
            softAssert.assertTrue(
                    EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2014, 1, 1)));

            value = results.forExpression("StringToTime").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(14, 30, 0, 0)));

            value = results.forExpression("ConvertQuantity").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(
                    value, new Quantity().withValue(new BigDecimal("0.005")).withUnit("g")));

            value = results.forExpression("ConvertSyntax").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(
                    value, new Quantity().withValue(new BigDecimal("0.005")).withUnit("g")));

            value = results.forExpression("ConvertsToBooleanTrue").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToBooleanFalse").value();
            softAssert.assertFalse((Boolean) value);

            value = results.forExpression("ConvertsToBooleanNull").value();
            softAssert.assertNull(value);

            value = results.forExpression("ConvertsToDateTrue").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToDateFalse").value();
            softAssert.assertFalse((Boolean) value);

            value = results.forExpression("ConvertsToDateNull").value();
            softAssert.assertNull(value);

            value = results.forExpression("ConvertsToDateTimeStringTrue").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToDateTimeDateTrue").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToDateTimeFalse").value();
            softAssert.assertFalse((Boolean) value);

            value = results.forExpression("ConvertsToDateTimeNull").value();
            softAssert.assertNull(value);

            value = results.forExpression("ConvertsToDecimalTrue").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToDecimalFalse").value();
            softAssert.assertFalse((Boolean) value);

            value = results.forExpression("ConvertsToDecimalNull").value();
            softAssert.assertNull(value);

            value = results.forExpression("ConvertsToIntegerTrue").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToIntegerLong").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToIntegerFalse").value();
            softAssert.assertFalse((Boolean) value);

            value = results.forExpression("ConvertsToIntegerNull").value();
            softAssert.assertNull(value);

            value = results.forExpression("ConvertsToLongTrue").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToLongFalse").value();
            softAssert.assertFalse((Boolean) value);

            value = results.forExpression("ConvertsToLongNull").value();
            softAssert.assertNull(value);

            value = results.forExpression("ConvertsToQuantityStringTrue").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToQuantityStringFalse").value();
            softAssert.assertFalse((Boolean) value);

            value = results.forExpression("ConvertsToQuantityIntegerTrue").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToQuantityDecimalTrue").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToQuantityRatioTrue").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToQuantityNull").value();
            softAssert.assertNull(value);

            value = results.forExpression("ConvertsToStringBoolean").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringInteger").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringLong").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringDecimal").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringQuantity").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringRatio").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringDate").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringDateTime").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringTime").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToStringNull").value();
            softAssert.assertNull(value);

            value = results.forExpression("ConvertsToTimeTrue").value();
            softAssert.assertTrue((Boolean) value);

            value = results.forExpression("ConvertsToTimeFalse").value();
            softAssert.assertFalse((Boolean) value);

            value = results.forExpression("ConvertsToTimeNull").value();
            softAssert.assertNull(value);

            value = results.forExpression("IntegerIsInteger").value();
            softAssert.assertEquals(value, true);

            value = results.forExpression("StringIsInteger").value();
            softAssert.assertEquals(value, false);

            value = results.forExpression("StringNoToBoolean").value();
            softAssert.assertEquals(value, false);

            value = results.forExpression("CodeToConcept1").value();
            softAssert.assertTrue(((Concept) value).equivalent(new Concept().withCode(new Code().withCode("8480-6"))));

            value = results.forExpression("ToDateTime0").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2014, 1)));

            value = results.forExpression("ToDateTime1").value();
            softAssert.assertTrue(
                    EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2014, 1, 1)));

            value = results.forExpression("ToDateTime2").value();
            softAssert.assertTrue(
                    EquivalentEvaluator.equivalent(value, new DateTime(bigDecimalZoneOffset, 2014, 1, 1, 12, 5)));

            value = results.forExpression("ToDateTime3").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(
                    value, new DateTime(bigDecimalZoneOffset, 2014, 1, 1, 12, 5, 5, 955)));

            value = results.forExpression("ToDateTime4").value();
            softAssert.assertTrue(
                    EquivalentEvaluator.equivalent(
                            value, new DateTime(new BigDecimal("1.5"), 2014, 1, 1, 12, 5, 5, 955)),
                    "ToDateTime4 vs. new DateTime(-1.5)");

            value = results.forExpression("ToDateTime5").value();
            softAssert.assertTrue(
                    EquivalentEvaluator.equivalent(
                            value, new DateTime(new BigDecimal("-1.25"), 2014, 1, 1, 12, 5, 5, 955)),
                    "ToDateTime5 vs. new DateTime(-1.25)");

            value = results.forExpression("ToDateTime6").value();
            final BigDecimal bigDecimalOffsetForUtc = getBigDecimalZoneOffset(ZoneId.of("UTC"));
            softAssert.assertTrue(EquivalentEvaluator.equivalent(
                    value, new DateTime(bigDecimalOffsetForUtc, 2014, 1, 1, 12, 5, 5, 955)));

            value = results.forExpression("ToDateTimeMalformed").value();
            softAssert.assertNull(value);

            value = results.forExpression("String25D5ToDecimal").value();
            softAssert.assertEquals(value, new BigDecimal("25.5"));

            value = results.forExpression("StringNeg25ToInteger").value();
            softAssert.assertEquals(value, -25);

            value = results.forExpression("String123ToLong").value();
            softAssert.assertEquals(value, 123L);

            value = results.forExpression("String5D5CMToQuantity").value();
            softAssert.assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("5.5")).withUnit("cm")));

            value = results.forExpression("StringInvalidToQuantityNull").value();
            softAssert.assertNull(value);

            value = results.forExpression("String100PerMinPerSqMeterToQuantity").value();
            softAssert.assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("100")).withUnit("daL/min/m2")));

            value = results.forExpression("String100UnitPer10BillionToQuantity").value();
            softAssert.assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("100")).withUnit("U/10*10{cells}")));

            value = results.forExpression("String60DayPer7DayToQuantity").value();
            softAssert.assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("60")).withUnit("d/(7.d)")));

            value = results.forExpression("String60EhrlichPer100gmToQuantity").value();
            softAssert.assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("60")).withUnit("{EhrlichU}/100.g")));

            value = results.forExpression("StringPercentToQuantity").value();
            softAssert.assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("60")).withUnit("%")));

            value = results.forExpression("StringPercentWithoutQuoteToQuantity").value();
            softAssert.assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("70")).withUnit("%")));

            value = results.forExpression("StringPercentWithTabToQuantity").value();
            softAssert.assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("80")).withUnit("%")));

            value = results.forExpression("StringPercentWithMultiSpacesToQuantity")
                    .value();
            softAssert.assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("90")).withUnit("%")));

            value = results.forExpression("StringPercentWithSpacesUnitToQuantity")
                    .value();
            softAssert.assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("10")).withUnit("ml")));

            value = results.forExpression("StringPercentWithQuoteUnitToQuantity")
                    .value();
            softAssert.assertTrue(((Quantity) value)
                    .equal(new Quantity().withValue(new BigDecimal("20")).withUnit("ml")));

            value = results.forExpression("ToRatioIsValid").value();
            softAssert.assertTrue(((Ratio) value)
                    .getNumerator()
                    .equal(new Quantity().withValue(new BigDecimal("1.0")).withUnit("mg")));
            softAssert.assertTrue(((Ratio) value)
                    .getDenominator()
                    .equal(new Quantity().withValue(new BigDecimal("2.0")).withUnit("mg")));

            value = results.forExpression("ToRatioIsNull").value();
            softAssert.assertNull(value);

            value = results.forExpression("IntegerNeg5ToString").value();
            softAssert.assertEquals(value, "-5");

            value = results.forExpression("LongNeg5ToString").value();
            softAssert.assertEquals(value, "-5");

            value = results.forExpression("Decimal18D55ToString").value();
            softAssert.assertEquals(value, "18.55");

            value = results.forExpression("Quantity5D5CMToString").value();
            softAssert.assertEquals(value, "5.5 'cm'");

            value = results.forExpression("BooleanTrueToString").value();
            softAssert.assertEquals(value, "true");

            value = results.forExpression("ToTime1").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(value, new Time(14, 30, 0, 0)));

            value = results.forExpression("ToTimeMalformed").value();
            softAssert.assertNull(value);

            softAssert.assertAll();
        } finally {
            TimeZone.setDefault(TimeZone.getTimeZone(oldTz));
        }
    }
}
