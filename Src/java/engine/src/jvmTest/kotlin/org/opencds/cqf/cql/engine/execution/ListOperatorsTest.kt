package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.cqframework.cql.cql2elm.CqlCompilerException
import org.cqframework.cql.cql2elm.CqlCompilerException.Companion.hasErrors
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.CqlCompilerOptions.Companion.defaultOptions
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Integer
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString

internal class ListOperatorsTest : CqlTestBase() {
    @Test
    fun cql_list_test_suite_compiles() {
        val errors = ArrayList<CqlCompilerException>()
        this.getLibrary(toElmIdentifier("CqlListOperatorsTest"), errors, testCompilerOptions())
        assertFalse(
            hasErrors(errors),
            String.format(
                "Test library compiled with the following errors : %s",
                this.toString(errors),
            ),
        )
    }

    @Test
    fun all_interval_operators() {
        val bigDecimalZoneOffset = bigDecimalZoneOffset

        val eng = getEngine(testCompilerOptions())

        val results = eng.evaluate { library("CqlListOperatorsTest") }.onlyResultOrThrow

        var value = results["simpleList"]!!.value
        assertEquals(
            listOf(
                    4.toCqlInteger(),
                    5.toCqlInteger(),
                    Integer.ONE,
                    6.toCqlInteger(),
                    2.toCqlInteger(),
                    Integer.ONE,
                )
                .toCqlList(),
            value,
        )

        value = results["simpleSortAsc"]!!.value
        assertEquals(
            listOf(
                    Integer.ONE,
                    Integer.ONE,
                    2.toCqlInteger(),
                    4.toCqlInteger(),
                    5.toCqlInteger(),
                    6.toCqlInteger(),
                )
                .toCqlList(),
            value,
        )

        value = results["simpleSortDesc"]!!.value
        assertEquals(
            listOf(
                    6.toCqlInteger(),
                    5.toCqlInteger(),
                    4.toCqlInteger(),
                    2.toCqlInteger(),
                    Integer.ONE,
                    Integer.ONE,
                )
                .toCqlList(),
            value,
        )

        value = results["simpleSortStringAsc"]!!.value
        assertEquals(
            listOf(
                    "Armadillo".toCqlString(),
                    "Wolf".toCqlString(),
                    "aardvark".toCqlString(),
                    "alligator".toCqlString(),
                    "back".toCqlString(),
                    "iguana".toCqlString(),
                    "zebra".toCqlString(),
                )
                .toCqlList(),
            value,
        )

        value = results["simpleSortStringDesc"]!!.value
        assertEquals(
            listOf(
                    "zebra".toCqlString(),
                    "iguana".toCqlString(),
                    "back".toCqlString(),
                    "alligator".toCqlString(),
                    "aardvark".toCqlString(),
                    "Wolf".toCqlString(),
                    "Armadillo".toCqlString(),
                )
                .toCqlList(),
            value,
        )

        value = results["SortDatesAsc"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent(
                    (value as List).elementAt(0),
                    DateTime(bigDecimalZoneOffset, 2012, 1, 1),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    value.elementAt(1),
                    DateTime(bigDecimalZoneOffset, 2012, 1, 1, 12),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    value.elementAt(2),
                    DateTime(bigDecimalZoneOffset, 2012, 10, 5),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    value.elementAt(3),
                    DateTime(bigDecimalZoneOffset, 2012, 10, 5, 10),
                )
                .value == true
        )

        value = results["SortDatesDesc"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent(
                    (value as List).elementAt(0),
                    DateTime(bigDecimalZoneOffset, 2012, 10, 5, 10),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    value.elementAt(1),
                    DateTime(bigDecimalZoneOffset, 2012, 10, 5),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    value.elementAt(2),
                    DateTime(bigDecimalZoneOffset, 2012, 1, 1, 12),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    value.elementAt(3),
                    DateTime(bigDecimalZoneOffset, 2012, 1, 1),
                )
                .value == true
        )

        value = results["SortIntWithNullAsc1"]!!.value
        assertEquals(
            listOf(null, Integer.ONE, 2.toCqlInteger(), 3.toCqlInteger()).toCqlList(),
            value,
        )

        value = results["SortIntWithNullAsc2"]!!.value
        assertEquals(
            listOf(null, Integer.ONE, 2.toCqlInteger(), 3.toCqlInteger()).toCqlList(),
            value,
        )

        value = results["SortIntWithNullDesc1"]!!.value
        assertEquals(
            listOf(3.toCqlInteger(), 2.toCqlInteger(), Integer.ONE, null).toCqlList(),
            value,
        )

        value = results["SortIntWithNullDesc2"]!!.value
        assertEquals(
            listOf(3.toCqlInteger(), 2.toCqlInteger(), Integer.ONE, null).toCqlList(),
            value,
        )

        value = results["intList"]!!.value
        assertEquals(
            listOf(3.toCqlInteger(), 2.toCqlInteger(), 1.toCqlInteger()).toCqlList(),
            value,
        )

        //        value = results["decimalList"].value;
        //        assertThat(value, is(Arrays.asList(new BigDecimal(3.8).setScale(1,
        // RoundingMode.HALF_EVEN), new
        // BigDecimal(2.4).setScale(1, RoundingMode.HALF_EVEN), new BigDecimal(1.9).setScale(1,
        // RoundingMode.HALF_EVEN))));

        //    value = results["quantityList"].value;
        //    assertThat(value, is(Arrays.asList(new Quantity().withValue(new
        // BigDecimal("19.99")).withUnit("lbs") ,
        // new Quantity().withValue(new BigDecimal("17.33")).withUnit("lbs") ,  new
        // Quantity().withValue(new
        // BigDecimal("10.66")).withUnit("lbs") )));
        value = results["dateTimeList"]!!.value
        val arrListDateTime =
            listOf(
                DateTime(bigDecimalZoneOffset, 2016),
                DateTime(bigDecimalZoneOffset, 2015),
                DateTime(bigDecimalZoneOffset, 2010),
            )
        assertTrue(EquivalentEvaluator.equivalent(value, arrListDateTime.toCqlList()).value == true)

        value = results["timeList"]!!.value
        val arrList = listOf(Time(15, 59, 59, 999), Time(15, 12, 59, 999), Time(15, 12, 13, 999))
        assertTrue(EquivalentEvaluator.equivalent(value, arrList.toCqlList()).value == true)

        value = results["ContainsABNullHasNull"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ContainsNullFirst"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ContainsABCHasA"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ContainsJan2012True"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ContainsJan2012False"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ContainsTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ContainsTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        //        value = results["ContainsNullLeft"]!!.value;
        //        assertThat(value, is(false));
        value = results["DescendentsEmptyList"]!!.value
        assertNull(value)

        value = results["DistinctEmptyList"]!!.value
        assertEquals(List.EMPTY_LIST, value)

        value = results["DistinctNullNullNull"]!!.value
        assertEquals(listOf(null).toCqlList(), value)

        value = results["DistinctANullANull"]!!.value
        assertEquals(listOf("a".toCqlString(), null).toCqlList(), value)

        value = results["Distinct112233"]!!.value
        assertEquals(listOf(Integer.ONE, 2.toCqlInteger(), 3.toCqlInteger()).toCqlList(), value)

        value = results["Distinct123123"]!!.value
        assertEquals(listOf(Integer.ONE, 2.toCqlInteger(), 3.toCqlInteger()).toCqlList(), value)

        value = results["DistinctAABBCC"]!!.value
        assertEquals(
            listOf("a".toCqlString(), "b".toCqlString(), "c".toCqlString()).toCqlList(),
            value,
        )

        value = results["DistinctABCABC"]!!.value
        assertEquals(
            listOf("a".toCqlString(), "b".toCqlString(), "c".toCqlString()).toCqlList(),
            value,
        )

        value = results["DistinctDateTime"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent(
                    (value as List).elementAt(0),
                    DateTime(bigDecimalZoneOffset, 2012, 10, 5),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    value.elementAt(1),
                    DateTime(bigDecimalZoneOffset, 2012, 1, 1),
                )
                .value == true
        )
        assertEquals(2, value.count())

        value = results["DistinctTime"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent((value as List).elementAt(0), Time(15, 59, 59, 999))
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(value.elementAt(1), Time(20, 59, 59, 999)).value == true
        )
        assertEquals(2, value.count())

        value = results["EqualNullNull"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["EqualEmptyListNull"]!!.value
        assertNull(value)

        value = results["EqualNullEmptyList"]!!.value
        assertNull(value)

        value = results["EqualEmptyListAndEmptyList"]!!.value
        assertNull(value)

        value = results["Equal12And123"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["Equal123And12"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["Equal123And123"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["EqualDateTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["EqualDateTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["EqualTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["EqualTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ExceptEmptyListAndEmptyList"]!!.value
        assertEquals(List.EMPTY_LIST, value)

        value = results["Except1234And23"]!!.value
        assertEquals(listOf(Integer.ONE, 4.toCqlInteger()).toCqlList(), value)

        value = results["Except23And1234"]!!.value
        assertEquals(List.EMPTY_LIST, value)

        value = results["ExceptDateTimeList"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent(
                    (value as List).elementAt(0),
                    DateTime(bigDecimalZoneOffset, 2012, 5, 10),
                )
                .value == true
        )
        assertEquals(1, value.count())

        value = results["ExceptTimeList"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent((value as List).elementAt(0), Time(15, 59, 59, 999))
                .value == true
        )
        assertEquals(1, value.count())

        value = results["ExceptNullRight"]!!.value
        assertEquals(listOf(Integer.ONE, 4.toCqlInteger()).toCqlList(), value)

        value = results["ExistsEmpty"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ExistsListNull"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["Exists1"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["Exists12"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ExistsDateTime"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ExistsTime"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ExistsNull"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["FlattenEmpty"]!!.value
        assertEquals(List.EMPTY_LIST, value)

        value = results["FlattenListNullAndNull"]!!.value
        assertEquals(listOf(null, null).toCqlList(), value)

        value = results["FlattenNullAndListNull"]!!.value
        assertEquals(listOf(null, null).toCqlList(), value)

        value = results["FlattenList12And34"]!!.value
        assertEquals(
            listOf(Integer.ONE, 2.toCqlInteger(), 3.toCqlInteger(), 4.toCqlInteger()).toCqlList(),
            value,
        )

        value = results["FlattenDateTime"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent(
                    (value as List).elementAt(0),
                    DateTime(bigDecimalZoneOffset, 2012, 5, 10),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    value.elementAt(1),
                    DateTime(bigDecimalZoneOffset, 2014, 12, 10),
                )
                .value == true
        )
        assertEquals(2, value.count())

        value = results["FlattenTime"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent((value as List).elementAt(0), Time(15, 59, 59, 999))
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(value.elementAt(1), Time(20, 59, 59, 999)).value == true
        )
        assertEquals(2, value.count())

        value = results["FirstEmpty"]!!.value
        assertNull(value)

        value = results["FirstNull1"]!!.value
        assertNull(value)

        value = results["First1Null"]!!.value
        assertEquals(Integer.ONE, value)

        value = results["First12"]!!.value
        assertEquals(Integer.ONE, value)

        value = results["FirstDateTime"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 5, 10))
                .value == true
        )

        value = results["FirstTime"]!!.value
        assertTrue(EquivalentEvaluator.equivalent(value, Time(15, 59, 59, 999)).value == true)

        value = results["InNullEmpty"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["InNullAnd1Null"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["In1Null"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["In1And12"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["In3And12"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["InDateTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["InDateTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["InTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["InTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IncludesEmptyAndEmpty"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IncludesListNullAndListNull"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["Includes123AndEmpty"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["Includes123And2"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["Includes123And4"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IncludesDateTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IncludesDateTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IncludesTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IncludesTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IncludesNullLeft"]!!.value
        assertEquals(Boolean.FALSE, value)

        // TODO: fix test - going to ContainsEvaluator
        //        value = results["IncludesNullRight"]!!.value;
        //        assertThat(value, is(true));
        value = results["IncludedInEmptyAndEmpty"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IncludedInListNullAndListNull"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IncludedInEmptyAnd123"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IncludedIn2And123"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IncludedIn4And123"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IncludedInDateTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IncludedInDateTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IncludedInTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IncludedInTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        // TODO: fix test - going to InEvaluator
        //        value = results["IncludedInNullLeft"]!!.value;
        //        assertThat(value, is(true));
        value = results["IncludedInNullRight"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IndexerNull1List"]!!.value
        assertNull(value)

        value = results["Indexer0Of12"]!!.value
        assertEquals(Integer.ONE, value)

        value = results["Indexer1Of12"]!!.value
        assertEquals(2.toCqlInteger(), value)

        value = results["Indexer2Of12"]!!.value
        assertNull(value)

        value = results["IndexerNeg1Of12"]!!.value
        assertNull(value)

        value = results["IndexerDateTime"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 5, 10))
                .value == true
        )

        value = results["IndexerTime"]!!.value
        assertTrue(EquivalentEvaluator.equivalent(value, Time(15, 59, 59, 999)).value == true)

        value = results["IndexOfEmptyNull"]!!.value
        assertNull(value)

        value = results["IndexOfNullEmpty"]!!.value
        assertNull(value)

        value = results["IndexOfNullIn1Null"]!!.value
        assertNull(value)

        value = results["IndexOf1In12"]!!.value
        assertEquals(Integer.ZERO, value)

        value = results["IndexOf2In12"]!!.value
        assertEquals(Integer.ONE, value)

        value = results["IndexOf3In12"]!!.value
        assertEquals((-1).toCqlInteger(), value)

        value = results["IndexOfDateTime"]!!.value
        assertEquals(2.toCqlInteger(), value)

        value = results["IndexOfTime"]!!.value
        assertEquals(Integer.ONE, value)

        value = results["IntersectEmptyListAndEmptyList"]!!.value
        assertEquals(List.EMPTY_LIST, value)

        value = results["Intersect1234And23"]!!.value
        assertEquals(listOf(2.toCqlInteger(), 3.toCqlInteger()).toCqlList(), value)

        value = results["Intersect23And1234"]!!.value
        assertEquals(listOf(2.toCqlInteger(), 3.toCqlInteger()).toCqlList(), value)

        value = results["IntersectDateTime"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent(
                    (value as List).elementAt(0),
                    DateTime(bigDecimalZoneOffset, 2012, 5, 10),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    value.elementAt(1),
                    DateTime(bigDecimalZoneOffset, 2014, 12, 10),
                )
                .value == true
        )
        assertEquals(2, value.count())

        value = results["IntersectTime"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent((value as List).elementAt(0), Time(15, 59, 59, 999))
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(value.elementAt(1), Time(20, 59, 59, 999)).value == true
        )
        assertEquals(2, value.count())

        value = results["LastEmpty"]!!.value
        assertNull(value)

        value = results["LastNull1"]!!.value
        assertEquals(Integer.ONE, value)

        value = results["Last1Null"]!!.value
        assertNull(value)

        value = results["Last12"]!!.value
        assertEquals(2.toCqlInteger(), value)

        value = results["LastDateTime"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 12, 10))
                .value == true
        )

        value = results["LastTime"]!!.value
        assertTrue(EquivalentEvaluator.equivalent(value, Time(20, 59, 59, 999)).value == true)

        value = results["LengthEmptyList"]!!.value
        assertEquals(Integer.ZERO, value)

        value = results["LengthNull1"]!!.value
        assertEquals(2.toCqlInteger(), value)

        value = results["Length1Null"]!!.value
        assertEquals(2.toCqlInteger(), value)

        value = results["Length12"]!!.value
        assertEquals(2.toCqlInteger(), value)

        value = results["LengthDateTime"]!!.value
        assertEquals(3.toCqlInteger(), value)

        value = results["LengthTime"]!!.value
        assertEquals(6.toCqlInteger(), value)

        value = results["LengthNullList"]!!.value
        assertEquals(Integer.ZERO, value)

        value = results["EquivalentEmptyAndEmpty"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["EquivalentABCAndABC"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["EquivalentABCAndAB"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["EquivalentABCAnd123"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["Equivalent123AndABC"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["Equivalent123AndString123"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["EquivalentDateTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["EquivalentDateTimeNull"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["EquivalentDateTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["EquivalentTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["EquivalentTimeNull"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["EquivalentTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["NotEqualEmptyAndEmpty"]!!.value
        assertNull(value)

        value = results["NotEqualABCAndABC"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["NotEqualABCAndAB"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["NotEqualABCAnd123"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["NotEqual123AndABC"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["NotEqual123AndString123"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["NotEqualDateTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["NotEqualDateTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["NotEqualTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["NotEqualTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ProperIncludesEmptyAndEmpty"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ProperIncludesListNullAndListNull"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ProperIncludes123AndEmpty"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ProperIncludes123And2"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ProperIncludes123And4"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ProperIncludesDateTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ProperIncludesDateTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ProperIncludesTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ProperIncludesTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ProperlyIncludesNullLeft"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ProperlyIncludes1And111"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ProperContainsNullRightFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ProperContainsNullRightTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ProperContainsTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ProperContainsTimeNull"]!!.value
        assertNull(value)

        value = results["ProperInNullRightFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ProperInNullRightTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ProperInTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ProperInTimeNull"]!!.value
        assertNull(value)

        value = results["ProperIncludedInEmptyAndEmpty"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ProperIncludedInListNullAndListNull"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ProperIncludedInEmptyAnd123"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ProperIncludedIn2And123"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ProperIncludedIn4And123"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ProperIncludedInDateTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ProperIncludedInDateTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ProperIncludedInTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["ProperIncludedInTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ProperlyIncludedInNullRight"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["ProperlyIncludedIn11And1"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["SingletonFromEmpty"]!!.value
        assertNull(value)

        value = results["SingletonFromListNull"]!!.value
        assertNull(value)

        value = results["SingletonFrom1"]!!.value
        assertEquals(Integer.ONE, value)

        value = results["SingletonFromDateTime"]!!.value
        assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 5, 10))
                .value == true
        )

        value = results["SingletonFromTime"]!!.value
        assertTrue(EquivalentEvaluator.equivalent(value, Time(15, 59, 59, 999)).value == true)

        value = results["SkipNull"]!!.value
        assertNull(value)

        value = results["SkipEven"]!!.value
        assertEquals(
            listOf(3.toCqlInteger(), 4.toCqlInteger(), 5.toCqlInteger()).toCqlList(),
            value,
        )

        value = results["SkipOdd"]!!.value
        assertEquals(listOf(4.toCqlInteger(), 5.toCqlInteger()).toCqlList(), value)

        value = results["SkipNone"]!!.value
        assertEquals(
            listOf(
                    Integer.ONE,
                    2.toCqlInteger(),
                    3.toCqlInteger(),
                    4.toCqlInteger(),
                    5.toCqlInteger(),
                )
                .toCqlList(),
            value,
        )

        value = results["SkipAll"]!!.value
        assertEquals(List.EMPTY_LIST, value)

        value = results["TailNull"]!!.value
        assertNull(value)

        value = results["TailEven"]!!.value
        assertEquals(
            listOf(2.toCqlInteger(), 3.toCqlInteger(), 4.toCqlInteger()).toCqlList(),
            value,
        )

        value = results["TailOdd"]!!.value
        assertEquals(
            listOf(2.toCqlInteger(), 3.toCqlInteger(), 4.toCqlInteger(), 5.toCqlInteger())
                .toCqlList(),
            value,
        )

        value = results["TailEmpty"]!!.value
        assertEquals(List.EMPTY_LIST, value)

        value = results["TailOneElement"]!!.value
        assertEquals(List.EMPTY_LIST, value)

        value = results["TakeNull"]!!.value
        assertNull(value)

        value = results["TakeNullEmpty"]!!.value
        assertEquals(List.EMPTY_LIST, value)

        value = results["TakeEmpty"]!!.value
        assertEquals(List.EMPTY_LIST, value)

        value = results["TakeEven"]!!.value
        assertEquals(listOf(Integer.ONE, 2.toCqlInteger()).toCqlList(), value)

        value = results["TakeOdd"]!!.value
        assertEquals(listOf(Integer.ONE, 2.toCqlInteger(), 3.toCqlInteger()).toCqlList(), value)

        value = results["TakeAll"]!!.value
        assertEquals(
            listOf(Integer.ONE, 2.toCqlInteger(), 3.toCqlInteger(), 4.toCqlInteger()).toCqlList(),
            value,
        )
    }

    @Test
    fun union_operator() {
        val bigDecimalZoneOffset = bigDecimalZoneOffset
        val eng = getEngine(testCompilerOptions())
        val results = eng.evaluate { library("CqlListOperatorsTest") }.onlyResultOrThrow

        var value = results["UnionEmptyAndEmpty"]!!.value as List
        assertEquals(List.EMPTY_LIST, value)

        value = results["UnionListNullAndListNull"]!!.value as List
        assertEquals(listOf(null).toCqlList(), value)

        value = results["Union123AndEmpty"]!!.value as List
        assertEquals(listOf(Integer.ONE, 2.toCqlInteger(), 3.toCqlInteger()).toCqlList(), value)

        value = results["Union123And2"]!!.value as List
        assertEquals(listOf(Integer.ONE, 2.toCqlInteger(), 3.toCqlInteger()).toCqlList(), value)

        value = results["Union123And4"]!!.value as List
        assertEquals(
            listOf(Integer.ONE, 2.toCqlInteger(), 3.toCqlInteger(), 4.toCqlInteger()).toCqlList(),
            value,
        )

        value = results["UnionDateTime"]!!.value as List
        assertTrue(
            EquivalentEvaluator.equivalent(
                    value.elementAt(0),
                    DateTime(bigDecimalZoneOffset, 2001, 9, 11),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    value.elementAt(1),
                    DateTime(bigDecimalZoneOffset, 2012, 5, 10),
                )
                .value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(
                    value.elementAt(2),
                    DateTime(bigDecimalZoneOffset, 2014, 12, 10),
                )
                .value == true
        )
        assertEquals(3, value.count())

        value = results["UnionTime"]!!.value as List
        assertTrue(
            EquivalentEvaluator.equivalent(value.elementAt(0), Time(15, 59, 59, 999)).value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(value.elementAt(1), Time(20, 59, 59, 999)).value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(value.elementAt(2), Time(12, 59, 59, 999)).value == true
        )
        assertTrue(
            EquivalentEvaluator.equivalent(value.elementAt(3), Time(10, 59, 59, 999)).value == true
        )
        assertEquals(4, value.count())

        value = results["UnionDisparateTypes"]!!.value as List
        assertEquals(4, value.count())
        assertTrue(EquivalentEvaluator.equivalent(value.elementAt(0), Integer.ONE).value == true)
        assertTrue(
            EquivalentEvaluator.equivalent(value.elementAt(1), "hi".toCqlString()).value == true
        )
        assertTrue(EquivalentEvaluator.equivalent(value.elementAt(2), Boolean.TRUE).value == true)
        assertTrue(
            EquivalentEvaluator.equivalent(value.elementAt(3), BigDecimal("1.0").toCqlDecimal())
                .value == true
        )
    }

    private fun testCompilerOptions(): CqlCompilerOptions {
        val options = defaultOptions()
        // This test suite contains some definitions that use features that are usually
        // turned off for CQL.
        options.options.remove(CqlCompilerOptions.Options.DisableListDemotion)
        options.options.remove(CqlCompilerOptions.Options.DisableListPromotion)
        return options
    }

    fun toString(errors: MutableList<CqlCompilerException>): String {
        val builder = StringBuilder()

        for (e in errors) {
            builder.append(e.toString() + System.lineSeparator())
            if (e.locator != null) {
                builder.append("at" + System.lineSeparator())
                builder.append(e.locator!!.toLocator() + System.lineSeparator())
            }
            builder.append(System.lineSeparator())
        }

        return builder.toString()
    }
}
