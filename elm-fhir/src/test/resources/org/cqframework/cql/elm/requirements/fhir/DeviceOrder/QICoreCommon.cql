library QICoreCommon version '1.5.000'

using QICore version '4.1.1'

include FHIRHelpers version '4.0.1'

codesystem "LOINC": 'http://loinc.org'
codesystem "SNOMEDCT": 'http://snomed.info/sct'
codesystem "ActCode": 'http://terminology.hl7.org/CodeSystem/v3-ActCode'
codesystem "RoleCode": 'http://terminology.hl7.org/CodeSystem/v3-RoleCode'
codesystem "Diagnosis Role": 'http://terminology.hl7.org/CodeSystem/diagnosis-role'
codesystem "RequestIntent": 'http://hl7.org/fhir/request-intent'
codesystem "MedicationRequestCategory": 'http://terminology.hl7.org/CodeSystem/medicationrequest-category'
codesystem "ConditionClinicalStatusCodes": 'http://terminology.hl7.org/CodeSystem/condition-clinical'
codesystem "ConditionVerificationStatusCodes": 'http://terminology.hl7.org/CodeSystem/condition-ver-status'
codesystem "AllergyIntoleranceClinicalStatusCodes": 'http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical'
codesystem "AllergyIntoleranceVerificationStatusCodes": 'http://terminology.hl7.org/CodeSystem/allergyintolerance-verification'
codesystem "ObservationCategoryCodes": 'http://terminology.hl7.org/CodeSystem/observation-category'
codesystem "USCoreObservationCategoryExtensionCodes": 'http://hl7.org/fhir/us/core/CodeSystem/us-core-observation-category'
codesystem "ConditionCategory": 'http://terminology.hl7.org/CodeSystem/condition-category'
codesystem "USCoreConditionCategoryExtensionCodes": 'http://hl7.org/fhir/us/core/CodeSystem/condition-category'

code "Birthdate": '21112-8' from "LOINC" display 'Birth date'
code "Dead": '419099009' from "SNOMEDCT" display 'Dead'
code "ER": 'ER' from "RoleCode" display 'Emergency room'
code "ICU": 'ICU' from "RoleCode" display 'Intensive care unit'
code "Billing": 'billing' from "Diagnosis Role" display 'Billing'

// Encounter Class Codes
code "ambulatory": 'AMB' from ActCode display 'ambulatory'
code "emergency": 'EMER' from ActCode display 'emergency'
code "field": 'FLD' from ActCode display 'field'
code "home health": 'HH' from ActCode display 'home health'
code "inpatient encounter": 'IMP' from ActCode display 'inpatient encounter'
code "inpatient acute": 'ACUTE' from ActCode display 'inpatient acute'
code "inpatient non-acute": 'NONAC' from ActCode display 'inpatient non-acute'
code "observation encounter": 'OBSENC' from ActCode display 'observation encounter'
code "pre-admission": 'PRENC' from ActCode display 'pre-admission'
code "short stay": 'SS' from ActCode display 'short stay'
code "virtual": 'VR' from ActCode display 'VR'

// Condition Category Codes
code "problem-list-item": 'problem-list-item' from "ConditionCategory" display 'Problem List Item'
code "encounter-diagnosis": 'encounter-diagnosis' from "ConditionCategory" display 'Encounter Diagnosis'
code "health-concern": 'health-concern' from "USCoreConditionCategoryExtensionCodes" display 'Health Concern'

// Condition Clinical Status Codes - Consider value sets for these
code "active": 'active' from "ConditionClinicalStatusCodes"
code "recurrence": 'recurrence' from "ConditionClinicalStatusCodes"
code "relapse": 'relapse' from "ConditionClinicalStatusCodes"
code "inactive": 'inactive' from "ConditionClinicalStatusCodes"
code "remission": 'remission' from "ConditionClinicalStatusCodes"
code "resolved": 'resolved' from "ConditionClinicalStatusCodes"

// Condition Verification Status Codes - Consider value sets for these
code "unconfirmed": 'unconfirmed' from ConditionVerificationStatusCodes
code "provisional": 'provisional' from ConditionVerificationStatusCodes
code "differential": 'differential' from ConditionVerificationStatusCodes
code "confirmed": 'confirmed' from ConditionVerificationStatusCodes
code "refuted": 'refuted' from ConditionVerificationStatusCodes
code "entered-in-error": 'entered-in-error' from ConditionVerificationStatusCodes

