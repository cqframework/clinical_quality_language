package org.cqframework.cql.elm.serializing.xmlutil;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ElmXmlutilTest {

    @Test
    void deserializeReserializeElmJson() {
        var elm =
                """
{"library":{"annotation":[],"identifier":{"id":"AdultOutpatientEncounters_FHIR4","version":"2.0.000"}}}""";
        var lib = new ElmJsonLibraryReader().read(elm);
        var libReserialized = new ElmJsonLibraryWriter().writeAsString(lib);
        assertEquals(elm, libReserialized);
    }

    @Test
    @Disabled(
            "TODO: Polymorphic serializer for class org.hl7.elm.r1.ChoiceTypeSpecifier (Kotlin reflection is not available) has property 'type' that conflicts with JSON class discriminator. You can either change class discriminator in JsonConfiguration, rename property with @SerialName annotation or fall back to array polymorphism")
    void deserializeBigElmJson() {
        var lib = new ElmJsonLibraryReader()
                .read(
                        """
{
  "library": {
    "annotation": [
      {
        "translatorOptions": "EnableAnnotations",
        "type": "CqlToElmInfo",
        "signatureLevel" : "Differing"
      }
    ],
    "identifier": {
      "id": "AdultOutpatientEncounters_FHIR4",
      "version": "2.0.000"
    },
    "schemaIdentifier": {
      "id": "urn:hl7-org:elm",
      "version": "r1"
    },
    "usings": {
      "def": [
        {
          "localIdentifier": "System",
          "uri": "urn:hl7-org:elm-types:r1"
        },
        {
          "localIdentifier": "FHIR",
          "uri": "http://hl7.org/fhir",
          "version": "4.0.1",
          "annotation": [
            {
              "type": "Annotation",
              "t": [
                {
                  "name": "update",
                  "value": ""
                }
              ]
            }
          ]
        }
      ]
    },
    "includes": {
      "def": [
        {
          "localIdentifier": "FHIRHelpers",
          "path": "FHIRHelpers",
          "version": "4.0.1"
        }
      ]
    },
    "parameters": {
      "def": [
        {
          "name": "Measurement Period",
          "accessLevel": "Public",
          "default": {
            "lowClosed": true,
            "highClosed": false,
            "type": "Interval",
            "low": {
              "type": "DateTime",
              "year": {
                "valueType": "{urn:hl7-org:elm-types:r1}Integer",
                "value": "2019",
                "type": "Literal"
              },
              "month": {
                "valueType": "{urn:hl7-org:elm-types:r1}Integer",
                "value": "1",
                "type": "Literal"
              },
              "day": {
                "valueType": "{urn:hl7-org:elm-types:r1}Integer",
                "value": "1",
                "type": "Literal"
              },
              "hour": {
                "valueType": "{urn:hl7-org:elm-types:r1}Integer",
                "value": "0",
                "type": "Literal"
              },
              "minute": {
                "valueType": "{urn:hl7-org:elm-types:r1}Integer",
                "value": "0",
                "type": "Literal"
              },
              "second": {
                "valueType": "{urn:hl7-org:elm-types:r1}Integer",
                "value": "0",
                "type": "Literal"
              },
              "millisecond": {
                "valueType": "{urn:hl7-org:elm-types:r1}Integer",
                "value": "0",
                "type": "Literal"
              }
            },
            "high": {
              "type": "DateTime",
              "year": {
                "valueType": "{urn:hl7-org:elm-types:r1}Integer",
                "value": "2020",
                "type": "Literal"
              },
              "month": {
                "valueType": "{urn:hl7-org:elm-types:r1}Integer",
                "value": "1",
                "type": "Literal"
              },
              "day": {
                "valueType": "{urn:hl7-org:elm-types:r1}Integer",
                "value": "1",
                "type": "Literal"
              },
              "hour": {
                "valueType": "{urn:hl7-org:elm-types:r1}Integer",
                "value": "0",
                "type": "Literal"
              },
              "minute": {
                "valueType": "{urn:hl7-org:elm-types:r1}Integer",
                "value": "0",
                "type": "Literal"
              },
              "second": {
                "valueType": "{urn:hl7-org:elm-types:r1}Integer",
                "value": "0",
                "type": "Literal"
              },
              "millisecond": {
                "valueType": "{urn:hl7-org:elm-types:r1}Integer",
                "value": "0",
                "type": "Literal"
              }
            }
          },
          "parameterTypeSpecifier": {
            "type": "IntervalTypeSpecifier",
            "pointType": {
              "name": "{urn:hl7-org:elm-types:r1}DateTime",
              "type": "NamedTypeSpecifier"
            }
          }
        }
      ]
    },
    "valueSets": {
      "def": [
        {
          "name": "Office Visit",
          "id": "http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1001",
          "accessLevel": "Public"
        },
        {
          "name": "Annual Wellness Visit",
          "id": "http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1240",
          "accessLevel": "Public"
        },
        {
          "name": "Preventive Care Services - Established Office Visit, 18 and Up",
          "id": "http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1025",
          "accessLevel": "Public"
        },
        {
          "name": "Preventive Care Services-Initial Office Visit, 18 and Up",
          "id": "http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1023",
          "accessLevel": "Public"
        },
        {
          "name": "Home Healthcare Services",
          "id": "http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1016",
          "accessLevel": "Public"
        }
      ]
    },
    "contexts": {
      "def": [
        {
          "name": "Patient"
        }
      ]
    },
    "statements": {
      "def": [
        {
          "name": "Patient",
          "context": "Patient",
          "expression": {
            "type": "SingletonFrom",
            "operand": {
              "dataType": "{http://hl7.org/fhir}Patient",
              "templateId": "http://hl7.org/fhir/StructureDefinition/Patient",
              "type": "Retrieve"
            }
          }
        },
        {
          "name": "Qualifying Encounters",
          "context": "Patient",
          "accessLevel": "Public",
          "expression": {
            "type": "Query",
            "source": [
              {
                "alias": "ValidEncounter",
                "expression": {
                  "type": "Union",
                  "operand": [
                    {
                      "type": "Union",
                      "operand": [
                        {
                          "type": "Union",
                          "operand": [
                            {
                              "dataType": "{http://hl7.org/fhir}Encounter",
                              "templateId": "http://hl7.org/fhir/StructureDefinition/Encounter",
                              "codeProperty": "type",
                              "codeComparator": "in",
                              "type": "Retrieve",
                              "codes": {
                                "name": "Office Visit",
                                "type": "ValueSetRef"
                              }
                            },
                            {
                              "dataType": "{http://hl7.org/fhir}Encounter",
                              "templateId": "http://hl7.org/fhir/StructureDefinition/Encounter",
                              "codeProperty": "type",
                              "codeComparator": "in",
                              "type": "Retrieve",
                              "codes": {
                                "name": "Annual Wellness Visit",
                                "type": "ValueSetRef"
                              }
                            }
                          ]
                        },
                        {
                          "type": "Union",
                          "operand": [
                            {
                              "dataType": "{http://hl7.org/fhir}Encounter",
                              "templateId": "http://hl7.org/fhir/StructureDefinition/Encounter",
                              "codeProperty": "type",
                              "codeComparator": "in",
                              "type": "Retrieve",
                              "codes": {
                                "name": "Preventive Care Services - Established Office Visit, 18 and Up",
                                "type": "ValueSetRef"
                              }
                            },
                            {
                              "dataType": "{http://hl7.org/fhir}Encounter",
                              "templateId": "http://hl7.org/fhir/StructureDefinition/Encounter",
                              "codeProperty": "type",
                              "codeComparator": "in",
                              "type": "Retrieve",
                              "codes": {
                                "name": "Preventive Care Services-Initial Office Visit, 18 and Up",
                                "type": "ValueSetRef"
                              }
                            }
                          ]
                        }
                      ]
                    },
                    {
                      "dataType": "{http://hl7.org/fhir}Encounter",
                      "templateId": "http://hl7.org/fhir/StructureDefinition/Encounter",
                      "codeProperty": "type",
                      "codeComparator": "in",
                      "type": "Retrieve",
                      "codes": {
                        "name": "Home Healthcare Services",
                        "type": "ValueSetRef"
                      }
                    }
                  ]
                }
              }
            ],
            "relationship": [],
            "where": {
              "type": "Null"
            }
          }
        }
      ]
    }
  }
}""");
        System.out.println(new ElmJsonLibraryWriter().writeAsString(lib));
    }

    @Test
    void deserializeReserializeElmXml() {
        var elm =
                """
<?xml version="1.1" encoding="UTF-8"?><library xmlns="urn:hl7-org:elm:r1"><identifier id="PropertyTest"/></library>""";
        var lib = new ElmXmlLibraryReader().read(elm);
        var libReserialized = new ElmXmlLibraryWriter().writeAsString(lib);
        assertEquals(elm, libReserialized);
    }

    @Test
    void deserializeBigElmXml() {
        var lib = new ElmXmlLibraryReader()
                .read(
                        """
<?xml version="1.0" encoding="UTF-8"?>
<library xmlns="urn:hl7-org:elm:r1" xmlns:t="urn:hl7-org:elm-types:r1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:fhir="http://hl7.org/fhir" xmlns:a="urn:hl7-org:cql-annotations:r1">
   <identifier id="CDC" version="1"/>
   <schemaIdentifier id="urn:hl7-org:elm" version="r1"/>
   <usings>
      <def localIdentifier="System" uri="urn:hl7-org:elm-types:r1"/>
      <def localIdentifier="FHIR" uri="http://hl7.org/fhir"/>
   </usings>
   <parameters>
      <def name="MeasurementPeriod" accessLevel="Public">
         <parameterTypeSpecifier xsi:type="IntervalTypeSpecifier">
            <pointType name="t:DateTime" xsi:type="NamedTypeSpecifier"/>
         </parameterTypeSpecifier>
      </def>
   </parameters>
   <valueSets>
      <def name="HbA1c Tests" id="2.16.840.1.113883.3.464.1004.1116" accessLevel="Public"/>
      <def name="HbA1c Level Less than 7.0" id="2.16.840.1.113883.3.464.1004.1115" accessLevel="Public"/>
      <def name="HbA1c Level 7.0-9.0" id="2.16.840.1.113883.3.464.1004.1113" accessLevel="Public"/>
      <def name="HbA1c Level Greater Than 9.0" id="2.16.840.1.113883.3.464.1004.1114" accessLevel="Public"/>
      <def name="Diabetic Retinal Screening" id="2.16.840.1.113883.3.464.1004.1078" accessLevel="Public"/>
      <def name="Diabetic Retinal Screening Negative" id="2.16.840.1.113883.3.464.1004.1079" accessLevel="Public"/>
      <def name="Diabetic Retinal Screening With Eye Care Professional" id="2.16.840.1.113883.3.464.1004.1080" accessLevel="Public"/>
      <def name="Urine Macroalbumin Tests" id="2.16.840.1.113883.3.464.1004.1257" accessLevel="Public"/>
      <def name="Positive Urine Macroalbumin Tests" id="2.16.840.1.113883.3.464.1004.1216" accessLevel="Public"/>
      <def name="Urine Protein Test" id="Urine Protein Test" accessLevel="Public"/>
   </valueSets>
   <statements>
      <def name="Patient" context="Patient">
         <expression xsi:type="SingletonFrom">
            <operand dataType="fhir:Patient" xsi:type="Retrieve"/>
         </expression>
      </def>
      <def name="Lookback Interval One Year" context="Patient" accessLevel="Public">
         <expression lowClosed="true" highClosed="true" xsi:type="Interval">
            <low xsi:type="Subtract">
               <operand xsi:type="Start">
                  <operand name="MeasurementPeriod" xsi:type="ParameterRef"/>
               </operand>
               <operand value="1" unit="years" xsi:type="Quantity"/>
            </low>
            <high xsi:type="End">
               <operand name="MeasurementPeriod" xsi:type="ParameterRef"/>
            </high>
         </expression>
      </def>
      <def name="Lookback Interval Two Years" context="Patient" accessLevel="Public">
         <expression lowClosed="true" highClosed="true" xsi:type="Interval">
            <low xsi:type="Subtract">
               <operand xsi:type="Start">
                  <operand name="MeasurementPeriod" xsi:type="ParameterRef"/>
               </operand>
               <operand value="2" unit="years" xsi:type="Quantity"/>
            </low>
            <high xsi:type="End">
               <operand name="MeasurementPeriod" xsi:type="ParameterRef"/>
            </high>
         </expression>
      </def>
      <def name="In Demographic" context="Patient" accessLevel="Public">
         <expression xsi:type="GreaterOrEqual">
            <operand precision="Year" xsi:type="CalculateAgeAt">
               <operand path="birthDate.value" xsi:type="Property">
                  <source name="Patient" xsi:type="ExpressionRef"/>
               </operand>
               <operand xsi:type="Start">
                  <operand name="MeasurementPeriod" xsi:type="ParameterRef"/>
               </operand>
            </operand>
            <operand valueType="t:Integer" value="40" xsi:type="Literal"/>
         </expression>
      </def>
      <def name="Heamoglobin A1C Tests" context="Patient" accessLevel="Public">
         <expression xsi:type="Query">
            <source alias="T">
               <expression dataType="fhir:Observation" codeProperty="code" dateProperty="effectiveDateTime.value" xsi:type="Retrieve">
                  <codes name="HbA1c Tests" xsi:type="ValueSetRef"/>
                  <dateRange name="Lookback Interval One Year" xsi:type="ExpressionRef"/>
               </expression>
            </source>
            <where xsi:type="In">
               <operand path="value" xsi:type="Property">
                  <source path="status" scope="T" xsi:type="Property"/>
               </operand>
               <operand xsi:type="List">
                  <element valueType="t:String" value="final" xsi:type="Literal"/>
                  <element valueType="t:String" value="amended" xsi:type="Literal"/>
               </operand>
            </where>
         </expression>
      </def>
      <def name="Heamoglobin A1C Level Less than 7.0" context="Patient" accessLevel="Public">
         <expression xsi:type="Query">
            <source alias="H">
               <expression dataType="fhir:Observation" codeProperty="code" dateProperty="effectiveDateTime.value" xsi:type="Retrieve">
                  <codes name="HbA1c Level Less than 7.0" xsi:type="ValueSetRef"/>
                  <dateRange name="Lookback Interval One Year" xsi:type="ExpressionRef"/>
               </expression>
            </source>
            <where xsi:type="In">
               <operand path="value" xsi:type="Property">
                  <source path="status" scope="H" xsi:type="Property"/>
               </operand>
               <operand xsi:type="List">
                  <element valueType="t:String" value="final" xsi:type="Literal"/>
                  <element valueType="t:String" value="amended" xsi:type="Literal"/>
               </operand>
            </where>
         </expression>
      </def>
      <def name="Heamoglobin A1C Level 7.0-9.0" context="Patient" accessLevel="Public">
         <expression xsi:type="Query">
            <source alias="H">
               <expression dataType="fhir:Observation" codeProperty="code" dateProperty="effectiveDateTime.value" xsi:type="Retrieve">
                  <codes name="HbA1c Level 7.0-9.0" xsi:type="ValueSetRef"/>
                  <dateRange name="Lookback Interval One Year" xsi:type="ExpressionRef"/>
               </expression>
            </source>
            <where xsi:type="In">
               <operand path="value" xsi:type="Property">
                  <source path="status" scope="H" xsi:type="Property"/>
               </operand>
               <operand xsi:type="List">
                  <element valueType="t:String" value="final" xsi:type="Literal"/>
                  <element valueType="t:String" value="amended" xsi:type="Literal"/>
               </operand>
            </where>
         </expression>
      </def>
      <def name="Heamoglobin A1C Level Greater than 9.0" context="Patient" accessLevel="Public">
         <expression xsi:type="Query">
            <source alias="H">
               <expression dataType="fhir:Observation" codeProperty="code" dateProperty="effectiveDateTime.value" xsi:type="Retrieve">
                  <codes name="HbA1c Level Greater Than 9.0" xsi:type="ValueSetRef"/>
                  <dateRange name="Lookback Interval One Year" xsi:type="ExpressionRef"/>
               </expression>
            </source>
            <where xsi:type="In">
               <operand path="value" xsi:type="Property">
                  <source path="status" scope="H" xsi:type="Property"/>
               </operand>
               <operand xsi:type="List">
                  <element valueType="t:String" value="final" xsi:type="Literal"/>
                  <element valueType="t:String" value="amended" xsi:type="Literal"/>
               </operand>
            </where>
         </expression>
      </def>
      <def name="ARB Medication Order" context="Patient" accessLevel="Public">
         <expression xsi:type="Query">
            <source alias="A">
               <expression dataType="fhir:MedicationOrder" codeProperty="medicationCodeableConcept" dateProperty="dateWritten.value" xsi:type="Retrieve">
                  <codes name="ARB" xsi:type="IdentifierRef"/>
                  <dateRange name="Lookback Interval One Year" xsi:type="ExpressionRef"/>
               </expression>
            </source>
            <where xsi:type="Equal">
               <operand path="value" xsi:type="Property">
                  <source path="status" scope="A" xsi:type="Property"/>
               </operand>
               <operand valueType="t:String" value="completed" xsi:type="Literal"/>
            </where>
         </expression>
      </def>
      <def name="ACE Medication Order" context="Patient" accessLevel="Public">
         <expression xsi:type="Query">
            <source alias="A">
               <expression dataType="fhir:MedicationOrder" codeProperty="medicationCodeableConcept" dateProperty="dateWritten.value" xsi:type="Retrieve">
                  <codes name="ACE" xsi:type="IdentifierRef"/>
                  <dateRange name="Lookback Interval One Year" xsi:type="ExpressionRef"/>
               </expression>
            </source>
            <where xsi:type="Equal">
               <operand path="value" xsi:type="Property">
                  <source path="status" scope="A" xsi:type="Property"/>
               </operand>
               <operand valueType="t:String" value="completed" xsi:type="Literal"/>
            </where>
         </expression>
      </def>
      <def name="Diabetic Retinal Screening Eye Exam" context="Patient" accessLevel="Public">
         <expression xsi:type="Query">
            <source alias="D">
               <expression dataType="fhir:Procedure" codeProperty="code" dateProperty="performedDateTime.value" xsi:type="Retrieve">
                  <codes name="Diabetic Retinal Screening" xsi:type="ValueSetRef"/>
                  <dateRange name="Lookback Interval Two Years" xsi:type="ExpressionRef"/>
               </expression>
            </source>
            <where xsi:type="Equal">
               <operand path="value" xsi:type="Property">
                  <source path="status" scope="D" xsi:type="Property"/>
               </operand>
               <operand valueType="t:String" value="completed" xsi:type="Literal"/>
            </where>
         </expression>
      </def>
      <def name="Diabetic Retinal Screening Negative Eye Exam" context="Patient" accessLevel="Public">
         <expression xsi:type="Query">
            <source alias="D">
               <expression dataType="fhir:Procedure" codeProperty="code" dateProperty="performedDateTime.value" xsi:type="Retrieve">
                  <codes name="Diabetic Retinal Screening Negative" xsi:type="ValueSetRef"/>
                  <dateRange name="Lookback Interval Two Years" xsi:type="ExpressionRef"/>
               </expression>
            </source>
            <where xsi:type="Equal">
               <operand path="value" xsi:type="Property">
                  <source path="status" scope="D" xsi:type="Property"/>
               </operand>
               <operand valueType="t:String" value="completed" xsi:type="Literal"/>
            </where>
         </expression>
      </def>
      <def name="Diabetic Retinal Screening With Eye Care Professional Eye Exam" context="Patient" accessLevel="Public">
         <expression xsi:type="Query">
            <source alias="D">
               <expression dataType="fhir:Procedure" codeProperty="code" dateProperty="performedDateTime.value" xsi:type="Retrieve">
                  <codes name="Diabetic Retinal Screening With Eye Care Professional" xsi:type="ValueSetRef"/>
                  <dateRange name="Lookback Interval Two Years" xsi:type="ExpressionRef"/>
               </expression>
            </source>
            <where xsi:type="Equal">
               <operand path="value" xsi:type="Property">
                  <source path="status" scope="D" xsi:type="Property"/>
               </operand>
               <operand valueType="t:String" value="completed" xsi:type="Literal"/>
            </where>
         </expression>
      </def>
      <def name="Macroalbumin Results" context="Patient" accessLevel="Public">
         <expression xsi:type="Query">
            <source alias="M">
               <expression dataType="fhir:Observation" codeProperty="code" dateProperty="effectiveDateTime.value" xsi:type="Retrieve">
                  <codes name="Urine Macroalbumin Tests" xsi:type="ValueSetRef"/>
                  <dateRange name="Lookback Interval One Year" xsi:type="ExpressionRef"/>
               </expression>
            </source>
            <where xsi:type="In">
               <operand path="value" xsi:type="Property">
                  <source path="status" scope="M" xsi:type="Property"/>
               </operand>
               <operand xsi:type="List">
                  <element valueType="t:String" value="final" xsi:type="Literal"/>
                  <element valueType="t:String" value="amended" xsi:type="Literal"/>
               </operand>
            </where>
         </expression>
      </def>
      <def name="Positive Macroalbumin Tests" context="Patient" accessLevel="Public">
         <expression xsi:type="Query">
            <source alias="M">
               <expression dataType="fhir:Observation" codeProperty="code" dateProperty="effectiveDateTime.value" xsi:type="Retrieve">
                  <codes name="Positive Urine Macroalbumin Tests" xsi:type="ValueSetRef"/>
                  <dateRange name="Lookback Interval One Year" xsi:type="ExpressionRef"/>
               </expression>
            </source>
            <where xsi:type="In">
               <operand path="value" xsi:type="Property">
                  <source path="status" scope="M" xsi:type="Property"/>
               </operand>
               <operand xsi:type="List">
                  <element valueType="t:String" value="final" xsi:type="Literal"/>
                  <element valueType="t:String" value="amended" xsi:type="Literal"/>
               </operand>
            </where>
         </expression>
      </def>
      <def name="Diabetic Retinopathy Detected" context="Patient" accessLevel="Public">
         <expression xsi:type="Query">
            <source alias="D">
               <expression dataType="fhir:Observation" codeProperty="code" dateProperty="effectiveDateTime.value" xsi:type="Retrieve">
                  <codes name="Diabetic Retinal Screening" xsi:type="ValueSetRef"/>
                  <dateRange name="Lookback Interval Two Years" xsi:type="ExpressionRef"/>
               </expression>
            </source>
            <where xsi:type="In">
               <operand path="value" xsi:type="Property">
                  <source path="status" scope="D" xsi:type="Property"/>
               </operand>
               <operand xsi:type="List">
                  <element valueType="t:String" value="final" xsi:type="Literal"/>
                  <element valueType="t:String" value="amended" xsi:type="Literal"/>
               </operand>
            </where>
         </expression>
      </def>
   </statements>
</library>""");
        System.out.println(new ElmXmlLibraryWriter().writeAsString(lib));
    }
}
