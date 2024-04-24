package org.opencds.cqf.cql.engine.fhir.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.opencds.cqf.cql.engine.fhir.converter.ConverterTestUtils.*;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import org.apache.commons.lang3.NotImplementedException;
import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.Base;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.DateType;
import org.hl7.fhir.dstu3.model.DecimalType;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.InstantType;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Range;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.TimeType;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.opencds.cqf.cql.engine.runtime.*;

class Dstu3TypeConverterTests {

    private static Dstu3FhirTypeConverter typeConverter;

    protected Boolean compareIterables(Iterable<Object> left, Iterable<Object> right) {
        Iterator<Object> leftIterator = left.iterator();
        Iterator<Object> rightIterator = right.iterator();

        while (leftIterator.hasNext() && rightIterator.hasNext()) {
            Object currentL = leftIterator.next();
            Object currentR = rightIterator.next();

            Boolean result = compareObjects(currentL, currentR);
            if (!result) {
                return false;
            }
        }

        return !leftIterator.hasNext() && !rightIterator.hasNext();
    }

    @SuppressWarnings("unchecked")
    protected Boolean compareObjects(Object left, Object right) {
        if (left == null ^ right == null) {
            return false;
        }

        if (left == null && right == null) {
            return true;
        }

        if (!left.getClass().equals(right.getClass())) {
            return false;
        }

        if (left instanceof Iterable<?>) {
            return compareIterables((Iterable<Object>) left, (Iterable<Object>) right);
        }

        if (left instanceof CqlType) {
            return ((CqlType) left).equals((CqlType) right);
        }

        if (left instanceof Base) {
            return ((Base) left).equalsDeep((Base) right);
        }

        return left.equals(right);
    }

    @BeforeAll
    static void initialize() {
        typeConverter = new Dstu3FhirTypeConverter();
    }

    // CQL-to-FHIR
    @Test
    void isFhirType() {
        assertTrue(typeConverter.isFhirType(new Patient()));
        assertTrue(typeConverter.isFhirType(new IdType()));
        assertTrue(typeConverter.isFhirType(new org.hl7.fhir.dstu3.model.Quantity()));
        assertTrue(typeConverter.isFhirType(new org.hl7.fhir.dstu3.model.Ratio()));
        assertTrue(typeConverter.isFhirType(new BooleanType()));
        assertTrue(typeConverter.isFhirType(new IntegerType()));
        assertTrue(typeConverter.isFhirType(new DecimalType()));
        assertTrue(typeConverter.isFhirType(new DateType()));
        assertTrue(typeConverter.isFhirType(new InstantType()));
        assertTrue(typeConverter.isFhirType(new DateTimeType()));
        assertTrue(typeConverter.isFhirType(new TimeType()));
        assertTrue(typeConverter.isFhirType(new StringType()));
        assertTrue(typeConverter.isFhirType(new Coding()));
        assertTrue(typeConverter.isFhirType(new CodeableConcept()));
        assertTrue(typeConverter.isFhirType(new Period()));
        assertTrue(typeConverter.isFhirType(new Range()));

        assertFalse(typeConverter.isFhirType(5));
        assertFalse(typeConverter.isFhirType(new BigDecimal(0)));
        assertFalse(typeConverter.isFhirType(new Code()));
    }

    @Test
    void nullIsFhirType() {
        assertThrows(NullPointerException.class, () -> {
            typeConverter.isFhirType(null);
        });
    }

