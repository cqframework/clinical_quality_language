library MATGlobalCommonFunctionsFHIR4 version '6.1.000'

/*@update: BTR 2020-03-31 ->
Incremented version to 5.0.000
Updated FHIR version to 4.0.1
Changed timezone keyword to timezoneoffset for use with CQL 1.4
Removed Normalize Onset in favor of more general Normalize Interval
Updated CodeSystems for ConditionVerificationStatusCodes and RoleCodes

@update: BTR 2021-05-13 ->
Added ActiveCondition Codes and Inactive Condition Codes value sets
Added function documentation throughout
Fixed EDVisit not using Last
Updated prevalence period to use an inclusive boundary if the condition is active
Added HasStart, HasEnd, Earliest, and Latest functions
Removed ToDate and Age calculation functions

@update: BTR 2021-06-25 ->
Added GetBaseExtension overloads for Element*/

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.001' called FHIRHelpers

codesystem "ConditionClinicalStatusCodes": 'http://terminology.hl7.org/CodeSystem/condition-clinical' 
codesystem "AllergyIntoleranceClinicalStatusCodes": 'http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical' 
codesystem "AllergyIntoleranceVerificationStatusCodes": 'http://terminology.hl7.org/CodeSystem/allergyintolerance-verification' 
codesystem "Diagnosis Role": 'http://terminology.hl7.org/CodeSystem/diagnosis-role' 
codesystem "LOINC": 'http://loinc.org' 
codesystem "MedicationRequestCategory": 'http://terminology.hl7.org/CodeSystem/medicationrequest-category' 
codesystem "ConditionVerificationStatusCodes": 'http://terminology.hl7.org/CodeSystem/condition-ver-status' 
codesystem "SNOMEDCT": 'http://snomed.info/sct' 
codesystem "RoleCode": 'http://terminology.hl7.org/CodeSystem/v3-RoleCode' 

valueset "Emergency Department Visit": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.292' 
valueset "Encounter Inpatient": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.666.5.307' 
valueset "Observation Services": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1111.143' 

code "active": 'active' from "ConditionClinicalStatusCodes" display 'active'
code "allergy-active": 'active' from "AllergyIntoleranceClinicalStatusCodes" display 'allergy-active'
code "allergy-confirmed": 'confirmed' from "AllergyIntoleranceVerificationStatusCodes" display 'allergy-confirmed'
code "allergy-inactive": 'inactive' from "AllergyIntoleranceClinicalStatusCodes" display 'allergy-inactive'
code "allergy-refuted": 'refuted' from "AllergyIntoleranceVerificationStatusCodes" display 'allergy-refuted'
code "allergy-resolved": 'resolved' from "AllergyIntoleranceClinicalStatusCodes" display 'allergy-resolved'
code "allergy-unconfirmed": 'unconfirmed' from "AllergyIntoleranceVerificationStatusCodes" display 'allergy-unconfirmed'
code "Billing": 'billing' from "Diagnosis Role" display 'Billing'
code "Birthdate": '21112-8' from "LOINC" display 'Birth date'
code "Community": 'community' from "MedicationRequestCategory" display 'Community'
code "confirmed": 'confirmed' from "ConditionVerificationStatusCodes" display 'confirmed'
code "Dead": '419099009' from "SNOMEDCT" display 'Dead'
code "differential": 'differential' from "ConditionVerificationStatusCodes" display 'differential'
code "Discharge": 'discharge' from "MedicationRequestCategory" display 'Discharge'
code "entered-in-error": 'entered-in-error' from "ConditionVerificationStatusCodes" display 'entered-in-error'
code "ER": 'ER' from "RoleCode" display 'Emergency room'
code "ICU": 'ICU' from "RoleCode" display 'Intensive care unit'
code "inactive": 'inactive' from "ConditionClinicalStatusCodes" display 'inactive'
code "provisional": 'provisional' from "ConditionVerificationStatusCodes" display 'provisional'
code "recurrence": 'recurrence' from "ConditionClinicalStatusCodes" display 'recurrence'
code "refuted": 'refuted' from "ConditionVerificationStatusCodes" display 'refuted'
code "relapse": 'relapse' from "ConditionClinicalStatusCodes" display 'relapse'
code "remission": 'remission' from "ConditionClinicalStatusCodes" display 'remission'
code "resolved": 'resolved' from "ConditionClinicalStatusCodes" display 'resolved'
code "unconfirmed": 'unconfirmed' from "ConditionVerificationStatusCodes" display 'unconfirmed'

