library EXM124_QICore4 version '8.2.000'

/*
Based on CMS124v7 - Cervical Cancer Screening
*/

/*
This example is a work in progress and should not be considered a final specification
or recommendation for guidance. This example will help guide and direct the process
of finding conventions and usage patterns that meet the needs of the various stakeholders
in the measure development community.
*/

using QICore version '4.1.0'

include FHIRHelpers version '4.0.1'

include Hospice_QICore4 version '2.0.000' called Hospice
include AdultOutpatientEncounters_QICore4 version '2.0.000' called AdultOutpatientEncounters
include MATGlobalCommonFunctions_QICore4 version '5.0.000' called Global
include SupplementalDataElements_QICore4 version '2.0.0' called SDE

codesystem "SNOMEDCT:2017-09": 'http://snomed.info/sct/731000124108' version 'http://snomed.info/sct/731000124108/version/201709'

valueset "ONC Administrative Sex": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1'
valueset "Race": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.836'
valueset "Ethnicity": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.837'
valueset "Payer": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.114222.4.11.3591'
valueset "Female": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.560.100.2'
valueset "Home Healthcare Services": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Hysterectomy with No Residual Cervix": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.198.12.1014'
valueset "Office Visit": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "Pap Test": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.108.12.1017'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1023'
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
  	and Global."CalendarAgeInYearsAt"(Patient.birthDate, start of "Measurement Period") in Interval[23, 64]
  	and exists AdultOutpatientEncounters."Qualifying Encounters"

define "Denominator":
    	"Initial Population"

define "Denominator Exclusion":
	Hospice."Has Hospice"
  		or exists "Surgical Absence of Cervix"
  		or exists "Absence of Cervix"


define "Absence of Cervix":
	[Condition : "Congenital absence of cervix (disorder)"] NoCervixBirth
  		where Global."Normalize Interval"(NoCervixBirth.onset) starts before end of "Measurement Period"
        and NoCervixBirth.clinicalStatus ~ ToConcept(Global."active")

define "Surgical Absence of Cervix":
	[Procedure: "Hysterectomy with No Residual Cervix"] NoCervixHysterectomy
		where Global."Normalize Interval"(NoCervixHysterectomy.performed) ends before end of "Measurement Period"
			and NoCervixHysterectomy.status = 'completed'

define "Numerator":
	exists "Pap Test Within 3 Years"
		or exists "Pap Test With HPV Within 5 Years"

define "Pap Test with Results":
	[Observation: "Pap Test"] PapTest
		where PapTest.value is not null
			and PapTest.status in { 'final', 'amended', 'corrected', 'preliminary' }

define "Pap Test Within 3 Years":
	"Pap Test with Results" PapTest
		where Global."Normalize Interval"(PapTest.effective) ends 3 years or less before end of "Measurement Period"

define "PapTest Within 5 Years":
	( "Pap Test with Results" PapTestOver30YearsOld
			where Global."CalendarAgeInYearsAt"(Patient.birthDate, start of Global."Normalize Interval"(PapTestOver30YearsOld.effective))>= 30
				and Global."Normalize Interval"(PapTestOver30YearsOld.effective) ends 5 years or less before end of "Measurement Period"
	)

define "Pap Test With HPV Within 5 Years":
	"PapTest Within 5 Years" PapTestOver30YearsOld
		with [Observation: "HPV Test"] HPVTest
			such that HPVTest.value is not null
        and Global."Normalize Interval"(HPVTest.effective) starts within 1 day of start of Global."Normalize Interval"(PapTestOver30YearsOld.effective)
				and HPVTest.status in { 'final', 'amended', 'corrected', 'preliminary' }
