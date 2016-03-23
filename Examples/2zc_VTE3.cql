library Guideline_2zc_VTE3

using QUICK

valueset "VTE Diagnosis Valueset": 'XYZ'
valueset "Warfarin Medication Valueset": 'XYZ'
valueset "Inpatient Encounter Valueset": 'XYZ'

parameter Measurement Period default Interval[@2014-01-01, @2015-01-01)

// Denominator
//Encounters
  //where the patient is 18 or over
  //has no documentation of Comfort Measures Only
  //is not enrolled in a clinical trial
  //was not discharged to a health care facility for hospice care
  //was not discharged to home for hospice care
  //did not expire
  //did not leave against medical advice
  //was not discharged to another hospital
  //who were discharged with a diagnosis code of VTE
  //and were administered warfarin during the encounter
  //and did not have absence of VTE confirmed by diagnostic testing

// Numerator
// Encounters
    // where the overlapping duration of warfarin and parenteral anticoagulation was >= 5 days
        // and the INR ending parenteral therapy >= 2
            // or the INR ending parenteral therapy < 2
                // and the patient was discharged with overlap therapy
    // or there is documentation of reason for discontinuation of parenteral therapy
    // or there is documentation of reason for no overlap therapy

// Patients who received warfarin and parenteral anticoagulation:
// * Five or more days, with an INR greater than or equal to 2 prior to discontinuation of parenteral therapy OR
// * Five or more days, with an INR less than 2 and discharged on overlap therapy OR
// * Less than five days and discharged on overlap therapy OR
// * With documentation of reason for discontinuation of parenteral therapy OR
// * With documentation of a reason for no overlap therapy

context Patient

define "$Encounters": ["Encounter": "Inpatient"] E where E."period" during Measurement Period
define "$Warfarin Therapy": ["MedicationAdministration": "Warfarin"]
define "$Parenteral Therapy": ["MedicationAdministration": "Parenteral Anticoagulation"]

define "$Encounters with Warfarin and Parenteral Therapies":
    "$Encounters" E
        with "$Warfarin Therapy" W such that W."whenGiven" starts during E."period"
        with "$Parenteral Therapy" P such that P."whenGiven" starts during E."period"

define "$Encounters with overlapping Warfarin and Parenteral Therapies":
    from "$Encounters" E,
        "$Warfarin Therapy" W,
        "$Parenteral Therapy" P
    where W."whenGiven" starts during E."period"
        and P."whenGiven" starts during E."period"
        and duration in days of (W."whenGiven" intersect P."whenGiven") >= 5

define "$Encounters with appropriate overlap therapy":
    from
        "$Encounters" E,
        "$Warfarin Therapy" W,
        "$Parenteral Therapy" P
    define
        "$overlapDuration": duration in days of (W."performanceTime" intersect P."performanceTime"),
        "$endingINRResult":
            Last(["ObservationResult": "INR Value"] I where I."observedAtTime" during P."performanceTime" sort by I."observedAtTime")
    where "$overlapDuration" >= 5 and "$endingINRResult" >= 2
        or "$overlapDuration" >= 5 and "$endingINRResult" < 2 and P."performanceTime" overlaps after E."effectiveTime"
        or "$overlapDuration" < 5 and P."performanceTime" overlaps after E."effectiveTime"
    return E

define "$Patient has no allergy or intolerance to Penicillin":
    not exists (["AllergyOrIntolerance": "Penicillin"])

define "$18 or over":
    AgeInYearsAt(start of Measurement Period) >= 18

define "$Comfort Measures Only":
    exists (["Documentation": "Comfort Measures Only Valueset"] Doc where Doc."documentedOn" during Measurement Period)

//NOTE: I don't think "Encounter, Performed" is a QUICK datatype, but I'm not certain.

define "$VTE Diagnosis":
    exists (["Condition": "VTE Diagnosis Valueset"] overlaps after ["Encounter, Performed": "Inpatient Encounter Valueset"])

define "$Denominator":
    "$18 or over"
        and "$VTE Diagnosis"
        and "$Warfarin Medication"
        // ...

// TODO: Numerator
