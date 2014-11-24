###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit cql-test-data.txt to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-js:generateTestData
###

### InAgeDemographic
library TestSnippet version '1'
using QUICK
parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))

context Patient

define InDemographic =
    AgeAt(start of MeasurementPeriod) >= 2 and AgeAt(start of MeasurementPeriod) < 18
###

module.exports.InAgeDemographic = {
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
      "parameters" : {
         "def" : [ {
            "name" : "MeasurementPeriod",
            "default" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2014",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               }
            }
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
            "name" : "InDemographic",
            "context" : "Patient",
            "expression" : {
               "type" : "And",
               "operand" : [ {
                  "type" : "GreaterOrEqual",
                  "operand" : [ {
                     "name" : "AgeAt",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "type" : "Start",
                        "operand" : {
                           "name" : "MeasurementPeriod",
                           "type" : "ParameterRef"
                        }
                     } ]
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "Less",
                  "operand" : [ {
                     "name" : "AgeAt",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "type" : "Start",
                        "operand" : {
                           "name" : "MeasurementPeriod",
                           "type" : "ParameterRef"
                        }
                     } ]
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "18",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

### ExpressionDef
library TestSnippet version '1'
using QUICK
context Patient
define Foo = 'Bar'
###

module.exports.ExpressionDef = {
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
            "name" : "Foo",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
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
define Life = 42
define Foo = Life
###

module.exports.ExpressionRef = {
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
            "name" : "Life",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "42",
               "type" : "Literal"
            }
         }, {
            "name" : "Foo",
            "context" : "Patient",
            "expression" : {
               "name" : "Life",
               "type" : "ExpressionRef"
            }
         } ]
      }
   }
}

### ParameterDef
library TestSnippet version '1'
using QUICK
parameter MeasureYear default 2012
###

module.exports.ParameterDef = {
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
      "parameters" : {
         "def" : [ {
            "name" : "MeasureYear",
            "default" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "2012",
               "type" : "Literal"
            }
         } ]
      }
   }
}

### ParameterRef
library TestSnippet version '1'
using QUICK
parameter FooP default 'Bar'
context Patient
define Foo = FooP
###

module.exports.ParameterRef = {
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
      "parameters" : {
         "def" : [ {
            "name" : "FooP",
            "default" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
               "value" : "Bar",
               "type" : "Literal"
            }
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
            "name" : "Foo",
            "context" : "Patient",
            "expression" : {
               "name" : "FooP",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

### ValueSetDef
library TestSnippet version '1'
using QUICK
valueset "Known" = '2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Unknown One Arg" = '1.2.3.4.5.6.7.8.9'
valueset "Unknown Two Arg" = '1.2.3.4.5.6.7.8.9' version '1'
###

module.exports.ValueSetDef = {
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
      "valueSets" : {
         "def" : [ {
            "name" : "Known",
            "id" : "2.16.840.1.113883.3.464.1003.101.12.1061"
         }, {
            "name" : "Unknown One Arg",
            "id" : "1.2.3.4.5.6.7.8.9"
         }, {
            "name" : "Unknown Two Arg",
            "id" : "1.2.3.4.5.6.7.8.9",
            "version" : "1"
         } ]
      }
   }
}

### ValueSetRef
library TestSnippet version '1'
using QUICK
valueset "Acute Pharyngitis" = '2.16.840.1.113883.3.464.1003.101.12.1001'
context Patient
define Foo = "Acute Pharyngitis"
###

module.exports.ValueSetRef = {
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
      "valueSets" : {
         "def" : [ {
            "name" : "Acute Pharyngitis",
            "id" : "2.16.840.1.113883.3.464.1003.101.12.1001"
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
            "name" : "Foo",
            "context" : "Patient",
            "expression" : {
               "name" : "Acute Pharyngitis",
               "type" : "ValueSetRef"
            }
         } ]
      }
   }
}

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

module.exports.And = {
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
            "name" : "TT",
            "context" : "Patient",
            "expression" : {
               "type" : "And",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
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
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TN",
            "context" : "Patient",
            "expression" : {
               "type" : "And",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               }, {
                  "type" : "Null"
               } ]
            }
         }, {
            "name" : "FF",
            "context" : "Patient",
            "expression" : {
               "type" : "And",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
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
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FN",
            "context" : "Patient",
            "expression" : {
               "type" : "And",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               }, {
                  "type" : "Null"
               } ]
            }
         }, {
            "name" : "NN",
            "context" : "Patient",
            "expression" : {
               "type" : "And",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "type" : "Null"
               } ]
            }
         }, {
            "name" : "NT",
            "context" : "Patient",
            "expression" : {
               "type" : "And",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "NF",
            "context" : "Patient",
            "expression" : {
               "type" : "And",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               } ]
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

module.exports.Or = {
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
            "name" : "TT",
            "context" : "Patient",
            "expression" : {
               "type" : "Or",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
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
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TN",
            "context" : "Patient",
            "expression" : {
               "type" : "Or",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               }, {
                  "type" : "Null"
               } ]
            }
         }, {
            "name" : "FF",
            "context" : "Patient",
            "expression" : {
               "type" : "Or",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
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
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FN",
            "context" : "Patient",
            "expression" : {
               "type" : "Or",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               }, {
                  "type" : "Null"
               } ]
            }
         }, {
            "name" : "NN",
            "context" : "Patient",
            "expression" : {
               "type" : "Or",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "type" : "Null"
               } ]
            }
         }, {
            "name" : "NT",
            "context" : "Patient",
            "expression" : {
               "type" : "Or",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "NF",
            "context" : "Patient",
            "expression" : {
               "type" : "Or",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               } ]
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

module.exports.XOr = {
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
            "name" : "TT",
            "context" : "Patient",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
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
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TN",
            "context" : "Patient",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               }, {
                  "type" : "Null"
               } ]
            }
         }, {
            "name" : "FF",
            "context" : "Patient",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
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
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FN",
            "context" : "Patient",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               }, {
                  "type" : "Null"
               } ]
            }
         }, {
            "name" : "NN",
            "context" : "Patient",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "type" : "Null"
               } ]
            }
         }, {
            "name" : "NT",
            "context" : "Patient",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "NF",
            "context" : "Patient",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               } ]
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

module.exports.Not = {
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
            "name" : "NotTrue",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
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
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "NotNull",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Null"
               }
            }
         } ]
      }
   }
}

### AgeAtFunctionRef
library TestSnippet version '1'
using QUICK
context Patient
define AgeAt2012 = AgeAt(Date(2012))
define AgeAt19810216 = AgeAt(Date(1981, 2, 16))
define AgeAt1975 = AgeAt(Date(1975))
###

