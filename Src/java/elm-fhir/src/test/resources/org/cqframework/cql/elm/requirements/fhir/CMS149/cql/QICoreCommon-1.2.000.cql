library QICoreCommon version '1.2.000'

using QICore version '4.1.1'

include FHIRHelpers version '4.1.000'

codesystem "LOINC": 'http://loinc.org'
codesystem "SNOMEDCT": 'http://snomed.info/sct'
codesystem "ICD10CM": 'http://hl7.org/fhir/sid/icd-10-cm'
codesystem "RoleCode": 'http://terminology.hl7.org/CodeSystem/v3-RoleCode'
codesystem "Diagnosis Role": 'http://terminology.hl7.org/CodeSystem/diagnosis-role'
codesystem "RequestIntent": 'http://terminology.hl7.org/CodeSystem/request-intent'
codesystem "MedicationRequestCategory": 'http://terminology.hl7.org/CodeSystem/medicationrequest-category'
codesystem "ConditionClinicalStatusCodes": 'http://terminology.hl7.org/CodeSystem/condition-clinical'
codesystem "ConditionVerificationStatusCodes": 'http://terminology.hl7.org/CodeSystem/condition-ver-status'
codesystem "AllergyIntoleranceClinicalStatusCodes": 'http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical'
codesystem "AllergyIntoleranceVerificationStatusCodes": 'http://terminology.hl7.org/CodeSystem/allergyintolerance-verification'
codesystem "ConditionCategoryCodes": 'http://terminology.hl7.org/CodeSystem/condition-category'
codesystem "ObservationCategoryCodes": 'http://terminology.hl7.org/CodeSystem/observation-category'

//Currently unresolveable in VSAC
//valueset "Active Condition": 'http://fhir.org/guides/cqf/common/ValueSet/active-condition'
//valueset "Inactive Condition": 'http://fhir.org/guides/cqf/common/ValueSet/inactive-condition'

code "Birthdate": '21112-8' from "LOINC" display 'Birth date'
code "Dead": '419099009' from "SNOMEDCT" display 'Dead'
code "ER": 'ER' from "RoleCode" display 'Emergency room'
code "ICU": 'ICU' from "RoleCode" display 'Intensive care unit'
code "Billing": 'billing' from "Diagnosis Role" display 'Billing'

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

// Condition Category Codes
code "problem-list-item": 'problem-list-item' from "ConditionCategoryCodes" display 'Problem List Item'
code "encounter-diagnosis": 'encounter-diagnosis' from "ConditionCategoryCodes" display 'Encounter Diagnosis'

context Patient

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
Any other input will reslt in a null DateTime Interval
*/
define function ToInterval(choice Choice<DateTime, Quantity, Interval<DateTime>, Interval<Quantity>>):
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
Any other input will reslt in a null DateTime Interval
*/
define fluent function toInterval(choice Choice<DateTime, Quantity, Interval<DateTime>, Interval<Quantity>>):
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
define fluent function toAbatementInterval(condition Condition):
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
onset to abatement as specified in the given Condition. If the condition is active, the resulting interval will have
a closed ending boundary. If the condition is not active, the resulting interval will have an open ending boundary.
*/
define function ToPrevalenceInterval(condition Condition):
if condition.clinicalStatus ~ "active"
  or condition.clinicalStatus ~ "recurrence"
  or condition.clinicalStatus ~ "relapse" then
  Interval[start of ToInterval(condition.onset), end of ToAbatementInterval(condition)]
else
  Interval[start of ToInterval(condition.onset), end of ToAbatementInterval(condition))

/*
@description: Returns an interval representing the normalized prevalence period of a given Condition.
@comment: Uses the ToInterval and ToAbatementInterval functions to determine the widest potential interval from
onset to abatement as specified in the given Condition. If the condition is active, the resulting interval will have
a closed ending boundary. If the condition is not active, the resulting interval will have an open ending boundary.
*/
define fluent function toPrevalenceInterval(condition Condition):
if condition.clinicalStatus ~ "active"
  or condition.clinicalStatus ~ "recurrence"
  or condition.clinicalStatus ~ "relapse" then
  Interval[start of ToInterval(condition.onset), end of ToAbatementInterval(condition)]
else
  Interval[start of ToInterval(condition.onset), end of ToAbatementInterval(condition))

/*
@description: Returns the tail of the given uri (i.e. everything after the last slash in the URI).
@comment: This function can be used to determine the logical id of a given resource. It can be used in
a single-server environment to trace references. However, this function does not attempt to resolve
or distinguish the base of the given url, and so cannot be used safely in multi-server environments.
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
      if (HasEnd(period)) then end of period
      else start of period

/*
@description: Given an interval, return the starting point if the interval has a starting boundary specified, 
otherwise, return the ending point
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
      if (HasStart(period)) then start of period
      else end of period

/*
@description: Creates a list of integers from 1 to how many days are in the interval. Note, this wont create an index for
the final day if it is less than 24 hours. This also includes the first 24 hour period.
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