###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### Nil
library TestSnippet version '1'
using QUICK
context Patient
define Nil = null
###

module.exports['Nil'] = {
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
            "name" : "Nil",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         } ]
      }
   }
}

### IsNull
library TestSnippet version '1'
using QUICK
context Patient
define Nil = null
define One = 1
define NullIsNull = IsNull(null)
define NullVarIsNull = IsNull(Nil)
define StringIsNull = IsNull('')
define NonNullVarIsNull = IsNull(One)
###

module.exports['IsNull'] = {
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
            "name" : "Nil",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "One",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{urn:hl7-org:elm:r1}Integer",
               "value" : "1",
               "type" : "Literal"
            }
         }, {
            "name" : "NullIsNull",
            "context" : "Patient",
            "expression" : {
               "name" : "IsNull",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "Null"
               } ]
            }
         }, {
            "name" : "NullVarIsNull",
            "context" : "Patient",
            "expression" : {
               "name" : "IsNull",
               "type" : "FunctionRef",
               "operand" : [ {
                  "name" : "Nil",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "StringIsNull",
            "context" : "Patient",
            "expression" : {
               "name" : "IsNull",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}String",
                  "value" : "",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "NonNullVarIsNull",
            "context" : "Patient",
            "expression" : {
               "name" : "IsNull",
               "type" : "FunctionRef",
               "operand" : [ {
                  "name" : "One",
                  "type" : "ExpressionRef"
               } ]
            }
         } ]
      }
   }
}

### IfNull
library TestSnippet version '1'
using QUICK
context Patient
define NullAndA = IfNull(null, 'a')
define ZeroAndB = IfNull(0, 1)
define BothNull = IfNull(null, null)
###

module.exports['IfNull'] = {
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
            "name" : "NullAndA",
            "context" : "Patient",
            "expression" : {
               "name" : "IfNull",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}String",
                  "value" : "a",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ZeroAndB",
            "context" : "Patient",
            "expression" : {
               "name" : "IfNull",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "0",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "BothNull",
            "context" : "Patient",
            "expression" : {
               "name" : "IfNull",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "type" : "Null"
               } ]
            }
         } ]
      }
   }
}

### Coalesce
library TestSnippet version '1'
using QUICK
context Patient
define NullNullHelloNullWorld = Coalesce(null, null, 'Hello', null, 'World')
define FooNullNullBar = Coalesce('Foo', null, null, 'Bar')
define AllNull = Coalesce(null, null, null)
###

###
Translation Error(s):
[4:33, 4:76] Could not resolve call to operator Coalesce with signature (System.Any,System.Any,System.String,System.Any,System.String).
[5:25, 5:58] Could not resolve call to operator Coalesce with signature (System.String,System.Any,System.Any,System.String).
[6:18, 6:43] Could not resolve call to operator Coalesce with signature (System.Any,System.Any,System.Any).
###
module.exports['Coalesce'] = {
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
            "name" : "NullNullHelloNullWorld",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "FooNullNullBar",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "AllNull",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         } ]
      }
   }
}

