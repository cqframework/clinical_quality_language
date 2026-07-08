library ChlamydiaScreeningCDS version '1'
/*
*  CDS implementation based on USPSTF recommendation:
*  screen for chlamydia (and gonorrhea) in sexually active women age 24 and younger,
*  and in older women at increased risk for infection;
*  applies to all sexually active adolescents and adults, including pregnant women;
*  age is strongest risk factor with age risk strata 20-24 > age 15-19;
*  other risk factors include: 
*    new sex partner, more than one sex partner,
*    sex partner with STI, inconsistent condom use, h/o previous STI, 
*    exchanging sex for money or drugs, incarceration, military recruits,
*    and patients receiving care at public STI clinic;
*  optimal screening interval for non-pregnant women not known 
*  but recommendation is to screen when risk factors change; 
*  CDC recommends at least annual screening for women at increased risk; 
*  pregnant women should be screened at first prenatal visit 
*  and again in 3rd trimester for those who acquire a new risk factor
*
*  can reuse some criteria from measure NQF33/CMS153v3, July 2014 EP;
*
*  identify women 16-24 years of age 
*      with evidence of sexual activity 
*      and who have not had at least one test for chlamydia in the past year
*      to recommend chlamydia screening
*
*  identify women of any age
*      at increased risk of infection based upon 
*      non-age based STI risk factors new or present in past year:
*        new sex partner, more than 1 sex partner,
*        sex partner with STI, personal h/o STI,
*        h/o prostitution, h/o incarceration, 
*        active military status, h/o care at public STI clinic
*      to recommend chlamydia screening 
*    
*  identify pregnant women to check for initial prenatal screening chlamydia test
*      to recommend chlamydia screen if not present
*      and recommend 3rd trimester screening if new risk factor present
*  
*  Assume CDS is run on-demand for patient-specific recommendations
*/ 

using QUICK


/*
*  value set names listed in alphabetical order
*  grouped by QDM data model use
*/ 
valueset "Diagnosis, Active: Chlamydia" = '2.16.840.1.113883.3.464.1003.112.12.1003'
valueset "Diagnosis, Active: Complications of Pregnancy, Childbirth and the Puerperium" = '2.16.840.1.113883.3.464.1003.111.12.1012'
valueset "Diagnosis, Active: Genital Herpes" = '2.16.840.1.113883.3.464.1003.110.12.1049'
valueset "Diagnosis, Active: Genococcal Infections and Venereal Diseases" = '2.16.840.1.113883.3.464.1003.112.12.1001'
valueset "Diagnosis, Active: HIV" = '2.16.840.1.113883.3.464.1003.120.12.1003'
valueset "Diagnosis, Active: Inflammatory Diseases of Female Reproductive Organs" = '2.16.840.1.113883.3.464.1003.112.12.1004'
valueset "Diagnosis, Active: Other Female Reproductive Conditions" = '2.16.840.1.113883.3.464.1003.111.12.1006'
valueset "Diagnosis, Active: Syphilis" = '2.16.840.1.113883.3.464.1003.112.12.1002'

valueset "Diagnostic Study, Order: Diagnostic Studies During Pregnancy" = '2.16.840.1.113883.3.464.1003.111.12.1008'
valueset "Diagnostic Study, Order: X-Ray Study (all inclusive)" = '2.16.840.1.113883.3.464.1003.198.12.1034'

valueset "Laboratory Test, Order: Lab Tests During Pregnancy" = '2.16.840.1.113883.3.464.1003.111.12.1007'
valueset "Laboratory Test, Order: Lab Tests for Sexually Transmitted Infections" = '2.16.840.1.113883.3.464.1003.110.12.1051'
valueset "Laboratory Test, Order: Pap Test" = '2.16.840.1.113883.3.464.1003.108.12.1017'
valueset "Laboratory Test, Order: Pregnancy Test" = '2.16.840.1.113883.3.464.1003.111.12.1011'

valueset "Laboratory Test, Result: Chlamydia Screening" = '2.16.840.1.113883.3.464.1003.110.12.1052'

