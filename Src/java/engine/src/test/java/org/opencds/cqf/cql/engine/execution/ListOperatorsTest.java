package org.opencds.cqf.cql.engine.execution;

import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ListOperatorsTest extends CqlTestBase {

    @Test
    public void test_all_interval_operators() {

        Set<String> set = new HashSet<>();
        EvaluationResult evaluationResult;

        evaluationResult = engineVisitor.evaluate(toElmIdentifier("CqlListOperatorsTest"));
        Object result;

        result = evaluationResult.expressionResults.get("simpleList").value();
        assertThat(result, is(Arrays.asList(4, 5, 1, 6, 2, 1)));


        result = evaluationResult.expressionResults.get("simpleSortAsc").value();
        assertThat(result, is(Arrays.asList(1, 1, 2, 4, 5, 6)));


        result = evaluationResult.expressionResults.get("simpleSortDesc").value();
        assertThat(result, is(Arrays.asList(6, 5, 4, 2, 1, 1)));

        result = evaluationResult.expressionResults.get("simpleSortStringAsc").value();
        assertThat(result, is(Arrays.asList("Armadillo", "Wolf", "aardvark", "alligator", "back", "iguana", "zebra")));

        result = evaluationResult.expressionResults.get("simpleSortStringDesc").value();
        assertThat(result, is(Arrays.asList("zebra", "iguana", "back", "alligator", "aardvark", "Wolf", "Armadillo")));

        result = evaluationResult.expressionResults.get("SortDatesAsc").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List) result).get(0), new DateTime(null, 2012, 1, 1)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List) result).get(1), new DateTime(null, 2012, 1, 1, 12)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List) result).get(2), new DateTime(null, 2012, 10, 5)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List) result).get(3), new DateTime(null, 2012, 10, 5, 10)));

        result = evaluationResult.expressionResults.get("SortDatesDesc").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List) result).get(0), new DateTime(null, 2012, 10, 5, 10)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List) result).get(1), new DateTime(null, 2012, 10, 5)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List) result).get(2), new DateTime(null, 2012, 1, 1, 12)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List) result).get(3), new DateTime(null, 2012, 1, 1)));

        result = evaluationResult.expressionResults.get("SortIntWithNullAsc1").value();
        assertThat(result, is(Arrays.asList(null, 1, 2, 3)));

        result = evaluationResult.expressionResults.get("SortIntWithNullAsc2").value();
        assertThat(result, is(Arrays.asList(null, 1, 2, 3)));

        result = evaluationResult.expressionResults.get("SortIntWithNullDesc1").value();
        assertThat(result, is(Arrays.asList(3, 2, 1, null)));

        result = evaluationResult.expressionResults.get("SortIntWithNullDesc2").value();
        assertThat(result, is(Arrays.asList(3, 2, 1, null)));

        result = evaluationResult.expressionResults.get("intList").value();
        assertThat(result, is(Arrays.asList(3, 2, 1)));

//        result = evaluationResult.expressionResults.get("decimalList").value();
//        assertThat(result, is(Arrays.asList(new BigDecimal(3.8).setScale(1, RoundingMode.HALF_EVEN), new BigDecimal(2.4).setScale(1, RoundingMode.HALF_EVEN), new BigDecimal(1.9).setScale(1, RoundingMode.HALF_EVEN))));

        //    result = evaluationResult.expressionResults.get("quantityList").value();
        //    assertThat(result, is(Arrays.asList(new Quantity().withValue(new BigDecimal("19.99")).withUnit("lbs") , new Quantity().withValue(new BigDecimal("17.33")).withUnit("lbs") ,  new Quantity().withValue(new BigDecimal("10.66")).withUnit("lbs") )));

        result = evaluationResult.expressionResults.get("dateTimeList").value();
        List<DateTime> arrListDateTime = new ArrayList<>();
        arrListDateTime.add(new DateTime(null, 2016));
        arrListDateTime.add(new DateTime(null, 2015));
        arrListDateTime.add(new DateTime(null, 2010));
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, arrListDateTime));


        result = evaluationResult.expressionResults.get("timeList").value();
        List<Time> arrList = new ArrayList<>();
        arrList.add(new Time(15, 59, 59, 999));
        arrList.add(new Time(15, 12, 59, 999));
        arrList.add(new Time(15, 12, 13, 999));
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, arrList));

        result = evaluationResult.expressionResults.get("ContainsNullFirst").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ContainsABCHasA").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ContainsJan2012True").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ContainsJan2012False").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ContainsTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ContainsTimeFalse").value();
        assertThat(result, is(false));

