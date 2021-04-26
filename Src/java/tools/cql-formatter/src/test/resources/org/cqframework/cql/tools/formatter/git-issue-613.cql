define "Test":
  "Heart Failture Inpatient Encounter" HFInpatientEncounter
       with "Moderate or Severe LVSD Findings" LVSDFindings
	   such that Coalesce(LVSDFindings.prevalencePeriod, Global."NormalizeInterval"(LVSDFindings.relevantDatetime,
       LVSDFindings.relevantPeriod)) starts before end of HFInpatientEncounter.relevantPeriod
