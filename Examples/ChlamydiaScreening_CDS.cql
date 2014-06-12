using QLIM

context PATIENT

valueset "Other Female Reproductive Conditions" = ValueSet("2.16.840.1.113883.3.464.1003.111.12.1006")
valueset "Genital Herpes" = ValueSet("2.16.840.1.113883.3.464.1003.110.12.1049")
valueset "Genococcal Infections and Venereal Diseases" = ValueSet("2.16.840.1.113883.3.464.1003.112.12.1001")
valueset "Inflammatory Diseases of Female Reproductive Organs" = ValueSet("2.16.840.1.113883.3.464.1003.112.12.1004")
valueset "Chlamydia" = ValueSet("2.16.840.1.113883.3.464.1003.112.12.1003")
valueset "HIV" = ValueSet("2.16.840.1.113883.3.464.1003.120.12.1003")
valueset "Syphilis" = ValueSet("2.16.840.1.113883.3.464.1003.112.12.1002")
valueset "Complications of Pregnancy, Childbirth and the Puerperium" = ValueSet("2.16.840.1.113883.3.464.1003.111.12.1012")
valueset "Pregnancy Test" = ValueSet("2.16.840.1.113883.3.464.1003.111.12.1011")
valueset "Pap Test" = ValueSet("2.16.840.1.113883.3.464.1003.108.12.1017")
valueset "Lab Tests During Pregnancy" = ValueSet("2.16.840.1.113883.3.464.1003.111.12.1007")
valueset "Lab Tests for Sexually Transmitted Infections" = ValueSet("2.16.840.1.113883.3.464.1003.110.12.1051")
valueset "Chlamydia Screening" = ValueSet("2.16.840.1.113883.3.464.1003.110.12.1052")
valueset "Reason for not performing Chlamydia Screening" = ValueSet("TBD") // Not used in the measure, need a value set for this

let InDemographic = 
	Age() >= 16 and Age() < 24 and Gender = "female"

let SexuallyActive =
	exists ([Condition: "Other Female Reproductive Conditions"])
	or exists ([Condition: "Genital Herpes"])
	or exists ([Condition: "Genococcal Infections and Venereal Diseases"])
	or exists ([Condition: "Inflammatory Diseases of Female Reproductive Organs"])
	or exists ([Condition: "Chlamydia"])
	or exists ([Condition: "HIV"])
	or exists ([Condition: "Syphilis"])
	or exists ([Condition: "Complications of Pregnancy, Childbirth and the Puerperium"])
	or exists ([ObservationResult: "Pregnancy Test"])
	or exists ([ObservationResult: "Pap Test"])
	or exists ([ObservationResult: "Lab Tests During Pregnancy"])
	or exists ([ObservationResult: "Lab Tests for Sexually Transmitted Infections"])
		
let NoScreening =
	not exists ([ObservationResult: "Chlamydia Screening", during interval[today - 1 years, today]])
	and not exists ([Procedure, Planned: "Chlamydia Screening", during interval[today, null]])
	and not exists ([ObservationResult: "Reason for not performing Chlamydia Screening"])

let NeedScreening = InDemographic and SexuallyActive and NoScreening

