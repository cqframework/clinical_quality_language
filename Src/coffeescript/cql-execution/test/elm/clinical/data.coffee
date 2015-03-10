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

###
Translation Error(s):
[5:60, 5:60] no viable alternative at input '<EOF>'
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
            "localIdentifier" : "System",
            "uri" : "urn:hl7-org:elm:r1"
         }, {
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
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
            "localIdentifier" : "System",
            "uri" : "urn:hl7-org:elm:r1"
         }, {
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
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
                  "dataType" : "{http://hl7.org/fhir}Patient",
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

###
Translation Error(s):
[8:20, 8:28] Could not resolve call to operator Code with signature (System.String).
[8:20, 8:40] Could not determine signature for invocation of operator System.InValueSet.
[9:21, 9:55] Could not resolve call to operator Code with signature (System.String,System.String).
[9:21, 9:67] Could not determine signature for invocation of operator System.InValueSet.
[10:19, 10:64] Could not resolve call to operator Code with signature (System.String,System.String,System.String).
[10:19, 10:76] Could not determine signature for invocation of operator System.InValueSet.
[13:25, 13:33] Could not resolve call to operator Code with signature (System.String).
[13:25, 13:45] Could not determine signature for invocation of operator System.InValueSet.
[14:26, 14:60] Could not resolve call to operator Code with signature (System.String,System.String).
[14:26, 14:72] Could not determine signature for invocation of operator System.InValueSet.
[15:24, 15:69] Could not resolve call to operator Code with signature (System.String,System.String,System.String).
[15:24, 15:81] Could not determine signature for invocation of operator System.InValueSet.
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
            "localIdentifier" : "System",
            "uri" : "urn:hl7-org:elm:r1"
         }, {
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
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
                  "dataType" : "{http://hl7.org/fhir}Patient",
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
                  "valueType" : "{urn:hl7-org:elm:r1}String",
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
                  "valueType" : "{urn:hl7-org:elm:r1}String",
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
               "type" : "Null"
            }
         }, {
            "name" : "MediumCode",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "LongCode",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "WrongString",
            "context" : "Patient",
            "expression" : {
               "type" : "InValueSet",
               "code" : {
                  "valueType" : "{urn:hl7-org:elm:r1}String",
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
                  "valueType" : "{urn:hl7-org:elm:r1}String",
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
               "type" : "Null"
            }
         }, {
            "name" : "WrongMediumCode",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
            }
         }, {
            "name" : "WrongLongCode",
            "context" : "Patient",
            "expression" : {
               "type" : "Null"
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
            "localIdentifier" : "System",
            "uri" : "urn:hl7-org:elm:r1"
         }, {
            "localIdentifier" : "QUICK",
            "uri" : "http://hl7.org/fhir"
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
                  "dataType" : "{http://hl7.org/fhir}Patient",
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

### CalculateAge
library TestSnippet version '1'
using QUICK
context Patient
define Years = AgeInYears()
define Months = AgeInMonths()
define Days = AgeInDays()
define Hours = AgeInHours()
define Minutes = AgeInMinutes()
define Seconds = AgeInSeconds()
###

module.exports['CalculateAge'] = {
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
            "name" : "Years",
            "context" : "Patient",
            "expression" : {
               "precision" : "Year",
               "type" : "CalculateAge",
               "operand" : {
                  "path" : "birthDate",
                  "type" : "Property",
                  "source" : {
                     "name" : "Patient",
                     "type" : "ExpressionRef"
                  }
               }
            }
         }, {
            "name" : "Months",
            "context" : "Patient",
            "expression" : {
               "precision" : "Month",
               "type" : "CalculateAge",
               "operand" : {
                  "path" : "birthDate",
                  "type" : "Property",
                  "source" : {
                     "name" : "Patient",
                     "type" : "ExpressionRef"
                  }
               }
            }
         }, {
            "name" : "Days",
            "context" : "Patient",
            "expression" : {
               "precision" : "Day",
               "type" : "CalculateAge",
               "operand" : {
                  "path" : "birthDate",
                  "type" : "Property",
                  "source" : {
                     "name" : "Patient",
                     "type" : "ExpressionRef"
                  }
               }
            }
         }, {
            "name" : "Hours",
            "context" : "Patient",
            "expression" : {
               "precision" : "Hour",
               "type" : "CalculateAge",
               "operand" : {
                  "path" : "birthDate",
                  "type" : "Property",
                  "source" : {
                     "name" : "Patient",
                     "type" : "ExpressionRef"
                  }
               }
            }
         }, {
            "name" : "Minutes",
            "context" : "Patient",
            "expression" : {
               "precision" : "Minute",
               "type" : "CalculateAge",
               "operand" : {
                  "path" : "birthDate",
                  "type" : "Property",
                  "source" : {
                     "name" : "Patient",
                     "type" : "ExpressionRef"
                  }
               }
            }
         }, {
            "name" : "Seconds",
            "context" : "Patient",
            "expression" : {
               "precision" : "Second",
               "type" : "CalculateAge",
               "operand" : {
                  "path" : "birthDate",
                  "type" : "Property",
                  "source" : {
                     "name" : "Patient",
                     "type" : "ExpressionRef"
                  }
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
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
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
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1981",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "2",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
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
                     "valueType" : "{urn:hl7-org:elm:r1}Integer",
                     "value" : "1975",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

