###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.cql to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### Count
library TestSnippet version '1'
using QUICK
context Patient
define not_null: Count({1,2,3,4,5})
define has_null: Count({1,null,null,null,2})
define empty: Count({})
###

module.exports['Count'] = {
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
            "localId" : "9",
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","not_null",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "Count","(" ]
                     }, {
                        "r" : "7",
                        "s" : [ {
                           "value" : [ "{","1",",","2",",","3",",","4",",","5","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "Count",
               "source" : {
                  "localId" : "7",
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
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "6",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "17",
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "17",
                  "s" : [ {
                     "value" : [ "define ","has_null",": " ]
                  }, {
                     "r" : "16",
                     "s" : [ {
                        "value" : [ "Count","(" ]
                     }, {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "{","1",",","null",",","null",",","null",",","2","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "16",
               "type" : "Count",
               "source" : {
                  "localId" : "15",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "10",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "localId" : "11",
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "localId" : "12",
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "localId" : "13",
                        "type" : "Null"
                     }
                  }, {
                     "localId" : "14",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "20",
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "20",
                  "s" : [ {
                     "value" : [ "define ","empty",": " ]
                  }, {
                     "r" : "19",
                     "s" : [ {
                        "value" : [ "Count","(","{}",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "19",
               "type" : "Count",
               "source" : {
                  "localId" : "18",
                  "type" : "List"
               }
            }
         } ]
      }
   }
}

### Sum
library TestSnippet version '1'
using QUICK
context Patient
define not_null: Sum({1,2,3,4,5})
define has_null: Sum({1,null,null,null,2})
define not_null_q: Sum({1 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
define has_null_q: Sum({1 'ml',null,null,null,2 'ml'})
define unmatched_units_q: Min({1 'ml',2 'L',3 'ml',4 'ml',5 'ml',0 'ml'})
define empty: Sum(List<Integer>{})
define q_diff_units: Sum({1 'ml',0.002 'l',0.03 'dl',4 'ml',0.005 'l'})
###

module.exports['Sum'] = {
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
            "localId" : "9",
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","not_null",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "Sum","(" ]
                     }, {
                        "r" : "7",
                        "s" : [ {
                           "value" : [ "{","1",",","2",",","3",",","4",",","5","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "Sum",
               "source" : {
                  "localId" : "7",
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
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "6",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "17",
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "17",
                  "s" : [ {
                     "value" : [ "define ","has_null",": " ]
                  }, {
                     "r" : "16",
                     "s" : [ {
                        "value" : [ "Sum","(" ]
                     }, {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "{","1",",","null",",","null",",","null",",","2","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "16",
               "type" : "Sum",
               "source" : {
                  "localId" : "15",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "10",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "localId" : "11",
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "localId" : "12",
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "localId" : "13",
                        "type" : "Null"
                     }
                  }, {
                     "localId" : "14",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "25",
            "name" : "not_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "25",
                  "s" : [ {
                     "value" : [ "define ","not_null_q",": " ]
                  }, {
                     "r" : "24",
                     "s" : [ {
                        "value" : [ "Sum","(" ]
                     }, {
                        "r" : "23",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "18",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "19",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "20",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "21",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "22",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "24",
               "type" : "Sum",
               "source" : {
                  "localId" : "23",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "18",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "19",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "20",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "21",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "22",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "33",
            "name" : "has_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "33",
                  "s" : [ {
                     "value" : [ "define ","has_null_q",": " ]
                  }, {
                     "r" : "32",
                     "s" : [ {
                        "value" : [ "Sum","(" ]
                     }, {
                        "r" : "31",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "26",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ ",","null",",","null",",","null","," ]
                        }, {
                           "r" : "30",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "32",
               "type" : "Sum",
               "source" : {
                  "localId" : "31",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "26",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "localId" : "27",
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "localId" : "28",
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "localId" : "29",
                        "type" : "Null"
                     }
                  }, {
                     "localId" : "30",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "42",
            "name" : "unmatched_units_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "42",
                  "s" : [ {
                     "value" : [ "define ","unmatched_units_q",": " ]
                  }, {
                     "r" : "41",
                     "s" : [ {
                        "value" : [ "Min","(" ]
                     }, {
                        "r" : "40",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "34",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "35",
                           "s" : [ {
                              "value" : [ "2 ","'L'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "36",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "37",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "38",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "39",
                           "s" : [ {
                              "value" : [ "0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "41",
               "type" : "Min",
               "source" : {
                  "localId" : "40",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "34",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "35",
                     "value" : 2,
                     "unit" : "L",
                     "type" : "Quantity"
                  }, {
                     "localId" : "36",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "37",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "38",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "39",
                     "value" : 0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "46",
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "46",
                  "s" : [ {
                     "value" : [ "define ","empty",": " ]
                  }, {
                     "r" : "45",
                     "s" : [ {
                        "value" : [ "Sum","(" ]
                     }, {
                        "r" : "44",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "43",
                           "s" : [ {
                              "value" : [ "Integer" ]
                           } ]
                        }, {
                           "value" : [ ">{}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "45",
               "type" : "Sum",
               "source" : {
                  "localId" : "44",
                  "type" : "List"
               }
            }
         }, {
            "localId" : "54",
            "name" : "q_diff_units",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "54",
                  "s" : [ {
                     "value" : [ "define ","q_diff_units",": " ]
                  }, {
                     "r" : "53",
                     "s" : [ {
                        "value" : [ "Sum","(" ]
                     }, {
                        "r" : "52",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "47",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "48",
                           "s" : [ {
                              "value" : [ "0.002 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "49",
                           "s" : [ {
                              "value" : [ "0.03 ","'dl'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "50",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "51",
                           "s" : [ {
                              "value" : [ "0.005 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "53",
               "type" : "Sum",
               "source" : {
                  "localId" : "52",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "47",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "48",
                     "value" : 0.002,
                     "unit" : "l",
                     "type" : "Quantity"
                  }, {
                     "localId" : "49",
                     "value" : 0.03,
                     "unit" : "dl",
                     "type" : "Quantity"
                  }, {
                     "localId" : "50",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "51",
                     "value" : 0.005,
                     "unit" : "l",
                     "type" : "Quantity"
                  } ]
               }
            }
         } ]
      }
   }
}

### Min
library TestSnippet version '1'
using QUICK
context Patient
define not_null: Min({1,2,3,4,5,0})
define has_null: Min({1,null,-1,null,2})
define empty: Min(List<Integer>{})
define not_null_q: Min({1 'ml',2 'ml',3 'ml',4 'ml',5 'ml',0 'ml'})
define has_null_q: Min({1 'ml',null,-1 'ml',null,2 'ml'})
define q_diff_units: Min({1 'ml',2 'dl',3 'l',4 'l',5 'l',0 'ml'})
define q_throw1: Min({1 'ml',2 'm',3 'ml',4 'ml',5 'ml',0 'ml'})
define q_throw2: Min({1 ,2 'ml',3 'ml',4 'ml',5 'ml',0 'ml'})
###

module.exports['Min'] = {
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
            "localId" : "10",
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "10",
                  "s" : [ {
                     "value" : [ "define ","not_null",": " ]
                  }, {
                     "r" : "9",
                     "s" : [ {
                        "value" : [ "Min","(" ]
                     }, {
                        "r" : "8",
                        "s" : [ {
                           "value" : [ "{","1",",","2",",","3",",","4",",","5",",","0","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "9",
               "type" : "Min",
               "source" : {
                  "localId" : "8",
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
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "6",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "7",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "19",
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "19",
                  "s" : [ {
                     "value" : [ "define ","has_null",": " ]
                  }, {
                     "r" : "18",
                     "s" : [ {
                        "value" : [ "Min","(" ]
                     }, {
                        "r" : "17",
                        "s" : [ {
                           "value" : [ "{","1",",","null","," ]
                        }, {
                           "r" : "14",
                           "s" : [ {
                              "value" : [ "-","1" ]
                           } ]
                        }, {
                           "value" : [ ",","null",",","2","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "18",
               "type" : "Min",
               "source" : {
                  "localId" : "17",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "11",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "localId" : "12",
                        "type" : "Null"
                     }
                  }, {
                     "localId" : "14",
                     "type" : "Negate",
                     "operand" : {
                        "localId" : "13",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "localId" : "15",
                        "type" : "Null"
                     }
                  }, {
                     "localId" : "16",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "23",
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "23",
                  "s" : [ {
                     "value" : [ "define ","empty",": " ]
                  }, {
                     "r" : "22",
                     "s" : [ {
                        "value" : [ "Min","(" ]
                     }, {
                        "r" : "21",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "20",
                           "s" : [ {
                              "value" : [ "Integer" ]
                           } ]
                        }, {
                           "value" : [ ">{}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "22",
               "type" : "Min",
               "source" : {
                  "localId" : "21",
                  "type" : "List"
               }
            }
         }, {
            "localId" : "32",
            "name" : "not_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "32",
                  "s" : [ {
                     "value" : [ "define ","not_null_q",": " ]
                  }, {
                     "r" : "31",
                     "s" : [ {
                        "value" : [ "Min","(" ]
                     }, {
                        "r" : "30",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "24",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "25",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "26",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "27",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "28",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "29",
                           "s" : [ {
                              "value" : [ "0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "31",
               "type" : "Min",
               "source" : {
                  "localId" : "30",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "24",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "25",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "26",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "27",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "28",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "29",
                     "value" : 0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "41",
            "name" : "has_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "41",
                  "s" : [ {
                     "value" : [ "define ","has_null_q",": " ]
                  }, {
                     "r" : "40",
                     "s" : [ {
                        "value" : [ "Min","(" ]
                     }, {
                        "r" : "39",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "33",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ ",","null","," ]
                        }, {
                           "r" : "36",
                           "s" : [ {
                              "value" : [ "-" ]
                           }, {
                              "r" : "35",
                              "s" : [ {
                                 "value" : [ "1 ","'ml'" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ ",","null","," ]
                        }, {
                           "r" : "38",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "40",
               "type" : "Min",
               "source" : {
                  "localId" : "39",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "33",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "localId" : "34",
                        "type" : "Null"
                     }
                  }, {
                     "localId" : "36",
                     "type" : "Negate",
                     "operand" : {
                        "localId" : "35",
                        "value" : 1,
                        "unit" : "ml",
                        "type" : "Quantity"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "localId" : "37",
                        "type" : "Null"
                     }
                  }, {
                     "localId" : "38",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "50",
            "name" : "q_diff_units",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "50",
                  "s" : [ {
                     "value" : [ "define ","q_diff_units",": " ]
                  }, {
                     "r" : "49",
                     "s" : [ {
                        "value" : [ "Min","(" ]
                     }, {
                        "r" : "48",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "42",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "43",
                           "s" : [ {
                              "value" : [ "2 ","'dl'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "44",
                           "s" : [ {
                              "value" : [ "3 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "45",
                           "s" : [ {
                              "value" : [ "4 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "46",
                           "s" : [ {
                              "value" : [ "5 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "47",
                           "s" : [ {
                              "value" : [ "0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "49",
               "type" : "Min",
               "source" : {
                  "localId" : "48",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "42",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "43",
                     "value" : 2,
                     "unit" : "dl",
                     "type" : "Quantity"
                  }, {
                     "localId" : "44",
                     "value" : 3,
                     "unit" : "l",
                     "type" : "Quantity"
                  }, {
                     "localId" : "45",
                     "value" : 4,
                     "unit" : "l",
                     "type" : "Quantity"
                  }, {
                     "localId" : "46",
                     "value" : 5,
                     "unit" : "l",
                     "type" : "Quantity"
                  }, {
                     "localId" : "47",
                     "value" : 0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "59",
            "name" : "q_throw1",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "59",
                  "s" : [ {
                     "value" : [ "define ","q_throw1",": " ]
                  }, {
                     "r" : "58",
                     "s" : [ {
                        "value" : [ "Min","(" ]
                     }, {
                        "r" : "57",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "51",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "52",
                           "s" : [ {
                              "value" : [ "2 ","'m'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "53",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "54",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "55",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "56",
                           "s" : [ {
                              "value" : [ "0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "58",
               "type" : "Min",
               "source" : {
                  "localId" : "57",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "51",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "52",
                     "value" : 2,
                     "unit" : "m",
                     "type" : "Quantity"
                  }, {
                     "localId" : "53",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "54",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "55",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "56",
                     "value" : 0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "68",
            "name" : "q_throw2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "68",
                  "s" : [ {
                     "value" : [ "define ","q_throw2",": " ]
                  }, {
                     "r" : "67",
                     "s" : [ {
                        "value" : [ "Min","(" ]
                     }, {
                        "r" : "66",
                        "s" : [ {
                           "value" : [ "{","1"," ," ]
                        }, {
                           "r" : "61",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "62",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "63",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "64",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "65",
                           "s" : [ {
                              "value" : [ "0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "67",
               "type" : "Min",
               "source" : {
                  "localId" : "66",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "60",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "61",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "62",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "63",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "64",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "65",
                     "value" : 0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         } ]
      }
   }
}

### Max
library TestSnippet version '1'
using QUICK
context Patient
define not_null: Max({10,1,2,3,4,5})
define has_null: Max({1,null,null,2})
define not_null_q: Max({10 'ml',1 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
define has_null_q: Max({1 'ml',null,null,2 'ml'})
define q_diff_units: Max({10 'ml',1 'ml',2 'ml',3 'ml',4 'ml',5 'l'})
define q_throw1: Max({10 'ml',1 'm',2 'ml',3 'ml',4 'ml',5 'ml'})
define q_throw2: Max({10 ,1 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})

define empty: Max(List<Integer>{})
###

module.exports['Max'] = {
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
            "localId" : "10",
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "10",
                  "s" : [ {
                     "value" : [ "define ","not_null",": " ]
                  }, {
                     "r" : "9",
                     "s" : [ {
                        "value" : [ "Max","(" ]
                     }, {
                        "r" : "8",
                        "s" : [ {
                           "value" : [ "{","10",",","1",",","2",",","3",",","4",",","5","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "9",
               "type" : "Max",
               "source" : {
                  "localId" : "8",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "2",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }, {
                     "localId" : "3",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
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
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "7",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "17",
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "17",
                  "s" : [ {
                     "value" : [ "define ","has_null",": " ]
                  }, {
                     "r" : "16",
                     "s" : [ {
                        "value" : [ "Max","(" ]
                     }, {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "{","1",",","null",",","null",",","2","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "16",
               "type" : "Max",
               "source" : {
                  "localId" : "15",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "11",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "localId" : "12",
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "localId" : "13",
                        "type" : "Null"
                     }
                  }, {
                     "localId" : "14",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "26",
            "name" : "not_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "26",
                  "s" : [ {
                     "value" : [ "define ","not_null_q",": " ]
                  }, {
                     "r" : "25",
                     "s" : [ {
                        "value" : [ "Max","(" ]
                     }, {
                        "r" : "24",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "18",
                           "s" : [ {
                              "value" : [ "10 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "19",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "20",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "21",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "22",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "23",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "25",
               "type" : "Max",
               "source" : {
                  "localId" : "24",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "18",
                     "value" : 10,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "19",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "20",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "21",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "22",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "23",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "33",
            "name" : "has_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "33",
                  "s" : [ {
                     "value" : [ "define ","has_null_q",": " ]
                  }, {
                     "r" : "32",
                     "s" : [ {
                        "value" : [ "Max","(" ]
                     }, {
                        "r" : "31",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "27",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ ",","null",",","null","," ]
                        }, {
                           "r" : "30",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "32",
               "type" : "Max",
               "source" : {
                  "localId" : "31",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "27",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "localId" : "28",
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "localId" : "29",
                        "type" : "Null"
                     }
                  }, {
                     "localId" : "30",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "42",
            "name" : "q_diff_units",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "42",
                  "s" : [ {
                     "value" : [ "define ","q_diff_units",": " ]
                  }, {
                     "r" : "41",
                     "s" : [ {
                        "value" : [ "Max","(" ]
                     }, {
                        "r" : "40",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "34",
                           "s" : [ {
                              "value" : [ "10 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "35",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "36",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "37",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "38",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "39",
                           "s" : [ {
                              "value" : [ "5 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "41",
               "type" : "Max",
               "source" : {
                  "localId" : "40",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "34",
                     "value" : 10,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "35",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "36",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "37",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "38",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "39",
                     "value" : 5,
                     "unit" : "l",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "51",
            "name" : "q_throw1",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "51",
                  "s" : [ {
                     "value" : [ "define ","q_throw1",": " ]
                  }, {
                     "r" : "50",
                     "s" : [ {
                        "value" : [ "Max","(" ]
                     }, {
                        "r" : "49",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "43",
                           "s" : [ {
                              "value" : [ "10 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "44",
                           "s" : [ {
                              "value" : [ "1 ","'m'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "45",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "46",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "47",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "48",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "50",
               "type" : "Max",
               "source" : {
                  "localId" : "49",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "43",
                     "value" : 10,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "44",
                     "value" : 1,
                     "unit" : "m",
                     "type" : "Quantity"
                  }, {
                     "localId" : "45",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "46",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "47",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "48",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "60",
            "name" : "q_throw2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "60",
                  "s" : [ {
                     "value" : [ "define ","q_throw2",": " ]
                  }, {
                     "r" : "59",
                     "s" : [ {
                        "value" : [ "Max","(" ]
                     }, {
                        "r" : "58",
                        "s" : [ {
                           "value" : [ "{","10"," ," ]
                        }, {
                           "r" : "53",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "54",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "55",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "56",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "57",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "59",
               "type" : "Max",
               "source" : {
                  "localId" : "58",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "52",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }, {
                     "localId" : "53",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "54",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "55",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "56",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "57",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "64",
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "64",
                  "s" : [ {
                     "value" : [ "define ","empty",": " ]
                  }, {
                     "r" : "63",
                     "s" : [ {
                        "value" : [ "Max","(" ]
                     }, {
                        "r" : "62",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "61",
                           "s" : [ {
                              "value" : [ "Integer" ]
                           } ]
                        }, {
                           "value" : [ ">{}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "63",
               "type" : "Max",
               "source" : {
                  "localId" : "62",
                  "type" : "List"
               }
            }
         } ]
      }
   }
}

### Avg
library TestSnippet version '1'
using QUICK
context Patient
define not_null: Avg({1,2,3,4,5})
define has_null: Avg({1,null,null,2})
define not_null_q: Avg({1 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
define has_null_q: Avg({1 'ml',null,null,2 'ml'})
define empty: Avg(List<Integer>{})
define q_diff_units: Avg({1 'ml',0.002 'l',0.03 'dl',4 'ml',5 'ml'})
define q_throw1: Avg({1 'ml',0.002 'm',0.03 'dl',4 'ml',5 'ml'})
###

module.exports['Avg'] = {
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
            "localId" : "9",
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","not_null",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "Avg","(" ]
                     }, {
                        "r" : "7",
                        "s" : [ {
                           "value" : [ "{","1",",","2",",","3",",","4",",","5","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "Avg",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "localId" : "7",
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
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "localId" : "5",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "localId" : "6",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "type" : "ToDecimal",
                        "operand" : {
                           "name" : "X",
                           "type" : "AliasRef"
                        }
                     }
                  }
               }
            }
         }, {
            "localId" : "16",
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "16",
                  "s" : [ {
                     "value" : [ "define ","has_null",": " ]
                  }, {
                     "r" : "15",
                     "s" : [ {
                        "value" : [ "Avg","(" ]
                     }, {
                        "r" : "14",
                        "s" : [ {
                           "value" : [ "{","1",",","null",",","null",",","2","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "15",
               "type" : "Avg",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "localId" : "14",
                        "type" : "List",
                        "element" : [ {
                           "localId" : "10",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "type" : "As",
                           "operand" : {
                              "localId" : "11",
                              "type" : "Null"
                           }
                        }, {
                           "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "type" : "As",
                           "operand" : {
                              "localId" : "12",
                              "type" : "Null"
                           }
                        }, {
                           "localId" : "13",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "type" : "ToDecimal",
                        "operand" : {
                           "name" : "X",
                           "type" : "AliasRef"
                        }
                     }
                  }
               }
            }
         }, {
            "localId" : "24",
            "name" : "not_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "24",
                  "s" : [ {
                     "value" : [ "define ","not_null_q",": " ]
                  }, {
                     "r" : "23",
                     "s" : [ {
                        "value" : [ "Avg","(" ]
                     }, {
                        "r" : "22",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "17",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "18",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "19",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "20",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "21",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "23",
               "type" : "Avg",
               "source" : {
                  "localId" : "22",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "17",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "18",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "19",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "20",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "21",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "31",
            "name" : "has_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "31",
                  "s" : [ {
                     "value" : [ "define ","has_null_q",": " ]
                  }, {
                     "r" : "30",
                     "s" : [ {
                        "value" : [ "Avg","(" ]
                     }, {
                        "r" : "29",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "25",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ ",","null",",","null","," ]
                        }, {
                           "r" : "28",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "30",
               "type" : "Avg",
               "source" : {
                  "localId" : "29",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "25",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "localId" : "26",
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "localId" : "27",
                        "type" : "Null"
                     }
                  }, {
                     "localId" : "28",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "35",
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "35",
                  "s" : [ {
                     "value" : [ "define ","empty",": " ]
                  }, {
                     "r" : "34",
                     "s" : [ {
                        "value" : [ "Avg","(" ]
                     }, {
                        "r" : "33",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "32",
                           "s" : [ {
                              "value" : [ "Integer" ]
                           } ]
                        }, {
                           "value" : [ ">{}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "34",
               "type" : "Avg",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "localId" : "33",
                        "type" : "List"
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "type" : "ToDecimal",
                        "operand" : {
                           "name" : "X",
                           "type" : "AliasRef"
                        }
                     }
                  }
               }
            }
         }, {
            "localId" : "43",
            "name" : "q_diff_units",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "43",
                  "s" : [ {
                     "value" : [ "define ","q_diff_units",": " ]
                  }, {
                     "r" : "42",
                     "s" : [ {
                        "value" : [ "Avg","(" ]
                     }, {
                        "r" : "41",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "36",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "37",
                           "s" : [ {
                              "value" : [ "0.002 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "38",
                           "s" : [ {
                              "value" : [ "0.03 ","'dl'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "39",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "40",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "42",
               "type" : "Avg",
               "source" : {
                  "localId" : "41",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "36",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "37",
                     "value" : 0.002,
                     "unit" : "l",
                     "type" : "Quantity"
                  }, {
                     "localId" : "38",
                     "value" : 0.03,
                     "unit" : "dl",
                     "type" : "Quantity"
                  }, {
                     "localId" : "39",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "40",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "51",
            "name" : "q_throw1",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "51",
                  "s" : [ {
                     "value" : [ "define ","q_throw1",": " ]
                  }, {
                     "r" : "50",
                     "s" : [ {
                        "value" : [ "Avg","(" ]
                     }, {
                        "r" : "49",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "44",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "45",
                           "s" : [ {
                              "value" : [ "0.002 ","'m'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "46",
                           "s" : [ {
                              "value" : [ "0.03 ","'dl'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "47",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "48",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "50",
               "type" : "Avg",
               "source" : {
                  "localId" : "49",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "44",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "45",
                     "value" : 0.002,
                     "unit" : "m",
                     "type" : "Quantity"
                  }, {
                     "localId" : "46",
                     "value" : 0.03,
                     "unit" : "dl",
                     "type" : "Quantity"
                  }, {
                     "localId" : "47",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "48",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         } ]
      }
   }
}

### Median
library TestSnippet version '1'
using QUICK
context Patient
define odd: Median({5,1,2,3,4})
define even: Median({5,1,2,3,4,6})
define odd_q: Median({5 'ml',1 'ml',2 'ml',3 'ml',4 'ml'})
define even_q: Median({5 'ml',1 'ml',2 'ml',3 'ml',4 'ml',6 'ml'})
define q_diff_units: Median({5 'ml',0.001 'l',0.02 'dl',3 'ml',4 'ml',6 'ml'})
define q_throw1: Median({5 'ml',0.001 'l',0.22 'dl',3 'm',4 'h',6 'ml'})
define q_throw2: Median({5 ,1 ,2 ,3 ,4 'ml',6 'ml'})

define empty: Median(List<Integer>{})
define has_null: Median({1,null,null,2})
define dup_vals_even: Median({3,1,2,2,2,3,4,5})
define dup_vals_odd:  Median({3,1,2,2,2,3,4,5,6})
define has_null_q: Median({1 'ml',null,null,2 'ml'})
define dup_vals_even_q: Median({3 'ml',1 'ml',2 'ml',2 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
define dup_vals_odd_q:  Median({3 'ml',1 'ml',2 'ml',2 'ml',2 'ml',3 'ml',4 'ml',5 'ml',6 'ml'})
###

module.exports['Median'] = {
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
            "localId" : "9",
            "name" : "odd",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","odd",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "Median","(" ]
                     }, {
                        "r" : "7",
                        "s" : [ {
                           "value" : [ "{","5",",","1",",","2",",","3",",","4","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "Median",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "localId" : "7",
                        "type" : "List",
                        "element" : [ {
                           "localId" : "2",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        }, {
                           "localId" : "3",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
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
                           "value" : "4",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "type" : "ToDecimal",
                        "operand" : {
                           "name" : "X",
                           "type" : "AliasRef"
                        }
                     }
                  }
               }
            }
         }, {
            "localId" : "18",
            "name" : "even",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "18",
                  "s" : [ {
                     "value" : [ "define ","even",": " ]
                  }, {
                     "r" : "17",
                     "s" : [ {
                        "value" : [ "Median","(" ]
                     }, {
                        "r" : "16",
                        "s" : [ {
                           "value" : [ "{","5",",","1",",","2",",","3",",","4",",","6","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "17",
               "type" : "Median",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "localId" : "16",
                        "type" : "List",
                        "element" : [ {
                           "localId" : "10",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        }, {
                           "localId" : "11",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "localId" : "12",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "localId" : "13",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "localId" : "14",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "localId" : "15",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "6",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "type" : "ToDecimal",
                        "operand" : {
                           "name" : "X",
                           "type" : "AliasRef"
                        }
                     }
                  }
               }
            }
         }, {
            "localId" : "26",
            "name" : "odd_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "26",
                  "s" : [ {
                     "value" : [ "define ","odd_q",": " ]
                  }, {
                     "r" : "25",
                     "s" : [ {
                        "value" : [ "Median","(" ]
                     }, {
                        "r" : "24",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "19",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "20",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "21",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "22",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "23",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "25",
               "type" : "Median",
               "source" : {
                  "localId" : "24",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "19",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "20",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "21",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "22",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "23",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "35",
            "name" : "even_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "35",
                  "s" : [ {
                     "value" : [ "define ","even_q",": " ]
                  }, {
                     "r" : "34",
                     "s" : [ {
                        "value" : [ "Median","(" ]
                     }, {
                        "r" : "33",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "27",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "28",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "29",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "30",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "31",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "32",
                           "s" : [ {
                              "value" : [ "6 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "34",
               "type" : "Median",
               "source" : {
                  "localId" : "33",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "27",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "28",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "29",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "30",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "31",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "32",
                     "value" : 6,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "44",
            "name" : "q_diff_units",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "44",
                  "s" : [ {
                     "value" : [ "define ","q_diff_units",": " ]
                  }, {
                     "r" : "43",
                     "s" : [ {
                        "value" : [ "Median","(" ]
                     }, {
                        "r" : "42",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "36",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "37",
                           "s" : [ {
                              "value" : [ "0.001 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "38",
                           "s" : [ {
                              "value" : [ "0.02 ","'dl'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "39",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "40",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "41",
                           "s" : [ {
                              "value" : [ "6 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "43",
               "type" : "Median",
               "source" : {
                  "localId" : "42",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "36",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "37",
                     "value" : 0.001,
                     "unit" : "l",
                     "type" : "Quantity"
                  }, {
                     "localId" : "38",
                     "value" : 0.02,
                     "unit" : "dl",
                     "type" : "Quantity"
                  }, {
                     "localId" : "39",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "40",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "41",
                     "value" : 6,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "53",
            "name" : "q_throw1",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "53",
                  "s" : [ {
                     "value" : [ "define ","q_throw1",": " ]
                  }, {
                     "r" : "52",
                     "s" : [ {
                        "value" : [ "Median","(" ]
                     }, {
                        "r" : "51",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "45",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "46",
                           "s" : [ {
                              "value" : [ "0.001 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "47",
                           "s" : [ {
                              "value" : [ "0.22 ","'dl'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "48",
                           "s" : [ {
                              "value" : [ "3 ","'m'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "49",
                           "s" : [ {
                              "value" : [ "4 ","'h'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "50",
                           "s" : [ {
                              "value" : [ "6 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "52",
               "type" : "Median",
               "source" : {
                  "localId" : "51",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "45",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "46",
                     "value" : 0.001,
                     "unit" : "l",
                     "type" : "Quantity"
                  }, {
                     "localId" : "47",
                     "value" : 0.22,
                     "unit" : "dl",
                     "type" : "Quantity"
                  }, {
                     "localId" : "48",
                     "value" : 3,
                     "unit" : "m",
                     "type" : "Quantity"
                  }, {
                     "localId" : "49",
                     "value" : 4,
                     "unit" : "h",
                     "type" : "Quantity"
                  }, {
                     "localId" : "50",
                     "value" : 6,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "62",
            "name" : "q_throw2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "62",
                  "s" : [ {
                     "value" : [ "define ","q_throw2",": " ]
                  }, {
                     "r" : "61",
                     "s" : [ {
                        "value" : [ "Median","(" ]
                     }, {
                        "r" : "60",
                        "s" : [ {
                           "value" : [ "{","5"," ,","1"," ,","2"," ,","3"," ," ]
                        }, {
                           "r" : "58",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "59",
                           "s" : [ {
                              "value" : [ "6 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "61",
               "type" : "Median",
               "source" : {
                  "localId" : "60",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "54",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "55",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "56",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "57",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "58",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "59",
                     "value" : 6,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "66",
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "66",
                  "s" : [ {
                     "value" : [ "define ","empty",": " ]
                  }, {
                     "r" : "65",
                     "s" : [ {
                        "value" : [ "Median","(" ]
                     }, {
                        "r" : "64",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "63",
                           "s" : [ {
                              "value" : [ "Integer" ]
                           } ]
                        }, {
                           "value" : [ ">{}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "65",
               "type" : "Median",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "localId" : "64",
                        "type" : "List"
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "type" : "ToDecimal",
                        "operand" : {
                           "name" : "X",
                           "type" : "AliasRef"
                        }
                     }
                  }
               }
            }
         }, {
            "localId" : "73",
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "73",
                  "s" : [ {
                     "value" : [ "define ","has_null",": " ]
                  }, {
                     "r" : "72",
                     "s" : [ {
                        "value" : [ "Median","(" ]
                     }, {
                        "r" : "71",
                        "s" : [ {
                           "value" : [ "{","1",",","null",",","null",",","2","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "72",
               "type" : "Median",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "localId" : "71",
                        "type" : "List",
                        "element" : [ {
                           "localId" : "67",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "type" : "As",
                           "operand" : {
                              "localId" : "68",
                              "type" : "Null"
                           }
                        }, {
                           "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "type" : "As",
                           "operand" : {
                              "localId" : "69",
                              "type" : "Null"
                           }
                        }, {
                           "localId" : "70",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "type" : "ToDecimal",
                        "operand" : {
                           "name" : "X",
                           "type" : "AliasRef"
                        }
                     }
                  }
               }
            }
         }, {
            "localId" : "84",
            "name" : "dup_vals_even",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "84",
                  "s" : [ {
                     "value" : [ "define ","dup_vals_even",": " ]
                  }, {
                     "r" : "83",
                     "s" : [ {
                        "value" : [ "Median","(" ]
                     }, {
                        "r" : "82",
                        "s" : [ {
                           "value" : [ "{","3",",","1",",","2",",","2",",","2",",","3",",","4",",","5","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "83",
               "type" : "Median",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "localId" : "82",
                        "type" : "List",
                        "element" : [ {
                           "localId" : "74",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "localId" : "75",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "localId" : "76",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "localId" : "77",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "localId" : "78",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "localId" : "79",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "localId" : "80",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "localId" : "81",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "type" : "ToDecimal",
                        "operand" : {
                           "name" : "X",
                           "type" : "AliasRef"
                        }
                     }
                  }
               }
            }
         }, {
            "localId" : "96",
            "name" : "dup_vals_odd",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "96",
                  "s" : [ {
                     "value" : [ "define ","dup_vals_odd",":  " ]
                  }, {
                     "r" : "95",
                     "s" : [ {
                        "value" : [ "Median","(" ]
                     }, {
                        "r" : "94",
                        "s" : [ {
                           "value" : [ "{","3",",","1",",","2",",","2",",","2",",","3",",","4",",","5",",","6","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "95",
               "type" : "Median",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "localId" : "94",
                        "type" : "List",
                        "element" : [ {
                           "localId" : "85",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "localId" : "86",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "localId" : "87",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "localId" : "88",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "localId" : "89",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "localId" : "90",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "localId" : "91",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "localId" : "92",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        }, {
                           "localId" : "93",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "6",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "type" : "ToDecimal",
                        "operand" : {
                           "name" : "X",
                           "type" : "AliasRef"
                        }
                     }
                  }
               }
            }
         }, {
            "localId" : "103",
            "name" : "has_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "103",
                  "s" : [ {
                     "value" : [ "define ","has_null_q",": " ]
                  }, {
                     "r" : "102",
                     "s" : [ {
                        "value" : [ "Median","(" ]
                     }, {
                        "r" : "101",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "97",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ ",","null",",","null","," ]
                        }, {
                           "r" : "100",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "102",
               "type" : "Median",
               "source" : {
                  "localId" : "101",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "97",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "localId" : "98",
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "localId" : "99",
                        "type" : "Null"
                     }
                  }, {
                     "localId" : "100",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "114",
            "name" : "dup_vals_even_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "114",
                  "s" : [ {
                     "value" : [ "define ","dup_vals_even_q",": " ]
                  }, {
                     "r" : "113",
                     "s" : [ {
                        "value" : [ "Median","(" ]
                     }, {
                        "r" : "112",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "104",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "105",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "106",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "107",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "108",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "109",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "110",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "111",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "113",
               "type" : "Median",
               "source" : {
                  "localId" : "112",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "104",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "105",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "106",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "107",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "108",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "109",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "110",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "111",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "126",
            "name" : "dup_vals_odd_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "126",
                  "s" : [ {
                     "value" : [ "define ","dup_vals_odd_q",":  " ]
                  }, {
                     "r" : "125",
                     "s" : [ {
                        "value" : [ "Median","(" ]
                     }, {
                        "r" : "124",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "115",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "116",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "117",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "118",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "119",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "120",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "121",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "122",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "123",
                           "s" : [ {
                              "value" : [ "6 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "125",
               "type" : "Median",
               "source" : {
                  "localId" : "124",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "115",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "116",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "117",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "118",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "119",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "120",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "121",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "122",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "123",
                     "value" : 6,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         } ]
      }
   }
}

### Mode
library TestSnippet version '1'
using QUICK
context Patient
define not_null: Mode({1,2,2,2,3,4,5})
define has_null: Mode({1,null,null,2,2})
define empty: Mode({})

define bi_modal: Mode({1,2,2,2,3,3,3,4,5})
###

module.exports['Mode'] = {
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
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","not_null",": " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "value" : [ "Mode","(" ]
                     }, {
                        "r" : "9",
                        "s" : [ {
                           "value" : [ "{","1",",","2",",","2",",","2",",","3",",","4",",","5","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "type" : "Mode",
               "source" : {
                  "localId" : "9",
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
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "6",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "7",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "8",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "19",
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "19",
                  "s" : [ {
                     "value" : [ "define ","has_null",": " ]
                  }, {
                     "r" : "18",
                     "s" : [ {
                        "value" : [ "Mode","(" ]
                     }, {
                        "r" : "17",
                        "s" : [ {
                           "value" : [ "{","1",",","null",",","null",",","2",",","2","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "18",
               "type" : "Mode",
               "source" : {
                  "localId" : "17",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "12",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "localId" : "13",
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "localId" : "14",
                        "type" : "Null"
                     }
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
                  } ]
               }
            }
         }, {
            "localId" : "22",
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "22",
                  "s" : [ {
                     "value" : [ "define ","empty",": " ]
                  }, {
                     "r" : "21",
                     "s" : [ {
                        "value" : [ "Mode","(","{}",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "21",
               "type" : "Mode",
               "source" : {
                  "localId" : "20",
                  "type" : "List"
               }
            }
         }, {
            "localId" : "34",
            "name" : "bi_modal",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "34",
                  "s" : [ {
                     "value" : [ "define ","bi_modal",": " ]
                  }, {
                     "r" : "33",
                     "s" : [ {
                        "value" : [ "Mode","(" ]
                     }, {
                        "r" : "32",
                        "s" : [ {
                           "value" : [ "{","1",",","2",",","2",",","2",",","3",",","3",",","3",",","4",",","5","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "33",
               "type" : "Mode",
               "source" : {
                  "localId" : "32",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "23",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "24",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "25",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "26",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "27",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "28",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "29",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "30",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "31",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }
            }
         } ]
      }
   }
}

### Variance
library TestSnippet version '1'
using QUICK
context Patient
define v: Variance({1,2,3,4,5})
define v_q: Variance({1 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
define q_diff_units: Variance({1.0 'ml',0.002 'l',0.003 'l',0.04 'dl',5.0 'ml'})
define q_throw1: Variance({1.0 'm',2.0 'l',3.0 'h',4.0 'ml',5.0 'ml'})
define q_throw2: Variance({1.0 ,2.0 ,3.0 ,4.0 'ml',5.0 'ml'})
###

module.exports['Variance'] = {
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
            "localId" : "9",
            "name" : "v",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","v",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "Variance","(" ]
                     }, {
                        "r" : "7",
                        "s" : [ {
                           "value" : [ "{","1",",","2",",","3",",","4",",","5","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "Variance",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "localId" : "7",
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
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "localId" : "5",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "localId" : "6",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "type" : "ToDecimal",
                        "operand" : {
                           "name" : "X",
                           "type" : "AliasRef"
                        }
                     }
                  }
               }
            }
         }, {
            "localId" : "17",
            "name" : "v_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "17",
                  "s" : [ {
                     "value" : [ "define ","v_q",": " ]
                  }, {
                     "r" : "16",
                     "s" : [ {
                        "value" : [ "Variance","(" ]
                     }, {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "10",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "11",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "12",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "13",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "14",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "16",
               "type" : "Variance",
               "source" : {
                  "localId" : "15",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "10",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "11",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "12",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "13",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "14",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "25",
            "name" : "q_diff_units",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "25",
                  "s" : [ {
                     "value" : [ "define ","q_diff_units",": " ]
                  }, {
                     "r" : "24",
                     "s" : [ {
                        "value" : [ "Variance","(" ]
                     }, {
                        "r" : "23",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "18",
                           "s" : [ {
                              "value" : [ "1.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "19",
                           "s" : [ {
                              "value" : [ "0.002 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "20",
                           "s" : [ {
                              "value" : [ "0.003 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "21",
                           "s" : [ {
                              "value" : [ "0.04 ","'dl'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "22",
                           "s" : [ {
                              "value" : [ "5.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "24",
               "type" : "Variance",
               "source" : {
                  "localId" : "23",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "18",
                     "value" : 1.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "19",
                     "value" : 0.002,
                     "unit" : "l",
                     "type" : "Quantity"
                  }, {
                     "localId" : "20",
                     "value" : 0.003,
                     "unit" : "l",
                     "type" : "Quantity"
                  }, {
                     "localId" : "21",
                     "value" : 0.04,
                     "unit" : "dl",
                     "type" : "Quantity"
                  }, {
                     "localId" : "22",
                     "value" : 5.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "33",
            "name" : "q_throw1",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "33",
                  "s" : [ {
                     "value" : [ "define ","q_throw1",": " ]
                  }, {
                     "r" : "32",
                     "s" : [ {
                        "value" : [ "Variance","(" ]
                     }, {
                        "r" : "31",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "26",
                           "s" : [ {
                              "value" : [ "1.0 ","'m'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "27",
                           "s" : [ {
                              "value" : [ "2.0 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "28",
                           "s" : [ {
                              "value" : [ "3.0 ","'h'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "29",
                           "s" : [ {
                              "value" : [ "4.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "30",
                           "s" : [ {
                              "value" : [ "5.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "32",
               "type" : "Variance",
               "source" : {
                  "localId" : "31",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "26",
                     "value" : 1.0,
                     "unit" : "m",
                     "type" : "Quantity"
                  }, {
                     "localId" : "27",
                     "value" : 2.0,
                     "unit" : "l",
                     "type" : "Quantity"
                  }, {
                     "localId" : "28",
                     "value" : 3.0,
                     "unit" : "h",
                     "type" : "Quantity"
                  }, {
                     "localId" : "29",
                     "value" : 4.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "30",
                     "value" : 5.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "41",
            "name" : "q_throw2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "41",
                  "s" : [ {
                     "value" : [ "define ","q_throw2",": " ]
                  }, {
                     "r" : "40",
                     "s" : [ {
                        "value" : [ "Variance","(" ]
                     }, {
                        "r" : "39",
                        "s" : [ {
                           "value" : [ "{","1.0"," ,","2.0"," ,","3.0"," ," ]
                        }, {
                           "r" : "37",
                           "s" : [ {
                              "value" : [ "4.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "38",
                           "s" : [ {
                              "value" : [ "5.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "40",
               "type" : "Variance",
               "source" : {
                  "localId" : "39",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "34",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  }, {
                     "localId" : "35",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "2.0",
                     "type" : "Literal"
                  }, {
                     "localId" : "36",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "3.0",
                     "type" : "Literal"
                  }, {
                     "localId" : "37",
                     "value" : 4.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "38",
                     "value" : 5.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         } ]
      }
   }
}

### PopulationVariance
library TestSnippet version '1'
using QUICK
context Patient
define v: PopulationVariance({1.0,2.0,3.0,4.0,5.0})
define v_q: PopulationVariance({1.0 'ml',2.0 'ml',3.0 'ml',4.0 'ml',5.0 'ml'})
define q_diff_units: PopulationVariance({1.0 'ml',0.002 'l',0.003 'l',0.04 'dl',5.0 'ml'})
define q_throw1: PopulationVariance({1.0 'm',2.0 'l',3.0 'h',4.0 'ml',5.0 'ml'})
define q_throw2: PopulationVariance({1.0 ,2.0 ,3.0 ,4.0 'ml',5.0 'ml'})
###

module.exports['PopulationVariance'] = {
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
            "localId" : "9",
            "name" : "v",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","v",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "PopulationVariance","(" ]
                     }, {
                        "r" : "7",
                        "s" : [ {
                           "value" : [ "{","1.0",",","2.0",",","3.0",",","4.0",",","5.0","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "PopulationVariance",
               "source" : {
                  "localId" : "7",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "2",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  }, {
                     "localId" : "3",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "2.0",
                     "type" : "Literal"
                  }, {
                     "localId" : "4",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "3.0",
                     "type" : "Literal"
                  }, {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "4.0",
                     "type" : "Literal"
                  }, {
                     "localId" : "6",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "5.0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "17",
            "name" : "v_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "17",
                  "s" : [ {
                     "value" : [ "define ","v_q",": " ]
                  }, {
                     "r" : "16",
                     "s" : [ {
                        "value" : [ "PopulationVariance","(" ]
                     }, {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "10",
                           "s" : [ {
                              "value" : [ "1.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "11",
                           "s" : [ {
                              "value" : [ "2.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "12",
                           "s" : [ {
                              "value" : [ "3.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "13",
                           "s" : [ {
                              "value" : [ "4.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "14",
                           "s" : [ {
                              "value" : [ "5.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "16",
               "type" : "PopulationVariance",
               "source" : {
                  "localId" : "15",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "10",
                     "value" : 1.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "11",
                     "value" : 2.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "12",
                     "value" : 3.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "13",
                     "value" : 4.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "14",
                     "value" : 5.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "25",
            "name" : "q_diff_units",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "25",
                  "s" : [ {
                     "value" : [ "define ","q_diff_units",": " ]
                  }, {
                     "r" : "24",
                     "s" : [ {
                        "value" : [ "PopulationVariance","(" ]
                     }, {
                        "r" : "23",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "18",
                           "s" : [ {
                              "value" : [ "1.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "19",
                           "s" : [ {
                              "value" : [ "0.002 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "20",
                           "s" : [ {
                              "value" : [ "0.003 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "21",
                           "s" : [ {
                              "value" : [ "0.04 ","'dl'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "22",
                           "s" : [ {
                              "value" : [ "5.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "24",
               "type" : "PopulationVariance",
               "source" : {
                  "localId" : "23",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "18",
                     "value" : 1.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "19",
                     "value" : 0.002,
                     "unit" : "l",
                     "type" : "Quantity"
                  }, {
                     "localId" : "20",
                     "value" : 0.003,
                     "unit" : "l",
                     "type" : "Quantity"
                  }, {
                     "localId" : "21",
                     "value" : 0.04,
                     "unit" : "dl",
                     "type" : "Quantity"
                  }, {
                     "localId" : "22",
                     "value" : 5.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "33",
            "name" : "q_throw1",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "33",
                  "s" : [ {
                     "value" : [ "define ","q_throw1",": " ]
                  }, {
                     "r" : "32",
                     "s" : [ {
                        "value" : [ "PopulationVariance","(" ]
                     }, {
                        "r" : "31",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "26",
                           "s" : [ {
                              "value" : [ "1.0 ","'m'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "27",
                           "s" : [ {
                              "value" : [ "2.0 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "28",
                           "s" : [ {
                              "value" : [ "3.0 ","'h'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "29",
                           "s" : [ {
                              "value" : [ "4.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "30",
                           "s" : [ {
                              "value" : [ "5.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "32",
               "type" : "PopulationVariance",
               "source" : {
                  "localId" : "31",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "26",
                     "value" : 1.0,
                     "unit" : "m",
                     "type" : "Quantity"
                  }, {
                     "localId" : "27",
                     "value" : 2.0,
                     "unit" : "l",
                     "type" : "Quantity"
                  }, {
                     "localId" : "28",
                     "value" : 3.0,
                     "unit" : "h",
                     "type" : "Quantity"
                  }, {
                     "localId" : "29",
                     "value" : 4.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "30",
                     "value" : 5.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "41",
            "name" : "q_throw2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "41",
                  "s" : [ {
                     "value" : [ "define ","q_throw2",": " ]
                  }, {
                     "r" : "40",
                     "s" : [ {
                        "value" : [ "PopulationVariance","(" ]
                     }, {
                        "r" : "39",
                        "s" : [ {
                           "value" : [ "{","1.0"," ,","2.0"," ,","3.0"," ," ]
                        }, {
                           "r" : "37",
                           "s" : [ {
                              "value" : [ "4.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "38",
                           "s" : [ {
                              "value" : [ "5.0 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "40",
               "type" : "PopulationVariance",
               "source" : {
                  "localId" : "39",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "34",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  }, {
                     "localId" : "35",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "2.0",
                     "type" : "Literal"
                  }, {
                     "localId" : "36",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "3.0",
                     "type" : "Literal"
                  }, {
                     "localId" : "37",
                     "value" : 4.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "38",
                     "value" : 5.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         } ]
      }
   }
}

### StdDev
library TestSnippet version '1'
using QUICK
context Patient
define std: StdDev({1,2,3,4,5})
define std_q: StdDev({1 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
define q_diff_units: StdDev({1 'ml', 0.002 'l',3 'ml',4 'ml', 0.05 'dl'})
define sq_throw1: StdDev({1 'ml',2 'ml',3 'ml',4 'ml',5 'm'})
define q_throw2: StdDev({1 ,2 ,3 ,4 'ml',5 })
###

module.exports['StdDev'] = {
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
            "localId" : "9",
            "name" : "std",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","std",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "StdDev","(" ]
                     }, {
                        "r" : "7",
                        "s" : [ {
                           "value" : [ "{","1",",","2",",","3",",","4",",","5","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "StdDev",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "localId" : "7",
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
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "localId" : "5",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "localId" : "6",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "type" : "ToDecimal",
                        "operand" : {
                           "name" : "X",
                           "type" : "AliasRef"
                        }
                     }
                  }
               }
            }
         }, {
            "localId" : "17",
            "name" : "std_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "17",
                  "s" : [ {
                     "value" : [ "define ","std_q",": " ]
                  }, {
                     "r" : "16",
                     "s" : [ {
                        "value" : [ "StdDev","(" ]
                     }, {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "10",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "11",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "12",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "13",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "14",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "16",
               "type" : "StdDev",
               "source" : {
                  "localId" : "15",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "10",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "11",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "12",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "13",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "14",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "25",
            "name" : "q_diff_units",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "25",
                  "s" : [ {
                     "value" : [ "define ","q_diff_units",": " ]
                  }, {
                     "r" : "24",
                     "s" : [ {
                        "value" : [ "StdDev","(" ]
                     }, {
                        "r" : "23",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "18",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "19",
                           "s" : [ {
                              "value" : [ "0.002 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "20",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "21",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "22",
                           "s" : [ {
                              "value" : [ "0.05 ","'dl'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "24",
               "type" : "StdDev",
               "source" : {
                  "localId" : "23",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "18",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "19",
                     "value" : 0.002,
                     "unit" : "l",
                     "type" : "Quantity"
                  }, {
                     "localId" : "20",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "21",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "22",
                     "value" : 0.05,
                     "unit" : "dl",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "33",
            "name" : "sq_throw1",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "33",
                  "s" : [ {
                     "value" : [ "define ","sq_throw1",": " ]
                  }, {
                     "r" : "32",
                     "s" : [ {
                        "value" : [ "StdDev","(" ]
                     }, {
                        "r" : "31",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "26",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "27",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "28",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "29",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "30",
                           "s" : [ {
                              "value" : [ "5 ","'m'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "32",
               "type" : "StdDev",
               "source" : {
                  "localId" : "31",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "26",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "27",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "28",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "29",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "30",
                     "value" : 5,
                     "unit" : "m",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "41",
            "name" : "q_throw2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "41",
                  "s" : [ {
                     "value" : [ "define ","q_throw2",": " ]
                  }, {
                     "r" : "40",
                     "s" : [ {
                        "value" : [ "StdDev","(" ]
                     }, {
                        "r" : "39",
                        "s" : [ {
                           "value" : [ "{","1"," ,","2"," ,","3"," ," ]
                        }, {
                           "r" : "37",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ ",","5"," }" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "40",
               "type" : "StdDev",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "localId" : "39",
                        "type" : "List",
                        "element" : [ {
                           "localId" : "34",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "localId" : "35",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "localId" : "36",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "localId" : "37",
                           "value" : 4,
                           "unit" : "ml",
                           "type" : "Quantity"
                        }, {
                           "localId" : "38",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "type" : "ToDecimal",
                        "operand" : {
                           "name" : "X",
                           "type" : "AliasRef"
                        }
                     }
                  }
               }
            }
         } ]
      }
   }
}

### PopulationStdDev
library TestSnippet version '1'
using QUICK
context Patient
define dev: PopulationStdDev({1,2,3,4,5})
define dev_q: PopulationStdDev({1 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
define q_diff_units: PopulationStdDev({1 'ml', 0.002 'l',3 'ml',4 'ml', 0.05 'dl'})
define q_throw1: PopulationStdDev({1 'ml',2 'ml',3 'ml',4 'ml',5 'm'})
define q_throw2: PopulationStdDev({1 ,2 ,3 ,4 'ml',5 })
###

module.exports['PopulationStdDev'] = {
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
            "localId" : "9",
            "name" : "dev",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","dev",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "PopulationStdDev","(" ]
                     }, {
                        "r" : "7",
                        "s" : [ {
                           "value" : [ "{","1",",","2",",","3",",","4",",","5","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "PopulationStdDev",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "localId" : "7",
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
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "localId" : "5",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "localId" : "6",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "type" : "ToDecimal",
                        "operand" : {
                           "name" : "X",
                           "type" : "AliasRef"
                        }
                     }
                  }
               }
            }
         }, {
            "localId" : "17",
            "name" : "dev_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "17",
                  "s" : [ {
                     "value" : [ "define ","dev_q",": " ]
                  }, {
                     "r" : "16",
                     "s" : [ {
                        "value" : [ "PopulationStdDev","(" ]
                     }, {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "10",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "11",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "12",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "13",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "14",
                           "s" : [ {
                              "value" : [ "5 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "16",
               "type" : "PopulationStdDev",
               "source" : {
                  "localId" : "15",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "10",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "11",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "12",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "13",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "14",
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "25",
            "name" : "q_diff_units",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "25",
                  "s" : [ {
                     "value" : [ "define ","q_diff_units",": " ]
                  }, {
                     "r" : "24",
                     "s" : [ {
                        "value" : [ "PopulationStdDev","(" ]
                     }, {
                        "r" : "23",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "18",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "19",
                           "s" : [ {
                              "value" : [ "0.002 ","'l'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "20",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "21",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "22",
                           "s" : [ {
                              "value" : [ "0.05 ","'dl'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "24",
               "type" : "PopulationStdDev",
               "source" : {
                  "localId" : "23",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "18",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "19",
                     "value" : 0.002,
                     "unit" : "l",
                     "type" : "Quantity"
                  }, {
                     "localId" : "20",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "21",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "22",
                     "value" : 0.05,
                     "unit" : "dl",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "33",
            "name" : "q_throw1",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "33",
                  "s" : [ {
                     "value" : [ "define ","q_throw1",": " ]
                  }, {
                     "r" : "32",
                     "s" : [ {
                        "value" : [ "PopulationStdDev","(" ]
                     }, {
                        "r" : "31",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "26",
                           "s" : [ {
                              "value" : [ "1 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "27",
                           "s" : [ {
                              "value" : [ "2 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "28",
                           "s" : [ {
                              "value" : [ "3 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "29",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "30",
                           "s" : [ {
                              "value" : [ "5 ","'m'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "32",
               "type" : "PopulationStdDev",
               "source" : {
                  "localId" : "31",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "26",
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "27",
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "28",
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "29",
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "localId" : "30",
                     "value" : 5,
                     "unit" : "m",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "localId" : "41",
            "name" : "q_throw2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "41",
                  "s" : [ {
                     "value" : [ "define ","q_throw2",": " ]
                  }, {
                     "r" : "40",
                     "s" : [ {
                        "value" : [ "PopulationStdDev","(" ]
                     }, {
                        "r" : "39",
                        "s" : [ {
                           "value" : [ "{","1"," ,","2"," ,","3"," ," ]
                        }, {
                           "r" : "37",
                           "s" : [ {
                              "value" : [ "4 ","'ml'" ]
                           } ]
                        }, {
                           "value" : [ ",","5"," }" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "40",
               "type" : "PopulationStdDev",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "localId" : "39",
                        "type" : "List",
                        "element" : [ {
                           "localId" : "34",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "localId" : "35",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "localId" : "36",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "localId" : "37",
                           "value" : 4,
                           "unit" : "ml",
                           "type" : "Quantity"
                        }, {
                           "localId" : "38",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "type" : "ToDecimal",
                        "operand" : {
                           "name" : "X",
                           "type" : "AliasRef"
                        }
                     }
                  }
               }
            }
         } ]
      }
   }
}

### AllTrue
library TestSnippet version '1'
using QUICK
context Patient
define at: AllTrue({true,true,true,true})
define atwn: AllTrue({true,true,null,null,true,true})

define atf: AllTrue({true,true,true,false})
define atfwn: AllTrue({true,true,null,null,true,false})
###

module.exports['AllTrue'] = {
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
            "name" : "at",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "8",
                  "s" : [ {
                     "value" : [ "define ","at",": " ]
                  }, {
                     "r" : "7",
                     "s" : [ {
                        "value" : [ "AllTrue","(" ]
                     }, {
                        "r" : "6",
                        "s" : [ {
                           "value" : [ "{","true",",","true",",","true",",","true","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "7",
               "type" : "AllTrue",
               "source" : {
                  "localId" : "6",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "2",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "localId" : "3",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "localId" : "4",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "17",
            "name" : "atwn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "17",
                  "s" : [ {
                     "value" : [ "define ","atwn",": " ]
                  }, {
                     "r" : "16",
                     "s" : [ {
                        "value" : [ "AllTrue","(" ]
                     }, {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "{","true",",","true",",","null",",","null",",","true",",","true","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "16",
               "type" : "AllTrue",
               "source" : {
                  "localId" : "15",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "localId" : "10",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "localId" : "11",
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "localId" : "12",
                        "type" : "Null"
                     }
                  }, {
                     "localId" : "13",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "localId" : "14",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "24",
            "name" : "atf",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "24",
                  "s" : [ {
                     "value" : [ "define ","atf",": " ]
                  }, {
                     "r" : "23",
                     "s" : [ {
                        "value" : [ "AllTrue","(" ]
                     }, {
                        "r" : "22",
                        "s" : [ {
                           "value" : [ "{","true",",","true",",","true",",","false","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "23",
               "type" : "AllTrue",
               "source" : {
                  "localId" : "22",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "18",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "localId" : "19",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "localId" : "20",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "localId" : "21",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "33",
            "name" : "atfwn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "33",
                  "s" : [ {
                     "value" : [ "define ","atfwn",": " ]
                  }, {
                     "r" : "32",
                     "s" : [ {
                        "value" : [ "AllTrue","(" ]
                     }, {
                        "r" : "31",
                        "s" : [ {
                           "value" : [ "{","true",",","true",",","null",",","null",",","true",",","false","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "32",
               "type" : "AllTrue",
               "source" : {
                  "localId" : "31",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "25",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "localId" : "26",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "localId" : "27",
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "localId" : "28",
                        "type" : "Null"
                     }
                  }, {
                     "localId" : "29",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "localId" : "30",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  } ]
               }
            }
         } ]
      }
   }
}

### AnyTrue
library TestSnippet version '1'
using QUICK
context Patient
define at: AnyTrue({true,false,false,true})
define atwn: AnyTrue({true,false,null,null,false,true})

define atf: AnyTrue({false,false,false,false})
define atfwn: AnyTrue({false,false,null,null,false,false})
###

module.exports['AnyTrue'] = {
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
            "name" : "at",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "8",
                  "s" : [ {
                     "value" : [ "define ","at",": " ]
                  }, {
                     "r" : "7",
                     "s" : [ {
                        "value" : [ "AnyTrue","(" ]
                     }, {
                        "r" : "6",
                        "s" : [ {
                           "value" : [ "{","true",",","false",",","false",",","true","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "7",
               "type" : "AnyTrue",
               "source" : {
                  "localId" : "6",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "2",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "localId" : "3",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "localId" : "4",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "17",
            "name" : "atwn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "17",
                  "s" : [ {
                     "value" : [ "define ","atwn",": " ]
                  }, {
                     "r" : "16",
                     "s" : [ {
                        "value" : [ "AnyTrue","(" ]
                     }, {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "{","true",",","false",",","null",",","null",",","false",",","true","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "16",
               "type" : "AnyTrue",
               "source" : {
                  "localId" : "15",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "localId" : "10",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "localId" : "11",
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "localId" : "12",
                        "type" : "Null"
                     }
                  }, {
                     "localId" : "13",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "localId" : "14",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "24",
            "name" : "atf",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "24",
                  "s" : [ {
                     "value" : [ "define ","atf",": " ]
                  }, {
                     "r" : "23",
                     "s" : [ {
                        "value" : [ "AnyTrue","(" ]
                     }, {
                        "r" : "22",
                        "s" : [ {
                           "value" : [ "{","false",",","false",",","false",",","false","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "23",
               "type" : "AnyTrue",
               "source" : {
                  "localId" : "22",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "18",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "localId" : "19",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "localId" : "20",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "localId" : "21",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "33",
            "name" : "atfwn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "33",
                  "s" : [ {
                     "value" : [ "define ","atfwn",": " ]
                  }, {
                     "r" : "32",
                     "s" : [ {
                        "value" : [ "AnyTrue","(" ]
                     }, {
                        "r" : "31",
                        "s" : [ {
                           "value" : [ "{","false",",","false",",","null",",","null",",","false",",","false","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "32",
               "type" : "AnyTrue",
               "source" : {
                  "localId" : "31",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "25",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "localId" : "26",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "localId" : "27",
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "localId" : "28",
                        "type" : "Null"
                     }
                  }, {
                     "localId" : "29",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "localId" : "30",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  } ]
               }
            }
         } ]
      }
   }
}

