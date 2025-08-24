/*
@issue: [#405](https://github.com/cqframework/clinical_quality_language/issues/405)
*/

library Issue405

using QDM version '5.4'

valueset "Cancer": 'urn:oid:2.16.840.1.113883.3.526.3.1010'
valueset "Chemotherapy Administration": 'urn:oid:2.16.840.1.113883.3.526.3.1027'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'

context Patient

define "Face to Face Encounter with Ongoing Chemotherapy":
	from
		["Encounter, Performed": "Office Visit"] FaceToFaceEncounter,
		["Procedure, Performed": "Chemotherapy Administration"] ChemoBeforeEncounter,
		["Procedure, Performed": "Chemotherapy Administration"] ChemoAfterEncounter,
		["Diagnosis": "Cancer"] Cancer
		where ChemoBeforeEncounter !~ ChemoAfterEncounter
		return FaceToFaceEncounter

define EquivInt1Int2                : 1 ~ 2
define EquivStringAStringA          : 'a' ~ 'a'
define EquivStringAStringB          : 'a' ~ 'b'
define EquivFloat1Float1            : 1.0 ~ 1.0