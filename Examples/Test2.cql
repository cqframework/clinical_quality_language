[Diagnosis, Present: "Other Female Reproductive Conditions"]

define retrieve [Condition, Present: Code, Range]
{
	foreach element in
		[FHIR.Condition: Code, Range]
			where status in { "provisional", "working", "confirmed" }
		return
			tuple
			{
				code: element.code,
				effectiveTime: 
					interval
					[
						if element.onsetAge is not null 
							then Date(today.Year - (Age() - element.onsetAge), null, null) 
							else element.onsetDate,
						case
							when element.abatement is not null then null
							when element.abatementAge is not null
								then Date(today.Year - (Age() - element.abatementAge), null, null)
							else element.abatementDate
						end
					],
				…
			}
}

define retrieve no [Diagnosis, Present: Code]
{
	foreach element in
		[FHIR.Condition : Code]
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
