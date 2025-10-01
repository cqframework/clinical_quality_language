package org.opencds.cqf.cql.engine.fhir.data

import org.cqframework.cql.cql2elm.CqlCompilerOptions.Companion.defaultOptions
import org.cqframework.cql.cql2elm.LibraryBuilder
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.Environment

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
        Assertions.assertThrows(CqlException::class.java) { badOptionsEngine.evaluate(identifier) }
    }

    @Test
    fun testFhirHelpers() {
        engine.environment.registerDataProvider("http://hl7.org/fhir", r4Provider)
        val results = engine.evaluate(library!!.identifier)

        // Primitives
        // instant
        // define TestInstant: instant { value: @2020-10-03T10:00:00.0 }
        // define TestInstantConverts: TestInstant = @2020-10-03T10:00:00.0
        var value = results.forExpression("TestInstantConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // time
        // define TestTime: time { value: @T10:00:00.0 }
        // define TestTimeConverts: TestTime = @T10:00:00.0
        value = results.forExpression("TestTimeConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))
        // define TestHour: time { value: @T10 }
        // define TestHourConverts: TestHour = @T10
        value = results.forExpression("TestHourConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))
        // define TestMinute: time { value: @T10:00 }
        // define TestMinuteConverts: TestMinute = @T10:00
        value = results.forExpression("TestMinuteConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))
        // define TestSecond: time { value: @T10:00:00 }
        // define TestSecondConverts: TestSecond = @T10:00:00
        value = results.forExpression("TestSecondConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // date
        // define TestDate: date { value: @2020-10-03 }
        // define TestDateConverts: TestDate = @2020-10-03
        value = results.forExpression("TestDateConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))
        // define TestYear: date { value: @2020 }
        // define TestYearConverts: TestYear = @2020
        value = results.forExpression("TestYearConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))
        // define TestMonth: date { value: @2020-10 }
        // define TestMonthConverts: TestMonth = @2020-10
        value = results.forExpression("TestMonthConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // dateTime
        // define TestDateTime: dateTime { value: @2020-10-03T10:00:00.0 }
        // define TestDateTimeConverts: TestDateTime = @2020-10-03T10:00:00.0
        value = results.forExpression("TestDateTimeConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))
        // define TestYearDateTime: dateTime { value: @2020T }
        // define TestYearDateTimeConverts: TestYearDateTime = @2020T
        value = results.forExpression("TestYearDateTimeConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))
        // define TestMonthDateTime: dateTime { value: @2020-10T }
        // define TestMonthDateTimeConverts: TestMonthDateTime = @2020-10T
        value = results.forExpression("TestMonthDateTimeConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))
        // define TestDayDateTime: dateTime { value: @2020-10-03T }
        // define TestDayDateTimeConverts: TestDayDateTime = @2020-10-03T
        value = results.forExpression("TestDayDateTimeConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))
        // define TestHourDateTime: dateTime { value: @2020-10-03T10 }
        // define TestHourDateTimeConverts: TestHourDateTime = @2020-10-03T10
        // DateTime in FHIR does not support expressing times with only an hour component, so this
        // precision is lost in
        // the round-trip
        // value = results.forExpression("TestHourDateTimeConverts").value();
        // assertThat(value, instanceOf(Boolean.class));
        // assertThat(value, is(true));
        // define TestMinuteDateTime: dateTime { value: @2020-10-03T10:00 }
        // define TestMinuteDateTimeConverts: TestMinuteDateTime = @2020-10-03T10:00
        value = results.forExpression("TestMinuteDateTimeConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))
        // define TestSecondDateTime: dateTime { value: @2020-10-03T10:00:00 }
        // define TestSecondDateTimeConverts: TestSecondDateTime = @2020-10-03T10:00:00
        value = results.forExpression("TestSecondDateTimeConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // base64Binary
        // define TestBase64Binary: base64Binary { value: 'Rm9vYmFy' }
        // define TestBase64BinaryConverts: TestBase64Binary = 'Rm9vYmFy'
        value = results.forExpression("TestBase64BinaryConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // decimal
        // define TestDecimal: decimal { value: 10.0 }
        // define TestDecimalConverts: TestDecimal = 10.0
        value = results.forExpression("TestDecimalConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // boolean
        // define TestBoolean: boolean { value: true }
        // define TestBooleanConverts: TestBoolean = true
        value = results.forExpression("TestBooleanConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // uri
        // define TestUri: uri { value: 'http://hl7.org/fhir' }
        // define TestUriConverts: TestUri = 'http://hl7.org/fhir'
        value = results.forExpression("TestUriConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // url
        // define TestUrl: url { value: 'http://hl7.org/fhir' }
        // define TestUrlConverts: TestUrl = 'http://hl7.org/fhir'
        value = results.forExpression("TestUrlConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestUrlSpecificallyConverts: FHIRHelpers.ToString(TestUrl) = 'http://hl7.org/fhir'
        value = results.forExpression("TestUrlSpecificallyConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // canonical
        // define TestCanonical: canonical { value: 'http://hl7.org/fhir/CodeSystem/calendar-units'
        // }
        // define TestCanonicalConverts: TestCanonical =
        // 'http://hl7.org/fhir/CodeSystem/calendar-units'
        value = results.forExpression("TestCanonicalConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestCanonicalSpecificallyConverts: FHIRHelpers.ToString(TestCanonical) =
        // 'http://hl7.org/fhir/CodeSystem/calendar-units'
        value = results.forExpression("TestCanonicalSpecificallyConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // uuid
        // define TestUuid: uuid { value: 'urn:uuid:c757873d-ec9a-4326-a141-556f43239520'}
        // define TestUuidConverts: TestUuid = 'urn:uuid:c757873d-ec9a-4326-a141-556f43239520'
        value = results.forExpression("TestUuidConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestUuidSpecificallyConverts: FHIRHelpers.ToString(TestUuid) =
        // 'urn:uuid:c757873d-ec9a-4326-a141-556f43239520'
        value = results.forExpression("TestUuidSpecificallyConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // oid
        // define TestOid: oid { value: 'urn:oid:1.2.3.4.5' }
        // define TestOidConverts: TestOid = 'urn:oid:1.2.3.4.5'
        value = results.forExpression("TestOidConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestOidSpecificallyConverts: FHIRHelpers.ToString(TestOid) = 'urn:oid:1.2.3.4.5'
        value = results.forExpression("TestOidSpecificallyConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // integer
        // define TestInteger: integer { value: 1 }
        // define TestIntegerConverts: TestInteger = 1
        value = results.forExpression("TestIntegerConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestIntegerSpecificallyConverts: FHIRHelpers.ToInteger(TestInteger) = 1
        value = results.forExpression("TestIntegerSpecificallyConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // unsignedInt
        // define TestUnsignedInt: unsignedInt { value: 1 }
        // define TestUnsignedIntConverts: TestUnsignedInt = 1
        value = results.forExpression("TestUnsignedIntConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestUnsignedIntSpecificallyConverts: FHIRHelpers.ToInteger(TestUnsignedInt) = 1
        value = results.forExpression("TestUnsignedIntSpecificallyConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // positiveInt
        // define TestPositiveInt: positiveInt { value: 1 }
        // define TestPositiveIntConverts: TestPositiveInt = 1
        value = results.forExpression("TestPositiveIntConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestPositiveIntSpecificallyConverts: FHIRHelpers.ToInteger(TestPositiveInt) = 1
        value = results.forExpression("TestPositiveIntSpecificallyConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // string
        // define TestString: string { value: 'Foobar' }
        // define TestStringConverts: TestString = 'Foobar'
        value = results.forExpression("TestStringConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // code
        // define TestCode: code { value: 'year' }
        // define TestCodeConverts: TestCode = 'year'
        value = results.forExpression("TestCodeConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestCodeSpecificallyConverts: FHIRHelpers.ToString(TestCode) = 'year'
        value = results.forExpression("TestCodeSpecificallyConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // markdown
        // define TestMarkdown: markdown { value: '#Markdown Content' }
        // define TestMarkdownConverts: TestMarkdown = '#Markdown Content'
        value = results.forExpression("TestMarkdownConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestMarkdownSpecificallyConverts: FHIRHelpers.ToString(TestMarkdown) = '#Markdown
        // Content'
        value = results.forExpression("TestMarkdownSpecificallyConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // id
        // define TestId: id { value: 'calendar-units' }
        // define TestIdConverts: TestId = 'calendar-units'
        value = results.forExpression("TestIdConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestIdSpecificallyConverts: FHIRHelpers.ToString(TestId) = 'calendar-units'
        value = results.forExpression("TestIdSpecificallyConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // Ratio
        // define TestRatio: Ratio {
        //    numerator: Quantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' } },
        //    denominator: Quantity { value: decimal { value: 100.0 }, unit: string { value: 'mg' }
        // }
        // }
        // define TestRatioConverts: TestRatio = 10.0 'mg' : 100.0 'mg'
        value = results.forExpression("TestRatioConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // Period
        // define TestPeriod: Period {
        //    start: dateTime { value: @2020-10-03T10:00:00 },
        //    end: dateTime { value: @2020-10-03T10:00:00 }
        // }
        // define TestPeriodConverts: TestPeriod = Interval[@2020-10-03T10:00:00,
        // @2020-10-03T10:00:00]
        value = results.forExpression("TestPeriodConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // Range
        // define TestRange: Range {
        //    low: SimpleQuantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' } },
        //    high: SimpleQuantity { value: decimal { value: 100.0 }, unit: string { value: 'mg' } }
        // }
        // define TestRangeConverts: TestRange = Interval[10.0 'mg', 100.0 'mg']
        value = results.forExpression("TestRangeConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // Coding
        // define TestCoding: Coding {
        //    system: uri { value: 'http://hl7.org/fhir/CodeSystem/calendar-units' },
        //    code: code { value: 'year' },
        //    display: string { value: 'year' }
        // }
        // define TestCodingConverts: TestCoding = Code { code: 'year', system:
        // 'http://hl7.org/fhir/CodeSystem/calendar-units', display: 'year' }
        value = results.forExpression("TestCodingConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

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
        value = results.forExpression("TestCodeableConceptConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // Money
        // Money implicit conversions are not supported

        // Quantity
        // define TestQuantity: Quantity { value: decimal { value: 10.0 }, unit: string { value:
        // 'mg' } }
        // define TestQuantityConverts: TestQuantity = 10.0 'mg'
        value = results.forExpression("TestQuantityConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // Age
        // define TestAge: Age { value: decimal { value: 12.0 }, unit: string { value: 'a' },
        // system: uri { value:
        // 'http://unitsofmeasure.org' }, code: code { value: 'a' } }
        // define TestAgeConverts: TestAge = 12 years
        value = results.forExpression("TestAgeConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestAgeSpecificallyConverts: FHIRHelpers.ToQuantity(TestAge) = 12 years
        value = results.forExpression("TestAgeSpecificallyConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // Distance
        // define TestDistance: Distance { value: decimal { value: 100 }, unit: string { value: 'km'
        // }, system: uri {
        // value: 'http://unitsofmeasure.org' }, code: code { value: 'km' } }
        // define TestDistanceConverts: TestDistance = 100 'km'
        value = results.forExpression("TestDistanceConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestDistanceSpecificallyConverts: FHIRHelpers.ToQuantity(TestDistance) = 100 'km'
        value = results.forExpression("TestDistanceSpecificallyConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // Duration
        // define TestDuration: Duration { value: decimal { value: 100 }, unit: string { value: 's'
        // }, system: uri {
        // value: 'http://unitsofmeasure.org' }, code: code { value: 's' } }
        // define TestDurationConverts: TestDuration = 100 seconds
        value = results.forExpression("TestDurationConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestDurationSpecificallyConverts: FHIRHelpers.ToQuantity(TestDuration) = 100
        // seconds
        value = results.forExpression("TestDurationSpecificallyConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // Count
        // define TestCount: Count { value: decimal { value: 100 }, unit: string { value: '1' },
        // system: uri { value:
        // 'http://unitsofmeasure.org' }, code: code { value: '1' } }
        // define TestCountConverts: TestCount = 100 '1'
        value = results.forExpression("TestCountConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestCountSpecificallyConverts: FHIRHelpers.ToQuantity(TestCount) = 100 '1'
        value = results.forExpression("TestCountSpecificallyConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // MoneyQuantity
        // MoneyQuantity implicit conversions would result in a runtime error
        // SimpleQuantity
        // define TestSimpleQuantity: SimpleQuantity { value: decimal { value: 10 }, unit: string {
        // value: 'g' },
        // system: uri { value: 'http://unitsofmeasure.org' }, code: code { value: 'g' } }
        // define TestSimpleQuantityConverts: TestSimpleQuantity = 10 'g'
        value = results.forExpression("TestSimpleQuantityConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestSimpleQuantitySpecificallyConverts: FHIRHelpers.ToQuantity(TestSimpleQuantity)
        // = 10 'g'
        value = results.forExpression("TestSimpleQuantitySpecificallyConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // Quantity with Comparator
        // define TestQuantityWithoutComparator: Quantity { value: decimal { value: 10.0 }, unit:
        // string { value: 'mg' }
        // }
        // define TestQuantityWithoutComparatorConverts:
        // FHIRHelpers.ToInterval(TestQuantityWithoutComparator) =
        // Interval[10.0 'mg', 10.0 'mg']
        value = results.forExpression("TestQuantityWithoutComparatorConverts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestQuantityWithComparator1: Quantity { value: decimal { value: 10.0 }, unit:
        // string { value: 'mg' },
        // comparator: FHIR.QuantityComparator { value: '<' } }
        // define TestQuantityWithComparator1Converts:
        // FHIRHelpers.ToInterval(TestQuantityWithComparator1) =
        // Interval[null, 10 'mg')
        value = results.forExpression("TestQuantityWithComparator1Converts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestQuantityWithComparator2: Quantity { value: decimal { value: 10.0 }, unit:
        // string { value: 'mg' },
        // comparator: FHIR.QuantityComparator { value: '<=' } }
        // define TestQuantityWithComparator2Converts:
        // FHIRHelpers.ToInterval(TestQuantityWithComparator2) =
        // Interval[null, 10 'mg']
        value = results.forExpression("TestQuantityWithComparator2Converts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestQuantityWithComparator3: Quantity { value: decimal { value: 10.0 }, unit:
        // string { value: 'mg' },
        // comparator: FHIR.QuantityComparator { value: '>=' } }
        // define TestQuantityWithComparator3Converts:
        // FHIRHelpers.ToInterval(TestQuantityWithComparator3) = Interval[10
        // 'mg', null]
        value = results.forExpression("TestQuantityWithComparator3Converts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestQuantityWithComparator4: Quantity { value: decimal { value: 10.0 }, unit:
        // string { value: 'mg' },
        // comparator: FHIR.QuantityComparator { value: '>' } }
        // define TestQuantityWithComparator4Converts:
        // FHIRHelpers.ToInterval(TestQuantityWithComparator4) = Interval(10
        // 'mg', null]
        value = results.forExpression("TestQuantityWithComparator4Converts").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))

        // define TestQuantityWithToValue: FHIRHelpers.ToValue(FHIR.Quantity { value: FHIR.decimal {
        // value: 1001 },
        // unit: FHIR.string { value: 'mg/dL' } }) >= 200 'mg/dL'
        value = results.forExpression("TestQuantityWithToValue").value()
        MatcherAssert.assertThat(value, CoreMatchers.instanceOf(Boolean::class.java))
        MatcherAssert.assertThat(value, CoreMatchers.`is`(true))
    }
}
