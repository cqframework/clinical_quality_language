###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### Interval
library TestSnippet version '1'
using QUICK
context Patient
define Open = interval(DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define LeftOpen = interval(DateTime(2012, 1, 1), DateTime(2013, 1, 1)]
define RightOpen = interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define Closed = interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)]
###

module.exports['Interval'] = {
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
            "name" : "Open",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : false,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
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
               }
            }
         }, {
            "name" : "LeftOpen",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : false,
               "highClosed" : true,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
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
               }
            }
         }, {
            "name" : "RightOpen",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
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
               }
            }
         }, {
            "name" : "Closed",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : true,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
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
define EqualClosed = interval[1, 5] = interval[1, 5]
define EqualOpen = interval(1, 5) = interval(1, 5)
define EqualOpenClosed = interval(1, 5) = interval[2, 4]
define UnequalClosed = interval[1, 5] = interval[2, 4]
define UnequalOpen = interval(1, 5) = interval(2, 4)
define UnequalClosedOpen = interval[1, 5] = interval(2, 4)
define EqualDates = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)) = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define EqualDatesOpenClosed = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)) = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define SameDays = interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)) = interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define DifferentDays = interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)) = interval[DateTime(2012, 1, 1), DateTime(2012, 7, 1))
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
            "name" : "EqualClosed",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "EqualOpen",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "lowClosed" : false,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : false,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "EqualOpenClosed",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "lowClosed" : false,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "UnequalClosed",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "UnequalOpen",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "lowClosed" : false,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : false,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "UnequalClosedOpen",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : false,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "4",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "EqualDates",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     } ]
                  },
                  "high" : {
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
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     } ]
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     } ]
                  },
                  "high" : {
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
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     } ]
                  }
               } ]
            }
         }, {
            "name" : "EqualDatesOpenClosed",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     } ]
                  },
                  "high" : {
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
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     } ]
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "0",
                        "type" : "Literal"
                     } ]
                  },
                  "high" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "12",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "31",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "23",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "59",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "59",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "999",
                        "type" : "Literal"
                     } ]
                  }
               } ]
            }
         }, {
            "name" : "SameDays",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2012",
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
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2012",
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
                  }
               } ]
            }
         }, {
            "name" : "DifferentDays",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2012",
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
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2012",
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
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     } ]
                  }
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
define EqualClosed = interval[1, 5] <> interval[1, 5]
define EqualOpen = interval(1, 5) <> interval(1, 5)
define EqualOpenClosed = interval(1, 5) <> interval[2, 4]
define UnequalClosed = interval[1, 5] <> interval[2, 4]
define UnequalOpen = interval(1, 5) <> interval(2, 4)
define UnequalClosedOpen = interval[1, 5] <> interval(2, 4)
define EqualDates = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)) <> interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define EqualDatesOpenClosed = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0)) <> interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 12, 31, 23, 59, 59, 999)]
define SameDays = interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)) <> interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1))
define DifferentDays = interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)) <> interval[DateTime(2012, 1, 1), DateTime(2012, 7, 1))
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
            "name" : "EqualClosed",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "lowClosed" : true,
                     "highClosed" : true,
                     "type" : "Interval",
                     "low" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     },
                     "high" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }
                  }, {
                     "lowClosed" : true,
                     "highClosed" : true,
                     "type" : "Interval",
                     "low" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     },
                     "high" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }
                  } ]
               }
            }
         }, {
            "name" : "EqualOpen",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "lowClosed" : false,
                     "highClosed" : false,
                     "type" : "Interval",
                     "low" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     },
                     "high" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }
                  }, {
                     "lowClosed" : false,
                     "highClosed" : false,
                     "type" : "Interval",
                     "low" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     },
                     "high" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }
                  } ]
               }
            }
         }, {
            "name" : "EqualOpenClosed",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "lowClosed" : false,
                     "highClosed" : false,
                     "type" : "Interval",
                     "low" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     },
                     "high" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }
                  }, {
                     "lowClosed" : true,
                     "highClosed" : true,
                     "type" : "Interval",
                     "low" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     },
                     "high" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }
                  } ]
               }
            }
         }, {
            "name" : "UnequalClosed",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "lowClosed" : true,
                     "highClosed" : true,
                     "type" : "Interval",
                     "low" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     },
                     "high" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }
                  }, {
                     "lowClosed" : true,
                     "highClosed" : true,
                     "type" : "Interval",
                     "low" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     },
                     "high" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }
                  } ]
               }
            }
         }, {
            "name" : "UnequalOpen",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "lowClosed" : false,
                     "highClosed" : false,
                     "type" : "Interval",
                     "low" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     },
                     "high" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }
                  }, {
                     "lowClosed" : false,
                     "highClosed" : false,
                     "type" : "Interval",
                     "low" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     },
                     "high" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }
                  } ]
               }
            }
         }, {
            "name" : "UnequalClosedOpen",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "lowClosed" : true,
                     "highClosed" : true,
                     "type" : "Interval",
                     "low" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     },
                     "high" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "5",
                        "type" : "Literal"
                     }
                  }, {
                     "lowClosed" : false,
                     "highClosed" : false,
                     "type" : "Interval",
                     "low" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2",
                        "type" : "Literal"
                     },
                     "high" : {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "4",
                        "type" : "Literal"
                     }
                  } ]
               }
            }
         }, {
            "name" : "EqualDates",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "lowClosed" : true,
                     "highClosed" : false,
                     "type" : "Interval",
                     "low" : {
                        "name" : "DateTime",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "2012",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        } ]
                     },
                     "high" : {
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
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        } ]
                     }
                  }, {
                     "lowClosed" : true,
                     "highClosed" : false,
                     "type" : "Interval",
                     "low" : {
                        "name" : "DateTime",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "2012",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        } ]
                     },
                     "high" : {
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
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        } ]
                     }
                  } ]
               }
            }
         }, {
            "name" : "EqualDatesOpenClosed",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "lowClosed" : true,
                     "highClosed" : false,
                     "type" : "Interval",
                     "low" : {
                        "name" : "DateTime",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "2012",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        } ]
                     },
                     "high" : {
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
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        } ]
                     }
                  }, {
                     "lowClosed" : true,
                     "highClosed" : true,
                     "type" : "Interval",
                     "low" : {
                        "name" : "DateTime",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "2012",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        } ]
                     },
                     "high" : {
                        "name" : "DateTime",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "2012",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "12",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "31",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "23",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "59",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "59",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "999",
                           "type" : "Literal"
                        } ]
                     }
                  } ]
               }
            }
         }, {
            "name" : "SameDays",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "lowClosed" : true,
                     "highClosed" : false,
                     "type" : "Interval",
                     "low" : {
                        "name" : "DateTime",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "2012",
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
                     }
                  }, {
                     "lowClosed" : true,
                     "highClosed" : false,
                     "type" : "Interval",
                     "low" : {
                        "name" : "DateTime",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "2012",
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
                     }
                  } ]
               }
            }
         }, {
            "name" : "DifferentDays",
            "context" : "Patient",
            "expression" : {
               "type" : "Not",
               "operand" : {
                  "type" : "Equal",
                  "operand" : [ {
                     "lowClosed" : true,
                     "highClosed" : false,
                     "type" : "Interval",
                     "low" : {
                        "name" : "DateTime",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "2012",
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
                     }
                  }, {
                     "lowClosed" : true,
                     "highClosed" : false,
                     "type" : "Interval",
                     "low" : {
                        "name" : "DateTime",
                        "type" : "FunctionRef",
                        "operand" : [ {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "2012",
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
                           "value" : "2012",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "7",
                           "type" : "Literal"
                        }, {
                           "valueType" : "{urn:hl7-org:elm:r1}Integer",
                           "value" : "1",
                           "type" : "Literal"
                        } ]
                     }
                  } ]
               }
            }
         } ]
      }
   }
}

