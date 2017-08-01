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
define AGtB_Int: 5 = 4
define AEqB_Int: 5 = 5
define ALtB_Int: 5 = 6
define EqTuples: Tuple{a: 1, b: Tuple{c: 1}} = Tuple{a: 1, b: Tuple{c: 1}}
define UneqTuples: Tuple{a: 1, b: Tuple{c: 1}} = Tuple{a: 1, b: Tuple{c: -1}}
define EqDateTimes: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) = DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define UneqDateTimes: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) = DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define EqDateTimesTZ: DateTime(2000, 3, 15, 23, 30, 25, 200, +1.0) = DateTime(2000, 3, 16, 2, 30, 25, 200, +4.0)
define UneqDateTimesTZ: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) = DateTime(2000, 3, 15, 13, 30, 25, 200, +2.0)
define PossiblyEqualDateTimes: DateTime(2000, 3, 15) = DateTime(2000)
define ImpossiblyEqualDateTimes: DateTime(2000, 3, 15) = DateTime(2000, 4)
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
            "uri" : "urn:hl7-org:elm-types:r1"
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
                  "templateId" : "patient-qicore-qicore-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "AGtB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "AEqB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ALtB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "6",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "EqTuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "Tuple",
                  "element" : [ {
                     "name" : "a",
                     "value" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "Tuple",
                  "element" : [ {
                     "name" : "a",
                     "value" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
                                 "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "13",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "30",
                     "type" : "Literal"
                  },
                  "second" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  },
                  "millisecond" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "200",
                     "type" : "Literal"
                  },
                  "timezoneOffset" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  }
               }, {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "13",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "30",
                     "type" : "Literal"
                  },
                  "second" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  },
                  "millisecond" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "200",
                     "type" : "Literal"
                  },
                  "timezoneOffset" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "UneqDateTimes",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "13",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "30",
                     "type" : "Literal"
                  },
                  "second" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  },
                  "millisecond" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "200",
                     "type" : "Literal"
                  },
                  "timezoneOffset" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  }
               }, {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "13",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "30",
                     "type" : "Literal"
                  },
                  "second" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  },
                  "millisecond" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "201",
                     "type" : "Literal"
                  },
                  "timezoneOffset" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "EqDateTimesTZ",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "23",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "30",
                     "type" : "Literal"
                  },
                  "second" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  },
                  "millisecond" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "200",
                     "type" : "Literal"
                  },
                  "timezoneOffset" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  }
               }, {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "16",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "30",
                     "type" : "Literal"
                  },
                  "second" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  },
                  "millisecond" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "200",
                     "type" : "Literal"
                  },
                  "timezoneOffset" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "4.0",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "UneqDateTimesTZ",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "13",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "30",
                     "type" : "Literal"
                  },
                  "second" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  },
                  "millisecond" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "200",
                     "type" : "Literal"
                  },
                  "timezoneOffset" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  }
               }, {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "13",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "30",
                     "type" : "Literal"
                  },
                  "second" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  },
                  "millisecond" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "200",
                     "type" : "Literal"
                  },
                  "timezoneOffset" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "2.0",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "PossiblyEqualDateTimes",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  }
               }, {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "ImpossiblyEqualDateTimes",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  }
               }, {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2000",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }
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
define AGtB_Int: 5 != 4
define AEqB_Int: 5 != 5
define ALtB_Int: 5 != 6
define EqTuples: Tuple{a: 1, b: Tuple{c: 1}} != Tuple{a: 1, b: Tuple{c: 1}}
define UneqTuples: Tuple{a: 1, b: Tuple{c: 1}} != Tuple{a: 1, b: Tuple{c: -1}}
define EqDateTimes: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) != DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0)
define UneqDateTimes: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) != DateTime(2000, 3, 15, 13, 30, 25, 201, +1.0)
define EqDateTimesTZ: DateTime(2000, 3, 15, 23, 30, 25, 200, +1.0) != DateTime(2000, 3, 16, 2, 30, 25, 200, +4.0)
define UneqDateTimesTZ: DateTime(2000, 3, 15, 13, 30, 25, 200, +1.0) != DateTime(2000, 3, 15, 13, 30, 25, 200, +2.0)
define PossiblyEqualDateTimes: DateTime(2000, 3, 15) != DateTime(2000)
define ImpossiblyEqualDateTimes: DateTime(2000, 3, 15) != DateTime(2000, 4)
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
            "uri" : "urn:hl7-org:elm-types:r1"
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
                  "templateId" : "patient-qicore-qicore-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "AGtB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "AEqB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ALtB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "EqTuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
                                 "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
                                 "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
                                 "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
                                    "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "DateTime",
                     "year" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     },
                     "month" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     },
                     "day" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     },
                     "hour" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "13",
                        "type" : "Literal"
                     },
                     "minute" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "30",
                        "type" : "Literal"
                     },
                     "second" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "25",
                        "type" : "Literal"
                     },
                     "millisecond" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "200",
                        "type" : "Literal"
                     },
                     "timezoneOffset" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                        "value" : "1.0",
                        "type" : "Literal"
                     }
                  }, {
                     "type" : "DateTime",
                     "year" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     },
                     "month" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     },
                     "day" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     },
                     "hour" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "13",
                        "type" : "Literal"
                     },
                     "minute" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "30",
                        "type" : "Literal"
                     },
                     "second" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "25",
                        "type" : "Literal"
                     },
                     "millisecond" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "200",
                        "type" : "Literal"
                     },
                     "timezoneOffset" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                        "value" : "1.0",
                        "type" : "Literal"
                     }
                  } ]
               }
            }
         }, {
            "name" : "UneqDateTimes",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "DateTime",
                     "year" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     },
                     "month" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     },
                     "day" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     },
                     "hour" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "13",
                        "type" : "Literal"
                     },
                     "minute" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "30",
                        "type" : "Literal"
                     },
                     "second" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "25",
                        "type" : "Literal"
                     },
                     "millisecond" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "200",
                        "type" : "Literal"
                     },
                     "timezoneOffset" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                        "value" : "1.0",
                        "type" : "Literal"
                     }
                  }, {
                     "type" : "DateTime",
                     "year" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     },
                     "month" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     },
                     "day" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     },
                     "hour" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "13",
                        "type" : "Literal"
                     },
                     "minute" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "30",
                        "type" : "Literal"
                     },
                     "second" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "25",
                        "type" : "Literal"
                     },
                     "millisecond" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "201",
                        "type" : "Literal"
                     },
                     "timezoneOffset" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                        "value" : "1.0",
                        "type" : "Literal"
                     }
                  } ]
               }
            }
         }, {
            "name" : "EqDateTimesTZ",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "DateTime",
                     "year" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     },
                     "month" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     },
                     "day" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     },
                     "hour" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "23",
                        "type" : "Literal"
                     },
                     "minute" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "30",
                        "type" : "Literal"
                     },
                     "second" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "25",
                        "type" : "Literal"
                     },
                     "millisecond" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "200",
                        "type" : "Literal"
                     },
                     "timezoneOffset" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                        "value" : "1.0",
                        "type" : "Literal"
                     }
                  }, {
                     "type" : "DateTime",
                     "year" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     },
                     "month" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     },
                     "day" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "16",
                        "type" : "Literal"
                     },
                     "hour" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     },
                     "minute" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "30",
                        "type" : "Literal"
                     },
                     "second" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "25",
                        "type" : "Literal"
                     },
                     "millisecond" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "200",
                        "type" : "Literal"
                     },
                     "timezoneOffset" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                        "value" : "4.0",
                        "type" : "Literal"
                     }
                  } ]
               }
            }
         }, {
            "name" : "UneqDateTimesTZ",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "DateTime",
                     "year" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     },
                     "month" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     },
                     "day" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     },
                     "hour" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "13",
                        "type" : "Literal"
                     },
                     "minute" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "30",
                        "type" : "Literal"
                     },
                     "second" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "25",
                        "type" : "Literal"
                     },
                     "millisecond" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "200",
                        "type" : "Literal"
                     },
                     "timezoneOffset" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                        "value" : "1.0",
                        "type" : "Literal"
                     }
                  }, {
                     "type" : "DateTime",
                     "year" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     },
                     "month" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     },
                     "day" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     },
                     "hour" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "13",
                        "type" : "Literal"
                     },
                     "minute" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "30",
                        "type" : "Literal"
                     },
                     "second" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "25",
                        "type" : "Literal"
                     },
                     "millisecond" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "200",
                        "type" : "Literal"
                     },
                     "timezoneOffset" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                        "value" : "2.0",
                        "type" : "Literal"
                     }
                  } ]
               }
            }
         }, {
            "name" : "PossiblyEqualDateTimes",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "DateTime",
                     "year" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     },
                     "month" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     },
                     "day" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     }
                  }, {
                     "type" : "DateTime",
                     "year" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     }
                  } ]
               }
            }
         }, {
            "name" : "ImpossiblyEqualDateTimes",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "DateTime",
                     "year" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     },
                     "month" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     },
                     "day" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     }
                  }, {
                     "type" : "DateTime",
                     "year" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2000",
                        "type" : "Literal"
                     },
                     "month" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }
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
define AGtB_Int: 5 < 4
define AEqB_Int: 5 < 5
define ALtB_Int: 5 < 6
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
            "uri" : "urn:hl7-org:elm-types:r1"
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
                  "templateId" : "patient-qicore-qicore-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "AGtB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "AEqB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ALtB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
