package org.opencds.cqf.cql.engine.fhir.data

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue
import org.cqframework.cql.cql2elm.CqlCompilerOptions.Companion.defaultOptions
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.Environment
import org.opencds.cqf.cql.engine.runtime.Boolean

internal class TestFHIRHelpers : FhirExecutionTestBase() {
    @Test
    fun testWithAmbiguousCompilerOptions() {
        // This tests the behavior of the engine when the compiler
        // options are set to allow ambiguous overloads
        // It's expected that the engine will throw an exception
        //
        // If we update the FHIRHelpers content to not have ambiguous overloads
        // the results of this test will change
        val compilerOptions = defaultOptions()
        compilerOptions.signatureLevel = LibraryBuilder.SignatureLevel.None
        val modelManager = ModelManager()
        val libraryManager = LibraryManager(modelManager, compilerOptions)
        libraryManager.librarySourceLoader.clearProviders()
        libraryManager.librarySourceLoader.registerProvider(FhirLibrarySourceProvider())
        libraryManager.librarySourceLoader.registerProvider(TestLibrarySourceProvider())

        val badOptionsEngine = CqlEngine(Environment(libraryManager))
        badOptionsEngine.environment.registerDataProvider("http://hl7.org/fhir", r4Provider)

        val identifier = library!!.identifier
        assertFailsWith<CqlException> {
            badOptionsEngine.evaluate { library(identifier!!) }.onlyResultOrThrow
        }
    }

