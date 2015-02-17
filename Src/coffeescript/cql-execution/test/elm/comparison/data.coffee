###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### Equal
library TestSnippet version '1'
using QUICK
context Patient
define AGtB_Int = 5 = 4
define AEqB_Int = 5 = 5
define ALtB_Int = 5 = 6
define EqTuples = tuple{a: 1, b: tuple{c: 1}} = tuple{a: 1, b: tuple{c: 1}}
define UneqTuples = tuple{a: 1, b: tuple{c: 1}} = tuple{a: 1, b: tuple{c: -1}}
define EqDateTimes = DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) = DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define UneqDateTimes = DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) = DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define EqDateTimesTZ = DateTime(2000, 3, 15, 23, 30, 25, 200, +1.0) = DateTime(2000, 3, 16, 2, 30, 25, 200, +4.0)
define UneqDateTimesTZ = DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) = DateTime(2000, 3, 15, 13, 30, 25, 200, +2.0)
define PossiblyEqualDateTimes = DateTime(2000, 3, 15) = DateTime(2000)
define ImpossiblyEqualDateTimes = DateTime(2000, 3, 15) = DateTime(2000, 4)
###

module.exports['Equal'] = {
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
            "name" : "AGtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
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
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
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
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "6",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "EqTuples",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "Tuple",
                  "element" : [ {
                     "name" : "a",
                     "value" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }
                  }, {
                     "name" : "b",
                     "value" : {
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "c",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        } ]
                     }
                  } ]
               }, {
                  "type" : "Tuple",
                  "element" : [ {
                     "name" : "a",
                     "value" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }
                  }, {
                     "name" : "b",
                     "value" : {
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "c",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        } ]
                     }
                  } ]
               } ]
            }
         }, {
            "name" : "UneqTuples",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "Tuple",
                  "element" : [ {
                     "name" : "a",
                     "value" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }
                  }, {
                     "name" : "b",
                     "value" : {
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "c",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        } ]
                     }
                  } ]
               }, {
                  "type" : "Tuple",
                  "element" : [ {
                     "name" : "a",
                     "value" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }
                  }, {
                     "name" : "b",
                     "value" : {
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "c",
                           "value" : {
                              "type" : "Negate",
                              "operand" : {
                                 "valueType" : "{urn:hl7-org:elm:r1}Integer",
                                 "value" : "1",
                                 "type" : "Literal"
                              }
                           }
                        } ]
                     }
                  } ]
               } ]
            }
         }, {
            "name" : "EqDateTimes",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "13",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "30",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "200",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  } ]
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "13",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "30",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "200",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "UneqDateTimes",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "13",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "30",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "200",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  } ]
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "13",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "30",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "201",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "EqDateTimesTZ",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "23",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "30",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "200",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  } ]
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "16",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "30",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "200",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "4.0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "UneqDateTimesTZ",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "13",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "30",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "200",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  } ]
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "13",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "30",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "200",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "2.0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "PossiblyEqualDateTimes",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  } ]
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "ImpossiblyEqualDateTimes",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  } ]
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

### NotEqual
library TestSnippet version '1'
using QUICK
context Patient
define AGtB_Int = 5 <> 4
define AEqB_Int = 5 <> 5
define ALtB_Int = 5 <> 6
define EqTuples = tuple{a: 1, b: tuple{c: 1}} <> tuple{a: 1, b: tuple{c: 1}}
define UneqTuples = tuple{a: 1, b: tuple{c: 1}} <> tuple{a: 1, b: tuple{c: -1}}
define EqDateTimes = DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) <> DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define UneqDateTimes = DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) <> DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define EqDateTimesTZ = DateTime(2000, 3, 15, 23, 30, 25, 200, +1.0) <> DateTime(2000, 3, 16, 2, 30, 25, 200, +4.0)
define UneqDateTimesTZ = DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) <> DateTime(2000, 3, 15, 13, 30, 25, 200, +2.0)
define PossiblyEqualDateTimes = DateTime(2000, 3, 15) <> DateTime(2000)
define ImpossiblyEqualDateTimes = DateTime(2000, 3, 15) <> DateTime(2000, 4)
###

module.exports['NotEqual'] = {
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
            "name" : "AGtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "AEqB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ALtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "EqTuples",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "type" : "Tuple",
                           "element" : [ {
                              "name" : "c",
                              "value" : {
                                 "valueType" : "{urn:hl7-org:elm:r1}Integer",
                                 "value" : "1",
                                 "type" : "Literal"
                              }
                           } ]
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "type" : "Tuple",
                           "element" : [ {
                              "name" : "c",
                              "value" : {
                                 "valueType" : "{urn:hl7-org:elm:r1}Integer",
                                 "value" : "1",
                                 "type" : "Literal"
                              }
                           } ]
                        }
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "UneqTuples",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "type" : "Tuple",
                           "element" : [ {
                              "name" : "c",
                              "value" : {
                                 "valueType" : "{urn:hl7-org:elm:r1}Integer",
                                 "value" : "1",
                                 "type" : "Literal"
                              }
                           } ]
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "type" : "Tuple",
                           "element" : [ {
                              "name" : "c",
                              "value" : {
                                 "type" : "Negate",
                                 "operand" : {
                                    "valueType" : "{urn:hl7-org:elm:r1}Integer",
                                    "value" : "1",
                                    "type" : "Literal"
                                 }
                              }
                           } ]
                        }
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "EqDateTimes",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "13",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "30",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "25",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "200",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                        "value" : "1.0",
                        "type" : "Literal"
                     } ]
                  }, {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "13",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "30",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "25",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "200",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                        "value" : "1.0",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "UneqDateTimes",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "13",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "30",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "25",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "200",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                        "value" : "1.0",
                        "type" : "Literal"
                     } ]
                  }, {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "13",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "30",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "25",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "201",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                        "value" : "1.0",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "EqDateTimesTZ",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "23",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "30",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "25",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "200",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                        "value" : "1.0",
                        "type" : "Literal"
                     } ]
                  }, {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "16",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "30",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "25",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "200",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                        "value" : "4.0",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "UneqDateTimesTZ",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "13",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "30",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "25",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "200",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                        "value" : "1.0",
                        "type" : "Literal"
                     } ]
                  }, {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "13",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "30",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "25",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "200",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                        "value" : "2.0",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "PossiblyEqualDateTimes",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     } ]
                  }, {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "ImpossiblyEqualDateTimes",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     } ]
                  }, {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     } ]
                  } ]
               }
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

module.exports['Less'] = {
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
            "name" : "AGtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
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
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
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
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
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

module.exports['LessOrEqual'] = {
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
            "name" : "AGtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
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
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
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
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "6",
                  "type" : "Literal"
               } ]
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

module.exports['Greater'] = {
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
            "name" : "AGtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
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
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
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
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
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

module.exports['GreaterOrEqual'] = {
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
            "name" : "AGtB_Int",
            "context" : "Patient",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
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
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
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
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}Integer",
                  "value" : "6",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