define AGtB_Int: 5 <= 4
define AEqB_Int: 5 <= 5
define ALtB_Int: 5 <= 6
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
            "uri" : "urn:hl7-org:elm-types:r1"
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
                  "templateId" : "patient-qicore-qicore-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "AGtB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "AEqB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ALtB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
define AGtB_Int: 5 > 4
define AEqB_Int: 5 > 5
define ALtB_Int: 5 > 6
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
            "uri" : "urn:hl7-org:elm-types:r1"
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
                  "templateId" : "patient-qicore-qicore-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "AGtB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "AEqB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ALtB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
define AGtB_Int: 5 >= 4
define AEqB_Int: 5 >= 5
define ALtB_Int: 5 >= 6
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
            "uri" : "urn:hl7-org:elm-types:r1"
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
                  "templateId" : "patient-qicore-qicore-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "AGtB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "AEqB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ALtB_Int",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "6",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

### DurationsGreaterThan
library TestSnippet version '1'
using QUICK
context Patient
define AGtB__Year_G_Year_G: 3 year > 2 years
define AGtB__Year_G_Year_J: 3 year > 2 'a_j'
define AGtB__Year_G_Mo_G: 3 year > 2 months
define AGtB__Year_G_Mo_J: 3 year > 2 'mo_j'
define AGtB__Year_G_Week: 3 year > 52 weeks
define AGtB__Year_G_Day: 3 year > 365 days
define AGtB__Year_G_Hour: 3 year > 24 hours
define AGtB__Year_G_Minute: 3 year > 60 minutes
define AGtB__Year_G_Second: 3 year > 60 seconds
define AGtB__Year_G_Millisecond: 3 year > 1000 milliseconds
define AGtB__Year_J_Year_J: 3 'a_j' > 2 'a_j'
define AGtB__Year_J_Mo_G: 3 'a_j' > 30 months
define AGtB__Year_J_Mo_J: 3 'a_j' > 30 'mo_j'
define AGtB__Year_J_Week: 3 'a_j' > 52 weeks
define AGtB__Year_J_Day: 3 'a_j' > 365 days
define AGtB__Year_J_Hour: 3 'a_j' > 24 hours
define AGtB__Year_J_Minute: 3 'a_j' > 60 minutes
define AGtB__Year_J_Second: 3 'a_j' > 60 seconds
define AGtB__Year_J_Millisecond: 3 'a_j' > 1000 milliseconds
define AEqB__Year_G_Year_G: 3 year = 2 years
define AEqB__Year_G_Year_J: 3 year = 2 'a_j'
define AEqB__Year_G_Mo_G: 3 year = 2 months
define AEqB__Year_G_Mo_J: 3 year = 2 'mo_j'
define AEqB__Year_G_Week: 3 year = 52 weeks
define AEqB__Year_G_Day: 3 year = 365 days
define AEqB__Year_G_Hour: 3 year = 24 hours
define AEqB__Year_G_Minute: 3 year = 60 minutes
define AEqB__Year_G_Second: 3 year = 60 seconds
define AEqB__Year_G_Millisecond: 3 year = 1000 milliseconds
define AEqB__Year_J_Year_J: 3 'a_j' = 2 'a_j'
define AEqB__Year_J_Mo_G: 3 'a_j' = 30 months
define AEqB__Year_J_Mo_J: 3 'a_j' = 30 'mo_j'
define AEqB__Year_J_Week: 3 'a_j' = 52 weeks
define AEqB__Year_J_Day: 3 'a_j' = 365 days
define AEqB__Year_J_Hour: 3 'a_j' = 24 hours
define AEqB__Year_J_Minute: 3 'a_j' = 60 minutes
define AEqB__Year_J_Second: 3 'a_j' = 60 seconds
define AEqB__Year_J_Millisecond: 3 'a_j' = 1000 milliseconds
define ALtB__Year_G_Year_G: 3 year < 2 years
define ALtB__Year_G_Year_J: 3 year < 2 'a_j'
define ALtB__Year_G_Mo_G: 3 year < 2 months
define ALtB__Year_G_Mo_J: 3 year < 2 'mo_j'
define ALtB__Year_G_Week: 3 year < 52 weeks
define ALtB__Year_G_Day: 3 year < 365 days
define ALtB__Year_G_Hour: 3 year < 24 hours
define ALtB__Year_G_Minute: 3 year < 60 minutes
define ALtB__Year_G_Second: 3 year < 60 seconds
define ALtB__Year_G_Millisecond: 3 year < 1000 milliseconds
define ALtB__Year_J_Year_J: 3 'a_j' < 2 'a_j'
define ALtB__Year_J_Mo_G: 3 'a_j' < 30 months
define ALtB__Year_J_Mo_J: 3 'a_j' < 30 'mo_j'
define ALtB__Year_J_Week: 3 'a_j' < 52 weeks
define ALtB__Year_J_Day: 3 'a_j' < 365 days
define ALtB__Year_J_Hour: 3 'a_j' < 24 hours
define ALtB__Year_J_Minute: 3 'a_j' < 60 minutes
define ALtB__Year_J_Second: 3 'a_j' < 60 seconds
define ALtB__Year_J_Millisecond: 3 'a_j' < 1000 milliseconds
###

