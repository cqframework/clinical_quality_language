###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### DateRangeOptimizedQuery
library TestSnippet version '1'
using QUICK
valueset "Ambulatory/ED Visit" = '2.16.840.1.113883.3.464.1003.101.12.1061'
parameter MeasurementPeriod default interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))
context Patient
define EncountersDuringMP = [Encounter] E where E.period during MeasurementPeriod
define AmbulatoryEncountersDuringMP = [Encounter: "Ambulatory/ED Visit"] E where E.period during MeasurementPeriod
define AmbulatoryEncountersIncludedInMP = [Encounter: "Ambulatory/ED Visit"] E where E.period included in MeasurementPeriod
###

module.exports['DateRangeOptimizedQuery'] = {
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
      "parameters" : {
         "def" : [ {
            "name" : "MeasurementPeriod",
            "default" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2014",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               }
            }
         } ]
      },
      "valueSets" : {
         "def" : [ {
            "name" : "Ambulatory/ED Visit",
            "id" : "2.16.840.1.113883.3.464.1003.101.12.1061"
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
            "name" : "EncountersDuringMP",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "dateProperty" : "period",
                     "type" : "Retrieve",
                     "dateRange" : {
                        "name" : "MeasurementPeriod",
                        "type" : "ParameterRef"
                     }
                  }
               } ],
               "relationship" : [ ]
            }
         }, {
            "name" : "AmbulatoryEncountersDuringMP",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "codeProperty" : "type",
                     "dateProperty" : "period",
                     "type" : "Retrieve",
                     "codes" : {
                        "name" : "Ambulatory/ED Visit",
                        "type" : "ValueSetRef"
                     },
                     "dateRange" : {
                        "name" : "MeasurementPeriod",
                        "type" : "ParameterRef"
                     }
                  }
               } ],
               "relationship" : [ ]
            }
         }, {
            "name" : "AmbulatoryEncountersIncludedInMP",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "codeProperty" : "type",
                     "dateProperty" : "period",
                     "type" : "Retrieve",
                     "codes" : {
                        "name" : "Ambulatory/ED Visit",
                        "type" : "ValueSetRef"
                     },
                     "dateRange" : {
                        "name" : "MeasurementPeriod",
                        "type" : "ParameterRef"
                     }
                  }
               } ],
               "relationship" : [ ]
            }
         } ]
      }
   }
}

### IncludesQuery
library TestSnippet version '1'
using QUICK
valueset "Ambulatory/ED Visit" = '2.16.840.1.113883.3.464.1003.101.12.1061'
parameter MeasurementPeriod default interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))
context Patient
define MPIncludedAmbulatoryEncounters = [Encounter: "Ambulatory/ED Visit"] E where MeasurementPeriod includes E.period
###

module.exports['IncludesQuery'] = {
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
      "parameters" : {
         "def" : [ {
            "name" : "MeasurementPeriod",
            "default" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2014",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               }
            }
         } ]
      },
      "valueSets" : {
         "def" : [ {
            "name" : "Ambulatory/ED Visit",
            "id" : "2.16.840.1.113883.3.464.1003.101.12.1061"
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
            "name" : "MPIncludedAmbulatoryEncounters",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "codeProperty" : "type",
                     "type" : "Retrieve",
                     "codes" : {
                        "name" : "Ambulatory/ED Visit",
                        "type" : "ValueSetRef"
                     }
                  }
               } ],
               "relationship" : [ ],
               "where" : {
                  "type" : "Includes",
                  "operand" : [ {
                     "name" : "MeasurementPeriod",
                     "type" : "ParameterRef"
                  }, {
                     "path" : "period",
                     "scope" : "E",
                     "type" : "Property"
                  } ]
               }
            }
         } ]
      }
   }
}

### MultiSourceQuery
library TestSnippet version '1'
using QUICK
parameter MeasurementPeriod default interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))
context Patient
define msQueryWhere = foreach [Encounter] E,
[Condition] C
where E.period included in MeasurementPeriod

define msQueryWhere2 = foreach [Encounter] E, [Condition] C
where  E.period  included in MeasurementPeriod and C.id = 'http://cqframework.org/3/2'

define msQuery = foreach [Encounter] E, [Condition] C return {E: E, C:C}
###