valueset "Medication, Active: Contraceptive Medications" = '2.16.840.1.113883.3.464.1003.196.12.1080'

valueset "Medication, Order: Contraceptive Medications" = '2.16.840.1.113883.3.464.1003.196.12.1080'
valueset "Medication, Order: Isotretinoin" = '2.16.840.1.113883.3.464.1003.196.12.1143'

valueset "Patient Characteristic Birthdate: birth date" = '2.16.840.1.113883.3.560.100.4'
valueset "Patient Characteristic Sex: Female" = '2.16.840.1.113883.3.560.100.2'

valueset "Procedure, Performed: Delivery Live Births" = '2.16.840.1.113883.3.464.1003.111.12.1015'
valueset "Procedure, Performed: Procedures During Pregnancy" = '2.16.840.1.113883.3.464.1003.111.12.1009'
valueset "Procedure, Performed: Procedures Involving Contraceptive Devices" = '2.16.840.1.113883.3.464.1003.111.12.1010'

/* 
*  new value sets needed for CDS, listed below
*/

/*
*  Need new value set for documentation of sexual activity;
*  used to answer the question directly: is this person sexually active?
*  values may be recorded as part of sexual history
*
*  UMLS metathesaurus search reveals "sexually active" terminology;
*  many SNOMEDCT_US terms to document/describe sexual activity.
*  LOINC has "HEDIS 2014 Value Set - Sexually Active" (details not clear)
*     --> cannot get access to this at NCQA -- charge for proprietary documentation
*     --> is documented to be based on claims data, likely repeated in CMS measure criteria;
*  other LOINC codes related to sexual history/form.
*  MEDCIN has terms for documenting sexual activity including "sexual activity denied."
*  MSH has "sex behavior" as more general term including sexual activity.
*/
valueset "Patient Characteristic Sexual Activity: sexually active" = 'TO BE DEFINED-1'

/*
*  Need new value set for standard birth-control indication for OCP -- for pregnancy prevention
*  found this concept in UMLS RSAB ICNP (nursing practice), cannot find appropriate birth control
*  or pregnancy prevention concept in SNOMEDCT_US;
*
*    below suggested valueset name gives concept, skips state (action/being), uses attribute=Reason
*/
valueset "Medication, Reason: Contraceptive for Pregnancy Prevention" = 'TO BE DEFINED-2'

/*
*  Need new value set for non-birth control conditions for which OCPs are prescribed;
*  these are conditions which can be treated by OCP for menstural cycle regulation;
*  if these conditions are present, we may not be able to infer sexual activity;
*  conditions for which OCP used for menstrual cycle regulation include:
*    menorrhagia, metrorrhagia, menometrorrhagia, amenorrhia,
*    dysmenorrhea, premenstrual dysphoric disorder, premenstrual syndrome (PMS),
*    endometriosis, adenomyosis, polycystic ovary disease, acne
*
*  ? also specifiy as valueset "Medication, Reason: Non-Birth Control Uses for Oral Contraceptive Pill" ?
*/
valueset "Diagnosis, Active: Non-Birth Control Uses for Oral Contraceptive Pill" = 'TO BE DIFINED-3'

/*
*  Need new value set for non-age related STI/chlamydia risk factors:
*    new sex partner, more than 1 sex partner,
*    sex partner with STI, personal h/o STI,
*    h/o prostitution, h/o incarceration, military recruit,
*    h/o care at public STI clinic
*
*   ? Risk Evaluation is the correct QDM concept?  Document is best QDM state of action?
*/
valueset "Risk Evaluation, Document: Sexually Transmitted Infection" = 'TO BE DEFINED-4'



context Patient

/*
*  population criterion: patient must be female
*/
define IsFemale =
	Patient.gender in "Patient Characteristic Sex: Female"

/* 
*  identify at-risk age group members: age 16-24 at time of CDS
*/
define AtRiskByAgeGroup =
        AgeInYears() >= 16
    and AgeInYears() <= 24

/*
*  eligible at-risk person must be sexually active;
*  look for direct record of sexual activity;
*  will require use of new value set
*/ 
define DocumentedSexuallyActive =
        exists ([Condition: "Patient Characteristic Sexual Activity: sexually active"])
    or  exists ([Observation: "Patient Characteristic Sexual Activity: sexually active"])


