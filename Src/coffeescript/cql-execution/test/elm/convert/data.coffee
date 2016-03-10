###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### FromString
library TestSnippet version '1'
using QUICK
context Patient
define stringStr: convert 'str' to String
define stringNull: convert null to String
define boolTrue: convert 'true' to Boolean
define boolFalse: convert 'false' to Boolean
define decimalValid: convert '10.2' to Decimal
define decimalInvalid: convert 'abc' to Decimal
define integerValid: convert '10' to Integer
define integerDropDecimal: convert '10.2' to Integer
define integerInvalid: convert 'abc' to Integer
define quantityStr: convert '10A' to Quantity
define quantityStrDecimal: convert '10.0mA' to Quantity
define dateStr: convert '2015-01-02' to DateTime
###

module.exports['FromString'] = {
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
            "name" : "stringStr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "asType" : "{urn:hl7-org:elm-types:r1}String",
               "type" : "As",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "str",
                  "type" : "Literal"
               },
               "asTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}String",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "stringNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "asType" : "{urn:hl7-org:elm-types:r1}String",
               "type" : "As",
               "operand" : {
                  "type" : "Null"
               },
               "asTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}String",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "boolTrue",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Boolean",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "true",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Boolean",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "boolFalse",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Boolean",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "false",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Boolean",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "decimalValid",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Decimal",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "10.2",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "decimalInvalid",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Decimal",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "abc",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "integerValid",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Integer",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "10",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "integerDropDecimal",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Integer",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "10.2",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "integerInvalid",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Integer",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "abc",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "quantityStr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Quantity",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "10A",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Quantity",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "quantityStrDecimal",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Quantity",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "10.0mA",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Quantity",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "dateStr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}DateTime",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "2015-01-02",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}DateTime",
                  "type" : "NamedTypeSpecifier"
               }
            }
         } ]
      }
   }
}

### FromInteger
library TestSnippet version '1'
using QUICK
context Patient
define string10: convert 10 to String
define decimal10: convert 10 to Decimal
define intNull: convert null to Decimal
define intInt: convert 10 to Integer
###

module.exports['FromInteger'] = {
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
            "name" : "string10",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}String",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "10",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}String",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "decimal10",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}Decimal",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "10",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "intNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "asType" : "{urn:hl7-org:elm-types:r1}Decimal",
               "type" : "As",
               "operand" : {
                  "type" : "Null"
               },
               "asTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Decimal",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "intInt",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "asType" : "{urn:hl7-org:elm-types:r1}Integer",
               "type" : "As",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "10",
                  "type" : "Literal"
               },
               "asTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Integer",
                  "type" : "NamedTypeSpecifier"
               }
            }
         } ]
      }
   }
}

### FromQuantity
library TestSnippet version '1'
using QUICK
context Patient
define quantityStr: convert 10 'A' to String
define quantityQuantity: convert 10 'A' to Quantity
###

module.exports['FromQuantity'] = {
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
            "name" : "quantityStr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}String",
               "type" : "Convert",
               "operand" : {
                  "value" : 10,
                  "unit" : "A",
                  "type" : "Quantity"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}String",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "quantityQuantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "asType" : "{urn:hl7-org:elm-types:r1}Quantity",
               "type" : "As",
               "operand" : {
                  "value" : 10,
                  "unit" : "A",
                  "type" : "Quantity"
               },
               "asTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Quantity",
                  "type" : "NamedTypeSpecifier"
               }
            }
         } ]
      }
   }
}

### FromBoolean
library TestSnippet version '1'
using QUICK
context Patient
define booleanTrueStr: convert true to String
define booleanFalseStr: convert false to String
define booleanTrueBool: convert true to Boolean
define booleanFalseBool: convert false to Boolean
###

module.exports['FromBoolean'] = {
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
            "name" : "booleanTrueStr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}String",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                  "value" : "true",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}String",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "booleanFalseStr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}String",
               "type" : "Convert",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                  "value" : "false",
                  "type" : "Literal"
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}String",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "booleanTrueBool",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
               "type" : "As",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                  "value" : "true",
                  "type" : "Literal"
               },
               "asTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Boolean",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "booleanFalseBool",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "asType" : "{urn:hl7-org:elm-types:r1}Boolean",
               "type" : "As",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                  "value" : "false",
                  "type" : "Literal"
               },
               "asTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}Boolean",
                  "type" : "NamedTypeSpecifier"
               }
            }
         } ]
      }
   }
}

### FromDateTime
library TestSnippet version '1'
using QUICK
context Patient
define dateStr: convert @2015-01-02 to String
define dateDate: convert @2015-01-02 to DateTime
###

module.exports['FromDateTime'] = {
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
            "name" : "dateStr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "toType" : "{urn:hl7-org:elm-types:r1}String",
               "type" : "Convert",
               "operand" : {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }
               },
               "toTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}String",
                  "type" : "NamedTypeSpecifier"
               }
            }
         }, {
            "name" : "dateDate",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "asType" : "{urn:hl7-org:elm-types:r1}DateTime",
               "type" : "As",
               "operand" : {
                  "type" : "DateTime",
                  "year" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2015",
                     "type" : "Literal"
                  },
                  "month" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "day" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }
               },
               "asTypeSpecifier" : {
                  "name" : "{urn:hl7-org:elm-types:r1}DateTime",
                  "type" : "NamedTypeSpecifier"
               }
            }
         } ]
      }
   }
}

