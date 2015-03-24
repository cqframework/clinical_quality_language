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
            "uri" : "urn:hl7-org:elm:r1"
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Count",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Count",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Count",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List"
               } ]
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
            "uri" : "urn:hl7-org:elm:r1"
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Sum",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Sum",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Sum",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List"
               } ]
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
define empty: Min({})
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
            "uri" : "urn:hl7-org:elm:r1"
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Min",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Min",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "type" : "Negate",
                     "operand" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Min",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List"
               } ]
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
define empty: Max({})
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
            "uri" : "urn:hl7-org:elm:r1"
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Max",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Max",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Max",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List"
               } ]
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
            "uri" : "urn:hl7-org:elm:r1"
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Avg",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Avg",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Avg",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List"
               } ]
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

define empty: Median({})
define has_null: Median({1,null,null,2})
define dup_vals_even: Median({3,1,2,2,2,3,4,5})
define dup_vals_odd:  Median({3,1,2,2,2,3,4,5,6})
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
            "uri" : "urn:hl7-org:elm:r1"
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "odd",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Median",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "even",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Median",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Median",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List"
               } ]
            }
         }, {
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Median",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "dup_vals_even",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Median",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "dup_vals_odd",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Median",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  } ]
               } ]
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
            "uri" : "urn:hl7-org:elm:r1"
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "not_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Mode",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "has_null",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Mode",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "empty",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Mode",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List"
               } ]
            }
         }, {
            "name" : "bi_modal",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Mode",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  } ]
               } ]
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
            "uri" : "urn:hl7-org:elm:r1"
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "v",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Variance",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "type" : "List",
                        "element" : [ {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
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
               } ]
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
            "uri" : "urn:hl7-org:elm:r1"
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "v",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "PopulationVariance",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "2.0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "3.0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "4.0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "5.0",
                     "type" : "Literal"
                  } ]
               } ]
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
            "uri" : "urn:hl7-org:elm:r1"
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "std",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "StdDev",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "type" : "List",
                        "element" : [ {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
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
               } ]
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
            "uri" : "urn:hl7-org:elm:r1"
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "dev",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "PopulationStdDev",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "Query",
                  "source" : [ {
                     "alias" : "X",
                     "expression" : {
                        "type" : "List",
                        "element" : [ {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "2",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "3",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "4",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "5",
                           "type" : "Literal"
                        } ]
                     }
                  } ],
                  "return" : {
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
               } ]
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
            "uri" : "urn:hl7-org:elm:r1"
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "at",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "AllTrue",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "atwn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "AllTrue",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "atf",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "AllTrue",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "atfwn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "AllTrue",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  } ]
               } ]
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
            "uri" : "urn:hl7-org:elm:r1"
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "at",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "AnyTrue",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "atwn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "AnyTrue",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "true",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "atf",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "AnyTrue",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "atfwn",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "AnyTrue",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "List",
                  "element" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm:r1}Boolean",
                     "type" : "As",
                     "operand" : {
                        "type" : "Null"
                     }
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Boolean",
                     "value" : "false",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

