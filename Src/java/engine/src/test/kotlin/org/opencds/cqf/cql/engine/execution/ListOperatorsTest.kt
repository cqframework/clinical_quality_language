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

        val results = eng.evaluate(toElmIdentifier("CqlListOperatorsTest"))

        var value = results.forExpression("simpleList").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(4, 5, 1, 6, 2, 1)))

        value = results.forExpression("simpleSortAsc").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 1, 2, 4, 5, 6)))

        value = results.forExpression("simpleSortDesc").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(6, 5, 4, 2, 1, 1)))

        value = results.forExpression("simpleSortStringAsc").value()
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

        value = results.forExpression("simpleSortStringDesc").value()
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

        value = results.forExpression("SortDatesAsc").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as MutableList<*>)[0],
                DateTime(bigDecimalZoneOffset, 2012, 1, 1),
            )
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[1], DateTime(bigDecimalZoneOffset, 2012, 1, 1, 12))
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[2], DateTime(bigDecimalZoneOffset, 2012, 10, 5))
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                value[3],
                DateTime(bigDecimalZoneOffset, 2012, 10, 5, 10),
            )
        )

        value = results.forExpression("SortDatesDesc").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as MutableList<*>)[0],
                DateTime(bigDecimalZoneOffset, 2012, 10, 5, 10),
            )
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[1], DateTime(bigDecimalZoneOffset, 2012, 10, 5))
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[2], DateTime(bigDecimalZoneOffset, 2012, 1, 1, 12))
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[3], DateTime(bigDecimalZoneOffset, 2012, 1, 1))
        )

        value = results.forExpression("SortIntWithNullAsc1").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf(null, 1, 2, 3)))

        value = results.forExpression("SortIntWithNullAsc2").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf(null, 1, 2, 3)))

        value = results.forExpression("SortIntWithNullDesc1").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf(3, 2, 1, null)))

        value = results.forExpression("SortIntWithNullDesc2").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf(3, 2, 1, null)))

        value = results.forExpression("intList").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(3, 2, 1)))

        //        value = results.forExpression("decimalList").value();
        //        assertThat(value, is(Arrays.asList(new BigDecimal(3.8).setScale(1,
        // RoundingMode.HALF_EVEN), new
        // BigDecimal(2.4).setScale(1, RoundingMode.HALF_EVEN), new BigDecimal(1.9).setScale(1,
        // RoundingMode.HALF_EVEN))));

        //    value = results.forExpression("quantityList").value();
        //    assertThat(value, is(Arrays.asList(new Quantity().withValue(new
        // BigDecimal("19.99")).withUnit("lbs") ,
        // new Quantity().withValue(new BigDecimal("17.33")).withUnit("lbs") ,  new
        // Quantity().withValue(new
        // BigDecimal("10.66")).withUnit("lbs") )));
        value = results.forExpression("dateTimeList").value()
        val arrListDateTime: MutableList<DateTime?> = ArrayList()
        arrListDateTime.add(DateTime(bigDecimalZoneOffset, 2016))
        arrListDateTime.add(DateTime(bigDecimalZoneOffset, 2015))
        arrListDateTime.add(DateTime(bigDecimalZoneOffset, 2010))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, arrListDateTime))

        value = results.forExpression("timeList").value()
        val arrList: MutableList<Time?> = ArrayList()
        arrList.add(Time(15, 59, 59, 999))
        arrList.add(Time(15, 12, 59, 999))
        arrList.add(Time(15, 12, 13, 999))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, arrList))

        value = results.forExpression("ContainsABNullHasNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ContainsNullFirst").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ContainsABCHasA").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ContainsJan2012True").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ContainsJan2012False").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ContainsTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ContainsTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        //        value = results.forExpression("ContainsNullLeft").value();
        //        assertThat(value, is(false));
        value = results.forExpression("DescendentsEmptyList").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("DistinctEmptyList").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results.forExpression("DistinctNullNullNull").value()
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

        value = results.forExpression("DistinctANullANull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf("a", null)))

        value = results.forExpression("Distinct112233").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3)))

        value = results.forExpression("Distinct123123").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3)))

        value = results.forExpression("DistinctAABBCC").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<String?>("a", "b", "c")))

        value = results.forExpression("DistinctABCABC").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<String?>("a", "b", "c")))

        value = results.forExpression("DistinctDateTime").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as MutableList<*>)[0],
                DateTime(bigDecimalZoneOffset, 2012, 10, 5),
            )
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[1], DateTime(bigDecimalZoneOffset, 2012, 1, 1))
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results.forExpression("DistinctTime").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as MutableList<*>)[0], Time(15, 59, 59, 999))
        )
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[1], Time(20, 59, 59, 999)))
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results.forExpression("EqualNullNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("EqualEmptyListNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("EqualNullEmptyList").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("EqualEmptyListAndEmptyList").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("Equal12And123").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("Equal123And12").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("Equal123And123").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("EqualDateTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("EqualDateTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("EqualTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("EqualTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ExceptEmptyListAndEmptyList").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results.forExpression("Except1234And23").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 4)))

        value = results.forExpression("Except23And1234").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results.forExpression("ExceptDateTimeList").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as MutableList<*>)[0],
                DateTime(bigDecimalZoneOffset, 2012, 5, 10),
            )
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(1))

        value = results.forExpression("ExceptTimeList").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as MutableList<*>)[0], Time(15, 59, 59, 999))
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(1))

        value = results.forExpression("ExceptNullRight").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 4)))

        value = results.forExpression("ExistsEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ExistsListNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("Exists1").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("Exists12").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ExistsDateTime").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ExistsTime").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ExistsNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("FlattenEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results.forExpression("FlattenListNullAndNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>(null, null)))

        value = results.forExpression("FlattenNullAndListNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>(null, null)))

        value = results.forExpression("FlattenList12And34").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3, 4)))

        value = results.forExpression("FlattenDateTime").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as MutableList<*>)[0],
                DateTime(bigDecimalZoneOffset, 2012, 5, 10),
            )
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[1], DateTime(bigDecimalZoneOffset, 2014, 12, 10))
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results.forExpression("FlattenTime").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as MutableList<*>)[0], Time(15, 59, 59, 999))
        )
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[1], Time(20, 59, 59, 999)))
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results.forExpression("FirstEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("FirstNull1").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("First1Null").value()
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results.forExpression("First12").value()
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results.forExpression("FirstDateTime").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 5, 10))
        )

        value = results.forExpression("FirstTime").value()
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(15, 59, 59, 999)))

        value = results.forExpression("InNullEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("InNullAnd1Null").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("In1Null").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("In1And12").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("In3And12").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("InDateTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("InDateTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("InTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("InTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IncludesEmptyAndEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IncludesListNullAndListNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("Includes123AndEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("Includes123And2").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("Includes123And4").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IncludesDateTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IncludesDateTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IncludesTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IncludesTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IncludesNullLeft").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        // TODO: fix test - going to ContainsEvaluator
        //        value = results.forExpression("IncludesNullRight").value();
        //        assertThat(value, is(true));
        value = results.forExpression("IncludedInEmptyAndEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IncludedInListNullAndListNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IncludedInEmptyAnd123").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IncludedIn2And123").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IncludedIn4And123").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IncludedInDateTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IncludedInDateTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IncludedInTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IncludedInTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        // TODO: fix test - going to InEvaluator
        //        value = results.forExpression("IncludedInNullLeft").value();
        //        assertThat(value, is(true));
        value = results.forExpression("IncludedInNullRight").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IndexerNull1List").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("Indexer0Of12").value()
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results.forExpression("Indexer1Of12").value()
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        value = results.forExpression("Indexer2Of12").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IndexerNeg1Of12").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IndexerDateTime").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 5, 10))
        )

        value = results.forExpression("IndexerTime").value()
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(15, 59, 59, 999)))

        value = results.forExpression("IndexOfEmptyNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IndexOfNullEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IndexOfNullIn1Null").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IndexOf1In12").value()
        MatcherAssert.assertThat(value, Matchers.`is`(0))

        value = results.forExpression("IndexOf2In12").value()
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results.forExpression("IndexOf3In12").value()
        MatcherAssert.assertThat(value, Matchers.`is`(-1))

        value = results.forExpression("IndexOfDateTime").value()
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        value = results.forExpression("IndexOfTime").value()
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results.forExpression("IntersectEmptyListAndEmptyList").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results.forExpression("Intersect1234And23").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(2, 3)))

        value = results.forExpression("Intersect23And1234").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(2, 3)))

        value = results.forExpression("IntersectDateTime").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as MutableList<*>)[0],
                DateTime(bigDecimalZoneOffset, 2012, 5, 10),
            )
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[1], DateTime(bigDecimalZoneOffset, 2014, 12, 10))
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results.forExpression("IntersectTime").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as MutableList<*>)[0], Time(15, 59, 59, 999))
        )
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[1], Time(20, 59, 59, 999)))
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results.forExpression("LastEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("LastNull1").value()
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results.forExpression("Last1Null").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("Last12").value()
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        value = results.forExpression("LastDateTime").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2014, 12, 10))
        )

        value = results.forExpression("LastTime").value()
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(20, 59, 59, 999)))

        value = results.forExpression("LengthEmptyList").value()
        MatcherAssert.assertThat(value, Matchers.`is`(0))

        value = results.forExpression("LengthNull1").value()
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        value = results.forExpression("Length1Null").value()
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        value = results.forExpression("Length12").value()
        MatcherAssert.assertThat(value, Matchers.`is`(2))

        value = results.forExpression("LengthDateTime").value()
        MatcherAssert.assertThat(value, Matchers.`is`(3))

        value = results.forExpression("LengthTime").value()
        MatcherAssert.assertThat(value, Matchers.`is`(6))

        value = results.forExpression("LengthNullList").value()
        MatcherAssert.assertThat(value, Matchers.`is`(0))

        value = results.forExpression("EquivalentEmptyAndEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("EquivalentABCAndABC").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("EquivalentABCAndAB").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("EquivalentABCAnd123").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("Equivalent123AndABC").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("Equivalent123AndString123").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("EquivalentDateTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("EquivalentDateTimeNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("EquivalentDateTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("EquivalentTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("EquivalentTimeNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("EquivalentTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("NotEqualEmptyAndEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("NotEqualABCAndABC").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("NotEqualABCAndAB").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("NotEqualABCAnd123").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("NotEqual123AndABC").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("NotEqual123AndString123").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("NotEqualDateTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("NotEqualDateTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("NotEqualTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("NotEqualTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ProperIncludesEmptyAndEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ProperIncludesListNullAndListNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ProperIncludes123AndEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ProperIncludes123And2").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ProperIncludes123And4").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ProperIncludesDateTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ProperIncludesDateTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ProperIncludesTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ProperIncludesTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ProperlyIncludesNullLeft").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ProperlyIncludes1And111").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ProperContainsNullRightFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ProperContainsNullRightTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ProperContainsTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ProperContainsTimeNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("ProperInNullRightFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ProperInNullRightTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ProperInTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ProperInTimeNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("ProperIncludedInEmptyAndEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ProperIncludedInListNullAndListNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ProperIncludedInEmptyAnd123").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ProperIncludedIn2And123").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ProperIncludedIn4And123").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ProperIncludedInDateTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ProperIncludedInDateTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ProperIncludedInTimeTrue").value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("ProperIncludedInTimeFalse").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ProperlyIncludedInNullRight").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("ProperlyIncludedIn11And1").value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("SingletonFromEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("SingletonFromListNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("SingletonFrom1").value()
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results.forExpression("SingletonFromDateTime").value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value, DateTime(bigDecimalZoneOffset, 2012, 5, 10))
        )

        value = results.forExpression("SingletonFromTime").value()
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(15, 59, 59, 999)))

        value = results.forExpression("SkipNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("SkipEven").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(3, 4, 5)))

        value = results.forExpression("SkipOdd").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(4, 5)))

        value = results.forExpression("SkipNone").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3, 4, 5)))

        value = results.forExpression("SkipAll").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results.forExpression("TailNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("TailEven").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(2, 3, 4)))

        value = results.forExpression("TailOdd").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(2, 3, 4, 5)))

        value = results.forExpression("TailEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results.forExpression("TailOneElement").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results.forExpression("TakeNull").value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("TakeNullEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results.forExpression("TakeEmpty").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = results.forExpression("TakeEven").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2)))

        value = results.forExpression("TakeOdd").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3)))

        value = results.forExpression("TakeAll").value()
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3, 4)))
    }

    @Test
    fun union_operator() {
        val bigDecimalZoneOffset = bigDecimalZoneOffset
        val eng = getEngine(testCompilerOptions())
        val results = eng.evaluate(toElmIdentifier("CqlListOperatorsTest"))

        var value = results.forExpression("UnionEmptyAndEmpty").value() as MutableList<*>
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Any?>()))

        value = (results.forExpression("UnionListNullAndListNull").value() as MutableList<*>?)!!
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf(null)))

        value = (results.forExpression("Union123AndEmpty").value() as MutableList<*>?)!!
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3)))

        value = (results.forExpression("Union123And2").value() as MutableList<*>?)!!
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3)))

        value = (results.forExpression("Union123And4").value() as MutableList<*>?)!!
        MatcherAssert.assertThat(value, Matchers.`is`(mutableListOf<Int?>(1, 2, 3, 4)))

        value = (results.forExpression("UnionDateTime").value() as MutableList<*>?)!!
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[0], DateTime(bigDecimalZoneOffset, 2001, 9, 11))
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[1], DateTime(bigDecimalZoneOffset, 2012, 5, 10))
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value[2], DateTime(bigDecimalZoneOffset, 2014, 12, 10))
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(3))

        value = (results.forExpression("UnionTime").value() as MutableList<*>?)!!
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[0], Time(15, 59, 59, 999)))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[1], Time(20, 59, 59, 999)))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[2], Time(12, 59, 59, 999)))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[3], Time(10, 59, 59, 999)))
        MatcherAssert.assertThat(value.size, Matchers.`is`(4))

        value = (results.forExpression("UnionDisparateTypes").value() as MutableList<*>?)!!
        MatcherAssert.assertThat(value.size, Matchers.`is`(4))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[0], 1))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[1], "hi"))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[2], true))
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value[3], BigDecimal("1.0")))
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
