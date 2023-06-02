library ChildhoodImmunizationStatus version '9.2.000'

using QDM version '5.5'

include MATGlobalCommonFunctions version '5.0.000' called Global
include Hospice version '2.2.000' called Hospice

codesystem "SNOMEDCT": 'urn:oid:2.16.840.1.113883.6.96'
codesystem "LOINC": 'urn:oid:2.16.840.1.113883.6.1'
codesystem "CVX": 'urn:oid:2.16.840.1.113883.12.292'

valueset "Anaphylactic Reaction to Common Baker's Yeast": 'urn:oid:2.16.840.1.113883.3.464.1003.199.12.1032'
valueset "Anaphylactic Reaction to DTaP Vaccine": 'urn:oid:2.16.840.1.113883.3.464.1003.199.12.1031'
valueset "Anaphylactic Reaction to Hepatitis A Vaccine": 'urn:oid:2.16.840.1.113883.3.464.1003.199.12.1026'
valueset "Anti Hepatitis A IgG Antigen Test": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1033'
valueset "Anti Hepatitis B Virus Surface Ab": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1073'
valueset "Disorders of the Immune System": 'urn:oid:2.16.840.1.113883.3.464.1003.120.12.1001'
valueset "DTaP Vaccine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1214'
valueset "DTaP Vaccine Administered": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1022'
valueset "Encephalopathy due to Childhood Vaccination": 'urn:oid:2.16.840.1.113883.3.464.1003.114.12.1007'
valueset "Ethnicity": 'urn:oid:2.16.840.1.114222.4.11.837'
valueset "Hepatitis A": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1024'
valueset "Hepatitis A Vaccine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1215'
valueset "Hepatitis A Vaccine Administered": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1041'
valueset "Hepatitis B": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1025'
valueset "Hepatitis B Vaccine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1216'
valueset "Hepatitis B Vaccine Administered": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1042'
valueset "Hib Vaccine (3 dose schedule)": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1083'
valueset "Hib Vaccine (3 dose schedule) Administered": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1084'
valueset "Hib Vaccine (4 dose schedule)": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1085'
valueset "Hib Vaccine (4 dose schedule) Administered": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1086'
valueset "HIV": 'urn:oid:2.16.840.1.113883.3.464.1003.120.12.1003'
valueset "Home Healthcare Services": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Inactivated Polio Vaccine (IPV)": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1219'
valueset "Inactivated Polio Vaccine (IPV) Administered": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1045'
valueset "Influenza Vaccine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1218'
valueset "Influenza Vaccine Administered": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1044'
valueset "Intussusception": 'urn:oid:2.16.840.1.113883.3.464.1003.199.12.1056'
valueset "Malignant Neoplasm of Lymphatic and Hematopoietic Tissue": 'urn:oid:2.16.840.1.113883.3.464.1003.108.12.1009'
valueset "Measles": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1053'
valueset "Measles Antibody Test (IgG Antibody presence)": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1060'
valueset "Measles Antibody Test (IgG Antibody Titer)": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1059'
valueset "Measles, Mumps and Rubella (MMR) Vaccine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1224'
valueset "Measles, Mumps and Rubella (MMR) Vaccine Administered": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1031'
valueset "Mumps": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1032'
valueset "Mumps Antibody Test (IgG Antibody presence)": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1062'
valueset "Mumps Antibody Test (IgG Antibody Titer)": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1061'
valueset "Office Visit": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "ONC Administrative Sex": 'urn:oid:2.16.840.1.113762.1.4.1'
valueset "Payer": 'urn:oid:2.16.840.1.114222.4.11.3591'
valueset "Pneumococcal Conjugate Vaccine": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1221'
valueset "Pneumococcal Conjugate Vaccine Administered": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1046'
valueset "Positive Finding": 'urn:oid:2.16.840.1.113883.3.464.1003.121.12.1016'
valueset "Preventive Care Services, Initial Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1022'
valueset "Preventive Care, Established Office Visit, 0 to 17": 'urn:oid:2.16.840.1.113883.3.464.1003.101.12.1024'
valueset "Race": 'urn:oid:2.16.840.1.114222.4.11.836'
valueset "Rotavirus Vaccine (2 dose schedule) Administered": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1048'
valueset "Rotavirus Vaccine (3 dose schedule)": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1223'
valueset "Rotavirus Vaccine (3 dose schedule) Administered": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1047'
valueset "Rubella": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1037'
valueset "Rubella Antibody Test (IgG Antibody presence)": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1064'
valueset "Rubella Antibody Test (IgG Antibody Titer)": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1063'
valueset "Severe Combined Immunodeficiency": 'urn:oid:2.16.840.1.113883.3.464.1003.120.12.1007'
valueset "Varicella Zoster": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1039'
valueset "Varicella Zoster Antibody Test (IgG Antibody Presence)": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1067'
valueset "Varicella Zoster Antibody Test (IgG Antibody Titer)": 'urn:oid:2.16.840.1.113883.3.464.1003.198.12.1066'
valueset "Varicella Zoster Vaccine (VZV)": 'urn:oid:2.16.840.1.113883.3.464.1003.196.12.1170'
valueset "Varicella Zoster Vaccine (VZV) Administered": 'urn:oid:2.16.840.1.113883.3.464.1003.110.12.1040'

