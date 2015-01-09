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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
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
            "name" : "EqualClosed",
            "context" : "Patient",
            "expression" : {
               "type" : "Equal",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : false,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : false,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : false,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     } ]
                  },
                  "high" : {
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
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     } ]
                  },
                  "high" : {
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
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     } ]
                  },
                  "high" : {
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
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     } ]
                  },
                  "high" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "12",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "31",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "23",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "59",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "59",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
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
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
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
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
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
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
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
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
            "name" : "EqualClosed",
            "context" : "Patient",
            "expression" : {
               "type" : "NotEqual",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "EqualOpen",
            "context" : "Patient",
            "expression" : {
               "type" : "NotEqual",
               "operand" : [ {
                  "lowClosed" : false,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : false,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "EqualOpenClosed",
            "context" : "Patient",
            "expression" : {
               "type" : "NotEqual",
               "operand" : [ {
                  "lowClosed" : false,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "UnequalClosed",
            "context" : "Patient",
            "expression" : {
               "type" : "NotEqual",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "UnequalOpen",
            "context" : "Patient",
            "expression" : {
               "type" : "NotEqual",
               "operand" : [ {
                  "lowClosed" : false,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : false,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "UnequalClosedOpen",
            "context" : "Patient",
            "expression" : {
               "type" : "NotEqual",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "5",
                     "type" : "Literal"
                  }
               }, {
                  "lowClosed" : false,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "high" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }
               } ]
            }
         }, {
            "name" : "EqualDates",
            "context" : "Patient",
            "expression" : {
               "type" : "NotEqual",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     } ]
                  },
                  "high" : {
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
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     } ]
                  },
                  "high" : {
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
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
               "type" : "NotEqual",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     } ]
                  },
                  "high" : {
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
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "1",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "0",
                        "type" : "Literal"
                     } ]
                  },
                  "high" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "12",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "31",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "23",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "59",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "59",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
               "type" : "NotEqual",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
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
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
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
                  }
               } ]
            }
         }, {
            "name" : "DifferentDays",
            "context" : "Patient",
            "expression" : {
               "type" : "NotEqual",
               "operand" : [ {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
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
                  }
               }, {
                  "lowClosed" : true,
                  "highClosed" : false,
                  "type" : "Interval",
                  "low" : {
                     "name" : "DateTime",
                     "type" : "FunctionRef",
                     "operand" : [ {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
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
                        "value" : "2012",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "7",
                        "type" : "Literal"
                     }, {
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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

### Overlaps
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "9",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
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
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
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
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2014",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2016",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
               "type" : "Overlaps",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "StartOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "EndOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "12",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "31",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "23",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "59",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "59",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "999",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "NoOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "UnknownOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "name" : "ivlE",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "OverlapsUnknownDate",
            "context" : "Patient",
            "expression" : {
               "type" : "Overlaps",
               "operand" : [ {
                  "name" : "ivlB",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

### OverlapsAfter
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "9",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
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
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
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
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2014",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2016",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "StartOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "EndOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "12",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "31",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "23",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "59",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "59",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "999",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "NoOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "UnknownOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "name" : "ivlE",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "OverlapsUnknownDate",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsAfter",
               "operand" : [ {
                  "name" : "ivlB",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

### OverlapsBefore
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "6",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "3",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "9",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
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
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
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
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2014",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2016",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
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
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "StartOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "EndOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "12",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "31",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "23",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "59",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "59",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "999",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "NoOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "name" : "ivlC",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "UnknownOverlapsDate",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "name" : "ivlE",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "4",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "0",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "OverlapsUnknownDate",
            "context" : "Patient",
            "expression" : {
               "type" : "OverlapsBefore",
               "operand" : [ {
                  "name" : "ivlB",
                  "type" : "ExpressionRef"
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2012",
                     "type" : "Literal"
                  } ]
               } ]
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
                        "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                        "value" : "2012",
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
                  }
               }
            }
         } ]
      }
   }
}

