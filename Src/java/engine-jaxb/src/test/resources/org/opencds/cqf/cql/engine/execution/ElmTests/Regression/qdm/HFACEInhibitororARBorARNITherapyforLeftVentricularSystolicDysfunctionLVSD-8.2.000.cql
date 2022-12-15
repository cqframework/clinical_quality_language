library HeartFailureHFAngiotensinConvertingEnzymeACEInhibitororAngiotensinReceptorBlockerARBorAngiotensinReceptorNeprilysinInhibitorARNITherapyforLeftVentricularSystolicDysfunctionLVSD version '8.2.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "ACE Inhibitor or ARB": 'urn:oid:2.16.840.1.113883.3.526.3.1139'
valueset "ACE Inhibitor or ARB Ingredient": 'urn:oid:2.16.840.1.113883.3.526.3.1489'
valueset "Allergy to ACE Inhibitor or ARB": 'urn:oid:2.16.840.1.113883.3.526.3.1211'
valueset "Care Services in Long-Term Residential Facility": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1014'
valueset "Discharge Services - Hospital Inpatient": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1007'
valueset "Ejection Fraction": 'urn:oid:2.16.840.1.113883.3.526.3.1134'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Heart Failure": 'urn:oid:2.16.840.1.113883.3.526.3.376'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Intolerance to ACE Inhibitor or ARB": 'urn:oid:2.16.840.1.113883.3.526.3.1212'
valueset "Left Ventricular Systolic Dysfunction": 'urn:oid:2.16.840.1.113883.3.526.3.1091'
valueset "Medical Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1007'
valueset "Moderate or Severe": 'urn:oid:2.16.840.1.113883.3.526.3.1092'
valueset "Moderate or Severe LVSD": 'urn:oid:2.16.840.1.113883.3.526.3.1090'
valueset "Nursing Facility Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1012'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Outpatient Consultation": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1008'
valueset "Patient Provider Interaction": 'urn:oid:2.16.840.1.113883.3.526.3.1012'
valueset "Patient Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1008'
valueset "Patient Reason for ACE Inhibitor or ARB Decline": 'urn:oid:2.16.840.1.113883.3.526.3.1140'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Pregnancy": 'urn:oid:2.16.840.1.113883.3.526.3.378'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Renal Failure Due to ACE Inhibitor": 'urn:oid:2.16.840.1.113883.3.526.3.1151'
valueset "System Reason": 'urn:oid:2.16.840.1.113883.3.526.3.1009'

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

