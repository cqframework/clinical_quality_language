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
            "name" : "Three",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Add",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "IntList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "List",
               "element" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "9",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "7",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "8",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "StringList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "List",
               "element" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "a",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "bee",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "see",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "MixedList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "List",
               "element" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "two",
                  "type" : "Literal"
               }, {
                  "name" : "Three",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "EmptyList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
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
            "name" : "EmptyList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Exists",
               "operand" : {
                  "type" : "List"
               }
            }
         }, {
            "name" : "FullList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Exists",
               "operand" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
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
            "name" : "EqualIntList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "UnequalIntList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "ReverseIntList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "EqualStringList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "hello",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "world",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "hello",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "world",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "UnequalStringList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "hello",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "world",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "foo",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "bar",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "EqualTupleList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
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
                        "name" : "x",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "y",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "z",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
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
                        "name" : "x",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "y",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "z",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "UnequalTupleList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
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
                        "name" : "x",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "y",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "z",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
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
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "x",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "y",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "z",
                        "value" : {
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
            "name" : "EqualIntList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "UnequalIntList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "ReverseIntList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "EqualStringList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "hello",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "world",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "hello",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "world",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "UnequalStringList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "hello",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "world",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "foo",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "bar",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "EqualTupleList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "List",
                     "element" : [ {
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
                           "name" : "x",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm-types:r1}String",
                              "value" : "y",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "z",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
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
                           "name" : "x",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm-types:r1}String",
                              "value" : "y",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "z",
                           "value" : {
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
            "name" : "UnequalTupleList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "type" : "List",
                     "element" : [ {
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
                           "name" : "x",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm-types:r1}String",
                              "value" : "y",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "z",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
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
                     }, {
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "x",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm-types:r1}String",
                              "value" : "y",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "z",
                           "value" : {
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
            "name" : "OneToTen",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Union",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "7",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "9",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "OneToFiveOverlapped",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Union",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "Disjoint",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Union",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "NestedToFifteen",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Union",
               "operand" : [ {
                  "type" : "Union",
                  "operand" : [ {
                     "type" : "Union",
                     "operand" : [ {
                        "type" : "Union",
                        "operand" : [ {
                           "type" : "List",
                           "element" : [ {
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }, {
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }, {
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "3",
                              "type" : "Literal"
                           } ]
                        }, {
                           "type" : "List",
                           "element" : [ {
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "4",
                              "type" : "Literal"
                           }, {
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "5",
                              "type" : "Literal"
                           }, {
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "6",
                              "type" : "Literal"
                           } ]
                        } ]
                     }, {
                        "type" : "List",
                        "element" : [ {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "7",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "8",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "9",
                           "type" : "Literal"
                        } ]
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "10",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "11",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "12",
                        "type" : "Literal"
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "13",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "14",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "15",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "NullUnion",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Union",
               "operand" : [ {
                  "type" : "As",
                  "operand" : {
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
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "UnionNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Union",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "As",
                  "operand" : {
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
            "name" : "ExceptThreeFour",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Except",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "ThreeFourExcept",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Except",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "ExceptFiveThree",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Except",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "ExceptNoOp",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Except",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "7",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "9",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "ExceptEverything",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Except",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "SomethingExceptNothing",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Except",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List"
               } ]
            }
         }, {
            "name" : "NothingExceptSomething",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Except",
               "operand" : [ {
                  "type" : "List"
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "ExceptTuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Except",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "ExceptNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Except",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "As",
                  "operand" : {
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
            "name" : "NullExcept",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Except",
               "operand" : [ {
                  "type" : "As",
                  "operand" : {
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
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
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
            "name" : "NoIntersection",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IntersectOnFive",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "7",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IntersectOnEvens",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "7",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "9",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "12",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IntersectOnAll",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "NestedIntersects",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "Intersect",
                  "operand" : [ {
                     "type" : "Intersect",
                     "operand" : [ {
                        "type" : "List",
                        "element" : [ {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        } ]
                     }, {
                        "type" : "List",
                        "element" : [ {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "6",
                           "type" : "Literal"
                        } ]
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "7",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IntersectTuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "e",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "NullIntersect",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "As",
                  "operand" : {
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
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IntersectNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "As",
                  "operand" : {
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
            "name" : "IndexOfSecond",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "IndexOf",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "d",
                     "type" : "Literal"
                  } ]
               },
               "element" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "b",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "IndexOfThirdTuple",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "IndexOf",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               },
               "element" : {
                  "type" : "Tuple",
                  "element" : [ {
                     "name" : "a",
                     "value" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }
                  } ]
               }
            }
         }, {
            "name" : "MultipleMatches",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "IndexOf",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "d",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "d",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "e",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "d",
                     "type" : "Literal"
                  } ]
               },
               "element" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "d",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "ItemNotFound",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "IndexOf",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  } ]
               },
               "element" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "d",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "NullList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "IndexOf",
               "source" : {
                  "type" : "As",
                  "operand" : {
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
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "a",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "NullItem",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "IndexOf",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  } ]
               },
               "element" : {
                  "asType" : "{urn:hl7-org:elm-types:r1}String",
                  "type" : "As",
                  "operand" : {
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
            "name" : "SecondItem",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Indexer",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "d",
                     "type" : "Literal"
                  } ]
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "ZeroIndex",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Indexer",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "d",
                     "type" : "Literal"
                  } ]
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "0",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "OutOfBounds",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Indexer",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "d",
                     "type" : "Literal"
                  } ]
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "100",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "NullList",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Indexer",
               "operand" : [ {
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "name" : "{urn:hl7-org:elm-types:r1}String",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "NullIndexer",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Indexer",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "d",
                     "type" : "Literal"
                  } ]
               }, {
                  "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "As",
                  "operand" : {
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
            "name" : "IsIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "In",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IsNotIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "In",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "TupleIsIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "In",
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
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "c",
                        "type" : "Literal"
                     }
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "TupleIsNotIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "In",
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
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "c",
                        "type" : "Literal"
                     }
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "NullIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "In",
               "operand" : [ {
                  "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "As",
                  "operand" : {
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "InNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "In",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "type" : "As",
                  "operand" : {
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
            "name" : "IsIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Contains",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "IsNotIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Contains",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               }, {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "TupleIsIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Contains",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
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
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "c",
                        "type" : "Literal"
                     }
                  } ]
               } ]
            }
         }, {
            "name" : "TupleIsNotIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Contains",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
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
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "c",
                        "type" : "Literal"
                     }
                  } ]
               } ]
            }
         }, {
            "name" : "InNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Contains",
               "operand" : [ {
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
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
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "NullIn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Contains",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               }, {
                  "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "As",
                  "operand" : {
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
            "name" : "IsIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Includes",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IsIncludedReversed",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Includes",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IsSame",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Includes",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IsNotIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Includes",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "TuplesIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Includes",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "TuplesNotIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Includes",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "NullIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Contains",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "As",
                  "operand" : {
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "NamedTypeSpecifier"
                  }
               } ]
            }
         }, {
            "name" : "NullIncludes",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Includes",
               "operand" : [ {
                  "type" : "As",
                  "operand" : {
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
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
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
            "name" : "IsIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "IncludedIn",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IsIncludedReversed",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "IncludedIn",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IsSame",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "IncludedIn",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IsNotIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "IncludedIn",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "TuplesIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "IncludedIn",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "TuplesNotIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "IncludedIn",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "NullIncludes",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "IncludedIn",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "As",
                  "operand" : {
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
            "name" : "NullIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "In",
               "operand" : [ {
                  "type" : "Null"
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
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
            "name" : "IsIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ProperIncludes",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IsIncludedReversed",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ProperIncludes",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IsSame",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ProperIncludes",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IsNotIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ProperIncludes",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "TuplesIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ProperIncludes",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "TuplesNotIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ProperIncludes",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "NullIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ProperContains",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "As",
                  "operand" : {
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "NamedTypeSpecifier"
                  }
               } ]
            }
         }, {
            "name" : "NullIncludes",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ProperIncludes",
               "operand" : [ {
                  "type" : "As",
                  "operand" : {
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
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
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
            "name" : "IsIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ProperIncludedIn",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IsIncludedReversed",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ProperIncludedIn",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IsSame",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ProperIncludedIn",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IsNotIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ProperIncludedIn",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "TuplesIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ProperIncludedIn",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "TuplesNotIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ProperIncludedIn",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "d",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "c",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "NullIncludes",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ProperIncludedIn",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "As",
                  "operand" : {
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
            "name" : "NullIncluded",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ProperIncludedIn",
               "operand" : [ {
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
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
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
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
            "name" : "ListOfLists",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Flatten",
               "operand" : {
                  "type" : "List",
                  "element" : [ {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "9",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "9",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "NullValue",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Flatten",
               "operand" : {
                  "type" : "As",
                  "operand" : {
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
            "name" : "LotsOfDups",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Distinct",
               "operand" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "NoDups",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Distinct",
               "operand" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "DupsTuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Distinct",
               "operand" : {
                  "type" : "List",
                  "element" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "hello",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "world",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "hello",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "cleveland",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "hello",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "world",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "hello",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "dolly",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "NoDupsTuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Distinct",
               "operand" : {
                  "type" : "List",
                  "element" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "hello",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}String",
                           "value" : "world",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "hello",
                        "value" : {
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
            "name" : "Numbers",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "First",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "Letters",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "First",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "Lists",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "First",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "a",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "b",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "c",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "d",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "e",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "f",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "Tuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "First",
               "source" : {
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "c",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "24",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "25",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "c",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "26",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "Unordered",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "First",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "Empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "First",
               "source" : {
                  "type" : "List"
               }
            }
         }, {
            "name" : "NullValue",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "First",
               "source" : {
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
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
            "name" : "Numbers",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Last",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "Letters",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Last",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "c",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "Lists",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Last",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "a",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "b",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "c",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "d",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "e",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}String",
                        "value" : "f",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "Tuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Last",
               "source" : {
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "c",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "24",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "25",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "c",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "26",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "Unordered",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Last",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "Empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Last",
               "source" : {
                  "type" : "List"
               }
            }
         }, {
            "name" : "NullValue",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Last",
               "source" : {
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
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
            "name" : "Numbers",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Length",
               "operand" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "Lists",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Length",
               "operand" : {
                  "type" : "List",
                  "element" : [ {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "9",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "10",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "11",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "12",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "Tuples",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Length",
               "operand" : {
                  "type" : "List",
                  "element" : [ {
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
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "c",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "24",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "25",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "c",
                        "value" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "26",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "Empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Length",
               "operand" : {
                  "type" : "List"
               }
            }
         }, {
            "name" : "NullValue",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Length",
               "operand" : {
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
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