module.exports['DurationsGreaterThan'] = {
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
            "name" : "AGtB__Year_G_Year_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 2,
                  "unit" : "years",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 2,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 2,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 2,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 52,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 365,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 24,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 60,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 60,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 1000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 2,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 30,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 30,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 52,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 365,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 24,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 60,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 60,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 1000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Year_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 2,
                  "unit" : "years",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 2,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 2,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 2,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 52,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 365,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 24,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 60,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 60,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 1000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 2,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 30,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 30,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 52,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 365,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 24,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 60,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 60,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 1000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Year_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 2,
                  "unit" : "years",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 2,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 2,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 2,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 52,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 365,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 24,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 60,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 60,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 1000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 2,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 30,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 30,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 52,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 365,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 24,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 60,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 60,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 1000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         } ]
      }
   }
}

### DurationsLessThan
library TestSnippet version '1'
using QUICK
context Patient
define ALtB__Year_G_Year_G: 3 year < 4 year
define ALtB__Year_G_Year_J: 3 year < 5 'a_j'
define ALtB__Year_G_Mo_G: 3 year < 40 months
define ALtB__Year_G_Mo_J: 3 year < 40 'mo_j'
define ALtB__Year_G_Week: 3 year < 160 weeks
define ALtB__Year_G_Day: 3 year < 1100 days
define ALtB__Year_G_Hour: 3 year < 26400 hours
define ALtB__Year_G_Minute: 3 year < 1584000 minutes
define ALtB__Year_G_Second: 3 year < 95040000 seconds
define ALtB__Year_G_Millisecond: 3 year < 95040000000 milliseconds
define ALtB__Year_J_Year_J: 3 'a_j' < 4 'a_j'
define ALtB__Year_J_Mo_G: 3 'a_j' < 40 months
define ALtB__Year_J_Mo_J: 3 'a_j' < 37 'mo_j'
define ALtB__Year_J_Week: 3 'a_j' < 200 weeks
define ALtB__Year_J_Day: 3 'a_j' < 1100 days
define ALtB__Year_J_Hour: 3 'a_j' < 26400 hours
define ALtB__Year_J_Minute: 3 'a_j' < 1584000 minutes
define ALtB__Year_J_Second: 3 'a_j' < 95040000 seconds
define ALtB__Year_J_Millisecond: 3 'a_j' < 95040000000 milliseconds
define AGtB__Year_G_Year_G: 3 year > 4 year
define AGtB__Year_G_Year_J: 3 year > 5 'a_j'
define AGtB__Year_G_Mo_G: 3 year > 40 months
define AGtB__Year_G_Mo_J: 3 year > 40 'mo_j'
define AGtB__Year_G_Week: 3 year > 160 weeks
define AGtB__Year_G_Day: 3 year > 1100 days
define AGtB__Year_G_Hour: 3 year > 26400 hours
define AGtB__Year_G_Minute: 3 year > 1584000 minutes
define AGtB__Year_G_Second: 3 year > 95040000 seconds
define AGtB__Year_G_Millisecond: 3 year > 95040000000 milliseconds
define AGtB__Year_J_Year_J: 3 'a_j' > 4 'a_j'
define AGtB__Year_J_Mo_G: 3 'a_j' > 40 months
define AGtB__Year_J_Mo_J: 3 'a_j' > 37 'mo_j'
define AGtB__Year_J_Week: 3 'a_j' > 200 weeks
define AGtB__Year_J_Day: 3 'a_j' > 1100 days
define AGtB__Year_J_Hour: 3 'a_j' > 26400 hours
define AGtB__Year_J_Minute: 3 'a_j' > 1584000 minutes
define AGtB__Year_J_Second: 3 'a_j' > 95040000 seconds
define AGtB__Year_J_Millisecond: 3 'a_j' > 95040000000 milliseconds
define AEqB__Year_G_Year_G: 3 year = 4 year
define AEqB__Year_G_Year_J: 3 year = 5 'a_j'
define AEqB__Year_G_Mo_G: 3 year = 40 months
define AEqB__Year_G_Mo_J: 3 year = 40 'mo_j'
define AEqB__Year_G_Week: 3 year = 160 weeks
define AEqB__Year_G_Day: 3 year = 1100 days
define AEqB__Year_G_Hour: 3 year = 26400 hours
define AEqB__Year_G_Minute: 3 year = 1584000 minutes
define AEqB__Year_G_Second: 3 year = 95040000 seconds
define AEqB__Year_G_Millisecond: 3 year = 95040000000 milliseconds
define AEqB__Year_J_Year_J: 3 'a_j' = 4 'a_j'
define AEqB__Year_J_Mo_G: 3 'a_j' = 40 months
define AEqB__Year_J_Mo_J: 3 'a_j' = 37 'mo_j'
define AEqB__Year_J_Week: 3 'a_j' = 200 weeks
define AEqB__Year_J_Day: 3 'a_j' = 1100 days
define AEqB__Year_J_Hour: 3 'a_j' = 26400 hours
define AEqB__Year_J_Minute: 3 'a_j' = 1584000 minutes
define AEqB__Year_J_Second: 3 'a_j' = 95040000 seconds
define AEqB__Year_J_Millisecond: 3 'a_j' = 95040000000 milliseconds
###

