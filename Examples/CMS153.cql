library CMS153_CQM version '1'

/*
*  based on NQF33/CMS153v3, July 2014 EP;
*  this is v1 of the CQL library to implement;
*  report percentage of women 16-24 years of age 
*      who were identified as sexually active 
*      and who had at least one test for chlamydia during the measurement period;
*/ 

using QUICK

/*
*  data criteria = value sets
*  names listed in alphabetical order
*  grouped by QDM data model
*/ 
valueset "Diagnosis, Active: Chlamydia": '2.16.840.1.113883.3.464.1003.112.12.1003'
valueset "Diagnosis, Active: Complications of Pregnancy, Childbirth and the Puerperium": '2.16.840.1.113883.3.464.1003.111.12.1012'
valueset "Diagnosis, Active: Genital Herpes": '2.16.840.1.113883.3.464.1003.110.12.1049'
valueset "Diagnosis, Active: Genococcal Infections and Venereal Diseases": '2.16.840.1.113883.3.464.1003.112.12.1001'
valueset "Diagnosis, Active: HIV": '2.16.840.1.113883.3.464.1003.120.12.1003'
valueset "Diagnosis, Active: Inflammatory Diseases of Female Reproductive Organs": '2.16.840.1.113883.3.464.1003.112.12.1004'
valueset "Diagnosis, Active: Other Female Reproductive Conditions": '2.16.840.1.113883.3.464.1003.111.12.1006'
valueset "Diagnosis, Active: Syphilis": '2.16.840.1.113883.3.464.1003.112.12.1002'

valueset "Diagnostic Study, Order: Diagnostic Studies During Pregnancy": '2.16.840.1.113883.3.464.1003.111.12.1008'
valueset "Diagnostic Study, Order: X-Ray Study (all inclusive)": '2.16.840.1.113883.3.464.1003.198.12.1034'

valueset "Encounter, Performed: Face-to-Face Interaction": '2.16.840.1.113883.3.464.1003.101.12.1048'
valueset "Encounter, Performed: Home Healthcare Services": '2.16.840.1.113883.3.464.1003.101.12.1016'
valueset "Encounter, Performed: Office Visit": '2.16.840.1.113883.3.464.1003.101.12.1001'
valueset "Encounter, Performed: Preventive Care Services - Established Office Visit, 0 to 17": '2.16.840.1.113883.3.464.1003.101.12.1024'
valueset "Encounter, Performed: Preventive Care Services - Established Office Visit, 18 and Up": '2.16.840.1.113883.3.464.1003.101.12.1025'
valueset "Encounter, Performed: Preventive Care Services - Initial Office Visit, 18 and Up": '2.16.840.1.113883.3.464.1003.101.12.1023'
valueset "Encounter, Performed: Preventive Care Services - Initial Office Visit, 0 to 17": '2.16.840.1.113883.3.464.1003.101.12.1022'

valueset "Laboratory Test, Order: Lab Tests During Pregnancy": '2.16.840.1.113883.3.464.1003.111.12.1007'
valueset "Laboratory Test, Order: Lab Tests for Sexually Transmitted Infections": '2.16.840.1.113883.3.464.1003.110.12.1051'
valueset "Laboratory Test, Order: Pap Test": '2.16.840.1.113883.3.464.1003.108.12.1017'
valueset "Laboratory Test, Order: Pregnancy Test": '2.16.840.1.113883.3.464.1003.111.12.1011'

valueset "Laboratory Test, Result: Chlamydia Screening": '2.16.840.1.113883.3.464.1003.110.12.1052'

valueset "Medication, Active: Contraceptive Medications": '2.16.840.1.113883.3.464.1003.196.12.1080'

valueset "Medication, Order: Contraceptive Medications": '2.16.840.1.113883.3.464.1003.196.12.1080'
valueset "Medication, Order: Isotretinoin": '2.16.840.1.113883.3.464.1003.196.12.1143'

valueset "Patient Characteristic Birthdate: birth date": '2.16.840.1.113883.3.560.100.4'
valueset "Patient Characteristic Sex: Female": '2.16.840.1.113883.3.560.100.2'