module.exports.AgeAtFunctionRef = {
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
            "name" : "AgeAt2012",
            "context" : "Patient",
            "expression" : {
               "name" : "AgeAt",
               "type" : "FunctionRef",
               "operand" : [ {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "AgeAt19810216",
            "context" : "Patient",
            "expression" : {
               "name" : "AgeAt",
               "type" : "FunctionRef",
               "operand" : [ {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1981",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "16",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "AgeAt1975",
            "context" : "Patient",
            "expression" : {
               "name" : "AgeAt",
               "type" : "FunctionRef",
               "operand" : [ {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1975",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

### DateFunctionRef
library TestSnippet version '1'
using QUICK
context Patient
define Year = Date(2012)
define Month = Date(2012, 4)
define Day = Date(2012, 4, 15)
define Hour = Date(2012, 4, 15, 12)
define Minute = Date(2012, 4, 15, 12, 10)
define Second = Date(2012, 4, 15, 12, 10, 59)
define Millisecond = Date(2012, 4, 15, 12, 10, 59, 456)
define TimeZoneOffset = Date(2012, 4, 15, 12, 10, 59, 456, -5)
###

module.exports.DateFunctionRef = {
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
            "name" : "Year",
            "context" : "Patient",
            "expression" : {
               "name" : "Date",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2012",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Month",
            "context" : "Patient",
            "expression" : {
               "name" : "Date",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2012",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Day",
            "context" : "Patient",
            "expression" : {
               "name" : "Date",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2012",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "15",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Hour",
            "context" : "Patient",
            "expression" : {
               "name" : "Date",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2012",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "15",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "12",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Minute",
            "context" : "Patient",
            "expression" : {
               "name" : "Date",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2012",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "15",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "12",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "10",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Second",
            "context" : "Patient",
            "expression" : {
               "name" : "Date",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2012",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "15",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "12",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "10",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "59",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Millisecond",
            "context" : "Patient",
            "expression" : {
               "name" : "Date",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2012",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "15",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "12",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "10",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "59",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "456",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TimeZoneOffset",
            "context" : "Patient",
            "expression" : {
               "name" : "Date",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2012",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "15",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "12",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "10",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "59",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "456",
                  "type" : "Literal"
               }, {
                  "type" : "Negate",
                  "operand" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }
               } ]
            }
         } ]
      }
   }
}

### Interval
library TestSnippet version '1'
using QUICK
context Patient
define Open = interval(Date(2012, 1, 1), Date(2013, 1, 1))
define LeftOpen = interval(Date(2012, 1, 1), Date(2013, 1, 1)]
define RightOpen = interval[Date(2012, 1, 1), Date(2013, 1, 1))
define Closed = interval[Date(2012, 1, 1), Date(2013, 1, 1)]
###

module.exports.Interval = {
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
            "name" : "Open",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : false,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "LeftOpen",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : false,
               "highClosed" : true,
               "type" : "Interval",
               "low" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "RightOpen",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "Closed",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : true,
               "type" : "Interval",
               "low" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               }
            }
         } ]
      }
   }
}

### Greater
library TestSnippet version '1'
using QUICK
context Patient
define AGtB_Int = 5 > 4
define AEqB_Int = 5 > 5
define ALtB_Int = 5 > 6
###

module.exports.Greater = {
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
            "name" : "AGtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "AEqB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ALtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "6",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

### GreaterOrEqual
library TestSnippet version '1'
using QUICK
context Patient
define AGtB_Int = 5 >= 4
define AEqB_Int = 5 >= 5
define ALtB_Int = 5 >= 6
###

module.exports.GreaterOrEqual = {
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
            "name" : "AGtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "AEqB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ALtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "6",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

### Equal
library TestSnippet version '1'
using QUICK
context Patient
define AGtB_Int = 5 = 4
define AEqB_Int = 5 = 5
define ALtB_Int = 5 = 6
###

module.exports.Equal = {
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
            "name" : "AGtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "AEqB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ALtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "6",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

### LessOrEqual
library TestSnippet version '1'
using QUICK
context Patient
define AGtB_Int = 5 <= 4
define AEqB_Int = 5 <= 5
define ALtB_Int = 5 <= 6
###

module.exports.LessOrEqual = {
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
            "name" : "AGtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "AEqB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ALtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "6",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

### Less
library TestSnippet version '1'
using QUICK
context Patient
define AGtB_Int = 5 < 4
define AEqB_Int = 5 < 5
define ALtB_Int = 5 < 6
###

module.exports.Less = {
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
            "name" : "AGtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "AEqB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ALtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "6",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

### List
library TestSnippet version '1'
using QUICK
context Patient
define Three = 1 + 2
define IntList = { 9, 7, 8 }
define StringList = { 'a', 'bee', 'see' }
define MixedList = { 1, 'two', Three }
define EmptyList = {}
###

module.exports.List = {
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
            "name" : "Three",
            "context" : "Patient",
            "expression" : {
               "type" : "Add",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "IntList",
            "context" : "Patient",
            "expression" : {
               "type" : "List",
               "element" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "9",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "7",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "8",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "StringList",
            "context" : "Patient",
            "expression" : {
               "type" : "List",
               "element" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "a",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "bee",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "see",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "MixedList",
            "context" : "Patient",
            "expression" : {
               "type" : "List",
               "element" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "two",
                  "type" : "Literal"
               }, {
                  "name" : "Three",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "EmptyList",
            "context" : "Patient",
            "expression" : {
               "type" : "List"
            }
         } ]
      }
   }
}

### Exists
library TestSnippet version '1'
using QUICK
context Patient
define EmptyList = exists ({})
define FullList = exists ({ 1, 2, 3 })
###

module.exports.Exists = {
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
            "name" : "EmptyList",
            "context" : "Patient",
            "expression" : {
               "type" : "Exists",
               "operand" : {
                  "type" : "List"
               }
            }
         }, {
            "name" : "FullList",
            "context" : "Patient",
            "expression" : {
               "type" : "Exists",
               "operand" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               }
            }
         } ]
      }
   }
}

### Start
library TestSnippet version '1'
using QUICK
context Patient
define Foo = start of interval[Date(2012, 1, 1), Date(2013, 1, 1)]
###

module.exports.Start = {
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
            "name" : "Foo",
            "context" : "Patient",
            "expression" : {
               "type" : "Start",
               "operand" : {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "name" : "Date",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     } ]
                  },
                  "high" : {
                     "name" : "Date",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2013",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     } ]
                  }
               }
            }
         } ]
      }
   }
}

### InList
library TestSnippet version '1'
using QUICK
context Patient
define IsIn = 4 in { 3, 4, 5 }
define IsNotIn = 4 in { 3, 5, 6 }
###

