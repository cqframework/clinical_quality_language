library MedianAdmitDecisionTimetoEDDepartureTimeforAdmittedPatients version '9.1.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global

valueset "Admit Inpatient": 'urn:oid:2.16.840.1.113762.1.4.1111.164'
valueset "Decision to Admit to Hospital Inpatient": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.295'
valueset "Emergency Department Evaluation": 'urn:oid:2.16.840.1.113762.1.4.1111.163'
valueset "Emergency Department Visit": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.292'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Hospital Settings": 'urn:oid:2.16.840.1.113762.1.4.1111.126'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Psychiatric/Mental Health Diagnosis": 'urn:oid:2.16.840.1.113883.3.117.1.7.1.299'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'

context Patient

define "Initial Population":
  "ED Encounter with Decision to Admit"

define "Measure Population":
  "Initial Population"

define "SDE Ethnicity":
  ["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
  ["Patient Characteristic Payer": "Payer"]

define "SDE Race":
  ["Patient Characteristic Race": "Race"]

define "SDE Sex":
  ["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Stratification 2":
  /*Patient encounters with a principal diagnosis (rank=1) of "Psychiatric/Mental Health Diagnosis*/
  Global."Inpatient Encounter" EncounterInpatient
    where exists ( EncounterInpatient.diagnoses Diagnosis
        where Diagnosis.code in "Psychiatric/Mental Health Diagnosis"
          and Diagnosis.rank = 1
    )

define "Stratification 1":
  /*Patient encounters without a principal diagnosis (rank=1) of "Psychiatric/Mental Health Diagnosis*/
  Global."Inpatient Encounter" EncounterInpatient
    where not exists ( EncounterInpatient.diagnoses Diagnosis
        where Diagnosis.code in "Psychiatric/Mental Health Diagnosis"
          and Diagnosis.rank = 1
    )

define "Measure Population Exclusions":
  /*Exclude the most recent ED encounter (LastEDVisit) that occurred within an hour of the inpatient admission with ED admission source in "Hospital Setting" (any different facility- by location or CCN )*/
  Global."Inpatient Encounter" EncounterInpatient
    where "LastEDEncounter"(EncounterInpatient).admissionSource in "Hospital Settings"

define "ED Encounter with Decision to Admit":
  /*Constrains the inpatient encounter to having an ED visit with a decision to admit (assessment or order) to inpatient and ED facility location period is not null*/
  from
    Global."Inpatient Encounter" EncounterInpatient
    let LastEDVisit: LastEDEncounter(EncounterInpatient)
    where ( "AdmitDecisionUsingAssessmentDuringLastEDBeforeDeparture"(EncounterInpatient).relevantDatetime during LastEDVisit.relevantPeriod
        or "AdmitDecisionUsingEncounterOrderDuringLastEDandBeforeDeparture"(EncounterInpatient).authorDatetime during LastEDVisit.relevantPeriod
    )
      and exists ( LastEDVisit.facilityLocations Location
          where Location.code in "Emergency Department Visit"
            and 
            end of Location.locationPeriod != maximum DateTime
      )

define function "AdmitDecisionUsingAssessmentDuringLastEDBeforeDeparture"(EncounterInpatient "Encounter, Performed"):
  /*Captures the decision to admit assessment, time, and result that was performed during the last ED visit*/
  Last(["Assessment, Performed": "Emergency Department Evaluation"] EDEvaluation
      let LastEDVisit: "LastEDEncounter"(EncounterInpatient)
      where EDEvaluation.relevantDatetime during LastEDVisit.relevantPeriod
        and EDEvaluation.result in "Admit Inpatient"
        and EDEvaluation.relevantDatetime before or on "EDDepartureTime"(LastEDVisit)
      sort by relevantDatetime
  )

define function "LastEDEncounter"(EncounterInpatient "Encounter, Performed"):
  /*The most recent (last) ED encounter that is within an hour of an inpatient encounter*/
  Last(Global."ED Encounter" EDVisit
      where EDVisit.relevantPeriod ends 1 hour or less before or on start of EncounterInpatient.relevantPeriod
      sort by 
      end of relevantPeriod ascending
  )

define function "MeasureObservation"(EncounterInpatient "Encounter, Performed"):
  /*The duration from the Decision to Admit (order or assessment) to the departure from the Emergency Department*/
  duration in minutes of Interval[Coalesce("AdmitDecisionUsingAssessmentDuringLastEDBeforeDeparture"(EncounterInpatient).relevantDatetime, "AdmitDecisionUsingEncounterOrderDuringLastEDandBeforeDeparture"(EncounterInpatient).authorDatetime), "EDDepartureTime"("LastEDEncounter"(EncounterInpatient))]

define function "AdmitDecisionUsingEncounterOrderDuringLastEDandBeforeDeparture"(EncounterInpatient "Encounter, Performed"):
  /*Captures the decision to admit order and time that occured during the last ED visit*/
  Last(["Encounter, Order": "Decision to Admit to Hospital Inpatient"] AdmitOrder
      let LastEDVisit: "LastEDEncounter"(EncounterInpatient)
      where AdmitOrder.authorDatetime during LastEDVisit.relevantPeriod
        and AdmitOrder.authorDatetime before or on "EDDepartureTime"(LastEDVisit)
      sort by authorDatetime
  )

define function "EDDepartureTime"(Encounter "Encounter, Performed"):
  /*The time the patient physically departed the Emergency Department*/
  Last(Encounter.facilityLocations Location
      where Location.code in "Emergency Department Visit"
        and 
        end of Location.locationPeriod != maximum DateTime
      return 
      end of Location.locationPeriod
      sort ascending
  )