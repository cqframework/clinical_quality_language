###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### If
library TestSnippet version '1'
using QUICK
parameter var : Boolean
context Patient
define exp = if var then 'true return' else 'false return'
###

module.exports['If'] = {
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
            "name" : "var",
            "parameterTypeSpecifier" : {
               "name" : "{urn:hl7-org:elm:r1}Boolean",
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "exp",
            "context" : "Patient",
            "expression" : {
               "type" : "If",
               "condition" : {
                  "name" : "var",
                  "type" : "ParameterRef"
               },
               "then" : {
                  "valueType" : "{urn:hl7-org:elm:r1}String",
                  "value" : "true return",
                  "type" : "Literal"
               },
               "else" : {
                  "valueType" : "{urn:hl7-org:elm:r1}String",
                  "value" : "false return",
                  "type" : "Literal"
               }
            }
         } ]
      }
   }
}

### Case
library TestSnippet version '1'
using QUICK
parameter var : Integer
parameter X : Integer
parameter Y : Integer
context Patient

define selected =
  case var
   when 1 then 'one'
   when 2 then 'two'
   else
    '?'
  end

define standard =
  case
    when X > Y then 'X > Y'
    when X < Y then 'X < Y'
    else 'X == Y'
  end
###

module.exports['Case'] = {
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
            "name" : "var",
            "parameterTypeSpecifier" : {
               "name" : "{urn:hl7-org:elm:r1}Integer",
               "type" : "NamedTypeSpecifier"
            }
         }, {
            "name" : "X",
            "parameterTypeSpecifier" : {
               "name" : "{urn:hl7-org:elm:r1}Integer",
               "type" : "NamedTypeSpecifier"
            }
         }, {
            "name" : "Y",
            "parameterTypeSpecifier" : {
               "name" : "{urn:hl7-org:elm:r1}Integer",
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
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "selected",
            "context" : "Patient",
            "expression" : {
               "type" : "Case",
               "comparand" : {
                  "name" : "var",
                  "type" : "ParameterRef"
               },
               "caseItem" : [ {
                  "when" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "then" : {
                     "valueType" : "{urn:hl7-org:elm:r1}String",
                     "value" : "one",
                     "type" : "Literal"
                  }
               }, {
                  "when" : {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "then" : {
                     "valueType" : "{urn:hl7-org:elm:r1}String",
                     "value" : "two",
                     "type" : "Literal"
                  }
               } ],
               "else" : {
                  "valueType" : "{urn:hl7-org:elm:r1}String",
                  "value" : "?",
                  "type" : "Literal"
               }
            }
         }, {
            "name" : "standard",
            "context" : "Patient",
            "expression" : {
               "type" : "Case",
               "caseItem" : [ {
                  "when" : {
                     "type" : "Greater",
                     "operand" : [ {
                        "name" : "X",
                        "type" : "ParameterRef"
                     }, {
                        "name" : "Y",
                        "type" : "ParameterRef"
                     } ]
                  },
                  "then" : {
                     "valueType" : "{urn:hl7-org:elm:r1}String",
                     "value" : "X > Y",
                     "type" : "Literal"
                  }
               }, {
                  "when" : {
                     "type" : "Less",
                     "operand" : [ {
                        "name" : "X",
                        "type" : "ParameterRef"
                     }, {
                        "name" : "Y",
                        "type" : "ParameterRef"
                     } ]
                  },
                  "then" : {
                     "valueType" : "{urn:hl7-org:elm:r1}String",
                     "value" : "X < Y",
                     "type" : "Literal"
                  }
               } ],
               "else" : {
                  "valueType" : "{urn:hl7-org:elm:r1}String",
                  "value" : "X == Y",
                  "type" : "Literal"
               }
            }
         } ]
      }
   }
}

