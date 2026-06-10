package org.opencds.cqf.cql.engine.execution

import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator.equal
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator.equivalent
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Decimal
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

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
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IntegerIntervalPointAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalPointAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IntegerIntervalAfterPointTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalAfterPointFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalPointAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalPointAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalAfterPointTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalAfterPointFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalPointAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalPointAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalAfterPointTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalAfterPointFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestBeforeNull"]!!.value
        assertNull(value)

        value = results["IntegerIntervalBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IntegerIntervalBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalPointBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalPointBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IntegerIntervalBeforePointTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalBeforePointFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalPointBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalPointBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalBeforePointTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalBeforePointFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalPointBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalPointBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalBeforePointTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalBeforePointFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestCollapseNull"]!!.value
        assertNull(value)

        value = results["IntegerIntervalCollapse"]!!.value
        assertTrue(
            equal(
                    (value as List).elementAt(0),
                    Interval(1.toCqlInteger(), true, 10.toCqlInteger(), true),
                )
                ?.value == true
        )
        assertTrue(
            equal(value.elementAt(1), Interval(12.toCqlInteger(), true, 19.toCqlInteger(), true))
                ?.value == true
        )

        value = results["IntegerIntervalCollapse2"]!!.value
        assertTrue(
            equal(
                    (value as List).elementAt(0),
                    Interval(1.toCqlInteger(), true, 19.toCqlInteger(), true),
                )
                ?.value == true
        )

        value = results["IntegerIntervalCollapse3"]!!.value
        assertTrue(
            equal(
                    (value as List).elementAt(0),
                    Interval(4.toCqlInteger(), true, 8.toCqlInteger(), true),
                )
                ?.value == true
        )

        value = results["IntegerIntervalCollapse4"]!!.value
        assertTrue(
            equal(
                    (value as List).elementAt(0),
                    Interval(4.toCqlInteger(), true, 6.toCqlInteger(), true),
                )
                ?.value == true
        )
        assertTrue(
            equal(value.elementAt(1), Interval(8.toCqlInteger(), true, 10.toCqlInteger(), true))
                ?.value == true
        )

        value = results["DecimalIntervalCollapse"]!!.value
        assertTrue(
            equal(
                    (value as List).elementAt(0),
                    Interval(
                        BigDecimal("1.0").toCqlDecimal(),
                        true,
                        BigDecimal("10.0").toCqlDecimal(),
                        true,
                    ),
                )
                ?.value == true
        )
        assertTrue(
            equal(
                    value.elementAt(1),
                    Interval(
                        BigDecimal("12.0").toCqlDecimal(),
                        true,
                        BigDecimal("19.0").toCqlDecimal(),
                        true,
                    ),
                )
                ?.value == true
        )

        value = results["DecimalIntervalCollapse2"]!!.value
        assertTrue(
            equal(
                    (value as List).elementAt(0),
                    Interval(
                        BigDecimal("4.0").toCqlDecimal(),
                        true,
                        BigDecimal("8.0").toCqlDecimal(),
                        true,
                    ),
                )
                ?.value == true
        )

        value = results["QuantityIntervalCollapse"]!!.value
        assertTrue(
            equal(
                    (value as List).elementAt(0),
                    Interval(
                        Quantity().withValue(BigDecimal("1.0")).withUnit("g"),
                        true,
                        Quantity().withValue(BigDecimal("10.0")).withUnit("g"),
                        true,
                    ),
                )
                ?.value == true
        )
        assertTrue(
            equal(
                    value.elementAt(1),
                    Interval(
                        Quantity().withValue(BigDecimal("12.0")).withUnit("g"),
                        true,
                        Quantity().withValue(BigDecimal("19.0")).withUnit("g"),
                        true,
                    ),
                )
                ?.value == true
        )

        value = results["DateTimeCollapse"]!!.value
        assertTrue(
            equivalent(
                    ((value as List).elementAt(0) as Interval).start,
                    DateTime(bigDecimalZoneOffset, 2012, 1, 1),
                )
                .value == true
        )
        assertTrue(
            equivalent(
                    (value.elementAt(0) as Interval).end,
                    DateTime(bigDecimalZoneOffset, 2012, 1, 25),
                )
                .value == true
        )
        assertTrue(
            equivalent(
                    (value.elementAt(1) as Interval).start,
                    DateTime(bigDecimalZoneOffset, 2012, 5, 10),
                )
                .value == true
        )
        assertTrue(
            equivalent(
                    (value.elementAt(1) as Interval).end,
                    DateTime(bigDecimalZoneOffset, 2012, 5, 30),
                )
                .value == true
        )
        assertEquals(2, value.count())

        value = results["DateTimeCollapse2"]!!.value

        assertTrue(
            equivalent(
                    ((value as List).elementAt(0) as Interval).start,
                    DateTime(bigDecimalZoneOffset, 2012, 1, 1),
                )
                .value == true
        )
        assertTrue(
            equivalent(
                    (value.elementAt(0) as Interval).end,
                    DateTime(bigDecimalZoneOffset, 2012, 5, 25),
                )
                .value == true
        )
        assertEquals(1, value.count())

        value = results["DateTimeCollapse3"]!!.value
        assertTrue(
            equivalent(
                    ((value as List).elementAt(0) as Interval).start,
                    DateTime(bigDecimalZoneOffset, 2018, 1, 1),
                )
                .value == true
        )
        assertTrue(
            equivalent(
                    (value.elementAt(0) as Interval).end,
                    DateTime(bigDecimalZoneOffset, 2018, 8, 28),
                )
                .value == true
        )
        assertTrue(
            equivalent(
                    (value.elementAt(1) as Interval).start,
                    DateTime(bigDecimalZoneOffset, 2018, 8, 30),
                )
                .value == true
        )
        assertTrue(
            equivalent(
                    (value.elementAt(1) as Interval).end,
                    DateTime(bigDecimalZoneOffset, 2018, 10, 15),
                )
                .value == true
        )
        assertEquals(2, value.count())

        value = results["DateTimeCollapse4"]!!.value
        assertTrue(
            equivalent(((value as List).elementAt(0) as Interval).start, Date(2018, 1, 1)).value ==
                true
        )
        assertTrue(
            equivalent((value.elementAt(0) as Interval).end, Date(2018, 8, 28)).value == true
        )
        assertTrue(
            equivalent((value.elementAt(1) as Interval).start, Date(2018, 8, 30)).value == true
        )
        assertTrue(
            equivalent((value.elementAt(1) as Interval).end, Date(2018, 10, 15)).value == true
        )

        value = results["DateTimeCollapse5"]!!.value
        println(value)
        assertTrue(
            equivalent(((value as List).elementAt(0) as Interval).start, Date(2018, 1, 1)).value ==
                true
        )
        assertTrue(
            equivalent((value.elementAt(0) as Interval).end, Date(2018, 8, 28)).value == true
        )
        assertTrue(
            equivalent((value.elementAt(1) as Interval).start, Date(2018, 8, 30)).value == true
        )
        assertTrue(
            equivalent((value.elementAt(1) as Interval).end, Date(2018, 10, 15)).value == true
        )

        value = results["DateTimeCollapse6"]!!.value
        assertTrue(
            equivalent(((value as List).elementAt(0) as Interval).start, Date(2018, 1, 1)).value ==
                true
        )
        assertTrue(
            equivalent((value.elementAt(0) as Interval).end, Date(2018, 10, 15)).value == true
        )

        value = results["TimeCollapse"]!!.value
        assertTrue(
            equivalent(((value as List).elementAt(0) as Interval).start, Time(1, 59, 59, 999))
                .value == true
        )
        assertTrue(
            equivalent((value.elementAt(0) as Interval).end, Time(15, 59, 59, 999)).value == true
        )
        assertTrue(
            equivalent((value.elementAt(1) as Interval).start, Time(17, 59, 59, 999)).value == true
        )
        assertTrue(
            equivalent((value.elementAt(1) as Interval).end, Time(22, 59, 59, 999)).value == true
        )
        assertEquals(2, value.count())

        value = results["TimeCollapse2"]!!.value
        assertTrue(
            equivalent(((value as List).elementAt(0) as Interval).start, Time(1, 59, 59, 999))
                .value == true
        )
        assertTrue(
            equivalent((value.elementAt(0) as Interval).end, Time(15, 59, 59, 999)).value == true
        )
        assertEquals(1, value.count())
        value = results["TestContainsNull"]!!.value
        assertNull(value)

        value = results["TestNullElement1"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestNullElement2"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestNullElementTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalContainsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalContainsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalContainsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalContainsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalContainsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalContainsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        //        value = results["DateTimeContainsNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["DateTimeContainsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeContainsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        //        value = results["TimeContainsNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["TimeContainsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeContainsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IntegerIntervalEnd"]!!.value
        assertEquals(10.toCqlInteger(), value)

        value = results["DecimalIntervalEnd"]!!.value
        assertEquals(BigDecimal("10.0").toCqlDecimal(), value)

        value = results["QuantityIntervalEnd"]!!.value
        assertTrue(
            equal(value, Quantity().withValue(BigDecimal("10.0")).withUnit("g"))?.value == true
        )

        value = results["DateTimeIntervalEnd"]!!.value
        assertTrue(
            equivalent(value, DateTime(bigDecimalZoneOffset, 2016, 5, 2, 0, 0, 0, 0)).value == true
        )

        value = results["TimeIntervalEnd"]!!.value
        assertTrue(equivalent(value, Time(23, 59, 59, 599)).value == true)

        //        value = results["TestEndsNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["IntegerIntervalEndsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalEndsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalEndsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalEndsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalEndsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalEndsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeEndsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        //        value = results["DateTimeEndsNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["DateTimeEndsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeEndsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeEndsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        //        value = results["TestEqualNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["IntegerIntervalEqualTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalEqualFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalEqualTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalEqualFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalEqualTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalEqualFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeEqualTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeEqualFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeEqualTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeEqualFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        //        value = results["TestExceptNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["IntegerIntervalExcept1to3"]!!.value
        assertTrue(
            equal(value, Interval(1.toCqlInteger(), true, 3.toCqlInteger(), true))?.value == true
        )

        value = results["IntegerIntervalExcept4to6"]!!.value
        assertTrue(
            equal(value, Interval((-4).toCqlInteger(), false, 6.toCqlInteger(), false))?.value ==
                true
        )

        value = results["IntegerIntervalExceptNullOutNull"]!!.value
        assertNull(value)

        value = results["IntegerIntervalExceptNull"]!!.value
        assertNull(value)

        value = results["DecimalIntervalExcept1to3"]!!.value
        assertTrue(
            equal(
                    value,
                    Interval(
                        BigDecimal("1.0").toCqlDecimal(),
                        true,
                        BigDecimal("3.99999999").toCqlDecimal(),
                        true,
                    ),
                )
                ?.value == true
        )

        value = results["DecimalIntervalExceptNull"]!!.value
        assertNull(value)

        value = results["QuantityIntervalExcept1to4"]!!.value
        assertTrue(
            equal(
                    value,
                    Interval(
                        Quantity().withValue(BigDecimal("1.0")).withUnit("g"),
                        true,
                        Quantity().withValue(BigDecimal("4.99999999")).withUnit("g"),
                        true,
                    ),
                )
                ?.value == true
        )

        value = results["Except12"]!!.value
        assertTrue(
            equal(value, Interval(1.toCqlInteger(), true, 2.toCqlInteger(), true))?.value == true
        )

        value = results["ExceptDateTimeInterval"]!!.value
        assertTrue(
            equivalent((value as Interval).start, DateTime(bigDecimalZoneOffset, 2012, 1, 5))
                .value == true
        )
        assertTrue(equivalent(value.end, DateTime(bigDecimalZoneOffset, 2012, 1, 6)).value == true)

        value = results["ExceptDateTime2"]!!.value
        assertTrue(
            equivalent((value as Interval).start, DateTime(bigDecimalZoneOffset, 2012, 1, 13))
                .value == true
        )
        assertTrue(equivalent(value.end, DateTime(bigDecimalZoneOffset, 2012, 1, 16)).value == true)

        value = results["ExceptTimeInterval"]!!.value
        assertTrue(equivalent((value as Interval).start, Time(5, 59, 59, 999)).value == true)
        assertTrue(equivalent(value.end, Time(8, 59, 59, 998)).value == true)

        value = results["ExceptTime2"]!!.value
        assertTrue(equivalent((value as Interval).start, Time(11, 0, 0, 0)).value == true)
        assertTrue(equivalent(value.end, Time(11, 59, 59, 999)).value == true)

        value = results["TestInNull"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestInNullEnd"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TestNullIn"]!!.value
        assertNull(value)

        value = results["IntegerIntervalInTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalInFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalInTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalInFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalInTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalInFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeInTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        //        value = results["DateTimeInNullPrecision"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["DateTimeInFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeInNullTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeInTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeInFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeInNull"]!!.value
        assertNull(value)

        value = results["Issue32Interval"]!!.value
        assertEquals(Boolean.TRUE, value)
        value = results["TestIncludesNull"]!!.value
        assertNull(value)

        value = results["IntegerIntervalIncludesTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalIncludesFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalIncludesTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalIncludesFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalIncludesTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalIncludesFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeIncludesTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeIncludesFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeIncludesTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeIncludesFalse"]!!.value
        assertEquals(Boolean.FALSE, value)
        value = results["IntegerIntervalIncludedInTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalIncludedInFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalIncludedInTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalIncludedInFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalIncludedInTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalIncludedInFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeIncludedInTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeIncludedInFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeIncludedInTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeIncludedInFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeIncludedInNull"]!!.value
        assertNull(value)

        value = results["DateTimeIncludedInPrecisionTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeIncludedInPrecisionNull"]!!.value
        assertNull(value)

        value = results["TestIntersectNullRightStart"]!!.value
        // Because of how nulls work, equivalence, not equality, is the relevant test here (equality
        // just gives null).
        assertTrue(equivalent(value, Interval(null, false, 5.toCqlInteger(), true)).value == true)

        value = results["TestIntersectNullRightEnd"]!!.value
        assertTrue(equivalent(value, Interval(5.toCqlInteger(), true, null, false)).value == true)

        value = results["TestIntersectNullLeftStart"]!!.value
        assertTrue(equivalent(value, Interval(null, false, 5.toCqlInteger(), true)).value == true)

        value = results["TestIntersectNullLeftEnd"]!!.value
        assertTrue(equivalent(value, Interval(5.toCqlInteger(), true, null, false)).value == true)

        value = results["TestIntersectNull1"]!!.value
        assertTrue((value as Boolean).value)

        value = results["TestIntersectNull2"]!!.value
        assertTrue((value as Boolean).value)

        value = results["TestIntersectNull3"]!!.value
        assertFalse((value as Boolean).value)

        value = results["TestIntersectNull4"]!!.value
        assertFalse((value as Boolean).value)

        value = results["IntegerIntervalIntersectTest4to10"]!!.value
        assertTrue(
            equal(value, Interval(4.toCqlInteger(), true, 10.toCqlInteger(), true))?.value == true
        )

        value = results["IntegerIntervalIntersectTestNull"]!!.value
        assertNull(value)

        value = results["DecimalIntervalIntersectTest4to10"]!!.value
        assertTrue(
            equal(
                    value,
                    Interval(
                        BigDecimal("4.0").toCqlDecimal(),
                        true,
                        BigDecimal("10.0").toCqlDecimal(),
                        true,
                    ),
                )
                ?.value == true
        )

        value = results["IntegerIntervalIntersectTestNull"]!!.value
        assertNull(value)

        value = results["QuantityIntervalIntersectTest5to10"]!!.value
        assertTrue(
            equal(
                    value,
                    Interval(
                        Quantity().withValue(BigDecimal("5.0")).withUnit("g"),
                        true,
                        Quantity().withValue(BigDecimal("10.0")).withUnit("g"),
                        true,
                    ),
                )
                ?.value == true
        )

        value = results["QuantityIntervalIntersectTestNull"]!!.value
        assertNull(value)

        value = results["DateTimeIntersect"]!!.value
        assertTrue(
            equivalent((value as Interval).start, DateTime(bigDecimalZoneOffset, 2012, 1, 7))
                .value == true
        )
        assertTrue(equivalent(value.end, DateTime(bigDecimalZoneOffset, 2012, 1, 10)).value == true)

        value = results["TimeIntersect"]!!.value
        assertTrue(equivalent((value as Interval).start, Time(4, 59, 59, 999)).value == true)
        assertTrue(equivalent(value.end, Time(6, 59, 59, 999)).value == true)

        value = results["IntegerIntervalEquivalentTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalEquivalentFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalEquivalentTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalEquivalentFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalEquivalentTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalEquivalentFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeEquivalentTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeEquivalentFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeEquivalentTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeEquivalentFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestMeetsNull"]!!.value
        assertNull(value)

        value = results["IntegerIntervalMeetsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalMeetsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalMeetsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalMeetsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalMeetsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalMeetsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeMeetsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeMeetsNull"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeMeetsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeMeetsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeMeetsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestMeetsBeforeNull"]!!.value
        assertNull(value)

        value = results["IntegerIntervalMeetsBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalMeetsBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalMeetsBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalMeetsBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalMeetsBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalMeetsBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeMeetsBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeMeetsBeforeNull"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeMeetsBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeMeetsBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeMeetsBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestMeetsAfterNull"]!!.value
        assertNull(value)

        value = results["IntegerIntervalMeetsAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalMeetsAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalMeetsAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalMeetsAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalMeetsAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalMeetsAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeMeetsAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeMeetsAfterNull"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeMeetsAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeMeetsAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeMeetsAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["IntegerIntervalNotEqualTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalNotEqualFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalNotEqualTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalNotEqualFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalNotEqualTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalNotEqualFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeNotEqualTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeNotEqualFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeNotEqualTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeNotEqualFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestOnOrAfterNull"]!!.value
        assertNull(value)

        value = results["TestOnOrAfterDateTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TestOnOrAfterDateFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestOnOrAfterTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TestOnOrAfterTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestOnOrAfterIntegerTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TestOnOrAfterDecimalFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestOnOrAfterQuantityTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TestOnOrBeforeNull"]!!.value
        assertNull(value)

        value = results["TestOnOrBeforeDateTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TestOnOrBeforeDateFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestOnOrBeforeTimeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TestOnOrBeforeTimeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestOnOrBeforeIntegerTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TestOnOrBeforeDecimalFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestOnOrBeforeQuantityTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TestOverlapsNull"]!!.value
        assertNull(value)

        value = results["IntegerIntervalOverlapsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalOverlapsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalOverlapsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalOverlapsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalOverlapsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalOverlapsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeOverlapsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        //        value = results["DateTimeOverlapsNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["DateTimeOverlapsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeOverlapsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeOverlapsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestOverlapsBeforeNull"]!!.value
        assertNull(value)

        value = results["IntegerIntervalOverlapsBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalOverlapsBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalOverlapsBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalOverlapsBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalOverlapsBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalOverlapsBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeOverlapsBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        //        value = results["DateTimeOverlapsBeforeNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["DateTimeOverlapsBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeOverlapsBeforeTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeOverlapsBeforeFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestOverlapsAfterNull"]!!.value
        assertNull(value)

        value = results["IntegerIntervalOverlapsAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalOverlapsAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalOverlapsAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalOverlapsAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalOverlapsAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalOverlapsAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeOverlapsAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        //        value = results["DateTimeOverlapsAfterNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["DateTimeOverlapsAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeOverlapsAfterTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeOverlapsAfterFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestPointFromNull"]!!.value
        assertTrue(value == null)

        value = results["TestPointFromInteger"]!!.value
        assertEquals(1.toCqlInteger(), value)

        value = results["TestPointFromDecimal"]!!.value
        assertEquals(0, (value as Decimal).value.compareTo(BigDecimal("1.0")))

        value = results["TestPointFromQuantity"]!!.value
        assertTrue(
            equal(value, Quantity().withValue(BigDecimal("1.0")).withUnit("cm"))?.value == true
        )

        value = results["TestProperlyIncludesNull"]!!.value
        assertNull(value)

        value = results["IntegerIntervalProperlyIncludesTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalProperlyIncludesFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalProperlyIncludesTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalProperlyIncludesFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalProperlyIncludesTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalProperlyIncludesFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeProperlyIncludesTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeProperlyIncludesFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeProperlyIncludesTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeProperlyIncludesFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeProperContainsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeProperContainsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeProperContainsNull"]!!.value
        assertNull(value)

        value = results["TimeProperContainsPrecisionTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeProperContainsPrecisionFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeProperContainsPrecisionNull"]!!.value
        assertNull(value)

        value = results["TimeProperInTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeProperInFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeProperInNull"]!!.value
        assertNull(value)

        value = results["TimeProperInPrecisionTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeProperInPrecisionFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeProperInPrecisionNull"]!!.value
        assertNull(value)

        value = results["TestProperlyIncludedInNull"]!!.value
        assertNull(value)

        value = results["IntegerIntervalProperlyIncludedInTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalProperlyIncludedInFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalProperlyIncludedInTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalProperlyIncludedInFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalProperlyIncludedInTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalProperlyIncludedInFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeProperlyIncludedInTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DateTimeProperlyIncludedInFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeProperlyIncludedInTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeProperlyIncludedInFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["SizeTest"]!!.value
        assertEquals(5.toCqlInteger(), value)

        value = results["SizeTestEquivalent"]!!.value
        assertEquals(5.toCqlInteger(), value)

        value = results["SizeIsNull"]!!.value
        assertNull(value)

        value = results["IntegerIntervalStart"]!!.value
        assertEquals(1.toCqlInteger(), value)

        value = results["DecimalIntervalStart"]!!.value
        assertEquals(BigDecimal("1.0").toCqlDecimal(), value)

        value = results["QuantityIntervalStart"]!!.value
        assertTrue(
            equal(value, Quantity().withValue(BigDecimal("1.0")).withUnit("g"))?.value == true
        )

        value = results["DateTimeIntervalStart"]!!.value
        assertTrue(
            equivalent(value, DateTime(bigDecimalZoneOffset, 2016, 5, 1, 0, 0, 0, 0)).value == true
        )

        value = results["TimeIntervalStart"]!!.value
        assertTrue(equivalent(value, Time(0, 0, 0, 0)).value == true)

        value = results["TestStartsNull"]!!.value
        assertNull(value)

        value = results["IntegerIntervalStartsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["IntegerIntervalStartsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DecimalIntervalStartsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["DecimalIntervalStartsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["QuantityIntervalStartsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["QuantityIntervalStartsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["DateTimeStartsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        //        value = results["DateTimeStartsNull"]!!.value;
        //        assertThat(value, is(nullValue()));
        value = results["DateTimeStartsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TimeStartsTrue"]!!.value
        assertEquals(Boolean.TRUE, value)

        value = results["TimeStartsFalse"]!!.value
        assertEquals(Boolean.FALSE, value)

        value = results["TestUnionNull"]!!.value
        assertNull(value)

        value = results["IntegerIntervalUnion1To15"]!!.value
        assertTrue(
            equal(value, Interval(1.toCqlInteger(), true, 15.toCqlInteger(), true))?.value == true
        )

        value = results["IntegerIntervalUnionNull"]!!.value
        assertNull(value)

        value = results["DecimalIntervalUnion1To15"]!!.value
        assertTrue(
            equal(
                    value,
                    Interval(
                        BigDecimal("1.0").toCqlDecimal(),
                        true,
                        BigDecimal("15.0").toCqlDecimal(),
                        true,
                    ),
                )
                ?.value == true
        )

        value = results["DecimalIntervalUnionNull"]!!.value
        assertNull(value)

        value = results["QuantityIntervalUnion1To15"]!!.value
        assertTrue(
            equal(
                    value,
                    Interval(
                        Quantity().withValue(BigDecimal("1.0")).withUnit("g"),
                        true,
                        Quantity().withValue(BigDecimal("15.0")).withUnit("g"),
                        true,
                    ),
                )
                ?.value == true
        )

        value = results["QuantityIntervalUnionNull"]!!.value
        assertNull(value)

        value = results["DateTimeUnion"]!!.value
        assertTrue(
            equivalent((value as Interval).start, DateTime(bigDecimalZoneOffset, 2012, 1, 5))
                .value == true
        )
        assertTrue(equivalent(value.end, DateTime(bigDecimalZoneOffset, 2012, 1, 28)).value == true)

        value = results["DateTimeUnionNull"]!!.value
        assertNull(value)

        value = results["TimeUnion"]!!.value
        assertTrue(equivalent((value as Interval).start, Time(5, 59, 59, 999)).value == true)
        assertTrue(equivalent(value.end, Time(20, 59, 59, 999)).value == true)

        value = results["TimeUnionNull"]!!.value
        assertNull(value)
    }
}
