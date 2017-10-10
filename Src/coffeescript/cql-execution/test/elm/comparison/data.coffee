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
define AGtB_Quantity: 5 'm' = 4 'm'
define AEqB_Quantity: 5 'm' = 5 'm'
define ALtB_Quantity: 5 'm' = 6 'm'
define AGtB_Quantity_diff: 5 'm' = 5 'cm'
define AEqB_Quantity_diff: 5 'm' = 500 'cm'
define ALtB_Quantity_diff: 5 'm' = 5 'km'
define AGtB_Quantity_incompatible: 5 'Cel' = 4 'm'
define AEqB_Quantity_incompatible: 5 'Cel' = 5 'm'
define ALtB_Quantity_incompatible: 5 'Cel' = 40 'm'
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
         }, {
            "name" : "AGtB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 6,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "cm",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 500,
                  "unit" : "cm",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "km",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "Cel",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "Cel",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "Cel",
                  "type" : "Quantity"
               }, {
                  "value" : 40,
                  "unit" : "m",
                  "type" : "Quantity"
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
define AGtB_Quantity: 5 'm' != 4 'm'
define AEqB_Quantity: 5 'm' != 5 'm'
define ALtB_Quantity: 5 'm' != 6 'm'
define AGtB_Quantity_diff: 5 'm' != 5 'cm'
define AEqB_Quantity_diff: 5 'm' != 500 'cm'
define ALtB_Quantity_diff: 5 'm' != 5 'km'
define AGtB_Quantity_incompatible: 5 'Cel' != 4 'm'
define AEqB_Quantity_incompatible: 5 'Cel' != 5 'm'
define ALtB_Quantity_incompatible: 5 'Cel' != 40 'm'
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
         }, {
            "name" : "AGtB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "value" : 5,
                     "unit" : "m",
                     "type" : "Quantity"
                  }, {
                     "value" : 4,
                     "unit" : "m",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "AEqB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "value" : 5,
                     "unit" : "m",
                     "type" : "Quantity"
                  }, {
                     "value" : 5,
                     "unit" : "m",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "ALtB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "value" : 5,
                     "unit" : "m",
                     "type" : "Quantity"
                  }, {
                     "value" : 6,
                     "unit" : "m",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "AGtB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "value" : 5,
                     "unit" : "m",
                     "type" : "Quantity"
                  }, {
                     "value" : 5,
                     "unit" : "cm",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "AEqB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "value" : 5,
                     "unit" : "m",
                     "type" : "Quantity"
                  }, {
                     "value" : 500,
                     "unit" : "cm",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "ALtB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "value" : 5,
                     "unit" : "m",
                     "type" : "Quantity"
                  }, {
                     "value" : 5,
                     "unit" : "km",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "AGtB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "value" : 5,
                     "unit" : "Cel",
                     "type" : "Quantity"
                  }, {
                     "value" : 4,
                     "unit" : "m",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "AEqB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "value" : 5,
                     "unit" : "Cel",
                     "type" : "Quantity"
                  }, {
                     "value" : 5,
                     "unit" : "m",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "ALtB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "value" : 5,
                     "unit" : "Cel",
                     "type" : "Quantity"
                  }, {
                     "value" : 40,
                     "unit" : "m",
                     "type" : "Quantity"
                  } ]
               }
            }
         } ]
      }
   }
}

### Equivalent
library TestSnippet version '1'
using QUICK
context Patient
define ANull_BDefined: null ~ 4
define ADefined_BNull: 5 ~ null
define ANull_BNull: null ~ null
define ADefined_BDefined: 3 ~ 3
###

