###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### If
library TestSnippet version '1'
using QUICK
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
            "name" : "exp",
            "context" : "Patient",
            "expression" : {
               "type" : "If",
               "condition" : {
                  "name" : "var",
                  "type" : "IdentifierRef"
               },
               "then" : {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "true return",
                  "type" : "Literal"
               },
               "else" : {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
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
context Patient

define selected = 
  case var
   when 1 then 'one'
   when 2 then 'two'
   else 
    var
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
            "name" : "selected",
            "context" : "Patient",
            "expression" : {
               "type" : "Case",
               "comparand" : {
                  "name" : "var",
                  "type" : "IdentifierRef"
               },
               "caseItem" : [ {
                  "when" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  },
                  "then" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "one",
                     "type" : "Literal"
                  }
               }, {
                  "when" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  },
                  "then" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "two",
                     "type" : "Literal"
                  }
               } ],
               "else" : {
                  "name" : "var",
                  "type" : "IdentifierRef"
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
                        "type" : "IdentifierRef"
                     }, {
                        "name" : "Y",
                        "type" : "IdentifierRef"
                     } ]
                  },
                  "then" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "X > Y",
                     "type" : "Literal"
                  }
               }, {
                  "when" : {
                     "type" : "Less",
                     "operand" : [ {
                        "name" : "X",
                        "type" : "IdentifierRef"
                     }, {
                        "name" : "Y",
                        "type" : "IdentifierRef"
                     } ]
                  },
                  "then" : {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "X < Y",
                     "type" : "Literal"
                  }
               } ],
               "else" : {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "X == Y",
                  "type" : "Literal"
               }
            }
         } ]
      }
   }
}

