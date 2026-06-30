parameter "Measurement Period" = interval[Date(2014, 1, 1), Date(2015, 1, 1))

define "VTE Diagnosis Valueset" = ValueSet('XYZ')
define "Warfarin Medication Valueset" = ValueSet('XYZ')
define "Inpatient Encounter Valueset" = ValueSet('XYZ')

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

Encounter of "Inpatient" was performed during "Measurement Period"
MedicationTreatment of "Warfarin" was performed
MedicationTreatment of "Parenteral Anticoagulation" was performed

[Encounter, Performed: "Inpatient"] E where E.effectiveTime during "Measurement Period"

// Patients who received warfarin and parenteral anticoagulation:
// • Five or more days, with an INR greater than or equal to 2 prior to discontinuation of parenteral therapy OR
// • Five or more days, with an INR less than 2 and discharged on overlap therapy OR
// • Less than five days and discharged on overlap therapy OR
// • With documentation of reason for discontinuation of parenteral therapy OR
// • With documentation of a reason for no overlap therapy


define "Encounters" = [Encounter: "Inpatient"] E where E.period during "Measurement Period"
define "Warfarin Therapy" = [MedicationAdministration: "Warfarin"]
define "Parenteral Therapy" = [MedicationAdministration: "Parenteral Anticoagulation"]

[Encounter, Performance: "Inpatient"] E return E.id, E.serviceType, E.effectiveTime

define "Encounters with Warfarin and Parenteral Therapies" =
    "Encounters" E
        with "Warfarin Therapy" W such that W.whenGiven starts during E.period
        with "Parenteral Therapy" P such that P.whenGiven starts during E.period

define "Encounters with overlapping Warfarin and Parenteral Therapies" =
    from "Encounters" E,
        "Warfarin Therapy" W,
        "Parenteral Therapy" P
    where W.whenGiven starts during E.period
        and P.whenGiven starts during E.period
        and duration in days of (W.whenGiven intersect P.whenGiven) >= 5

Last([Observation: "INR Value"] I where I.applies during P.whenGiven sort by I.applies)

define "Encounters with appropriate overlap therapy" =
    foreach 
        "Encounters" E, 
        "Warfarin Therapy" W, 
        "Parenteral Therapy" P
    define
        overlapDuration = duration in days of (W.performanceTime intersect P.performanceTime),
        endingINRResult = 
            Last([ObservationResult of "INR Value"] I where I.observedAtTime during P.performanceTime sort by I.observedAtTime)
    where overlapDuration >= 5 and endingINRResult >= 2
        or overlapDuration >= 5 and endingINRResult < 2 and P.performanceTime overlaps after E.effectiveTime
        or overlapDuration < 5 and P.performanceTime overlaps after E.effectiveTime
    return E


define "Encounters with appropriate overlap therapy" =
    "Encounters" E
        combine "Warfarin Therapy" W
        combine "Parenteral Therapy" P
        define overlapDuration = duration in days of (W.performanceTime intersect P.performanceTime)
        define endingINRResult = Last([ObservationResult: "INR Value"] I where I.observedAtTime during P.performanceTime order by I.observedAtTime)
        where (overlapDuration >= 5 and endingINRResult >= 2)
            or (overlapDuration >= 5 and endingINRResult < 2 and P.performanceTime overlaps after E.effectivTime)
            or (overlapDuration < 5 and P.performanceTime overlaps after E.effectiveTime)
        return E

define "Patient has no allergy or intolerance to Penicillin" =
    not exists ([AllergyOrIntolerance: "Penicillin"])

    [Patient has AllergyIntolerance to "Penicillin"]
    [Patient does not have AllergyIntolerance to "Penicillin"]
    [Unknown that Patient has AllergyIntolernace to "Penicillin"]

        
        


define "Encounters and Therapies" =
    [Encounter, Performed: "Inpatient"] E
        return
        {
            encounter: E,
            warfarinTherapies: [MedicationTreatment, Performed: "Warfarin"] W where W.performanceTime during E.effectiveTime,
            parenteralTherapies: [MedicationTreatment, Performed: "Parenteral Anticoagulation"] P where P.performanceTime during E.effectiveTime
        }

define "Encounters with Overlap Therapies" =
    [Encounter, Performed: "Inpatient"] E
        combine [MedicationTreatment, Performed: "Warfarin"] W such that W.performanceTime during E.effectiveTime
        combine [MedicationTreatment, Performed: "Parenteral Anticoagulation"] P such that P.performanceTime during E.effectiveTime
        where W.performanceTime overlaps P.performanceTime
        return 
        { 
            encounter: E, 
            warfarin: W, 
            parenteral: P, 
            overlap: duration in days of W.performanceTime intersect P.performanceTime,
            inrValue: Last([ObservationResult: "INR Value"] I where I.observedAtTime during P.performanceTime sort by I.observedAtTime)
        }



define "Encounters with Overlapping Therapies" =
    "Encounters and Therapies" E
        return
        {
            encounter: E,
            therapies:
                expand
                (
                    E.warfarinTherapies W
                        return 
                        { 
                            E.parenteralTherapies P where W.performanceTime overlaps P.performanceTime 
                                return 
                                { 
                                    warfarin: W, 
                                    parenteral: P, 
                                    overlap: duration in days of W.performanceTime intersect P.performanceTime
                                }
                        }
                )
        }

define "EncountersAndTherapiesAndINR" =
    "EncountersAndTherapies" E
        return
        {
            encounter: E.encounter,
            warfarinTherapies: E.warfarinTherapies,
            parenteralTherapies: E.parenteralTherapies

define "Encounters" =
    [Encounter, Performed: "Inpatient"] E
        combine [MedicationTreatment, Performed: "Warfarin"] W such that W.performanceTime during E.effectiveTime
        combine [MedicationTreatment, Performed: "Parenteral Anticoagulation"] P such that P.performanceTime during E.effectiveTime
        where W.performanceTime overlaps P.performanceTime


define "Warfarin Therapy" =
    [MedicationTreatment, Performed: "Warfarin"] M
        with "Encounters" E such that M.performanceTime during E.effectiveTime

define "Parenteral Therapy" =
    [MedicationTreatment, Performed: "

define "18 or over" =
    "AgeInYearsAt"(start of "Measurement Period") >= 18

define "Comfort Measures Only" =
    exists ([Documentation: "Comfort Measures Only Valueset"] Doc where Doc.documentedOn during "Measurement Period")

define "VTE Diagnosis" =
    exists ([Condition: "VTE Diagnosis Valueset"] overlaps after [Encounter, Performed: "Inpatient Encounter Valueset"])

define "Warfarin Medication" =
    exists ([MedicationTreatment, Performance: "Warfarin Medication Valueset"])

define "Denominator" =
    "18 or over" 
        and "VTE Diagnosis" 
        and "Warfarin Medication"

