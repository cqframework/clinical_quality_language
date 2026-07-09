library NCQA_Encounter version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include NCQA_FHIRBase version '1.0.0' called FHIRBase
include NCQA_Status version '1.0.0' called Status
include NCQA_Terminology version '1.0.0' called Terminology

define function "Encounter Has Diagnosis"(Encounter FHIR.Encounter, Conditions List<FHIR.Condition>):
  AnyTrue((Encounter.diagnosis D
      return D.condition.reference)CRef
      return exists(Conditions C
          where C.id = FHIRBase."GetId"(CRef)
      )
  )

define function "Encounter Has Principal Diagnosis"(Encounter FHIR.Encounter, Conditions List<FHIR.Condition>):
  ( singleton from ( Encounter.diagnosis D
      where FHIRHelpers.ToInteger ( D.rank ) = 1
  ) ) PrincipalDiagnosis
    return exists ( Conditions C
        where C.id = FHIRBase."GetId" ( PrincipalDiagnosis.condition.reference )
    )

define function "Encounter Completed during Period"(Enc List<FHIR.Encounter>, timeperiod Interval<DateTime>):
  exists ( ( Status."Finished Encounter" ( Enc ) ) EncounterPeriod
      where 
      end of FHIRBase."Normalize Interval" ( EncounterPeriod.period ) during timeperiod
  )

define function "Finished Encounter with Telehealth POS"(Encounter List<FHIR.Encounter>):
  ( Status."Finished Encounter" ( Encounter ) ) E
    where E.class is not null
      and FHIRHelpers.ToCode ( E.class ) ~ Terminology."virtual"

define function "Finished Encounter with Outpatient POS"(Encounter List<FHIR.Encounter>):
  ( Status."Finished Encounter" ( Encounter ) ) E
    where E.class is not null
      and ( FHIRHelpers.ToCode ( E.class ) ~ Terminology."ambulatory"
          or FHIRHelpers.ToCode ( E.class ) ~ Terminology."home health"
      )

define function "Finished Encounter with Ambulatory POS"(Encounter List<FHIR.Encounter>):
  ( Status."Finished Encounter" ( Encounter ) ) E
    where E.class is not null
      and FHIRHelpers.ToCode ( E.class ) ~ Terminology."ambulatory"