module.exports.InList = {
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
            "name" : "IsIn",
            "context" : "Patient",
            "expression" : {
               "type" : "In",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IsNotIn",
            "context" : "Patient",
            "expression" : {
               "type" : "In",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

### InValueSet
library TestSnippet version '1'
using QUICK
valueset "Female" = '2.16.840.1.113883.3.560.100.2'
valueset "Versioned Female" = '2.16.840.1.113883.3.560.100.2' version '20121025'
context Patient
define String = 'F' in "Female"
define StringInVersionedValueSet = 'F' in "Versioned Female"
define ShortCode = Code('F') in "Female"
define MediumCode = Code('F', '2.16.840.1.113883.18.2') in "Female"
define LongCode = Code('F', '2.16.840.1.113883.18.2', 'HL7V2.5') in "Female"
define WrongString = 'M' in "Female"
define WrongStringInVersionedValueSet = 'M' in "Versioned Female"
define WrongShortCode = Code('M') in "Female"
define WrongMediumCode = Code('F', '3.16.840.1.113883.18.2') in "Female"
define WrongLongCode = Code('F', '2.16.840.1.113883.18.2', 'HL7V2.6') in "Female"
###

module.exports.InValueSet = {
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
      "valueSets" : {
         "def" : [ {
            "name" : "Female",
            "id" : "2.16.840.1.113883.3.560.100.2"
         }, {
            "name" : "Versioned Female",
            "id" : "2.16.840.1.113883.3.560.100.2",
            "version" : "20121025"
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
            "name" : "String",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "F",
                  "type" : "Literal"
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         }, {
            "name" : "StringInVersionedValueSet",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "F",
                  "type" : "Literal"
               },
               "valueset" : {
                  "name" : "Versioned Female"
               }
            }
         }, {
            "name" : "ShortCode",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "F",
                     "type" : "Literal"
                  } ]
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         }, {
            "name" : "MediumCode",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "F",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "2.16.840.1.113883.18.2",
                     "type" : "Literal"
                  } ]
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         }, {
            "name" : "LongCode",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "F",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "2.16.840.1.113883.18.2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "HL7V2.5",
                     "type" : "Literal"
                  } ]
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         }, {
            "name" : "WrongString",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "M",
                  "type" : "Literal"
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         }, {
            "name" : "WrongStringInVersionedValueSet",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "M",
                  "type" : "Literal"
               },
               "valueset" : {
                  "name" : "Versioned Female"
               }
            }
         }, {
            "name" : "WrongShortCode",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "M",
                     "type" : "Literal"
                  } ]
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         }, {
            "name" : "WrongMediumCode",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "F",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "3.16.840.1.113883.18.2",
                     "type" : "Literal"
                  } ]
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         }, {
            "name" : "WrongLongCode",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "F",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "2.16.840.1.113883.18.2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "HL7V2.6",
                     "type" : "Literal"
                  } ]
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         } ]
      }
   }
}

### InValueSetFunction
library TestSnippet version '1'
using QUICK
valueset "Female" = '2.16.840.1.113883.3.560.100.2'
valueset "Versioned Female" = '2.16.840.1.113883.3.560.100.2' version '20121025'
context Patient
define String = InValueSet('F', '2.16.840.1.113883.3.560.100.2')
define StringInVersionedValueSet = InValueSet('F', '2.16.840.1.113883.3.560.100.2', '20121025')
define ShortCode = InValueSet(Code('F'), '2.16.840.1.113883.3.560.100.2')
define MediumCode = InValueSet(Code('F', '2.16.840.1.113883.18.2'), '2.16.840.1.113883.3.560.100.2')
define LongCode = InValueSet(Code('F', '2.16.840.1.113883.18.2', 'HL7V2.5'), '2.16.840.1.113883.3.560.100.2')
define WrongString = InValueSet('M', '2.16.840.1.113883.3.560.100.2')
define WrongStringInVersionedValueSet = InValueSet('M', '2.16.840.1.113883.3.560.100.2', '20121025')
define WrongShortCode = InValueSet(Code('M'), '2.16.840.1.113883.3.560.100.2')
define WrongMediumCode = InValueSet(Code('F', '3.16.840.1.113883.18.2'), '2.16.840.1.113883.3.560.100.2')
define WrongLongCode = InValueSet(Code('F', '2.16.840.1.113883.18.2', 'HL7V2.6'), '2.16.840.1.113883.3.560.100.2')
###