code "allergy-active": 'active' from "AllergyIntoleranceClinicalStatusCodes"
code "allergy-inactive": 'inactive' from "AllergyIntoleranceClinicalStatusCodes"
code "allergy-resolved": 'resolved' from "AllergyIntoleranceClinicalStatusCodes"

// Allergy/Intolerance Verification Status Codes - Consider value sets for these
code "allergy-unconfirmed": 'unconfirmed' from AllergyIntoleranceVerificationStatusCodes
code "allergy-confirmed": 'confirmed' from AllergyIntoleranceVerificationStatusCodes
code "allergy-refuted": 'refuted' from AllergyIntoleranceVerificationStatusCodes

// MedicationRequest Category Codes
code "Inpatient": 'inpatient' from "MedicationRequestCategory" display 'Inpatient'
code "Outpatient": 'outpatient' from "MedicationRequestCategory" display 'Outpatient'
code "Community": 'community' from "MedicationRequestCategory" display 'Community'
code "Discharge": 'discharge' from "MedicationRequestCategory" display 'Discharge'

// Diagnosis Role Codes
code "AD": 'AD' from "Diagnosis Role" display 'Admission diagnosis'
code "DD": 'DD' from "Diagnosis Role" display 'Discharge diagnosis'
code "CC": 'CC' from "Diagnosis Role" display 'Chief complaint'
code "CM": 'CM' from "Diagnosis Role" display 'Comorbidity diagnosis'
code "pre-op": 'pre-op' from "Diagnosis Role" display 'pre-op diagnosis'
code "post-op": 'post-op' from "Diagnosis Role" display 'post-op diagnosis'
code "billing": 'billing' from "Diagnosis Role" display 'billing diagnosis'

// Observation Category Codes
code "social-history": 'social-history' from "ObservationCategoryCodes" display 'Social History'
code "vital-signs": 'vital-signs' from "ObservationCategoryCodes" display 'Vital Signs'
code "imaging": 'imaging' from "ObservationCategoryCodes" display 'Imaging'
code "laboratory": 'laboratory' from "ObservationCategoryCodes" display 'Laboratory'
code "procedure": 'procedure' from "ObservationCategoryCodes" display 'Procedure'
code "survey": 'survey' from "ObservationCategoryCodes" display 'Survey'
code "exam": 'exam' from "ObservationCategoryCodes" display 'Exam'
code "therapy": 'therapy' from "ObservationCategoryCodes" display 'Therapy'
code "activity": 'activity' from "ObservationCategoryCodes" display 'Activity'
code "clinical-test": 'clinical-test' from "USCoreObservationCategoryExtensionCodes" display 'Clinical Test'

context Patient

/* Candidates for FHIRCommon */

/*
@description: Returns true if the given condition has a clinical status of active, recurrence, or relapse
*/
define fluent function isActive(condition Condition):
  condition.clinicalStatus ~ "active"
    or condition.clinicalStatus ~ "recurrence"
    or condition.clinicalStatus ~ "relapse"

/*
@description: Returns true if the given condition has the given category
*/
define fluent function hasCategory(condition Condition, category Code):
  exists (condition.category C
    where C ~ category
  )

/*
@description: Returns true if the given condition is a problem list item.
*/
define fluent function isProblemListItem(condition Condition):
  exists (condition.category C
    where C ~ "problem-list-item"
  )

/*
@description: Returns true if the given condition is an encounter diagnosis
*/
define fluent function isEncounterDiagnosis(condition Condition):
  exists (condition.category C
    where C ~ "encounter-diagnosis"
  )

/*
@description: Returns true if the given condition is a health concern
*/
define fluent function isHealthConcern(condition Condition):
  exists (condition.category C
    where C ~ "health-concern"
  )

/*
@description: Returns true if the given observation has the given category
*/
define fluent function hasCategory(observation Observation, category Code):
  exists (observation.category C
    where C ~ category
  )

/*
@description: Returns true if the given observation is a social history observation
*/
define fluent function isSocialHistory(observation Observation):
  exists (observation.category C
    where C ~ "social-history"
  )

/*
@description: Returns true if the given observation is a vital sign
*/
define fluent function isVitalSign(observation Observation):
  exists (observation.category C
    where C ~ "vital-signs"
  )

/*
@description: Returns true if the given observation is an imaging observation
*/
define fluent function isImaging(observation Observation):
  exists (observation.category C
    where C ~ "imaging"
  )

