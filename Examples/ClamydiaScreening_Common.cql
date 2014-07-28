library CMS153_Common version '2'

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

let ConditionsIndicatingSexualActivity =
	[Condition: "Other Female Reproductive Conditions"]
	union [Condition: "Genital Herpes"]
    union [Condition: "Genococcal Infections and Venereal Diseases"]
    union [Condition: "Inflammatory Diseases of Female Reproductive Organs"]
    union [Condition: "Chlamydia"]
    union [Condition: "HIV"]
    union [Condition: "Syphilis"]
    union [Condition: "Complications of Pregnancy, Childbirth and the Puerperium"]

let LaboratoryTestsIndicatingSexualActivity =
    [LaboratoryTest, Order: "Pregnancy"]
    union [LaboratoryTest, Order: "Pap"]
    union [LaboratoryTest, Order: "Lab Tests During Pregnancy"]
	union [LaboratoryTest, Order: "Lab Tests for Sexually Transmitted Infections"]

let TestsPerformedForChlamydia =
	[Procedure, Performance: "Chlamydia Screening"]

let ResultsPresentForChlamydiaScreening = 
    [ObservationResult: "Chlamydia Screening"] R where R.result is not null