module.exports.InValueSetFunction = {
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
      "valueSets" : {
         "def" : [ {
            "name" : "Female",
            "id" : "2.16.840.1.113883.3.560.100.2"
         }, {
            "name" : "Versioned Female",
            "id" : "2.16.840.1.113883.3.560.100.2",
            "version" : "20121025"
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
            "name" : "String",
            "context" : "Patient",
            "expression" : {
               "name" : "InValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "F",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.560.100.2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "StringInVersionedValueSet",
            "context" : "Patient",
            "expression" : {
               "name" : "InValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "F",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.560.100.2",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "20121025",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ShortCode",
            "context" : "Patient",
            "expression" : {
               "name" : "InValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "F",
                     "type" : "Literal"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.560.100.2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "MediumCode",
            "context" : "Patient",
            "expression" : {
               "name" : "InValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "F",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "2.16.840.1.113883.18.2",
                     "type" : "Literal"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.560.100.2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "LongCode",
            "context" : "Patient",
            "expression" : {
               "name" : "InValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "F",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "2.16.840.1.113883.18.2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "HL7V2.5",
                     "type" : "Literal"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.560.100.2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "WrongString",
            "context" : "Patient",
            "expression" : {
               "name" : "InValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "M",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.560.100.2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "WrongStringInVersionedValueSet",
            "context" : "Patient",
            "expression" : {
               "name" : "InValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "M",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.560.100.2",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "20121025",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "WrongShortCode",
            "context" : "Patient",
            "expression" : {
               "name" : "InValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "M",
                     "type" : "Literal"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.560.100.2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "WrongMediumCode",
            "context" : "Patient",
            "expression" : {
               "name" : "InValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "F",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "3.16.840.1.113883.18.2",
                     "type" : "Literal"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.560.100.2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "WrongLongCode",
            "context" : "Patient",
            "expression" : {
               "name" : "InValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "F",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "2.16.840.1.113883.18.2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "HL7V2.6",
                     "type" : "Literal"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.560.100.2",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

### PatientPropertyInValueSet
library TestSnippet version '1'
using QUICK
valueset "Female" = '2.16.840.1.113883.3.560.100.2'
context Patient
define IsFemale = Patient.gender in "Female"
###

module.exports.PatientPropertyInValueSet = {
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
      "valueSets" : {
         "def" : [ {
            "name" : "Female",
            "id" : "2.16.840.1.113883.3.560.100.2"
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
            "name" : "IsFemale",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "path" : "gender",
                  "type" : "Property",
                  "source" : {
                     "name" : "Patient",
                     "type" : "ExpressionRef"
                  }
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         } ]
      }
   }
}

### Union
library TestSnippet version '1'
using QUICK
context Patient
define OneToTen = {1, 2, 3, 4, 5} union {6, 7, 8, 9, 10}
define OneToFiveOverlapped = {1, 2, 3, 4} union {3, 4, 5}
define Disjoint = {1, 2} union {4, 5}
define NestedToFifteen = {1, 2, 3} union {4, 5, 6} union {7 ,8 , 9} union {10, 11, 12} union {13, 14, 15}
###

module.exports.Union = {
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
            "name" : "OneToTen",
            "context" : "Patient",
            "expression" : {
               "type" : "Union",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "7",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "9",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "10",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "OneToFiveOverlapped",
            "context" : "Patient",
            "expression" : {
               "type" : "Union",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "Disjoint",
            "context" : "Patient",
            "expression" : {
               "type" : "Union",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "NestedToFifteen",
            "context" : "Patient",
            "expression" : {
               "type" : "Union",
               "operand" : [ {
                  "type" : "Union",
                  "operand" : [ {
                     "type" : "Union",
                     "operand" : [ {
                        "type" : "Union",
                        "operand" : [ {
                           "type" : "List",
                           "element" : [ {
                              "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                              "value" : "1",
                              "type" : "Literal"
                           }, {
                              "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                              "value" : "2",
                              "type" : "Literal"
                           }, {
                              "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                              "value" : "3",
                              "type" : "Literal"
                           } ]
                        }, {
                           "type" : "List",
                           "element" : [ {
                              "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                              "value" : "4",
                              "type" : "Literal"
                           }, {
                              "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                              "value" : "5",
                              "type" : "Literal"
                           }, {
                              "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                              "value" : "6",
                              "type" : "Literal"
                           } ]
                        } ]
                     }, {
                        "type" : "List",
                        "element" : [ {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "7",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "8",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "9",
                           "type" : "Literal"
                        } ]
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "10",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "11",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "12",
                        "type" : "Literal"
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "13",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "14",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "15",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

### Intersect
library TestSnippet version '1'
using QUICK
context Patient
define NoIntersection = {1, 2, 3} intersect {4, 5, 6}
define IntersectOnFive = {4, 5, 6} intersect {1, 3, 5, 7}
define IntersectOnEvens = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10} intersect {0, 2, 4, 6, 8, 10, 12}
define IntersectOnAll = {1, 2, 3, 4, 5} intersect {5, 4, 3, 2, 1}
define NestedIntersects = {1, 2, 3, 4, 5} intersect {2, 3, 4, 5, 6} intersect {3, 4, 5, 6, 7} intersect {4, 5, 6, 7, 8}
###

module.exports.Intersect = {
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
            "name" : "NoIntersection",
            "context" : "Patient",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IntersectOnFive",
            "context" : "Patient",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "7",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IntersectOnEvens",
            "context" : "Patient",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "7",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "9",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "10",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "10",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "12",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IntersectOnAll",
            "context" : "Patient",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "NestedIntersects",
            "context" : "Patient",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "Intersect",
                  "operand" : [ {
                     "type" : "Intersect",
                     "operand" : [ {
                        "type" : "List",
                        "element" : [ {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "5",
                           "type" : "Literal"
                        } ]
                     }, {
                        "type" : "List",
                        "element" : [ {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "5",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "6",
                           "type" : "Literal"
                        } ]
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "6",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "7",
                        "type" : "Literal"
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "7",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "8",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

### Distinct
library TestSnippet version '1'
using QUICK
context Patient
define LotsOfDups = distinct {1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5, 4, 3, 2, 1}
define NoDups = distinct {2, 4, 6, 8, 10}
###

module.exports.Distinct = {
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
            "name" : "LotsOfDups",
            "context" : "Patient",
            "expression" : {
               "type" : "Distinct",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "NoDups",
            "context" : "Patient",
            "expression" : {
               "type" : "Distinct",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "10",
                     "type" : "Literal"
                  } ]
               }
            }
         } ]
      }
   }
}

### Add
library TestSnippet version '1'
using QUICK
context Patient
define Ten = 10
define Eleven = 11
define OnePlusTwo = 1 + 2
define AddMultiple = 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 + 10
define AddVariables = Ten + Eleven
###

module.exports.Add = {
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
            "name" : "Ten",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "10",
               "type" : "Literal"
            }
         }, {
            "name" : "Eleven",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "11",
               "type" : "Literal"
            }
         }, {
            "name" : "OnePlusTwo",
            "context" : "Patient",
            "expression" : {
               "type" : "Add",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "AddMultiple",
            "context" : "Patient",
            "expression" : {
               "type" : "Add",
               "operand" : [ {
                  "type" : "Add",
                  "operand" : [ {
                     "type" : "Add",
                     "operand" : [ {
                        "type" : "Add",
                        "operand" : [ {
                           "type" : "Add",
                           "operand" : [ {
                              "type" : "Add",
                              "operand" : [ {
                                 "type" : "Add",
                                 "operand" : [ {
                                    "type" : "Add",
                                    "operand" : [ {
                                       "type" : "Add",
                                       "operand" : [ {
                                          "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                                          "value" : "1",
                                          "type" : "Literal"
                                       }, {
                                          "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                                          "value" : "2",
                                          "type" : "Literal"
                                       } ]
                                    }, {
                                       "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                                       "value" : "3",
                                       "type" : "Literal"
                                    } ]
                                 }, {
                                    "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                                    "value" : "4",
                                    "type" : "Literal"
                                 } ]
                              }, {
                                 "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                                 "value" : "5",
                                 "type" : "Literal"
                              } ]
                           }, {
                              "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                              "value" : "6",
                              "type" : "Literal"
                           } ]
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "7",
                           "type" : "Literal"
                        } ]
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "8",
                        "type" : "Literal"
                     } ]
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "9",
                     "type" : "Literal"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "10",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "AddVariables",
            "context" : "Patient",
            "expression" : {
               "type" : "Add",
               "operand" : [ {
                  "name" : "Ten",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "Eleven",
                  "type" : "ExpressionRef"
               } ]
            }
         } ]
      }
   }
}

### Subtract
library TestSnippet version '1'
using QUICK
context Patient
define Ten = 10
define Eleven = 11
define FiveMinusTwo = 5 - 2
define SubtractMultiple = 100 - 50 - 25 - 10
define SubtractVariables = Eleven - Ten
###

module.exports.Subtract = {
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
            "name" : "Ten",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "10",
               "type" : "Literal"
            }
         }, {
            "name" : "Eleven",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "11",
               "type" : "Literal"
            }
         }, {
            "name" : "FiveMinusTwo",
            "context" : "Patient",
            "expression" : {
               "type" : "Subtract",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "SubtractMultiple",
            "context" : "Patient",
            "expression" : {
               "type" : "Subtract",
               "operand" : [ {
                  "type" : "Subtract",
                  "operand" : [ {
                     "type" : "Subtract",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "100",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "50",
                        "type" : "Literal"
                     } ]
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "25",
                     "type" : "Literal"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "10",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "SubtractVariables",
            "context" : "Patient",
            "expression" : {
               "type" : "Subtract",
               "operand" : [ {
                  "name" : "Eleven",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "Ten",
                  "type" : "ExpressionRef"
               } ]
            }
         } ]
      }
   }
}

### Multiply
library TestSnippet version '1'
using QUICK
context Patient
define Ten = 10
define Eleven = 11
define FiveTimesTwo = 5 * 2
define MultiplyMultiple = 1 * 2 * 3 * 4 * 5
define MultiplyVariables = Eleven * Ten
###

module.exports.Multiply = {
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
            "name" : "Ten",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "10",
               "type" : "Literal"
            }
         }, {
            "name" : "Eleven",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "11",
               "type" : "Literal"
            }
         }, {
            "name" : "FiveTimesTwo",
            "context" : "Patient",
            "expression" : {
               "type" : "Multiply",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "MultiplyMultiple",
            "context" : "Patient",
            "expression" : {
               "type" : "Multiply",
               "operand" : [ {
                  "type" : "Multiply",
                  "operand" : [ {
                     "type" : "Multiply",
                     "operand" : [ {
                        "type" : "Multiply",
                        "operand" : [ {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "2",
                           "type" : "Literal"
                        } ]
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "3",
                        "type" : "Literal"
                     } ]
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "MultiplyVariables",
            "context" : "Patient",
            "expression" : {
               "type" : "Multiply",
               "operand" : [ {
                  "name" : "Eleven",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "Ten",
                  "type" : "ExpressionRef"
               } ]
            }
         } ]
      }
   }
}

### Divide
library TestSnippet version '1'
using QUICK
context Patient
define Hundred = 100
define Four = 4
define TenDividedByTwo = 10 / 2
define TenDividedByFour = 10 / 4
define DivideMultiple = 1000 / 4 / 10 / 5
define DivideVariables = Hundred / Four
###

module.exports.Divide = {
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
            "name" : "Hundred",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "100",
               "type" : "Literal"
            }
         }, {
            "name" : "Four",
            "context" : "Patient",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "4",
               "type" : "Literal"
            }
         }, {
            "name" : "TenDividedByTwo",
            "context" : "Patient",
            "expression" : {
               "type" : "Divide",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "10",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TenDividedByFour",
            "context" : "Patient",
            "expression" : {
               "type" : "Divide",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "10",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "DivideMultiple",
            "context" : "Patient",
            "expression" : {
               "type" : "Divide",
               "operand" : [ {
                  "type" : "Divide",
                  "operand" : [ {
                     "type" : "Divide",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1000",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "4",
                        "type" : "Literal"
                     } ]
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "10",
                     "type" : "Literal"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "DivideVariables",
            "context" : "Patient",
            "expression" : {
               "type" : "Divide",
               "operand" : [ {
                  "name" : "Hundred",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "Four",
                  "type" : "ExpressionRef"
               } ]
            }
         } ]
      }
   }
}

### Negate
library TestSnippet version '1'
using QUICK
context Patient
define NegativeOne = -1
###

module.exports.Negate = {
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
            "name" : "NegativeOne",
            "context" : "Patient",
            "expression" : {
               "type" : "Negate",
               "operand" : {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "1",
                  "type" : "Literal"
               }
            }
         } ]
      }
   }
}

### MathPrecedence
library TestSnippet version '1'
using QUICK
context Patient
define Mixed = 1 + 5 * 10 - 15 / 3
define Parenthetical = (1 + 5) * (10 - 15) / 3
###

module.exports.MathPrecedence = {
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
            "name" : "Mixed",
            "context" : "Patient",
            "expression" : {
               "type" : "Subtract",
               "operand" : [ {
                  "type" : "Add",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "type" : "Multiply",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "10",
                        "type" : "Literal"
                     } ]
                  } ]
               }, {
                  "type" : "Divide",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "15",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "Parenthetical",
            "context" : "Patient",
            "expression" : {
               "type" : "Divide",
               "operand" : [ {
                  "type" : "Multiply",
                  "operand" : [ {
                     "type" : "Add",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "5",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "Subtract",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "10",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "15",
                        "type" : "Literal"
                     } ]
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "3",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

### DurationBetween
library TestSnippet version '1'
using QUICK
context Patient
define NewYear2013 = Date(2013, 1, 1, 0, 0, 0, 0)
define NewYear2014 = Date(2014, 1, 1, 0, 0, 0, 0)
define January2014 = Date(2014, 1)
define YearsBetween = years between NewYear2013 and NewYear2014
define MonthsBetween = months between NewYear2013 and NewYear2014
define DaysBetween = days between NewYear2013 and NewYear2014
define HoursBetween = hours between NewYear2013 and NewYear2014
define MinutesBetween = minutes between NewYear2013 and NewYear2014
define SecondsBetween = seconds between NewYear2013 and NewYear2014
define MillisecondsBetween = milliseconds between NewYear2013 and NewYear2014
define MillisecondsBetweenReversed = milliseconds between NewYear2014 and NewYear2013
define YearsBetweenUncertainty = years between NewYear2014 and January2014
define MonthsBetweenUncertainty = months between NewYear2014 and January2014
define DaysBetweenUncertainty = days between NewYear2014 and January2014
define HoursBetweenUncertainty = hours between NewYear2014 and January2014
define MinutesBetweenUncertainty = minutes between NewYear2014 and January2014
define SecondsBetweenUncertainty = seconds between NewYear2014 and January2014
define MillisecondsBetweenUncertainty = milliseconds between NewYear2014 and January2014
define MillisecondsBetweenReversedUncertainty = milliseconds between January2014 and NewYear2014
###

module.exports.DurationBetween = {
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
            "name" : "NewYear2013",
            "context" : "Patient",
            "expression" : {
               "name" : "Date",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2013",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "0",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "0",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "0",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "0",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "NewYear2014",
            "context" : "Patient",
            "expression" : {
               "name" : "Date",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2014",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "0",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "0",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "0",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "0",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "January2014",
            "context" : "Patient",
            "expression" : {
               "name" : "Date",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2014",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "1",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "YearsBetween",
            "context" : "Patient",
            "expression" : {
               "precision" : "Year",
               "type" : "DurationBetween",
               "operand" : [ {
                  "name" : "NewYear2013",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "NewYear2014",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "MonthsBetween",
            "context" : "Patient",
            "expression" : {
               "precision" : "Month",
               "type" : "DurationBetween",
               "operand" : [ {
                  "name" : "NewYear2013",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "NewYear2014",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "DaysBetween",
            "context" : "Patient",
            "expression" : {
               "precision" : "Day",
               "type" : "DurationBetween",
               "operand" : [ {
                  "name" : "NewYear2013",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "NewYear2014",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "HoursBetween",
            "context" : "Patient",
            "expression" : {
               "precision" : "Hour",
               "type" : "DurationBetween",
               "operand" : [ {
                  "name" : "NewYear2013",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "NewYear2014",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "MinutesBetween",
            "context" : "Patient",
            "expression" : {
               "precision" : "Minute",
               "type" : "DurationBetween",
               "operand" : [ {
                  "name" : "NewYear2013",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "NewYear2014",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "SecondsBetween",
            "context" : "Patient",
            "expression" : {
               "precision" : "Second",
               "type" : "DurationBetween",
               "operand" : [ {
                  "name" : "NewYear2013",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "NewYear2014",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "MillisecondsBetween",
            "context" : "Patient",
            "expression" : {
               "precision" : "Millisecond",
               "type" : "DurationBetween",
               "operand" : [ {
                  "name" : "NewYear2013",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "NewYear2014",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "MillisecondsBetweenReversed",
            "context" : "Patient",
            "expression" : {
               "precision" : "Millisecond",
               "type" : "DurationBetween",
               "operand" : [ {
                  "name" : "NewYear2014",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "NewYear2013",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "YearsBetweenUncertainty",
            "context" : "Patient",
            "expression" : {
               "precision" : "Year",
               "type" : "DurationBetween",
               "operand" : [ {
                  "name" : "NewYear2014",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "January2014",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "MonthsBetweenUncertainty",
            "context" : "Patient",
            "expression" : {
               "precision" : "Month",
               "type" : "DurationBetween",
               "operand" : [ {
                  "name" : "NewYear2014",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "January2014",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "DaysBetweenUncertainty",
            "context" : "Patient",
            "expression" : {
               "precision" : "Day",
               "type" : "DurationBetween",
               "operand" : [ {
                  "name" : "NewYear2014",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "January2014",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "HoursBetweenUncertainty",
            "context" : "Patient",
            "expression" : {
               "precision" : "Hour",
               "type" : "DurationBetween",
               "operand" : [ {
                  "name" : "NewYear2014",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "January2014",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "MinutesBetweenUncertainty",
            "context" : "Patient",
            "expression" : {
               "precision" : "Minute",
               "type" : "DurationBetween",
               "operand" : [ {
                  "name" : "NewYear2014",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "January2014",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "SecondsBetweenUncertainty",
            "context" : "Patient",
            "expression" : {
               "precision" : "Second",
               "type" : "DurationBetween",
               "operand" : [ {
                  "name" : "NewYear2014",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "January2014",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "MillisecondsBetweenUncertainty",
            "context" : "Patient",
            "expression" : {
               "precision" : "Millisecond",
               "type" : "DurationBetween",
               "operand" : [ {
                  "name" : "NewYear2014",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "January2014",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "MillisecondsBetweenReversedUncertainty",
            "context" : "Patient",
            "expression" : {
               "precision" : "Millisecond",
               "type" : "DurationBetween",
               "operand" : [ {
                  "name" : "January2014",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "NewYear2014",
                  "type" : "ExpressionRef"
               } ]
            }
         } ]
      }
   }
}

### DurationBetweenComparisons
library TestSnippet version '1'
using QUICK
context Patient
define NewYear2014 = Date(2014, 1, 1, 0, 0, 0, 0)
define February2014 = Date(2014, 2)
define GreaterThan25DaysAfter = days between NewYear2014 and February2014 > 25
define GreaterThan40DaysAfter = days between NewYear2014 and February2014 > 40
define GreaterThan80DaysAfter = days between NewYear2014 and February2014 > 80
define GreaterOrEqualTo25DaysAfter = days between NewYear2014 and February2014 >= 25
define GreaterOrEqualTo40DaysAfter = days between NewYear2014 and February2014 >= 40
define GreaterOrEqualTo80DaysAfter = days between NewYear2014 and February2014 >= 80
define EqualTo25DaysAfter = days between NewYear2014 and February2014 = 25
define EqualTo40DaysAfter = days between NewYear2014 and February2014 = 40
define EqualTo80DaysAfter = days between NewYear2014 and February2014 = 80
define LessOrEqualTo25DaysAfter = days between NewYear2014 and February2014 <= 25
define LessOrEqualTo40DaysAfter = days between NewYear2014 and February2014 <= 40
define LessOrEqualTo80DaysAfter = days between NewYear2014 and February2014 <= 80
define LessThan25DaysAfter = days between NewYear2014 and February2014 < 25
define LessThan40DaysAfter = days between NewYear2014 and February2014 < 40
define LessThan80DaysAfter = days between NewYear2014 and February2014 < 80
define TwentyFiveDaysLessThanDaysBetween = 25 < days between NewYear2014 and February2014
define FortyDaysEqualToDaysBetween = 40 = days between NewYear2014 and February2014
define TwentyFiveDaysGreaterThanDaysBetween = 25 > days between NewYear2014 and February2014
###

module.exports.DurationBetweenComparisons = {
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
            "name" : "NewYear2014",
            "context" : "Patient",
            "expression" : {
               "name" : "Date",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2014",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "0",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "0",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "0",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "0",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "February2014",
            "context" : "Patient",
            "expression" : {
               "name" : "Date",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2014",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "GreaterThan25DaysAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "25",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "GreaterThan40DaysAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "40",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "GreaterThan80DaysAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "80",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "GreaterOrEqualTo25DaysAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "25",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "GreaterOrEqualTo40DaysAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "40",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "GreaterOrEqualTo80DaysAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "80",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "EqualTo25DaysAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "25",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "EqualTo40DaysAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "40",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "EqualTo80DaysAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "80",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "LessOrEqualTo25DaysAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "25",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "LessOrEqualTo40DaysAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "40",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "LessOrEqualTo80DaysAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "80",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "LessThan25DaysAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "25",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "LessThan40DaysAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "40",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "LessThan80DaysAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "80",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TwentyFiveDaysLessThanDaysBetween",
            "context" : "Patient",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "25",
                  "type" : "Literal"
               }, {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               } ]
            }
         }, {
            "name" : "FortyDaysEqualToDaysBetween",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "40",
                  "type" : "Literal"
               }, {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               } ]
            }
         }, {
            "name" : "TwentyFiveDaysGreaterThanDaysBetween",
            "context" : "Patient",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "25",
                  "type" : "Literal"
               }, {
                  "precision" : "Day",
                  "type" : "DurationBetween",
                  "operand" : [ {
                     "name" : "NewYear2014",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "February2014",
                     "type" : "ExpressionRef"
                  } ]
               } ]
            }
         } ]
      }
   }
}

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

module.exports.Literal = {
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

### Nil
library TestSnippet version '1'
using QUICK
context Patient
define Nil = null
###

module.exports.Nil = {
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

### Retrieve
library TestSnippet version '1'
using QUICK
valueset "Acute Pharyngitis" = '2.16.840.1.113883.3.464.1003.102.12.1011'
valueset "Ambulatory/ED Visit" = '2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Annual Wellness Visit" = '2.16.840.1.113883.3.526.3.1240'
context Patient
define Conditions = [Condition]
define Encounters = [Encounter]
define PharyngitisConditions = [Condition: "Acute Pharyngitis"]
define AmbulatoryEncounters = [Encounter: "Ambulatory/ED Visit"]
define EncountersByServiceType = [Encounter: type in "Annual Wellness Visit"]
define WrongDataType = [EncounterProposal: "Ambulatory/ED Visit"]
define WrongValueSet = [Condition: "Ambulatory/ED Visit"]
define WrongCodeProperty = [Encounter: class in "Annual Wellness Visit"]
###

module.exports.Retrieve = {
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
      "valueSets" : {
         "def" : [ {
            "name" : "Acute Pharyngitis",
            "id" : "2.16.840.1.113883.3.464.1003.102.12.1011"
         }, {
            "name" : "Ambulatory/ED Visit",
            "id" : "2.16.840.1.113883.3.464.1003.101.12.1061"
         }, {
            "name" : "Annual Wellness Visit",
            "id" : "2.16.840.1.113883.3.526.3.1240"
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
            "name" : "Conditions",
            "context" : "Patient",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}Condition",
               "templateId" : "cqf-condition",
               "type" : "Retrieve"
            }
         }, {
            "name" : "Encounters",
            "context" : "Patient",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}Encounter",
               "templateId" : "cqf-encounter",
               "type" : "Retrieve"
            }
         }, {
            "name" : "PharyngitisConditions",
            "context" : "Patient",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}Condition",
               "templateId" : "cqf-condition",
               "codeProperty" : "code",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Acute Pharyngitis",
                  "type" : "ValueSetRef"
               }
            }
         }, {
            "name" : "AmbulatoryEncounters",
            "context" : "Patient",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}Encounter",
               "templateId" : "cqf-encounter",
               "codeProperty" : "class",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Ambulatory/ED Visit",
                  "type" : "ValueSetRef"
               }
            }
         }, {
            "name" : "EncountersByServiceType",
            "context" : "Patient",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}Encounter",
               "templateId" : "cqf-encounter",
               "codeProperty" : "type",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Annual Wellness Visit",
                  "type" : "ValueSetRef"
               }
            }
         }, {
            "name" : "WrongDataType",
            "context" : "Patient",
            "expression" : {
               "dataType" : "{http://www.w3.org/2001/XMLSchema}EncounterProposal",
               "templateId" : "EncounterProposal",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Ambulatory/ED Visit",
                  "type" : "ValueSetRef"
               }
            }
         }, {
            "name" : "WrongValueSet",
            "context" : "Patient",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}Condition",
               "templateId" : "cqf-condition",
               "codeProperty" : "code",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Ambulatory/ED Visit",
                  "type" : "ValueSetRef"
               }
            }
         }, {
            "name" : "WrongCodeProperty",
            "context" : "Patient",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}Encounter",
               "templateId" : "cqf-encounter",
               "codeProperty" : "class",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Annual Wellness Visit",
                  "type" : "ValueSetRef"
               }
            }
         } ]
      }
   }
}

### DateRangeOptimizedQuery
library TestSnippet version '1'
using QUICK
parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))
valueset "Ambulatory/ED Visit" = '2.16.840.1.113883.3.464.1003.101.12.1061'
context Patient
define EncountersDuringMP = [Encounter] E where E.period during MeasurementPeriod
define AmbulatoryEncountersDuringMP = [Encounter: "Ambulatory/ED Visit"] E where E.period during MeasurementPeriod
define AmbulatoryEncountersIncludedInMP = [Encounter: "Ambulatory/ED Visit"] E where E.period included in MeasurementPeriod
###

module.exports.DateRangeOptimizedQuery = {
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
      "parameters" : {
         "def" : [ {
            "name" : "MeasurementPeriod",
            "default" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2014",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               }
            }
         } ]
      },
      "valueSets" : {
         "def" : [ {
            "name" : "Ambulatory/ED Visit",
            "id" : "2.16.840.1.113883.3.464.1003.101.12.1061"
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
            "name" : "EncountersDuringMP",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "dateProperty" : "period",
                     "type" : "Retrieve",
                     "dateRange" : {
                        "name" : "MeasurementPeriod",
                        "type" : "ParameterRef"
                     }
                  }
               } ],
               "relationship" : [ ]
            }
         }, {
            "name" : "AmbulatoryEncountersDuringMP",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "codeProperty" : "class",
                     "dateProperty" : "period",
                     "type" : "Retrieve",
                     "codes" : {
                        "name" : "Ambulatory/ED Visit",
                        "type" : "ValueSetRef"
                     },
                     "dateRange" : {
                        "name" : "MeasurementPeriod",
                        "type" : "ParameterRef"
                     }
                  }
               } ],
               "relationship" : [ ]
            }
         }, {
            "name" : "AmbulatoryEncountersIncludedInMP",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "codeProperty" : "class",
                     "dateProperty" : "period",
                     "type" : "Retrieve",
                     "codes" : {
                        "name" : "Ambulatory/ED Visit",
                        "type" : "ValueSetRef"
                     },
                     "dateRange" : {
                        "name" : "MeasurementPeriod",
                        "type" : "ParameterRef"
                     }
                  }
               } ],
               "relationship" : [ ]
            }
         } ]
      }
   }
}

### IncludesQuery
library TestSnippet version '1'
using QUICK
parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))
valueset "Ambulatory/ED Visit" = '2.16.840.1.113883.3.464.1003.101.12.1061'
context Patient
define MPIncludedAmbulatoryEncounters = [Encounter: "Ambulatory/ED Visit"] E where MeasurementPeriod includes E.period
###

module.exports.IncludesQuery = {
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
      "parameters" : {
         "def" : [ {
            "name" : "MeasurementPeriod",
            "default" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2014",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               }
            }
         } ]
      },
      "valueSets" : {
         "def" : [ {
            "name" : "Ambulatory/ED Visit",
            "id" : "2.16.840.1.113883.3.464.1003.101.12.1061"
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
            "name" : "MPIncludedAmbulatoryEncounters",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "codeProperty" : "class",
                     "type" : "Retrieve",
                     "codes" : {
                        "name" : "Ambulatory/ED Visit",
                        "type" : "ValueSetRef"
                     }
                  }
               } ],
               "relationship" : [ ],
               "where" : {
                  "type" : "Includes",
                  "operand" : [ {
                     "name" : "MeasurementPeriod",
                     "type" : "ParameterRef"
                  }, {
                     "path" : "period",
                     "scope" : "E",
                     "type" : "Property"
                  } ]
               }
            }
         } ]
      }
   }
}

### MultiSourceQuery
library TestSnippet version '1'
using QUICK
parameter MeasurementPeriod default interval[Date(2013, 1, 1), Date(2014, 1, 1))
context Patient
define msQueryWhere = foreach [Encounter] E,
                              [Condition] C
                              where E.period included in MeasurementPeriod

define msQueryWhere2 = foreach [Encounter] E, [Condition] C
  where  E.period  included in MeasurementPeriod and  C.identifier.value = 'http://cqframework.org/3/2'

define msQuery = foreach [Encounter] E, [Condition] C return {E: E, C:C}
###

module.exports.MultiSourceQuery = {
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
      "parameters" : {
         "def" : [ {
            "name" : "MeasurementPeriod",
            "default" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2014",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               }
            }
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
            "name" : "msQueryWhere",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "dateProperty" : "period",
                     "type" : "Retrieve",
                     "dateRange" : {
                        "name" : "MeasurementPeriod",
                        "type" : "ParameterRef"
                     }
                  }
               }, {
                  "alias" : "C",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Condition",
                     "templateId" : "cqf-condition",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "distinct" : true,
                  "expression" : {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "E",
                        "value" : {
                           "name" : "E",
                           "type" : "AliasRef"
                        }
                     }, {
                        "name" : "C",
                        "value" : {
                           "name" : "C",
                           "type" : "AliasRef"
                        }
                     } ]
                  }
               }
            }
         }, {
            "name" : "msQueryWhere2",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "dateProperty" : "period",
                     "type" : "Retrieve",
                     "dateRange" : {
                        "name" : "MeasurementPeriod",
                        "type" : "ParameterRef"
                     }
                  }
               }, {
                  "alias" : "C",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Condition",
                     "templateId" : "cqf-condition",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "where" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "path" : "identifier.value",
                     "scope" : "C",
                     "type" : "Property"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "http://cqframework.org/3/2",
                     "type" : "Literal"
                  } ]
               },
               "return" : {
                  "distinct" : true,
                  "expression" : {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "E",
                        "value" : {
                           "name" : "E",
                           "type" : "AliasRef"
                        }
                     }, {
                        "name" : "C",
                        "value" : {
                           "name" : "C",
                           "type" : "AliasRef"
                        }
                     } ]
                  }
               }
            }
         }, {
            "name" : "msQuery",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               }, {
                  "alias" : "C",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Condition",
                     "templateId" : "cqf-condition",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "E",
                        "value" : {
                           "name" : "E",
                           "type" : "AliasRef"
                        }
                     }, {
                        "name" : "C",
                        "value" : {
                           "name" : "C",
                           "type" : "AliasRef"
                        }
                     } ]
                  }
               }
            }
         } ]
      }
   }
}

