using QLIM

context PATIENT

concept "Other Female Reproductive Conditions" = ValueSet("2.16.840.1.113883.3.464.1003.111.12.1006")
concept "Genital Herpes" = ValueSet("2.16.840.1.113883.3.464.1003.110.12.1049")
concept "Genococcal Infections and Venereal Diseases" = ValueSet("2.16.840.1.113883.3.464.1003.112.12.1001")
concept "Inflammatory Diseases of Female Reproductive Organs" = ValueSet("2.16.840.1.113883.3.464.1003.112.12.1004")
concept "Chlamydia" = ValueSet("2.16.840.1.113883.3.464.1003.112.12.1003")
concept "HIV" = ValueSet("2.16.840.1.113883.3.464.1003.120.12.1003")
concept "Syphilis" = ValueSet("2.16.840.1.113883.3.464.1003.112.12.1002")
concept "Complications of Pregnancy, Childbirth and the Puerperium" = ValueSet("2.16.840.1.113883.3.464.1003.111.12.1012")
concept "Pregnancy Test" = ValueSet("2.16.840.1.113883.3.464.1003.111.12.1011")
concept "Pap Test" = ValueSet("2.16.840.1.113883.3.464.1003.108.12.1017")
concept "Lab Tests During Pregnancy" = ValueSet("2.16.840.1.113883.3.464.1003.111.12.1007")
concept "Lab Tests for Sexually Transmitted Infections" = ValueSet("2.16.840.1.113883.3.464.1003.110.12.1051")
concept "Chlamydia Screening" = ValueSet("2.16.840.1.113883.3.464.1003.110.12.1052")
concept "Reason for not performing Chlamydia Screening" = ValueSet("TBD") // Not used in the measure, need a value set for this

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
	not exists ([ObservationResult: "Chlamydia Screening"] where effectiveTime during interval[today - 1 years, now])
	and not exists ([Procedure, Planned: "Chlamydia Screening"] where effectiveTime >= now)
	and not exists ([ObservationResult: "Reason for not performing Chlamydia Screening"])

let NeedScreening = InDemographic and SexuallyActive and NoScreening

