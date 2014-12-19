###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### DateRangeOptimizedQuery
library TestSnippet version '1'
using QUICK
parameter MeasurementPeriod default interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))
valueset "Ambulatory/ED Visit" = '2.16.840.1.113883.3.464.1003.101.12.1061'
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
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2014",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                  "dataType" : "{http://org.hl7.fhir}Patient",
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
                     "dataType" : "{http://org.hl7.fhir}Encounter",
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
                     "dataType" : "{http://org.hl7.fhir}Encounter",
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
                     "dataType" : "{http://org.hl7.fhir}Encounter",
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
parameter MeasurementPeriod default interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))
valueset "Ambulatory/ED Visit" = '2.16.840.1.113883.3.464.1003.101.12.1061'
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
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2014",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                  "dataType" : "{http://org.hl7.fhir}Patient",
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
                     "dataType" : "{http://org.hl7.fhir}Encounter",
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
where  E.period  included in MeasurementPeriod and  C.id = 'http://cqframework.org/3/2'

define msQuery = foreach [Encounter] E, [Condition] C return {E: E, C:C}
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
            "localIdentifier" : "QUICK",
            "uri" : "http://org.hl7.fhir"
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2014",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                  "dataType" : "{http://org.hl7.fhir}Patient",
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "msQueryWhere",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "dateProperty" : "period",
                     "type" : "Retrieve",
                     "dateRange" : {
                        "name" : "MeasurementPeriod",
                        "type" : "ParameterRef"
                     }
                  }
               }, {
                  "alias" : "C",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Condition",
                     "templateId" : "cqf-condition",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "return" : {
                  "distinct" : true,
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
         }, {
            "name" : "msQueryWhere2",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "dateProperty" : "period",
                     "type" : "Retrieve",
                     "dateRange" : {
                        "name" : "MeasurementPeriod",
                        "type" : "ParameterRef"
                     }
                  }
               }, {
                  "alias" : "C",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Condition",
                     "templateId" : "cqf-condition",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ ],
               "where" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "path" : "id",
                     "scope" : "C",
                     "type" : "Property"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "http://cqframework.org/3/2",
                     "type" : "Literal"
                  } ]
               },
               "return" : {
                  "distinct" : true,
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
         }, {
            "name" : "msQuery",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               }, {
                  "alias" : "C",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Condition",
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
with [Condition] C such that C.id= 'http://cqframework.org/3/2'

define withQuery2 =  [Encounter] E
with [Condition] C such that C.id = 'http://cqframework.org/3'

define withOutQuery =  [Encounter] E
without [Condition] C such that C.id = 'http://cqframework.org/3/'

define withOutQuery2 =  [Encounter] E
without [Condition] C such that C.id = 'http://cqframework.org/3/2'
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
            "name" : "withQuery",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ {
                  "alias" : "C",
                  "type" : "With",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Condition",
                     "templateId" : "cqf-condition",
                     "type" : "Retrieve"
                  },
                  "suchThat" : {
                     "type" : "Equal",
                     "operand" : [ {
                        "path" : "id",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                        "value" : "http://cqframework.org/3/2",
                        "type" : "Literal"
                     } ]
                  }
               } ]
            }
         }, {
            "name" : "withQuery2",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ {
                  "alias" : "C",
                  "type" : "With",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Condition",
                     "templateId" : "cqf-condition",
                     "type" : "Retrieve"
                  },
                  "suchThat" : {
                     "type" : "Equal",
                     "operand" : [ {
                        "path" : "id",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                        "value" : "http://cqframework.org/3",
                        "type" : "Literal"
                     } ]
                  }
               } ]
            }
         }, {
            "name" : "withOutQuery",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ {
                  "alias" : "C",
                  "type" : "Without",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Condition",
                     "templateId" : "cqf-condition",
                     "type" : "Retrieve"
                  },
                  "suchThat" : {
                     "type" : "Equal",
                     "operand" : [ {
                        "path" : "id",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                        "value" : "http://cqframework.org/3/",
                        "type" : "Literal"
                     } ]
                  }
               } ]
            }
         }, {
            "name" : "withOutQuery2",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
                     "templateId" : "cqf-encounter",
                     "type" : "Retrieve"
                  }
               } ],
               "relationship" : [ {
                  "alias" : "C",
                  "type" : "Without",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Condition",
                     "templateId" : "cqf-condition",
                     "type" : "Retrieve"
                  },
                  "suchThat" : {
                     "type" : "Equal",
                     "operand" : [ {
                        "path" : "id",
                        "scope" : "C",
                        "type" : "Property"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                        "value" : "http://cqframework.org/3/2",
                        "type" : "Literal"
                     } ]
                  }
               } ]
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
            "name" : "query",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
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
            "name" : "query",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
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
define singleAsc =  [Encounter] E  return {E : E} sort by E.id
define singleDesc =  [Encounter] E return {E : E} sort by E.id desc
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
            "name" : "singleAsc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
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
            "name" : "singleDesc",
            "context" : "Patient",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://org.hl7.fhir}Encounter",
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
         } ]
      }
   }
}

