package org.opencds.cqf.cql.engine.execution;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator.equivalent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.runtime.*;

class ListOperatorsTest extends CqlTestBase {

    @Test
    void cql_list_test_suite_compiles() {
        var errors = new ArrayList<CqlCompilerException>();
        this.getLibrary(toElmIdentifier("CqlListOperatorsTest"), errors, testCompilerOptions());
        assertFalse(
                CqlCompilerException.hasErrors(errors),
                String.format("Test library compiled with the following errors : %s", this.toString(errors)));
    }

    @Test
    void all_interval_operators() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        var eng = getEngine(testCompilerOptions());

        var results = eng.evaluate(toElmIdentifier("CqlListOperatorsTest"));

        var value = results.forExpression("simpleList").value();
        assertThat(value, is(Arrays.asList(4, 5, 1, 6, 2, 1)));

        value = results.forExpression("simpleSortAsc").value();
        assertThat(value, is(Arrays.asList(1, 1, 2, 4, 5, 6)));

        value = results.forExpression("simpleSortDesc").value();
        assertThat(value, is(Arrays.asList(6, 5, 4, 2, 1, 1)));

        value = results.forExpression("simpleSortStringAsc").value();
        assertThat(value, is(Arrays.asList("Armadillo", "Wolf", "aardvark", "alligator", "back", "iguana", "zebra")));

        value = results.forExpression("simpleSortStringDesc").value();
        assertThat(value, is(Arrays.asList("zebra", "iguana", "back", "alligator", "aardvark", "Wolf", "Armadillo")));

        value = results.forExpression("SortDatesAsc").value();
        assertTrue(equivalent(((List<?>) value).get(0), new DateTime(bigDecimalZoneOffset, 2012, 1, 1)));
        assertTrue(equivalent(((List<?>) value).get(1), new DateTime(bigDecimalZoneOffset, 2012, 1, 1, 12)));
        assertTrue(equivalent(((List<?>) value).get(2), new DateTime(bigDecimalZoneOffset, 2012, 10, 5)));
        assertTrue(equivalent(((List<?>) value).get(3), new DateTime(bigDecimalZoneOffset, 2012, 10, 5, 10)));

        value = results.forExpression("SortDatesDesc").value();
        assertTrue(equivalent(((List<?>) value).get(0), new DateTime(bigDecimalZoneOffset, 2012, 10, 5, 10)));
        assertTrue(equivalent(((List<?>) value).get(1), new DateTime(bigDecimalZoneOffset, 2012, 10, 5)));
        assertTrue(equivalent(((List<?>) value).get(2), new DateTime(bigDecimalZoneOffset, 2012, 1, 1, 12)));
        assertTrue(equivalent(((List<?>) value).get(3), new DateTime(bigDecimalZoneOffset, 2012, 1, 1)));

        value = results.forExpression("SortIntWithNullAsc1").value();
        assertThat(value, is(Arrays.asList(null, 1, 2, 3)));

        value = results.forExpression("SortIntWithNullAsc2").value();
        assertThat(value, is(Arrays.asList(null, 1, 2, 3)));

        value = results.forExpression("SortIntWithNullDesc1").value();
        assertThat(value, is(Arrays.asList(3, 2, 1, null)));

        value = results.forExpression("SortIntWithNullDesc2").value();
        assertThat(value, is(Arrays.asList(3, 2, 1, null)));

        value = results.forExpression("intList").value();
        assertThat(value, is(Arrays.asList(3, 2, 1)));

        //        value = results.forExpression("decimalList").value();
        //        assertThat(value, is(Arrays.asList(new BigDecimal(3.8).setScale(1, RoundingMode.HALF_EVEN), new
        // BigDecimal(2.4).setScale(1, RoundingMode.HALF_EVEN), new BigDecimal(1.9).setScale(1,
        // RoundingMode.HALF_EVEN))));

