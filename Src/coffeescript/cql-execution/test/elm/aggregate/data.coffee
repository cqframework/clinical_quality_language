###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
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
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Count",
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
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Count",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Count",
               "source" : {
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
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Sum",
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
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Sum",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "not_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Sum",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "has_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Sum",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "unmatched_units_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Min",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "L",
                     "type" : "Quantity"
                  }, {
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Sum",
               "source" : {
                  "type" : "List"
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
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Min",
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
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Min",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "type" : "Negate",
                     "operand" : {
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Min",
               "source" : {
                  "type" : "List"
               }
            }
         }, {
            "name" : "not_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Min",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "has_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Min",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "type" : "Negate",
                     "operand" : {
                        "value" : 1,
                        "unit" : "ml",
                        "type" : "Quantity"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "value" : 2,
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
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Max",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }, {
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
               }
            }
         }, {
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Max",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "not_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Max",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 10,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "has_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Max",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Max",
               "source" : {
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
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Avg",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
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
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "name" : "ToDecimal",
                        "libraryName" : "System",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "name" : "X",
                           "type" : "AliasRef"
                        } ]
                     }
                  }
               }
            }
         }, {
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Avg",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "type" : "List",
                        "element" : [ {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "type" : "As",
                           "operand" : {
                              "type" : "Null"
                           }
                        }, {
                           "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "type" : "As",
                           "operand" : {
                              "type" : "Null"
                           }
                        }, {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "name" : "ToDecimal",
                        "libraryName" : "System",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "name" : "X",
                           "type" : "AliasRef"
                        } ]
                     }
                  }
               }
            }
         }, {
            "name" : "not_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Avg",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "has_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Avg",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Avg",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "type" : "List"
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "name" : "ToDecimal",
                        "libraryName" : "System",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "name" : "X",
                           "type" : "AliasRef"
                        } ]
                     }
                  }
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
            "name" : "odd",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Median",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "type" : "List",
                        "element" : [ {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        }, {
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
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "name" : "ToDecimal",
                        "libraryName" : "System",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "name" : "X",
                           "type" : "AliasRef"
                        } ]
                     }
                  }
               }
            }
         }, {
            "name" : "even",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Median",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "type" : "List",
                        "element" : [ {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        }, {
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
                           "value" : "6",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "name" : "ToDecimal",
                        "libraryName" : "System",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "name" : "X",
                           "type" : "AliasRef"
                        } ]
                     }
                  }
               }
            }
         }, {
            "name" : "odd_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Median",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "even_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Median",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 6,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Median",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "type" : "List"
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "name" : "ToDecimal",
                        "libraryName" : "System",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "name" : "X",
                           "type" : "AliasRef"
                        } ]
                     }
                  }
               }
            }
         }, {
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Median",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "type" : "List",
                        "element" : [ {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "type" : "As",
                           "operand" : {
                              "type" : "Null"
                           }
                        }, {
                           "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "type" : "As",
                           "operand" : {
                              "type" : "Null"
                           }
                        }, {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "name" : "ToDecimal",
                        "libraryName" : "System",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "name" : "X",
                           "type" : "AliasRef"
                        } ]
                     }
                  }
               }
            }
         }, {
            "name" : "dup_vals_even",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Median",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
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
                           "value" : "2",
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
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "name" : "ToDecimal",
                        "libraryName" : "System",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "name" : "X",
                           "type" : "AliasRef"
                        } ]
                     }
                  }
               }
            }
         }, {
            "name" : "dup_vals_odd",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Median",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
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
                           "value" : "2",
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
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "name" : "ToDecimal",
                        "libraryName" : "System",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "name" : "X",
                           "type" : "AliasRef"
                        } ]
                     }
                  }
               }
            }
         }, {
            "name" : "has_null_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Median",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "dup_vals_even_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Median",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  } ]
               }
            }
         }, {
            "name" : "dup_vals_odd_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Median",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 5,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
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
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Mode",
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
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Mode",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Mode",
               "source" : {
                  "type" : "List"
               }
            }
         }, {
            "name" : "bi_modal",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Mode",
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
            "name" : "v",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Variance",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
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
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "name" : "ToDecimal",
                        "libraryName" : "System",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "name" : "X",
                           "type" : "AliasRef"
                        } ]
                     }
                  }
               }
            }
         }, {
            "name" : "v_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Variance",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
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

### PopulationVariance
library TestSnippet version '1'
using QUICK
context Patient
define v: PopulationVariance({1.0,2.0,3.0,4.0,5.0})
define v_q: PopulationVariance({1.0 'ml',2.0 'ml',3.0 'ml',4.0 'ml',5.0 'ml'})
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
            "name" : "v",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "PopulationVariance",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "2.0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "3.0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "4.0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
                     "value" : "5.0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "v_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "PopulationVariance",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 1.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 3.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 4.0,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
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
            "name" : "std",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "StdDev",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
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
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "name" : "ToDecimal",
                        "libraryName" : "System",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "name" : "X",
                           "type" : "AliasRef"
                        } ]
                     }
                  }
               }
            }
         }, {
            "name" : "std_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "StdDev",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
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

### PopulationStdDev
library TestSnippet version '1'
using QUICK
context Patient
define dev: PopulationStdDev({1,2,3,4,5})
define dev_q: PopulationStdDev({1 'ml',2 'ml',3 'ml',4 'ml',5 'ml'})
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
            "name" : "dev",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "PopulationStdDev",
               "source" : {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
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
                     }
                  } ],
                  "return" : {
                     "distinct" : false,
                     "expression" : {
                        "name" : "ToDecimal",
                        "libraryName" : "System",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "name" : "X",
                           "type" : "AliasRef"
                        } ]
                     }
                  }
               }
            }
         }, {
            "name" : "dev_q",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "PopulationStdDev",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "value" : 1,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 2,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 3,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
                     "value" : 4,
                     "unit" : "ml",
                     "type" : "Quantity"
                  }, {
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
            "name" : "at",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "AllTrue",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "atwn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "AllTrue",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "atf",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "AllTrue",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "atfwn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "AllTrue",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
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
            "name" : "at",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "AnyTrue",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "atwn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "AnyTrue",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "atf",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "AnyTrue",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "atfwn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "AnyTrue",
               "source" : {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
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