### QueryDefine
library TestSnippet version '1'
using QUICK
context Patient
define query =  [Encounter] E
 define a = E
 return {E: E, a:a}
###

module.exports.QueryDefine = {
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
            "name" : "query",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "define" : [ {
                  "identifier" : "a",
                  "expression" : {
                     "name" : "E",
                     "type" : "AliasRef"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "E",
                        "value" : {
                           "name" : "E",
                           "type" : "AliasRef"
                        }
                     }, {
                        "name" : "a",
                        "value" : {
                           "name" : "a",
                           "type" : "QueryDefineRef"
                        }
                     } ]
                  }
               }
            }
         } ]
      }
   }
}

### Tuple
library TestSnippet version '1'
using QUICK
context Patient
define tup = {a: 1, b: 2}
define query =  [Encounter] E return {id: E.id, thing: E.status}
###

module.exports.Tuple = {
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
            "name" : "tup",
            "context" : "Patient",
            "expression" : {
               "type" : "Tuple",
               "element" : [ {
                  "name" : "a",
                  "value" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }
               }, {
                  "name" : "b",
                  "value" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "query",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "id",
                        "value" : {
                           "path" : "id",
                           "scope" : "E",
                           "type" : "Property"
                        }
                     }, {
                        "name" : "thing",
                        "value" : {
                           "path" : "status",
                           "scope" : "E",
                           "type" : "Property"
                        }
                     } ]
                  }
               }
            }
         } ]
      }
   }
}

