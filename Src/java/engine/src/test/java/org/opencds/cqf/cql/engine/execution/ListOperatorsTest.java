package org.opencds.cqf.cql.engine.execution;

import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertFalse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListOperatorsTest extends CqlTestBase {

@Test
    public void test_cql_list_test_suite_compiles() {
        var errors = new ArrayList<CqlCompilerException>();
        this.getLibrary(toElmIdentifier("CqlListOperatorsTest"), errors, testCompilerOptions());
        assertFalse(CqlCompilerException.hasErrors(errors), String.format("Test library compiled with the following errors : %s", this.toString(errors)));
    }

    @Test
    public void test_all_interval_operators() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        var eng = getEngine(testCompilerOptions());

        var evaluationResult = eng.evaluate(toElmIdentifier("CqlListOperatorsTest"));

        var result = evaluationResult.forExpression("simpleList").value();
        assertThat(result, is(Arrays.asList(4, 5, 1, 6, 2, 1)));


        result = evaluationResult.forExpression("simpleSortAsc").value();
        assertThat(result, is(Arrays.asList(1, 1, 2, 4, 5, 6)));

        result = evaluationResult.forExpression("simpleSortDesc").value();
        assertThat(result, is(Arrays.asList(6, 5, 4, 2, 1, 1)));

        result = evaluationResult.forExpression("simpleSortStringAsc").value();
        assertThat(result, is(Arrays.asList("Armadillo", "Wolf", "aardvark", "alligator", "back", "iguana", "zebra")));

        result = evaluationResult.forExpression("simpleSortStringDesc").value();
        assertThat(result, is(Arrays.asList("zebra", "iguana", "back", "alligator", "aardvark", "Wolf", "Armadillo")));

        result = evaluationResult.forExpression("SortDatesAsc").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) result).get(0), new DateTime(bigDecimalZoneOffset, 2012, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) result).get(1), new DateTime(bigDecimalZoneOffset, 2012, 1, 1, 12)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) result).get(2), new DateTime(bigDecimalZoneOffset, 2012, 10, 5)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) result).get(3), new DateTime(bigDecimalZoneOffset, 2012, 10, 5, 10)));

        result = evaluationResult.forExpression("SortDatesDesc").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) result).get(0), new DateTime(bigDecimalZoneOffset, 2012, 10, 5, 10)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) result).get(1), new DateTime(bigDecimalZoneOffset, 2012, 10, 5)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) result).get(2), new DateTime(bigDecimalZoneOffset, 2012, 1, 1, 12)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>) result).get(3), new DateTime(bigDecimalZoneOffset, 2012, 1, 1)));

        result = evaluationResult.forExpression("SortIntWithNullAsc1").value();
        assertThat(result, is(Arrays.asList(null, 1, 2, 3)));

        result = evaluationResult.forExpression("SortIntWithNullAsc2").value();
        assertThat(result, is(Arrays.asList(null, 1, 2, 3)));

        result = evaluationResult.forExpression("SortIntWithNullDesc1").value();
        assertThat(result, is(Arrays.asList(3, 2, 1, null)));

        result = evaluationResult.forExpression("SortIntWithNullDesc2").value();
        assertThat(result, is(Arrays.asList(3, 2, 1, null)));

        result = evaluationResult.forExpression("intList").value();
        assertThat(result, is(Arrays.asList(3, 2, 1)));

//        result = evaluationResult.forExpression("decimalList").value();
//        assertThat(result, is(Arrays.asList(new BigDecimal(3.8).setScale(1, RoundingMode.HALF_EVEN), new BigDecimal(2.4).setScale(1, RoundingMode.HALF_EVEN), new BigDecimal(1.9).setScale(1, RoundingMode.HALF_EVEN))));

        //    result = evaluationResult.forExpression("quantityList").value();
        //    assertThat(result, is(Arrays.asList(new Quantity().withValue(new BigDecimal("19.99")).withUnit("lbs") , new Quantity().withValue(new BigDecimal("17.33")).withUnit("lbs") ,  new Quantity().withValue(new BigDecimal("10.66")).withUnit("lbs") )));

        result = evaluationResult.forExpression("dateTimeList").value();
        List<DateTime> arrListDateTime = new ArrayList<>();
        arrListDateTime.add(new DateTime(bigDecimalZoneOffset, 2016));
        arrListDateTime.add(new DateTime(bigDecimalZoneOffset, 2015));
        arrListDateTime.add(new DateTime(bigDecimalZoneOffset, 2010));
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, arrListDateTime));


        result = evaluationResult.forExpression("timeList").value();
        List<Time> arrList = new ArrayList<>();
        arrList.add(new Time(15, 59, 59, 999));
        arrList.add(new Time(15, 12, 59, 999));
        arrList.add(new Time(15, 12, 13, 999));
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, arrList));

        result = evaluationResult.forExpression("ContainsNullFirst").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ContainsABCHasA").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ContainsJan2012True").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ContainsJan2012False").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ContainsTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ContainsTimeFalse").value();
        assertThat(result, is(false));

