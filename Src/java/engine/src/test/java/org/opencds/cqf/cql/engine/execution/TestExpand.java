package org.opencds.cqf.cql.engine.execution;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Date;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TestExpand extends CqlTestBase {

    @Test
    void expandPer2Days() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPer2Days"));
        Object value = results.forExpression("ExpandPer2Days").value();
        List<?> intervals = (List<?>) value;
        assertEquals(2, intervals.size());
        assertTrue(((Interval) intervals.get(0)).equal(
                new Interval(new Date(2018, 1, 1), true, new Date(2018, 1, 2), true)));
        assertTrue(((Interval) intervals.get(1)).equal(
                new Interval(new Date(2018, 1, 3), true, new Date(2018, 1, 4), true)));
    }

    @Test
    void expandPer2DaysIntervalOverload() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPer2DaysIntervalOverload"));
        Object value = results.forExpression("ExpandPer2DaysIntervalOverload").value();
        assertEquals(2, ((List<?>) value).size());
        assertTrue(new Date(2018, 1, 1).equal((Date) ((List<?>) value).get(0)));
        assertTrue(new Date(2018, 1, 3).equal((Date) ((List<?>) value).get(1)));
    }

    @Test
    void expandPer1() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPer1"));
        Object value = results.forExpression("ExpandPer1").value();
        List<?> intervals = (List<?>) value;
        assertEquals(3, intervals.size());
        assertTrue(((Interval) intervals.get(0)).equal(
                new Interval(new BigDecimal("10.0"), true, new BigDecimal("10.0"), true)));
        assertTrue(((Interval) intervals.get(1)).equal(
                new Interval(new BigDecimal("11.0"), true, new BigDecimal("11.0"), true)));
        assertTrue(((Interval) intervals.get(2)).equal(
                new Interval(new BigDecimal("12.0"), true, new BigDecimal("12.0"), true)));
    }

    @Test
    void expandPer1IntervalOverload() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPer1IntervalOverload"));
        Object value = results.forExpression("ExpandPer1IntervalOverload").value();
        assertEquals(List.of(new BigDecimal("10.0"), new BigDecimal("11.0"), new BigDecimal("12.0")), value);
    }

    @Test
    void expandPer1Open() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPer1Open"));
        Object value = results.forExpression("ExpandPer1Open").value();
        List<?> intervals = (List<?>) value;
        assertEquals(3, intervals.size());
        assertTrue(((Interval) intervals.get(0)).equal(
                new Interval(new BigDecimal("10.0"), true, new BigDecimal("10.0"), true)));
        assertTrue(((Interval) intervals.get(1)).equal(
                new Interval(new BigDecimal("11.0"), true, new BigDecimal("11.0"), true)));
        assertTrue(((Interval) intervals.get(2)).equal(
                new Interval(new BigDecimal("12.0"), true, new BigDecimal("12.0"), true)));
    }

    @Test
    void expandPer1OpenIntervalOverload() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPer1OpenIntervalOverload"));
        Object value = results.forExpression("ExpandPer1OpenIntervalOverload").value();
        assertEquals(List.of(new BigDecimal("10.0"), new BigDecimal("11.0"), new BigDecimal("12.0")), value);
    }

    @Test
    void expandPerMinute() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPerMinute"));
        Object value = results.forExpression("ExpandPerMinute").value();
        List<?> intervals = (List<?>) value;
        assertEquals(0, intervals.size());
    }

    @Test
    void expandPerMinuteIntervalOverload() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPerMinuteIntervalOverload"));
        Object value = results.forExpression("ExpandPerMinuteIntervalOverload").value();
        List<?> intervals = (List<?>) value;
        assertEquals(0, intervals.size());
    }

    @Test
    void expandPer0D1() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPer0D1"));
        Object value = results.forExpression("ExpandPer0D1").value();
        List<?> intervals = (List<?>) value;
        assertEquals(10, intervals.size());
        for (int i = 0; i < 10; i++) {
            BigDecimal v = new BigDecimal("10." + i);
            assertTrue(((Interval) intervals.get(i)).equal(
                    new Interval(v, true, v, true)));
        }
    }

    @Test
    void expandPer0D1IntervalOverload() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPer0D1IntervalOverload"));
        Object value = results.forExpression("ExpandPer0D1IntervalOverload").value();
        List<?> points = (List<?>) value;
        assertEquals(10, points.size());
        for (int i = 0; i < 10; i++) {
            BigDecimal v = new BigDecimal("10." + i);
            assertEquals(v, points.get(i));
        }
    }
}