### Overlaps
library TestSnippet version '1'
using QUICK
context Patient
define OverlapsBeforeIntIvl = interval[1, 5] overlaps interval[2, 7]
define OverlapsAfterIntIvl = interval[3, 8] overlaps interval[1, 6]
define OverlapsBoundaryIntIvl = interval[1, 5] overlaps interval[5, 10]
define NoOverlapsIntIvl = interval[1,5) overlaps interval[5, 10]
define StartOverlapsInt = interval[1, 5] overlaps 1
define EndOverlapsInt = interval[1, 5] overlaps 5
define NoOverlapsInt = interval[1, 5) overlaps 5
define OverlapsBeforeRealIvl = interval[1.234, 1.567] overlaps interval[1.345, 1.678]
define OverlapsAfterRealIvl = interval[1.345, 1.678] overlaps interval[1.234, 1.567]
define OverlapsBoundaryRealIvl = interval[1.0, 1.234] overlaps interval[1.234, 2.0]
define NoOverlapsRealIvl = interval[1.0, 1.23456789) overlaps interval[1.23456789, 2.0]
define StartOverlapsReal = interval[1.234, 5.678] overlaps 1.234
define EndOverlapsReal = interval[1.234, 5.678] overlaps 5.678
define NoOverlapsReal = interval[1.234, 5.678) overlaps 5.678
###