    @Test
    void iterableIsFhirType() {
        var value = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> {
            typeConverter.isFhirType(value);
        });
    }

    @Test
    void toFhirType() {
        IBase actual = typeConverter.toFhirType(new Code());
        assertThat(actual, instanceOf(Coding.class));

        actual = typeConverter.toFhirType(5);
        assertThat(actual, instanceOf(IntegerType.class));

        actual = typeConverter.toFhirType(new IdType());
        assertThat(actual, instanceOf(IdType.class));

        actual = typeConverter.toFhirType(null);
        assertNull(null);
    }

    @Test
    void toFhirTypeIterable() {
        var value = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> {
            typeConverter.toFhirType(value);
        });
    }

    @Test
    void toFhirTypeNotCql() {
        var offset = ZoneOffset.ofHours(3);
        assertThrows(IllegalArgumentException.class, () -> {
            typeConverter.toFhirType(offset);
        });
    }

    @Test
    void toFhirTypes() {
        List<Object> innerExpected = new ArrayList<>();
        innerExpected.add(new StringType("123"));
        innerExpected.add(null);
        List<Object> expected = new ArrayList<>();
        expected.add(innerExpected);
        expected.add(null);
        expected.add(new IntegerType(5));

        List<Object> innerTest = new ArrayList<>();
        innerTest.add("123");
        innerTest.add(null);
        List<Object> test = new ArrayList<>();
        test.add(innerTest);
        test.add(null);
        test.add(5);

        Iterable<Object> actual = typeConverter.toFhirTypes(test);

        assertTrue(compareIterables(expected, actual));
    }

    @Test
    void stringToFhirId() {
        IIdType expected = new IdType("123");
        IIdType actual = typeConverter.toFhirId("123");
        assertEquals(expected.getValue(), actual.getValue());

        actual = typeConverter.toFhirId(null);
        assertNull(actual);
    }

    @Test
    void primitiveCqlTypeToFhirType() {
        IPrimitiveType<Boolean> expectedBoolean = new BooleanType(false);
        IPrimitiveType<Boolean> actualBoolean = typeConverter.toFhirBoolean(false);
        assertEquals(expectedBoolean.getValue(), actualBoolean.getValue());

        expectedBoolean = typeConverter.toFhirBoolean(null);
        assertNull(expectedBoolean);

        IPrimitiveType<Integer> expectedInteger = new IntegerType(5);
        IPrimitiveType<Integer> actualInteger = typeConverter.toFhirInteger(5);
        assertEquals(expectedInteger.getValue(), actualInteger.getValue());

        expectedInteger = typeConverter.toFhirInteger(null);
        assertNull(expectedInteger);

        IPrimitiveType<String> expectedString = new StringType("5");
        IPrimitiveType<String> actualString = typeConverter.toFhirString("5");
        assertEquals(expectedString.getValue(), actualString.getValue());

        expectedString = typeConverter.toFhirString(null);
        assertNull(expectedString);

        IPrimitiveType<BigDecimal> expectedDecimal = new DecimalType(new BigDecimal(2.0));
        IPrimitiveType<BigDecimal> actualDecimal = typeConverter.toFhirDecimal(new BigDecimal(2.0));
        assertEquals(expectedDecimal.getValue(), actualDecimal.getValue());

        expectedDecimal = typeConverter.toFhirDecimal(null);
        assertNull(expectedDecimal);
    }

    @Test
    void dateToFhirDate() {
        IPrimitiveType<java.util.Date> expectedDate = new DateType("2019-02-03");
        IPrimitiveType<java.util.Date> actualDate = typeConverter.toFhirDate(new Date("2019-02-03"));
        assertEquals(expectedDate.getValue(), actualDate.getValue());

        expectedDate = new DateType("2019");
        actualDate = typeConverter.toFhirDate(new Date("2019"));
        assertEquals(expectedDate.getValue(), actualDate.getValue());
    }

    private static Object[][] nowsAndEvaluationTimes() {
        return ConverterTestUtils.nowsAndEvaluationTimes();
    }

    @ParameterizedTest
    @MethodSource("nowsAndEvaluationTimes")
    void dateTimeToFhirDateTime(LocalDateTime now, LocalDateTime evaluationTime) {
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault());
        final ZoneOffset defaultOffset = zonedDateTime.getOffset();

        final String evalTimeWithOffset =
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(evaluationTime.atOffset(defaultOffset));
        final String evalDate = DateTimeFormatter.ISO_DATE.format(evaluationTime);

        var expectedDate = new DateTimeType(evalTimeWithOffset);
        IPrimitiveType<java.util.Date> actualDate = typeConverter.toFhirDateTime(new DateTime(evalDate, defaultOffset));
        assertEquals(expectedDate.getValue(), actualDate.getValue());

        expectedDate = new DateTimeType(evalTimeWithOffset);
        actualDate = typeConverter.toFhirDateTime(new DateTime("" + evaluationTime.getYear(), defaultOffset));
        expectedDate.setPrecision(TemporalPrecisionEnum.YEAR);
        assertEquals(expectedDate.getValue(), actualDate.getValue());
        assertEquals(expectedDate.getValueAsString(), actualDate.getValueAsString());
    }

    @Test
    void dateTimeToFhirDateTimeTimezones() {
        var expectedDate = new DateTimeType("2019-10-10T01:00:00-06:00");
        ((DateTimeType) expectedDate).setTimeZone(TimeZone.getTimeZone("MST"));
        var actualDate = typeConverter.toFhirDateTime(new DateTime("2019-10-10T00:00:00", ZoneOffset.ofHours(-7)));
        assertEquals(expectedDate.getValueAsString(), actualDate.getValueAsString());

        expectedDate = new DateTimeType("2019-10-10T19:35:53.000Z");
        ((DateTimeType) expectedDate).setPrecision(TemporalPrecisionEnum.MILLI);
        actualDate = typeConverter.toFhirDateTime(
                new DateTime("2019-10-10T19:35:53", ZoneOffset.UTC).withPrecision(Precision.MILLISECOND));
        assertEquals(expectedDate.getValueAsString(), actualDate.getValueAsString());
    }

    @Test
    void quantityToFhirQuantity() {
        org.hl7.fhir.dstu3.model.Quantity expected = new org.hl7.fhir.dstu3.model.Quantity()
                .setValue(new BigDecimal("2.0"))
                .setCode("ml")
                .setSystem("http://unitsofmeasure.org")
                .setUnit("ml");
        org.hl7.fhir.dstu3.model.Quantity actual = (org.hl7.fhir.dstu3.model.Quantity) typeConverter.toFhirQuantity(
                new Quantity().withValue(new BigDecimal("2.0")).withUnit("ml"));
        assertTrue(expected.equalsDeep(actual));
    }

    @Test
    void ratioToFhirRatio() {
        org.hl7.fhir.dstu3.model.Quantity expectedNumerator = new org.hl7.fhir.dstu3.model.Quantity()
                .setValue(new BigDecimal("1.0"))
                .setCode("ml")
                .setSystem("http://unitsofmeasure.org")
                .setUnit("ml");
        org.hl7.fhir.dstu3.model.Quantity expectedDenominator = new org.hl7.fhir.dstu3.model.Quantity()
                .setValue(new BigDecimal("2.0"))
                .setCode("ml")
                .setSystem("http://unitsofmeasure.org")
                .setUnit("ml");

        org.hl7.fhir.dstu3.model.Ratio expected = new org.hl7.fhir.dstu3.model.Ratio()
                .setNumerator(expectedNumerator)
                .setDenominator(expectedDenominator);

        Ratio testData = new Ratio();
        testData.setNumerator(new Quantity().withValue(BigDecimal.valueOf(1.0)).withUnit("ml"));
        testData.setDenominator(
                new Quantity().withValue(BigDecimal.valueOf(2.0)).withUnit("ml"));

        org.hl7.fhir.dstu3.model.Ratio actual = (org.hl7.fhir.dstu3.model.Ratio) typeConverter.toFhirRatio(testData);

        assertTrue(expected.equalsDeep(actual));
    }

    @Test
    void nullToFhirAny() {
        IBase expected = typeConverter.toFhirAny(null);
        assertNull(expected);
    }

    @Test
    void objectToFhirAny() {
        assertThrows(NotImplementedException.class, () -> {
            typeConverter.toFhirAny("Huh");
        });
    }

    @Test
    void codeToFhirCoding() {
        Coding expected = new Coding()
                .setSystem("http://the-system.com")
                .setCode("test")
                .setDisplay("system-test")
                .setVersion("1.5");
        Coding actual = (Coding) typeConverter.toFhirCoding(new Code()
                .withSystem("http://the-system.com")
                .withCode("test")
                .withDisplay("system-test")
                .withVersion("1.5"));
        assertTrue(expected.equalsDeep(actual));

        expected = (Coding) typeConverter.toFhirCoding(null);
        assertNull(expected);
    }

    @Test
    void conceptToFhirCodeableConcept() {
        CodeableConcept expected = new CodeableConcept(new Coding()
                        .setSystem("http://the-system.com")
                        .setCode("test")
                        .setDisplay("system-test")
                        .setVersion("1.5"))
                .setText("additional-text");
        CodeableConcept actual = (CodeableConcept) typeConverter.toFhirCodeableConcept(new Concept()
                .withCode(new Code()
                        .withSystem("http://the-system.com")
                        .withCode("test")
                        .withDisplay("system-test")
                        .withVersion("1.5"))
                .withDisplay("additional-text"));
        assertTrue(expected.equalsDeep(actual));

        expected = (CodeableConcept) typeConverter.toFhirCodeableConcept(null);
        assertNull(expected);
    }

    private static Object[][] startAndEndTimes() {
        return ConverterTestUtils.startAndEndTimes();
    }

    @ParameterizedTest
    @MethodSource("startAndEndTimes")
    void intervalToFhirPeriodYyyyMMdd(LocalDateTime startTime, LocalDateTime endTime) {
        final String startTime_yyyyMMdd = YYYY_MM_DD.format(startTime);
        final String endTime_yyyyMMdd = YYYY_MM_DD.format(endTime);

        final Period expected = new Period()
                .setStartElement(new DateTimeType(startTime_yyyyMMdd))
                .setEndElement(new DateTimeType(endTime_yyyyMMdd));
        final Period actual = (Period) typeConverter.toFhirPeriod(
                new Interval(new Date(startTime_yyyyMMdd), true, new Date(endTime_yyyyMMdd), true));
        assertTrue(expected.equalsDeep(actual));
    }

    private static Object[][] dateTimes() {
        return ConverterTestUtils.dateTimes();
    }

    @ParameterizedTest
    @MethodSource("dateTimes")
    void intervalToFhirPeriodTimestampWithOffsets(LocalDateTime now, LocalDateTime startTime, LocalDateTime endTime) {
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault());
        final ZoneOffset defaultOffset = zonedDateTime.getOffset();

        final String startTimeWithOffset =
                DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(startTime.atOffset(defaultOffset));
        final String endTimeWithOffset = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(endTime.atOffset(defaultOffset));
        final String startTimeNoOffset = DateTimeFormatter.ISO_DATE_TIME.format(startTime.atOffset(defaultOffset));
        final String endTimeNoOffset = DateTimeFormatter.ISO_DATE_TIME.format(endTime.atOffset(defaultOffset));

        final DateTimeType dateTimeTypeStart = new DateTimeType(startTimeWithOffset);
        final DateTimeType dateTimeTypeEnd = new DateTimeType(endTimeWithOffset);
        var expected = new Period().setStartElement(dateTimeTypeStart).setEndElement(dateTimeTypeEnd);

        final DateTime dateTimeStart = new DateTime(startTimeNoOffset, defaultOffset);
        final DateTime dateTimeEnd = new DateTime(endTimeNoOffset, defaultOffset);
        final Interval intervalStartEnd = new Interval(dateTimeStart, true, dateTimeEnd, true);
        var actual = (Period) typeConverter.toFhirPeriod(intervalStartEnd);

        assertTrue(expected.equalsDeep(actual));
    }

    private static Object[][] startAndEndYears() {
        return ConverterTestUtils.startAndEndYears();
    }

    @ParameterizedTest
    @MethodSource("startAndEndYears")
    void intervalToFhirPeriodStartAndEndYears(LocalDateTime now, int startYear, int endYear) {
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault());
        final ZoneOffset defaultOffset = zonedDateTime.getOffset();

        final Period expected = new Period()
                .setStartElement(new DateTimeType(startYear + "-01-01T00:00:00" + defaultOffset))
                .setEndElement(new DateTimeType(endYear + "-01-01T00:00:00" + defaultOffset));
        final Period actual = (Period) typeConverter.toFhirPeriod(new Interval(
                new DateTime("" + startYear, defaultOffset), true, new DateTime("" + endYear, defaultOffset), true));
        assertTrue(expected.equalsDeep(actual));
    }

    @Test
    void intervalToFhirPeriodNull() {
        assertNull(typeConverter.toFhirPeriod(null));
    }

    @Test
    void invalidIntervalToFhirPeriod() {
        var interval = new Interval(5, true, 6, true);
        assertThrows(IllegalArgumentException.class, () -> {
            typeConverter.toFhirPeriod(interval);
        });
    }

    @Test
    void intervalToFhirRange() {
        Range expected = new Range()
                .setLow((SimpleQuantity) new org.hl7.fhir.dstu3.model.SimpleQuantity()
                        .setValue(2.0)
                        .setCode("ml")
                        .setUnit("ml")
                        .setSystem("http://unitsofmeasure.org"))
                .setHigh((SimpleQuantity) new org.hl7.fhir.dstu3.model.SimpleQuantity()
                        .setValue(5.0)
                        .setCode("ml")
                        .setUnit("ml")
                        .setSystem("http://unitsofmeasure.org"));
        Range actual = (Range) typeConverter.toFhirRange(new Interval(
                new Quantity().withValue(new BigDecimal("2.0")).withUnit("ml"),
                true,
                new Quantity().withValue(new BigDecimal("5.0")).withUnit("ml"),
                true));
        assertTrue(expected.equalsDeep(actual));

        actual = (Range) typeConverter.toFhirRange(null);
        assertNull(null);
    }

    @Test
    void invalidIntervalToFhirRange() {
        var interval = new Interval(5, true, 6, true);
        assertThrows(IllegalArgumentException.class, () -> {
            typeConverter.toFhirRange(interval);
        });
    }

    @Test
    void intervalToFhirInterval() {
        Period expectedPeriod = new Period()
                .setStartElement(new DateTimeType("2019-02-03"))
                .setEndElement(new DateTimeType("2019-02-05"));
        Period actualPeriod = (Period)
                typeConverter.toFhirInterval(new Interval(new Date("2019-02-03"), true, new Date("2019-02-05"), true));
        assertTrue(expectedPeriod.equalsDeep(actualPeriod));

        Range expectedRange = new Range()
                .setLow((SimpleQuantity) new org.hl7.fhir.dstu3.model.SimpleQuantity()
                        .setValue(2.0)
                        .setCode("ml")
                        .setUnit("ml")
                        .setSystem("http://unitsofmeasure.org"))
                .setHigh((SimpleQuantity) new org.hl7.fhir.dstu3.model.SimpleQuantity()
                        .setValue(5.0)
                        .setCode("ml")
                        .setUnit("ml")
                        .setSystem("http://unitsofmeasure.org"));
        Range actualRange = (Range) typeConverter.toFhirInterval(new Interval(
                new Quantity().withValue(new BigDecimal("2.0")).withUnit("ml"),
                true,
                new Quantity().withValue(new BigDecimal("5.0")).withUnit("ml"),
                true));
        assertTrue(expectedRange.equalsDeep(actualRange));

        ICompositeType expected = typeConverter.toFhirInterval(null);
        assertNull(expected);
    }

    @Test
    void invalidIntervalToFhirInterval() {
        var interval = new Interval(5, true, 6, true);

        assertThrows(IllegalArgumentException.class, () -> {
            typeConverter.toFhirInterval(interval);
        });
    }

    @Test
    void tupleToFhirTuple() {
        IBase expected = typeConverter.toFhirTuple(null);
        assertNull(expected);

        var tuple = new Tuple();
        assertThrows(NotImplementedException.class, () -> {
            typeConverter.toFhirTuple(tuple);
        });
    }

    // FHIR-to-CQL
    @Test
    void isCqlType() {
        assertTrue(typeConverter.isCqlType(5));
        assertTrue(typeConverter.isCqlType(new BigDecimal(0)));
        assertTrue(typeConverter.isCqlType(new Code()));

        assertFalse(typeConverter.isCqlType(new Patient()));
        assertFalse(typeConverter.isCqlType(new IdType()));
    }

    @Test
    void nullIsCqlType() {
        assertThrows(NullPointerException.class, () -> typeConverter.isCqlType(null));
    }

    @Test
    void iterableIsCqlType() {
        var value = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> typeConverter.isCqlType(value));
    }

    @Test
    void toCqlType() {
        Object actual = typeConverter.toCqlType(new Code());
        assertThat(actual, instanceOf(Code.class));

        actual = typeConverter.toCqlType(new IntegerType(5));
        assertThat(actual, instanceOf(Integer.class));

        actual = typeConverter.toCqlType(new StringType("test"));
        assertThat(actual, instanceOf(String.class));

        actual = typeConverter.toCqlType(new IdType("test"));
        assertThat(actual, instanceOf(String.class));

        actual = typeConverter.toCqlType(new BooleanType(true));
        assertThat(actual, instanceOf(Boolean.class));

        actual = typeConverter.toCqlType(new DecimalType(1.0));
        assertThat(actual, instanceOf(BigDecimal.class));

        actual = typeConverter.toCqlType(new DateType(Calendar.getInstance().getTime()));
        assertThat(actual, instanceOf(Date.class));

        actual = typeConverter.toCqlType(new InstantType(Calendar.getInstance()));
        assertThat(actual, instanceOf(DateTime.class));

        actual = typeConverter.toCqlType(new DateTimeType(Calendar.getInstance()));
        assertThat(actual, instanceOf(DateTime.class));

        actual = typeConverter.toCqlType(new TimeType("10:00:00.0000"));
        assertThat(actual, instanceOf(Time.class));

        actual = typeConverter.toCqlType(new StringType("test"));
        assertThat(actual, instanceOf(String.class));

        actual = typeConverter.toCqlType(new org.hl7.fhir.dstu3.model.Quantity());
        assertThat(actual, instanceOf(Quantity.class));

        actual = typeConverter.toCqlType(new org.hl7.fhir.dstu3.model.Ratio());
        assertThat(actual, instanceOf(Ratio.class));

        actual = typeConverter.toCqlType(new Coding());
        assertThat(actual, instanceOf(Code.class));

        actual = typeConverter.toCqlType(new CodeableConcept());
        assertThat(actual, instanceOf(Concept.class));

        actual = typeConverter.toCqlType(new Period()
                .setStart(Calendar.getInstance().getTime())
                .setEnd(Calendar.getInstance().getTime()));
        assertThat(actual, instanceOf(Interval.class));

        SimpleQuantity low = new SimpleQuantity();
        low.setValue(BigDecimal.valueOf(1.0));
        low.setUnit("d");

        SimpleQuantity high = new SimpleQuantity();
        high.setValue(BigDecimal.valueOf(4.0));
        high.setUnit("d");
        actual = typeConverter.toCqlType(new Range().setLow(low).setHigh(high));
        assertThat(actual, instanceOf(Interval.class));

        actual = typeConverter.toCqlType(null);
        assertNull(null);
    }

    @Test
    void toCqlTypeIterable() {
        assertThrows(IllegalArgumentException.class, () -> {
            typeConverter.toCqlType(new ArrayList<>());
        });
    }

    @Test
    void toCqlTypeNotCql() {
        var offset = ZoneOffset.ofHours(3);
        assertThrows(IllegalArgumentException.class, () -> {
            typeConverter.toCqlType(offset);
        });
    }

    @Test
    void toCqlTypes() {

        List<Object> innerExpected = new ArrayList<>();
        innerExpected.add("123");
        innerExpected.add(null);
        List<Object> expected = new ArrayList<>();
        expected.add(innerExpected);
        expected.add(null);
        expected.add(5);

        List<Object> innerTest = new ArrayList<>();
        innerTest.add(new StringType("123"));
        innerTest.add(null);
        List<Object> test = new ArrayList<>();
        test.add(innerTest);
        test.add(null);
        test.add(new IntegerType(5));

        Iterable<Object> actual = typeConverter.toCqlTypes(test);

        assertTrue(compareIterables(expected, actual));
    }

    @Test
    void stringToCqlId() {
        String expected = "123";
        String actual = typeConverter.toCqlId(new IdType("123"));
        assertEquals(expected, actual);

        actual = typeConverter.toCqlId(null);
        assertNull(actual);
    }

    @Test
    void primitiveFhirTypeToCqlType() {
        Boolean expectedBoolean = false;
        Boolean actualBoolean = typeConverter.toCqlBoolean(new BooleanType(false));
        assertEquals(expectedBoolean, actualBoolean);

        expectedBoolean = typeConverter.toCqlBoolean(null);
        assertNull(expectedBoolean);

        Integer expectedInteger = 5;
        Integer actualInteger = typeConverter.toCqlInteger(new IntegerType(5));
        assertEquals(expectedInteger, actualInteger);

        expectedInteger = typeConverter.toCqlInteger(null);
        assertNull(expectedInteger);

        String expectedString = "5";
        String actualString = typeConverter.toCqlString(new StringType("5"));
        assertEquals(expectedString, actualString);

        expectedString = typeConverter.toCqlString(null);
        assertNull(expectedString);

        BigDecimal expectedDecimal = new BigDecimal(2.0);
        BigDecimal actualDecimal = typeConverter.toCqlDecimal(new DecimalType(new BigDecimal(2.0)));
        assertEquals(expectedDecimal, actualDecimal);

        expectedDecimal = typeConverter.toCqlDecimal(null);
        assertNull(expectedDecimal);
    }

    @Test
    void dateToCqlType() {
        Date expectedDate = new Date("2019-02-03");
        Date actualDate = typeConverter.toCqlDate(new org.hl7.fhir.dstu3.model.DateType("2019-02-03"));
        assertTrue(expectedDate.equal(actualDate));

        expectedDate = new Date("2019");
        actualDate = typeConverter.toCqlDate(new DateType("2019"));
        assertTrue(expectedDate.equal(actualDate));
    }

    @Test
    void dateTimeToCqlType() {
        DateTime expectedDate = new DateTime("2019-02-03", ZoneOffset.UTC);
        DateTime actualDate = typeConverter.toCqlDateTime(new DateTimeType("2019-02-03"));
        assertTrue(expectedDate.equal(actualDate));

        expectedDate = new DateTime("2019", ZoneOffset.UTC);
        actualDate = typeConverter.toCqlDateTime(new DateTimeType("2019"));
        assertTrue(expectedDate.equal(actualDate));

        expectedDate = new DateTime("2019", ZoneOffset.UTC);
        actualDate = typeConverter.toCqlDateTime(new DateTimeType("2019"));
        assertTrue(expectedDate.equal(actualDate));
    }

    @Test
    void quantityToCqlType() {
        Quantity expected = (new Quantity().withValue(new BigDecimal("2.0")).withUnit("ml"));
        Quantity actual = typeConverter.toCqlQuantity(new org.hl7.fhir.dstu3.model.Quantity()
                .setValue(new BigDecimal("2.0"))
                .setUnit("ml")
                .setSystem("http://unitsofmeasure.org"));
        assertTrue(expected.equal(actual));
    }

    @Test
    void ratioToCqlType() {
        Ratio expected = new Ratio();
        expected.setNumerator(new Quantity().withValue(BigDecimal.valueOf(1.0)).withUnit("ml"));
        expected.setDenominator(
                new Quantity().withValue(BigDecimal.valueOf(2.0)).withUnit("ml"));

        org.hl7.fhir.dstu3.model.Quantity testNumerator = new org.hl7.fhir.dstu3.model.Quantity()
                .setValue(new BigDecimal("1.0"))
                .setUnit("ml")
                .setSystem("http://unitsofmeasure.org");
        org.hl7.fhir.dstu3.model.Quantity testDenominator = new org.hl7.fhir.dstu3.model.Quantity()
                .setValue(new BigDecimal("2.0"))
                .setUnit("ml")
                .setSystem("http://unitsofmeasure.org");

        org.hl7.fhir.dstu3.model.Ratio test =
                new org.hl7.fhir.dstu3.model.Ratio().setNumerator(testNumerator).setDenominator(testDenominator);

        Ratio actual = typeConverter.toCqlRatio(test);
        assertTrue(expected.equal(actual));
    }

    @Test
    void nullToCqlType() {
        Object expected = typeConverter.toCqlAny(null);
        assertNull(expected);
    }

    @Test
    void objectToCqlType() {
        var id = new IdType();
        assertThrows(NotImplementedException.class, () -> {
            typeConverter.toCqlAny(id);
        });
    }

    @Test
    void codingToCqlCode() {
        Code expected = new Code()
                .withSystem("http://the-system.com")
                .withCode("test")
                .withDisplay("system-test")
                .withVersion("1.5");
        Code actual = typeConverter.toCqlCode(new Coding()
                .setSystem("http://the-system.com")
                .setCode("test")
                .setDisplay("system-test")
                .setVersion("1.5"));
        assertTrue(expected.equal(actual));

        expected = typeConverter.toCqlCode(null);
        assertNull(expected);
    }

    @Test
    void codeableConceptToCqlConcept() {
        Concept expected = new Concept()
                .withCode(new Code()
                        .withSystem("http://the-system.com")
                        .withCode("test")
                        .withDisplay("system-test")
                        .withVersion("1.5"))
                .withDisplay("additional-text");
        Concept actual = typeConverter.toCqlConcept(new CodeableConcept(new Coding()
                        .setSystem("http://the-system.com")
                        .setCode("test")
                        .setDisplay("system-test")
                        .setVersion("1.5"))
                .setText("additional-text"));

        assertTrue(expected.equal(actual));

        expected = typeConverter.toCqlConcept(null);
        assertNull(expected);
    }

    @Test
    void periodToCqlInterval() {
        Interval expected = new Interval(new Date("2019-02-03"), true, new Date("2019-02-05"), true);
        Interval actual = typeConverter.toCqlInterval(new Period()
                .setStartElement(new DateTimeType("2019-02-03"))
                .setEndElement(new DateTimeType("2019-02-05")));
        assertTrue(expected.equal(actual));

        expected = new Interval(new Date("2019"), true, new Date("2020"), true);
        actual = typeConverter.toCqlInterval(
                new Period().setStartElement(new DateTimeType("2019")).setEndElement(new DateTimeType("2020")));
        assertTrue(expected.equal(actual));

        expected = new Interval(
                new DateTime("2020-09-18T19:35:53", ZoneOffset.UTC),
                true,
                new DateTime("2020-09-18T19:37:00", ZoneOffset.UTC),
                true);
        actual = typeConverter.toCqlInterval(new Period()
                .setStartElement(new DateTimeType("2020-09-18T19:35:53+00:00"))
                .setEndElement(new DateTimeType("2020-09-18T19:37:00+00:00")));
        assertTrue(expected.equal(actual));

        actual = typeConverter.toCqlInterval(null);
        assertNull(null);
    }

    @Test
    void rangeToCqlInterval() {
        Interval expected = new Interval(
                new Quantity().withValue(new BigDecimal("2.0")).withUnit("ml"),
                true,
                new Quantity().withValue(new BigDecimal("5.0")).withUnit("ml"),
                true);
        Interval actual = typeConverter.toCqlInterval(new Range()
                .setLow((SimpleQuantity) new org.hl7.fhir.dstu3.model.SimpleQuantity()
                        .setValue(new BigDecimal("2.0"))
                        .setUnit("ml")
                        .setSystem("http://unitsofmeasure.org"))
                .setHigh((SimpleQuantity) new org.hl7.fhir.dstu3.model.SimpleQuantity()
                        .setValue(new BigDecimal("5.0"))
                        .setUnit("ml")
                        .setSystem("http://unitsofmeasure.org")));
        assertTrue(expected.equal(actual));

        actual = typeConverter.toCqlInterval(null);
        assertNull(null);
    }

    @Test
    void invalidTypeToCqlInterval() {
        var attachment = new Attachment();
        assertThrows(IllegalArgumentException.class, () -> {
            typeConverter.toCqlInterval(attachment);
        });
    }

    @Test
    void tupleToCqlTuple() {
        Object expected = typeConverter.toCqlTuple(null);
        assertNull(expected);

        var p = new Patient();
        assertThrows(NotImplementedException.class, () -> {
            typeConverter.toCqlTuple(p);
        });
    }
}
