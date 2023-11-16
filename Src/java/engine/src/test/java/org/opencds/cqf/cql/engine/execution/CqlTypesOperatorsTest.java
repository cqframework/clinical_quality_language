package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.annotations.Test;

import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.testng.asserts.SoftAssert;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

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

            EvaluationResult evaluationResult;

            evaluationResult = engine.evaluate(toElmIdentifier("CqlTypeOperatorsTest"));
            Object result;

            result = evaluationResult.forExpression("AsQuantity").value();
            softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

            result = evaluationResult.forExpression("CastAsQuantity").value();
            softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

            result = evaluationResult.forExpression("AsDateTime").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2014, 1, 1)));

            result = evaluationResult.forExpression("IntegerToDecimal").value();
            softAssert.assertEquals(result, new BigDecimal(5));

            result = evaluationResult.forExpression("IntegerToString").value();
            softAssert.assertEquals(result, "5");

            result = evaluationResult.forExpression("StringToDateTime").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2014, 1, 1)));

            result = evaluationResult.forExpression("StringToTime").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(14, 30, 0, 0)));

            result = evaluationResult.forExpression("ConvertQuantity").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new Quantity().withValue(new BigDecimal("0.005")).withUnit("g")));

            result = evaluationResult.forExpression("ConvertSyntax").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new Quantity().withValue(new BigDecimal("0.005")).withUnit("g")));

            result = evaluationResult.forExpression("ConvertsToBooleanTrue").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToBooleanFalse").value();
            softAssert.assertFalse((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToBooleanNull").value();
            softAssert.assertNull(result);

            result = evaluationResult.forExpression("ConvertsToDateTrue").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToDateFalse").value();
            softAssert.assertFalse((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToDateNull").value();
            softAssert.assertNull(result);

            result = evaluationResult.forExpression("ConvertsToDateTimeStringTrue").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToDateTimeDateTrue").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToDateTimeFalse").value();
            softAssert.assertFalse((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToDateTimeNull").value();
            softAssert.assertNull(result);

            result = evaluationResult.forExpression("ConvertsToDecimalTrue").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToDecimalFalse").value();
            softAssert.assertFalse((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToDecimalNull").value();
            softAssert.assertNull(result);

            result = evaluationResult.forExpression("ConvertsToIntegerTrue").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToIntegerLong").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToIntegerFalse").value();
            softAssert.assertFalse((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToIntegerNull").value();
            softAssert.assertNull(result);

            result = evaluationResult.forExpression("ConvertsToLongTrue").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToLongFalse").value();
            softAssert.assertFalse((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToLongNull").value();
            softAssert.assertNull(result);

            result = evaluationResult.forExpression("ConvertsToQuantityStringTrue").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToQuantityStringFalse").value();
            softAssert.assertFalse((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToQuantityIntegerTrue").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToQuantityDecimalTrue").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToQuantityRatioTrue").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToQuantityNull").value();
            softAssert.assertNull(result);

            result = evaluationResult.forExpression("ConvertsToStringBoolean").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringInteger").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringLong").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringDecimal").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringQuantity").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringRatio").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringDate").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringDateTime").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringTime").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringNull").value();
            softAssert.assertNull(result);

            result = evaluationResult.forExpression("ConvertsToTimeTrue").value();
            softAssert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToTimeFalse").value();
            softAssert.assertFalse((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToTimeNull").value();
            softAssert.assertNull(result);

            result = evaluationResult.forExpression("IntegerIsInteger").value();
            softAssert.assertEquals(result, true);

            result = evaluationResult.forExpression("StringIsInteger").value();
            softAssert.assertEquals(result, false);

            result = evaluationResult.forExpression("StringNoToBoolean").value();
            softAssert.assertEquals(result, false);

            result = evaluationResult.forExpression("CodeToConcept1").value();
            softAssert.assertTrue(((Concept) result).equivalent(new Concept().withCode(new Code().withCode("8480-6"))));

            result = evaluationResult.forExpression("ToDateTime0").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2014, 1)));

            result = evaluationResult.forExpression("ToDateTime1").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2014, 1, 1)));

            result = evaluationResult.forExpression("ToDateTime2").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2014, 1, 1, 12, 5)));

            result = evaluationResult.forExpression("ToDateTime3").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2014, 1, 1, 12, 5, 5, 955)));

            result = evaluationResult.forExpression("ToDateTime4").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(new BigDecimal("1.5"), 2014, 1, 1, 12, 5, 5, 955)), "ToDateTime4 vs. new DateTime(-1.5)");

            result = evaluationResult.forExpression("ToDateTime5").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(new BigDecimal("-1.25"), 2014, 1, 1, 12, 5, 5, 955)), "ToDateTime5 vs. new DateTime(-1.25)");

            result = evaluationResult.forExpression("ToDateTime6").value();
            final BigDecimal bigDecimalOffsetForUtc = getBigDecimalZoneOffset(ZoneId.of("UTC"));
            softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalOffsetForUtc, 2014, 1, 1, 12, 5, 5, 955)));

            result = evaluationResult.forExpression("ToDateTimeMalformed").value();
            softAssert.assertNull(result);

            result = evaluationResult.forExpression("String25D5ToDecimal").value();
            softAssert.assertEquals(result, new BigDecimal("25.5"));

            result = evaluationResult.forExpression("StringNeg25ToInteger").value();
            softAssert.assertEquals(result, -25);

            result = evaluationResult.forExpression("String123ToLong").value();
            softAssert.assertEquals(result, 123L);

            result = evaluationResult.forExpression("String5D5CMToQuantity").value();
            softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("5.5")).withUnit("cm")));

            result = evaluationResult.forExpression("StringInvalidToQuantityNull").value();
            softAssert.assertNull(result);

            result = evaluationResult.forExpression("String100PerMinPerSqMeterToQuantity").value();
            softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("100")).withUnit("daL/min/m2")));

            result = evaluationResult.forExpression("String100UnitPer10BillionToQuantity").value();
            softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("100")).withUnit("U/10*10{cells}")));

            result = evaluationResult.forExpression("String60DayPer7DayToQuantity").value();
            softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("60")).withUnit("d/(7.d)")));

            result = evaluationResult.forExpression("String60EhrlichPer100gmToQuantity").value();
            softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("60")).withUnit("{EhrlichU}/100.g")));

            result = evaluationResult.forExpression("StringPercentToQuantity").value();
            softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("60")).withUnit("%")));

            result = evaluationResult.forExpression("StringPercentWithoutQuoteToQuantity").value();
            softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("70")).withUnit("%")));

            result = evaluationResult.forExpression("StringPercentWithTabToQuantity").value();
            softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("80")).withUnit("%")));

            result = evaluationResult.forExpression("StringPercentWithMultiSpacesToQuantity").value();
            softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("90")).withUnit("%")));

            result = evaluationResult.forExpression("StringPercentWithSpacesUnitToQuantity").value();
            softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("10")).withUnit("ml")));

            result = evaluationResult.forExpression("StringPercentWithQuoteUnitToQuantity").value();
            softAssert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("20")).withUnit("ml")));

            result = evaluationResult.forExpression("ToRatioIsValid").value();
            softAssert.assertTrue(((Ratio) result).getNumerator().equal(new Quantity().withValue(new BigDecimal("1.0")).withUnit("mg")));
            softAssert.assertTrue(((Ratio) result).getDenominator().equal(new Quantity().withValue(new BigDecimal("2.0")).withUnit("mg")));

            result = evaluationResult.forExpression("ToRatioIsNull").value();
            softAssert.assertNull(result);

            result = evaluationResult.forExpression("IntegerNeg5ToString").value();
            softAssert.assertEquals(result, "-5");

            result = evaluationResult.forExpression("LongNeg5ToString").value();
            softAssert.assertEquals(result, "-5");

            result = evaluationResult.forExpression("Decimal18D55ToString").value();
            softAssert.assertEquals(result, "18.55");

            result = evaluationResult.forExpression("Quantity5D5CMToString").value();
            softAssert.assertEquals(result, "5.5 'cm'");

            result = evaluationResult.forExpression("BooleanTrueToString").value();
            softAssert.assertEquals(result, "true");

            result = evaluationResult.forExpression("ToTime1").value();
            softAssert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(14, 30, 0, 0)));

            result = evaluationResult.forExpression("ToTimeMalformed").value();
            softAssert.assertNull(result);

            softAssert.assertAll();
        } finally {
            TimeZone.setDefault(TimeZone.getTimeZone(oldTz));
        }
    }
}
