###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.cql to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### Literal
library TestSnippet version '1'
using QUICK
context Patient
define BoolTrue: true
define BoolFalse: false
define IntOne: 1
define DecimalTenth: 0.1
define StringTrue: 'true'
define DateTimeX: @2012-02-15T12:10:59.456Z
define TimeX: @T12:10:59.456Z
###

module.exports['Literal'] = {
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
            "localId" : "3",
            "name" : "BoolTrue",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "3",
                  "s" : [ {
                     "value" : [ "define ","BoolTrue",": ","true" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "2",
               "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
               "value" : "true",
               "type" : "Literal"
            }
         }, {
            "localId" : "5",
            "name" : "BoolFalse",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","BoolFalse",": ","false" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
               "value" : "false",
               "type" : "Literal"
            }
         }, {
            "localId" : "7",
            "name" : "IntOne",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "7",
                  "s" : [ {
                     "value" : [ "define ","IntOne",": ","1" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "6",
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "1",
               "type" : "Literal"
            }
         }, {
            "localId" : "9",
            "name" : "DecimalTenth",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","DecimalTenth",": ","0.1" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
               "value" : "0.1",
               "type" : "Literal"
            }
         }, {
            "localId" : "11",
            "name" : "StringTrue",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","StringTrue",": " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "value" : [ "'true'" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "valueType" : "{urn:hl7-org:elm-types:r1}String",
               "value" : "true",
               "type" : "Literal"
            }
         }, {
            "localId" : "13",
            "name" : "DateTimeX",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "13",
                  "s" : [ {
                     "value" : [ "define ","DateTimeX",": ","@2012-02-15T12:10:59.456Z" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "12",
               "type" : "DateTime",
               "year" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2012",
                  "type" : "Literal"
               },
               "month" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2",
                  "type" : "Literal"
               },
               "day" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "15",
                  "type" : "Literal"
               },
               "hour" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "12",
                  "type" : "Literal"
               },
               "minute" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "10",
                  "type" : "Literal"
               },
               "second" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "59",
                  "type" : "Literal"
               },
               "millisecond" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "456",
                  "type" : "Literal"
               },
               "timezoneOffset" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "value" : "0.0",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "15",
            "name" : "TimeX",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "15",
                  "s" : [ {
                     "value" : [ "define ","TimeX",": ","@T12:10:59.456Z" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "14",
               "type" : "Time",
               "hour" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "12",
                  "type" : "Literal"
               },
               "minute" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "10",
                  "type" : "Literal"
               },
               "second" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "59",
                  "type" : "Literal"
               },
               "millisecond" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "456",
                  "type" : "Literal"
               },
               "timezoneOffset" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "value" : "0.0",
                  "type" : "Literal"
               }
            }
         } ]
      }
   }
}