/* 
*  other than via a direct record of sexual function status or sexual activity,
*  a person may have evidence of sexual activity suggested by
*  specified prior or active conditions/problems/diagnoses
*  as per CMS153
*/
define HasHadConditionOfSexualActivity =
	    exists ([Condition: "Diagnosis, Active: Other Female Reproductive Conditions"]) // infertility
	or  exists ([Condition: "Diagnosis, Active: Genital Herpes"]) 
	or  exists ([Condition: "Diagnosis, Active: Genococcal Infections and Venereal Diseases"]) 
	or  exists ([Condition: "Diagnosis, Active: Inflammatory Diseases of Female Reproductive Organs"]) 
	or  exists ([Condition: "Diagnosis, Active: Chlamydia"]) 
	or  exists ([Condition: "Diagnosis, Active: HIV"])
	or  exists ([Condition: "Diagnosis, Active: Syphilis"])
	or  exists ([Condition: "Diagnosis, Active: Complications of Pregnancy, Childbirth and the Puerperium"])


/*
*  sexual activity is also evidenced by a pregnancy test;
*
*  however need to also consider exclusion criteria from CMS153 ("denominator exclusion")
*  which says that pregnancy tests done a short time before specific xray and medication orders
*  cannot be used as evidence of sexual activity  
*  (because pregnancy test is done routinely and for safety and medicolegal reasons 
*  prior to initiating teratogenic medications and prior to certain radiologic examinations
*  in any woman of childbearing age regardless of stated sexual activity).
*
*  The logic below seeks to remove pregnancy tests with an associated exclusion action 
*  (i.e. medication or imaging order), and determine if any pregnancy tests are left
*  which might validly suggest sexual activity (pregnancy tests done for
*  reasons other than radiologic imaging or initiating therapy with a teratogen)
*
*  N.B.: use QUICK Order class attribute orderedAtTime rather than expectedPerformanceTime
*  for consistency and because assessment of sexual activity likely made at orderedAtTime
*  rather than in anticipation of expectedPerformanceTime, although normally there
*  should be little difference between these attribute values.
*
*  Also for Procedures, is it reasonable to assume that all Performances are associated with
*  or preceded by an Order?  Likewise, is it reasonable to assume that all Orders
*  are associated with or succeeded by a Performance?  Is it reasonable to
*  assume that Orders persist once the Performance has occurred?
*  For now, will check both Order and Performance for most Procedures.
*/
define HasHadPregnancyTestForSexualActivity =
        exists ([DiagnosticOrder: "Laboratory Test, Order: Pregnancy Test"] L
                 without [MedicationPrescription: "Medication, Order: Isotretinoin"] M
                 such that date from M.orderedAtTime same day or after date from L.orderedAtTime
                   and date from M.orderedAtTime 7 days or less after date from L.orderedAtTime)
	or  exists ([DiagnosticOrder: "Laboratory Test, Order: Pregnancy Test"] L
	             without [DiagnosticOrder: "Diagnostic Study, Order: X-Ray Study (all inclusive)"] X
	             such that date from X.orderedAtTime same day or after date from L.orderedAtTime
	               and date from X.orderedAtTime 7 days or less after date from L.orderedAtTime)


/* 
*  sexual activity is also evidenced by
*  other specific types of laboratory tests ordered or performed
*  as per CMS153;
*
*/
	
define HasHadOtherLabTestForSexualActivity =
	    exists ([DiagnosticOrder: "Laboratory Test, Order: Pap Test"])
	or  exists ([DiagnosticOrder: "Laboratory Test, Order: Lab Tests During Pregnancy"])
	or  exists ([DiagnosticOrder: "Laboratory Test, Order: Lab Tests for Sexually Transmitted Infections"])