module.exports['Equivalent'] = {
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
            "name" : "ANull_BDefined",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equivalent",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ADefined_BNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equivalent",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "type" : "Null"
               } ]
            }
         }, {
            "name" : "ANull_BNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equivalent",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "type" : "Null"
               } ]
            }
         }, {
            "name" : "ADefined_BDefined",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equivalent",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "3",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "3",
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
define AGtB_Int: 5 < 4
define AEqB_Int: 5 < 5
define ALtB_Int: 5 < 6
define AGtB_Quantity: 5 'm' < 4 'm'
define AEqB_Quantity: 5 'm' < 5 'm'
define ALtB_Quantity: 5 'm' < 6 'm'
define AGtB_Quantity_diff: 5 'm' < 5 'cm'
define AEqB_Quantity_diff: 5 'm' < 500 'cm'
define ALtB_Quantity_diff: 5 'm' < 5 'km'
define AGtB_Quantity_incompatible: 5 'Cel' < 4 'm'
define AEqB_Quantity_incompatible: 5 'Cel' < 5 'm'
define ALtB_Quantity_incompatible: 5 'Cel' < 40 'm'
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
         }, {
            "name" : "AGtB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 6,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "cm",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 500,
                  "unit" : "cm",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "km",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "Cel",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "Cel",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Less",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "Cel",
                  "type" : "Quantity"
               }, {
                  "value" : 40,
                  "unit" : "m",
                  "type" : "Quantity"
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
define AGtB_Quantity: 5 'm' <= 4 'm'
define AEqB_Quantity: 5 'm' <= 5 'm'
define ALtB_Quantity: 5 'm' <= 6 'm'
define AGtB_Quantity_diff: 5 'm' <= 4 'm'
define AEqB_Quantity_diff: 5 'm' <= 500 'cm'
define ALtB_Quantity_diff: 5 'm' <= 5 'km'
define AGtB_Quantity_incompatible: 5 'Cel' <= 4 'm'
define AEqB_Quantity_incompatible: 5 'Cel' <= 5 'm'
define ALtB_Quantity_incompatible: 5 'Cel' <= 40 'm'
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
         }, {
            "name" : "AGtB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 6,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 500,
                  "unit" : "cm",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "km",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "Cel",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "Cel",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "LessOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "Cel",
                  "type" : "Quantity"
               }, {
                  "value" : 40,
                  "unit" : "m",
                  "type" : "Quantity"
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
define AGtB_Quantity: 5 'm' > 4 'm'
define AEqB_Quantity: 5 'm' > 5 'm'
define ALtB_Quantity: 5 'm' > 6 'm'
define AGtB_Quantity_diff: 5 'm' > 5 'cm'
define AEqB_Quantity_diff: 5 'm' > 500 'cm'
define ALtB_Quantity_diff: 5 'm' > 5 'km'
define AGtB_Quantity_incompatible: 5 'Cel' > 4 'm'
define AEqB_Quantity_incompatible: 5 'Cel' > 5 'm'
define ALtB_Quantity_incompatible: 5 'Cel' > 40 'm'
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
         }, {
            "name" : "AGtB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 6,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "cm",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 500,
                  "unit" : "cm",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "km",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "Cel",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "Cel",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "Cel",
                  "type" : "Quantity"
               }, {
                  "value" : 40,
                  "unit" : "m",
                  "type" : "Quantity"
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
define AGtB_Quantity: 5 'm' >= 4 'm'
define AEqB_Quantity: 5 'm' >= 5 'm'
define ALtB_Quantity: 5 'm' >= 6 'm'
define AGtB_Quantity_diff: 5 'm' >= 5 'cm'
define AEqB_Quantity_diff: 5 'm' >= 500 'cm'
define ALtB_Quantity_diff: 5 'm' >= 5 'km'
define AGtB_Quantity_incompatible: 5 'Cel' >= 4 'm'
define AEqB_Quantity_incompatible: 5 'Cel' >= 5 'm'
define ALtB_Quantity_incompatible: 5 'Cel' >= 40 'm'
define DivideUcum_incompatible: (100 '[nmi_i]' / 2 'h') > 49 'mg/[lb_av]'
define DivideUcum: (100 'mg' / 2 '[lb_av]') > 49 'mg/[lb_av]'
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
         }, {
            "name" : "AGtB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB_Quantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 6,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "cm",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 500,
                  "unit" : "cm",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB_Quantity_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "km",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AGtB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "Cel",
                  "type" : "Quantity"
               }, {
                  "value" : 4,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "AEqB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "Cel",
                  "type" : "Quantity"
               }, {
                  "value" : 5,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "ALtB_Quantity_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "GreaterOrEqual",
               "operand" : [ {
                  "value" : 5,
                  "unit" : "Cel",
                  "type" : "Quantity"
               }, {
                  "value" : 40,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "DivideUcum_incompatible",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "type" : "Divide",
                  "operand" : [ {
                     "value" : 100,
                     "unit" : "[nmi_i]",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "h",
                     "type" : "Quantity"
                  } ]
               }, {
                  "value" : 49,
                  "unit" : "mg/[lb_av]",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "name" : "DivideUcum",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Greater",
               "operand" : [ {
                  "type" : "Divide",
                  "operand" : [ {
                     "value" : 100,
                     "unit" : "mg",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "[lb_av]",
                     "type" : "Quantity"
                  } ]
               }, {
                  "value" : 49,
                  "unit" : "mg/[lb_av]",
                  "type" : "Quantity"
               } ]
            }
         } ]
      }
   }
}