code "Anaphylaxis due to Haemophilus influenzae type b vaccine (disorder)": '433621000124101' from "SNOMEDCT" display 'Anaphylaxis due to Haemophilus influenzae type b vaccine (disorder)'
code "Anaphylaxis due to Hepatitis B vaccine (disorder)": '428321000124101' from "SNOMEDCT" display 'Anaphylaxis due to Hepatitis B vaccine (disorder)'
code "Anaphylaxis due to rotavirus vaccine (disorder)": '428331000124103' from "SNOMEDCT" display 'Anaphylaxis due to rotavirus vaccine (disorder)'
code "Birth date": '21112-8' from "LOINC" display 'Birth date'
code "Influenza virus vaccine adverse reaction (disorder)": '420113004' from "SNOMEDCT" display 'Influenza virus vaccine adverse reaction (disorder)'
code "Neomycin adverse reaction (disorder)": '292927007' from "SNOMEDCT" display 'Neomycin adverse reaction (disorder)'
code "Pneumococcal vaccine adverse reaction (disorder)": '293116002' from "SNOMEDCT" display 'Pneumococcal vaccine adverse reaction (disorder)'
code "Poliomyelitis vaccine adverse reaction (disorder)": '293117006' from "SNOMEDCT" display 'Poliomyelitis vaccine adverse reaction (disorder)'
code "Polymyxin B adverse reaction (disorder)": '292992006' from "SNOMEDCT" display 'Polymyxin B adverse reaction (disorder)'
code "rotavirus, live, monovalent vaccine": '119' from "CVX" display 'rotavirus, live, monovalent vaccine'
code "Streptomycin adverse reaction (disorder)": '292925004' from "SNOMEDCT" display 'Streptomycin adverse reaction (disorder)'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Denominator":
  "Initial Population"

define "Denominator Exclusions":
  Hospice."Has Hospice"

define "Qualifying Encounter":
  ( ["Encounter, Performed": "Office Visit"]
    union ["Encounter, Performed": "Home Healthcare Services"]
    union ["Encounter, Performed": "Preventive Care, Established Office Visit, 0 to 17"]
    union ["Encounter, Performed": "Preventive Care Services, Initial Office Visit, 0 to 17"] ) ValidEncounter
    where ValidEncounter.relevantPeriod during "Measurement Period"

define "Rotavirus 2 or 3 Dose Immunizations":
  "Rotavirus 2 Dose Immunizations or Procedures"
    union "Rotavirus 3 Dose Immunizations or Procedures"

define "SDE Ethnicity":
  ["Patient Characteristic Ethnicity": "Ethnicity"]

define "SDE Payer":
  ["Patient Characteristic Payer": "Payer"]

define "SDE Race":
  ["Patient Characteristic Race": "Race"]

define "SDE Sex":
  ["Patient Characteristic Sex": "ONC Administrative Sex"]