parameter "Measurement Period" Interval<DateTime>
  default Interval[@2019-01-01T00:00:00.0, @2020-01-01T00:00:00.0)

context Patient

define "Inpatient Encounter":
  [Encounter: "Encounter Inpatient"] EncounterInpatient
      		where EncounterInpatient.status = 'finished'
      		    and "LengthInDays"(EncounterInpatient.period) <= 120
      			and EncounterInpatient.period ends during "Measurement Period"

/*Calculates the difference in calendar days between the start and end of the given interval.*/
define function "LengthInDays"(Value Interval<DateTime> ):
  difference in days between start of Value and end of Value

/*Returns the most recent emergency department visit, if any, that occurs 1 hour or less prior to the given encounter.*/
define function "ED Visit"(TheEncounter FHIR.Encounter ):
  Last(
    [Encounter: "Emergency Department Visit"] EDVisit
      where EDVisit.status = 'finished'
        and EDVisit.period ends 1 hour or less on or before start of FHIRHelpers.ToInterval(TheEncounter.period)
      sort by end of period
    )

/*Hospitalization returns the total interval for admission to discharge for the given encounter, or for the admission of any immediately prior emergency department visit to the discharge of the given encounter.*/
define function "Hospitalization"(TheEncounter FHIR.Encounter ):
  ( "ED Visit"(TheEncounter) ) X
    return
        if X is null then TheEncounter.period
        else Interval[start of FHIRHelpers.ToInterval(X.period), end of FHIRHelpers.ToInterval(TheEncounter.period)]

/*Returns list of all locations within an encounter, including locations for immediately prior ED visit.*/
define function "Hospitalization Locations"(TheEncounter FHIR.Encounter ):
  ( "ED Visit"(TheEncounter) ) EDEncounter
    return
        if EDEncounter is null then TheEncounter.location
        else flatten { EDEncounter.location, TheEncounter.location }

/*Returns the length of stay in days (i.e. the number of days between admission and discharge) for the given encounter, or from the admission of any immediately prior emergency department visit to the discharge of the encounter*/
define function "Hospitalization Length of Stay"(TheEncounter FHIR.Encounter ):
  LengthInDays("Hospitalization"(TheEncounter))

/*Returns admission time for an encounter or for immediately prior emergency department visit.*/
define function "Hospital Admission Time"(TheEncounter FHIR.Encounter ):
  start of "Hospitalization"(TheEncounter)

/*Hospital Discharge Time returns the discharge time for an encounter*/
define function "Hospital Discharge Time"(TheEncounter FHIR.Encounter ):
  end of FHIRHelpers.ToInterval(TheEncounter.period)