###
Translation Error(s):
[8:42, 8:49] Could not resolve call to operator Overlaps with signature (interval<System.Integer>,System.Integer).
[9:40, 9:47] Could not resolve call to operator Overlaps with signature (interval<System.Integer>,System.Integer).
[10:39, 10:46] Could not resolve call to operator Overlaps with signature (interval<System.Integer>,System.Integer).
[15:51, 15:58] Could not resolve call to operator Overlaps with signature (interval<System.Decimal>,System.Decimal).
[16:49, 16:56] Could not resolve call to operator Overlaps with signature (interval<System.Decimal>,System.Decimal).
[17:48, 17:55] Could not resolve call to operator Overlaps with signature (interval<System.Decimal>,System.Decimal).
###
module.exports['Overlaps'] = {
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
            "name" : "OverlapsBeforeIntIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "7",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "OverlapsAfterIntIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "OverlapsBoundaryIntIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "NoOverlapsIntIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "StartOverlapsInt",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "EndOverlapsInt",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NoOverlapsInt",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "OverlapsBeforeRealIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.234",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.567",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.345",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.678",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "OverlapsAfterRealIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.345",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.678",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.234",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.567",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "OverlapsBoundaryRealIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.234",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.234",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "2.0",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "NoOverlapsRealIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.23456789",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.23456789",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "2.0",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "StartOverlapsReal",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "EndOverlapsReal",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NoOverlapsReal",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         } ]
      }
   }
}

### OverlapsDateTime
library TestSnippet version '1'
using QUICK
context Patient
define ivlA = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define ivlB = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ivlC = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define ivlD = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))
define ivlE = interval[DateTime(2013), DateTime(2015)]
define ivlF = interval[DateTime(2014), DateTime(2016)]
define ivlG = interval[DateTime(2016), DateTime(2017)]
define OverlapsBefore = ivlA overlaps ivlB
define OverlapsAfter = ivlB overlaps ivlA
define OverlapsContained = ivlB overlaps ivlC
define OverlapsContains = ivlC overlaps ivlB
define ImpreciseOverlap = ivlD overlaps ivlE
define NoOverlap = ivlC overlaps ivlD
define NoImpreciseOverlap = ivlE overlaps ivlG
define UnknownOverlap = ivlF overlaps ivlG
define OverlapsDate = ivlC overlaps DateTime(2012, 4, 1, 0, 0, 0, 0)
define StartOverlapsDate = ivlC overlaps DateTime(2012, 1, 1, 0, 0, 0, 0)
define EndOverlapsDate = ivlC overlaps DateTime(2012, 12, 31, 23, 59, 59, 999)
define NoOverlapsDate = ivlC overlaps DateTime(2013, 4, 1, 0, 0, 0, 0)
define UnknownOverlapsDate = ivlE overlaps DateTime(2013, 4, 1, 0, 0, 0, 0)
define OverlapsUnknownDate = ivlB overlaps DateTime(2012)
###

