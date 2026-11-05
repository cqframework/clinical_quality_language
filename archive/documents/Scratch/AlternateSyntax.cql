Patient is allergic to penicillin
Patient is not allergic to penicillin

		[subject has|does not have] <Act> [<concept>] was|is [not] <Modality> //[for <reason>] [during <expression>] [with <expression>]
		<Observable> [<concept>] was|is [not] <Modality> //[where <condition>]
		Unknown that <Topic> [<concept>] was|is <Modality>

"Imaging Procedure" was performed for "Headache" during "Measurement Period"

Patient was taking Drug


QUICK Acts
CareProgramParticipation
    CareProgramParticipation [of programConcept] is|was [not] proposed [for reason]
    CareProgramParticipation [of programConcept] is|was [not] ordered [for reason]
    CareProgramParticipation [of programConcept] is|was [not] planned [for reason]
    CareProgramParticipation [of programConcept] is|was [not] performed [for reason]
Communication
    Communication [of concept] [to recipient] is|was [not] <modality> [for reason]
CompositeIntravenousMedicationAdministration
DeviceUse
DiagnosticImaging
Diet
Encounter
Goal
Hospitalization
Immunization
LaboratoryTest
MedicationTreatment
PatientControlledAnalgesia
Procedure
Radiotherapy
RespiratoryCare

Encounter
    Encounter of "Inpatient Encounters" performed
    Encounter of "Inpatient Encounters" ordered
    Encounter of "Inpatient Encounters" proposed
Procedure
LaboratoryTest
MedicationTreatment

QUICK Act Modalities
Order
Performance
Plan
Proposal
ProposalAgainst

QUICK Observables
AdverseReaction - [Unknown that] Patient had|did not have AdverseReation to [substance]
AllergyIntolerance - [Unknown that] Patient has [no known] AllergyIntolerance [to substance]
CareExperience - Patient had CareExperience [of concept]
Condition - [Unknown that] Patient has|does not have Condition [of concept]
Contraindication - Patient has|does not have Contraindication [to concept]
Exposure - [Unknown that] Patient had|did not have Exposure [to substance]
FamilyHistory - [Unknown that] Patient has|does not have FamilyHistory [of concept]
Inference - ??
ManifestedSymptom - [Unknown that] Patient has|does not have ManifestedSymptom of concept
MicrobiologySensitivityResult - [Unknown that] Patient has|does not have MicrobiologySensitivtyResult [for concept]
ObservationResult - [Unknown that] Patient has|does not have ObservationResult [for concept]
ObservationResultGroup - ??
OrganismSensitivity - [Unknown that] Patient has|does not have OrganismSensitivty to concept
Prediction - Patient has|does not have Prediction [of concept]
RelatedObservation - Patient has related observation [of concept]

"Diabetes" condition

[Condition: "Diabetes"] D where D.effectiveTime during "Measurement Period"

QUICK Observable Modalities
Observation

QUICK Occurrences
Occurrence
NonOccurrence
UnknownOccurrence


// Chart-based measure:

Included Populations: Patients who received warfarin and parenteral anticoagulation:
• Five or more days, with an INR greater than or equal to 2 prior to discontinuation of parenteral therapy OR
• Five or more days, with an INR less than 2 and discharged on overlap therapy OR
• Less than five days and discharged on overlap therapy OR
• With documentation of reason for discontinuation of parenteral therapy OR
• With documentation of a reason for no overlap therapy
Excluded Populations: None
Data Elements:
• INR Value
• Overlap Therapy
• Overlap Therapy Start Date
• Parenteral Anticoagulant End Date
• Parenteral Anticoagulant Prescribed at Discharge
• Reason for Discontinuation of Parenteral Anticoagulation Therapy
• Reason for No Overlap Therapy

define "Encounters" = [Encounter of "Inpatient" was performed during "Measurement Period"]
define "Warfarin Therapy" = [MedicationTreatment of "Warfarin" was performed]
define "Parenteral Therapy" = [MedicationTreatment of "Parenteral Anticoagulation" was performed]

define "Encounters with appropriate overlap therapy" =
    using 
        "Encounters" E, 
        "Warfarin Therapy" W, 
        "Parenteral Therapy" P
    compute
        overlapDuration: duration in days of (W.performanceTime intersect P.performanceTime),
        endingINRResult: Last([ObservationResult of "INR Value"] I where I.observedAtTime during P.performanceTime order by I.observedAtTime)
    where overlapDuration >= 5 and endingINRResult >= 2
        or overlapDuration >= 5 and endingINRResult < 2 and P.performanceTime overlaps after E.effectiveTime
        or overlapDuration < 5 and P.performanceTime overlaps after E.effectiveTime
    return E


define "Encounters" = [Encounter, Performance: "Inpatient"] E where E.effectiveTime during "Measurement Period"
define "Warfarin Therapy" = [MedicationTreatment, Performance: "Warfarin"]
define "Parenteral Therapy" = [MedicationTreatment, Performance: "Parenteral Anticoagulation"]

define "Encounters with appropriate overlap therapy" =
    foreach
        "Encounters" E, 
        "Warfarin Therapy" W, 
        "Parenteral Therapy" P
    define
        overlapDuration = duration in days of (W.performanceTime intersect P.performanceTime),
        endingINRResult = Last([ObservationResult: "INR Value"] I where I.observedAtTime during P.performanceTime order by I.observedAtTime)
    where overlapDuration >= 5 and endingINRResult >= 2
        or overlapDuration >= 5 and endingINRResult < 2 and P.performanceTime overlaps after E.effectiveTime
        or overlapDuration < 5 and P.performanceTime overlaps after E.effectiveTime
    return E

    return tuple { id: E.id, serviceType: E.serviceType }
    return tuple { E: E, W: W, P: P }
    return tuple { E_Id: E.Id, ... }

// Chlamydia Screening

define "Conditions" = [Condition] C where C.name in "Conditions of interest"
define "Conditions" = [Condition] C where exists (C.name X where X.coding in "Conditions of interest")
define "Conditions" = [Condition] C where exists (C.name X where X.coding.isPrimary and X.coding in "Conditions of interest") or

define "Conditions" = [Condition] C where C.name in "Conditions of interest"