define "Face to Face Encounter During Measurement Period":
	( ["Encounter, Performed": "Care Services in Long-Term Residential Facility"]
		union ["Encounter, Performed": "Home Healthcare Services"]
		union ["Encounter, Performed": "Nursing Facility Visit"]
		union ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Outpatient Consultation"] ) FaceToFaceEncounter
		where FaceToFaceEncounter.relevantPeriod during "Measurement Period"

define "Inpatient Discharge During Measurement Period":
	["Encounter, Performed": "Discharge Services - Hospital Inpatient"] InpatientEncounter
		where InpatientEncounter.relevantPeriod during "Measurement Period"

define "Heart Failure Inpatient Encounter":
	"Inpatient Discharge During Measurement Period" InpatientEncounter
		with ["Diagnosis": "Heart Failure"] HeartFailure
			such that HeartFailure.prevalencePeriod overlaps InpatientEncounter.relevantPeriod

define "Heart Failure Outpatient Encounter with Moderate or Severe LVSD":
	"Heart Failure Outpatient Encounter" HFOutpatientEncounter
		with "Moderate or Severe LVSD Findings" OutpatientLVSDFindings
			such that OutpatientLVSDFindings.prevalencePeriod starts before 
			end of HFOutpatientEncounter.relevantPeriod
				or OutpatientLVSDFindings.relevantPeriod starts before 
				end of HFOutpatientEncounter.relevantPeriod

define "Diagnosis of Pregnancy Outpatient":
	["Diagnosis": "Pregnancy"] Pregnancy
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that Pregnancy.prevalencePeriod overlaps ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Diagnosis of Pregnancy Inpatient":
	["Diagnosis": "Pregnancy"] Pregnancy
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that Pregnancy.prevalencePeriod overlaps ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Heart Failure Inpatient Encounter with Moderate or Severe LVSD":
	"Heart Failure Inpatient Encounter" HFInpatientEncounter
		with "Moderate or Severe LVSD Findings" InpatientLVSDFindings
			such that InpatientLVSDFindings.prevalencePeriod starts before 
			end of HFInpatientEncounter.relevantPeriod
				or InpatientLVSDFindings.relevantPeriod starts before 
				end of HFInpatientEncounter.relevantPeriod

define "Denominator 1":
	"Initial Population 1"
		and exists "Heart Failure Outpatient Encounter with Moderate or Severe LVSD"

define "Denominator 2":
	"Initial Population 2"
		and exists "Heart Failure Inpatient Encounter with Moderate or Severe LVSD"

define "Qualifying Encounter":
	( ["Encounter, Performed": "Office Visit"]
		union ["Encounter, Performed": "Outpatient Consultation"]
		union ["Encounter, Performed": "Nursing Facility Visit"]
		union ["Encounter, Performed": "Care Services in Long-Term Residential Facility"]
		union ["Encounter, Performed": "Home Healthcare Services"]
		union ["Encounter, Performed": "Patient Provider Interaction"] ) ValidEncounter
		where ValidEncounter.relevantPeriod during "Measurement Period"

define "Moderate or Severe LVSD Findings":
	( ["Diagnostic Study, Performed": "Ejection Fraction"] EjectionFraction
			where EjectionFraction.result < 40 '%'
	)
		union ["Diagnosis": "Moderate or Severe LVSD"]
		union ( ["Diagnosis": "Left Ventricular Systolic Dysfunction"] LVSD
				where LVSD.severity in "Moderate or Severe"
		)

define "ACEI or ARB or ARNI at Inpatient Discharge":
	["Medication, Discharge": "ACE Inhibitor or ARB"] ACEIOrARBOrARNIAtDischarge
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that ACEIOrARBOrARNIAtDischarge.authorDatetime during ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Allergy or Intolerance to ACEI or ARB or ARNI Ingredient Inpatient":
	["Allergy/Intolerance": "ACE Inhibitor or ARB Ingredient"] ACEIOrARBOrARNIAllergyIntolerance
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that ACEIOrARBOrARNIAllergyIntolerance.prevalencePeriod overlaps after ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Currently Taking ACEI or ARB or ARNI Inpatient":
	["Medication, Active": "ACE Inhibitor or ARB"] ActiveACEIOrARBOrARNI
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that ActiveACEIOrARBOrARNI.relevantPeriod overlaps after ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Currently Taking ACEI or ARB or ARNI Outpatient":
	["Medication, Active": "ACE Inhibitor or ARB"] ActiveACEIOrARBOrARNI
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that ActiveACEIOrARBOrARNI.relevantPeriod overlaps after ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Diagnosis of Allergy to ACEI or ARB Outpatient":
	["Diagnosis": "Allergy to ACE Inhibitor or ARB"] ACEIOrARBAllergyDiagnosis
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that ACEIOrARBAllergyDiagnosis.prevalencePeriod overlaps after ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Diagnosis of Allergy to ACEI or ARB Inpatient":
	["Diagnosis": "Allergy to ACE Inhibitor or ARB"] ACEIOrARBAllergyDiagnosis
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that ACEIOrARBAllergyDiagnosis.prevalencePeriod overlaps after ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Diagnosis of Intolerance to ACEI or ARB Inpatient":
	["Diagnosis": "Intolerance to ACE Inhibitor or ARB"] ACEIOrARBIntoleranceDiagnosis
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that ACEIOrARBIntoleranceDiagnosis.prevalencePeriod overlaps after ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Diagnosis of Intolerance to ACEI or ARB Outpatient":
	["Diagnosis": "Intolerance to ACE Inhibitor or ARB"] ACEIOrARBIntoleranceDiagnosis
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that ACEIOrARBIntoleranceDiagnosis.prevalencePeriod overlaps after ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Diagnosis of Renal Failure Due to ACEI Inpatient":
	["Diagnosis": "Renal Failure Due to ACE Inhibitor"] RenalFailureDueToACEI
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that RenalFailureDueToACEI.prevalencePeriod overlaps ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod

define "Diagnosis of Renal Failure Due to ACEI Outpatient":
	["Diagnosis": "Renal Failure Due to ACE Inhibitor"] RenalFailureDueToACEI
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that RenalFailureDueToACEI.prevalencePeriod overlaps ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Heart Failure Outpatient Encounter":
	"Face to Face Encounter During Measurement Period" ValidFaceToFaceEncounter
		with ["Diagnosis": "Heart Failure"] HeartFailure
			such that HeartFailure.prevalencePeriod overlaps ValidFaceToFaceEncounter.relevantPeriod

define "ACEI or ARB or ARNI Ordered Outpatient":
	["Medication, Order": "ACE Inhibitor or ARB"] ACEIOrARBOrARNIOrdered
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that ACEIOrARBOrARNIOrdered.authorDatetime during ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Numerator 1":
	exists "ACEI or ARB or ARNI Ordered Outpatient"
		or exists "Currently Taking ACEI or ARB or ARNI Outpatient"

define "Allergy or Intolerance to ACEI or ARB or ARNI Ingredient Outpatient":
	["Allergy/Intolerance": "ACE Inhibitor or ARB Ingredient"] ACEIOrARBOrARNIAllergyIntolerance
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that ACEIOrARBOrARNIAllergyIntolerance.prevalencePeriod overlaps after ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod

define "Denominator Exceptions 1":
	exists "Medical Patient or System Reason for Not Ordering ACEI or ARB or ARNI Outpatient"
		or exists "Allergy or Intolerance to ACEI or ARB or ARNI Ingredient Outpatient"
		or exists "Diagnosis of Allergy to ACEI or ARB Outpatient"
		or exists "Diagnosis of Intolerance to ACEI or ARB Outpatient"
		or exists "Diagnosis of Pregnancy Outpatient"
		or exists "Diagnosis of Renal Failure Due to ACEI Outpatient"

define "Denominator Exceptions 2":
	exists "Medical Patient or System Reason for No ACEI or ARB or ARNI at Inpatient Discharge"
		or exists "Allergy or Intolerance to ACEI or ARB or ARNI Ingredient Inpatient"
		or exists "Diagnosis of Allergy to ACEI or ARB Inpatient"
		or exists "Diagnosis of Intolerance to ACEI or ARB Inpatient"
		or exists "Diagnosis of Pregnancy Inpatient"
		or exists "Diagnosis of Renal Failure Due to ACEI Inpatient"

define "Numerator 2":
	exists "ACEI or ARB or ARNI at Inpatient Discharge"
		or exists "Currently Taking ACEI or ARB or ARNI Inpatient"

define "Medical Patient or System Reason for No ACEI or ARB or ARNI at Inpatient Discharge":
	["Medication, Not Discharged": "ACE Inhibitor or ARB"] NoACEIOrARBOrARNIAtDischarge
		with "Heart Failure Inpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFInpatientEncounter
			such that NoACEIOrARBOrARNIAtDischarge.authorDatetime during ModerateOrSevereLVSDHFInpatientEncounter.relevantPeriod
		where ( NoACEIOrARBOrARNIAtDischarge.negationRationale in "Medical Reason"
				or NoACEIOrARBOrARNIAtDischarge.negationRationale in "Patient Reason"
				or NoACEIOrARBOrARNIAtDischarge.negationRationale in "Patient Reason for ACE Inhibitor or ARB Decline"
				or NoACEIOrARBOrARNIAtDischarge.negationRationale in "System Reason"
		)

define "Medical Patient or System Reason for Not Ordering ACEI or ARB or ARNI Outpatient":
	["Medication, Not Ordered": "ACE Inhibitor or ARB"] NoACEIOrARBOrARNIOrdered
		with "Heart Failure Outpatient Encounter with Moderate or Severe LVSD" ModerateOrSevereLVSDHFOutpatientEncounter
			such that NoACEIOrARBOrARNIOrdered.authorDatetime during ModerateOrSevereLVSDHFOutpatientEncounter.relevantPeriod
		where ( NoACEIOrARBOrARNIOrdered.negationRationale in "Medical Reason"
				or NoACEIOrARBOrARNIOrdered.negationRationale in "Patient Reason"
				or NoACEIOrARBOrARNIOrdered.negationRationale in "Patient Reason for ACE Inhibitor or ARB Decline"
				or NoACEIOrARBOrARNIOrdered.negationRationale in "System Reason"
		)

define "Initial Population 1":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 18
	)
		and Count("Qualifying Encounter")>= 2
		and exists "Heart Failure Outpatient Encounter"

define "Initial Population 2":
	exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
			where Global."CalendarAgeInYearsAt"(BirthDate.birthDatetime, start of "Measurement Period")>= 18
	)
		and exists "Heart Failure Inpatient Encounter"