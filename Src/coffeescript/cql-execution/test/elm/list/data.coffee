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
define Three = 1 + 2
define IntList = { 9, 7, 8 }
define StringList = { 'a', 'bee', 'see' }
define MixedList = { 1, 'two', Three }
define EmptyList = {}
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
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Patient",
            "context" : "Patient",
            "expression" : {
               "type" : "SingletonFrom",
               "operand" : {
                  "dataType" : "{http://org.hl7.fhir}Patient",
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "Three",
            "context" : "Patient",
            "expression" : {
               "type" : "Add",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "2",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "IntList",
            "context" : "Patient",
            "expression" : {
               "type" : "List",
               "element" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "9",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "7",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "8",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "StringList",
            "context" : "Patient",
            "expression" : {
               "type" : "List",
               "element" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "a",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "bee",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "see",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "MixedList",
            "context" : "Patient",
            "expression" : {
               "type" : "List",
               "element" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "1",
                  "type" : "Literal"
               }, {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
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
define EmptyList = exists ({})
define FullList = exists ({ 1, 2, 3 })
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
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Patient",
            "context" : "Patient",
            "expression" : {
               "type" : "SingletonFrom",
               "operand" : {
                  "dataType" : "{http://org.hl7.fhir}Patient",
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "EmptyList",
            "context" : "Patient",
            "expression" : {
               "type" : "Exists",
               "operand" : {
                  "type" : "List"
               }
            }
         }, {
            "name" : "FullList",
            "context" : "Patient",
            "expression" : {
               "type" : "Exists",
               "operand" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
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
define OneToTen = {1, 2, 3, 4, 5} union {6, 7, 8, 9, 10}
define OneToFiveOverlapped = {1, 2, 3, 4} union {3, 4, 5}
define Disjoint = {1, 2} union {4, 5}
define NestedToFifteen = {1, 2, 3} union {4, 5, 6} union {7 ,8 , 9} union {10, 11, 12} union {13, 14, 15}
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
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Patient",
            "context" : "Patient",
            "expression" : {
               "type" : "SingletonFrom",
               "operand" : {
                  "dataType" : "{http://org.hl7.fhir}Patient",
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "OneToTen",
            "context" : "Patient",
            "expression" : {
               "type" : "Union",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "7",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "9",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "10",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "OneToFiveOverlapped",
            "context" : "Patient",
            "expression" : {
               "type" : "Union",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "Disjoint",
            "context" : "Patient",
            "expression" : {
               "type" : "Union",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "NestedToFifteen",
            "context" : "Patient",
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
                              "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                              "value" : "1",
                              "type" : "Literal"
                           }, {
                              "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                              "value" : "2",
                              "type" : "Literal"
                           }, {
                              "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                              "value" : "3",
                              "type" : "Literal"
                           } ]
                        }, {
                           "type" : "List",
                           "element" : [ {
                              "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                              "value" : "4",
                              "type" : "Literal"
                           }, {
                              "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                              "value" : "5",
                              "type" : "Literal"
                           }, {
                              "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                              "value" : "6",
                              "type" : "Literal"
                           } ]
                        } ]
                     }, {
                        "type" : "List",
                        "element" : [ {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "7",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "8",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "9",
                           "type" : "Literal"
                        } ]
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "10",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "11",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "12",
                        "type" : "Literal"
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "13",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "14",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "15",
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
define NoIntersection = {1, 2, 3} intersect {4, 5, 6}
define IntersectOnFive = {4, 5, 6} intersect {1, 3, 5, 7}
define IntersectOnEvens = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10} intersect {0, 2, 4, 6, 8, 10, 12}
define IntersectOnAll = {1, 2, 3, 4, 5} intersect {5, 4, 3, 2, 1}
define NestedIntersects = {1, 2, 3, 4, 5} intersect {2, 3, 4, 5, 6} intersect {3, 4, 5, 6, 7} intersect {4, 5, 6, 7, 8}
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
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Patient",
            "context" : "Patient",
            "expression" : {
               "type" : "SingletonFrom",
               "operand" : {
                  "dataType" : "{http://org.hl7.fhir}Patient",
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "NoIntersection",
            "context" : "Patient",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IntersectOnFive",
            "context" : "Patient",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "7",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IntersectOnEvens",
            "context" : "Patient",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "7",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "9",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "10",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "10",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "12",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IntersectOnAll",
            "context" : "Patient",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "NestedIntersects",
            "context" : "Patient",
            "expression" : {
               "type" : "Intersect",
               "operand" : [ {
                  "type" : "Intersect",
                  "operand" : [ {
                     "type" : "Intersect",
                     "operand" : [ {
                        "type" : "List",
                        "element" : [ {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "5",
                           "type" : "Literal"
                        } ]
                     }, {
                        "type" : "List",
                        "element" : [ {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "5",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "6",
                           "type" : "Literal"
                        } ]
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "6",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "7",
                        "type" : "Literal"
                     } ]
                  } ]
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "7",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "8",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

### InList
library TestSnippet version '1'
using QUICK
context Patient
define IsIn = 4 in { 3, 4, 5 }
define IsNotIn = 4 in { 3, 5, 6 }
###

module.exports['InList'] = {
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
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Patient",
            "context" : "Patient",
            "expression" : {
               "type" : "SingletonFrom",
               "operand" : {
                  "dataType" : "{http://org.hl7.fhir}Patient",
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "IsIn",
            "context" : "Patient",
            "expression" : {
               "type" : "In",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "IsNotIn",
            "context" : "Patient",
            "expression" : {
               "type" : "In",
               "operand" : [ {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                  "value" : "4",
                  "type" : "Literal"
               }, {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

### Expand
library TestSnippet version '1'
using QUICK
context Patient
define ListOfLists = expand { {1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {9, 8, 7, 6, 5}, {4}, {3, 2, 1} }
define ListOfInts = expand { 1, 2, 3, 4, 5, 6, 7, 8, 9 }
define MixedList = expand { 1, 2, 3, {4, 5, 6}, 7, 8, 9 }
define NullValue = expand null
###

module.exports['Expand'] = {
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
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Patient",
            "context" : "Patient",
            "expression" : {
               "type" : "SingletonFrom",
               "operand" : {
                  "dataType" : "{http://org.hl7.fhir}Patient",
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "ListOfLists",
            "context" : "Patient",
            "expression" : {
               "type" : "Expand",
               "operand" : {
                  "type" : "List",
                  "element" : [ {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "3",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "6",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "9",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "9",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "6",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "5",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "4",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "ListOfInts",
            "context" : "Patient",
            "expression" : {
               "type" : "Expand",
               "operand" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "7",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "9",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "MixedList",
            "context" : "Patient",
            "expression" : {
               "type" : "Expand",
               "operand" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "6",
                        "type" : "Literal"
                     } ]
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "7",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "9",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "NullValue",
            "context" : "Patient",
            "expression" : {
               "type" : "Expand",
               "operand" : {
                  "type" : "Null"
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
define LotsOfDups = distinct {1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5, 4, 3, 2, 1}
define NoDups = distinct {2, 4, 6, 8, 10}
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
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Patient",
            "context" : "Patient",
            "expression" : {
               "type" : "SingletonFrom",
               "operand" : {
                  "dataType" : "{http://org.hl7.fhir}Patient",
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "LotsOfDups",
            "context" : "Patient",
            "expression" : {
               "type" : "Distinct",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "NoDups",
            "context" : "Patient",
            "expression" : {
               "type" : "Distinct",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "8",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "10",
                     "type" : "Literal"
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
define Numbers = First({1, 2, 3, 4})
define Letters = First({'a', 'b', 'c'})
define Lists = First({{'a','b','c'},{1,2,3}})
define Tuples = First({ tuple{a: 1, b: 2, c: 3}, tuple{x: 24, y: 25, z: 26} })
define Unordered = First({3, 1, 4, 2})
define Empty = First({})
define NullValue = First(null)
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
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Patient",
            "context" : "Patient",
            "expression" : {
               "type" : "SingletonFrom",
               "operand" : {
                  "dataType" : "{http://org.hl7.fhir}Patient",
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "Numbers",
            "context" : "Patient",
            "expression" : {
               "name" : "First",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "Letters",
            "context" : "Patient",
            "expression" : {
               "name" : "First",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "c",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "Lists",
            "context" : "Patient",
            "expression" : {
               "name" : "First",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                        "value" : "a",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                        "value" : "b",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                        "value" : "c",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "3",
                        "type" : "Literal"
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "Tuples",
            "context" : "Patient",
            "expression" : {
               "name" : "First",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "c",
                        "value" : {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "x",
                        "value" : {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "24",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "y",
                        "value" : {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "25",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "z",
                        "value" : {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "26",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "Unordered",
            "context" : "Patient",
            "expression" : {
               "name" : "First",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "Empty",
            "context" : "Patient",
            "expression" : {
               "name" : "First",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List"
               } ]
            }
         }, {
            "name" : "NullValue",
            "context" : "Patient",
            "expression" : {
               "name" : "First",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "Null"
               } ]
            }
         } ]
      }
   }
}

### Last
library TestSnippet version '1'
using QUICK
context Patient
define Numbers = Last({1, 2, 3, 4})
define Letters = Last({'a', 'b', 'c'})
define Lists = Last({{'a','b','c'},{1,2,3}})
define Tuples = Last({ tuple{a: 1, b: 2, c: 3}, tuple{x: 24, y: 25, z: 26} })
define Unordered = Last({3, 1, 4, 2})
define Empty = Last({})
define NullValue = Last(null)
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
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
         } ]
      },
      "statements" : {
         "def" : [ {
            "name" : "Patient",
            "context" : "Patient",
            "expression" : {
               "type" : "SingletonFrom",
               "operand" : {
                  "dataType" : "{http://org.hl7.fhir}Patient",
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "Numbers",
            "context" : "Patient",
            "expression" : {
               "name" : "Last",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "Letters",
            "context" : "Patient",
            "expression" : {
               "name" : "Last",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "a",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "b",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "c",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "Lists",
            "context" : "Patient",
            "expression" : {
               "name" : "Last",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                        "value" : "a",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                        "value" : "b",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                        "value" : "c",
                        "type" : "Literal"
                     } ]
                  }, {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "3",
                        "type" : "Literal"
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "Tuples",
            "context" : "Patient",
            "expression" : {
               "name" : "Last",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "a",
                        "value" : {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "1",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "b",
                        "value" : {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "2",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "c",
                        "value" : {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "3",
                           "type" : "Literal"
                        }
                     } ]
                  }, {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "x",
                        "value" : {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "24",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "y",
                        "value" : {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "25",
                           "type" : "Literal"
                        }
                     }, {
                        "name" : "z",
                        "value" : {
                           "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                           "value" : "26",
                           "type" : "Literal"
                        }
                     } ]
                  } ]
               } ]
            }
         }, {
            "name" : "Unordered",
            "context" : "Patient",
            "expression" : {
               "name" : "Last",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "Empty",
            "context" : "Patient",
            "expression" : {
               "name" : "Last",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List"
               } ]
            }
         }, {
            "name" : "NullValue",
            "context" : "Patient",
            "expression" : {
               "name" : "Last",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "Null"
               } ]
            }
         } ]
      }
   }
}

