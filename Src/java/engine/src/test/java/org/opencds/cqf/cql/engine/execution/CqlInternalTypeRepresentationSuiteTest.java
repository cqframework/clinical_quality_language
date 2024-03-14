package org.opencds.cqf.cql.engine.execution;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import org.opencds.cqf.cql.engine.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CqlInternalTypeRepresentationSuiteTest extends CqlTestBase {
    private static final Logger logger = LoggerFactory.getLogger(CqlInternalTypeRepresentationSuiteTest.class);

    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId MONTREAL = ZoneId.of("America/Montreal");
    private static final ZoneId REGINA =
            ZoneId.of("America/Regina"); // Saskatchewan does not have standard time (non-DST) all year round
    private static final LocalDateTime DST_2023_11_01 = LocalDateTime.of(2023, Month.NOVEMBER, 1, 0, 0, 0);
    private static final LocalDateTime NON_DST_2023_11_13 = LocalDateTime.of(2023, Month.NOVEMBER, 13, 0, 0, 0);
    private static final LocalDateTime NON_DST_2018_01_01 = LocalDateTime.of(2018, Month.JANUARY, 1, 7, 0, 0);

    @DataProvider
    private static Object[][] timeZones() {
        return new Object[][] {
            {UTC, DST_2023_11_01}, {MONTREAL, DST_2023_11_01}, {REGINA, DST_2023_11_01},
            {UTC, NON_DST_2023_11_13}, {MONTREAL, NON_DST_2023_11_13}, {REGINA, NON_DST_2023_11_13},
            {UTC, NON_DST_2018_01_01}, {MONTREAL, NON_DST_2018_01_01}, {REGINA, NON_DST_2018_01_01}
        };
    }

    @Test(dataProvider = "timeZones")
    public void test_all_internal_type_representation(ZoneId zoneId, LocalDateTime now) {
        var results =
                engine.evaluate(toElmIdentifier("CqlInternalTypeRepresentationSuite"), ZonedDateTime.of(now, zoneId));

        final BigDecimal bigDecimalZoneOffset = getBigDecimalZoneOffset();

        var value = results.forExpression("BoolTrue").value();
        Assert.assertTrue(value instanceof Boolean);
        Assert.assertTrue((Boolean) value);

        value = results.forExpression("BoolFalse").value();
        Assert.assertTrue(value instanceof Boolean);
        Assert.assertTrue(!(Boolean) value);

        value = results.forExpression("IntOne").value();
        Assert.assertTrue(value instanceof Integer);
        Assert.assertTrue((Integer) value == 1);

        value = results.forExpression("DecimalTenth").value();
        Assert.assertTrue(value instanceof BigDecimal);
        Assert.assertTrue(((BigDecimal) value).compareTo(new BigDecimal("0.1")) == 0);

        value = results.forExpression("StringTrue").value();
        Assert.assertTrue(value instanceof String);
        Assert.assertTrue(value.equals("true"));

        value = results.forExpression("DateTimeX").value();
        Assert.assertTrue(value instanceof DateTime);
        Assert.assertTrue(((DateTime) value).equal(new DateTime(new BigDecimal("0.0"), 2012, 2, 15, 12, 10, 59, 456)));

        value = results.forExpression("DateTimeFX").value();
        Assert.assertTrue(value instanceof DateTime);
        Assert.assertTrue(((DateTime) value).equal(new DateTime(new BigDecimal("0.0"), 2012, 2, 15, 12, 10, 59, 456)));

        value = results.forExpression("TimeX").value();
        Assert.assertTrue(value instanceof Time);
        Assert.assertTrue(((Time) value).equal(new Time(12, 10, 59, 456)));

        value = results.expressionResults.get("DateTime_Year").value();
        Assert.assertTrue(value instanceof DateTime);
        Assert.assertTrue(((DateTime) value).equal(new DateTime(bigDecimalZoneOffset, 2012)));

        value = results.expressionResults.get("DateTime_Month").value();
        Assert.assertTrue(value instanceof DateTime);
        Assert.assertTrue(((DateTime) value).equal(new DateTime(bigDecimalZoneOffset, 2012, 2)));

        value = results.expressionResults.get("DateTime_Day").value();
        Assert.assertTrue(value instanceof DateTime);
        Assert.assertTrue(((DateTime) value).equal(new DateTime(bigDecimalZoneOffset, 2012, 2, 15)));

        value = results.expressionResults.get("DateTime_Hour").value();
        Assert.assertTrue(value instanceof DateTime);
        Assert.assertTrue(((DateTime) value).equal(new DateTime(bigDecimalZoneOffset, 2012, 2, 15, 12)));

        value = results.expressionResults.get("DateTime_Minute").value();
        Assert.assertTrue(value instanceof DateTime);
        Assert.assertTrue(((DateTime) value).equal(new DateTime(bigDecimalZoneOffset, 2012, 2, 15, 12, 10)));

        value = results.expressionResults.get("DateTime_Second").value();
        Assert.assertTrue(value instanceof DateTime);
        Assert.assertTrue(((DateTime) value).equal(new DateTime(bigDecimalZoneOffset, 2012, 2, 15, 12, 10, 59)));

        value = results.expressionResults.get("DateTime_Millisecond").value();
        Assert.assertTrue(value instanceof DateTime);
        Assert.assertTrue(((DateTime) value).equal(new DateTime(bigDecimalZoneOffset, 2012, 2, 15, 12, 10, 59, 456)));

        value = results.expressionResults.get("DateTime_TimezoneOffset").value();
        Assert.assertTrue(value instanceof DateTime);
        Assert.assertTrue(((DateTime) value).equal(new DateTime(new BigDecimal("-8.0"), 2012, 2, 15, 12, 10, 59, 456)));

        value = results.expressionResults.get("Time_Hour").value();
        Assert.assertTrue(value instanceof Time);
        Assert.assertTrue(((Time) value).equal(new Time(12)));

        value = results.expressionResults.get("Time_Minute").value();
        Assert.assertTrue(value instanceof Time);
        Assert.assertTrue(((Time) value).equal(new Time(12, 10)));

        value = results.expressionResults.get("Time_Second").value();
        Assert.assertTrue(value instanceof Time);
        Assert.assertTrue(((Time) value).equal(new Time(12, 10, 59)));

        value = results.expressionResults.get("Time_Millisecond").value();
        Assert.assertTrue(value instanceof Time);
        Assert.assertTrue(((Time) value).equal(new Time(12, 10, 59, 456)));

        value = results.expressionResults.get("Clinical_quantity").value();
        Assert.assertTrue(value instanceof Quantity);
        Assert.assertTrue(((Quantity) value)
                .equal(new Quantity().withValue(new BigDecimal(12)).withUnit("a")));

        value = results.expressionResults.get("Clinical_QuantityA").value();
        Assert.assertTrue(value instanceof Quantity);
        Assert.assertTrue(((Quantity) value)
                .equal(new Quantity().withValue(new BigDecimal(12)).withUnit("a")));

        value = results.expressionResults.get("Clinical_CodeA").value();
        Assert.assertTrue(value instanceof Code);
        Assert.assertTrue(((Code) value)
                .equal(new Code()
                        .withCode("12345")
                        .withSystem("http://loinc.org")
                        .withVersion("1")
                        .withDisplay("Test Code")));

        value = results.expressionResults.get("Clinical_ConceptA").value();
        Assert.assertTrue(value instanceof Concept);
        Assert.assertTrue(((Concept) value)
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
        Assert.assertTrue(value instanceof Tuple);
        Assert.assertTrue(((Tuple) value).equal(new Tuple(engine.getState()).withElements(elements)));

        elements.clear();
        elements.put("class", "Portable CQL Test Suite");
        elements.put("versionNum", new BigDecimal("1.0"));
        elements.put("date", new DateTime(bigDecimalZoneOffset, 2018, 7, 18));
        elements.put("developer", "Christopher Schuler");

        value = results.expressionResults.get("Structured_TupleA").value();
        Assert.assertTrue(value instanceof Tuple);
        Assert.assertTrue(((Tuple) value).equal(new Tuple(engine.getState()).withElements(elements)));

        value = results.expressionResults.get("Interval_Open").value();
        Assert.assertTrue(value instanceof Interval);
        Assert.assertTrue(((Interval) value)
                .equal(new Interval(
                        new DateTime(bigDecimalZoneOffset, 2012, 1, 1), false,
                        new DateTime(bigDecimalZoneOffset, 2013, 1, 1), false)));

        value = results.expressionResults.get("Interval_LeftOpen").value();
        Assert.assertTrue(value instanceof Interval);
        Assert.assertTrue(((Interval) value)
                .equal(new Interval(
                        new DateTime(bigDecimalZoneOffset, 2012, 1, 1), false,
                        new DateTime(bigDecimalZoneOffset, 2013, 1, 1), true)));

        value = results.expressionResults.get("Interval_RightOpen").value();
        Assert.assertTrue(value instanceof Interval);
        Assert.assertTrue(((Interval) value)
                .equal(new Interval(
                        new DateTime(bigDecimalZoneOffset, 2012, 1, 1), true,
                        new DateTime(bigDecimalZoneOffset, 2013, 1, 1), false)));

        value = results.expressionResults.get("Interval_Closed").value();
        Assert.assertTrue(value instanceof Interval);
        Assert.assertTrue(((Interval) value)
                .equal(new Interval(
                        new DateTime(bigDecimalZoneOffset, 2012, 1, 1), true,
                        new DateTime(bigDecimalZoneOffset, 2013, 1, 1), true)));

        value = results.expressionResults.get("List_BoolList").value();
        Assert.assertTrue(value instanceof Iterable);
        Boolean listComp = CqlList.equal((Iterable<?>) value, Arrays.asList(true, false, true), engine.getState());
        Assert.assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_IntList").value();
        Assert.assertTrue(value instanceof Iterable);
        listComp = CqlList.equal((Iterable<?>) value, Arrays.asList(9, 7, 8), engine.getState());
        Assert.assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_DecimalList").value();
        Assert.assertTrue(value instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) value,
                Arrays.asList(new BigDecimal("1.0"), new BigDecimal("2.1"), new BigDecimal("3.2")),
                engine.getState());
        Assert.assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_StringList").value();
        Assert.assertTrue(value instanceof Iterable);
        listComp = CqlList.equal((Iterable<?>) value, Arrays.asList("a", "bee", "see"), engine.getState());
        Assert.assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_DateTimeList").value();
        Assert.assertTrue(value instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) value,
                Arrays.asList(
                        new DateTime(new BigDecimal("0.0"), 2012, 2, 15, 12, 10, 59, 456),
                        new DateTime(new BigDecimal("0.0"), 2012, 3, 15, 12, 10, 59, 456),
                        new DateTime(new BigDecimal("0.0"), 2012, 4, 15, 12, 10, 59, 456)),
                engine.getState());
        Assert.assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_TimeList").value();
        Assert.assertTrue(value instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) value,
                Arrays.asList(new Time(12, 10, 59, 456), new Time(13, 10, 59, 456), new Time(14, 10, 59, 456)),
                engine.getState());
        Assert.assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_QuantityList").value();
        Assert.assertTrue(value instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) value,
                Arrays.asList(
                        new Quantity().withValue(new BigDecimal("1.0")).withUnit("m"),
                        new Quantity().withValue(new BigDecimal("2.1")).withUnit("m"),
                        new Quantity().withValue(new BigDecimal("3.2")).withUnit("m")),
                engine.getState());
        Assert.assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_CodeList").value();
        Assert.assertTrue(value instanceof Iterable);
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
        Assert.assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_ConceptList").value();
        Assert.assertTrue(value instanceof Iterable);
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
        Assert.assertTrue(listComp != null && listComp);

        elements.clear();
        elements.put("a", 1);
        elements.put("b", "2");
        LinkedHashMap<String, Object> elements2 = new LinkedHashMap<>();
        elements2.put("x", 2);
        elements2.put("z", "3");
        value = results.expressionResults.get("List_TupleList").value();
        Assert.assertTrue(value instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) value,
                Arrays.asList(
                        new Tuple(engine.getState()).withElements(elements),
                        new Tuple(engine.getState()).withElements(elements2)),
                engine.getState());
        Assert.assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_ListList").value();
        Assert.assertTrue(value instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) value,
                Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList("a", "b", "c")),
                engine.getState());
        Assert.assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_IntervalList").value();
        Assert.assertTrue(value instanceof Iterable);
        listComp = CqlList.equal(
                (Iterable<?>) value,
                Arrays.asList(
                        new Interval(1, true, 5, true),
                        new Interval(5, false, 9, false),
                        new Interval(8, true, 10, false)),
                engine.getState());
        Assert.assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_MixedList").value();
        Assert.assertTrue(value instanceof Iterable);
        listComp = CqlList.equal((Iterable<?>) value, Arrays.asList(1, "two", 3), engine.getState());
        Assert.assertTrue(listComp != null && listComp);

        value = results.expressionResults.get("List_EmptyList").value();
        Assert.assertTrue(value instanceof Iterable);
        listComp = CqlList.equal((Iterable<?>) value, Collections.EMPTY_LIST, engine.getState());
        Assert.assertTrue(listComp != null && listComp);
    }
}