define "Has Appropriate Number of Rotavirus Immunizations":
  exists ( "All Rotavirus Vaccinations" RotavirusImmunization
      let has_3_dose: Count("Rotavirus 3 Dose Immunizations or Procedures")> 0
      where ( has_3_dose
          and Count(distinct("All Rotavirus Vaccinations" RotavirusVaccinations
                return RotavirusVaccinations.relevantDatetime
            )
          )>= 3
      )
        or ( has_3_dose is false
            and Count(distinct("All Rotavirus Vaccinations" RotavirusVaccinations
                  return RotavirusVaccinations.relevantDatetime
              )
            )>= 2
        )
  )

define "Four Pneumococcal Conjugate Vaccinations":
  from
    "Pneumococcal Conjugate Immunizations or Procedures" PneumococcalVaccination1,
    "Pneumococcal Conjugate Immunizations or Procedures" PneumococcalVaccination2,
    "Pneumococcal Conjugate Immunizations or Procedures" PneumococcalVaccination3,
    "Pneumococcal Conjugate Immunizations or Procedures" PneumococcalVaccination4
    where PneumococcalVaccination2.relevantDatetime 1 day or more after day of PneumococcalVaccination1.relevantDatetime
      and PneumococcalVaccination3.relevantDatetime 1 day or more after day of PneumococcalVaccination2.relevantDatetime
      and PneumococcalVaccination4.relevantDatetime 1 day or more after day of PneumococcalVaccination3.relevantDatetime
    return PneumococcalVaccination1

define "Two Influenza Vaccinations":
  from
    "Influenza Immunizations or Procedures" FluVaccination1,
    "Influenza Immunizations or Procedures" FluVaccination2
    where FluVaccination2.relevantDatetime 1 day or more after day of FluVaccination1.relevantDatetime
    return FluVaccination1

define "Three Polio Vaccinations":
  from
    "Polio Immunizations or Procedures" PolioVaccination1,
    "Polio Immunizations or Procedures" PolioVaccination2,
    "Polio Immunizations or Procedures" PolioVaccination3
    where PolioVaccination2.relevantDatetime 1 day or more after day of PolioVaccination1.relevantDatetime
      and PolioVaccination3.relevantDatetime 1 day or more after day of PolioVaccination2.relevantDatetime
    return PolioVaccination1

define "Four DTaP Vaccinations":
  from
    "DTaP Immunizations or Procedures" DTaPVaccination1,
    "DTaP Immunizations or Procedures" DTaPVaccination2,
    "DTaP Immunizations or Procedures" DTaPVaccination3,
    "DTaP Immunizations or Procedures" DTaPVaccination4
    where DTaPVaccination2.relevantDatetime 1 day or more after day of DTaPVaccination1.relevantDatetime
      and DTaPVaccination3.relevantDatetime 1 day or more after day of DTaPVaccination2.relevantDatetime
      and DTaPVaccination4.relevantDatetime 1 day or more after day of DTaPVaccination3.relevantDatetime
    return DTaPVaccination1

define "Influenza Immunizations or Procedures":
  ( ( ["Immunization, Administered": "Influenza Vaccine"]
      union ["Procedure, Performed": "Influenza Vaccine Administered"] ) InfluenzaAdministration
      where InfluenzaAdministration.relevantDatetime during Interval[Patient.birthDatetime + 180 days, Patient.birthDatetime + 2 years]
  )

define "DTaP Immunizations or Procedures":
  ( ( ["Immunization, Administered": "DTaP Vaccine"]
      union ["Procedure, Performed": "DTaP Vaccine Administered"] ) DTaPVaccination
      where DTaPVaccination.relevantDatetime during Interval[Patient.birthDatetime + 42 days, Patient.birthDatetime + 2 years]
  )

define "Hepatitis B Immunizations or Procedures":
  ( ( ["Immunization, Administered": "Hepatitis B Vaccine"]
      union ["Procedure, Performed": "Hepatitis B Vaccine Administered"] ) HepatitisBVaccination
      where HepatitisBVaccination.relevantDatetime during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years]
  )

