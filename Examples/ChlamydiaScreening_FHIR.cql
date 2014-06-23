library FHIRMap

using FHIR

context PATIENT

define retrieve [Condition: code in Concept, effectiveTime during Range]
{
	foreach element in
		[FHIR.Condition: Concept, during Range]
			where status in { 'provisional', 'working', 'confirmed' }
		return
			tuple
			{
				code: element.code,
				effectiveTime: 
					interval
					[
						if element.onsetAge is not null 
							then Date(today.Year - (Age() - onsetAge), null, null) 
							else element.onsetDate,
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

define retrieve no [Condition: code in Concept, effectiveTime during Range]
{
	foreach element in
		[FHIR.Condition : Concept, during Range]
			where status = 'refuted'
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

define retrieve [ObservationResult: name in Concept, observedAtTime during Range]
{
	foreach element in 
		[FHIR.Observation: Concept, during Range]
			where status in { 'final', 'amended' }
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
