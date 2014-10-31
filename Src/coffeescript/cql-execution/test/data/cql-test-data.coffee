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

context PATIENT

define InDemographic =
    AgeAt(start of MeasurementPeriod) >= 2 and AgeAt(start of MeasurementPeriod) < 18
###

module.exports.InAgeDemographic = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
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
            "name" : "InDemographic",
            "context" : "PATIENT",
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
context PATIENT
define Foo = 'Bar'
###

module.exports.ExpressionDef = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Foo",
            "context" : "PATIENT",
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
context PATIENT
define Life = 42
define Foo = Life
###

module.exports.ExpressionRef = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Life",
            "context" : "PATIENT",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "42",
               "type" : "Literal"
            }
         }, {
            "name" : "Foo",
            "context" : "PATIENT",
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
context PATIENT
define Foo = FooP
###

module.exports.ParameterRef = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
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
            "name" : "Foo",
            "context" : "PATIENT",
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
context PATIENT
define "Known" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1061')
define "Unknown One Arg" = ValueSet('1.2.3.4.5.6.7.8.9')
define "Unknown Two Arg" = ValueSet('1.2.3.4.5.6.7.8.9', '1')
###

module.exports.ValueSetDef = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Known",
            "context" : "PATIENT",
            "expression" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.464.1003.101.12.1061",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Unknown One Arg",
            "context" : "PATIENT",
            "expression" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "1.2.3.4.5.6.7.8.9",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Unknown Two Arg",
            "context" : "PATIENT",
            "expression" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "1.2.3.4.5.6.7.8.9",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "1",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

### ValueSetRef
library TestSnippet version '1'
using QUICK
context PATIENT
define "Acute Pharyngitis" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1001')
define Foo = "Acute Pharyngitis"
###

module.exports.ValueSetRef = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Acute Pharyngitis",
            "context" : "PATIENT",
            "expression" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.464.1003.101.12.1001",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Foo",
            "context" : "PATIENT",
            "expression" : {
               "name" : "Acute Pharyngitis",
               "type" : "ExpressionRef"
            }
         } ]
      }
   }
}

### And
library TestSnippet version '1'
using QUICK
context PATIENT
define TT = true and true
define FF = false and false
define TF = true and false
define FT = false and true
define TTT = true and true and true
define FFF = false and false and false
define TFT = true and false and true
###

