package org.opencds.cqf.cql.engine.fhir.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.opencds.cqf.cql.engine.fhir.converter.ConverterTestUtils.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.NotImplementedException;
import org.hl7.fhir.dstu2.model.Attachment;
import org.hl7.fhir.dstu2.model.Base;
import org.hl7.fhir.dstu2.model.BooleanType;
import org.hl7.fhir.dstu2.model.CodeableConcept;
import org.hl7.fhir.dstu2.model.Coding;
import org.hl7.fhir.dstu2.model.DateTimeType;
import org.hl7.fhir.dstu2.model.DateType;
import org.hl7.fhir.dstu2.model.DecimalType;
import org.hl7.fhir.dstu2.model.IdType;
import org.hl7.fhir.dstu2.model.InstantType;
import org.hl7.fhir.dstu2.model.IntegerType;
import org.hl7.fhir.dstu2.model.Patient;
import org.hl7.fhir.dstu2.model.Period;
import org.hl7.fhir.dstu2.model.Range;
import org.hl7.fhir.dstu2.model.SimpleQuantity;
import org.hl7.fhir.dstu2.model.StringType;
import org.hl7.fhir.dstu2.model.TimeType;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.opencds.cqf.cql.engine.runtime.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;

public class Dstu2TypeConverterTests {

    private Dstu2FhirTypeConverter typeConverter;

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

        return !leftIterator.hasNext() && !leftIterator.hasNext();
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

    @BeforeClass
    public void initialize() {
        this.typeConverter = new Dstu2FhirTypeConverter();
    }

