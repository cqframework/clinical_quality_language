using QLIM

context PATIENT

valueset "Other Female Reproductive Conditions" = ValueSet("2.1...")

let conditionsIndicatingSexualActivity =
    union(
        [Condition: "Other Female Reproductive Conditions"],
        [Condition: "Genital Herpes"],
        [Condition: "Genococcal Infections and Venereal Diseases"],
        [Condition: "Inflammatory Diseases of Female Reproductive Organs"],
        [Condition: "Chlamydia"],
        [Condition: "HIV"],
        [Condition: "Syphilis"],
        [Condition: "Complications of Pregnancy, Childbirth and the Puerperium"]
    )

let resultsIndicatingSexualActivity =
    union(
        [ObservationResult: "Pregnancy"],
        [ObservationResult: "Pap"],
        [ObservationResult: "Lab Tests During Pregnancy"],
        [ObservationResult: "Lab Tests for Sexually Transmitted Infections"]
    )

let chlamydiaScreening = 
    [ObservationResult: "Chlamydia Screening"]

let reasonForNoScreening = 
    [ObservationResult: "Reason for not performing Chlamydia Screening"]

