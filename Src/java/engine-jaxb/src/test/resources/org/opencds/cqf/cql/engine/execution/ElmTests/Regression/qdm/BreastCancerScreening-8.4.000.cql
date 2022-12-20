library BreastCancerScreening version '8.4.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global
include Adult_Outpatient_Encounters version '1.2.000' called AdultOutpatientEncounters
include Hospice version '2.0.000' called Hospice
include AdvancedIllnessandFrailtyExclusionECQM version '4.0.000' called FrailtyLTI

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Bilateral Mastectomy": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1005'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Female": 'urn:oid:2.16.840.1.113883.3.560.100.2'
valueset "History of bilateral mastectomy": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1068'
valueset "Left": 'urn:oid:2.16.840.1.113883.3.464.1003.122.12.1036'
valueset "Mammography": 'urn:oid:2.16.840.1.113883.3.464.1003.108.12.1018'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Right": 'urn:oid:2.16.840.1.113883.3.464.1003.122.12.1035'
valueset "Status Post Left Mastectomy": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1069'
valueset "Status Post Right Mastectomy": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1070'
valueset "Unilateral Mastectomy": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1020'
valueset "Unilateral Mastectomy Left": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1133'
valueset "Unilateral Mastectomy Right": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1134'
valueset "Unilateral Mastectomy, Unspecified Laterality": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1071'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "SDE Ethnicity":
	["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
	["Patient Characteristic Payer": "Payer"]

define "SDE Race":
	["Patient Characteristic Race": "Race"]

define "SDE Sex":
	["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Denominator":
	"Initial Population"

define "Bilateral Mastectomy Procedure":
	["Procedure, Performed": "Bilateral Mastectomy"] BilateralMastectomyPerformed
		where BilateralMastectomyPerformed.relevantPeriod ends before 
		end of "Measurement Period"

define "Numerator":
	exists ( ["Diagnostic Study, Performed": "Mammography"] Mammogram
			where ( Mammogram.relevantPeriod ends 27 months or less before day of 
				end "Measurement Period"
			)
	)

define "Left Mastectomy Procedure":
	["Procedure, Performed": "Unilateral Mastectomy Left"] UnilateralMastectomyLeftPerformed
		where UnilateralMastectomyLeftPerformed.relevantPeriod ends before 
		end of "Measurement Period"

define "Right Mastectomy Procedure":
	["Procedure, Performed": "Unilateral Mastectomy Right"] UnilateralMastectomyRightPerformed
		where UnilateralMastectomyRightPerformed.relevantPeriod ends before 
		end of "Measurement Period"

define "Unilateral Mastectomy Procedure":
	["Procedure, Performed": "Unilateral Mastectomy"] UnilateralMastectomy
		where UnilateralMastectomy.relevantPeriod ends before 
		end of "Measurement Period"

define "Bilateral Mastectomy Diagnosis":
	["Diagnosis": "History of bilateral mastectomy"] BilateralMastectomyHistory
		where BilateralMastectomyHistory.prevalencePeriod starts before 
		end of "Measurement Period"

define "Left Mastectomy Diagnosis":
	( ["Diagnosis": "Status Post Left Mastectomy"]
		union ( ["Diagnosis": "Unilateral Mastectomy, Unspecified Laterality"] UnilateralMastectomyDiagnosis
				where UnilateralMastectomyDiagnosis.anatomicalLocationSite in "Left"
		) ) LeftMastectomy
		where LeftMastectomy.prevalencePeriod starts before 
		end of "Measurement Period"

define "Right Mastectomy Diagnosis":
	( ["Diagnosis": "Status Post Right Mastectomy"] RightMastectomyProcedure
		union ( ["Diagnosis": "Unilateral Mastectomy, Unspecified Laterality"] UnilateralMastectomyDiagnosis
				where UnilateralMastectomyDiagnosis.anatomicalLocationSite in "Right"
		) ) RightMastectomy
		where RightMastectomy.prevalencePeriod starts before 
		end of "Measurement Period"

define "Initial Population":
	exists ( ["Patient Characteristic Sex": "Female"] )
		and exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")in Interval[51, 74 )
				and exists AdultOutpatientEncounters."Qualifying Encounters"

define "Denominator Exclusions":
	Hospice."Has Hospice"
		or ( Count("Unilateral Mastectomy Procedure")= 2 )
		or ( ( exists ( "Right Mastectomy Diagnosis" )
					or exists ( "Right Mastectomy Procedure" )
			)
				and ( exists ( "Left Mastectomy Diagnosis" )
						or exists ( "Left Mastectomy Procedure" )
				)
		)
		or exists "Bilateral Mastectomy Diagnosis"
		or exists "Bilateral Mastectomy Procedure"
		or FrailtyLTI."Advanced Illness and Frailty Exclusion Including Under Age 80"
		or ( exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
				where ( Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 65 )
					and FrailtyLTI."Has Spent More Than 90 Days in Long Term Care"
		)