/*
@description: Returns true if the given observation is a laboratory observation
*/
define fluent function isLaboratory(observation Observation):
  exists (observation.category C
    where C ~ "laboratory"
  )

/*
@description: REturns true if the given observation is a procedure observation
*/
define fluent function isProcedure(observation Observation):
  exists (observation.category C
    where C ~ "procedure"
  )

/*
@description: Returns true if the given observation is a survey observation
*/
define fluent function isSurvey(observation Observation):
  exists (observation.category C
    where C ~ "survey"
  )

/*
@description: Returns true if the given observation is an exam observation
*/
define fluent function isExam(observation Observation):
  exists (observation.category C
    where C ~ "exam"
  )

/*
@description: Returns true if the given observation is a therapy observation
*/
define fluent function isTherapy(observation Observation):
  exists (observation.category C
    where C ~ "therapy"
  )

/*
@description: Returns true if the given observation is an activity observation
*/
define fluent function isActivity(observation Observation):
  exists (observation.category C
    where C ~ "activity"
  )

/*
@description: Returns true if the given observation is a clinical test result
*/
define fluent function isClinicalTest(observation Observation):
  exists (observation.category C
    where C ~ "clinical-test"
  )

/*
@description: Returns true if the given MedicationRequest has a category of Community
*/
define fluent function isCommunity(medicationRequest MedicationRequest):
  exists (medicationRequest.category C
    where C ~ Community
  )

/*
@description: Returns true if the given MedicationRequest has a category of Community
*/
define fluent function isCommunity(medicationRequest MedicationNotRequested):
  exists (medicationRequest.category C
    where C ~ Community
  )

/*
@description: Returns true if the given MedicationRequest has a category of Discharge
*/
define fluent function isDischarge(medicationRequest MedicationRequest):
  exists (medicationRequest.category C
    where C ~ Discharge
  )

/*
@description: Returns true if the given MedicationRequest has a category of Discharge
*/
define fluent function isDischarge(medicationRequest MedicationNotRequested):
  exists (medicationRequest.category C
    where C ~ Discharge
  )

/*
@description: Returns true if the given DeviceRequest is a negation (i.e. do not perform this order)
*/
define fluent function doNotPerform(deviceRequest DeviceRequest):
  singleton from (
    deviceRequest.modifierExtension E
      where E.url = 'http://hl7.org/fhir/us/qicore/StructureDefinition/qicore-doNotPerform'
      return E.value as Boolean
  )

/*
@description: Normalizes a value that is a choice of timing-valued types to an equivalent interval
@comment: Normalizes a choice type of DateTime, Quanitty, Interval<DateTime>, or Interval<Quantity> types
to an equivalent interval. This selection of choice types is a superset of the majority of choice types that are used as possible
representations for timing-valued elements in QICore, allowing this function to be used across any resource.
The input can be provided as a DateTime, Quantity, Interval<DateTime> or Interval<Quantity>.
The intent of this function is to provide a clear and concise mechanism to treat single
elements that have multiple possible representations as intervals so that logic doesn't have to account
for the variability. More complex calculations (such as medication request period or dispense period
calculation) need specific guidance and consideration. That guidance may make use of this function, but
the focus of this function is on single element calculations where the semantics are unambiguous.
If the input is a DateTime, the result a DateTime Interval beginning and ending on that DateTime.
If the input is a Quantity, the quantity is expected to be a calendar-duration interpreted as an Age,
and the result is a DateTime Interval beginning on the Date the patient turned that age and ending immediately before one year later.
If the input is a DateTime Interval, the result is the input.
If the input is a Quantity Interval, the quantities are expected to be calendar-durations interpreted as an Age, and the result
is a DateTime Interval beginning on the date the patient turned the age given as the start of the quantity interval, and ending
immediately before one year later than the date the patient turned the age given as the end of the quantity interval.
If the input is a Timing, an error will be thrown indicating that Timing calculations are not implemented.
Any other input will reslt in a null DateTime Interval
@deprecated: This function is deprecated. Use the fluent function `toInterval()` instead
*/
define function ToInterval(choice Choice<DateTime, Quantity, Interval<DateTime>, Interval<Quantity>, Timing>):
  case
	  when choice is DateTime then
    	Interval[choice as DateTime, choice as DateTime]
		when choice is Interval<DateTime> then
  		choice as Interval<DateTime>
		when choice is Quantity then
		  Interval[Patient.birthDate + (choice as Quantity),
			  Patient.birthDate + (choice as Quantity) + 1 year)
		when choice is Interval<Quantity> then
		  Interval[Patient.birthDate + (choice.low as Quantity),
			  Patient.birthDate + (choice.high as Quantity) + 1 year)
		when choice is Timing then
      Message(null, true, 'NOT_IMPLEMENTED', 'Error', 'Calculation of an interval from a Timing value is not supported') as Interval<DateTime>
		else
			null as Interval<DateTime>
	end

