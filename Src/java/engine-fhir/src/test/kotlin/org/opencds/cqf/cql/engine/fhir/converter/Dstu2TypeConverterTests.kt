package org.opencds.cqf.cql.engine.fhir.converter

import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors
import org.apache.commons.lang3.NotImplementedException
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hl7.fhir.dstu2.model.Attachment
import org.hl7.fhir.dstu2.model.Base
import org.hl7.fhir.dstu2.model.BooleanType
import org.hl7.fhir.dstu2.model.CodeableConcept
import org.hl7.fhir.dstu2.model.Coding
import org.hl7.fhir.dstu2.model.DateTimeType
import org.hl7.fhir.dstu2.model.DateType
import org.hl7.fhir.dstu2.model.DecimalType
import org.hl7.fhir.dstu2.model.Encounter
import org.hl7.fhir.dstu2.model.Extension
import org.hl7.fhir.dstu2.model.IdType
import org.hl7.fhir.dstu2.model.InstantType
import org.hl7.fhir.dstu2.model.IntegerType
import org.hl7.fhir.dstu2.model.OperationOutcome
import org.hl7.fhir.dstu2.model.Parameters
import org.hl7.fhir.dstu2.model.Patient
import org.hl7.fhir.dstu2.model.Period
import org.hl7.fhir.dstu2.model.Quantity
import org.hl7.fhir.dstu2.model.Range
import org.hl7.fhir.dstu2.model.Ratio
import org.hl7.fhir.dstu2.model.SimpleQuantity
import org.hl7.fhir.dstu2.model.StringType
import org.hl7.fhir.dstu2.model.TimeType
import org.hl7.fhir.instance.model.api.IBase
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome
import org.hl7.fhir.instance.model.api.IIdType
import org.hl7.fhir.instance.model.api.IPrimitiveType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator.equal
import org.opencds.cqf.cql.engine.fhir.converter.ConverterTestUtils.YYYY_MM_DD
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Concept
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Tuple

internal class Dstu2TypeConverterTests {
    private fun compareIterables(left: Iterable<Any?>, right: Iterable<Any?>): Boolean {
        val leftIterator = left.iterator()
        val rightIterator = right.iterator()

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

    private fun compareObjects(left: Any?, right: Any?): Boolean {
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
        Assertions.assertTrue(typeConverter.isFhirType(Patient()))
        Assertions.assertTrue(typeConverter.isFhirType(IdType()))
        Assertions.assertTrue(typeConverter.isFhirType(Quantity()))
        Assertions.assertTrue(typeConverter.isFhirType(Ratio()))
        Assertions.assertTrue(typeConverter.isFhirType(BooleanType()))
        Assertions.assertTrue(typeConverter.isFhirType(IntegerType()))
        Assertions.assertTrue(typeConverter.isFhirType(DecimalType()))
        Assertions.assertTrue(typeConverter.isFhirType(DateType()))
        Assertions.assertTrue(typeConverter.isFhirType(InstantType()))
        Assertions.assertTrue(typeConverter.isFhirType(DateTimeType()))
        Assertions.assertTrue(typeConverter.isFhirType(TimeType()))
        Assertions.assertTrue(typeConverter.isFhirType(StringType()))
        Assertions.assertTrue(typeConverter.isFhirType(Coding()))
        Assertions.assertTrue(typeConverter.isFhirType(CodeableConcept()))
        Assertions.assertTrue(typeConverter.isFhirType(Period()))
        Assertions.assertTrue(typeConverter.isFhirType(Range()))

        Assertions.assertFalse(typeConverter.isFhirType(5))
        Assertions.assertFalse(typeConverter.isFhirType(BigDecimal(0)))
        Assertions.assertFalse(typeConverter.isFhirType(Code()))
    }

    @Test
    fun iterableIsFhirType() {
        val value = ArrayList<Any?>()
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            typeConverter.isFhirType(value)
        }
    }

    @Test
    fun toFhirType() {
        var actual: IBase? = typeConverter.toFhirType(Code())
        MatcherAssert.assertThat<IBase?>(actual, Matchers.instanceOf<IBase?>(Coding::class.java))

        actual = typeConverter.toFhirType(5)
        MatcherAssert.assertThat(actual, Matchers.instanceOf(IntegerType::class.java))

        actual = typeConverter.toFhirType(IdType())
        MatcherAssert.assertThat(actual, Matchers.instanceOf(IdType::class.java))

        actual = typeConverter.toFhirType(null)
        Assertions.assertNull(actual)
    }

