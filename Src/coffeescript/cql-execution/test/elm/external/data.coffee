###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### Retrieve
library TestSnippet version '1'
using QUICK
valueset "Acute Pharyngitis": '2.16.840.1.113883.3.464.1003.102.12.1011'
valueset "Ambulatory/ED Visit": '2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Annual Wellness Visit": '2.16.840.1.113883.3.526.3.1240'
context Patient
define Conditions: [Condition]
define Encounters: [Encounter]
define PharyngitisConditions: [Condition: "Acute Pharyngitis"]
define AmbulatoryEncounters: [Encounter: "Ambulatory/ED Visit"]
define EncountersByServiceType: [Encounter: type in "Ambulatory/ED Visit"]
define WrongValueSet: [Condition: "Ambulatory/ED Visit"]
define WrongCodeProperty: [Encounter: class in "Ambulatory/ED Visit"]
###

module.exports['Retrieve'] = {
   "library" : {
      "annotation" : [ {
         "message" : "List-valued expression was demoted to a singleton.",
         "errorType" : "semantic",
         "errorSeverity" : "warning",
         "type" : "CqlToElmError"
      }, {
         "message" : "List-valued expression was demoted to a singleton.",
         "errorType" : "semantic",
         "errorSeverity" : "warning",
         "type" : "CqlToElmError"
      } ],
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "schemaIdentifier" : {
         "id" : "urn:hl7-org:elm",
         "version" : "r1"
      },
      "usings" : {
         "def" : [ {
            "localIdentifier" : "System",
            "uri" : "urn:hl7-org:elm-types:r1"
         }, {
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "valueSets" : {
         "def" : [ {
            "name" : "Acute Pharyngitis",
            "id" : "2.16.840.1.113883.3.464.1003.102.12.1011",
            "accessLevel" : "Public"
         }, {
            "name" : "Ambulatory/ED Visit",
            "id" : "2.16.840.1.113883.3.464.1003.101.12.1061",
            "accessLevel" : "Public"
         }, {
            "name" : "Annual Wellness Visit",
            "id" : "2.16.840.1.113883.3.526.3.1240",
            "accessLevel" : "Public"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Patient",
            "context" : "Patient",
            "expression" : {
               "type" : "SingletonFrom",
               "operand" : {
                  "dataType" : "{http://hl7.org/fhir}Patient",
                  "templateId" : "patient-qicore-qicore-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "Conditions",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "dataType" : "{http://hl7.org/fhir}Condition",
               "templateId" : "condition-qicore-qicore-condition",
               "type" : "Retrieve"
            }
         }, {
            "name" : "Encounters",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "dataType" : "{http://hl7.org/fhir}Encounter",
               "templateId" : "encounter-qicore-qicore-encounter",
               "type" : "Retrieve"
            }
         }, {
            "name" : "PharyngitisConditions",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "dataType" : "{http://hl7.org/fhir}Condition",
               "templateId" : "condition-qicore-qicore-condition",
               "codeProperty" : "code",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Acute Pharyngitis",
                  "type" : "ValueSetRef"
               }
            }
         }, {
            "name" : "AmbulatoryEncounters",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "dataType" : "{http://hl7.org/fhir}Encounter",
               "templateId" : "encounter-qicore-qicore-encounter",
               "codeProperty" : "type",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Ambulatory/ED Visit",
                  "type" : "ValueSetRef"
               }
            }
         }, {
            "name" : "EncountersByServiceType",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "dataType" : "{http://hl7.org/fhir}Encounter",
               "templateId" : "encounter-qicore-qicore-encounter",
               "codeProperty" : "type",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Ambulatory/ED Visit",
                  "type" : "ValueSetRef"
               }
            }
         }, {
            "name" : "WrongValueSet",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "dataType" : "{http://hl7.org/fhir}Condition",
               "templateId" : "condition-qicore-qicore-condition",
               "codeProperty" : "code",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Ambulatory/ED Visit",
                  "type" : "ValueSetRef"
               }
            }
         }, {
            "name" : "WrongCodeProperty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "dataType" : "{http://hl7.org/fhir}Encounter",
               "templateId" : "encounter-qicore-qicore-encounter",
               "codeProperty" : "class",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Ambulatory/ED Visit",
                  "type" : "ValueSetRef"
               }
            }
         } ]
      }
   }
}