module.exports.And = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "TT",
            "context" : "PATIENT",
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
            "name" : "FF",
            "context" : "PATIENT",
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
            "name" : "TF",
            "context" : "PATIENT",
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
            "name" : "FT",
            "context" : "PATIENT",
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
            "name" : "TTT",
            "context" : "PATIENT",
            "expression" : {
               "type" : "And",
               "operand" : [ {
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
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FFF",
            "context" : "PATIENT",
            "expression" : {
               "type" : "And",
               "operand" : [ {
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
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TFT",
            "context" : "PATIENT",
            "expression" : {
               "type" : "And",
               "operand" : [ {
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
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
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
context PATIENT
define TT = true or true
define FF = false or false
define TF = true or false
define FT = false or true
define TTT = true or true or true
define FFF = false or false or false
define TFT = true or false or true
###

module.exports.Or = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "TT",
            "context" : "PATIENT",
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
            "name" : "FF",
            "context" : "PATIENT",
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
            "name" : "TF",
            "context" : "PATIENT",
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
            "name" : "FT",
            "context" : "PATIENT",
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
            "name" : "TTT",
            "context" : "PATIENT",
            "expression" : {
               "type" : "Or",
               "operand" : [ {
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
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FFF",
            "context" : "PATIENT",
            "expression" : {
               "type" : "Or",
               "operand" : [ {
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
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TFT",
            "context" : "PATIENT",
            "expression" : {
               "type" : "Or",
               "operand" : [ {
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
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
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
context PATIENT
define TT = true xor true
define FF = false xor false
define TF = true xor false
define FT = false xor true
define TTT = true xor true xor true
define TTF = true xor true xor false
define TFT = true xor false xor true
define TFF = true xor false xor false
define FTT = false xor true xor true
define FTF = false xor true xor false
define FFT = false xor false xor true
define FFF = false xor false xor false
###

module.exports.XOr = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "TT",
            "context" : "PATIENT",
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
            "name" : "FF",
            "context" : "PATIENT",
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
            "name" : "TF",
            "context" : "PATIENT",
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
            "name" : "FT",
            "context" : "PATIENT",
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
            "name" : "TTT",
            "context" : "PATIENT",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
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
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TTF",
            "context" : "PATIENT",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
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
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TFT",
            "context" : "PATIENT",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
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
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TFF",
            "context" : "PATIENT",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
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
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FTT",
            "context" : "PATIENT",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
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
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FTF",
            "context" : "PATIENT",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
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
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "false",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FFT",
            "context" : "PATIENT",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
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
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
                  "value" : "true",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FFF",
            "context" : "PATIENT",
            "expression" : {
               "type" : "Xor",
               "operand" : [ {
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

### AgeAtFunctionRef
library TestSnippet version '1'
using QUICK
context PATIENT
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
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "AgeAt2012",
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
context PATIENT
define Year = Date(2012)
define Month = Date(2012, 4)
define Day = Date(2012, 4, 15)
define Hour = Date(2012, 4, 15, 12)
define Minute = Date(2012, 4, 15, 12, 10)
define Second = Date(2012, 4, 15, 12, 10, 59)
###

module.exports.DateFunctionRef = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Year",
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
         } ]
      }
   }
}

### Interval
library TestSnippet version '1'
using QUICK
context PATIENT
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
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Open",
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
context PATIENT
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
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "AGtB_Int",
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
context PATIENT
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
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "AGtB_Int",
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
context PATIENT
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
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "AGtB_Int",
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
context PATIENT
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
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "AGtB_Int",
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
context PATIENT
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
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "AGtB_Int",
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
context PATIENT
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
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Three",
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
context PATIENT
define EmptyList = exists ({})
define FullList = exists ({ 1, 2, 3 })
###

module.exports.Exists = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "EmptyList",
            "context" : "PATIENT",
            "expression" : {
               "type" : "Exists",
               "operand" : {
                  "type" : "List"
               }
            }
         }, {
            "name" : "FullList",
            "context" : "PATIENT",
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
context PATIENT
define Foo = start of interval[Date(2012, 1, 1), Date(2013, 1, 1)]
###

module.exports.Start = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Foo",
            "context" : "PATIENT",
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
context PATIENT
define IsIn = 4 in { 3, 4, 5 }
define IsNotIn = 4 in { 3, 5, 6 }
###

module.exports.InList = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "IsIn",
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
context PATIENT
define "Female" = ValueSet('2.16.840.1.113883.3.560.100.2')
define "Versioned Female" = ValueSet('2.16.840.1.113883.3.560.100.2', '20121025')
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
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Female",
            "context" : "PATIENT",
            "expression" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.560.100.2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Versioned Female",
            "context" : "PATIENT",
            "expression" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
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
            "name" : "String",
            "context" : "PATIENT",
            "expression" : {
               "type" : "In",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "F",
                  "type" : "Literal"
               }, {
                  "name" : "Female",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "StringInVersionedValueSet",
            "context" : "PATIENT",
            "expression" : {
               "type" : "In",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "F",
                  "type" : "Literal"
               }, {
                  "name" : "Versioned Female",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "ShortCode",
            "context" : "PATIENT",
            "expression" : {
               "type" : "In",
               "operand" : [ {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "F",
                     "type" : "Literal"
                  } ]
               }, {
                  "name" : "Female",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "MediumCode",
            "context" : "PATIENT",
            "expression" : {
               "type" : "In",
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
                  "name" : "Female",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "LongCode",
            "context" : "PATIENT",
            "expression" : {
               "type" : "In",
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
                  "name" : "Female",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "WrongString",
            "context" : "PATIENT",
            "expression" : {
               "type" : "In",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "M",
                  "type" : "Literal"
               }, {
                  "name" : "Female",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "WrongStringInVersionedValueSet",
            "context" : "PATIENT",
            "expression" : {
               "type" : "In",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "M",
                  "type" : "Literal"
               }, {
                  "name" : "Versioned Female",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "WrongShortCode",
            "context" : "PATIENT",
            "expression" : {
               "type" : "In",
               "operand" : [ {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "M",
                     "type" : "Literal"
                  } ]
               }, {
                  "name" : "Female",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "WrongMediumCode",
            "context" : "PATIENT",
            "expression" : {
               "type" : "In",
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
                  "name" : "Female",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "WrongLongCode",
            "context" : "PATIENT",
            "expression" : {
               "type" : "In",
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
                  "name" : "Female",
                  "type" : "ExpressionRef"
               } ]
            }
         } ]
      }
   }
}

### InValueSetFunction
library TestSnippet version '1'
using QUICK
context PATIENT
define "Female" = ValueSet('2.16.840.1.113883.3.560.100.2')
define "Versioned Female" = ValueSet('2.16.840.1.113883.3.560.100.2', '20121025')
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
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Female",
            "context" : "PATIENT",
            "expression" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.560.100.2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Versioned Female",
            "context" : "PATIENT",
            "expression" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
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
            "name" : "String",
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
context PATIENT
define "Female" = ValueSet('2.16.840.1.113883.3.560.100.2')
define IsFemale = gender in "Female Administrative Sex"
###

module.exports.PatientPropertyInValueSet = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Female",
            "context" : "PATIENT",
            "expression" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.560.100.2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "IsFemale",
            "context" : "PATIENT",
            "expression" : {
               "type" : "In",
               "operand" : [ {
               }, {
               } ]
            }
         } ]
      }
   }
}

### Add
library TestSnippet version '1'
using QUICK
context PATIENT
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
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Ten",
            "context" : "PATIENT",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "10",
               "type" : "Literal"
            }
         }, {
            "name" : "Eleven",
            "context" : "PATIENT",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "11",
               "type" : "Literal"
            }
         }, {
            "name" : "OnePlusTwo",
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
context PATIENT
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
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Ten",
            "context" : "PATIENT",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "10",
               "type" : "Literal"
            }
         }, {
            "name" : "Eleven",
            "context" : "PATIENT",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "11",
               "type" : "Literal"
            }
         }, {
            "name" : "FiveMinusTwo",
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
context PATIENT
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
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Ten",
            "context" : "PATIENT",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "10",
               "type" : "Literal"
            }
         }, {
            "name" : "Eleven",
            "context" : "PATIENT",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "11",
               "type" : "Literal"
            }
         }, {
            "name" : "FiveTimesTwo",
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
context PATIENT
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
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Hundred",
            "context" : "PATIENT",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "100",
               "type" : "Literal"
            }
         }, {
            "name" : "Four",
            "context" : "PATIENT",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "4",
               "type" : "Literal"
            }
         }, {
            "name" : "TenDividedByTwo",
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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

### MathPrecedence
library TestSnippet version '1'
using QUICK
context PATIENT
define Mixed = 1 + 5 * 10 - 15 / 3
define Parenthetical = (1 + 5) * (10 - 15) / 3
###

module.exports.MathPrecedence = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Mixed",
            "context" : "PATIENT",
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
            "context" : "PATIENT",
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

### Literal
library TestSnippet version '1'
using QUICK
context PATIENT
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
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "BoolTrue",
            "context" : "PATIENT",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
               "value" : "true",
               "type" : "Literal"
            }
         }, {
            "name" : "BoolFalse",
            "context" : "PATIENT",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}bool",
               "value" : "false",
               "type" : "Literal"
            }
         }, {
            "name" : "IntOne",
            "context" : "PATIENT",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
               "value" : "1",
               "type" : "Literal"
            }
         }, {
            "name" : "DecimalTenth",
            "context" : "PATIENT",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}decimal",
               "value" : "0.1",
               "type" : "Literal"
            }
         }, {
            "name" : "StringTrue",
            "context" : "PATIENT",
            "expression" : {
               "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
               "value" : "true",
               "type" : "Literal"
            }
         } ]
      }
   }
}

