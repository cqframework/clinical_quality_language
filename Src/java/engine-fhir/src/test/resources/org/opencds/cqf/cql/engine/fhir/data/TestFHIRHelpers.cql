library TestFHIRHelpers

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

// Primitives
// instant
define TestInstant: instant { value: @2020-10-03T10:00:00.0 }
define TestInstantConverts: TestInstant = @2020-10-03T10:00:00.0

// time
define TestTime: time { value: @T10:00:00.0 }
define TestTimeConverts: TestTime = @T10:00:00.0
define TestHour: time { value: @T10 }
define TestHourConverts: TestHour = @T10
define TestMinute: time { value: @T10:00 }
define TestMinuteConverts: TestMinute = @T10:00
define TestSecond: time { value: @T10:00:00 }
define TestSecondConverts: TestSecond = @T10:00:00

// date
define TestDate: date { value: @2020-10-03 }
define TestDateConverts: TestDate = @2020-10-03
define TestYear: date { value: @2020 }
define TestYearConverts: TestYear = @2020
define TestMonth: date { value: @2020-10 }
define TestMonthConverts: TestMonth = @2020-10

// dateTime
define TestDateTime: dateTime { value: @2020-10-03T10:00:00.0 }
define TestDateTimeConverts: TestDateTime = @2020-10-03T10:00:00.0
define TestYearDateTime: dateTime { value: @2020T }
define TestYearDateTimeConverts: TestYearDateTime = @2020T
define TestMonthDateTime: dateTime { value: @2020-10T }
define TestMonthDateTimeConverts: TestMonthDateTime = @2020-10T
define TestDayDateTime: dateTime { value: @2020-10-03T }
define TestDayDateTimeConverts: TestDayDateTime = @2020-10-03T
define TestHourDateTime: dateTime { value: @2020-10-03T10 }
define TestHourDateTimeConverts: TestHourDateTime = @2020-10-03T10
define TestMinuteDateTime: dateTime { value: @2020-10-03T10:00 }
define TestMinuteDateTimeConverts: TestMinuteDateTime = @2020-10-03T10:00
define TestSecondDateTime: dateTime { value: @2020-10-03T10:00:00 }
define TestSecondDateTimeConverts: TestSecondDateTime = @2020-10-03T10:00:00

// base64Binary
define TestBase64Binary: base64Binary { value: 'Rm9vYmFy' }
define TestBase64BinaryConverts: TestBase64Binary = 'Rm9vYmFy'

// decimal
define TestDecimal: decimal { value: 10.0 }
define TestDecimalConverts: TestDecimal = 10.0

// boolean
define TestBoolean: boolean { value: true }
define TestBooleanConverts: TestBoolean = true

// uri
define TestUri: uri { value: 'http://hl7.org/fhir' }
define TestUriConverts: TestUri = 'http://hl7.org/fhir'
  // url
define TestUrl: url { value: 'http://hl7.org/fhir' }
define TestUrlConverts: TestUrl = 'http://hl7.org/fhir'
define TestUrlSpecificallyConverts: FHIRHelpers.ToString(TestUrl) = 'http://hl7.org/fhir'
  // canonical
define TestCanonical: canonical { value: 'http://hl7.org/fhir/CodeSystem/calendar-units' }
define TestCanonicalConverts: TestCanonical = 'http://hl7.org/fhir/CodeSystem/calendar-units'
define TestCanonicalSpecificallyConverts: FHIRHelpers.ToString(TestCanonical) = 'http://hl7.org/fhir/CodeSystem/calendar-units'
  // uuid
define TestUuid: uuid { value: 'urn:uuid:c757873d-ec9a-4326-a141-556f43239520'}
define TestUuidConverts: TestUuid = 'urn:uuid:c757873d-ec9a-4326-a141-556f43239520'
define TestUuidSpecificallyConverts: FHIRHelpers.ToString(TestUuid) = 'urn:uuid:c757873d-ec9a-4326-a141-556f43239520'
  // oid
define TestOid: oid { value: 'urn:oid:1.2.3.4.5' }
define TestOidConverts: TestOid = 'urn:oid:1.2.3.4.5'
define TestOidSpecificallyConverts: FHIRHelpers.ToString(TestOid) = 'urn:oid:1.2.3.4.5'

// integer
define TestInteger: integer { value: 1 }
define TestIntegerConverts: TestInteger = 1
define TestIntegerSpecificallyConverts: FHIRHelpers.ToInteger(TestInteger) = 1
  // unsignedInt
define TestUnsignedInt: unsignedInt { value: 1 }
define TestUnsignedIntConverts: TestUnsignedInt = 1
define TestUnsignedIntSpecificallyConverts: FHIRHelpers.ToInteger(TestUnsignedInt) = 1
  // positiveInt
define TestPositiveInt: positiveInt { value: 1 }
define TestPositiveIntConverts: TestPositiveInt = 1
define TestPositiveIntSpecificallyConverts: FHIRHelpers.ToInteger(TestPositiveInt) = 1

// string
define TestString: string { value: 'Foobar' }
define TestStringConverts: TestString = 'Foobar'
  // code
define TestCode: code { value: 'year' }
define TestCodeConverts: TestCode = 'year'
define TestCodeSpecificallyConverts: FHIRHelpers.ToString(TestCode) = 'year'
  // markdown
define TestMarkdown: markdown { value: '#Markdown Content' }
define TestMarkdownConverts: TestMarkdown = '#Markdown Content'
define TestMarkdownSpecificallyConverts: FHIRHelpers.ToString(TestMarkdown) = '#Markdown Content'
  // id