/*
@description: Normalizes a value that is a choice of timing-valued types to an equivalent interval
@comment: Normalizes a choice type of DateTime, Quanitty, Interval<DateTime>, or Interval<Quantity> types
to an equivalent interval. This selection of choice types is a superset of the majority of choice types that are used as possible
representations for timing-valued elements in QICore, allowing this function to be used across any resource.
The input can be provided as a DateTime, Quantity, Interval<DateTime> or Interval<Quantity>.
The intent of this function is to provide a clear and concise mechanism to treat single
elements that have multiple possible representations as intervals so that logic doesn't have to account
for the variability. More complex calculations (such as medication request period or dispense period
calculation) need specific guidance and consideration. That guidance may make use of this function, but
the focus of this function is on single element calculations where the semantics are unambiguous.
If the input is a DateTime, the result a DateTime Interval beginning and ending on that DateTime.
If the input is a Quantity, the quantity is expected to be a calendar-duration interpreted as an Age,
and the result is a DateTime Interval beginning on the Date the patient turned that age and ending immediately before one year later.
If the input is a DateTime Interval, the result is the input.
If the input is a Quantity Interval, the quantities are expected to be calendar-durations interpreted as an Age, and the result
is a DateTime Interval beginning on the date the patient turned the age given as the start of the quantity interval, and ending
immediately before one year later than the date the patient turned the age given as the end of the quantity interval.
If the input is a Timing, an error will be thrown indicating that Timing calculations are not implemented.
Any other input will reslt in a null DateTime Interval
*/
define fluent function toInterval(choice Choice<DateTime, Quantity, Interval<DateTime>, Interval<Quantity>, Timing>):
  case
	  when choice is DateTime then
    	Interval[choice as DateTime, choice as DateTime]
		when choice is Interval<DateTime> then
  		choice as Interval<DateTime>
		when choice is Quantity then
		  Interval[Patient.birthDate + (choice as Quantity),
			  Patient.birthDate + (choice as Quantity) + 1 year)
		when choice is Interval<Quantity> then
		  Interval[Patient.birthDate + (choice.low as Quantity),
			  Patient.birthDate + (choice.high as Quantity) + 1 year)
		when choice is Timing then
      Message(null, true, 'NOT_IMPLEMENTED', 'Error', 'Calculation of an interval from a Timing value is not supported') as Interval<DateTime>
		else
			null as Interval<DateTime>
	end

/*
@description: Returns an interval representing the normalized abatement of a given Condition.
@comment: If the abatement element of the Condition is represented as a DateTime, the result
is an interval beginning and ending on that DateTime.
If the abatement is represented as a Quantity, the quantity is expected to be a calendar-duration and is interpreted as the age of the patient. The
result is an interval from the date the patient turned that age to immediately before one year later.
If the abatement is represented as a Quantity Interval, the quantities are expected to be calendar-durations and are interpreted as an age range during
which the abatement occurred. The result is an interval from the date the patient turned the starting age of the quantity interval, and ending immediately
before one year later than the date the patient turned the ending age of the quantity interval.
@deprecated: This function is deprecated. Use the fluent function `abatementInterval()` instead.
*/
define function ToAbatementInterval(condition Condition):
	if condition.abatement is DateTime then
	  Interval[condition.abatement as DateTime, condition.abatement as DateTime]
	else if condition.abatement is Quantity then
		Interval[Patient.birthDate + (condition.abatement as Quantity),
			Patient.birthDate + (condition.abatement as Quantity) + 1 year)
	else if condition.abatement is Interval<Quantity> then
	  Interval[Patient.birthDate + (condition.abatement.low as Quantity),
		  Patient.birthDate + (condition.abatement.high as Quantity) + 1 year)
	else if condition.abatement is Interval<DateTime> then
	  Interval[condition.abatement.low, condition.abatement.high)
	else null as Interval<DateTime>

