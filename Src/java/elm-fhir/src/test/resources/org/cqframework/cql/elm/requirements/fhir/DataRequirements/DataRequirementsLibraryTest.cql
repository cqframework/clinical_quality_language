library DataRequirementsLibraryTest version '1.0.000'

using FHIR version '4.0.1'

include DataRequirementsLibraryTestDependency version '1.0.000' called LET2

context Patient

// This expression does not directly retrieve data and so it is not necessary for
// DataRequirement identification. It is composed this was in the indications though
// and so it seems that it _is_ the "question" as opposed to the underlying retrievals
// being.
/* 7AEB32D7BD8E52C7-F1CFC1157962C1F3-5DB0D0DA537908E5 */
define "Has Presence of significant radiographic findings, which may include knee joint destruction, angular deformity, or severe narrowing":
  "Has Bone Narrowing Conditions"
    or "Has Osteonecrosis Knee Conditions"
    or "Has Angular Deformity Knee Conditions"

define "Has Bone Narrowing Conditions":
  exists ([Condition])

define "Has Osteonecrosis Knee Conditions":
  exists ([Condition])

define "Has Angular Deformity Knee Conditions":
  exists ([Condition])

/* "PathId": "7AEB32D7BD8E52C7-D9E9104ABD48B3ED" */
define "Has Failure of Previous Proximal Tibial or Distal Femoral Osteotomy":
  exists ([Procedure])

define "Encounters from Dependency Library":
  LET2."Encounters"

define "Has Encounters":
    exists ("Encounters from Dependency Library")
