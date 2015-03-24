library ClauseTesting

using QUICK

define SimpleQuery:
	[ObservationResult: "Lab Tests for Sexually Transmitted Infections"]

define WhereClause:
	[ObservationResult: "Pregnancy"] R where R.effectiveTime during MeasurementPeriod

define TargetEncounters:
	[Encounter, Performance: "Ambulatory/ED Visit"] E
		with Pharyngitis P such that P.effectiveTime overlaps after E.PerformanceAtTime
		with Antibiotics A such that A.orderedAtTime starts at most 3 days after start E.PerformanceAtTime
	    where E.PerformanceAtTime during MeasurementPeriod

define SimpleRetrieve:
	[Encounter, Performance]

define CodedRetrieve:
	[Encounter, Performance: "Ambulatory/ED Visit"]

define ExplicitlyCodedRetrieve:
	[Encounter, Performance: serviceType in "Ambulatory/ED Visit"]

// define WhereExplicitlyCodedRetrieve:
//  [Encounter, Performance where serviceType in "Ambulatory/ED Visit"]

define CodedWhere:
	[Encounter, Performance] E where E.serviceType in ValueSet("Ambulatory/ED Visit")

define RangedRetrieve:
	[Encounter, Performance, during MeasurementPeriod]

define ExplicitlyRangedRetrieve:
	[Encounter, Performance, PerformanceAtTime during MeasurementPeriod]

// define WhereExplicitlyRangedRetrieve:
//  	[Encounter, Performance where PerformanceAtTime during MeasurementPeriod]

define RangedWhere:
	[Encounter, Performance] E where E.PerformanceAtTime during MeasurementPeriod

define CodedRangedRetrieve:
	[Encounter, Performance: "Ambulatory/ED Visit", during MeasurementPeriod]

define ExplicitlyCodedRangedRetrieve:
	[Encounter, Performance: serviceType in "Ambulatory/ED Visit", PerformanceAtTime during MeasurementPeriod]

// define WhereExplicitlyCodedRangedRetrieve:
//  	[Encounter, Performance where serviceType in "Ambulatory/ED Visit" and PerformanceAtTime during MeasurementPeriod]

define CodedRangedWhere:
	[Encounter, Performance] E
		where E.serviceType in ValueSet("Ambulatory/ED Visit")
			and E.PerformanceAtTime during MeasurementPeriod