define "Pneumococcal Conjugate Immunizations or Procedures":
  ( ( ["Immunization, Administered": "Pneumococcal Conjugate Vaccine"]
      union ["Procedure, Performed": "Pneumococcal Conjugate Vaccine Administered"] ) PneumococcalVaccination
      where PneumococcalVaccination.relevantDatetime during Interval[Patient.birthDatetime + 42 days, Patient.birthDatetime + 2 years]
  )

define "Polio Immunizations or Procedures":
  ( ( ["Immunization, Administered": "Inactivated Polio Vaccine (IPV)"]
      union ["Procedure, Performed": "Inactivated Polio Vaccine (IPV) Administered"] ) PolioVaccination
      where PolioVaccination.relevantDatetime during Interval[Patient.birthDatetime + 42 days, Patient.birthDatetime + 2 years]
  )

define "Rotavirus 2 Dose Immunizations or Procedures":
  ( ( ["Immunization, Administered": "rotavirus, live, monovalent vaccine"]
      union ["Procedure, Performed": "Rotavirus Vaccine (2 dose schedule) Administered"] ) TwoDoseRotavirus
      where TwoDoseRotavirus.relevantDatetime during Interval[Patient.birthDatetime + 42 days, Patient.birthDatetime + 2 years]
  )

define "Rotavirus 3 Dose Immunizations or Procedures":
  ( ( ["Immunization, Administered": "Rotavirus Vaccine (3 dose schedule)"]
      union ["Procedure, Performed": "Rotavirus Vaccine (3 dose schedule) Administered"] ) ThreeDoseRotavirus
      where ThreeDoseRotavirus.relevantDatetime during Interval[Patient.birthDatetime + 42 days, Patient.birthDatetime + 2 years]
  )

define "Three Hepatitis B Vaccinations":
  from
    "Hepatitis B Immunizations or Procedures" HepatitisBVaccination1,
    "Hepatitis B Immunizations or Procedures" HepatitisBVaccination2,
    "Hepatitis B Immunizations or Procedures" HepatitisBVaccination3
    where HepatitisBVaccination2.relevantDatetime 1 day or more after day of HepatitisBVaccination1.relevantDatetime
      and HepatitisBVaccination3.relevantDatetime 1 day or more after day of HepatitisBVaccination2.relevantDatetime
    return HepatitisBVaccination1

define "One Chicken Pox Vaccination":
  ( ( ["Immunization, Administered": "Varicella Zoster Vaccine (VZV)"]
      union ["Procedure, Performed": "Varicella Zoster Vaccine (VZV) Administered"] ) ChickenPoxVaccination
      where ChickenPoxVaccination.relevantDatetime during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years]
  )

define "One Hepatitis A Vaccinations":
  ( ( ["Immunization, Administered": "Hepatitis A Vaccine"]
      union ["Procedure, Performed": "Hepatitis A Vaccine Administered"] ) HepatitisAVaccination
      where HepatitisAVaccination.relevantDatetime during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years]
  )

define "One MMR Vaccination":
  ( ( ["Immunization, Administered": "Measles, Mumps and Rubella (MMR) Vaccine"]
      union ["Procedure, Performed": "Measles, Mumps and Rubella (MMR) Vaccine Administered"] ) MMRVaccination
      where MMRVaccination.relevantDatetime during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years]
  )

define "Initial Population":
  exists ( ["Patient Characteristic Birthdate": "Birth date"] BirthDate
      where Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, start of "Measurement Period" ) >= 1
        and Global."CalendarAgeInYearsAt" ( BirthDate.birthDatetime, 
        end of "Measurement Period" ) = 2
  )
    and exists ( "Qualifying Encounter" )

