library CMS153_CQM version '2'

using QUICK

parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))

valueset "Female Administrative Sex": '2.16.840.1.113883.3.560.100.2'
valueset "Other Female Reproductive Conditions": '2.16.840.1.113883.3.464.1003.111.12.1006'
valueset "Genital Herpes": '2.16.840.1.113883.3.464.1003.110.12.1049'
valueset "Genococcal Infections and Venereal Diseases": '2.16.840.1.113883.3.464.1003.112.12.1001'
valueset "Inflammatory Diseases of Female Reproductive Organs": '2.16.840.1.113883.3.464.1003.112.12.1004'
valueset "Chlamydia": '2.16.840.1.113883.3.464.1003.112.12.1003'
valueset "HIV": '2.16.840.1.113883.3.464.1003.120.12.1003'
valueset "Syphilis": '2.16.840.1.113883.3.464.1003.112.12.1002'
valueset "Complications of Pregnancy, Childbirth and the Puerperium": '2.16.840.1.113883.3.464.1003.111.12.1012'
valueset "Pregnancy Test": '2.16.840.1.113883.3.464.1003.111.12.1011'
valueset "Pap Test": '2.16.840.1.113883.3.464.1003.108.12.1017'
valueset "Lab Tests During Pregnancy": '2.16.840.1.113883.3.464.1003.111.12.1007'
valueset "Lab Tests for Sexually Transmitted Infections": '2.16.840.1.113883.3.464.1003.110.12.1051'
valueset "Chlamydia Screening": '2.16.840.1.113883.3.464.1003.110.12.1052'

context Patient

define InDemographicCriteria:
	AgeInYearsAt(start of MeasurementPeriod) >= 16 and AgeInYearsAt(start of MeasurementPeriod) < 24
		and Patient.gender in "Female Administrative Sex"

define SexuallyActive:
	exists ([Condition: "Other Female Reproductive Conditions"] C where interval[C.onsetDateTime, C.abatementDateTime] overlaps MeasurementPeriod)
		or exists ([Condition: "Genital Herpes"] C where interval[C.onsetDateTime, C.abatementDateTime] overlaps MeasurementPeriod)
		or exists ([Condition: "Genococcal Infections and Venereal Diseases"] C where interval[C.onsetDateTime, C.abatementDateTime] overlaps MeasurementPeriod)
		or exists ([Condition: "Inflammatory Diseases of Female Reproductive Organs"] C where interval[C.onsetDateTime, C.abatementDateTime] overlaps MeasurementPeriod)
		or exists ([Condition: "Chlamydia"] C where interval[C.onsetDateTime, C.abatementDateTime] overlaps MeasurementPeriod)
		or exists ([Condition: "HIV"] C where interval[C.onsetDateTime, C.abatementDateTime] overlaps MeasurementPeriod)
		or exists ([Condition: "Syphilis"] C where interval[C.onsetDateTime, C.abatementDateTime] overlaps MeasurementPeriod)
		or exists ([Condition: "Complications of Pregnancy, Childbirth and the Puerperium"] C where interval[C.onsetDateTime, C.abatementDateTime] overlaps MeasurementPeriod)
		or exists ([DiagnosticOrder: "Pregnancy Test"] O where Last(O.event E where E.status = 'completed' sort by E.date).date during MeasurementPeriod)
		or exists ([DiagnosticOrder: "Pap Test"] O where where Last(O.event E where E.status = 'completed' sort by E.date).date during MeasurementPeriod)
		or exists ([DiagnosticOrder: "Lab Tests During Pregnancy"] O where Last(O.event E where E.status = 'completed').date during MeasurementPeriod)
		or exists ([DiagnosticOrder: "Lab Tests for Sexually Transmitted Infections"] O where Last(O.event E where E.status = 'completed').date during MeasurementPeriod)

define InInitialPopulation:
	InDemographic and SexuallyActive

define InDenominator:
	true

define InDenominator:
	InInitialPopulation

define InNumerator:
	exists ([DiagnosticReport: "Chlamydia Screening"] R where R.issued during MeasurementPeriod and R.result is not null)

define InNumerator:
	InDenominator and exists ([DiagnosticReport: "Chlamydia Screening"] R where R.diagnosticDateTime during MeasurementPeriod and R.result is not null)

context POPULATION

define MeasureScore: (Count(Patient P where InInitialPopulation and InDenominator and InNumerator) / Count(Patient P where InInitialPopulation and InDenominator)) * 100

define MeasureScore: (Count(Patient P where InNumerator) / Count(Patient P where InDenominator)) * 100