    // CQL-to-FHIR
    @Test
    public void TestIsFhirType() {
        assertTrue(this.typeConverter.isFhirType(new Patient()));
        assertTrue(this.typeConverter.isFhirType(new IdType()));
        assertTrue(this.typeConverter.isFhirType(new org.hl7.fhir.dstu2.model.Quantity()));
        assertTrue(this.typeConverter.isFhirType(new org.hl7.fhir.dstu2.model.Ratio()));
        assertTrue(this.typeConverter.isFhirType(new BooleanType()));
        assertTrue(this.typeConverter.isFhirType(new IntegerType()));
        assertTrue(this.typeConverter.isFhirType(new DecimalType()));
        assertTrue(this.typeConverter.isFhirType(new DateType()));
        assertTrue(this.typeConverter.isFhirType(new InstantType()));
        assertTrue(this.typeConverter.isFhirType(new DateTimeType()));
        assertTrue(this.typeConverter.isFhirType(new TimeType()));
        assertTrue(this.typeConverter.isFhirType(new StringType()));
        assertTrue(this.typeConverter.isFhirType(new Coding()));
        assertTrue(this.typeConverter.isFhirType(new CodeableConcept()));
        assertTrue(this.typeConverter.isFhirType(new Period()));
        assertTrue(this.typeConverter.isFhirType(new Range()));

        assertFalse(this.typeConverter.isFhirType(5));
        assertFalse(this.typeConverter.isFhirType(new BigDecimal(0)));
        assertFalse(this.typeConverter.isFhirType(new Code()));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void TestNullIsFhirType() {
        this.typeConverter.isFhirType(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void TestIterableIsFhirType() {
        this.typeConverter.isFhirType(new ArrayList<>());
    }

    @Test
    public void TestToFhirType() {
        IBase actual = this.typeConverter.toFhirType(new Code());
        assertThat(actual, instanceOf(Coding.class));

        actual = this.typeConverter.toFhirType(5);
        assertThat(actual, instanceOf(IntegerType.class));

        actual = this.typeConverter.toFhirType(new IdType());
        assertThat(actual, instanceOf(IdType.class));

        actual = this.typeConverter.toFhirType(null);
        assertNull(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void TestToFhirTypeIterable() {
        this.typeConverter.toFhirType(new ArrayList<>());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void TestToFhirTypeNotCql() {
        this.typeConverter.toFhirType(ZoneOffset.ofHours(3));
    }

    @Test
    public void TestToFhirTypes() {
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

        Iterable<Object> actual = this.typeConverter.toFhirTypes(test);

        assertTrue(compareIterables(expected, actual));
    }

    @Test
    public void TestStringToFhirId() {
        IIdType expected = new IdType("123");
        IIdType actual = this.typeConverter.toFhirId("123");
        assertEquals(expected.getValue(), actual.getValue());

        actual = this.typeConverter.toFhirId(null);
        assertNull(actual);
    }

    @Test
    public void TestPrimitiveCqlTypeToFhirType() {
        IPrimitiveType<Boolean> expectedBoolean = new BooleanType(false);
        IPrimitiveType<Boolean> actualBoolean = this.typeConverter.toFhirBoolean(false);
        assertEquals(expectedBoolean.getValue(), actualBoolean.getValue());

        expectedBoolean = this.typeConverter.toFhirBoolean(null);
        assertNull(expectedBoolean);

        IPrimitiveType<Integer> expectedInteger = new IntegerType(5);
        IPrimitiveType<Integer> actualInteger = this.typeConverter.toFhirInteger(5);
        assertEquals(expectedInteger.getValue(), actualInteger.getValue());

        expectedInteger = this.typeConverter.toFhirInteger(null);
        assertNull(expectedInteger);

        IPrimitiveType<String> expectedString = new StringType("5");
        IPrimitiveType<String> actualString = this.typeConverter.toFhirString("5");
        assertEquals(expectedString.getValue(), actualString.getValue());

        expectedString = this.typeConverter.toFhirString(null);
        assertNull(expectedString);

        IPrimitiveType<BigDecimal> expectedDecimal = new DecimalType(new BigDecimal(2.0));
        IPrimitiveType<BigDecimal> actualDecimal = this.typeConverter.toFhirDecimal(new BigDecimal(2.0));
        assertEquals(expectedDecimal.getValue(), actualDecimal.getValue());

        expectedDecimal = this.typeConverter.toFhirDecimal(null);
        assertNull(expectedDecimal);
    }

    @Test
    public void TestDateToFhirDate() {
        IPrimitiveType<java.util.Date> expectedDate = new DateType("2019-02-03");
        IPrimitiveType<java.util.Date> actualDate = this.typeConverter.toFhirDate(new Date("2019-02-03"));
        assertEquals(expectedDate.getValue(), actualDate.getValue());

        expectedDate = new DateType("2019");
        actualDate = this.typeConverter.toFhirDate(new Date("2019"));
        assertEquals(expectedDate.getValue(), actualDate.getValue());
    }


    @DataProvider
    private static Object[][] nowsAndEvaluationTimes() {
        return ConverterTestUtils.nowsAndEvaluationTimes();
    }

    @Test(dataProvider = "nowsAndEvaluationTimes")
    public void TestDateTimeToFhirDateTime(LocalDateTime now, LocalDateTime evaluationTime) {
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault());
        final ZoneOffset defaultOffset = zonedDateTime.getOffset();

        final String evalTimeWithOffset = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(evaluationTime.atOffset(defaultOffset));
        final String evalDate = DateTimeFormatter.ISO_DATE.format(evaluationTime);

        var expectedDate = new DateTimeType(evalTimeWithOffset);
        IPrimitiveType<java.util.Date> actualDate = this.typeConverter
                .toFhirDateTime(new DateTime(evalDate, defaultOffset));
        assertEquals(expectedDate.getValue(), actualDate.getValue());

        expectedDate = new DateTimeType(evalTimeWithOffset);
        actualDate = this.typeConverter.toFhirDateTime(new DateTime(""+evaluationTime.getYear(), defaultOffset));
        expectedDate.setPrecision(TemporalPrecisionEnum.YEAR);
        assertEquals(expectedDate.getValue(), actualDate.getValue());
        assertEquals(expectedDate.getValueAsString(), actualDate.getValueAsString());
    }

    @Test
    public void TestDateTimeToFhirDateTime_Timezones() {
        var expectedDate = new DateTimeType("2019-10-10T01:00:00-06:00");
        ((DateTimeType) expectedDate).setTimeZone(TimeZone.getTimeZone("MST"));
        var actualDate = this.typeConverter.toFhirDateTime(new DateTime("2019-10-10T00:00:00", ZoneOffset.ofHours(-7)));
        assertEquals(expectedDate.getValueAsString(), actualDate.getValueAsString());

        expectedDate = new DateTimeType("2019-10-10T19:35:53.000Z");
        ((DateTimeType) expectedDate).setPrecision(TemporalPrecisionEnum.MILLI);
        actualDate = this.typeConverter.toFhirDateTime(new DateTime("2019-10-10T19:35:53", ZoneOffset.UTC).withPrecision(Precision.MILLISECOND));
        assertEquals(expectedDate.getValueAsString(), actualDate.getValueAsString());
    }

    @Test
    public void TestQuantityToFhirQuantity() {
        org.hl7.fhir.dstu2.model.Quantity expected = new org.hl7.fhir.dstu2.model.Quantity().setValue(new BigDecimal("2.0")).setCode("ml")
                .setSystem("http://unitsofmeasure.org").setUnit("ml");
        org.hl7.fhir.dstu2.model.Quantity actual = (org.hl7.fhir.dstu2.model.Quantity) this.typeConverter
                .toFhirQuantity(new Quantity().withValue(new BigDecimal("2.0")).withUnit("ml"));
        assertTrue(expected.equalsDeep(actual));
    }

    @Test
    public void TestRatioToFhirRatio() {
        org.hl7.fhir.dstu2.model.Quantity expectedNumerator = new org.hl7.fhir.dstu2.model.Quantity().setValue(new BigDecimal("1.0")).setCode("ml")
                .setSystem("http://unitsofmeasure.org").setUnit("ml");
        org.hl7.fhir.dstu2.model.Quantity expectedDenominator = new org.hl7.fhir.dstu2.model.Quantity().setValue(new BigDecimal("2.0")).setCode("ml")
                .setSystem("http://unitsofmeasure.org").setUnit("ml");

        org.hl7.fhir.dstu2.model.Ratio expected = new org.hl7.fhir.dstu2.model.Ratio().setNumerator(expectedNumerator)
                .setDenominator(expectedDenominator);

        Ratio testData = new Ratio();
        testData.setNumerator(new Quantity().withValue(BigDecimal.valueOf(1.0)).withUnit("ml"));
        testData.setDenominator(new Quantity().withValue(BigDecimal.valueOf(2.0)).withUnit("ml"));

        org.hl7.fhir.dstu2.model.Ratio actual = (org.hl7.fhir.dstu2.model.Ratio) this.typeConverter.toFhirRatio(testData);

        assertTrue(expected.equalsDeep(actual));
    }

    @Test()
    public void TestNullToFhirAny() {
        IBase expected = this.typeConverter.toFhirAny(null);
        assertNull(expected);
    }

    @Test(expectedExceptions = NotImplementedException.class)
    public void TestObjectToFhirAny() {
        this.typeConverter.toFhirAny("Huh");
    }

    @Test
    public void TestCodeToFhirCoding() {
        Coding expected = new Coding().setSystem("http://the-system.com").setCode("test").setDisplay("system-test").setVersion("1.5");
        Coding actual = (Coding) this.typeConverter.toFhirCoding(new Code().withSystem("http://the-system.com")
                .withCode("test").withDisplay("system-test").withVersion("1.5"));
        assertTrue(expected.equalsDeep(actual));

        expected = (Coding) this.typeConverter.toFhirCoding(null);
        assertNull(expected);
    }

    @Test
    public void TestConceptToFhirCodeableConcept() {
        CodeableConcept expected = new CodeableConcept(
            new Coding().setSystem("http://the-system.com").setCode("test").setDisplay("system-test").setVersion("1.5"))
                        .setText("additional-text");
        CodeableConcept actual = (CodeableConcept) this.typeConverter.toFhirCodeableConcept(
                new Concept().withCode(new Code().withSystem("http://the-system.com").withCode("test")
                        .withDisplay("system-test").withVersion("1.5")).withDisplay("additional-text"));
        assertTrue(expected.equalsDeep(actual));

        expected = (CodeableConcept) this.typeConverter.toFhirCodeableConcept(null);
        assertNull(expected);
    }

    @DataProvider
    private static Object[][] startAndEndTimes() {
        return ConverterTestUtils.startAndEndTimes();
    }

    @Test(dataProvider = "startAndEndTimes")
    public void TestIntervalToFhirPeriod_yyyyMMdd(LocalDateTime startTime, LocalDateTime endTime) {
        final String startTime_yyyyMMdd = YYYY_MM_DD.format(startTime);
        final String endTime_yyyyMMdd = YYYY_MM_DD.format(endTime);

        final Period expected = new Period().setStartElement(new DateTimeType(startTime_yyyyMMdd))
                .setEndElement(new DateTimeType(endTime_yyyyMMdd));
        final Period actual = (Period) this.typeConverter
                .toFhirPeriod(new Interval(new Date(startTime_yyyyMMdd), true, new Date(endTime_yyyyMMdd), true));
        assertTrue(expected.equalsDeep(actual));
    }

    @DataProvider
    private static Object[][] dateTimes() {
        return ConverterTestUtils.dateTimes();
    }

    @Test(dataProvider = "dateTimes")
    public void TestIntervalToFhirPeriod_timestampWithOffsets(LocalDateTime now, LocalDateTime startTime, LocalDateTime endTime) {
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault());
        final ZoneOffset defaultOffset = zonedDateTime.getOffset();

        final String startTimeWithOffset = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(startTime.atOffset(defaultOffset));
        final String endTimeWithOffset = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(endTime.atOffset(defaultOffset));
        final String startTimeNoOffset = DateTimeFormatter.ISO_DATE_TIME.format(startTime.atOffset(defaultOffset));
        final String endTimeNoOffset = DateTimeFormatter.ISO_DATE_TIME.format(endTime.atOffset(defaultOffset));

        final DateTimeType dateTimeTypeStart = new DateTimeType(startTimeWithOffset);
        final DateTimeType dateTimeTypeEnd = new DateTimeType(endTimeWithOffset);
        var expected = new Period().setStartElement(dateTimeTypeStart).setEndElement(dateTimeTypeEnd);

        final DateTime dateTimeStart = new DateTime(startTimeNoOffset, defaultOffset);
        final DateTime dateTimeEnd = new DateTime(endTimeNoOffset, defaultOffset);
        final Interval intervalStartEnd = new Interval(dateTimeStart, true, dateTimeEnd, true);
        var actual = (Period) this.typeConverter.toFhirPeriod(intervalStartEnd);

        assertTrue(expected.equalsDeep(actual));
    }

    @DataProvider
    private static Object[][] startAndEndYears() {
        return ConverterTestUtils.startAndEndYears();
    }

    @Test(dataProvider = "startAndEndYears")
    public void TestIntervalToFhirPeriod_startAndEndYears(LocalDateTime now, int startYear, int endYear) {
        final ZonedDateTime zonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault());
        final ZoneOffset defaultOffset = zonedDateTime.getOffset();

        final Period expected = new Period().setStartElement(new DateTimeType(startYear+"-01-01T00:00:00"+defaultOffset)).setEndElement(new DateTimeType(endYear+"-01-01T00:00:00"+defaultOffset));
        final Period actual = (Period) this.typeConverter.toFhirPeriod(
                new Interval(new DateTime(""+startYear, defaultOffset), true, new DateTime(""+endYear, defaultOffset), true));
        assertTrue(expected.equalsDeep(actual));
    }

    @Test
    public void TestIntervalToFhirPeriod_null() {
        assertNull(this.typeConverter.toFhirPeriod(null));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void TestInvalidIntervalToFhirPeriod() {
        this.typeConverter.toFhirPeriod(new Interval(5, true, 6, true));
    }

    @Test
    public void TestIntervalToFhirRange() {
        Range expected = new Range()
                .setLow((SimpleQuantity)new org.hl7.fhir.dstu2.model.SimpleQuantity().setValue(new BigDecimal("2.0")).setCode("ml").setSystem("http://unitsofmeasure.org"))
                .setHigh((SimpleQuantity)new org.hl7.fhir.dstu2.model.SimpleQuantity().setValue(new BigDecimal("5.0")).setCode("ml").setSystem("http://unitsofmeasure.org"));
        Range actual = (Range) this.typeConverter
                .toFhirRange(new Interval(new Quantity().withValue(new BigDecimal("2.0")).withUnit("ml"), true,
                        new Quantity().withValue(new BigDecimal("5.0")).withUnit("ml"), true));
        assertTrue(expected.equalsDeep(actual));

        actual = (Range) this.typeConverter.toFhirRange(null);
        assertNull(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void TestInvalidIntervalToFhirRange() {
        this.typeConverter.toFhirRange(new Interval(5, true, 6, true));
    }

    @Test
    public void TestIntervalToFhirInterval() {
        Period expectedPeriod = new Period().setStartElement(new DateTimeType("2019-02-03"))
                .setEndElement(new DateTimeType("2019-02-05"));
        Period actualPeriod = (Period) this.typeConverter
                .toFhirInterval(new Interval(new Date("2019-02-03"), true, new Date("2019-02-05"), true));
        assertTrue(expectedPeriod.equalsDeep(actualPeriod));

        Range expectedRange = new Range()
                .setLow((SimpleQuantity)new org.hl7.fhir.dstu2.model.SimpleQuantity().setValue(new BigDecimal("2.0")).setCode("ml").setSystem("http://unitsofmeasure.org"))
                .setHigh((SimpleQuantity)new org.hl7.fhir.dstu2.model.SimpleQuantity().setValue(new BigDecimal("5.0")).setCode("ml").setSystem("http://unitsofmeasure.org"));
        Range actualRange = (Range) this.typeConverter
                .toFhirInterval(new Interval(new Quantity().withValue(new BigDecimal("2.0")).withUnit("ml"), true,
                        new Quantity().withValue(new BigDecimal("5.0")).withUnit("ml"), true));
        assertTrue(expectedRange.equalsDeep(actualRange));

        ICompositeType expected = this.typeConverter.toFhirInterval(null);
        assertNull(expected);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void TestInvalidIntervalToFhirInterval() {
        this.typeConverter.toFhirInterval(new Interval(5, true, 6, true));
    }

    @Test(expectedExceptions = NotImplementedException.class)
    public void TestTupleToFhirTuple() {
        IBase expected = this.typeConverter.toFhirTuple(null);
        assertNull(expected);

        this.typeConverter.toFhirTuple(new Tuple());
    }

    // FHIR-to-CQL
    @Test
    public void TestIsCqlType() {
        assertTrue(this.typeConverter.isCqlType(5));
        assertTrue(this.typeConverter.isCqlType(new BigDecimal(0)));
        assertTrue(this.typeConverter.isCqlType(new Code()));

        assertFalse(this.typeConverter.isCqlType(new Patient()));
        assertFalse(this.typeConverter.isCqlType(new IdType()));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void TestNullIsCqlType() {
        this.typeConverter.isCqlType(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void TestIterableIsCqlType() {
        this.typeConverter.isCqlType(new ArrayList<>());
    }

    @Test
    public void TestToCqlType() {
        Object actual = this.typeConverter.toCqlType(new Code());
        assertThat(actual, instanceOf(Code.class));

        actual = this.typeConverter.toCqlType(new IntegerType(5));
        assertThat(actual, instanceOf(Integer.class));

        actual = this.typeConverter.toCqlType(new StringType("test"));
        assertThat(actual, instanceOf(String.class));

        actual = this.typeConverter.toCqlType(new IdType("test"));
        assertThat(actual, instanceOf(String.class));

        actual = this.typeConverter.toCqlType(new BooleanType(true));
        assertThat(actual, instanceOf(Boolean.class));

        actual = this.typeConverter.toCqlType(new DecimalType(1.0));
        assertThat(actual, instanceOf(BigDecimal.class));

        actual = this.typeConverter.toCqlType(new DateType(Calendar.getInstance().getTime()));
        assertThat(actual, instanceOf(Date.class));

        actual = this.typeConverter.toCqlType(new InstantType(Calendar.getInstance()));
        assertThat(actual, instanceOf(DateTime.class));

        actual = this.typeConverter.toCqlType(new DateTimeType(Calendar.getInstance()));
        assertThat(actual, instanceOf(DateTime.class));

        actual = this.typeConverter.toCqlType(new TimeType("10:00:00.0000"));
        assertThat(actual, instanceOf(Time.class));

        actual = this.typeConverter.toCqlType(new StringType("test"));
        assertThat(actual, instanceOf(String.class));

        actual = this.typeConverter.toCqlType(new org.hl7.fhir.dstu2.model.Quantity());
        assertThat(actual, instanceOf( Quantity.class));

        actual = this.typeConverter.toCqlType(new org.hl7.fhir.dstu2.model.Ratio());
        assertThat(actual, instanceOf(Ratio.class));

        actual = this.typeConverter.toCqlType(new Coding());
        assertThat(actual, instanceOf(Code.class));

        actual = this.typeConverter.toCqlType(new CodeableConcept());
        assertThat(actual, instanceOf(Concept.class));

        actual = this.typeConverter.toCqlType(new Period().setStart(Calendar.getInstance().getTime()).setEnd(Calendar.getInstance().getTime()));
        assertThat(actual, instanceOf(Interval.class));

        SimpleQuantity low = new SimpleQuantity();
        low.setValue(BigDecimal.valueOf(1.0));
        low.setUnit("d");

        SimpleQuantity high = new SimpleQuantity();
        high.setValue(BigDecimal.valueOf(4.0));
        high.setUnit("d");
        actual = this.typeConverter.toCqlType(new Range().setLow(low).setHigh(high));
        assertThat(actual, instanceOf(Interval.class));

        actual = this.typeConverter.toCqlType(null);
        assertNull(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void TestToCqlTypeIterable() {
        this.typeConverter.toCqlType(new ArrayList<>());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void TestToCqlTypeNotCql() {
        this.typeConverter.toCqlType(ZoneOffset.ofHours(3));
    }

    @Test
    public void TestToCqlTypes() {

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

        Iterable<Object> actual = this.typeConverter.toCqlTypes(test);

        assertTrue(compareIterables(expected, actual));
    }

    @Test
    public void TestStringToCqlId() {
        String expected = "123";
        String actual = this.typeConverter.toCqlId(new IdType("123"));
        assertEquals(expected, actual);

        actual = this.typeConverter.toCqlId(null);
        assertNull(actual);
    }

    @Test
    public void TestPrimitiveFhirTypeToCqlType() {
        Boolean expectedBoolean = false;
        Boolean actualBoolean = this.typeConverter.toCqlBoolean(new BooleanType(false));
        assertEquals(expectedBoolean, actualBoolean);

        expectedBoolean = this.typeConverter.toCqlBoolean(null);
        assertNull(expectedBoolean);

        Integer expectedInteger = 5;
        Integer actualInteger = this.typeConverter.toCqlInteger(new IntegerType(5));
        assertEquals(expectedInteger, actualInteger);

        expectedInteger = this.typeConverter.toCqlInteger(null);
        assertNull(expectedInteger);

        String expectedString = "5";
        String actualString = this.typeConverter.toCqlString(new StringType("5"));
        assertEquals(expectedString, actualString);

        expectedString = this.typeConverter.toCqlString(null);
        assertNull(expectedString);

        BigDecimal expectedDecimal = new BigDecimal(2.0);
        BigDecimal actualDecimal = this.typeConverter.toCqlDecimal(new DecimalType(new BigDecimal(2.0)));
        assertEquals(expectedDecimal, actualDecimal);

        expectedDecimal = this.typeConverter.toCqlDecimal(null);
        assertNull(expectedDecimal);
    }

    @Test
    public void TestDateToCqlType() {
        Date expectedDate = new Date("2019-02-03");
        Date actualDate = this.typeConverter.toCqlDate(new org.hl7.fhir.dstu2.model.DateType("2019-02-03"));
        assertTrue(expectedDate.equal(actualDate));

        expectedDate = new Date("2019");
        actualDate = this.typeConverter.toCqlDate(new DateType("2019"));
        assertTrue(expectedDate.equal(actualDate));
    }

    @Test
    public void TestDateTimeToCqlType() {
        DateTime expectedDate = new DateTime("2019-02-03", ZoneOffset.UTC);
        DateTime actualDate = this.typeConverter.toCqlDateTime(new DateTimeType("2019-02-03"));
        assertTrue(expectedDate.equal(actualDate));

        expectedDate = new DateTime("2019", ZoneOffset.UTC);
        actualDate = this.typeConverter.toCqlDateTime(new DateTimeType("2019"));
        assertTrue(expectedDate.equal(actualDate));

        expectedDate = new DateTime("2019", ZoneOffset.UTC);
        actualDate = this.typeConverter.toCqlDateTime(new DateTimeType("2019"));
        assertTrue(expectedDate.equal(actualDate));
    }

    @Test
    public void TestQuantityToCqlType() {
        Quantity expected = (new Quantity().withValue(new BigDecimal("2.0")).withUnit("ml"));
        Quantity actual = this.typeConverter
                .toCqlQuantity(new org.hl7.fhir.dstu2.model.Quantity().setValue(new BigDecimal("2.0")).setUnit("ml")
                .setSystem("http://unitsofmeasure.org"));
        assertTrue(expected.equal(actual));
    }

    @Test
    public void TestRatioToCqlType() {
        Ratio expected = new Ratio();
        expected.setNumerator(new Quantity().withValue(BigDecimal.valueOf(1.0)).withUnit("ml"));
        expected.setDenominator(new Quantity().withValue(BigDecimal.valueOf(2.0)).withUnit("ml"));

        org.hl7.fhir.dstu2.model.Quantity testNumerator = new org.hl7.fhir.dstu2.model.Quantity().setValue(new BigDecimal("1.0")).setUnit("ml")
                .setSystem("http://unitsofmeasure.org");
        org.hl7.fhir.dstu2.model.Quantity testDenominator = new org.hl7.fhir.dstu2.model.Quantity().setValue(new BigDecimal("2.0")).setUnit("ml")
                .setSystem("http://unitsofmeasure.org");

        org.hl7.fhir.dstu2.model.Ratio test = new org.hl7.fhir.dstu2.model.Ratio().setNumerator(testNumerator)
                .setDenominator(testDenominator);

        Ratio actual = this.typeConverter.toCqlRatio(test);
        assertTrue(expected.equal(actual));
    }

    @Test()
    public void TestNullToCqlType() {
        Object expected = this.typeConverter.toCqlAny(null);
        assertNull(expected);
    }

    @Test(expectedExceptions = NotImplementedException.class)
    public void TestObjectToCqlType() {
        this.typeConverter.toCqlAny(new IdType());
    }

    @Test
    public void TestCodingToCqlCode() {
        Code expected = new Code().withSystem("http://the-system.com")
        .withCode("test").withDisplay("system-test").withVersion("1.5");
        Code actual = this.typeConverter.toCqlCode(new Coding().setSystem("http://the-system.com").setCode("test").setDisplay("system-test").setVersion("1.5"));
        assertTrue(expected.equal(actual));

        expected = this.typeConverter.toCqlCode(null);
        assertNull(expected);
    }

    @Test
    public void TestCodeableConceptToCqlConcept() {
        Concept expected = new Concept().withCode(new Code().withSystem("http://the-system.com").withCode("test")
                .withDisplay("system-test").withVersion("1.5")).withDisplay("additional-text");
        Concept actual = this.typeConverter.toCqlConcept(
                new CodeableConcept(new Coding().setSystem("http://the-system.com").setCode("test").setDisplay("system-test").setVersion("1.5"))
                        .setText("additional-text"));

        assertTrue(expected.equal(actual));

        expected = this.typeConverter.toCqlConcept(null);
        assertNull(expected);
    }

    @Test
    public void TestPeriodToCqlInterval() {
        Interval expected = new Interval(new Date("2019-02-03"), true, new Date("2019-02-05"), true);
        Interval actual = this.typeConverter
                .toCqlInterval(new Period().setStartElement(new DateTimeType("2019-02-03"))
                .setEndElement(new DateTimeType("2019-02-05")));
        assertTrue(expected.equal(actual));

        expected = new Interval(new Date("2019"), true, new Date("2020"), true);
        actual = this.typeConverter.toCqlInterval(new Period().setStartElement(new DateTimeType("2019")).setEndElement(new DateTimeType("2020")));
        assertTrue(expected.equal(actual));


        expected = new Interval(new DateTime("2020-09-18T19:35:53", ZoneOffset.UTC), true, new DateTime("2020-09-18T19:37:00", ZoneOffset.UTC), true);
        actual = this.typeConverter.toCqlInterval(new Period().setStartElement(new DateTimeType("2020-09-18T19:35:53+00:00")).setEndElement(new DateTimeType("2020-09-18T19:37:00+00:00")));
        assertTrue(expected.equal(actual));

        actual = this.typeConverter.toCqlInterval(null);
        assertNull(null);
    }

    @Test
    public void TestRangeToCqlInterval() {
        Interval expected = new Interval(new Quantity().withValue(new BigDecimal("2.0")).withUnit("ml"), true,
        new Quantity().withValue(new BigDecimal("5.0")).withUnit("ml"), true);
        Interval actual = this.typeConverter
                .toCqlInterval(new Range()
                .setLow((SimpleQuantity)new org.hl7.fhir.dstu2.model.SimpleQuantity().setValue(new BigDecimal("2.0")).setUnit("ml").setSystem("http://unitsofmeasure.org"))
                .setHigh((SimpleQuantity) new org.hl7.fhir.dstu2.model.SimpleQuantity().setValue(new BigDecimal("5.0")).setUnit("ml").setSystem("http://unitsofmeasure.org")));
        assertTrue(expected.equal(actual));

        actual = this.typeConverter.toCqlInterval(null);
        assertNull(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void TestInvalidTypeToCqlInterval() {
        this.typeConverter.toCqlInterval(new Attachment());
    }

    @Test(expectedExceptions = NotImplementedException.class)
    public void TestTupleToCqlTuple() {
        Object expected = this.typeConverter.toCqlTuple(null);
        assertNull(expected);

        this.typeConverter.toCqlTuple(new Patient());
    }
}