/*
*  sexual activity is also evidenced by specific procedures ordered or performed 
*
*/
define HasHadProcedureForSexualActivity =
        exists ([Procedure: "Procedure, Performed: Delivery Live Births"])
    or  exists ([Procedure: "Procedure, Performed: Procedures During Pregnancy"])
    or  exists ([Procedure: "Procedure, Performed: Procedures Involving Contraceptive Devices"])
    or  exists ([Procedure: "Diagnostic Study, Order: Diagnostic Studies During Pregnancy"])


/*
*  sexual activity is also evidenced by
*  specified prior or active OCP medication order or administration (performance);
*  standard practice would be to assume use of OCP is for birth control
*  and specific indication often not provided with Rx
*/
define ReceivedOCP =
        exists ([MedicationPrescription: "Medication, Active: Contraceptive Medications"])
    or  exists ([MedicationTreatment: "Medication, Active: Contraceptive Medications"])

/* 
*  detect if OCP prescribed with specific indication for birth control or pregnancy prevention
*/
define ReceivedOCP_ForBirthControl =
        exists ([MedicationPrescription: "Medication, Active: Contraceptive Medications"] M
                where exists (M.indication I
                              where I.reason in "Medication, Reason: Contraceptive for Pregnancy Prevention"))
    or  exists ([MedicationTreatment: "Medication, Active: Contraceptive Medications"] M
                where exists (M.indication I
                              where I.reason in "Medication, Reason: Contraceptive for Pregnancy Prevention"))

/*
*  OCP use may be for other reasons than birth control, so
*  check if OCP used for non-birth control reason as recorded by a medication "indication";
*  non-birth control reasons are related to menstrual cycle regulation; i.e. for
*    menorrhagia, metrorrhagia, menometrorrhagia, amenorrhia,
*    dysmenorrhea, premenstrual dysphoric disorder, premenstrual syndrome (PMS),
*    endometriosis, adenomyosis, polycystic ovary disease, acne;
*
*  many women use OCP for birth control + menstrual regulation disorder,
*  so cannot assume pt is not sexually active based on presense of non-birth control indication alone;
*  fewer women are on OCP for menstrual regulation disorder alone
*
*  need to be careful with logic, if possible
*  attempt to identify birth control uses if present
*  and not invalidate use of OCP (and therefore sexual activity status)
*  based upon non-birth control indication alone, unless appropriate;
*/
// separate modality, use order and performance
//
// which approach to use?
//  detect presense of OCP use with indication for menstrual cycle regulation?
define ReceivedOCP_ForMenstrualRegulation =
        exists ([MedicationPrescription: "Medication, Active: Contraceptive Medications"] M
                where exists (M.indication I
                              where I.reason in "Diagnosis, Active: Non-Birth Control Uses for Oral Contraceptive Pill"))

//  or detect OCP use with indication other than menstrual cycle regulation?                  
define ReceivedOCP_NotForMenstrualRegulation =
        exists ([MedicationPrescription: "Medication, Active: Contraceptive Medications"] M
                where exists (M.indication I 
                              where I.reason is not null
                                and not I.reason in "Diagnosis, Active: Non-Birth Control Uses for Oral Contraceptive Pill"))

// alternative method -- OCP use with indication other than menstrual cycle regulation?
//   but detailed list processing specified
// cannot get this to parse, it is rejecting the alias C with error: "no viable alternative at input" reference "C"
//  also describes mismatched input ')' expecting EOF/except/* etc.
//  does not tolerate this level nesting?
/*
define ReceivedOCP_NotForMenstrualRegulation =
        exists ([MedicationPrescription: "Medication, Active: Contraceptive Medications"] M
                where exists (M.indication I where I.reason not in "Diagnosis, Active: ...")

                              where exists (I.reason.coding C
                                            where C.code is not null
                                              and not C.code in "Diagnosis, Active: Non-Birth Control Uses for Oral Contraceptive Pill"))) 
*/


