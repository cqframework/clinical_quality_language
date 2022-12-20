library ClinicalOperator version '1'

codesystem "LOINC": 'http://loinc.org'

valueset "Feeding Intention-Breast": '2.16.840.1.113762.1.4.1045.29'

//CalculateAge
define CalculateAgeYears: CalculateAgeInYears(DateTime(2010, 1, 1))
define CalculateAgeMonths: CalculateAgeInMonths(DateTime(2010, 1, 1))
define CalculateAgeDays: CalculateAgeInDays(DateTime(2010, 1, 1))
define CalculateAgeHours: CalculateAgeInHours(DateTime(2010, 1, 1, 1))
define CalculateAgeMinutes: CalculateAgeInMinutes(DateTime(2010, 1, 1, 0, 0))
define CalculateAgeSeconds: CalculateAgeInSeconds(DateTime(2010, 1, 1, 0, 0, 0))
define CalculateAgeUncertain: CalculateAgeInMonths(DateTime(2010))

//CalculateAgeAt
define CalculateAgeAtYears: CalculateAgeInYearsAt(DateTime(2000, 1, 1), DateTime(2017, 1, 1))
define CalculateAgeAtMonths: CalculateAgeInMonthsAt(DateTime(2000, 1, 1), DateTime(2016, 6, 14))
define CalculateAgeAtDays: CalculateAgeInDaysAt(DateTime(2000, 1, 1), DateTime(2016, 7, 13))
define CalculateAgeAtHours: CalculateAgeInHoursAt(DateTime(2000, 1, 1, 0), DateTime(2000, 1, 2, 3)) // 27
define CalculateAgeAtMinutes: CalculateAgeInMinutesAt(DateTime(2000, 1, 1, 0, 0), DateTime(2000, 1, 2, 3, 10)) // 27 * 60 + 10
define CalculateAgeAtSeconds: CalculateAgeInSecondsAt(DateTime(2000, 1, 1, 0, 0, 0), DateTime(2000, 1, 2, 3, 10, 15)) // (27 * 60 + 10) * 60 + 15
define CalculateAgeAtUncertain: CalculateAgeInMonthsAt(DateTime(2000), DateTime(2016, 7, 14))

//Equal
define Code1:
  Code {
    system: 'http://loinc.org',
    code: '01234-5',
    version: '1.0',
    display: 'Test code 1'
  }
define Code2:
  Code {
    system: 'http://loinc.org',
    code: '12345-6',
    version: '1.0',
    display: 'Test code 2'
  }
define Code3:
  Code {
    system: 'http://loinc.org',
    code: '23456-7',
    version: '1.0',
    display: 'Test code 3'
  }
define Code4:
  Code {
    system: 'http://loinc.org',
    code: '34567-8',
    version: '1.0',
    display: 'Test code 4'
  }
define Code5:
  Code {
    system: 'http://loinc.org',
    code: '34567-8',
    display: 'Test code 4'
  }
define Concept1:
  Concept {
    codes: { Code1, Code2 },
    display: 'Test Concept 1'
  }
define Concept2:
  Concept {
    codes: { Code3, Code4 },
    display: 'Test Concept 2'
  }
define Concept3:
  Concept {
    codes: { Code3, Code4 }
  }
define Concept4:
  Concept {
    codes: { Code1, Code2, Code3, Code4 }
  }

define C1:
  Code {
    code: 'ABC',
    display: 'Code ABC',
    system: 'http://example.com',
    version: '2017-01'
  }

define C2:
  Code {
    code: 'ABC',
    display: 'Variant Description',
    system: 'http://example.com',
    version: '2017-05'
  }


define DisplayTestCode1:
  Code {
    system: 'http://loinc.org',
    code: '34567-8',
    display: 'Display'
  }

define DisplayTestCode2:
  Code {
    system: 'http://loinc.org',
    code: '34567-8',
    display: 'Not-Display'
  }

define DisplayTestCode3:
  Code {
    system: 'http://loinc.org',
    code: '34567-8'
  }

define DisplayTestConceptWithDisplay:
    Concept {
      codes: { DisplayTestCode1, DisplayTestCode2 }
    }

define DisplayTestConceptWithoutDisplay:
      Concept {
        codes: { DisplayTestCode3 }
      }

define Issue70A: C1 = C2
define Issue70B: C1 ~ C2
define CodeEqualTrue: Code1 = Code1
define CodeEqualFalse: Code1 = Code2
define CodeEqualNullVersion: Code1 = Code5
define ConceptEqualTrue: Concept1 = Concept1
define ConceptEqualFalse: Concept1 = Concept2
define ConceptEqualNullDisplay: Concept1 = Concept3
define CodeEqualNull: Code1 = null
define ConceptEqualNull: Concept1 = null

//Equivalent
define CodeEquivalentTrue: Code1 ~ Code1
define CodeEquivalentFalse: Code1 ~ Code2
define ConceptEquivalentTrue: Concept1 ~ Concept1
define ConceptEquivalentFalse: Concept1 ~ Concept2
define ConceptEquivalentTrueDisplayMismatch: Concept2 ~ Concept3
define ConceptEquivalentTrueIntersection1And4: Concept1 ~ Concept4
define ConceptEquivalentTrueIntersection2And4: Concept2 ~ Concept4
define CodeEquivalentNull: Code1 ~ null
define ConceptEquivalentNull: Concept1 ~ null
define CodeToConceptEquivalentTrue: Concept1 ~ Code1
define CodeToConceptEquivalentFalse: Concept1 ~ Code3

define ConceptToConceptMismatchedDisplayTrue: DisplayTestConceptWithDisplay ~ DisplayTestConceptWithoutDisplay
