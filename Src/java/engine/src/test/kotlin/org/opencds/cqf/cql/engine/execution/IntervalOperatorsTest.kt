package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator.equal
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator.equivalent
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Time

internal class IntervalOperatorsTest : CqlTestBase() {
    //    @Test
    //    public void test_fail() {
    //
    //        Set<String> set = new HashSet<>();
    //        set.add("InvalidIntegerIntervalA");
    //
    //        try {
    //            results = engine.evaluate(toElmIdentifier("CqlIntervalOperatorsTest"), set, null,
    // null, null,
    // null);
    //            Object value = results["InvalidIntegerIntervalA"].value;
    //            Assertions.fail();
    //
    //
    //        } catch (Exception e) {
    //
    //        }
    //
    //    }
    @Test
    fun all_interval_operators() {
        val bigDecimalZoneOffset = bigDecimalZoneOffset

        val results = engine.evaluate { library("CqlIntervalOperatorsTest") }.onlyResultOrThrow
        var value = results["IntegerIntervalAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IntegerIntervalPointAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalPointAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IntegerIntervalAfterPointTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalAfterPointFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalPointAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalPointAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalAfterPointTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalAfterPointFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalPointAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalPointAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalAfterPointTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalAfterPointFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestBeforeNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IntegerIntervalBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IntegerIntervalBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalPointBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalPointBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IntegerIntervalBeforePointTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalBeforePointFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalPointBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalPointBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalBeforePointTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalBeforePointFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalPointBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalPointBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalBeforePointTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalBeforePointFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestCollapseNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IntegerIntervalCollapse"]!!.value
        Assertions.assertTrue(equal((value as List<*>)[0], Interval(1, true, 10, true)) == true)
        Assertions.assertTrue(equal(value[1], Interval(12, true, 19, true)) == true)

        value = results["IntegerIntervalCollapse2"]!!.value
        Assertions.assertTrue(equal((value as List<*>)[0], Interval(1, true, 19, true)) == true)

        value = results["IntegerIntervalCollapse3"]!!.value
        Assertions.assertTrue(equal((value as List<*>)[0], Interval(4, true, 8, true)) == true)

        value = results["IntegerIntervalCollapse4"]!!.value
        Assertions.assertTrue(equal((value as List<*>)[0], Interval(4, true, 6, true)) == true)
        Assertions.assertTrue(equal(value[1], Interval(8, true, 10, true)) == true)

        value = results["DecimalIntervalCollapse"]!!.value
        Assertions.assertTrue(
            equal(
                (value as List<*>)[0],
                Interval(BigDecimal("1.0"), true, BigDecimal("10.0"), true),
            ) == true
        )
        Assertions.assertTrue(
            equal(value[1], Interval(BigDecimal("12.0"), true, BigDecimal("19.0"), true)) == true
        )

        value = results["DecimalIntervalCollapse2"]!!.value
        Assertions.assertTrue(
            equal(
                (value as List<*>)[0],
                Interval(BigDecimal("4.0"), true, BigDecimal("8.0"), true),
            ) == true
        )

        value = results["QuantityIntervalCollapse"]!!.value
        Assertions.assertTrue(
            equal(
                (value as List<*>)[0],
                Interval(
                    Quantity().withValue(BigDecimal("1.0")).withUnit("g"),
                    true,
                    Quantity().withValue(BigDecimal("10.0")).withUnit("g"),
                    true,
                ),
            ) == true
        )
        Assertions.assertTrue(
            equal(
                value[1],
                Interval(
                    Quantity().withValue(BigDecimal("12.0")).withUnit("g"),
                    true,
                    Quantity().withValue(BigDecimal("19.0")).withUnit("g"),
                    true,
                ),
            ) == true
        )

        value = results["DateTimeCollapse"]!!.value
        Assertions.assertTrue(
            equivalent(
                ((value as List<*>)[0] as Interval).start,
                DateTime(bigDecimalZoneOffset, 2012, 1, 1),
            ) == true
        )
        Assertions.assertTrue(
            equivalent((value[0] as Interval).end, DateTime(bigDecimalZoneOffset, 2012, 1, 25)) ==
                true
        )
        Assertions.assertTrue(
            equivalent((value[1] as Interval).start, DateTime(bigDecimalZoneOffset, 2012, 5, 10)) ==
                true
        )
        Assertions.assertTrue(
            equivalent((value[1] as Interval).end, DateTime(bigDecimalZoneOffset, 2012, 5, 30)) ==
                true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results["DateTimeCollapse2"]!!.value

        Assertions.assertTrue(
            equivalent(
                ((value as List<*>)[0] as Interval).start,
                DateTime(bigDecimalZoneOffset, 2012, 1, 1),
            ) == true
        )
        Assertions.assertTrue(
            equivalent((value[0] as Interval).end, DateTime(bigDecimalZoneOffset, 2012, 5, 25)) ==
                true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(1))

        value = results["DateTimeCollapse3"]!!.value
        Assertions.assertTrue(
            equivalent(
                ((value as List<*>)[0] as Interval).start,
                DateTime(bigDecimalZoneOffset, 2018, 1, 1),
            ) == true
        )
        Assertions.assertTrue(
            equivalent((value[0] as Interval).end, DateTime(bigDecimalZoneOffset, 2018, 8, 28)) ==
                true
        )
        Assertions.assertTrue(
            equivalent((value[1] as Interval).start, DateTime(bigDecimalZoneOffset, 2018, 8, 30)) ==
                true
        )
        Assertions.assertTrue(
            equivalent((value[1] as Interval).end, DateTime(bigDecimalZoneOffset, 2018, 10, 15)) ==
                true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results["DateTimeCollapse4"]!!.value
        Assertions.assertTrue(
            equivalent(((value as List<*>)[0] as Interval).start, Date(2018, 1, 1)) == true
        )
        Assertions.assertTrue(equivalent((value[0] as Interval).end, Date(2018, 8, 28)) == true)
        Assertions.assertTrue(equivalent((value[1] as Interval).start, Date(2018, 8, 30)) == true)
        Assertions.assertTrue(equivalent((value[1] as Interval).end, Date(2018, 10, 15)) == true)

        value = results["DateTimeCollapse5"]!!.value
        println(value)
        Assertions.assertTrue(
            equivalent(((value as MutableList<*>)[0] as Interval).start, Date(2018, 1, 1)) == true
        )
        Assertions.assertTrue(equivalent((value[0] as Interval).end, Date(2018, 8, 28)) == true)
        Assertions.assertTrue(equivalent((value[1] as Interval).start, Date(2018, 8, 30)) == true)
        Assertions.assertTrue(equivalent((value[1] as Interval).end, Date(2018, 10, 15)) == true)

        value = results["DateTimeCollapse6"]!!.value
        Assertions.assertTrue(
            equivalent(((value as List<*>)[0] as Interval).start, Date(2018, 1, 1)) == true
        )
        Assertions.assertTrue(equivalent((value[0] as Interval).end, Date(2018, 10, 15)) == true)

        value = results["TimeCollapse"]!!.value
        Assertions.assertTrue(
            equivalent(((value as List<*>)[0] as Interval).start, Time(1, 59, 59, 999)) == true
        )
        Assertions.assertTrue(equivalent((value[0] as Interval).end, Time(15, 59, 59, 999)) == true)
        Assertions.assertTrue(
            equivalent((value[1] as Interval).start, Time(17, 59, 59, 999)) == true
        )
        Assertions.assertTrue(equivalent((value[1] as Interval).end, Time(22, 59, 59, 999)) == true)
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results["TimeCollapse2"]!!.value
        Assertions.assertTrue(
            equivalent(((value as List<*>)[0] as Interval).start, Time(1, 59, 59, 999)) == true
        )
        Assertions.assertTrue(equivalent((value[0] as Interval).end, Time(15, 59, 59, 999)) == true)
        MatcherAssert.assertThat(value.size, Matchers.`is`(1))
        value = results["TestContainsNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["TestNullElement1"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestNullElement2"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestNullElementTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalContainsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalContainsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalContainsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalContainsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalContainsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalContainsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        //        value = results["DateTimeContainsNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["DateTimeContainsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeContainsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        //        value = results["TimeContainsNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["TimeContainsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeContainsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IntegerIntervalEnd"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(10))

        value = results["DecimalIntervalEnd"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("10.0")))

        value = results["QuantityIntervalEnd"]!!.value
        Assertions.assertTrue(
            equal(value, Quantity().withValue(BigDecimal("10.0")).withUnit("g")) == true
        )

        value = results["DateTimeIntervalEnd"]!!.value
        Assertions.assertTrue(
            equivalent(value, DateTime(bigDecimalZoneOffset, 2016, 5, 2, 0, 0, 0, 0)) == true
        )

        value = results["TimeIntervalEnd"]!!.value
        Assertions.assertTrue(equivalent(value, Time(23, 59, 59, 599)) == true)

        //        value = results["TestEndsNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["IntegerIntervalEndsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalEndsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalEndsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalEndsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalEndsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalEndsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeEndsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        //        value = results["DateTimeEndsNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["DateTimeEndsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeEndsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeEndsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        //        value = results["TestEqualNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["IntegerIntervalEqualTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalEqualFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalEqualTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalEqualFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalEqualTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalEqualFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeEqualTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeEqualFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeEqualTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeEqualFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        //        value = results["TestExceptNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["IntegerIntervalExcept1to3"]!!.value
        Assertions.assertTrue(equal(value, Interval(1, true, 3, true)) == true)

        value = results["IntegerIntervalExcept4to6"]!!.value
        Assertions.assertTrue(equal(value, Interval(-4, false, 6, false)) == true)

        value = results["IntegerIntervalExceptNullOutNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IntegerIntervalExceptNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["DecimalIntervalExcept1to3"]!!.value
        Assertions.assertTrue(
            equal(value, Interval(BigDecimal("1.0"), true, BigDecimal("3.99999999"), true)) == true
        )

        value = results["DecimalIntervalExceptNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["QuantityIntervalExcept1to4"]!!.value
        Assertions.assertTrue(
            equal(
                value,
                Interval(
                    Quantity().withValue(BigDecimal("1.0")).withUnit("g"),
                    true,
                    Quantity().withValue(BigDecimal("4.99999999")).withUnit("g"),
                    true,
                ),
            ) == true
        )

        value = results["Except12"]!!.value
        Assertions.assertTrue(equal(value, Interval(1, true, 2, true)) == true)

        value = results["ExceptDateTimeInterval"]!!.value
        Assertions.assertTrue(
            equivalent((value as Interval).start, DateTime(bigDecimalZoneOffset, 2012, 1, 5)) ==
                true
        )
        Assertions.assertTrue(
            equivalent(value.end, DateTime(bigDecimalZoneOffset, 2012, 1, 6)) == true
        )

        value = results["ExceptDateTime2"]!!.value
        Assertions.assertTrue(
            equivalent((value as Interval).start, DateTime(bigDecimalZoneOffset, 2012, 1, 13)) ==
                true
        )
        Assertions.assertTrue(
            equivalent(value.end, DateTime(bigDecimalZoneOffset, 2012, 1, 16)) == true
        )

        value = results["ExceptTimeInterval"]!!.value
        Assertions.assertTrue(equivalent((value as Interval).start, Time(5, 59, 59, 999)) == true)
        Assertions.assertTrue(equivalent(value.end, Time(8, 59, 59, 998)) == true)

        value = results["ExceptTime2"]!!.value
        Assertions.assertTrue(equivalent((value as Interval).start, Time(11, 0, 0, 0)) == true)
        Assertions.assertTrue(equivalent(value.end, Time(11, 59, 59, 999)) == true)

        value = results["TestInNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestInNullEnd"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TestNullIn"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IntegerIntervalInTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalInFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalInTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalInFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalInTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalInFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeInTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        //        value = results["DateTimeInNullPrecision"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["DateTimeInFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeInNullTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeInTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeInFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeInNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["Issue32Interval"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))
        value = results["TestIncludesNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IntegerIntervalIncludesTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalIncludesFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalIncludesTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalIncludesFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalIncludesTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalIncludesFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeIncludesTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeIncludesFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeIncludesTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeIncludesFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))
        value = results["IntegerIntervalIncludedInTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalIncludedInFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalIncludedInTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalIncludedInFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalIncludedInTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalIncludedInFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeIncludedInTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeIncludedInFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeIncludedInTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeIncludedInFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeIncludedInNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["DateTimeIncludedInPrecisionTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeIncludedInPrecisionNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["TestIntersectNullRightStart"]!!.value
        // Because of how nulls work, equivalence, not equality, is the relevant test here (equality
        // just gives null).
        Assertions.assertTrue(equivalent(value, Interval(null, false, 5, true)) == true)

        value = results["TestIntersectNullRightEnd"]!!.value
        Assertions.assertTrue(equivalent(value, Interval(5, true, null, false)) == true)

        value = results["TestIntersectNullLeftStart"]!!.value
        Assertions.assertTrue(equivalent(value, Interval(null, false, 5, true)) == true)

        value = results["TestIntersectNullLeftEnd"]!!.value
        Assertions.assertTrue(equivalent(value, Interval(5, true, null, false)) == true)

        value = results["TestIntersectNull1"]!!.value
        Assertions.assertTrue((value as Boolean?)!!)

        value = results["TestIntersectNull2"]!!.value
        Assertions.assertTrue((value as Boolean?)!!)

        value = results["TestIntersectNull3"]!!.value
        Assertions.assertFalse((value as Boolean?)!!)

        value = results["TestIntersectNull4"]!!.value
        Assertions.assertFalse((value as Boolean?)!!)

        value = results["IntegerIntervalIntersectTest4to10"]!!.value
        Assertions.assertTrue(equal(value, Interval(4, true, 10, true)) == true)

        value = results["IntegerIntervalIntersectTestNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["DecimalIntervalIntersectTest4to10"]!!.value
        Assertions.assertTrue(
            equal(value, Interval(BigDecimal("4.0"), true, BigDecimal("10.0"), true)) == true
        )

        value = results["IntegerIntervalIntersectTestNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["QuantityIntervalIntersectTest5to10"]!!.value
        Assertions.assertTrue(
            equal(
                value,
                Interval(
                    Quantity().withValue(BigDecimal("5.0")).withUnit("g"),
                    true,
                    Quantity().withValue(BigDecimal("10.0")).withUnit("g"),
                    true,
                ),
            ) == true
        )

        value = results["QuantityIntervalIntersectTestNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["DateTimeIntersect"]!!.value
        Assertions.assertTrue(
            equivalent((value as Interval).start, DateTime(bigDecimalZoneOffset, 2012, 1, 7)) ==
                true
        )
        Assertions.assertTrue(
            equivalent(value.end, DateTime(bigDecimalZoneOffset, 2012, 1, 10)) == true
        )

        value = results["TimeIntersect"]!!.value
        Assertions.assertTrue(equivalent((value as Interval).start, Time(4, 59, 59, 999)) == true)
        Assertions.assertTrue(equivalent(value.end, Time(6, 59, 59, 999)) == true)

        value = results["IntegerIntervalEquivalentTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalEquivalentFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalEquivalentTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalEquivalentFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalEquivalentTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalEquivalentFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeEquivalentTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeEquivalentFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeEquivalentTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeEquivalentFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestMeetsNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IntegerIntervalMeetsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalMeetsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalMeetsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalMeetsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalMeetsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalMeetsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeMeetsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeMeetsNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeMeetsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeMeetsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeMeetsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestMeetsBeforeNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IntegerIntervalMeetsBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalMeetsBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalMeetsBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalMeetsBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalMeetsBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalMeetsBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeMeetsBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeMeetsBeforeNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeMeetsBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeMeetsBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeMeetsBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestMeetsAfterNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IntegerIntervalMeetsAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalMeetsAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalMeetsAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalMeetsAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalMeetsAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalMeetsAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeMeetsAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeMeetsAfterNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeMeetsAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeMeetsAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeMeetsAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["IntegerIntervalNotEqualTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalNotEqualFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalNotEqualTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalNotEqualFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalNotEqualTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalNotEqualFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeNotEqualTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeNotEqualFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeNotEqualTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeNotEqualFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestOnOrAfterNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["TestOnOrAfterDateTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TestOnOrAfterDateFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestOnOrAfterTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TestOnOrAfterTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestOnOrAfterIntegerTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TestOnOrAfterDecimalFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestOnOrAfterQuantityTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TestOnOrBeforeNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["TestOnOrBeforeDateTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TestOnOrBeforeDateFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestOnOrBeforeTimeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TestOnOrBeforeTimeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestOnOrBeforeIntegerTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TestOnOrBeforeDecimalFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestOnOrBeforeQuantityTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TestOverlapsNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IntegerIntervalOverlapsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalOverlapsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalOverlapsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalOverlapsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalOverlapsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalOverlapsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeOverlapsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        //        value = results["DateTimeOverlapsNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["DateTimeOverlapsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeOverlapsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeOverlapsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestOverlapsBeforeNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IntegerIntervalOverlapsBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalOverlapsBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalOverlapsBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalOverlapsBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalOverlapsBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalOverlapsBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeOverlapsBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        //        value = results["DateTimeOverlapsBeforeNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["DateTimeOverlapsBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeOverlapsBeforeTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeOverlapsBeforeFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestOverlapsAfterNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IntegerIntervalOverlapsAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalOverlapsAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalOverlapsAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalOverlapsAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalOverlapsAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalOverlapsAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeOverlapsAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        //        value = results["DateTimeOverlapsAfterNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["DateTimeOverlapsAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeOverlapsAfterTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeOverlapsAfterFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestPointFromNull"]!!.value
        Assertions.assertTrue(value == null)

        value = results["TestPointFromInteger"]!!.value
        Assertions.assertTrue(value as Int? == 1)

        value = results["TestPointFromDecimal"]!!.value
        Assertions.assertEquals(0, (value as BigDecimal).compareTo(BigDecimal("1.0")))

        value = results["TestPointFromQuantity"]!!.value
        Assertions.assertTrue(
            equal(value, Quantity().withValue(BigDecimal("1.0")).withUnit("cm")) == true
        )

        value = results["TestProperlyIncludesNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IntegerIntervalProperlyIncludesTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalProperlyIncludesFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalProperlyIncludesTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalProperlyIncludesFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalProperlyIncludesTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalProperlyIncludesFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeProperlyIncludesTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeProperlyIncludesFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeProperlyIncludesTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeProperlyIncludesFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeProperContainsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeProperContainsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeProperContainsNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["TimeProperContainsPrecisionTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeProperContainsPrecisionFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeProperContainsPrecisionNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["TimeProperInTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeProperInFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeProperInNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["TimeProperInPrecisionTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeProperInPrecisionFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeProperInPrecisionNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["TestProperlyIncludedInNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IntegerIntervalProperlyIncludedInTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalProperlyIncludedInFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalProperlyIncludedInTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalProperlyIncludedInFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalProperlyIncludedInTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalProperlyIncludedInFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeProperlyIncludedInTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DateTimeProperlyIncludedInFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeProperlyIncludedInTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeProperlyIncludedInFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["SizeTest"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(5))

        value = results["SizeTestEquivalent"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(5))

        value = results["SizeIsNull"]!!.value
        Assertions.assertNull(value)

        value = results["IntegerIntervalStart"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results["DecimalIntervalStart"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("1.0")))

        value = results["QuantityIntervalStart"]!!.value
        Assertions.assertTrue(
            equal(value, Quantity().withValue(BigDecimal("1.0")).withUnit("g")) == true
        )

        value = results["DateTimeIntervalStart"]!!.value
        Assertions.assertTrue(
            equivalent(value, DateTime(bigDecimalZoneOffset, 2016, 5, 1, 0, 0, 0, 0)) == true
        )

        value = results["TimeIntervalStart"]!!.value
        Assertions.assertTrue(equivalent(value, Time(0, 0, 0, 0)) == true)

        value = results["TestStartsNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IntegerIntervalStartsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["IntegerIntervalStartsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DecimalIntervalStartsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["DecimalIntervalStartsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["QuantityIntervalStartsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["QuantityIntervalStartsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["DateTimeStartsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        //        value = results["DateTimeStartsNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["DateTimeStartsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TimeStartsTrue"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results["TimeStartsFalse"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results["TestUnionNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["IntegerIntervalUnion1To15"]!!.value
        Assertions.assertTrue(equal(value, Interval(1, true, 15, true)) == true)

        value = results["IntegerIntervalUnionNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["DecimalIntervalUnion1To15"]!!.value
        Assertions.assertTrue(
            equal(value, Interval(BigDecimal("1.0"), true, BigDecimal("15.0"), true)) == true
        )

        value = results["DecimalIntervalUnionNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["QuantityIntervalUnion1To15"]!!.value
        Assertions.assertTrue(
            equal(
                value,
                Interval(
                    Quantity().withValue(BigDecimal("1.0")).withUnit("g"),
                    true,
                    Quantity().withValue(BigDecimal("15.0")).withUnit("g"),
                    true,
                ),
            ) == true
        )

        value = results["QuantityIntervalUnionNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["DateTimeUnion"]!!.value
        Assertions.assertTrue(
            equivalent((value as Interval).start, DateTime(bigDecimalZoneOffset, 2012, 1, 5)) ==
                true
        )
        Assertions.assertTrue(
            equivalent(value.end, DateTime(bigDecimalZoneOffset, 2012, 1, 28)) == true
        )

        value = results["DateTimeUnionNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results["TimeUnion"]!!.value
        Assertions.assertTrue(equivalent((value as Interval).start, Time(5, 59, 59, 999)) == true)
        Assertions.assertTrue(equivalent(value.end, Time(20, 59, 59, 999)) == true)

        value = results["TimeUnionNull"]!!.value
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))
    }
}
