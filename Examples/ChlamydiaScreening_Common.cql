library CMS153_Common version '2'

using QUICK

define "Female Administrative Sex" = ValueSet('2.16.840.1.113883.3.560.100.2')
define "Other Female Reproductive Conditions" = ValueSet('2.16.840.1.113883.3.464.1003.111.12.1006')
define "Genital Herpes" = ValueSet('2.16.840.1.113883.3.464.1003.110.12.1049')
define "Genococcal Infections and Venereal Diseases" = ValueSet('2.16.840.1.113883.3.464.1003.112.12.1001')
define "Inflammatory Diseases of Female Reproductive Organs" = ValueSet('2.16.840.1.113883.3.464.1003.112.12.1004')
define "Chlamydia" = ValueSet('2.16.840.1.113883.3.464.1003.112.12.1003')
define "HIV" = ValueSet('2.16.840.1.113883.3.464.1003.120.12.1003')
define "Syphilis" = ValueSet('2.16.840.1.113883.3.464.1003.112.12.1002')
define "Complications of Pregnancy, Childbirth and the Puerperium" = ValueSet('2.16.840.1.113883.3.464.1003.111.12.1012')
define "Pregnancy Test" = ValueSet('2.16.840.1.113883.3.464.1003.111.12.1011')
define "Pap Test" = ValueSet('2.16.840.1.113883.3.464.1003.108.12.1017')
define "Lab Tests During Pregnancy" = ValueSet('2.16.840.1.113883.3.464.1003.111.12.1007')
define "Lab Tests for Sexually Transmitted Infections" = ValueSet('2.16.840.1.113883.3.464.1003.110.12.1051')
define "Chlamydia Screening" = ValueSet('2.16.840.1.113883.3.464.1003.110.12.1052')

context PATIENT

define ConditionsIndicatingSexualActivity =
	([Condition: "Other Female Reproductive Conditions"])
	union ([Condition: "Genital Herpes"])
    union ([Condition: "Genococcal Infections and Venereal Diseases"])
    union ([Condition: "Inflammatory Diseases of Female Reproductive Organs"])
    union ([Condition: "Chlamydia"])
    union ([Condition: "HIV"])
    union ([Condition: "Syphilis"])
    union ([Condition: "Complications of Pregnancy, Childbirth and the Puerperium"])

define LaboratoryTestsIndicatingSexualActivity =
    ([LaboratoryTest, Order: "Pregnancy Test"])
    union ([LaboratoryTest, Order: "Pap Test"])
    union ([LaboratoryTest, Order: "Lab Tests During Pregnancy"])
	union ([LaboratoryTest, Order: "Lab Tests for Sexually Transmitted Infections"])

define TestsPerformedForChlamydia =
	[Procedure, Performance: "Chlamydia Screening"]

define ResultsPresentForChlamydiaScreening =
    [ObservationResult: "Chlamydia Screening"] R where R.result is not null