### QueryRelationship
library TestSnippet version '1'
using QUICK
context Patient

define withQuery =  [Encounter] E
  with [Condition] C such that C.identifier.value = 'http://cqframework.org/3/2'

define withQuery2 =  [Encounter] E
  with [Condition] C such that C.identifier.value = 'http://cqframework.org/3'

define withOutQuery =  [Encounter] E
  without [Condition] C such that C.identifier.value = 'http://cqframework.org/3/'

define withOutQuery2 =  [Encounter] E
  without [Condition] C such that C.identifier.value = 'http://cqframework.org/3/2'
###

module.exports.QueryRelationship = {
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
            "name" : "withQuery",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ {
                  "alias" : "C",
                  "type" : "With",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Condition",
                     "templateId" : "cqf-condition",
                     "type" : "Retrieve"
                  },
                  "suchThat" : {
                     "type" : "Equal",
                     "operand" : [ {
                        "path" : "identifier.value",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                        "value" : "http://cqframework.org/3/2",
                        "type" : "Literal"
                     } ]
                  }
               } ]
            }
         }, {
            "name" : "withQuery2",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ {
                  "alias" : "C",
                  "type" : "With",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Condition",
                     "templateId" : "cqf-condition",
                     "type" : "Retrieve"
                  },
                  "suchThat" : {
                     "type" : "Equal",
                     "operand" : [ {
                        "path" : "identifier.value",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                        "value" : "http://cqframework.org/3",
                        "type" : "Literal"
                     } ]
                  }
               } ]
            }
         }, {
            "name" : "withOutQuery",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ {
                  "alias" : "C",
                  "type" : "Without",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Condition",
                     "templateId" : "cqf-condition",
                     "type" : "Retrieve"
                  },
                  "suchThat" : {
                     "type" : "Equal",
                     "operand" : [ {
                        "path" : "identifier.value",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                        "value" : "http://cqframework.org/3/",
                        "type" : "Literal"
                     } ]
                  }
               } ]
            }
         }, {
            "name" : "withOutQuery2",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ {
                  "alias" : "C",
                  "type" : "Without",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Condition",
                     "templateId" : "cqf-condition",
                     "type" : "Retrieve"
                  },
                  "suchThat" : {
                     "type" : "Equal",
                     "operand" : [ {
                        "path" : "identifier.value",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                        "value" : "http://cqframework.org/3/2",
                        "type" : "Literal"
                     } ]
                  }
               } ]
            }
         } ]
      }
   }
}

