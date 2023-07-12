package org.opencds.cqf.cql.engine.fhir.data;

import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.EvaluationResult;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestFHIRHelpers extends FhirExecutionTestBase {

    @Test
    public void test() {

        CqlEngine engineVisitor = getEngine();
        engineVisitor.getState().getEnvironment().registerDataProvider("http://hl7.org/fhir", r4Provider);
        EvaluationResult evaluationResult = engineVisitor.evaluate(library.getIdentifier(),
                null, null, null, null, null);

        Object result;

        // Primitives
        // instant
        //define TestInstant: instant { value: @2020-10-03T10:00:00.0 }
        //define TestInstantConverts: TestInstant = @2020-10-03T10:00:00.0
        result = evaluationResult.expressionResults.get("TestInstantConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // time
        //define TestTime: time { value: @T10:00:00.0 }
        //define TestTimeConverts: TestTime = @T10:00:00.0
        result = evaluationResult.expressionResults.get("TestTimeConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));
        //define TestHour: time { value: @T10 }
        //define TestHourConverts: TestHour = @T10
        result = evaluationResult.expressionResults.get("TestHourConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));
        //define TestMinute: time { value: @T10:00 }
        //define TestMinuteConverts: TestMinute = @T10:00
        result = evaluationResult.expressionResults.get("TestMinuteConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));
        //define TestSecond: time { value: @T10:00:00 }
        //define TestSecondConverts: TestSecond = @T10:00:00
        result = evaluationResult.expressionResults.get("TestSecondConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // date
        //define TestDate: date { value: @2020-10-03 }
        //define TestDateConverts: TestDate = @2020-10-03
        result = evaluationResult.expressionResults.get("TestDateConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));
        //define TestYear: date { value: @2020 }
        //define TestYearConverts: TestYear = @2020
        result = evaluationResult.expressionResults.get("TestYearConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));
        //define TestMonth: date { value: @2020-10 }
        //define TestMonthConverts: TestMonth = @2020-10
        result = evaluationResult.expressionResults.get("TestMonthConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // dateTime
        //define TestDateTime: dateTime { value: @2020-10-03T10:00:00.0 }
        //define TestDateTimeConverts: TestDateTime = @2020-10-03T10:00:00.0
        result = evaluationResult.expressionResults.get("TestDateTimeConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));
        //define TestYearDateTime: dateTime { value: @2020T }
        //define TestYearDateTimeConverts: TestYearDateTime = @2020T
        result = evaluationResult.expressionResults.get("TestYearDateTimeConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));
        //define TestMonthDateTime: dateTime { value: @2020-10T }
        //define TestMonthDateTimeConverts: TestMonthDateTime = @2020-10T
        result = evaluationResult.expressionResults.get("TestMonthDateTimeConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));
        //define TestDayDateTime: dateTime { value: @2020-10-03T }
        //define TestDayDateTimeConverts: TestDayDateTime = @2020-10-03T
        result = evaluationResult.expressionResults.get("TestDayDateTimeConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));
        //define TestHourDateTime: dateTime { value: @2020-10-03T10 }
        //define TestHourDateTimeConverts: TestHourDateTime = @2020-10-03T10
        // DateTime in FHIR does not support expressing times with only an hour component, so this precision is lost in the round-trip
        //result = evaluationResult.expressionResults.get("TestHourDateTimeConverts").value();
        //assertThat(result, instanceOf(Boolean.class));
        //assertThat(result, is(true));
        //define TestMinuteDateTime: dateTime { value: @2020-10-03T10:00 }
        //define TestMinuteDateTimeConverts: TestMinuteDateTime = @2020-10-03T10:00
        result = evaluationResult.expressionResults.get("TestMinuteDateTimeConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));
        //define TestSecondDateTime: dateTime { value: @2020-10-03T10:00:00 }
        //define TestSecondDateTimeConverts: TestSecondDateTime = @2020-10-03T10:00:00
        result = evaluationResult.expressionResults.get("TestSecondDateTimeConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // base64Binary
        //define TestBase64Binary: base64Binary { value: 'Rm9vYmFy' }
        //define TestBase64BinaryConverts: TestBase64Binary = 'Rm9vYmFy'
        result = evaluationResult.expressionResults.get("TestBase64BinaryConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // decimal
        //define TestDecimal: decimal { value: 10.0 }
        //define TestDecimalConverts: TestDecimal = 10.0
        result = evaluationResult.expressionResults.get("TestDecimalConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // boolean
        //define TestBoolean: boolean { value: true }
        //define TestBooleanConverts: TestBoolean = true
        result = evaluationResult.expressionResults.get("TestBooleanConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // uri
        //define TestUri: uri { value: 'http://hl7.org/fhir' }
        //define TestUriConverts: TestUri = 'http://hl7.org/fhir'
        result = evaluationResult.expressionResults.get("TestUriConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // url
        //define TestUrl: url { value: 'http://hl7.org/fhir' }
        //define TestUrlConverts: TestUrl = 'http://hl7.org/fhir'
        result = evaluationResult.expressionResults.get("TestUrlConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestUrlSpecificallyConverts: FHIRHelpers.ToString(TestUrl) = 'http://hl7.org/fhir'
        result = evaluationResult.expressionResults.get("TestUrlSpecificallyConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // canonical
        //define TestCanonical: canonical { value: 'http://hl7.org/fhir/CodeSystem/calendar-units' }
        //define TestCanonicalConverts: TestCanonical = 'http://hl7.org/fhir/CodeSystem/calendar-units'
        result = evaluationResult.expressionResults.get("TestCanonicalConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestCanonicalSpecificallyConverts: FHIRHelpers.ToString(TestCanonical) = 'http://hl7.org/fhir/CodeSystem/calendar-units'
        result = evaluationResult.expressionResults.get("TestCanonicalSpecificallyConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // uuid
        //define TestUuid: uuid { value: 'urn:uuid:c757873d-ec9a-4326-a141-556f43239520'}
        //define TestUuidConverts: TestUuid = 'urn:uuid:c757873d-ec9a-4326-a141-556f43239520'
        result = evaluationResult.expressionResults.get("TestUuidConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestUuidSpecificallyConverts: FHIRHelpers.ToString(TestUuid) = 'urn:uuid:c757873d-ec9a-4326-a141-556f43239520'
        result = evaluationResult.expressionResults.get("TestUuidSpecificallyConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // oid
        //define TestOid: oid { value: 'urn:oid:1.2.3.4.5' }
        //define TestOidConverts: TestOid = 'urn:oid:1.2.3.4.5'
        result = evaluationResult.expressionResults.get("TestOidConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestOidSpecificallyConverts: FHIRHelpers.ToString(TestOid) = 'urn:oid:1.2.3.4.5'
        result = evaluationResult.expressionResults.get("TestOidSpecificallyConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // integer
        //define TestInteger: integer { value: 1 }
        //define TestIntegerConverts: TestInteger = 1
        result = evaluationResult.expressionResults.get("TestIntegerConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestIntegerSpecificallyConverts: FHIRHelpers.ToInteger(TestInteger) = 1
        result = evaluationResult.expressionResults.get("TestIntegerSpecificallyConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // unsignedInt
        //define TestUnsignedInt: unsignedInt { value: 1 }
        //define TestUnsignedIntConverts: TestUnsignedInt = 1
        result = evaluationResult.expressionResults.get("TestUnsignedIntConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestUnsignedIntSpecificallyConverts: FHIRHelpers.ToInteger(TestUnsignedInt) = 1
        result = evaluationResult.expressionResults.get("TestUnsignedIntSpecificallyConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // positiveInt
        //define TestPositiveInt: positiveInt { value: 1 }
        //define TestPositiveIntConverts: TestPositiveInt = 1
        result = evaluationResult.expressionResults.get("TestPositiveIntConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestPositiveIntSpecificallyConverts: FHIRHelpers.ToInteger(TestPositiveInt) = 1
        result = evaluationResult.expressionResults.get("TestPositiveIntSpecificallyConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // string
        //define TestString: string { value: 'Foobar' }
        //define TestStringConverts: TestString = 'Foobar'
        result = evaluationResult.expressionResults.get("TestStringConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // code
        //define TestCode: code { value: 'year' }
        //define TestCodeConverts: TestCode = 'year'
        result = evaluationResult.expressionResults.get("TestCodeConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestCodeSpecificallyConverts: FHIRHelpers.ToString(TestCode) = 'year'
        result = evaluationResult.expressionResults.get("TestCodeSpecificallyConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // markdown
        //define TestMarkdown: markdown { value: '#Markdown Content' }
        //define TestMarkdownConverts: TestMarkdown = '#Markdown Content'
        result = evaluationResult.expressionResults.get("TestMarkdownConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestMarkdownSpecificallyConverts: FHIRHelpers.ToString(TestMarkdown) = '#Markdown Content'
        result = evaluationResult.expressionResults.get("TestMarkdownSpecificallyConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // id
        //define TestId: id { value: 'calendar-units' }
        //define TestIdConverts: TestId = 'calendar-units'
        result = evaluationResult.expressionResults.get("TestIdConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestIdSpecificallyConverts: FHIRHelpers.ToString(TestId) = 'calendar-units'
        result = evaluationResult.expressionResults.get("TestIdSpecificallyConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // Ratio
        //define TestRatio: Ratio {
        //    numerator: Quantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' } },
        //    denominator: Quantity { value: decimal { value: 100.0 }, unit: string { value: 'mg' } }
        //}
        //define TestRatioConverts: TestRatio = 10.0 'mg' : 100.0 'mg'
        result = evaluationResult.expressionResults.get("TestRatioConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // Period
        //define TestPeriod: Period {
        //    start: dateTime { value: @2020-10-03T10:00:00 },
        //    end: dateTime { value: @2020-10-03T10:00:00 }
        //}
        //define TestPeriodConverts: TestPeriod = Interval[@2020-10-03T10:00:00, @2020-10-03T10:00:00]
        result = evaluationResult.expressionResults.get("TestPeriodConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // Range
        //define TestRange: Range {
        //    low: SimpleQuantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' } },
        //    high: SimpleQuantity { value: decimal { value: 100.0 }, unit: string { value: 'mg' } }
        //}
        //define TestRangeConverts: TestRange = Interval[10.0 'mg', 100.0 'mg']
        result = evaluationResult.expressionResults.get("TestRangeConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // Coding
        //define TestCoding: Coding {
        //    system: uri { value: 'http://hl7.org/fhir/CodeSystem/calendar-units' },
        //    code: code { value: 'year' },
        //    display: string { value: 'year' }
        //}
        //define TestCodingConverts: TestCoding = Code { code: 'year', system: 'http://hl7.org/fhir/CodeSystem/calendar-units', display: 'year' }
        result = evaluationResult.expressionResults.get("TestCodingConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // CodeableConcept
        //define TestCodeableConcept: CodeableConcept {
        //    coding: {
        //        Coding {
        //            system: uri { value: 'http://hl7.org/fhir/CodeSystem/calendar-units' },
        //            code: code { value: 'year' },
        //            display: string { value: 'year' }
        //        }
        //    }
        //}
        //define TestCodeableConceptConverts: TestCodeableConcept = Concept { codes: { Code { code: 'year', system: 'http://hl7.org/fhir/CodeSystem/calendar-units', display: 'year' } } }
        result = evaluationResult.expressionResults.get("TestCodeableConceptConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // Money
        // Money implicit conversions are not supported

        // Quantity
        //define TestQuantity: Quantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' } }
        //define TestQuantityConverts: TestQuantity = 10.0 'mg'
        result = evaluationResult.expressionResults.get("TestQuantityConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // Age
        //define TestAge: Age { value: decimal { value: 12.0 }, unit: string { value: 'a' }, system: uri { value: 'http://unitsofmeasure.org' }, code: code { value: 'a' } }
        //define TestAgeConverts: TestAge = 12 years
        result = evaluationResult.expressionResults.get("TestAgeConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestAgeSpecificallyConverts: FHIRHelpers.ToQuantity(TestAge) = 12 years
        result = evaluationResult.expressionResults.get("TestAgeSpecificallyConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // Distance
        //define TestDistance: Distance { value: decimal { value: 100 }, unit: string { value: 'km' }, system: uri { value: 'http://unitsofmeasure.org' }, code: code { value: 'km' } }
        //define TestDistanceConverts: TestDistance = 100 'km'
        result = evaluationResult.expressionResults.get("TestDistanceConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestDistanceSpecificallyConverts: FHIRHelpers.ToQuantity(TestDistance) = 100 'km'
        result = evaluationResult.expressionResults.get("TestDistanceSpecificallyConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // Duration
        //define TestDuration: Duration { value: decimal { value: 100 }, unit: string { value: 's' }, system: uri { value: 'http://unitsofmeasure.org' }, code: code { value: 's' } }
        //define TestDurationConverts: TestDuration = 100 seconds
        result = evaluationResult.expressionResults.get("TestDurationConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestDurationSpecificallyConverts: FHIRHelpers.ToQuantity(TestDuration) = 100 seconds
        result = evaluationResult.expressionResults.get("TestDurationSpecificallyConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // Count
        //define TestCount: Count { value: decimal { value: 100 }, unit: string { value: '1' }, system: uri { value: 'http://unitsofmeasure.org' }, code: code { value: '1' } }
        //define TestCountConverts: TestCount = 100 '1'
        result = evaluationResult.expressionResults.get("TestCountConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestCountSpecificallyConverts: FHIRHelpers.ToQuantity(TestCount) = 100 '1'
        result = evaluationResult.expressionResults.get("TestCountSpecificallyConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // MoneyQuantity
        // MoneyQuantity implicit conversions would result in a runtime error
        // SimpleQuantity
        //define TestSimpleQuantity: SimpleQuantity { value: decimal { value: 10 }, unit: string { value: 'g' }, system: uri { value: 'http://unitsofmeasure.org' }, code: code { value: 'g' } }
        //define TestSimpleQuantityConverts: TestSimpleQuantity = 10 'g'
        result = evaluationResult.expressionResults.get("TestSimpleQuantityConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestSimpleQuantitySpecificallyConverts: FHIRHelpers.ToQuantity(TestSimpleQuantity) = 10 'g'
        result = evaluationResult.expressionResults.get("TestSimpleQuantitySpecificallyConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        // Quantity with Comparator
        //define TestQuantityWithoutComparator: Quantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' } }
        //define TestQuantityWithoutComparatorConverts: FHIRHelpers.ToInterval(TestQuantityWithoutComparator) = Interval[10.0 'mg', 10.0 'mg']
        result = evaluationResult.expressionResults.get("TestQuantityWithoutComparatorConverts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestQuantityWithComparator1: Quantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' }, comparator: FHIR.QuantityComparator { value: '<' } }
        //define TestQuantityWithComparator1Converts: FHIRHelpers.ToInterval(TestQuantityWithComparator1) = Interval[null, 10 'mg')
        result = evaluationResult.expressionResults.get("TestQuantityWithComparator1Converts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestQuantityWithComparator2: Quantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' }, comparator: FHIR.QuantityComparator { value: '<=' } }
        //define TestQuantityWithComparator2Converts: FHIRHelpers.ToInterval(TestQuantityWithComparator2) = Interval[null, 10 'mg']
        result = evaluationResult.expressionResults.get("TestQuantityWithComparator2Converts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestQuantityWithComparator3: Quantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' }, comparator: FHIR.QuantityComparator { value: '>=' } }
        //define TestQuantityWithComparator3Converts: FHIRHelpers.ToInterval(TestQuantityWithComparator3) = Interval[10 'mg', null]
        result = evaluationResult.expressionResults.get("TestQuantityWithComparator3Converts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));

        //define TestQuantityWithComparator4: Quantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' }, comparator: FHIR.QuantityComparator { value: '>' } }
        //define TestQuantityWithComparator4Converts: FHIRHelpers.ToInterval(TestQuantityWithComparator4) = Interval(10 'mg', null]
        result = evaluationResult.expressionResults.get("TestQuantityWithComparator4Converts").value();
        assertThat(result, instanceOf(Boolean.class));
        assertThat(result, is(true));
    }
}
