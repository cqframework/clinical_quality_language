package org.opencds.cqf.cql.engine.execution;


import org.opencds.cqf.cql.engine.elm.visiting.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.*;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.comparesEqualTo;

public class EngineTests extends CqlTestBase {
    @Test
    public void test_all_evaluator() throws Exception {

        Environment environment = new Environment(getLibraryManager());
        CqlEngineVisitor engineVisitor = new CqlEngineVisitor(environment, null, null, null, createOptionsMin());

        Set<String> set = new HashSet<>();


        EvaluationResult evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlAllInOne", "1"), null, null, null, null,
                ZonedDateTime.of(2016, 1, 1, 0, 0, 0, 0, TimeZone.getDefault().toZoneId()));

        Object result;

        assertThat(evaluationResult.expressionResults.get("AllTrueAllTrue").value(), is(true));
        assertThat(evaluationResult.expressionResults.get("AbsNull").value(), is(nullValue()));
        assertThat(evaluationResult.expressionResults.get("Add11").value(), is(2));
        assertThat(evaluationResult.expressionResults.get("TestAfterNull").value(), is(nullValue()));
        assertThat(evaluationResult.expressionResults.get("TrueAndTrue").value(), is(true));
        assertThat(evaluationResult.expressionResults.get("AnyTrueAllTrue").value(), is(true));
        assertThat(evaluationResult.expressionResults.get("AnyTrueAllFalse").value(), is(false));

