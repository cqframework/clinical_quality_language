###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### And
library TestSnippet version '1'
using QUICK
context Patient
define TT = true and true
define TF = true and false
define TN = true and null
define FF = false and false
define FT = false and true
define FN = false and null
define NN = null and null
define NT = null and true
define NF = null and false
###

###
Translation Error(s):
[6:13, 6:25] Could not resolve call to operator And with signature (System.Boolean,System.Any).
[9:13, 9:26] Could not resolve call to operator And with signature (System.Boolean,System.Any).
[10:13, 10:25] Could not resolve call to operator And with signature (System.Any,System.Any).
[11:13, 11:25] Could not resolve call to operator And with signature (System.Any,System.Boolean).
[12:13, 12:26] Could not resolve call to operator And with signature (System.Any,System.Boolean).
###
module.exports['And'] = {
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
            "name" : "TT",
            "context" : "Patient",
            "expression" : {
               "type" : "And",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "true",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TF",
            "context" : "Patient",
            "expression" : {
               "type" : "And",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "true",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "false",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TN",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "FF",
            "context" : "Patient",
            "expression" : {
               "type" : "And",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "false",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "false",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FT",
            "context" : "Patient",
            "expression" : {
               "type" : "And",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "false",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FN",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NN",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NT",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NF",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         } ]
      }
   }
}

### Or
library TestSnippet version '1'
using QUICK
context Patient
define TT = true or true
define TF = true or false
define TN = true or null
define FF = false or false
define FT = false or true
define FN = false or null
define NN = null or null
define NT = null or true
define NF = null or false
###

###
Translation Error(s):
[6:13, 6:24] Could not resolve call to operator Or with signature (System.Boolean,System.Any).
[9:13, 9:25] Could not resolve call to operator Or with signature (System.Boolean,System.Any).
[10:13, 10:24] Could not resolve call to operator Or with signature (System.Any,System.Any).
[11:13, 11:24] Could not resolve call to operator Or with signature (System.Any,System.Boolean).
[12:13, 12:25] Could not resolve call to operator Or with signature (System.Any,System.Boolean).
###
module.exports['Or'] = {
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
            "name" : "TT",
            "context" : "Patient",
            "expression" : {
               "type" : "Or",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "true",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TF",
            "context" : "Patient",
            "expression" : {
               "type" : "Or",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "true",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "false",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TN",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "FF",
            "context" : "Patient",
            "expression" : {
               "type" : "Or",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "false",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "false",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FT",
            "context" : "Patient",
            "expression" : {
               "type" : "Or",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "false",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FN",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NN",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NT",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NF",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         } ]
      }
   }
}

### XOr
library TestSnippet version '1'
using QUICK
context Patient
define TT = true xor true
define TF = true xor false
define TN = true xor null
define FF = false xor false
define FT = false xor true
define FN = false xor null
define NN = null xor null
define NT = null xor true
define NF = null xor false
###

###
Translation Error(s):
[6:13, 6:25] Could not resolve call to operator Xor with signature (System.Boolean,System.Any).
[9:13, 9:26] Could not resolve call to operator Xor with signature (System.Boolean,System.Any).
[10:13, 10:25] Could not resolve call to operator Xor with signature (System.Any,System.Any).
[11:13, 11:25] Could not resolve call to operator Xor with signature (System.Any,System.Boolean).
[12:13, 12:26] Could not resolve call to operator Xor with signature (System.Any,System.Boolean).
###
module.exports['XOr'] = {
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
            "name" : "TT",
            "context" : "Patient",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "true",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TF",
            "context" : "Patient",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "true",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "false",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TN",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "FF",
            "context" : "Patient",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "false",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "false",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FT",
            "context" : "Patient",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "false",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FN",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NN",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NT",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NF",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         } ]
      }
   }
}

### Not
library TestSnippet version '1'
using QUICK
context Patient
define NotTrue = not true
define NotFalse = not false
define NotNull = not null
###

###
Translation Error(s):
[6:18, 6:25] Could not resolve call to operator Not with signature (System.Any).
###
module.exports['Not'] = {
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
            "name" : "NotTrue",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "true",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "NotFalse",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                  "value" : "false",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "NotNull",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         } ]
      }
   }
}

