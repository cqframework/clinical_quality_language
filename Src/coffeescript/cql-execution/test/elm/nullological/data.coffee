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
            "uri" : "urn:hl7-org:elm-types:r1"
         }, {
            "localId" : "1",
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
            "localId" : "3",
            "name" : "Nil",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "3",
                  "s" : [ {
                     "value" : [ "define ","Nil",": ","null" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "2",
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
define NullIsNull: null is null
define NullVarIsNull: Nil is null
define StringIsNull: '' is null
define NonNullVarIsNull: One is null
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
            "uri" : "urn:hl7-org:elm-types:r1"
         }, {
            "localId" : "1",
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
            "localId" : "3",
            "name" : "Nil",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "3",
                  "s" : [ {
                     "value" : [ "define ","Nil",": ","null" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "2",
               "type" : "Null"
            }
         }, {
            "localId" : "5",
            "name" : "One",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "5",
                  "s" : [ {
                     "value" : [ "define ","One",": ","1" ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "4",
               "valueType" : "{urn:hl7-org:elm-types:r1}Integer",
               "value" : "1",
               "type" : "Literal"
            }
         }, {
            "localId" : "8",
            "name" : "NullIsNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "8",
                  "s" : [ {
                     "value" : [ "define ","NullIsNull",": " ]
                  }, {
                     "r" : "7",
                     "s" : [ {
                        "r" : "6",
                        "value" : [ "null"," is null" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "7",
               "type" : "IsNull",
               "operand" : {
                  "localId" : "6",
                  "type" : "Null"
               }
            }
         }, {
            "localId" : "11",
            "name" : "NullVarIsNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "11",
                  "s" : [ {
                     "value" : [ "define ","NullVarIsNull",": " ]
                  }, {
                     "r" : "10",
                     "s" : [ {
                        "r" : "9",
                        "s" : [ {
                           "value" : [ "Nil" ]
                        } ]
                     }, {
                        "value" : [ " is null" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "10",
               "type" : "IsNull",
               "operand" : {
                  "localId" : "9",
                  "name" : "Nil",
                  "type" : "ExpressionRef"
               }
            }
         }, {
            "localId" : "14",
            "name" : "StringIsNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "14",
                  "s" : [ {
                     "value" : [ "define ","StringIsNull",": " ]
                  }, {
                     "r" : "13",
                     "s" : [ {
                        "r" : "12",
                        "s" : [ {
                           "value" : [ "''" ]
                        } ]
                     }, {
                        "value" : [ " is null" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "13",
               "type" : "IsNull",
               "operand" : {
                  "localId" : "12",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "",
                  "type" : "Literal"
               }
            }
         }, {
            "localId" : "17",
            "name" : "NonNullVarIsNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "17",
                  "s" : [ {
                     "value" : [ "define ","NonNullVarIsNull",": " ]
                  }, {
                     "r" : "16",
                     "s" : [ {
                        "r" : "15",
                        "s" : [ {
                           "value" : [ "One" ]
                        } ]
                     }, {
                        "value" : [ " is null" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "16",
               "type" : "IsNull",
               "operand" : {
                  "localId" : "15",
                  "name" : "One",
                  "type" : "ExpressionRef"
               }
            }
         } ]
      }
   }
}

### Coalesce
library TestSnippet version '1'
using QUICK
context Patient
define NullNullHelloNullWorld: Coalesce(null, null, 'Hello', null, 'World')
define FooNullNullBar: Coalesce('Foo', null, null, 'Bar')
define AllNull: Coalesce(null, null, null)
define ListArgStartsWithNull: Coalesce(List{null, null, 'One', null, 'Two'})
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
            "uri" : "urn:hl7-org:elm-types:r1"
         }, {
            "localId" : "1",
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
            "localId" : "8",
            "name" : "NullNullHelloNullWorld",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "8",
                  "s" : [ {
                     "value" : [ "define ","NullNullHelloNullWorld",": " ]
                  }, {
                     "r" : "7",
                     "s" : [ {
                        "value" : [ "Coalesce","(","null",", ","null",", " ]
                     }, {
                        "r" : "4",
                        "s" : [ {
                           "value" : [ "'Hello'" ]
                        } ]
                     }, {
                        "value" : [ ", ","null",", " ]
                     }, {
                        "r" : "6",
                        "s" : [ {
                           "value" : [ "'World'" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "7",
               "type" : "Coalesce",
               "operand" : [ {
                  "localId" : "2",
                  "type" : "Null"
               }, {
                  "localId" : "3",
                  "type" : "Null"
               }, {
                  "localId" : "4",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "Hello",
                  "type" : "Literal"
               }, {
                  "localId" : "5",
                  "type" : "Null"
               }, {
                  "localId" : "6",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "World",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "14",
            "name" : "FooNullNullBar",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "14",
                  "s" : [ {
                     "value" : [ "define ","FooNullNullBar",": " ]
                  }, {
                     "r" : "13",
                     "s" : [ {
                        "value" : [ "Coalesce","(" ]
                     }, {
                        "r" : "9",
                        "s" : [ {
                           "value" : [ "'Foo'" ]
                        } ]
                     }, {
                        "value" : [ ", ","null",", ","null",", " ]
                     }, {
                        "r" : "12",
                        "s" : [ {
                           "value" : [ "'Bar'" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "13",
               "type" : "Coalesce",
               "operand" : [ {
                  "localId" : "9",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "Foo",
                  "type" : "Literal"
               }, {
                  "asType" : "{urn:hl7-org:elm-types:r1}String",
                  "type" : "As",
                  "operand" : {
                     "localId" : "10",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm-types:r1}String",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "asType" : "{urn:hl7-org:elm-types:r1}String",
                  "type" : "As",
                  "operand" : {
                     "localId" : "11",
                     "type" : "Null"
                  },
                  "asTypeSpecifier" : {
                     "name" : "{urn:hl7-org:elm-types:r1}String",
                     "type" : "NamedTypeSpecifier"
                  }
               }, {
                  "localId" : "12",
                  "valueType" : "{urn:hl7-org:elm-types:r1}String",
                  "value" : "Bar",
                  "type" : "Literal"
               } ]
            }
         }, {
            "localId" : "19",
            "name" : "AllNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "19",
                  "s" : [ {
                     "value" : [ "define ","AllNull",": " ]
                  }, {
                     "r" : "18",
                     "s" : [ {
                        "value" : [ "Coalesce","(","null",", ","null",", ","null",")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "18",
               "type" : "Coalesce",
               "operand" : [ {
                  "localId" : "15",
                  "type" : "Null"
               }, {
                  "localId" : "16",
                  "type" : "Null"
               }, {
                  "localId" : "17",
                  "type" : "Null"
               } ]
            }
         }, {
            "localId" : "27",
            "name" : "ListArgStartsWithNull",
            "context" : "Patient",
            "accessLevel" : "Public",
            "annotation" : [ {
               "type" : "Annotation",
               "s" : {
                  "r" : "27",
                  "s" : [ {
                     "value" : [ "define ","ListArgStartsWithNull",": " ]
                  }, {
                     "r" : "26",
                     "s" : [ {
                        "value" : [ "Coalesce","(" ]
                     }, {
                        "r" : "25",
                        "s" : [ {
                           "value" : [ "List{","null",", ","null",", " ]
                        }, {
                           "r" : "22",
                           "s" : [ {
                              "value" : [ "'One'" ]
                           } ]
                        }, {
                           "value" : [ ", ","null",", " ]
                        }, {
                           "r" : "24",
                           "s" : [ {
                              "value" : [ "'Two'" ]
                           } ]
                        }, {
                           "value" : [ "}" ]
                        } ]
                     }, {
                        "value" : [ ")" ]
                     } ]
                  } ]
               }
            } ],
            "expression" : {
               "localId" : "26",
               "type" : "Coalesce",
               "operand" : [ {
                  "localId" : "25",
                  "type" : "List",
                  "element" : [ {
                     "asType" : "{urn:hl7-org:elm-types:r1}String",
                     "type" : "As",
                     "operand" : {
                        "localId" : "20",
                        "type" : "Null"
                     },
                     "asTypeSpecifier" : {
                        "name" : "{urn:hl7-org:elm-types:r1}String",
                        "type" : "NamedTypeSpecifier"
                     }
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}String",
                     "type" : "As",
                     "operand" : {
                        "localId" : "21",
                        "type" : "Null"
                     },
                     "asTypeSpecifier" : {
                        "name" : "{urn:hl7-org:elm-types:r1}String",
                        "type" : "NamedTypeSpecifier"
                     }
                  }, {
                     "localId" : "22",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "One",
                     "type" : "Literal"
                  }, {
                     "asType" : "{urn:hl7-org:elm-types:r1}String",
                     "type" : "As",
                     "operand" : {
                        "localId" : "23",
                        "type" : "Null"
                     },
                     "asTypeSpecifier" : {
                        "name" : "{urn:hl7-org:elm-types:r1}String",
                        "type" : "NamedTypeSpecifier"
                     }
                  }, {
                     "localId" : "24",
                     "valueType" : "{urn:hl7-org:elm-types:r1}String",
                     "value" : "Two",
                     "type" : "Literal"
                  } ]
               } ]
            }
         } ]
      }
   }
}