/*
*  condition for non-birth control use of OCP may be recorded independent of med indication;
*  if present, then timing of medication should correspond to time of diagnosis;
*  
*  this logic assumes that OCP prescribed within 1 year of diagnosis (of condition requiring
*  menstrual cycle regulation) for menstrual cycle regulation only and not for birth control;
*  there will be no way to know if prescribed for both indications unless indications are
*  specifically given and captured above
*/
define ReceivedOCPwithMensturalCycleCondition =
            exists ([MedicationTreatment: "Medication, Active: Contraceptive Medications"] M
                     with [Condition: "Non-Birth Control Uses for Oral Contraceptive Pill"] C
                     such that start of M.performanceTime 1 year or less after start of C.effectiveTime)
        or  exists ([MedicationPrescription: "Medication, Active: Contraceptive Medications"] M
                     with [Condition: "Non-Birth Control Uses for Oral Contraceptive Pill"] C
                     such that start of M.orderedAtTime 1 year or less after start of C.effectiveTime)
  


/* 
*  measured population criterion: look for evidence of sexual activity
*  which is suggested by specific conditions, medications, laboratory tests, or procedures
*  either before or during MeasurementPeriod, as above
*
*/    
define HasEvidenceOfSexualActivity =
        DocumentedSexuallyActive
    or  HasHadConditionOfSexualActivity
    or  HasHadPregnancyTestForSexualActivity
    or  HasHadOtherLabTestForSexualActivity
    or  HasHadProcedureForSexualActivity
    or  ReceivedOCP_ForBirthControl
    or  ReceivedOCP_NotForMenstrualRegulation
    or  (ReceivedOCP 
         and not (ReceivedOCP_ForMenstrualRegulation or ReceivedOCPwithMenstrualCycleCondition))


/*
*  identify women (age irrelevant) at increased risk of STI/chlamydia infection based upon:
*      new sex partner, more than 1 sex partner in past year,
*      sex partner with STI in past 1 year, personal h/o STI in past year,
*      h/o prostitution, h/o incarceration, active military status < 1 year,
*      h/o care at public STI clinic in past year
*       to recommend chlamydia screening 
*  for exposure -- is primary attribute Exposure.stimulus?  Exposure.action.
*  N.B. ObservationResult with assumed "Occurence" (StatementOfOccurence) and
*       assumed modality "Observation" is the same as class SimpleObservationOccurrence;
*       should use compositional class or hierarchical class with assumed Occurence/Modality?
*/   
define HasSTIriskFactor =
        exists ([Condition: "Risk Evaluation, Document: Sexually Transmitted Infection"] C
                 where C.effectiveTime starts 1 year or less before today())
    or  exists ([Observation: "Risk Evaluation, Document: Sexually Transmitted Infection"] O
                 where O.observedAtTime starts 1 year or less before today())
     
/*
*  has pt had a resulted chlamydia test in past year?: 
*
*  use ObservationResult with assumed Occurrence and modality, or use SimpleObservationOccurence ?
*/    
define HasChlamydiaTest =
        exists ([DiagnosticReport: "Laboratory Test, Result: Chlamydia Screening"] O
                 where O.observedAtTime starts 1 year or less before today()
                   and O.value is not null)


/*
*  Initial at-risk Patient Population = 
*  Women 16 to 24 years of age who have evidence of sexual activity 
*
*  problem here if false negatives -- failure of automated identification of sexual activity
*
*  if no evidence of sexual activity, consider verification
*  even if evidence of sexual activity, consider verification from provider
*  consider structured documentation form for sexual history <-- first KA before CDS
*/
define InAtRiskAgePopulation =
        IsFemale
    and AtRiskByAgeGroup
    and HasEvidenceOfSexualActivity
    and not HasChlamydiaTest

define InOtherAtRiskPopulation =
        HasSTIriskFactor
    and not HasChlamydiaTest

define InAgeAndOtherAtRiskPopulation =
        InAtRiskAgePopulation 
    and InOtherAtRiskPopulation
    and not HasChlamydiaTest
    
define AtRiskByAgeMessage =
    tuple {message: 'Patient is at risk for chlamydia infection by age group, no screening test available in past 1 year'}
    as CommunicationRequest

// the LOINC codes in value set "Laboratory Test, Result: Chlamydia Screening" are used for both orders and observations
define ChlamydiaScreeningTestProposal =
    tuple {procedureCode: "Laboratory Test, Result: Chlamydia Screening"} 
    as ProcedureRequest
    