###
Translation Error(s):
[5:23, 7:44] type
[10:52, 10:86] Could not resolve call to operator Equal with signature (QUICK.id,System.String).
[10:8, 10:86] Could not determine signature for invocation of operator System.And.
[10:1, 10:86] Expected an expression of type 'System.Boolean', but found an expression of type '<unknown>'.
[9:24, 10:86] type
###
module.exports['MultiSourceQuery'] = {
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
      "parameters" : {
         "def" : [ {
            "name" : "MeasurementPeriod",
            "default" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2014",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "msQueryWhere",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "msQueryWhere2",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "msQuery",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               }, {
                  "alias" : "C",
                  "expression" : {
                     "dataType" : "{http://hl7.org/fhir}Condition",
                     "templateId" : "cqf-condition",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "E",
                        "value" : {
                           "name" : "E",
                           "type" : "AliasRef"
                        }
                     }, {
                        "name" : "C",
                        "value" : {
                           "name" : "C",
                           "type" : "AliasRef"
                        }
                     } ]
                  }
               }
            }
         } ]
      }
   }
}

### QueryRelationship
library TestSnippet version '1'
using QUICK
context Patient
define withQuery =  [Encounter] E
with [Condition] C such that C.id = 'http://cqframework.org/3/2'

define withQuery2 =  [Encounter] E
with [Condition] C such that C.id = 'http://cqframework.org/3'

define withOutQuery =  [Encounter] E
without [Condition] C such that C.id = 'http://cqframework.org/3/'

define withOutQuery2 =  [Encounter] E
without [Condition] C such that C.id = 'http://cqframework.org/3/2'
###

###
Translation Error(s):
[5:30, 5:64] Could not resolve call to operator Equal with signature (QUICK.id,System.String).
[5:1, 5:64] Expected an expression of type 'System.Boolean', but found an expression of type '<unknown>'.
[4:21, 5:64] org.hl7.elm.r1.Null cannot be cast to org.hl7.elm.r1.RelationshipClause
[8:30, 8:62] Could not resolve call to operator Equal with signature (QUICK.id,System.String).
[8:1, 8:62] Expected an expression of type 'System.Boolean', but found an expression of type '<unknown>'.
[7:22, 8:62] org.hl7.elm.r1.Null cannot be cast to org.hl7.elm.r1.RelationshipClause
[11:33, 11:66] Could not resolve call to operator Equal with signature (QUICK.id,System.String).
[11:1, 11:66] Expected an expression of type 'System.Boolean', but found an expression of type '<unknown>'.
[10:24, 11:66] org.hl7.elm.r1.Null cannot be cast to org.hl7.elm.r1.RelationshipClause
[14:33, 14:67] Could not resolve call to operator Equal with signature (QUICK.id,System.String).
[14:1, 14:67] Expected an expression of type 'System.Boolean', but found an expression of type '<unknown>'.
[13:25, 14:67] org.hl7.elm.r1.Null cannot be cast to org.hl7.elm.r1.RelationshipClause
###
module.exports['QueryRelationship'] = {
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
            "name" : "withQuery",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "withQuery2",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "withOutQuery",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "withOutQuery2",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         } ]
      }
   }
}

### QueryDefine
library TestSnippet version '1'
using QUICK
context Patient
define query =  [Encounter] E
define a = E
return {E: E, a:a}
###

module.exports['QueryDefine'] = {
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
            "name" : "query",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "define" : [ {
                  "identifier" : "a",
                  "expression" : {
                     "name" : "E",
                     "type" : "AliasRef"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "E",
                        "value" : {
                           "name" : "E",
                           "type" : "AliasRef"
                        }
                     }, {
                        "name" : "a",
                        "value" : {
                           "name" : "a",
                           "type" : "QueryDefineRef"
                        }
                     } ]
                  }
               }
            }
         } ]
      }
   }
}

### Tuple
library TestSnippet version '1'
using QUICK
context Patient
define query =  [Encounter] E return {id: E.id, thing: E.status}
###

module.exports['Tuple'] = {
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
            "name" : "query",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "id",
                        "value" : {
                           "path" : "id",
                           "scope" : "E",
                           "type" : "Property"
                        }
                     }, {
                        "name" : "thing",
                        "value" : {
                           "path" : "status",
                           "scope" : "E",
                           "type" : "Property"
                        }
                     } ]
                  }
               }
            }
         } ]
      }
   }
}