    @Test
    fun testFhirHelpers() {
        engine.environment.registerDataProvider("http://hl7.org/fhir", r4Provider)
        val results = engine.evaluate { library(library!!.identifier!!) }.onlyResultOrThrow

        // Primitives
        // instant
        // define TestInstant: instant { value: @2020-10-03T10:00:00.0 }
        // define TestInstantConverts: TestInstant = @2020-10-03T10:00:00.0
        var value = results["TestInstantConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // time
        // define TestTime: time { value: @T10:00:00.0 }
        // define TestTimeConverts: TestTime = @T10:00:00.0
        value = results["TestTimeConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)
        // define TestHour: time { value: @T10 }
        // define TestHourConverts: TestHour = @T10
        value = results["TestHourConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)
        // define TestMinute: time { value: @T10:00 }
        // define TestMinuteConverts: TestMinute = @T10:00
        value = results["TestMinuteConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)
        // define TestSecond: time { value: @T10:00:00 }
        // define TestSecondConverts: TestSecond = @T10:00:00
        value = results["TestSecondConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // date
        // define TestDate: date { value: @2020-10-03 }
        // define TestDateConverts: TestDate = @2020-10-03
        value = results["TestDateConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)
        // define TestYear: date { value: @2020 }
        // define TestYearConverts: TestYear = @2020
        value = results["TestYearConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)
        // define TestMonth: date { value: @2020-10 }
        // define TestMonthConverts: TestMonth = @2020-10
        value = results["TestMonthConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // dateTime
        // define TestDateTime: dateTime { value: @2020-10-03T10:00:00.0 }
        // define TestDateTimeConverts: TestDateTime = @2020-10-03T10:00:00.0
        value = results["TestDateTimeConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)
        // define TestYearDateTime: dateTime { value: @2020T }
        // define TestYearDateTimeConverts: TestYearDateTime = @2020T
        value = results["TestYearDateTimeConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)
        // define TestMonthDateTime: dateTime { value: @2020-10T }
        // define TestMonthDateTimeConverts: TestMonthDateTime = @2020-10T
        value = results["TestMonthDateTimeConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)
        // define TestDayDateTime: dateTime { value: @2020-10-03T }
        // define TestDayDateTimeConverts: TestDayDateTime = @2020-10-03T
        value = results["TestDayDateTimeConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)
        // define TestHourDateTime: dateTime { value: @2020-10-03T10 }
        // define TestHourDateTimeConverts: TestHourDateTime = @2020-10-03T10
        // DateTime in FHIR does not support expressing times with only an hour component, so this
        // precision is lost in
        // the round-trip
        // value = results["TestHourDateTimeConverts"]!!.value;
        // assertThat(value, instanceOf(Boolean.class));
        // assertThat(value, is(true));
        // define TestMinuteDateTime: dateTime { value: @2020-10-03T10:00 }
        // define TestMinuteDateTimeConverts: TestMinuteDateTime = @2020-10-03T10:00
        value = results["TestMinuteDateTimeConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)
        // define TestSecondDateTime: dateTime { value: @2020-10-03T10:00:00 }
        // define TestSecondDateTimeConverts: TestSecondDateTime = @2020-10-03T10:00:00
        value = results["TestSecondDateTimeConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // base64Binary
        // define TestBase64Binary: base64Binary { value: 'Rm9vYmFy' }
        // define TestBase64BinaryConverts: TestBase64Binary = 'Rm9vYmFy'
        value = results["TestBase64BinaryConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // decimal
        // define TestDecimal: decimal { value: 10.0 }
        // define TestDecimalConverts: TestDecimal = 10.0
        value = results["TestDecimalConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // boolean
        // define TestBoolean: boolean { value: true }
        // define TestBooleanConverts: TestBoolean = true
        value = results["TestBooleanConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // uri
        // define TestUri: uri { value: 'http://hl7.org/fhir' }
        // define TestUriConverts: TestUri = 'http://hl7.org/fhir'
        value = results["TestUriConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // url
        // define TestUrl: url { value: 'http://hl7.org/fhir' }
        // define TestUrlConverts: TestUrl = 'http://hl7.org/fhir'
        value = results["TestUrlConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestUrlSpecificallyConverts: FHIRHelpers.ToString(TestUrl) = 'http://hl7.org/fhir'
        value = results["TestUrlSpecificallyConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // canonical
        // define TestCanonical: canonical { value: 'http://hl7.org/fhir/CodeSystem/calendar-units'
        // }
        // define TestCanonicalConverts: TestCanonical =
        // 'http://hl7.org/fhir/CodeSystem/calendar-units'
        value = results["TestCanonicalConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestCanonicalSpecificallyConverts: FHIRHelpers.ToString(TestCanonical) =
        // 'http://hl7.org/fhir/CodeSystem/calendar-units'
        value = results["TestCanonicalSpecificallyConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // uuid
        // define TestUuid: uuid { value: 'urn:uuid:c757873d-ec9a-4326-a141-556f43239520'}
        // define TestUuidConverts: TestUuid = 'urn:uuid:c757873d-ec9a-4326-a141-556f43239520'
        value = results["TestUuidConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestUuidSpecificallyConverts: FHIRHelpers.ToString(TestUuid) =
        // 'urn:uuid:c757873d-ec9a-4326-a141-556f43239520'
        value = results["TestUuidSpecificallyConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // oid
        // define TestOid: oid { value: 'urn:oid:1.2.3.4.5' }
        // define TestOidConverts: TestOid = 'urn:oid:1.2.3.4.5'
        value = results["TestOidConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestOidSpecificallyConverts: FHIRHelpers.ToString(TestOid) = 'urn:oid:1.2.3.4.5'
        value = results["TestOidSpecificallyConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // integer
        // define TestInteger: integer { value: 1 }
        // define TestIntegerConverts: TestInteger = 1
        value = results["TestIntegerConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestIntegerSpecificallyConverts: FHIRHelpers.ToInteger(TestInteger) = 1
        value = results["TestIntegerSpecificallyConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // unsignedInt
        // define TestUnsignedInt: unsignedInt { value: 1 }
        // define TestUnsignedIntConverts: TestUnsignedInt = 1
        value = results["TestUnsignedIntConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestUnsignedIntSpecificallyConverts: FHIRHelpers.ToInteger(TestUnsignedInt) = 1
        value = results["TestUnsignedIntSpecificallyConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // positiveInt
        // define TestPositiveInt: positiveInt { value: 1 }
        // define TestPositiveIntConverts: TestPositiveInt = 1
        value = results["TestPositiveIntConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestPositiveIntSpecificallyConverts: FHIRHelpers.ToInteger(TestPositiveInt) = 1
        value = results["TestPositiveIntSpecificallyConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // string
        // define TestString: string { value: 'Foobar' }
        // define TestStringConverts: TestString = 'Foobar'
        value = results["TestStringConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // code
        // define TestCode: code { value: 'year' }
        // define TestCodeConverts: TestCode = 'year'
        value = results["TestCodeConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestCodeSpecificallyConverts: FHIRHelpers.ToString(TestCode) = 'year'
        value = results["TestCodeSpecificallyConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // markdown
        // define TestMarkdown: markdown { value: '#Markdown Content' }
        // define TestMarkdownConverts: TestMarkdown = '#Markdown Content'
        value = results["TestMarkdownConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestMarkdownSpecificallyConverts: FHIRHelpers.ToString(TestMarkdown) = '#Markdown
        // Content'
        value = results["TestMarkdownSpecificallyConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // id
        // define TestId: id { value: 'calendar-units' }
        // define TestIdConverts: TestId = 'calendar-units'
        value = results["TestIdConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestIdSpecificallyConverts: FHIRHelpers.ToString(TestId) = 'calendar-units'
        value = results["TestIdSpecificallyConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // Ratio
        // define TestRatio: Ratio {
        //    numerator: Quantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' } },
        //    denominator: Quantity { value: decimal { value: 100.0 }, unit: string { value: 'mg' }
        // }
        // }
        // define TestRatioConverts: TestRatio = 10.0 'mg' : 100.0 'mg'
        value = results["TestRatioConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // Period
        // define TestPeriod: Period {
        //    start: dateTime { value: @2020-10-03T10:00:00 },
        //    end: dateTime { value: @2020-10-03T10:00:00 }
        // }
        // define TestPeriodConverts: TestPeriod = Interval[@2020-10-03T10:00:00,
        // @2020-10-03T10:00:00]
        value = results["TestPeriodConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // Range
        // define TestRange: Range {
        //    low: SimpleQuantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' } },
        //    high: SimpleQuantity { value: decimal { value: 100.0 }, unit: string { value: 'mg' } }
        // }
        // define TestRangeConverts: TestRange = Interval[10.0 'mg', 100.0 'mg']
        value = results["TestRangeConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // Coding
        // define TestCoding: Coding {
        //    system: uri { value: 'http://hl7.org/fhir/CodeSystem/calendar-units' },
        //    code: code { value: 'year' },
        //    display: string { value: 'year' }
        // }
        // define TestCodingConverts: TestCoding = Code { code: 'year', system:
        // 'http://hl7.org/fhir/CodeSystem/calendar-units', display: 'year' }
        value = results["TestCodingConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // CodeableConcept
        // define TestCodeableConcept: CodeableConcept {
        //    coding: {
        //        Coding {
        //            system: uri { value: 'http://hl7.org/fhir/CodeSystem/calendar-units' },
        //            code: code { value: 'year' },
        //            display: string { value: 'year' }
        //        }
        //    }
        // }
        // define TestCodeableConceptConverts: TestCodeableConcept = Concept { codes: { Code { code:
        // 'year', system:
        // 'http://hl7.org/fhir/CodeSystem/calendar-units', display: 'year' } } }
        value = results["TestCodeableConceptConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // Money
        // Money implicit conversions are not supported

        // Quantity
        // define TestQuantity: Quantity { value: decimal { value: 10.0 }, unit: string { value:
        // 'mg' } }
        // define TestQuantityConverts: TestQuantity = 10.0 'mg'
        value = results["TestQuantityConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // Age
        // define TestAge: Age { value: decimal { value: 12.0 }, unit: string { value: 'a' },
        // system: uri { value:
        // 'http://unitsofmeasure.org' }, code: code { value: 'a' } }
        // define TestAgeConverts: TestAge = 12 years
        value = results["TestAgeConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestAgeSpecificallyConverts: FHIRHelpers.ToQuantity(TestAge) = 12 years
        value = results["TestAgeSpecificallyConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // Distance
        // define TestDistance: Distance { value: decimal { value: 100 }, unit: string { value: 'km'
        // }, system: uri {
        // value: 'http://unitsofmeasure.org' }, code: code { value: 'km' } }
        // define TestDistanceConverts: TestDistance = 100 'km'
        value = results["TestDistanceConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestDistanceSpecificallyConverts: FHIRHelpers.ToQuantity(TestDistance) = 100 'km'
        value = results["TestDistanceSpecificallyConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // Duration
        // define TestDuration: Duration { value: decimal { value: 100 }, unit: string { value: 's'
        // }, system: uri {
        // value: 'http://unitsofmeasure.org' }, code: code { value: 's' } }
        // define TestDurationConverts: TestDuration = 100 seconds
        value = results["TestDurationConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestDurationSpecificallyConverts: FHIRHelpers.ToQuantity(TestDuration) = 100
        // seconds
        value = results["TestDurationSpecificallyConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // Count
        // define TestCount: Count { value: decimal { value: 100 }, unit: string { value: '1' },
        // system: uri { value:
        // 'http://unitsofmeasure.org' }, code: code { value: '1' } }
        // define TestCountConverts: TestCount = 100 '1'
        value = results["TestCountConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestCountSpecificallyConverts: FHIRHelpers.ToQuantity(TestCount) = 100 '1'
        value = results["TestCountSpecificallyConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // MoneyQuantity
        // MoneyQuantity implicit conversions would result in a runtime error
        // SimpleQuantity
        // define TestSimpleQuantity: SimpleQuantity { value: decimal { value: 10 }, unit: string {
        // value: 'g' },
        // system: uri { value: 'http://unitsofmeasure.org' }, code: code { value: 'g' } }
        // define TestSimpleQuantityConverts: TestSimpleQuantity = 10 'g'
        value = results["TestSimpleQuantityConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestSimpleQuantitySpecificallyConverts: FHIRHelpers.ToQuantity(TestSimpleQuantity)
        // = 10 'g'
        value = results["TestSimpleQuantitySpecificallyConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // Quantity with Comparator
        // define TestQuantityWithoutComparator: Quantity { value: decimal { value: 10.0 }, unit:
        // string { value: 'mg' }
        // }
        // define TestQuantityWithoutComparatorConverts:
        // FHIRHelpers.ToInterval(TestQuantityWithoutComparator) =
        // Interval[10.0 'mg', 10.0 'mg']
        value = results["TestQuantityWithoutComparatorConverts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestQuantityWithComparator1: Quantity { value: decimal { value: 10.0 }, unit:
        // string { value: 'mg' },
        // comparator: FHIR.QuantityComparator { value: '<' } }
        // define TestQuantityWithComparator1Converts:
        // FHIRHelpers.ToInterval(TestQuantityWithComparator1) =
        // Interval[null, 10 'mg')
        value = results["TestQuantityWithComparator1Converts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestQuantityWithComparator2: Quantity { value: decimal { value: 10.0 }, unit:
        // string { value: 'mg' },
        // comparator: FHIR.QuantityComparator { value: '<=' } }
        // define TestQuantityWithComparator2Converts:
        // FHIRHelpers.ToInterval(TestQuantityWithComparator2) =
        // Interval[null, 10 'mg']
        value = results["TestQuantityWithComparator2Converts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestQuantityWithComparator3: Quantity { value: decimal { value: 10.0 }, unit:
        // string { value: 'mg' },
        // comparator: FHIR.QuantityComparator { value: '>=' } }
        // define TestQuantityWithComparator3Converts:
        // FHIRHelpers.ToInterval(TestQuantityWithComparator3) = Interval[10
        // 'mg', null]
        value = results["TestQuantityWithComparator3Converts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestQuantityWithComparator4: Quantity { value: decimal { value: 10.0 }, unit:
        // string { value: 'mg' },
        // comparator: FHIR.QuantityComparator { value: '>' } }
        // define TestQuantityWithComparator4Converts:
        // FHIRHelpers.ToInterval(TestQuantityWithComparator4) = Interval(10
        // 'mg', null]
        value = results["TestQuantityWithComparator4Converts"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)

        // define TestQuantityWithToValue: FHIRHelpers.ToValue(FHIR.Quantity { value: FHIR.decimal {
        // value: 1001 },
        // unit: FHIR.string { value: 'mg/dL' } }) >= 200 'mg/dL'
        value = results["TestQuantityWithToValue"]!!.value
        assertIs<Boolean>(value)
        assertTrue(value.value)
    }
}
