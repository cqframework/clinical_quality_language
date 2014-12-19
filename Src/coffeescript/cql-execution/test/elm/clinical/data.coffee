###
   WARNING: This is a GENERATED file.  Do not manually edit!

   To generate this file:
       - Edit data.coffee to add a CQL Snippet
       - From java dir: ./gradlew :cql-to-elm:generateTestData
###

### ValueSetDef
library TestSnippet version '1'
using QUICK
valueset "Known" = '2.16.840.1.113883.3.464.1003.101.12.1061'
valueset "Unknown One Arg" = '1.2.3.4.5.6.7.8.9'
valueset "Unknown Two Arg" = '1.2.3.4.5.6.7.8.9' version '1'
###

module.exports['ValueSetDef'] = {
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
      "valueSets" : {
         "def" : [ {
            "name" : "Known",
            "id" : "2.16.840.1.113883.3.464.1003.101.12.1061"
         }, {
            "name" : "Unknown One Arg",
            "id" : "1.2.3.4.5.6.7.8.9"
         }, {
            "name" : "Unknown Two Arg",
            "id" : "1.2.3.4.5.6.7.8.9",
            "version" : "1"
         } ]
      }
   }
}

### ValueSetRef
library TestSnippet version '1'
using QUICK
valueset "Acute Pharyngitis" = '2.16.840.1.113883.3.464.1003.101.12.1001'
context Patient
define Foo = "Acute Pharyngitis"
###

module.exports['ValueSetRef'] = {
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
      "valueSets" : {
         "def" : [ {
            "name" : "Acute Pharyngitis",
            "id" : "2.16.840.1.113883.3.464.1003.101.12.1001"
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
               "name" : "Acute Pharyngitis",
               "type" : "ValueSetRef"
            }
         } ]
      }
   }
}

### InValueSet
library TestSnippet version '1'
using QUICK
valueset "Female" = '2.16.840.1.113883.3.560.100.2'
valueset "Versioned Female" = '2.16.840.1.113883.3.560.100.2' version '20121025'
context Patient
define String = 'F' in "Female"
define StringInVersionedValueSet = 'F' in "Versioned Female"
define ShortCode = Code('F') in "Female"
define MediumCode = Code('F', '2.16.840.1.113883.18.2') in "Female"
define LongCode = Code('F', '2.16.840.1.113883.18.2', 'HL7V2.5') in "Female"
define WrongString = 'M' in "Female"
define WrongStringInVersionedValueSet = 'M' in "Versioned Female"
define WrongShortCode = Code('M') in "Female"
define WrongMediumCode = Code('F', '3.16.840.1.113883.18.2') in "Female"
define WrongLongCode = Code('F', '2.16.840.1.113883.18.2', 'HL7V2.6') in "Female"
###

module.exports['InValueSet'] = {
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
      "valueSets" : {
         "def" : [ {
            "name" : "Female",
            "id" : "2.16.840.1.113883.3.560.100.2"
         }, {
            "name" : "Versioned Female",
            "id" : "2.16.840.1.113883.3.560.100.2",
            "version" : "20121025"
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
            "name" : "String",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "F",
                  "type" : "Literal"
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         }, {
            "name" : "StringInVersionedValueSet",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "F",
                  "type" : "Literal"
               },
               "valueset" : {
                  "name" : "Versioned Female"
               }
            }
         }, {
            "name" : "ShortCode",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "F",
                     "type" : "Literal"
                  } ]
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         }, {
            "name" : "MediumCode",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "F",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "2.16.840.1.113883.18.2",
                     "type" : "Literal"
                  } ]
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         }, {
            "name" : "LongCode",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "F",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "2.16.840.1.113883.18.2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "HL7V2.5",
                     "type" : "Literal"
                  } ]
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         }, {
            "name" : "WrongString",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "M",
                  "type" : "Literal"
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         }, {
            "name" : "WrongStringInVersionedValueSet",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                  "value" : "M",
                  "type" : "Literal"
               },
               "valueset" : {
                  "name" : "Versioned Female"
               }
            }
         }, {
            "name" : "WrongShortCode",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "M",
                     "type" : "Literal"
                  } ]
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         }, {
            "name" : "WrongMediumCode",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "F",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "3.16.840.1.113883.18.2",
                     "type" : "Literal"
                  } ]
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         }, {
            "name" : "WrongLongCode",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "name" : "Code",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "F",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "2.16.840.1.113883.18.2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}string",
                     "value" : "HL7V2.6",
                     "type" : "Literal"
                  } ]
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         } ]
      }
   }
}

### Patient Property In ValueSet
library TestSnippet version '1'
using QUICK
valueset "Female" = '2.16.840.1.113883.3.560.100.2'
context Patient
define IsFemale = Patient.gender in "Female"
###

module.exports['Patient Property In ValueSet'] = {
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
      "valueSets" : {
         "def" : [ {
            "name" : "Female",
            "id" : "2.16.840.1.113883.3.560.100.2"
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
            "name" : "IsFemale",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "path" : "gender",
                  "type" : "Property",
                  "source" : {
                     "name" : "Patient",
                     "type" : "ExpressionRef"
                  }
               },
               "valueset" : {
                  "name" : "Female"
               }
            }
         } ]
      }
   }
}

### CalculateAgeAt
library TestSnippet version '1'
using QUICK
context Patient
define AgeAt2012 = AgeInYearsAt(DateTime(2012))
define AgeAt19810216 = AgeInYearsAt(DateTime(1981, 2, 16))
define AgeAt1975 = AgeInYearsAt(DateTime(1975))
###

module.exports['CalculateAgeAt'] = {
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
            "name" : "AgeAt2012",
            "context" : "Patient",
            "expression" : {
               "precision" : "Year",
               "type" : "CalculateAgeAt",
               "operand" : [ {
                  "path" : "birthDate",
                  "type" : "Property",
                  "source" : {
                     "name" : "Patient",
                     "type" : "ExpressionRef"
                  }
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
         }, {
            "name" : "AgeAt19810216",
            "context" : "Patient",
            "expression" : {
               "precision" : "Year",
               "type" : "CalculateAgeAt",
               "operand" : [ {
                  "path" : "birthDate",
                  "type" : "Property",
                  "source" : {
                     "name" : "Patient",
                     "type" : "ExpressionRef"
                  }
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1981",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "16",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "AgeAt1975",
            "context" : "Patient",
            "expression" : {
               "precision" : "Year",
               "type" : "CalculateAgeAt",
               "operand" : [ {
                  "path" : "birthDate",
                  "type" : "Property",
                  "source" : {
                     "name" : "Patient",
                     "type" : "ExpressionRef"
                  }
               }, {
                  "name" : "DateTime",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1975",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