/*
@description: Returns an interval representing the normalized abatement of a given Condition.
@comment: If the abatement element of the Condition is represented as a DateTime, the result
is an interval beginning and ending on that DateTime.
If the abatement is represented as a Quantity, the quantity is expected to be a calendar-duration and is interpreted as the age of the patient. The
result is an interval from the date the patient turned that age to immediately before one year later.
If the abatement is represented as a Quantity Interval, the quantities are expected to be calendar-durations and are interpreted as an age range during
which the abatement occurred. The result is an interval from the date the patient turned the starting age of the quantity interval, and ending immediately
before one year later than the date the patient turned the ending age of the quantity interval.
*/
define fluent function abatementInterval(condition Condition):
	if condition.abatement is DateTime then
	  Interval[condition.abatement as DateTime, condition.abatement as DateTime]
	else if condition.abatement is Quantity then
		Interval[Patient.birthDate + (condition.abatement as Quantity),
			Patient.birthDate + (condition.abatement as Quantity) + 1 year)
	else if condition.abatement is Interval<Quantity> then
	  Interval[Patient.birthDate + (condition.abatement.low as Quantity),
		  Patient.birthDate + (condition.abatement.high as Quantity) + 1 year)
	else if condition.abatement is Interval<DateTime> then
	  Interval[condition.abatement.low, condition.abatement.high)
	else null as Interval<DateTime>

/*
@description: Returns an interval representing the normalized prevalence period of a given Condition.
@comment: Uses the ToInterval and ToAbatementInterval functions to determine the widest potential interval from
onset to abatement as specified in the given Condition. If the condition is active, or has an abatement date the resulting
interval will have a closed ending boundary. Otherwise, the resulting interval will have an open ending boundary.
@deprecated: This function is deprecated. Use the `prevalenceInterval()` fluent function instead
*/
define function ToPrevalenceInterval(condition Condition):
if condition.clinicalStatus ~ "active"
  or condition.clinicalStatus ~ "recurrence"
  or condition.clinicalStatus ~ "relapse" then
  Interval[start of ToInterval(condition.onset), end of ToAbatementInterval(condition)]
else
  (end of ToAbatementInterval(condition)) abatementDate
    return if abatementDate is null then
      Interval[start of ToInterval(condition.onset), abatementDate)
    else
      Interval[start of ToInterval(condition.onset), abatementDate]

/*
@description: Returns an interval representing the normalized prevalence period of a given Condition.
@comment: Uses the ToInterval and ToAbatementInterval functions to determine the widest potential interval from
onset to abatement as specified in the given Condition. If the condition is active, or has an abatement date the resulting
interval will have a closed ending boundary. Otherwise, the resulting interval will have an open ending boundary.
*/
define fluent function prevalenceInterval(condition Condition):
if condition.clinicalStatus ~ "active"
  or condition.clinicalStatus ~ "recurrence"
  or condition.clinicalStatus ~ "relapse" then
  Interval[start of condition.onset.toInterval(), end of condition.abatementInterval()]
else
  (end of ToAbatementInterval(condition)) abatementDate
    return if abatementDate is null then
      Interval[start of ToInterval(condition.onset), abatementDate)
    else
      Interval[start of ToInterval(condition.onset), abatementDate]

/*
@description: Returns the tail of the given uri (i.e. everything after the last slash in the URI).
@comment: This function can be used to determine the logical id of a given resource. It can be used in
a single-server environment to trace references. However, this function does not attempt to resolve
or distinguish the base of the given url, and so cannot be used safely in multi-server environments.
@deprecated: This function is deprecated. Use the fluent function `getId()` instead
*/
define function GetId(uri String ):
  Last(Split(uri, '/'))

/*
@description: Returns the tail of the given uri (i.e. everything after the last slash in the URI).
@comment: This function can be used to determine the logical id of a given resource. It can be used in
a single-server environment to trace references. However, this function does not attempt to resolve
or distinguish the base of the given url, and so cannot be used safely in multi-server environments.
*/
define fluent function getId(uri String):
  Last(Split(uri, '/'))

/*
@description: Given an interval, return true if the interval has a starting boundary specified
(i.e. the start of the interval is not null and not the minimum DateTime value)
@deprecated: This function is deprecated. Uee the fluent function `hasStart()` instead
*/
define function "HasStart"(period Interval<DateTime> ):
  not ( start of period is null
      or start of period = minimum DateTime
  )

/*
@description: Given an interval, return true if the interval has a starting boundary specified
(i.e. the start of the interval is not null and not the minimum DateTime value)
*/
define fluent function hasStart(period Interval<DateTime> ):
  not ( start of period is null
      or start of period = minimum DateTime
  )

