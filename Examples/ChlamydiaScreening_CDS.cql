library CMS153_CDS version '2'

using QUICK

define "Female Administrative Sex" = ValueSet('2.16.840.1.113883.3.560.100.2')
define "Other Female Reproductive Conditions" = ValueSet('2.16.840.1.113883.3.464.1003.111.12.1006')
define "Genital Herpes" = ValueSet('2.16.840.1.113883.3.464.1003.110.12.1049')
define "Genococcal Infections and Venereal Diseases" = ValueSet('2.16.840.1.113883.3.464.1003.112.12.1001')
define "Inflammatory Diseases of Female Reproductive Organs" = ValueSet('2.16.840.1.113883.3.464.1003.112.12.1004')
define "Chlamydia" = ValueSet('2.16.840.1.113883.3.464.1003.112.12.1003')
define "HIV" = ValueSet('2.16.840.1.113883.3.464.1003.120.12.1003')
define "Syphilis" = ValueSet('2.16.840.1.113883.3.464.1003.112.12.1002')
define "Complications of Pregnancy, Childbirth and the Puerperium" = ValueSet('2.16.840.1.113883.3.464.1003.111.12.1012')
define "Pregnancy Test" = ValueSet('2.16.840.1.113883.3.464.1003.111.12.1011')
define "Pap Test" = ValueSet('2.16.840.1.113883.3.464.1003.108.12.1017')
define "Lab Tests During Pregnancy" = ValueSet('2.16.840.1.113883.3.464.1003.111.12.1007')
define "Lab Tests for Sexually Transmitted Infections" = ValueSet('2.16.840.1.113883.3.464.1003.110.12.1051')
define "Chlamydia Screening" = ValueSet('2.16.840.1.113883.3.464.1003.110.12.1052')
define "Reason for not performing Chlamydia Screening" = ValueSet('TBD') // Not used in the measure, need a value set for this

context PATIENT

define InDemographic =
	Age() >= 16 and Age() < 24 and gender in "Female Administrative Sex"

define SexuallyActive =
	exists ([Condition: "Other Female Reproductive Conditions"])
	or exists ([Condition: "Genital Herpes"])
	or exists ([Condition: "Genococcal Infections and Venereal Diseases"])
	or exists ([Condition: "Inflammatory Diseases of Female Reproductive Organs"])
	or exists ([Condition: "Chlamydia"])
	or exists ([Condition: "HIV"])
	or exists ([Condition: "Syphilis"])
	or exists ([Condition: "Complications of Pregnancy, Childbirth and the Puerperium"])
	or exists ([LaboratoryTest, Order: "Pregnancy Test"])
	or exists ([LaboratoryTest, Order: "Pap Test"])
	or exists ([LaboratoryTest, Order: "Lab Tests During Pregnancy"])
	or exists ([LaboratoryTest, Order: "Lab Tests for Sexually Transmitted Infections"])
		
define NoScreening =
    not exists ([SimpleObservation: "Chlamydia Screening"] R where R.observedAtTime during interval[Today() - 1 years, Today()] and R.result is not null)
    and not exists ([Procedure, Plan: "Chlamydia Screening"] P where R.plannedTime during interval[Today(), null])
	and not exists ([SimpleObservation: "Reason for not performing Chlamydia Screening"])

define NeedScreening = InDemographic and SexuallyActive and NoScreening

//define Proposal =
//	tuple(typeof[Procedure, Proposal]) { procedureCode: CodeableConcept { code: 'XYZ', codeSystem: 'SNOMED-CT', displayName: 'Chlamydia Screening' } };

// Need to be able to map this to FHIR, something like: ToFHIR(Proposal)
// The mapping needs to be kept separate from the artifact definition, the same way the FHIRMap libraries are separate