library ChlamydiaScreeningforWomen version '9.2.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global
include Hospice version '2.2.000' called Hospice

codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96'

valueset "Carrier of Predominantly Sexually Transmitted Infection": 'urn:oid:2.16.840.1.113883.3.464.1003.112.11.1023'
valueset "Chlamydia": 'urn:oid:2.16.840.1.113883.3.464.1003.112.12.1003'
valueset "Chlamydia Screening": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1052'
valueset "Complications of Pregnancy, Childbirth and the Puerperium": 'urn:oid:2.16.840.1.113883.3.464.1003.111.12.1012'
valueset "Contraceptive Medications": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1080'
valueset "Delivery Live Births": 'urn:oid:2.16.840.1.113883.3.464.1003.111.12.1015'
valueset "Diagnostic Studies During Pregnancy": 'urn:oid:2.16.840.1.113883.3.464.1003.111.12.1008'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Female": 'urn:oid:2.16.840.1.113883.3.560.100.2'
valueset "Genital Herpes": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1049'
valueset "Gonococcal Infections and Venereal Diseases": 'urn:oid:2.16.840.1.113883.3.464.1003.112.12.1001'
valueset "HIV": 'urn:oid:2.16.840.1.113883.3.464.1003.120.12.1003'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Inflammatory Diseases of Female Reproductive Organs": 'urn:oid:2.16.840.1.113883.3.464.1003.112.12.1004'
valueset "Isotretinoin": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1143'
valueset "Lab Tests During Pregnancy": 'urn:oid:2.16.840.1.113883.3.464.1003.111.12.1007'
valueset "Lab Tests for Sexually Transmitted Infections": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1051'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Other Female Reproductive Conditions": 'urn:oid:2.16.840.1.113883.3.464.1003.111.12.1006'
valueset "Pap Test": 'urn:oid:2.16.840.1.113883.3.464.1003.108.12.1017'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Pregnancy Test": 'urn:oid:2.16.840.1.113883.3.464.1003.111.12.1011'
valueset "Preventive Care - Established Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1024'
valueset "Preventive Care Services - Established Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Preventive Care Services-Initial Office Visit, 18 and Up": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1023'
valueset "Preventive Care- Initial Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1022'
valueset "Procedures During Pregnancy": 'urn:oid:2.16.840.1.113883.3.464.1003.111.12.1009'
valueset "Procedures Involving Contraceptive Devices": 'urn:oid:2.16.840.1.113883.3.464.1003.111.12.1010'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Sexually Active": 'urn:oid:2.16.840.1.113883.3.464.1003.121.12.1040'
valueset "Syphilis": 'urn:oid:2.16.840.1.113883.3.464.1003.112.12.1002'
valueset "X-Ray Study (all inclusive)": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1034'

code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Yes (qualifier value)": '373066001' from "SNOMEDCT" display 'Yes (qualifier value)'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Denominator":
  "Initial Population"

define "SDE Ethnicity":
  ["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
  ["Patient Characteristic Payer": "Payer"]

define "SDE Race":
  ["Patient Characteristic Race": "Race"]

define "SDE Sex":
  ["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Numerator":
  exists ( ["Laboratory Test, Performed": "Chlamydia Screening"] ChlamydiaTest
      where ChlamydiaTest.result is not null
        and ChlamydiaTest.relevantDatetime during "Measurement Period"
  )

define "Stratification 1":
  exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) in Interval[16, 21 )
  )

define "Stratification 2":
  exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) in Interval[21, 24 )
  )

define "Active Contraceptive Medications":
  ["Medication, Active": "Contraceptive Medications"] ActiveContraceptives
    where ActiveContraceptives.relevantPeriod overlaps "Measurement Period"

define "Assessments Identifying Sexual Activity":
  ( ["Assessment, Performed": "Sexually Active"] SexualActivityAssessment
      where SexualActivityAssessment.result ~ "Yes (qualifier value)"
        and SexualActivityAssessment.relevantDatetime before 
        end of "Measurement Period"
  )

define "Diagnoses Identifying Sexual Activity":
  ( ( ["Diagnosis": "Other Female Reproductive Conditions"]
      union ["Diagnosis": "Genital Herpes"]
      union ["Diagnosis": "Gonococcal Infections and Venereal Diseases"]
      union ["Diagnosis": "Inflammatory Diseases of Female Reproductive Organs"]
      union ["Diagnosis": "Chlamydia"]
      union ["Diagnosis": "HIV"]
      union ["Diagnosis": "Syphilis"]
      union ["Diagnosis": "Carrier of Predominantly Sexually Transmitted Infection"]
      union ["Diagnosis": "Complications of Pregnancy, Childbirth and the Puerperium"] ) SexualActivityDiagnosis
      where SexualActivityDiagnosis.prevalencePeriod overlaps "Measurement Period"
  )