define "Hepatitis A Numerator Inclusion Conditions":
  ( ( ["Laboratory Test, Performed": "Anti Hepatitis A IgG Antigen Test"] HepatitisAAntigenTest
      where HepatitisAAntigenTest.result as Code in "Positive Finding"
  )
    union ( ["Diagnosis": "Anaphylactic Reaction to Hepatitis A Vaccine"] HepatitisAReaction
        return "Laboratory Test, Performed" { relevantDatetime: start of HepatitisAReaction.prevalencePeriod }
    )
    union ( ["Diagnosis": "Hepatitis A"] HepatitisADiagnosis
        return "Laboratory Test, Performed" { relevantDatetime: start of HepatitisADiagnosis.prevalencePeriod }
    ) ) HepatitisAConditions
    where ( HepatitisAConditions.relevantDatetime during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years])

define "Hepatitis B Numerator Inclusion Conditions":
  ( ( ["Laboratory Test, Performed": "Anti Hepatitis B Virus Surface Ab"] HepatitisBAntigenTest
      where HepatitisBAntigenTest.result as Code in "Positive Finding"
  )
    union ( ["Diagnosis": "Anaphylaxis due to Hepatitis B vaccine (disorder)"] HepatitisBReaction
        return "Laboratory Test, Performed" { relevantDatetime: start of HepatitisBReaction.prevalencePeriod }
    )
    union ( ["Diagnosis": "Anaphylactic Reaction to Common Baker's Yeast"] YeastReaction
        return "Laboratory Test, Performed" { relevantDatetime: start of YeastReaction.prevalencePeriod }
    )
    union ( ["Diagnosis": "Hepatitis B"] HepatitisBDiagnosis
        return "Laboratory Test, Performed" { relevantDatetime: start of HepatitisBDiagnosis.prevalencePeriod }
    ) ) HepBConditions
    where ( HepBConditions.relevantDatetime during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years])

define "MMR Numerator Inclusion Conditions":
  ( ["Diagnosis": "Disorders of the Immune System"]
    union ["Diagnosis": "HIV"]
    union ["Diagnosis": "Malignant Neoplasm of Lymphatic and Hematopoietic Tissue"]
    union ["Diagnosis": "Neomycin adverse reaction (disorder)"] ) MMRConditions
    where ( start of MMRConditions.prevalencePeriod during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years])

define "Influenza Numerator Inclusion Conditions":
  ( ["Diagnosis": "Influenza virus vaccine adverse reaction (disorder)"]
    union ["Diagnosis": "Malignant Neoplasm of Lymphatic and Hematopoietic Tissue"]
    union ["Diagnosis": "Neomycin adverse reaction (disorder)"]
    union ["Diagnosis": "HIV"]
    union ["Diagnosis": "Disorders of the Immune System"] ) InfluenzaConditions
    where ( start of InfluenzaConditions.prevalencePeriod during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years])

define "Mumps Indicators":
  ( ( ["Laboratory Test, Performed": "Mumps Antibody Test (IgG Antibody Titer)"] MumpsTiter
      where MumpsTiter.result >= 1.10
  )
    union ( ["Laboratory Test, Performed": "Mumps Antibody Test (IgG Antibody presence)"] MumpsIgGPresence
        where MumpsIgGPresence.result as Code in "Positive Finding"
    )
    union ( ["Diagnosis": "Mumps"] MumpsDiagnosis
        return "Laboratory Test, Performed" { relevantDatetime: start of MumpsDiagnosis.prevalencePeriod }
    ) ) Mumps
    where ( Mumps.relevantDatetime during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years])

define "Pneumococcal Conjugate Numerator Inclusion Conditions":
  ["Diagnosis": "Pneumococcal vaccine adverse reaction (disorder)"] PneumococcalReaction
    where ( start of PneumococcalReaction.prevalencePeriod during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years])

define "Polio Numerator Inclusion Conditions":
  ( ["Diagnosis": "Poliomyelitis vaccine adverse reaction (disorder)"]
    union ["Diagnosis": "Streptomycin adverse reaction (disorder)"]
    union ["Diagnosis": "Polymyxin B adverse reaction (disorder)"]
    union ["Diagnosis": "Neomycin adverse reaction (disorder)"] ) PolioConditions
    where ( start of PolioConditions.prevalencePeriod during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years])