### Sorting
library TestSnippet version '1'
using QUICK
context Patient
define tupleAsc = [Encounter] E sort by id
define tupleReturnAsc = [Encounter] E return E sort by id
define tupleReturnTupleAsc = [Encounter] E return {E : E} sort by E.id
define tupleDesc = [Encounter] E sort by id desc
define tupleReturnDesc = [Encounter] E return E sort by id desc
define tupleReturnTupleDesc =  [Encounter] E return {E : E} sort by E.id desc
define numberAsc = ({8, 6, 7, 5, 3, 0, 9}) N sort asc
define numberReturnAsc = ({8, 6, 7, 5, 3, 0, 9}) N return N sort asc
define numberDesc = ({8, 6, 7, 5, 3, 0, 9}) N sort desc
define numberReturnDesc = ({8, 6, 7, 5, 3, 0, 9}) N return N sort desc
define stringAsc = ({'jenny', 'dont', 'change', 'your', 'number'}) S sort asc
define stringReturnAsc = ({'jenny', 'dont', 'change', 'your', 'number'}) S return S sort asc
define stringDesc = ({'jenny', 'dont', 'change', 'your', 'number'}) S sort desc
define stringReturnDesc = ({'jenny', 'dont', 'change', 'your', 'number'}) S return S sort desc
###

