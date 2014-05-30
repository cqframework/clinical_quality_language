using FHIR

define retrieve [Condition: Concept]
{
	foreach element in
		[FHIR.Condition: Concept]
			where status in { "provisional", "working", "confirmed" }
		return
			tuple
			{
				code: code,
				effectiveTime: 
					interval
					[
						if onsetAge is not null 
							then Date(today.Year - (Age() - onsetAge), null, null) 
							else onsetDate,
						case
							when abatement is not null then null
							when abatementAge is not null
								then Date(today.Year - (Age() - abatementAge), null, null)
							else abatementDate
						end
					],
				…
			}
}

define retrieve no [Condition: Concept]
{
	foreach element in
		[FHIR.Condition : Concept]
			where status = "refuted"
		return
			tuple
			{
				code,
				effectiveTime: 
					interval
					[
						if onsetAge is not null 
							then Date(today.Year - (Age() - onsetAge), null, null) 
							else onsetDate,
						case
							when abatement is not null then null
							when abatementAge is not null
								then Date(today.Year - (Age() - abatementAge), null, null)
							else abatementDate
						end
					],
				…
			}
}

define retrieve [ObservationResult: Concept]
{
	foreach element in 
		[FHIR.Observation: Concept]
			where status in { "final", "amended" }
		return
			tuple
			{
				bodySite : element.bodySite, // TODO: Map from FHIR.Observation.bodySite code binding
				interpretation : element.interpretation, // TODO: Map from FHIR.Observation.interpretation
				method : element.method, // TODO: Map to method value set
				name : element.name,
				reliability : element.reliability, // TODO: Map to reliability value set
				status : element.status, // TODO: Map to status value set
				validationMethod : element.validationMethod,
				value : 
					coalesce 
					(
						element.valueQuantity, 
						element.valueCodeableConcept, 
						element.valueAttachment, 
						element.valueRatio, 
						element.valuePeriod, 
						element.valueSampledData, 
						element.valuestring 
					)
			}
}
