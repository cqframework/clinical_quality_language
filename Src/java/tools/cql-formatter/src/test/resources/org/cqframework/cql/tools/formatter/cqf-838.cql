library MyLibrary version '1.0.000'

using QDM version '5.5'

context Patient

define "Heart Failture Inpatient Encounter": ["Encounter, Performed"]

define "Moderate or Severe LVSD Findings": ["Laboratory Test, Performed"] union ["Diagnosis"]

define "Test":
  "Heart Failture Inpatient Encounter" HFInpatientEncounter
       with "Moderate or Severe LVSD Findings" LVSDFindings
	   such that Coalesce(LVSDFindings.prevalencePeriod, Global."NormalizeInterval"(LVSDFindings.relevantDatetime,
       LVSDFindings.relevantPeriod)) starts before end of HFInpatientEncounter.relevantPeriod

define "Test2":
        exists ( ["Diagnostic Study, Performed": "Mammography"] Mammogram
        where Mammogram.result is not null
        and Mammogram.relevantPeriod ends 26 months or less on or before end of "Measurement Period"
        )

define "Encounter With Principal Procedure of SCIP VTE Selected Surgery":
  "Encounter With Age Range and Without VTE Diagnosis or Obstetrical Conditions" QualifyingEncounter
    with ( "SCIP VTE Selected Surgery" Procedure
      where Procedure.rank = 1 //in "Principal"
          ) SelectedSCIPProcedure
      such that SelectedSCIPProcedure.relevantPeriod during QualifyingEncounter.relevantPeriod