###
Translation Error(s):
[19:28, 19:35] Could not resolve call to operator Overlaps with signature (interval<System.DateTime>,System.DateTime).
[20:33, 20:40] Could not resolve call to operator Overlaps with signature (interval<System.DateTime>,System.DateTime).
[21:31, 21:38] Could not resolve call to operator Overlaps with signature (interval<System.DateTime>,System.DateTime).
[22:30, 22:37] Could not resolve call to operator Overlaps with signature (interval<System.DateTime>,System.DateTime).
[23:35, 23:42] Could not resolve call to operator Overlaps with signature (interval<System.DateTime>,System.DateTime).
[24:35, 24:42] Could not resolve call to operator Overlaps with signature (interval<System.DateTime>,System.DateTime).
###
module.exports['OverlapsDateTime'] = {
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
            "name" : "ivlA",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlB",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "9",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlC",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
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
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlD",
            "context" : "Patient",
            "expression" : {
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
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
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
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlE",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : true,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2013",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlF",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : true,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2014",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2016",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlG",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : true,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2016",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2017",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "OverlapsBefore",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "name" : "ivlA",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlB",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "OverlapsAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "name" : "ivlB",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlA",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "OverlapsContained",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "name" : "ivlB",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "OverlapsContains",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlB",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "ImpreciseOverlap",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "name" : "ivlD",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlE",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "NoOverlap",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlD",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "NoImpreciseOverlap",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "name" : "ivlE",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlG",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "UnknownOverlap",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "name" : "ivlF",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlG",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "OverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "StartOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "EndOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NoOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "UnknownOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "OverlapsUnknownDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         } ]
      }
   }
}

### OverlapsAfter
library TestSnippet version '1'
using QUICK
context Patient
define OverlapsBeforeIntIvl = interval[1, 5] overlaps after interval[2, 7]
define OverlapsAfterIntIvl = interval[3, 8] overlaps after interval[1, 6]
define OverlapsBoundaryIntIvl = interval[5, 10] overlaps after interval[1, 5]
define NoOverlapsIntIvl = interval[1,5) overlaps after interval[5, 10]
define StartOverlapsInt = interval[1, 5] overlaps after 1
define EndOverlapsInt = interval[1, 5] overlaps after 5
define NoOverlapsInt = interval[1, 5) overlaps after 5
define OverlapsBeforeRealIvl = interval[1.234, 1.567] overlaps after interval[1.345, 1.678]
define OverlapsAfterRealIvl = interval[1.345, 1.678] overlaps after interval[1.234, 1.567]
define OverlapsBoundaryRealIvl = interval[1.234, 2.0] overlaps after interval[1.0, 1.234]
define NoOverlapsRealIvl = interval[1.0, 1.23456789) overlaps after interval[1.23456789, 2.0]
define StartOverlapsReal = interval[1.234, 5.678] overlaps after 1.234
define EndOverlapsReal = interval[1.234, 5.678] overlaps after 5.678
define NoOverlapsReal = interval[1.234, 5.678) overlaps after 5.678
###

