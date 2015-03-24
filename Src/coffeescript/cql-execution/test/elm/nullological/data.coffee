###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### Nil
library TestSnippet version '1'
using QUICK
context Patient
define Nil: null
###

module.exports['Nil'] = {
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
            "name" : "Nil",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Null"
            }
         } ]
      }
   }
}

### IsNull
library TestSnippet version '1'
using QUICK
context Patient
define Nil: null
define One: 1
define NullIsNull: IsNull(null)
define NullVarIsNull: IsNull(Nil)
define StringIsNull: IsNull('')
define NonNullVarIsNull: IsNull(One)
###

module.exports['IsNull'] = {
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
            "name" : "Nil",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "One",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "valueType" : "{urn:hl7-org:elm:r1}Integer",
               "value" : "1",
               "type" : "Literal"
            }
         }, {
            "name" : "NullIsNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "IsNull",
               "type" : "FunctionRef",
               "operand" : [ {
                  "type" : "Null"
               } ]
            }
         }, {
            "name" : "NullVarIsNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "IsNull",
               "type" : "FunctionRef",
               "operand" : [ {
                  "name" : "Nil",
                  "type" : "ExpressionRef"
               } ]
            }
         }, {
            "name" : "StringIsNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "IsNull",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}String",
                  "value" : "",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "NonNullVarIsNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "IsNull",
               "type" : "FunctionRef",
               "operand" : [ {
                  "name" : "One",
                  "type" : "ExpressionRef"
               } ]
            }
         } ]
      }
   }
}

### Coalesce
library TestSnippet version '1'
using QUICK
context Patient
define NullNullHelloNullWorld: Coalesce(null as String, null as String, 'Hello', null as String, 'World')
define FooNullNullBar: Coalesce('Foo', null as String, null as String, 'Bar')
define AllNull: Coalesce(null as String, null as String, null as String)
###

module.exports['Coalesce'] = {
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
            "name" : "NullNullHelloNullWorld",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Coalesce",
               "type" : "FunctionRef",
               "operand" : [ {
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm:r1}String",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm:r1}String",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}String",
                  "value" : "Hello",
                  "type" : "Literal"
               }, {
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm:r1}String",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}String",
                  "value" : "World",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "FooNullNullBar",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Coalesce",
               "type" : "FunctionRef",
               "operand" : [ {
                  "valueType" : "{urn:hl7-org:elm:r1}String",
                  "value" : "Foo",
                  "type" : "Literal"
               }, {
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm:r1}String",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm:r1}String",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "valueType" : "{urn:hl7-org:elm:r1}String",
                  "value" : "Bar",
                  "type" : "Literal"
               } ]
            }
         }, {
            "name" : "AllNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "Coalesce",
               "type" : "FunctionRef",
               "operand" : [ {
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm:r1}String",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm:r1}String",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "strict" : false,
                  "type" : "As",
                  "operand" : {
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm:r1}String",
                     "type" : "NamedTypeSpecifier"
                  }
               } ]
            }
         } ]
      }
   }
}

