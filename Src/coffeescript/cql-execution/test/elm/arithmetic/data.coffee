###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### Add
library TestSnippet version '1'
using QUICK
context Patient
define Ten: 10
define Eleven: 11
define OnePlusTwo: 1 + 2
define AddMultiple: 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 + 10
define AddVariables: Ten + Eleven
###

module.exports['Add'] = {
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
            "localId" : "3",
            "name" : "Ten",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "3",
                  "s" : [ {
                     "value" : [ "define ","Ten",": ","10" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "2",
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "10",
               "type" : "Literal"
            }
         }, {
            "localId" : "5",
            "name" : "Eleven",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","Eleven",": ","11" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "11",
               "type" : "Literal"
            }
         }, {
            "localId" : "9",
            "name" : "OnePlusTwo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","OnePlusTwo",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "r" : "6",
                        "value" : [ "1"," + ","2" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "Add",
               "operand" : [ {
                  "localId" : "6",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "localId" : "7",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "29",
            "name" : "AddMultiple",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "29",
                  "s" : [ {
                     "value" : [ "define ","AddMultiple",": " ]
                  }, {
                     "r" : "28",
                     "s" : [ {
                        "r" : "26",
                        "s" : [ {
                           "r" : "24",
                           "s" : [ {
                              "r" : "22",
                              "s" : [ {
                                 "r" : "20",
                                 "s" : [ {
                                    "r" : "18",
                                    "s" : [ {
                                       "r" : "16",
                                       "s" : [ {
                                          "r" : "14",
                                          "s" : [ {
                                             "r" : "12",
                                             "s" : [ {
                                                "r" : "10",
                                                "value" : [ "1"," + ","2" ]
                                             } ]
                                          }, {
                                             "value" : [ " + ","3" ]
                                          } ]
                                       }, {
                                          "value" : [ " + ","4" ]
                                       } ]
                                    }, {
                                       "value" : [ " + ","5" ]
                                    } ]
                                 }, {
                                    "value" : [ " + ","6" ]
                                 } ]
                              }, {
                                 "value" : [ " + ","7" ]
                              } ]
                           }, {
                              "value" : [ " + ","8" ]
                           } ]
                        }, {
                           "value" : [ " + ","9" ]
                        } ]
                     }, {
                        "value" : [ " + ","10" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "28",
               "type" : "Add",
               "operand" : [ {
                  "localId" : "26",
                  "type" : "Add",
                  "operand" : [ {
                     "localId" : "24",
                     "type" : "Add",
                     "operand" : [ {
                        "localId" : "22",
                        "type" : "Add",
                        "operand" : [ {
                           "localId" : "20",
                           "type" : "Add",
                           "operand" : [ {
                              "localId" : "18",
                              "type" : "Add",
                              "operand" : [ {
                                 "localId" : "16",
                                 "type" : "Add",
                                 "operand" : [ {
                                    "localId" : "14",
                                    "type" : "Add",
                                    "operand" : [ {
                                       "localId" : "12",
                                       "type" : "Add",
                                       "operand" : [ {
                                          "localId" : "10",
                                          "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                                          "value" : "1",
                                          "type" : "Literal"
                                       }, {
                                          "localId" : "11",
                                          "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                                          "value" : "2",
                                          "type" : "Literal"
                                       } ]
                                    }, {
                                       "localId" : "13",
                                       "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                                       "value" : "3",
                                       "type" : "Literal"
                                    } ]
                                 }, {
                                    "localId" : "15",
                                    "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                                    "value" : "4",
                                    "type" : "Literal"
                                 } ]
                              }, {
                                 "localId" : "17",
                                 "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                                 "value" : "5",
                                 "type" : "Literal"
                              } ]
                           }, {
                              "localId" : "19",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "6",
                              "type" : "Literal"
                           } ]
                        }, {
                           "localId" : "21",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "7",
                           "type" : "Literal"
                        } ]
                     }, {
                        "localId" : "23",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "8",
                        "type" : "Literal"
                     } ]
                  }, {
                     "localId" : "25",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "9",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "27",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "10",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "33",
            "name" : "AddVariables",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "33",
                  "s" : [ {
                     "value" : [ "define ","AddVariables",": " ]
                  }, {
                     "r" : "32",
                     "s" : [ {
                        "r" : "30",
                        "s" : [ {
                           "value" : [ "Ten" ]
                        } ]
                     }, {
                        "value" : [ " + " ]
                     }, {
                        "r" : "31",
                        "s" : [ {
                           "value" : [ "Eleven" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "32",
               "type" : "Add",
               "operand" : [ {
                  "localId" : "30",
                  "name" : "Ten",
                  "type" : "ExpressionRef"
               }, {
                  "localId" : "31",
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
define Ten: 10
define Eleven: 11
define FiveMinusTwo: 5 - 2
define SubtractMultiple: 100 - 50 - 25 - 10
define SubtractVariables: Eleven - Ten
###

module.exports['Subtract'] = {
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
            "localId" : "3",
            "name" : "Ten",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "3",
                  "s" : [ {
                     "value" : [ "define ","Ten",": ","10" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "2",
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "10",
               "type" : "Literal"
            }
         }, {
            "localId" : "5",
            "name" : "Eleven",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","Eleven",": ","11" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "11",
               "type" : "Literal"
            }
         }, {
            "localId" : "9",
            "name" : "FiveMinusTwo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","FiveMinusTwo",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "r" : "6",
                        "value" : [ "5"," - ","2" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "Subtract",
               "operand" : [ {
                  "localId" : "6",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "localId" : "7",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "17",
            "name" : "SubtractMultiple",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "17",
                  "s" : [ {
                     "value" : [ "define ","SubtractMultiple",": " ]
                  }, {
                     "r" : "16",
                     "s" : [ {
                        "r" : "14",
                        "s" : [ {
                           "r" : "12",
                           "s" : [ {
                              "r" : "10",
                              "value" : [ "100"," - ","50" ]
                           } ]
                        }, {
                           "value" : [ " - ","25" ]
                        } ]
                     }, {
                        "value" : [ " - ","10" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "16",
               "type" : "Subtract",
               "operand" : [ {
                  "localId" : "14",
                  "type" : "Subtract",
                  "operand" : [ {
                     "localId" : "12",
                     "type" : "Subtract",
                     "operand" : [ {
                        "localId" : "10",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "100",
                        "type" : "Literal"
                     }, {
                        "localId" : "11",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "50",
                        "type" : "Literal"
                     } ]
                  }, {
                     "localId" : "13",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "25",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "15",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "10",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "21",
            "name" : "SubtractVariables",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "21",
                  "s" : [ {
                     "value" : [ "define ","SubtractVariables",": " ]
                  }, {
                     "r" : "20",
                     "s" : [ {
                        "r" : "18",
                        "s" : [ {
                           "value" : [ "Eleven" ]
                        } ]
                     }, {
                        "value" : [ " - " ]
                     }, {
                        "r" : "19",
                        "s" : [ {
                           "value" : [ "Ten" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "20",
               "type" : "Subtract",
               "operand" : [ {
                  "localId" : "18",
                  "name" : "Eleven",
                  "type" : "ExpressionRef"
               }, {
                  "localId" : "19",
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
define Ten: 10
define Eleven: 11
define FiveTimesTwo: 5 * 2
define MultiplyMultiple: 1 * 2 * 3 * 4 * 5
define MultiplyVariables: Eleven * Ten
###

module.exports['Multiply'] = {
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
            "localId" : "3",
            "name" : "Ten",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "3",
                  "s" : [ {
                     "value" : [ "define ","Ten",": ","10" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "2",
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "10",
               "type" : "Literal"
            }
         }, {
            "localId" : "5",
            "name" : "Eleven",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","Eleven",": ","11" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "11",
               "type" : "Literal"
            }
         }, {
            "localId" : "9",
            "name" : "FiveTimesTwo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","FiveTimesTwo",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "r" : "6",
                        "value" : [ "5"," * ","2" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "Multiply",
               "operand" : [ {
                  "localId" : "6",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "localId" : "7",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "19",
            "name" : "MultiplyMultiple",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "19",
                  "s" : [ {
                     "value" : [ "define ","MultiplyMultiple",": " ]
                  }, {
                     "r" : "18",
                     "s" : [ {
                        "r" : "16",
                        "s" : [ {
                           "r" : "14",
                           "s" : [ {
                              "r" : "12",
                              "s" : [ {
                                 "r" : "10",
                                 "value" : [ "1"," * ","2" ]
                              } ]
                           }, {
                              "value" : [ " * ","3" ]
                           } ]
                        }, {
                           "value" : [ " * ","4" ]
                        } ]
                     }, {
                        "value" : [ " * ","5" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "18",
               "type" : "Multiply",
               "operand" : [ {
                  "localId" : "16",
                  "type" : "Multiply",
                  "operand" : [ {
                     "localId" : "14",
                     "type" : "Multiply",
                     "operand" : [ {
                        "localId" : "12",
                        "type" : "Multiply",
                        "operand" : [ {
                           "localId" : "10",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "localId" : "11",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        } ]
                     }, {
                        "localId" : "13",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     } ]
                  }, {
                     "localId" : "15",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "17",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "23",
            "name" : "MultiplyVariables",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "23",
                  "s" : [ {
                     "value" : [ "define ","MultiplyVariables",": " ]
                  }, {
                     "r" : "22",
                     "s" : [ {
                        "r" : "20",
                        "s" : [ {
                           "value" : [ "Eleven" ]
                        } ]
                     }, {
                        "value" : [ " * " ]
                     }, {
                        "r" : "21",
                        "s" : [ {
                           "value" : [ "Ten" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "22",
               "type" : "Multiply",
               "operand" : [ {
                  "localId" : "20",
                  "name" : "Eleven",
                  "type" : "ExpressionRef"
               }, {
                  "localId" : "21",
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
define Hundred: 100
define Four: 4
define TenDividedByTwo: 10 / 2
define TenDividedByFour: 10 / 4
define DivideMultiple: 1000 / 4 / 10 / 5
define DivideVariables: Hundred / Four
###

module.exports['Divide'] = {
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
            "localId" : "3",
            "name" : "Hundred",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "3",
                  "s" : [ {
                     "value" : [ "define ","Hundred",": ","100" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "2",
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "100",
               "type" : "Literal"
            }
         }, {
            "localId" : "5",
            "name" : "Four",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","Four",": ","4" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "4",
               "type" : "Literal"
            }
         }, {
            "localId" : "9",
            "name" : "TenDividedByTwo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","TenDividedByTwo",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "r" : "6",
                        "value" : [ "10"," / ","2" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "Divide",
               "operand" : [ {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "6",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }
               }, {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "7",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "localId" : "13",
            "name" : "TenDividedByFour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "13",
                  "s" : [ {
                     "value" : [ "define ","TenDividedByFour",": " ]
                  }, {
                     "r" : "12",
                     "s" : [ {
                        "r" : "10",
                        "value" : [ "10"," / ","4" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "12",
               "type" : "Divide",
               "operand" : [ {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "10",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }
               }, {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "11",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "localId" : "21",
            "name" : "DivideMultiple",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "21",
                  "s" : [ {
                     "value" : [ "define ","DivideMultiple",": " ]
                  }, {
                     "r" : "20",
                     "s" : [ {
                        "r" : "18",
                        "s" : [ {
                           "r" : "16",
                           "s" : [ {
                              "r" : "14",
                              "value" : [ "1000"," / ","4" ]
                           } ]
                        }, {
                           "value" : [ " / ","10" ]
                        } ]
                     }, {
                        "value" : [ " / ","5" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "20",
               "type" : "Divide",
               "operand" : [ {
                  "localId" : "18",
                  "type" : "Divide",
                  "operand" : [ {
                     "localId" : "16",
                     "type" : "Divide",
                     "operand" : [ {
                        "type" : "ToDecimal",
                        "operand" : {
                           "localId" : "14",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1000",
                           "type" : "Literal"
                        }
                     }, {
                        "type" : "ToDecimal",
                        "operand" : {
                           "localId" : "15",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "4",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "ToDecimal",
                     "operand" : {
                        "localId" : "17",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "10",
                        "type" : "Literal"
                     }
                  } ]
               }, {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "19",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "localId" : "25",
            "name" : "DivideVariables",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "25",
                  "s" : [ {
                     "value" : [ "define ","DivideVariables",": " ]
                  }, {
                     "r" : "24",
                     "s" : [ {
                        "r" : "22",
                        "s" : [ {
                           "value" : [ "Hundred" ]
                        } ]
                     }, {
                        "value" : [ " / " ]
                     }, {
                        "r" : "23",
                        "s" : [ {
                           "value" : [ "Four" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "24",
               "type" : "Divide",
               "operand" : [ {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "22",
                     "name" : "Hundred",
                     "type" : "ExpressionRef"
                  }
               }, {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "23",
                     "name" : "Four",
                     "type" : "ExpressionRef"
                  }
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
define NegativeOne: -1
###

module.exports['Negate'] = {
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
            "localId" : "4",
            "name" : "NegativeOne",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "4",
                  "s" : [ {
                     "value" : [ "define ","NegativeOne",": " ]
                  }, {
                     "r" : "3",
                     "s" : [ {
                        "value" : [ "-","1" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "3",
               "type" : "Negate",
               "operand" : {
                  "localId" : "2",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
define Mixed: 1 + 5 * 10 - 15 / 3
define Parenthetical: (1 + 5) * (10 - 15) / 3
###

module.exports['MathPrecedence'] = {
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
            "name" : "Mixed",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","Mixed",": " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "r" : "6",
                        "s" : [ {
                           "r" : "2",
                           "value" : [ "1"," + " ]
                        }, {
                           "r" : "5",
                           "s" : [ {
                              "r" : "3",
                              "value" : [ "5"," * ","10" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " - " ]
                     }, {
                        "r" : "9",
                        "s" : [ {
                           "r" : "7",
                           "value" : [ "15"," / ","3" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "type" : "Subtract",
               "operand" : [ {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "6",
                     "type" : "Add",
                     "operand" : [ {
                        "localId" : "2",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "localId" : "5",
                        "type" : "Multiply",
                        "operand" : [ {
                           "localId" : "3",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        }, {
                           "localId" : "4",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "10",
                           "type" : "Literal"
                        } ]
                     } ]
                  }
               }, {
                  "localId" : "9",
                  "type" : "Divide",
                  "operand" : [ {
                     "type" : "ToDecimal",
                     "operand" : {
                        "localId" : "7",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "15",
                        "type" : "Literal"
                     }
                  }, {
                     "type" : "ToDecimal",
                     "operand" : {
                        "localId" : "8",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }
                  } ]
               } ]
            }
         }, {
            "localId" : "21",
            "name" : "Parenthetical",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "21",
                  "s" : [ {
                     "value" : [ "define ","Parenthetical",": " ]
                  }, {
                     "r" : "20",
                     "s" : [ {
                        "r" : "18",
                        "s" : [ {
                           "r" : "14",
                           "s" : [ {
                              "value" : [ "(" ]
                           }, {
                              "r" : "14",
                              "s" : [ {
                                 "r" : "12",
                                 "value" : [ "1"," + ","5" ]
                              } ]
                           }, {
                              "value" : [ ")" ]
                           } ]
                        }, {
                           "value" : [ " * " ]
                        }, {
                           "r" : "17",
                           "s" : [ {
                              "value" : [ "(" ]
                           }, {
                              "r" : "17",
                              "s" : [ {
                                 "r" : "15",
                                 "value" : [ "10"," - ","15" ]
                              } ]
                           }, {
                              "value" : [ ")" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " / ","3" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "20",
               "type" : "Divide",
               "operand" : [ {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "18",
                     "type" : "Multiply",
                     "operand" : [ {
                        "localId" : "14",
                        "type" : "Add",
                        "operand" : [ {
                           "localId" : "12",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "localId" : "13",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        } ]
                     }, {
                        "localId" : "17",
                        "type" : "Subtract",
                        "operand" : [ {
                           "localId" : "15",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "10",
                           "type" : "Literal"
                        }, {
                           "localId" : "16",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "15",
                           "type" : "Literal"
                        } ]
                     } ]
                  }
               }, {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "19",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }
               } ]
            }
         } ]
      }
   }
}

### Power
library TestSnippet version '1'
using QUICK
context Patient
define Pow: 3 ^ 4
###

module.exports['Power'] = {
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
            "name" : "Pow",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","Pow",": " ]
                  }, {
                     "r" : "4",
                     "s" : [ {
                        "r" : "2",
                        "value" : [ "3"," ^ ","4" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "type" : "Power",
               "operand" : [ {
                  "localId" : "2",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "3",
                  "type" : "Literal"
               }, {
                  "localId" : "3",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

### TruncatedDivide
library TestSnippet version '1'
using QUICK
context Patient
define Trunc: 10 div 3
define Even: 9 div 3
###

module.exports['TruncatedDivide'] = {
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
            "name" : "Trunc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","Trunc",": " ]
                  }, {
                     "r" : "4",
                     "s" : [ {
                        "r" : "2",
                        "value" : [ "10"," div ","3" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "type" : "TruncatedDivide",
               "operand" : [ {
                  "localId" : "2",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "10",
                  "type" : "Literal"
               }, {
                  "localId" : "3",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "3",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "9",
            "name" : "Even",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","Even",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "r" : "6",
                        "value" : [ "9"," div ","3" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "TruncatedDivide",
               "operand" : [ {
                  "localId" : "6",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "9",
                  "type" : "Literal"
               }, {
                  "localId" : "7",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "3",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

### Modulo
library TestSnippet version '1'
using QUICK
context Patient
define Mod: 3 mod 2
###

module.exports['Modulo'] = {
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
            "name" : "Mod",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","Mod",": " ]
                  }, {
                     "r" : "4",
                     "s" : [ {
                        "r" : "2",
                        "value" : [ "3"," mod ","2" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "type" : "Modulo",
               "operand" : [ {
                  "localId" : "2",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "3",
                  "type" : "Literal"
               }, {
                  "localId" : "3",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

### Ceiling
library TestSnippet version '1'
using QUICK
context Patient
define Ceil: Ceiling(10.1)
define Even: Ceiling(10)
###

module.exports['Ceiling'] = {
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
            "localId" : "4",
            "name" : "Ceil",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "4",
                  "s" : [ {
                     "value" : [ "define ","Ceil",": " ]
                  }, {
                     "r" : "3",
                     "s" : [ {
                        "value" : [ "Ceiling","(","10.1",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "3",
               "type" : "Ceiling",
               "operand" : {
                  "localId" : "2",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "value" : "10.1",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "7",
            "name" : "Even",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "7",
                  "s" : [ {
                     "value" : [ "define ","Even",": " ]
                  }, {
                     "r" : "6",
                     "s" : [ {
                        "value" : [ "Ceiling","(","10",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "6",
               "type" : "Ceiling",
               "operand" : {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }
               }
            }
         } ]
      }
   }
}

### Floor
library TestSnippet version '1'
using QUICK
context Patient
define flr: Floor(10.1)
define Even: Floor(10)
###

module.exports['Floor'] = {
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
            "localId" : "4",
            "name" : "flr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "4",
                  "s" : [ {
                     "value" : [ "define ","flr",": " ]
                  }, {
                     "r" : "3",
                     "s" : [ {
                        "value" : [ "Floor","(","10.1",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "3",
               "type" : "Floor",
               "operand" : {
                  "localId" : "2",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "value" : "10.1",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "7",
            "name" : "Even",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "7",
                  "s" : [ {
                     "value" : [ "define ","Even",": " ]
                  }, {
                     "r" : "6",
                     "s" : [ {
                        "value" : [ "Floor","(","10",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "6",
               "type" : "Floor",
               "operand" : {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }
               }
            }
         } ]
      }
   }
}

### Truncate
library TestSnippet version '1'
using QUICK
context Patient
define Trunc: Truncate(10.1)
define Even: Truncate(10)
###

module.exports['Truncate'] = {
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
            "localId" : "4",
            "name" : "Trunc",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "4",
                  "s" : [ {
                     "value" : [ "define ","Trunc",": " ]
                  }, {
                     "r" : "3",
                     "s" : [ {
                        "value" : [ "Truncate","(","10.1",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "3",
               "type" : "Truncate",
               "operand" : {
                  "localId" : "2",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "value" : "10.1",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "7",
            "name" : "Even",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "7",
                  "s" : [ {
                     "value" : [ "define ","Even",": " ]
                  }, {
                     "r" : "6",
                     "s" : [ {
                        "value" : [ "Truncate","(","10",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "6",
               "type" : "Truncate",
               "operand" : {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }
               }
            }
         } ]
      }
   }
}

### Abs
library TestSnippet version '1'
using QUICK
context Patient
define Pos: Abs(10)
define Neg: Abs(-10)
define Zero: Abs(0)
###

module.exports['Abs'] = {
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
            "localId" : "4",
            "name" : "Pos",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "4",
                  "s" : [ {
                     "value" : [ "define ","Pos",": " ]
                  }, {
                     "r" : "3",
                     "s" : [ {
                        "value" : [ "Abs","(","10",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "3",
               "type" : "Abs",
               "operand" : {
                  "localId" : "2",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "10",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "8",
            "name" : "Neg",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "8",
                  "s" : [ {
                     "value" : [ "define ","Neg",": " ]
                  }, {
                     "r" : "7",
                     "s" : [ {
                        "value" : [ "Abs","(" ]
                     }, {
                        "r" : "6",
                        "s" : [ {
                           "value" : [ "-","10" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "7",
               "type" : "Abs",
               "operand" : {
                  "localId" : "6",
                  "type" : "Negate",
                  "operand" : {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "localId" : "11",
            "name" : "Zero",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","Zero",": " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "value" : [ "Abs","(","0",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "type" : "Abs",
               "operand" : {
                  "localId" : "9",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "0",
                  "type" : "Literal"
               }
            }
         } ]
      }
   }
}

### Round
library TestSnippet version '1'
using QUICK
context Patient
define Up: Round(4.56)
define Up_percent: Round(4.56,1)
define Down: Round(4.49)
define Down_percent: Round(4.43,1)
###

module.exports['Round'] = {
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
            "localId" : "4",
            "name" : "Up",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "4",
                  "s" : [ {
                     "value" : [ "define ","Up",": " ]
                  }, {
                     "r" : "3",
                     "s" : [ {
                        "value" : [ "Round","(","4.56",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "3",
               "type" : "Round",
               "operand" : {
                  "localId" : "2",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "value" : "4.56",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "8",
            "name" : "Up_percent",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "8",
                  "s" : [ {
                     "value" : [ "define ","Up_percent",": " ]
                  }, {
                     "r" : "7",
                     "s" : [ {
                        "value" : [ "Round","(","4.56",",","1",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "7",
               "type" : "Round",
               "operand" : {
                  "localId" : "5",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "value" : "4.56",
                  "type" : "Literal"
               },
               "precision" : {
                  "localId" : "6",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "11",
            "name" : "Down",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","Down",": " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "value" : [ "Round","(","4.49",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "type" : "Round",
               "operand" : {
                  "localId" : "9",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "value" : "4.49",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "15",
            "name" : "Down_percent",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "15",
                  "s" : [ {
                     "value" : [ "define ","Down_percent",": " ]
                  }, {
                     "r" : "14",
                     "s" : [ {
                        "value" : [ "Round","(","4.43",",","1",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "14",
               "type" : "Round",
               "operand" : {
                  "localId" : "12",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "value" : "4.43",
                  "type" : "Literal"
               },
               "precision" : {
                  "localId" : "13",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               }
            }
         } ]
      }
   }
}

### Ln
library TestSnippet version '1'
using QUICK
context Patient
define ln: Ln(4)
###

module.exports['Ln'] = {
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
            "localId" : "4",
            "name" : "ln",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "4",
                  "s" : [ {
                     "value" : [ "define ","ln",": " ]
                  }, {
                     "r" : "3",
                     "s" : [ {
                        "value" : [ "Ln","(","4",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "3",
               "type" : "Ln",
               "operand" : {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "2",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }
               }
            }
         } ]
      }
   }
}

### Log
library TestSnippet version '1'
using QUICK
context Patient
define log: Log(10,10000)
###

module.exports['Log'] = {
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
            "name" : "log",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","log",": " ]
                  }, {
                     "r" : "4",
                     "s" : [ {
                        "value" : [ "Log","(","10",",","10000",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "type" : "Log",
               "operand" : [ {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "2",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }
               }, {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "3",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10000",
                     "type" : "Literal"
                  }
               } ]
            }
         } ]
      }
   }
}

### Successor
library TestSnippet version '1'
using QUICK
context Patient
define Is: successor of 2
define Rs: successor of 2.2
define ofr: successor of 2147483647
define y_date: successor of DateTime(2015)
define ym_date: successor of DateTime(2015,01)
define ymd_date: successor of DateTime(2015,01,01)
define ymdh_date: successor of DateTime(2015,01,01,0)
define ymdhm_date: successor of DateTime(2015,01,01,0,0)
define ymdhms_date: successor of DateTime(2015,01,01,0,0,0)
define ymdhmsm_date: successor of  DateTime(2015,01,01,0,0,0,0)
define max_date: successor of DateTime(9999,12,31,23,59,59,999)
###

module.exports['Successor'] = {
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
            "localId" : "4",
            "name" : "Is",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "4",
                  "s" : [ {
                     "value" : [ "define ","Is",": " ]
                  }, {
                     "r" : "3",
                     "s" : [ {
                        "value" : [ "successor of ","2" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "3",
               "type" : "Successor",
               "operand" : {
                  "localId" : "2",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "7",
            "name" : "Rs",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "7",
                  "s" : [ {
                     "value" : [ "define ","Rs",": " ]
                  }, {
                     "r" : "6",
                     "s" : [ {
                        "value" : [ "successor of ","2.2" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "6",
               "type" : "Successor",
               "operand" : {
                  "localId" : "5",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "value" : "2.2",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "10",
            "name" : "ofr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "10",
                  "s" : [ {
                     "value" : [ "define ","ofr",": " ]
                  }, {
                     "r" : "9",
                     "s" : [ {
                        "value" : [ "successor of ","2147483647" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "9",
               "type" : "Successor",
               "operand" : {
                  "localId" : "8",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2147483647",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "14",
            "name" : "y_date",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "14",
                  "s" : [ {
                     "value" : [ "define ","y_date",": " ]
                  }, {
                     "r" : "13",
                     "s" : [ {
                        "value" : [ "successor of " ]
                     }, {
                        "r" : "12",
                        "s" : [ {
                           "value" : [ "DateTime","(","2015",")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "13",
               "type" : "Successor",
               "operand" : {
                  "localId" : "12",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "11",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "localId" : "19",
            "name" : "ym_date",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "19",
                  "s" : [ {
                     "value" : [ "define ","ym_date",": " ]
                  }, {
                     "r" : "18",
                     "s" : [ {
                        "value" : [ "successor of " ]
                     }, {
                        "r" : "17",
                        "s" : [ {
                           "value" : [ "DateTime","(","2015",",","01",")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "18",
               "type" : "Successor",
               "operand" : {
                  "localId" : "17",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "15",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "16",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "localId" : "25",
            "name" : "ymd_date",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "25",
                  "s" : [ {
                     "value" : [ "define ","ymd_date",": " ]
                  }, {
                     "r" : "24",
                     "s" : [ {
                        "value" : [ "successor of " ]
                     }, {
                        "r" : "23",
                        "s" : [ {
                           "value" : [ "DateTime","(","2015",",","01",",","01",")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "24",
               "type" : "Successor",
               "operand" : {
                  "localId" : "23",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "20",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "21",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "22",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "localId" : "32",
            "name" : "ymdh_date",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "32",
                  "s" : [ {
                     "value" : [ "define ","ymdh_date",": " ]
                  }, {
                     "r" : "31",
                     "s" : [ {
                        "value" : [ "successor of " ]
                     }, {
                        "r" : "30",
                        "s" : [ {
                           "value" : [ "DateTime","(","2015",",","01",",","01",",","0",")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "31",
               "type" : "Successor",
               "operand" : {
                  "localId" : "30",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "26",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "27",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "28",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "localId" : "29",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "localId" : "40",
            "name" : "ymdhm_date",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "40",
                  "s" : [ {
                     "value" : [ "define ","ymdhm_date",": " ]
                  }, {
                     "r" : "39",
                     "s" : [ {
                        "value" : [ "successor of " ]
                     }, {
                        "r" : "38",
                        "s" : [ {
                           "value" : [ "DateTime","(","2015",",","01",",","01",",","0",",","0",")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "39",
               "type" : "Successor",
               "operand" : {
                  "localId" : "38",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "33",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "34",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "35",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "localId" : "36",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "localId" : "37",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "localId" : "49",
            "name" : "ymdhms_date",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "49",
                  "s" : [ {
                     "value" : [ "define ","ymdhms_date",": " ]
                  }, {
                     "r" : "48",
                     "s" : [ {
                        "value" : [ "successor of " ]
                     }, {
                        "r" : "47",
                        "s" : [ {
                           "value" : [ "DateTime","(","2015",",","01",",","01",",","0",",","0",",","0",")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "48",
               "type" : "Successor",
               "operand" : {
                  "localId" : "47",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "41",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "42",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "43",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "localId" : "44",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "localId" : "45",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  },
                  "second" : {
                     "localId" : "46",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "localId" : "59",
            "name" : "ymdhmsm_date",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "59",
                  "s" : [ {
                     "value" : [ "define ","ymdhmsm_date",": " ]
                  }, {
                     "r" : "58",
                     "s" : [ {
                        "value" : [ "successor of  " ]
                     }, {
                        "r" : "57",
                        "s" : [ {
                           "value" : [ "DateTime","(","2015",",","01",",","01",",","0",",","0",",","0",",","0",")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "58",
               "type" : "Successor",
               "operand" : {
                  "localId" : "57",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "50",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "51",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "52",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "localId" : "53",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "localId" : "54",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  },
                  "second" : {
                     "localId" : "55",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  },
                  "millisecond" : {
                     "localId" : "56",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "localId" : "69",
            "name" : "max_date",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "69",
                  "s" : [ {
                     "value" : [ "define ","max_date",": " ]
                  }, {
                     "r" : "68",
                     "s" : [ {
                        "value" : [ "successor of " ]
                     }, {
                        "r" : "67",
                        "s" : [ {
                           "value" : [ "DateTime","(","9999",",","12",",","31",",","23",",","59",",","59",",","999",")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "68",
               "type" : "Successor",
               "operand" : {
                  "localId" : "67",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "60",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "9999",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "61",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "12",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "62",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "31",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "localId" : "63",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "23",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "localId" : "64",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "59",
                     "type" : "Literal"
                  },
                  "second" : {
                     "localId" : "65",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "59",
                     "type" : "Literal"
                  },
                  "millisecond" : {
                     "localId" : "66",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "999",
                     "type" : "Literal"
                  }
               }
            }
         } ]
      }
   }
}

### Predecessor
library TestSnippet version '1'
using QUICK
context Patient
define Is: predecessor of 2
define Rs: predecessor of 2.2
define ufr: predecessor of -2147483648
define y_date: predecessor of DateTime(2015)
define ym_date: predecessor of DateTime(2015,01)
define ymd_date: predecessor of DateTime(2015,01,01)
define ymdh_date: predecessor of DateTime(2015,01,01,0)
define ymdhm_date: predecessor of DateTime(2015,01,01,0,0)
define ymdhms_date: predecessor of DateTime(2015,01,01,0,0,0)
define ymdhmsm_date: predecessor of DateTime(2015,01,01,0,0,0,0)
define min_date: predecessor of DateTime(0001,01,01,0,0,0,0)
###

module.exports['Predecessor'] = {
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
            "localId" : "4",
            "name" : "Is",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "4",
                  "s" : [ {
                     "value" : [ "define ","Is",": " ]
                  }, {
                     "r" : "3",
                     "s" : [ {
                        "value" : [ "predecessor of ","2" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "3",
               "type" : "Predecessor",
               "operand" : {
                  "localId" : "2",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "7",
            "name" : "Rs",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "7",
                  "s" : [ {
                     "value" : [ "define ","Rs",": " ]
                  }, {
                     "r" : "6",
                     "s" : [ {
                        "value" : [ "predecessor of ","2.2" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "6",
               "type" : "Predecessor",
               "operand" : {
                  "localId" : "5",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "value" : "2.2",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "11",
            "name" : "ufr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","ufr",": " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "value" : [ "predecessor of " ]
                     }, {
                        "r" : "9",
                        "s" : [ {
                           "value" : [ "-","2147483648" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "type" : "Predecessor",
               "operand" : {
                  "localId" : "9",
                  "type" : "Negate",
                  "operand" : {
                     "localId" : "8",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2147483648",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "localId" : "15",
            "name" : "y_date",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "15",
                  "s" : [ {
                     "value" : [ "define ","y_date",": " ]
                  }, {
                     "r" : "14",
                     "s" : [ {
                        "value" : [ "predecessor of " ]
                     }, {
                        "r" : "13",
                        "s" : [ {
                           "value" : [ "DateTime","(","2015",")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "14",
               "type" : "Predecessor",
               "operand" : {
                  "localId" : "13",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "12",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "localId" : "20",
            "name" : "ym_date",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "20",
                  "s" : [ {
                     "value" : [ "define ","ym_date",": " ]
                  }, {
                     "r" : "19",
                     "s" : [ {
                        "value" : [ "predecessor of " ]
                     }, {
                        "r" : "18",
                        "s" : [ {
                           "value" : [ "DateTime","(","2015",",","01",")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "19",
               "type" : "Predecessor",
               "operand" : {
                  "localId" : "18",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "16",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "17",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "localId" : "26",
            "name" : "ymd_date",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "26",
                  "s" : [ {
                     "value" : [ "define ","ymd_date",": " ]
                  }, {
                     "r" : "25",
                     "s" : [ {
                        "value" : [ "predecessor of " ]
                     }, {
                        "r" : "24",
                        "s" : [ {
                           "value" : [ "DateTime","(","2015",",","01",",","01",")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "25",
               "type" : "Predecessor",
               "operand" : {
                  "localId" : "24",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "21",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "22",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "23",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "localId" : "33",
            "name" : "ymdh_date",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "33",
                  "s" : [ {
                     "value" : [ "define ","ymdh_date",": " ]
                  }, {
                     "r" : "32",
                     "s" : [ {
                        "value" : [ "predecessor of " ]
                     }, {
                        "r" : "31",
                        "s" : [ {
                           "value" : [ "DateTime","(","2015",",","01",",","01",",","0",")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "32",
               "type" : "Predecessor",
               "operand" : {
                  "localId" : "31",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "27",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "28",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "29",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "localId" : "30",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "localId" : "41",
            "name" : "ymdhm_date",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "41",
                  "s" : [ {
                     "value" : [ "define ","ymdhm_date",": " ]
                  }, {
                     "r" : "40",
                     "s" : [ {
                        "value" : [ "predecessor of " ]
                     }, {
                        "r" : "39",
                        "s" : [ {
                           "value" : [ "DateTime","(","2015",",","01",",","01",",","0",",","0",")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "40",
               "type" : "Predecessor",
               "operand" : {
                  "localId" : "39",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "34",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "35",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "36",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "localId" : "37",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "localId" : "38",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "localId" : "50",
            "name" : "ymdhms_date",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "50",
                  "s" : [ {
                     "value" : [ "define ","ymdhms_date",": " ]
                  }, {
                     "r" : "49",
                     "s" : [ {
                        "value" : [ "predecessor of " ]
                     }, {
                        "r" : "48",
                        "s" : [ {
                           "value" : [ "DateTime","(","2015",",","01",",","01",",","0",",","0",",","0",")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "49",
               "type" : "Predecessor",
               "operand" : {
                  "localId" : "48",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "42",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "43",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "44",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "localId" : "45",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "localId" : "46",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  },
                  "second" : {
                     "localId" : "47",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "localId" : "60",
            "name" : "ymdhmsm_date",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "60",
                  "s" : [ {
                     "value" : [ "define ","ymdhmsm_date",": " ]
                  }, {
                     "r" : "59",
                     "s" : [ {
                        "value" : [ "predecessor of " ]
                     }, {
                        "r" : "58",
                        "s" : [ {
                           "value" : [ "DateTime","(","2015",",","01",",","01",",","0",",","0",",","0",",","0",")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "59",
               "type" : "Predecessor",
               "operand" : {
                  "localId" : "58",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "51",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "52",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "53",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "localId" : "54",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "localId" : "55",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  },
                  "second" : {
                     "localId" : "56",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  },
                  "millisecond" : {
                     "localId" : "57",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "localId" : "70",
            "name" : "min_date",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "70",
                  "s" : [ {
                     "value" : [ "define ","min_date",": " ]
                  }, {
                     "r" : "69",
                     "s" : [ {
                        "value" : [ "predecessor of " ]
                     }, {
                        "r" : "68",
                        "s" : [ {
                           "value" : [ "DateTime","(","0001",",","01",",","01",",","0",",","0",",","0",",","0",")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "69",
               "type" : "Predecessor",
               "operand" : {
                  "localId" : "68",
                  "type" : "DateTime",
                  "year" : {
                     "localId" : "61",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0001",
                     "type" : "Literal"
                  },
                  "month" : {
                     "localId" : "62",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "day" : {
                     "localId" : "63",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "01",
                     "type" : "Literal"
                  },
                  "hour" : {
                     "localId" : "64",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "localId" : "65",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  },
                  "second" : {
                     "localId" : "66",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  },
                  "millisecond" : {
                     "localId" : "67",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }
               }
            }
         } ]
      }
   }
}

### Quantity
library TestSnippet version '1'
using QUICK
context Patient

define days_10: 10 days
define QL10Days: Quantity{value: 10, unit: 'days'}
define QL10Min : Quantity{value: 10, unit: 'min' }
define Jan1_2000: DateTime(2000, 1, 1)
define add_q_q : days_10 + QL10Days
define add_d_q : Jan1_2000 + days_10
define sub_q_q : days_10 - QL10Days
define sub_d_q : Jan1_2000 - days_10
define add_q_q_diff : QL10Days + QL10Min
define sub_q_q_diff : QL10Days - QL10Min
define div_q_d : days_10 / 2
define div_q_q : days_10 / QL10Days
define mul_q_d : days_10 * 2
define mul_d_q : 2 * QL10Days
define mul_q_q : 2 'm' * 10 'm'
define mul_q_q_diff : 2 'm' * 10 '/d'
define neg : - days_10
define abs : Abs(neg)
define MultiplyUcum: (5 'm' * 25 'km') = 125000 'm2'
define DivideUcum: (20 'm2' / 5 'm') = 4 'm'
define AddUcum: (5 'm' + 5 'km') = 5005 'm'
define SubtractUcum: (25 'km' - 5 'm') = 24995 'm'
###

module.exports['Quantity'] = {
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
            "localId" : "3",
            "name" : "days_10",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "3",
                  "s" : [ {
                     "value" : [ "define ","days_10",": " ]
                  }, {
                     "r" : "2",
                     "s" : [ {
                        "value" : [ "10 ","days" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "2",
               "value" : 10,
               "unit" : "days",
               "type" : "Quantity"
            }
         }, {
            "localId" : "7",
            "name" : "QL10Days",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "7",
                  "s" : [ {
                     "value" : [ "define ","QL10Days",": " ]
                  }, {
                     "r" : "6",
                     "s" : [ {
                        "value" : [ "Quantity","{" ]
                     }, {
                        "s" : [ {
                           "value" : [ "value",": ","10" ]
                        } ]
                     }, {
                        "value" : [ ", " ]
                     }, {
                        "s" : [ {
                           "value" : [ "unit",": " ]
                        }, {
                           "r" : "5",
                           "s" : [ {
                              "value" : [ "'days'" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ "}" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "6",
               "classType" : "{urn:hl7-org:elm-types:r1}Quantity",
               "type" : "Instance",
               "element" : [ {
                  "name" : "value",
                  "value" : {
                     "type" : "ToDecimal",
                     "operand" : {
                        "localId" : "4",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "10",
                        "type" : "Literal"
                     }
                  }
               }, {
                  "name" : "unit",
                  "value" : {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "days",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "localId" : "11",
            "name" : "QL10Min",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","QL10Min"," : " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "value" : [ "Quantity","{" ]
                     }, {
                        "s" : [ {
                           "value" : [ "value",": ","10" ]
                        } ]
                     }, {
                        "value" : [ ", " ]
                     }, {
                        "s" : [ {
                           "value" : [ "unit",": " ]
                        }, {
                           "r" : "9",
                           "s" : [ {
                              "value" : [ "'min'" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " }" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "classType" : "{urn:hl7-org:elm-types:r1}Quantity",
               "type" : "Instance",
               "element" : [ {
                  "name" : "value",
                  "value" : {
                     "type" : "ToDecimal",
                     "operand" : {
                        "localId" : "8",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "10",
                        "type" : "Literal"
                     }
                  }
               }, {
                  "name" : "unit",
                  "value" : {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "min",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "localId" : "16",
            "name" : "Jan1_2000",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "16",
                  "s" : [ {
                     "value" : [ "define ","Jan1_2000",": " ]
                  }, {
                     "r" : "15",
                     "s" : [ {
                        "value" : [ "DateTime","(","2000",", ","1",", ","1",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "15",
               "type" : "DateTime",
               "year" : {
                  "localId" : "12",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2000",
                  "type" : "Literal"
               },
               "month" : {
                  "localId" : "13",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               },
               "day" : {
                  "localId" : "14",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "20",
            "name" : "add_q_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "20",
                  "s" : [ {
                     "value" : [ "define ","add_q_q"," : " ]
                  }, {
                     "r" : "19",
                     "s" : [ {
                        "r" : "17",
                        "s" : [ {
                           "value" : [ "days_10" ]
                        } ]
                     }, {
                        "value" : [ " + " ]
                     }, {
                        "r" : "18",
                        "s" : [ {
                           "value" : [ "QL10Days" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "19",
               "type" : "Add",
               "operand" : [ {
                  "localId" : "17",
                  "name" : "days_10",
                  "type" : "ExpressionRef"
               }, {
                  "localId" : "18",
                  "name" : "QL10Days",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "localId" : "24",
            "name" : "add_d_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "24",
                  "s" : [ {
                     "value" : [ "define ","add_d_q"," : " ]
                  }, {
                     "r" : "23",
                     "s" : [ {
                        "r" : "21",
                        "s" : [ {
                           "value" : [ "Jan1_2000" ]
                        } ]
                     }, {
                        "value" : [ " + " ]
                     }, {
                        "r" : "22",
                        "s" : [ {
                           "value" : [ "days_10" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "23",
               "type" : "Add",
               "operand" : [ {
                  "localId" : "21",
                  "name" : "Jan1_2000",
                  "type" : "ExpressionRef"
               }, {
                  "localId" : "22",
                  "name" : "days_10",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "localId" : "28",
            "name" : "sub_q_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "28",
                  "s" : [ {
                     "value" : [ "define ","sub_q_q"," : " ]
                  }, {
                     "r" : "27",
                     "s" : [ {
                        "r" : "25",
                        "s" : [ {
                           "value" : [ "days_10" ]
                        } ]
                     }, {
                        "value" : [ " - " ]
                     }, {
                        "r" : "26",
                        "s" : [ {
                           "value" : [ "QL10Days" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "27",
               "type" : "Subtract",
               "operand" : [ {
                  "localId" : "25",
                  "name" : "days_10",
                  "type" : "ExpressionRef"
               }, {
                  "localId" : "26",
                  "name" : "QL10Days",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "localId" : "32",
            "name" : "sub_d_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "32",
                  "s" : [ {
                     "value" : [ "define ","sub_d_q"," : " ]
                  }, {
                     "r" : "31",
                     "s" : [ {
                        "r" : "29",
                        "s" : [ {
                           "value" : [ "Jan1_2000" ]
                        } ]
                     }, {
                        "value" : [ " - " ]
                     }, {
                        "r" : "30",
                        "s" : [ {
                           "value" : [ "days_10" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "31",
               "type" : "Subtract",
               "operand" : [ {
                  "localId" : "29",
                  "name" : "Jan1_2000",
                  "type" : "ExpressionRef"
               }, {
                  "localId" : "30",
                  "name" : "days_10",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "localId" : "36",
            "name" : "add_q_q_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "36",
                  "s" : [ {
                     "value" : [ "define ","add_q_q_diff"," : " ]
                  }, {
                     "r" : "35",
                     "s" : [ {
                        "r" : "33",
                        "s" : [ {
                           "value" : [ "QL10Days" ]
                        } ]
                     }, {
                        "value" : [ " + " ]
                     }, {
                        "r" : "34",
                        "s" : [ {
                           "value" : [ "QL10Min" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "35",
               "type" : "Add",
               "operand" : [ {
                  "localId" : "33",
                  "name" : "QL10Days",
                  "type" : "ExpressionRef"
               }, {
                  "localId" : "34",
                  "name" : "QL10Min",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "localId" : "40",
            "name" : "sub_q_q_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "40",
                  "s" : [ {
                     "value" : [ "define ","sub_q_q_diff"," : " ]
                  }, {
                     "r" : "39",
                     "s" : [ {
                        "r" : "37",
                        "s" : [ {
                           "value" : [ "QL10Days" ]
                        } ]
                     }, {
                        "value" : [ " - " ]
                     }, {
                        "r" : "38",
                        "s" : [ {
                           "value" : [ "QL10Min" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "39",
               "type" : "Subtract",
               "operand" : [ {
                  "localId" : "37",
                  "name" : "QL10Days",
                  "type" : "ExpressionRef"
               }, {
                  "localId" : "38",
                  "name" : "QL10Min",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "localId" : "44",
            "name" : "div_q_d",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "44",
                  "s" : [ {
                     "value" : [ "define ","div_q_d"," : " ]
                  }, {
                     "r" : "43",
                     "s" : [ {
                        "r" : "41",
                        "s" : [ {
                           "value" : [ "days_10" ]
                        } ]
                     }, {
                        "value" : [ " / ","2" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "43",
               "type" : "Divide",
               "operand" : [ {
                  "localId" : "41",
                  "name" : "days_10",
                  "type" : "ExpressionRef"
               }, {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "42",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "localId" : "48",
            "name" : "div_q_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "48",
                  "s" : [ {
                     "value" : [ "define ","div_q_q"," : " ]
                  }, {
                     "r" : "47",
                     "s" : [ {
                        "r" : "45",
                        "s" : [ {
                           "value" : [ "days_10" ]
                        } ]
                     }, {
                        "value" : [ " / " ]
                     }, {
                        "r" : "46",
                        "s" : [ {
                           "value" : [ "QL10Days" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "47",
               "type" : "Divide",
               "operand" : [ {
                  "localId" : "45",
                  "name" : "days_10",
                  "type" : "ExpressionRef"
               }, {
                  "localId" : "46",
                  "name" : "QL10Days",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "localId" : "52",
            "name" : "mul_q_d",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "52",
                  "s" : [ {
                     "value" : [ "define ","mul_q_d"," : " ]
                  }, {
                     "r" : "51",
                     "s" : [ {
                        "r" : "49",
                        "s" : [ {
                           "value" : [ "days_10" ]
                        } ]
                     }, {
                        "value" : [ " * ","2" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "51",
               "type" : "Multiply",
               "operand" : [ {
                  "localId" : "49",
                  "name" : "days_10",
                  "type" : "ExpressionRef"
               }, {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "50",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "localId" : "56",
            "name" : "mul_d_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "56",
                  "s" : [ {
                     "value" : [ "define ","mul_d_q"," : " ]
                  }, {
                     "r" : "55",
                     "s" : [ {
                        "r" : "53",
                        "value" : [ "2"," * " ]
                     }, {
                        "r" : "54",
                        "s" : [ {
                           "value" : [ "QL10Days" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "55",
               "type" : "Multiply",
               "operand" : [ {
                  "type" : "ToDecimal",
                  "operand" : {
                     "localId" : "53",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }
               }, {
                  "localId" : "54",
                  "name" : "QL10Days",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "localId" : "60",
            "name" : "mul_q_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "60",
                  "s" : [ {
                     "value" : [ "define ","mul_q_q"," : " ]
                  }, {
                     "r" : "59",
                     "s" : [ {
                        "r" : "57",
                        "s" : [ {
                           "value" : [ "2 ","'m'" ]
                        } ]
                     }, {
                        "value" : [ " * " ]
                     }, {
                        "r" : "58",
                        "s" : [ {
                           "value" : [ "10 ","'m'" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "59",
               "type" : "Multiply",
               "operand" : [ {
                  "localId" : "57",
                  "value" : 2,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "localId" : "58",
                  "value" : 10,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "localId" : "64",
            "name" : "mul_q_q_diff",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "64",
                  "s" : [ {
                     "value" : [ "define ","mul_q_q_diff"," : " ]
                  }, {
                     "r" : "63",
                     "s" : [ {
                        "r" : "61",
                        "s" : [ {
                           "value" : [ "2 ","'m'" ]
                        } ]
                     }, {
                        "value" : [ " * " ]
                     }, {
                        "r" : "62",
                        "s" : [ {
                           "value" : [ "10 ","'/d'" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "63",
               "type" : "Multiply",
               "operand" : [ {
                  "localId" : "61",
                  "value" : 2,
                  "unit" : "m",
                  "type" : "Quantity"
               }, {
                  "localId" : "62",
                  "value" : 10,
                  "unit" : "/d",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "localId" : "67",
            "name" : "neg",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "67",
                  "s" : [ {
                     "value" : [ "define ","neg"," : " ]
                  }, {
                     "r" : "66",
                     "s" : [ {
                        "value" : [ "- " ]
                     }, {
                        "r" : "65",
                        "s" : [ {
                           "value" : [ "days_10" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "66",
               "type" : "Negate",
               "operand" : {
                  "localId" : "65",
                  "name" : "days_10",
                  "type" : "ExpressionRef"
               }
            }
         }, {
            "localId" : "70",
            "name" : "abs",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "70",
                  "s" : [ {
                     "value" : [ "define ","abs"," : " ]
                  }, {
                     "r" : "69",
                     "s" : [ {
                        "value" : [ "Abs","(" ]
                     }, {
                        "r" : "68",
                        "s" : [ {
                           "value" : [ "neg" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "69",
               "type" : "Abs",
               "operand" : {
                  "localId" : "68",
                  "name" : "neg",
                  "type" : "ExpressionRef"
               }
            }
         }, {
            "localId" : "76",
            "name" : "MultiplyUcum",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "76",
                  "s" : [ {
                     "value" : [ "define ","MultiplyUcum",": " ]
                  }, {
                     "r" : "75",
                     "s" : [ {
                        "r" : "73",
                        "s" : [ {
                           "value" : [ "(" ]
                        }, {
                           "r" : "73",
                           "s" : [ {
                              "r" : "71",
                              "s" : [ {
                                 "value" : [ "5 ","'m'" ]
                              } ]
                           }, {
                              "value" : [ " * " ]
                           }, {
                              "r" : "72",
                              "s" : [ {
                                 "value" : [ "25 ","'km'" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ ")" ]
                        } ]
                     }, {
                        "value" : [ " ","="," " ]
                     }, {
                        "r" : "74",
                        "s" : [ {
                           "value" : [ "125000 ","'m2'" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "75",
               "type" : "Equal",
               "operand" : [ {
                  "localId" : "73",
                  "type" : "Multiply",
                  "operand" : [ {
                     "localId" : "71",
                     "value" : 5,
                     "unit" : "m",
                     "type" : "Quantity"
                  }, {
                     "localId" : "72",
                     "value" : 25,
                     "unit" : "km",
                     "type" : "Quantity"
                  } ]
               }, {
                  "localId" : "74",
                  "value" : 125000,
                  "unit" : "m2",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "localId" : "82",
            "name" : "DivideUcum",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "82",
                  "s" : [ {
                     "value" : [ "define ","DivideUcum",": " ]
                  }, {
                     "r" : "81",
                     "s" : [ {
                        "r" : "79",
                        "s" : [ {
                           "value" : [ "(" ]
                        }, {
                           "r" : "79",
                           "s" : [ {
                              "r" : "77",
                              "s" : [ {
                                 "value" : [ "20 ","'m2'" ]
                              } ]
                           }, {
                              "value" : [ " / " ]
                           }, {
                              "r" : "78",
                              "s" : [ {
                                 "value" : [ "5 ","'m'" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ ")" ]
                        } ]
                     }, {
                        "value" : [ " ","="," " ]
                     }, {
                        "r" : "80",
                        "s" : [ {
                           "value" : [ "4 ","'m'" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "81",
               "type" : "Equal",
               "operand" : [ {
                  "localId" : "79",
                  "type" : "Divide",
                  "operand" : [ {
                     "localId" : "77",
                     "value" : 20,
                     "unit" : "m2",
                     "type" : "Quantity"
                  }, {
                     "localId" : "78",
                     "value" : 5,
                     "unit" : "m",
                     "type" : "Quantity"
                  } ]
               }, {
                  "localId" : "80",
                  "value" : 4,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "localId" : "88",
            "name" : "AddUcum",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "88",
                  "s" : [ {
                     "value" : [ "define ","AddUcum",": " ]
                  }, {
                     "r" : "87",
                     "s" : [ {
                        "r" : "85",
                        "s" : [ {
                           "value" : [ "(" ]
                        }, {
                           "r" : "85",
                           "s" : [ {
                              "r" : "83",
                              "s" : [ {
                                 "value" : [ "5 ","'m'" ]
                              } ]
                           }, {
                              "value" : [ " + " ]
                           }, {
                              "r" : "84",
                              "s" : [ {
                                 "value" : [ "5 ","'km'" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ ")" ]
                        } ]
                     }, {
                        "value" : [ " ","="," " ]
                     }, {
                        "r" : "86",
                        "s" : [ {
                           "value" : [ "5005 ","'m'" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "87",
               "type" : "Equal",
               "operand" : [ {
                  "localId" : "85",
                  "type" : "Add",
                  "operand" : [ {
                     "localId" : "83",
                     "value" : 5,
                     "unit" : "m",
                     "type" : "Quantity"
                  }, {
                     "localId" : "84",
                     "value" : 5,
                     "unit" : "km",
                     "type" : "Quantity"
                  } ]
               }, {
                  "localId" : "86",
                  "value" : 5005,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         }, {
            "localId" : "94",
            "name" : "SubtractUcum",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "94",
                  "s" : [ {
                     "value" : [ "define ","SubtractUcum",": " ]
                  }, {
                     "r" : "93",
                     "s" : [ {
                        "r" : "91",
                        "s" : [ {
                           "value" : [ "(" ]
                        }, {
                           "r" : "91",
                           "s" : [ {
                              "r" : "89",
                              "s" : [ {
                                 "value" : [ "25 ","'km'" ]
                              } ]
                           }, {
                              "value" : [ " - " ]
                           }, {
                              "r" : "90",
                              "s" : [ {
                                 "value" : [ "5 ","'m'" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ ")" ]
                        } ]
                     }, {
                        "value" : [ " ","="," " ]
                     }, {
                        "r" : "92",
                        "s" : [ {
                           "value" : [ "24995 ","'m'" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "93",
               "type" : "Equal",
               "operand" : [ {
                  "localId" : "91",
                  "type" : "Subtract",
                  "operand" : [ {
                     "localId" : "89",
                     "value" : 25,
                     "unit" : "km",
                     "type" : "Quantity"
                  }, {
                     "localId" : "90",
                     "value" : 5,
                     "unit" : "m",
                     "type" : "Quantity"
                  } ]
               }, {
                  "localId" : "92",
                  "value" : 24995,
                  "unit" : "m",
                  "type" : "Quantity"
               } ]
            }
         } ]
      }
   }
}

