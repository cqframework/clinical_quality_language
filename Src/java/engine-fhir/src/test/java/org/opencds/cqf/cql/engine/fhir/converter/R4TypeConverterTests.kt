package org.opencds.cqf.cql.engine.fhir.converter

import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import org.apache.commons.lang3.NotImplementedException
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hl7.fhir.instance.model.api.IBase
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome
import org.hl7.fhir.instance.model.api.IIdType
import org.hl7.fhir.instance.model.api.IPrimitiveType
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Range
import org.hl7.fhir.r4.model.Ratio
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Tuple
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors

internal class R4TypeConverterTests {
    protected fun compareIterables(left: Iterable<Any?>, right: Iterable<Any?>): Boolean {
        val leftIterator: MutableIterator<Any?> = left.iterator()
        val rightIterator: MutableIterator<Any?> = right.iterator()

        while (leftIterator.hasNext() && rightIterator.hasNext()) {
            val currentL = leftIterator.next()
            val currentR = rightIterator.next()

            val result = compareObjects(currentL, currentR)
            if (!result) {
                return false
            }
        }

        return !leftIterator.hasNext() && !rightIterator.hasNext()
    }

    protected fun compareObjects(left: Any?, right: Any?): Boolean {
        if ((left == null) xor (right == null)) {
            return false
        }

        if (left == null && right == null) {
            return true
        }

        if (left!!.javaClass != right!!.javaClass) {
            return false
        }

        if (left is Iterable<*>) {
            return compareIterables(left, right as Iterable<Any?>)
        }

        if (left is CqlType) {
            return left == right as CqlType
        }

        if (left is Base) {
            return left.equalsDeep(right as Base)
        }

        return left == right
    }

    // CQL-to-FHIR
    @Test
    fun isFhirType() {
        Assertions.assertTrue(typeConverter!!.isFhirType(Patient()))
        Assertions.assertTrue(typeConverter!!.isFhirType(IdType()))
        Assertions.assertTrue(typeConverter!!.isFhirType(Quantity()))
        Assertions.assertTrue(typeConverter!!.isFhirType(Ratio()))
        Assertions.assertTrue(typeConverter!!.isFhirType(BooleanType()))
        Assertions.assertTrue(typeConverter!!.isFhirType(IntegerType()))
        Assertions.assertTrue(typeConverter!!.isFhirType(DecimalType()))
        Assertions.assertTrue(typeConverter!!.isFhirType(DateType()))
        Assertions.assertTrue(typeConverter!!.isFhirType(InstantType()))
        Assertions.assertTrue(typeConverter!!.isFhirType(DateTimeType()))
        Assertions.assertTrue(typeConverter!!.isFhirType(TimeType()))
        Assertions.assertTrue(typeConverter!!.isFhirType(StringType()))
        Assertions.assertTrue(typeConverter!!.isFhirType(Coding()))
        Assertions.assertTrue(typeConverter!!.isFhirType(CodeableConcept()))
        Assertions.assertTrue(typeConverter!!.isFhirType(Period()))
        Assertions.assertTrue(typeConverter!!.isFhirType(Range()))

        Assertions.assertFalse(typeConverter!!.isFhirType(5))
        Assertions.assertFalse(typeConverter!!.isFhirType(BigDecimal(0)))
        Assertions.assertFalse(typeConverter!!.isFhirType(Code()))
    }

    @Test
    fun nullIsFhirType() {
        Assertions.assertThrows<NullPointerException?>(
            NullPointerException::class.java,
            Executable { typeConverter!!.isFhirType(null) })
    }

    @Test
    fun iterableIsFhirType() {
        val value = ArrayList<Any?>()
        Assertions.assertThrows<IllegalArgumentException?>(
            IllegalArgumentException::class.java,
            Executable { typeConverter!!.isFhirType(value) })
    }

    @Test
    fun toFhirType() {
        var actual: IBase? = typeConverter!!.toFhirType(Code())
        MatcherAssert.assertThat<IBase?>(actual, Matchers.instanceOf<IBase?>(Coding::class.java))

        actual = typeConverter!!.toFhirType(5)
        MatcherAssert.assertThat<IBase?>(actual, Matchers.instanceOf<IBase?>(IntegerType::class.java))

        actual = typeConverter!!.toFhirType(IdType())
        MatcherAssert.assertThat<IBase?>(actual, Matchers.instanceOf<IBase?>(IdType::class.java))

        actual = typeConverter!!.toFhirType(null)
        Assertions.assertNull(actual)
    }

    @Test
    fun toFhirTypeIterable() {
        val value = ArrayList<Any?>()
        Assertions.assertThrows<IllegalArgumentException?>(
            IllegalArgumentException::class.java,
            Executable { typeConverter!!.toFhirType(value) })
    }

    @Test
    fun toFhirTypeNotCql() {
        val offset = ZoneOffset.ofHours(3)
        Assertions.assertThrows<IllegalArgumentException?>(
            IllegalArgumentException::class.java,
            Executable { typeConverter!!.toFhirType(offset) })
    }

    @Test
    fun toFhirTypes() {
        val innerExpected: MutableList<Any?> = ArrayList<Any?>()
        innerExpected.add(StringType("123"))
        innerExpected.add(null)
        val expected: MutableList<Any?> = ArrayList<Any?>()
        expected.add(innerExpected)
        expected.add(null)
        expected.add(IntegerType(5))

        val innerTest: MutableList<Any?> = ArrayList<Any?>()
        innerTest.add("123")
        innerTest.add(null)
        val test: MutableList<Any?> = ArrayList<Any?>()
        test.add(innerTest)
        test.add(null)
        test.add(5)

        val actual: Iterable<Any?> = typeConverter!!.toFhirTypes(test)

        Assertions.assertTrue(compareIterables(expected, actual))
    }

