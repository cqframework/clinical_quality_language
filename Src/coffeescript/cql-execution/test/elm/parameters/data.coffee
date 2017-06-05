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
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "MeasureYear",
            "accessLevel" : "Public",
            "default" : {
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "2012",
               "type" : "Literal"
            }
         }, {
            "name" : "IntParameter",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "name" : "{urn:hl7-org:elm-types:r1}Integer",
               "type" : "NamedTypeSpecifier"
            }
         }, {
            "name" : "ListParameter",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "type" : "ListTypeSpecifier",
               "elementType" : {
                  "name" : "{urn:hl7-org:elm-types:r1}String",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "TupleParameter",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "type" : "TupleTypeSpecifier",
               "element" : [ {
                  "name" : "a",
                  "type" : {
                     "name" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "name" : "b",
                  "type" : {
                     "name" : "{urn:hl7-org:elm-types:r1}String",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "name" : "c",
                  "type" : {
                     "name" : "{urn:hl7-org:elm-types:r1}Boolean",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "name" : "d",
                  "type" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "name" : "{urn:hl7-org:elm-types:r1}Integer",
                        "type" : "NamedTypeSpecifier"
                     }
                  }
               }, {
                  "name" : "e",
                  "type" : {
                     "type" : "TupleTypeSpecifier",
                     "element" : [ {
                        "name" : "f",
                        "type" : {
                           "name" : "{urn:hl7-org:elm-types:r1}String",
                           "type" : "NamedTypeSpecifier"
                        }
                     }, {
                        "name" : "g",
                        "type" : {
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
            "name" : "foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
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
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "FooP",
            "accessLevel" : "Public",
            "default" : {
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
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
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
context Patient
define Foo: FooP
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
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "name" : "{urn:hl7-org:elm-types:r1}Boolean",
               "type" : "NamedTypeSpecifier"
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
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "FooP",
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
context Patient
define Foo: FooP
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
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "name" : "{urn:hl7-org:elm-types:r1}Decimal",
               "type" : "NamedTypeSpecifier"
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
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "FooP",
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
context Patient
define Foo: FooP
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
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "name" : "{urn:hl7-org:elm-types:r1}Integer",
               "type" : "NamedTypeSpecifier"
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
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "FooP",
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
context Patient
define Foo: FooP
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
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "name" : "{urn:hl7-org:elm-types:r1}String",
               "type" : "NamedTypeSpecifier"
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
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "FooP",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

### ConceptParameterTypes
library TestSnippet version '1'
using QUICK
parameter FooP Concept
context Patient
define Foo: FooP
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
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "name" : "{urn:hl7-org:elm-types:r1}Concept",
               "type" : "NamedTypeSpecifier"
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
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "FooP",
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
context Patient
define Foo: FooP
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
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "name" : "{urn:hl7-org:elm-types:r1}DateTime",
               "type" : "NamedTypeSpecifier"
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
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "FooP",
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
context Patient
define Foo: FooP
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
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "name" : "{urn:hl7-org:elm-types:r1}Quantity",
               "type" : "NamedTypeSpecifier"
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
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "FooP",
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
context Patient
define Foo: FooP
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
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "name" : "{urn:hl7-org:elm-types:r1}Time",
               "type" : "NamedTypeSpecifier"
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
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "FooP",
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
context Patient
define Foo: FooP
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
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "type" : "ListTypeSpecifier",
               "elementType" : {
                  "name" : "{urn:hl7-org:elm-types:r1}String",
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
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "FooP",
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
context Patient
define Foo: FooP
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
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "type" : "IntervalTypeSpecifier",
               "pointType" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
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
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "FooP",
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
context Patient
define Foo: FooP
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
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "FooP",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "type" : "TupleTypeSpecifier",
               "element" : [ {
                  "name" : "Hello",
                  "type" : {
                     "name" : "{urn:hl7-org:elm-types:r1}String",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "name" : "MeaningOfLife",
                  "type" : {
                     "name" : "{urn:hl7-org:elm-types:r1}Integer",
                     "type" : "NamedTypeSpecifier"
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
            "name" : "Foo",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "FooP",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

