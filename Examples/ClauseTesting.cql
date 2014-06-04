using QLIM

let SimpleQuery =
	[ObservationResult: "Lab Tests for Sexually Transmitted Infections"]

let WhereClause =
	[ObservationResult: "Pregnancy"] R where R.effectiveTime during MeasurementPeriod

let TargetEncounters =
	[Encounter, Performed: "Ambulatory/ED Visit"] E 
		with Pharyngitis P where P.effectiveTime overlaps after E.performedAtTime
		with Antibiotics A where A.orderedAtTime starts within 3 days after start E.performedAtTime 
	    where E.performedAtTime during MeasurementPeriod

let SimpleRetrieve =
	[Encounter, Performed]

let CodedRetrieve =
	[Encounter, Performed: "Ambulatory/ED Visit"]

let ExplicitlyCodedRetrieve =
	[Encounter, Performed: serviceType in "Ambulatory/ED Visit"]

let CodedWhere =
	[Encounter, Performed] E where E.serviceType in ValueSet("Ambulatory/ED Visit")

let RangedRetrieve =
	[Encounter, Performed, during MeasurementPeriod]

let ExplicitlyRangedRetrieve =
	[Encounter, Performed, performedAtTime during MeasurementPeriod]

let RangedWhere =
	[Encounter, Performed] E where E.performedAtTime during MeasurementPeriod

let CodedRangedRetrieve =
	[Encounter, Performed: "Ambulatory/ED Visit", during MeasurementPeriod]

let ExplicitlyCodedRangedRetrieve =
	[Encounter, Performed: serviceType in "Ambulatory/ED Visit", performedAtTime during MeasurementPeriod]

let CodedRangedWhere =
	[Encounter, Performed] E 
		where E.serviceType in ValueSet("Ambulatory/ED Visit") 
			and E.performedAtTime during MeasurementPeriod