### Retrieve
library TestSnippet version '1'
using QUICK
context PATIENT
define "Acute Pharyngitis" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1011')
define "Ambulatory/ED Visit" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1061')
define "Annual Wellness Visit" = ValueSet('2.16.840.1.113883.3.526.3.1240')
define Conditions = [Condition]
define Encounters = [Encounter, Performance]
define PharyngitisConditions = [Condition: "Acute Pharyngitis"]
define AmbulatoryEncounters = [Encounter, Performance: "Ambulatory/ED Visit"]
define EncountersByServiceType = [Encounter, Performance: serviceType in "Annual Wellness Visit"]
define WrongDataType = [Encounter, Proposal: "Ambulatory/ED Visit"]
define WrongValueSet = [Condition: "Ambulatory/ED Visit"]
define WrongCodeProperty = [Encounter, Performance: class in "Annual Wellness Visit"]
###

module.exports.Retrieve = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Acute Pharyngitis",
            "context" : "PATIENT",
            "expression" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.464.1003.102.12.1011",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Ambulatory/ED Visit",
            "context" : "PATIENT",
            "expression" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.464.1003.101.12.1061",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Annual Wellness Visit",
            "context" : "PATIENT",
            "expression" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.526.3.1240",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Conditions",
            "context" : "PATIENT",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}ConditionOccurrence",
               "type" : "Retrieve"
            }
         }, {
            "name" : "Encounters",
            "context" : "PATIENT",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}EncounterPerformanceOccurrence",
               "type" : "Retrieve"
            }
         }, {
            "name" : "PharyngitisConditions",
            "context" : "PATIENT",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}ConditionOccurrence",
               "codeProperty" : "code",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Acute Pharyngitis",
                  "type" : "ExpressionRef"
               }
            }
         }, {
            "name" : "AmbulatoryEncounters",
            "context" : "PATIENT",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}EncounterPerformanceOccurrence",
               "codeProperty" : "class",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Ambulatory/ED Visit",
                  "type" : "ExpressionRef"
               }
            }
         }, {
            "name" : "EncountersByServiceType",
            "context" : "PATIENT",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}EncounterPerformanceOccurrence",
               "codeProperty" : "serviceType",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Annual Wellness Visit",
                  "type" : "ExpressionRef"
               }
            }
         }, {
            "name" : "WrongDataType",
            "context" : "PATIENT",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}EncounterProposalOccurrence",
               "codeProperty" : "class",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Ambulatory/ED Visit",
                  "type" : "ExpressionRef"
               }
            }
         }, {
            "name" : "WrongValueSet",
            "context" : "PATIENT",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}ConditionOccurrence",
               "codeProperty" : "code",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Ambulatory/ED Visit",
                  "type" : "ExpressionRef"
               }
            }
         }, {
            "name" : "WrongCodeProperty",
            "context" : "PATIENT",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}EncounterPerformanceOccurrence",
               "codeProperty" : "class",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Annual Wellness Visit",
                  "type" : "ExpressionRef"
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
context PATIENT
define "Ambulatory/ED Visit" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1061')
define EncountersDuringMP = [Encounter, Performance] E where E.performanceTime during MeasurementPeriod
define AmbulatoryEncountersDuringMP = [Encounter, Performance: "Ambulatory/ED Visit"] E where E.performanceTime during MeasurementPeriod
define AmbulatoryEncountersIncludedInMP = [Encounter, Performance: "Ambulatory/ED Visit"] E where E.performanceTime included in MeasurementPeriod
###

