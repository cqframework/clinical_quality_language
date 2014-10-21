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

### List
library TestSnippet version '1'
using QUICK
context PATIENT
define three = 1 + 2
define IntList = { 9, 7, 8 }
define StringList = { 'a', 'bee', 'see' }
define mixedList = { 1, 'two', three }
###

module.exports.List = {
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
            "name" : "three",
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
            "name" : "mixedList",
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
                  "name" : "three",
                  "type" : "ExpressionRef"
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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
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
valueset "Female" = ValueSet('2.16.840.1.113883.3.560.100.2')
valueset "Versioned Female" = ValueSet('2.16.840.1.113883.3.560.100.2', '20121025')
context PATIENT
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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
         } ]
      },
      "valueSets" : {
         "def" : [ {
            "name" : "Female",
            "valueSet" : {
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
            "valueSet" : {
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
         } ]
      },
      "statements" : {
         "def" : [ {
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
                  "type" : "ValueSetRef"
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
                  "type" : "ValueSetRef"
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
                  "type" : "ValueSetRef"
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
                  "type" : "ValueSetRef"
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
                  "type" : "ValueSetRef"
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
                  "type" : "ValueSetRef"
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
                  "type" : "ValueSetRef"
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
                  "type" : "ValueSetRef"
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
                  "type" : "ValueSetRef"
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
                  "type" : "ValueSetRef"
               } ]
            }
         } ]
      }
   }
}

### InValueSetFunction
library TestSnippet version '1'
using QUICK
valueset "Female" = ValueSet('2.16.840.1.113883.3.560.100.2')
valueset "Versioned Female" = ValueSet('2.16.840.1.113883.3.560.100.2', '20121025')
context PATIENT
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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
         } ]
      },
      "valueSets" : {
         "def" : [ {
            "name" : "Female",
            "valueSet" : {
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
            "valueSet" : {
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
         } ]
      },
      "statements" : {
         "def" : [ {
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
valueset "Female" = ValueSet('2.16.840.1.113883.3.560.100.2')
context PATIENT
define IsFemale = gender in "Female Administrative Sex"
###

module.exports.PatientPropertyInValueSet = {
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
            "name" : "Female",
            "valueSet" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.560.100.2",
                  "type" : "Literal"
               } ]
            }
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "IsFemale",
            "context" : "PATIENT",
            "expression" : {
               "type" : "In",
               "operand" : [ {
               }, {
                  "name" : "Female Administrative Sex",
                  "type" : "ValueSetRef"
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
      "dataModels" : {
         "modelReference" : [ {
            "referencedModel" : {
               "value" : "http://org.hl7.fhir"
            }
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

### ClinicalRequest
library TestSnippet version '1'
using QUICK
valueset "Acute Pharyngitis" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1011')
valueset "Ambulatory/ED Visit" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1061')
valueset "Annual Wellness Visit" = ValueSet('2.16.840.1.113883.3.526.3.1240')
context PATIENT
define Conditions = [Condition]
define Encounters = [Encounter, Performance]
define PharyngitisConditions = [Condition: "Acute Pharyngitis"]
define AmbulatoryEncounters = [Encounter, Performance: "Ambulatory/ED Visit"]
define EncountersByServiceType = [Encounter, Performance: serviceType in "Annual Wellness Visit"]
define WrongDataType = [Encounter, Proposal: "Ambulatory/ED Visit"]
define WrongValueSet = [Condition: "Ambulatory/ED Visit"]
define WrongCodeProperty = [Encounter, Performance: class in "Annual Wellness Visit"]
###

module.exports.ClinicalRequest = {
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
                  "value" : "2.16.840.1.113883.3.464.1003.102.12.1011",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Ambulatory/ED Visit",
            "valueSet" : {
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
            "valueSet" : {
               "name" : "ValueSet",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "2.16.840.1.113883.3.526.3.1240",
                  "type" : "Literal"
               } ]
            }
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Conditions",
            "context" : "PATIENT",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}ConditionOccurrence",
               "type" : "ClinicalRequest"
            }
         }, {
            "name" : "Encounters",
            "context" : "PATIENT",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}EncounterPerformanceOccurrence",
               "type" : "ClinicalRequest"
            }
         }, {
            "name" : "PharyngitisConditions",
            "context" : "PATIENT",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}ConditionOccurrence",
               "codeProperty" : "code",
               "type" : "ClinicalRequest",
               "codes" : {
                  "name" : "Acute Pharyngitis",
                  "type" : "ValueSetRef"
               }
            }
         }, {
            "name" : "AmbulatoryEncounters",
            "context" : "PATIENT",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}EncounterPerformanceOccurrence",
               "codeProperty" : "class",
               "type" : "ClinicalRequest",
               "codes" : {
                  "name" : "Ambulatory/ED Visit",
                  "type" : "ValueSetRef"
               }
            }
         }, {
            "name" : "EncountersByServiceType",
            "context" : "PATIENT",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}EncounterPerformanceOccurrence",
               "codeProperty" : "serviceType",
               "type" : "ClinicalRequest",
               "codes" : {
                  "name" : "Annual Wellness Visit",
                  "type" : "ValueSetRef"
               }
            }
         }, {
            "name" : "WrongDataType",
            "context" : "PATIENT",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}EncounterProposalOccurrence",
               "codeProperty" : "class",
               "type" : "ClinicalRequest",
               "codes" : {
                  "name" : "Ambulatory/ED Visit",
                  "type" : "ValueSetRef"
               }
            }
         }, {
            "name" : "WrongValueSet",
            "context" : "PATIENT",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}ConditionOccurrence",
               "codeProperty" : "code",
               "type" : "ClinicalRequest",
               "codes" : {
                  "name" : "Ambulatory/ED Visit",
                  "type" : "ValueSetRef"
               }
            }
         }, {
            "name" : "WrongCodeProperty",
            "context" : "PATIENT",
            "expression" : {
               "dataType" : "{http://org.hl7.fhir}EncounterPerformanceOccurrence",
               "codeProperty" : "class",
               "type" : "ClinicalRequest",
               "codes" : {
                  "name" : "Annual Wellness Visit",
                  "type" : "ValueSetRef"
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
               "name" : "foo",
               "type" : "ValueSetRef"
            }
         } ]
      }
   }
}

