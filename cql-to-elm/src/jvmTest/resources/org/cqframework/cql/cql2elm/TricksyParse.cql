/*
The overlap of keywords in CQL with FHIRPath functions means that FHIRPath
expressions won't parse correctly. However, introducing the keywords as identifiers
in the grammar (the recommended approach to allowing keywords to be identifiers within ANTLR)
results in unacceptable performance in 4.5 and unacceptable changes to the parsing in 4.6+.

The TestFunctionKeywords expression addresses the keywords issue. It should be parsable in CQL
regardless of FHIRPath support.

The "Has First Hypertensive Reading With Interventions" expression is an example of an expression
 that parses correctly in 4.5, but incorrectly in 4.6+. Specifically, the phrase
   `not "Last Blood Pressure With One Year High"`
 parses as a query rather than a not expression if the keywords are included as identifiers.
*/


define "TestFunctionKeywords":
  ({ 1, 2, 3 }) A
    where(not(exists(distinct({ 1, 2, 3 })) or ({ 1, 2, 3 } contains(1)))
      or exists(({ 1, 2, 3 }) B return all(B))
    )

define "High Blood Pressure at Most Recent Screening Encounter":
  { id: 1, date: @2012-01-01 }

define "Last Blood Pressure Within One Year High":
  true

define "Hypertensive Interventions at Most Recent Blood Pressure Encounter":
  { { id: 1, date: @2012-01-01 } }

define "Follow Up Within 4 Weeks":
  { { id: 1, date: @2012-01-01 } }

define "Referral to Alternate Provider":
  { { id: 1, date: @2012-01-01 } }

define "Has First Hypertensive Reading With Interventions":
	"High Blood Pressure at Most Recent Screening Encounter" is not null
		and not "Last Blood Pressure Within One Year High"
		and ( exists ( "Hypertensive Interventions at Most Recent Blood Pressure Encounter" )
				and exists ( "Follow Up Within 4 Weeks" )
		)
		or exists ( "Referral to Alternate Provider" )