###
Translation Error(s):
[8:42, 8:55] Could not resolve call to operator OverlapsAfter with signature (interval<System.Integer>,System.Integer).
[9:40, 9:53] Could not resolve call to operator OverlapsAfter with signature (interval<System.Integer>,System.Integer).
[10:39, 10:52] Could not resolve call to operator OverlapsAfter with signature (interval<System.Integer>,System.Integer).
[15:51, 15:64] Could not resolve call to operator OverlapsAfter with signature (interval<System.Decimal>,System.Decimal).
[16:49, 16:62] Could not resolve call to operator OverlapsAfter with signature (interval<System.Decimal>,System.Decimal).
[17:48, 17:61] Could not resolve call to operator OverlapsAfter with signature (interval<System.Decimal>,System.Decimal).
###
module.exports['OverlapsAfter'] = {
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
            "name" : "OverlapsBeforeIntIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "7",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "OverlapsAfterIntIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "OverlapsBoundaryIntIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "NoOverlapsIntIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "StartOverlapsInt",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "EndOverlapsInt",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NoOverlapsInt",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "OverlapsBeforeRealIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.234",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.567",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.345",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.678",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "OverlapsAfterRealIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.345",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.678",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.234",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.567",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "OverlapsBoundaryRealIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.234",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "2.0",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.234",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "NoOverlapsRealIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.23456789",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.23456789",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "2.0",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "StartOverlapsReal",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "EndOverlapsReal",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NoOverlapsReal",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         } ]
      }
   }
}

### OverlapsAfterDateTime
library TestSnippet version '1'
using QUICK
context Patient
define ivlA = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define ivlB = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ivlC = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define ivlD = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))
define ivlE = interval[DateTime(2013), DateTime(2015)]
define ivlF = interval[DateTime(2014), DateTime(2016)]
define ivlG = interval[DateTime(2016), DateTime(2017)]
define OverlapsBefore = ivlA overlaps after ivlB
define OverlapsAfter = ivlB overlaps after ivlA
define OverlapsContained = ivlB overlaps after ivlC
define OverlapsContains = ivlC overlaps after ivlB
define ImpreciseOverlapBefore = ivlE overlaps after ivlF
define ImpreciseOverlapAfter = ivlF overlaps after ivlE
define NoOverlap = ivlC overlaps after ivlD
define NoImpreciseOverlap = ivlE overlaps after ivlG
define UnknownOverlap = ivlG overlaps after ivlF
define OverlapsDate = ivlC overlaps after DateTime(2012, 4, 1, 0, 0, 0, 0)
define StartOverlapsDate = ivlC overlaps after DateTime(2012, 1, 1, 0, 0, 0, 0)
define EndOverlapsDate = ivlC overlaps after DateTime(2012, 12, 31, 23, 59, 59, 999)
define NoOverlapsDate = ivlC overlaps after DateTime(2013, 4, 1, 0, 0, 0, 0)
define UnknownOverlapsDate = ivlE overlaps after DateTime(2013, 4, 1, 0, 0, 0, 0)
define OverlapsUnknownDate = ivlB overlaps after DateTime(2012)
###

###
Translation Error(s):
[20:28, 20:41] Could not resolve call to operator OverlapsAfter with signature (interval<System.DateTime>,System.DateTime).
[21:33, 21:46] Could not resolve call to operator OverlapsAfter with signature (interval<System.DateTime>,System.DateTime).
[22:31, 22:44] Could not resolve call to operator OverlapsAfter with signature (interval<System.DateTime>,System.DateTime).
[23:30, 23:43] Could not resolve call to operator OverlapsAfter with signature (interval<System.DateTime>,System.DateTime).
[24:35, 24:48] Could not resolve call to operator OverlapsAfter with signature (interval<System.DateTime>,System.DateTime).
[25:35, 25:48] Could not resolve call to operator OverlapsAfter with signature (interval<System.DateTime>,System.DateTime).
###
module.exports['OverlapsAfterDateTime'] = {
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
            "name" : "ivlA",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlB",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "9",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlC",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
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
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlD",
            "context" : "Patient",
            "expression" : {
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
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
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
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlE",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : true,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2013",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlF",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : true,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2014",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2016",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlG",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : true,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2016",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2017",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "OverlapsBefore",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "name" : "ivlA",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlB",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "OverlapsAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "name" : "ivlB",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlA",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "OverlapsContained",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "name" : "ivlB",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "OverlapsContains",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlB",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "ImpreciseOverlapBefore",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "name" : "ivlE",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlF",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "ImpreciseOverlapAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "name" : "ivlF",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlE",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "NoOverlap",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlD",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "NoImpreciseOverlap",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "name" : "ivlE",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlG",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "UnknownOverlap",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "name" : "ivlG",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlF",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "OverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "StartOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "EndOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NoOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "UnknownOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "OverlapsUnknownDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         } ]
      }
   }
}

