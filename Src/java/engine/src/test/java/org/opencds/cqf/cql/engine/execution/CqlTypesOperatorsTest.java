package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.opencds.cqf.cql.engine.elm.visiting.EquivalentEvaluator;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlTypesOperatorsTest extends CqlTestBase {

    @Test
    public void test_all_types_operators() {

        Set<String> set = new HashSet<>();
        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlTypeOperatorsTest"));
        Object result;

        result = evaluationResult.expressionResults.get("AsQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

        result = evaluationResult.expressionResults.get("CastAsQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

        result = evaluationResult.expressionResults.get("AsDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1)));

        result = evaluationResult.expressionResults.get("IntegerToDecimal").value();
        assertThat(result, is(new BigDecimal(5)));

        result = evaluationResult.expressionResults.get("IntegerToString").value();
        assertThat(result, is("5"));


        result = evaluationResult.expressionResults.get("StringToDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1)));

        result = evaluationResult.expressionResults.get("StringToTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(14, 30, 0, 0)));

        result = evaluationResult.expressionResults.get("ConvertQuantity").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Quantity().withValue(new BigDecimal("0.005")).withUnit("g")));

        result = evaluationResult.expressionResults.get("ConvertSyntax").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Quantity().withValue(new BigDecimal("0.005")).withUnit("g")));

        result = evaluationResult.expressionResults.get("ConvertsToBooleanTrue").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToBooleanFalse").value();
        Assert.assertFalse((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToBooleanNull").value();
        Assert.assertNull(result);

        result = evaluationResult.expressionResults.get("ConvertsToDateTrue").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToDateFalse").value();
        Assert.assertFalse((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToDateNull").value();
        Assert.assertNull(result);

        result = evaluationResult.expressionResults.get("ConvertsToDateTimeStringTrue").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToDateTimeDateTrue").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToDateTimeFalse").value();
        Assert.assertFalse((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToDateTimeNull").value();
        Assert.assertNull(result);

        result = evaluationResult.expressionResults.get("ConvertsToDecimalTrue").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToDecimalFalse").value();
        Assert.assertFalse((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToDecimalNull").value();
        Assert.assertNull(result);

        result = evaluationResult.expressionResults.get("ConvertsToIntegerTrue").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToIntegerLong").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToIntegerFalse").value();
        Assert.assertFalse((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToIntegerNull").value();
        Assert.assertNull(result);

        result = evaluationResult.expressionResults.get("ConvertsToLongTrue").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToLongFalse").value();
        Assert.assertFalse((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToLongNull").value();
        Assert.assertNull(result);

        result = evaluationResult.expressionResults.get("ConvertsToQuantityStringTrue").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToQuantityStringFalse").value();
        Assert.assertFalse((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToQuantityIntegerTrue").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToQuantityDecimalTrue").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToQuantityRatioTrue").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToQuantityNull").value();
        Assert.assertNull(result);

        result = evaluationResult.expressionResults.get("ConvertsToStringBoolean").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToStringInteger").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToStringLong").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToStringDecimal").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToStringQuantity").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToStringRatio").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToStringDate").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToStringDateTime").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToStringTime").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToStringNull").value();
        Assert.assertNull(result);

        result = evaluationResult.expressionResults.get("ConvertsToTimeTrue").value();
        Assert.assertTrue((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToTimeFalse").value();
        Assert.assertFalse((Boolean) result);

        result = evaluationResult.expressionResults.get("ConvertsToTimeNull").value();
        Assert.assertNull(result);

        result = evaluationResult.expressionResults.get("IntegerIsInteger").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("StringIsInteger").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("StringNoToBoolean").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("CodeToConcept1").value();
        Assert.assertTrue(((Concept) result).equivalent(new Concept().withCode(new Code().withCode("8480-6"))));

        result = evaluationResult.expressionResults.get("ToDateTime0").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1)));

        result = evaluationResult.expressionResults.get("ToDateTime1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1)));
        // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("-7")));

        result = evaluationResult.expressionResults.get("ToDateTime2").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1, 12, 5)));
        // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("-7")));

        result = evaluationResult.expressionResults.get("ToDateTime3").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 1, 1, 12, 5, 5, 955)));
        // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("-7")));

        result = evaluationResult.expressionResults.get("ToDateTime4").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(new BigDecimal("1.5"), 2014, 1, 1, 12, 5, 5, 955)));
        // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("1.5")));

        result = evaluationResult.expressionResults.get("ToDateTime5").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(new BigDecimal("-1.25"), 2014, 1, 1, 12, 5, 5, 955)));
        // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("-1.25")));

        result = evaluationResult.expressionResults.get("ToDateTime6").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(new BigDecimal(0), 2014, 1, 1, 12, 5, 5, 955)));
        // assertThat(((DateTime)result).getTimezoneOffset(), is(new BigDecimal("-7")));

        result = evaluationResult.expressionResults.get("ToDateTimeMalformed").value();
        Assert.assertNull(result);

        result = evaluationResult.expressionResults.get("String25D5ToDecimal").value();
        assertThat(result, is(new BigDecimal("25.5")));

        result = evaluationResult.expressionResults.get("StringNeg25ToInteger").value();
        assertThat(result, is(-25));

        result = evaluationResult.expressionResults.get("String123ToLong").value();
        assertThat(result, is(123L));

        result = evaluationResult.expressionResults.get("String5D5CMToQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("5.5")).withUnit("cm")));

        result = evaluationResult.expressionResults.get("StringInvalidToQuantityNull").value();
        Assert.assertNull(result);

        result = evaluationResult.expressionResults.get("String100PerMinPerSqMeterToQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("100")).withUnit("daL/min/m2")));

        result = evaluationResult.expressionResults.get("String100UnitPer10BillionToQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("100")).withUnit("U/10*10{cells}")));

        result = evaluationResult.expressionResults.get("String60DayPer7DayToQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("60")).withUnit("d/(7.d)")));

        result = evaluationResult.expressionResults.get("String60EhrlichPer100gmToQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("60")).withUnit("{EhrlichU}/100.g")));

        result = evaluationResult.expressionResults.get("StringPercentToQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("60")).withUnit("%")));

        result = evaluationResult.expressionResults.get("StringPercentWithoutQuoteToQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("70")).withUnit("%")));

        result = evaluationResult.expressionResults.get("StringPercentWithTabToQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("80")).withUnit("%")));

        result = evaluationResult.expressionResults.get("StringPercentWithMultiSpacesToQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("90")).withUnit("%")));

        result = evaluationResult.expressionResults.get("StringPercentWithSpacesUnitToQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("10")).withUnit("ml")));

        result = evaluationResult.expressionResults.get("StringPercentWithQuoteUnitToQuantity").value();
        Assert.assertTrue(((Quantity) result).equal(new Quantity().withValue(new BigDecimal("20")).withUnit("ml")));

        result = evaluationResult.expressionResults.get("ToRatioIsValid").value();
        Assert.assertTrue(((Ratio) result).getNumerator().equal(new Quantity().withValue(new BigDecimal("1.0")).withUnit("mg")));
        Assert.assertTrue(((Ratio) result).getDenominator().equal(new Quantity().withValue(new BigDecimal("2.0")).withUnit("mg")));

        result = evaluationResult.expressionResults.get("ToRatioIsNull").value();
        Assert.assertNull(result);

        result = evaluationResult.expressionResults.get("IntegerNeg5ToString").value();
        assertThat(result, is("-5"));

        result = evaluationResult.expressionResults.get("LongNeg5ToString").value();
        assertThat(result, is("-5"));

        result = evaluationResult.expressionResults.get("Decimal18D55ToString").value();
        assertThat(result, is("18.55"));

        result = evaluationResult.expressionResults.get("Quantity5D5CMToString").value();
        assertThat(result, is("5.5 'cm'"));

        result = evaluationResult.expressionResults.get("BooleanTrueToString").value();
        assertThat(result, is("true"));

        result = evaluationResult.expressionResults.get("ToTime1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(14, 30, 0, 0)));

        result = evaluationResult.expressionResults.get("ToTimeMalformed").value();
        Assert.assertNull(result);



    }
}