    @Test
    fun toFhirTypeIterable() {
        val value = ArrayList<Any?>()
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            typeConverter.toFhirType(value)
        }
    }

    @Test
    fun toFhirTypeNotCql() {
        val offset = ZoneOffset.ofHours(3)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            typeConverter.toFhirType(offset)
        }
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

        val actual: Iterable<Any?> = typeConverter.toFhirTypes(test)

        Assertions.assertTrue(compareIterables(expected, actual))
    }

    @Test
    fun stringToFhirId() {
        val expected: IIdType = IdType("123")
        var actual: IIdType? = typeConverter.toFhirId("123")
        Assertions.assertEquals(expected.value, actual!!.value)

        actual = typeConverter.toFhirId(null)
        Assertions.assertNull(actual)
    }

    @Test
    fun primitiveCqlTypeToFhirType() {
        val expectedBoolean: IPrimitiveType<Boolean> = BooleanType(false)
        var actualBoolean: IPrimitiveType<Boolean>? = typeConverter.toFhirBoolean(false)
        Assertions.assertEquals(expectedBoolean.getValue(), actualBoolean!!.getValue())

        actualBoolean = typeConverter.toFhirBoolean(null)
        Assertions.assertNull(actualBoolean)

        val expectedInteger: IPrimitiveType<Int> = IntegerType(5)
        var actualInteger: IPrimitiveType<Int>? = typeConverter.toFhirInteger(5)
        Assertions.assertEquals(expectedInteger.getValue(), actualInteger!!.getValue())

        actualInteger = typeConverter.toFhirInteger(null)
        Assertions.assertNull(actualInteger)

        val expectedString: IPrimitiveType<String> = StringType("5")
        var actualString: IPrimitiveType<String>? = typeConverter.toFhirString("5")
        Assertions.assertEquals(expectedString.getValue(), actualString!!.getValue())

        actualString = typeConverter.toFhirString(null)
        Assertions.assertNull(actualString)

        val expectedDecimal: IPrimitiveType<BigDecimal> = DecimalType(BigDecimal("2.0"))
        var actualDecimal: IPrimitiveType<BigDecimal>? =
            typeConverter.toFhirDecimal(BigDecimal("2.0"))
        Assertions.assertEquals(expectedDecimal.getValue(), actualDecimal!!.getValue())

        actualDecimal = typeConverter.toFhirDecimal(null)
        Assertions.assertNull(actualDecimal)
    }

    @Test
    fun dateToFhirDate() {
        var expectedDate: IPrimitiveType<Date> = DateType("2019-02-03")
        var actualDate: IPrimitiveType<Date>? =
            typeConverter.toFhirDate(org.opencds.cqf.cql.engine.runtime.Date("2019-02-03"))
        Assertions.assertEquals(expectedDate.getValue(), actualDate!!.getValue())

        expectedDate = DateType("2019")
        actualDate = typeConverter.toFhirDate(org.opencds.cqf.cql.engine.runtime.Date("2019"))
        Assertions.assertEquals(expectedDate.value, actualDate!!.getValue())
    }

    @ParameterizedTest
    @MethodSource("nowsAndEvaluationTimes")
    fun dateTimeToFhirDateTime(now: LocalDateTime, evaluationTime: LocalDateTime) {
        val zonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault())
        val defaultOffset = zonedDateTime.offset

        val evalTimeWithOffset =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(evaluationTime.atOffset(defaultOffset))
        val evalDate = DateTimeFormatter.ISO_DATE.format(evaluationTime)

        var expectedDate = DateTimeType(evalTimeWithOffset)
        var actualDate: IPrimitiveType<Date>? =
            typeConverter.toFhirDateTime(DateTime(evalDate, defaultOffset))
        Assertions.assertEquals(expectedDate.value, actualDate!!.getValue())

        expectedDate = DateTimeType(evalTimeWithOffset)
        actualDate = typeConverter.toFhirDateTime(DateTime("" + evaluationTime.year, defaultOffset))
        expectedDate.precision = TemporalPrecisionEnum.YEAR
        Assertions.assertEquals(expectedDate.value, actualDate!!.getValue())
        Assertions.assertEquals(expectedDate.valueAsString, actualDate.valueAsString)
    }

    @Test
    fun dateTimeToFhirDateTimeTimezones() {
        var expectedDate = DateTimeType("2019-10-10T01:00:00-06:00")
        expectedDate.timeZone = TimeZone.getTimeZone("MST")
        var actualDate: IPrimitiveType<Date>? =
            typeConverter.toFhirDateTime(DateTime("2019-10-10T00:00:00", ZoneOffset.ofHours(-7)))
        Assertions.assertEquals(expectedDate.valueAsString, actualDate!!.valueAsString)

        expectedDate = DateTimeType("2019-10-10T19:35:53.000Z")
        expectedDate.precision = TemporalPrecisionEnum.MILLI
        actualDate =
            typeConverter.toFhirDateTime(
                DateTime("2019-10-10T19:35:53", ZoneOffset.UTC).withPrecision(Precision.MILLISECOND)
            )
        Assertions.assertEquals(expectedDate.valueAsString, actualDate!!.valueAsString)
    }

    @Test
    fun quantityToFhirQuantity() {
        val expected =
            Quantity()
                .setValue(BigDecimal("2.0"))
                .setCode("ml")
                .setSystem("http://unitsofmeasure.org")
                .setUnit("ml")
        val actual =
            typeConverter.toFhirQuantity(
                org.opencds.cqf.cql.engine.runtime
                    .Quantity()
                    .withValue(BigDecimal("2.0"))
                    .withUnit("ml")
            ) as Quantity?
        Assertions.assertTrue(expected.equalsDeep(actual))
    }

    @Test
    fun ratioToFhirRatio() {
        val expectedNumerator =
            Quantity()
                .setValue(BigDecimal("1.0"))
                .setCode("ml")
                .setSystem("http://unitsofmeasure.org")
                .setUnit("ml")
        val expectedDenominator =
            Quantity()
                .setValue(BigDecimal("2.0"))
                .setCode("ml")
                .setSystem("http://unitsofmeasure.org")
                .setUnit("ml")

        val expected = Ratio().setNumerator(expectedNumerator).setDenominator(expectedDenominator)

        val testData = org.opencds.cqf.cql.engine.runtime.Ratio()
        testData.numerator =
            org.opencds.cqf.cql.engine.runtime
                .Quantity()
                .withValue(BigDecimal.valueOf(1.0))
                .withUnit("ml")
        testData.denominator =
            org.opencds.cqf.cql.engine.runtime
                .Quantity()
                .withValue(BigDecimal.valueOf(2.0))
                .withUnit("ml")

        val actual = typeConverter.toFhirRatio(testData) as Ratio?

        Assertions.assertTrue(expected.equalsDeep(actual))
    }

    @Test
    fun nullToFhirAny() {
        val expected: IBase? = typeConverter.toFhirAny(null)
        Assertions.assertNull(expected)
    }

    @Test
    fun objectToFhirAny() {
        Assertions.assertThrows(NotImplementedException::class.java) {
            typeConverter.toFhirAny("Huh")
        }
    }

    @Test
    fun codeToFhirCoding() {
        var expected: Coding? =
            Coding()
                .setSystem("http://the-system.com")
                .setCode("test")
                .setDisplay("system-test")
                .setVersion("1.5")
        val actual =
            typeConverter.toFhirCoding(
                Code()
                    .withSystem("http://the-system.com")
                    .withCode("test")
                    .withDisplay("system-test")
                    .withVersion("1.5")
            ) as Coding?
        Assertions.assertTrue(expected!!.equalsDeep(actual))

        expected = typeConverter.toFhirCoding(null) as Coding?
        Assertions.assertNull(expected)
    }

    @Test
    fun conceptToFhirCodeableConcept() {
        var expected =
            CodeableConcept(
                    Coding()
                        .setSystem("http://the-system.com")
                        .setCode("test")
                        .setDisplay("system-test")
                        .setVersion("1.5")
                )
                .setText("additional-text")
        val actual =
            typeConverter.toFhirCodeableConcept(
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

        expected = typeConverter.toFhirCodeableConcept(null) as CodeableConcept?
        Assertions.assertNull(expected)
    }

    @ParameterizedTest
    @MethodSource("startAndEndTimes")
    fun intervalToFhirPeriodYyyyMMdd(startTime: LocalDateTime, endTime: LocalDateTime) {
        val startTimeAsyyyyMMdd = YYYY_MM_DD.format(startTime)
        val endTimeAsyyyyMMdd = YYYY_MM_DD.format(endTime)

        val expected =
            Period()
                .setStartElement(DateTimeType(startTimeAsyyyyMMdd))
                .setEndElement(DateTimeType(endTimeAsyyyyMMdd))
        val actual =
            typeConverter.toFhirPeriod(
                Interval(
                    org.opencds.cqf.cql.engine.runtime.Date(startTimeAsyyyyMMdd),
                    true,
                    org.opencds.cqf.cql.engine.runtime.Date(endTimeAsyyyyMMdd),
                    true,
                )
            ) as Period?
        Assertions.assertTrue(expected.equalsDeep(actual))
    }

    @ParameterizedTest
    @MethodSource("dateTimes")
    fun intervalToFhirPeriodTimestampWithOffsets(
        now: LocalDateTime,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
    ) {
        val zonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault())
        val defaultOffset = zonedDateTime.offset

        val startTimeWithOffset =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(startTime.atOffset(defaultOffset))
        val endTimeWithOffset =
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(endTime.atOffset(defaultOffset))
        val startTimeNoOffset =
            DateTimeFormatter.ISO_DATE_TIME.format(startTime.atOffset(defaultOffset))
        val endTimeNoOffset =
            DateTimeFormatter.ISO_DATE_TIME.format(endTime.atOffset(defaultOffset))

        val dateTimeTypeStart = DateTimeType(startTimeWithOffset)
        val dateTimeTypeEnd = DateTimeType(endTimeWithOffset)
        val expected = Period().setStartElement(dateTimeTypeStart).setEndElement(dateTimeTypeEnd)

        val dateTimeStart = DateTime(startTimeNoOffset, defaultOffset)
        val dateTimeEnd = DateTime(endTimeNoOffset, defaultOffset)
        val intervalStartEnd = Interval(dateTimeStart, true, dateTimeEnd, true)
        val actual = typeConverter.toFhirPeriod(intervalStartEnd) as Period?

        Assertions.assertTrue(expected.equalsDeep(actual))
    }

    @ParameterizedTest
    @MethodSource("startAndEndYears")
    fun intervalToFhirPeriodStartAndEndYears(now: LocalDateTime, startYear: Int, endYear: Int) {
        val zonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault())
        val defaultOffset = zonedDateTime.offset

        val expected =
            Period()
                .setStartElement(DateTimeType("$startYear-01-01T00:00:00$defaultOffset"))
                .setEndElement(DateTimeType("$endYear-01-01T00:00:00$defaultOffset"))
        val actual =
            typeConverter.toFhirPeriod(
                Interval(
                    DateTime("" + startYear, defaultOffset),
                    true,
                    DateTime("" + endYear, defaultOffset),
                    true,
                )
            ) as Period?
        Assertions.assertTrue(expected.equalsDeep(actual))
    }

    @Test
    fun intervalToFhirPeriodNull() {
        Assertions.assertNull(typeConverter.toFhirPeriod(null))
    }

    @Test
    fun invalidIntervalToFhirPeriod() {
        val interval = Interval(5, true, 6, true)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            typeConverter.toFhirPeriod(interval)
        }
    }

    @Test
    fun intervalToFhirRange() {
        val expected =
            Range()
                .setLow(
                    SimpleQuantity()
                        .setValue(BigDecimal("2.0"))
                        .setCode("ml")
                        .setSystem("http://unitsofmeasure.org") as SimpleQuantity?
                )
                .setHigh(
                    SimpleQuantity()
                        .setValue(BigDecimal("5.0"))
                        .setCode("ml")
                        .setSystem("http://unitsofmeasure.org") as SimpleQuantity?
                )
        var actual =
            typeConverter.toFhirRange(
                Interval(
                    org.opencds.cqf.cql.engine.runtime
                        .Quantity()
                        .withValue(BigDecimal("2.0"))
                        .withUnit("ml"),
                    true,
                    org.opencds.cqf.cql.engine.runtime
                        .Quantity()
                        .withValue(BigDecimal("5.0"))
                        .withUnit("ml"),
                    true,
                )
            ) as Range?
        Assertions.assertTrue(expected.equalsDeep(actual))

        actual = typeConverter.toFhirRange(null) as Range?
        Assertions.assertNull(actual)
    }

    @Test
    fun invalidIntervalToFhirRange() {
        val interval = Interval(5, true, 6, true)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            typeConverter.toFhirRange(interval)
        }
    }

    @Test
    fun intervalToFhirInterval() {
        val expectedPeriod =
            Period()
                .setStartElement(DateTimeType("2019-02-03"))
                .setEndElement(DateTimeType("2019-02-05"))
        val actualPeriod =
            typeConverter.toFhirInterval(
                Interval(
                    org.opencds.cqf.cql.engine.runtime.Date("2019-02-03"),
                    true,
                    org.opencds.cqf.cql.engine.runtime.Date("2019-02-05"),
                    true,
                )
            ) as Period?
        Assertions.assertTrue(expectedPeriod.equalsDeep(actualPeriod))

        val expectedRange =
            Range()
                .setLow(
                    SimpleQuantity()
                        .setValue(BigDecimal("2.0"))
                        .setCode("ml")
                        .setSystem("http://unitsofmeasure.org") as SimpleQuantity?
                )
                .setHigh(
                    SimpleQuantity()
                        .setValue(BigDecimal("5.0"))
                        .setCode("ml")
                        .setSystem("http://unitsofmeasure.org") as SimpleQuantity?
                )
        val actualRange =
            typeConverter.toFhirInterval(
                Interval(
                    org.opencds.cqf.cql.engine.runtime
                        .Quantity()
                        .withValue(BigDecimal("2.0"))
                        .withUnit("ml"),
                    true,
                    org.opencds.cqf.cql.engine.runtime
                        .Quantity()
                        .withValue(BigDecimal("5.0"))
                        .withUnit("ml"),
                    true,
                )
            ) as Range?
        Assertions.assertTrue(expectedRange.equalsDeep(actualRange))

        val expected: IBase? = typeConverter.toFhirInterval(null)
        Assertions.assertNull(expected)
    }

    @Test
    fun integerIntervalToFhirString() {
        val interval = Interval(5, true, 6, true)
        val result: IBase? = typeConverter.toFhirInterval(interval)
        Assertions.assertNotNull(result)
        val stringType = Assertions.assertInstanceOf(StringType::class.java, result)
        Assertions.assertEquals("Interval[5, 6]", stringType!!.value)
        val extension =
            stringType
                .getExtension()
                .stream()
                .filter { e: Extension? -> e!!.getUrl() == FhirTypeConverter.CQL_TEXT_EXT_URL }
                .findFirst()
        Assertions.assertTrue(extension.isPresent)
    }

    @Test
    fun exceptionToFhirOperationOutcome() {
        val exception = IllegalArgumentException("Test exception")
        exception.fillInStackTrace()
        val result: IBaseOperationOutcome? = typeConverter.toFhirOperationOutcome(exception)
        Assertions.assertNotNull(result)
        val outcome = Assertions.assertInstanceOf(OperationOutcome::class.java, result)
        Assertions.assertEquals(1, outcome!!.getIssue().size)
        val issue = outcome.getIssue()[0]
        Assertions.assertEquals(OperationOutcome.IssueType.EXCEPTION, issue.getCode())
        Assertions.assertEquals(OperationOutcome.IssueSeverity.ERROR, issue.getSeverity())
        Assertions.assertEquals("Test exception", issue.getDiagnostics())

        val stackTrace =
            issue
                .getExtension()
                .stream()
                .filter { e: Extension? ->
                    e!!.getUrl() == FhirTypeConverter.NATIVE_STACK_TRACE_EXT_URL
                }
                .findFirst()
        Assertions.assertTrue(stackTrace.isPresent)
        Assertions.assertNotNull(stackTrace.get().getValue().toString())
    }

    @Test
    fun tupleToFhirTuple() {
        var actual = typeConverter.toFhirTuple(null) as Parameters.ParametersParameterComponent?
        Assertions.assertNull(actual)

        val tuple = Tuple()
        actual = typeConverter.toFhirTuple(tuple) as Parameters.ParametersParameterComponent
        Assertions.assertNotNull(actual)
        Assertions.assertEquals(
            FhirTypeConverter.EMPTY_TUPLE_EXT_URL,
            actual.getValue().getExtension()[0].getUrl(),
        )

        val ints = ArrayList<Int?>()
        for (i in 0..4) {
            ints.add(i)
        }

        tuple.elements["V"] = ints
        tuple.elements["W"] = null
        tuple.elements["X"] = 5
        tuple.elements["Y"] = Encounter().setId("123")
        tuple.elements["Z"] = ArrayList<Any?>()

        actual = typeConverter.toFhirTuple(tuple) as Parameters.ParametersParameterComponent
        val first = actual
        Assertions.assertEquals(9, first.getPart().size)

        val v: MutableList<Parameters.ParametersParameterComponent> = getPartsByName(first, "V")
        Assertions.assertEquals(5, v.size)
        Assertions.assertEquals(0, (v[0].getValue() as IntegerType).value)

        val w: Parameters.ParametersParameterComponent = getPartsByName(first, "W")[0]
        Assertions.assertEquals(
            FhirTypeConverter.DATA_ABSENT_REASON_EXT_URL,
            w.getValue().getExtension()[0].getUrl(),
        )

        val x: Parameters.ParametersParameterComponent = getPartsByName(first, "X")[0]
        Assertions.assertEquals(5, (x.getValue() as IntegerType).value)

        val y: Parameters.ParametersParameterComponent = getPartsByName(first, "Y")[0]
        Assertions.assertEquals("123", y.getResource().getId())

        val z: Parameters.ParametersParameterComponent = getPartsByName(first, "Z")[0]
        Assertions.assertEquals(
            FhirTypeConverter.EMPTY_LIST_EXT_URL,
            z.getValue().getExtension()[0].getUrl(),
        )
    }

    @Test
    fun complexTupleToFhirTuple() {
        val innerTuple = Tuple()
        innerTuple.elements["X"] = 1
        innerTuple.elements["Y"] = 2
        innerTuple.elements["Z"] = null
        val outerTuple = Tuple()
        outerTuple.elements["A"] = innerTuple
        val tupleList = ArrayList<Tuple?>()
        for (i in 0..2) {
            val elementTuple = Tuple()
            elementTuple.elements["P"] = i
            elementTuple.elements["Q"] = i + 1
            tupleList.add(elementTuple)
        }
        outerTuple.elements["B"] = tupleList

        val actual =
            typeConverter.toFhirTuple(outerTuple) as Parameters.ParametersParameterComponent
        Assertions.assertEquals(4, actual.getPart().size)

        val a: MutableList<Parameters.ParametersParameterComponent> = getPartsByName(actual, "A")
        Assertions.assertEquals(1, a.size)
        Assertions.assertEquals(3, a[0].getPart().size)
        val x = a[0].getPart()[0]
        Assertions.assertEquals(1, (x.getValue() as IntegerType).value)
        val y = a[0].getPart()[1]
        Assertions.assertEquals(2, (y.getValue() as IntegerType).value)
        val z = a[0].getPart()[2]
        Assertions.assertEquals(
            FhirTypeConverter.DATA_ABSENT_REASON_EXT_URL,
            z.getValue().getExtension()[0].getUrl(),
        )

        val b: MutableList<Parameters.ParametersParameterComponent> = getPartsByName(actual, "B")
        Assertions.assertEquals(3, b.size)
        val b1 = b[0]
        val p: MutableList<Parameters.ParametersParameterComponent> = getPartsByName(b1, "P")
        Assertions.assertEquals(1, p.size)
        Assertions.assertEquals(0, (p[0].getValue() as IntegerType).value)
        val q: MutableList<Parameters.ParametersParameterComponent> = getPartsByName(b1, "Q")
        Assertions.assertEquals(1, q.size)
        Assertions.assertEquals(1, (q[0].getValue() as IntegerType).value)
    }

    // FHIR-to-CQL
    @Test
    fun isCqlType() {
        Assertions.assertTrue(typeConverter.isCqlType(5))
        Assertions.assertTrue(typeConverter.isCqlType(BigDecimal(0)))
        Assertions.assertTrue(typeConverter.isCqlType(Code()))

        Assertions.assertFalse(typeConverter.isCqlType(Patient()))
        Assertions.assertFalse(typeConverter.isCqlType(IdType()))
    }

    @Test
    fun iterableIsCqlType() {
        val value = ArrayList<Any?>()
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            typeConverter.isCqlType(value)
        }
    }

    @Test
    fun toCqlType() {
        var actual: Any? = typeConverter.toCqlType(Code())
        MatcherAssert.assertThat<Any?>(actual, Matchers.instanceOf<Any?>(Code::class.java))

        actual = typeConverter.toCqlType(IntegerType(5))
        MatcherAssert.assertThat(actual, Matchers.instanceOf(Int::class.java))

        actual = typeConverter.toCqlType(StringType("test"))
        MatcherAssert.assertThat(actual, Matchers.instanceOf(String::class.java))

        actual = typeConverter.toCqlType(IdType("test"))
        MatcherAssert.assertThat(actual, Matchers.instanceOf(String::class.java))

        actual = typeConverter.toCqlType(BooleanType(true))
        MatcherAssert.assertThat(actual, Matchers.instanceOf(Boolean::class.java))

        actual = typeConverter.toCqlType(DecimalType(1.0))
        MatcherAssert.assertThat(actual, Matchers.instanceOf(BigDecimal::class.java))

        actual = typeConverter.toCqlType(DateType(Calendar.getInstance().getTime()))
        MatcherAssert.assertThat(
            actual,
            Matchers.instanceOf(org.opencds.cqf.cql.engine.runtime.Date::class.java),
        )

        actual = typeConverter.toCqlType(InstantType(Calendar.getInstance()))
        MatcherAssert.assertThat(actual, Matchers.instanceOf(DateTime::class.java))

        actual = typeConverter.toCqlType(DateTimeType(Calendar.getInstance()))
        MatcherAssert.assertThat(actual, Matchers.instanceOf(DateTime::class.java))

        actual = typeConverter.toCqlType(TimeType("10:00:00.0000"))
        MatcherAssert.assertThat(actual, Matchers.instanceOf(Time::class.java))

        actual = typeConverter.toCqlType(StringType("test"))
        MatcherAssert.assertThat(actual, Matchers.instanceOf(String::class.java))

        actual = typeConverter.toCqlType(Quantity())
        MatcherAssert.assertThat(
            actual,
            Matchers.instanceOf(org.opencds.cqf.cql.engine.runtime.Quantity::class.java),
        )

        actual = typeConverter.toCqlType(Ratio())
        MatcherAssert.assertThat(
            actual,
            Matchers.instanceOf(org.opencds.cqf.cql.engine.runtime.Ratio::class.java),
        )

        actual = typeConverter.toCqlType(Coding())
        MatcherAssert.assertThat(actual, Matchers.instanceOf(Code::class.java))

        actual = typeConverter.toCqlType(CodeableConcept())
        MatcherAssert.assertThat(actual, Matchers.instanceOf(Concept::class.java))

        actual =
            typeConverter.toCqlType(
                Period()
                    .setStart(Calendar.getInstance().getTime())
                    .setEnd(Calendar.getInstance().getTime())
            )
        MatcherAssert.assertThat(actual, Matchers.instanceOf(Interval::class.java))

        val low = SimpleQuantity()
        low.setValue(BigDecimal.valueOf(1.0))
        low.setUnit("d")

        val high = SimpleQuantity()
        high.setValue(BigDecimal.valueOf(4.0))
        high.setUnit("d")
        actual = typeConverter.toCqlType(Range().setLow(low).setHigh(high))
        MatcherAssert.assertThat(actual, Matchers.instanceOf(Interval::class.java))

        actual = typeConverter.toCqlType(null)
        Assertions.assertNull(actual)
    }

    @Test
    fun toCqlTypeIterable() {
        val list = ArrayList<Any?>()
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            typeConverter.toCqlType(list)
        }
    }

    @Test
    fun toCqlTypeNotCql() {
        val offset = ZoneOffset.ofHours(3)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            typeConverter.toCqlType(offset)
        }
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

        val actual: Iterable<Any?> = typeConverter.toCqlTypes(test)

        Assertions.assertTrue(compareIterables(expected, actual))
    }

    @Test
    fun stringToCqlId() {
        val expected = "123"
        var actual: String? = typeConverter.toCqlId(IdType("123"))
        Assertions.assertEquals(expected, actual)

        actual = typeConverter.toCqlId(null)
        Assertions.assertNull(actual)
    }

    @Test
    fun primitiveFhirTypeToCqlType() {
        var actualBoolean: Boolean? =
            typeConverter.toCqlBoolean(org.hl7.fhir.r5.model.BooleanType(false))
        Assertions.assertFalse(actualBoolean == true)

        actualBoolean = typeConverter.toCqlBoolean(null)
        Assertions.assertNull(actualBoolean)

        var expectedInteger: Int? = 5
        val actualInteger: Int? = typeConverter.toCqlInteger(org.hl7.fhir.r5.model.IntegerType(5))
        Assertions.assertEquals(expectedInteger, actualInteger)

        expectedInteger = typeConverter.toCqlInteger(null)
        Assertions.assertNull(expectedInteger)

        val expectedString = "5"
        var actualString: String? = typeConverter.toCqlString(org.hl7.fhir.r5.model.StringType("5"))
        Assertions.assertEquals(expectedString, actualString)

        actualString = typeConverter.toCqlString(null)
        Assertions.assertNull(actualString)

        val expectedDecimal = BigDecimal("2.0")
        var actualDecimal: BigDecimal? =
            typeConverter.toCqlDecimal(org.hl7.fhir.r5.model.DecimalType(BigDecimal("2.0")))
        Assertions.assertEquals(expectedDecimal, actualDecimal)

        actualDecimal = typeConverter.toCqlDecimal(null)
        Assertions.assertNull(actualDecimal)
    }

    @Test
    fun dateToCqlType() {
        var expectedDate = org.opencds.cqf.cql.engine.runtime.Date("2019-02-03")
        var actualDate: org.opencds.cqf.cql.engine.runtime.Date? =
            typeConverter.toCqlDate(DateType("2019-02-03"))
        Assertions.assertTrue(equal(expectedDate, actualDate) == true)

        expectedDate = org.opencds.cqf.cql.engine.runtime.Date("2019")
        actualDate = typeConverter.toCqlDate(DateType("2019"))
        Assertions.assertTrue(equal(expectedDate, actualDate) == true)
    }

    @Test
    fun dateTimeToCqlType() {
        var expectedDate = DateTime("2019-02-03", ZoneOffset.UTC)
        var actualDate: DateTime? = typeConverter.toCqlDateTime(DateTimeType("2019-02-03"))
        Assertions.assertTrue(equal(expectedDate, actualDate) == true)

        expectedDate = DateTime("2019", ZoneOffset.UTC)
        actualDate = typeConverter.toCqlDateTime(DateTimeType("2019"))
        Assertions.assertTrue(equal(expectedDate, actualDate) == true)

        expectedDate = DateTime("2019", ZoneOffset.UTC)
        actualDate = typeConverter.toCqlDateTime(DateTimeType("2019"))
        Assertions.assertTrue(equal(expectedDate, actualDate) == true)
    }

    @Test
    fun quantityToCqlType() {
        val expected =
            (org.opencds.cqf.cql.engine.runtime
                .Quantity()
                .withValue(BigDecimal("2.0"))
                .withUnit("ml"))
        val actual: org.opencds.cqf.cql.engine.runtime.Quantity? =
            typeConverter.toCqlQuantity(
                Quantity()
                    .setValue(BigDecimal("2.0"))
                    .setUnit("ml")
                    .setSystem("http://unitsofmeasure.org")
            )
        Assertions.assertTrue(equal(expected, actual) == true)
    }

    @Test
    fun ratioToCqlType() {
        val expected = org.opencds.cqf.cql.engine.runtime.Ratio()
        expected.numerator =
            org.opencds.cqf.cql.engine.runtime
                .Quantity()
                .withValue(BigDecimal.valueOf(1.0))
                .withUnit("ml")
        expected.denominator =
            org.opencds.cqf.cql.engine.runtime
                .Quantity()
                .withValue(BigDecimal.valueOf(2.0))
                .withUnit("ml")

        val testNumerator =
            Quantity()
                .setValue(BigDecimal("1.0"))
                .setUnit("ml")
                .setSystem("http://unitsofmeasure.org")
        val testDenominator =
            Quantity()
                .setValue(BigDecimal("2.0"))
                .setUnit("ml")
                .setSystem("http://unitsofmeasure.org")

        val test = Ratio().setNumerator(testNumerator).setDenominator(testDenominator)

        val actual: org.opencds.cqf.cql.engine.runtime.Ratio? = typeConverter.toCqlRatio(test)
        Assertions.assertTrue(equal(expected, actual) == true)
    }

    @Test
    fun nullToCqlType() {
        val expected: Any? = typeConverter.toCqlAny(null)
        Assertions.assertNull(expected)
    }

    @Test
    fun objectToCqlType() {
        val id = IdType()
        Assertions.assertThrows(NotImplementedException::class.java) { typeConverter.toCqlAny(id) }
    }

    @Test
    fun codingToCqlCode() {
        var expected: Code? =
            Code()
                .withSystem("http://the-system.com")
                .withCode("test")
                .withDisplay("system-test")
                .withVersion("1.5")
        val actual: Code? =
            typeConverter.toCqlCode(
                Coding()
                    .setSystem("http://the-system.com")
                    .setCode("test")
                    .setDisplay("system-test")
                    .setVersion("1.5")
            )
        Assertions.assertTrue(equal(expected, actual) == true)

        expected = typeConverter.toCqlCode(null)
        Assertions.assertNull(expected)
    }

    @Test
    fun codeableConceptToCqlConcept() {
        var expected: Concept? =
            Concept()
                .withCode(
                    Code()
                        .withSystem("http://the-system.com")
                        .withCode("test")
                        .withDisplay("system-test")
                        .withVersion("1.5")
                )
                .withDisplay("additional-text")
        val actual: Concept? =
            typeConverter.toCqlConcept(
                CodeableConcept(
                        Coding()
                            .setSystem("http://the-system.com")
                            .setCode("test")
                            .setDisplay("system-test")
                            .setVersion("1.5")
                    )
                    .setText("additional-text")
            )

        Assertions.assertTrue(equal(expected, actual) == true)

        expected = typeConverter.toCqlConcept(null)
        Assertions.assertNull(expected)
    }

    @Test
    fun periodToCqlInterval() {
        var expected =
            Interval(
                org.opencds.cqf.cql.engine.runtime.Date("2019-02-03"),
                true,
                org.opencds.cqf.cql.engine.runtime.Date("2019-02-05"),
                true,
            )
        var actual: Interval? =
            typeConverter.toCqlInterval(
                Period()
                    .setStartElement(DateTimeType("2019-02-03"))
                    .setEndElement(DateTimeType("2019-02-05"))
            )
        Assertions.assertTrue(equal(expected, actual) == true)

        expected =
            Interval(
                org.opencds.cqf.cql.engine.runtime.Date("2019"),
                true,
                org.opencds.cqf.cql.engine.runtime.Date("2020"),
                true,
            )
        actual =
            typeConverter.toCqlInterval(
                Period().setStartElement(DateTimeType("2019")).setEndElement(DateTimeType("2020"))
            )
        Assertions.assertTrue(equal(expected, actual) == true)

        expected =
            Interval(
                DateTime("2020-09-18T19:35:53", ZoneOffset.UTC),
                true,
                DateTime("2020-09-18T19:37:00", ZoneOffset.UTC),
                true,
            )
        actual =
            typeConverter.toCqlInterval(
                Period()
                    .setStartElement(DateTimeType("2020-09-18T19:35:53+00:00"))
                    .setEndElement(DateTimeType("2020-09-18T19:37:00+00:00"))
            )
        Assertions.assertTrue(equal(expected, actual) == true)

        actual = typeConverter.toCqlInterval(null)
        Assertions.assertNull(actual)
    }

    @Test
    fun rangeToCqlInterval() {
        val expected =
            Interval(
                org.opencds.cqf.cql.engine.runtime
                    .Quantity()
                    .withValue(BigDecimal("2.0"))
                    .withUnit("ml"),
                true,
                org.opencds.cqf.cql.engine.runtime
                    .Quantity()
                    .withValue(BigDecimal("5.0"))
                    .withUnit("ml"),
                true,
            )
        var actual: Interval? =
            typeConverter.toCqlInterval(
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
        Assertions.assertTrue(equal(expected, actual) == true)

        actual = typeConverter.toCqlInterval(null)
        Assertions.assertNull(actual)
    }

    @Test
    fun invalidTypeToCqlInterval() {
        val attachment = Attachment()
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            typeConverter.toCqlInterval(attachment)
        }
    }

    @Test
    fun tupleToCqlTuple() {
        val expected: Any? = typeConverter.toCqlTuple(null)
        Assertions.assertNull(expected)

        val p = Patient()
        Assertions.assertThrows(NotImplementedException::class.java) { typeConverter.toCqlTuple(p) }
    }

    @Test
    fun longToCqlLong() {
        val expected = 5L
        val actual: Any? = typeConverter.toCqlType(expected)
        Assertions.assertEquals(expected, actual)
    }

    @Test
    fun longToFhirInteger64() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            typeConverter.toFhirInteger64(5L)
        }
    }

    companion object {
        private val typeConverter = Dstu2FhirTypeConverter()

        @JvmStatic
        private fun nowsAndEvaluationTimes(): Array<Array<Any?>?> {
            return ConverterTestUtils.nowsAndEvaluationTimes()
        }

        @JvmStatic
        private fun startAndEndTimes(): Array<Array<Any?>?> {
            return ConverterTestUtils.startAndEndTimes()
        }

        @JvmStatic
        private fun dateTimes(): Array<Array<Any?>?> {
            return ConverterTestUtils.dateTimes()
        }

        @JvmStatic
        private fun startAndEndYears(): Array<Array<Any?>?> {
            return ConverterTestUtils.startAndEndYears()
        }

        private fun getPartsByName(
            ppc: Parameters.ParametersParameterComponent,
            name: String?,
        ): MutableList<Parameters.ParametersParameterComponent> {
            return ppc.getPart()
                .stream()
                .filter { p: Parameters.ParametersParameterComponent? -> p!!.getName() == name }
                .collect(Collectors.toList())
        }
    }
}
