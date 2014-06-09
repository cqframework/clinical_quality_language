using QLIM

context PATIENT

include "ChlamydiaScreening_Common.cql"

let InDemographic =
    Age() >= 16 and Age() < 24 and Gender = "female"

let SexuallyActive =
    exists (conditionsIndicatingSexualActivity)
    or exists (resultsIndicatingSexualActivity)

let NoScreening =
    not exists (chlamydiaScreening S where S.effectiveTime during interval[today - 1 years, now])
    and not exists ([Procedure, Planned: "Chlamydia Screening"] P where P.effectiveTime >= now)
    and not exists (reasonForNoScreening)

let NeedScreening = InDemographic and SexuallyActive and NoScreening


true, 16, "female"

3 months, 6 u"gm/cm3"

[Encounter, Performed]
tuple { Name: "Patrick", DOB: Date(2014, 1, 1) }

{ 1, 2, 3, 4, 5 }

starts before start, starts concurrent with 

starts 3 days before start
starts within 3 days before start
starts within 3 days of start

meets, overlaps, during

start of MeasurementPeriod

X in { 1, 2, 3, 4, 5 }

A and B
A and not (B or C)

A >= B
A <> B

A + B
A + B * C

[Encounter, Performed]
no [Procedure, Proposal]
unknown [DeviceUse]

let InpatientEncounters = [Encounter, Performed: serviceType in "Inpatient"]
[Encounter, Performed: during MeasurementPeriod]

[Encounter, Performed: "Inpatient"] E 
	where E.lengthOfStay > 120 days

concept "Inpatient" = ValueSet("2.1.6....")

