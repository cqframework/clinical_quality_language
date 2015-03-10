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
parameter IntParameter : Integer
parameter ListParameter : list<String>
parameter TupleParameter : tuple{a : Integer, b : String, c : Boolean, d : list<Integer>, e : tuple{ f : String, g : Boolean}}
###

###
Translation Error(s):
[6:126, 6:126] no viable alternative at input '<EOF>'
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
            "uri" : "urn:hl7-org:elm:r1"
         }, {
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "MeasureYear",
            "default" : {
               "valueType" : "{urn:hl7-org:elm:r1}Integer",
               "value" : "2012",
               "type" : "Literal"
            }
         }, {
            "name" : "IntParameter",
            "parameterTypeSpecifier" : {
               "name" : "{urn:hl7-org:elm:r1}Integer",
               "type" : "NamedTypeSpecifier"
            }
         }, {
            "name" : "ListParameter",
            "parameterTypeSpecifier" : {
               "type" : "ListTypeSpecifier",
               "elementType" : {
                  "name" : "{urn:hl7-org:elm:r1}String",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "TupleParameter",
            "parameterTypeSpecifier" : {
               "type" : "TupleTypeSpecifier",
               "element" : [ {
                  "name" : "a",
                  "type" : {
                     "name" : "{urn:hl7-org:elm:r1}Integer",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "name" : "b",
                  "type" : {
                     "name" : "{urn:hl7-org:elm:r1}String",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "name" : "c",
                  "type" : {
                     "name" : "{urn:hl7-org:elm:r1}Boolean",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "name" : "d",
                  "type" : {
                     "type" : "ListTypeSpecifier",
                     "elementType" : {
                        "name" : "{urn:hl7-org:elm:r1}Integer",
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
                           "name" : "{urn:hl7-org:elm:r1}String",
                           "type" : "NamedTypeSpecifier"
                        }
                     }, {
                        "name" : "g",
                        "type" : {
                           "name" : "{urn:hl7-org:elm:r1}Boolean",
                           "type" : "NamedTypeSpecifier"
                        }
                     } ]
                  }
               } ]
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
define Foo = FooP
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
            "uri" : "urn:hl7-org:elm:r1"
         }, {
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
         } ]
      },
      "parameters" : {
         "def" : [ {
            "name" : "FooP",
            "default" : {
               "valueType" : "{urn:hl7-org:elm:r1}String",
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "Foo",
            "context" : "Patient",
            "expression" : {
               "name" : "FooP",
               "type" : "ParameterRef"
            }
         } ]
      }
   }
}