        result = evaluationResult.expressionResults.get("AsQuantity").value();
        Assert.assertTrue(((Quantity)result).equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

        result = evaluationResult.expressionResults.get("CastAsQuantity").value();
        Assert.assertTrue(((Quantity)result).equal(new Quantity().withValue(new BigDecimal("45.5")).withUnit("g")));

        result = evaluationResult.expressionResults.get("AsDateTime").value();
        Assert.assertTrue(((DateTime)result).equal(new DateTime(null, 2014, 1, 1)));

        result = evaluationResult.expressionResults.get("AvgTest1").value();
        assertThat(result, is(new BigDecimal("3.0")));

        result = evaluationResult.expressionResults.get("Product_Long").value();
        assertThat(result, is(100L));

        result = evaluationResult.expressionResults.get("CountTest1").value();
        assertThat(result, is(4));

        result = evaluationResult.expressionResults.get("MaxTestInteger").value();
        assertThat(result, is(90));

        result = evaluationResult.expressionResults.get("MedianTestDecimal").value();
        assertThat(result, is(new BigDecimal("3.5")));

        result = evaluationResult.expressionResults.get("MinTestInteger").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("ModeTestInteger").value();
        assertThat(result, is(9));

        result = evaluationResult.expressionResults.get("ModeTestDateTime").value();
        Assert.assertTrue(((DateTime)result).equal(new DateTime(null, 2012, 9, 5)));

        result = evaluationResult.expressionResults.get("PopStdDevTest1").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("1.41421356")) == 0); //23730951454746218587388284504413604736328125

        result = evaluationResult.expressionResults.get("PopVarianceTest1").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("2.0")) == 0);

        result = evaluationResult.expressionResults.get("StdDevTest1").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("1.58113883")) == 0); //00841897613935316257993690669536590576171875

        result = evaluationResult.expressionResults.get("SumTest1").value();
        assertThat(result, is(new BigDecimal("20.0")));

        result = evaluationResult.expressionResults.get("VarianceTest1").value();
        Assert.assertTrue(((BigDecimal) result).compareTo(new BigDecimal("2.5")) == 0);


        result = evaluationResult.expressionResults.get("CeilingNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Ceiling1D").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("Ceiling1D1").value();
        assertThat(result, is(2));


        result = evaluationResult.expressionResults.get("DivideNull").value();
        assertThat(result, is(nullValue()));


        result = evaluationResult.expressionResults.get("Divide10").value();
        assertThat(result, is(nullValue()));


        result = evaluationResult.expressionResults.get("Divide01").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal("0.0")));


        result = evaluationResult.expressionResults.get("Divide11").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal("1.0")));

        result = evaluationResult.expressionResults.get("FloorNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Floor1").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("Floor1D").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("ExpNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Exp0").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal("1.0")));

        result = evaluationResult.expressionResults.get("ExpNeg0").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal("1.0")));

        result = evaluationResult.expressionResults.get("HighBoundaryDec").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal("1.58799999")));


        result = evaluationResult.expressionResults.get("HighBoundaryDate").value();
        Assert.assertTrue(((Date)result).equal(new Date(2014, 12)));

        result = evaluationResult.expressionResults.get("HighBoundaryDateTime").value();
        Assert.assertTrue(((DateTime)result).equal(new DateTime(null, 2014, 1, 1, 8, 59, 59, 999)));


        result = evaluationResult.expressionResults.get("LogNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Log1BaseNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Log1Base1").value();
        assertThat(((BigDecimal)result), comparesEqualTo(new BigDecimal(0d)));

        result = evaluationResult.expressionResults.get("LowBoundaryDec").value();
        assertThat(((BigDecimal) result), comparesEqualTo((new BigDecimal("1.58700000"))));

        result = evaluationResult.expressionResults.get("LowBoundaryDate").value();
        Assert.assertTrue(((Date)result).equal(new Date(2014, 1)));

        result = evaluationResult.expressionResults.get("LowBoundaryDateTime").value();
        Assert.assertTrue(((DateTime)result).equal(new DateTime(null, 2014, 1, 1, 8, 0, 0, 0)));

        result = evaluationResult.expressionResults.get("IntegerMinValue").value();
        assertThat(result, is(Integer.MIN_VALUE));

        result = evaluationResult.expressionResults.get("LongMinValue").value();
        assertThat(result, is(Long.MIN_VALUE));

        result = evaluationResult.expressionResults.get("IntegerMaxValue").value();
        assertThat(result, is(Integer.MAX_VALUE));

        result = evaluationResult.expressionResults.get("LongMaxValue").value();
        assertThat(result, is(Long.MAX_VALUE));


        result = evaluationResult.expressionResults.get("ModuloNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Modulo0By0").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Modulo4By2").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("MultiplyNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Multiply1By1").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("Multiply2By3Long").value();
        assertThat(result, is(6L));

        result = evaluationResult.expressionResults.get("NegateNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Negate0").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("NegateNeg0").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("Negate1").value();
        assertThat(result, is(-1));

        result = evaluationResult.expressionResults.get("PrecisionDecimal5").value();
        Assert.assertEquals(result, 5);

        result = evaluationResult.expressionResults.get("PrecisionDateYear").value();
        Assert.assertEquals(result, 4);

        result = evaluationResult.expressionResults.get("PrecisionDateTimeMs").value();
        Assert.assertEquals(result, 17);

        result = evaluationResult.expressionResults.get("PredecessorNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("PredecessorOf0").value();
        assertThat(result, is(-1));

        result = evaluationResult.expressionResults.get("PredecessorOf1").value();
        assertThat(result, is(0));


        result = evaluationResult.expressionResults.get("PowerNullToNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Power0To0").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("Power2To2").value();
        assertThat(result, is(4));

        result = evaluationResult.expressionResults.get("Power2To2Long").value();
        assertThat(result, is(4L));

        result = evaluationResult.expressionResults.get("RoundNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Round1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = evaluationResult.expressionResults.get("Round0D5").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(1.0)));

        result = evaluationResult.expressionResults.get("Round0D4").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(0.0)));


        result = evaluationResult.expressionResults.get("SubtractNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Subtract1And1").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("Subtract1And1Long").value();
        assertThat(result, is(0L));


        result = evaluationResult.expressionResults.get("SuccessorNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SuccessorOf0").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("SuccessorOf1").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("TruncateNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Truncate0").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("Truncate0D0").value();
        assertThat(result, is((0)));

        result = evaluationResult.expressionResults.get("TruncatedDivideNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("TruncatedDivide2By1").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("TruncatedDivide10By3").value();
        assertThat(result, is(3));

        result = evaluationResult.expressionResults.get("TruncatedDivide10d1By3D1").value();
        assertThat((BigDecimal)result, comparesEqualTo(new BigDecimal(3.0)));


        result = evaluationResult.expressionResults.get("CalculateAgeYears").value();
        assertThat(result, is(6));

        result = evaluationResult.expressionResults.get("CalculateAgeAtYears").value();
        assertThat(result, is(17));

        result = evaluationResult.expressionResults.get("BetweenIntTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleEqTrueTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleEqNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("QuantityEqDiffPrecision").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("RatioNotEqual").value();
        assertThat(result, is(false));


        result = evaluationResult.expressionResults.get("TupleEqJohn1John2").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeEqJanJan").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("GreaterZNeg1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("GreaterOrEqualCM0NegCM1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivFalseFalse").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("RatioEquivalent").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SimpleNotEqFloat1Float2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("x").value();
        System.out.println(result);

        result = evaluationResult.expressionResults.get("IfTrue1").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("IfFalse1").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("IfNull1").value();
        assertThat(result, is(10));

        result = evaluationResult.expressionResults.get("StandardCase1").value();
                assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("StandardCase2").value();
                assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("StandardCase3").value();
                assertThat(result, is(15));

        result = evaluationResult.expressionResults.get("SelectedCase1").value();
                assertThat(result, is(12));

        result = evaluationResult.expressionResults.get("SelectedCase2").value();
                assertThat(result, is(15));

        result = evaluationResult.expressionResults.get("SelectedCase3").value();
                assertThat(result, is(5));



        result = evaluationResult.expressionResults.get("Issue70A").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("Issue70B").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("CodeEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("CodeEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("CodeEqualNullVersion").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("ConceptEqualTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ConceptEqualFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ConceptEqualNullDisplay").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("CodeEqualNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("ConceptEqualNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("CodeEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("CodeEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ConceptEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ConceptEquivalentTrueDisplayMismatch").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ConceptEquivalentTrueIntersection1And4").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ConceptEquivalentTrueIntersection2And4").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ConceptEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("CodeEquivalentNull").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ConceptEquivalentNull").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("CodeToConceptEquivalentFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("CodeToConceptEquivalentTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ConceptToConceptMismatchedDisplayTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAdd5Years").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2010, 10, 10)));


        result = evaluationResult.expressionResults.get("DateTimeAdd5Months").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 10, 10)));

        result = evaluationResult.expressionResults.get("DateTimeAddMonthsOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2006, 3, 10)));

        result = evaluationResult.expressionResults.get("DateTimeAddThreeWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2018, 5, 23)));

        result = evaluationResult.expressionResults.get("DateTimeAddYearInWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2019, 5, 23)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 15)));

        result = evaluationResult.expressionResults.get("DateTimeAddDaysOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 7, 1)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 10)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5HoursWithLeftMinPrecisionSecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 10, 20, 30)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5HoursWithLeftMinPrecisionDay").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5HoursWithLeftMinPrecisionDayOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 11)));

        result = evaluationResult.expressionResults.get("DateAdd2YearsAsMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2016)));

        result = evaluationResult.expressionResults.get("DateAdd2YearsAsMonthsRem1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2016)));

        result = evaluationResult.expressionResults.get("DateAdd33Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2014, 7)));

        result = evaluationResult.expressionResults.get("DateAdd1Year").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2015, 6)));

        result = evaluationResult.expressionResults.get("DateTimeAddHoursOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 11, 0)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5Minutes").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 10)));

        result = evaluationResult.expressionResults.get("DateTimeAddMinutesOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 6, 0)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5Seconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 5, 10)));

        result = evaluationResult.expressionResults.get("DateTimeAddSecondsOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 5, 6, 0)));

        result = evaluationResult.expressionResults.get("DateTimeAdd5Milliseconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 5, 5, 10)));

        result = evaluationResult.expressionResults.get("DateTimeAddMillisecondsOverflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 5, 5, 6, 0)));

        result = evaluationResult.expressionResults.get("DateTimeAddLeapYear").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2013, 2, 28)));

        result = evaluationResult.expressionResults.get("DateTimeAdd2YearsByMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016)));

        result = evaluationResult.expressionResults.get("DateTimeAdd2YearsByDays").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016)));

        result = evaluationResult.expressionResults.get("DateTimeAdd2YearsByDaysRem5Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016)));

        result = evaluationResult.expressionResults.get("TimeAdd5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(20, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("TimeAdd1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(16, 0, 59, 999)));

        result = evaluationResult.expressionResults.get("TimeAdd1Second").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(16, 0, 0, 999)));

        result = evaluationResult.expressionResults.get("TimeAdd1Millisecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(16, 0, 0, 0)));

        result = evaluationResult.expressionResults.get("TimeAdd5Hours1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(21, 0, 59, 999)));

        // checking access ordering and returning correct result
        result = evaluationResult.expressionResults.get("TimeAdd1Second").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(16, 0, 0, 999)));

        result = evaluationResult.expressionResults.get("TimeAdd5hoursByMinute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(20, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("DateTimeAfterYearTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAfterMonthTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterMonthFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAfterDayTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterDayTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterDayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAfterHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAfterMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAfterSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAfterMillisecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeAfterMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAfterUncertain").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeAfterHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeAfterHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeAfterMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeAfterMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeAfterSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeAfterSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeAfterMillisecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeAfterMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeBeforeYearTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeBeforeMonthTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeMonthFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeBeforeDayTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeDayTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeDayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeBeforeHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeBeforeMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeBeforeSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeBeforeMillisecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeBeforeMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("BeforeTimezoneTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("BeforeTimezoneFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeBeforeHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeBeforeHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeBeforeMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeBeforeMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeBeforeSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeBeforeSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeBeforeMillisecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeBeforeMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeYear").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003)));

        result = evaluationResult.expressionResults.get("DateTimeMonth").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10)));

        result = evaluationResult.expressionResults.get("DateTimeDay").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10, 29)));

        result = evaluationResult.expressionResults.get("DateTimeHour").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10, 29, 20)));

        result = evaluationResult.expressionResults.get("DateTimeMinute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10, 29, 20, 50)));

        result = evaluationResult.expressionResults.get("DateTimeSecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10, 29, 20, 50, 33)));

        result = evaluationResult.expressionResults.get("DateTimeMillisecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2003, 10, 29, 20, 50, 33, 955)));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromYear").value();
        assertThat(result, is(2003));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromMonth").value();
        assertThat(result, is(10));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromMonthMinBoundary").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromDay").value();
        assertThat(result, is(29));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromHour").value();
        assertThat(result, is(20));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromMinute").value();
        assertThat(result, is(50));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromSecond").value();
        assertThat(result, is(33));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromMillisecond").value();
        assertThat(result, is(955));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromTimezone").value();
        assertThat(result, is(new BigDecimal("1.0")));

        result = evaluationResult.expressionResults.get("DateTimeComponentFromDate").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2003, 10, 29)));

        result = evaluationResult.expressionResults.get("TimeComponentFromHour").value();
        assertThat(result, is(23));

        result = evaluationResult.expressionResults.get("TimeComponentFromMinute").value();
        assertThat(result, is(20));

        result = evaluationResult.expressionResults.get("TimeComponentFromSecond").value();
        assertThat(result, is(15));

        result = evaluationResult.expressionResults.get("TimeComponentFromMilli").value();
        assertThat(result, is(555));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceYear").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceMonth").value();
        assertThat(result, is(8));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceDay").value();
        assertThat(result, is(10));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceHour").value();
        assertThat(result, is(8));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceMinute").value();
        assertThat(result, is(9));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceSecond").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceMillisecond").value();
        assertThat(result, is(3600400));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceWeeks").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceWeeks2").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceWeeks2").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceNegative").value();
        assertThat(result, is(-18));

        result = evaluationResult.expressionResults.get("DateTimeDifferenceUncertain").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeDifferenceHour").value();
        assertThat(result, is(3));

        result = evaluationResult.expressionResults.get("TimeDifferenceMinute").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("TimeDifferenceSecond").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("TimeDifferenceMillis").value();
        assertThat(result, is(-5));

        result = evaluationResult.expressionResults.get("DifferenceInHoursA").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DifferenceInMinutesA").value();
        assertThat(result, is(45));

        result = evaluationResult.expressionResults.get("DifferenceInDaysA").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DifferenceInHoursAA").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DifferenceInMinutesAA").value();
        assertThat(result, is(45));

        result = evaluationResult.expressionResults.get("DifferenceInDaysAA").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenYear").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("DurationInWeeks").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DurationInWeeks2").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DurationInWeeks3").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenYearOffset").value();
        assertThat(result, is(4));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenMonth").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenDaysDiffYears").value();
        assertThat(result, is(-788));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenUncertainInterval").value();
        Assert.assertTrue(((Interval) result).getStart().equals(17));
        Assert.assertTrue(((Interval) result).getEnd().equals(44));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenUncertainInterval2").value();
        Assert.assertTrue(((Interval) result).getStart().equals(5));
        Assert.assertTrue(((Interval) result).getEnd().equals(16));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenUncertainAdd").value();
        Assert.assertTrue(((Interval) result).getStart().equals(34));
        Assert.assertTrue(((Interval) result).getEnd().equals(88));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenUncertainSubtract").value();
        Assert.assertTrue(((Interval) result).getStart().equals(12));
        Assert.assertTrue(((Interval) result).getEnd().equals(28));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenUncertainMultiply").value();
        Assert.assertTrue(((Interval) result).getStart().equals(289));
        Assert.assertTrue(((Interval) result).getEnd().equals(1936));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenMonthUncertain").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenMonthUncertain2").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenMonthUncertain3").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenMonthUncertain4").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenMonthUncertain5").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenMonthUncertain6").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeDurationBetweenMonthUncertain7").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DurationInYears").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TimeDurationBetweenHour").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("TimeDurationBetweenHourDiffPrecision").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TimeDurationBetweenMinute").value();
        assertThat(result, is(4));

        result = evaluationResult.expressionResults.get("TimeDurationBetweenSecond").value();
        assertThat(result, is(4));

        result = evaluationResult.expressionResults.get("TimeDurationBetweenMillis").value();
        assertThat(result, is(5));

        result = evaluationResult.expressionResults.get("DurationInHoursA").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DurationInMinutesA").value();
        assertThat(result, is(45));

        result = evaluationResult.expressionResults.get("DurationInHoursAA").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DurationInMinutesAA").value();
        assertThat(result, is(45));

        result = evaluationResult.expressionResults.get("DurationInDaysAA").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("DateTimeNow").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameAsYearTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameAsYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameAsMonthTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameAsMonthFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameAsDayTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameAsDayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameAsHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameAsHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameAsMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameAsMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameAsSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameAsSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameAsMillisecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameAsMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameAsNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SameAsTimezoneTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SameAsTimezoneFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeSameAsHourTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameAsHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeSameAsMinuteTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameAsMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeSameAsSecondTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameAsSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeSameAsMillisTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameAsMillisFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMonthTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMonthTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMonthFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterDayTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterDayTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterDayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterHourTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterHourTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMinuteTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMinuteTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterSecondTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterSecondTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMillisecondTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMillisecondTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrAfterNull1").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SameOrAfterTimezoneTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SameOrAfterTimezoneFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterHourTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterHourTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterMinuteTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterMinuteTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterSecondTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterSecondTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterMillisTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterMillisTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("TimeSameOrAfterMillisFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("OnOrAfterTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("Issue32DateTime").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMonthTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMonthTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMonthFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeDayTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeDayTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeDayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeHourTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeHourTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeHourFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMinuteTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMinuteTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMinuteFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeSecondTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeSecondTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeSecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMillisecondTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMillisecondTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeMillisecondFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeNull1").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SameOrBeforeTimezoneTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("SameOrBeforeTimezoneFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeYearFalse").value();

        result = evaluationResult.expressionResults.get("DateTimeSubtract5Years").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2000, 10, 10)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract5Months").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 1, 10)));

        result = evaluationResult.expressionResults.get("DateTimeSubtractMonthsUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2004, 11, 10)));

        result = evaluationResult.expressionResults.get("DateTimeSubtractThreeWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2018, 5, 2)));

        result = evaluationResult.expressionResults.get("DateTimeSubtractYearInWeeks").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2017, 5, 23)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract5Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 5)));

        result = evaluationResult.expressionResults.get("DateTimeSubtractDaysUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 5, 30)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5)));

        result = evaluationResult.expressionResults.get("DateTimeSubtractHoursUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 9, 23)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract5Minutes").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 5)));

        result = evaluationResult.expressionResults.get("DateTimeSubtractMinutesUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 4, 59)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract5Seconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 5, 5)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract1YearInSeconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2015, 5)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract15HourPrecisionSecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 9, 30, 19, 20, 30)));


        result = evaluationResult.expressionResults.get("DateTimeSubtractSecondsUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 5, 4, 59)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract5Milliseconds").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2005, 5, 10, 5, 5, 5, 5)));

        result = evaluationResult.expressionResults.get("DateTimeSubtractMillisecondsUnderflow").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2016, 6, 10, 5, 5, 4, 999)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract2YearsAsMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012)));

        result = evaluationResult.expressionResults.get("DateTimeSubtract2YearsAsMonthsRem1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012)));

        result = evaluationResult.expressionResults.get("DateSubtract2YearsAsMonthsRem1").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2012)));

        result = evaluationResult.expressionResults.get("DateSubtract2YearsAsMonths").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2012)));

        result = evaluationResult.expressionResults.get("DateSubtract33Days").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2014, 5)));

        result = evaluationResult.expressionResults.get("DateSubtract1Year").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Date(2013, 6)));

        result = evaluationResult.expressionResults.get("TimeSubtract5Hours").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("TimeSubtract1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 58, 59, 999)));

        result = evaluationResult.expressionResults.get("TimeSubtract1Second").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 59, 58, 999)));

        result = evaluationResult.expressionResults.get("TimeSubtract1Millisecond").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 59, 58, 999)));

        result = evaluationResult.expressionResults.get("TimeSubtract5Hours1Minute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 58, 59, 999)));

        result = evaluationResult.expressionResults.get("TimeSubtract5hoursByMinute").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(10, 59, 59, 999)));


        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeTodayTrue1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeTodayTrue2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("DateTimeSameOrBeforeTodayFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DateTimeAddTodayTrue").value();
        assertThat(result, is(true));


        result = evaluationResult.expressionResults.get("TestMessageInfo").value();
        assertThat(result, is(1));
        //Assert.assertEquals(result.toString(), "100: Test Message");

        result = evaluationResult.expressionResults.get("TestMessageWarn").value();
        assertThat(result, is(2));
        //Assert.assertEquals(result.toString(), "200: You have been warned!");


        result = evaluationResult.expressionResults.get("TestMessageWithNullSeverity").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TestMessageWithNullSource").value();
        assertThat(result == null, is(true));

        result = evaluationResult.expressionResults.get("TestMessageWithNullCondition").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TestMessageWithNullCode").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TestMessageWithNullMessage").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TestWarningWithNullSource").value();
        assertThat(result == null, is(true));

        result = evaluationResult.expressionResults.get("TestWarningWithNullCondition").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TestWarningWithNullCode").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TestWarningWithNullMessage").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TestTraceWithNullSource").value();
        assertThat(result == null, is(true));

        result = evaluationResult.expressionResults.get("TestTraceWithNullCondition").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TestTraceWithNullCode").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("TestTraceWithNullMessage").value();
        assertThat(result, is(1));