valueset "Procedure, Performed: Delivery Live Births": '2.16.840.1.113883.3.464.1003.111.12.1015'
valueset "Procedure, Performed: Procedures During Pregnancy": '2.16.840.1.113883.3.464.1003.111.12.1009'
valueset "Procedure, Performed: Procedures Involving Contraceptive Devices": '2.16.840.1.113883.3.464.1003.111.12.1010'

/*
*  measurement period = 20xx Jan 1 to 20xx Dec 31;
*  default to prior full calendar year;
*  Measurement Year = ((year of today()) - 1)
*/
parameter MeasurementPeriod default Interval[DateTime( ((year from Today()) - 1), 1, 1 ), DateTime( ((year from Today()) - 1), 12, 31 )]

context Patient

/* 
*  Measured population criterion: age 16-24.
*  Apply CMS153 age group identification logic to strata;
*  i.e., patients in measured population are age 16-23 at start of MeasurementPeriod 
*  and will turn 17-24 during MeasurementPeriod;
*  Stratum1 captures 16-19 y.o.'s at start of MeasurementPeriod
*  who turn 17-20 y.o. during MeasurementPeriod;
*  Stratum2 captures 20-23 y.o.'s at start of MeasurementPeriod
*  who turn 21-24 during MeasurementPeriod.
*/
define "$InAgeStratum1":
        AgeInYearsAt(start of MeasurementPeriod) >= 16
    and AgeInYearsAt(start of MeasurementPeriod) < 20

define "$InAgeStratum2":
        AgeInYearsAt(start of MeasurementPeriod) >= 20
    and AgeInYearsAt(start of MeasurementPeriod) < 24

define "$InPopulationAgeGroup":
        "$InAgeStratum1"
    or  "$InAgeStratum2"

/*
*  measured population criterion: patient must be female
*/
define "$IsFemale": 
	gender in "Patient Characteristic Sex: Female"

/*
*  measured population criterion: patient must have encounter during MeasurementPeriod
*/	
define "$HasEncounterInMeasurementPeriod": 
        exists (["Encounter, Performance": "Encounter, Performed: Office Visit"] E where E."performanceTime" during MeasurementPeriod)
    or  exists (["Encounter, Performance": "Encounter, Performed: Face-to-Face Interaction"] E where E."performanceTime" during MeasurementPeriod)
    or  exists (["Encounter, Performance": "Encounter, Performed: Preventive Care Services - Established Office Visit, 18 and Up"] E where E."performanceTime" during MeasurementPeriod)
    or  exists (["Encounter, Performance": "Encounter, Performed: Preventive Care Services - Initial Office Visit, 18 and Up"] E where E."performanceTime" during MeasurementPeriod)
    or  exists (["Encounter, Performance": "Encounter, Performed: Preventive Care Services - Established Office Visit, 0 to 17"] E where E."performanceTime" during MeasurementPeriod)
    or  exists (["Encounter, Performance": "Encounter, Performed: Preventive Care Services - Initial Office Visit, 0 to 17"] E where E."performanceTime" during MeasurementPeriod)
    or  exists (["Encounter, Performance": "Encounter, Performed: Home Healthcare Services"] E where E."performanceTime" during MeasurementPeriod)
        
/* 
*  measured population criterion: must be sexually active, which is suggested by
*  conditions/problems/diagnoses either before or during MeasurementPeriod
*/
define "$HasHadConditionOfSexualActivity":
	    exists (["Condition": "Diagnosis, Active: Other Female Reproductive Conditions"] C where C."effectiveTime" overlaps MeasurementPeriod)
	or  exists (["Condition": "Diagnosis, Active: Genital Herpes"] C where C."effectiveTime" overlaps MeasurementPeriod)
	or  exists (["Condition": "Diagnosis, Active: Genococcal Infections and Venereal Diseases"] C where C."effectiveTime" overlaps MeasurementPeriod)
	or  exists (["Condition": "Diagnosis, Active: Inflammatory Diseases of Female Reproductive Organs"] C where C."effectiveTime" overlaps MeasurementPeriod)
	or  exists (["Condition": "Diagnosis, Active: Chlamydia"] C where C."effectiveTime" overlaps MeasurementPeriod)
	or  exists (["Condition": "Diagnosis, Active: HIV"] C where C."effectiveTime" overlaps MeasurementPeriod)
	or  exists (["Condition": "Diagnosis, Active: Syphilis"] C where C."effectiveTime" overlaps MeasurementPeriod)
	or  exists (["Condition": "Diagnosis, Active: Complications of Pregnancy, Childbirth and the Puerperium"] C where C."effectiveTime" overlaps MeasurementPeriod)