/*Returns earliest arrival time for an encounter including any prior ED visit.*/
define function "Hospital Arrival Time"(TheEncounter FHIR.Encounter ):
  start of FHIRHelpers.ToInterval(First(
  	    ( "Hospitalization Locations"(TheEncounter) ) HospitalLocation
  			sort by start of FHIRHelpers.ToInterval(period)
  	).period)
  
  // TODO - fix these (must fetch Location resources and compare id to reference)
  /*Returns the latest departure time for encounter including any prior ED visit. */
  /*
  define function "Hospital Departure Time"(TheEncounter FHIR.Encounter):
  	end of FHIRHelpers.ToInterval(Last(
  	    ( "Hospitalization Locations"(TheEncounter) ) HospitalLocation
  			sort by start of FHIRHelpers.ToInterval(period)
  	).period)
  
  define function "Emergency Department Arrival Time"(TheEncounter FHIR.Encounter):
  	start of FHIRHelpers.ToInterval((
  	    singleton from (
  	        ( "Hospitalization Locations"(TheEncounter) ) HospitalLocation
  				where HospitalLocation.type ~ "ER"
  		)
  	).period)
  
  define function "First Inpatient Intensive Care Unit"(TheEncounter FHIR.Encounter):
  	First(
  	    ( TheEncounter.location ) HospitalLocation
  			where HospitalLocation.type ~ "ICU"
  				and HospitalLocation.period during TheEncounter.period
  			sort by start of FHIRHelpers.ToInterval(period)
  	)
  */
  
  /*Hospitalization with Observation and Outpatient Surgery Service returns the total interval from the start of any immediately prior emergency department visit, outpatient surgery visit or observation visit to the discharge of the given encounter.*/
  /* TODO:
  define function "HospitalizationWithObservationAndOutpatientSurgeryService"(Encounter "Encounter, Performed" ):
  Encounter Visit
  	let ObsVisit: Last(["Encounter, Performed": "Observation Services"] LastObs
  			where LastObs.relevantPeriod ends 1 hour or less on or before start of Visit.relevantPeriod
  			sort by
  			end of relevantPeriod
  	),
  	VisitStart: Coalesce(start of ObsVisit.relevantPeriod, start of Visit.relevantPeriod),
  	EDVisit: Last(["Encounter, Performed": "Emergency Department Visit"] LastED
  			where LastED.relevantPeriod ends 1 hour or less on or before VisitStart
  			sort by
  			end of relevantPeriod
  	),
  	VisitStartWithED: Coalesce(start of EDVisit.relevantPeriod, VisitStart),
  	OutpatientSurgeryVisit: Last(["Encounter, Performed": "Outpatient Surgery Service"] LastSurgeryOP
  			where LastSurgeryOP.relevantPeriod ends 1 hour or less on or before VisitStartWithED
  			sort by
  			end of relevantPeriod
  	)
  	return Interval[Coalesce(start of OutpatientSurgeryVisit.relevantPeriod, VisitStartWithED),
  	end of Visit.relevantPeriod]
  */

/*Hospitalization with Observation returns the total interval from the start of any immediately prior emergency department visit through the observation visit to the discharge of the given encounter*/
define function "HospitalizationWithObservation"(TheEncounter FHIR.Encounter ):
  TheEncounter Visit
  		let ObsVisit: Last([Encounter: "Observation Services"] LastObs
  				where LastObs.period ends 1 hour or less on or before start of Visit.period
  				sort by end of period
  			),
  			VisitStart: Coalesce(start of ObsVisit.period, start of Visit.period),
  			EDVisit: Last([Encounter: "Emergency Department Visit"] LastED
  				where LastED.period ends 1 hour or less on or before VisitStart
  				sort by end of period
  			)
  		return Interval[Coalesce(start of EDVisit.period, VisitStart), end of Visit.period]

/**
* Normalizes the input argument to an interval representation.
* The input can be provided as a dateTime, Period, Timing, instant, string, Age, or Range.
* The intent of this function is to provide a clear and concise mechanism to treat single
* elements that have multiple possible representations as intervals so that logic doesn't have to account
* for the variability. More complex calculations (such as medication request period or dispense period
* calculation) need specific guidance and consideration. That guidance may make use of this function, but
* the focus of this function is on single element calculations where the semantics are unambiguous.
* If the input is a dateTime, the result a DateTime Interval beginning and ending on that dateTime.
* If the input is a Period, the result is a DateTime Interval.
* If the input is a Timing, an error is raised indicating a single interval cannot be computed from a Timing.
* If the input is an instant, the result is a DateTime Interval beginning and ending on that instant.
* If the input is a string, an error is raised indicating a single interval cannot be computed from a string.
* If the input is an Age, the result is a DateTime Interval beginning when the patient was the given Age,
and ending immediately prior to when the patient was the given Age plus one year.
* If the input is a Range, the result is a DateTime Interval beginning when the patient was the Age given
by the low end of the Range, and ending immediately prior to when the patient was the Age given by the
high end of the Range plus one year.*/
define function "Normalize Interval"(choice Choice<FHIR.dateTime, FHIR.Period, FHIR.Timing, FHIR.instant, FHIR.string, FHIR.Age, FHIR.Range> ):
  case
  	  when choice is FHIR.dateTime then
  	Interval[FHIRHelpers.ToDateTime(choice as FHIR.dateTime), FHIRHelpers.ToDateTime(choice as FHIR.dateTime)]
  		when choice is FHIR.Period then
  		FHIRHelpers.ToInterval(choice as FHIR.Period)
  		when choice is FHIR.instant then
  			Interval[FHIRHelpers.ToDateTime(choice as FHIR.instant), FHIRHelpers.ToDateTime(choice as FHIR.instant)]
  		when choice is FHIR.Age then
  		  Interval[FHIRHelpers.ToDate(Patient.birthDate) + FHIRHelpers.ToQuantity(choice as FHIR.Age),
  			  FHIRHelpers.ToDate(Patient.birthDate) + FHIRHelpers.ToQuantity(choice as FHIR.Age) + 1 year)
  		when choice is FHIR.Range then
  		  Interval[FHIRHelpers.ToDate(Patient.birthDate) + FHIRHelpers.ToQuantity((choice as FHIR.Range).low),
  			  FHIRHelpers.ToDate(Patient.birthDate) + FHIRHelpers.ToQuantity((choice as FHIR.Range).high) + 1 year)
  		when choice is FHIR.Timing then
  		  Message(null as Interval<DateTime>, true, '1', 'Error', 'Cannot compute a single interval from a Timing type')
    when choice is FHIR.string then
      Message(null as Interval<DateTime>, true, '1', 'Error', 'Cannot compute an interval from a String value')
  		else
  			null as Interval<DateTime>
  	end