        //    value = results.forExpression("quantityList").value();
        //    assertThat(value, is(Arrays.asList(new Quantity().withValue(new BigDecimal("19.99")).withUnit("lbs") ,
        // new Quantity().withValue(new BigDecimal("17.33")).withUnit("lbs") ,  new Quantity().withValue(new
        // BigDecimal("10.66")).withUnit("lbs") )));

        value = results.forExpression("dateTimeList").value();
        List<DateTime> arrListDateTime = new ArrayList<>();
        arrListDateTime.add(new DateTime(bigDecimalZoneOffset, 2016));
        arrListDateTime.add(new DateTime(bigDecimalZoneOffset, 2015));
        arrListDateTime.add(new DateTime(bigDecimalZoneOffset, 2010));
        assertTrue(equivalent(value, arrListDateTime));

        value = results.forExpression("timeList").value();
        List<Time> arrList = new ArrayList<>();
        arrList.add(new Time(15, 59, 59, 999));
        arrList.add(new Time(15, 12, 59, 999));
        arrList.add(new Time(15, 12, 13, 999));
        assertTrue(equivalent(value, arrList));

        value = results.forExpression("ContainsABNullHasNull").value();
        assertThat(value, is(true));

        value = results.forExpression("ContainsNullFirst").value();
        assertThat(value, is(false));

        value = results.forExpression("ContainsABCHasA").value();
        assertThat(value, is(true));

        value = results.forExpression("ContainsJan2012True").value();
        assertThat(value, is(true));

        value = results.forExpression("ContainsJan2012False").value();
        assertThat(value, is(false));