define "Rotavirus Numerator Inclusion Conditions":
  ( ( ["Diagnosis": "Anaphylaxis due to rotavirus vaccine (disorder)"]
      union ["Diagnosis": "Severe Combined Immunodeficiency"]
      union ["Diagnosis": "Intussusception"] ) RotavirusConditions
      where ( start of RotavirusConditions.prevalencePeriod during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years])
  )

define "Rubella Indicators":
  ( ( ["Laboratory Test, Performed": "Rubella Antibody Test (IgG Antibody Titer)"] RubellaTiter
      where RubellaTiter.result >= 1.10
  )
    union ( ["Laboratory Test, Performed": "Rubella Antibody Test (IgG Antibody presence)"] RubellaIgGPresence
        where RubellaIgGPresence.result as Code in "Positive Finding"
    )
    union ( ["Diagnosis": "Rubella"] RubellaDiagnosis
        return "Laboratory Test, Performed" { relevantDatetime: start of RubellaDiagnosis.prevalencePeriod }
    ) ) Rubella
    where ( Rubella.relevantDatetime during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years])

define "Varicella Zoster Numerator Inclusion Conditions":
  ( ( ["Laboratory Test, Performed": "Varicella Zoster Antibody Test (IgG Antibody Titer)"] ChickenPoxTiter
      where ChickenPoxTiter.result >= 1.10
  )
    union ( ["Laboratory Test, Performed": "Varicella Zoster Antibody Test (IgG Antibody Presence)"] ChickenPoxIgGPresence
        where ChickenPoxIgGPresence.result as Code in "Positive Finding"
    )
    union ( ["Diagnosis": "Disorders of the Immune System"] ImmuneDisorder
        return "Laboratory Test, Performed" { relevantDatetime: start of ImmuneDisorder.prevalencePeriod }
    )
    union ( ["Diagnosis": "HIV"] HIV
        return "Laboratory Test, Performed" { relevantDatetime: start of HIV.prevalencePeriod }
    )
    union ( ["Diagnosis": "Malignant Neoplasm of Lymphatic and Hematopoietic Tissue"] LymphaticMalignantNeoplasm
        return "Laboratory Test, Performed" { relevantDatetime: start of LymphaticMalignantNeoplasm.prevalencePeriod }
    )
    union ( ["Diagnosis": "Neomycin adverse reaction (disorder)"] NeomycinReaction
        return "Laboratory Test, Performed" { relevantDatetime: start of NeomycinReaction.prevalencePeriod }
    )
    union ( ["Diagnosis": "Varicella Zoster"] VaricellaZoster
        return "Laboratory Test, Performed" { relevantDatetime: start of VaricellaZoster.prevalencePeriod }
    ) ) VaricellaZosterConditions
    where ( VaricellaZosterConditions.relevantDatetime during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years])

define "Measles Indicators":
  ( ( ["Laboratory Test, Performed": "Measles Antibody Test (IgG Antibody Titer)"] MeaslesTiter
      where MeaslesTiter.result >= 1.10
  )
    union ( ["Laboratory Test, Performed": "Measles Antibody Test (IgG Antibody presence)"] MeaslesIgGPresence
        where MeaslesIgGPresence.result as Code in "Positive Finding"
    )
    union ( ["Diagnosis": "Measles"] MeaslesDiagnosis
        return "Laboratory Test, Performed" { relevantDatetime: start of MeaslesDiagnosis.prevalencePeriod }
    ) ) Measles
    where ( Measles.relevantDatetime during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years])

define "All Rotavirus Vaccinations":
  "Rotavirus 2 or 3 Dose Immunizations" AllRotavirusDoses1
    without "Rotavirus 2 or 3 Dose Immunizations" AllRotavirusDoses2
      such that AllRotavirusDoses1.relevantDatetime less than 1 day before AllRotavirusDoses2.relevantDatetime

define "DTaP Numerator Inclusion Conditions":
  ( ["Diagnosis": "Anaphylactic Reaction to DTaP Vaccine"]
    union ["Diagnosis": "Encephalopathy due to Childhood Vaccination"] ) DTaPConditions
    where ( start of DTaPConditions.prevalencePeriod during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years])