### Sorting
library TestSnippet version '1'
using QUICK
context Patient
define singleAsc =  [Encounter] E  return {E : E} sort by E.identifier.value
define singleDesc =  [Encounter] E return {E : E} sort by E.identifier.value desc
###

module.exports.Sorting = {
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
            "name" : "singleAsc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "E",
                        "value" : {
                           "name" : "E",
                           "type" : "AliasRef"
                        }
                     } ]
                  }
               },
               "sort" : {
                  "by" : [ {
                     "direction" : "asc",
                     "type" : "ByExpression",
                     "expression" : {
                        "path" : "identifier.value",
                        "scope" : "E",
                        "type" : "Property"
                     }
                  } ]
               }
            }
         }, {
            "name" : "singleDesc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "E",
                        "value" : {
                           "name" : "E",
                           "type" : "AliasRef"
                        }
                     } ]
                  }
               },
               "sort" : {
                  "by" : [ {
                     "direction" : "desc",
                     "type" : "ByExpression",
                     "expression" : {
                        "path" : "identifier.value",
                        "scope" : "E",
                        "type" : "Property"
                     }
                  } ]
               }
            }
         } ]
      }
   }
}

### ScratchPad
library TestSnippet version '1'
using QUICK
context Patient
define Foo = "foo"
###

module.exports.ScratchPad = {
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
            "name" : "Foo",
            "context" : "Patient",
            "expression" : {
               "name" : "foo",
               "type" : "IdentifierRef"
            }
         } ]
      }
   }
}

