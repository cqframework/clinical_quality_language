library ChlamydiaScreening_CDS version '2'

using QUICK

codesystem "SNOMED": 'http://snomed.info/sct'

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
valueset "Reason for not performing Chlamydia Screening": 'TBD' // Not used in the measure, need a value set for this

context Patient

define "InDemographic":
	AgeInYears() >= 16 and AgeInYears() < 24 and "Patient"."gender" in "Female Administrative Sex"

define "SexuallyActive":
	exists (["Condition": "Other Female Reproductive Conditions"])
	or exists (["Condition": "Genital Herpes"])
	or exists (["Condition": "Genococcal Infections and Venereal Diseases"])
	or exists (["Condition": "Inflammatory Diseases of Female Reproductive Organs"])
	or exists (["Condition": "Chlamydia"])
	or exists (["Condition": "HIV"])
	or exists (["Condition": "Syphilis"])
	or exists (["Condition": "Complications of Pregnancy, Childbirth and the Puerperium"])
	or exists (["DiagnosticOrder": "Pregnancy Test"])
	or exists (["DiagnosticOrder": "Pap Test"])
	or exists (["DiagnosticOrder": "Lab Tests During Pregnancy"])
	or exists (["DiagnosticOrder": "Lab Tests for Sexually Transmitted Infections"])

define "NoScreening":
  not exists (["DiagnosticReport": "Chlamydia Screening"] R where R."issued" during Interval[Today() - 1 years, Today()] and R."result" is not null)
  and not exists (["ProcedureRequest": "Chlamydia Screening"] P where P."orderedOn" same day or after Today())
	and not exists (["Observation": "Reason for not performing Chlamydia Screening"])

define "NeedScreening": "InDemographic" and "SexuallyActive" and "NoScreening"

//The following previously read "ProcedureRequest" where it currently reads "Tuple"

define "ChlamydiaScreeningRequest": Tuple {
  type: Code '442487003' from "SNOMED" display 'Screening for Chlamydia trachomatis (procedure)',
  status: 'proposed'
  // values for other elements of the request...
}

