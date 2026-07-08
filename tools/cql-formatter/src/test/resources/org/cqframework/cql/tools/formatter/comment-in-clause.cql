define "test":
	["Medication, Order": "Dapsone and pyrimethamine"] MO
	// some comment
		  let var: 2

define "Active Medications for Androgen deprivation therapy for Urology Care":
  ["Medication, Active": "Androgen deprivation therapy for Urology Care"] ADT
    with "Prostate Cancer Diagnosis" ProstateCancerDx // comments within a clause
      such that /* multi-line within a line */ ADT.relevantPeriod starts on or after start ProstateCancerDx.prevalencePeriod
    with ["Procedure, Order": "Injection Leuprolide Acetate"] ADTOrder /* multi-line comments
        within a clause */ // followed by a comment
      such that ADT.relevantPeriod includes ADTOrder.authorDatetime

define "Active Medications for Androgen deprivation therapy for Urology Care":
  ["Medication, Active": "Androgen deprivation therapy for Urology Care"] ADT with "Prostate Cancer Diagnosis" ProstateCancerDx // comments within a clause
      such that /* multi-line within a line */ ADT.relevantPeriod starts on or after start ProstateCancerDx.prevalencePeriod with ["Procedure, Order": "Injection Leuprolide Acetate"] ADTOrder /* multi-line comments
        within a clause */
        // followed by a comment
      such that ADT.relevantPeriod includes ADTOrder.authorDatetime