library CMS153 version '2'

using QUICK

valueset "Female Administrative Sex" = ValueSet('2.16.840.1.113883.3.560.100.2')
valueset "Other Female Reproductive Conditions" = ValueSet('2.16.840.1.113883.3.464.1003.111.12.1006')
valueset "Genital Herpes" = ValueSet('2.16.840.1.113883.3.464.1003.110.12.1049')
valueset "Genococcal Infections and Venereal Diseases" = ValueSet('2.16.840.1.113883.3.464.1003.112.12.1001')
valueset "Inflammatory Diseases of Female Reproductive Organs" = ValueSet('2.16.840.1.113883.3.464.1003.112.12.1004')
valueset "Chlamydia" = ValueSet('2.16.840.1.113883.3.464.1003.112.12.1003')
valueset "HIV" = ValueSet('2.16.840.1.113883.3.464.1003.120.12.1003')
valueset "Syphilis" = ValueSet('2.16.840.1.113883.3.464.1003.112.12.1002')
valueset "Complications of Pregnancy, Childbirth and the Puerperium" = ValueSet('2.16.840.1.113883.3.464.1003.111.12.1012')
valueset "Pregnancy Test" = ValueSet('2.16.840.1.113883.3.464.1003.111.12.1011')
valueset "Pap Test" = ValueSet('2.16.840.1.113883.3.464.1003.108.12.1017')
valueset "Lab Tests During Pregnancy" = ValueSet('2.16.840.1.113883.3.464.1003.111.12.1007')
valueset "Lab Tests for Sexually Transmitted Infections" = ValueSet('2.16.840.1.113883.3.464.1003.110.12.1051')
valueset "Chlamydia Screening" = ValueSet('2.16.840.1.113883.3.464.1003.110.12.1052')

context PATIENT

define otherFemaleReproductiveConditions = [Condition: "Other Female Reproductive Conditions"]
define genitalHerpes = [Condition: "Genital Herpes"]
define genococcalInfectionsAndVenerealDiseases = [Condition: "Genococcal Infections and Venereal Diseases"]
define inflammatoryDiseasesOfFemaleReproductiveOrgans = [Condition: "Inflammatory Diseases of Female Reproductive Organs"]
define chlamydia = [Condition: "Chlamydia"]
define hiv = [Condition: "HIV"]
define syphilis = [Condition: "Syphilis"]
define complicationsOfPregnancyChildbirthAndPuerperium = [Condition: "Complications of Pregnancy, Childbirth and the Puerperium"]
define pregnancy = [ObservationResult: "Pregnancy Test"]
define pap = [ObservationResult: "Pap Test"]
define labTestsDuringPregnancy = [ObservationResult: "Lab Tests During Pregnancy"]
define labTestsForSexuallyTransmittedInfections = [ObservationResult: "Lab Tests for Sexually Transmitted Infections"]
define chlamydiaScreening = [ObservationResult: "Chlamydia Screening"]
define reasonForNoScreening = [ObservationResult: "Reason for not performing Chlamydia Screening"]

define conditionsIndicatingSexualActivity =
    otherFemaleReproductiveConditions
        union genitalHerpes
        union genococcalInfectionsAndVeneralDiseases
        union inflammatoryDiseasesOfFemaleReproductiveOrgans
        union chlamydia
        union hiv
        union syphilis
        union complicationsOfPregnancyChildbirthAndPuerperium

define resultsIndicatingSexualActivity =
    pregnancy
        union pap
        union labTestsDuringPregnancy
        union labTestsForSexuallyTransmittedInfections

