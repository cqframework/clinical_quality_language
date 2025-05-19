package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.Interval;

class TestExpand extends CqlTestBase {

    @Test
    void expandPer2Days() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPer2Days"));
        Object value = results.forExpression("ExpandPer2Days").value();
        List<?> intervals = (List<?>) value;
        assertIterableEquals(
                List.of(
                        closed(new Date(2018, 1, 1), new Date(2018, 1, 2)),
                        closed(new Date(2018, 1, 3), new Date(2018, 1, 4))),
                intervals);
    }

    @Test
    void expandPer2DaysIntervalOverload() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPer2DaysIntervalOverload"));
        Object value = results.forExpression("ExpandPer2DaysIntervalOverload").value();
        var dates = (List<?>) value;
        assertIterableEquals(List.of(new Date(2018, 1, 1), new Date(2018, 1, 3)), dates);
    }

    @Test
    void expandPer1() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPer1"));
        Object value = results.forExpression("ExpandPer1").value();
        List<?> intervals = (List<?>) value;
        assertIterableEquals(
                List.of(closedDecimal("10", "10"), closedDecimal("11", "11"), closedDecimal("12", "12")), intervals);
    }

    @Test
    void expandPer1IntervalOverload() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPer1IntervalOverload"));
        Object value = results.forExpression("ExpandPer1IntervalOverload").value();
        assertIterableEquals(
                List.of(new BigDecimal("10"), new BigDecimal("11"), new BigDecimal("12")), (List<?>) value);
    }

    @Test
    void expandPer1Open() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPer1Open"));
        Object value = results.forExpression("ExpandPer1Open").value();
        List<?> intervals = (List<?>) value;
        assertIterableEquals(
                List.of(closedDecimal("10", "10"), closedDecimal("11", "11"), closedDecimal("12", "12")), intervals);
    }

    @Test
    void expandPer1OpenIntervalOverload() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPer1OpenIntervalOverload"));
        Object value = results.forExpression("ExpandPer1OpenIntervalOverload").value();
        assertIterableEquals(
                List.of(new BigDecimal("10"), new BigDecimal("11"), new BigDecimal("12")), (List<?>) value);
    }

    @Test
    void expandPerMinute() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPerMinute"));
        Object value = results.forExpression("ExpandPerMinute").value();
        List<?> intervals = (List<?>) value;
        assertIterableEquals(List.of(), intervals);
    }

    @Test
    void expandPerMinuteIntervalOverload() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPerMinuteIntervalOverload"));
        Object value = results.forExpression("ExpandPerMinuteIntervalOverload").value();
        List<?> intervals = (List<?>) value;
        assertIterableEquals(List.of(), intervals);
    }

    @Test
    void expandPer0D1() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPer0D1"));
        Object value = results.forExpression("ExpandPer0D1").value();
        List<?> intervals = (List<?>) value;
        assertIterableEquals(
                List.of(
                        closedDecimal("10.0", "10.0"),
                        closedDecimal("10.1", "10.1"),
                        closedDecimal("10.2", "10.2"),
                        closedDecimal("10.3", "10.3"),
                        closedDecimal("10.4", "10.4"),
                        closedDecimal("10.5", "10.5"),
                        closedDecimal("10.6", "10.6"),
                        closedDecimal("10.7", "10.7"),
                        closedDecimal("10.8", "10.8"),
                        closedDecimal("10.9", "10.9")),
                intervals);
    }

    @Test
    void expandPer0D1IntervalOverload() {
        var results = engine.evaluate(toElmIdentifier("TestExpand"), Set.of("ExpandPer0D1IntervalOverload"));
        Object value = results.forExpression("ExpandPer0D1IntervalOverload").value();
        List<?> points = (List<?>) value;
        assertIterableEquals(
                List.of(
                        new BigDecimal("10.0"),
                        new BigDecimal("10.1"),
                        new BigDecimal("10.2"),
                        new BigDecimal("10.3"),
                        new BigDecimal("10.4"),
                        new BigDecimal("10.5"),
                        new BigDecimal("10.6"),
                        new BigDecimal("10.7"),
                        new BigDecimal("10.8"),
                        new BigDecimal("10.9")),
                points);
    }

    private static Interval closed(Object start, Object end) {
        return new Interval(start, true, end, true);
    }

    private static Interval closedDecimal(String start, String end) {
        return new Interval(new BigDecimal(start), true, new BigDecimal(end), true);
    }
}
