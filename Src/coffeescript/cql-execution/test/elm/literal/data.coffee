###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### Literal
library TestSnippet version '1'
using QUICK
context Patient
define BoolTrue = true
define BoolFalse = false
define IntOne = 1
define DecimalTenth = 0.1
define StringTrue = 'true'
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
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Patient",
            "context" : "Patient",
            "expression" : {
               "type" : "SingletonFrom",
               "operand" : {
                  "dataType" : "{http://org.hl7.fhir}Patient",
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "BoolTrue",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
               "value" : "true",
               "type" : "Literal"
            }
         }, {
            "name" : "BoolFalse",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
               "value" : "false",
               "type" : "Literal"
            }
         }, {
            "name" : "IntOne",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "1",
               "type" : "Literal"
            }
         }, {
            "name" : "DecimalTenth",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}decimal",
               "value" : "0.1",
               "type" : "Literal"
            }
         }, {
            "name" : "StringTrue",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
               "value" : "true",
               "type" : "Literal"
            }
         } ]
      }
   }
}