/**
* Returns an interval representing the abatement of the given condition, if an
abatement element is present, null otherwise.
This function uses the semantics of Normalize Interval to interpret the abatement
element.*/
define function "Normalize Abatement"(condition Condition ):
  if condition.abatement is FHIR.dateTime then
  	  Interval[FHIRHelpers.ToDateTime(condition.abatement as FHIR.dateTime), FHIRHelpers.ToDateTime(condition.abatement as FHIR.dateTime)]
  	else if condition.abatement is FHIR.Period then
  	  FHIRHelpers.ToInterval(condition.abatement as FHIR.Period)
  	else if condition.abatement is FHIR.string then
  Message(null as Interval<DateTime>, true, '1', 'Error', 'Cannot compute an interval from a String value')
  	else if condition.abatement is FHIR.Age then
  		Interval[FHIRHelpers.ToDate(Patient.birthDate) + FHIRHelpers.ToQuantity(condition.abatement as FHIR.Age),
  			FHIRHelpers.ToDate(Patient.birthDate) + FHIRHelpers.ToQuantity(condition.abatement as FHIR.Age) + 1 year)
  	else if condition.abatement is FHIR.Range then
  	  Interval[FHIRHelpers.ToDate(Patient.birthDate) + FHIRHelpers.ToQuantity((condition.abatement as FHIR.Range).low),
  		  FHIRHelpers.ToDate(Patient.birthDate) + FHIRHelpers.ToQuantity((condition.abatement as FHIR.Range).high) + 1 year)
  	else if condition.abatement is FHIR.boolean then
  	  Interval[end of "Normalize Interval"(condition.onset), condition.recordedDate)
  	else null

/*Returns an interval representing the period during which the condition was prevalent (i.e. onset to abatement)
If the condition is "active", then abatement being unknown
would indicate the condition is ongoing, and the ending boundary of the prevalence
period is inclusive, otherwise, the abatement is considered unknown and the ending boundary
of the prevalence period is exclusive.
Note that when using this function it should be noted that many clinical systems
do not actually capture abatement, so care should be taken when using this function
to meet clinical intent.*/
define function "Prevalence Period"(condition Condition ):
  if condition.clinicalStatus ~ "active"
    or condition.clinicalStatus ~ "recurrence"
    or condition.clinicalStatus ~ "relapse" then
    Interval[start of "Normalize Interval"(condition.onset), end of "Normalize Abatement"(condition)]
  else
    Interval[start of "Normalize Interval"(condition.onset), end of "Normalize Abatement"(condition))

/*Returns the tail of the given uri (i.e. everything after the last slash in the URI).*/
define function "GetId"(uri String ):
  Last(Split(uri, '/'))