//        result = evaluationResult.forExpression("ContainsNullLeft").value();
//        assertThat(result, is(false));

        result = evaluationResult.forExpression("DescendentsEmptyList").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("DistinctEmptyList").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.forExpression("DistinctNullNullNull").value();
        assertThat(result, is(new ArrayList<Object>() {{
            add(null);
        }}));

        result = evaluationResult.forExpression("DistinctANullANull").value();
        assertThat(result, is(Arrays.asList("a", null)));

        result = evaluationResult.forExpression("Distinct112233").value();
        assertThat(result, is(Arrays.asList(1, 2, 3)));

        result = evaluationResult.forExpression("Distinct123123").value();
        assertThat(result, is(Arrays.asList(1, 2, 3)));

        result = evaluationResult.forExpression("DistinctAABBCC").value();
        assertThat(result, is(Arrays.asList("a", "b", "c")));

        result = evaluationResult.forExpression("DistinctABCABC").value();
        assertThat(result, is(Arrays.asList("a", "b", "c")));

        result = evaluationResult.forExpression("DistinctDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(0), new DateTime(bigDecimalZoneOffset, 2012, 10, 5)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(1), new DateTime(bigDecimalZoneOffset, 2012, 1, 1)));
        assertThat(((List<?>)result).size(), is(2));

        result = evaluationResult.forExpression("DistinctTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(0), new Time(15, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(1), new Time(20, 59, 59, 999)));
        assertThat(((List<?>)result).size(), is(2));

        result = evaluationResult.forExpression("EqualNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("EqualEmptyListNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("EqualNullEmptyList").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("EqualEmptyListAndEmptyList").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("Equal12And123").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("Equal123And12").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("Equal123And123").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EqualDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EqualDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EqualTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EqualTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ExceptEmptyListAndEmptyList").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.forExpression("Except1234And23").value();
        assertThat(result, is(Arrays.asList(1, 4)));

        result = evaluationResult.forExpression("Except23And1234").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.forExpression("ExceptDateTimeList").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(0), new DateTime(bigDecimalZoneOffset, 2012, 5, 10)));
        assertThat(((List<?>)result).size(), is(1));

        result = evaluationResult.forExpression("ExceptTimeList").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(0), new Time(15, 59, 59, 999)));
        assertThat(((List<?>)result).size(), is(1));

        result = evaluationResult.forExpression("ExceptNullRight").value();
        assertThat(result, is(Arrays.asList(1, 4)));

        result = evaluationResult.forExpression("ExistsEmpty").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ExistsListNull").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("Exists1").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("Exists12").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ExistsDateTime").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ExistsTime").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ExistsNull").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("FlattenEmpty").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.forExpression("FlattenListNullAndNull").value();
        assertThat(result, is(Arrays.asList(null, null)));

        result = evaluationResult.forExpression("FlattenNullAndListNull").value();
        assertThat(result, is(Arrays.asList(null, null)));

        result = evaluationResult.forExpression("FlattenList12And34").value();
        assertThat(result, is(Arrays.asList(1, 2, 3, 4)));

        result = evaluationResult.forExpression("FlattenDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(0), new DateTime(bigDecimalZoneOffset, 2012, 5, 10)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(1), new DateTime(bigDecimalZoneOffset, 2014, 12, 10)));
        assertThat(((List<?>)result).size(), is(2));

        result = evaluationResult.forExpression("FlattenTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(0), new Time(15, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(1), new Time(20, 59, 59, 999)));
        assertThat(((List<?>)result).size(), is(2));

        result = evaluationResult.forExpression("FirstEmpty").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("FirstNull1").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("First1Null").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("First12").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("FirstDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2012, 5, 10)));

        result = evaluationResult.forExpression("FirstTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 59, 59, 999)));

        result = evaluationResult.forExpression("InNullEmpty").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("InNullAnd1Null").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("In1Null").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("In1And12").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("In3And12").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("InDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("InDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("InTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("InTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IncludesEmptyAndEmpty").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IncludesListNullAndListNull").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("Includes123AndEmpty").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("Includes123And2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("Includes123And4").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IncludesDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IncludesDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IncludesTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IncludesTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IncludesNullLeft").value();
        assertThat(result, is(false));

        // TODO: fix test - going to ContainsEvaluator
//        result = evaluationResult.forExpression("IncludesNullRight").value();
//        assertThat(result, is(true));

        result = evaluationResult.forExpression("IncludedInEmptyAndEmpty").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IncludedInListNullAndListNull").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IncludedInEmptyAnd123").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IncludedIn2And123").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IncludedIn4And123").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IncludedInDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IncludedInDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IncludedInTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("IncludedInTimeFalse").value();
        assertThat(result, is(false));

        // TODO: fix test - going to InEvaluator
//        result = evaluationResult.forExpression("IncludedInNullLeft").value();
//        assertThat(result, is(true));

        result = evaluationResult.forExpression("IncludedInNullRight").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("IndexerNull1List").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Indexer0Of12").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("Indexer1Of12").value();
        assertThat(result, is(2));

        result = evaluationResult.forExpression("Indexer2Of12").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IndexerNeg1Of12").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IndexerDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2012, 5, 10)));

        result = evaluationResult.forExpression("IndexerTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 59, 59, 999)));

        result = evaluationResult.forExpression("IndexOfEmptyNull").value();
        assertThat(result, is(-1));

        result = evaluationResult.forExpression("IndexOfNullEmpty").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("IndexOfNullIn1Null").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("IndexOf1In12").value();
        assertThat(result, is(0));

        result = evaluationResult.forExpression("IndexOf2In12").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("IndexOf3In12").value();
        assertThat(result, is(-1));

        result = evaluationResult.forExpression("IndexOfDateTime").value();
        assertThat(result, is(2));

        result = evaluationResult.forExpression("IndexOfTime").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("IntersectEmptyListAndEmptyList").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.forExpression("Intersect1234And23").value();
        assertThat(result, is(Arrays.asList(2, 3)));

        result = evaluationResult.forExpression("Intersect23And1234").value();
        assertThat(result, is(Arrays.asList(2, 3)));

        result = evaluationResult.forExpression("IntersectDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(0), new DateTime(bigDecimalZoneOffset, 2012, 5, 10)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(1), new DateTime(bigDecimalZoneOffset, 2014, 12, 10)));
        assertThat(((List<?>)result).size(), is(2));

        result = evaluationResult.forExpression("IntersectTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(0), new Time(15, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(1), new Time(20, 59, 59, 999)));
        assertThat(((List<?>)result).size(), is(2));

        result = evaluationResult.forExpression("LastEmpty").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("LastNull1").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("Last1Null").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("Last12").value();
        assertThat(result, is(2));

        result = evaluationResult.forExpression("LastDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2014, 12, 10)));

        result = evaluationResult.forExpression("LastTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(20, 59, 59, 999)));

        result = evaluationResult.forExpression("LengthEmptyList").value();
        assertThat(result, is(0));

        result = evaluationResult.forExpression("LengthNull1").value();
        assertThat(result, is(2));

        result = evaluationResult.forExpression("Length1Null").value();
        assertThat(result, is(2));

        result = evaluationResult.forExpression("Length12").value();
        assertThat(result, is(2));

        result = evaluationResult.forExpression("LengthDateTime").value();
        assertThat(result, is(3));

        result = evaluationResult.forExpression("LengthTime").value();
        assertThat(result, is(6));

        result = evaluationResult.forExpression("LengthNullList").value();
        assertThat(result, is(0));

        result = evaluationResult.forExpression("EquivalentEmptyAndEmpty").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivalentABCAndABC").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivalentABCAndAB").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivalentABCAnd123").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("Equivalent123AndABC").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("Equivalent123AndString123").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivalentDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivalentDateTimeNull").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivalentDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivalentTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("EquivalentTimeNull").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("EquivalentTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("NotEqualEmptyAndEmpty").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("NotEqualABCAndABC").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("NotEqualABCAndAB").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("NotEqualABCAnd123").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("NotEqual123AndABC").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("NotEqual123AndString123").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("NotEqualDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("NotEqualDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("NotEqualTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("NotEqualTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperIncludesEmptyAndEmpty").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperIncludesListNullAndListNull").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperIncludes123AndEmpty").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ProperIncludes123And2").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ProperIncludes123And4").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperIncludesDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ProperIncludesDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperIncludesTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ProperIncludesTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperlyIncludesNullLeft").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperlyIncludes1And111").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperContainsNullRightFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperContainsNullRightTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ProperContainsTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ProperContainsTimeNull").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperInNullRightFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperInNullRightTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ProperInTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ProperInTimeNull").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperIncludedInEmptyAndEmpty").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperIncludedInListNullAndListNull").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperIncludedInEmptyAnd123").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ProperIncludedIn2And123").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ProperIncludedIn4And123").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperIncludedInDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ProperIncludedInDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperIncludedInTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.forExpression("ProperIncludedInTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperlyIncludedInNullRight").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("ProperlyIncludedIn11And1").value();
        assertThat(result, is(false));

        result = evaluationResult.forExpression("SingletonFromEmpty").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SingletonFromListNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SingletonFrom1").value();
        assertThat(result, is(1));

        result = evaluationResult.forExpression("SingletonFromDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(bigDecimalZoneOffset, 2012, 5, 10)));

        result = evaluationResult.forExpression("SingletonFromTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 59, 59, 999)));

        result = evaluationResult.forExpression("SkipNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("SkipEven").value();
        assertThat(result, is(Arrays.asList(3,4,5)));

        result = evaluationResult.forExpression("SkipOdd").value();
        assertThat(result, is(Arrays.asList(4,5)));

        result = evaluationResult.forExpression("SkipNone").value();
        assertThat(result, is(Arrays.asList(1,2,3,4,5)));

        result = evaluationResult.forExpression("SkipAll").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.forExpression("TailNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("TailEven").value();
        assertThat(result, is(Arrays.asList(2,3,4)));

        result = evaluationResult.forExpression("TailOdd").value();
        assertThat(result, is(Arrays.asList(2,3,4,5)));

        result = evaluationResult.forExpression("TailEmpty").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.forExpression("TailOneElement").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.forExpression("TakeNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.forExpression("TakeNullEmpty").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.forExpression("TakeEmpty").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.forExpression("TakeEven").value();
        assertThat(result, is(Arrays.asList(1,2)));

        result = evaluationResult.forExpression("TakeOdd").value();
        assertThat(result, is(Arrays.asList(1,2,3)));

        result = evaluationResult.forExpression("TakeAll").value();
        assertThat(result, is(Arrays.asList(1,2,3,4)));

        result = evaluationResult.forExpression("UnionEmptyAndEmpty").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.forExpression("UnionListNullAndListNull").value();
        assertThat(result, is(Collections.singletonList(null)));

        result = evaluationResult.forExpression("Union123AndEmpty").value();
        assertThat(result, is(Arrays.asList(1, 2, 3)));

        result = evaluationResult.forExpression("Union123And2").value();
        assertThat(result, is(Arrays.asList(1, 2, 3)));

        result = evaluationResult.forExpression("Union123And4").value();
        assertThat(result, is(Arrays.asList(1, 2, 3, 4)));

        result = evaluationResult.forExpression("UnionDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(0), new DateTime(bigDecimalZoneOffset, 2001, 9, 11)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(1), new DateTime(bigDecimalZoneOffset, 2012, 5, 10)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(2), new DateTime(bigDecimalZoneOffset, 2014, 12, 10)));
        assertThat(((List<?>)result).size(), is(3));

        result = evaluationResult.forExpression("UnionTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(0), new Time(15, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(1), new Time(20, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(2), new Time(12, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List<?>)result).get(3), new Time(10, 59, 59, 999)));
        assertThat(((List<?>)result).size(), is(4));
    }

    protected CqlCompilerOptions testCompilerOptions() {
        var options = CqlCompilerOptions.defaultOptions();
        // This test suite contains some definitions that use features that are usually
        // turned off for CQL.
        options.getOptions().remove(CqlCompilerOptions.Options.DisableListDemotion);
        options.getOptions().remove(CqlCompilerOptions.Options.DisableListPromotion);
        return options;
    }


    String toString(List<CqlCompilerException> errors) {
        StringBuilder builder = new StringBuilder();

        for (var e : errors) {
            builder.append(e.toString() + System.lineSeparator());
            if (e.getLocator() != null) {
                builder.append("at" + System.lineSeparator());
                builder.append(e.getLocator().toLocator() + System.lineSeparator());
            }
            builder.append(System.lineSeparator());
        }

        return builder.toString();
    }
}
