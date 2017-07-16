// @Test: Retrieve
codesystem "SNOMED": '2.16.840.1.113883.6.96'
valueset "Acute Pharyngitis": '2.16.840.1.113883.3.464.1003.102.12.1011'
valueset "Ambulatory/ED Visit": '2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Annual Wellness Visit": '2.16.840.1.113883.3.526.3.1240'
code "Viral pharyngitis code": '1532007' from "SNOMED" display 'Viral pharyngitis (disorder)'
concept "Viral pharyngitis": { "Viral pharyngitis code" } display 'Viral pharyngitis (disorder)'
define Conditions: [Condition]
define Encounters: [Encounter]
define PharyngitisConditions: [Condition: "Acute Pharyngitis"]
define AmbulatoryEncounters: [Encounter: "Ambulatory/ED Visit"]
define EncountersByServiceType: [Encounter: type in "Ambulatory/ED Visit"]
define WrongValueSet: [Condition: "Ambulatory/ED Visit"]
define WrongCodeProperty: [Encounter: class in "Ambulatory/ED Visit"]
define ConditionsByCode: [Condition: "Viral pharyngitis code"]
define ConditionsByConcept: [Condition: "Viral pharyngitis"]