### OverlapsBefore
library TestSnippet version '1'
using QUICK
context Patient
define OverlapsBeforeIntIvl = interval[1, 5] overlaps before interval[2, 7]
define OverlapsAfterIntIvl = interval[3, 8] overlaps before interval[1, 6]
define OverlapsBoundaryIntIvl = interval[1, 5] overlaps before interval[5, 10]
define NoOverlapsIntIvl = interval[1,5) overlaps before interval[5, 10]
define StartOverlapsInt = interval[1, 5] overlaps before 1
define EndOverlapsInt = interval[1, 5] overlaps before 5
define NoOverlapsInt = interval[1, 5) overlaps before 5
define OverlapsBeforeRealIvl = interval[1.234, 1.567] overlaps before interval[1.345, 1.678]
define OverlapsAfterRealIvl = interval[1.345, 1.678] overlaps before interval[1.234, 1.567]
define OverlapsBoundaryRealIvl = interval[1.0, 1.234] overlaps before interval[1.234, 2.0]
define NoOverlapsRealIvl = interval[1.0, 1.23456789) overlaps before interval[1.23456789, 2.0]
define StartOverlapsReal = interval[1.234, 5.678] overlaps before 1.234
define EndOverlapsReal = interval[1.234, 5.678] overlaps before 5.678
define NoOverlapsReal = interval[1.234, 5.678) overlaps before 5.678
###

###
Translation Error(s):
[8:42, 8:56] Could not resolve call to operator OverlapsBefore with signature (interval<System.Integer>,System.Integer).
[9:40, 9:54] Could not resolve call to operator OverlapsBefore with signature (interval<System.Integer>,System.Integer).
[10:39, 10:53] Could not resolve call to operator OverlapsBefore with signature (interval<System.Integer>,System.Integer).
[15:51, 15:65] Could not resolve call to operator OverlapsBefore with signature (interval<System.Decimal>,System.Decimal).
[16:49, 16:63] Could not resolve call to operator OverlapsBefore with signature (interval<System.Decimal>,System.Decimal).
[17:48, 17:62] Could not resolve call to operator OverlapsBefore with signature (interval<System.Decimal>,System.Decimal).
###
module.exports['OverlapsBefore'] = {
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
            "name" : "OverlapsBeforeIntIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "7",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "OverlapsAfterIntIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "8",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "OverlapsBoundaryIntIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "NoOverlapsIntIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "5",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "10",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "StartOverlapsInt",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "EndOverlapsInt",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NoOverlapsInt",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "OverlapsBeforeRealIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.234",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.567",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.345",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.678",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "OverlapsAfterRealIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.345",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.678",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.234",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.567",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "OverlapsBoundaryRealIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.234",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.234",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "2.0",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "NoOverlapsRealIvl",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.0",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.23456789",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "1.23456789",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Decimal",
                     "value" : "2.0",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "StartOverlapsReal",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "EndOverlapsReal",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NoOverlapsReal",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         } ]
      }
   }
}

