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
define quantityStr: convert '10 \'A\'' to Quantity
define posQuantityStr: convert '+10 \'A\'' to Quantity
define negQuantityStr: convert '-10 \'A\'' to Quantity
define quantityStrDecimal: convert '10.0 \'mA\'' to Quantity
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
               "valueType" : "{urn:hl7-org:elm-types:r1}String",
               "value" : "str",
               "type" : "Literal"
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
               }
            }
         }, {
            "name" : "boolTrue",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToBoolean",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "true",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "boolFalse",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToBoolean",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "false",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "decimalValid",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToDecimal",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "10.2",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "decimalInvalid",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToDecimal",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "abc",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "integerValid",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToInteger",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "10",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "integerDropDecimal",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToInteger",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "10.2",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "integerInvalid",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToInteger",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "abc",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "quantityStr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToQuantity",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "10 'A'",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "posQuantityStr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToQuantity",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "+10 'A'",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "negQuantityStr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToQuantity",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "-10 'A'",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "quantityStrDecimal",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToQuantity",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "10.0 'mA'",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "dateStr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToDateTime",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "2015-01-02",
                  "type" : "Literal"
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
               "type" : "ToString",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "10",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "decimal10",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToDecimal",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "10",
                  "type" : "Literal"
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
               }
            }
         }, {
            "name" : "intInt",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "10",
               "type" : "Literal"
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
define negQuantityStr: convert -10 'A' to String
define posQuantityStr: convert +10 'A' to String
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
               "type" : "ToString",
               "operand" : {
                  "value" : 10,
                  "unit" : "A",
                  "type" : "Quantity"
               }
            }
         }, {
            "name" : "negQuantityStr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToString",
               "operand" : {
                  "type" : "Negate",
                  "operand" : {
                     "value" : 10,
                     "unit" : "A",
                     "type" : "Quantity"
                  }
               }
            }
         }, {
            "name" : "posQuantityStr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToString",
               "operand" : {
                  "value" : 10,
                  "unit" : "A",
                  "type" : "Quantity"
               }
            }
         }, {
            "name" : "quantityQuantity",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "value" : 10,
               "unit" : "A",
               "type" : "Quantity"
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
               "type" : "ToString",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                  "value" : "true",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "booleanFalseStr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToString",
               "operand" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
                  "value" : "false",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "booleanTrueBool",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
               "value" : "true",
               "type" : "Literal"
            }
         }, {
            "name" : "booleanFalseBool",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "valueType" : "{urn:hl7-org:elm-types:r1}Boolean",
               "value" : "false",
               "type" : "Literal"
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
               "type" : "ToString",
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
               }
            }
         }, {
            "name" : "dateDate",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
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
            }
         } ]
      }
   }
}

### FromTime
library TestSnippet version '1'
using QUICK
context Patient
define timeStr: convert @T11:57 to String
define timeTime: convert @T11:57 to Time
###

module.exports['FromTime'] = {
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
            "name" : "timeStr",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "ToString",
               "operand" : {
                  "type" : "Time",
                  "hour" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "11",
                     "type" : "Literal"
                  },
                  "minute" : {
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "57",
                     "type" : "Literal"
                  }
               }
            }
         }, {
            "name" : "timeTime",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Time",
               "hour" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "11",
                  "type" : "Literal"
               },
               "minute" : {
                  "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                  "value" : "57",
                  "type" : "Literal"
               }
            }
         } ]
      }
   }
}

### FromCode
library TestSnippet version '1'
using QUICK
// define hepB: Code '66071002' from "SNOMED-CT" display 'Type B viral hepatitis'
// define codeConcept: convert hepB to Concept
// define codeCode: convert hepB to Code
context Patient
define foo: 'bar'
###

module.exports['FromCode'] = {
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