module.exports['DurationsLessThan'] = {
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
            "name" : "ALtB__Year_G_Year_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "year",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 40,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 40,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 160,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 1100,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 26400,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 1584000,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 95040000,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 95040000000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 40,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 37,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 200,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 1100,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 26400,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 1584000,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 95040000,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 95040000000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Year_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "year",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 40,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 40,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 160,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 1100,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 26400,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 1584000,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 95040000,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 95040000000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 40,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 37,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 200,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 1100,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 26400,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 1584000,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 95040000,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 95040000000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Year_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "year",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 40,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 40,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 160,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 1100,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 26400,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 1584000,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 95040000,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 95040000000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 40,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 37,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 200,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 1100,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 26400,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 1584000,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 95040000,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 95040000000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         } ]
      }
   }
}

### DurationsEqual
library TestSnippet version '1'
using QUICK
context Patient
define AEqB__Year_G_Year_G: 3 year = 3 year
define AEqB__Year_G_Year_J: 3 year = 2.99993839835729 'a_j'
define AEqB__Year_G_Mo_G: 3 year = 36 months
define AEqB__Year_G_Mo_J: 3 year = 35.999260780287 'mo_j'
define AEqB__Year_G_Week: 3 year = 156.5325 weeks
define AEqB__Year_G_Day: 3 year = 1095.7275 days
define AEqB__Year_G_Hour: 3 year = 26297.46 hours
define AEqB__Year_G_Minute: 3 year = 1577847.6 minutes
define AEqB__Year_G_Second: 3 year = 94670856 seconds
define AEqB__Year_G_Millisecond: 3 year = 94670856000 milliseconds
define AEqB__Year_J_Year_J: 3 'a_j' = 3 'a_j'
define AEqB__Year_J_Mo_G: 3 'a_j' = 36.000739234893004 months
define AEqB__Year_J_Mo_J: 3 'a_j' = 36 'mo_j'
define AEqB__Year_J_Week: 3 'a_j' = 156.535714285713 weeks
define AEqB__Year_J_Day: 3 'a_j' = 1095.75 days
define AEqB__Year_J_Hour: 3 'a_j' = 26298 hours
define AEqB__Year_J_Minute: 3 'a_j' = 1577880 minutes
define AEqB__Year_J_Second: 3 'a_j' = 94672800 seconds
define AEqB__Year_J_Millisecond: 3 'a_j' = 94672800000 milliseconds
define ALtB__Year_G_Year_G: 3 year < 3 year
define ALtB__Year_G_Year_J: 3 year < 2.99993839835729 'a_j'
define ALtB__Year_G_Mo_G: 3 year < 36 months
define ALtB__Year_G_Mo_J: 3 year < 35.999260780287 'mo_j'
define ALtB__Year_G_Week: 3 year < 156.5325 weeks
define ALtB__Year_G_Day: 3 year < 1095.7275 days
define ALtB__Year_G_Hour: 3 year < 26297.46 hours
define ALtB__Year_G_Minute: 3 year < 1577847.6 minutes
define ALtB__Year_G_Second: 3 year < 94670856 seconds
define ALtB__Year_G_Millisecond: 3 year < 94670856000 milliseconds
define ALtB__Year_J_Year_J: 3 'a_j' < 3 'a_j'
define ALtB__Year_J_Mo_G: 3 'a_j' < 36.000739234893004 months
define ALtB__Year_J_Mo_J: 3 'a_j' < 36 'mo_j'
define ALtB__Year_J_Week: 3 'a_j' < 156.535714285713 weeks
define ALtB__Year_J_Day: 3 'a_j' < 1095.75 days
define ALtB__Year_J_Hour: 3 'a_j' < 26298 hours
define ALtB__Year_J_Minute: 3 'a_j' < 1577880 minutes
define ALtB__Year_J_Second: 3 'a_j' < 94672800 seconds
define ALtB__Year_J_Millisecond: 3 'a_j' < 94672800000 milliseconds
define AGtB__Year_G_Year_G: 3 year > 3 year
define AGtB__Year_G_Year_J: 3 year > 2.99993839835729 'a_j'
define AGtB__Year_G_Mo_G: 3 year > 36 months
define AGtB__Year_G_Mo_J: 3 year > 35.999260780287 'mo_j'
define AGtB__Year_G_Week: 3 year > 156.5325 weeks
define AGtB__Year_G_Day: 3 year > 1095.7275 days
define AGtB__Year_G_Hour: 3 year > 26297.46 hours
define AGtB__Year_G_Minute: 3 year > 1577847.6 minutes
define AGtB__Year_G_Second: 3 year > 94670856 seconds
define AGtB__Year_G_Millisecond: 3 year > 94670856000 milliseconds
define AGtB__Year_J_Year_J: 3 'a_j' > 3 'a_j'
define AGtB__Year_J_Mo_G: 3 'a_j' > 36.000739234893004 months
define AGtB__Year_J_Mo_J: 3 'a_j' > 36 'mo_j'
define AGtB__Year_J_Week: 3 'a_j' > 156.535714285713 weeks
define AGtB__Year_J_Day: 3 'a_j' > 1095.75 days
define AGtB__Year_J_Hour: 3 'a_j' > 26298 hours
define AGtB__Year_J_Minute: 3 'a_j' > 1577880 minutes
define AGtB__Year_J_Second: 3 'a_j' > 94672800 seconds
define AGtB__Year_J_Millisecond: 3 'a_j' > 94672800000 milliseconds
###

