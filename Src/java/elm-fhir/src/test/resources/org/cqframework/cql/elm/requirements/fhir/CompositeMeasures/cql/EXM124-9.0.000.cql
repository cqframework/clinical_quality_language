library EXM124 version '9.0.000'


/*
Based on CMS124v7 - Cervical Cancer Screening
*/

/*
This example is a work in progress and should not be considered a final specification
or recommendation for guidance. This example will help guide and direct the process
of finding conventions and usage patterns that meet the needs of the various stakeholders
in the measure development community.
*/

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.001' called FHIRHelpers
include HospiceFHIR4 version '2.0.000' called Hospice
include AdultOutpatientEncountersFHIR4 version '2.0.000' called AdultOutpatientEncounters
include MATGlobalCommonFunctionsFHIR4 version '5.0.000' called Global
include SupplementalDataElementsFHIR4 version '2.0.000' called SDE

codesystem "SNOMEDCT:2017-09": 'http://snomed.info/sct/731000124108' version 'http://snomed.info/sct/731000124108/version/201709'

valueset "Hysterectomy with No Residual Cervix": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.198.12.1014'
valueset "Pap Test": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.108.12.1017'
valueset "HPV Test": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.110.12.1059'

code "Congenital absence of cervix (disorder)": '37687000' from "SNOMEDCT:2017-09" display 'Congenital absence of cervix (disorder)'

parameter "Measurement Period" Interval<DateTime>
  default Interval[@2019-01-01T00:00:00.0, @2020-01-01T00:00:00.0)

context Patient

define "SDE Ethnicity":
  SDE."SDE Ethnicity"

define "SDE Payer":
  SDE."SDE Payer"

define "SDE Race":
  SDE."SDE Race"

define "SDE Sex":
  SDE."SDE Sex"

define "Initial Population":
	Patient.gender = 'female'
		and Global."CalendarAgeInYearsAt"(FHIRHelpers.ToDate(Patient.birthDate), start of "Measurement Period") in Interval[23, 64]
		and exists AdultOutpatientEncounters."Qualifying Encounters"

define "Denominator":
	"Initial Population"

define "Denominator Exclusion":
	Hospice."Has Hospice"
		or exists "Absence of Cervix"

define "Absence of Cervix":
  ([Procedure: "Hysterectomy with No Residual Cervix"] NoCervixProcedure
   where Global."Normalize Interval"(NoCervixProcedure.performed) ends on or before end of "Measurement Period"
     and NoCervixProcedure.status = 'completed'
     )
  union [Condition : "Congenital absence of cervix (disorder)"] NoCervixBirth
		where Global."Normalize Interval"(NoCervixBirth.onset) starts on or before end of "Measurement Period"
  //    and NoCervixBirth.clinicalStatus ~ ToConcept(Global."active") because this is an excluion, all statuses are acceptable

define "Numerator":
	exists "Cervical Cytology Within 3 Years"
		or exists "HPV Test Within 5 Years for Women Age 30 and Older"

define "Cervical Cytology Within 3 Years":
	[Observation: "Pap Test"] CervicalCytology
		where CervicalCytology.value is not null
			and CervicalCytology.status in { 'final', 'amended', 'corrected', 'preliminary' }
		  and Global."Normalize Interval"(CervicalCytology.effective) ends 3 years or less on or before end of "Measurement Period"

define "HPV Test Within 5 Years for Women Age 30 and Older":
	[Observation: "HPV Test"] HPVTest
      where HPVTest.value is not null
        and HPVTest.status in { 'final', 'amended', 'corrected', 'preliminary' }
        and  Global."CalendarAgeInYearsAt"(FHIRHelpers.ToDate(Patient.birthDate), start of Global."Normalize Interval"(HPVTest.effective))>= 30
        and Global."Normalize Interval"(HPVTest.effective) ends 5 years or less on or before end of "Measurement Period"
