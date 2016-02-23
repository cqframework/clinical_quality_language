###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### FromString
library TestSnippet version '1'
using QUICK
context Patient
define boolTrue: convert 'true' to Boolean
define boolFalse: convert 'false' to Boolean
define decimalValid: convert '10.2' to Decimal
define decimalInvalid: convert 'abc' to Decimal
define integerValid: convert '10' to Integer
define integerDropDecimal: convert '10.2' to Integer
define integerInvalid: convert 'abc' to Integer
###

module.exports['FromString'] = {
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
            "name" : "boolTrue",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Boolean",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "true",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Boolean",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "boolFalse",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Boolean",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "false",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Boolean",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "decimalValid",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Decimal",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "10.2",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "decimalInvalid",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Decimal",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "abc",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "integerValid",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Integer",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "10",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "integerDropDecimal",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Integer",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "10.2",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "integerInvalid",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Integer",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "abc",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            }
         } ]
      }
   }
}

