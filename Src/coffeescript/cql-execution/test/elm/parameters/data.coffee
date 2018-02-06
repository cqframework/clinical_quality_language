###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### ParameterDef
library TestSnippet version '1'
using QUICK
parameter MeasureYear default 2012
parameter IntParameter Integer
parameter ListParameter List<String>
parameter TupleParameter Tuple{a Integer, b String, c Boolean, d List<Integer>, e Tuple{ f String, g Boolean}}
context Patient
define foo: 'bar'
###

module.exports['ParameterDef'] = {
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
            "name" : "MeasureYear",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "2",
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "2012",
               "type" : "Literal"
            }
         }, {
            "localId" : "5",
            "name" : "IntParameter",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "4",
               "name" : "{urn:hl7-org:elm-types:r1}Integer",
               "type" : "NamedTypeSpecifier"
            }
         }, {
            "localId" : "8",
            "name" : "ListParameter",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "7",
               "type" : "ListTypeSpecifier",
               "elementType" : {
                  "localId" : "6",
                  "name" : "{urn:hl7-org:elm-types:r1}String",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "localId" : "25",
            "name" : "TupleParameter",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "24",
               "type" : "TupleTypeSpecifier",
               "element" : [ {
                  "localId" : "10",
                  "name" : "a",
                  "type" : {
                     "localId" : "9",
                     "name" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "localId" : "12",
                  "name" : "b",
                  "type" : {
                     "localId" : "11",
                     "name" : "{urn:hl7-org:elm-types:r1}String",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "localId" : "14",
                  "name" : "c",
                  "type" : {
                     "localId" : "13",
                     "name" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "localId" : "17",
                  "name" : "d",
                  "type" : {
                     "localId" : "16",
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "localId" : "15",
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               }, {
                  "localId" : "23",
                  "name" : "e",
                  "type" : {
                     "localId" : "22",
                     "type" : "TupleTypeSpecifier",
                     "element" : [ {
                        "localId" : "19",
                        "name" : "f",
                        "type" : {
                           "localId" : "18",
                           "name" : "{urn:hl7-org:elm-types:r1}String",
                           "type" : "NamedTypeSpecifier"
                        }
                     }, {
                        "localId" : "21",
                        "name" : "g",
                        "type" : {
                           "localId" : "20",
                           "name" : "{urn:hl7-org:elm-types:r1}Boolean",
                           "type" : "NamedTypeSpecifier"
                        }
                     } ]
                  }
               } ]
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
            "localId" : "27",
            "name" : "foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "27",
                  "s" : [ {
                     "value" : [ "define ","foo",": " ]
                  }, {
                     "r" : "26",
                     "s" : [ {
                        "value" : [ "'bar'" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "26",
               "valueType" : "{urn:hl7-org:elm-types:r1}String",
               "value" : "bar",
               "type" : "Literal"
            }
         } ]
      }
   }
}

### ParameterRef
library TestSnippet version '1'
using QUICK
parameter FooP default 'Bar'
context Patient
define Foo: FooP
###

module.exports['ParameterRef'] = {
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
            "name" : "FooP",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "2",
               "valueType" : "{urn:hl7-org:elm-types:r1}String",
               "value" : "Bar",
               "type" : "Literal"
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
                        "value" : [ "FooP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "name" : "FooP",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

### BooleanParameterTypes
library TestSnippet version '1'
using QUICK
parameter FooP Boolean
parameter FooDP default true
context Patient
define Foo: FooP
define Foo2: FooDP
###

module.exports['BooleanParameterTypes'] = {
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
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "2",
               "name" : "{urn:hl7-org:elm-types:r1}Boolean",
               "type" : "NamedTypeSpecifier"
            }
         }, {
            "localId" : "5",
            "name" : "FooDP",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "4",
               "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
               "value" : "true",
               "type" : "Literal"
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
            "localId" : "7",
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "7",
                  "s" : [ {
                     "value" : [ "define ","Foo",": " ]
                  }, {
                     "r" : "6",
                     "s" : [ {
                        "value" : [ "FooP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "6",
               "name" : "FooP",
               "type" : "ParameterRef"
            }
         }, {
            "localId" : "9",
            "name" : "Foo2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","Foo2",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "FooDP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "name" : "FooDP",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

### DecimalParameterTypes
library TestSnippet version '1'
using QUICK
parameter FooP Decimal
parameter FooDP default 1.5
context Patient
define Foo: FooP
define Foo2: FooDP
###

module.exports['DecimalParameterTypes'] = {
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
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "2",
               "name" : "{urn:hl7-org:elm-types:r1}Decimal",
               "type" : "NamedTypeSpecifier"
            }
         }, {
            "localId" : "5",
            "name" : "FooDP",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "4",
               "valueType" : "{urn:hl7-org:elm-types:r1}Decimal",
               "value" : "1.5",
               "type" : "Literal"
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
            "localId" : "7",
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "7",
                  "s" : [ {
                     "value" : [ "define ","Foo",": " ]
                  }, {
                     "r" : "6",
                     "s" : [ {
                        "value" : [ "FooP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "6",
               "name" : "FooP",
               "type" : "ParameterRef"
            }
         }, {
            "localId" : "9",
            "name" : "Foo2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","Foo2",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "FooDP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "name" : "FooDP",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

### IntegerParameterTypes
library TestSnippet version '1'
using QUICK
parameter FooP Integer
parameter FooDP default 2
context Patient
define Foo: FooP
define Foo2: FooDP
###

module.exports['IntegerParameterTypes'] = {
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
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "2",
               "name" : "{urn:hl7-org:elm-types:r1}Integer",
               "type" : "NamedTypeSpecifier"
            }
         }, {
            "localId" : "5",
            "name" : "FooDP",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "4",
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "2",
               "type" : "Literal"
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
            "localId" : "7",
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "7",
                  "s" : [ {
                     "value" : [ "define ","Foo",": " ]
                  }, {
                     "r" : "6",
                     "s" : [ {
                        "value" : [ "FooP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "6",
               "name" : "FooP",
               "type" : "ParameterRef"
            }
         }, {
            "localId" : "9",
            "name" : "Foo2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","Foo2",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "FooDP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "name" : "FooDP",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

### StringParameterTypes
library TestSnippet version '1'
using QUICK
parameter FooP String
parameter FooDP default 'Hello'
context Patient
define Foo: FooP
define Foo2: FooDP
###

module.exports['StringParameterTypes'] = {
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
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "2",
               "name" : "{urn:hl7-org:elm-types:r1}String",
               "type" : "NamedTypeSpecifier"
            }
         }, {
            "localId" : "5",
            "name" : "FooDP",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "4",
               "valueType" : "{urn:hl7-org:elm-types:r1}String",
               "value" : "Hello",
               "type" : "Literal"
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
            "localId" : "7",
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "7",
                  "s" : [ {
                     "value" : [ "define ","Foo",": " ]
                  }, {
                     "r" : "6",
                     "s" : [ {
                        "value" : [ "FooP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "6",
               "name" : "FooP",
               "type" : "ParameterRef"
            }
         }, {
            "localId" : "9",
            "name" : "Foo2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","Foo2",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "FooDP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "name" : "FooDP",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

### ConceptParameterTypes
library TestSnippet version '1'
using QUICK
codesystem "FOOTESTCS": 'http://footest.org'
parameter FooP Concept
parameter FooDP default Concept { Code 'FooTest' from "FOOTESTCS" } display 'Foo Test'
context Patient
define Foo: FooP
define Foo2: FooDP
###

module.exports['ConceptParameterTypes'] = {
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
            "localId" : "4",
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "3",
               "name" : "{urn:hl7-org:elm-types:r1}Concept",
               "type" : "NamedTypeSpecifier"
            }
         }, {
            "localId" : "8",
            "name" : "FooDP",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "7",
               "display" : "Foo Test",
               "type" : "Concept",
               "code" : [ {
                  "localId" : "6",
                  "code" : "FooTest",
                  "system" : {
                     "localId" : "5",
                     "name" : "FOOTESTCS"
                  }
               } ]
            }
         } ]
      },
      "codeSystems" : {
         "def" : [ {
            "localId" : "2",
            "name" : "FOOTESTCS",
            "id" : "http://footest.org",
            "accessLevel" : "Public"
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
            "localId" : "10",
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "10",
                  "s" : [ {
                     "value" : [ "define ","Foo",": " ]
                  }, {
                     "r" : "9",
                     "s" : [ {
                        "value" : [ "FooP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "9",
               "name" : "FooP",
               "type" : "ParameterRef"
            }
         }, {
            "localId" : "12",
            "name" : "Foo2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "12",
                  "s" : [ {
                     "value" : [ "define ","Foo2",": " ]
                  }, {
                     "r" : "11",
                     "s" : [ {
                        "value" : [ "FooDP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "11",
               "name" : "FooDP",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

### DateTimeParameterTypes
library TestSnippet version '1'
using QUICK
parameter FooP DateTime
parameter FooDP default @2012-04-01
context Patient
define Foo: FooP
define Foo2: FooDP
###

module.exports['DateTimeParameterTypes'] = {
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
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "2",
               "name" : "{urn:hl7-org:elm-types:r1}DateTime",
               "type" : "NamedTypeSpecifier"
            }
         }, {
            "localId" : "5",
            "name" : "FooDP",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "4",
               "type" : "DateTime",
               "year" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2012",
                  "type" : "Literal"
               },
               "month" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "4",
                  "type" : "Literal"
               },
               "day" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "1",
                  "type" : "Literal"
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
                  "templateId" : "patient-qicore-qicore-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "localId" : "7",
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "7",
                  "s" : [ {
                     "value" : [ "define ","Foo",": " ]
                  }, {
                     "r" : "6",
                     "s" : [ {
                        "value" : [ "FooP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "6",
               "name" : "FooP",
               "type" : "ParameterRef"
            }
         }, {
            "localId" : "9",
            "name" : "Foo2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","Foo2",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "FooDP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "name" : "FooDP",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

### QuantityParameterTypes
library TestSnippet version '1'
using QUICK
parameter FooP Quantity
parameter FooDP default 10 'dL'
context Patient
define Foo: FooP
define Foo2: FooDP
###

module.exports['QuantityParameterTypes'] = {
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
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "2",
               "name" : "{urn:hl7-org:elm-types:r1}Quantity",
               "type" : "NamedTypeSpecifier"
            }
         }, {
            "localId" : "5",
            "name" : "FooDP",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "4",
               "value" : 10,
               "unit" : "dL",
               "type" : "Quantity"
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
            "localId" : "7",
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "7",
                  "s" : [ {
                     "value" : [ "define ","Foo",": " ]
                  }, {
                     "r" : "6",
                     "s" : [ {
                        "value" : [ "FooP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "6",
               "name" : "FooP",
               "type" : "ParameterRef"
            }
         }, {
            "localId" : "9",
            "name" : "Foo2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","Foo2",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "FooDP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "name" : "FooDP",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

### TimeParameterTypes
library TestSnippet version '1'
using QUICK
parameter FooP Time
parameter FooDP default @T12:00:00
context Patient
define Foo: FooP
define Foo2: FooDP
###

module.exports['TimeParameterTypes'] = {
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
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "2",
               "name" : "{urn:hl7-org:elm-types:r1}Time",
               "type" : "NamedTypeSpecifier"
            }
         }, {
            "localId" : "5",
            "name" : "FooDP",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "4",
               "type" : "Time",
               "hour" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "12",
                  "type" : "Literal"
               },
               "minute" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "0",
                  "type" : "Literal"
               },
               "second" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "0",
                  "type" : "Literal"
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
                  "templateId" : "patient-qicore-qicore-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "localId" : "7",
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "7",
                  "s" : [ {
                     "value" : [ "define ","Foo",": " ]
                  }, {
                     "r" : "6",
                     "s" : [ {
                        "value" : [ "FooP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "6",
               "name" : "FooP",
               "type" : "ParameterRef"
            }
         }, {
            "localId" : "9",
            "name" : "Foo2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","Foo2",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "FooDP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "name" : "FooDP",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

### ListParameterTypes
library TestSnippet version '1'
using QUICK
parameter FooP List<String>
parameter FooDP default { 'a', 'b', 'c' }
context Patient
define Foo: FooP
define Foo2: FooDP
###

module.exports['ListParameterTypes'] = {
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
            "localId" : "4",
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "3",
               "type" : "ListTypeSpecifier",
               "elementType" : {
                  "localId" : "2",
                  "name" : "{urn:hl7-org:elm-types:r1}String",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "localId" : "9",
            "name" : "FooDP",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "8",
               "type" : "List",
               "element" : [ {
                  "localId" : "5",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "a",
                  "type" : "Literal"
               }, {
                  "localId" : "6",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "b",
                  "type" : "Literal"
               }, {
                  "localId" : "7",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "c",
                  "type" : "Literal"
               } ]
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
            "localId" : "11",
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","Foo",": " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "value" : [ "FooP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "name" : "FooP",
               "type" : "ParameterRef"
            }
         }, {
            "localId" : "13",
            "name" : "Foo2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "13",
                  "s" : [ {
                     "value" : [ "define ","Foo2",": " ]
                  }, {
                     "r" : "12",
                     "s" : [ {
                        "value" : [ "FooDP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "12",
               "name" : "FooDP",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

### IntervalParameterTypes
library TestSnippet version '1'
using QUICK
parameter FooP Interval<Integer>
parameter FooDP default Interval[2,6]
context Patient
define Foo: FooP
define Foo2: FooDP
###

module.exports['IntervalParameterTypes'] = {
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
            "localId" : "4",
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "3",
               "type" : "IntervalTypeSpecifier",
               "pointType" : {
                  "localId" : "2",
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "localId" : "8",
            "name" : "FooDP",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "7",
               "lowClosed" : true,
               "highClosed" : true,
               "type" : "Interval",
               "low" : {
                  "localId" : "5",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "2",
                  "type" : "Literal"
               },
               "high" : {
                  "localId" : "6",
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "6",
                  "type" : "Literal"
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
                  "templateId" : "patient-qicore-qicore-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "localId" : "10",
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "10",
                  "s" : [ {
                     "value" : [ "define ","Foo",": " ]
                  }, {
                     "r" : "9",
                     "s" : [ {
                        "value" : [ "FooP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "9",
               "name" : "FooP",
               "type" : "ParameterRef"
            }
         }, {
            "localId" : "12",
            "name" : "Foo2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "12",
                  "s" : [ {
                     "value" : [ "define ","Foo2",": " ]
                  }, {
                     "r" : "11",
                     "s" : [ {
                        "value" : [ "FooDP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "11",
               "name" : "FooDP",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

### TupleParameterTypes
library TestSnippet version '1'
using QUICK
parameter FooP Tuple { Hello String, MeaningOfLife Integer }
parameter FooDP default Tuple { Hello: 'Universe', MeaningOfLife: 24 }
context Patient
define Foo: FooP
define Foo2: FooDP
###

module.exports['TupleParameterTypes'] = {
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
            "localId" : "7",
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "6",
               "type" : "TupleTypeSpecifier",
               "element" : [ {
                  "localId" : "3",
                  "name" : "Hello",
                  "type" : {
                     "localId" : "2",
                     "name" : "{urn:hl7-org:elm-types:r1}String",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "localId" : "5",
                  "name" : "MeaningOfLife",
                  "type" : {
                     "localId" : "4",
                     "name" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "NamedTypeSpecifier"
                  }
               } ]
            }
         }, {
            "localId" : "11",
            "name" : "FooDP",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "10",
               "type" : "Tuple",
               "element" : [ {
                  "name" : "Hello",
                  "value" : {
                     "localId" : "8",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "Universe",
                     "type" : "Literal"
                  }
               }, {
                  "name" : "MeaningOfLife",
                  "value" : {
                     "localId" : "9",
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "24",
                     "type" : "Literal"
                  }
               } ]
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
            "localId" : "13",
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "13",
                  "s" : [ {
                     "value" : [ "define ","Foo",": " ]
                  }, {
                     "r" : "12",
                     "s" : [ {
                        "value" : [ "FooP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "12",
               "name" : "FooP",
               "type" : "ParameterRef"
            }
         }, {
            "localId" : "15",
            "name" : "Foo2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "15",
                  "s" : [ {
                     "value" : [ "define ","Foo2",": " ]
                  }, {
                     "r" : "14",
                     "s" : [ {
                        "value" : [ "FooDP" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "14",
               "name" : "FooDP",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

### DefaultAndNoDefault
library TestSnippet version '1'
using QUICK
parameter FooWithNoDefault Integer
parameter FooWithDefault default 5
context Patient
define Foo: FooWithNoDefault
define Foo2: FooWithDefault
###

module.exports['DefaultAndNoDefault'] = {
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
            "name" : "FooWithNoDefault",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "2",
               "name" : "{urn:hl7-org:elm-types:r1}Integer",
               "type" : "NamedTypeSpecifier"
            }
         }, {
            "localId" : "5",
            "name" : "FooWithDefault",
            "accessLevel" : "Public",
            "default" : {
               "localId" : "4",
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "5",
               "type" : "Literal"
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
            "localId" : "7",
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "7",
                  "s" : [ {
                     "value" : [ "define ","Foo",": " ]
                  }, {
                     "r" : "6",
                     "s" : [ {
                        "value" : [ "FooWithNoDefault" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "6",
               "name" : "FooWithNoDefault",
               "type" : "ParameterRef"
            }
         }, {
            "localId" : "9",
            "name" : "Foo2",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "9",
                  "s" : [ {
                     "value" : [ "define ","Foo2",": " ]
                  }, {
                     "r" : "8",
                     "s" : [ {
                        "value" : [ "FooWithDefault" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "8",
               "name" : "FooWithDefault",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

### MeasurementPeriodParameter
library TestSnippet version '1'
using QUICK
parameter "Measurement Period" Interval<DateTime>
context Patient
define MeasurementPeriod: Interval[DateTime(2011, 1, 1), DateTime(2013, 1, 1)] overlaps "Measurement Period"
###

module.exports['MeasurementPeriodParameter'] = {
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
            "localId" : "4",
            "name" : "Measurement Period",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "localId" : "3",
               "type" : "IntervalTypeSpecifier",
               "pointType" : {
                  "localId" : "2",
                  "name" : "{urn:hl7-org:elm-types:r1}DateTime",
                  "type" : "NamedTypeSpecifier"
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
                  "templateId" : "patient-qicore-qicore-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "localId" : "16",
            "name" : "MeasurementPeriod",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "16",
                  "s" : [ {
                     "value" : [ "define ","MeasurementPeriod",": " ]
                  }, {
                     "r" : "15",
                     "s" : [ {
                        "r" : "13",
                        "s" : [ {
                           "value" : [ "Interval[" ]
                        }, {
                           "r" : "8",
                           "s" : [ {
                              "value" : [ "DateTime","(","2011",", ","1",", ","1",")" ]
                           } ]
                        }, {
                           "value" : [ ", " ]
                        }, {
                           "r" : "12",
                           "s" : [ {
                              "value" : [ "DateTime","(","2013",", ","1",", ","1",")" ]
                           } ]
                        }, {
                           "value" : [ "]" ]
                        } ]
                     }, {
                        "value" : [ " ","overlaps"," " ]
                     }, {
                        "r" : "14",
                        "s" : [ {
                           "value" : [ "\"Measurement Period\"" ]
                        } ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "15",
               "type" : "Overlaps",
               "operand" : [ {
                  "localId" : "13",
                  "lowClosed" : true,
                  "highClosed" : true,
                  "type" : "Interval",
                  "low" : {
                     "localId" : "8",
                     "type" : "DateTime",
                     "year" : {
                        "localId" : "5",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2011",
                        "type" : "Literal"
                     },
                     "month" : {
                        "localId" : "6",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     },
                     "day" : {
                        "localId" : "7",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }
                  },
                  "high" : {
                     "localId" : "12",
                     "type" : "DateTime",
                     "year" : {
                        "localId" : "9",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "2013",
                        "type" : "Literal"
                     },
                     "month" : {
                        "localId" : "10",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     },
                     "day" : {
                        "localId" : "11",
                        "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                        "value" : "1",
                        "type" : "Literal"
                     }
                  }
               }, {
                  "localId" : "14",
                  "name" : "Measurement Period",
                  "type" : "ParameterRef"
               } ]
            }
         } ]
      }
   }
}