    @Test
    fun stringToFhirId() {
        val expected: IIdType = IdType("123")
        var actual: IIdType = typeConverter!!.toFhirId("123")
        Assertions.assertEquals(expected.getValue(), actual.getValue())

        actual = typeConverter!!.toFhirId(null)
        Assertions.assertNull(actual)
    }

    @Test
    fun primitiveCqlTypeToFhirType() {
        var expectedBoolean: IPrimitiveType<Boolean?> = BooleanType(false)
        val actualBoolean: IPrimitiveType<Boolean?> = typeConverter!!.toFhirBoolean(false)
        Assertions.assertEquals(expectedBoolean.getValue(), actualBoolean.getValue())

        expectedBoolean = typeConverter!!.toFhirBoolean(null)
        Assertions.assertNull(expectedBoolean)

        var expectedInteger: IPrimitiveType<Int?> = IntegerType(5)
        val actualInteger: IPrimitiveType<Int?> = typeConverter!!.toFhirInteger(5)
        Assertions.assertEquals(expectedInteger.getValue(), actualInteger.getValue())

        expectedInteger = typeConverter!!.toFhirInteger(null)
        Assertions.assertNull(expectedInteger)

        var expectedString: IPrimitiveType<String?> = StringType("5")
        val actualString: IPrimitiveType<String?> = typeConverter!!.toFhirString("5")
        Assertions.assertEquals(expectedString.getValue(), actualString.getValue())

        expectedString = typeConverter!!.toFhirString(null)
        Assertions.assertNull(expectedString)

        var expectedDecimal: IPrimitiveType<BigDecimal?> = DecimalType(BigDecimal("2.0"))
        val actualDecimal: IPrimitiveType<BigDecimal?> = typeConverter!!.toFhirDecimal(BigDecimal("2.0"))
        Assertions.assertEquals(expectedDecimal.getValue(), actualDecimal.getValue())

        expectedDecimal = typeConverter!!.toFhirDecimal(null)
        Assertions.assertNull(expectedDecimal)
    }

    @Test
    fun dateToFhirDate() {
        var expectedDate: IPrimitiveType<Date?> = DateType("2019-02-03")
        var actualDate: IPrimitiveType<Date?> =
            typeConverter!!.toFhirDate(org.opencds.cqf.cql.engine.runtime.Date("2019-02-03"))
        Assertions.assertEquals(expectedDate.getValue(), actualDate.getValue())

        expectedDate = DateType("2019")
        actualDate = typeConverter!!.toFhirDate(org.opencds.cqf.cql.engine.runtime.Date("2019"))
        Assertions.assertEquals(expectedDate.getValue(), actualDate.getValue())
    }

    @ParameterizedTest
    @MethodSource("nowsAndEvaluationTimes")
    fun dateTimeToFhirDateTime(now: LocalDateTime, evaluationTime: LocalDateTime) {
        val zonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault())
        val defaultOffset = zonedDateTime.getOffset()

        val evalTimeWithOffset =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(evaluationTime.atOffset(defaultOffset))
        val evalDate = DateTimeFormatter.ISO_DATE.format(evaluationTime)

        var expectedDate = DateTimeType(evalTimeWithOffset)
        var actualDate: IPrimitiveType<Date?> = typeConverter!!.toFhirDateTime(DateTime(evalDate, defaultOffset))
        Assertions.assertEquals(expectedDate.getValue(), actualDate.getValue())

