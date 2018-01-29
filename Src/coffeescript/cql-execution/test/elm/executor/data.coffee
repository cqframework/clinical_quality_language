###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### Age
library TestSnippet version '1'
using QUICK
parameter MeasurementPeriod default Interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))

context Patient

define Age:
  AgeInYearsAt(start of MeasurementPeriod)

define InDemographic:
  AgeInYearsAt(start of MeasurementPeriod) >= 2 and AgeInYearsAt(start of MeasurementPeriod) < 18  

context Population

define AgeSum: Sum(Age)

define DEMO: Count(InDemographic w where w is true )

define AgeSumRef : AgeSum
###

module.exports['Age'] = {
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
            "localId" : "15",
            "name" : "Age",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "15",
                  "s" : [ {
                     "value" : [ "define ","Age",":\n  " ]
                  }, {
                     "r" : "14",
                     "s" : [ {
                        "value" : [ "AgeInYearsAt","(" ]
                     }, {
                        "r" : "13",
                        "s" : [ {
                           "value" : [ "start of " ]
                        }, {
                           "r" : "12",
                           "s" : [ {
                              "value" : [ "MeasurementPeriod" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "14",
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
                  "localId" : "13",
                  "type" : "Start",
                  "operand" : {
                     "localId" : "12",
                     "name" : "MeasurementPeriod",
                     "type" : "ParameterRef"
                  }
               } ]
            }
         }, {
            "localId" : "27",
            "name" : "InDemographic",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "27",
                  "s" : [ {
                     "value" : [ "define ","InDemographic",":\n  " ]
                  }, {
                     "r" : "26",
                     "s" : [ {
                        "r" : "20",
                        "s" : [ {
                           "r" : "18",
                           "s" : [ {
                              "value" : [ "AgeInYearsAt","(" ]
                           }, {
                              "r" : "17",
                              "s" : [ {
                                 "value" : [ "start of " ]
                              }, {
                                 "r" : "16",
                                 "s" : [ {
                                    "value" : [ "MeasurementPeriod" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ ")" ]
                           } ]
                        }, {
                           "value" : [ " ",">="," ","2" ]
                        } ]
                     }, {
                        "value" : [ " and " ]
                     }, {
                        "r" : "25",
                        "s" : [ {
                           "r" : "23",
                           "s" : [ {
                              "value" : [ "AgeInYearsAt","(" ]
                           }, {
                              "r" : "22",
                              "s" : [ {
                                 "value" : [ "start of " ]
                              }, {
                                 "r" : "21",
                                 "s" : [ {
                                    "value" : [ "MeasurementPeriod" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ ")" ]
                           } ]
                        }, {
                           "value" : [ " ","<"," ","18" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "26",
               "type" : "And",
               "operand" : [ {
                  "localId" : "20",
                  "type" : "GreaterOrEqual",
                  "operand" : [ {
                     "localId" : "18",
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
                        "localId" : "17",
                        "type" : "Start",
                        "operand" : {
                           "localId" : "16",
                           "name" : "MeasurementPeriod",
                           "type" : "ParameterRef"
                        }
                     } ]
                  }, {
                     "localId" : "19",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "25",
                  "type" : "Less",
                  "operand" : [ {
                     "localId" : "23",
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
                        "localId" : "22",
                        "type" : "Start",
                        "operand" : {
                           "localId" : "21",
                           "name" : "MeasurementPeriod",
                           "type" : "ParameterRef"
                        }
                     } ]
                  }, {
                     "localId" : "24",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "18",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "30",
            "name" : "AgeSum",
            "context" : "Population",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "30",
                  "s" : [ {
                     "value" : [ "define ","AgeSum",": " ]
                  }, {
                     "r" : "29",
                     "s" : [ {
                        "value" : [ "Sum","(" ]
                     }, {
                        "r" : "28",
                        "s" : [ {
                           "value" : [ "Age" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "29",
               "type" : "Sum",
               "source" : {
                  "localId" : "28",
                  "name" : "Age",
                  "type" : "ExpressionRef"
               }
            }
         }, {
            "localId" : "37",
            "name" : "DEMO",
            "context" : "Population",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "37",
                  "s" : [ {
                     "value" : [ "define ","DEMO",": " ]
                  }, {
                     "r" : "36",
                     "s" : [ {
                        "value" : [ "Count","(" ]
                     }, {
                        "r" : "35",
                        "s" : [ {
                           "s" : [ {
                              "r" : "32",
                              "s" : [ {
                                 "r" : "31",
                                 "s" : [ {
                                    "s" : [ {
                                       "value" : [ "InDemographic" ]
                                    } ]
                                 } ]
                              }, {
                                 "value" : [ " ","w" ]
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
                                 "r" : "33",
                                 "s" : [ {
                                    "value" : [ "w" ]
                                 } ]
                              }, {
                                 "value" : [ " is true" ]
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
               "localId" : "36",
               "type" : "Count",
               "source" : {
                  "localId" : "35",
                  "type" : "Query",
                  "source" : [ {
                     "localId" : "32",
                     "alias" : "w",
                     "expression" : {
                        "localId" : "31",
                        "name" : "InDemographic",
                        "type" : "ExpressionRef"
                     }
                  } ],
                  "relationship" : [ ],
                  "where" : {
                     "localId" : "34",
                     "type" : "IsTrue",
                     "operand" : {
                        "localId" : "33",
                        "name" : "w",
                        "type" : "AliasRef"
                     }
                  }
               }
            }
         }, {
            "localId" : "39",
            "name" : "AgeSumRef",
            "context" : "Population",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "39",
                  "s" : [ {
                     "value" : [ "define ","AgeSumRef"," : " ]
                  }, {
                     "r" : "38",
                     "s" : [ {
                        "value" : [ "AgeSum" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "38",
               "name" : "AgeSum",
               "type" : "ExpressionRef"
            }
         } ]
      }
   }
}

