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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "MeasurementPeriod",
            "default" : {
               "beginOpen" : false,
               "endOpen" : true,
               "type" : "Interval",
               "begin" : {
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
               "end" : {
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
                        "type" : "Begin",
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
                        "type" : "Begin",
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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
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
valueset "One Arg" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1011')
valueset "Two Arg" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1011', '20140501')
valueset "Three Arg" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1011', '20140501', 'National Committee for Quality Assurance')
###

module.exports.ValueSetDef = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
         } ]
      },
      "valueSets" : {
         "def" : [ {
            "name" : "One Arg",
            "valueSet" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.464.1003.102.12.1011",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Two Arg",
            "valueSet" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.464.1003.102.12.1011",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "20140501",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Three Arg",
            "valueSet" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.464.1003.102.12.1011",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "20140501",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "National Committee for Quality Assurance",
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
valueset "Acute Pharyngitis" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1001')
context PATIENT
define Foo = "Acute Pharyngitis"
###

module.exports.ValueSetRef = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
         } ]
      },
      "valueSets" : {
         "def" : [ {
            "name" : "Acute Pharyngitis",
            "valueSet" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.464.1003.101.12.1001",
                  "type" : "Literal"
               } ]
            }
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Foo",
            "context" : "PATIENT",
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
context PATIENT
define AllTrue = true and true
define AllFalse = false and false
define SomeTrue = true and false
###

module.exports.And = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "AllTrue",
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
            "name" : "AllFalse",
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
            "name" : "SomeTrue",
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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Open",
            "context" : "PATIENT",
            "expression" : {
               "beginOpen" : true,
               "endOpen" : true,
               "type" : "Interval",
               "begin" : {
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
               "end" : {
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
               "beginOpen" : true,
               "endOpen" : false,
               "type" : "Interval",
               "begin" : {
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
               "end" : {
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
               "beginOpen" : false,
               "endOpen" : true,
               "type" : "Interval",
               "begin" : {
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
               "end" : {
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
               "beginOpen" : false,
               "endOpen" : false,
               "type" : "Interval",
               "begin" : {
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
               "end" : {
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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
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

### Begin
library TestSnippet version '1'
using QUICK
context PATIENT
define Foo = start of interval[Date(2012, 1, 1), Date(2013, 1, 1)]
###

module.exports.Begin = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Foo",
            "context" : "PATIENT",
            "expression" : {
               "type" : "Begin",
               "operand" : {
                  "beginOpen" : false,
                  "endOpen" : false,
                  "type" : "Interval",
                  "begin" : {
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
                  "end" : {
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

### Literal
library TestSnippet version '1'
using QUICK
context PATIENT
define BoolTrue = true
define BoolFalse = false
define IntOne = 1
define StringTrue = 'true'
###

module.exports.Literal = {
   "library" : {
      "identifier" : {
         "id" : "TestSnippet",
         "version" : "1"
      },
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
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

