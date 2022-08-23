library NCQA_PalliativeCare version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers
include NCQA_FHIRBase version '1.0.0' called FHIRBase
include NCQA_Status version '1.0.0' called Status

codesystem "ICD-10": 'http://hl7.org/fhir/sid/icd-10-cm'

valueset "Palliative Care Assessment": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.2225'
valueset "Palliative Care Encounter": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.1450'
valueset "Palliative Care Intervention": 'https://www.ncqa.org/fhir/valueset/2.16.840.1.113883.3.464.1004.2224'

code "Encounter for palliative care": 'Z51.5' from "ICD-10" display 'Encounter for palliative care'

define function "Palliative Care Overlapping Period"(Period Interval<DateTime>):
  exists ( [Observation: "Palliative Care Assessment"] PalliativeAssessment
      where Interval[date from start of FHIRBase."Normalize Interval" ( PalliativeAssessment.effective ), date from 
      end of FHIRBase."Normalize Interval" ( PalliativeAssessment.effective )]overlaps Interval[date from start of Period, date from 
      end of Period]
  )
    or exists ( ( Status."Finished Encounter" ( [Encounter: "Palliative Care Encounter"] ) ) PalliativeEncounter
        where Interval[date from start of FHIRBase."Normalize Interval" ( PalliativeEncounter.period ), date from 
        end of FHIRBase."Normalize Interval" ( PalliativeEncounter.period )]overlaps Interval[date from start of Period, date from 
        end of Period]
    )
    or exists ( ( Status."Completed or Ongoing Procedure" ( [Procedure: "Palliative Care Intervention"] ) ) PalliativeIntervention
        where Interval[date from start of FHIRBase."Normalize Interval" ( PalliativeIntervention.performed ), date from 
        end of FHIRBase."Normalize Interval" ( PalliativeIntervention.performed )]overlaps Interval[date from start of Period, date from 
        end of Period]
    )
    or exists ( ( Status."Active Condition" ( [Condition: "Encounter for palliative care"] ) ) PalliativeDiagnosis
        where Interval[date from start of FHIRBase."Prevalence Period" ( PalliativeDiagnosis ), date from 
        end of FHIRBase."Prevalence Period" ( PalliativeDiagnosis )]overlaps Interval[date from start of Period, date from 
        end of Period]
    )