define "Diagnostic Studies Identifying Sexual Activity":
  ["Diagnostic Study, Order": "Diagnostic Studies During Pregnancy"] SexualActivityDiagnostics
    where SexualActivityDiagnostics.authorDatetime during "Measurement Period"

define "Laboratory Tests Identifying Sexual Activity But Not Pregnancy":
  ( ["Laboratory Test, Order": "Pap Test"]
    union ["Laboratory Test, Order": "Lab Tests During Pregnancy"]
    union ["Laboratory Test, Order": "Lab Tests for Sexually Transmitted Infections"] ) LabOrders
    where LabOrders.authorDatetime during "Measurement Period"

define "Ordered Contraceptive Medications":
  ["Medication, Order": "Contraceptive Medications"] OrderedContraceptives
    where OrderedContraceptives.authorDatetime during "Measurement Period"

define "Pregnancy Test Exclusion":
  ( ["Laboratory Test, Order": "Pregnancy Test"] PregnancyTest
      with ["Diagnostic Study, Order": "X-Ray Study (all inclusive)"] XrayOrder
        such that XrayOrder.authorDatetime occurs 7 days or less on or after day of PregnancyTest.authorDatetime
      where PregnancyTest.authorDatetime during "Measurement Period"
  )
    union ( ["Laboratory Test, Order": "Pregnancy Test"] PregnancyTestOrder
        with ["Medication, Order": "Isotretinoin"] AccutaneOrder
          such that AccutaneOrder.authorDatetime occurs 7 days or less on or after day of PregnancyTestOrder.authorDatetime
        where PregnancyTestOrder.authorDatetime during "Measurement Period"
    )

define "Laboratory Tests Identifying Sexual Activity":
  ( ["Laboratory Test, Order": "Pregnancy Test"]
    union "Laboratory Tests Identifying Sexual Activity But Not Pregnancy" ) AllLabOrders
    where AllLabOrders.authorDatetime during "Measurement Period"

define "Denominator Exclusions":
  Hospice."Has Hospice"
    or ( exists ( "Pregnancy Test Exclusion" )
        and not exists ( "Diagnoses Identifying Sexual Activity" )
        and not exists ( "Active Contraceptive Medications" )
        and not exists ( "Laboratory Tests Identifying Sexual Activity But Not Pregnancy" )
        and not exists ( "Procedures Identifying Sexual Activity" )
        and not exists ( "Assessments Identifying Sexual Activity" )
        and not exists ( "Ordered Contraceptive Medications" )
        and not exists ( "Diagnostic Studies Identifying Sexual Activity" )
    )

define "Initial Population":
  exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) in Interval[16, 24 )
  )
    and exists ( ["Patient Characteristic Sex": "Female"] )
    and exists ( "Qualifying Encounters" )
    and ( exists ( "Assessments Identifying Sexual Activity" )
        or exists ( "Diagnoses Identifying Sexual Activity" )
        or exists ( "Active Contraceptive Medications" )
        or exists ( "Ordered Contraceptive Medications" )
        or exists ( "Laboratory Tests Identifying Sexual Activity" )
        or exists ( "Diagnostic Studies Identifying Sexual Activity" )
        or exists ( "Procedures Identifying Sexual Activity" )
    )

define "Qualifying Encounters":
  ( ["Encounter, Performed": "Office Visit"]
    union ["Encounter, Performed": "Preventive Care Services - Established Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Preventive Care Services-Initial Office Visit, 18 and Up"]
    union ["Encounter, Performed": "Preventive Care- Initial Office Visit, 0 to 17"]
    union ["Encounter, Performed": "Preventive Care - Established Office Visit, 0 to 17"]
    union ["Encounter, Performed": "Home Healthcare Services"] ) ValidEncounter
    where ValidEncounter.relevantPeriod during "Measurement Period"

define "Procedures Identifying Sexual Activity":
  ( ["Procedure, Performed": "Delivery Live Births"]
    union ["Procedure, Performed": "Procedures During Pregnancy"]
    union ["Procedure, Performed": "Procedures Involving Contraceptive Devices"] ) ProceduresForSexualActivity
    where ProceduresForSexualActivity.relevantDatetime during "Measurement Period"