//        result = evaluationResult.expressionResults.get("ContainsNullLeft").value();
//        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("DescendentsEmptyList").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("DistinctEmptyList").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.expressionResults.get("DistinctNullNullNull").value();
        assertThat(result, is(new ArrayList<Object>() {{
            add(null);
        }}));

        result = evaluationResult.expressionResults.get("DistinctANullANull").value();
        assertThat(result, is(Arrays.asList("a", null)));

        result = evaluationResult.expressionResults.get("Distinct112233").value();
        assertThat(result, is(Arrays.asList(1, 2, 3)));

        result = evaluationResult.expressionResults.get("Distinct123123").value();
        assertThat(result, is(Arrays.asList(1, 2, 3)));

        result = evaluationResult.expressionResults.get("DistinctAABBCC").value();
        assertThat(result, is(Arrays.asList("a", "b", "c")));

        result = evaluationResult.expressionResults.get("DistinctABCABC").value();
        assertThat(result, is(Arrays.asList("a", "b", "c")));

        result = evaluationResult.expressionResults.get("DistinctDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new DateTime(null, 2012, 10, 5)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(1), new DateTime(null, 2012, 1, 1)));
        assertThat(((List)result).size(), is(2));

        result = evaluationResult.expressionResults.get("DistinctTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new Time(15, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(1), new Time(20, 59, 59, 999)));
        assertThat(((List)result).size(), is(2));

        result = evaluationResult.expressionResults.get("EqualNullNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("EqualEmptyListNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("EqualNullEmptyList").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("EqualEmptyListAndEmptyList").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("Equal12And123").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("Equal123And12").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("Equal123And123").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EqualDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EqualDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EqualTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EqualTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ExceptEmptyListAndEmptyList").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.expressionResults.get("Except1234And23").value();
        assertThat(result, is(Arrays.asList(1, 4)));

        result = evaluationResult.expressionResults.get("Except23And1234").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.expressionResults.get("ExceptDateTimeList").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new DateTime(null, 2012, 5, 10)));
        assertThat(((List)result).size(), is(1));

        result = evaluationResult.expressionResults.get("ExceptTimeList").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new Time(15, 59, 59, 999)));
        assertThat(((List)result).size(), is(1));

        result = evaluationResult.expressionResults.get("ExceptNullRight").value();
        assertThat(result, is(Arrays.asList(1, 4)));

        result = evaluationResult.expressionResults.get("ExistsEmpty").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ExistsListNull").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("Exists1").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("Exists12").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ExistsDateTime").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ExistsTime").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ExistsNull").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("FlattenEmpty").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.expressionResults.get("FlattenListNullAndNull").value();
        assertThat(result, is(Arrays.asList(null, null)));

        result = evaluationResult.expressionResults.get("FlattenNullAndListNull").value();
        assertThat(result, is(Arrays.asList(null, null)));

        result = evaluationResult.expressionResults.get("FlattenList12And34").value();
        assertThat(result, is(Arrays.asList(1, 2, 3, 4)));

        result = evaluationResult.expressionResults.get("FlattenDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new DateTime(null, 2012, 5, 10)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(1), new DateTime(null, 2014, 12, 10)));
        assertThat(((List)result).size(), is(2));

        result = evaluationResult.expressionResults.get("FlattenTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new Time(15, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(1), new Time(20, 59, 59, 999)));
        assertThat(((List)result).size(), is(2));

        result = evaluationResult.expressionResults.get("FirstEmpty").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("FirstNull1").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("First1Null").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("First12").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("FirstDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012, 5, 10)));

        result = evaluationResult.expressionResults.get("FirstTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("InNullEmpty").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("InNullAnd1Null").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("In1Null").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("In1And12").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("In3And12").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("InDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("InDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("InTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("InTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IncludesEmptyAndEmpty").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IncludesListNullAndListNull").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("Includes123AndEmpty").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("Includes123And2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("Includes123And4").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IncludesDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IncludesDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IncludesTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IncludesTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IncludesNullLeft").value();
        assertThat(result, is(false));

        // TODO: fix test - going to ContainsEvaluator
//        result = evaluationResult.expressionResults.get("IncludesNullRight").value();
//        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IncludedInEmptyAndEmpty").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IncludedInListNullAndListNull").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IncludedInEmptyAnd123").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IncludedIn2And123").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IncludedIn4And123").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IncludedInDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IncludedInDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IncludedInTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IncludedInTimeFalse").value();
        assertThat(result, is(false));

        // TODO: fix test - going to InEvaluator
//        result = evaluationResult.expressionResults.get("IncludedInNullLeft").value();
//        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("IncludedInNullRight").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("IndexerNull1List").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Indexer0Of12").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("Indexer1Of12").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("Indexer2Of12").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IndexerNeg1Of12").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IndexerDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012, 5, 10)));

        result = evaluationResult.expressionResults.get("IndexerTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("IndexOfEmptyNull").value();
        assertThat(result, is(-1));

        result = evaluationResult.expressionResults.get("IndexOfNullEmpty").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("IndexOfNullIn1Null").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("IndexOf1In12").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("IndexOf2In12").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("IndexOf3In12").value();
        assertThat(result, is(-1));

        result = evaluationResult.expressionResults.get("IndexOfDateTime").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("IndexOfTime").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("IntersectEmptyListAndEmptyList").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.expressionResults.get("Intersect1234And23").value();
        assertThat(result, is(Arrays.asList(2, 3)));

        result = evaluationResult.expressionResults.get("Intersect23And1234").value();
        assertThat(result, is(Arrays.asList(2, 3)));

        result = evaluationResult.expressionResults.get("IntersectDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new DateTime(null, 2012, 5, 10)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(1), new DateTime(null, 2014, 12, 10)));
        assertThat(((ArrayList)result).size(), is(2));

        result = evaluationResult.expressionResults.get("IntersectTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new Time(15, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(1), new Time(20, 59, 59, 999)));
        assertThat(((ArrayList)result).size(), is(2));

        result = evaluationResult.expressionResults.get("LastEmpty").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("LastNull1").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("Last1Null").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("Last12").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("LastDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2014, 12, 10)));

        result = evaluationResult.expressionResults.get("LastTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(20, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("LengthEmptyList").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("LengthNull1").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("Length1Null").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("Length12").value();
        assertThat(result, is(2));

        result = evaluationResult.expressionResults.get("LengthDateTime").value();
        assertThat(result, is(3));

        result = evaluationResult.expressionResults.get("LengthTime").value();
        assertThat(result, is(6));

        result = evaluationResult.expressionResults.get("LengthNullList").value();
        assertThat(result, is(0));

        result = evaluationResult.expressionResults.get("EquivalentEmptyAndEmpty").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivalentABCAndABC").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivalentABCAndAB").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivalentABCAnd123").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("Equivalent123AndABC").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("Equivalent123AndString123").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivalentDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivalentDateTimeNull").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivalentDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivalentTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("EquivalentTimeNull").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("EquivalentTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("NotEqualEmptyAndEmpty").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("NotEqualABCAndABC").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("NotEqualABCAndAB").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("NotEqualABCAnd123").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("NotEqual123AndABC").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("NotEqual123AndString123").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("NotEqualDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("NotEqualDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("NotEqualTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("NotEqualTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperIncludesEmptyAndEmpty").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperIncludesListNullAndListNull").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperIncludes123AndEmpty").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ProperIncludes123And2").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ProperIncludes123And4").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperIncludesDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ProperIncludesDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperIncludesTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ProperIncludesTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperlyIncludesNullLeft").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperlyIncludes1And111").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperContainsNullRightFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperContainsNullRightTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ProperContainsTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ProperContainsTimeNull").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperInNullRightFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperInNullRightTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ProperInTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ProperInTimeNull").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperIncludedInEmptyAndEmpty").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperIncludedInListNullAndListNull").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperIncludedInEmptyAnd123").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ProperIncludedIn2And123").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ProperIncludedIn4And123").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperIncludedInDateTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ProperIncludedInDateTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperIncludedInTimeTrue").value();
        assertThat(result, is(true));

        result = evaluationResult.expressionResults.get("ProperIncludedInTimeFalse").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperlyIncludedInNullRight").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("ProperlyIncludedIn11And1").value();
        assertThat(result, is(false));

        result = evaluationResult.expressionResults.get("SingletonFromEmpty").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SingletonFromListNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SingletonFrom1").value();
        assertThat(result, is(1));

        result = evaluationResult.expressionResults.get("SingletonFromDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new DateTime(null, 2012, 5, 10)));

        result = evaluationResult.expressionResults.get("SingletonFromTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(result, new Time(15, 59, 59, 999)));

        result = evaluationResult.expressionResults.get("SkipNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("SkipEven").value();
        assertThat(result, is(Arrays.asList(3,4,5)));

        result = evaluationResult.expressionResults.get("SkipOdd").value();
        assertThat(result, is(Arrays.asList(4,5)));

        result = evaluationResult.expressionResults.get("SkipNone").value();
        assertThat(result, is(Arrays.asList(1,2,3,4,5)));

        result = evaluationResult.expressionResults.get("SkipAll").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.expressionResults.get("TailNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("TailEven").value();
        assertThat(result, is(Arrays.asList(2,3,4)));

        result = evaluationResult.expressionResults.get("TailOdd").value();
        assertThat(result, is(Arrays.asList(2,3,4,5)));

        result = evaluationResult.expressionResults.get("TailEmpty").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.expressionResults.get("TailOneElement").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.expressionResults.get("TakeNull").value();
        assertThat(result, is(nullValue()));

        result = evaluationResult.expressionResults.get("TakeNullEmpty").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.expressionResults.get("TakeEmpty").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.expressionResults.get("TakeEven").value();
        assertThat(result, is(Arrays.asList(1,2)));

        result = evaluationResult.expressionResults.get("TakeOdd").value();
        assertThat(result, is(Arrays.asList(1,2,3)));

        result = evaluationResult.expressionResults.get("TakeAll").value();
        assertThat(result, is(Arrays.asList(1,2,3,4)));

        result = evaluationResult.expressionResults.get("UnionEmptyAndEmpty").value();
        assertThat(result, is(Collections.emptyList()));

        result = evaluationResult.expressionResults.get("UnionListNullAndListNull").value();
        assertThat(result, is(Collections.singletonList(null)));

        result = evaluationResult.expressionResults.get("Union123AndEmpty").value();
        assertThat(result, is(Arrays.asList(1, 2, 3)));

        result = evaluationResult.expressionResults.get("Union123And2").value();
        assertThat(result, is(Arrays.asList(1, 2, 3)));

        result = evaluationResult.expressionResults.get("Union123And4").value();
        assertThat(result, is(Arrays.asList(1, 2, 3, 4)));

        result = evaluationResult.expressionResults.get("UnionDateTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new DateTime(null, 2001, 9, 11)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(1), new DateTime(null, 2012, 5, 10)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(2), new DateTime(null, 2014, 12, 10)));
        assertThat(((List)result).size(), is(3));

        result = evaluationResult.expressionResults.get("UnionTime").value();
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(0), new Time(15, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(1), new Time(20, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(2), new Time(12, 59, 59, 999)));
        Assert.assertTrue(EquivalentEvaluator.equivalent(((List)result).get(3), new Time(10, 59, 59, 999)));
        assertThat(((List)result).size(), is(4));


    }
}
