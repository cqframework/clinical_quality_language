###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### List
library TestSnippet version '1'
using QUICK
context Patient
define Three: 1 + 2
define IntList: { 9, 7, 8 }
define StringList: { 'a', 'bee', 'see' }
define MixedList: List<Any>{ 1, 'two', Three }
define EmptyList: List<Integer>{}
###

module.exports['List'] = {
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
            "name" : "Three",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","Three",": " ]
                  }, {
                     "r" : "4",
                     "s" : [ {
                        "r" : "2",
                        "value" : [ "1"," + ","2" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "type" : "Add",
               "operand" : [ {
                  "localId" : "2",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "localId" : "3",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "10",
            "name" : "IntList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "10",
                  "s" : [ {
                     "value" : [ "define ","IntList",": " ]
                  }, {
                     "r" : "9",
                     "s" : [ {
                        "value" : [ "{ ","9",", ","7",", ","8"," }" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "9",
               "type" : "List",
               "element" : [ {
                  "localId" : "6",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "9",
                  "type" : "Literal"
               }, {
                  "localId" : "7",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "7",
                  "type" : "Literal"
               }, {
                  "localId" : "8",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "8",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "15",
            "name" : "StringList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "15",
                  "s" : [ {
                     "value" : [ "define ","StringList",": " ]
                  }, {
                     "r" : "14",
                     "s" : [ {
                        "value" : [ "{ " ]
                     }, {
                        "r" : "11",
                        "s" : [ {
                           "value" : [ "'a'" ]
                        } ]
                     }, {
                        "value" : [ ", " ]
                     }, {
                        "r" : "12",
                        "s" : [ {
                           "value" : [ "'bee'" ]
                        } ]
                     }, {
                        "value" : [ ", " ]
                     }, {
                        "r" : "13",
                        "s" : [ {
                           "value" : [ "'see'" ]
                        } ]
                     }, {
                        "value" : [ " }" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "14",
               "type" : "List",
               "element" : [ {
                  "localId" : "11",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "a",
                  "type" : "Literal"
               }, {
                  "localId" : "12",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "bee",
                  "type" : "Literal"
               }, {
                  "localId" : "13",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "see",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "21",
            "name" : "MixedList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "21",
                  "s" : [ {
                     "value" : [ "define ","MixedList",": " ]
                  }, {
                     "r" : "20",
                     "s" : [ {
                        "value" : [ "List<" ]
                     }, {
                        "r" : "16",
                        "s" : [ {
                           "value" : [ "Any" ]
                        } ]
                     }, {
                        "value" : [ ">{ ","1",", " ]
                     }, {
                        "r" : "18",
                        "s" : [ {
                           "value" : [ "'two'" ]
                        } ]
                     }, {
                        "value" : [ ", " ]
                     }, {
                        "r" : "19",
                        "s" : [ {
                           "value" : [ "Three" ]
                        } ]
                     }, {
                        "value" : [ " }" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "20",
               "type" : "List",
               "element" : [ {
                  "localId" : "17",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "localId" : "18",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "two",
                  "type" : "Literal"
               }, {
                  "localId" : "19",
                  "name" : "Three",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "localId" : "24",
            "name" : "EmptyList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "24",
                  "s" : [ {
                     "value" : [ "define ","EmptyList",": " ]
                  }, {
                     "r" : "23",
                     "s" : [ {
                        "value" : [ "List<" ]
                     }, {
                        "r" : "22",
                        "s" : [ {
                           "value" : [ "Integer" ]
                        } ]
                     }, {
                        "value" : [ ">{}" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "23",
               "type" : "List"
            }
         } ]
      }
   }
}

### Exists
library TestSnippet version '1'
using QUICK
context Patient
define EmptyList: exists (List<Integer>{})
define FullList: exists ({ 1, 2, 3 })
###

module.exports['Exists'] = {
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
            "name" : "EmptyList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","EmptyList",": " ]
                  }, {
                     "r" : "4",
                     "s" : [ {
                        "value" : [ "exists " ]
                     }, {
                        "r" : "3",
                        "s" : [ {
                           "value" : [ "(" ]
                        }, {
                           "r" : "3",
                           "s" : [ {
                              "value" : [ "List<" ]
                           }, {
                              "r" : "2",
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
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "type" : "Exists",
               "operand" : {
                  "localId" : "3",
                  "type" : "List"
               }
            }
         }, {
            "localId" : "11",
            "name" : "FullList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","FullList",": " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "value" : [ "exists " ]
                     }, {
                        "r" : "9",
                        "s" : [ {
                           "value" : [ "(" ]
                        }, {
                           "r" : "9",
                           "s" : [ {
                              "value" : [ "{ ","1",", ","2",", ","3"," }" ]
                           } ]
                        }, {
                           "value" : [ ")" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "type" : "Exists",
               "operand" : {
                  "localId" : "9",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "6",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "7",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "8",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               }
            }
         } ]
      }
   }
}

### Equal
library TestSnippet version '1'
using QUICK
context Patient
define EqualIntList: {1, 2, 3} = {1, 2, 3}
define UnequalIntList: {1, 2, 3} = {1, 2}
define ReverseIntList: {1, 2, 3} = {3, 2, 1}
define EqualStringList: {'hello', 'world'} = {'hello', 'world'}
define UnequalStringList: {'hello', 'world'} = {'foo', 'bar'}
define EqualTupleList: List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} } = List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} }
define UnequalTupleList: List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} } = List<Any>{ Tuple{a: 1, b: Tuple{c: -1}}, Tuple{x: 'y', z: 2} }
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
            "name" : "EqualIntList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","EqualIntList",": " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "r" : "5",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3","}" ]
                        } ]
                     }, {
                        "value" : [ " ","="," " ]
                     }, {
                        "r" : "9",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "type" : "Equal",
               "operand" : [ {
                  "localId" : "5",
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
                  } ]
               }, {
                  "localId" : "9",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "6",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "7",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "8",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "20",
            "name" : "UnequalIntList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "20",
                  "s" : [ {
                     "value" : [ "define ","UnequalIntList",": " ]
                  }, {
                     "r" : "19",
                     "s" : [ {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3","}" ]
                        } ]
                     }, {
                        "value" : [ " ","="," " ]
                     }, {
                        "r" : "18",
                        "s" : [ {
                           "value" : [ "{","1",", ","2","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "19",
               "type" : "Equal",
               "operand" : [ {
                  "localId" : "15",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "12",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "13",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "14",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "18",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "16",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "17",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "30",
            "name" : "ReverseIntList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "30",
                  "s" : [ {
                     "value" : [ "define ","ReverseIntList",": " ]
                  }, {
                     "r" : "29",
                     "s" : [ {
                        "r" : "24",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3","}" ]
                        } ]
                     }, {
                        "value" : [ " ","="," " ]
                     }, {
                        "r" : "28",
                        "s" : [ {
                           "value" : [ "{","3",", ","2",", ","1","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "29",
               "type" : "Equal",
               "operand" : [ {
                  "localId" : "24",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "21",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "22",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "23",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "28",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "25",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "26",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "27",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "38",
            "name" : "EqualStringList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "38",
                  "s" : [ {
                     "value" : [ "define ","EqualStringList",": " ]
                  }, {
                     "r" : "37",
                     "s" : [ {
                        "r" : "33",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "31",
                           "s" : [ {
                              "value" : [ "'hello'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "32",
                           "s" : [ {
                              "value" : [ "'world'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " ","="," " ]
                     }, {
                        "r" : "36",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "34",
                           "s" : [ {
                              "value" : [ "'hello'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "35",
                           "s" : [ {
                              "value" : [ "'world'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "37",
               "type" : "Equal",
               "operand" : [ {
                  "localId" : "33",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "31",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "hello",
                     "type" : "Literal"
                  }, {
                     "localId" : "32",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "world",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "36",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "34",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "hello",
                     "type" : "Literal"
                  }, {
                     "localId" : "35",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "world",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "46",
            "name" : "UnequalStringList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "46",
                  "s" : [ {
                     "value" : [ "define ","UnequalStringList",": " ]
                  }, {
                     "r" : "45",
                     "s" : [ {
                        "r" : "41",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "39",
                           "s" : [ {
                              "value" : [ "'hello'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "40",
                           "s" : [ {
                              "value" : [ "'world'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " ","="," " ]
                     }, {
                        "r" : "44",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "42",
                           "s" : [ {
                              "value" : [ "'foo'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "43",
                           "s" : [ {
                              "value" : [ "'bar'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "45",
               "type" : "Equal",
               "operand" : [ {
                  "localId" : "41",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "39",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "hello",
                     "type" : "Literal"
                  }, {
                     "localId" : "40",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "world",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "44",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "42",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "foo",
                     "type" : "Literal"
                  }, {
                     "localId" : "43",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "bar",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "66",
            "name" : "EqualTupleList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "66",
                  "s" : [ {
                     "value" : [ "define ","EqualTupleList",": " ]
                  }, {
                     "r" : "65",
                     "s" : [ {
                        "r" : "55",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "47",
                           "s" : [ {
                              "value" : [ "Any" ]
                           } ]
                        }, {
                           "value" : [ ">{ " ]
                        }, {
                           "r" : "51",
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
                                 "value" : [ "b",": " ]
                              }, {
                                 "r" : "50",
                                 "s" : [ {
                                    "value" : [ "Tuple{" ]
                                 }, {
                                    "s" : [ {
                                       "value" : [ "c",": ","1" ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "54",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "x",": " ]
                              }, {
                                 "r" : "52",
                                 "s" : [ {
                                    "value" : [ "'y'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "z",": ","2" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ " }" ]
                        } ]
                     }, {
                        "value" : [ " ","="," " ]
                     }, {
                        "r" : "64",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "56",
                           "s" : [ {
                              "value" : [ "Any" ]
                           } ]
                        }, {
                           "value" : [ ">{ " ]
                        }, {
                           "r" : "60",
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
                                 "value" : [ "b",": " ]
                              }, {
                                 "r" : "59",
                                 "s" : [ {
                                    "value" : [ "Tuple{" ]
                                 }, {
                                    "s" : [ {
                                       "value" : [ "c",": ","1" ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "63",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "x",": " ]
                              }, {
                                 "r" : "61",
                                 "s" : [ {
                                    "value" : [ "'y'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "z",": ","2" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ " }" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "65",
               "type" : "Equal",
               "operand" : [ {
                  "localId" : "55",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "51",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "48",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "50",
                           "type" : "Tuple",
                           "element" : [ {
                              "name" : "c",
                              "value" : {
                                 "localId" : "49",
                                 "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                                 "value" : "1",
                                 "type" : "Literal"
                              }
                           } ]
                        }
                     } ]
                  }, {
                     "localId" : "54",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "x",
                        "value" : {
                           "localId" : "52",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "y",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "z",
                        "value" : {
                           "localId" : "53",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "localId" : "64",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "60",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "57",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "59",
                           "type" : "Tuple",
                           "element" : [ {
                              "name" : "c",
                              "value" : {
                                 "localId" : "58",
                                 "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                                 "value" : "1",
                                 "type" : "Literal"
                              }
                           } ]
                        }
                     } ]
                  }, {
                     "localId" : "63",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "x",
                        "value" : {
                           "localId" : "61",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "y",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "z",
                        "value" : {
                           "localId" : "62",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "localId" : "87",
            "name" : "UnequalTupleList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "87",
                  "s" : [ {
                     "value" : [ "define ","UnequalTupleList",": " ]
                  }, {
                     "r" : "86",
                     "s" : [ {
                        "r" : "75",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "67",
                           "s" : [ {
                              "value" : [ "Any" ]
                           } ]
                        }, {
                           "value" : [ ">{ " ]
                        }, {
                           "r" : "71",
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
                                 "value" : [ "b",": " ]
                              }, {
                                 "r" : "70",
                                 "s" : [ {
                                    "value" : [ "Tuple{" ]
                                 }, {
                                    "s" : [ {
                                       "value" : [ "c",": ","1" ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "74",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "x",": " ]
                              }, {
                                 "r" : "72",
                                 "s" : [ {
                                    "value" : [ "'y'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "z",": ","2" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ " }" ]
                        } ]
                     }, {
                        "value" : [ " ","="," " ]
                     }, {
                        "r" : "85",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "76",
                           "s" : [ {
                              "value" : [ "Any" ]
                           } ]
                        }, {
                           "value" : [ ">{ " ]
                        }, {
                           "r" : "81",
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
                                 "value" : [ "b",": " ]
                              }, {
                                 "r" : "80",
                                 "s" : [ {
                                    "value" : [ "Tuple{" ]
                                 }, {
                                    "s" : [ {
                                       "value" : [ "c",": " ]
                                    }, {
                                       "r" : "79",
                                       "s" : [ {
                                          "value" : [ "-","1" ]
                                       } ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "84",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "x",": " ]
                              }, {
                                 "r" : "82",
                                 "s" : [ {
                                    "value" : [ "'y'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "z",": ","2" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ " }" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "86",
               "type" : "Equal",
               "operand" : [ {
                  "localId" : "75",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "71",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "68",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "70",
                           "type" : "Tuple",
                           "element" : [ {
                              "name" : "c",
                              "value" : {
                                 "localId" : "69",
                                 "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                                 "value" : "1",
                                 "type" : "Literal"
                              }
                           } ]
                        }
                     } ]
                  }, {
                     "localId" : "74",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "x",
                        "value" : {
                           "localId" : "72",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "y",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "z",
                        "value" : {
                           "localId" : "73",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "localId" : "85",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "81",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "77",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "80",
                           "type" : "Tuple",
                           "element" : [ {
                              "name" : "c",
                              "value" : {
                                 "localId" : "79",
                                 "type" : "Negate",
                                 "operand" : {
                                    "localId" : "78",
                                    "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                                    "value" : "1",
                                    "type" : "Literal"
                                 }
                              }
                           } ]
                        }
                     } ]
                  }, {
                     "localId" : "84",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "x",
                        "value" : {
                           "localId" : "82",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "y",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "z",
                        "value" : {
                           "localId" : "83",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     } ]
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
define EqualIntList: {1, 2, 3} != {1, 2, 3}
define UnequalIntList: {1, 2, 3} != {1, 2}
define ReverseIntList: {1, 2, 3} != {3, 2, 1}
define EqualStringList: {'hello', 'world'} != {'hello', 'world'}
define UnequalStringList: {'hello', 'world'} != {'foo', 'bar'}
define EqualTupleList: List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} } != List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} }
define UnequalTupleList: List<Any>{ Tuple{a: 1, b: Tuple{c: 1}}, Tuple{x: 'y', z: 2} } != List<Any>{ Tuple{a: 1, b: Tuple{c: -1}}, Tuple{x: 'y', z: 2} }
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
            "name" : "EqualIntList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","EqualIntList",": " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "r" : "5",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3","}" ]
                        } ]
                     }, {
                        "value" : [ " ","!="," " ]
                     }, {
                        "r" : "9",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "localId" : "5",
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
                     } ]
                  }, {
                     "localId" : "9",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "6",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "localId" : "7",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "8",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "localId" : "20",
            "name" : "UnequalIntList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "20",
                  "s" : [ {
                     "value" : [ "define ","UnequalIntList",": " ]
                  }, {
                     "r" : "19",
                     "s" : [ {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3","}" ]
                        } ]
                     }, {
                        "value" : [ " ","!="," " ]
                     }, {
                        "r" : "18",
                        "s" : [ {
                           "value" : [ "{","1",", ","2","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "19",
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "localId" : "15",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "12",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "localId" : "13",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "14",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     } ]
                  }, {
                     "localId" : "18",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "16",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "localId" : "17",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "localId" : "30",
            "name" : "ReverseIntList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "30",
                  "s" : [ {
                     "value" : [ "define ","ReverseIntList",": " ]
                  }, {
                     "r" : "29",
                     "s" : [ {
                        "r" : "24",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3","}" ]
                        } ]
                     }, {
                        "value" : [ " ","!="," " ]
                     }, {
                        "r" : "28",
                        "s" : [ {
                           "value" : [ "{","3",", ","2",", ","1","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "29",
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "localId" : "24",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "21",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "localId" : "22",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "23",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     } ]
                  }, {
                     "localId" : "28",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "25",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "26",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "27",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "localId" : "38",
            "name" : "EqualStringList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "38",
                  "s" : [ {
                     "value" : [ "define ","EqualStringList",": " ]
                  }, {
                     "r" : "37",
                     "s" : [ {
                        "r" : "33",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "31",
                           "s" : [ {
                              "value" : [ "'hello'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "32",
                           "s" : [ {
                              "value" : [ "'world'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " ","!="," " ]
                     }, {
                        "r" : "36",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "34",
                           "s" : [ {
                              "value" : [ "'hello'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "35",
                           "s" : [ {
                              "value" : [ "'world'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "37",
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "localId" : "33",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "31",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "hello",
                        "type" : "Literal"
                     }, {
                        "localId" : "32",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "world",
                        "type" : "Literal"
                     } ]
                  }, {
                     "localId" : "36",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "34",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "hello",
                        "type" : "Literal"
                     }, {
                        "localId" : "35",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "world",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "localId" : "46",
            "name" : "UnequalStringList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "46",
                  "s" : [ {
                     "value" : [ "define ","UnequalStringList",": " ]
                  }, {
                     "r" : "45",
                     "s" : [ {
                        "r" : "41",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "39",
                           "s" : [ {
                              "value" : [ "'hello'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "40",
                           "s" : [ {
                              "value" : [ "'world'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " ","!="," " ]
                     }, {
                        "r" : "44",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "42",
                           "s" : [ {
                              "value" : [ "'foo'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "43",
                           "s" : [ {
                              "value" : [ "'bar'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "45",
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "localId" : "41",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "39",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "hello",
                        "type" : "Literal"
                     }, {
                        "localId" : "40",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "world",
                        "type" : "Literal"
                     } ]
                  }, {
                     "localId" : "44",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "42",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "foo",
                        "type" : "Literal"
                     }, {
                        "localId" : "43",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "bar",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "localId" : "66",
            "name" : "EqualTupleList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "66",
                  "s" : [ {
                     "value" : [ "define ","EqualTupleList",": " ]
                  }, {
                     "r" : "65",
                     "s" : [ {
                        "r" : "55",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "47",
                           "s" : [ {
                              "value" : [ "Any" ]
                           } ]
                        }, {
                           "value" : [ ">{ " ]
                        }, {
                           "r" : "51",
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
                                 "value" : [ "b",": " ]
                              }, {
                                 "r" : "50",
                                 "s" : [ {
                                    "value" : [ "Tuple{" ]
                                 }, {
                                    "s" : [ {
                                       "value" : [ "c",": ","1" ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "54",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "x",": " ]
                              }, {
                                 "r" : "52",
                                 "s" : [ {
                                    "value" : [ "'y'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "z",": ","2" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ " }" ]
                        } ]
                     }, {
                        "value" : [ " ","!="," " ]
                     }, {
                        "r" : "64",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "56",
                           "s" : [ {
                              "value" : [ "Any" ]
                           } ]
                        }, {
                           "value" : [ ">{ " ]
                        }, {
                           "r" : "60",
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
                                 "value" : [ "b",": " ]
                              }, {
                                 "r" : "59",
                                 "s" : [ {
                                    "value" : [ "Tuple{" ]
                                 }, {
                                    "s" : [ {
                                       "value" : [ "c",": ","1" ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "63",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "x",": " ]
                              }, {
                                 "r" : "61",
                                 "s" : [ {
                                    "value" : [ "'y'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "z",": ","2" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ " }" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "65",
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "localId" : "55",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "51",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "localId" : "48",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "localId" : "50",
                              "type" : "Tuple",
                              "element" : [ {
                                 "name" : "c",
                                 "value" : {
                                    "localId" : "49",
                                    "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                                    "value" : "1",
                                    "type" : "Literal"
                                 }
                              } ]
                           }
                        } ]
                     }, {
                        "localId" : "54",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "x",
                           "value" : {
                              "localId" : "52",
                              "valueType" : "{urn:hl7-org:elm-types:r1}String",
                              "value" : "y",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "z",
                           "value" : {
                              "localId" : "53",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     } ]
                  }, {
                     "localId" : "64",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "60",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "localId" : "57",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "localId" : "59",
                              "type" : "Tuple",
                              "element" : [ {
                                 "name" : "c",
                                 "value" : {
                                    "localId" : "58",
                                    "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                                    "value" : "1",
                                    "type" : "Literal"
                                 }
                              } ]
                           }
                        } ]
                     }, {
                        "localId" : "63",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "x",
                           "value" : {
                              "localId" : "61",
                              "valueType" : "{urn:hl7-org:elm-types:r1}String",
                              "value" : "y",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "z",
                           "value" : {
                              "localId" : "62",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     } ]
                  } ]
               }
            }
         }, {
            "localId" : "87",
            "name" : "UnequalTupleList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "87",
                  "s" : [ {
                     "value" : [ "define ","UnequalTupleList",": " ]
                  }, {
                     "r" : "86",
                     "s" : [ {
                        "r" : "75",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "67",
                           "s" : [ {
                              "value" : [ "Any" ]
                           } ]
                        }, {
                           "value" : [ ">{ " ]
                        }, {
                           "r" : "71",
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
                                 "value" : [ "b",": " ]
                              }, {
                                 "r" : "70",
                                 "s" : [ {
                                    "value" : [ "Tuple{" ]
                                 }, {
                                    "s" : [ {
                                       "value" : [ "c",": ","1" ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "74",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "x",": " ]
                              }, {
                                 "r" : "72",
                                 "s" : [ {
                                    "value" : [ "'y'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "z",": ","2" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ " }" ]
                        } ]
                     }, {
                        "value" : [ " ","!="," " ]
                     }, {
                        "r" : "85",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "76",
                           "s" : [ {
                              "value" : [ "Any" ]
                           } ]
                        }, {
                           "value" : [ ">{ " ]
                        }, {
                           "r" : "81",
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
                                 "value" : [ "b",": " ]
                              }, {
                                 "r" : "80",
                                 "s" : [ {
                                    "value" : [ "Tuple{" ]
                                 }, {
                                    "s" : [ {
                                       "value" : [ "c",": " ]
                                    }, {
                                       "r" : "79",
                                       "s" : [ {
                                          "value" : [ "-","1" ]
                                       } ]
                                    } ]
                                 }, {
                                    "value" : [ "}" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "84",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "x",": " ]
                              }, {
                                 "r" : "82",
                                 "s" : [ {
                                    "value" : [ "'y'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "z",": ","2" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ " }" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "86",
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "localId" : "75",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "71",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "localId" : "68",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "localId" : "70",
                              "type" : "Tuple",
                              "element" : [ {
                                 "name" : "c",
                                 "value" : {
                                    "localId" : "69",
                                    "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                                    "value" : "1",
                                    "type" : "Literal"
                                 }
                              } ]
                           }
                        } ]
                     }, {
                        "localId" : "74",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "x",
                           "value" : {
                              "localId" : "72",
                              "valueType" : "{urn:hl7-org:elm-types:r1}String",
                              "value" : "y",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "z",
                           "value" : {
                              "localId" : "73",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     } ]
                  }, {
                     "localId" : "85",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "81",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "localId" : "77",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "localId" : "80",
                              "type" : "Tuple",
                              "element" : [ {
                                 "name" : "c",
                                 "value" : {
                                    "localId" : "79",
                                    "type" : "Negate",
                                    "operand" : {
                                       "localId" : "78",
                                       "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                                       "value" : "1",
                                       "type" : "Literal"
                                    }
                                 }
                              } ]
                           }
                        } ]
                     }, {
                        "localId" : "84",
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "x",
                           "value" : {
                              "localId" : "82",
                              "valueType" : "{urn:hl7-org:elm-types:r1}String",
                              "value" : "y",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "z",
                           "value" : {
                              "localId" : "83",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     } ]
                  } ]
               }
            }
         } ]
      }
   }
}

### Union
library TestSnippet version '1'
using QUICK
context Patient
define OneToTen: {1, 2, 3, 4, 5} union {6, 7, 8, 9, 10}
define OneToFiveOverlapped: {1, 2, 3, 4} union {3, 4, 5}
define Disjoint: {1, 2} union {4, 5}
define NestedToFifteen: {1, 2, 3} union {4, 5, 6} union {7 ,8 , 9} union {10, 11, 12} union {13, 14, 15}
define NullUnion: null union {1, 2, 3}
define UnionNull: {1, 2, 3} union null
###

module.exports['Union'] = {
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
            "localId" : "15",
            "name" : "OneToTen",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "15",
                  "s" : [ {
                     "value" : [ "define ","OneToTen",": " ]
                  }, {
                     "r" : "14",
                     "s" : [ {
                        "r" : "7",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " union " ]
                     }, {
                        "r" : "13",
                        "s" : [ {
                           "value" : [ "{","6",", ","7",", ","8",", ","9",", ","10","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "14",
               "type" : "Union",
               "operand" : [ {
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
               }, {
                  "localId" : "13",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "8",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "7",
                     "type" : "Literal"
                  }, {
                     "localId" : "10",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "localId" : "11",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "9",
                     "type" : "Literal"
                  }, {
                     "localId" : "12",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "26",
            "name" : "OneToFiveOverlapped",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "26",
                  "s" : [ {
                     "value" : [ "define ","OneToFiveOverlapped",": " ]
                  }, {
                     "r" : "25",
                     "s" : [ {
                        "r" : "20",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4","}" ]
                        } ]
                     }, {
                        "value" : [ " union " ]
                     }, {
                        "r" : "24",
                        "s" : [ {
                           "value" : [ "{","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "25",
               "type" : "Union",
               "operand" : [ {
                  "localId" : "20",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "16",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "17",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "18",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "19",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "24",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "21",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "22",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "23",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "34",
            "name" : "Disjoint",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "34",
                  "s" : [ {
                     "value" : [ "define ","Disjoint",": " ]
                  }, {
                     "r" : "33",
                     "s" : [ {
                        "r" : "29",
                        "s" : [ {
                           "value" : [ "{","1",", ","2","}" ]
                        } ]
                     }, {
                        "value" : [ " union " ]
                     }, {
                        "r" : "32",
                        "s" : [ {
                           "value" : [ "{","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "33",
               "type" : "Union",
               "operand" : [ {
                  "localId" : "29",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "27",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "28",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "32",
                  "type" : "List",
                  "element" : [ {
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
               } ]
            }
         }, {
            "localId" : "59",
            "name" : "NestedToFifteen",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "59",
                  "s" : [ {
                     "value" : [ "define ","NestedToFifteen",": " ]
                  }, {
                     "r" : "58",
                     "s" : [ {
                        "r" : "53",
                        "s" : [ {
                           "r" : "48",
                           "s" : [ {
                              "r" : "43",
                              "s" : [ {
                                 "r" : "38",
                                 "s" : [ {
                                    "value" : [ "{","1",", ","2",", ","3","}" ]
                                 } ]
                              }, {
                                 "value" : [ " union " ]
                              }, {
                                 "r" : "42",
                                 "s" : [ {
                                    "value" : [ "{","4",", ","5",", ","6","}" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " union " ]
                           }, {
                              "r" : "47",
                              "s" : [ {
                                 "value" : [ "{","7"," ,","8"," , ","9","}" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ " union " ]
                        }, {
                           "r" : "52",
                           "s" : [ {
                              "value" : [ "{","10",", ","11",", ","12","}" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " union " ]
                     }, {
                        "r" : "57",
                        "s" : [ {
                           "value" : [ "{","13",", ","14",", ","15","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "58",
               "type" : "Union",
               "operand" : [ {
                  "localId" : "53",
                  "type" : "Union",
                  "operand" : [ {
                     "localId" : "48",
                     "type" : "Union",
                     "operand" : [ {
                        "localId" : "43",
                        "type" : "Union",
                        "operand" : [ {
                           "localId" : "38",
                           "type" : "List",
                           "element" : [ {
                              "localId" : "35",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }, {
                              "localId" : "36",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }, {
                              "localId" : "37",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "3",
                              "type" : "Literal"
                           } ]
                        }, {
                           "localId" : "42",
                           "type" : "List",
                           "element" : [ {
                              "localId" : "39",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "4",
                              "type" : "Literal"
                           }, {
                              "localId" : "40",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "5",
                              "type" : "Literal"
                           }, {
                              "localId" : "41",
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "6",
                              "type" : "Literal"
                           } ]
                        } ]
                     }, {
                        "localId" : "47",
                        "type" : "List",
                        "element" : [ {
                           "localId" : "44",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "7",
                           "type" : "Literal"
                        }, {
                           "localId" : "45",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "8",
                           "type" : "Literal"
                        }, {
                           "localId" : "46",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "9",
                           "type" : "Literal"
                        } ]
                     } ]
                  }, {
                     "localId" : "52",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "49",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "10",
                        "type" : "Literal"
                     }, {
                        "localId" : "50",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "11",
                        "type" : "Literal"
                     }, {
                        "localId" : "51",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "12",
                        "type" : "Literal"
                     } ]
                  } ]
               }, {
                  "localId" : "57",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "54",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "13",
                     "type" : "Literal"
                  }, {
                     "localId" : "55",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "14",
                     "type" : "Literal"
                  }, {
                     "localId" : "56",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "66",
            "name" : "NullUnion",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "66",
                  "s" : [ {
                     "value" : [ "define ","NullUnion",": " ]
                  }, {
                     "r" : "65",
                     "s" : [ {
                        "r" : "60",
                        "value" : [ "null"," union " ]
                     }, {
                        "r" : "64",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "65",
               "type" : "Union",
               "operand" : [ {
                  "type" : "As",
                  "operand" : {
                     "localId" : "60",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               }, {
                  "localId" : "64",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "61",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "62",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "63",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "73",
            "name" : "UnionNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "73",
                  "s" : [ {
                     "value" : [ "define ","UnionNull",": " ]
                  }, {
                     "r" : "72",
                     "s" : [ {
                        "r" : "70",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3","}" ]
                        } ]
                     }, {
                        "value" : [ " union ","null" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "72",
               "type" : "Union",
               "operand" : [ {
                  "localId" : "70",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "67",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "68",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "69",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "As",
                  "operand" : {
                     "localId" : "71",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               } ]
            }
         } ]
      }
   }
}

### Except
library TestSnippet version '1'
using QUICK
context Patient
define ExceptThreeFour: {1, 2, 3, 4, 5} except {3, 4}
define ThreeFourExcept: {3, 4} except {1, 2, 3, 4, 5}
define ExceptFiveThree: {1, 2, 3, 4, 5} except {5, 3}
define ExceptNoOp: {1, 2, 3, 4, 5} except {6, 7, 8, 9, 10}
define ExceptEverything: {1, 2, 3, 4, 5} except {1, 2, 3, 4, 5}
define SomethingExceptNothing: {1, 2, 3, 4, 5} except List<Integer>{}
define NothingExceptSomething: List<Integer>{} except {1, 2, 3, 4, 5}
define ExceptTuples: {Tuple{a: 1}, Tuple{a: 2}, Tuple{a: 3}} except {Tuple{a: 2}}
define ExceptNull: {1, 2, 3, 4, 5} except null
define NullExcept: null except {1, 2, 3, 4, 5}
###

module.exports['Except'] = {
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
            "name" : "ExceptThreeFour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "12",
                  "s" : [ {
                     "value" : [ "define ","ExceptThreeFour",": " ]
                  }, {
                     "r" : "11",
                     "s" : [ {
                        "r" : "7",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " except " ]
                     }, {
                        "r" : "10",
                        "s" : [ {
                           "value" : [ "{","3",", ","4","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "11",
               "type" : "Except",
               "operand" : [ {
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
               }, {
                  "localId" : "10",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "8",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "23",
            "name" : "ThreeFourExcept",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "23",
                  "s" : [ {
                     "value" : [ "define ","ThreeFourExcept",": " ]
                  }, {
                     "r" : "22",
                     "s" : [ {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "{","3",", ","4","}" ]
                        } ]
                     }, {
                        "value" : [ " except " ]
                     }, {
                        "r" : "21",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "22",
               "type" : "Except",
               "operand" : [ {
                  "localId" : "15",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "13",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "14",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "21",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "16",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "17",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "18",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "19",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "20",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "34",
            "name" : "ExceptFiveThree",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "34",
                  "s" : [ {
                     "value" : [ "define ","ExceptFiveThree",": " ]
                  }, {
                     "r" : "33",
                     "s" : [ {
                        "r" : "29",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " except " ]
                     }, {
                        "r" : "32",
                        "s" : [ {
                           "value" : [ "{","5",", ","3","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "33",
               "type" : "Except",
               "operand" : [ {
                  "localId" : "29",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "24",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "25",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "26",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "27",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "28",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "32",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "30",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "31",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "48",
            "name" : "ExceptNoOp",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "48",
                  "s" : [ {
                     "value" : [ "define ","ExceptNoOp",": " ]
                  }, {
                     "r" : "47",
                     "s" : [ {
                        "r" : "40",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " except " ]
                     }, {
                        "r" : "46",
                        "s" : [ {
                           "value" : [ "{","6",", ","7",", ","8",", ","9",", ","10","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "47",
               "type" : "Except",
               "operand" : [ {
                  "localId" : "40",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "35",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "36",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "37",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "38",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "39",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "46",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "41",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "localId" : "42",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "7",
                     "type" : "Literal"
                  }, {
                     "localId" : "43",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "localId" : "44",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "9",
                     "type" : "Literal"
                  }, {
                     "localId" : "45",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "62",
            "name" : "ExceptEverything",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "62",
                  "s" : [ {
                     "value" : [ "define ","ExceptEverything",": " ]
                  }, {
                     "r" : "61",
                     "s" : [ {
                        "r" : "54",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " except " ]
                     }, {
                        "r" : "60",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "61",
               "type" : "Except",
               "operand" : [ {
                  "localId" : "54",
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
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "52",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "53",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "60",
                  "type" : "List",
                  "element" : [ {
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
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "59",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "72",
            "name" : "SomethingExceptNothing",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "72",
                  "s" : [ {
                     "value" : [ "define ","SomethingExceptNothing",": " ]
                  }, {
                     "r" : "71",
                     "s" : [ {
                        "r" : "68",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " except " ]
                     }, {
                        "r" : "70",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "69",
                           "s" : [ {
                              "value" : [ "Integer" ]
                           } ]
                        }, {
                           "value" : [ ">{}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "71",
               "type" : "Except",
               "operand" : [ {
                  "localId" : "68",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "63",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "64",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "65",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "66",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "67",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "70",
                  "type" : "List"
               } ]
            }
         }, {
            "localId" : "82",
            "name" : "NothingExceptSomething",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "82",
                  "s" : [ {
                     "value" : [ "define ","NothingExceptSomething",": " ]
                  }, {
                     "r" : "81",
                     "s" : [ {
                        "r" : "74",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "73",
                           "s" : [ {
                              "value" : [ "Integer" ]
                           } ]
                        }, {
                           "value" : [ ">{}" ]
                        } ]
                     }, {
                        "value" : [ " except " ]
                     }, {
                        "r" : "80",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "81",
               "type" : "Except",
               "operand" : [ {
                  "localId" : "74",
                  "type" : "List"
               }, {
                  "localId" : "80",
                  "type" : "List",
                  "element" : [ {
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
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "78",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "79",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "94",
            "name" : "ExceptTuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "94",
                  "s" : [ {
                     "value" : [ "define ","ExceptTuples",": " ]
                  }, {
                     "r" : "93",
                     "s" : [ {
                        "r" : "89",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "84",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",": ","1" ]
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
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "88",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",": ","3" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " except " ]
                     }, {
                        "r" : "92",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "91",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",": ","2" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "93",
               "type" : "Except",
               "operand" : [ {
                  "localId" : "89",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "84",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "83",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "86",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "85",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "88",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "87",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "localId" : "92",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "91",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "90",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "localId" : "103",
            "name" : "ExceptNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "103",
                  "s" : [ {
                     "value" : [ "define ","ExceptNull",": " ]
                  }, {
                     "r" : "102",
                     "s" : [ {
                        "r" : "100",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " except ","null" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "102",
               "type" : "Except",
               "operand" : [ {
                  "localId" : "100",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "95",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "96",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "97",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "98",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "99",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "As",
                  "operand" : {
                     "localId" : "101",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               } ]
            }
         }, {
            "localId" : "112",
            "name" : "NullExcept",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "112",
                  "s" : [ {
                     "value" : [ "define ","NullExcept",": " ]
                  }, {
                     "r" : "111",
                     "s" : [ {
                        "r" : "104",
                        "value" : [ "null"," except " ]
                     }, {
                        "r" : "110",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "111",
               "type" : "Except",
               "operand" : [ {
                  "type" : "As",
                  "operand" : {
                     "localId" : "104",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               }, {
                  "localId" : "110",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "105",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "106",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "107",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "108",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "109",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

### Intersect
library TestSnippet version '1'
using QUICK
context Patient
define NoIntersection: {1, 2, 3} intersect {4, 5, 6}
define IntersectOnFive: {4, 5, 6} intersect {1, 3, 5, 7}
define IntersectOnEvens: {1, 2, 3, 4, 5, 6, 7, 8, 9, 10} intersect {0, 2, 4, 6, 8, 10, 12}
define IntersectOnAll: {1, 2, 3, 4, 5} intersect {5, 4, 3, 2, 1}
define NestedIntersects: {1, 2, 3, 4, 5} intersect {2, 3, 4, 5, 6} intersect {3, 4, 5, 6, 7} intersect {4, 5, 6, 7, 8}
define IntersectTuples: {Tuple{a:1, b:'d'}, Tuple{a:1, b:'c'}, Tuple{a:2, b:'c'}} intersect {Tuple{a:2, b:'d'}, Tuple{a:1, b:'c'}, Tuple{a:2, b:'c'}, Tuple{a:5, b:'e'}}
define NullIntersect: null intersect {1, 2, 3}
define IntersectNull: {1, 2, 3} intersect null
###

module.exports['Intersect'] = {
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
            "name" : "NoIntersection",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","NoIntersection",": " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "r" : "5",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3","}" ]
                        } ]
                     }, {
                        "value" : [ " intersect " ]
                     }, {
                        "r" : "9",
                        "s" : [ {
                           "value" : [ "{","4",", ","5",", ","6","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "type" : "Intersect",
               "operand" : [ {
                  "localId" : "5",
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
                  } ]
               }, {
                  "localId" : "9",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "6",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "7",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "8",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "22",
            "name" : "IntersectOnFive",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "22",
                  "s" : [ {
                     "value" : [ "define ","IntersectOnFive",": " ]
                  }, {
                     "r" : "21",
                     "s" : [ {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "{","4",", ","5",", ","6","}" ]
                        } ]
                     }, {
                        "value" : [ " intersect " ]
                     }, {
                        "r" : "20",
                        "s" : [ {
                           "value" : [ "{","1",", ","3",", ","5",", ","7","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "21",
               "type" : "Intersect",
               "operand" : [ {
                  "localId" : "15",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "12",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "13",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "14",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "20",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "16",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "17",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "18",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "19",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "7",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "43",
            "name" : "IntersectOnEvens",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "43",
                  "s" : [ {
                     "value" : [ "define ","IntersectOnEvens",": " ]
                  }, {
                     "r" : "42",
                     "s" : [ {
                        "r" : "33",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5",", ","6",", ","7",", ","8",", ","9",", ","10","}" ]
                        } ]
                     }, {
                        "value" : [ " intersect " ]
                     }, {
                        "r" : "41",
                        "s" : [ {
                           "value" : [ "{","0",", ","2",", ","4",", ","6",", ","8",", ","10",", ","12","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "42",
               "type" : "Intersect",
               "operand" : [ {
                  "localId" : "33",
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
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "26",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "27",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "28",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "localId" : "29",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "7",
                     "type" : "Literal"
                  }, {
                     "localId" : "30",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "localId" : "31",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "9",
                     "type" : "Literal"
                  }, {
                     "localId" : "32",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "41",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "34",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "localId" : "35",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "36",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "37",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "localId" : "38",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "localId" : "39",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }, {
                     "localId" : "40",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "12",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "57",
            "name" : "IntersectOnAll",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "57",
                  "s" : [ {
                     "value" : [ "define ","IntersectOnAll",": " ]
                  }, {
                     "r" : "56",
                     "s" : [ {
                        "r" : "49",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " intersect " ]
                     }, {
                        "r" : "55",
                        "s" : [ {
                           "value" : [ "{","5",", ","4",", ","3",", ","2",", ","1","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "56",
               "type" : "Intersect",
               "operand" : [ {
                  "localId" : "49",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "44",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "45",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "46",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "47",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "48",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "55",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "50",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "51",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "52",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "53",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "54",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "85",
            "name" : "NestedIntersects",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "85",
                  "s" : [ {
                     "value" : [ "define ","NestedIntersects",": " ]
                  }, {
                     "r" : "84",
                     "s" : [ {
                        "r" : "77",
                        "s" : [ {
                           "r" : "70",
                           "s" : [ {
                              "r" : "63",
                              "s" : [ {
                                 "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                              } ]
                           }, {
                              "value" : [ " intersect " ]
                           }, {
                              "r" : "69",
                              "s" : [ {
                                 "value" : [ "{","2",", ","3",", ","4",", ","5",", ","6","}" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ " intersect " ]
                        }, {
                           "r" : "76",
                           "s" : [ {
                              "value" : [ "{","3",", ","4",", ","5",", ","6",", ","7","}" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ " intersect " ]
                     }, {
                        "r" : "83",
                        "s" : [ {
                           "value" : [ "{","4",", ","5",", ","6",", ","7",", ","8","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "84",
               "type" : "Intersect",
               "operand" : [ {
                  "localId" : "77",
                  "type" : "Intersect",
                  "operand" : [ {
                     "localId" : "70",
                     "type" : "Intersect",
                     "operand" : [ {
                        "localId" : "63",
                        "type" : "List",
                        "element" : [ {
                           "localId" : "58",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "localId" : "59",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "localId" : "60",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "localId" : "61",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "localId" : "62",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        } ]
                     }, {
                        "localId" : "69",
                        "type" : "List",
                        "element" : [ {
                           "localId" : "64",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "localId" : "65",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "localId" : "66",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "localId" : "67",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        }, {
                           "localId" : "68",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "6",
                           "type" : "Literal"
                        } ]
                     } ]
                  }, {
                     "localId" : "76",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "71",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "72",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "localId" : "73",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "localId" : "74",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     }, {
                        "localId" : "75",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     } ]
                  } ]
               }, {
                  "localId" : "83",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "78",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "79",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "80",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "localId" : "81",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "7",
                     "type" : "Literal"
                  }, {
                     "localId" : "82",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "110",
            "name" : "IntersectTuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "110",
                  "s" : [ {
                     "value" : [ "define ","IntersectTuples",": " ]
                  }, {
                     "r" : "109",
                     "s" : [ {
                        "r" : "95",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "88",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "87",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "91",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "90",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "94",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "93",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " intersect " ]
                     }, {
                        "r" : "108",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "98",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "97",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "101",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "100",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "104",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "103",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "107",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","5" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "106",
                                 "s" : [ {
                                    "value" : [ "'e'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "109",
               "type" : "Intersect",
               "operand" : [ {
                  "localId" : "95",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "88",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "86",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "87",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "91",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "89",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "90",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "94",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "92",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "93",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "localId" : "108",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "98",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "96",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "97",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "101",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "99",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "100",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "104",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "102",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "103",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "107",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "105",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "106",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "e",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "localId" : "117",
            "name" : "NullIntersect",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "117",
                  "s" : [ {
                     "value" : [ "define ","NullIntersect",": " ]
                  }, {
                     "r" : "116",
                     "s" : [ {
                        "r" : "111",
                        "value" : [ "null"," intersect " ]
                     }, {
                        "r" : "115",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "116",
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "As",
                  "operand" : {
                     "localId" : "111",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               }, {
                  "localId" : "115",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "112",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "113",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "114",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "124",
            "name" : "IntersectNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "124",
                  "s" : [ {
                     "value" : [ "define ","IntersectNull",": " ]
                  }, {
                     "r" : "123",
                     "s" : [ {
                        "r" : "121",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3","}" ]
                        } ]
                     }, {
                        "value" : [ " intersect ","null" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "123",
               "type" : "Intersect",
               "operand" : [ {
                  "localId" : "121",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "118",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "119",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "120",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "As",
                  "operand" : {
                     "localId" : "122",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               } ]
            }
         } ]
      }
   }
}

### IndexOf
library TestSnippet version '1'
using QUICK
context Patient
define IndexOfSecond: IndexOf({'a', 'b', 'c', 'd'}, 'b')
define IndexOfThirdTuple: IndexOf({Tuple{a: 1}, Tuple{a: 2}, Tuple{a: 3}}, Tuple{a: 3})
define MultipleMatches: IndexOf({'a', 'b', 'c', 'd', 'd', 'e', 'd'}, 'd')
define ItemNotFound: IndexOf({'a', 'b', 'c'}, 'd')
define NullList: IndexOf(null, 'a')
define NullItem: IndexOf({'a', 'b', 'c'}, null)
###

module.exports['IndexOf'] = {
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
            "name" : "IndexOfSecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","IndexOfSecond",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "IndexOf","(" ]
                     }, {
                        "r" : "6",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "2",
                           "s" : [ {
                              "value" : [ "'a'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "3",
                           "s" : [ {
                              "value" : [ "'b'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "4",
                           "s" : [ {
                              "value" : [ "'c'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "5",
                           "s" : [ {
                              "value" : [ "'d'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ", " ]
                     }, {
                        "r" : "7",
                        "s" : [ {
                           "value" : [ "'b'" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "IndexOf",
               "source" : {
                  "localId" : "6",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "2",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "localId" : "3",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "localId" : "4",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  }, {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "d",
                     "type" : "Literal"
                  } ]
               },
               "element" : {
                  "localId" : "7",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "b",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "20",
            "name" : "IndexOfThirdTuple",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "20",
                  "s" : [ {
                     "value" : [ "define ","IndexOfThirdTuple",": " ]
                  }, {
                     "r" : "19",
                     "s" : [ {
                        "value" : [ "IndexOf","(" ]
                     }, {
                        "r" : "16",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "11",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",": ","1" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "13",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",": ","2" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "15",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",": ","3" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ", " ]
                     }, {
                        "r" : "18",
                        "s" : [ {
                           "value" : [ "Tuple{" ]
                        }, {
                           "s" : [ {
                              "value" : [ "a",": ","3" ]
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
               "localId" : "19",
               "type" : "IndexOf",
               "source" : {
                  "localId" : "16",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "11",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "10",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "13",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "12",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "15",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "14",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               },
               "element" : {
                  "localId" : "18",
                  "type" : "Tuple",
                  "element" : [ {
                     "name" : "a",
                     "value" : {
                        "localId" : "17",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }
                  } ]
               }
            }
         }, {
            "localId" : "31",
            "name" : "MultipleMatches",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "31",
                  "s" : [ {
                     "value" : [ "define ","MultipleMatches",": " ]
                  }, {
                     "r" : "30",
                     "s" : [ {
                        "value" : [ "IndexOf","(" ]
                     }, {
                        "r" : "28",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "21",
                           "s" : [ {
                              "value" : [ "'a'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "22",
                           "s" : [ {
                              "value" : [ "'b'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "23",
                           "s" : [ {
                              "value" : [ "'c'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "24",
                           "s" : [ {
                              "value" : [ "'d'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "25",
                           "s" : [ {
                              "value" : [ "'d'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "26",
                           "s" : [ {
                              "value" : [ "'e'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "27",
                           "s" : [ {
                              "value" : [ "'d'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ", " ]
                     }, {
                        "r" : "29",
                        "s" : [ {
                           "value" : [ "'d'" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "30",
               "type" : "IndexOf",
               "source" : {
                  "localId" : "28",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "21",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "localId" : "22",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "localId" : "23",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  }, {
                     "localId" : "24",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "d",
                     "type" : "Literal"
                  }, {
                     "localId" : "25",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "d",
                     "type" : "Literal"
                  }, {
                     "localId" : "26",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "e",
                     "type" : "Literal"
                  }, {
                     "localId" : "27",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "d",
                     "type" : "Literal"
                  } ]
               },
               "element" : {
                  "localId" : "29",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "d",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "38",
            "name" : "ItemNotFound",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "38",
                  "s" : [ {
                     "value" : [ "define ","ItemNotFound",": " ]
                  }, {
                     "r" : "37",
                     "s" : [ {
                        "value" : [ "IndexOf","(" ]
                     }, {
                        "r" : "35",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "32",
                           "s" : [ {
                              "value" : [ "'a'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "33",
                           "s" : [ {
                              "value" : [ "'b'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "34",
                           "s" : [ {
                              "value" : [ "'c'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ", " ]
                     }, {
                        "r" : "36",
                        "s" : [ {
                           "value" : [ "'d'" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "37",
               "type" : "IndexOf",
               "source" : {
                  "localId" : "35",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "32",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "localId" : "33",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "localId" : "34",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  } ]
               },
               "element" : {
                  "localId" : "36",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "d",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "42",
            "name" : "NullList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "42",
                  "s" : [ {
                     "value" : [ "define ","NullList",": " ]
                  }, {
                     "r" : "41",
                     "s" : [ {
                        "value" : [ "IndexOf","(","null",", " ]
                     }, {
                        "r" : "40",
                        "s" : [ {
                           "value" : [ "'a'" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "41",
               "type" : "IndexOf",
               "source" : {
                  "type" : "As",
                  "operand" : {
                     "localId" : "39",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "name" : "{urn:hl7-org:elm-types:r1}String",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               },
               "element" : {
                  "localId" : "40",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "a",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "49",
            "name" : "NullItem",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "49",
                  "s" : [ {
                     "value" : [ "define ","NullItem",": " ]
                  }, {
                     "r" : "48",
                     "s" : [ {
                        "value" : [ "IndexOf","(" ]
                     }, {
                        "r" : "46",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "43",
                           "s" : [ {
                              "value" : [ "'a'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "44",
                           "s" : [ {
                              "value" : [ "'b'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "45",
                           "s" : [ {
                              "value" : [ "'c'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ", ","null",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "48",
               "type" : "IndexOf",
               "source" : {
                  "localId" : "46",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "43",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "localId" : "44",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "localId" : "45",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  } ]
               },
               "element" : {
                  "asType" : "{urn:hl7-org:elm-types:r1}String",
                  "type" : "As",
                  "operand" : {
                     "localId" : "47",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm-types:r1}String",
                     "type" : "NamedTypeSpecifier"
                  }
               }
            }
         } ]
      }
   }
}

### Indexer
library TestSnippet version '1'
using QUICK
context Patient
define SecondItem: {'a', 'b', 'c', 'd'}[1]
define ZeroIndex: {'a', 'b', 'c', 'd'}[0]
define OutOfBounds: {'a', 'b', 'c', 'd'}[100]
define NullList: (null as List<String>)[1]
define NullIndexer: {'a', 'b', 'c', 'd'}[null]
###

module.exports['Indexer'] = {
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
            "name" : "SecondItem",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","SecondItem",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "r" : "6",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "2",
                           "s" : [ {
                              "value" : [ "'a'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "3",
                           "s" : [ {
                              "value" : [ "'b'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "4",
                           "s" : [ {
                              "value" : [ "'c'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "5",
                           "s" : [ {
                              "value" : [ "'d'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ "[","1","]" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "Indexer",
               "operand" : [ {
                  "localId" : "6",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "2",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "localId" : "3",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "localId" : "4",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  }, {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "d",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "7",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "17",
            "name" : "ZeroIndex",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "17",
                  "s" : [ {
                     "value" : [ "define ","ZeroIndex",": " ]
                  }, {
                     "r" : "16",
                     "s" : [ {
                        "r" : "14",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "10",
                           "s" : [ {
                              "value" : [ "'a'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "11",
                           "s" : [ {
                              "value" : [ "'b'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "12",
                           "s" : [ {
                              "value" : [ "'c'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "13",
                           "s" : [ {
                              "value" : [ "'d'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ "[","0","]" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "16",
               "type" : "Indexer",
               "operand" : [ {
                  "localId" : "14",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "10",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "localId" : "11",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "localId" : "12",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  }, {
                     "localId" : "13",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "d",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "15",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "0",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "25",
            "name" : "OutOfBounds",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "25",
                  "s" : [ {
                     "value" : [ "define ","OutOfBounds",": " ]
                  }, {
                     "r" : "24",
                     "s" : [ {
                        "r" : "22",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "18",
                           "s" : [ {
                              "value" : [ "'a'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "19",
                           "s" : [ {
                              "value" : [ "'b'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "20",
                           "s" : [ {
                              "value" : [ "'c'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "21",
                           "s" : [ {
                              "value" : [ "'d'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ "[","100","]" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "24",
               "type" : "Indexer",
               "operand" : [ {
                  "localId" : "22",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "18",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "localId" : "19",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "localId" : "20",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  }, {
                     "localId" : "21",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "d",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "23",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "100",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "32",
            "name" : "NullList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "32",
                  "s" : [ {
                     "value" : [ "define ","NullList",": " ]
                  }, {
                     "r" : "31",
                     "s" : [ {
                        "r" : "29",
                        "s" : [ {
                           "value" : [ "(" ]
                        }, {
                           "r" : "29",
                           "s" : [ {
                              "r" : "26",
                              "value" : [ "null"," as " ]
                           }, {
                              "r" : "28",
                              "s" : [ {
                                 "value" : [ "List<" ]
                              }, {
                                 "r" : "27",
                                 "s" : [ {
                                    "value" : [ "String" ]
                                 } ]
                              }, {
                                 "value" : [ ">" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ ")" ]
                        } ]
                     }, {
                        "value" : [ "[","1","]" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "31",
               "type" : "Indexer",
               "operand" : [ {
                  "localId" : "29",
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "localId" : "26",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "localId" : "28",
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "localId" : "27",
                        "name" : "{urn:hl7-org:elm-types:r1}String",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               }, {
                  "localId" : "30",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "40",
            "name" : "NullIndexer",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "40",
                  "s" : [ {
                     "value" : [ "define ","NullIndexer",": " ]
                  }, {
                     "r" : "39",
                     "s" : [ {
                        "r" : "37",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "33",
                           "s" : [ {
                              "value" : [ "'a'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "34",
                           "s" : [ {
                              "value" : [ "'b'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "35",
                           "s" : [ {
                              "value" : [ "'c'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "36",
                           "s" : [ {
                              "value" : [ "'d'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ "[","null","]" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "39",
               "type" : "Indexer",
               "operand" : [ {
                  "localId" : "37",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "33",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "localId" : "34",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "localId" : "35",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  }, {
                     "localId" : "36",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "d",
                     "type" : "Literal"
                  } ]
               }, {
                  "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "As",
                  "operand" : {
                     "localId" : "38",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "NamedTypeSpecifier"
                  }
               } ]
            }
         } ]
      }
   }
}

### In
library TestSnippet version '1'
using QUICK
context Patient
define IsIn: 4 in { 3, 4, 5 }
define IsNotIn: 4 in { 3, 5, 6 }
define TupleIsIn: Tuple{a: 1, b: 'c'} in {Tuple{a:1, b:'d'}, Tuple{a:1, b:'c'}, Tuple{a:2, b:'c'}}
define TupleIsNotIn: Tuple{a: 1, b: 'c'} in {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}}
define NullIn: null in {1, 2, 3}
define InNull: 1 in null
###

module.exports['In'] = {
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
            "name" : "IsIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "8",
                  "s" : [ {
                     "value" : [ "define ","IsIn",": " ]
                  }, {
                     "r" : "7",
                     "s" : [ {
                        "r" : "2",
                        "value" : [ "4"," in " ]
                     }, {
                        "r" : "6",
                        "s" : [ {
                           "value" : [ "{ ","3",", ","4",", ","5"," }" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "7",
               "type" : "In",
               "operand" : [ {
                  "localId" : "2",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               }, {
                  "localId" : "6",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "3",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "4",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "15",
            "name" : "IsNotIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "15",
                  "s" : [ {
                     "value" : [ "define ","IsNotIn",": " ]
                  }, {
                     "r" : "14",
                     "s" : [ {
                        "r" : "9",
                        "value" : [ "4"," in " ]
                     }, {
                        "r" : "13",
                        "s" : [ {
                           "value" : [ "{ ","3",", ","5",", ","6"," }" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "14",
               "type" : "In",
               "operand" : [ {
                  "localId" : "9",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               }, {
                  "localId" : "13",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "10",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "11",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "12",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "30",
            "name" : "TupleIsIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "30",
                  "s" : [ {
                     "value" : [ "define ","TupleIsIn",": " ]
                  }, {
                     "r" : "29",
                     "s" : [ {
                        "r" : "18",
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
                              "value" : [ "b",": " ]
                           }, {
                              "r" : "17",
                              "s" : [ {
                                 "value" : [ "'c'" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " in " ]
                     }, {
                        "r" : "28",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "21",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "20",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "24",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "23",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "27",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "26",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "29",
               "type" : "In",
               "operand" : [ {
                  "localId" : "18",
                  "type" : "Tuple",
                  "element" : [ {
                     "name" : "a",
                     "value" : {
                        "localId" : "16",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }
                  }, {
                     "name" : "b",
                     "value" : {
                        "localId" : "17",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "c",
                        "type" : "Literal"
                     }
                  } ]
               }, {
                  "localId" : "28",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "21",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "19",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "20",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "24",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "22",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "23",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "27",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "25",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "26",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "localId" : "45",
            "name" : "TupleIsNotIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "45",
                  "s" : [ {
                     "value" : [ "define ","TupleIsNotIn",": " ]
                  }, {
                     "r" : "44",
                     "s" : [ {
                        "r" : "33",
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
                              "value" : [ "b",": " ]
                           }, {
                              "r" : "32",
                              "s" : [ {
                                 "value" : [ "'c'" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " in " ]
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
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "35",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
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
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "38",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
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
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "41",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "44",
               "type" : "In",
               "operand" : [ {
                  "localId" : "33",
                  "type" : "Tuple",
                  "element" : [ {
                     "name" : "a",
                     "value" : {
                        "localId" : "31",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }
                  }, {
                     "name" : "b",
                     "value" : {
                        "localId" : "32",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "c",
                        "type" : "Literal"
                     }
                  } ]
               }, {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
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
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "41",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "localId" : "52",
            "name" : "NullIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "52",
                  "s" : [ {
                     "value" : [ "define ","NullIn",": " ]
                  }, {
                     "r" : "51",
                     "s" : [ {
                        "r" : "46",
                        "value" : [ "null"," in " ]
                     }, {
                        "r" : "50",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "51",
               "type" : "In",
               "operand" : [ {
                  "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "As",
                  "operand" : {
                     "localId" : "46",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "localId" : "50",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "47",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "48",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "49",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "56",
            "name" : "InNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "56",
                  "s" : [ {
                     "value" : [ "define ","InNull",": " ]
                  }, {
                     "r" : "55",
                     "s" : [ {
                        "r" : "53",
                        "value" : [ "1"," in ","null" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "55",
               "type" : "In",
               "operand" : [ {
                  "localId" : "53",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "type" : "As",
                  "operand" : {
                     "localId" : "54",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               } ]
            }
         } ]
      }
   }
}

### Contains
library TestSnippet version '1'
using QUICK
context Patient
define IsIn: { 3, 4, 5 } contains 4
define IsNotIn: { 3, 5, 6 } contains 4
define TupleIsIn: {Tuple{a:1, b:'d'}, Tuple{a:1, b:'c'}, Tuple{a:2, b:'c'}} contains Tuple{a: 1, b: 'c'}
define TupleIsNotIn: {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}} contains Tuple{a: 1, b: 'c'}
define InNull: (null as List<Integer>) contains 1
define NullIn: {1, 2, 3} contains null
###

module.exports['Contains'] = {
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
            "name" : "IsIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "8",
                  "s" : [ {
                     "value" : [ "define ","IsIn",": " ]
                  }, {
                     "r" : "7",
                     "s" : [ {
                        "r" : "5",
                        "s" : [ {
                           "value" : [ "{ ","3",", ","4",", ","5"," }" ]
                        } ]
                     }, {
                        "value" : [ " contains ","4" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "7",
               "type" : "Contains",
               "operand" : [ {
                  "localId" : "5",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "2",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "3",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "4",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "6",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "15",
            "name" : "IsNotIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "15",
                  "s" : [ {
                     "value" : [ "define ","IsNotIn",": " ]
                  }, {
                     "r" : "14",
                     "s" : [ {
                        "r" : "12",
                        "s" : [ {
                           "value" : [ "{ ","3",", ","5",", ","6"," }" ]
                        } ]
                     }, {
                        "value" : [ " contains ","4" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "14",
               "type" : "Contains",
               "operand" : [ {
                  "localId" : "12",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "10",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "11",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "13",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "30",
            "name" : "TupleIsIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "30",
                  "s" : [ {
                     "value" : [ "define ","TupleIsIn",": " ]
                  }, {
                     "r" : "29",
                     "s" : [ {
                        "r" : "25",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "18",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "17",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "21",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "20",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "24",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "23",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " contains " ]
                     }, {
                        "r" : "28",
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
                              "value" : [ "b",": " ]
                           }, {
                              "r" : "27",
                              "s" : [ {
                                 "value" : [ "'c'" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "29",
               "type" : "Contains",
               "operand" : [ {
                  "localId" : "25",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "18",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "16",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "17",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "21",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "19",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "20",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "24",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "22",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "23",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "localId" : "28",
                  "type" : "Tuple",
                  "element" : [ {
                     "name" : "a",
                     "value" : {
                        "localId" : "26",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }
                  }, {
                     "name" : "b",
                     "value" : {
                        "localId" : "27",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "c",
                        "type" : "Literal"
                     }
                  } ]
               } ]
            }
         }, {
            "localId" : "45",
            "name" : "TupleIsNotIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "45",
                  "s" : [ {
                     "value" : [ "define ","TupleIsNotIn",": " ]
                  }, {
                     "r" : "44",
                     "s" : [ {
                        "r" : "40",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "33",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "32",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "36",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "35",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
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
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "38",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " contains " ]
                     }, {
                        "r" : "43",
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
                              "value" : [ "b",": " ]
                           }, {
                              "r" : "42",
                              "s" : [ {
                                 "value" : [ "'c'" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "44",
               "type" : "Contains",
               "operand" : [ {
                  "localId" : "40",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "33",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "31",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "32",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "36",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "34",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "35",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "localId" : "43",
                  "type" : "Tuple",
                  "element" : [ {
                     "name" : "a",
                     "value" : {
                        "localId" : "41",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }
                  }, {
                     "name" : "b",
                     "value" : {
                        "localId" : "42",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "c",
                        "type" : "Literal"
                     }
                  } ]
               } ]
            }
         }, {
            "localId" : "52",
            "name" : "InNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "52",
                  "s" : [ {
                     "value" : [ "define ","InNull",": " ]
                  }, {
                     "r" : "51",
                     "s" : [ {
                        "r" : "49",
                        "s" : [ {
                           "value" : [ "(" ]
                        }, {
                           "r" : "49",
                           "s" : [ {
                              "r" : "46",
                              "value" : [ "null"," as " ]
                           }, {
                              "r" : "48",
                              "s" : [ {
                                 "value" : [ "List<" ]
                              }, {
                                 "r" : "47",
                                 "s" : [ {
                                    "value" : [ "Integer" ]
                                 } ]
                              }, {
                                 "value" : [ ">" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ ")" ]
                        } ]
                     }, {
                        "value" : [ " contains ","1" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "51",
               "type" : "Contains",
               "operand" : [ {
                  "localId" : "49",
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "localId" : "46",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "localId" : "48",
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "localId" : "47",
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               }, {
                  "localId" : "50",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "59",
            "name" : "NullIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "59",
                  "s" : [ {
                     "value" : [ "define ","NullIn",": " ]
                  }, {
                     "r" : "58",
                     "s" : [ {
                        "r" : "56",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3","}" ]
                        } ]
                     }, {
                        "value" : [ " contains ","null" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "58",
               "type" : "Contains",
               "operand" : [ {
                  "localId" : "56",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "53",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "54",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "55",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               }, {
                  "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "As",
                  "operand" : {
                     "localId" : "57",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "NamedTypeSpecifier"
                  }
               } ]
            }
         } ]
      }
   }
}

### Includes
library TestSnippet version '1'
using QUICK
context Patient
define IsIncluded: {1, 2, 3, 4, 5} includes {2, 3, 4}
define IsIncludedReversed: {1, 2, 3, 4, 5} includes {4, 3, 2}
define IsSame: {1, 2, 3, 4, 5} includes {1, 2, 3, 4, 5}
define IsNotIncluded: {1, 2, 3, 4, 5} includes {4, 5, 6}
define TuplesIncluded: {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}} includes {Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}}
define TuplesNotIncluded: {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}} includes {Tuple{a:2, b:'d'}, Tuple{a:3, b:'c'}}
define NullIncluded: {1, 2, 3, 4, 5} includes null
define NullIncludes: null includes {1, 2, 3, 4, 5}
###

module.exports['Includes'] = {
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
            "localId" : "13",
            "name" : "IsIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "13",
                  "s" : [ {
                     "value" : [ "define ","IsIncluded",": " ]
                  }, {
                     "r" : "12",
                     "s" : [ {
                        "r" : "7",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " ","includes"," " ]
                     }, {
                        "r" : "11",
                        "s" : [ {
                           "value" : [ "{","2",", ","3",", ","4","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "12",
               "type" : "Includes",
               "operand" : [ {
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
               }, {
                  "localId" : "11",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "8",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "10",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "25",
            "name" : "IsIncludedReversed",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "25",
                  "s" : [ {
                     "value" : [ "define ","IsIncludedReversed",": " ]
                  }, {
                     "r" : "24",
                     "s" : [ {
                        "r" : "19",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " ","includes"," " ]
                     }, {
                        "r" : "23",
                        "s" : [ {
                           "value" : [ "{","4",", ","3",", ","2","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "24",
               "type" : "Includes",
               "operand" : [ {
                  "localId" : "19",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "14",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "15",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "16",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "17",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "18",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "23",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "20",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "21",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "22",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "39",
            "name" : "IsSame",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "39",
                  "s" : [ {
                     "value" : [ "define ","IsSame",": " ]
                  }, {
                     "r" : "38",
                     "s" : [ {
                        "r" : "31",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " ","includes"," " ]
                     }, {
                        "r" : "37",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "38",
               "type" : "Includes",
               "operand" : [ {
                  "localId" : "31",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "26",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "27",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "28",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "29",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "30",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "37",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "32",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "33",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "34",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "35",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "36",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "51",
            "name" : "IsNotIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "51",
                  "s" : [ {
                     "value" : [ "define ","IsNotIncluded",": " ]
                  }, {
                     "r" : "50",
                     "s" : [ {
                        "r" : "45",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " ","includes"," " ]
                     }, {
                        "r" : "49",
                        "s" : [ {
                           "value" : [ "{","4",", ","5",", ","6","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "50",
               "type" : "Includes",
               "operand" : [ {
                  "localId" : "45",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "40",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "41",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "42",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "43",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "44",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "49",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "46",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "47",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "48",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "70",
            "name" : "TuplesIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "70",
                  "s" : [ {
                     "value" : [ "define ","TuplesIncluded",": " ]
                  }, {
                     "r" : "69",
                     "s" : [ {
                        "r" : "61",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "54",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "53",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "57",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "56",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "60",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "59",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " ","includes"," " ]
                     }, {
                        "r" : "68",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "64",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "63",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "67",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "66",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "69",
               "type" : "Includes",
               "operand" : [ {
                  "localId" : "61",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "54",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "52",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "53",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "57",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "55",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "56",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "60",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "58",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "59",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "localId" : "68",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "64",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "62",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "63",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "67",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "65",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "66",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "localId" : "89",
            "name" : "TuplesNotIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "89",
                  "s" : [ {
                     "value" : [ "define ","TuplesNotIncluded",": " ]
                  }, {
                     "r" : "88",
                     "s" : [ {
                        "r" : "80",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "73",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "72",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "76",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "75",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "79",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "78",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " ","includes"," " ]
                     }, {
                        "r" : "87",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "83",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "82",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
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
                                 "value" : [ "a",":","3" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "85",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "88",
               "type" : "Includes",
               "operand" : [ {
                  "localId" : "80",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "73",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "71",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "72",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "76",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "74",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "75",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "79",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "77",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "78",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "localId" : "87",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "83",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "81",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "82",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
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
                           "value" : "3",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "85",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "localId" : "98",
            "name" : "NullIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "98",
                  "s" : [ {
                     "value" : [ "define ","NullIncluded",": " ]
                  }, {
                     "r" : "97",
                     "s" : [ {
                        "r" : "95",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " ","includes"," ","null" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "97",
               "type" : "Contains",
               "operand" : [ {
                  "localId" : "95",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "90",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "91",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "92",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "93",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "94",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "As",
                  "operand" : {
                     "localId" : "96",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "NamedTypeSpecifier"
                  }
               } ]
            }
         }, {
            "localId" : "107",
            "name" : "NullIncludes",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "107",
                  "s" : [ {
                     "value" : [ "define ","NullIncludes",": " ]
                  }, {
                     "r" : "106",
                     "s" : [ {
                        "r" : "99",
                        "value" : [ "null"," ","includes"," " ]
                     }, {
                        "r" : "105",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "106",
               "type" : "Includes",
               "operand" : [ {
                  "type" : "As",
                  "operand" : {
                     "localId" : "99",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               }, {
                  "localId" : "105",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "100",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "101",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "102",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "103",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "104",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

### IncludedIn
library TestSnippet version '1'
using QUICK
context Patient
define IsIncluded: {2, 3, 4} included in {1, 2, 3, 4, 5}
define IsIncludedReversed: {4, 3, 2} included in {1, 2, 3, 4, 5}
define IsSame: {1, 2, 3, 4, 5} included in {1, 2, 3, 4, 5}
define IsNotIncluded: {4, 5, 6} included in {1, 2, 3, 4, 5}
define TuplesIncluded: {Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}} included in {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}}
define TuplesNotIncluded: {Tuple{a:2, b:'d'}, Tuple{a:3, b:'c'}} included in {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}}
define NullIncludes: {1, 2, 3, 4, 5} included in null
define NullIncluded: null included in {1, 2, 3, 4, 5}
###

module.exports['IncludedIn'] = {
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
            "localId" : "13",
            "name" : "IsIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "13",
                  "s" : [ {
                     "value" : [ "define ","IsIncluded",": " ]
                  }, {
                     "r" : "12",
                     "s" : [ {
                        "r" : "5",
                        "s" : [ {
                           "value" : [ "{","2",", ","3",", ","4","}" ]
                        } ]
                     }, {
                        "value" : [ " ","included in"," " ]
                     }, {
                        "r" : "11",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "12",
               "type" : "IncludedIn",
               "operand" : [ {
                  "localId" : "5",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "2",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "3",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "4",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "11",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "6",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "7",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "8",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "10",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "25",
            "name" : "IsIncludedReversed",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "25",
                  "s" : [ {
                     "value" : [ "define ","IsIncludedReversed",": " ]
                  }, {
                     "r" : "24",
                     "s" : [ {
                        "r" : "17",
                        "s" : [ {
                           "value" : [ "{","4",", ","3",", ","2","}" ]
                        } ]
                     }, {
                        "value" : [ " ","included in"," " ]
                     }, {
                        "r" : "23",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "24",
               "type" : "IncludedIn",
               "operand" : [ {
                  "localId" : "17",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "14",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "15",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "16",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "23",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "18",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "19",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "20",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "21",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "22",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "39",
            "name" : "IsSame",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "39",
                  "s" : [ {
                     "value" : [ "define ","IsSame",": " ]
                  }, {
                     "r" : "38",
                     "s" : [ {
                        "r" : "31",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " ","included in"," " ]
                     }, {
                        "r" : "37",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "38",
               "type" : "IncludedIn",
               "operand" : [ {
                  "localId" : "31",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "26",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "27",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "28",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "29",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "30",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "37",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "32",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "33",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "34",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "35",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "36",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "51",
            "name" : "IsNotIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "51",
                  "s" : [ {
                     "value" : [ "define ","IsNotIncluded",": " ]
                  }, {
                     "r" : "50",
                     "s" : [ {
                        "r" : "43",
                        "s" : [ {
                           "value" : [ "{","4",", ","5",", ","6","}" ]
                        } ]
                     }, {
                        "value" : [ " ","included in"," " ]
                     }, {
                        "r" : "49",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "50",
               "type" : "IncludedIn",
               "operand" : [ {
                  "localId" : "43",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "40",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "41",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "42",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "49",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "44",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "45",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "46",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "47",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "48",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "70",
            "name" : "TuplesIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "70",
                  "s" : [ {
                     "value" : [ "define ","TuplesIncluded",": " ]
                  }, {
                     "r" : "69",
                     "s" : [ {
                        "r" : "58",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "54",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "53",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "57",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "56",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " ","included in"," " ]
                     }, {
                        "r" : "68",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "61",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "60",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "64",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "63",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "67",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "66",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "69",
               "type" : "IncludedIn",
               "operand" : [ {
                  "localId" : "58",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "54",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "52",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "53",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "57",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "55",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "56",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "localId" : "68",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "61",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "59",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "60",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "64",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "62",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "63",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "67",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "65",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "66",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "localId" : "89",
            "name" : "TuplesNotIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "89",
                  "s" : [ {
                     "value" : [ "define ","TuplesNotIncluded",": " ]
                  }, {
                     "r" : "88",
                     "s" : [ {
                        "r" : "77",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "73",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "72",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "76",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","3" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "75",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " ","included in"," " ]
                     }, {
                        "r" : "87",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "80",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "79",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "83",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "82",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
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
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "85",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "88",
               "type" : "IncludedIn",
               "operand" : [ {
                  "localId" : "77",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "73",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "71",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "72",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "76",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "74",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "75",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "localId" : "87",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "80",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "78",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "79",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "83",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "81",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "82",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "localId" : "98",
            "name" : "NullIncludes",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "98",
                  "s" : [ {
                     "value" : [ "define ","NullIncludes",": " ]
                  }, {
                     "r" : "97",
                     "s" : [ {
                        "r" : "95",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " ","included in"," ","null" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "97",
               "type" : "IncludedIn",
               "operand" : [ {
                  "localId" : "95",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "90",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "91",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "92",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "93",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "94",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "As",
                  "operand" : {
                     "localId" : "96",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               } ]
            }
         }, {
            "localId" : "107",
            "name" : "NullIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "107",
                  "s" : [ {
                     "value" : [ "define ","NullIncluded",": " ]
                  }, {
                     "r" : "106",
                     "s" : [ {
                        "r" : "99",
                        "value" : [ "null"," ","included in"," " ]
                     }, {
                        "r" : "105",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "106",
               "type" : "In",
               "operand" : [ {
                  "localId" : "99",
                  "type" : "Null"
               }, {
                  "localId" : "105",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "100",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "101",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "102",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "103",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "104",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

### ProperIncludes
library TestSnippet version '1'
using QUICK
context Patient
define IsIncluded: {1, 2, 3, 4, 5} properly includes {2, 3, 4, 5}
define IsIncludedReversed: {1, 2, 3, 4, 5} properly includes {5, 4, 3, 2}
define IsSame: {1, 2, 3, 4, 5} properly includes {1, 2, 3, 4, 5}
define IsNotIncluded: {1, 2, 3, 4, 5} properly includes {3, 4, 5, 6}
define TuplesIncluded: {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}} properly includes {Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}}
define TuplesNotIncluded: {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}} properly includes {Tuple{a:2, b:'d'}, Tuple{a:3, b:'c'}}
define NullIncluded: {1, 2, 3, 4, 5} properly includes null
define NullIncludes: null properly includes {1, 2, 3, 4, 5}
###

module.exports['ProperIncludes'] = {
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
            "localId" : "14",
            "name" : "IsIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "14",
                  "s" : [ {
                     "value" : [ "define ","IsIncluded",": " ]
                  }, {
                     "r" : "13",
                     "s" : [ {
                        "r" : "7",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " ","properly includes"," " ]
                     }, {
                        "r" : "12",
                        "s" : [ {
                           "value" : [ "{","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "13",
               "type" : "ProperIncludes",
               "operand" : [ {
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
               }, {
                  "localId" : "12",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "8",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "10",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "11",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "27",
            "name" : "IsIncludedReversed",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "27",
                  "s" : [ {
                     "value" : [ "define ","IsIncludedReversed",": " ]
                  }, {
                     "r" : "26",
                     "s" : [ {
                        "r" : "20",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " ","properly includes"," " ]
                     }, {
                        "r" : "25",
                        "s" : [ {
                           "value" : [ "{","5",", ","4",", ","3",", ","2","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "26",
               "type" : "ProperIncludes",
               "operand" : [ {
                  "localId" : "20",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "15",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "16",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "17",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "18",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "19",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "25",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "21",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "22",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "23",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "24",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "41",
            "name" : "IsSame",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "41",
                  "s" : [ {
                     "value" : [ "define ","IsSame",": " ]
                  }, {
                     "r" : "40",
                     "s" : [ {
                        "r" : "33",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " ","properly includes"," " ]
                     }, {
                        "r" : "39",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "40",
               "type" : "ProperIncludes",
               "operand" : [ {
                  "localId" : "33",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "28",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "29",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "30",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "31",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "32",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
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
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "38",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "54",
            "name" : "IsNotIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "54",
                  "s" : [ {
                     "value" : [ "define ","IsNotIncluded",": " ]
                  }, {
                     "r" : "53",
                     "s" : [ {
                        "r" : "47",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " ","properly includes"," " ]
                     }, {
                        "r" : "52",
                        "s" : [ {
                           "value" : [ "{","3",", ","4",", ","5",", ","6","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "53",
               "type" : "ProperIncludes",
               "operand" : [ {
                  "localId" : "47",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "42",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "43",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "44",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "45",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "46",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "52",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "48",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "49",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "50",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "51",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "73",
            "name" : "TuplesIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "73",
                  "s" : [ {
                     "value" : [ "define ","TuplesIncluded",": " ]
                  }, {
                     "r" : "72",
                     "s" : [ {
                        "r" : "64",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "57",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "56",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "60",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "59",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "63",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "62",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " ","properly includes"," " ]
                     }, {
                        "r" : "71",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "67",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "66",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "70",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "69",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "72",
               "type" : "ProperIncludes",
               "operand" : [ {
                  "localId" : "64",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "57",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "55",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "56",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "60",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "58",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "59",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "63",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "61",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "62",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "localId" : "71",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "67",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "65",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "66",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "70",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "68",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "69",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "localId" : "92",
            "name" : "TuplesNotIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "92",
                  "s" : [ {
                     "value" : [ "define ","TuplesNotIncluded",": " ]
                  }, {
                     "r" : "91",
                     "s" : [ {
                        "r" : "83",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "76",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "75",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "79",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "78",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "82",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "81",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " ","properly includes"," " ]
                     }, {
                        "r" : "90",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "86",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "85",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
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
                                 "value" : [ "a",":","3" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "88",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "91",
               "type" : "ProperIncludes",
               "operand" : [ {
                  "localId" : "83",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "76",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "74",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "75",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "79",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "77",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "78",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "82",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "80",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "81",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "localId" : "90",
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
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
                           "value" : "3",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "88",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "localId" : "101",
            "name" : "NullIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "101",
                  "s" : [ {
                     "value" : [ "define ","NullIncluded",": " ]
                  }, {
                     "r" : "100",
                     "s" : [ {
                        "r" : "98",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " ","properly includes"," ","null" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "100",
               "type" : "ProperContains",
               "operand" : [ {
                  "localId" : "98",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "93",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "94",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "95",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "96",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "97",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "As",
                  "operand" : {
                     "localId" : "99",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "NamedTypeSpecifier"
                  }
               } ]
            }
         }, {
            "localId" : "110",
            "name" : "NullIncludes",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "110",
                  "s" : [ {
                     "value" : [ "define ","NullIncludes",": " ]
                  }, {
                     "r" : "109",
                     "s" : [ {
                        "r" : "102",
                        "value" : [ "null"," ","properly includes"," " ]
                     }, {
                        "r" : "108",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "109",
               "type" : "ProperIncludes",
               "operand" : [ {
                  "type" : "As",
                  "operand" : {
                     "localId" : "102",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               }, {
                  "localId" : "108",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "103",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "104",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "105",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "106",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "107",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

### ProperIncludedIn
library TestSnippet version '1'
using QUICK
context Patient
define IsIncluded: {2, 3, 4} properly included in {1, 2, 3, 4, 5}
define IsIncludedReversed: {4, 3, 2} properly included in {1, 2, 3, 4, 5}
define IsSame: {1, 2, 3, 4, 5} properly included in {1, 2, 3, 4, 5}
define IsNotIncluded: {4, 5, 6} properly included in {1, 2, 3, 4, 5}
define TuplesIncluded: {Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}} properly included in {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}}
define TuplesNotIncluded: {Tuple{a:2, b:'d'}, Tuple{a:3, b:'c'}} properly included in {Tuple{a:1, b:'d'}, Tuple{a:2, b:'d'}, Tuple{a:2, b:'c'}}
define NullIncludes: {1, 2, 3, 4, 5} properly included in null
define NullIncluded: (null as List<Integer>) properly included in {1, 2, 3, 4, 5}
###

module.exports['ProperIncludedIn'] = {
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
            "localId" : "13",
            "name" : "IsIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "13",
                  "s" : [ {
                     "value" : [ "define ","IsIncluded",": " ]
                  }, {
                     "r" : "12",
                     "s" : [ {
                        "r" : "5",
                        "s" : [ {
                           "value" : [ "{","2",", ","3",", ","4","}" ]
                        } ]
                     }, {
                        "value" : [ " ","properly included in"," " ]
                     }, {
                        "r" : "11",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "12",
               "type" : "ProperIncludedIn",
               "operand" : [ {
                  "localId" : "5",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "2",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "3",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "4",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "11",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "6",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "7",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "8",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "10",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "25",
            "name" : "IsIncludedReversed",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "25",
                  "s" : [ {
                     "value" : [ "define ","IsIncludedReversed",": " ]
                  }, {
                     "r" : "24",
                     "s" : [ {
                        "r" : "17",
                        "s" : [ {
                           "value" : [ "{","4",", ","3",", ","2","}" ]
                        } ]
                     }, {
                        "value" : [ " ","properly included in"," " ]
                     }, {
                        "r" : "23",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "24",
               "type" : "ProperIncludedIn",
               "operand" : [ {
                  "localId" : "17",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "14",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "15",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "16",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "23",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "18",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "19",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "20",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "21",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "22",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "39",
            "name" : "IsSame",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "39",
                  "s" : [ {
                     "value" : [ "define ","IsSame",": " ]
                  }, {
                     "r" : "38",
                     "s" : [ {
                        "r" : "31",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " ","properly included in"," " ]
                     }, {
                        "r" : "37",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "38",
               "type" : "ProperIncludedIn",
               "operand" : [ {
                  "localId" : "31",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "26",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "27",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "28",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "29",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "30",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "37",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "32",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "33",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "34",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "35",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "36",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "51",
            "name" : "IsNotIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "51",
                  "s" : [ {
                     "value" : [ "define ","IsNotIncluded",": " ]
                  }, {
                     "r" : "50",
                     "s" : [ {
                        "r" : "43",
                        "s" : [ {
                           "value" : [ "{","4",", ","5",", ","6","}" ]
                        } ]
                     }, {
                        "value" : [ " ","properly included in"," " ]
                     }, {
                        "r" : "49",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "50",
               "type" : "ProperIncludedIn",
               "operand" : [ {
                  "localId" : "43",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "40",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "41",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "42",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               }, {
                  "localId" : "49",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "44",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "45",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "46",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "47",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "48",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "localId" : "70",
            "name" : "TuplesIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "70",
                  "s" : [ {
                     "value" : [ "define ","TuplesIncluded",": " ]
                  }, {
                     "r" : "69",
                     "s" : [ {
                        "r" : "58",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "54",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "53",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "57",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "56",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " ","properly included in"," " ]
                     }, {
                        "r" : "68",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "61",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "60",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "64",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "63",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "67",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "66",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "69",
               "type" : "ProperIncludedIn",
               "operand" : [ {
                  "localId" : "58",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "54",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "52",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "53",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "57",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "55",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "56",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "localId" : "68",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "61",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "59",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "60",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "64",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "62",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "63",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "67",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "65",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "66",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "localId" : "89",
            "name" : "TuplesNotIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "89",
                  "s" : [ {
                     "value" : [ "define ","TuplesNotIncluded",": " ]
                  }, {
                     "r" : "88",
                     "s" : [ {
                        "r" : "77",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "73",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "72",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "76",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","3" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "75",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ " ","properly included in"," " ]
                     }, {
                        "r" : "87",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "80",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","1" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "79",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "83",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "82",
                                 "s" : [ {
                                    "value" : [ "'d'" ]
                                 } ]
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
                                 "value" : [ "a",":","2" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",":" ]
                              }, {
                                 "r" : "85",
                                 "s" : [ {
                                    "value" : [ "'c'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "88",
               "type" : "ProperIncludedIn",
               "operand" : [ {
                  "localId" : "77",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "73",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "71",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "72",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "76",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "74",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "75",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "localId" : "87",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "80",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "78",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "79",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "83",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "81",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "82",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "localId" : "98",
            "name" : "NullIncludes",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "98",
                  "s" : [ {
                     "value" : [ "define ","NullIncludes",": " ]
                  }, {
                     "r" : "97",
                     "s" : [ {
                        "r" : "95",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     }, {
                        "value" : [ " ","properly included in"," ","null" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "97",
               "type" : "ProperIncludedIn",
               "operand" : [ {
                  "localId" : "95",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "90",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "91",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "92",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "93",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "94",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "As",
                  "operand" : {
                     "localId" : "96",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               } ]
            }
         }, {
            "localId" : "110",
            "name" : "NullIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "110",
                  "s" : [ {
                     "value" : [ "define ","NullIncluded",": " ]
                  }, {
                     "r" : "109",
                     "s" : [ {
                        "r" : "102",
                        "s" : [ {
                           "value" : [ "(" ]
                        }, {
                           "r" : "102",
                           "s" : [ {
                              "r" : "99",
                              "value" : [ "null"," as " ]
                           }, {
                              "r" : "101",
                              "s" : [ {
                                 "value" : [ "List<" ]
                              }, {
                                 "r" : "100",
                                 "s" : [ {
                                    "value" : [ "Integer" ]
                                 } ]
                              }, {
                                 "value" : [ ">" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ ")" ]
                        } ]
                     }, {
                        "value" : [ " ","properly included in"," " ]
                     }, {
                        "r" : "108",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4",", ","5","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "109",
               "type" : "ProperIncludedIn",
               "operand" : [ {
                  "localId" : "102",
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "localId" : "99",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "localId" : "101",
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "localId" : "100",
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               }, {
                  "localId" : "108",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "103",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "104",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "105",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "106",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "107",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

### Flatten
library TestSnippet version '1'
using QUICK
context Patient
define ListOfLists: flatten { {1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {9, 8, 7, 6, 5}, {4}, {3, 2, 1} }
define NullValue: flatten null
###

module.exports['Flatten'] = {
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
            "localId" : "28",
            "name" : "ListOfLists",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "28",
                  "s" : [ {
                     "value" : [ "define ","ListOfLists",": " ]
                  }, {
                     "r" : "27",
                     "s" : [ {
                        "value" : [ "flatten " ]
                     }, {
                        "r" : "26",
                        "s" : [ {
                           "value" : [ "{ " ]
                        }, {
                           "r" : "5",
                           "s" : [ {
                              "value" : [ "{","1",", ","2",", ","3","}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "9",
                           "s" : [ {
                              "value" : [ "{","4",", ","5",", ","6","}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "13",
                           "s" : [ {
                              "value" : [ "{","7",", ","8",", ","9","}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "19",
                           "s" : [ {
                              "value" : [ "{","9",", ","8",", ","7",", ","6",", ","5","}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "21",
                           "s" : [ {
                              "value" : [ "{","4","}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "25",
                           "s" : [ {
                              "value" : [ "{","3",", ","2",", ","1","}" ]
                           } ]
                        }, {
                           "value" : [ " }" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "27",
               "type" : "Flatten",
               "operand" : {
                  "localId" : "26",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "5",
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
                     } ]
                  }, {
                     "localId" : "9",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "6",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "localId" : "7",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "localId" : "8",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     } ]
                  }, {
                     "localId" : "13",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "10",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "localId" : "11",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "localId" : "12",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "9",
                        "type" : "Literal"
                     } ]
                  }, {
                     "localId" : "19",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "14",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "9",
                        "type" : "Literal"
                     }, {
                        "localId" : "15",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "localId" : "16",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "localId" : "17",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     }, {
                        "localId" : "18",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     } ]
                  }, {
                     "localId" : "21",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "20",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     } ]
                  }, {
                     "localId" : "25",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "22",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "localId" : "23",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "24",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "localId" : "31",
            "name" : "NullValue",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "31",
                  "s" : [ {
                     "value" : [ "define ","NullValue",": " ]
                  }, {
                     "r" : "30",
                     "s" : [ {
                        "value" : [ "flatten ","null" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "30",
               "type" : "Flatten",
               "operand" : {
                  "type" : "As",
                  "operand" : {
                     "localId" : "29",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "type" : "ListTypeSpecifier",
                        "elementType" : {
                           "name" : "{urn:hl7-org:elm-types:r1}Integer",
                           "type" : "NamedTypeSpecifier"
                        }
                     }
                  }
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
define LotsOfDups: distinct {1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5, 4, 3, 2, 1}
define NoDups: distinct {2, 4, 6, 8, 10}
define DupsTuples: distinct { Tuple{ hello: 'world' }, Tuple{ hello: 'cleveland' }, Tuple{ hello: 'world' }, Tuple{ hello: 'dolly' } }
define NoDupsTuples: distinct { Tuple{ hello: 'world' }, Tuple{ hello: 'cleveland' } }
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
            "name" : "LotsOfDups",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "23",
                  "s" : [ {
                     "value" : [ "define ","LotsOfDups",": " ]
                  }, {
                     "r" : "22",
                     "s" : [ {
                        "value" : [ "distinct " ]
                     }, {
                        "r" : "21",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","2",", ","3",", ","3",", ","3",", ","4",", ","4",", ","4",", ","4",", ","5",", ","5",", ","5",", ","5",", ","5",", ","4",", ","3",", ","2",", ","1","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "22",
               "type" : "Distinct",
               "operand" : {
                  "localId" : "21",
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
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "13",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "14",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "15",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "16",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "localId" : "17",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "18",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "19",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "20",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "31",
            "name" : "NoDups",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "31",
                  "s" : [ {
                     "value" : [ "define ","NoDups",": " ]
                  }, {
                     "r" : "30",
                     "s" : [ {
                        "value" : [ "distinct " ]
                     }, {
                        "r" : "29",
                        "s" : [ {
                           "value" : [ "{","2",", ","4",", ","6",", ","8",", ","10","}" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "30",
               "type" : "Distinct",
               "operand" : {
                  "localId" : "29",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "24",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "25",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "26",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "localId" : "27",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "localId" : "28",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "42",
            "name" : "DupsTuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "42",
                  "s" : [ {
                     "value" : [ "define ","DupsTuples",": " ]
                  }, {
                     "r" : "41",
                     "s" : [ {
                        "value" : [ "distinct " ]
                     }, {
                        "r" : "40",
                        "s" : [ {
                           "value" : [ "{ " ]
                        }, {
                           "r" : "33",
                           "s" : [ {
                              "value" : [ "Tuple{ " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "hello",": " ]
                              }, {
                                 "r" : "32",
                                 "s" : [ {
                                    "value" : [ "'world'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " }" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "35",
                           "s" : [ {
                              "value" : [ "Tuple{ " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "hello",": " ]
                              }, {
                                 "r" : "34",
                                 "s" : [ {
                                    "value" : [ "'cleveland'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " }" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "37",
                           "s" : [ {
                              "value" : [ "Tuple{ " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "hello",": " ]
                              }, {
                                 "r" : "36",
                                 "s" : [ {
                                    "value" : [ "'world'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " }" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "39",
                           "s" : [ {
                              "value" : [ "Tuple{ " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "hello",": " ]
                              }, {
                                 "r" : "38",
                                 "s" : [ {
                                    "value" : [ "'dolly'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " }" ]
                           } ]
                        }, {
                           "value" : [ " }" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "41",
               "type" : "Distinct",
               "operand" : {
                  "localId" : "40",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "33",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "hello",
                        "value" : {
                           "localId" : "32",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "world",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "35",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "hello",
                        "value" : {
                           "localId" : "34",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "cleveland",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "37",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "hello",
                        "value" : {
                           "localId" : "36",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "world",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "39",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "hello",
                        "value" : {
                           "localId" : "38",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "dolly",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }
            }
         }, {
            "localId" : "49",
            "name" : "NoDupsTuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "49",
                  "s" : [ {
                     "value" : [ "define ","NoDupsTuples",": " ]
                  }, {
                     "r" : "48",
                     "s" : [ {
                        "value" : [ "distinct " ]
                     }, {
                        "r" : "47",
                        "s" : [ {
                           "value" : [ "{ " ]
                        }, {
                           "r" : "44",
                           "s" : [ {
                              "value" : [ "Tuple{ " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "hello",": " ]
                              }, {
                                 "r" : "43",
                                 "s" : [ {
                                    "value" : [ "'world'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " }" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "46",
                           "s" : [ {
                              "value" : [ "Tuple{ " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "hello",": " ]
                              }, {
                                 "r" : "45",
                                 "s" : [ {
                                    "value" : [ "'cleveland'" ]
                                 } ]
                              } ]
                           }, {
                              "value" : [ " }" ]
                           } ]
                        }, {
                           "value" : [ " }" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "48",
               "type" : "Distinct",
               "operand" : {
                  "localId" : "47",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "44",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "hello",
                        "value" : {
                           "localId" : "43",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "world",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "46",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "hello",
                        "value" : {
                           "localId" : "45",
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "cleveland",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }
            }
         } ]
      }
   }
}

### First
library TestSnippet version '1'
using QUICK
context Patient
define Numbers: First({1, 2, 3, 4})
define Letters: First({'a', 'b', 'c'})
define Lists: First({{'a','b','c'},{'d','e','f'}})
define Tuples: First({ Tuple{a: 1, b: 2, c: 3}, Tuple{a: 24, b: 25, c: 26} })
define Unordered: First({3, 1, 4, 2})
define Empty: First(List<Integer>{})
define NullValue: First(null as List<Integer>)
###

module.exports['First'] = {
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
            "name" : "Numbers",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "8",
                  "s" : [ {
                     "value" : [ "define ","Numbers",": " ]
                  }, {
                     "r" : "7",
                     "s" : [ {
                        "value" : [ "First","(" ]
                     }, {
                        "r" : "6",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "7",
               "type" : "First",
               "source" : {
                  "localId" : "6",
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
                  } ]
               }
            }
         }, {
            "localId" : "14",
            "name" : "Letters",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "14",
                  "s" : [ {
                     "value" : [ "define ","Letters",": " ]
                  }, {
                     "r" : "13",
                     "s" : [ {
                        "value" : [ "First","(" ]
                     }, {
                        "r" : "12",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "9",
                           "s" : [ {
                              "value" : [ "'a'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "10",
                           "s" : [ {
                              "value" : [ "'b'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "11",
                           "s" : [ {
                              "value" : [ "'c'" ]
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
               "localId" : "13",
               "type" : "First",
               "source" : {
                  "localId" : "12",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "localId" : "10",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "localId" : "11",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "25",
            "name" : "Lists",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "25",
                  "s" : [ {
                     "value" : [ "define ","Lists",": " ]
                  }, {
                     "r" : "24",
                     "s" : [ {
                        "value" : [ "First","(" ]
                     }, {
                        "r" : "23",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "18",
                           "s" : [ {
                              "value" : [ "{" ]
                           }, {
                              "r" : "15",
                              "s" : [ {
                                 "value" : [ "'a'" ]
                              } ]
                           }, {
                              "value" : [ "," ]
                           }, {
                              "r" : "16",
                              "s" : [ {
                                 "value" : [ "'b'" ]
                              } ]
                           }, {
                              "value" : [ "," ]
                           }, {
                              "r" : "17",
                              "s" : [ {
                                 "value" : [ "'c'" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "22",
                           "s" : [ {
                              "value" : [ "{" ]
                           }, {
                              "r" : "19",
                              "s" : [ {
                                 "value" : [ "'d'" ]
                              } ]
                           }, {
                              "value" : [ "," ]
                           }, {
                              "r" : "20",
                              "s" : [ {
                                 "value" : [ "'e'" ]
                              } ]
                           }, {
                              "value" : [ "," ]
                           }, {
                              "r" : "21",
                              "s" : [ {
                                 "value" : [ "'f'" ]
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
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "24",
               "type" : "First",
               "source" : {
                  "localId" : "23",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "18",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "15",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "a",
                        "type" : "Literal"
                     }, {
                        "localId" : "16",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "b",
                        "type" : "Literal"
                     }, {
                        "localId" : "17",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "c",
                        "type" : "Literal"
                     } ]
                  }, {
                     "localId" : "22",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "19",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "d",
                        "type" : "Literal"
                     }, {
                        "localId" : "20",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "e",
                        "type" : "Literal"
                     }, {
                        "localId" : "21",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "f",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "localId" : "36",
            "name" : "Tuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "36",
                  "s" : [ {
                     "value" : [ "define ","Tuples",": " ]
                  }, {
                     "r" : "35",
                     "s" : [ {
                        "value" : [ "First","(" ]
                     }, {
                        "r" : "34",
                        "s" : [ {
                           "value" : [ "{ " ]
                        }, {
                           "r" : "29",
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
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "c",": ","3" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "33",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",": ","24" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",": ","25" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "c",": ","26" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ " }" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "35",
               "type" : "First",
               "source" : {
                  "localId" : "34",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "29",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "26",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "27",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "c",
                        "value" : {
                           "localId" : "28",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "33",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "30",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "24",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "31",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "25",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "c",
                        "value" : {
                           "localId" : "32",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "26",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }
            }
         }, {
            "localId" : "43",
            "name" : "Unordered",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "43",
                  "s" : [ {
                     "value" : [ "define ","Unordered",": " ]
                  }, {
                     "r" : "42",
                     "s" : [ {
                        "value" : [ "First","(" ]
                     }, {
                        "r" : "41",
                        "s" : [ {
                           "value" : [ "{","3",", ","1",", ","4",", ","2","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "42",
               "type" : "First",
               "source" : {
                  "localId" : "41",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "37",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "38",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "39",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "40",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "47",
            "name" : "Empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "47",
                  "s" : [ {
                     "value" : [ "define ","Empty",": " ]
                  }, {
                     "r" : "46",
                     "s" : [ {
                        "value" : [ "First","(" ]
                     }, {
                        "r" : "45",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "44",
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
               "localId" : "46",
               "type" : "First",
               "source" : {
                  "localId" : "45",
                  "type" : "List"
               }
            }
         }, {
            "localId" : "53",
            "name" : "NullValue",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "53",
                  "s" : [ {
                     "value" : [ "define ","NullValue",": " ]
                  }, {
                     "r" : "52",
                     "s" : [ {
                        "value" : [ "First","(" ]
                     }, {
                        "r" : "51",
                        "s" : [ {
                           "r" : "48",
                           "value" : [ "null"," as " ]
                        }, {
                           "r" : "50",
                           "s" : [ {
                              "value" : [ "List<" ]
                           }, {
                              "r" : "49",
                              "s" : [ {
                                 "value" : [ "Integer" ]
                              } ]
                           }, {
                              "value" : [ ">" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "52",
               "type" : "First",
               "source" : {
                  "localId" : "51",
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "localId" : "48",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "localId" : "50",
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "localId" : "49",
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               }
            }
         } ]
      }
   }
}

### Last
library TestSnippet version '1'
using QUICK
context Patient
define Numbers: Last({1, 2, 3, 4})
define Letters: Last({'a', 'b', 'c'})
define Lists: Last({{'a','b','c'},{'d','e','f'}})
define Tuples: Last({ Tuple{a: 1, b: 2, c: 3}, Tuple{a: 24, b: 25, c: 26} })
define Unordered: Last({3, 1, 4, 2})
define Empty: Last(List<Integer>{})
define NullValue: Last(null as List<Integer>)
###

module.exports['Last'] = {
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
            "name" : "Numbers",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "8",
                  "s" : [ {
                     "value" : [ "define ","Numbers",": " ]
                  }, {
                     "r" : "7",
                     "s" : [ {
                        "value" : [ "Last","(" ]
                     }, {
                        "r" : "6",
                        "s" : [ {
                           "value" : [ "{","1",", ","2",", ","3",", ","4","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "7",
               "type" : "Last",
               "source" : {
                  "localId" : "6",
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
                  } ]
               }
            }
         }, {
            "localId" : "14",
            "name" : "Letters",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "14",
                  "s" : [ {
                     "value" : [ "define ","Letters",": " ]
                  }, {
                     "r" : "13",
                     "s" : [ {
                        "value" : [ "Last","(" ]
                     }, {
                        "r" : "12",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "9",
                           "s" : [ {
                              "value" : [ "'a'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "10",
                           "s" : [ {
                              "value" : [ "'b'" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "11",
                           "s" : [ {
                              "value" : [ "'c'" ]
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
               "localId" : "13",
               "type" : "Last",
               "source" : {
                  "localId" : "12",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "localId" : "10",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "localId" : "11",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "25",
            "name" : "Lists",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "25",
                  "s" : [ {
                     "value" : [ "define ","Lists",": " ]
                  }, {
                     "r" : "24",
                     "s" : [ {
                        "value" : [ "Last","(" ]
                     }, {
                        "r" : "23",
                        "s" : [ {
                           "value" : [ "{" ]
                        }, {
                           "r" : "18",
                           "s" : [ {
                              "value" : [ "{" ]
                           }, {
                              "r" : "15",
                              "s" : [ {
                                 "value" : [ "'a'" ]
                              } ]
                           }, {
                              "value" : [ "," ]
                           }, {
                              "r" : "16",
                              "s" : [ {
                                 "value" : [ "'b'" ]
                              } ]
                           }, {
                              "value" : [ "," ]
                           }, {
                              "r" : "17",
                              "s" : [ {
                                 "value" : [ "'c'" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ "," ]
                        }, {
                           "r" : "22",
                           "s" : [ {
                              "value" : [ "{" ]
                           }, {
                              "r" : "19",
                              "s" : [ {
                                 "value" : [ "'d'" ]
                              } ]
                           }, {
                              "value" : [ "," ]
                           }, {
                              "r" : "20",
                              "s" : [ {
                                 "value" : [ "'e'" ]
                              } ]
                           }, {
                              "value" : [ "," ]
                           }, {
                              "r" : "21",
                              "s" : [ {
                                 "value" : [ "'f'" ]
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
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "24",
               "type" : "Last",
               "source" : {
                  "localId" : "23",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "18",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "15",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "a",
                        "type" : "Literal"
                     }, {
                        "localId" : "16",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "b",
                        "type" : "Literal"
                     }, {
                        "localId" : "17",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "c",
                        "type" : "Literal"
                     } ]
                  }, {
                     "localId" : "22",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "19",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "d",
                        "type" : "Literal"
                     }, {
                        "localId" : "20",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "e",
                        "type" : "Literal"
                     }, {
                        "localId" : "21",
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "f",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "localId" : "36",
            "name" : "Tuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "36",
                  "s" : [ {
                     "value" : [ "define ","Tuples",": " ]
                  }, {
                     "r" : "35",
                     "s" : [ {
                        "value" : [ "Last","(" ]
                     }, {
                        "r" : "34",
                        "s" : [ {
                           "value" : [ "{ " ]
                        }, {
                           "r" : "29",
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
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "c",": ","3" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "33",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",": ","24" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",": ","25" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "c",": ","26" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ " }" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "35",
               "type" : "Last",
               "source" : {
                  "localId" : "34",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "29",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "26",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "27",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "c",
                        "value" : {
                           "localId" : "28",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "33",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "30",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "24",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "31",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "25",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "c",
                        "value" : {
                           "localId" : "32",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "26",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }
            }
         }, {
            "localId" : "43",
            "name" : "Unordered",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "43",
                  "s" : [ {
                     "value" : [ "define ","Unordered",": " ]
                  }, {
                     "r" : "42",
                     "s" : [ {
                        "value" : [ "Last","(" ]
                     }, {
                        "r" : "41",
                        "s" : [ {
                           "value" : [ "{","3",", ","1",", ","4",", ","2","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "42",
               "type" : "Last",
               "source" : {
                  "localId" : "41",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "37",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "localId" : "38",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "localId" : "39",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "40",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "47",
            "name" : "Empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "47",
                  "s" : [ {
                     "value" : [ "define ","Empty",": " ]
                  }, {
                     "r" : "46",
                     "s" : [ {
                        "value" : [ "Last","(" ]
                     }, {
                        "r" : "45",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "44",
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
               "localId" : "46",
               "type" : "Last",
               "source" : {
                  "localId" : "45",
                  "type" : "List"
               }
            }
         }, {
            "localId" : "53",
            "name" : "NullValue",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "53",
                  "s" : [ {
                     "value" : [ "define ","NullValue",": " ]
                  }, {
                     "r" : "52",
                     "s" : [ {
                        "value" : [ "Last","(" ]
                     }, {
                        "r" : "51",
                        "s" : [ {
                           "r" : "48",
                           "value" : [ "null"," as " ]
                        }, {
                           "r" : "50",
                           "s" : [ {
                              "value" : [ "List<" ]
                           }, {
                              "r" : "49",
                              "s" : [ {
                                 "value" : [ "Integer" ]
                              } ]
                           }, {
                              "value" : [ ">" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "52",
               "type" : "Last",
               "source" : {
                  "localId" : "51",
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "localId" : "48",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "localId" : "50",
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "localId" : "49",
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               }
            }
         } ]
      }
   }
}

### Length
library TestSnippet version '1'
using QUICK
context Patient
define Numbers: Length({2, 4, 6, 8, 10})
define Lists: Length({ {1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}})
define Tuples: Length({ Tuple{a: 1, b: 2, c: 3}, Tuple{a: 24, b: 25, c: 26} })
define Empty: Length(List<Integer>{})
define NullValue: Length(null as List<Integer>)
###

module.exports['Length'] = {
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
            "name" : "Numbers",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","Numbers",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "Length","(" ]
                     }, {
                        "r" : "7",
                        "s" : [ {
                           "value" : [ "{","2",", ","4",", ","6",", ","8",", ","10","}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "Length",
               "operand" : {
                  "localId" : "7",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "2",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "localId" : "3",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "localId" : "4",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "localId" : "5",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "localId" : "6",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "localId" : "28",
            "name" : "Lists",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "28",
                  "s" : [ {
                     "value" : [ "define ","Lists",": " ]
                  }, {
                     "r" : "27",
                     "s" : [ {
                        "value" : [ "Length","(" ]
                     }, {
                        "r" : "26",
                        "s" : [ {
                           "value" : [ "{ " ]
                        }, {
                           "r" : "13",
                           "s" : [ {
                              "value" : [ "{","1",", ","2",", ","3","}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "17",
                           "s" : [ {
                              "value" : [ "{","4",", ","5",", ","6","}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "21",
                           "s" : [ {
                              "value" : [ "{","7",", ","8",", ","9","}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "25",
                           "s" : [ {
                              "value" : [ "{","10",", ","11",", ","12","}" ]
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
               "localId" : "27",
               "type" : "Length",
               "operand" : {
                  "localId" : "26",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "13",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "10",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "localId" : "11",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "localId" : "12",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     } ]
                  }, {
                     "localId" : "17",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "14",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "localId" : "15",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "localId" : "16",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     } ]
                  }, {
                     "localId" : "21",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "18",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "localId" : "19",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "localId" : "20",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "9",
                        "type" : "Literal"
                     } ]
                  }, {
                     "localId" : "25",
                     "type" : "List",
                     "element" : [ {
                        "localId" : "22",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "10",
                        "type" : "Literal"
                     }, {
                        "localId" : "23",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "11",
                        "type" : "Literal"
                     }, {
                        "localId" : "24",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "12",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "localId" : "39",
            "name" : "Tuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "39",
                  "s" : [ {
                     "value" : [ "define ","Tuples",": " ]
                  }, {
                     "r" : "38",
                     "s" : [ {
                        "value" : [ "Length","(" ]
                     }, {
                        "r" : "37",
                        "s" : [ {
                           "value" : [ "{ " ]
                        }, {
                           "r" : "32",
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
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "c",": ","3" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "36",
                           "s" : [ {
                              "value" : [ "Tuple{" ]
                           }, {
                              "s" : [ {
                                 "value" : [ "a",": ","24" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "b",": ","25" ]
                              } ]
                           }, {
                              "value" : [ ", " ]
                           }, {
                              "s" : [ {
                                 "value" : [ "c",": ","26" ]
                              } ]
                           }, {
                              "value" : [ "}" ]
                           } ]
                        }, {
                           "value" : [ " }" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "38",
               "type" : "Length",
               "operand" : {
                  "localId" : "37",
                  "type" : "List",
                  "element" : [ {
                     "localId" : "32",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "29",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "30",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "c",
                        "value" : {
                           "localId" : "31",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "localId" : "36",
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "localId" : "33",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "24",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "localId" : "34",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "25",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "c",
                        "value" : {
                           "localId" : "35",
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "26",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }
            }
         }, {
            "localId" : "43",
            "name" : "Empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "43",
                  "s" : [ {
                     "value" : [ "define ","Empty",": " ]
                  }, {
                     "r" : "42",
                     "s" : [ {
                        "value" : [ "Length","(" ]
                     }, {
                        "r" : "41",
                        "s" : [ {
                           "value" : [ "List<" ]
                        }, {
                           "r" : "40",
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
               "localId" : "42",
               "type" : "Length",
               "operand" : {
                  "localId" : "41",
                  "type" : "List"
               }
            }
         }, {
            "localId" : "49",
            "name" : "NullValue",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "49",
                  "s" : [ {
                     "value" : [ "define ","NullValue",": " ]
                  }, {
                     "r" : "48",
                     "s" : [ {
                        "value" : [ "Length","(" ]
                     }, {
                        "r" : "47",
                        "s" : [ {
                           "r" : "44",
                           "value" : [ "null"," as " ]
                        }, {
                           "r" : "46",
                           "s" : [ {
                              "value" : [ "List<" ]
                           }, {
                              "r" : "45",
                              "s" : [ {
                                 "value" : [ "Integer" ]
                              } ]
                           }, {
                              "value" : [ ">" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "48",
               "type" : "Length",
               "operand" : {
                  "localId" : "47",
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "localId" : "44",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "localId" : "46",
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "localId" : "45",
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               }
            }
         } ]
      }
   }
}

### ToList
library TestSnippet version '1'
using QUICK
context Patient
define FiveInFive: 5 in 5 // CQL-to-ELM will promote the second 5 to a list via ToList
define FourInFive: 4 in 5 // CQL-to-ELM will promote the 5 to a list via ToList
define LengthOfNull: Length(null as Integer) // CQL-to-ELM will promote the null to a list via ToList
###

module.exports['ToList'] = {
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
            "name" : "FiveInFive",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","FiveInFive",": " ]
                  }, {
                     "r" : "4",
                     "s" : [ {
                        "r" : "2",
                        "value" : [ "5"," in ","5" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "type" : "In",
               "operand" : [ {
                  "localId" : "2",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "5",
                  "type" : "Literal"
               }, {
                  "type" : "ToList",
                  "operand" : {
                     "localId" : "3",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "localId" : "9",
            "name" : "FourInFive",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","FourInFive",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "r" : "6",
                        "value" : [ "4"," in ","5" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "type" : "In",
               "operand" : [ {
                  "localId" : "6",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               }, {
                  "type" : "ToList",
                  "operand" : {
                     "localId" : "7",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "localId" : "14",
            "name" : "LengthOfNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "14",
                  "s" : [ {
                     "value" : [ "define ","LengthOfNull",": " ]
                  }, {
                     "r" : "13",
                     "s" : [ {
                        "value" : [ "Length","(" ]
                     }, {
                        "r" : "12",
                        "s" : [ {
                           "r" : "10",
                           "value" : [ "null"," as " ]
                        }, {
                           "r" : "11",
                           "s" : [ {
                              "value" : [ "Integer" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "13",
               "type" : "Length",
               "operand" : {
                  "type" : "ToList",
                  "operand" : {
                     "localId" : "12",
                     "strict" : false,
                     "type" : "As",
                     "operand" : {
                        "localId" : "10",
                        "type" : "Null"
                     },
                     "asTypeSpecifier" : {
                        "localId" : "11",
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               }
            }
         } ]
      }
   }
}