        expectedDate = DateTimeType(evalTimeWithOffset)
        actualDate = typeConverter!!.toFhirDateTime(DateTime("" + evaluationTime.getYear(), defaultOffset))
        expectedDate.setPrecision(TemporalPrecisionEnum.YEAR)
        Assertions.assertEquals(expectedDate.getValue(), actualDate.getValue())
        Assertions.assertEquals(expectedDate.getValueAsString(), actualDate.getValueAsString())
    }

    @Test
    fun dateTimeToFhirDateTimeTimezones() {
        var expectedDate = DateTimeType("2019-10-10T01:00:00-06:00")
        expectedDate.setTimeZone(TimeZone.getTimeZone("MST"))
        var actualDate: IPrimitiveType<Date?> =
            typeConverter!!.toFhirDateTime(DateTime("2019-10-10T00:00:00", ZoneOffset.ofHours(-7)))
        Assertions.assertEquals(expectedDate.getValueAsString(), actualDate.getValueAsString())

        expectedDate = DateTimeType("2019-10-10T19:35:53.000Z")
        expectedDate.setPrecision(TemporalPrecisionEnum.MILLI)
        actualDate = typeConverter!!.toFhirDateTime(
            DateTime("2019-10-10T19:35:53", ZoneOffset.UTC).withPrecision(Precision.MILLISECOND)
        )
        Assertions.assertEquals(expectedDate.getValueAsString(), actualDate.getValueAsString())
    }

    @Test
    fun quantityToFhirQuantity() {
        val expected = Quantity()
            .setValue(BigDecimal("2.0"))
            .setCode("ml")
            .setSystem("http://unitsofmeasure.org")
            .setUnit("ml")
        val actual = typeConverter!!.toFhirQuantity(
            org.opencds.cqf.cql.engine.runtime.Quantity().withValue(BigDecimal("2.0")).withUnit("ml")
        ) as Quantity?
        Assertions.assertTrue(expected.equalsDeep(actual))
    }

    @Test
    fun ratioToFhirRatio() {
        val expectedNumerator = Quantity()
            .setValue(BigDecimal("1.0"))
            .setCode("ml")
            .setSystem("http://unitsofmeasure.org")
            .setUnit("ml")
        val expectedDenominator = Quantity()
            .setValue(BigDecimal("2.0"))
            .setCode("ml")
            .setSystem("http://unitsofmeasure.org")
            .setUnit("ml")

        val expected = Ratio()
            .setNumerator(expectedNumerator)
            .setDenominator(expectedDenominator)

        val testData = org.opencds.cqf.cql.engine.runtime.Ratio()
        testData.setNumerator(
            org.opencds.cqf.cql.engine.runtime.Quantity().withValue(BigDecimal.valueOf(1.0)).withUnit("ml")
        )
        testData.setDenominator(
            org.opencds.cqf.cql.engine.runtime.Quantity().withValue(BigDecimal.valueOf(2.0)).withUnit("ml")
        )

        val actual = typeConverter!!.toFhirRatio(testData) as Ratio?

        Assertions.assertTrue(expected.equalsDeep(actual))
    }

    @Test
    fun nullToFhirAny() {
        val expected: IBase? = typeConverter!!.toFhirAny(null)
        Assertions.assertNull(expected)
    }

    @Test
    fun objectToFhirAny() {
        Assertions.assertThrows<NotImplementedException?>(
            NotImplementedException::class.java,
            Executable { typeConverter!!.toFhirAny("Huh") })
    }

    @Test
    fun codeToFhirCoding() {
        var expected = Coding()
            .setSystem("http://the-system.com")
            .setCode("test")
            .setDisplay("system-test")
            .setVersion("1.5")
        val actual = typeConverter!!.toFhirCoding(
            Code()
                .withSystem("http://the-system.com")
                .withCode("test")
                .withDisplay("system-test")
                .withVersion("1.5")
        ) as Coding?
        Assertions.assertTrue(expected.equalsDeep(actual))

        expected = typeConverter!!.toFhirCoding(null) as Coding
        Assertions.assertNull(expected)
    }

    @Test
    fun conceptToFhirCodeableConcept() {
        var expected = CodeableConcept(
            Coding()
                .setSystem("http://the-system.com")
                .setCode("test")
                .setDisplay("system-test")
                .setVersion("1.5")
        )
            .setText("additional-text")
        val actual = typeConverter!!.toFhirCodeableConcept(
            Concept()
                .withCode(
                    Code()
                        .withSystem("http://the-system.com")
                        .withCode("test")
                        .withDisplay("system-test")
                        .withVersion("1.5")
                )
                .withDisplay("additional-text")
        ) as CodeableConcept?
        Assertions.assertTrue(expected.equalsDeep(actual))

        expected = typeConverter!!.toFhirCodeableConcept(null) as CodeableConcept
        Assertions.assertNull(expected)
    }

    @ParameterizedTest
    @MethodSource("startAndEndTimes")
    fun intervalToFhirPeriodYyyyMMdd(startTime: LocalDateTime, endTime: LocalDateTime) {
        val startTime_yyyyMMdd = YYYY_MM_DD.format(startTime)
        val endTime_yyyyMMdd = YYYY_MM_DD.format(endTime)

        val expected = Period()
            .setStartElement(DateTimeType(startTime_yyyyMMdd))
            .setEndElement(DateTimeType(endTime_yyyyMMdd))
        val actual = typeConverter!!.toFhirPeriod(
            Interval(
                org.opencds.cqf.cql.engine.runtime.Date(startTime_yyyyMMdd),
                true,
                org.opencds.cqf.cql.engine.runtime.Date(endTime_yyyyMMdd),
                true
            )
        ) as Period?
        Assertions.assertTrue(expected.equalsDeep(actual))
    }

    @ParameterizedTest
    @MethodSource("dateTimes")
    fun intervalToFhirPeriodTimestampWithOffsets(now: LocalDateTime, startTime: LocalDateTime, endTime: LocalDateTime) {
        val zonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault())
        val defaultOffset = zonedDateTime.getOffset()

        val startTimeWithOffset =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(startTime.atOffset(defaultOffset))
        val endTimeWithOffset = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(endTime.atOffset(defaultOffset))
        val startTimeNoOffset = DateTimeFormatter.ISO_DATE_TIME.format(startTime.atOffset(defaultOffset))
        val endTimeNoOffset = DateTimeFormatter.ISO_DATE_TIME.format(endTime.atOffset(defaultOffset))

        val dateTimeTypeStart = DateTimeType(startTimeWithOffset)
        val dateTimeTypeEnd = DateTimeType(endTimeWithOffset)
        val expected = Period().setStartElement(dateTimeTypeStart).setEndElement(dateTimeTypeEnd)

        val dateTimeStart = DateTime(startTimeNoOffset, defaultOffset)
        val dateTimeEnd = DateTime(endTimeNoOffset, defaultOffset)
        val intervalStartEnd = Interval(dateTimeStart, true, dateTimeEnd, true)
        val actual = typeConverter!!.toFhirPeriod(intervalStartEnd) as Period?

        Assertions.assertTrue(expected.equalsDeep(actual))
    }

    @ParameterizedTest
    @MethodSource("startAndEndYears")
    fun intervalToFhirPeriodStartAndEndYears(now: LocalDateTime, startYear: Int, endYear: Int) {
        val zonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault())
        val defaultOffset = zonedDateTime.getOffset()

        val expected = Period()
            .setStartElement(DateTimeType(startYear.toString() + "-01-01T00:00:00" + defaultOffset))
            .setEndElement(DateTimeType(endYear.toString() + "-01-01T00:00:00" + defaultOffset))
        val actual = typeConverter!!.toFhirPeriod(
            Interval(
                DateTime("" + startYear, defaultOffset), true, DateTime("" + endYear, defaultOffset), true
            )
        ) as Period?
        Assertions.assertTrue(expected.equalsDeep(actual))
    }

    @Test
    fun intervalToFhirPeriodNull() {
        Assertions.assertNull(typeConverter!!.toFhirPeriod(null))
    }

    @Test
    fun intervalToFhirRange() {
        val expected = Range()
            .setLow(
                Quantity()
                    .setValue(BigDecimal("2.0"))
                    .setCode("ml")
                    .setUnit("ml")
                    .setSystem("http://unitsofmeasure.org")
            )
            .setHigh(
                Quantity()
                    .setValue(BigDecimal("5.0"))
                    .setCode("ml")
                    .setUnit("ml")
                    .setSystem("http://unitsofmeasure.org")
            )
        var actual = typeConverter!!.toFhirRange(
            Interval(
                org.opencds.cqf.cql.engine.runtime.Quantity().withValue(BigDecimal("2.0")).withUnit("ml"),
                true,
                org.opencds.cqf.cql.engine.runtime.Quantity().withValue(BigDecimal("5.0")).withUnit("ml"),
                true
            )
        ) as Range?
        Assertions.assertTrue(expected.equalsDeep(actual))

        actual = typeConverter!!.toFhirRange(null) as Range?
        Assertions.assertNull(actual)
    }

    @Test
    fun invalidIntervalToFhirRange() {
        val interval = Interval(5, true, 6, true)
        Assertions.assertThrows<IllegalArgumentException?>(
            IllegalArgumentException::class.java,
            Executable { typeConverter!!.toFhirRange(interval) })
    }

    @Test
    fun intervalToFhirInterval() {
        val expectedPeriod = Period()
            .setStartElement(DateTimeType("2019-02-03"))
            .setEndElement(DateTimeType("2019-02-05"))
        val actualPeriod = typeConverter!!.toFhirInterval(
            Interval(
                org.opencds.cqf.cql.engine.runtime.Date("2019-02-03"),
                true,
                org.opencds.cqf.cql.engine.runtime.Date("2019-02-05"),
                true
            )
        ) as Period?
        Assertions.assertTrue(expectedPeriod.equalsDeep(actualPeriod))

        val expectedRange = Range()
            .setLow(
                Quantity()
                    .setValue(BigDecimal("2.0"))
                    .setCode("ml")
                    .setUnit("ml")
                    .setSystem("http://unitsofmeasure.org")
            )
            .setHigh(
                Quantity()
                    .setValue(BigDecimal("5.0"))
                    .setCode("ml")
                    .setUnit("ml")
                    .setSystem("http://unitsofmeasure.org")
            )
        val actualRange = typeConverter!!.toFhirInterval(
            Interval(
                org.opencds.cqf.cql.engine.runtime.Quantity().withValue(BigDecimal("2.0")).withUnit("ml"),
                true,
                org.opencds.cqf.cql.engine.runtime.Quantity().withValue(BigDecimal("5.0")).withUnit("ml"),
                true
            )
        ) as Range?
        Assertions.assertTrue(expectedRange.equalsDeep(actualRange))

        val expected: IBase? = typeConverter!!.toFhirInterval(null)
        Assertions.assertNull(expected)
    }

    @Test
    fun integerIntervalToFhirString() {
        val interval = Interval(5, true, 6, true)
        val result: IBase? = typeConverter!!.toFhirInterval(interval)
        Assertions.assertNotNull(result)
        val stringType = Assertions.assertInstanceOf<StringType?>(StringType::class.java, result)
        Assertions.assertEquals("Interval[5, 6]", stringType!!.getValue())
        val extension = stringType.getExtension().stream()
            .filter { e: Extension? -> e!!.getUrl() == FhirTypeConverter.CQL_TEXT_EXT_URL }
            .findFirst()
        Assertions.assertTrue(extension.isPresent())
    }

    @Test
    fun exceptionToFhirOperationOutcome() {
        val exception = IllegalArgumentException("Test exception")
        exception.fillInStackTrace()
        val result: IBaseOperationOutcome? = typeConverter!!.toFhirOperationOutcome(exception)
        Assertions.assertNotNull(result)
        val outcome = Assertions.assertInstanceOf<OperationOutcome?>(OperationOutcome::class.java, result)
        Assertions.assertEquals(1, outcome!!.getIssue().size)
        val issue = outcome.getIssue().get(0)
        Assertions.assertEquals(OperationOutcome.IssueType.EXCEPTION, issue.getCode())
        Assertions.assertEquals(OperationOutcome.IssueSeverity.ERROR, issue.getSeverity())
        Assertions.assertEquals("Test exception", issue.getDiagnostics())

        val stackTrace = issue.getExtension().stream()
            .filter { e: Extension? -> e!!.getUrl() == FhirTypeConverter.NATIVE_STACK_TRACE_EXT_URL }
            .findFirst()
        Assertions.assertTrue(stackTrace.isPresent())
        Assertions.assertNotNull(stackTrace.get().getValue().toString())
    }

    @Test
    fun tupleToFhirTuple() {
        var actual =
            typeConverter!!.toFhirTuple(null) as Parameters.ParametersParameterComponent
        Assertions.assertNull(actual)

        val tuple = Tuple()
        actual = typeConverter!!.toFhirTuple(tuple) as Parameters.ParametersParameterComponent
        Assertions.assertNotNull(actual)
        Assertions.assertEquals(
            FhirTypeConverter.EMPTY_TUPLE_EXT_URL,
            actual.getValue().getExtension().get(0).getUrl()
        )

        val ints = ArrayList<Int?>()
        for (i in 0..4) {
            ints.add(i)
        }

        tuple.getElements().put("V", ints)
        tuple.getElements().put("W", null)
        tuple.getElements().put("X", 5)
        tuple.getElements().put("Y", Encounter().setId("123"))
        tuple.getElements().put("Z", ArrayList<Any?>())

        actual = typeConverter!!.toFhirTuple(tuple) as Parameters.ParametersParameterComponent
        val first = actual
        Assertions.assertEquals(9, first.getPart().size)

        val v: MutableList<Parameters.ParametersParameterComponent> = getPartsByName(first, "V")
        Assertions.assertEquals(5, v.size)
        Assertions.assertEquals(0, (v.get(0).getValue() as IntegerType).getValue())

        val w: Parameters.ParametersParameterComponent = getPartsByName(first, "W").get(0)
        Assertions.assertEquals(
            FhirTypeConverter.DATA_ABSENT_REASON_EXT_URL,
            w.getValue().getExtension().get(0).getUrl()
        )

        val x: Parameters.ParametersParameterComponent = getPartsByName(first, "X").get(0)
        Assertions.assertEquals(5, (x.getValue() as IntegerType).getValue())

        val y: Parameters.ParametersParameterComponent = getPartsByName(first, "Y").get(0)
        Assertions.assertEquals("123", y.getResource().getId())

        val z: Parameters.ParametersParameterComponent = getPartsByName(first, "Z").get(0)
        Assertions.assertEquals(
            FhirTypeConverter.EMPTY_LIST_EXT_URL,
            z.getValue().getExtension().get(0).getUrl()
        )
    }

    @Test
    fun complexTupleToFhirTuple() {
        val innerTuple = Tuple()
        innerTuple.getElements().put("X", 1)
        innerTuple.getElements().put("Y", 2)
        innerTuple.getElements().put("Z", null)
        val outerTuple = Tuple()
        outerTuple.getElements().put("A", innerTuple)
        val tupleList = ArrayList<Tuple?>()
        for (i in 0..2) {
            val elementTuple = Tuple()
            elementTuple.getElements().put("P", i)
            elementTuple.getElements().put("Q", i + 1)
            tupleList.add(elementTuple)
        }
        outerTuple.getElements().put("B", tupleList)

        val actual =
            typeConverter!!.toFhirTuple(outerTuple) as Parameters.ParametersParameterComponent
        val first = actual
        Assertions.assertEquals(4, first.getPart().size)

        val a: MutableList<Parameters.ParametersParameterComponent> = getPartsByName(first, "A")
        Assertions.assertEquals(1, a.size)
        Assertions.assertEquals(3, a.get(0).getPart().size)
        val x = a.get(0).getPart().get(0)
        Assertions.assertEquals(1, (x.getValue() as IntegerType).getValue())
        val y = a.get(0).getPart().get(1)
        Assertions.assertEquals(2, (y.getValue() as IntegerType).getValue())
        val z = a.get(0).getPart().get(2)
        Assertions.assertEquals(
            FhirTypeConverter.DATA_ABSENT_REASON_EXT_URL,
            z.getValue().getExtension().get(0).getUrl()
        )

        val b: MutableList<Parameters.ParametersParameterComponent> = getPartsByName(first, "B")
        Assertions.assertEquals(3, b.size)
        val b1 = b.get(0)
        val p: MutableList<Parameters.ParametersParameterComponent> = getPartsByName(b1, "P")
        Assertions.assertEquals(1, p.size)
        Assertions.assertEquals(0, (p.get(0).getValue() as IntegerType).getValue())
        val q: MutableList<Parameters.ParametersParameterComponent> = getPartsByName(b1, "Q")
        Assertions.assertEquals(1, q.size)
        Assertions.assertEquals(1, (q.get(0).getValue() as IntegerType).getValue())
    }

    // FHIR-to-CQL
    @Test
    fun isCqlType() {
        Assertions.assertTrue(typeConverter!!.isCqlType(5))
        Assertions.assertTrue(typeConverter!!.isCqlType(BigDecimal(0)))
        Assertions.assertTrue(typeConverter!!.isCqlType(Code()))

        Assertions.assertFalse(typeConverter!!.isCqlType(Patient()))
        Assertions.assertFalse(typeConverter!!.isCqlType(IdType()))
    }

    @Test
    fun nullIsCqlType() {
        Assertions.assertThrows<NullPointerException?>(
            NullPointerException::class.java,
            Executable { typeConverter!!.isCqlType(null) })
    }

    @Test
    fun iterableIsCqlType() {
        val value = ArrayList<Any?>()
        Assertions.assertThrows<IllegalArgumentException?>(
            IllegalArgumentException::class.java,
            Executable { typeConverter!!.isCqlType(value) })
    }

    @Test
    fun toCqlType() {
        var actual: Any? = typeConverter!!.toCqlType(Code())
        MatcherAssert.assertThat<Any?>(actual, Matchers.instanceOf<Any?>(Code::class.java))

        actual = typeConverter!!.toCqlType(IntegerType(5))
        MatcherAssert.assertThat<Any?>(actual, Matchers.instanceOf<Any?>(Int::class.java))

        actual = typeConverter!!.toCqlType(StringType("test"))
        MatcherAssert.assertThat<Any?>(actual, Matchers.instanceOf<Any?>(String::class.java))

        actual = typeConverter!!.toCqlType(IdType("test"))
        MatcherAssert.assertThat<Any?>(actual, Matchers.instanceOf<Any?>(String::class.java))

        actual = typeConverter!!.toCqlType(BooleanType(true))
        MatcherAssert.assertThat<Any?>(actual, Matchers.instanceOf<Any?>(Boolean::class.java))

        actual = typeConverter!!.toCqlType(DecimalType(1.0))
        MatcherAssert.assertThat<Any?>(actual, Matchers.instanceOf<Any?>(BigDecimal::class.java))

        actual = typeConverter!!.toCqlType(DateType(Calendar.getInstance().getTime()))
        MatcherAssert.assertThat<Any?>(
            actual,
            Matchers.instanceOf<Any?>(org.opencds.cqf.cql.engine.runtime.Date::class.java)
        )

        actual = typeConverter!!.toCqlType(InstantType(Calendar.getInstance()))
        MatcherAssert.assertThat<Any?>(actual, Matchers.instanceOf<Any?>(DateTime::class.java))

        actual = typeConverter!!.toCqlType(DateTimeType(Calendar.getInstance()))
        MatcherAssert.assertThat<Any?>(actual, Matchers.instanceOf<Any?>(DateTime::class.java))

        actual = typeConverter!!.toCqlType(TimeType("10:00:00.0000"))
        MatcherAssert.assertThat<Any?>(actual, Matchers.instanceOf<Any?>(Time::class.java))

        actual = typeConverter!!.toCqlType(StringType("test"))
        MatcherAssert.assertThat<Any?>(actual, Matchers.instanceOf<Any?>(String::class.java))

        actual = typeConverter!!.toCqlType(Quantity())
        MatcherAssert.assertThat<Any?>(
            actual,
            Matchers.instanceOf<Any?>(org.opencds.cqf.cql.engine.runtime.Quantity::class.java)
        )

        actual = typeConverter!!.toCqlType(Ratio())
        MatcherAssert.assertThat<Any?>(
            actual,
            Matchers.instanceOf<Any?>(org.opencds.cqf.cql.engine.runtime.Ratio::class.java)
        )

        actual = typeConverter!!.toCqlType(Coding())
        MatcherAssert.assertThat<Any?>(actual, Matchers.instanceOf<Any?>(Code::class.java))

        actual = typeConverter!!.toCqlType(CodeableConcept())
        MatcherAssert.assertThat<Any?>(actual, Matchers.instanceOf<Any?>(Concept::class.java))

        actual = typeConverter!!.toCqlType(
            Period()
                .setStart(Calendar.getInstance().getTime())
                .setEnd(Calendar.getInstance().getTime())
        )
        MatcherAssert.assertThat<Any?>(actual, Matchers.instanceOf<Any?>(Interval::class.java))

        val low = SimpleQuantity()
        low.setValue(BigDecimal.valueOf(1.0))
        low.setUnit("d")

        val high = SimpleQuantity()
        high.setValue(BigDecimal.valueOf(4.0))
        high.setUnit("d")
        actual = typeConverter!!.toCqlType(Range().setLow(low).setHigh(high))
        MatcherAssert.assertThat<Any?>(actual, Matchers.instanceOf<Any?>(Interval::class.java))

        actual = typeConverter!!.toCqlType(null)
        Assertions.assertNull(actual)
    }

    @Test
    fun toCqlTypeIterable() {
        val list = ArrayList<Any?>()
        Assertions.assertThrows<IllegalArgumentException?>(
            IllegalArgumentException::class.java,
            Executable { typeConverter!!.toCqlType(list) })
    }

    @Test
    fun toCqlTypeNotCql() {
        val offset = ZoneOffset.ofHours(3)
        Assertions.assertThrows<IllegalArgumentException?>(
            IllegalArgumentException::class.java,
            Executable { typeConverter!!.toCqlType(offset) })
    }

    @Test
    fun toCqlTypes() {
        val innerExpected: MutableList<Any?> = ArrayList<Any?>()
        innerExpected.add("123")
        innerExpected.add(null)
        val expected: MutableList<Any?> = ArrayList<Any?>()
        expected.add(innerExpected)
        expected.add(null)
        expected.add(5)

        val innerTest: MutableList<Any?> = ArrayList<Any?>()
        innerTest.add(StringType("123"))
        innerTest.add(null)
        val test: MutableList<Any?> = ArrayList<Any?>()
        test.add(innerTest)
        test.add(null)
        test.add(IntegerType(5))

        val actual: Iterable<Any?> = typeConverter!!.toCqlTypes(test)

        Assertions.assertTrue(compareIterables(expected, actual))
    }

    @Test
    fun stringToCqlId() {
        val expected = "123"
        var actual: String? = typeConverter!!.toCqlId(IdType("123"))
        Assertions.assertEquals(expected, actual)

        actual = typeConverter!!.toCqlId(null)
        Assertions.assertNull(actual)
    }

    @Test
    fun primitiveFhirTypeToCqlType() {
        var actualBoolean: Boolean = typeConverter!!.toCqlBoolean(org.hl7.fhir.r5.model.BooleanType(false))
        Assertions.assertFalse(actualBoolean)

        actualBoolean = typeConverter!!.toCqlBoolean(null)
        Assertions.assertNull(actualBoolean)

        var expectedInteger: Int? = 5
        val actualInteger: Int? = typeConverter!!.toCqlInteger(org.hl7.fhir.r5.model.IntegerType(5))
        Assertions.assertEquals(expectedInteger, actualInteger)

        expectedInteger = typeConverter!!.toCqlInteger(null)
        Assertions.assertNull(expectedInteger)

        val expectedString = "5"
        var actualString: String? = typeConverter!!.toCqlString(org.hl7.fhir.r5.model.StringType("5"))
        Assertions.assertEquals(expectedString, actualString)

        actualString = typeConverter!!.toCqlString(null)
        Assertions.assertNull(actualString)

        val expectedDecimal = BigDecimal("2.0")
        var actualDecimal: BigDecimal? =
            typeConverter!!.toCqlDecimal(org.hl7.fhir.r5.model.DecimalType(BigDecimal("2.0")))
        Assertions.assertEquals(expectedDecimal, actualDecimal)

        actualDecimal = typeConverter!!.toCqlDecimal(null)
        Assertions.assertNull(actualDecimal)
    }

    @Test
    fun dateToCqlType() {
        var expectedDate = org.opencds.cqf.cql.engine.runtime.Date("2019-02-03")
        var actualDate: org.opencds.cqf.cql.engine.runtime.Date? = typeConverter!!.toCqlDate(DateType("2019-02-03"))
        Assertions.assertTrue(expectedDate.equal(actualDate))

        expectedDate = org.opencds.cqf.cql.engine.runtime.Date("2019")
        actualDate = typeConverter!!.toCqlDate(DateType("2019"))
        Assertions.assertTrue(expectedDate.equal(actualDate))
    }

    @Test
    fun dateTimeToCqlType() {
        var expectedDate = DateTime("2019-02-03", ZoneOffset.UTC)
        var actualDate: DateTime? = typeConverter!!.toCqlDateTime(DateTimeType("2019-02-03"))
        Assertions.assertTrue(expectedDate.equal(actualDate))

        expectedDate = DateTime("2019", ZoneOffset.UTC)
        actualDate = typeConverter!!.toCqlDateTime(DateTimeType("2019"))
        Assertions.assertTrue(expectedDate.equal(actualDate))

        expectedDate = DateTime("2019", ZoneOffset.UTC)
        actualDate = typeConverter!!.toCqlDateTime(DateTimeType("2019"))
        Assertions.assertTrue(expectedDate.equal(actualDate))
    }

    @Test
    fun quantityToCqlType() {
        val expected = (org.opencds.cqf.cql.engine.runtime.Quantity().withValue(BigDecimal("2.0")).withUnit("ml"))
        val actual: org.opencds.cqf.cql.engine.runtime.Quantity? = typeConverter!!.toCqlQuantity(
            Quantity()
                .setValue(BigDecimal("2.0"))
                .setUnit("ml")
                .setSystem("http://unitsofmeasure.org")
        )
        Assertions.assertTrue(expected.equal(actual))
    }

    @Test
    fun ratioToCqlType() {
        val expected = org.opencds.cqf.cql.engine.runtime.Ratio()
        expected.setNumerator(
            org.opencds.cqf.cql.engine.runtime.Quantity().withValue(BigDecimal.valueOf(1.0)).withUnit("ml")
        )
        expected.setDenominator(
            org.opencds.cqf.cql.engine.runtime.Quantity().withValue(BigDecimal.valueOf(2.0)).withUnit("ml")
        )

        val testNumerator = Quantity()
            .setValue(BigDecimal("1.0"))
            .setUnit("ml")
            .setSystem("http://unitsofmeasure.org")
        val testDenominator = Quantity()
            .setValue(BigDecimal("2.0"))
            .setUnit("ml")
            .setSystem("http://unitsofmeasure.org")

        val test =
            Ratio().setNumerator(testNumerator).setDenominator(testDenominator)

        val actual: org.opencds.cqf.cql.engine.runtime.Ratio? = typeConverter!!.toCqlRatio(test)
        Assertions.assertTrue(expected.equal(actual))
    }

    @Test
    fun nullToCqlType() {
        val expected: Any? = typeConverter!!.toCqlAny(null)
        Assertions.assertNull(expected)
    }

    @Test
    fun objectToCqlType() {
        val id = IdType()
        Assertions.assertThrows<NotImplementedException?>(
            NotImplementedException::class.java,
            Executable { typeConverter!!.toCqlAny(id) })
    }

    @Test
    fun codingToCqlCode() {
        var expected = Code()
            .withSystem("http://the-system.com")
            .withCode("test")
            .withDisplay("system-test")
            .withVersion("1.5")
        val actual: Code = typeConverter!!.toCqlCode(
            Coding()
                .setSystem("http://the-system.com")
                .setCode("test")
                .setDisplay("system-test")
                .setVersion("1.5")
        )
        Assertions.assertTrue(expected.equal(actual))

        expected = typeConverter!!.toCqlCode(null)
        Assertions.assertNull(expected)
    }

    @Test
    fun codeableConceptToCqlConcept() {
        var expected = Concept()
            .withCode(
                Code()
                    .withSystem("http://the-system.com")
                    .withCode("test")
                    .withDisplay("system-test")
                    .withVersion("1.5")
            )
            .withDisplay("additional-text")
        val actual: Concept = typeConverter!!.toCqlConcept(
            CodeableConcept(
                Coding()
                    .setSystem("http://the-system.com")
                    .setCode("test")
                    .setDisplay("system-test")
                    .setVersion("1.5")
            )
                .setText("additional-text")
        )

        Assertions.assertTrue(expected.equal(actual))

        expected = typeConverter!!.toCqlConcept(null)
        Assertions.assertNull(expected)
    }

    @Test
    fun periodToCqlInterval() {
        var expected = Interval(
            org.opencds.cqf.cql.engine.runtime.Date("2019-02-03"),
            true,
            org.opencds.cqf.cql.engine.runtime.Date("2019-02-05"),
            true
        )
        var actual: Interval? = typeConverter!!.toCqlInterval(
            Period()
                .setStartElement(DateTimeType("2019-02-03"))
                .setEndElement(DateTimeType("2019-02-05"))
        )
        Assertions.assertTrue(expected.equal(actual))

        expected = Interval(
            org.opencds.cqf.cql.engine.runtime.Date("2019"),
            true,
            org.opencds.cqf.cql.engine.runtime.Date("2020"),
            true
        )
        actual = typeConverter!!.toCqlInterval(
            Period().setStartElement(DateTimeType("2019")).setEndElement(DateTimeType("2020"))
        )
        Assertions.assertTrue(expected.equal(actual))

        expected = Interval(
            DateTime("2020-09-18T19:35:53", ZoneOffset.UTC),
            true,
            DateTime("2020-09-18T19:37:00", ZoneOffset.UTC),
            true
        )
        actual = typeConverter!!.toCqlInterval(
            Period()
                .setStartElement(DateTimeType("2020-09-18T19:35:53+00:00"))
                .setEndElement(DateTimeType("2020-09-18T19:37:00+00:00"))
        )
        Assertions.assertTrue(expected.equal(actual))

        actual = typeConverter!!.toCqlInterval(null)
        Assertions.assertNull(actual)
    }

    @Test
    fun rangeToCqlInterval() {
        val expected = Interval(
            org.opencds.cqf.cql.engine.runtime.Quantity().withValue(BigDecimal("2.0")).withUnit("ml"),
            true,
            org.opencds.cqf.cql.engine.runtime.Quantity().withValue(BigDecimal("5.0")).withUnit("ml"),
            true
        )
        var actual: Interval? = typeConverter!!.toCqlInterval(
            Range()
                .setLow(
                    SimpleQuantity()
                        .setValue(BigDecimal("2.0"))
                        .setUnit("ml")
                        .setSystem("http://unitsofmeasure.org") as SimpleQuantity?
                )
                .setHigh(
                    SimpleQuantity()
                        .setValue(BigDecimal("5.0"))
                        .setUnit("ml")
                        .setSystem("http://unitsofmeasure.org") as SimpleQuantity?
                )
        )
        Assertions.assertTrue(expected.equal(actual))

        actual = typeConverter!!.toCqlInterval(null)
        Assertions.assertNull(actual)
    }

    @Test
    fun invalidTypeToCqlInterval() {
        val attachment = Attachment()
        Assertions.assertThrows<IllegalArgumentException?>(
            IllegalArgumentException::class.java,
            Executable { typeConverter!!.toCqlInterval(attachment) })
    }

    @Test
    fun tupleToCqlTuple() {
        val expected: Any? = typeConverter!!.toCqlTuple(null)
        Assertions.assertNull(expected)

        val p = Patient()
        Assertions.assertThrows<NotImplementedException?>(
            NotImplementedException::class.java,
            Executable { typeConverter!!.toCqlTuple(p) })
    }

    @Test
    fun longToCqlLong() {
        val expected = 5L
        val actual: Any? = typeConverter!!.toCqlType(expected)
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun longToFhirInteger64() {
        Assertions.assertThrows<IllegalArgumentException?>(
            IllegalArgumentException::class.java,
            Executable { typeConverter!!.toFhirInteger64(5L) })
    }

    companion object {
        private var typeConverter: R4FhirTypeConverter? = null

        @BeforeAll
        fun initialize() {
            typeConverter = R4FhirTypeConverter()
        }

        private fun nowsAndEvaluationTimes(): Array<Array<Any?>?> {
            return ConverterTestUtils.nowsAndEvaluationTimes()
        }

        private fun startAndEndTimes(): Array<Array<Any?>?> {
            return ConverterTestUtils.startAndEndTimes()
        }

        private fun dateTimes(): Array<Array<Any?>?> {
            return ConverterTestUtils.dateTimes()
        }

        private fun startAndEndYears(): Array<Array<Any?>?> {
            return ConverterTestUtils.startAndEndYears()
        }

        private fun getPartsByName(
            ppc: Parameters.ParametersParameterComponent,
            name: String?
        ): MutableList<Parameters.ParametersParameterComponent> {
            return ppc.getPart().stream()
                .filter { p: Parameters.ParametersParameterComponent? -> p!!.getName() == name }.collect(
                    Collectors.toList()
                )
        }
    }
}
