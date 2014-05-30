using QLIM

context PATIENT // TODO: Look at turning this into a parameter with a 'with' statement

parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

concept "Other Female Reproductive Conditions" = ValueSet("2.16.840.1.113883.3.464.1003.111.12.1006")
concept "Genital Herpes" = ValueSet("2.16.840.1.113883.3.464.1003.110.12.1049")
concept "Genococcal Infections and Venereal Diseases" = ValueSet("2.16.840.1.113883.3.464.1003.112.12.1001")
concept "Inflammatory Diseases of Female Reproductive Organs" = ValueSet("2.16.840.1.113883.3.464.1003.112.12.1004")
concept "Chlamydia" = ValueSet("2.16.840.1.113883.3.464.1003.112.12.1003")
concept "HIV" = ValueSet("2.16.840.1.113883.3.464.1003.120.12.1003")
concept "Syphilis" = ValueSet("2.16.840.1.113883.3.464.1003.112.12.1002")
concept "Complications of Pregnancy, Childbirth and the Puerperium" = ValueSet("2.16.840.1.113883.3.464.1003.111.12.1012")
concept "Pregnancy Test" = ValueSet("2.16.840.1.113883.3.464.1003.111.12.1011")
concept "Pap Test" = ValueSet("2.16.840.1.113883.3.464.1003.108.12.1017")
concept "Lab Tests During Pregnancy" = ValueSet("2.16.840.1.113883.3.464.1003.111.12.1007")
concept "Lab Tests for Sexually Transmitted Infections" = ValueSet("2.16.840.1.113883.3.464.1003.110.12.1051")
concept "Chlamydia Screening" = ValueSet("2.16.840.1.113883.3.464.1003.110.12.1052")

let InDemographic =
	AgeAt(start of MeasurementPeriod) >= 16
		and AgeAt(start of MeasurementPeriod) < 24
		and Gender = "female"

let SexuallyActive =
	exists ([Condition: "Other Female Reproductive Conditions"] where effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Genital Herpes"] where effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Genococcal Infections and Venereal Diseases"] where effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Inflammatory Diseases of Female Reproductive Organs"] where effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Chlamydia"] where effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "HIV"] where effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Syphilis"] where effectiveTime overlaps before MeasurementPeriod)
		or exists ([Condition: "Complications of Pregnancy, Childbirth and the Puerperium"] where effectiveTime overlaps before MeasurementPeriod)
		or exists ([ObservationResult: "Pregnancy"] where effectiveTime during MeasurementPeriod)
		or exists ([ObservationResult: "Pap"] where effectiveTime during MeasurementPeriod)
		or exists ([ObservationResult: "Lab Tests During Pregnancy"] where effectiveTime during MeasurementPeriod)
		or exists ([ObservationResult: "Lab Tests for Sexually Transmitted Infections"] where observedAtTime during MeasurementPeriod)

let InInitialPopulation =
	InDemographic and SexuallyActive

let InDenominator =
	InInitialPopulation

let InNumerator =
	InDenominator
		and exists ([ObservationResult: "Chlamydia Screening"] where effectiveTime during MeasurementPeriod)