define "All Hib Vaccinations":
  "Hib 3 or 4 Dose Immunizations" AllHibDoses1
    without "Hib 3 or 4 Dose Immunizations" AllHibDoses2
      such that AllHibDoses1.relevantDatetime less than 1 day before AllHibDoses2.relevantDatetime

define "Hib 3 or 4 Dose Immunizations":
  "Hib 3 Dose Immunizations or Procedures"
    union "Hib 4 Dose Immunizations or Procedures"

define "Hib Numerator Inclusion Conditions":
  ["Diagnosis": "Anaphylaxis due to Haemophilus influenzae type b vaccine (disorder)"] HibReaction
    where ( start of HibReaction.prevalencePeriod during Interval[Patient.birthDatetime, Patient.birthDatetime + 2 years])

define "Has Appropriate Number of Hib Immunizations":
  exists ( "All Hib Vaccinations" HibImmunization //union of 3 and 4 dose immunizations
  //4 dose test
      let has_4_dose: Count("Hib 4 Dose Immunizations or Procedures")> 0
      where ( has_4_dose //if pt had at least one 4 dose code
          and Count(distinct("All Hib Vaccinations" HibVaccinations
                return HibVaccinations.relevantDatetime
            )
          )>= 4 //pt must have 4 regimens (e.g., 3,3,4,4 or 4,3,4,4)
      )
        or ( has_4_dose is false //if pt did not have at least one 4 dose code
            and Count(distinct("All Hib Vaccinations" HibVaccinations
                  return HibVaccinations.relevantDatetime
              )
            )>= 3 //pt must have 3 regimens of 3 dose codes
        )
  )

define "Numerator":
  ( exists ( "Four DTaP Vaccinations" )
      or exists ( "DTaP Numerator Inclusion Conditions" )
  )
    and ( exists ( "Three Polio Vaccinations" )
        or exists ( "Polio Numerator Inclusion Conditions" )
    )
    and ( exists ( "One MMR Vaccination" )
        or exists ( "MMR Numerator Inclusion Conditions" )
        or ( exists ( "Measles Indicators" )
            and exists ( "Mumps Indicators" )
            and exists ( "Rubella Indicators" )
        )
    )
    and ( "Has Appropriate Number of Hib Immunizations"
        or exists ( "Hib Numerator Inclusion Conditions" )
    )
    and ( exists ( "Three Hepatitis B Vaccinations" )
        or exists ( "Hepatitis B Numerator Inclusion Conditions" )
    )
    and ( exists ( "One Chicken Pox Vaccination" )
        or exists ( "Varicella Zoster Numerator Inclusion Conditions" )
    )
    and ( exists ( "Four Pneumococcal Conjugate Vaccinations" )
        or exists ( "Pneumococcal Conjugate Numerator Inclusion Conditions" )
    )
    and ( exists ( "One Hepatitis A Vaccinations" )
        or exists ( "Hepatitis A Numerator Inclusion Conditions" )
    )
    and ( "Has Appropriate Number of Rotavirus Immunizations"
        or exists ( "Rotavirus Numerator Inclusion Conditions" )
    )
    and ( exists ( "Two Influenza Vaccinations" )
        or exists ( "Influenza Numerator Inclusion Conditions" )
    )

define "Hib 3 Dose Immunizations or Procedures":
  ( ( ["Immunization, Administered": "Hib Vaccine (3 dose schedule)"]
      union ["Procedure, Performed": "Hib Vaccine (3 dose schedule) Administered"] ) ThreeDoseHib
      where ThreeDoseHib.relevantDatetime during Interval[Patient.birthDatetime + 42 days, Patient.birthDatetime + 2 years]
  )

define "Hib 4 Dose Immunizations or Procedures":
  ( ( ["Immunization, Administered": "Hib Vaccine (4 dose schedule)"]
      union ["Procedure, Performed": "Hib Vaccine (4 dose schedule) Administered"] ) FourDoseHib
      where FourDoseHib.relevantDatetime during Interval[Patient.birthDatetime + 42 days, Patient.birthDatetime + 2 years]
  )