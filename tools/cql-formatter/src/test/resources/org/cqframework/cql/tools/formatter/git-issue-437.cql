// https://github.com/cqframework/clinical_quality_language/issues/437
// Issue:   Comment not triggering new line.
// Fix:     Added newLine() call to appendComment()

define "Encounter With Principal Procedure of SCIP VTE Selected Surgery":
  "Encounter With Age Range and Without VTE Diagnosis or Obstetrical Conditions" QualifyingEncounter
    with ( "SCIP VTE Selected Surgery" Procedure
      where Procedure.rank = 1 //in "Principal"
          ) SelectedSCIPProcedure
      such that SelectedSCIPProcedure.relevantPeriod during QualifyingEncounter.relevantPeriod