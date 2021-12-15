// https://github.com/cqframework/clinical_quality_language/issues/613
// Issue:   Parenthesis logic excluding trailing space.
// Fix:     Adjusted condition to account for inFunctionInvocation() || inFunctionDefinition()

define "Test":
  "Heart Failture Inpatient Encounter" HFInpatientEncounter
       with "Moderate or Severe LVSD Findings" LVSDFindings
	   such that Coalesce(LVSDFindings.prevalencePeriod, Global."NormalizeInterval"(LVSDFindings.relevantDatetime,
       LVSDFindings.relevantPeriod)) starts before end of HFInpatientEncounter.relevantPeriod
