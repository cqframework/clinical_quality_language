library PreventiveCareandScreeningScreeningforDepressionandFollowUpPlan version '9.1.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'

valueset "Additional evaluation for depression - adolescent": 'urn:oid:2.16.840.1.113883.3.600.1542'
valueset "Additional evaluation for depression - adult": 'urn:oid:2.16.840.1.113883.3.600.1545'
valueset "Bipolar Diagnosis": 'urn:oid:2.16.840.1.113883.3.600.450'
valueset "Depression diagnosis": 'urn:oid:2.16.840.1.113883.3.600.145'
valueset "Depression medications - adolescent": 'urn:oid:2.16.840.1.113883.3.600.469'
valueset "Depression medications - adult": 'urn:oid:2.16.840.1.113883.3.600.470'
valueset "Depression Screening  Encounter Codes": 'urn:oid:2.16.840.1.113883.3.600.1916'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Follow-up for depression - adolescent": 'urn:oid:2.16.840.1.113883.3.600.467'
valueset "Follow-up for depression - adult": 'urn:oid:2.16.840.1.113883.3.600.468'
valueset "Medical or Other reason not done": 'urn:oid:2.16.840.1.113883.3.600.1.1502'
valueset "Negative Depression Screening": 'urn:oid:2.16.840.1.113883.3.600.2451'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Patient Reason refused": 'urn:oid:2.16.840.1.113883.3.600.791'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Positive Depression Screening": 'urn:oid:2.16.840.1.113883.3.600.2450'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Referral for Depression Adolescent": 'urn:oid:2.16.840.1.113883.3.600.537'
valueset "Referral for Depression Adult": 'urn:oid:2.16.840.1.113883.3.600.538'
valueset "Suicide Risk Assessment": 'urn:oid:2.16.840.1.113883.3.600.559'

code "Adolescent depression screening assessment": '73831-0' from "LOINC" display 'Adolescent depression screening assessment'
code "Adult depression screening assessment": '73832-8' from "LOINC" display 'Adult depression screening assessment'
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

define "Follow up for Positive Adolescent Depression Screening":
	exists ( "Additional Evaluation for Depression as Follow Up for Adolescent" )
		or exists ( "Pharmacologic Depression Treatment as Follow Up for Adolescent" )
		or exists ( "Referral for Positive Adolescent Depression Screen" )
		or exists ( "Suicide Assessment as Follow Up for Adolescent" )
		or exists ( "Recommended Follow up for Positive Adolescent Depression Screen" )

define "Follow up for Positive Adult Depression Screening":
	exists ( "Additional Evaluation for Depression as Follow Up for Adult" )
		or exists ( "Pharmacologic Depression Treatment as Follow Up for Adult" )
		or exists ( "Referral for Positive Adult Depression Screen" )
		or exists ( "Suicide Assessment as Follow Up for Adult" )
		or exists ( "Recommended Follow up for Positive Adult Depression Screen" )

define "Initial Population":
	"Patient Age 12 Years or Older at Start of Measurement Period"
		and exists ( "Qualifying Encounter for Depression Screening" )

define "Has No Adult Depression Screening Due to Medical Reason":
	exists ( ["Assessment, Not Performed": "Adult depression screening assessment"] ScreeningNotPerfomedAdult
			with "Qualifying Encounter for Depression Screening" QualifyingEncounter
				such that ScreeningNotPerfomedAdult.negationRationale in "Medical or Other reason not done"
					and ScreeningNotPerfomedAdult.authorDatetime during QualifyingEncounter.relevantPeriod
	)

define "Has Diagnosis of Bipolar Starting Before Qualifying Encounter":
	exists ( ["Diagnosis": "Bipolar Diagnosis"] DiagnosisBipolar
			with "Qualifying Encounter for Depression Screening" QualifyingEncounter
				such that DiagnosisBipolar.prevalencePeriod overlaps before QualifyingEncounter.relevantPeriod
	)

define "Has Diagnosis of Depression Starting Before Qualifying Encounter":
	exists ( ["Diagnosis": "Depression diagnosis"] DiagnosisDepression
			with "Qualifying Encounter for Depression Screening" QualifyingEncounter
				such that DiagnosisDepression.prevalencePeriod overlaps before QualifyingEncounter.relevantPeriod
	)

define "Denominator Exclusions":
	"Has Diagnosis of Bipolar Starting Before Qualifying Encounter"
		or "Has Diagnosis of Depression Starting Before Qualifying Encounter"

define "Most Recent Adolescent Depression Screening Negative":
	"Most Recent Adolescent Depression Screening" RecentAdolescentScreening
		where RecentAdolescentScreening.result in "Negative Depression Screening"

define "Most Recent Adolescent Depression Screening Positive":
	"Most Recent Adolescent Depression Screening" RecentAdolescentScreening
		where RecentAdolescentScreening.result in "Positive Depression Screening"