//        result = evaluationResult.expressionResults.get("FunctionTestStringArg").value();
//        assertThat(result, is("hello"));
//
//        result = evaluationResult.expressionResults.get("FunctionTestNullStringArg").value();
//        assertThat(result, is(nullValue()));
//
//        result = evaluationResult.expressionResults.get("FunctionTestMultipleArgs").value();
//        assertThat(result, is("hell0"));
//
//        result = evaluationResult.expressionResults.get("FunctionTestNullMultipleArgs").value();
//        assertThat(result, is(nullValue()));
//
//        result = evaluationResult.expressionResults.get("FunctionTestOverload").value();
//        assertThat(result, is("hell00.000"));
//
//        result = evaluationResult.expressionResults.get("FunctionTestNullOverload").value();
//        assertThat(result, is(nullValue()));
//
//        result = evaluationResult.expressionResults.get("FunctionTestTupleArg").value();
//        assertThat(result, is(3));
//
//        result = evaluationResult.expressionResults.get("FunctionTestNullTupleArg").value();
//        assertThat(result, is(nullValue()));
//
//        result = evaluationResult.expressionResults.get("FunctionTestQuantityArg").value();
//        assertThat(result, is("cm"));
//
//        result = evaluationResult.expressionResults.get("FunctionTestNullQuantityArg").value();
//        assertThat(result, is(nullValue()));


//        result = evaluationResult.expressionResults.get("").value();
//        assertThat(obj, is(nullValue()));


    }


}