        value = results.forExpression("ContainsTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("ContainsTimeFalse").value();
        assertThat(value, is(false));

        //        value = results.forExpression("ContainsNullLeft").value();
        //        assertThat(value, is(false));

        value = results.forExpression("DescendentsEmptyList").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("DistinctEmptyList").value();
        assertThat(value, is(Collections.emptyList()));

        value = results.forExpression("DistinctNullNullNull").value();
        assertThat(value, is(new ArrayList<Object>() {
            {
                add(null);
            }
        }));

        value = results.forExpression("DistinctANullANull").value();
        assertThat(value, is(Arrays.asList("a", null)));

        value = results.forExpression("Distinct112233").value();
        assertThat(value, is(Arrays.asList(1, 2, 3)));

        value = results.forExpression("Distinct123123").value();
        assertThat(value, is(Arrays.asList(1, 2, 3)));

        value = results.forExpression("DistinctAABBCC").value();
        assertThat(value, is(Arrays.asList("a", "b", "c")));

        value = results.forExpression("DistinctABCABC").value();
        assertThat(value, is(Arrays.asList("a", "b", "c")));

        value = results.forExpression("DistinctDateTime").value();
        assertTrue(equivalent(((List<?>) value).get(0), new DateTime(bigDecimalZoneOffset, 2012, 10, 5)));
        assertTrue(equivalent(((List<?>) value).get(1), new DateTime(bigDecimalZoneOffset, 2012, 1, 1)));
        assertThat(((List<?>) value).size(), is(2));

        value = results.forExpression("DistinctTime").value();
        assertTrue(equivalent(((List<?>) value).get(0), new Time(15, 59, 59, 999)));
        assertTrue(equivalent(((List<?>) value).get(1), new Time(20, 59, 59, 999)));
        assertThat(((List<?>) value).size(), is(2));

        value = results.forExpression("EqualNullNull").value();
        assertThat(value, is(true));

        value = results.forExpression("EqualEmptyListNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("EqualNullEmptyList").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("EqualEmptyListAndEmptyList").value();
        assertThat(value, is(true));

        value = results.forExpression("Equal12And123").value();
        assertThat(value, is(false));

        value = results.forExpression("Equal123And12").value();
        assertThat(value, is(false));

        value = results.forExpression("Equal123And123").value();
        assertThat(value, is(true));

        value = results.forExpression("EqualDateTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("EqualDateTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("EqualTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("EqualTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("ExceptEmptyListAndEmptyList").value();
        assertThat(value, is(Collections.emptyList()));

        value = results.forExpression("Except1234And23").value();
        assertThat(value, is(Arrays.asList(1, 4)));

        value = results.forExpression("Except23And1234").value();
        assertThat(value, is(Collections.emptyList()));

        value = results.forExpression("ExceptDateTimeList").value();
        assertTrue(equivalent(((List<?>) value).get(0), new DateTime(bigDecimalZoneOffset, 2012, 5, 10)));
        assertThat(((List<?>) value).size(), is(1));

        value = results.forExpression("ExceptTimeList").value();
        assertTrue(equivalent(((List<?>) value).get(0), new Time(15, 59, 59, 999)));
        assertThat(((List<?>) value).size(), is(1));

        value = results.forExpression("ExceptNullRight").value();
        assertThat(value, is(Arrays.asList(1, 4)));

        value = results.forExpression("ExistsEmpty").value();
        assertThat(value, is(false));

        value = results.forExpression("ExistsListNull").value();
        assertThat(value, is(false));

        value = results.forExpression("Exists1").value();
        assertThat(value, is(true));

        value = results.forExpression("Exists12").value();
        assertThat(value, is(true));

        value = results.forExpression("ExistsDateTime").value();
        assertThat(value, is(true));

        value = results.forExpression("ExistsTime").value();
        assertThat(value, is(true));

        value = results.forExpression("ExistsNull").value();
        assertThat(value, is(false));

        value = results.forExpression("FlattenEmpty").value();
        assertThat(value, is(Collections.emptyList()));

        value = results.forExpression("FlattenListNullAndNull").value();
        assertThat(value, is(Arrays.asList(null, null)));

        value = results.forExpression("FlattenNullAndListNull").value();
        assertThat(value, is(Arrays.asList(null, null)));

        value = results.forExpression("FlattenList12And34").value();
        assertThat(value, is(Arrays.asList(1, 2, 3, 4)));

        value = results.forExpression("FlattenDateTime").value();
        assertTrue(equivalent(((List<?>) value).get(0), new DateTime(bigDecimalZoneOffset, 2012, 5, 10)));
        assertTrue(equivalent(((List<?>) value).get(1), new DateTime(bigDecimalZoneOffset, 2014, 12, 10)));
        assertThat(((List<?>) value).size(), is(2));

        value = results.forExpression("FlattenTime").value();
        assertTrue(equivalent(((List<?>) value).get(0), new Time(15, 59, 59, 999)));
        assertTrue(equivalent(((List<?>) value).get(1), new Time(20, 59, 59, 999)));
        assertThat(((List<?>) value).size(), is(2));

        value = results.forExpression("FirstEmpty").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("FirstNull1").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("First1Null").value();
        assertThat(value, is(1));

        value = results.forExpression("First12").value();
        assertThat(value, is(1));

        value = results.forExpression("FirstDateTime").value();
        assertTrue(equivalent(value, new DateTime(bigDecimalZoneOffset, 2012, 5, 10)));

        value = results.forExpression("FirstTime").value();
        assertTrue(equivalent(value, new Time(15, 59, 59, 999)));

        value = results.forExpression("InNullEmpty").value();
        assertThat(value, is(false));

        value = results.forExpression("InNullAnd1Null").value();
        assertThat(value, is(true));

        value = results.forExpression("In1Null").value();
        assertThat(value, is(false));

        value = results.forExpression("In1And12").value();
        assertThat(value, is(true));

        value = results.forExpression("In3And12").value();
        assertThat(value, is(false));

        value = results.forExpression("InDateTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("InDateTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("InTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("InTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("IncludesEmptyAndEmpty").value();
        assertThat(value, is(true));

        value = results.forExpression("IncludesListNullAndListNull").value();
        assertThat(value, is(true));

        value = results.forExpression("Includes123AndEmpty").value();
        assertThat(value, is(true));

        value = results.forExpression("Includes123And2").value();
        assertThat(value, is(true));

        value = results.forExpression("Includes123And4").value();
        assertThat(value, is(false));

        value = results.forExpression("IncludesDateTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IncludesDateTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("IncludesTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IncludesTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("IncludesNullLeft").value();
        assertThat(value, is(false));

        // TODO: fix test - going to ContainsEvaluator
        //        value = results.forExpression("IncludesNullRight").value();
        //        assertThat(value, is(true));

        value = results.forExpression("IncludedInEmptyAndEmpty").value();
        assertThat(value, is(true));

        value = results.forExpression("IncludedInListNullAndListNull").value();
        assertThat(value, is(true));

        value = results.forExpression("IncludedInEmptyAnd123").value();
        assertThat(value, is(true));

        value = results.forExpression("IncludedIn2And123").value();
        assertThat(value, is(true));

        value = results.forExpression("IncludedIn4And123").value();
        assertThat(value, is(false));

        value = results.forExpression("IncludedInDateTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IncludedInDateTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("IncludedInTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("IncludedInTimeFalse").value();
        assertThat(value, is(false));

        // TODO: fix test - going to InEvaluator
        //        value = results.forExpression("IncludedInNullLeft").value();
        //        assertThat(value, is(true));

        value = results.forExpression("IncludedInNullRight").value();
        assertThat(value, is(false));

        value = results.forExpression("IndexerNull1List").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("Indexer0Of12").value();
        assertThat(value, is(1));

        value = results.forExpression("Indexer1Of12").value();
        assertThat(value, is(2));

        value = results.forExpression("Indexer2Of12").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IndexerNeg1Of12").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IndexerDateTime").value();
        assertTrue(equivalent(value, new DateTime(bigDecimalZoneOffset, 2012, 5, 10)));

        value = results.forExpression("IndexerTime").value();
        assertTrue(equivalent(value, new Time(15, 59, 59, 999)));

        value = results.forExpression("IndexOfEmptyNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IndexOfNullEmpty").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IndexOfNullIn1Null").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("IndexOf1In12").value();
        assertThat(value, is(0));

        value = results.forExpression("IndexOf2In12").value();
        assertThat(value, is(1));

        value = results.forExpression("IndexOf3In12").value();
        assertThat(value, is(-1));

        value = results.forExpression("IndexOfDateTime").value();
        assertThat(value, is(2));

        value = results.forExpression("IndexOfTime").value();
        assertThat(value, is(1));

        value = results.forExpression("IntersectEmptyListAndEmptyList").value();
        assertThat(value, is(Collections.emptyList()));

        value = results.forExpression("Intersect1234And23").value();
        assertThat(value, is(Arrays.asList(2, 3)));

        value = results.forExpression("Intersect23And1234").value();
        assertThat(value, is(Arrays.asList(2, 3)));

        value = results.forExpression("IntersectDateTime").value();
        assertTrue(equivalent(((List<?>) value).get(0), new DateTime(bigDecimalZoneOffset, 2012, 5, 10)));
        assertTrue(equivalent(((List<?>) value).get(1), new DateTime(bigDecimalZoneOffset, 2014, 12, 10)));
        assertThat(((List<?>) value).size(), is(2));

        value = results.forExpression("IntersectTime").value();
        assertTrue(equivalent(((List<?>) value).get(0), new Time(15, 59, 59, 999)));
        assertTrue(equivalent(((List<?>) value).get(1), new Time(20, 59, 59, 999)));
        assertThat(((List<?>) value).size(), is(2));

        value = results.forExpression("LastEmpty").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("LastNull1").value();
        assertThat(value, is(1));

        value = results.forExpression("Last1Null").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("Last12").value();
        assertThat(value, is(2));

        value = results.forExpression("LastDateTime").value();
        assertTrue(equivalent(value, new DateTime(bigDecimalZoneOffset, 2014, 12, 10)));

        value = results.forExpression("LastTime").value();
        assertTrue(equivalent(value, new Time(20, 59, 59, 999)));

        value = results.forExpression("LengthEmptyList").value();
        assertThat(value, is(0));

        value = results.forExpression("LengthNull1").value();
        assertThat(value, is(2));

        value = results.forExpression("Length1Null").value();
        assertThat(value, is(2));

        value = results.forExpression("Length12").value();
        assertThat(value, is(2));

        value = results.forExpression("LengthDateTime").value();
        assertThat(value, is(3));

        value = results.forExpression("LengthTime").value();
        assertThat(value, is(6));

        value = results.forExpression("LengthNullList").value();
        assertThat(value, is(0));

        value = results.forExpression("EquivalentEmptyAndEmpty").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivalentABCAndABC").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivalentABCAndAB").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivalentABCAnd123").value();
        assertThat(value, is(false));

        value = results.forExpression("Equivalent123AndABC").value();
        assertThat(value, is(false));

        value = results.forExpression("Equivalent123AndString123").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivalentDateTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivalentDateTimeNull").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivalentDateTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivalentTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("EquivalentTimeNull").value();
        assertThat(value, is(false));

        value = results.forExpression("EquivalentTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("NotEqualEmptyAndEmpty").value();
        assertThat(value, is(false));

        value = results.forExpression("NotEqualABCAndABC").value();
        assertThat(value, is(false));

        value = results.forExpression("NotEqualABCAndAB").value();
        assertThat(value, is(true));

        value = results.forExpression("NotEqualABCAnd123").value();
        assertThat(value, is(true));

        value = results.forExpression("NotEqual123AndABC").value();
        assertThat(value, is(true));

        value = results.forExpression("NotEqual123AndString123").value();
        assertThat(value, is(true));

        value = results.forExpression("NotEqualDateTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("NotEqualDateTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("NotEqualTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("NotEqualTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("ProperIncludesEmptyAndEmpty").value();
        assertThat(value, is(false));

        value = results.forExpression("ProperIncludesListNullAndListNull").value();
        assertThat(value, is(false));

        value = results.forExpression("ProperIncludes123AndEmpty").value();
        assertThat(value, is(true));

        value = results.forExpression("ProperIncludes123And2").value();
        assertThat(value, is(true));

        value = results.forExpression("ProperIncludes123And4").value();
        assertThat(value, is(false));

        value = results.forExpression("ProperIncludesDateTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("ProperIncludesDateTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("ProperIncludesTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("ProperIncludesTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("ProperlyIncludesNullLeft").value();
        assertThat(value, is(false));

        value = results.forExpression("ProperlyIncludes1And111").value();
        assertThat(value, is(false));

        value = results.forExpression("ProperContainsNullRightFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("ProperContainsNullRightTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("ProperContainsTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("ProperContainsTimeNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("ProperInNullRightFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("ProperInNullRightTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("ProperInTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("ProperInTimeNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("ProperIncludedInEmptyAndEmpty").value();
        assertThat(value, is(false));

        value = results.forExpression("ProperIncludedInListNullAndListNull").value();
        assertThat(value, is(false));

        value = results.forExpression("ProperIncludedInEmptyAnd123").value();
        assertThat(value, is(true));

        value = results.forExpression("ProperIncludedIn2And123").value();
        assertThat(value, is(true));

        value = results.forExpression("ProperIncludedIn4And123").value();
        assertThat(value, is(false));

        value = results.forExpression("ProperIncludedInDateTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("ProperIncludedInDateTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("ProperIncludedInTimeTrue").value();
        assertThat(value, is(true));

        value = results.forExpression("ProperIncludedInTimeFalse").value();
        assertThat(value, is(false));

        value = results.forExpression("ProperlyIncludedInNullRight").value();
        assertThat(value, is(false));

        value = results.forExpression("ProperlyIncludedIn11And1").value();
        assertThat(value, is(false));

        value = results.forExpression("SingletonFromEmpty").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SingletonFromListNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SingletonFrom1").value();
        assertThat(value, is(1));

        value = results.forExpression("SingletonFromDateTime").value();
        assertTrue(equivalent(value, new DateTime(bigDecimalZoneOffset, 2012, 5, 10)));

        value = results.forExpression("SingletonFromTime").value();
        assertTrue(equivalent(value, new Time(15, 59, 59, 999)));

        value = results.forExpression("SkipNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("SkipEven").value();
        assertThat(value, is(Arrays.asList(3, 4, 5)));

        value = results.forExpression("SkipOdd").value();
        assertThat(value, is(Arrays.asList(4, 5)));

        value = results.forExpression("SkipNone").value();
        assertThat(value, is(Arrays.asList(1, 2, 3, 4, 5)));

        value = results.forExpression("SkipAll").value();
        assertThat(value, is(Collections.emptyList()));

        value = results.forExpression("TailNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("TailEven").value();
        assertThat(value, is(Arrays.asList(2, 3, 4)));

        value = results.forExpression("TailOdd").value();
        assertThat(value, is(Arrays.asList(2, 3, 4, 5)));

        value = results.forExpression("TailEmpty").value();
        assertThat(value, is(Collections.emptyList()));

        value = results.forExpression("TailOneElement").value();
        assertThat(value, is(Collections.emptyList()));

        value = results.forExpression("TakeNull").value();
        assertThat(value, is(nullValue()));

        value = results.forExpression("TakeNullEmpty").value();
        assertThat(value, is(Collections.emptyList()));

        value = results.forExpression("TakeEmpty").value();
        assertThat(value, is(Collections.emptyList()));

        value = results.forExpression("TakeEven").value();
        assertThat(value, is(Arrays.asList(1, 2)));

        value = results.forExpression("TakeOdd").value();
        assertThat(value, is(Arrays.asList(1, 2, 3)));

        value = results.forExpression("TakeAll").value();
        assertThat(value, is(Arrays.asList(1, 2, 3, 4)));
    }

    @Test
    void union_operator() {
        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();
        var eng = getEngine(testCompilerOptions());
        var results = eng.evaluate(toElmIdentifier("CqlListOperatorsTest"));

        List<?> value = (List<?>) results.forExpression("UnionEmptyAndEmpty").value();
        assertThat(value, is(Collections.emptyList()));

        value = (List<?>) results.forExpression("UnionListNullAndListNull").value();
        assertThat(value, is(Collections.singletonList(null)));

        value = (List<?>) results.forExpression("Union123AndEmpty").value();
        assertThat(value, is(Arrays.asList(1, 2, 3)));

        value = (List<?>) results.forExpression("Union123And2").value();
        assertThat(value, is(Arrays.asList(1, 2, 3)));

        value = (List<?>) results.forExpression("Union123And4").value();
        assertThat(value, is(Arrays.asList(1, 2, 3, 4)));

        value = (List<?>) results.forExpression("UnionDateTime").value();
        assertTrue(equivalent(value.get(0), new DateTime(bigDecimalZoneOffset, 2001, 9, 11)));
        assertTrue(equivalent(value.get(1), new DateTime(bigDecimalZoneOffset, 2012, 5, 10)));
        assertTrue(equivalent(value.get(2), new DateTime(bigDecimalZoneOffset, 2014, 12, 10)));
        assertThat(value.size(), is(3));

        value = (List<?>) results.forExpression("UnionTime").value();
        assertTrue(equivalent(value.get(0), new Time(15, 59, 59, 999)));
        assertTrue(equivalent(value.get(1), new Time(20, 59, 59, 999)));
        assertTrue(equivalent(value.get(2), new Time(12, 59, 59, 999)));
        assertTrue(equivalent(value.get(3), new Time(10, 59, 59, 999)));
        assertThat(value.size(), is(4));

        value = (List<?>) results.forExpression("UnionDisparateTypes").value();
        assertThat(value.size(), is(4));
        assertTrue(equivalent(value.get(0), 1));
        assertTrue(equivalent(value.get(1), "hi"));
        assertTrue(equivalent(value.get(2), true));
        assertTrue(equivalent(value.get(3), new BigDecimal("1.0")));
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