/*Returns the Condition resources referenced by the diagnosis element of the Encounter*/
define function "EncounterDiagnosis"(Encounter Encounter ):
  Encounter.diagnosis D
    return singleton from ([Condition] C where C.id = "GetId"(D.condition.reference))
  
  // Returns the condition that is specified as the principal diagnosis for the encounter
  // TODO: BTR 2019-07-30: Shouldn't need the FHIRHelpers reference here, investigate

define function "PrincipalDiagnosis"(Encounter Encounter ):
  (singleton from (Encounter.diagnosis D where FHIRHelpers.ToInteger(D.rank) = 1)) PD
  		return singleton from ([Condition] C where C.id = "GetId"(PD.condition.reference))
  // Returns the location for the given location reference

/*Returns the Location resource specified by the given reference*/
define function "GetLocation"(reference Reference ):
  singleton from (
    [Location] L where L.id = GetId(reference.reference)
  )

/*NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the
CQL model info.*/
define function "GetExtensions"(domainResource DomainResource, url String ):
  domainResource.extension E
  	  where E.url = ('http://hl7.org/fhir/us/qicore/StructureDefinition/' + url)
  		return E

define function "GetExtension"(domainResource DomainResource, url String ):
  singleton from "GetExtensions"(domainResource, url)

/*NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the
CQL model info.*/
define function "GetExtensions"(element Element, url String ):
  element.extension E
  	  where E.url = (url)
  		return E

define function "GetExtension"(element Element, url String ):
  singleton from "GetExtensions"(element, url)

/*NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the
CQL model info.*/
define function "GetBaseExtensions"(domainResource DomainResource, url String ):
  domainResource.extension E
  	  where E.url = ('http://hl7.org/fhir/StructureDefinition/' + url)
  		return E

define function "GetBaseExtension"(domainResource DomainResource, url String ):
  singleton from "GetBaseExtensions"(domainResource, url)

/*@description: Returns any base-FHIR extensions defined on the given element with the specified id.
@comment: NOTE: Extensions are not the preferred approach, but are used as a way to access
content that is defined by extensions but not yet surfaced in the CQL model info.*/
define function "BaseExtensions"(element Element, id String ):
  element.extension E
  	  where E.url = ('http://hl7.org/fhir/StructureDefinition/' + id)
  		return E

/*@description: Returns the single base-FHIR extension (if present) on the given element with the specified id.
@comment: This function uses singleton from to ensure that a run-time exception is thrown if there
is more than one extension on the given resource with the specified url.*/
define function "BaseExtension"(element Element, id String ):
  singleton from BaseExtensions(element, id)

/*NOTE: Provenance is not the preferred approach, this is provided only as an illustration
for what using Provenance could look like, and is not a tested pattern*/
define function "GetProvenance"(resource Resource ):
  singleton from ([Provenance: target in resource.id])

define function "GetMedicationCode"(request MedicationRequest ):
  if request.medication is CodeableConcept then
  	  request.medication as CodeableConcept
  	else
  	  (singleton from ([Medication] M where M.id = GetId((request.medication as Reference).reference))).code

/*Given an interval, return true if the interval has a starting boundary specified (i.e. the start of the interval is not null and not the minimum DateTime value)*/
define function "HasStart"(period Interval<DateTime> ):
  not ( start of period is null
      or start of period = minimum DateTime
  )

/*Given an interval, return true if the interval has an ending boundary specified (i.e. the end of the interval is not null and not the maximum DateTime value)*/
define function "HasEnd"(period Interval<DateTime> ):
  not (
    end of period is null
      or
      end of period = maximum DateTime
  )

/*Given an interval, return the ending point if the interval has an ending boundary specified, otherwise, return the starting point*/
define function "Latest"(choice Choice<FHIR.dateTime, FHIR.Period, FHIR.Timing, FHIR.instant, FHIR.string, FHIR.Age, FHIR.Range> ):
  ("Normalize Interval"(choice)) period
    return
      if ( HasEnd(period)) then end of period
      else start of period

/*Given an interval, return the starting point if the interval has a starting boundary specified, otherwise, return the ending point*/
define function "Earliest"(choice Choice<FHIR.dateTime, FHIR.Period, FHIR.Timing, FHIR.instant, FHIR.string, FHIR.Age, FHIR.Range> ):
  ("Normalize Interval"(choice)) period
    return
      if (HasStart(period)) then start of period
      else end of period