/*
*  measured population criterion: must be sexually active, which is suggested by
*  OCP medication use/administration either before or during MeasurementPeriod
*/
define "$HasHadMedicationForSexualActivity":
        exists (["MedicationTreatment, Performance": "Medication, Active: Contraceptive Medications"] M where M."performanceTime" overlaps MeasurementPeriod)

/*
*  measured population criterion: must be sexually active, which is suggested by
*  OCP medication orders (prescription) during MeasurementPeriod.
*
*  N.B.: use QUICK Order class attribute orderedAtTime (dateTime)
*  rather than expectedPerformanceTime (Period)
*  for consistency and because assessment of sexual activity likely made at orderedAtTime
*  rather than in anticipation of expectedPerformanceTime, although normally there
*  should be little difference between these attribute values.
*/ 
define "$HasMedicationForSexualActivity":
        exists (["MedicationTreatment, Order": "Medication, Order: Contraceptive Medications"] M where M."orderedAtTime" during MeasurementPeriod)

/*
*  measured population criterion: must be sexually active, which is suggested by
*  specific types of laboratory test orders during MeasurementPeriod;
*
*  N.B.: use QUICK Order class attribute orderedAtTime (dateTime) 
*  rather than expectedPerformanceTime (Period)
*  for consistency and because assessment of sexual activity likely made at orderedAtTime
*  rather than in anticipation of expectedPerformanceTime, although normally there
*  should be little difference between these attribute values.
*/
define "$HasLaboratoryTestForSexualActivity":               	
	    exists (["LaboratoryTest, Order": "Laboratory Test, Order: Pap Test"] L where L."orderedAtTime" during MeasurementPeriod)
	or  exists (["LaboratoryTest, Order": "Laboratory Test, Order: Lab Tests During Pregnancy"] L where L."orderedAtTime" during MeasurementPeriod)
	or  exists (["LaboratoryTest, Order": "Laboratory Test, Order: Lab Tests for Sexually Transmitted Infections"] L where L."orderedAtTime" during MeasurementPeriod)

/*
*  pregnancy test is another test which suggests sexually active status,
*  however need to address denominator exclusion criteria:
*  exclude pregnancy tests associated with prescription for teratogenic medication
*  and certain radiologic studies (because pregnancy tests done routinely
*  on women of childbearing age regardless of sexual activity status,
*  for safety and medicolegal reasons
*/
define "$HasPregnancyTestForSexualActivity":
        exists (["LaboratoryTest, Order": "Laboratory Test, Order: Pregnancy Test"] L
                 without ["MedicationTreatment, Order": "Medication, Order: Isotretinoin"] M
                    such that M."orderedAtTime" occurs 7 days or less after L."orderedAtTime"
                 where L."orderedAtTime" during MeasurementPeriod)
    or  exists (["LaboratoryTest, Order": "Laboratory Test, Order: Pregnancy Test"] L
                 without ["DiagnosticImaging, Order": "Diagnostic Study, Order: X-Ray Study (all inclusive)"] X
                    such that X."orderedAtTime" occurs 7 days or less after L."orderedAtTime"
                 where L."orderedAtTime" during MeasurementPeriod)

