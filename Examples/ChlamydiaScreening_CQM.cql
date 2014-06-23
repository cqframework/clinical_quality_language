library CMS153_CQM version '2'

using QUICK

context PATIENT // TODO: Look at turning this into a parameter

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

valueset "Female Administrative Sex" = ValueSet('2.16.840.1.113883.3.560.100.2')
valueset "Other Female Reproductive Conditions" = ValueSet('2.16.840.1.113883.3.464.1003.111.12.1006')
valueset "Genital Herpes" = ValueSet('2.16.840.1.113883.3.464.1003.110.12.1049')
valueset "Genococcal Infections and Venereal Diseases" = ValueSet('2.16.840.1.113883.3.464.1003.112.12.1001')
valueset "Inflammatory Diseases of Female Reproductive Organs" = ValueSet('2.16.840.1.113883.3.464.1003.112.12.1004')
valueset "Chlamydia" = ValueSet('2.16.840.1.113883.3.464.1003.112.12.1003')
valueset "HIV" = ValueSet('2.16.840.1.113883.3.464.1003.120.12.1003')
valueset "Syphilis" = ValueSet('2.16.840.1.113883.3.464.1003.112.12.1002')
valueset "Complications of Pregnancy, Childbirth and the Puerperium" = ValueSet('2.16.840.1.113883.3.464.1003.111.12.1012')
valueset "Pregnancy Test" = ValueSet('2.16.840.1.113883.3.464.1003.111.12.1011')
valueset "Pap Test" = ValueSet('2.16.840.1.113883.3.464.1003.108.12.1017')
valueset "Lab Tests During Pregnancy" = ValueSet('2.16.840.1.113883.3.464.1003.111.12.1007')
valueset "Lab Tests for Sexually Transmitted Infections" = ValueSet('2.16.840.1.113883.3.464.1003.110.12.1051')
valueset "Chlamydia Screening" = ValueSet('2.16.840.1.113883.3.464.1003.110.12.1052')

let InDemographic = 
	AgeAt(start of MeasurementPeriod) >= 16
		and AgeAt(start of MeasurementPeriod) < 24
		and Gender in "Female Administrative Sex"

let SexuallyActive =
	exists ([Condition: "Other Female Reproductive Conditions"] C where C.effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Genital Herpes"] C where C.effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Genococcal Infections and Venereal Diseases"] C where C.effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Inflammatory Diseases of Female Reproductive Organs"] C where C.effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Chlamydia"] C where C.effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "HIV"] C where C.effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Syphilis"] C where C.effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Complications of Pregnancy, Childbirth and the Puerperium"] C where C.effectiveTime overlaps before MeasurementPeriod)
		or exists ([ObservationResult: "Pregnancy"] R where R.effectiveTime during MeasurementPeriod)
		or exists ([ObservationResult: "Pap"] R where R.effectiveTime during MeasurementPeriod)
		or exists ([ObservationResult: "Lab Tests During Pregnancy"] R where R.effectiveTime during MeasurementPeriod)
		or exists ([ObservationResult: "Lab Tests for Sexually Transmitted Infections"] R where R.observedAtTime during MeasurementPeriod)

let InInitialPopulation =
	InDemographic and SexuallyActive

let InDenominator =
	InInitialPopulation

let InNumerator =
	InDenominator
		and exists ([ObservationResult: "Chlamydia Screening"] R where R.effectiveTime during MeasurementPeriod)