module.exports.DateRangeOptimizedQuery = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
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
            "name" : "Ambulatory/ED Visit",
            "context" : "PATIENT",
            "expression" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.464.1003.101.12.1061",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "EncountersDuringMP",
            "context" : "PATIENT",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}EncounterPerformanceOccurrence",
                     "dateProperty" : "performanceTime",
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
            "context" : "PATIENT",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}EncounterPerformanceOccurrence",
                     "codeProperty" : "class",
                     "dateProperty" : "performanceTime",
                     "type" : "Retrieve",
                     "codes" : {
                        "name" : "Ambulatory/ED Visit",
                        "type" : "ExpressionRef"
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
            "context" : "PATIENT",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}EncounterPerformanceOccurrence",
                     "codeProperty" : "class",
                     "dateProperty" : "performanceTime",
                     "type" : "Retrieve",
                     "codes" : {
                        "name" : "Ambulatory/ED Visit",
                        "type" : "ExpressionRef"
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
context PATIENT
define "Ambulatory/ED Visit" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1061')
define MPIncludedAmbulatoryEncounters = [Encounter, Performance: "Ambulatory/ED Visit"] E where MeasurementPeriod includes E.performanceTime
###

module.exports.IncludesQuery = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
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
            "name" : "Ambulatory/ED Visit",
            "context" : "PATIENT",
            "expression" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.464.1003.101.12.1061",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "MPIncludedAmbulatoryEncounters",
            "context" : "PATIENT",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}EncounterPerformanceOccurrence",
                     "codeProperty" : "class",
                     "type" : "Retrieve",
                     "codes" : {
                        "name" : "Ambulatory/ED Visit",
                        "type" : "ExpressionRef"
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
                     "path" : "performanceTime",
                     "scope" : "E",
                     "type" : "Property"
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
context PATIENT
define Foo = "foo"
###

module.exports.ScratchPad = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "usings" : {
         "def" : [ {
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Foo",
            "context" : "PATIENT",
            "expression" : {
            }
         } ]
      }
   }
}

