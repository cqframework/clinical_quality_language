// @Test: ValueSetDef
valueset "Known": '2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Unknown One Arg": '1.2.3.4.5.6.7.8.9'
valueset "Unknown Two Arg": '1.2.3.4.5.6.7.8.9' version '1'
define Foo: "Bar"

// @Test: ValueSetRef
valueset "Acute Pharyngitis": '2.16.840.1.113883.3.464.1003.101.12.1001'
define Foo: "Acute Pharyngitis"

// @Test: InValueSet
valueset "Female": '2.16.840.1.113883.3.560.100.2'
valueset "Versioned Female": '2.16.840.1.113883.3.560.100.2' version '20121025'
define String: 'F' in "Female"
define StringInVersionedValueSet: 'F' in "Versioned Female"
define ShortCode: Code { code: 'F' } in "Female"
define MediumCode: Code { code: 'F', system: '2.16.840.1.113883.18.2' } in "Female"
define LongCode: Code { code: 'F', system: '2.16.840.1.113883.18.2', version: 'HL7V2.5' } in "Female"
define WrongString: 'M' in "Female"
define WrongStringInVersionedValueSet: 'M' in "Versioned Female"
define WrongShortCode: Code { code: 'M' } in "Female"
define WrongMediumCode: Code { code: 'F', system: '3.16.840.1.113883.18.2' } in "Female"
define WrongLongCode: Code { code: 'F', system: '2.16.840.1.113883.18.2', version: 'HL7V2.6' } in "Female"

// @Test: Patient Property In ValueSet
valueset "Female": '2.16.840.1.113883.3.560.100.2'
define IsFemale: Patient.gender in "Female"

// @Test: CalculateAge
define Years: AgeInYears()
define Months: AgeInMonths()
define Days: AgeInDays()
define Hours: AgeInHours()
define Minutes: AgeInMinutes()
define Seconds: AgeInSeconds()

// @Test: CalculateAgeAt
define AgeAt2012: AgeInYearsAt(DateTime(2012))
define AgeAt19810216: AgeInYearsAt(DateTime(1981, 2, 16))
define AgeAt1975: AgeInYearsAt(DateTime(1975))
