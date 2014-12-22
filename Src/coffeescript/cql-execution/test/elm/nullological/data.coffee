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
            "name" : "Nil",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "One",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
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
define ZeroAndB = IfNull(0, 'b')
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
            "name" : "NullAndA",
            "context" : "Patient",
            "expression" : {
               "name" : "IfNull",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
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
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "0",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "b",
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
            "name" : "NullNullHelloNullWorld",
            "context" : "Patient",
            "expression" : {
               "name" : "Coalesce",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "type" : "Null"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "Hello",
                  "type" : "Literal"
               }, {
                  "type" : "Null"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "World",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FooNullNullBar",
            "context" : "Patient",
            "expression" : {
               "name" : "Coalesce",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "Foo",
                  "type" : "Literal"
               }, {
                  "type" : "Null"
               }, {
                  "type" : "Null"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "Bar",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "AllNull",
            "context" : "Patient",
            "expression" : {
               "name" : "Coalesce",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "type" : "Null"
               }, {
                  "type" : "Null"
               } ]
            }
         } ]
      }
   }
}

