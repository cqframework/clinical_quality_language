package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import org.cqframework.cql.cql2elm.CqlCompilerException
import org.cqframework.cql.cql2elm.CqlCompilerException.Companion.hasErrors
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.CqlCompilerOptions.Companion.defaultOptions
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Time

internal class ListOperatorsTest : CqlTestBase() {
    @Test
    fun cql_list_test_suite_compiles() {
        val errors = ArrayList<CqlCompilerException>()
        this.getLibrary(toElmIdentifier("CqlListOperatorsTest"), errors, testCompilerOptions())
        Assertions.assertFalse(
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
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(4, 5, 1, 6, 2, 1)))

        value = results["simpleSortAsc"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 1, 2, 4, 5, 6)))

        value = results["simpleSortDesc"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(6, 5, 4, 2, 1, 1)))

        value = results["simpleSortStringAsc"]!!.value
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(
                mutableListOf<String?>(
                    "Armadillo",
                    "Wolf",
                    "aardvark",
                    "alligator",
                    "back",
                    "iguana",
                    "zebra",
                )
            ),
        )

        value = results["simpleSortStringDesc"]!!.value
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(
                mutableListOf<String?>(
                    "zebra",
                    "iguana",
                    "back",
                    "alligator",
                    "aardvark",
                    "Wolf",
                    "Armadillo",
                )
            ),
        )

        value = results["SortDatesAsc"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as MutableList<*>)[0],
                DateTime(bigDecimalZoneOffset, 2012, 1, 1),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                value[1],
                DateTime(bigDecimalZoneOffset, 2012, 1, 1, 12),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[2], DateTime(bigDecimalZoneOffset, 2012, 10, 5)) ==
                true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                value[3],
                DateTime(bigDecimalZoneOffset, 2012, 10, 5, 10),
            ) == true
        )

        value = results["SortDatesDesc"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as MutableList<*>)[0],
                DateTime(bigDecimalZoneOffset, 2012, 10, 5, 10),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[1], DateTime(bigDecimalZoneOffset, 2012, 10, 5)) ==
                true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                value[2],
                DateTime(bigDecimalZoneOffset, 2012, 1, 1, 12),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[3], DateTime(bigDecimalZoneOffset, 2012, 1, 1)) ==
                true
        )

        value = results["SortIntWithNullAsc1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf(null, 1, 2, 3)))

        value = results["SortIntWithNullAsc2"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf(null, 1, 2, 3)))

        value = results["SortIntWithNullDesc1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf(3, 2, 1, null)))

        value = results["SortIntWithNullDesc2"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf(3, 2, 1, null)))

        value = results["intList"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(3, 2, 1)))

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
        val arrListDateTime: MutableList<DateTime?> = ArrayList()
        arrListDateTime.add(DateTime(bigDecimalZoneOffset, 2016))
        arrListDateTime.add(DateTime(bigDecimalZoneOffset, 2015))
        arrListDateTime.add(DateTime(bigDecimalZoneOffset, 2010))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, arrListDateTime) == true)

        value = results["timeList"]!!.value
        val arrList: MutableList<Time?> = ArrayList()
        arrList.add(Time(15, 59, 59, 999))
        arrList.add(Time(15, 12, 59, 999))
        arrList.add(Time(15, 12, 13, 999))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, arrList) == true)

        value = results["ContainsABNullHasNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ContainsNullFirst"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ContainsABCHasA"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ContainsJan2012True"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ContainsJan2012False"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ContainsTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ContainsTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        //        value = results["ContainsNullLeft"]!!.value;
        //        assertThat(value, is(false));
        value = results["DescendentsEmptyList"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["DistinctEmptyList"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results["DistinctNullNullNull"]!!.value
        MatcherAssert.assertThat(
            value,
            Matchers.`is`(
                object : ArrayList<Any?>() {
                    init {
                        add(null)
                    }
                }
            ),
        )

        value = results["DistinctANullANull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf("a", null)))

        value = results["Distinct112233"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3)))

        value = results["Distinct123123"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3)))

        value = results["DistinctAABBCC"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<String?>("a", "b", "c")))

        value = results["DistinctABCABC"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<String?>("a", "b", "c")))

        value = results["DistinctDateTime"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as MutableList<*>)[0],
                DateTime(bigDecimalZoneOffset, 2012, 10, 5),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[1], DateTime(bigDecimalZoneOffset, 2012, 1, 1)) ==
                true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results["DistinctTime"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as MutableList<*>)[0], Time(15, 59, 59, 999)) ==
                true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[1], Time(20, 59, 59, 999)) == true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results["EqualNullNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["EqualEmptyListNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["EqualNullEmptyList"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["EqualEmptyListAndEmptyList"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["Equal12And123"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["Equal123And12"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["Equal123And123"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["EqualDateTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["EqualDateTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["EqualTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["EqualTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ExceptEmptyListAndEmptyList"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results["Except1234And23"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 4)))

        value = results["Except23And1234"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results["ExceptDateTimeList"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as MutableList<*>)[0],
                DateTime(bigDecimalZoneOffset, 2012, 5, 10),
            ) == true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(1))

        value = results["ExceptTimeList"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as MutableList<*>)[0], Time(15, 59, 59, 999)) ==
                true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(1))

        value = results["ExceptNullRight"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 4)))

        value = results["ExistsEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ExistsListNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["Exists1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["Exists12"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ExistsDateTime"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ExistsTime"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ExistsNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["FlattenEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results["FlattenListNullAndNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>(null, null)))

        value = results["FlattenNullAndListNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>(null, null)))

        value = results["FlattenList12And34"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3, 4)))

        value = results["FlattenDateTime"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as MutableList<*>)[0],
                DateTime(bigDecimalZoneOffset, 2012, 5, 10),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                value[1],
                DateTime(bigDecimalZoneOffset, 2014, 12, 10),
            ) == true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results["FlattenTime"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as MutableList<*>)[0], Time(15, 59, 59, 999)) ==
                true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[1], Time(20, 59, 59, 999)) == true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results["FirstEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["FirstNull1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["First1Null"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results["First12"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results["FirstDateTime"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 5, 10)) ==
                true
        )

        value = results["FirstTime"]!!.value
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(15, 59, 59, 999)) == true)

        value = results["InNullEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["InNullAnd1Null"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["In1Null"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["In1And12"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["In3And12"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["InDateTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["InDateTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["InTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["InTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IncludesEmptyAndEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IncludesListNullAndListNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["Includes123AndEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["Includes123And2"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["Includes123And4"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IncludesDateTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IncludesDateTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IncludesTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IncludesTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IncludesNullLeft"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        // TODO: fix test - going to ContainsEvaluator
        //        value = results["IncludesNullRight"]!!.value;
        //        assertThat(value, is(true));
        value = results["IncludedInEmptyAndEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IncludedInListNullAndListNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IncludedInEmptyAnd123"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IncludedIn2And123"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IncludedIn4And123"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IncludedInDateTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IncludedInDateTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IncludedInTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IncludedInTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        // TODO: fix test - going to InEvaluator
        //        value = results["IncludedInNullLeft"]!!.value;
        //        assertThat(value, is(true));
        value = results["IncludedInNullRight"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IndexerNull1List"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["Indexer0Of12"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results["Indexer1Of12"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        value = results["Indexer2Of12"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IndexerNeg1Of12"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IndexerDateTime"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 5, 10)) ==
                true
        )

        value = results["IndexerTime"]!!.value
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(15, 59, 59, 999)) == true)

        value = results["IndexOfEmptyNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IndexOfNullEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IndexOfNullIn1Null"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IndexOf1In12"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(0))

        value = results["IndexOf2In12"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results["IndexOf3In12"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(-1))

        value = results["IndexOfDateTime"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        value = results["IndexOfTime"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results["IntersectEmptyListAndEmptyList"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results["Intersect1234And23"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(2, 3)))

        value = results["Intersect23And1234"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(2, 3)))

        value = results["IntersectDateTime"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as MutableList<*>)[0],
                DateTime(bigDecimalZoneOffset, 2012, 5, 10),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                value[1],
                DateTime(bigDecimalZoneOffset, 2014, 12, 10),
            ) == true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results["IntersectTime"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as MutableList<*>)[0], Time(15, 59, 59, 999)) ==
                true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[1], Time(20, 59, 59, 999)) == true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results["LastEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["LastNull1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results["Last1Null"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["Last12"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        value = results["LastDateTime"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 12, 10)) ==
                true
        )

        value = results["LastTime"]!!.value
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(20, 59, 59, 999)) == true)

        value = results["LengthEmptyList"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(0))

        value = results["LengthNull1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        value = results["Length1Null"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        value = results["Length12"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        value = results["LengthDateTime"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(3))

        value = results["LengthTime"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(6))

        value = results["LengthNullList"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(0))

        value = results["EquivalentEmptyAndEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["EquivalentABCAndABC"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["EquivalentABCAndAB"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["EquivalentABCAnd123"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["Equivalent123AndABC"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["Equivalent123AndString123"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["EquivalentDateTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["EquivalentDateTimeNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["EquivalentDateTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["EquivalentTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["EquivalentTimeNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["EquivalentTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["NotEqualEmptyAndEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["NotEqualABCAndABC"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["NotEqualABCAndAB"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["NotEqualABCAnd123"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["NotEqual123AndABC"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["NotEqual123AndString123"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["NotEqualDateTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["NotEqualDateTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["NotEqualTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["NotEqualTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ProperIncludesEmptyAndEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ProperIncludesListNullAndListNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ProperIncludes123AndEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ProperIncludes123And2"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ProperIncludes123And4"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ProperIncludesDateTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ProperIncludesDateTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ProperIncludesTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ProperIncludesTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ProperlyIncludesNullLeft"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ProperlyIncludes1And111"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ProperContainsNullRightFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ProperContainsNullRightTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ProperContainsTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ProperContainsTimeNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["ProperInNullRightFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ProperInNullRightTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ProperInTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ProperInTimeNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["ProperIncludedInEmptyAndEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ProperIncludedInListNullAndListNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ProperIncludedInEmptyAnd123"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ProperIncludedIn2And123"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ProperIncludedIn4And123"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ProperIncludedInDateTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ProperIncludedInDateTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ProperIncludedInTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["ProperIncludedInTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ProperlyIncludedInNullRight"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["ProperlyIncludedIn11And1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["SingletonFromEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["SingletonFromListNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["SingletonFrom1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results["SingletonFromDateTime"]!!.value
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 5, 10)) ==
                true
        )

        value = results["SingletonFromTime"]!!.value
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(15, 59, 59, 999)) == true)

        value = results["SkipNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["SkipEven"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(3, 4, 5)))

        value = results["SkipOdd"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(4, 5)))

        value = results["SkipNone"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3, 4, 5)))

        value = results["SkipAll"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results["TailNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["TailEven"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(2, 3, 4)))

        value = results["TailOdd"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(2, 3, 4, 5)))

        value = results["TailEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results["TailOneElement"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results["TakeNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["TakeNullEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results["TakeEmpty"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results["TakeEven"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2)))

        value = results["TakeOdd"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3)))

        value = results["TakeAll"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3, 4)))
    }

    @Test
    fun union_operator() {
        val bigDecimalZoneOffset = bigDecimalZoneOffset
        val eng = getEngine(testCompilerOptions())
        val results = eng.evaluate { library("CqlListOperatorsTest") }.onlyResultOrThrow

        var value = results["UnionEmptyAndEmpty"]!!.value as MutableList<*>
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = (results["UnionListNullAndListNull"]!!.value as MutableList<*>?)!!
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf(null)))

        value = (results["Union123AndEmpty"]!!.value as MutableList<*>?)!!
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3)))

        value = (results["Union123And2"]!!.value as MutableList<*>?)!!
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3)))

        value = (results["Union123And4"]!!.value as MutableList<*>?)!!
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3, 4)))

        value = (results["UnionDateTime"]!!.value as MutableList<*>?)!!
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[0], DateTime(bigDecimalZoneOffset, 2001, 9, 11)) ==
                true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[1], DateTime(bigDecimalZoneOffset, 2012, 5, 10)) ==
                true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                value[2],
                DateTime(bigDecimalZoneOffset, 2014, 12, 10),
            ) == true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(3))

        value = (results["UnionTime"]!!.value as MutableList<*>?)!!
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[0], Time(15, 59, 59, 999)) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[1], Time(20, 59, 59, 999)) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[2], Time(12, 59, 59, 999)) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[3], Time(10, 59, 59, 999)) == true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(4))

        value = (results["UnionDisparateTypes"]!!.value as MutableList<*>?)!!
        MatcherAssert.assertThat(value.size, Matchers.`is`(4))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[0], 1) == true)
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[1], "hi") == true)
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[2], true) == true)
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[3], BigDecimal("1.0")) == true)
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
