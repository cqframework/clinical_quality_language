###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### If
library TestSnippet version '1'
using QUICK
parameter var Boolean
context Patient
define exp: if var then 'true return' else 'false return'
###

module.exports['If'] = {
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
            "localId" : "3",
            "name" : "var",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "2",
               "name" : "{urn:hl7-org:elm-types:r1}Boolean",
               "type" : "NamedTypeSpecifier"
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
            "localId" : "8",
            "name" : "exp",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "8",
                  "s" : [ {
                     "value" : [ "define ","exp",": " ]
                  }, {
                     "r" : "7",
                     "s" : [ {
                        "value" : [ "if " ]
                     }, {
                        "r" : "4",
                        "s" : [ {
                           "value" : [ "var" ]
                        } ]
                     }, {
                        "value" : [ " then " ]
                     }, {
                        "r" : "5",
                        "s" : [ {
                           "value" : [ "'true return'" ]
                        } ]
                     }, {
                        "value" : [ " else " ]
                     }, {
                        "r" : "6",
                        "s" : [ {
                           "value" : [ "'false return'" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "7",
               "type" : "If",
               "condition" : {
                  "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                  "type" : "As",
                  "operand" : {
                     "localId" : "4",
                     "name" : "var",
                     "type" : "ParameterRef"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "NamedTypeSpecifier"
                  }
               },
               "then" : {
                  "localId" : "5",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "true return",
                  "type" : "Literal"
               },
               "else" : {
                  "localId" : "6",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "false return",
                  "type" : "Literal"
               }
            }
         } ]
      }
   }
}

### Case
library TestSnippet version '1'
using QUICK
parameter var Integer
parameter X Integer
parameter Y Integer
context Patient

define selected:
  case var
   when 1 then 'one'
   when 2 then 'two'
   else
    '?'
  end

define standard:
  case
    when X > Y then 'X > Y'
    when X < Y then 'X < Y'
    else 'X == Y'
  end
###

module.exports['Case'] = {
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
            "localId" : "3",
            "name" : "var",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "2",
               "name" : "{urn:hl7-org:elm-types:r1}Integer",
               "type" : "NamedTypeSpecifier"
            }
         }, {
            "localId" : "5",
            "name" : "X",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "4",
               "name" : "{urn:hl7-org:elm-types:r1}Integer",
               "type" : "NamedTypeSpecifier"
            }
         }, {
            "localId" : "7",
            "name" : "Y",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "6",
               "name" : "{urn:hl7-org:elm-types:r1}Integer",
               "type" : "NamedTypeSpecifier"
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
            "localId" : "17",
            "name" : "selected",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "17",
                  "s" : [ {
                     "value" : [ "define ","selected",":\n  " ]
                  }, {
                     "r" : "16",
                     "s" : [ {
                        "value" : [ "case " ]
                     }, {
                        "r" : "8",
                        "s" : [ {
                           "value" : [ "var" ]
                        } ]
                     }, {
                        "value" : [ "\n   " ]
                     }, {
                        "r" : "11",
                        "s" : [ {
                           "value" : [ "when ","1"," then " ]
                        }, {
                           "r" : "10",
                           "s" : [ {
                              "value" : [ "'one'" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ "\n   " ]
                     }, {
                        "r" : "14",
                        "s" : [ {
                           "value" : [ "when ","2"," then " ]
                        }, {
                           "r" : "13",
                           "s" : [ {
                              "value" : [ "'two'" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ "\n   else\n    " ]
                     }, {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "'?'" ]
                        } ]
                     }, {
                        "value" : [ "\n  end" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "16",
               "type" : "Case",
               "comparand" : {
                  "localId" : "8",
                  "name" : "var",
                  "type" : "ParameterRef"
               },
               "caseItem" : [ {
                  "localId" : "11",
                  "when" : {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "then" : {
                     "localId" : "10",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "one",
                     "type" : "Literal"
                  }
               }, {
                  "localId" : "14",
                  "when" : {
                     "localId" : "12",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "then" : {
                     "localId" : "13",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "two",
                     "type" : "Literal"
                  }
               } ],
               "else" : {
                  "localId" : "15",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "?",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "30",
            "name" : "standard",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "30",
                  "s" : [ {
                     "value" : [ "define ","standard",":\n  " ]
                  }, {
                     "r" : "29",
                     "s" : [ {
                        "value" : [ "case\n    " ]
                     }, {
                        "r" : "22",
                        "s" : [ {
                           "value" : [ "when " ]
                        }, {
                           "r" : "20",
                           "s" : [ {
                              "r" : "18",
                              "s" : [ {
                                 "value" : [ "X" ]
                              } ]
                           }, {
                              "value" : [ " ",">"," " ]
                           }, {
                              "r" : "19",
                              "s" : [ {
                                 "value" : [ "Y" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ " then " ]
                        }, {
                           "r" : "21",
                           "s" : [ {
                              "value" : [ "'X > Y'" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ "\n    " ]
                     }, {
                        "r" : "27",
                        "s" : [ {
                           "value" : [ "when " ]
                        }, {
                           "r" : "25",
                           "s" : [ {
                              "r" : "23",
                              "s" : [ {
                                 "value" : [ "X" ]
                              } ]
                           }, {
                              "value" : [ " ","<"," " ]
                           }, {
                              "r" : "24",
                              "s" : [ {
                                 "value" : [ "Y" ]
                              } ]
                           } ]
                        }, {
                           "value" : [ " then " ]
                        }, {
                           "r" : "26",
                           "s" : [ {
                              "value" : [ "'X < Y'" ]
                           } ]
                        } ]
                     }, {
                        "value" : [ "\n    else " ]
                     }, {
                        "r" : "28",
                        "s" : [ {
                           "value" : [ "'X == Y'" ]
                        } ]
                     }, {
                        "value" : [ "\n  end" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "29",
               "type" : "Case",
               "caseItem" : [ {
                  "localId" : "22",
                  "when" : {
                     "localId" : "20",
                     "type" : "Greater",
                     "operand" : [ {
                        "localId" : "18",
                        "name" : "X",
                        "type" : "ParameterRef"
                     }, {
                        "localId" : "19",
                        "name" : "Y",
                        "type" : "ParameterRef"
                     } ]
                  },
                  "then" : {
                     "localId" : "21",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "X > Y",
                     "type" : "Literal"
                  }
               }, {
                  "localId" : "27",
                  "when" : {
                     "localId" : "25",
                     "type" : "Less",
                     "operand" : [ {
                        "localId" : "23",
                        "name" : "X",
                        "type" : "ParameterRef"
                     }, {
                        "localId" : "24",
                        "name" : "Y",
                        "type" : "ParameterRef"
                     } ]
                  },
                  "then" : {
                     "localId" : "26",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "X < Y",
                     "type" : "Literal"
                  }
               } ],
               "else" : {
                  "localId" : "28",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "X == Y",
                  "type" : "Literal"
               }
            }
         } ]
      }
   }
}

