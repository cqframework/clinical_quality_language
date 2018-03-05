###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.cql to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### Retrieve
library TestSnippet version '1'
using QUICK
codesystem "SNOMED": '2.16.840.1.113883.6.96'
valueset "Acute Pharyngitis": '2.16.840.1.113883.3.464.1003.102.12.1011'
valueset "Ambulatory/ED Visit": '2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Annual Wellness Visit": '2.16.840.1.113883.3.526.3.1240'
code "Viral pharyngitis code": '1532007' from "SNOMED" display 'Viral pharyngitis (disorder)'
concept "Viral pharyngitis": { "Viral pharyngitis code" } display 'Viral pharyngitis (disorder)'
context Patient
define Conditions: [Condition]
define Encounters: [Encounter]
define PharyngitisConditions: [Condition: "Acute Pharyngitis"]
define AmbulatoryEncounters: [Encounter: "Ambulatory/ED Visit"]
define EncountersByServiceType: [Encounter: type in "Ambulatory/ED Visit"]
define WrongValueSet: [Condition: "Ambulatory/ED Visit"]
define WrongCodeProperty: [Encounter: class in "Ambulatory/ED Visit"]
define ConditionsByCode: [Condition: "Viral pharyngitis code"]
define ConditionsByConcept: [Condition: "Viral pharyngitis"]
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
            "localId" : "1",
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "codeSystems" : {
         "def" : [ {
            "localId" : "2",
            "name" : "SNOMED",
            "id" : "2.16.840.1.113883.6.96",
            "accessLevel" : "Public"
         } ]
      },
      "valueSets" : {
         "def" : [ {
            "localId" : "3",
            "name" : "Acute Pharyngitis",
            "id" : "2.16.840.1.113883.3.464.1003.102.12.1011",
            "accessLevel" : "Public"
         }, {
            "localId" : "4",
            "name" : "Ambulatory/ED Visit",
            "id" : "2.16.840.1.113883.3.464.1003.101.12.1061",
            "accessLevel" : "Public"
         }, {
            "localId" : "5",
            "name" : "Annual Wellness Visit",
            "id" : "2.16.840.1.113883.3.526.3.1240",
            "accessLevel" : "Public"
         } ]
      },
      "codes" : {
         "def" : [ {
            "localId" : "7",
            "name" : "Viral pharyngitis code",
            "id" : "1532007",
            "display" : "Viral pharyngitis (disorder)",
            "accessLevel" : "Public",
            "codeSystem" : {
               "localId" : "6",
               "name" : "SNOMED"
            }
         } ]
      },
      "concepts" : {
         "def" : [ {
            "localId" : "9",
            "name" : "Viral pharyngitis",
            "display" : "Viral pharyngitis (disorder)",
            "accessLevel" : "Public",
            "code" : [ {
               "localId" : "8",
               "name" : "Viral pharyngitis code"
            } ]
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
            "localId" : "11",
            "name" : "Conditions",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","Conditions",": " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "value" : [ "[","Condition","]" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "dataType" : "{http://hl7.org/fhir}Condition",
               "templateId" : "condition-qicore-qicore-condition",
               "type" : "Retrieve"
            }
         }, {
            "localId" : "13",
            "name" : "Encounters",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "13",
                  "s" : [ {
                     "value" : [ "define ","Encounters",": " ]
                  }, {
                     "r" : "12",
                     "s" : [ {
                        "value" : [ "[","Encounter","]" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "12",
               "dataType" : "{http://hl7.org/fhir}Encounter",
               "templateId" : "encounter-qicore-qicore-encounter",
               "type" : "Retrieve"
            }
         }, {
            "localId" : "15",
            "name" : "PharyngitisConditions",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "15",
                  "s" : [ {
                     "value" : [ "define ","PharyngitisConditions",": " ]
                  }, {
                     "r" : "14",
                     "s" : [ {
                        "value" : [ "[","Condition",": " ]
                     }, {
                        "s" : [ {
                           "value" : [ "\"Acute Pharyngitis\"" ]
                        } ]
                     }, {
                        "value" : [ "]" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "14",
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
            "localId" : "17",
            "name" : "AmbulatoryEncounters",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "17",
                  "s" : [ {
                     "value" : [ "define ","AmbulatoryEncounters",": " ]
                  }, {
                     "r" : "16",
                     "s" : [ {
                        "value" : [ "[","Encounter",": " ]
                     }, {
                        "s" : [ {
                           "value" : [ "\"Ambulatory/ED Visit\"" ]
                        } ]
                     }, {
                        "value" : [ "]" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "16",
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
            "localId" : "19",
            "name" : "EncountersByServiceType",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "19",
                  "s" : [ {
                     "value" : [ "define ","EncountersByServiceType",": " ]
                  }, {
                     "r" : "18",
                     "s" : [ {
                        "value" : [ "[","Encounter",": ","type"," in " ]
                     }, {
                        "s" : [ {
                           "value" : [ "\"Ambulatory/ED Visit\"" ]
                        } ]
                     }, {
                        "value" : [ "]" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "18",
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
            "localId" : "21",
            "name" : "WrongValueSet",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "21",
                  "s" : [ {
                     "value" : [ "define ","WrongValueSet",": " ]
                  }, {
                     "r" : "20",
                     "s" : [ {
                        "value" : [ "[","Condition",": " ]
                     }, {
                        "s" : [ {
                           "value" : [ "\"Ambulatory/ED Visit\"" ]
                        } ]
                     }, {
                        "value" : [ "]" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "20",
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
            "localId" : "23",
            "name" : "WrongCodeProperty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "23",
                  "s" : [ {
                     "value" : [ "define ","WrongCodeProperty",": " ]
                  }, {
                     "r" : "22",
                     "s" : [ {
                        "value" : [ "[","Encounter",": ","class"," in " ]
                     }, {
                        "s" : [ {
                           "value" : [ "\"Ambulatory/ED Visit\"" ]
                        } ]
                     }, {
                        "value" : [ "]" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "22",
               "dataType" : "{http://hl7.org/fhir}Encounter",
               "templateId" : "encounter-qicore-qicore-encounter",
               "codeProperty" : "class",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Ambulatory/ED Visit",
                  "type" : "ValueSetRef"
               }
            }
         }, {
            "localId" : "25",
            "name" : "ConditionsByCode",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "25",
                  "s" : [ {
                     "value" : [ "define ","ConditionsByCode",": " ]
                  }, {
                     "r" : "24",
                     "s" : [ {
                        "value" : [ "[","Condition",": " ]
                     }, {
                        "s" : [ {
                           "value" : [ "\"Viral pharyngitis code\"" ]
                        } ]
                     }, {
                        "value" : [ "]" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "24",
               "dataType" : "{http://hl7.org/fhir}Condition",
               "templateId" : "condition-qicore-qicore-condition",
               "codeProperty" : "code",
               "type" : "Retrieve",
               "codes" : {
                  "type" : "ToList",
                  "operand" : {
                     "type" : "ToConcept",
                     "operand" : {
                        "name" : "Viral pharyngitis code",
                        "type" : "CodeRef"
                     }
                  }
               }
            }
         }, {
            "localId" : "27",
            "name" : "ConditionsByConcept",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "27",
                  "s" : [ {
                     "value" : [ "define ","ConditionsByConcept",": " ]
                  }, {
                     "r" : "26",
                     "s" : [ {
                        "value" : [ "[","Condition",": " ]
                     }, {
                        "s" : [ {
                           "value" : [ "\"Viral pharyngitis\"" ]
                        } ]
                     }, {
                        "value" : [ "]" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "26",
               "dataType" : "{http://hl7.org/fhir}Condition",
               "templateId" : "condition-qicore-qicore-condition",
               "codeProperty" : "code",
               "type" : "Retrieve",
               "codes" : {
                  "type" : "ToList",
                  "operand" : {
                     "name" : "Viral pharyngitis",
                     "type" : "ConceptRef"
                  }
               }
            }
         } ]
      }
   }
}

