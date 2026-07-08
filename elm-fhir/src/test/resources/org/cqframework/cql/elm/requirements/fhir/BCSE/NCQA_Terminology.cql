library NCQA_Terminology version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1' called FHIRHelpers

codesystem "ConditionClinicalStatusCodes": 'http://terminology.hl7.org/CodeSystem/condition-clinical'
codesystem "ClaimTypeCodes": 'http://terminology.hl7.org/CodeSystem/claim-type'
codesystem "ActEncounterCodes": 'http://terminology.hl7.org/CodeSystem/v3-ActCode'
codesystem "IdentifierType": 'http://terminology.hl7.org/CodeSystem/v2-0203'

// Condition Clinical Status Codes

code "active": 'active' from "ConditionClinicalStatusCodes"
code "recurrence": 'recurrence' from "ConditionClinicalStatusCodes"
code "relapse": 'relapse' from "ConditionClinicalStatusCodes"
code "inactive": 'inactive' from "ConditionClinicalStatusCodes"
code "remission": 'remission' from "ConditionClinicalStatusCodes"
code "resolved": 'resolved' from "ConditionClinicalStatusCodes"

// Claim Type Codes
code "Pharmacy": 'pharmacy' from "ClaimTypeCodes"
code "Institutional": 'institutional' from "ClaimTypeCodes"
code "Professional": 'professional' from "ClaimTypeCodes"
code "Oral": 'oral' from "ClaimTypeCodes"
code "Vision": 'vision' from "ClaimTypeCodes"

// ActEncounterCodes
code "virtual": 'VR' from "ActEncounterCodes"
code "ambulatory": 'AMB' from "ActEncounterCodes"
code "home health": 'HH' from "ActEncounterCodes"
code "inpatient non-acute": 'NONAC' from "ActEncounterCodes"
code "emergency": 'EMER' from "ActEncounterCodes"
code "inpatient acute": 'ACUTE' from "ActEncounterCodes"

// Coverage-type
code "drug policy": 'DRUGPOL' from "ActEncounterCodes"
code "mental health policy": 'MENTPOL' from "ActEncounterCodes"
code "managed care policy": 'MCPOL' from "ActEncounterCodes"
code "subsidized health program": 'SUBSIDIZ' from "ActEncounterCodes"
code "retiree health program": 'RETIRE' from "ActEncounterCodes"
code "substance use policy": 'SUBPOL' from "ActEncounterCodes"

// Identifier Type Codes
code "Provider number": 'PRN' from "IdentifierType"
