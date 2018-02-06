###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### ExpressionDef
library TestSnippet version '1'
using QUICK
context Patient
define Foo: 'Bar'
###

module.exports['ExpressionDef'] = {
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
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "3",
                  "s" : [ {
                     "value" : [ "define ","Foo",": " ]
                  }, {
                     "r" : "2",
                     "s" : [ {
                        "value" : [ "'Bar'" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "2",
               "valueType" : "{urn:hl7-org:elm-types:r1}String",
               "value" : "Bar",
               "type" : "Literal"
            }
         } ]
      }
   }
}

### ExpressionRef
library TestSnippet version '1'
using QUICK
context Patient
define Life: 42
define Foo: Life
###

module.exports['ExpressionRef'] = {
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
            "name" : "Life",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "3",
                  "s" : [ {
                     "value" : [ "define ","Life",": ","42" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "2",
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "42",
               "type" : "Literal"
            }
         }, {
            "localId" : "5",
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","Foo",": " ]
                  }, {
                     "r" : "4",
                     "s" : [ {
                        "value" : [ "Life" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "name" : "Life",
               "type" : "ExpressionRef"
            }
         } ]
      }
   }
}

### FunctionDefinitions
library TestSnippet version '1'
using QUICK
context Patient
define function "foo bar"(a Integer, b Integer) :
  a + b

define testValue: "foo bar" (1,2)
###

module.exports['FunctionDefinitions'] = {
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
            "localId" : "7",
            "name" : "foo bar",
            "context" : "Patient",
            "accessLevel" : "Public",
            "type" : "FunctionDef",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "7",
                  "s" : [ {
                     "value" : [ "define function ","\"foo bar\"","(","a"," " ]
                  }, {
                     "r" : "2",
                     "s" : [ {
                        "value" : [ "Integer" ]
                     } ]
                  }, {
                     "value" : [ ", ","b"," " ]
                  }, {
                     "r" : "3",
                     "s" : [ {
                        "value" : [ "Integer" ]
                     } ]
                  }, {
                     "value" : [ ") :\n  " ]
                  }, {
                     "r" : "6",
                     "s" : [ {
                        "r" : "6",
                        "s" : [ {
                           "r" : "4",
                           "s" : [ {
                              "value" : [ "a" ]
                           } ]
                        }, {
                           "value" : [ " + " ]
                        }, {
                           "r" : "5",
                           "s" : [ {
                              "value" : [ "b" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "6",
               "type" : "Add",
               "operand" : [ {
                  "localId" : "4",
                  "name" : "a",
                  "type" : "OperandRef"
               }, {
                  "localId" : "5",
                  "name" : "b",
                  "type" : "OperandRef"
               } ]
            },
            "operand" : [ {
               "name" : "a",
               "operandTypeSpecifier" : {
                  "localId" : "2",
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            }, {
               "name" : "b",
               "operandTypeSpecifier" : {
                  "localId" : "3",
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            } ]
         }, {
            "localId" : "11",
            "name" : "testValue",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","testValue",": " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "value" : [ "\"foo bar\""," (","1",",","2",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "name" : "foo bar",
               "type" : "FunctionRef",
               "operand" : [ {
                  "localId" : "8",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "localId" : "9",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

### FunctionOverloads
library TestSnippet version '1'
using QUICK
context Patient
define function "foo bar" (a System.Integer) :
  a + 1

define function "foo bar" (a System.String) :
  'Hello ' + a

define testValue1: "foo bar"(1)
define testValue2: "foo bar"('World')
###

module.exports['FunctionOverloads'] = {
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
            "localId" : "6",
            "name" : "foo bar",
            "context" : "Patient",
            "accessLevel" : "Public",
            "type" : "FunctionDef",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "6",
                  "s" : [ {
                     "value" : [ "define function ","\"foo bar\""," (","a"," " ]
                  }, {
                     "r" : "2",
                     "s" : [ {
                        "value" : [ "System",".","Integer" ]
                     } ]
                  }, {
                     "value" : [ ") :\n  " ]
                  }, {
                     "r" : "5",
                     "s" : [ {
                        "r" : "5",
                        "s" : [ {
                           "r" : "3",
                           "s" : [ {
                              "value" : [ "a" ]
                           } ]
                        }, {
                           "value" : [ " + ","1" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "5",
               "type" : "Add",
               "operand" : [ {
                  "localId" : "3",
                  "name" : "a",
                  "type" : "OperandRef"
               }, {
                  "localId" : "4",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               } ]
            },
            "operand" : [ {
               "name" : "a",
               "operandTypeSpecifier" : {
                  "localId" : "2",
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            } ]
         }, {
            "localId" : "11",
            "name" : "foo bar",
            "context" : "Patient",
            "accessLevel" : "Public",
            "type" : "FunctionDef",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define function ","\"foo bar\""," (","a"," " ]
                  }, {
                     "r" : "7",
                     "s" : [ {
                        "value" : [ "System",".","String" ]
                     } ]
                  }, {
                     "value" : [ ") :\n  " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "r" : "10",
                        "s" : [ {
                           "r" : "8",
                           "s" : [ {
                              "value" : [ "'Hello '" ]
                           } ]
                        }, {
                           "value" : [ " + " ]
                        }, {
                           "r" : "9",
                           "s" : [ {
                              "value" : [ "a" ]
                           } ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "type" : "Concatenate",
               "operand" : [ {
                  "localId" : "8",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "Hello ",
                  "type" : "Literal"
               }, {
                  "localId" : "9",
                  "name" : "a",
                  "type" : "OperandRef"
               } ]
            },
            "operand" : [ {
               "name" : "a",
               "operandTypeSpecifier" : {
                  "localId" : "7",
                  "name" : "{urn:hl7-org:elm-types:r1}String",
                  "type" : "NamedTypeSpecifier"
               }
            } ]
         }, {
            "localId" : "14",
            "name" : "testValue1",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "14",
                  "s" : [ {
                     "value" : [ "define ","testValue1",": " ]
                  }, {
                     "r" : "13",
                     "s" : [ {
                        "value" : [ "\"foo bar\"","(","1",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "13",
               "name" : "foo bar",
               "type" : "FunctionRef",
               "operand" : [ {
                  "localId" : "12",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "17",
            "name" : "testValue2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "17",
                  "s" : [ {
                     "value" : [ "define ","testValue2",": " ]
                  }, {
                     "r" : "16",
                     "s" : [ {
                        "value" : [ "\"foo bar\"","(" ]
                     }, {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "'World'" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "16",
               "name" : "foo bar",
               "type" : "FunctionRef",
               "operand" : [ {
                  "localId" : "15",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "World",
                  "type" : "Literal"
               } ]
            }
         } ]
      }
   }
}