module.exports['Sorting'] = {
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
            "name" : "tupleAsc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "sort" : {
                  "by" : [ {
                     "direction" : "asc",
                     "type" : "ByExpression",
                     "expression" : {
                        "name" : "id",
                        "type" : "IdentifierRef"
                     }
                  } ]
               }
            }
         }, {
            "name" : "tupleReturnAsc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "name" : "E",
                     "type" : "AliasRef"
                  }
               },
               "sort" : {
                  "by" : [ {
                     "direction" : "asc",
                     "type" : "ByExpression",
                     "expression" : {
                        "name" : "id",
                        "type" : "IdentifierRef"
                     }
                  } ]
               }
            }
         }, {
            "name" : "tupleReturnTupleAsc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "E",
                        "value" : {
                           "name" : "E",
                           "type" : "AliasRef"
                        }
                     } ]
                  }
               },
               "sort" : {
                  "by" : [ {
                     "direction" : "asc",
                     "type" : "ByExpression",
                     "expression" : {
                        "path" : "id",
                        "scope" : "E",
                        "type" : "Property"
                     }
                  } ]
               }
            }
         }, {
            "name" : "tupleDesc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "sort" : {
                  "by" : [ {
                     "direction" : "desc",
                     "type" : "ByExpression",
                     "expression" : {
                        "name" : "id",
                        "type" : "IdentifierRef"
                     }
                  } ]
               }
            }
         }, {
            "name" : "tupleReturnDesc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "name" : "E",
                     "type" : "AliasRef"
                  }
               },
               "sort" : {
                  "by" : [ {
                     "direction" : "desc",
                     "type" : "ByExpression",
                     "expression" : {
                        "name" : "id",
                        "type" : "IdentifierRef"
                     }
                  } ]
               }
            }
         }, {
            "name" : "tupleReturnTupleDesc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "type" : "Tuple",
                     "element" : [ {
                        "name" : "E",
                        "value" : {
                           "name" : "E",
                           "type" : "AliasRef"
                        }
                     } ]
                  }
               },
               "sort" : {
                  "by" : [ {
                     "direction" : "desc",
                     "type" : "ByExpression",
                     "expression" : {
                        "path" : "id",
                        "scope" : "E",
                        "type" : "Property"
                     }
                  } ]
               }
            }
         }, {
            "name" : "numberAsc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "N",
                  "expression" : {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "9",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "sort" : {
                  "by" : [ {
                     "direction" : "asc",
                     "type" : "ByDirection"
                  } ]
               }
            }
         }, {
            "name" : "numberReturnAsc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "N",
                  "expression" : {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "9",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "name" : "N",
                     "type" : "AliasRef"
                  }
               },
               "sort" : {
                  "by" : [ {
                     "direction" : "asc",
                     "type" : "ByDirection"
                  } ]
               }
            }
         }, {
            "name" : "numberDesc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "N",
                  "expression" : {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "9",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "sort" : {
                  "by" : [ {
                     "direction" : "desc",
                     "type" : "ByDirection"
                  } ]
               }
            }
         }, {
            "name" : "numberReturnDesc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "N",
                  "expression" : {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "8",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "6",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "3",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "9",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "name" : "N",
                     "type" : "AliasRef"
                  }
               },
               "sort" : {
                  "by" : [ {
                     "direction" : "desc",
                     "type" : "ByDirection"
                  } ]
               }
            }
         }, {
            "name" : "stringAsc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "S",
                  "expression" : {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "jenny",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "dont",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "change",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "your",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "number",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "sort" : {
                  "by" : [ {
                     "direction" : "asc",
                     "type" : "ByDirection"
                  } ]
               }
            }
         }, {
            "name" : "stringReturnAsc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "S",
                  "expression" : {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "jenny",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "dont",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "change",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "your",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "number",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "name" : "S",
                     "type" : "AliasRef"
                  }
               },
               "sort" : {
                  "by" : [ {
                     "direction" : "asc",
                     "type" : "ByDirection"
                  } ]
               }
            }
         }, {
            "name" : "stringDesc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "S",
                  "expression" : {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "jenny",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "dont",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "change",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "your",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "number",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "sort" : {
                  "by" : [ {
                     "direction" : "desc",
                     "type" : "ByDirection"
                  } ]
               }
            }
         }, {
            "name" : "stringReturnDesc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "S",
                  "expression" : {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "jenny",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "dont",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "change",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "your",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "number",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "name" : "S",
                     "type" : "AliasRef"
                  }
               },
               "sort" : {
                  "by" : [ {
                     "direction" : "desc",
                     "type" : "ByDirection"
                  } ]
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
define defaultNumbers = ({1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 3, 3, 3, 2, 2, 1}) N return N
define defaultStrings = ({'foo', 'bar', 'baz', 'bar'}) S return S
define defaultTuples = ({tuple{a: 1, b:2}, tuple{a: 2, b: 3}, tuple{a: 1, b: 2}}) T return T
define distinctNumbers = ({1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 3, 3, 3, 2, 2, 1}) N return distinct N
define distinctStrings = ({'foo', 'bar', 'baz', 'bar'}) S return distinct S
define distinctTuples = ({tuple{a: 1, b:2}, tuple{a: 2, b: 3}, tuple{a: 1, b: 2}}) T return distinct T
define allNumbers = ({1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 3, 3, 3, 2, 2, 1}) N return all N
define allStrings = ({'foo', 'bar', 'baz', 'bar'}) S return all S
define allTuples = ({tuple{a: 1, b:2}, tuple{a: 2, b: 3}, tuple{a: 1, b: 2}}) T return all T
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
            "name" : "defaultNumbers",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "N",
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
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "4",
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
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "name" : "N",
                     "type" : "AliasRef"
                  }
               }
            }
         }, {
            "name" : "defaultStrings",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "S",
                  "expression" : {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "foo",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "bar",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "baz",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "bar",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "name" : "S",
                     "type" : "AliasRef"
                  }
               }
            }
         }, {
            "name" : "defaultTuples",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "T",
                  "expression" : {
                     "type" : "List",
                     "element" : [ {
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     }, {
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "3",
                              "type" : "Literal"
                           }
                        } ]
                     }, {
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "expression" : {
                     "name" : "T",
                     "type" : "AliasRef"
                  }
               }
            }
         }, {
            "name" : "distinctNumbers",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "N",
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
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "4",
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
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "distinct" : true,
                  "expression" : {
                     "name" : "N",
                     "type" : "AliasRef"
                  }
               }
            }
         }, {
            "name" : "distinctStrings",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "S",
                  "expression" : {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "foo",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "bar",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "baz",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "bar",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "distinct" : true,
                  "expression" : {
                     "name" : "S",
                     "type" : "AliasRef"
                  }
               }
            }
         }, {
            "name" : "distinctTuples",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "T",
                  "expression" : {
                     "type" : "List",
                     "element" : [ {
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     }, {
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "3",
                              "type" : "Literal"
                           }
                        } ]
                     }, {
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "distinct" : true,
                  "expression" : {
                     "name" : "T",
                     "type" : "AliasRef"
                  }
               }
            }
         }, {
            "name" : "allNumbers",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "N",
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
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "4",
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
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "distinct" : false,
                  "expression" : {
                     "name" : "N",
                     "type" : "AliasRef"
                  }
               }
            }
         }, {
            "name" : "allStrings",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "S",
                  "expression" : {
                     "type" : "List",
                     "element" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "foo",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "bar",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "baz",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}String",
                        "value" : "bar",
                        "type" : "Literal"
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "distinct" : false,
                  "expression" : {
                     "name" : "S",
                     "type" : "AliasRef"
                  }
               }
            }
         }, {
            "name" : "allTuples",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "T",
                  "expression" : {
                     "type" : "List",
                     "element" : [ {
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     }, {
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "3",
                              "type" : "Literal"
                           }
                        } ]
                     }, {
                        "type" : "Tuple",
                        "element" : [ {
                           "name" : "a",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "1",
                              "type" : "Literal"
                           }
                        }, {
                           "name" : "b",
                           "value" : {
                              "valueType" : "{urn:hl7-org:elm:r1}Integer",
                              "value" : "2",
                              "type" : "Literal"
                           }
                        } ]
                     } ]
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "distinct" : false,
                  "expression" : {
                     "name" : "T",
                     "type" : "AliasRef"
                  }
               }
            }
         } ]
      }
   }
}

