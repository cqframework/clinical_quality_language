package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlTypesOperatorsTest extends CqlTestBase {
    @Test(dataProvider = "timezones")
    public void test_all_types_operators(String timezone) {
        final String oldTz = System.getProperty("user.timezone");
        // This is the ONLY thing that will work.  System.setProperty() and -Duser.timezone do NOT work
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));

        try {
            EvaluationResult evaluationResult;

            evaluationResult = engine.evaluate(toElmIdentifier("CqlTypeOperatorsTest"));
            Object result;

            result = evaluationResult.forExpression("AsQuantity").value();
            Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

            result = evaluationResult.forExpression("CastAsQuantity").value();
            Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

            result = evaluationResult.forExpression("AsDateTime").value();
            Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1)));

            result = evaluationResult.forExpression("IntegerToDecimal").value();
            assertThat(result, is(new BigDecimal(5)));

            result = evaluationResult.forExpression("IntegerToString").value();
            assertThat(result, is("5"));


            result = evaluationResult.forExpression("StringToDateTime").value();
            Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1)));

            result = evaluationResult.forExpression("StringToTime").value();
            Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(14, 30, 0, 0)));

            result = evaluationResult.forExpression("ConvertQuantity").value();
            Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Quantity().withValue(new BigDecimal("0.005")).withUnit("g")));

            result = evaluationResult.forExpression("ConvertSyntax").value();
            Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Quantity().withValue(new BigDecimal("0.005")).withUnit("g")));

            result = evaluationResult.forExpression("ConvertsToBooleanTrue").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToBooleanFalse").value();
            Assert.assertFalse((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToBooleanNull").value();
            Assert.assertNull(result);

            result = evaluationResult.forExpression("ConvertsToDateTrue").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToDateFalse").value();
            Assert.assertFalse((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToDateNull").value();
            Assert.assertNull(result);

            result = evaluationResult.forExpression("ConvertsToDateTimeStringTrue").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToDateTimeDateTrue").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToDateTimeFalse").value();
            Assert.assertFalse((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToDateTimeNull").value();
            Assert.assertNull(result);

            result = evaluationResult.forExpression("ConvertsToDecimalTrue").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToDecimalFalse").value();
            Assert.assertFalse((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToDecimalNull").value();
            Assert.assertNull(result);

            result = evaluationResult.forExpression("ConvertsToIntegerTrue").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToIntegerLong").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToIntegerFalse").value();
            Assert.assertFalse((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToIntegerNull").value();
            Assert.assertNull(result);

            result = evaluationResult.forExpression("ConvertsToLongTrue").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToLongFalse").value();
            Assert.assertFalse((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToLongNull").value();
            Assert.assertNull(result);

            result = evaluationResult.forExpression("ConvertsToQuantityStringTrue").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToQuantityStringFalse").value();
            Assert.assertFalse((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToQuantityIntegerTrue").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToQuantityDecimalTrue").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToQuantityRatioTrue").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToQuantityNull").value();
            Assert.assertNull(result);

            result = evaluationResult.forExpression("ConvertsToStringBoolean").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringInteger").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringLong").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringDecimal").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringQuantity").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringRatio").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringDate").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringDateTime").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringTime").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToStringNull").value();
            Assert.assertNull(result);

            result = evaluationResult.forExpression("ConvertsToTimeTrue").value();
            Assert.assertTrue((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToTimeFalse").value();
            Assert.assertFalse((Boolean) result);

            result = evaluationResult.forExpression("ConvertsToTimeNull").value();
            Assert.assertNull(result);

            result = evaluationResult.forExpression("IntegerIsInteger").value();
            assertThat(result, is(true));

            result = evaluationResult.forExpression("StringIsInteger").value();
            assertThat(result, is(false));

            result = evaluationResult.forExpression("StringNoToBoolean").value();
            assertThat(result, is(false));

            result = evaluationResult.forExpression("CodeToConcept1").value();
            Assert.assertTrue(((Concept) result).equivalent(new Concept().withCode(new Code().withCode("8480-6"))));

            result = evaluationResult.forExpression("ToDateTime0").value();
            Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1)));

            result = evaluationResult.forExpression("ToDateTime1").value();
            Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1)));
            // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("-7")));

            result = evaluationResult.forExpression("ToDateTime2").value();
            Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1, 12, 5)));
            // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("-7")));

            result = evaluationResult.forExpression("ToDateTime3").value();
            Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1, 12, 5, 5, 955)));
            // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("-7")));

            result = evaluationResult.forExpression("ToDateTime4").value();
            Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(new BigDecimal("1.5"), 2014, 1, 1, 12, 5, 5, 955)));
            // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("1.5")));

            result = evaluationResult.forExpression("ToDateTime5").value();
            Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(new BigDecimal("-1.25"), 2014, 1, 1, 12, 5, 5, 955)));
            // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("-1.25")));

            result = evaluationResult.forExpression("ToDateTime6").value();
            Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(new BigDecimal(0), 2014, 1, 1, 12, 5, 5, 955)));
            // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("-7")));

            result = evaluationResult.forExpression("ToDateTimeMalformed").value();
            Assert.assertNull(result);

            result = evaluationResult.forExpression("String25D5ToDecimal").value();
            assertThat(result, is(new BigDecimal("25.5")));

            result = evaluationResult.forExpression("StringNeg25ToInteger").value();
            assertThat(result, is(-25));

            result = evaluationResult.forExpression("String123ToLong").value();
            assertThat(result, is(123L));

            result = evaluationResult.forExpression("String5D5CMToQuantity").value();
            Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("5.5")).withUnit("cm")));

            result = evaluationResult.forExpression("StringInvalidToQuantityNull").value();
            Assert.assertNull(result);

            result = evaluationResult.forExpression("String100PerMinPerSqMeterToQuantity").value();
            Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("100")).withUnit("daL/min/m2")));

            result = evaluationResult.forExpression("String100UnitPer10BillionToQuantity").value();
            Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("100")).withUnit("U/10*10{cells}")));

            result = evaluationResult.forExpression("String60DayPer7DayToQuantity").value();
            Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("60")).withUnit("d/(7.d)")));

            result = evaluationResult.forExpression("String60EhrlichPer100gmToQuantity").value();
            Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("60")).withUnit("{EhrlichU}/100.g")));

            result = evaluationResult.forExpression("StringPercentToQuantity").value();
            Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("60")).withUnit("%")));

            result = evaluationResult.forExpression("StringPercentWithoutQuoteToQuantity").value();
            Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("70")).withUnit("%")));

            result = evaluationResult.forExpression("StringPercentWithTabToQuantity").value();
            Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("80")).withUnit("%")));

            result = evaluationResult.forExpression("StringPercentWithMultiSpacesToQuantity").value();
            Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("90")).withUnit("%")));

            result = evaluationResult.forExpression("StringPercentWithSpacesUnitToQuantity").value();
            Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("10")).withUnit("ml")));

            result = evaluationResult.forExpression("StringPercentWithQuoteUnitToQuantity").value();
            Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("20")).withUnit("ml")));

            result = evaluationResult.forExpression("ToRatioIsValid").value();
            Assert.assertTrue(((Ratio) result).getNumerator().equal(new Quantity().withValue(new BigDecimal("1.0")).withUnit("mg")));
            Assert.assertTrue(((Ratio) result).getDenominator().equal(new Quantity().withValue(new BigDecimal("2.0")).withUnit("mg")));

            result = evaluationResult.forExpression("ToRatioIsNull").value();
            Assert.assertNull(result);

            result = evaluationResult.forExpression("IntegerNeg5ToString").value();
            assertThat(result, is("-5"));

            result = evaluationResult.forExpression("LongNeg5ToString").value();
            assertThat(result, is("-5"));

            result = evaluationResult.forExpression("Decimal18D55ToString").value();
            assertThat(result, is("18.55"));

            result = evaluationResult.forExpression("Quantity5D5CMToString").value();
            assertThat(result, is("5.5 'cm'"));

            result = evaluationResult.forExpression("BooleanTrueToString").value();
            assertThat(result, is("true"));

            result = evaluationResult.forExpression("ToTime1").value();
            Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(14, 30, 0, 0)));

            result = evaluationResult.forExpression("ToTimeMalformed").value();
            Assert.assertNull(result);
        } finally {
            TimeZone.setDefault(TimeZone.getTimeZone(oldTz));
        }
    }
}
