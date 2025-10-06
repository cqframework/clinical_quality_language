package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
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
    //            Object value = results.forExpression("InvalidIntegerIntervalA").value();
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

        val results = engine.evaluate(toElmIdentifier("CqlIntervalOperatorsTest"))
        var value = results.forExpression("IntegerIntervalAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IntegerIntervalPointAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalPointAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IntegerIntervalAfterPointTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalAfterPointFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalPointAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalPointAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalAfterPointTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalAfterPointFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalPointAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalPointAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalAfterPointTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalAfterPointFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestBeforeNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IntegerIntervalBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IntegerIntervalBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalPointBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalPointBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IntegerIntervalBeforePointTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalBeforePointFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalPointBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalPointBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalBeforePointTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalBeforePointFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalPointBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalPointBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalBeforePointTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalBeforePointFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestCollapseNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IntegerIntervalCollapse")!!.value()
        Assertions.assertTrue(
            ((value as MutableList<*>)[0] as Interval).equal(Interval(1, true, 10, true)) == true
        )
        Assertions.assertTrue((value[1] as Interval).equal(Interval(12, true, 19, true)) == true)

        value = results.forExpression("IntegerIntervalCollapse2")!!.value()
        Assertions.assertTrue(
            ((value as MutableList<*>)[0] as Interval).equal(Interval(1, true, 19, true)) == true
        )

        value = results.forExpression("IntegerIntervalCollapse3")!!.value()
        Assertions.assertTrue(
            ((value as MutableList<*>)[0] as Interval).equal(Interval(4, true, 8, true)) == true
        )

        value = results.forExpression("IntegerIntervalCollapse4")!!.value()
        Assertions.assertTrue(
            ((value as MutableList<*>)[0] as Interval).equal(Interval(4, true, 6, true)) == true
        )
        Assertions.assertTrue((value[1] as Interval).equal(Interval(8, true, 10, true)) == true)

        value = results.forExpression("DecimalIntervalCollapse")!!.value()
        Assertions.assertTrue(
            ((value as MutableList<*>)[0] as Interval).equal(
                Interval(BigDecimal("1.0"), true, BigDecimal("10.0"), true)
            ) == true
        )
        Assertions.assertTrue(
            (value[1] as Interval).equal(
                Interval(BigDecimal("12.0"), true, BigDecimal("19.0"), true)
            ) == true
        )

        value = results.forExpression("DecimalIntervalCollapse2")!!.value()
        Assertions.assertTrue(
            ((value as MutableList<*>)[0] as Interval).equal(
                Interval(BigDecimal("4.0"), true, BigDecimal("8.0"), true)
            ) == true
        )

        value = results.forExpression("QuantityIntervalCollapse")!!.value()
        Assertions.assertTrue(
            ((value as MutableList<*>)[0] as Interval).equal(
                Interval(
                    Quantity().withValue(BigDecimal("1.0")).withUnit("g"),
                    true,
                    Quantity().withValue(BigDecimal("10.0")).withUnit("g"),
                    true,
                )
            ) == true
        )
        Assertions.assertTrue(
            (value[1] as Interval).equal(
                Interval(
                    Quantity().withValue(BigDecimal("12.0")).withUnit("g"),
                    true,
                    Quantity().withValue(BigDecimal("19.0")).withUnit("g"),
                    true,
                )
            ) == true
        )

        value = results.forExpression("DateTimeCollapse")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                ((value as MutableList<*>)[0] as Interval).start,
                DateTime(bigDecimalZoneOffset, 2012, 1, 1),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value[0] as Interval).end,
                DateTime(bigDecimalZoneOffset, 2012, 1, 25),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value[1] as Interval).start,
                DateTime(bigDecimalZoneOffset, 2012, 5, 10),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value[1] as Interval).end,
                DateTime(bigDecimalZoneOffset, 2012, 5, 30),
            ) == true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results.forExpression("DateTimeCollapse2")!!.value()

        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                ((value as MutableList<*>)[0] as Interval).start,
                DateTime(bigDecimalZoneOffset, 2012, 1, 1),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value[0] as Interval).end,
                DateTime(bigDecimalZoneOffset, 2012, 5, 25),
            ) == true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(1))

        value = results.forExpression("DateTimeCollapse3")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                ((value as MutableList<*>)[0] as Interval).start,
                DateTime(bigDecimalZoneOffset, 2018, 1, 1),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value[0] as Interval).end,
                DateTime(bigDecimalZoneOffset, 2018, 8, 28),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value[1] as Interval).start,
                DateTime(bigDecimalZoneOffset, 2018, 8, 30),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value[1] as Interval).end,
                DateTime(bigDecimalZoneOffset, 2018, 10, 15),
            ) == true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results.forExpression("DateTimeCollapse4")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                ((value as MutableList<*>)[0] as Interval).start,
                Date(2018, 1, 1),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value[0] as Interval).end, Date(2018, 8, 28)) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value[1] as Interval).start, Date(2018, 8, 30)) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value[1] as Interval).end, Date(2018, 10, 15)) == true
        )

        value = results.forExpression("DateTimeCollapse5")!!.value()
        println(value)
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                ((value as MutableList<*>)[0] as Interval).start,
                Date(2018, 1, 1),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value[0] as Interval).end, Date(2018, 8, 28)) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value[1] as Interval).start, Date(2018, 8, 30)) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value[1] as Interval).end, Date(2018, 10, 15)) == true
        )

        value = results.forExpression("DateTimeCollapse6")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                ((value as MutableList<*>)[0] as Interval).start,
                Date(2018, 1, 1),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value[0] as Interval).end, Date(2018, 10, 15)) == true
        )

        value = results.forExpression("TimeCollapse")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                ((value as MutableList<*>)[0] as Interval).start,
                Time(1, 59, 59, 999),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value[0] as Interval).end, Time(15, 59, 59, 999)) ==
                true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value[1] as Interval).start, Time(17, 59, 59, 999)) ==
                true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value[1] as Interval).end, Time(22, 59, 59, 999)) ==
                true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(2))

        value = results.forExpression("TimeCollapse2")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                ((value as MutableList<*>)[0] as Interval).start,
                Time(1, 59, 59, 999),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value[0] as Interval).end, Time(15, 59, 59, 999)) ==
                true
        )
        MatcherAssert.assertThat(value.size, Matchers.`is`(1))
        value = results.forExpression("TestContainsNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("TestNullElement1")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestNullElement2")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestNullElementTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalContainsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalContainsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalContainsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalContainsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalContainsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalContainsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        //        value = results.forExpression("DateTimeContainsNull")!!.value();
        //        assertThat(value, is(nullValue()));
        value = results.forExpression("DateTimeContainsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeContainsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        //        value = results.forExpression("TimeContainsNull")!!.value();
        //        assertThat(value, is(nullValue()));
        value = results.forExpression("TimeContainsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeContainsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IntegerIntervalEnd")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(10))

        value = results.forExpression("DecimalIntervalEnd")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("10.0")))

        value = results.forExpression("QuantityIntervalEnd")!!.value()
        Assertions.assertTrue(
            (value as Quantity).equal(Quantity().withValue(BigDecimal("10.0")).withUnit("g")) ==
                true
        )

        value = results.forExpression("DateTimeIntervalEnd")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                value,
                DateTime(bigDecimalZoneOffset, 2016, 5, 2, 0, 0, 0, 0),
            ) == true
        )

        value = results.forExpression("TimeIntervalEnd")!!.value()
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(23, 59, 59, 599)) == true)

        //        value = results.forExpression("TestEndsNull")!!.value();
        //        assertThat(value, is(nullValue()));
        value = results.forExpression("IntegerIntervalEndsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalEndsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalEndsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalEndsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalEndsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalEndsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeEndsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        //        value = results.forExpression("DateTimeEndsNull")!!.value();
        //        assertThat(value, is(nullValue()));
        value = results.forExpression("DateTimeEndsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeEndsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeEndsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        //        value = results.forExpression("TestEqualNull")!!.value();
        //        assertThat(value, is(nullValue()));
        value = results.forExpression("IntegerIntervalEqualTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalEqualFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalEqualTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalEqualFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalEqualTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalEqualFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeEqualTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeEqualFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeEqualTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeEqualFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        //        value = results.forExpression("TestExceptNull")!!.value();
        //        assertThat(value, is(nullValue()));
        value = results.forExpression("IntegerIntervalExcept1to3")!!.value()
        Assertions.assertTrue((value as Interval).equal(Interval(1, true, 3, true)) == true)

        value = results.forExpression("IntegerIntervalExcept4to6")!!.value()
        Assertions.assertTrue((value as Interval).equal(Interval(-4, false, 6, false)) == true)

        value = results.forExpression("IntegerIntervalExceptNullOutNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IntegerIntervalExceptNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("DecimalIntervalExcept1to3")!!.value()
        Assertions.assertTrue(
            (value as Interval).equal(
                Interval(BigDecimal("1.0"), true, BigDecimal("3.99999999"), true)
            ) == true
        )

        value = results.forExpression("DecimalIntervalExceptNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("QuantityIntervalExcept1to4")!!.value()
        Assertions.assertTrue(
            (value as Interval).equal(
                Interval(
                    Quantity().withValue(BigDecimal("1.0")).withUnit("g"),
                    true,
                    Quantity().withValue(BigDecimal("4.99999999")).withUnit("g"),
                    true,
                )
            ) == true
        )

        value = results.forExpression("Except12")!!.value()
        Assertions.assertTrue((value as Interval).equal(Interval(1, true, 2, true)) == true)

        value = results.forExpression("ExceptDateTimeInterval")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as Interval).start,
                DateTime(bigDecimalZoneOffset, 2012, 1, 5),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value.end, DateTime(bigDecimalZoneOffset, 2012, 1, 6)) ==
                true
        )

        value = results.forExpression("ExceptDateTime2")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as Interval).start,
                DateTime(bigDecimalZoneOffset, 2012, 1, 13),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                value.end,
                DateTime(bigDecimalZoneOffset, 2012, 1, 16),
            ) == true
        )

        value = results.forExpression("ExceptTimeInterval")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as Interval).start, Time(5, 59, 59, 999)) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value.end, Time(8, 59, 59, 998)) == true
        )

        value = results.forExpression("ExceptTime2")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as Interval).start, Time(11, 0, 0, 0)) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value.end, Time(11, 59, 59, 999)) == true
        )

        value = results.forExpression("TestInNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestInNullEnd")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TestNullIn")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IntegerIntervalInTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalInFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalInTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalInFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalInTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalInFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeInTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        //        value = results.forExpression("DateTimeInNullPrecision")!!.value();
        //        assertThat(value, is(nullValue()));
        value = results.forExpression("DateTimeInFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeInNullTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeInTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeInFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeInNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("Issue32Interval")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))
        value = results.forExpression("TestIncludesNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IntegerIntervalIncludesTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalIncludesFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalIncludesTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalIncludesFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalIncludesTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalIncludesFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeIncludesTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeIncludesFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeIncludesTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeIncludesFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))
        value = results.forExpression("IntegerIntervalIncludedInTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalIncludedInFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalIncludedInTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalIncludedInFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalIncludedInTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalIncludedInFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeIncludedInTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeIncludedInFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeIncludedInTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeIncludedInFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeIncludedInNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("DateTimeIncludedInPrecisionTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeIncludedInPrecisionNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("TestIntersectNullRightStart")!!.value()
        // Because of how nulls work, equivalence, not equality, is the relevant test here (equality
        // just gives null).
        Assertions.assertTrue(
            (value as Interval).equivalent(Interval(null, false, 5, true)) == true
        )

        value = results.forExpression("TestIntersectNullRightEnd")!!.value()
        Assertions.assertTrue(
            (value as Interval).equivalent(Interval(5, true, null, false)) == true
        )

        value = results.forExpression("TestIntersectNullLeftStart")!!.value()
        Assertions.assertTrue(
            (value as Interval).equivalent(Interval(null, false, 5, true)) == true
        )

        value = results.forExpression("TestIntersectNullLeftEnd")!!.value()
        Assertions.assertTrue(
            (value as Interval).equivalent(Interval(5, true, null, false)) == true
        )

        value = results.forExpression("TestIntersectNull1")!!.value()
        Assertions.assertTrue((value as Boolean?)!!)

        value = results.forExpression("TestIntersectNull2")!!.value()
        Assertions.assertTrue((value as Boolean?)!!)

        value = results.forExpression("TestIntersectNull3")!!.value()
        Assertions.assertFalse((value as Boolean?)!!)

        value = results.forExpression("TestIntersectNull4")!!.value()
        Assertions.assertFalse((value as Boolean?)!!)

        value = results.forExpression("IntegerIntervalIntersectTest4to10")!!.value()
        Assertions.assertTrue((value as Interval).equal(Interval(4, true, 10, true)) == true)

        value = results.forExpression("IntegerIntervalIntersectTestNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("DecimalIntervalIntersectTest4to10")!!.value()
        Assertions.assertTrue(
            (value as Interval).equal(
                Interval(BigDecimal("4.0"), true, BigDecimal("10.0"), true)
            ) == true
        )

        value = results.forExpression("IntegerIntervalIntersectTestNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("QuantityIntervalIntersectTest5to10")!!.value()
        Assertions.assertTrue(
            (value as Interval).equal(
                Interval(
                    Quantity().withValue(BigDecimal("5.0")).withUnit("g"),
                    true,
                    Quantity().withValue(BigDecimal("10.0")).withUnit("g"),
                    true,
                )
            ) == true
        )

        value = results.forExpression("QuantityIntervalIntersectTestNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("DateTimeIntersect")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as Interval).start,
                DateTime(bigDecimalZoneOffset, 2012, 1, 7),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                value.end,
                DateTime(bigDecimalZoneOffset, 2012, 1, 10),
            ) == true
        )

        value = results.forExpression("TimeIntersect")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as Interval).start, Time(4, 59, 59, 999)) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value.end, Time(6, 59, 59, 999)) == true
        )

        value = results.forExpression("IntegerIntervalEquivalentTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalEquivalentFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalEquivalentTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalEquivalentFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalEquivalentTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalEquivalentFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeEquivalentTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeEquivalentFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeEquivalentTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeEquivalentFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestMeetsNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IntegerIntervalMeetsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalMeetsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalMeetsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalMeetsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalMeetsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalMeetsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeMeetsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeMeetsNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeMeetsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeMeetsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeMeetsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestMeetsBeforeNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IntegerIntervalMeetsBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalMeetsBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalMeetsBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalMeetsBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalMeetsBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalMeetsBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeMeetsBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeMeetsBeforeNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeMeetsBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeMeetsBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeMeetsBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestMeetsAfterNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IntegerIntervalMeetsAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalMeetsAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalMeetsAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalMeetsAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalMeetsAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalMeetsAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeMeetsAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeMeetsAfterNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeMeetsAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeMeetsAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeMeetsAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("IntegerIntervalNotEqualTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalNotEqualFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalNotEqualTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalNotEqualFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalNotEqualTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalNotEqualFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeNotEqualTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeNotEqualFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeNotEqualTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeNotEqualFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestOnOrAfterNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("TestOnOrAfterDateTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TestOnOrAfterDateFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestOnOrAfterTimeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TestOnOrAfterTimeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestOnOrAfterIntegerTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TestOnOrAfterDecimalFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestOnOrAfterQuantityTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TestOnOrBeforeNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("TestOnOrBeforeDateTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TestOnOrBeforeDateFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestOnOrBeforeTimeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TestOnOrBeforeTimeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestOnOrBeforeIntegerTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TestOnOrBeforeDecimalFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestOnOrBeforeQuantityTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TestOverlapsNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IntegerIntervalOverlapsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalOverlapsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalOverlapsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalOverlapsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalOverlapsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalOverlapsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeOverlapsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        //        value = results.forExpression("DateTimeOverlapsNull")!!.value();
        //        assertThat(value, is(nullValue()));
        value = results.forExpression("DateTimeOverlapsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeOverlapsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeOverlapsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestOverlapsBeforeNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IntegerIntervalOverlapsBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalOverlapsBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalOverlapsBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalOverlapsBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalOverlapsBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalOverlapsBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeOverlapsBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        //        value = results.forExpression("DateTimeOverlapsBeforeNull")!!.value();
        //        assertThat(value, is(nullValue()));
        value = results.forExpression("DateTimeOverlapsBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeOverlapsBeforeTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeOverlapsBeforeFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestOverlapsAfterNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IntegerIntervalOverlapsAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalOverlapsAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalOverlapsAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalOverlapsAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalOverlapsAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalOverlapsAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeOverlapsAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        //        value = results.forExpression("DateTimeOverlapsAfterNull")!!.value();
        //        assertThat(value, is(nullValue()));
        value = results.forExpression("DateTimeOverlapsAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeOverlapsAfterTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeOverlapsAfterFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestPointFromNull")!!.value()
        Assertions.assertTrue(value == null)

        value = results.forExpression("TestPointFromInteger")!!.value()
        Assertions.assertTrue(value as Int? == 1)

        value = results.forExpression("TestPointFromDecimal")!!.value()
        Assertions.assertEquals(0, (value as BigDecimal).compareTo(BigDecimal("1.0")))

        value = results.forExpression("TestPointFromQuantity")!!.value()
        Assertions.assertTrue(
            (value as Quantity).equal(Quantity().withValue(BigDecimal("1.0")).withUnit("cm")) ==
                true
        )

        value = results.forExpression("TestProperlyIncludesNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IntegerIntervalProperlyIncludesTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalProperlyIncludesFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalProperlyIncludesTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalProperlyIncludesFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalProperlyIncludesTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalProperlyIncludesFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeProperlyIncludesTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeProperlyIncludesFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeProperlyIncludesTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeProperlyIncludesFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeProperContainsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeProperContainsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeProperContainsNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("TimeProperContainsPrecisionTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeProperContainsPrecisionFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeProperContainsPrecisionNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("TimeProperInTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeProperInFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeProperInNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("TimeProperInPrecisionTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeProperInPrecisionFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeProperInPrecisionNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("TestProperlyIncludedInNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IntegerIntervalProperlyIncludedInTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalProperlyIncludedInFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalProperlyIncludedInTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalProperlyIncludedInFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalProperlyIncludedInTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalProperlyIncludedInFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeProperlyIncludedInTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DateTimeProperlyIncludedInFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeProperlyIncludedInTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeProperlyIncludedInFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("SizeTest")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(5))

        value = results.forExpression("SizeTestEquivalent")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(5))

        value = results.forExpression("SizeIsNull")!!.value()
        Assertions.assertNull(value)

        value = results.forExpression("IntegerIntervalStart")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(1))

        value = results.forExpression("DecimalIntervalStart")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(BigDecimal("1.0")))

        value = results.forExpression("QuantityIntervalStart")!!.value()
        Assertions.assertTrue(
            (value as Quantity).equal(Quantity().withValue(BigDecimal("1.0")).withUnit("g")) == true
        )

        value = results.forExpression("DateTimeIntervalStart")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                value,
                DateTime(bigDecimalZoneOffset, 2016, 5, 1, 0, 0, 0, 0),
            ) == true
        )

        value = results.forExpression("TimeIntervalStart")!!.value()
        Assertions.assertTrue(EquivalentEvaluator.equivalent(value, Time(0, 0, 0, 0)) == true)

        value = results.forExpression("TestStartsNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IntegerIntervalStartsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("IntegerIntervalStartsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DecimalIntervalStartsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("DecimalIntervalStartsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("QuantityIntervalStartsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("QuantityIntervalStartsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("DateTimeStartsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        //        value = results.forExpression("DateTimeStartsNull")!!.value();
        //        assertThat(value, is(nullValue()));
        value = results.forExpression("DateTimeStartsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TimeStartsTrue")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(true))

        value = results.forExpression("TimeStartsFalse")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(false))

        value = results.forExpression("TestUnionNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("IntegerIntervalUnion1To15")!!.value()
        Assertions.assertTrue((value as Interval).equal(Interval(1, true, 15, true)) == true)

        value = results.forExpression("IntegerIntervalUnionNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("DecimalIntervalUnion1To15")!!.value()
        Assertions.assertTrue(
            (value as Interval).equal(
                Interval(BigDecimal("1.0"), true, BigDecimal("15.0"), true)
            ) == true
        )

        value = results.forExpression("DecimalIntervalUnionNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("QuantityIntervalUnion1To15")!!.value()
        Assertions.assertTrue(
            (value as Interval).equal(
                Interval(
                    Quantity().withValue(BigDecimal("1.0")).withUnit("g"),
                    true,
                    Quantity().withValue(BigDecimal("15.0")).withUnit("g"),
                    true,
                )
            ) == true
        )

        value = results.forExpression("QuantityIntervalUnionNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("DateTimeUnion")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                (value as Interval).start,
                DateTime(bigDecimalZoneOffset, 2012, 1, 5),
            ) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(
                value.end,
                DateTime(bigDecimalZoneOffset, 2012, 1, 28),
            ) == true
        )

        value = results.forExpression("DateTimeUnionNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))

        value = results.forExpression("TimeUnion")!!.value()
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent((value as Interval).start, Time(5, 59, 59, 999)) == true
        )
        Assertions.assertTrue(
            EquivalentEvaluator.equivalent(value.end, Time(20, 59, 59, 999)) == true
        )

        value = results.forExpression("TimeUnionNull")!!.value()
        MatcherAssert.assertThat(value, Matchers.`is`(Matchers.nullValue()))
    }
}
