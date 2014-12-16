library FHIRMap

using FHIR

context PATIENT

define retrieve [QUICK.Condition: code in Concept, effectiveTime during Range]
{
	return
		[FHIR.Condition: Concept, during Range] C
			where C.status in { 'provisional', 'working', 'confirmed' }
			return
				tuple
				{
					code: C.name,
					effectiveTime: 
						interval
						[
							if C.onsetAge is not null 
								then DateTime((year of Today()) - (Age() - C.onsetAge))
								else C.onsetDate,
							case
								when C.abatement is not null then null
								when C.abatementAge is not null
									then DateTime((year of Today()) - (Age() - C.abatementAge))
								else C.abatementDate
							end
						],
					...
				}
}

define retrieve [NonOccurrence of Condition: code in Concept, effectiveTime during Range]
{
	return
		[FHIR.Condition : Concept, during Range] C
			where C.status = 'refuted'
			return
				tuple
				{
					code: C.name,
					effectiveTime: 
						interval
						[
							if C.onsetAge is not null 
								then DateTime((year of Today()) - (Age() - C.onsetAge))
								else C.onsetDate,
							case
								when C.abatement is not null then null
								when C.abatementAge is not null
									then DateTime((year of Today()) - (Age() - C.abatementAge))
								else C.abatementDate
							end
						],
					...
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


define retrieve [QUICK.MyCondition: code in Concept, effectiveTime during Range]
{
	return
		[FHIR.TheirCondition: Concept, during Range] C
			where C.status in { 'provisional', 'working', 'confirmed' }
			return
				tuple
				{
					code: C.name,
					effectiveTime: 
						interval
						[
							if C.onsetAge is not null 
								then DateTime((year of Today()) - (Age() - C.onsetAge))
								else C.onsetDate,
							case
								when C.abatement is not null then null
								when C.abatementAge is not null
									then DateTime((year of Today()) - (Age() - C.abatementAge))
								else C.abatementDate
							end
						],
					...,
					myAttribute1: C.extension['Attribute1'].valueInteger, // using FHIR extension attributes
					myAttribute2: C.Attribute2 // Assuming the FHIR data access layer returns first-class attributes based on the FHIR Profile
				}
}


define function ToFHIR(procedureProposal: [Procedure, Proposal])
{
	return
		tuple ([FHIR.Procedure])
		{
			code: procedureProposal.procedureCode,
			...
		}
}

