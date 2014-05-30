using QLIM

context PATIENT

concept "Other Female Reproductive Conditions" = ValueSet("2.1...")

let InDemographic = 
	Age() >= 16 and Age() < 24 and Gender = "female"

let SexuallyActive =
	exists ([Condition, Present: "Other Female Reproductive Conditions"])
	or exists ([Condition, Present: "Genital Herpes"])
	or exists ([Condition, Present: "Genococcal Infections and Venereal Diseases"])
	or exists ([Condition, Present: "Inflammatory Diseases of Female Reproductive Organs"])
	or exists ([Condition, Present: "Chlamydia"])
	or exists ([Condition, Present: "HIV"])
	or exists ([Condition, Present: "Syphilis"])
	or exists ([Condition, Present: "Complications of Pregnancy, Childbirth and the Puerperium"])
	or exists ([LabResult, Present: "Pregnancy"])
	or exists ([LabResult, Present: "Pap"])
	or exists ([LabResult, Present: "Lab Tests During Pregnancy"])
	or exists ([LabResult, Present: "Lab Tests for Sexually Transmitted Infections"])

let NoScreening =
	not exists ([LabResult, Present: "Chlamydia Screening"] where EffectiveTime during interval[today - 1 years, now])
	exists (combine LabResults, Procedures where LabResults.EffectiveTime starts within 3 days before start Procedures.EffectiveTime)
	and not exists ([Procedure, Planned: "Chlamydia Screening"] where EffectiveTime >= now)
	and not exists ([ObservationResult, Present: "Reason for not performing Chlamydia Screening"])

let NeedScreening = InDemographic and SexuallyActive and NoScreening
