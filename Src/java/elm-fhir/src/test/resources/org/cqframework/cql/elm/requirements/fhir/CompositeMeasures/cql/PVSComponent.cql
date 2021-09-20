library PVSComponent version '0.0.001'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.001' called FHIRHelpers
include SupplementalDataElementsFHIR4 version '2.0.000' called SDE
include MATGlobalCommonFunctionsFHIR4 version '5.0.000' called Global
include HospiceFHIR4 version '2.0.000' called Hospice

codesystem "LOINC": 'http://loinc.org'

valueset "Annual Wellness Visit": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.526.3.1240'
valueset "Care Services in Long-Term Residential Facility": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1014'
valueset "Discharge Services - Nursing Facility": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.11.1065'
valueset "Discharged to Health Care Facility for Hospice Care": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.207'
valueset "Discharged to Home for Hospice Care": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.209'
valueset "Encounter Inpatient": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.666.5.307'
valueset "Home Healthcare Services": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Hospice care ambulatory": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113762.1.4.1108.15'
valueset "Nursing Facility Visit": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1012'
valueset "Office Visit": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "Pneumococcal Vaccine": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.110.12.1027'
valueset "Pneumococcal Vaccine Administered": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.110.12.1034'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.101.12.1023'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "SDE Ethnicity":
  SDE."SDE Ethnicity"

define "SDE Payer":
  SDE."SDE Payer"

define "SDE Race":
  SDE."SDE Race"

define "SDE Sex":
  SDE."SDE Sex"

define "Initial Population":
  Global."CalendarAgeInYearsAt" ( FHIRHelpers.ToDate ( Patient.birthDate ), start of "Measurement Period" ) >= 65
      and exists "Qualifying Encounters"

define "Qualifying Encounters":
  ( [Encounter: "Office Visit"]
      union [Encounter: "Annual Wellness Visit"]
      union [Encounter: "Preventive Care Services - Established Office Visit, 18 and Up"]
      union [Encounter: "Preventive Care Services-Initial Office Visit, 18 and Up"]
      union [Encounter: "Home Healthcare Services"]
      union [Encounter: "Discharge Services - Nursing Facility"]
      union [Encounter: "Nursing Facility Visit"]
      union [Encounter: "Care Services in Long-Term Residential Facility"] ) ValidEncounter
      where ValidEncounter.period during "Measurement Period"
        and ValidEncounter.status = 'finished'

define "Denominator":
  "Initial Population"

define "Denominator Exclusions":
  Hospice."Has Hospice"

define "Numerator":
  exists ( [Immunization: "Pneumococcal Vaccine"] PneumococcalVaccine
        where PneumococcalVaccine.status = 'completed'
          and PneumococcalVaccine.occurrence as dateTime on or before
          end of "Measurement Period"
    )
      or exists ( [Procedure: "Pneumococcal Vaccine Administered"] PneumococcalVaccineGiven
          where PneumococcalVaccineGiven.status = 'completed'
            and PneumococcalVaccineGiven.performed as Period on or before
            end of "Measurement Period"
      )