define TestId: id { value: 'calendar-units' }
define TestIdConverts: TestId = 'calendar-units'
define TestIdSpecificallyConverts: FHIRHelpers.ToString(TestId) = 'calendar-units'

// Ratio
define TestRatio: Ratio {
  numerator: Quantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' } },
  denominator: Quantity { value: decimal { value: 100.0 }, unit: string { value: 'mg' } }
}
define TestRatioConverts: TestRatio = 10.0 'mg' : 100.0 'mg'

// Period
define TestPeriod: Period {
  start: dateTime { value: @2020-10-03T10:00:00 },
  end: dateTime { value: @2020-10-03T10:00:00 }
}
define TestPeriodConverts: TestPeriod = Interval[@2020-10-03T10:00:00, @2020-10-03T10:00:00]

// Range
define TestRange: Range {
  low: SimpleQuantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' } },
  high: SimpleQuantity { value: decimal { value: 100.0 }, unit: string { value: 'mg' } }
}
define TestRangeConverts: TestRange = Interval[10.0 'mg', 100.0 'mg']

// Coding
define TestCoding: Coding {
  system: uri { value: 'http://hl7.org/fhir/CodeSystem/calendar-units' },
  code: code { value: 'year' },
  display: string { value: 'year' }
}
define TestCodingConverts: TestCoding = Code { code: 'year', system: 'http://hl7.org/fhir/CodeSystem/calendar-units', display: 'year' }

// CodeableConcept
define TestCodeableConcept: CodeableConcept {
  coding: {
    Coding {
      system: uri { value: 'http://hl7.org/fhir/CodeSystem/calendar-units' },
      code: code { value: 'year' },
      display: string { value: 'year' }
    }
  }
}
define TestCodeableConceptConverts: TestCodeableConcept = Concept { codes: { Code { code: 'year', system: 'http://hl7.org/fhir/CodeSystem/calendar-units', display: 'year' } } }

// Money
// Money implicit conversions are not supported

// Quantity
define TestQuantity: Quantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' } }
define TestQuantityConverts: TestQuantity = 10.0 'mg'
  // Age
define TestAge: Age { value: decimal { value: 12.0 }, unit: string { value: 'a' }, system: uri { value: 'http://unitsofmeasure.org' }, code: code { value: 'a' } }
define TestAgeConverts: TestAge = 12 years
define TestAgeSpecificallyConverts: FHIRHelpers.ToQuantity(TestAge) = 12 years
  // Distance
define TestDistance: Distance { value: decimal { value: 100 }, unit: string { value: 'km' }, system: uri { value: 'http://unitsofmeasure.org' }, code: code { value: 'km' } }
define TestDistanceConverts: TestDistance = 100 'km'
define TestDistanceSpecificallyConverts: FHIRHelpers.ToQuantity(TestDistance) = 100 'km'
  // Duration
define TestDuration: Duration { value: decimal { value: 100 }, unit: string { value: 's' }, system: uri { value: 'http://unitsofmeasure.org' }, code: code { value: 's' } }
define TestDurationConverts: TestDuration = 100 seconds
define TestDurationSpecificallyConverts: FHIRHelpers.ToQuantity(TestDuration) = 100 seconds
  // Count
define TestCount: Count { value: decimal { value: 100 }, unit: string { value: '1' }, system: uri { value: 'http://unitsofmeasure.org' }, code: code { value: '1' } }
define TestCountConverts: TestCount = 100 '1'
define TestCountSpecificallyConverts: FHIRHelpers.ToQuantity(TestCount) = 100 '1'
  // MoneyQuantity
  // MoneyQuantity implicit conversions would result in a runtime error
  // SimpleQuantity
define TestSimpleQuantity: SimpleQuantity { value: decimal { value: 10 }, unit: string { value: 'g' }, system: uri { value: 'http://unitsofmeasure.org' }, code: code { value: 'g' } }
define TestSimpleQuantityConverts: TestSimpleQuantity = 10 'g'
define TestSimpleQuantitySpecificallyConverts: FHIRHelpers.ToQuantity(TestSimpleQuantity) = 10 'g'

// Quantity with Comparator
define TestQuantityWithoutComparator: Quantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' } }
define TestQuantityWithoutComparatorConverts: FHIRHelpers.ToInterval(TestQuantityWithoutComparator) = Interval[10.0 'mg', 10.0 'mg']
define TestQuantityWithComparator1: Quantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' }, comparator: FHIR.QuantityComparator { value: '<' } }
define TestQuantityWithComparator1Converts: FHIRHelpers.ToInterval(TestQuantityWithComparator1) = Interval[null, 10 'mg')
define TestQuantityWithComparator2: Quantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' }, comparator: FHIR.QuantityComparator { value: '<=' } }
define TestQuantityWithComparator2Converts: FHIRHelpers.ToInterval(TestQuantityWithComparator2) = Interval[null, 10 'mg']
define TestQuantityWithComparator3: Quantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' }, comparator: FHIR.QuantityComparator { value: '>=' } }
define TestQuantityWithComparator3Converts: FHIRHelpers.ToInterval(TestQuantityWithComparator3) = Interval[10 'mg', null]
define TestQuantityWithComparator4: Quantity { value: decimal { value: 10.0 }, unit: string { value: 'mg' }, comparator: FHIR.QuantityComparator { value: '>' } }
define TestQuantityWithComparator4Converts: FHIRHelpers.ToInterval(TestQuantityWithComparator4) = Interval(10 'mg', null]
