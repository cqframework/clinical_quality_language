using QUICK

let SimpleQuery =
	[ObservationResult: "Lab Tests for Sexually Transmitted Infections"]

let WhereClause =
	[ObservationResult: "Pregnancy"] R where R.effectiveTime during MeasurementPeriod

let TargetEncounters =
	[Encounter, Performance: "Ambulatory/ED Visit"] E 
		with Pharyngitis P where P.effectiveTime overlaps after E.PerformanceAtTime
		with Antibiotics A where A.orderedAtTime starts within 3 days after start E.PerformanceAtTime 
	    where E.PerformanceAtTime during MeasurementPeriod

let SimpleRetrieve =
	[Encounter, Performance]

let CodedRetrieve =
	[Encounter, Performance: "Ambulatory/ED Visit"]

let ExplicitlyCodedRetrieve =
	[Encounter, Performance: serviceType in "Ambulatory/ED Visit"]

let WhereExplicitlyCodedRetrieve =
	[Encounter, Performance where serviceType in "Ambulatory/ED Visit"]

let CodedWhere =
	[Encounter, Performance] E where E.serviceType in ValueSet("Ambulatory/ED Visit")

let RangedRetrieve =
	[Encounter, Performance, during MeasurementPeriod]

let ExplicitlyRangedRetrieve =
	[Encounter, Performance, PerformanceAtTime during MeasurementPeriod]

let WhereExplicitlyRangedRetrieve =
	[Encounter, Performance where PerformanceAtTime during MeasurementPeriod]

let RangedWhere =
	[Encounter, Performance] E where E.PerformanceAtTime during MeasurementPeriod

let CodedRangedRetrieve =
	[Encounter, Performance: "Ambulatory/ED Visit", during MeasurementPeriod]

let ExplicitlyCodedRangedRetrieve =
	[Encounter, Performance: serviceType in "Ambulatory/ED Visit", PerformanceAtTime during MeasurementPeriod]

let WhereExplicitlyCodedRangedRetrieve =
	[Encounter, Performance where serviceType in "Ambulatory/ED Visit" and PerformanceAtTime during MeasurementPeriod]

let CodedRangedWhere =
	[Encounter, Performance] E 
		where E.serviceType in ValueSet("Ambulatory/ED Visit") 
			and E.PerformanceAtTime during MeasurementPeriod


