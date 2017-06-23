###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### In Age Demographic
library TestSnippet version '1'
using QUICK
parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))

context Patient

define InDemographic:
AgeInYearsAt(start of MeasurementPeriod) >= 2 and AgeInYearsAt(start of MeasurementPeriod) < 18
###

module.exports['In Age Demographic'] = {
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
      "parameters" : {
         "def" : [ {
            "name" : "MeasurementPeriod",
            "accessLevel" : "Public",
            "default" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2013",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }
               },
               "high" : {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2014",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }
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
                  "dataType" : "{http://hl7.org/fhir}Patient",
                  "templateId" : "patient-qicore-qicore-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "InDemographic",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "And",
               "operand" : [ {
                  "type" : "GreaterOrEqual",
                  "operand" : [ {
                     "precision" : "Year",
                     "type" : "CalculateAgeAt",
                     "operand" : [ {
                        "path" : "birthDate",
                        "type" : "Property",
                        "source" : {
                           "name" : "Patient",
                           "type" : "ExpressionRef"
                        }
                     }, {
                        "type" : "Start",
                        "operand" : {
                           "name" : "MeasurementPeriod",
                           "type" : "ParameterRef"
                        }
                     } ]
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "Less",
                  "operand" : [ {
                     "precision" : "Year",
                     "type" : "CalculateAgeAt",
                     "operand" : [ {
                        "path" : "birthDate",
                        "type" : "Property",
                        "source" : {
                           "name" : "Patient",
                           "type" : "ExpressionRef"
                        }
                     }, {
                        "type" : "Start",
                        "operand" : {
                           "name" : "MeasurementPeriod",
                           "type" : "ParameterRef"
                        }
                     } ]
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "18",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

### CommonLib
library Common
using QUICK
parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))

context Patient

define InDemographic:
AgeInYearsAt(start of MeasurementPeriod) >= 2 and AgeInYearsAt(start of MeasurementPeriod) < 18

define function foo (a Integer, b Integer) :
  a + b
###

module.exports['CommonLib'] = {
   "library" : {
      "identifier" : {
         "id" : "Common"
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
      "parameters" : {
         "def" : [ {
            "name" : "MeasurementPeriod",
            "accessLevel" : "Public",
            "default" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2013",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }
               },
               "high" : {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2014",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }
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
                  "dataType" : "{http://hl7.org/fhir}Patient",
                  "templateId" : "patient-qicore-qicore-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "InDemographic",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "And",
               "operand" : [ {
                  "type" : "GreaterOrEqual",
                  "operand" : [ {
                     "precision" : "Year",
                     "type" : "CalculateAgeAt",
                     "operand" : [ {
                        "path" : "birthDate",
                        "type" : "Property",
                        "source" : {
                           "name" : "Patient",
                           "type" : "ExpressionRef"
                        }
                     }, {
                        "type" : "Start",
                        "operand" : {
                           "name" : "MeasurementPeriod",
                           "type" : "ParameterRef"
                        }
                     } ]
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "Less",
                  "operand" : [ {
                     "precision" : "Year",
                     "type" : "CalculateAgeAt",
                     "operand" : [ {
                        "path" : "birthDate",
                        "type" : "Property",
                        "source" : {
                           "name" : "Patient",
                           "type" : "ExpressionRef"
                        }
                     }, {
                        "type" : "Start",
                        "operand" : {
                           "name" : "MeasurementPeriod",
                           "type" : "ParameterRef"
                        }
                     } ]
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "18",
                     "type" : "Literal"
                  } ]
               } ]
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
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            }, {
               "name" : "b",
               "operandTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            } ]
         } ]
      }
   }
}

### Using CommonLib
library TestSnippet version '1'
using QUICK
include Common called common
parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))

context Patient

define ID: common.InDemographic

define L : Length(Patient.name[0].given[0])
define FuncTest : common.foo(2, 5)
###

module.exports['Using CommonLib'] = {
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
      "includes" : {
         "def" : [ {
            "localIdentifier" : "common",
            "path" : "Common"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "MeasurementPeriod",
            "accessLevel" : "Public",
            "default" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2013",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }
               },
               "high" : {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2014",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }
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
                  "dataType" : "{http://hl7.org/fhir}Patient",
                  "templateId" : "patient-qicore-qicore-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "ID",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "InDemographic",
               "libraryName" : "common",
               "type" : "ExpressionRef"
            }
         }, {
            "name" : "L",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Length",
               "operand" : {
                  "type" : "Indexer",
                  "operand" : [ {
                     "path" : "given",
                     "type" : "Property",
                     "source" : {
                        "type" : "Indexer",
                        "operand" : [ {
                           "path" : "name",
                           "type" : "Property",
                           "source" : {
                              "name" : "Patient",
                              "type" : "ExpressionRef"
                           }
                        }, {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        } ]
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "FuncTest",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "foo",
               "libraryName" : "common",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

### CommonLib2
library Common2
using QUICK
parameter SomeNumber default 17

context Patient

define TheParameter:
  SomeNumber

define function addToParameter(a Integer):
  SomeNumber + a

define function multiply(a Integer, b Integer) :
  a * b

define function square(a Integer):
  multiply(a, a)

define TwoTimesThree:
  multiply(2, 3)

define Two:
  2

define function addTwo(a Integer):
  a + Two

define TwoPlusOne:
  Two + 1

define SortUsingFunction:
  ({1, 3, 2, 5, 4}) N return Tuple{N: N} sort by square(N)
###

module.exports['CommonLib2'] = {
   "library" : {
      "identifier" : {
         "id" : "Common2"
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
      "parameters" : {
         "def" : [ {
            "name" : "SomeNumber",
            "accessLevel" : "Public",
            "default" : {
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "17",
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
                  "dataType" : "{http://hl7.org/fhir}Patient",
                  "templateId" : "patient-qicore-qicore-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "TheParameter",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "SomeNumber",
               "type" : "ParameterRef"
            }
         }, {
            "name" : "addToParameter",
            "context" : "Patient",
            "accessLevel" : "Public",
            "type" : "FunctionDef",
            "expression" : {
               "type" : "Add",
               "operand" : [ {
                  "name" : "SomeNumber",
                  "type" : "ParameterRef"
               }, {
                  "name" : "a",
                  "type" : "OperandRef"
               } ]
            },
            "operand" : [ {
               "name" : "a",
               "operandTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            } ]
         }, {
            "name" : "multiply",
            "context" : "Patient",
            "accessLevel" : "Public",
            "type" : "FunctionDef",
            "expression" : {
               "type" : "Multiply",
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
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            }, {
               "name" : "b",
               "operandTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            } ]
         }, {
            "name" : "square",
            "context" : "Patient",
            "accessLevel" : "Public",
            "type" : "FunctionDef",
            "expression" : {
               "name" : "multiply",
               "type" : "FunctionRef",
               "operand" : [ {
                  "name" : "a",
                  "type" : "OperandRef"
               }, {
                  "name" : "a",
                  "type" : "OperandRef"
               } ]
            },
            "operand" : [ {
               "name" : "a",
               "operandTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            } ]
         }, {
            "name" : "TwoTimesThree",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "multiply",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "3",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "Two",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "2",
               "type" : "Literal"
            }
         }, {
            "name" : "addTwo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "type" : "FunctionDef",
            "expression" : {
               "type" : "Add",
               "operand" : [ {
                  "name" : "a",
                  "type" : "OperandRef"
               }, {
                  "name" : "Two",
                  "type" : "ExpressionRef"
               } ]
            },
            "operand" : [ {
               "name" : "a",
               "operandTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            } ]
         }, {
            "name" : "TwoPlusOne",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Add",
               "operand" : [ {
                  "name" : "Two",
                  "type" : "ExpressionRef"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "SortUsingFunction",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "N",
                  "expression" : {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "N",
                        "value" : {
                           "name" : "N",
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
                        "name" : "square",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "name" : "N",
                           "type" : "IdentifierRef"
                        } ]
                     }
                  } ]
               }
            }
         } ]
      }
   }
}

### Using CommonLib2
library TestSnippet version '1'
using QUICK
include Common2 called common2

context Patient

define ExprUsesParam: common2.TheParameter
define FuncUsesParam: common2.addToParameter(5)
define ExprCallsFunc: common2.TwoTimesThree
define FuncCallsFunc: common2.square(5)
define ExprUsesExpr: common2.TwoPlusOne
define FuncUsesExpr: common2.addTwo(5)
define ExprSortsOnFunc: common2.SortUsingFunction
###

module.exports['Using CommonLib2'] = {
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
      "includes" : {
         "def" : [ {
            "localIdentifier" : "common2",
            "path" : "Common2"
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
            "name" : "ExprUsesParam",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "TheParameter",
               "libraryName" : "common2",
               "type" : "ExpressionRef"
            }
         }, {
            "name" : "FuncUsesParam",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "addToParameter",
               "libraryName" : "common2",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ExprCallsFunc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "TwoTimesThree",
               "libraryName" : "common2",
               "type" : "ExpressionRef"
            }
         }, {
            "name" : "FuncCallsFunc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "square",
               "libraryName" : "common2",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ExprUsesExpr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "TwoPlusOne",
               "libraryName" : "common2",
               "type" : "ExpressionRef"
            }
         }, {
            "name" : "FuncUsesExpr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "addTwo",
               "libraryName" : "common2",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ExprSortsOnFunc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "SortUsingFunction",
               "libraryName" : "common2",
               "type" : "ExpressionRef"
            }
         } ]
      }
   }
}

