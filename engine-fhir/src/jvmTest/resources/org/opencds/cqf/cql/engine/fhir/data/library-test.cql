library HEDIS10594_FHIR version '1'

/* Colorectal Cancer Screening (COL) - (10594) - July 2017 Publication
 * ============================================================================
*/

using FHIR version '1.8'

include FHIRHelpers version '1.8' called FHIRHelpers


valueset "VS_Colonoscopy":'2.16.840.1.113883.3.464.1004.1064'
valueset "VS_Colorectal Cancer":'2.16.840.1.113883.3.464.1004.1065'
valueset "VS_Flexible Sigmoidoscopy":'2.16.840.1.113883.3.464.1004.1102'
valueset "VS_FOBT":'2.16.840.1.113883.3.464.1004.1093'
valueset "VS_Total Colectomy":'2.16.840.1.113883.3.464.1004.1250'

parameter CutoffDate DateTime

context Patient

//-- Patient Eligibility

define "In Demographic":
  AgeInYearsAt(CutoffDate) >= 51
    and AgeInYearsAt(CutoffDate) <= 75

//-- Coverage/Enrollment Conditions - quoting Classic Grammar for now
// medEligibility (CUTOFF_DATE - (2 years - 1 day), CUTOFF_DATE, 1, 45)


define "Is Eligible":
    "In Demographic"


define "Denominator Events":
   {
     age: AgeInYearsAt(CutoffDate)
   }


//-- Numerator

define "Num Reason1: FOBT in the Measurement Year":
  [Claim] C
    with C.item I
     such that I.service in "VS_FOBT"
       and I.serviced during Interval (CutoffDate - 1 years, CutoffDate]

// Need to resolve this....
//  [Claim: item.service in "VS_FOBT"] C
//    where

//  [Claim.item: service in "VS_FOBT"] CI where CI.serviced during Interval(CutoffDate - 1 years, CutoffDate)

define "Num Reason2: Flexible Sigmoidscopy four years prior or during the Measurement Year":
  [Claim] C
    with C.item I
     such that I.service in "VS_Flexible Sigmoidoscopy"
       and I.serviced during Interval (CutoffDate - 5 years, CutoffDate]

define "Num Reason3: Colonoscopy nine years prior or during the measurement year":
  [Claim] C
    with C.item I
     such that I.service in "VS_Colonoscopy"
       and I.serviced during Interval (CutoffDate - 10 years, CutoffDate]

define "Is Compliant":
  exists "Num Reason1: FOBT in the Measurement Year"
  or exists "Num Reason2: Flexible Sigmoidscopy four years prior or during the Measurement Year"
  or exists "Num Reason3: Colonoscopy nine years prior or during the measurement year"

//-- Exclusions(Optional)

define "History of Colorectal Cancer":
  [Claim] C
    with C.item I
     such that I.service in "VS_Colorectal Cancer"
       and I.serviced <= CutoffDate

define "History of Total Colectomy":
  [Claim] C
    with C.item I
     such that I.service in "VS_Total Colectomy"
       and I.serviced <= CutoffDate

define "Has Optional Exclusions":
  exists "History of Colorectal Cancer"
  or exists "History of Total Colectomy"


define "Optional Exclusions Events":
  { claim1: "History of Colorectal Cancer",
    claim2: "History of Total Colectomy"
  }
