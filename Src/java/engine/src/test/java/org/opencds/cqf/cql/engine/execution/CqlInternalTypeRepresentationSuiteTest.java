package org.opencds.cqf.cql.engine.execution;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.opencds.cqf.cql.engine.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CqlInternalTypeRepresentationSuiteTest extends CqlTestBase {
    private static final Logger logger = LoggerFactory.getLogger(CqlInternalTypeRepresentationSuiteTest.class);

    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId MONTREAL = ZoneId.of("America/Montreal");
    private static final ZoneId REGINA =
            ZoneId.of("America/Regina"); // Saskatchewan does not have standard time (non-DST) all year round
    private static final LocalDateTime DST_2023_11_01 = LocalDateTime.of(2023, Month.NOVEMBER, 1, 0, 0, 0);
    private static final LocalDateTime NON_DST_2023_11_13 = LocalDateTime.of(2023, Month.NOVEMBER, 13, 0, 0, 0);
    private static final LocalDateTime NON_DST_2018_01_01 = LocalDateTime.of(2018, Month.JANUARY, 1, 7, 0, 0);

    private static Object[][] timeZones() {
        return new Object[][] {
            {UTC, DST_2023_11_01}, {MONTREAL, DST_2023_11_01}, {REGINA, DST_2023_11_01},
            {UTC, NON_DST_2023_11_13}, {MONTREAL, NON_DST_2023_11_13}, {REGINA, NON_DST_2023_11_13},
            {UTC, NON_DST_2018_01_01}, {MONTREAL, NON_DST_2018_01_01}, {REGINA, NON_DST_2018_01_01}
        };
    }

    @ParameterizedTest
    @MethodSource("timeZones")
    void all_internal_type_representation(ZoneId zoneId, LocalDateTime now) {
        var results =
                engine.evaluate(toElmIdentifier("CqlInternalTypeRepresentationSuite"), ZonedDateTime.of(now, zoneId));

        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        var value = results.forExpression("BoolTrue").value();
        assertTrue(value instanceof Boolean);
        assertTrue((Boolean) value);

        value = results.forExpression("BoolFalse").value();
        assertTrue(value instanceof Boolean);
        assertFalse((Boolean) value);

        value = results.forExpression("IntOne").value();
        assertTrue(value instanceof Integer);
        assertTrue((Integer) value == 1);

        value = results.forExpression("DecimalTenth").value();
        assertTrue(value instanceof BigDecimal);
        assertEquals(0, ((BigDecimal) value).compareTo(new BigDecimal("0.1")));

        value = results.forExpression("StringTrue").value();
        assertTrue(value instanceof String);
        assertEquals("true", value);

        value = results.forExpression("DateTimeX").value();
        assertTrue(value instanceof DateTime);
        assertTrue(((DateTime) value).equal(new DateTime(new BigDecimal("0.0"), 2012, 2, 15, 12, 10, 59, 456)));

        value = results.forExpression("DateTimeFX").value();
        assertTrue(value instanceof DateTime);
        assertTrue(((DateTime) value).equal(new DateTime(new BigDecimal("0.0"), 2012, 2, 15, 12, 10, 59, 456)));

        value = results.forExpression("TimeX").value();
        assertTrue(value instanceof Time);
        assertTrue(((Time) value).equal(new Time(12, 10, 59, 456)));

        value = results.expressionResults.get("DateTime_Year").value();
        assertTrue(value instanceof DateTime);
        assertTrue(((DateTime) value).equal(new DateTime(bigDecimalZoneOffset, 2012)));

        value = results.expressionResults.get("DateTime_Month").value();
        assertTrue(value instanceof DateTime);
        assertTrue(((DateTime) value).equal(new DateTime(bigDecimalZoneOffset, 2012, 2)));

        value = results.expressionResults.get("DateTime_Day").value();
        assertTrue(value instanceof DateTime);
        assertTrue(((DateTime) value).equal(new DateTime(bigDecimalZoneOffset, 2012, 2, 15)));

        value = results.expressionResults.get("DateTime_Hour").value();
        assertTrue(value instanceof DateTime);
        assertTrue(((DateTime) value).equal(new DateTime(bigDecimalZoneOffset, 2012, 2, 15, 12)));

        value = results.expressionResults.get("DateTime_Minute").value();
        assertTrue(value instanceof DateTime);
        assertTrue(((DateTime) value).equal(new DateTime(bigDecimalZoneOffset, 2012, 2, 15, 12, 10)));

        value = results.expressionResults.get("DateTime_Second").value();
        assertTrue(value instanceof DateTime);
        assertTrue(((DateTime) value).equal(new DateTime(bigDecimalZoneOffset, 2012, 2, 15, 12, 10, 59)));

        value = results.expressionResults.get("DateTime_Millisecond").value();
        assertTrue(value instanceof DateTime);
        assertTrue(((DateTime) value).equal(new DateTime(bigDecimalZoneOffset, 2012, 2, 15, 12, 10, 59, 456)));

        value = results.expressionResults.get("DateTime_TimezoneOffset").value();
        assertTrue(value instanceof DateTime);
        assertTrue(((DateTime) value).equal(new DateTime(new BigDecimal("-8.0"), 2012, 2, 15, 12, 10, 59, 456)));

        value = results.expressionResults.get("Time_Hour").value();
        assertTrue(value instanceof Time);
        assertTrue(((Time) value).equal(new Time(12)));

        value = results.expressionResults.get("Time_Minute").value();
        assertTrue(value instanceof Time);
        assertTrue(((Time) value).equal(new Time(12, 10)));

        value = results.expressionResults.get("Time_Second").value();
        assertTrue(value instanceof Time);
        assertTrue(((Time) value).equal(new Time(12, 10, 59)));

        value = results.expressionResults.get("Time_Millisecond").value();
        assertTrue(value instanceof Time);
        assertTrue(((Time) value).equal(new Time(12, 10, 59, 456)));

        value = results.expressionResults.get("Clinical_quantity").value();
        assertTrue(value instanceof Quantity);
        assertTrue(((Quantity) value)
                .equal(new Quantity().withValue(new BigDecimal(12)).withUnit("a")));

        value = results.expressionResults.get("Clinical_QuantityA").value();
        assertTrue(value instanceof Quantity);
        assertTrue(((Quantity) value)
                .equal(new Quantity().withValue(new BigDecimal(12)).withUnit("a")));

        value = results.expressionResults.get("Clinical_CodeA").value();
        assertTrue(value instanceof Code);
        assertTrue(((Code) value)
                .equal(new Code()
                        .withCode("12345")
                        .withSystem("http://loinc.org")
                        .withVersion("1")
                        .withDisplay("Test Code")));

        value = results.expressionResults.get("Clinical_ConceptA").value();
        assertTrue(value instanceof Concept);
        assertTrue(((Concept) value)
                .equal(new Concept()
                        .withCode(new Code()
                                .withCode("12345")
                                .withSystem("http://loinc.org")
                                .withVersion("1")
                                .withDisplay("Test Code"))
                        .withDisplay("Test Concept")));

        LinkedHashMap<String, Object> elements = new LinkedHashMap<>();
        elements.put("a", 1);
        elements.put("b", 2);
        value = results.expressionResults.get("Structured_tuple").value();
        assertTrue(value instanceof Tuple);
        assertTrue(((Tuple) value).equal(new Tuple(engine.getState()).withElements(elements)));

        elements.clear();
        elements.put("class", "Portable CQL Test Suite");
        elements.put("versionNum", new BigDecimal("1.0"));
        elements.put("date", new DateTime(bigDecimalZoneOffset, 2018, 7, 18));
        elements.put("developer", "Christopher Schuler");

        value = results.expressionResults.get("Structured_TupleA").value();
        assertTrue(value instanceof Tuple);
        assertTrue(((Tuple) value).equal(new Tuple(engine.getState()).withElements(elements)));

        value = results.expressionResults.get("Interval_Open").value();
        assertTrue(value instanceof Interval);
        assertTrue(((Interval) value)
                .equal(new Interval(
                        new DateTime(bigDecimalZoneOffset, 2012, 1, 1), false,
                        new DateTime(bigDecimalZoneOffset, 2013, 1, 1), false)));

        value = results.expressionResults.get("Interval_LeftOpen").value();
        assertTrue(value instanceof Interval);
        assertTrue(((Interval) value)
                .equal(new Interval(
                        new DateTime(bigDecimalZoneOffset, 2012, 1, 1), false,
                        new DateTime(bigDecimalZoneOffset, 2013, 1, 1), true)));

        value = results.expressionResults.get("Interval_RightOpen").value();
        assertTrue(value instanceof Interval);
        assertTrue(((Interval) value)
                .equal(new Interval(
                        new DateTime(bigDecimalZoneOffset, 2012, 1, 1), true,
                        new DateTime(bigDecimalZoneOffset, 2013, 1, 1), false)));

        value = results.expressionResults.get("Interval_Closed").value();
        assertTrue(value instanceof Interval);
        assertTrue(((Interval) value)
                .equal(new Interval(
                        new DateTime(bigDecimalZoneOffset, 2012, 1, 1), true,
                        new DateTime(bigDecimalZoneOffset, 2013, 1, 1), true)));

        value = results.expressionResults.get("List_BoolList").value();
        assertTrue(value instanceof Iterable);
        Boolean listComp = CqlList.equal((Iterable<?>) value, Arrays.asList(true, false, true), engine.getState());
        assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_IntList").value();
        assertTrue(value instanceof Iterable);
        listComp = CqlList.equal((Iterable<?>) value, Arrays.asList(9, 7, 8), engine.getState());
        assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_DecimalList").value();
        assertTrue(value instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) value,
                Arrays.asList(new BigDecimal("1.0"), new BigDecimal("2.1"), new BigDecimal("3.2")),
                engine.getState());
        assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_StringList").value();
        assertTrue(value instanceof Iterable);
        listComp = CqlList.equal((Iterable<?>) value, Arrays.asList("a", "bee", "see"), engine.getState());
        assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_DateTimeList").value();
        assertTrue(value instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) value,
                Arrays.asList(
                        new DateTime(new BigDecimal("0.0"), 2012, 2, 15, 12, 10, 59, 456),
                        new DateTime(new BigDecimal("0.0"), 2012, 3, 15, 12, 10, 59, 456),
                        new DateTime(new BigDecimal("0.0"), 2012, 4, 15, 12, 10, 59, 456)),
                engine.getState());
        assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_TimeList").value();
        assertTrue(value instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) value,
                Arrays.asList(new Time(12, 10, 59, 456), new Time(13, 10, 59, 456), new Time(14, 10, 59, 456)),
                engine.getState());
        assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_QuantityList").value();
        assertTrue(value instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) value,
                Arrays.asList(
                        new Quantity().withValue(new BigDecimal("1.0")).withUnit("m"),
                        new Quantity().withValue(new BigDecimal("2.1")).withUnit("m"),
                        new Quantity().withValue(new BigDecimal("3.2")).withUnit("m")),
                engine.getState());
        assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_CodeList").value();
        assertTrue(value instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) value,
                Arrays.asList(
                        new Code()
                                .withCode("12345")
                                .withSystem("http://loinc.org")
                                .withVersion("1")
                                .withDisplay("Test Code"),
                        new Code()
                                .withCode("123456")
                                .withSystem("http://loinc.org")
                                .withVersion("1")
                                .withDisplay("Another Test Code")),
                engine.getState());
        assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_ConceptList").value();
        assertTrue(value instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) value,
                Arrays.asList(
                        new Concept()
                                .withCode(new Code()
                                        .withCode("12345")
                                        .withSystem("http://loinc.org")
                                        .withVersion("1")
                                        .withDisplay("Test Code"))
                                .withDisplay("Test Concept"),
                        new Concept()
                                .withCode(new Code()
                                        .withCode("123456")
                                        .withSystem("http://loinc.org")
                                        .withVersion("1")
                                        .withDisplay("Another Test Code"))
                                .withDisplay("Another Test Concept")),
                engine.getState());
        assertTrue(listComp != null && listComp);

        elements.clear();
        elements.put("a", 1);
        elements.put("b", "2");
        LinkedHashMap<String, Object> elements2 = new LinkedHashMap<>();
        elements2.put("x", 2);
        elements2.put("z", "3");
        value = results.expressionResults.get("List_TupleList").value();
        assertTrue(value instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) value,
                Arrays.asList(
                        new Tuple(engine.getState()).withElements(elements),
                        new Tuple(engine.getState()).withElements(elements2)),
                engine.getState());
        assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_ListList").value();
        assertTrue(value instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) value,
                Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList("a", "b", "c")),
                engine.getState());
        assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_IntervalList").value();
        assertTrue(value instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) value,
                Arrays.asList(
                        new Interval(1, true, 5, true),
                        new Interval(5, false, 9, false),
                        new Interval(8, true, 10, false)),
                engine.getState());
        assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_MixedList").value();
        assertTrue(value instanceof Iterable);
        listComp = CqlList.equal((Iterable<?>) value, Arrays.asList(1, "two", 3), engine.getState());
        assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_EmptyList").value();
        assertTrue(value instanceof Iterable);
        listComp = CqlList.equal((Iterable<?>) value, Collections.EMPTY_LIST, engine.getState());
        assertTrue(listComp != null && listComp);
    }
}
