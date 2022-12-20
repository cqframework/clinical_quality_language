library TestFhirExecution version '1.0'

using FHIR

parameter MAXYEAR Integer

context Patient

define MedicationRequests:
    [MedicationRequest]

define testCoalesce:
    MedicationRequests M
        return
            Coalesce(M.dosageInstruction[0].timing.repeat.frequencyMax.value, M.dosageInstruction[0].timing.repeat.frequency.value)

define MeasurementPeriod:
    Interval[DateTime(MAXYEAR - 1, 10, 1), DateTime(MAXYEAR, 4, 30)]

define testMonthFrom:
    [Immunization] I
        where I.date.value in MeasurementPeriod

define "Immunizations between October and December":
    [Immunization] I
            where month from I.date.value in {10, 11, 12}

define "Immunizations between January and April":
    [Immunization] I
        where month from I.date.value in {1, 2, 3, 4}

define "Immunizations in range":
    from "Immunizations between October and December" OctDec,
    "Immunizations between January and April" JanApr
        where year from OctDec.date.value = year from JanApr.date.value - 1
        return Union(OctDec, JanApr)

define "Resource Id":
  [Encounter] E return E.id.value