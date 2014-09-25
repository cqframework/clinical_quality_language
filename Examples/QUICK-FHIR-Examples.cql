// Consistency of expressions
// This example assumes that a QUICK Condition is a FHIR Profile with status restricted by the profile so that only active conditions are returned
// The example highlights the difficulty of dealing with onsetDate/abatementDate versus just effectiveTime
// Condition w/ effectiveTime
[Condition: "Chlamydia"] C where C.effectiveTime overlaps MeasurementPeriod

// Condition w/ onsetDate, abatementDate
[Condition: "Chlamydia"] C where interval[C.onsetDate, C.abatementDate] overlaps MeasurementPeriod

// If onsetDate and Age are allowed, those need to be taken into account
// Condition w/ onsetDate/Age, abatementDate/Age
[Condition: "Chlamydia"] C
  where 
    interval
	[
	  Coalesce(C.onsetDate, Date((year of Today()) - (Age() - C.onsetAge))), 
	  Coalesce(C.abatementDate, Date(year of Today()) - (Age() - C.abatementAge)))
	] overlaps MeasurementPeriod

// If abatement as a boolean is allowed, it must be considered as well
// Condition w/ onsetDate/Age, abatement/Date/Age
[Condition: "Chlamydia"] C
  where
	interval
	[
		Coalesce(C.onsetDate, Date((year of Today()) - (Age() - C.onsetAge))),
		Coalesce(C.abatementDate, Date(Year of Today()) - (Age() - C.abatementAge)), if C.abatement is not null then null)
	]

// Condition w/ onsetDate vs Encounter w/ EffectiveTime
[Condition: "Chlamydia"] C where interval[C.onsetDate, C.abatementDate] overlaps MeasurementPeriod
[Condition: "Chlamydia"] C where C.onsetDate during MeasurementPeriod or C.abatementDate during MeasurementPeriod
// as opposed to the expected timing formulation
[Encounter, Performance: "Inpatient"] E where E.effectiveTime overlaps MeasurementPeriod


// Condition, Asthma started before 14 August 2013
// QUICK vs Raw FHIR
// QUICK:
[Condition: "Asthma"] C where C.effectiveTime includes interval[Date(2013, 8, 14), Today()]
// FHIR:
[Condition: "Asthma"] C 
  where C.status = 'confirmed' 
    and C.onsetAge <= Date(2013, 8, 14) 
	and not(C.abatement is not null and C.abatement) 
	and not(C.abatementDate is not null and C.abatementDate < Now()) 
	and not(C.abatementAge is not null and C.abatementAge < Age())
// FHIR w/ Profiling to require status = 'confirmed' and only onset/abatementDate values:
[Condition: "Asthma"] C where C.onsetAge <= Date(2013, 8, 14) and C.abatementDate is null

// Proposals/Orders

// Conceptual Structure

// Generalization
// A rule involving procedure level reasoning across LaboratoryProcedure and ImagingProcedure
