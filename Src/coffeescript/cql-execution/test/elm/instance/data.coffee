###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.cql to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### Instance
library TestSnippet version '1'
using QUICK
context Patient
define QuantityA: Quantity {
  unit: 'a',
  value: 12
}

define CodeA: Code {
  system: 'http://loinc.org',
  code: '12345',
  version: '1',
  display: 'Test Code'
}

define ConceptA: Concept {
  codes: { CodeA },
  display: 'Test Concept'
}

define Med : Medication {
  name: 'Best Med Ever',
  isBrand: false
}

define val: QuantityA.value
###

module.exports['Instance'] = {
   "library" : {
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
            "localId" : "5",
            "name" : "QuantityA",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","QuantityA",": " ]
                  }, {
                     "r" : "4",
                     "s" : [ {
                        "value" : [ "Quantity"," {\n  " ]
                     }, {
                        "s" : [ {
                           "value" : [ "unit",": " ]
                        }, {
                           "r" : "2",
                           "s" : [ {
                              "value" : [ "'a'" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ ",\n  " ]
                     }, {
                        "s" : [ {
                           "value" : [ "value",": ","12" ]
                        } ]
                     }, {
                        "value" : [ "\n}" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "classType" : "{urn:hl7-org:elm-types:r1}Quantity",
               "type" : "Instance",
               "element" : [ {
                  "name" : "unit",
                  "value" : {
                     "localId" : "2",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }
               }, {
                  "name" : "value",
                  "value" : {
                     "type" : "ToDecimal",
                     "operand" : {
                        "localId" : "3",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "12",
                        "type" : "Literal"
                     }
                  }
               } ]
            }
         }, {
            "localId" : "11",
            "name" : "CodeA",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","CodeA",": " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "value" : [ "Code"," {\n  " ]
                     }, {
                        "s" : [ {
                           "value" : [ "system",": " ]
                        }, {
                           "r" : "6",
                           "s" : [ {
                              "value" : [ "'http://loinc.org'" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ ",\n  " ]
                     }, {
                        "s" : [ {
                           "value" : [ "code",": " ]
                        }, {
                           "r" : "7",
                           "s" : [ {
                              "value" : [ "'12345'" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ ",\n  " ]
                     }, {
                        "s" : [ {
                           "value" : [ "version",": " ]
                        }, {
                           "r" : "8",
                           "s" : [ {
                              "value" : [ "'1'" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ ",\n  " ]
                     }, {
                        "s" : [ {
                           "value" : [ "display",": " ]
                        }, {
                           "r" : "9",
                           "s" : [ {
                              "value" : [ "'Test Code'" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ "\n}" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "classType" : "{urn:hl7-org:elm-types:r1}Code",
               "type" : "Instance",
               "element" : [ {
                  "name" : "system",
                  "value" : {
                     "localId" : "6",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "http://loinc.org",
                     "type" : "Literal"
                  }
               }, {
                  "name" : "code",
                  "value" : {
                     "localId" : "7",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "12345",
                     "type" : "Literal"
                  }
               }, {
                  "name" : "version",
                  "value" : {
                     "localId" : "8",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "1",
                     "type" : "Literal"
                  }
               }, {
                  "name" : "display",
                  "value" : {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "Test Code",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "localId" : "16",
            "name" : "ConceptA",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "16",
                  "s" : [ {
                     "value" : [ "define ","ConceptA",": " ]
                  }, {
                     "r" : "15",
                     "s" : [ {
                        "value" : [ "Concept"," {\n  " ]
                     }, {
                        "s" : [ {
                           "value" : [ "codes",": " ]
                        }, {
                           "r" : "13",
                           "s" : [ {
                              "value" : [ "{ " ]
                           }, {
                              "r" : "12",
                              "s" : [ {
                                 "value" : [ "CodeA" ]
                              } ]
                           }, {
                              "value" : [ " }" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ ",\n  " ]
                     }, {
                        "s" : [ {
                           "value" : [ "display",": " ]
                        }, {
                           "r" : "14",
                           "s" : [ {
                              "value" : [ "'Test Concept'" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ "\n}" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "15",
               "classType" : "{urn:hl7-org:elm-types:r1}Concept",
               "type" : "Instance",
               "element" : [ {
                  "name" : "codes",
                  "value" : {
                     "localId" : "13",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "12",
                        "name" : "CodeA",
                        "type" : "ExpressionRef"
                     } ]
                  }
               }, {
                  "name" : "display",
                  "value" : {
                     "localId" : "14",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "Test Concept",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "localId" : "20",
            "name" : "Med",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "20",
                  "s" : [ {
                     "value" : [ "define ","Med"," : " ]
                  }, {
                     "r" : "19",
                     "s" : [ {
                        "value" : [ "Medication"," {\n  " ]
                     }, {
                        "s" : [ {
                           "value" : [ "name",": " ]
                        }, {
                           "r" : "17",
                           "s" : [ {
                              "value" : [ "'Best Med Ever'" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ ",\n  " ]
                     }, {
                        "s" : [ {
                           "value" : [ "isBrand",": ","false" ]
                        } ]
                     }, {
                        "value" : [ "\n}" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "19",
               "classType" : "{http://hl7.org/fhir}Medication",
               "type" : "Instance",
               "element" : [ {
                  "name" : "name",
                  "value" : {
                     "localId" : "17",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "Best Med Ever",
                     "type" : "Literal"
                  }
               }, {
                  "name" : "isBrand",
                  "value" : {
                     "localId" : "18",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "localId" : "23",
            "name" : "val",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "23",
                  "s" : [ {
                     "value" : [ "define ","val",": " ]
                  }, {
                     "r" : "22",
                     "s" : [ {
                        "r" : "21",
                        "s" : [ {
                           "value" : [ "QuantityA" ]
                        } ]
                     }, {
                        "value" : [ "." ]
                     }, {
                        "r" : "22",
                        "s" : [ {
                           "value" : [ "value" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "22",
               "path" : "value",
               "type" : "Property",
               "source" : {
                  "localId" : "21",
                  "name" : "QuantityA",
                  "type" : "ExpressionRef"
               }
            }
         } ]
      }
   }
}