### OverlapsBeforeDateTime
library TestSnippet version '1'
using QUICK
context Patient
define ivlA = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2012, 6, 1, 0, 0, 0, 0))
define ivlB = interval[DateTime(2012, 3, 1, 0, 0, 0, 0), DateTime(2012, 9, 1, 0, 0, 0, 0))
define ivlC = interval[DateTime(2012, 1, 1, 0, 0, 0, 0), DateTime(2013, 1, 1, 0, 0, 0, 0))
define ivlD = interval[DateTime(2013, 1, 1, 0, 0, 0, 0), DateTime(2014, 1, 1, 0, 0, 0, 0))
define ivlE = interval[DateTime(2013), DateTime(2015)]
define ivlF = interval[DateTime(2014), DateTime(2016)]
define ivlG = interval[DateTime(2016), DateTime(2017)]
define OverlapsBefore = ivlA overlaps before ivlB
define OverlapsAfter = ivlB overlaps before ivlA
define OverlapsContained = ivlB overlaps before ivlC
define OverlapsContains = ivlC overlaps before ivlB
define ImpreciseOverlapBefore = ivlE overlaps before ivlF
define ImpreciseOverlapAfter = ivlF overlaps before ivlE
define NoOverlap = ivlC overlaps before ivlD
define NoImpreciseOverlap = ivlE overlaps before ivlG
define UnknownOverlap = ivlF overlaps before ivlG
define OverlapsDate = ivlC overlaps before DateTime(2012, 4, 1, 0, 0, 0, 0)
define StartOverlapsDate = ivlC overlaps before DateTime(2012, 1, 1, 0, 0, 0, 0)
define EndOverlapsDate = ivlC overlaps before DateTime(2012, 12, 31, 23, 59, 59, 999)
define NoOverlapsDate = ivlC overlaps before DateTime(2013, 4, 1, 0, 0, 0, 0)
define UnknownOverlapsDate = ivlE overlaps before DateTime(2013, 4, 1, 0, 0, 0, 0)
define OverlapsUnknownDate = ivlB overlaps before DateTime(2012)
###

###
Translation Error(s):
[20:28, 20:42] Could not resolve call to operator OverlapsBefore with signature (interval<System.DateTime>,System.DateTime).
[21:33, 21:47] Could not resolve call to operator OverlapsBefore with signature (interval<System.DateTime>,System.DateTime).
[22:31, 22:45] Could not resolve call to operator OverlapsBefore with signature (interval<System.DateTime>,System.DateTime).
[23:30, 23:44] Could not resolve call to operator OverlapsBefore with signature (interval<System.DateTime>,System.DateTime).
[24:35, 24:49] Could not resolve call to operator OverlapsBefore with signature (interval<System.DateTime>,System.DateTime).
[25:35, 25:49] Could not resolve call to operator OverlapsBefore with signature (interval<System.DateTime>,System.DateTime).
###
module.exports['OverlapsBeforeDateTime'] = {
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
            "name" : "ivlA",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlB",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "9",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlC",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
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
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlD",
            "context" : "Patient",
            "expression" : {
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
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
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
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlE",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : true,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2013",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlF",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : true,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2014",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2016",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "ivlG",
            "context" : "Patient",
            "expression" : {
               "lowClosed" : true,
               "highClosed" : true,
               "type" : "Interval",
               "low" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2016",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2017",
                     "type" : "Literal"
                  } ]
               }
            }
         }, {
            "name" : "OverlapsBefore",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "name" : "ivlA",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlB",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "OverlapsAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "name" : "ivlB",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlA",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "OverlapsContained",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "name" : "ivlB",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "OverlapsContains",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlB",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "ImpreciseOverlapBefore",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "name" : "ivlE",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlF",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "ImpreciseOverlapAfter",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "name" : "ivlF",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlE",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "NoOverlap",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlD",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "NoImpreciseOverlap",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "name" : "ivlE",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlG",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "UnknownOverlap",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "name" : "ivlF",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "ivlG",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "OverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "StartOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "EndOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "NoOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "UnknownOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "OverlapsUnknownDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         } ]
      }
   }
}

### Start
library TestSnippet version '1'
using QUICK
context Patient
define Foo = start of interval[DateTime(2012, 1, 1), DateTime(2013, 1, 1)]
###

module.exports['Start'] = {
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
            "name" : "Foo",
            "context" : "Patient",
            "expression" : {
               "type" : "Start",
               "operand" : {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{urn:hl7-org:elm:r1}Integer",
                        "value" : "2012",
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
                  }
               }
            }
         } ]
      }
   }
}

