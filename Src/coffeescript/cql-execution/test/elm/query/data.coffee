###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### DateRangeOptimizedQuery
library TestSnippet version '1'
using QUICK
valueset "Ambulatory/ED Visit": '2.16.840.1.113883.3.464.1003.101.12.1061'
parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))
context Patient
define EncountersDuringMP: [Encounter] E where E.period during MeasurementPeriod
define AmbulatoryEncountersDuringMP: [Encounter: "Ambulatory/ED Visit"] E where E.period during MeasurementPeriod
define AmbulatoryEncountersIncludedInMP: [Encounter: "Ambulatory/ED Visit"] E where E.period included in MeasurementPeriod
###

module.exports['DateRangeOptimizedQuery'] = {
   "library" : {
      "annotation" : [ {
         "message" : "List-valued expression was demoted to a singleton.",
         "errorType" : "semantic",
         "errorSeverity" : "warning",
         "type" : "CqlToElmError"
      }, {
         "message" : "List-valued expression was demoted to a singleton.",
         "errorType" : "semantic",
         "errorSeverity" : "warning",
         "type" : "CqlToElmError"
      } ],
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
            "localId" : "1",
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "localId" : "12",
            "name" : "MeasurementPeriod",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "11",
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "localId" : "6",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "3",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2013",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "4",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }
               },
               "high" : {
                  "localId" : "10",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "7",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2014",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "8",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }
               }
            }
         } ]
      },
      "valueSets" : {
         "def" : [ {
            "localId" : "2",
            "name" : "Ambulatory/ED Visit",
            "id" : "2.16.840.1.113883.3.464.1003.101.12.1061",
            "accessLevel" : "Public"
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
            "localId" : "20",
            "name" : "EncountersDuringMP",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "20",
                  "s" : [ {
                     "value" : [ "define ","EncountersDuringMP",": " ]
                  }, {
                     "r" : "19",
                     "s" : [ {
                        "s" : [ {
                           "r" : "14",
                           "s" : [ {
                              "r" : "13",
                              "s" : [ {
                                 "r" : "13",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "18",
                        "s" : [ {
                           "value" : [ "where " ]
                        }, {
                           "r" : "18",
                           "s" : [ {
                              "r" : "16",
                              "s" : [ {
                                 "r" : "15",
                                 "s" : [ {
                                    "value" : [ "E" ]
                                 } ]
                              }, {
                                 "value" : [ "." ]
                              }, {
                                 "r" : "16",
                                 "s" : [ {
                                    "value" : [ "period" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","during"," " ]
                           }, {
                              "r" : "17",
                              "s" : [ {
                                 "value" : [ "MeasurementPeriod" ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "19",
               "type" : "Query",
               "source" : [ {
                  "localId" : "14",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "13",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "dateProperty" : "period",
                     "type" : "Retrieve",
                     "dateRange" : {
                        "localId" : "17",
                        "name" : "MeasurementPeriod",
                        "type" : "ParameterRef"
                     }
                  }
               } ],
               "relationship" : [ ]
            }
         }, {
            "localId" : "28",
            "name" : "AmbulatoryEncountersDuringMP",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "28",
                  "s" : [ {
                     "value" : [ "define ","AmbulatoryEncountersDuringMP",": " ]
                  }, {
                     "r" : "27",
                     "s" : [ {
                        "s" : [ {
                           "r" : "22",
                           "s" : [ {
                              "r" : "21",
                              "s" : [ {
                                 "r" : "21",
                                 "s" : [ {
                                    "value" : [ "[","Encounter",": " ]
                                 }, {
                                    "s" : [ {
                                       "value" : [ "\"Ambulatory/ED Visit\"" ]
                                    } ]
                                 }, {
                                    "value" : [ "]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "26",
                        "s" : [ {
                           "value" : [ "where " ]
                        }, {
                           "r" : "26",
                           "s" : [ {
                              "r" : "24",
                              "s" : [ {
                                 "r" : "23",
                                 "s" : [ {
                                    "value" : [ "E" ]
                                 } ]
                              }, {
                                 "value" : [ "." ]
                              }, {
                                 "r" : "24",
                                 "s" : [ {
                                    "value" : [ "period" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","during"," " ]
                           }, {
                              "r" : "25",
                              "s" : [ {
                                 "value" : [ "MeasurementPeriod" ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "27",
               "type" : "Query",
               "source" : [ {
                  "localId" : "22",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "21",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "codeProperty" : "type",
                     "dateProperty" : "period",
                     "type" : "Retrieve",
                     "codes" : {
                        "name" : "Ambulatory/ED Visit",
                        "type" : "ValueSetRef"
                     },
                     "dateRange" : {
                        "localId" : "25",
                        "name" : "MeasurementPeriod",
                        "type" : "ParameterRef"
                     }
                  }
               } ],
               "relationship" : [ ]
            }
         }, {
            "localId" : "36",
            "name" : "AmbulatoryEncountersIncludedInMP",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "36",
                  "s" : [ {
                     "value" : [ "define ","AmbulatoryEncountersIncludedInMP",": " ]
                  }, {
                     "r" : "35",
                     "s" : [ {
                        "s" : [ {
                           "r" : "30",
                           "s" : [ {
                              "r" : "29",
                              "s" : [ {
                                 "r" : "29",
                                 "s" : [ {
                                    "value" : [ "[","Encounter",": " ]
                                 }, {
                                    "s" : [ {
                                       "value" : [ "\"Ambulatory/ED Visit\"" ]
                                    } ]
                                 }, {
                                    "value" : [ "]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "34",
                        "s" : [ {
                           "value" : [ "where " ]
                        }, {
                           "r" : "34",
                           "s" : [ {
                              "r" : "32",
                              "s" : [ {
                                 "r" : "31",
                                 "s" : [ {
                                    "value" : [ "E" ]
                                 } ]
                              }, {
                                 "value" : [ "." ]
                              }, {
                                 "r" : "32",
                                 "s" : [ {
                                    "value" : [ "period" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","included in"," " ]
                           }, {
                              "r" : "33",
                              "s" : [ {
                                 "value" : [ "MeasurementPeriod" ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "35",
               "type" : "Query",
               "source" : [ {
                  "localId" : "30",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "29",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "codeProperty" : "type",
                     "dateProperty" : "period",
                     "type" : "Retrieve",
                     "codes" : {
                        "name" : "Ambulatory/ED Visit",
                        "type" : "ValueSetRef"
                     },
                     "dateRange" : {
                        "localId" : "33",
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
valueset "Ambulatory/ED Visit": '2.16.840.1.113883.3.464.1003.101.12.1061'
parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))
context Patient
define MPIncludedAmbulatoryEncounters: [Encounter: "Ambulatory/ED Visit"] E where MeasurementPeriod includes E.period
###

module.exports['IncludesQuery'] = {
   "library" : {
      "annotation" : [ {
         "message" : "List-valued expression was demoted to a singleton.",
         "errorType" : "semantic",
         "errorSeverity" : "warning",
         "type" : "CqlToElmError"
      } ],
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
            "localId" : "1",
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "localId" : "12",
            "name" : "MeasurementPeriod",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "11",
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "localId" : "6",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "3",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2013",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "4",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }
               },
               "high" : {
                  "localId" : "10",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "7",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2014",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "8",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }
               }
            }
         } ]
      },
      "valueSets" : {
         "def" : [ {
            "localId" : "2",
            "name" : "Ambulatory/ED Visit",
            "id" : "2.16.840.1.113883.3.464.1003.101.12.1061",
            "accessLevel" : "Public"
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
            "localId" : "20",
            "name" : "MPIncludedAmbulatoryEncounters",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "20",
                  "s" : [ {
                     "value" : [ "define ","MPIncludedAmbulatoryEncounters",": " ]
                  }, {
                     "r" : "19",
                     "s" : [ {
                        "s" : [ {
                           "r" : "14",
                           "s" : [ {
                              "r" : "13",
                              "s" : [ {
                                 "r" : "13",
                                 "s" : [ {
                                    "value" : [ "[","Encounter",": " ]
                                 }, {
                                    "s" : [ {
                                       "value" : [ "\"Ambulatory/ED Visit\"" ]
                                    } ]
                                 }, {
                                    "value" : [ "]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "18",
                        "s" : [ {
                           "value" : [ "where " ]
                        }, {
                           "r" : "18",
                           "s" : [ {
                              "r" : "15",
                              "s" : [ {
                                 "value" : [ "MeasurementPeriod" ]
                              } ]
                           }, {
                              "value" : [ " ","includes"," " ]
                           }, {
                              "r" : "17",
                              "s" : [ {
                                 "r" : "16",
                                 "s" : [ {
                                    "value" : [ "E" ]
                                 } ]
                              }, {
                                 "value" : [ "." ]
                              }, {
                                 "r" : "17",
                                 "s" : [ {
                                    "value" : [ "period" ]
                                 } ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "19",
               "type" : "Query",
               "source" : [ {
                  "localId" : "14",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "13",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "codeProperty" : "type",
                     "type" : "Retrieve",
                     "codes" : {
                        "name" : "Ambulatory/ED Visit",
                        "type" : "ValueSetRef"
                     }
                  }
               } ],
               "relationship" : [ ],
               "where" : {
                  "localId" : "18",
                  "type" : "Includes",
                  "operand" : [ {
                     "localId" : "15",
                     "name" : "MeasurementPeriod",
                     "type" : "ParameterRef"
                  }, {
                     "localId" : "17",
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
parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))
context Patient
define msQueryWhere: from [Encounter] E,
[Condition] C
where E.period included in MeasurementPeriod

define msQueryWhere2: from [Encounter] E, [Condition] C
where  E.period  included in MeasurementPeriod and C.id = 'http://cqframework.org/3/2'

define msQuery: from [Encounter] E, [Condition] C return {E: E, C:C}
###

module.exports['MultiSourceQuery'] = {
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
            "localId" : "1",
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "localId" : "11",
            "name" : "MeasurementPeriod",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "10",
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "localId" : "5",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "2",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2013",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "3",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "4",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }
               },
               "high" : {
                  "localId" : "9",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "6",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2014",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "7",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "8",
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
            "localId" : "21",
            "name" : "msQueryWhere",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "21",
                  "s" : [ {
                     "value" : [ "define ","msQueryWhere",": " ]
                  }, {
                     "r" : "20",
                     "s" : [ {
                        "s" : [ {
                           "value" : [ "from " ]
                        }, {
                           "r" : "13",
                           "s" : [ {
                              "r" : "12",
                              "s" : [ {
                                 "r" : "12",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        }, {
                           "value" : [ ",\n" ]
                        }, {
                           "r" : "15",
                           "s" : [ {
                              "r" : "14",
                              "s" : [ {
                                 "r" : "14",
                                 "s" : [ {
                                    "value" : [ "[","Condition","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","C" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ "\n" ]
                     }, {
                        "r" : "19",
                        "s" : [ {
                           "value" : [ "where " ]
                        }, {
                           "r" : "19",
                           "s" : [ {
                              "r" : "17",
                              "s" : [ {
                                 "r" : "16",
                                 "s" : [ {
                                    "value" : [ "E" ]
                                 } ]
                              }, {
                                 "value" : [ "." ]
                              }, {
                                 "r" : "17",
                                 "s" : [ {
                                    "value" : [ "period" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","included in"," " ]
                           }, {
                              "r" : "18",
                              "s" : [ {
                                 "value" : [ "MeasurementPeriod" ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "20",
               "type" : "Query",
               "source" : [ {
                  "localId" : "13",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "12",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "dateProperty" : "period",
                     "type" : "Retrieve",
                     "dateRange" : {
                        "localId" : "18",
                        "name" : "MeasurementPeriod",
                        "type" : "ParameterRef"
                     }
                  }
               }, {
                  "localId" : "15",
                  "alias" : "C",
                  "expression" : {
                     "localId" : "14",
                     "dataType" : "{http://hl7.org/fhir}Condition",
                     "templateId" : "condition-qicore-qicore-condition",
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
            "localId" : "36",
            "name" : "msQueryWhere2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "36",
                  "s" : [ {
                     "value" : [ "define ","msQueryWhere2",": " ]
                  }, {
                     "r" : "35",
                     "s" : [ {
                        "s" : [ {
                           "value" : [ "from " ]
                        }, {
                           "r" : "23",
                           "s" : [ {
                              "r" : "22",
                              "s" : [ {
                                 "r" : "22",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "25",
                           "s" : [ {
                              "r" : "24",
                              "s" : [ {
                                 "r" : "24",
                                 "s" : [ {
                                    "value" : [ "[","Condition","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","C" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ "\n" ]
                     }, {
                        "r" : "34",
                        "s" : [ {
                           "value" : [ "where  " ]
                        }, {
                           "r" : "34",
                           "s" : [ {
                              "r" : "29",
                              "s" : [ {
                                 "r" : "27",
                                 "s" : [ {
                                    "r" : "26",
                                    "s" : [ {
                                       "value" : [ "E" ]
                                    } ]
                                 }, {
                                    "value" : [ "." ]
                                 }, {
                                    "r" : "27",
                                    "s" : [ {
                                       "value" : [ "period" ]
                                    } ]
                                 } ]
                              }, {
                                 "value" : [ "  ","included in"," " ]
                              }, {
                                 "r" : "28",
                                 "s" : [ {
                                    "value" : [ "MeasurementPeriod" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " and " ]
                           }, {
                              "r" : "33",
                              "s" : [ {
                                 "r" : "31",
                                 "s" : [ {
                                    "r" : "30",
                                    "s" : [ {
                                       "value" : [ "C" ]
                                    } ]
                                 }, {
                                    "value" : [ "." ]
                                 }, {
                                    "r" : "31",
                                    "s" : [ {
                                       "value" : [ "id" ]
                                    } ]
                                 } ]
                              }, {
                                 "value" : [ " ","="," " ]
                              }, {
                                 "r" : "32",
                                 "s" : [ {
                                    "value" : [ "'http://cqframework.org/3/2'" ]
                                 } ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "35",
               "type" : "Query",
               "source" : [ {
                  "localId" : "23",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "22",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "dateProperty" : "period",
                     "type" : "Retrieve",
                     "dateRange" : {
                        "localId" : "28",
                        "name" : "MeasurementPeriod",
                        "type" : "ParameterRef"
                     }
                  }
               }, {
                  "localId" : "25",
                  "alias" : "C",
                  "expression" : {
                     "localId" : "24",
                     "dataType" : "{http://hl7.org/fhir}Condition",
                     "templateId" : "condition-qicore-qicore-condition",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "where" : {
                  "localId" : "33",
                  "type" : "Equal",
                  "operand" : [ {
                     "localId" : "31",
                     "path" : "id",
                     "scope" : "C",
                     "type" : "Property"
                  }, {
                     "localId" : "32",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
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
            "localId" : "46",
            "name" : "msQuery",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "46",
                  "s" : [ {
                     "value" : [ "define ","msQuery",": " ]
                  }, {
                     "r" : "45",
                     "s" : [ {
                        "s" : [ {
                           "value" : [ "from " ]
                        }, {
                           "r" : "38",
                           "s" : [ {
                              "r" : "37",
                              "s" : [ {
                                 "r" : "37",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "40",
                           "s" : [ {
                              "r" : "39",
                              "s" : [ {
                                 "r" : "39",
                                 "s" : [ {
                                    "value" : [ "[","Condition","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","C" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "44",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "43",
                           "s" : [ {
                              "value" : [ "{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "E",": " ]
                              }, {
                                 "r" : "41",
                                 "s" : [ {
                                    "value" : [ "E" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "C",":" ]
                              }, {
                                 "r" : "42",
                                 "s" : [ {
                                    "value" : [ "C" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "45",
               "type" : "Query",
               "source" : [ {
                  "localId" : "38",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "37",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               }, {
                  "localId" : "40",
                  "alias" : "C",
                  "expression" : {
                     "localId" : "39",
                     "dataType" : "{http://hl7.org/fhir}Condition",
                     "templateId" : "condition-qicore-qicore-condition",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "44",
                  "expression" : {
                     "localId" : "43",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "E",
                        "value" : {
                           "localId" : "41",
                           "name" : "E",
                           "type" : "AliasRef"
                        }
                     }, {
                        "name" : "C",
                        "value" : {
                           "localId" : "42",
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

### QueryRelationship
library TestSnippet version '1'
using QUICK
context Patient
define withQuery:  [Encounter] E
with [Condition] C such that C.id = 'http://cqframework.org/3/2'

define withQuery2:  [Encounter] E
with [Condition] C such that C.id = 'http://cqframework.org/3'

define withOutQuery:  [Encounter] E
without [Condition] C such that C.id = 'http://cqframework.org/3/'

define withOutQuery2:  [Encounter] E
without [Condition] C such that C.id = 'http://cqframework.org/3/2'
###

module.exports['QueryRelationship'] = {
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
            "localId" : "1",
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
            "localId" : "12",
            "name" : "withQuery",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "12",
                  "s" : [ {
                     "value" : [ "define ","withQuery",":  " ]
                  }, {
                     "r" : "11",
                     "s" : [ {
                        "s" : [ {
                           "r" : "3",
                           "s" : [ {
                              "r" : "2",
                              "s" : [ {
                                 "r" : "2",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ "\n" ]
                     }, {
                        "r" : "10",
                        "s" : [ {
                           "value" : [ "with " ]
                        }, {
                           "r" : "5",
                           "s" : [ {
                              "r" : "4",
                              "s" : [ {
                                 "r" : "4",
                                 "s" : [ {
                                    "value" : [ "[","Condition","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","C" ]
                           } ]
                        }, {
                           "value" : [ " such that " ]
                        }, {
                           "r" : "9",
                           "s" : [ {
                              "r" : "7",
                              "s" : [ {
                                 "r" : "6",
                                 "s" : [ {
                                    "value" : [ "C" ]
                                 } ]
                              }, {
                                 "value" : [ "." ]
                              }, {
                                 "r" : "7",
                                 "s" : [ {
                                    "value" : [ "id" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","="," " ]
                           }, {
                              "r" : "8",
                              "s" : [ {
                                 "value" : [ "'http://cqframework.org/3/2'" ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "11",
               "type" : "Query",
               "source" : [ {
                  "localId" : "3",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "2",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ {
                  "localId" : "10",
                  "alias" : "C",
                  "type" : "With",
                  "expression" : {
                     "localId" : "4",
                     "dataType" : "{http://hl7.org/fhir}Condition",
                     "templateId" : "condition-qicore-qicore-condition",
                     "type" : "Retrieve"
                  },
                  "suchThat" : {
                     "localId" : "9",
                     "type" : "Equal",
                     "operand" : [ {
                        "localId" : "7",
                        "path" : "id",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "localId" : "8",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "http://cqframework.org/3/2",
                        "type" : "Literal"
                     } ]
                  }
               } ]
            }
         }, {
            "localId" : "23",
            "name" : "withQuery2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "23",
                  "s" : [ {
                     "value" : [ "define ","withQuery2",":  " ]
                  }, {
                     "r" : "22",
                     "s" : [ {
                        "s" : [ {
                           "r" : "14",
                           "s" : [ {
                              "r" : "13",
                              "s" : [ {
                                 "r" : "13",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ "\n" ]
                     }, {
                        "r" : "21",
                        "s" : [ {
                           "value" : [ "with " ]
                        }, {
                           "r" : "16",
                           "s" : [ {
                              "r" : "15",
                              "s" : [ {
                                 "r" : "15",
                                 "s" : [ {
                                    "value" : [ "[","Condition","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","C" ]
                           } ]
                        }, {
                           "value" : [ " such that " ]
                        }, {
                           "r" : "20",
                           "s" : [ {
                              "r" : "18",
                              "s" : [ {
                                 "r" : "17",
                                 "s" : [ {
                                    "value" : [ "C" ]
                                 } ]
                              }, {
                                 "value" : [ "." ]
                              }, {
                                 "r" : "18",
                                 "s" : [ {
                                    "value" : [ "id" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","="," " ]
                           }, {
                              "r" : "19",
                              "s" : [ {
                                 "value" : [ "'http://cqframework.org/3'" ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "22",
               "type" : "Query",
               "source" : [ {
                  "localId" : "14",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "13",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ {
                  "localId" : "21",
                  "alias" : "C",
                  "type" : "With",
                  "expression" : {
                     "localId" : "15",
                     "dataType" : "{http://hl7.org/fhir}Condition",
                     "templateId" : "condition-qicore-qicore-condition",
                     "type" : "Retrieve"
                  },
                  "suchThat" : {
                     "localId" : "20",
                     "type" : "Equal",
                     "operand" : [ {
                        "localId" : "18",
                        "path" : "id",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "localId" : "19",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "http://cqframework.org/3",
                        "type" : "Literal"
                     } ]
                  }
               } ]
            }
         }, {
            "localId" : "34",
            "name" : "withOutQuery",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "34",
                  "s" : [ {
                     "value" : [ "define ","withOutQuery",":  " ]
                  }, {
                     "r" : "33",
                     "s" : [ {
                        "s" : [ {
                           "r" : "25",
                           "s" : [ {
                              "r" : "24",
                              "s" : [ {
                                 "r" : "24",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ "\n" ]
                     }, {
                        "r" : "32",
                        "s" : [ {
                           "value" : [ "without " ]
                        }, {
                           "r" : "27",
                           "s" : [ {
                              "r" : "26",
                              "s" : [ {
                                 "r" : "26",
                                 "s" : [ {
                                    "value" : [ "[","Condition","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","C" ]
                           } ]
                        }, {
                           "value" : [ " such that " ]
                        }, {
                           "r" : "31",
                           "s" : [ {
                              "r" : "29",
                              "s" : [ {
                                 "r" : "28",
                                 "s" : [ {
                                    "value" : [ "C" ]
                                 } ]
                              }, {
                                 "value" : [ "." ]
                              }, {
                                 "r" : "29",
                                 "s" : [ {
                                    "value" : [ "id" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","="," " ]
                           }, {
                              "r" : "30",
                              "s" : [ {
                                 "value" : [ "'http://cqframework.org/3/'" ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "33",
               "type" : "Query",
               "source" : [ {
                  "localId" : "25",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "24",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ {
                  "localId" : "32",
                  "alias" : "C",
                  "type" : "Without",
                  "expression" : {
                     "localId" : "26",
                     "dataType" : "{http://hl7.org/fhir}Condition",
                     "templateId" : "condition-qicore-qicore-condition",
                     "type" : "Retrieve"
                  },
                  "suchThat" : {
                     "localId" : "31",
                     "type" : "Equal",
                     "operand" : [ {
                        "localId" : "29",
                        "path" : "id",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "localId" : "30",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "http://cqframework.org/3/",
                        "type" : "Literal"
                     } ]
                  }
               } ]
            }
         }, {
            "localId" : "45",
            "name" : "withOutQuery2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "45",
                  "s" : [ {
                     "value" : [ "define ","withOutQuery2",":  " ]
                  }, {
                     "r" : "44",
                     "s" : [ {
                        "s" : [ {
                           "r" : "36",
                           "s" : [ {
                              "r" : "35",
                              "s" : [ {
                                 "r" : "35",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ "\n" ]
                     }, {
                        "r" : "43",
                        "s" : [ {
                           "value" : [ "without " ]
                        }, {
                           "r" : "38",
                           "s" : [ {
                              "r" : "37",
                              "s" : [ {
                                 "r" : "37",
                                 "s" : [ {
                                    "value" : [ "[","Condition","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","C" ]
                           } ]
                        }, {
                           "value" : [ " such that " ]
                        }, {
                           "r" : "42",
                           "s" : [ {
                              "r" : "40",
                              "s" : [ {
                                 "r" : "39",
                                 "s" : [ {
                                    "value" : [ "C" ]
                                 } ]
                              }, {
                                 "value" : [ "." ]
                              }, {
                                 "r" : "40",
                                 "s" : [ {
                                    "value" : [ "id" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","="," " ]
                           }, {
                              "r" : "41",
                              "s" : [ {
                                 "value" : [ "'http://cqframework.org/3/2'" ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "44",
               "type" : "Query",
               "source" : [ {
                  "localId" : "36",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "35",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ {
                  "localId" : "43",
                  "alias" : "C",
                  "type" : "Without",
                  "expression" : {
                     "localId" : "37",
                     "dataType" : "{http://hl7.org/fhir}Condition",
                     "templateId" : "condition-qicore-qicore-condition",
                     "type" : "Retrieve"
                  },
                  "suchThat" : {
                     "localId" : "42",
                     "type" : "Equal",
                     "operand" : [ {
                        "localId" : "40",
                        "path" : "id",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "localId" : "41",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
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

### QueryLet
library TestSnippet version '1'
using QUICK
context Patient
define query:  [Encounter] E
let a: E
return {E: E, a:a}
###

module.exports['QueryLet'] = {
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
            "localId" : "1",
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
            "localId" : "11",
            "name" : "query",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","query",":  " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "s" : [ {
                           "r" : "3",
                           "s" : [ {
                              "r" : "2",
                              "s" : [ {
                                 "r" : "2",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ "\n" ]
                     }, {
                        "s" : [ {
                           "value" : [ "let " ]
                        }, {
                           "r" : "5",
                           "s" : [ {
                              "value" : [ "a",": " ]
                           }, {
                              "r" : "4",
                              "s" : [ {
                                 "value" : [ "E" ]
                              } ]
                           } ]
                        } ]
                     }, {
                        "value" : [ "\n" ]
                     }, {
                        "r" : "9",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "8",
                           "s" : [ {
                              "value" : [ "{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "E",": " ]
                              }, {
                                 "r" : "6",
                                 "s" : [ {
                                    "value" : [ "E" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":" ]
                              }, {
                                 "r" : "7",
                                 "s" : [ {
                                    "value" : [ "a" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "type" : "Query",
               "source" : [ {
                  "localId" : "3",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "2",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "let" : [ {
                  "localId" : "5",
                  "identifier" : "a",
                  "expression" : {
                     "localId" : "4",
                     "name" : "E",
                     "type" : "AliasRef"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "9",
                  "expression" : {
                     "localId" : "8",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "E",
                        "value" : {
                           "localId" : "6",
                           "name" : "E",
                           "type" : "AliasRef"
                        }
                     }, {
                        "name" : "a",
                        "value" : {
                           "localId" : "7",
                           "name" : "a",
                           "type" : "QueryLetRef"
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
define query:  [Encounter] E return {id: E.id, thing: E.status}
###

module.exports['Tuple'] = {
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
            "localId" : "1",
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
            "localId" : "11",
            "name" : "query",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","query",":  " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "s" : [ {
                           "r" : "3",
                           "s" : [ {
                              "r" : "2",
                              "s" : [ {
                                 "r" : "2",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "9",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "8",
                           "s" : [ {
                              "value" : [ "{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "id",": " ]
                              }, {
                                 "r" : "5",
                                 "s" : [ {
                                    "r" : "4",
                                    "s" : [ {
                                       "value" : [ "E" ]
                                    } ]
                                 }, {
                                    "value" : [ "." ]
                                 }, {
                                    "r" : "5",
                                    "s" : [ {
                                       "value" : [ "id" ]
                                    } ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "thing",": " ]
                              }, {
                                 "r" : "7",
                                 "s" : [ {
                                    "r" : "6",
                                    "s" : [ {
                                       "value" : [ "E" ]
                                    } ]
                                 }, {
                                    "value" : [ "." ]
                                 }, {
                                    "r" : "7",
                                    "s" : [ {
                                       "value" : [ "status" ]
                                    } ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "type" : "Query",
               "source" : [ {
                  "localId" : "3",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "2",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "9",
                  "expression" : {
                     "localId" : "8",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "id",
                        "value" : {
                           "localId" : "5",
                           "path" : "id",
                           "scope" : "E",
                           "type" : "Property"
                        }
                     }, {
                        "name" : "thing",
                        "value" : {
                           "localId" : "7",
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

### QueryFilterNulls
library TestSnippet version '1'
using QUICK
context Patient
define query:  (List{null, 'One', null, 'Two', null}) I where I is not null
###

module.exports['QueryFilterNulls'] = {
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
            "localId" : "1",
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
            "localId" : "12",
            "name" : "query",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "12",
                  "s" : [ {
                     "value" : [ "define ","query",":  " ]
                  }, {
                     "r" : "11",
                     "s" : [ {
                        "s" : [ {
                           "r" : "8",
                           "s" : [ {
                              "r" : "7",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "7",
                                 "s" : [ {
                                    "value" : [ "List{","null",", " ]
                                 }, {
                                    "r" : "3",
                                    "s" : [ {
                                       "value" : [ "'One'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", ","null",", " ]
                                 }, {
                                    "r" : "5",
                                    "s" : [ {
                                       "value" : [ "'Two'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", ","null","}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","I" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "10",
                        "s" : [ {
                           "value" : [ "where " ]
                        }, {
                           "r" : "10",
                           "s" : [ {
                              "r" : "9",
                              "s" : [ {
                                 "value" : [ "I" ]
                              } ]
                           }, {
                              "value" : [ " is not null" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "11",
               "type" : "Query",
               "source" : [ {
                  "localId" : "8",
                  "alias" : "I",
                  "expression" : {
                     "localId" : "7",
                     "type" : "List",
                     "element" : [ {
                        "asType" : "{urn:hl7-org:elm-types:r1}String",
                        "type" : "As",
                        "operand" : {
                           "localId" : "2",
                           "type" : "Null"
                        },
                        "asTypeSpecifier" : {
                           "name" : "{urn:hl7-org:elm-types:r1}String",
                           "type" : "NamedTypeSpecifier"
                        }
                     }, {
                        "localId" : "3",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "One",
                        "type" : "Literal"
                     }, {
                        "asType" : "{urn:hl7-org:elm-types:r1}String",
                        "type" : "As",
                        "operand" : {
                           "localId" : "4",
                           "type" : "Null"
                        },
                        "asTypeSpecifier" : {
                           "name" : "{urn:hl7-org:elm-types:r1}String",
                           "type" : "NamedTypeSpecifier"
                        }
                     }, {
                        "localId" : "5",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "Two",
                        "type" : "Literal"
                     }, {
                        "asType" : "{urn:hl7-org:elm-types:r1}String",
                        "type" : "As",
                        "operand" : {
                           "localId" : "6",
                           "type" : "Null"
                        },
                        "asTypeSpecifier" : {
                           "name" : "{urn:hl7-org:elm-types:r1}String",
                           "type" : "NamedTypeSpecifier"
                        }
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "where" : {
                  "localId" : "10",
                  "type" : "Not",
                  "operand" : {
                     "type" : "IsNull",
                     "operand" : {
                        "localId" : "9",
                        "name" : "I",
                        "type" : "AliasRef"
                     }
                  }
               }
            }
         } ]
      }
   }
}

### Sorting
library TestSnippet version '1'
using QUICK
context Patient
define TupleAsc: [Encounter] E sort by id
define TupleReturnAsc: [Encounter] E return E sort by id
define TupleReturnTupleAsc: [Encounter] E return {E : E} sort by E.id
define TupleDesc: [Encounter] E sort by id desc
define TupleReturnDesc: [Encounter] E return E sort by id desc
define TupleReturnTupleDesc:  [Encounter] E return {E : E} sort by E.id desc
define numberAsc: ({8, 6, 7, 5, 3, 0, 9}) N sort asc
define numberReturnAsc: ({8, 6, 7, 5, 3, 0, 9}) N return N sort asc
define numberDesc: ({8, 6, 7, 5, 3, 0, 9}) N sort desc
define numberReturnDesc: ({8, 6, 7, 5, 3, 0, 9}) N return N sort desc
define stringAsc: ({'jenny', 'dont', 'change', 'your', 'number'}) S sort asc
define stringReturnAsc: ({'jenny', 'dont', 'change', 'your', 'number'}) S return S sort asc
define stringDesc: ({'jenny', 'dont', 'change', 'your', 'number'}) S sort desc
define stringReturnDesc: ({'jenny', 'dont', 'change', 'your', 'number'}) S return S sort desc
define five: 5
define sortByExpression: ({8, 6, 7, 5, 3, 0, 9}) N return Tuple{N: N} sort by (five + N)
###

module.exports['Sorting'] = {
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
            "localId" : "1",
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
            "localId" : "8",
            "name" : "TupleAsc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "8",
                  "s" : [ {
                     "value" : [ "define ","TupleAsc",": " ]
                  }, {
                     "r" : "7",
                     "s" : [ {
                        "s" : [ {
                           "r" : "3",
                           "s" : [ {
                              "r" : "2",
                              "s" : [ {
                                 "r" : "2",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "6",
                        "s" : [ {
                           "value" : [ "sort by " ]
                        }, {
                           "r" : "5",
                           "s" : [ {
                              "r" : "4",
                              "s" : [ {
                                 "value" : [ "id" ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "7",
               "type" : "Query",
               "source" : [ {
                  "localId" : "3",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "2",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "sort" : {
                  "localId" : "6",
                  "by" : [ {
                     "localId" : "5",
                     "direction" : "asc",
                     "path" : "id",
                     "type" : "ByColumn"
                  } ]
               }
            }
         }, {
            "localId" : "17",
            "name" : "TupleReturnAsc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "17",
                  "s" : [ {
                     "value" : [ "define ","TupleReturnAsc",": " ]
                  }, {
                     "r" : "16",
                     "s" : [ {
                        "s" : [ {
                           "r" : "10",
                           "s" : [ {
                              "r" : "9",
                              "s" : [ {
                                 "r" : "9",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "12",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "11",
                           "s" : [ {
                              "value" : [ "E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "sort by " ]
                        }, {
                           "r" : "14",
                           "s" : [ {
                              "r" : "13",
                              "s" : [ {
                                 "value" : [ "id" ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "16",
               "type" : "Query",
               "source" : [ {
                  "localId" : "10",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "9",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "12",
                  "expression" : {
                     "localId" : "11",
                     "name" : "E",
                     "type" : "AliasRef"
                  }
               },
               "sort" : {
                  "localId" : "15",
                  "by" : [ {
                     "localId" : "14",
                     "direction" : "asc",
                     "path" : "id",
                     "type" : "ByColumn"
                  } ]
               }
            }
         }, {
            "localId" : "28",
            "name" : "TupleReturnTupleAsc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "28",
                  "s" : [ {
                     "value" : [ "define ","TupleReturnTupleAsc",": " ]
                  }, {
                     "r" : "27",
                     "s" : [ {
                        "s" : [ {
                           "r" : "19",
                           "s" : [ {
                              "r" : "18",
                              "s" : [ {
                                 "r" : "18",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "22",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "21",
                           "s" : [ {
                              "value" : [ "{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "E"," : " ]
                              }, {
                                 "r" : "20",
                                 "s" : [ {
                                    "value" : [ "E" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "26",
                        "s" : [ {
                           "value" : [ "sort by " ]
                        }, {
                           "r" : "25",
                           "s" : [ {
                              "r" : "24",
                              "s" : [ {
                                 "r" : "23",
                                 "s" : [ {
                                    "value" : [ "E" ]
                                 } ]
                              }, {
                                 "value" : [ "." ]
                              }, {
                                 "r" : "24",
                                 "s" : [ {
                                    "value" : [ "id" ]
                                 } ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "27",
               "type" : "Query",
               "source" : [ {
                  "localId" : "19",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "18",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "22",
                  "expression" : {
                     "localId" : "21",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "E",
                        "value" : {
                           "localId" : "20",
                           "name" : "E",
                           "type" : "AliasRef"
                        }
                     } ]
                  }
               },
               "sort" : {
                  "localId" : "26",
                  "by" : [ {
                     "localId" : "25",
                     "direction" : "asc",
                     "type" : "ByExpression",
                     "expression" : {
                        "localId" : "24",
                        "path" : "id",
                        "type" : "Property",
                        "source" : {
                           "localId" : "23",
                           "name" : "E",
                           "type" : "IdentifierRef"
                        }
                     }
                  } ]
               }
            }
         }, {
            "localId" : "35",
            "name" : "TupleDesc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "35",
                  "s" : [ {
                     "value" : [ "define ","TupleDesc",": " ]
                  }, {
                     "r" : "34",
                     "s" : [ {
                        "s" : [ {
                           "r" : "30",
                           "s" : [ {
                              "r" : "29",
                              "s" : [ {
                                 "r" : "29",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "33",
                        "s" : [ {
                           "value" : [ "sort by " ]
                        }, {
                           "r" : "32",
                           "s" : [ {
                              "r" : "31",
                              "s" : [ {
                                 "value" : [ "id" ]
                              } ]
                           }, {
                              "value" : [ " desc" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "34",
               "type" : "Query",
               "source" : [ {
                  "localId" : "30",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "29",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "sort" : {
                  "localId" : "33",
                  "by" : [ {
                     "localId" : "32",
                     "direction" : "desc",
                     "path" : "id",
                     "type" : "ByColumn"
                  } ]
               }
            }
         }, {
            "localId" : "44",
            "name" : "TupleReturnDesc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "44",
                  "s" : [ {
                     "value" : [ "define ","TupleReturnDesc",": " ]
                  }, {
                     "r" : "43",
                     "s" : [ {
                        "s" : [ {
                           "r" : "37",
                           "s" : [ {
                              "r" : "36",
                              "s" : [ {
                                 "r" : "36",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "39",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "38",
                           "s" : [ {
                              "value" : [ "E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "42",
                        "s" : [ {
                           "value" : [ "sort by " ]
                        }, {
                           "r" : "41",
                           "s" : [ {
                              "r" : "40",
                              "s" : [ {
                                 "value" : [ "id" ]
                              } ]
                           }, {
                              "value" : [ " desc" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "43",
               "type" : "Query",
               "source" : [ {
                  "localId" : "37",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "36",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "39",
                  "expression" : {
                     "localId" : "38",
                     "name" : "E",
                     "type" : "AliasRef"
                  }
               },
               "sort" : {
                  "localId" : "42",
                  "by" : [ {
                     "localId" : "41",
                     "direction" : "desc",
                     "path" : "id",
                     "type" : "ByColumn"
                  } ]
               }
            }
         }, {
            "localId" : "55",
            "name" : "TupleReturnTupleDesc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "55",
                  "s" : [ {
                     "value" : [ "define ","TupleReturnTupleDesc",":  " ]
                  }, {
                     "r" : "54",
                     "s" : [ {
                        "s" : [ {
                           "r" : "46",
                           "s" : [ {
                              "r" : "45",
                              "s" : [ {
                                 "r" : "45",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "49",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "48",
                           "s" : [ {
                              "value" : [ "{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "E"," : " ]
                              }, {
                                 "r" : "47",
                                 "s" : [ {
                                    "value" : [ "E" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "53",
                        "s" : [ {
                           "value" : [ "sort by " ]
                        }, {
                           "r" : "52",
                           "s" : [ {
                              "r" : "51",
                              "s" : [ {
                                 "r" : "50",
                                 "s" : [ {
                                    "value" : [ "E" ]
                                 } ]
                              }, {
                                 "value" : [ "." ]
                              }, {
                                 "r" : "51",
                                 "s" : [ {
                                    "value" : [ "id" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " desc" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "54",
               "type" : "Query",
               "source" : [ {
                  "localId" : "46",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "45",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "49",
                  "expression" : {
                     "localId" : "48",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "E",
                        "value" : {
                           "localId" : "47",
                           "name" : "E",
                           "type" : "AliasRef"
                        }
                     } ]
                  }
               },
               "sort" : {
                  "localId" : "53",
                  "by" : [ {
                     "localId" : "52",
                     "direction" : "desc",
                     "type" : "ByExpression",
                     "expression" : {
                        "localId" : "51",
                        "path" : "id",
                        "type" : "Property",
                        "source" : {
                           "localId" : "50",
                           "name" : "E",
                           "type" : "IdentifierRef"
                        }
                     }
                  } ]
               }
            }
         }, {
            "localId" : "67",
            "name" : "numberAsc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "67",
                  "s" : [ {
                     "value" : [ "define ","numberAsc",": " ]
                  }, {
                     "r" : "66",
                     "s" : [ {
                        "s" : [ {
                           "r" : "64",
                           "s" : [ {
                              "r" : "63",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "63",
                                 "s" : [ {
                                    "value" : [ "{","8",", ","6",", ","7",", ","5",", ","3",", ","0",", ","9","}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","N" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " ","sort asc" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "66",
               "type" : "Query",
               "source" : [ {
                  "localId" : "64",
                  "alias" : "N",
                  "expression" : {
                     "localId" : "63",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "56",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "localId" : "57",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     }, {
                        "localId" : "58",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "localId" : "59",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "localId" : "60",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "61",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "localId" : "62",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "9",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "sort" : {
                  "localId" : "65",
                  "by" : [ {
                     "direction" : "asc",
                     "type" : "ByDirection"
                  } ]
               }
            }
         }, {
            "localId" : "81",
            "name" : "numberReturnAsc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "81",
                  "s" : [ {
                     "value" : [ "define ","numberReturnAsc",": " ]
                  }, {
                     "r" : "80",
                     "s" : [ {
                        "s" : [ {
                           "r" : "76",
                           "s" : [ {
                              "r" : "75",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "75",
                                 "s" : [ {
                                    "value" : [ "{","8",", ","6",", ","7",", ","5",", ","3",", ","0",", ","9","}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","N" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "78",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "77",
                           "s" : [ {
                              "value" : [ "N" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " ","sort asc" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "80",
               "type" : "Query",
               "source" : [ {
                  "localId" : "76",
                  "alias" : "N",
                  "expression" : {
                     "localId" : "75",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "68",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "localId" : "69",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     }, {
                        "localId" : "70",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "localId" : "71",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "localId" : "72",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "73",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "localId" : "74",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "9",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "78",
                  "expression" : {
                     "localId" : "77",
                     "name" : "N",
                     "type" : "AliasRef"
                  }
               },
               "sort" : {
                  "localId" : "79",
                  "by" : [ {
                     "direction" : "asc",
                     "type" : "ByDirection"
                  } ]
               }
            }
         }, {
            "localId" : "93",
            "name" : "numberDesc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "93",
                  "s" : [ {
                     "value" : [ "define ","numberDesc",": " ]
                  }, {
                     "r" : "92",
                     "s" : [ {
                        "s" : [ {
                           "r" : "90",
                           "s" : [ {
                              "r" : "89",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "89",
                                 "s" : [ {
                                    "value" : [ "{","8",", ","6",", ","7",", ","5",", ","3",", ","0",", ","9","}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","N" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " ","sort desc" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "92",
               "type" : "Query",
               "source" : [ {
                  "localId" : "90",
                  "alias" : "N",
                  "expression" : {
                     "localId" : "89",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "82",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "localId" : "83",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     }, {
                        "localId" : "84",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "localId" : "85",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "localId" : "86",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "87",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "localId" : "88",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "9",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "sort" : {
                  "localId" : "91",
                  "by" : [ {
                     "direction" : "desc",
                     "type" : "ByDirection"
                  } ]
               }
            }
         }, {
            "localId" : "107",
            "name" : "numberReturnDesc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "107",
                  "s" : [ {
                     "value" : [ "define ","numberReturnDesc",": " ]
                  }, {
                     "r" : "106",
                     "s" : [ {
                        "s" : [ {
                           "r" : "102",
                           "s" : [ {
                              "r" : "101",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "101",
                                 "s" : [ {
                                    "value" : [ "{","8",", ","6",", ","7",", ","5",", ","3",", ","0",", ","9","}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","N" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "104",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "103",
                           "s" : [ {
                              "value" : [ "N" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " ","sort desc" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "106",
               "type" : "Query",
               "source" : [ {
                  "localId" : "102",
                  "alias" : "N",
                  "expression" : {
                     "localId" : "101",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "94",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "localId" : "95",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     }, {
                        "localId" : "96",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "localId" : "97",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "localId" : "98",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "99",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "localId" : "100",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "9",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "104",
                  "expression" : {
                     "localId" : "103",
                     "name" : "N",
                     "type" : "AliasRef"
                  }
               },
               "sort" : {
                  "localId" : "105",
                  "by" : [ {
                     "direction" : "desc",
                     "type" : "ByDirection"
                  } ]
               }
            }
         }, {
            "localId" : "117",
            "name" : "stringAsc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "117",
                  "s" : [ {
                     "value" : [ "define ","stringAsc",": " ]
                  }, {
                     "r" : "116",
                     "s" : [ {
                        "s" : [ {
                           "r" : "114",
                           "s" : [ {
                              "r" : "113",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "113",
                                 "s" : [ {
                                    "value" : [ "{" ]
                                 }, {
                                    "r" : "108",
                                    "s" : [ {
                                       "value" : [ "'jenny'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "109",
                                    "s" : [ {
                                       "value" : [ "'dont'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "110",
                                    "s" : [ {
                                       "value" : [ "'change'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "111",
                                    "s" : [ {
                                       "value" : [ "'your'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "112",
                                    "s" : [ {
                                       "value" : [ "'number'" ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","S" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " ","sort asc" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "116",
               "type" : "Query",
               "source" : [ {
                  "localId" : "114",
                  "alias" : "S",
                  "expression" : {
                     "localId" : "113",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "108",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "jenny",
                        "type" : "Literal"
                     }, {
                        "localId" : "109",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "dont",
                        "type" : "Literal"
                     }, {
                        "localId" : "110",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "change",
                        "type" : "Literal"
                     }, {
                        "localId" : "111",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "your",
                        "type" : "Literal"
                     }, {
                        "localId" : "112",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "number",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "sort" : {
                  "localId" : "115",
                  "by" : [ {
                     "direction" : "asc",
                     "type" : "ByDirection"
                  } ]
               }
            }
         }, {
            "localId" : "129",
            "name" : "stringReturnAsc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "129",
                  "s" : [ {
                     "value" : [ "define ","stringReturnAsc",": " ]
                  }, {
                     "r" : "128",
                     "s" : [ {
                        "s" : [ {
                           "r" : "124",
                           "s" : [ {
                              "r" : "123",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "123",
                                 "s" : [ {
                                    "value" : [ "{" ]
                                 }, {
                                    "r" : "118",
                                    "s" : [ {
                                       "value" : [ "'jenny'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "119",
                                    "s" : [ {
                                       "value" : [ "'dont'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "120",
                                    "s" : [ {
                                       "value" : [ "'change'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "121",
                                    "s" : [ {
                                       "value" : [ "'your'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "122",
                                    "s" : [ {
                                       "value" : [ "'number'" ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","S" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "126",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "125",
                           "s" : [ {
                              "value" : [ "S" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " ","sort asc" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "128",
               "type" : "Query",
               "source" : [ {
                  "localId" : "124",
                  "alias" : "S",
                  "expression" : {
                     "localId" : "123",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "118",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "jenny",
                        "type" : "Literal"
                     }, {
                        "localId" : "119",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "dont",
                        "type" : "Literal"
                     }, {
                        "localId" : "120",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "change",
                        "type" : "Literal"
                     }, {
                        "localId" : "121",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "your",
                        "type" : "Literal"
                     }, {
                        "localId" : "122",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "number",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "126",
                  "expression" : {
                     "localId" : "125",
                     "name" : "S",
                     "type" : "AliasRef"
                  }
               },
               "sort" : {
                  "localId" : "127",
                  "by" : [ {
                     "direction" : "asc",
                     "type" : "ByDirection"
                  } ]
               }
            }
         }, {
            "localId" : "139",
            "name" : "stringDesc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "139",
                  "s" : [ {
                     "value" : [ "define ","stringDesc",": " ]
                  }, {
                     "r" : "138",
                     "s" : [ {
                        "s" : [ {
                           "r" : "136",
                           "s" : [ {
                              "r" : "135",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "135",
                                 "s" : [ {
                                    "value" : [ "{" ]
                                 }, {
                                    "r" : "130",
                                    "s" : [ {
                                       "value" : [ "'jenny'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "131",
                                    "s" : [ {
                                       "value" : [ "'dont'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "132",
                                    "s" : [ {
                                       "value" : [ "'change'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "133",
                                    "s" : [ {
                                       "value" : [ "'your'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "134",
                                    "s" : [ {
                                       "value" : [ "'number'" ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","S" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " ","sort desc" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "138",
               "type" : "Query",
               "source" : [ {
                  "localId" : "136",
                  "alias" : "S",
                  "expression" : {
                     "localId" : "135",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "130",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "jenny",
                        "type" : "Literal"
                     }, {
                        "localId" : "131",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "dont",
                        "type" : "Literal"
                     }, {
                        "localId" : "132",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "change",
                        "type" : "Literal"
                     }, {
                        "localId" : "133",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "your",
                        "type" : "Literal"
                     }, {
                        "localId" : "134",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "number",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "sort" : {
                  "localId" : "137",
                  "by" : [ {
                     "direction" : "desc",
                     "type" : "ByDirection"
                  } ]
               }
            }
         }, {
            "localId" : "151",
            "name" : "stringReturnDesc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "151",
                  "s" : [ {
                     "value" : [ "define ","stringReturnDesc",": " ]
                  }, {
                     "r" : "150",
                     "s" : [ {
                        "s" : [ {
                           "r" : "146",
                           "s" : [ {
                              "r" : "145",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "145",
                                 "s" : [ {
                                    "value" : [ "{" ]
                                 }, {
                                    "r" : "140",
                                    "s" : [ {
                                       "value" : [ "'jenny'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "141",
                                    "s" : [ {
                                       "value" : [ "'dont'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "142",
                                    "s" : [ {
                                       "value" : [ "'change'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "143",
                                    "s" : [ {
                                       "value" : [ "'your'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "144",
                                    "s" : [ {
                                       "value" : [ "'number'" ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","S" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "148",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "147",
                           "s" : [ {
                              "value" : [ "S" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " ","sort desc" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "150",
               "type" : "Query",
               "source" : [ {
                  "localId" : "146",
                  "alias" : "S",
                  "expression" : {
                     "localId" : "145",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "140",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "jenny",
                        "type" : "Literal"
                     }, {
                        "localId" : "141",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "dont",
                        "type" : "Literal"
                     }, {
                        "localId" : "142",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "change",
                        "type" : "Literal"
                     }, {
                        "localId" : "143",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "your",
                        "type" : "Literal"
                     }, {
                        "localId" : "144",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "number",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "148",
                  "expression" : {
                     "localId" : "147",
                     "name" : "S",
                     "type" : "AliasRef"
                  }
               },
               "sort" : {
                  "localId" : "149",
                  "by" : [ {
                     "direction" : "desc",
                     "type" : "ByDirection"
                  } ]
               }
            }
         }, {
            "localId" : "153",
            "name" : "five",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "153",
                  "s" : [ {
                     "value" : [ "define ","five",": ","5" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "152",
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "5",
               "type" : "Literal"
            }
         }, {
            "localId" : "172",
            "name" : "sortByExpression",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "172",
                  "s" : [ {
                     "value" : [ "define ","sortByExpression",": " ]
                  }, {
                     "r" : "171",
                     "s" : [ {
                        "s" : [ {
                           "r" : "162",
                           "s" : [ {
                              "r" : "161",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "161",
                                 "s" : [ {
                                    "value" : [ "{","8",", ","6",", ","7",", ","5",", ","3",", ","0",", ","9","}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","N" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "165",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "164",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "N",": " ]
                              }, {
                                 "r" : "163",
                                 "s" : [ {
                                    "value" : [ "N" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "170",
                        "s" : [ {
                           "value" : [ "sort by " ]
                        }, {
                           "r" : "169",
                           "s" : [ {
                              "r" : "168",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "168",
                                 "s" : [ {
                                    "r" : "166",
                                    "s" : [ {
                                       "value" : [ "five" ]
                                    } ]
                                 }, {
                                    "value" : [ " + " ]
                                 }, {
                                    "r" : "167",
                                    "s" : [ {
                                       "value" : [ "N" ]
                                    } ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "171",
               "type" : "Query",
               "source" : [ {
                  "localId" : "162",
                  "alias" : "N",
                  "expression" : {
                     "localId" : "161",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "154",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "localId" : "155",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     }, {
                        "localId" : "156",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "localId" : "157",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "localId" : "158",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "159",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "localId" : "160",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "9",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "165",
                  "expression" : {
                     "localId" : "164",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "N",
                        "value" : {
                           "localId" : "163",
                           "name" : "N",
                           "type" : "AliasRef"
                        }
                     } ]
                  }
               },
               "sort" : {
                  "localId" : "170",
                  "by" : [ {
                     "localId" : "169",
                     "direction" : "asc",
                     "type" : "ByExpression",
                     "expression" : {
                        "localId" : "168",
                        "type" : "Add",
                        "operand" : [ {
                           "localId" : "166",
                           "name" : "five",
                           "type" : "ExpressionRef"
                        }, {
                           "localId" : "167",
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

### Distinct
library TestSnippet version '1'
using QUICK
context Patient
define defaultNumbers: ({1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 3, 3, 3, 2, 2, 1}) N return N
define defaultStrings: ({'foo', 'bar', 'baz', 'bar'}) S return S
define defaultTuples: ({Tuple{a: 1, b:2}, Tuple{a: 2, b: 3}, Tuple{a: 1, b: 2}}) T return T
define distinctNumbers: ({1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 3, 3, 3, 2, 2, 1}) N return distinct N
define distinctStrings: ({'foo', 'bar', 'baz', 'bar'}) S return distinct S
define distinctTuples: ({Tuple{a: 1, b:2}, Tuple{a: 2, b: 3}, Tuple{a: 1, b: 2}}) T return distinct T
define allNumbers: ({1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 3, 3, 3, 2, 2, 1}) N return all N
define allStrings: ({'foo', 'bar', 'baz', 'bar'}) S return all S
define allTuples: ({Tuple{a: 1, b:2}, Tuple{a: 2, b: 3}, Tuple{a: 1, b: 2}}) T return all T
###

module.exports['Distinct'] = {
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
            "localId" : "1",
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
            "localId" : "23",
            "name" : "defaultNumbers",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "23",
                  "s" : [ {
                     "value" : [ "define ","defaultNumbers",": " ]
                  }, {
                     "r" : "22",
                     "s" : [ {
                        "s" : [ {
                           "r" : "19",
                           "s" : [ {
                              "r" : "18",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "18",
                                 "s" : [ {
                                    "value" : [ "{","1",", ","2",", ","2",", ","3",", ","3",", ","3",", ","4",", ","4",", ","4",", ","4",", ","3",", ","3",", ","3",", ","2",", ","2",", ","1","}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","N" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "21",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "20",
                           "s" : [ {
                              "value" : [ "N" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "22",
               "type" : "Query",
               "source" : [ {
                  "localId" : "19",
                  "alias" : "N",
                  "expression" : {
                     "localId" : "18",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "2",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "localId" : "3",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "4",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "5",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "6",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "7",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "8",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "localId" : "9",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "localId" : "10",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "localId" : "11",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "localId" : "12",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "13",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "14",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "15",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "16",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "17",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "21",
                  "expression" : {
                     "localId" : "20",
                     "name" : "N",
                     "type" : "AliasRef"
                  }
               }
            }
         }, {
            "localId" : "33",
            "name" : "defaultStrings",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "33",
                  "s" : [ {
                     "value" : [ "define ","defaultStrings",": " ]
                  }, {
                     "r" : "32",
                     "s" : [ {
                        "s" : [ {
                           "r" : "29",
                           "s" : [ {
                              "r" : "28",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "28",
                                 "s" : [ {
                                    "value" : [ "{" ]
                                 }, {
                                    "r" : "24",
                                    "s" : [ {
                                       "value" : [ "'foo'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "25",
                                    "s" : [ {
                                       "value" : [ "'bar'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "26",
                                    "s" : [ {
                                       "value" : [ "'baz'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "27",
                                    "s" : [ {
                                       "value" : [ "'bar'" ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","S" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "31",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "30",
                           "s" : [ {
                              "value" : [ "S" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "32",
               "type" : "Query",
               "source" : [ {
                  "localId" : "29",
                  "alias" : "S",
                  "expression" : {
                     "localId" : "28",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "24",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "foo",
                        "type" : "Literal"
                     }, {
                        "localId" : "25",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "bar",
                        "type" : "Literal"
                     }, {
                        "localId" : "26",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "baz",
                        "type" : "Literal"
                     }, {
                        "localId" : "27",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "bar",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "31",
                  "expression" : {
                     "localId" : "30",
                     "name" : "S",
                     "type" : "AliasRef"
                  }
               }
            }
         }, {
            "localId" : "48",
            "name" : "defaultTuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "48",
                  "s" : [ {
                     "value" : [ "define ","defaultTuples",": " ]
                  }, {
                     "r" : "47",
                     "s" : [ {
                        "s" : [ {
                           "r" : "44",
                           "s" : [ {
                              "r" : "43",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "43",
                                 "s" : [ {
                                    "value" : [ "{" ]
                                 }, {
                                    "r" : "36",
                                    "s" : [ {
                                       "value" : [ "Tuple{" ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "a",": ","1" ]
                                       } ]
                                    }, {
                                       "value" : [ ", " ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "b",":","2" ]
                                       } ]
                                    }, {
                                       "value" : [ "}" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "39",
                                    "s" : [ {
                                       "value" : [ "Tuple{" ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "a",": ","2" ]
                                       } ]
                                    }, {
                                       "value" : [ ", " ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "b",": ","3" ]
                                       } ]
                                    }, {
                                       "value" : [ "}" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "42",
                                    "s" : [ {
                                       "value" : [ "Tuple{" ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "a",": ","1" ]
                                       } ]
                                    }, {
                                       "value" : [ ", " ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "b",": ","2" ]
                                       } ]
                                    }, {
                                       "value" : [ "}" ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","T" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "46",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "45",
                           "s" : [ {
                              "value" : [ "T" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "47",
               "type" : "Query",
               "source" : [ {
                  "localId" : "44",
                  "alias" : "T",
                  "expression" : {
                     "localId" : "43",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "36",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "localId" : "34",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "localId" : "35",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     }, {
                        "localId" : "39",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "localId" : "37",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "localId" : "38",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "3",
                              "type" : "Literal"
                           }
                        } ]
                     }, {
                        "localId" : "42",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "localId" : "40",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "localId" : "41",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "46",
                  "expression" : {
                     "localId" : "45",
                     "name" : "T",
                     "type" : "AliasRef"
                  }
               }
            }
         }, {
            "localId" : "70",
            "name" : "distinctNumbers",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "70",
                  "s" : [ {
                     "value" : [ "define ","distinctNumbers",": " ]
                  }, {
                     "r" : "69",
                     "s" : [ {
                        "s" : [ {
                           "r" : "66",
                           "s" : [ {
                              "r" : "65",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "65",
                                 "s" : [ {
                                    "value" : [ "{","1",", ","2",", ","2",", ","3",", ","3",", ","3",", ","4",", ","4",", ","4",", ","4",", ","3",", ","3",", ","3",", ","2",", ","2",", ","1","}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","N" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "68",
                        "s" : [ {
                           "value" : [ "return distinct " ]
                        }, {
                           "r" : "67",
                           "s" : [ {
                              "value" : [ "N" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "69",
               "type" : "Query",
               "source" : [ {
                  "localId" : "66",
                  "alias" : "N",
                  "expression" : {
                     "localId" : "65",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "49",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "localId" : "50",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "51",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "52",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "53",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "54",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "55",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "localId" : "56",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "localId" : "57",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "localId" : "58",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "localId" : "59",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "60",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "61",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "62",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "63",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "64",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "68",
                  "distinct" : true,
                  "expression" : {
                     "localId" : "67",
                     "name" : "N",
                     "type" : "AliasRef"
                  }
               }
            }
         }, {
            "localId" : "80",
            "name" : "distinctStrings",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "80",
                  "s" : [ {
                     "value" : [ "define ","distinctStrings",": " ]
                  }, {
                     "r" : "79",
                     "s" : [ {
                        "s" : [ {
                           "r" : "76",
                           "s" : [ {
                              "r" : "75",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "75",
                                 "s" : [ {
                                    "value" : [ "{" ]
                                 }, {
                                    "r" : "71",
                                    "s" : [ {
                                       "value" : [ "'foo'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "72",
                                    "s" : [ {
                                       "value" : [ "'bar'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "73",
                                    "s" : [ {
                                       "value" : [ "'baz'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "74",
                                    "s" : [ {
                                       "value" : [ "'bar'" ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","S" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "78",
                        "s" : [ {
                           "value" : [ "return distinct " ]
                        }, {
                           "r" : "77",
                           "s" : [ {
                              "value" : [ "S" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "79",
               "type" : "Query",
               "source" : [ {
                  "localId" : "76",
                  "alias" : "S",
                  "expression" : {
                     "localId" : "75",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "71",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "foo",
                        "type" : "Literal"
                     }, {
                        "localId" : "72",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "bar",
                        "type" : "Literal"
                     }, {
                        "localId" : "73",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "baz",
                        "type" : "Literal"
                     }, {
                        "localId" : "74",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "bar",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "78",
                  "distinct" : true,
                  "expression" : {
                     "localId" : "77",
                     "name" : "S",
                     "type" : "AliasRef"
                  }
               }
            }
         }, {
            "localId" : "95",
            "name" : "distinctTuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "95",
                  "s" : [ {
                     "value" : [ "define ","distinctTuples",": " ]
                  }, {
                     "r" : "94",
                     "s" : [ {
                        "s" : [ {
                           "r" : "91",
                           "s" : [ {
                              "r" : "90",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "90",
                                 "s" : [ {
                                    "value" : [ "{" ]
                                 }, {
                                    "r" : "83",
                                    "s" : [ {
                                       "value" : [ "Tuple{" ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "a",": ","1" ]
                                       } ]
                                    }, {
                                       "value" : [ ", " ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "b",":","2" ]
                                       } ]
                                    }, {
                                       "value" : [ "}" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "86",
                                    "s" : [ {
                                       "value" : [ "Tuple{" ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "a",": ","2" ]
                                       } ]
                                    }, {
                                       "value" : [ ", " ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "b",": ","3" ]
                                       } ]
                                    }, {
                                       "value" : [ "}" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "89",
                                    "s" : [ {
                                       "value" : [ "Tuple{" ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "a",": ","1" ]
                                       } ]
                                    }, {
                                       "value" : [ ", " ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "b",": ","2" ]
                                       } ]
                                    }, {
                                       "value" : [ "}" ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","T" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "93",
                        "s" : [ {
                           "value" : [ "return distinct " ]
                        }, {
                           "r" : "92",
                           "s" : [ {
                              "value" : [ "T" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "94",
               "type" : "Query",
               "source" : [ {
                  "localId" : "91",
                  "alias" : "T",
                  "expression" : {
                     "localId" : "90",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "83",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "localId" : "81",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "localId" : "82",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     }, {
                        "localId" : "86",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "localId" : "84",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "localId" : "85",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "3",
                              "type" : "Literal"
                           }
                        } ]
                     }, {
                        "localId" : "89",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "localId" : "87",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "localId" : "88",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "93",
                  "distinct" : true,
                  "expression" : {
                     "localId" : "92",
                     "name" : "T",
                     "type" : "AliasRef"
                  }
               }
            }
         }, {
            "localId" : "117",
            "name" : "allNumbers",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "117",
                  "s" : [ {
                     "value" : [ "define ","allNumbers",": " ]
                  }, {
                     "r" : "116",
                     "s" : [ {
                        "s" : [ {
                           "r" : "113",
                           "s" : [ {
                              "r" : "112",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "112",
                                 "s" : [ {
                                    "value" : [ "{","1",", ","2",", ","2",", ","3",", ","3",", ","3",", ","4",", ","4",", ","4",", ","4",", ","3",", ","3",", ","3",", ","2",", ","2",", ","1","}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","N" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "115",
                        "s" : [ {
                           "value" : [ "return all " ]
                        }, {
                           "r" : "114",
                           "s" : [ {
                              "value" : [ "N" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "116",
               "type" : "Query",
               "source" : [ {
                  "localId" : "113",
                  "alias" : "N",
                  "expression" : {
                     "localId" : "112",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "96",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "localId" : "97",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "98",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "99",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "100",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "101",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "102",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "localId" : "103",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "localId" : "104",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "localId" : "105",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "localId" : "106",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "107",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "108",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "109",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "110",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "111",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "115",
                  "distinct" : false,
                  "expression" : {
                     "localId" : "114",
                     "name" : "N",
                     "type" : "AliasRef"
                  }
               }
            }
         }, {
            "localId" : "127",
            "name" : "allStrings",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "127",
                  "s" : [ {
                     "value" : [ "define ","allStrings",": " ]
                  }, {
                     "r" : "126",
                     "s" : [ {
                        "s" : [ {
                           "r" : "123",
                           "s" : [ {
                              "r" : "122",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "122",
                                 "s" : [ {
                                    "value" : [ "{" ]
                                 }, {
                                    "r" : "118",
                                    "s" : [ {
                                       "value" : [ "'foo'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "119",
                                    "s" : [ {
                                       "value" : [ "'bar'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "120",
                                    "s" : [ {
                                       "value" : [ "'baz'" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "121",
                                    "s" : [ {
                                       "value" : [ "'bar'" ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","S" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "125",
                        "s" : [ {
                           "value" : [ "return all " ]
                        }, {
                           "r" : "124",
                           "s" : [ {
                              "value" : [ "S" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "126",
               "type" : "Query",
               "source" : [ {
                  "localId" : "123",
                  "alias" : "S",
                  "expression" : {
                     "localId" : "122",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "118",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "foo",
                        "type" : "Literal"
                     }, {
                        "localId" : "119",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "bar",
                        "type" : "Literal"
                     }, {
                        "localId" : "120",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "baz",
                        "type" : "Literal"
                     }, {
                        "localId" : "121",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "bar",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "125",
                  "distinct" : false,
                  "expression" : {
                     "localId" : "124",
                     "name" : "S",
                     "type" : "AliasRef"
                  }
               }
            }
         }, {
            "localId" : "142",
            "name" : "allTuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "142",
                  "s" : [ {
                     "value" : [ "define ","allTuples",": " ]
                  }, {
                     "r" : "141",
                     "s" : [ {
                        "s" : [ {
                           "r" : "138",
                           "s" : [ {
                              "r" : "137",
                              "s" : [ {
                                 "value" : [ "(" ]
                              }, {
                                 "r" : "137",
                                 "s" : [ {
                                    "value" : [ "{" ]
                                 }, {
                                    "r" : "130",
                                    "s" : [ {
                                       "value" : [ "Tuple{" ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "a",": ","1" ]
                                       } ]
                                    }, {
                                       "value" : [ ", " ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "b",":","2" ]
                                       } ]
                                    }, {
                                       "value" : [ "}" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "133",
                                    "s" : [ {
                                       "value" : [ "Tuple{" ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "a",": ","2" ]
                                       } ]
                                    }, {
                                       "value" : [ ", " ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "b",": ","3" ]
                                       } ]
                                    }, {
                                       "value" : [ "}" ]
                                    } ]
                                 }, {
                                    "value" : [ ", " ]
                                 }, {
                                    "r" : "136",
                                    "s" : [ {
                                       "value" : [ "Tuple{" ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "a",": ","1" ]
                                       } ]
                                    }, {
                                       "value" : [ ", " ]
                                    }, {
                                       "s" : [ {
                                          "value" : [ "b",": ","2" ]
                                       } ]
                                    }, {
                                       "value" : [ "}" ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              }, {
                                 "value" : [ ")" ]
                              } ]
                           }, {
                              "value" : [ " ","T" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "140",
                        "s" : [ {
                           "value" : [ "return all " ]
                        }, {
                           "r" : "139",
                           "s" : [ {
                              "value" : [ "T" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "141",
               "type" : "Query",
               "source" : [ {
                  "localId" : "138",
                  "alias" : "T",
                  "expression" : {
                     "localId" : "137",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "130",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "localId" : "128",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "localId" : "129",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     }, {
                        "localId" : "133",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "localId" : "131",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "localId" : "132",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "3",
                              "type" : "Literal"
                           }
                        } ]
                     }, {
                        "localId" : "136",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "localId" : "134",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "localId" : "135",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "140",
                  "distinct" : false,
                  "expression" : {
                     "localId" : "139",
                     "name" : "T",
                     "type" : "AliasRef"
                  }
               }
            }
         } ]
      }
   }
}

### SingleObjectAlias
library TestSnippet version '1'
using QUICK
context Patient
define encounters: [Encounter] E
define conditions: [Condition] C
define firstEncounter: First([Encounter] E )
define firstCondition: First([Condition] C where C.id = 'http://cqframework.org/3/2')
define singleAlias: firstEncounter E
define singleAliasWhere: firstEncounter E where E is not null
define singleAliasWhereToNull: firstEncounter  E where E.period is null
define singleAliases: from firstEncounter E, firstCondition C
define singleAliasesAndList: from firstEncounter E, firstCondition C , conditions Con
define singleAliasWith:  [Encounter] E with firstCondition C such that C.id = 'http://cqframework.org/3/2'
define singleAliasWithOut:  [Encounter] E without firstCondition C such that C.id = 'http://cqframework.org/3'
define singleAliasWithEmpty:  [Encounter] E with firstCondition C such that C.id = 'http://cqframework.org/3'
define singleAliasWithOutEmpty:  [Encounter] E without firstCondition C such that C.id = 'http://cqframework.org/3/2'
define asNull: null
define nullQuery: asNull N

//define singleAliasWith: firstEncounter E with [Condition] C
//                         such that C.id = 'http://cqframework.org/3/2'
//define singleAliasWithNull: firstEncounter E with conditions C
//                        such that C.id is null
define singleAliasReturnTuple: firstEncounter E return Tuple{a:1}
define singleAliasReturnList: firstEncounter E return {'foo', 'bar', 'baz', 'bar'}
###

module.exports['SingleObjectAlias'] = {
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
            "localId" : "1",
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
            "localId" : "5",
            "name" : "encounters",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","encounters",": " ]
                  }, {
                     "r" : "4",
                     "s" : [ {
                        "s" : [ {
                           "r" : "3",
                           "s" : [ {
                              "r" : "2",
                              "s" : [ {
                                 "r" : "2",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "type" : "Query",
               "source" : [ {
                  "localId" : "3",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "2",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ]
            }
         }, {
            "localId" : "9",
            "name" : "conditions",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","conditions",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "s" : [ {
                           "r" : "7",
                           "s" : [ {
                              "r" : "6",
                              "s" : [ {
                                 "r" : "6",
                                 "s" : [ {
                                    "value" : [ "[","Condition","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","C" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "Query",
               "source" : [ {
                  "localId" : "7",
                  "alias" : "C",
                  "expression" : {
                     "localId" : "6",
                     "dataType" : "{http://hl7.org/fhir}Condition",
                     "templateId" : "condition-qicore-qicore-condition",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ]
            }
         }, {
            "localId" : "14",
            "name" : "firstEncounter",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "14",
                  "s" : [ {
                     "value" : [ "define ","firstEncounter",": " ]
                  }, {
                     "r" : "13",
                     "s" : [ {
                        "value" : [ "First","(" ]
                     }, {
                        "r" : "12",
                        "s" : [ {
                           "s" : [ {
                              "r" : "11",
                              "s" : [ {
                                 "r" : "10",
                                 "s" : [ {
                                    "r" : "10",
                                    "s" : [ {
                                       "value" : [ "[","Encounter","]" ]
                                    } ]
                                 } ]
                              }, {
                                 "value" : [ " ","E" ]
                              } ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " )" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "13",
               "type" : "First",
               "source" : {
                  "localId" : "12",
                  "type" : "Query",
                  "source" : [ {
                     "localId" : "11",
                     "alias" : "E",
                     "expression" : {
                        "localId" : "10",
                        "dataType" : "{http://hl7.org/fhir}Encounter",
                        "templateId" : "encounter-qicore-qicore-encounter",
                        "type" : "Retrieve"
                     }
                  } ],
                  "relationship" : [ ]
               }
            }
         }, {
            "localId" : "23",
            "name" : "firstCondition",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "23",
                  "s" : [ {
                     "value" : [ "define ","firstCondition",": " ]
                  }, {
                     "r" : "22",
                     "s" : [ {
                        "value" : [ "First","(" ]
                     }, {
                        "r" : "21",
                        "s" : [ {
                           "s" : [ {
                              "r" : "16",
                              "s" : [ {
                                 "r" : "15",
                                 "s" : [ {
                                    "r" : "15",
                                    "s" : [ {
                                       "value" : [ "[","Condition","]" ]
                                    } ]
                                 } ]
                              }, {
                                 "value" : [ " ","C" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ " " ]
                        }, {
                           "r" : "20",
                           "s" : [ {
                              "value" : [ "where " ]
                           }, {
                              "r" : "20",
                              "s" : [ {
                                 "r" : "18",
                                 "s" : [ {
                                    "r" : "17",
                                    "s" : [ {
                                       "value" : [ "C" ]
                                    } ]
                                 }, {
                                    "value" : [ "." ]
                                 }, {
                                    "r" : "18",
                                    "s" : [ {
                                       "value" : [ "id" ]
                                    } ]
                                 } ]
                              }, {
                                 "value" : [ " ","="," " ]
                              }, {
                                 "r" : "19",
                                 "s" : [ {
                                    "value" : [ "'http://cqframework.org/3/2'" ]
                                 } ]
                              } ]
                           } ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "22",
               "type" : "First",
               "source" : {
                  "localId" : "21",
                  "type" : "Query",
                  "source" : [ {
                     "localId" : "16",
                     "alias" : "C",
                     "expression" : {
                        "localId" : "15",
                        "dataType" : "{http://hl7.org/fhir}Condition",
                        "templateId" : "condition-qicore-qicore-condition",
                        "type" : "Retrieve"
                     }
                  } ],
                  "relationship" : [ ],
                  "where" : {
                     "localId" : "20",
                     "type" : "Equal",
                     "operand" : [ {
                        "localId" : "18",
                        "path" : "id",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "localId" : "19",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "http://cqframework.org/3/2",
                        "type" : "Literal"
                     } ]
                  }
               }
            }
         }, {
            "localId" : "27",
            "name" : "singleAlias",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "27",
                  "s" : [ {
                     "value" : [ "define ","singleAlias",": " ]
                  }, {
                     "r" : "26",
                     "s" : [ {
                        "s" : [ {
                           "r" : "25",
                           "s" : [ {
                              "r" : "24",
                              "s" : [ {
                                 "s" : [ {
                                    "value" : [ "firstEncounter" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "26",
               "type" : "Query",
               "source" : [ {
                  "localId" : "25",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "24",
                     "name" : "firstEncounter",
                     "type" : "ExpressionRef"
                  }
               } ],
               "relationship" : [ ]
            }
         }, {
            "localId" : "33",
            "name" : "singleAliasWhere",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "33",
                  "s" : [ {
                     "value" : [ "define ","singleAliasWhere",": " ]
                  }, {
                     "r" : "32",
                     "s" : [ {
                        "s" : [ {
                           "r" : "29",
                           "s" : [ {
                              "r" : "28",
                              "s" : [ {
                                 "s" : [ {
                                    "value" : [ "firstEncounter" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "31",
                        "s" : [ {
                           "value" : [ "where " ]
                        }, {
                           "r" : "31",
                           "s" : [ {
                              "r" : "30",
                              "s" : [ {
                                 "value" : [ "E" ]
                              } ]
                           }, {
                              "value" : [ " is not null" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "32",
               "type" : "Query",
               "source" : [ {
                  "localId" : "29",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "28",
                     "name" : "firstEncounter",
                     "type" : "ExpressionRef"
                  }
               } ],
               "relationship" : [ ],
               "where" : {
                  "localId" : "31",
                  "type" : "Not",
                  "operand" : {
                     "type" : "IsNull",
                     "operand" : {
                        "localId" : "30",
                        "name" : "E",
                        "type" : "AliasRef"
                     }
                  }
               }
            }
         }, {
            "localId" : "40",
            "name" : "singleAliasWhereToNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "40",
                  "s" : [ {
                     "value" : [ "define ","singleAliasWhereToNull",": " ]
                  }, {
                     "r" : "39",
                     "s" : [ {
                        "s" : [ {
                           "r" : "35",
                           "s" : [ {
                              "r" : "34",
                              "s" : [ {
                                 "s" : [ {
                                    "value" : [ "firstEncounter" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "  ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "38",
                        "s" : [ {
                           "value" : [ "where " ]
                        }, {
                           "r" : "38",
                           "s" : [ {
                              "r" : "37",
                              "s" : [ {
                                 "r" : "36",
                                 "s" : [ {
                                    "value" : [ "E" ]
                                 } ]
                              }, {
                                 "value" : [ "." ]
                              }, {
                                 "r" : "37",
                                 "s" : [ {
                                    "value" : [ "period" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " is null" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "39",
               "type" : "Query",
               "source" : [ {
                  "localId" : "35",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "34",
                     "name" : "firstEncounter",
                     "type" : "ExpressionRef"
                  }
               } ],
               "relationship" : [ ],
               "where" : {
                  "localId" : "38",
                  "type" : "IsNull",
                  "operand" : {
                     "localId" : "37",
                     "path" : "period",
                     "scope" : "E",
                     "type" : "Property"
                  }
               }
            }
         }, {
            "localId" : "46",
            "name" : "singleAliases",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "46",
                  "s" : [ {
                     "value" : [ "define ","singleAliases",": " ]
                  }, {
                     "r" : "45",
                     "s" : [ {
                        "s" : [ {
                           "value" : [ "from " ]
                        }, {
                           "r" : "42",
                           "s" : [ {
                              "r" : "41",
                              "s" : [ {
                                 "s" : [ {
                                    "value" : [ "firstEncounter" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "44",
                           "s" : [ {
                              "r" : "43",
                              "s" : [ {
                                 "s" : [ {
                                    "value" : [ "firstCondition" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","C" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "45",
               "type" : "Query",
               "source" : [ {
                  "localId" : "42",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "41",
                     "name" : "firstEncounter",
                     "type" : "ExpressionRef"
                  }
               }, {
                  "localId" : "44",
                  "alias" : "C",
                  "expression" : {
                     "localId" : "43",
                     "name" : "firstCondition",
                     "type" : "ExpressionRef"
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
            "localId" : "54",
            "name" : "singleAliasesAndList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "54",
                  "s" : [ {
                     "value" : [ "define ","singleAliasesAndList",": " ]
                  }, {
                     "r" : "53",
                     "s" : [ {
                        "s" : [ {
                           "value" : [ "from " ]
                        }, {
                           "r" : "48",
                           "s" : [ {
                              "r" : "47",
                              "s" : [ {
                                 "s" : [ {
                                    "value" : [ "firstEncounter" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "50",
                           "s" : [ {
                              "r" : "49",
                              "s" : [ {
                                 "s" : [ {
                                    "value" : [ "firstCondition" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","C" ]
                           } ]
                        }, {
                           "value" : [ " , " ]
                        }, {
                           "r" : "52",
                           "s" : [ {
                              "r" : "51",
                              "s" : [ {
                                 "s" : [ {
                                    "value" : [ "conditions" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","Con" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "53",
               "type" : "Query",
               "source" : [ {
                  "localId" : "48",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "47",
                     "name" : "firstEncounter",
                     "type" : "ExpressionRef"
                  }
               }, {
                  "localId" : "50",
                  "alias" : "C",
                  "expression" : {
                     "localId" : "49",
                     "name" : "firstCondition",
                     "type" : "ExpressionRef"
                  }
               }, {
                  "localId" : "52",
                  "alias" : "Con",
                  "expression" : {
                     "localId" : "51",
                     "name" : "conditions",
                     "type" : "ExpressionRef"
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
                     }, {
                        "name" : "Con",
                        "value" : {
                           "name" : "Con",
                           "type" : "AliasRef"
                        }
                     } ]
                  }
               }
            }
         }, {
            "localId" : "65",
            "name" : "singleAliasWith",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "65",
                  "s" : [ {
                     "value" : [ "define ","singleAliasWith",":  " ]
                  }, {
                     "r" : "64",
                     "s" : [ {
                        "s" : [ {
                           "r" : "56",
                           "s" : [ {
                              "r" : "55",
                              "s" : [ {
                                 "r" : "55",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "63",
                        "s" : [ {
                           "value" : [ "with " ]
                        }, {
                           "r" : "58",
                           "s" : [ {
                              "r" : "57",
                              "s" : [ {
                                 "s" : [ {
                                    "value" : [ "firstCondition" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","C" ]
                           } ]
                        }, {
                           "value" : [ " such that " ]
                        }, {
                           "r" : "62",
                           "s" : [ {
                              "r" : "60",
                              "s" : [ {
                                 "r" : "59",
                                 "s" : [ {
                                    "value" : [ "C" ]
                                 } ]
                              }, {
                                 "value" : [ "." ]
                              }, {
                                 "r" : "60",
                                 "s" : [ {
                                    "value" : [ "id" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","="," " ]
                           }, {
                              "r" : "61",
                              "s" : [ {
                                 "value" : [ "'http://cqframework.org/3/2'" ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "64",
               "type" : "Query",
               "source" : [ {
                  "localId" : "56",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "55",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ {
                  "localId" : "63",
                  "alias" : "C",
                  "type" : "With",
                  "expression" : {
                     "localId" : "57",
                     "name" : "firstCondition",
                     "type" : "ExpressionRef"
                  },
                  "suchThat" : {
                     "localId" : "62",
                     "type" : "Equal",
                     "operand" : [ {
                        "localId" : "60",
                        "path" : "id",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "localId" : "61",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "http://cqframework.org/3/2",
                        "type" : "Literal"
                     } ]
                  }
               } ]
            }
         }, {
            "localId" : "76",
            "name" : "singleAliasWithOut",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "76",
                  "s" : [ {
                     "value" : [ "define ","singleAliasWithOut",":  " ]
                  }, {
                     "r" : "75",
                     "s" : [ {
                        "s" : [ {
                           "r" : "67",
                           "s" : [ {
                              "r" : "66",
                              "s" : [ {
                                 "r" : "66",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "74",
                        "s" : [ {
                           "value" : [ "without " ]
                        }, {
                           "r" : "69",
                           "s" : [ {
                              "r" : "68",
                              "s" : [ {
                                 "s" : [ {
                                    "value" : [ "firstCondition" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","C" ]
                           } ]
                        }, {
                           "value" : [ " such that " ]
                        }, {
                           "r" : "73",
                           "s" : [ {
                              "r" : "71",
                              "s" : [ {
                                 "r" : "70",
                                 "s" : [ {
                                    "value" : [ "C" ]
                                 } ]
                              }, {
                                 "value" : [ "." ]
                              }, {
                                 "r" : "71",
                                 "s" : [ {
                                    "value" : [ "id" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","="," " ]
                           }, {
                              "r" : "72",
                              "s" : [ {
                                 "value" : [ "'http://cqframework.org/3'" ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "75",
               "type" : "Query",
               "source" : [ {
                  "localId" : "67",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "66",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ {
                  "localId" : "74",
                  "alias" : "C",
                  "type" : "Without",
                  "expression" : {
                     "localId" : "68",
                     "name" : "firstCondition",
                     "type" : "ExpressionRef"
                  },
                  "suchThat" : {
                     "localId" : "73",
                     "type" : "Equal",
                     "operand" : [ {
                        "localId" : "71",
                        "path" : "id",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "localId" : "72",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "http://cqframework.org/3",
                        "type" : "Literal"
                     } ]
                  }
               } ]
            }
         }, {
            "localId" : "87",
            "name" : "singleAliasWithEmpty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "87",
                  "s" : [ {
                     "value" : [ "define ","singleAliasWithEmpty",":  " ]
                  }, {
                     "r" : "86",
                     "s" : [ {
                        "s" : [ {
                           "r" : "78",
                           "s" : [ {
                              "r" : "77",
                              "s" : [ {
                                 "r" : "77",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "85",
                        "s" : [ {
                           "value" : [ "with " ]
                        }, {
                           "r" : "80",
                           "s" : [ {
                              "r" : "79",
                              "s" : [ {
                                 "s" : [ {
                                    "value" : [ "firstCondition" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","C" ]
                           } ]
                        }, {
                           "value" : [ " such that " ]
                        }, {
                           "r" : "84",
                           "s" : [ {
                              "r" : "82",
                              "s" : [ {
                                 "r" : "81",
                                 "s" : [ {
                                    "value" : [ "C" ]
                                 } ]
                              }, {
                                 "value" : [ "." ]
                              }, {
                                 "r" : "82",
                                 "s" : [ {
                                    "value" : [ "id" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","="," " ]
                           }, {
                              "r" : "83",
                              "s" : [ {
                                 "value" : [ "'http://cqframework.org/3'" ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "86",
               "type" : "Query",
               "source" : [ {
                  "localId" : "78",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "77",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ {
                  "localId" : "85",
                  "alias" : "C",
                  "type" : "With",
                  "expression" : {
                     "localId" : "79",
                     "name" : "firstCondition",
                     "type" : "ExpressionRef"
                  },
                  "suchThat" : {
                     "localId" : "84",
                     "type" : "Equal",
                     "operand" : [ {
                        "localId" : "82",
                        "path" : "id",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "localId" : "83",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "http://cqframework.org/3",
                        "type" : "Literal"
                     } ]
                  }
               } ]
            }
         }, {
            "localId" : "98",
            "name" : "singleAliasWithOutEmpty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "98",
                  "s" : [ {
                     "value" : [ "define ","singleAliasWithOutEmpty",":  " ]
                  }, {
                     "r" : "97",
                     "s" : [ {
                        "s" : [ {
                           "r" : "89",
                           "s" : [ {
                              "r" : "88",
                              "s" : [ {
                                 "r" : "88",
                                 "s" : [ {
                                    "value" : [ "[","Encounter","]" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "96",
                        "s" : [ {
                           "value" : [ "without " ]
                        }, {
                           "r" : "91",
                           "s" : [ {
                              "r" : "90",
                              "s" : [ {
                                 "s" : [ {
                                    "value" : [ "firstCondition" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","C" ]
                           } ]
                        }, {
                           "value" : [ " such that " ]
                        }, {
                           "r" : "95",
                           "s" : [ {
                              "r" : "93",
                              "s" : [ {
                                 "r" : "92",
                                 "s" : [ {
                                    "value" : [ "C" ]
                                 } ]
                              }, {
                                 "value" : [ "." ]
                              }, {
                                 "r" : "93",
                                 "s" : [ {
                                    "value" : [ "id" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","="," " ]
                           }, {
                              "r" : "94",
                              "s" : [ {
                                 "value" : [ "'http://cqframework.org/3/2'" ]
                              } ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "97",
               "type" : "Query",
               "source" : [ {
                  "localId" : "89",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "88",
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ {
                  "localId" : "96",
                  "alias" : "C",
                  "type" : "Without",
                  "expression" : {
                     "localId" : "90",
                     "name" : "firstCondition",
                     "type" : "ExpressionRef"
                  },
                  "suchThat" : {
                     "localId" : "95",
                     "type" : "Equal",
                     "operand" : [ {
                        "localId" : "93",
                        "path" : "id",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "localId" : "94",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "http://cqframework.org/3/2",
                        "type" : "Literal"
                     } ]
                  }
               } ]
            }
         }, {
            "localId" : "100",
            "name" : "asNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "100",
                  "s" : [ {
                     "value" : [ "define ","asNull",": ","null" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "99",
               "type" : "Null"
            }
         }, {
            "localId" : "104",
            "name" : "nullQuery",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "104",
                  "s" : [ {
                     "value" : [ "define ","nullQuery",": " ]
                  }, {
                     "r" : "103",
                     "s" : [ {
                        "s" : [ {
                           "r" : "102",
                           "s" : [ {
                              "r" : "101",
                              "s" : [ {
                                 "s" : [ {
                                    "value" : [ "asNull" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","N" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "103",
               "type" : "Query",
               "source" : [ {
                  "localId" : "102",
                  "alias" : "N",
                  "expression" : {
                     "localId" : "101",
                     "name" : "asNull",
                     "type" : "ExpressionRef"
                  }
               } ],
               "relationship" : [ ]
            }
         }, {
            "localId" : "111",
            "name" : "singleAliasReturnTuple",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "111",
                  "s" : [ {
                     "value" : [ "define ","singleAliasReturnTuple",": " ]
                  }, {
                     "r" : "110",
                     "s" : [ {
                        "s" : [ {
                           "r" : "106",
                           "s" : [ {
                              "r" : "105",
                              "s" : [ {
                                 "s" : [ {
                                    "value" : [ "firstEncounter" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "109",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "108",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "110",
               "type" : "Query",
               "source" : [ {
                  "localId" : "106",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "105",
                     "name" : "firstEncounter",
                     "type" : "ExpressionRef"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "109",
                  "expression" : {
                     "localId" : "108",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "107",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     } ]
                  }
               }
            }
         }, {
            "localId" : "121",
            "name" : "singleAliasReturnList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "121",
                  "s" : [ {
                     "value" : [ "define ","singleAliasReturnList",": " ]
                  }, {
                     "r" : "120",
                     "s" : [ {
                        "s" : [ {
                           "r" : "113",
                           "s" : [ {
                              "r" : "112",
                              "s" : [ {
                                 "s" : [ {
                                    "value" : [ "firstEncounter" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " ","E" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " " ]
                     }, {
                        "r" : "119",
                        "s" : [ {
                           "value" : [ "return " ]
                        }, {
                           "r" : "118",
                           "s" : [ {
                              "value" : [ "{" ]
                           }, {
                              "r" : "114",
                              "s" : [ {
                                 "value" : [ "'foo'" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "r" : "115",
                              "s" : [ {
                                 "value" : [ "'bar'" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "r" : "116",
                              "s" : [ {
                                 "value" : [ "'baz'" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "r" : "117",
                              "s" : [ {
                                 "value" : [ "'bar'" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "120",
               "type" : "Query",
               "source" : [ {
                  "localId" : "113",
                  "alias" : "E",
                  "expression" : {
                     "localId" : "112",
                     "name" : "firstEncounter",
                     "type" : "ExpressionRef"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "localId" : "119",
                  "expression" : {
                     "localId" : "118",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "114",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "foo",
                        "type" : "Literal"
                     }, {
                        "localId" : "115",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "bar",
                        "type" : "Literal"
                     }, {
                        "localId" : "116",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "baz",
                        "type" : "Literal"
                     }, {
                        "localId" : "117",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "bar",
                        "type" : "Literal"
                     } ]
                  }
               }
            }
         } ]
      }
   }
}

