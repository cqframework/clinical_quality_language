library CMS153 version '2'

using QUICK

context PATIENT

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

let otherFemaleReproductiveConditions = [Condition: "Other Female Reproductive Conditions"]
let genitalHerpes = [Condition: "Genital Herpes"]
let genococcalInfectionsAndVenerealDiseases = [Condition: "Genococcal Infections and Venereal Diseases"]
let inflammatoryDiseasesOfFemaleReproductiveOrgans = [Condition: "Inflammatory Diseases of Female Reproductive Organs"]
let chlamydia = [Condition: "Chlamydia"]
let hiv = [Condition: "HIV"]
let syphilis = [Condition: "Syphilis"]
let complicationsOfPregnancyChildbirthAndPuerperium = [Condition: "Complications of Pregnancy, Childbirth and the Puerperium"]
let pregnancy = [ObservationResult: "Pregnancy"]
let pap = [ObservationResult: "Pap"]
let labTestsDuringPregnancy = [ObservationResult: "Lab Tests During Pregnancy"]
let labTestsForSexuallyTransmittedInfections = [ObservationResult: "Lab Tests for Sexually Transmitted Infections"]
let chlamydiaScreening = [ObservationResult: "Chlamydia Screening"]
let reasonForNoScreening = [ObservationResult: "Reason for not performing Chlamydia Screening"]

let conditionsIndicatingSexualActivity =
    union(
        otherFemaleReproductiveConditions,
        genitalHerpes,
        genococcalInfectionsAndVeneralDiseases,
        inflammatoryDiseasesOfFemaleReproductiveOrgans,
        chlamydia,
        hiv,
        syphilis,
        complicationsOfPregnancyChildbirthAndPuerperium
    )

let resultsIndicatingSexualActivity =
    union(
        pregnancy,
        pap,
        labTestsDuringPregnancy,
        labTestsForSexuallyTransmittedInfections
    )

