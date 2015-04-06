###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### ExpressionDef
library TestSnippet version '1'
using QUICK
context Patient
define Foo: 'Bar'
###

module.exports['ExpressionDef'] = {
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
            "uri" : "urn:hl7-org:elm:r1"
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "valueType" : "{urn:hl7-org:elm:r1}String",
               "value" : "Bar",
               "type" : "Literal"
            }
         } ]
      }
   }
}

### ExpressionRef
library TestSnippet version '1'
using QUICK
context Patient
define Life: 42
define Foo: Life
###

module.exports['ExpressionRef'] = {
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
            "uri" : "urn:hl7-org:elm:r1"
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "Life",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "valueType" : "{urn:hl7-org:elm:r1}Integer",
               "value" : "42",
               "type" : "Literal"
            }
         }, {
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Life",
               "type" : "ExpressionRef"
            }
         } ]
      }
   }
}

### FunctionDefinitions
library TestSnippet version '1'
using QUICK
context Patient
define function foo (a Integer, b Integer) :
  a + b

define testValue: foo(1,2)
###

module.exports['FunctionDefinitions'] = {
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
            "uri" : "urn:hl7-org:elm:r1"
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "type" : "FunctionDef",
            "expression" : {
               "type" : "Add",
               "operand" : [ {
                  "name" : "a",
                  "type" : "OperandRef"
               }, {
                  "name" : "b",
                  "type" : "OperandRef"
               } ]
            },
            "operand" : [ {
               "name" : "a",
               "operandTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            }, {
               "name" : "b",
               "operandTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            } ]
         }, {
            "name" : "testValue",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "foo",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "2",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