module.exports['DurationsEqual'] = {
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
            "name" : "AEqB__Year_G_Year_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 2.99993839835729,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 36,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 35.999260780287,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 156.5325,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 1095.7275,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 26297.46,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 1577847.6,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 94670856,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_G_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 94670856000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 36.000739234893004,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 36,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 156.535714285713,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 1095.75,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 26298,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 1577880,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 94672800,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB__Year_J_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 94672800000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Year_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 2.99993839835729,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 36,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 35.999260780287,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 156.5325,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 1095.7275,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 26297.46,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 1577847.6,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 94670856,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_G_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 94670856000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 36.000739234893004,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 36,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 156.535714285713,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 1095.75,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 26298,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 1577880,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 94672800,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB__Year_J_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 94672800000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Year_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 2.99993839835729,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 36,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 35.999260780287,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 156.5325,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 1095.7275,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 26297.46,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 1577847.6,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 94670856,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_G_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "year",
                  "type" : "Quantity"
               }, {
                  "value" : 94670856000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Year_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Mo_G",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 36.000739234893004,
                  "unit" : "months",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Mo_J",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 36,
                  "unit" : "mo_j",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Week",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 156.535714285713,
                  "unit" : "weeks",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Day",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 1095.75,
                  "unit" : "days",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Hour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 26298,
                  "unit" : "hours",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Minute",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 1577880,
                  "unit" : "minutes",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Second",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 94672800,
                  "unit" : "seconds",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB__Year_J_Millisecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 3,
                  "unit" : "a_j",
                  "type" : "Quantity"
               }, {
                  "value" : 94672800000,
                  "unit" : "milliseconds",
                  "type" : "Quantity"
               } ]
            }
         } ]
      }
   }
}

