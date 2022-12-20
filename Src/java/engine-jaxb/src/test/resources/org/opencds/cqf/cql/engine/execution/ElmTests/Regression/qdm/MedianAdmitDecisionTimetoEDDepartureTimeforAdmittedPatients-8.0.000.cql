library MedianAdmitDecisionTimetoEDDepartureTimeforAdmittedPatients version '8.0.000'

using QDM version '5.4'

include MATGlobalCommonFunctions version '4.0.000' called Global

valueset "Decision to Admit to Hospital Inpatient": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.295'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Hospital Settings": 'urn:oid:2.16.840.1.113762.1.4.1111.126'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Psychiatric/Mental Health Diagnosis": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.299'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'

context Patient

define "SDE Ethnicity":
	["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
	["Patient Characteristic Payer": "Payer"]

define "SDE Race":
	["Patient Characteristic Race": "Race"]

define "SDE Sex":
	["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Measure Population":
	"Initial Population"

define "Initial Population":
	/*Emergency Department visit with a Decision to Admit inpatient followed by an Inpatient encounter within or at an hour of the ED Visit*/
	Global."Inpatient Encounter" EncounterInpatient
		with "ED Visit with Admit Order" EDAdmitOrder
			such that EDAdmitOrder.relevantPeriod ends 1 hour or less before or on start of EncounterInpatient.relevantPeriod

define "Stratification 1":
	/*Encounters of patients without a principal diagnosis in the "Psychiatric/Mental Health Diagnosis" value set*/
	Global."Inpatient Encounter" EncounterInpatient
		where EncounterInpatient.principalDiagnosis is null
			or not ( EncounterInpatient.principalDiagnosis in "Psychiatric/Mental Health Diagnosis" )

define "Stratification 2":
	/*Encounters of patients with a principal diagnosis that is in the "Psychiatric/Mental Health Diagnosis" value set*/
	Global."Inpatient Encounter" EncounterInpatient
		where EncounterInpatient.principalDiagnosis in "Psychiatric/Mental Health Diagnosis"

define "Measure Population Exclusions":
	/* Exclude ED encounters with an admission source in "Hospital Setting"(any different facility, even if part of the same hospital system) resulting in an inpatient stay */
	Global."Inpatient Encounter" EncounterInpatient
		with Global."ED Encounter" EDVisit
			such that EDVisit.relevantPeriod ends 1 hour or less before or on start of EncounterInpatient.relevantPeriod
				and EDVisit.admissionSource in "Hospital Settings"

define "ED Visit with Admit Order":
	/*ED visit during the specified period with a Decision to admit order also during that specified period*/
	Global."ED Encounter" EDVisit
		with ["Encounter, Order": "Decision to Admit to Hospital Inpatient"] AdmitOrder
			such that AdmitOrder.authorDatetime during EDVisit.relevantPeriod
				and AdmitOrder.authorDatetime before or on "DepartureTime"(EDVisit)

define function "DepartureTime"(Encounter "Encounter, Performed"):
	/* The time the patient physically departed the Emergency Department*/
	Last(Encounter.facilityLocations Location
			return 
			end of Location.locationPeriod
			sort ascending
	)

define function "AdmitDecision"(Encounter "Encounter, Performed"):
	/*Decision to admit order that was during the ED visit that is being referenced*/
	Last(["Encounter, Order": "Decision to Admit to Hospital Inpatient"] AdmitOrder
			where AdmitOrder.authorDatetime during "RelatedEDVisit"(Encounter).relevantPeriod
			sort by authorDatetime
	)

define function "MeasureObservation"(Encounter "Encounter, Performed"):
	/* the duration from the Decision to Admit to the departure from the Emergency Department*/
	duration in minutes of Interval["AdmitDecision"(Encounter).authorDatetime, "DepartureTime"("RelatedEDVisit"(Encounter))]

define function "RelatedEDVisit"(Encounter "Encounter, Performed"):
	/*ED visit with a Decision to admit order that preceded an inpatient encounter*/
	Last(Global."ED Encounter" EDVisit
			with ["Encounter, Order": "Decision to Admit to Hospital Inpatient"] AdmitOrder
				such that AdmitOrder.authorDatetime during EDVisit.relevantPeriod
			where EDVisit.relevantPeriod ends 1 hour or less before or on start of Encounter.relevantPeriod
			sort by start of relevantPeriod
	)