/*
@description: Given an interval, returns true if the interval has an ending boundary specified
(i.e. the end of the interval is not null and not the maximum DateTime value)
@deprecated: This function is deprecated. Use the fluent function `hasEnd()` instead
*/
define function "HasEnd"(period Interval<DateTime> ):
  not (
    end of period is null
      or end of period = maximum DateTime
  )

/*
@description: Given an interval, returns true if the interval has an ending boundary specified
(i.e. the end of the interval is not null and not the maximum DateTime value)
*/
define fluent function hasEnd(period Interval<DateTime> ):
  not (
    end of period is null
      or end of period = maximum DateTime
  )

/*
@description: Given an interval, returns the ending point if the interval has an ending boundary specified,
otherwise, returns the starting point
@deprecated: This function is deprecated. Use the fluent function `latest()` instead
*/
define function "Latest"(choice Choice<DateTime, Quantity, Interval<DateTime>, Interval<Quantity>> ):
  (choice.toInterval()) period
    return
      if (HasEnd(period)) then end of period
      else start of period

/*
@description: Given an interval, returns the ending point if the interval has an ending boundary specified,
otherwise, returns the starting point
*/
define fluent function latest(choice Choice<DateTime, Quantity, Interval<DateTime>, Interval<Quantity>> ):
  (choice.toInterval()) period
    return
      if (period."hasEnd"()) then end of period
      else start of period

/*
@description: Given an interval, return the starting point if the interval has a starting boundary specified,
otherwise, return the ending point
@deprecated: This function is deprecated. Use the fluent function `earliest()` instead
*/
define function "Earliest"(choice Choice<DateTime, Quantity, Interval<DateTime>, Interval<Quantity>> ):
  (choice.toInterval()) period
    return
      if (HasStart(period)) then start of period
      else end of period

/*
@description: Given an interval, return the starting point if the interval has a starting boundary specified,
otherwise, return the ending point
*/
define fluent function earliest(choice Choice<DateTime, Quantity, Interval<DateTime>, Interval<Quantity>> ):
  (choice.toInterval()) period
    return
      if (period."hasStart"()) then start of period
      else end of period

/*
@description: Creates a list of integers from 1 to how many days are in the interval. Note, this wont create an index for
the final day if it is less than 24 hours. This also includes the first 24 hour period.
@deprecated: This function is deprecated. Use the fluent function `toDayNumbers()` instead
*/
define function "Interval To Day Numbers"(Period Interval<DateTime>):
  ( expand { Interval[1, duration in days between start of Period and end of Period]} ) DayNumber
    return end of DayNumber

/*
@description: Creates a list of integers from 1 to how many days are in the interval. Note, this wont create an index for
the final day if it is less than 24 hours. This also includes the first 24 hour period.
*/
define fluent function toDayNumbers(Period Interval<DateTime>):
  ( expand { Interval[1, duration in days between start of Period and end of Period]} ) DayNumber
    return end of DayNumber

/*
@description: Creates a list of 24 hour long intervals in an interval paired with the index (1 indexed) to which 24 hour interval it is.
Note that the result will include intervals that are closed at the beginning and open at the end
@deprecated: This function is deprecated. Use the fluent function `daysInPeriod()` instead
*/
define function "Days In Period"(Period Interval<DateTime>):
  ( "Interval To Day Numbers"(Period)) DayIndex
    let startPeriod: start of Period + (24 hours * (DayIndex - 1)),
    endPeriod: if (hours between startPeriod and end of Period < 24) then startPeriod
      else start of Period + (24 hours * DayIndex)
    return Tuple {
      dayIndex: DayIndex,
      dayPeriod: Interval[startPeriod, endPeriod)
    }

/*
@description: Creates a list of 24 hour long intervals in an interval paired with the index (1 indexed) to which 24 hour interval it is.
Note that the result will include intervals that are closed at the beginning and open at the end
*/
define fluent function daysInPeriod(Period Interval<DateTime>):
  ( "Interval To Day Numbers"(Period)) DayIndex
    let startPeriod: start of Period + (24 hours * (DayIndex - 1)),
    endPeriod: if (hours between startPeriod and end of Period < 24) then startPeriod
      else start of Period + (24 hours * DayIndex)
    return Tuple {
      dayIndex: DayIndex,
      dayPeriod: Interval[startPeriod, endPeriod)
    }