define "Most Recent Adult Depression Screening Negative":
	"Most Recent Adult Depression Screening" RecentAdultScreening
		where RecentAdultScreening.result in "Negative Depression Screening"

define "Most Recent Adult Depression Screening Positive":
	"Most Recent Adult Depression Screening" RecentAdultScreening
		where RecentAdultScreening.result in "Positive Depression Screening"

define "Numerator":
	( "Patient Age 12 to 17 Years at Start of Measurement Period"
			and ( "Most Recent Adolescent Depression Screening Negative" is not null
					or "Follow up for Positive Adolescent Depression Screening"
			)
	)
		or ( "Patient Age 18 Years or Older at Start of Measurement Period"
				and ( "Most Recent Adult Depression Screening Negative" is not null
						or "Follow up for Positive Adult Depression Screening"
				)
		)

define "Has Adolescent Depression Screening":
	exists ( ["Assessment, Performed": "Adolescent depression screening assessment"] AdolescentScreening
			with "Qualifying Encounter for Depression Screening" QualifyingEncounter
				such that AdolescentScreening.authorDatetime 14 days or less before or on day of start of QualifyingEncounter.relevantPeriod
					and AdolescentScreening.result is not null
	)

define "Has Adult Depression Screening":
	exists ( ["Assessment, Performed": "Adult depression screening assessment"] AdultScreening
			with "Qualifying Encounter for Depression Screening" QualifyingEncounter
				such that AdultScreening.authorDatetime 14 days or less before or on day of start of QualifyingEncounter.relevantPeriod
					and AdultScreening.result is not null
	)

define "Most Recent Adolescent Depression Screening":
	Last(["Assessment, Performed": "Adolescent depression screening assessment"] LastAdolescentScreen
			with "Qualifying Encounter for Depression Screening" QualifyingEncounter
				such that LastAdolescentScreen.authorDatetime 14 days or less before or on day of start of QualifyingEncounter.relevantPeriod
					and LastAdolescentScreen.result is not null
			sort by authorDatetime
	)

define "Most Recent Adult Depression Screening":
	Last(["Assessment, Performed": "Adult depression screening assessment"] LastAdultScreen
			with "Qualifying Encounter for Depression Screening" QualifyingEncounter
				such that LastAdultScreen.authorDatetime 14 days or less before or on day of start of QualifyingEncounter.relevantPeriod
					and LastAdultScreen.result is not null
			sort by authorDatetime
	)

define "Has No Adolescent Depression Screening Due to Patient Reason":
	exists ( ["Assessment, Not Performed": "Adolescent depression screening assessment"] ScreeningNotPerformedAdolescent
			with "Qualifying Encounter for Depression Screening" QualifyingEncounter
				such that ScreeningNotPerformedAdolescent.negationRationale in "Patient Reason refused"
					and ScreeningNotPerformedAdolescent.authorDatetime during QualifyingEncounter.relevantPeriod
	)

define "Has No Adult Depression Screening Due to Patient Reason":
	exists ( ["Assessment, Not Performed": "Adult depression screening assessment"] ScreeningNotPerformedAdult
			with "Qualifying Encounter for Depression Screening" QualifyingEncounter
				such that ScreeningNotPerformedAdult.negationRationale in "Patient Reason refused"
					and ScreeningNotPerformedAdult.authorDatetime during QualifyingEncounter.relevantPeriod
	)

define "Denominator Exceptions":
	( ( "Has No Adolescent Depression Screening Due to Medical Reason"
				or "Has No Adolescent Depression Screening Due to Patient Reason"
		)
			and not "Has Adolescent Depression Screening"
	)
		or ( ( "Has No Adult Depression Screening Due to Medical Reason"
					or "Has No Adult Depression Screening Due to Patient Reason"
			)
				and not "Has Adult Depression Screening"
		)

define "Qualifying Encounter for Depression Screening":
	["Encounter, Performed": "Depression Screening  Encounter Codes"] ValidEncounter
		where ValidEncounter.relevantPeriod during "Measurement Period"

define "Patient Age 12 to 17 Years at Start of Measurement Period":
	exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
		where Global.CalendarAgeInYearsAt(BirthDate.birthDatetime, start of "Measurement Period")< 18

define "Patient Age 12 Years or Older at Start of Measurement Period":
	exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
		where Global.CalendarAgeInYearsAt(BirthDate.birthDatetime, start of "Measurement Period")>= 12

define "Patient Age 18 Years or Older at Start of Measurement Period":
	exists ["Patient Characteristic Birthdate": "Birth date"] BirthDate
		where Global.CalendarAgeInYearsAt(BirthDate.birthDatetime, start of "Measurement Period")>= 18

define "Encounter Associated with Most Recent Positive Adolescent Depression Screening":
	"Qualifying Encounter for Depression Screening" QualifyingEncounter
		with "Most Recent Adolescent Depression Screening Positive" AdolescentScreening
			such that AdolescentScreening.authorDatetime 14 days or less before or on day of start of QualifyingEncounter.relevantPeriod

