using QLIM

let TargetEncounters =
	[Encounter, Performed: "Ambulatory/ED Visit"] E 
		with Pharyngitis P where P.effectiveTime overlaps after E.performedAtTime
		with Antibiotics A where A.orderedAtTime starts within 3 days after start E.performedAtTime 
	    where E.performedAtTime during MeasurementPeriod

