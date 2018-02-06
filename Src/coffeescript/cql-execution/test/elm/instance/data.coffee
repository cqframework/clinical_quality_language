###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### Instance
library TestSnippet version '1'
using QUICK
context Patient
define Quantity: Quantity {
  unit: 'a',
  value: 12
}

define Med : Medication {
  name: 'Best Med Ever',
  isBrand: false
}

define val: Quantity.value
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
            "name" : "Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","Quantity",": " ]
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
            "localId" : "9",
            "name" : "Med",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","Med"," : " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "Medication"," {\n  " ]
                     }, {
                        "s" : [ {
                           "value" : [ "name",": " ]
                        }, {
                           "r" : "6",
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
               "localId" : "8",
               "classType" : "{http://hl7.org/fhir}Medication",
               "type" : "Instance",
               "element" : [ {
                  "name" : "name",
                  "value" : {
                     "localId" : "6",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "Best Med Ever",
                     "type" : "Literal"
                  }
               }, {
                  "name" : "isBrand",
                  "value" : {
                     "localId" : "7",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "localId" : "12",
            "name" : "val",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "12",
                  "s" : [ {
                     "value" : [ "define ","val",": " ]
                  }, {
                     "r" : "11",
                     "s" : [ {
                        "r" : "10",
                        "s" : [ {
                           "value" : [ "Quantity" ]
                        } ]
                     }, {
                        "value" : [ "." ]
                     }, {
                        "r" : "11",
                        "s" : [ {
                           "value" : [ "value" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "11",
               "path" : "value",
               "type" : "Property",
               "source" : {
                  "localId" : "10",
                  "name" : "Quantity",
                  "type" : "ExpressionRef"
               }
            }
         } ]
      }
   }
}