define "Encounter Associated with Most Recent Positive Adult Depression Screening":
	"Qualifying Encounter for Depression Screening" QualifyingEncounter
		with "Most Recent Adult Depression Screening Positive" AdolescentScreening
			such that AdolescentScreening.authorDatetime 14 days or less before or on day of start of QualifyingEncounter.relevantPeriod

define "Additional Evaluation for Depression as Follow Up for Adolescent":
	["Intervention, Performed": "Additional evaluation for depression - adolescent"] AdditionalEvalAdolescent
		with "Encounter Associated with Most Recent Positive Adolescent Depression Screening" PositiveAdolescentScreeningEncounter
			such that AdditionalEvalAdolescent.authorDatetime same day as start of PositiveAdolescentScreeningEncounter.relevantPeriod

define "Additional Evaluation for Depression as Follow Up for Adult":
	["Intervention, Performed": "Additional evaluation for depression - adult"] AdditionalEvalAdult
		with "Encounter Associated with Most Recent Positive Adult Depression Screening" PositiveAdultScreeningEncounter
			such that AdditionalEvalAdult.authorDatetime same day as start of PositiveAdultScreeningEncounter.relevantPeriod

define "Pharmacologic Depression Treatment as Follow Up for Adolescent":
	["Medication, Order": "Depression medications - adolescent"] DepressionMedicationOrderAdolescent
		with "Encounter Associated with Most Recent Positive Adolescent Depression Screening" PositiveAdolescentScreeningEncounter
			such that DepressionMedicationOrderAdolescent.authorDatetime same day as start of PositiveAdolescentScreeningEncounter.relevantPeriod

define "Pharmacologic Depression Treatment as Follow Up for Adult":
	["Medication, Order": "Depression medications - adult"] MedicationOrderAdult
		with "Encounter Associated with Most Recent Positive Adult Depression Screening" PositiveAdultScreeningEncounter
			such that MedicationOrderAdult.authorDatetime same day as start of PositiveAdultScreeningEncounter.relevantPeriod

define "Referral for Positive Adolescent Depression Screen":
	["Intervention, Order": "Referral for Depression Adolescent"] ReferralForAdolescent
		with "Encounter Associated with Most Recent Positive Adolescent Depression Screening" PositiveAdolescentScreenEncounter
			such that ReferralForAdolescent.authorDatetime same day as start of PositiveAdolescentScreenEncounter.relevantPeriod

define "Referral for Positive Adult Depression Screen":
	["Intervention, Order": "Referral for Depression Adult"] ReferralForAdult
		with "Encounter Associated with Most Recent Positive Adult Depression Screening" PositiveAdultScreenEncounter
			such that ReferralForAdult.authorDatetime same day as start of PositiveAdultScreenEncounter.relevantPeriod

define "Suicide Assessment as Follow Up for Adolescent":
	["Assessment, Performed": "Suicide Risk Assessment"] AssessSuicideRisk
		with "Encounter Associated with Most Recent Positive Adolescent Depression Screening" PositiveAdolescentScreenEncounter
			such that AssessSuicideRisk.authorDatetime same day as start of PositiveAdolescentScreenEncounter.relevantPeriod

define "Suicide Assessment as Follow Up for Adult":
	["Assessment, Performed": "Suicide Risk Assessment"] AssessSuicideRisk
		with "Encounter Associated with Most Recent Positive Adult Depression Screening" PositiveAdultScreenEncounter
			such that AssessSuicideRisk.authorDatetime same day as start of PositiveAdultScreenEncounter.relevantPeriod

define "Recommended Follow up for Positive Adolescent Depression Screen":
	["Intervention, Performed": "Follow-up for depression - adolescent"] FollowupAdolescent
		with "Encounter Associated with Most Recent Positive Adolescent Depression Screening" PositiveAdolescentScreenEncounter
			such that FollowupAdolescent.authorDatetime same day as start of PositiveAdolescentScreenEncounter.relevantPeriod

define "Recommended Follow up for Positive Adult Depression Screen":
	["Intervention, Performed": "Follow-up for depression - adult"] FollowupAdult
		with "Encounter Associated with Most Recent Positive Adult Depression Screening" PositiveAdultScreenEncounter
			such that FollowupAdult.authorDatetime same day as start of PositiveAdultScreenEncounter.relevantPeriod

define "Has No Adolescent Depression Screening Due to Medical Reason":
	exists ( ["Assessment, Not Performed": "Adolescent depression screening assessment"] ScreeningNotPerformedAdolescent
			with "Qualifying Encounter for Depression Screening" QualifyingEncounter
				such that ScreeningNotPerformedAdolescent.negationRationale in "Medical or Other reason not done"
					and ScreeningNotPerformedAdolescent.authorDatetime during QualifyingEncounter.relevantPeriod
	)