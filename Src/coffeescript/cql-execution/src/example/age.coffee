module.exports = {
   "library" : {
      "identifier" : {
         "id" : "AgeAtMP",
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
      "parameters" : {
         "def" : [ {
            "name" : "MeasurementPeriod",
            "default" : {
               "lowClosed" : true,
               "highClosed" : false,
               "type" : "Interval",
               "low" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2013",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
               },
               "high" : {
                  "name" : "Date",
                  "type" : "FunctionRef",
                  "operand" : [ {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2014",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "1",
                     "type" : "Literal"
                  } ]
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
                  "dataType" : "{http://org.hl7.fhir}Patient",
                  "templateId" : "cqf-patient",
                  "type" : "Retrieve"
               }
            }
         }, {
            "name" : "InDemographic",
            "context" : "Patient",
            "expression" : {
               "type" : "And",
               "operand" : [ {
                  "type" : "GreaterOrEqual",
                  "operand" : [ {
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
                        "type" : "Start",
                        "operand" : {
                           "name" : "MeasurementPeriod",
                           "type" : "ParameterRef"
                        }
                     } ]
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "2",
                     "type" : "Literal"
                  } ]
               }, {
                  "type" : "Less",
                  "operand" : [ {
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
                        "type" : "Start",
                        "operand" : {
                           "name" : "MeasurementPeriod",
                           "type" : "ParameterRef"
                        }
                     } ]
                  }, {
                     "valueType" : "{http://www.w3.org/2001/XMLSchema}int",
                     "value" : "18",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