/*
*  measured population criterion: must be sexually active, which is suggested by
*  specific procedures performed or ordered during MeasurementPeriod;
*  N.B.: for ordered procedure, use QUICK Order class attribute orderedAtTime (dateTime
*  rather than expectedPerformanceTime (Period)
*  for consistency and because assessment of sexual activity likely made at orderedAtTime
*  rather than in anticipation of expectedPerformanceTime, although normally there
*  should be little difference between these attribute values;
*  also N.B.: performanceTime is Period
*/
define "$HasProcedureForSexualActivity":
    exists (["Procedure, Performance": "Procedure, Performed: Delivery Live Births"] P where P."performanceTime" during MeasurementPeriod)
    or  exists (["Procedure, Performance": "Procedure, Performed: Procedures During Pregnancy"] P where P."performanceTime" during MeasurementPeriod)
    or  exists (["Procedure, Performance": "Procedure, Performed: Procedures Involving Contraceptive Devices"] P where P."performanceTime" during MeasurementPeriod)
    or  exists (["Procedure, Order": "Diagnostic Study, Order: Diagnostic Studies During Pregnancy"] O where O."orderedAtTime" during MeasurementPeriod)

/* 
*  measured population criterion: must be sexually active,
*  which is suggested by specific conditions, medications, laboratory tests, or procedures
*  either before or during MeasurementPeriod, as above
*/          
define "$HasEvidenceOfSexualActivity":
        "$HasHadConditionOfSexualActivity"
    or  "$HasHadMedicationForSexualActivity"
    or  "$HasMedicationForSexualActivity"
    or  "$HasLaboratoryTestForSexualActivity"
    or  "$HasPregnancyTestForSexualActivity"  //this accommodates denominator exclusion
    or  "$HasProcedureForSexualActivity"

/*
*  Stratum1 Patient Population = 
*  Women 16 to 20 years of age who are sexually active 
*  and who had a visit in the measurement period;
*/
define "$InStratum1Denominator":
        "$IsFemale"
    and "$InAgeStratum1"
    and "$HasEncounterInMeasurementPeriod"
    and "$HasEvidenceOfSexualActivity"

/*
*  Stratum2 Patient Population = 
*  Women 21 to 24 years of age who are sexually active 
*  and who had a visit in the measurement period
*/
define "$InStratum2Denominator":
        "$IsFemale"
    and "$InAgeStratum2"
    and "$HasEncounterInMeasurementPeriod"
    and "$HasEvidenceOfSexualActivity"

/*
*  N.B.: Initial Patient Population for measure reporting = 
*  Women 16 to 24 years of age who are sexually active 
*  and who had a visit in the measurement period and is the same as
*  (InStratum1Population  or  InStratum2Population)
*/

/*
*  for numerator criteria: 
*  identify patients with at least one resulted chlamydia test during the measurement period;
*/    
define "$HasNumeratorCriteria":
        exists (["SimpleObservationOccurence": "Laboratory Test, Result: Chlamydia Screening"] L
                 where L."observedAtTime" during MeasurementPeriod
                    and L."value" is not null)

/*
*  Numerator = Women (denominator population)
*  with at least one chlamydia test during the measurement period;
*  identify for each strata and use later for measure population
*/         
define "$InStratum1Numerator":
        "$InStratum1Denominator"
	and "$HasNumeratorCriteria"
	
define "$InStratum2Numerator":
        "$InStratum2Denominator"
    and "$HasNumeratorCriteria"

context POPULATION

/*
*  calculate measure denominators and numerators
*/
define "$Stratum1Denominator": Count(Patient P where "$InStratum1Denominator")
define "$Stratum1Numerator": Count(Patient P where "$InStratum1Numerator")

define "$Stratum2Denominator": Count(Patient P where "$InStratum2Denominator")
define "$Stratum2Numerator": Count(Patient P where "$InStratum2Numerator")

define "$MeasureDenominator": "$Stratum1Denominator" + "$Stratum2Denominator"
define "$MeasureNumerator": "$Stratum1Numerator"   + "$Stratum2Numerator"

/*
*  calculate measure scores
*/
define "$Stratum1Score": Round( ("$Stratum1Numerator" / "$Stratum1Denominator") * 100 )
define "$Stratum2Score": Round( ("$Stratum2Numerator" / "$Stratum2Denominator") * 100 )
define "$MeasureScore": Round( ("$MeasureNumerator"  / "$MeasureDenominator")  * 100 )

