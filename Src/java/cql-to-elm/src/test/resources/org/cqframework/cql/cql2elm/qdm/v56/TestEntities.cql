library TestEntities

using QDM version '5.6'

valueset "Office Visit": 'TBD'
valueset "Inpatient": 'TBD'
valueset "Ambulatory Clinical Practice": 'TBD'
valueset "Ophthalmology": 'TBD'
valueset "Family": 'TBD'
valueset "ED": 'TBD'
valueset "Blood Pressure": 'TBD'
valueset "Diabetic Eye Exam": 'TBD'

context Patient

/*
Specifying Organization for an Encounter Participant

In this example, the eCQM defines a qualifying encounter as performed by an ambulatory clinical practice using the QDM Entity Organization and its type attribute:
 */
define "Qualifying Encounters (1)":
  ["Encounter, Performed": "Office Visit"] Encounter
    where exists (
      Encounter.participant Participant
        where Participant is "Organization"
          and Participant.organizationType in "Ambulatory Clinical Practice"
    )

/*
Specifying a Practitioner as an Encounter Participant

In this example, the eCQM uses the QDM Entity Practitioner and its specialty attribute to define a qualifying encounter as one performed by an ophthalmologist:
 */

define "Qualifying Encounters (2)":
  ["Encounter, Performed": "Office Visit"] Encounter
    where exists (
        Encounter.participant Participant
          where Participant is "Practitioner"
            and Participant.specialty in "Opthalmology"
    )

/*
Specifying an Encounter Organization Identifier

This example shows how to determine that the primary participant (performer) of an inpatient encounter is the same as the primary participant (performer) of an emergency department encounter using the organization entity. The example defines a function that the required identifier is a CCN.
 */

define "Qualifying Encounters (3)":
 ["Encounter, Performed": "Inpatient"] Encounter
   with ["Encounter, Performed": "ED"] ED
    such that ED.relevantPeriod ends 1 hour or less on or before start of Encounter.relevantPeriod
     and CCNOf(ED.participant.identifier) = CCNOf(Encounter.participant.identifier)

define function CCNOf(identifiers List<Identifier>):
  singleton from (identifiers I where I.namingSystem = 'CCN Identifier System' return I)

/*
Specifying a Physical Examination Performed by a Care Partner

This example shows how to determine that a blood pressure examination was performed by a Care Partner (a person that is related to a patient, but who is not the direct target of care). "Family" in this instance would use a value set of potential family members as defined by a measure developer (e.g., mother, father, brother, sister).
 */

define "Family-measured Blood Pressure Exams":
    ["Physical Exam, Performed": "Blood Pressure"] BloodPressure
      where exists (
        BloodPressure.performer Performer
          where Performer is CarePartner
            and Performer.relationship in "Family"
      )

/*
Specifying an Individual Actor is a Member of an Organization

This example defines an organization as the QDM Entity used for the encounter participant similar to the expression in Section 2.6.1. Further, it indicates that the individual who orders an eye examination ("Intervention, Order") using the QDM Entity practitioner and that the practitioner identifier is within the organization that performed the qualifying encounter (see Section 2.6.4). The expression also assures the individual who performs the eye examination ("Intervention, Performed") is also a practitioner whose identifier is within the organization that performed the qualifying encounter. Note that this is a hypothetical example. All the actor identification requirements are optional at the discretion of the measure developer and all require validation that seeking such detail is feasible for implementation.
 */

define "Qualifying Encounters (4)":
  ["Encounter, Performed": "Inpatient"] Encounter
    where exists (
      Encounter.participant Participant
        where Participant is "Organization"
    )

define "Eye Exam Order":
  ["Intervention, Order": "Diabetic Eye Exam"] ExamOrder
    with "Qualifying Encounters (4)" Encounter
      such that exists (
        ExamOrder.requester Requester
          where Requester is Organization
            and Requester.id in Encounter.participant.id
      )

define "Eye Exam Complete":
  ["Intervention, Performed": "Diabetic Eye Exam"] EyeExam
    with "Qualifying Encounters (4)" Encounter
      such that exists (
        EyeExam.performer Performer
          where Performer is Organization
            and Performer.id in Encounter.participant.id
      )
        and exists (
          EyeExam.performer Performer
            where Performer is "Practitioner"
              and Performer.specialty in "Ophthalmology"
        )

