module.exports = {
   "library" : {
      "identifier" : {
         "id" : "CMS146",
         "version" : "2"
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
            "name" : "MeasurementPeriod",
            "accessLevel" : "Public",
            "parameterTypeSpecifier" : {
               "type" : "IntervalTypeSpecifier",
               "pointType" : {
                  "name" : "{urn:hl7-org:elm-types:r1}DateTime",
                  "type" : "NamedTypeSpecifier"
               }
            }
         } ]
      },
      "valueSets" : {
         "def" : [ {
            "name" : "Acute Pharyngitis",
            "id" : "2.16.840.1.113883.3.464.1003.102.12.1011",
            "accessLevel" : "Public"
         }, {
            "name" : "Acute Tonsillitis",
            "id" : "2.16.840.1.113883.3.464.1003.102.12.1012",
            "accessLevel" : "Public"
         }, {
            "name" : "Ambulatory/ED Visit",
            "id" : "2.16.840.1.113883.3.464.1003.101.12.1061",
            "accessLevel" : "Public"
         }, {
            "name" : "Antibiotic Medications",
            "id" : "2.16.840.1.113883.3.464.1003.196.12.1001",
            "accessLevel" : "Public"
         }, {
            "name" : "Group A Streptococcus Test",
            "id" : "2.16.840.1.113883.3.464.1003.198.12.1012",
            "accessLevel" : "Public"
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
            "name" : "InDemographic",
            "context" : "Patient",
            "accessLevel" : "Public",
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
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
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
                     "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                     "value" : "18",
                     "type" : "Literal"
                  } ]
               } ]
            }
         }, {
            "name" : "Pharyngitis",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Union",
               "operand" : [ {
                  "dataType" : "{http://hl7.org/fhir}Condition",
                  "templateId" : "condition-qicore-qicore-condition",
                  "codeProperty" : "code",
                  "type" : "Retrieve",
                  "codes" : {
                     "name" : "Acute Pharyngitis",
                     "type" : "ValueSetRef"
                  }
               }, {
                  "dataType" : "{http://hl7.org/fhir}Condition",
                  "templateId" : "condition-qicore-qicore-condition",
                  "codeProperty" : "code",
                  "type" : "Retrieve",
                  "codes" : {
                     "name" : "Acute Tonsillitis",
                     "type" : "ValueSetRef"
                  }
               } ]
            }
         }, {
            "name" : "Antibiotics",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "dataType" : "{http://hl7.org/fhir}MedicationPrescription",
               "templateId" : "medicationprescription-qicore-qicore-medicationprescription",
               "codeProperty" : "medication.code",
               "type" : "Retrieve",
               "codes" : {
                  "name" : "Antibiotic Medications",
                  "type" : "ValueSetRef"
               }
            }
         }, {
            "name" : "MeasurementPeriodEncounters",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "dataType" : "{http://hl7.org/fhir}Encounter",
                     "templateId" : "encounter-qicore-qicore-encounter",
                     "codeProperty" : "type",
                     "type" : "Retrieve",
                     "codes" : {
                        "name" : "Ambulatory/ED Visit",
                        "type" : "ValueSetRef"
                     }
                  }
               } ],
               "relationship" : [ ],
               "where" : {
                  "type" : "And",
                  "operand" : [ {
                     "name" : "InDemographic",
                     "type" : "ExpressionRef"
                  }, {
                     "type" : "IncludedIn",
                     "operand" : [ {
                        "path" : "period",
                        "scope" : "E",
                        "type" : "Property"
                     }, {
                        "name" : "MeasurementPeriod",
                        "type" : "ParameterRef"
                     } ]
                  } ]
               }
            }
         }, {
            "name" : "PharyngitisEncounters",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "name" : "MeasurementPeriodEncounters",
                     "type" : "ExpressionRef"
                  }
               } ],
               "relationship" : [ {
                  "alias" : "P",
                  "type" : "With",
                  "expression" : {
                     "name" : "Pharyngitis",
                     "type" : "ExpressionRef"
                  },
                  "suchThat" : {
                     "type" : "Or",
                     "operand" : [ {
                        "type" : "Includes",
                        "operand" : [ {
                           "lowClosed" : true,
                           "highClosed" : true,
                           "type" : "Interval",
                           "low" : {
                              "path" : "onsetDateTime",
                              "scope" : "P",
                              "type" : "Property"
                           },
                           "high" : {
                              "path" : "abatementDate",
                              "scope" : "P",
                              "type" : "Property"
                           }
                        }, {
                           "path" : "period",
                           "scope" : "E",
                           "type" : "Property"
                        } ]
                     }, {
                        "type" : "In",
                        "operand" : [ {
                           "path" : "onsetDateTime",
                           "scope" : "P",
                           "type" : "Property"
                        }, {
                           "path" : "period",
                           "scope" : "E",
                           "type" : "Property"
                        } ]
                     } ]
                  }
               }, {
                  "alias" : "A",
                  "type" : "With",
                  "expression" : {
                     "name" : "Antibiotics",
                     "type" : "ExpressionRef"
                  },
                  "suchThat" : {
                     "type" : "In",
                     "operand" : [ {
                        "precision" : "Day",
                        "type" : "DurationBetween",
                        "operand" : [ {
                           "path" : "dateWritten",
                           "scope" : "A",
                           "type" : "Property"
                        }, {
                           "type" : "Start",
                           "operand" : {
                              "path" : "period",
                              "scope" : "E",
                              "type" : "Property"
                           }
                        } ]
                     }, {
                        "lowClosed" : true,
                        "highClosed" : false,
                        "type" : "Interval",
                        "low" : {
                           "type" : "Negate",
                           "operand" : {
                              "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                              "value" : "3",
                              "type" : "Literal"
                           }
                        },
                        "high" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        }
                     } ]
                  }
               } ]
            }
         }, {
            "name" : "PharyngitisWithPriorAntibiotics",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "P",
                  "expression" : {
                     "name" : "Pharyngitis",
                     "type" : "ExpressionRef"
                  }
               } ],
               "relationship" : [ {
                  "alias" : "A",
                  "type" : "With",
                  "expression" : {
                     "name" : "Antibiotics",
                     "type" : "ExpressionRef"
                  },
                  "suchThat" : {
                     "type" : "In",
                     "operand" : [ {
                        "precision" : "Day",
                        "type" : "DurationBetween",
                        "operand" : [ {
                           "path" : "dateWritten",
                           "scope" : "A",
                           "type" : "Property"
                        }, {
                           "path" : "onsetDateTime",
                           "scope" : "P",
                           "type" : "Property"
                        } ]
                     }, {
                        "lowClosed" : false,
                        "highClosed" : true,
                        "type" : "Interval",
                        "low" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "0",
                           "type" : "Literal"
                        },
                        "high" : {
                           "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
                           "value" : "30",
                           "type" : "Literal"
                        }
                     } ]
                  }
               } ]
            }
         }, {
            "name" : "ExcludedEncounters",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "name" : "PharyngitisEncounters",
                     "type" : "ExpressionRef"
                  }
               } ],
               "relationship" : [ {
                  "alias" : "P",
                  "type" : "With",
                  "expression" : {
                     "name" : "PharyngitisWithPriorAntibiotics",
                     "type" : "ExpressionRef"
                  },
                  "suchThat" : {
                     "type" : "Or",
                     "operand" : [ {
                        "type" : "Includes",
                        "operand" : [ {
                           "lowClosed" : true,
                           "highClosed" : true,
                           "type" : "Interval",
                           "low" : {
                              "path" : "onsetDateTime",
                              "scope" : "P",
                              "type" : "Property"
                           },
                           "high" : {
                              "path" : "abatementDate",
                              "scope" : "P",
                              "type" : "Property"
                           }
                        }, {
                           "path" : "period",
                           "scope" : "E",
                           "type" : "Property"
                        } ]
                     }, {
                        "type" : "In",
                        "operand" : [ {
                           "path" : "onsetDateTime",
                           "scope" : "P",
                           "type" : "Property"
                        }, {
                           "path" : "period",
                           "scope" : "E",
                           "type" : "Property"
                        } ]
                     } ]
                  }
               } ]
            }
         }, {
            "name" : "StrepTestEncounters",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Query",
               "source" : [ {
                  "alias" : "E",
                  "expression" : {
                     "name" : "PharyngitisEncounters",
                     "type" : "ExpressionRef"
                  }
               } ],
               "relationship" : [ {
                  "alias" : "T",
                  "type" : "With",
                  "expression" : {
                     "dataType" : "{http://hl7.org/fhir}DiagnosticReport",
                     "templateId" : "diagnosticreport-qicore-qicore-diagnosticreport",
                     "codeProperty" : "name",
                     "type" : "Retrieve",
                     "codes" : {
                        "name" : "Group A Streptococcus Test",
                        "type" : "ValueSetRef"
                     }
                  },
                  "suchThat" : {
                     "type" : "And",
                     "operand" : [ {
                        "type" : "Not",
                        "operand" : {
                           "type" : "IsNull",
                           "operand" : {
                              "path" : "result",
                              "scope" : "T",
                              "type" : "Property"
                           }
                        }
                     }, {
                        "type" : "In",
                        "operand" : [ {
                           "path" : "issued",
                           "scope" : "T",
                           "type" : "Property"
                        }, {
                           "lowClosed" : true,
                           "highClosed" : true,
                           "type" : "Interval",
                           "low" : {
                              "type" : "Subtract",
                              "operand" : [ {
                                 "type" : "Start",
                                 "operand" : {
                                    "path" : "period",
                                    "scope" : "E",
                                    "type" : "Property"
                                 }
                              }, {
                                 "value" : 3,
                                 "unit" : "days",
                                 "type" : "Quantity"
                              } ]
                           },
                           "high" : {
                              "type" : "Add",
                              "operand" : [ {
                                 "type" : "End",
                                 "operand" : {
                                    "path" : "period",
                                    "scope" : "E",
                                    "type" : "Property"
                                 }
                              }, {
                                 "value" : 3,
                                 "unit" : "days",
                                 "type" : "Quantity"
                              } ]
                           }
                        } ]
                     } ]
                  }
               } ]
            }
         }, {
            "name" : "IPPCount",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Count",
               "source" : {
                  "name" : "PharyngitisEncounters",
                  "type" : "ExpressionRef"
               }
            }
         }, {
            "name" : "DenominatorCount",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "name" : "IPPCount",
               "type" : "ExpressionRef"
            }
         }, {
            "name" : "DenominatorExclusionsCount",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Count",
               "source" : {
                  "name" : "ExcludedEncounters",
                  "type" : "ExpressionRef"
               }
            }
         }, {
            "name" : "NumeratorCount",
            "context" : "Patient",
            "accessLevel" : "Public",
            "expression" : {
               "type" : "Count",
               "source" : {
                  "type" : "Except",
                  "operand" : [ {
                     "name" : "StrepTestEncounters",
                     "type" : "ExpressionRef"
                  }, {
                     "name" : "ExcludedEncounters",
                     "type" : "ExpressionRef"
                  } ]
               }
            }
         } ]
      }
   }
}

