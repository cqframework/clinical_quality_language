library DataRequirements

using FHIR version '4.0.0'

include FHIRHelpers version '4.0.0'
// include DataRequirementsCommon called Common

valueset "ESRD Diagnosis": 'http://fakeurl.com/ersd-diagnosis'
valueset "Hospice Encounter": 'http://fakeurl.com/hospice-encounter'
valueset "HEDIS2020_Independent_Laboratory": 'com:testmi:valueset:xyz-33475'
valueset "ED": 'http://fakeurl.com/ed'

parameter "Measurement Period" Interval<DateTime>

// default Interval[@2019-01-01T00:00:00.0, @2020-01-01T00:00:00.0 )

context Patient

/*
# Overall optimization/dependency tracing strategy:
Two different types of requirements, reported requirements and inferred requirements.
Reported requirements are tracked at the ExpressionDef level and rolled-up across expression defs
Inferred requirements are inferred through expressions and reported at query boundaries
  (or reported directly if the retrieve occurs outside a query boundary)

The visitor is focused on two main tasks:
* Gathering dependencies (any artifacts or declarations used)
* Inferring data requirements (the minimum set of retrieves required to achieve a successful evaluation)

Dependencies in any give visit are tracked cumulatively at the root
Data Requirements must be tracked as inferred per expression def

So in the context, when a requirement is reported, if it's a data requirement, it's tracked at the expression def level,
whereas if it's a dependency, it's always tracked at the root.

# Where clause optimization strategy:
Visit a where clause looking for sargeable conditions of the form:

    A op B

Where:
* A is an order-preserving expression with a single property reference to a property of some source in the current query context
* op is a positive relative comparison operation (=, >, <, >=, <=)
* B is a functional, repeatable, and deterministic context literal expression with respect to the current query context

Gather sargeable conditions as Lists of conditions. At an AND, combine conditions from sub-nodes.
At an OR, the result is separate lists of condition lists.
At an AND, if there are already lists of lists, the condition is too complex for analysis (i.e. it's not in DNF or CNF)

At a property, return an ElmPropertyRequirement
At a literal return an ElmExpressionRequirement w/ contextLiteral true
At a parameter return an ElmExpressionRequirement w/ contextLiteral true
At a unary expression, return an ElmExpressionRequirement w/ contextLiteral false
At a binary comparison expression, return ElmConditionRequirement if possible
At a logical expression, return ElmConjuntiveRequirement or ElmDisjunctiveRequirement


Library
normalize / de-duplicate across all expressions
----
Expression
must support (don't do that)
the of query requirements
---
OR  - Type - AND
List<List<DataRequirement>>
Query Boundary
(OR {
     DataRequirement {
       type: X
       codeFilters : ANDS
       dateFilters ANDS
     },
     DataRequirement {
       type: Y
       codeFilters : !ANDS

     }
  },
  {
    DataRequirement {
      type: X
      dateFilters ANDS
    },
  })
*/

/*
1.
Stated DataRequirement: ERSD Observations
type: Observation
codeFilter: { path:  code, valueSet:  'http://fakeurl.com/ersd-diagnosis' }
*/

define "ESRD Observations":
  [Observation: "ESRD Diagnosis"]

/*
1b. Similar to 1, but not on a primary code path and with a constant
DataRequirement
type: Observation
codeFilter: { path: status, code: Coding { code: 'final'}}
*/

define "Observations":
  [Observation] O
    where O.status = 'final'


/*
2a
DataRequirement
type: Claim
codeFilter: { path:  item.revenue, valueSet: 'http://fakeurl.com/hospice-encounter' }
*/

define "HospiceEncounterClaimsA":
  [Claim] CEncounter
    where exists CEncounter.item IEncounter
      where IEncounter.revenue in "Hospice Encounter"

/*
2b - Unbound Measurement Period
DataRequirement
type: Claim
dateFilter: { path:  item.serviced.start }
*/

define "HospiceEncounterClaimsBUnboundDate":
    [Claim] CEncounter
      where exists CEncounter.item IEncounter
        where date from IEncounter.serviced."start" in day of "Measurement Period"

  /*
  2b - Bound Measurement Period
  Interval[@2019-01-01, @2020-01-01 )
  DataRequirement
  type: Claim
  dateFilter: { path:  item.serviced.start, valuePeriod: "@2019-01-01-@2020-01-01" },
  */

/* define "HospiceEncounterClaimsBBoundDate":
    [Claim] CEncounter
      where exists CEncounter.item IEncounter
        where IEncounter.serviced."start" in "Measurement Period" */

/*
2c - Bound Measurement Period
Interval[@2019-01-01, @2020-01-01)
DataRequirement
type: Claim
dateFilter: { path:  item.serviced.start, valuePeriod: "@2019-01-01T00:00:00.0-@2020-01-01T00:00:00.0" },
*/

/* define "HospiceEncounterClaimsBBoundDateFrom":
    [Claim] CEncounter
      where exists CEncounter.item IEncounter
        where date from IEncounter.serviced."start" in "Measurement Period" */


/*
2d -
Interval[@2019-01-01, @2020-01-01)
DataRequirement
type: Claim
*/

/* define "HospiceEncounterClaimsBBoundDateFromPlus1Day":
    [Claim] CEncounter
      where exists CEncounter.item IEncounter
        where (date from IEncounter.serviced."start") + 1 day in "Measurement Period" */



/*
2.
DataRequirement
Interval[@2019-01-01, @2020-01-01)
type: Claim
codeFilter: { path:  item.revenue, valueSet: 'http://fakeurl.com/hospice-encounter' }
dateFilter: { path: item.serviced.start, valuePeriod: "@2019-01-01T00:00:00.0-@2020-01-01T00:00:00.0"  }
*/

/* define "HospiceEncounterClaims":
  [Claim] CEncounter
    where exists CEncounter.item IEncounter
      where IEncounter.revenue in "Hospice Encounter"
        and date from IEncounter.serviced."start" in day of "Measurement Period" */

/*
3.
DataRequirement
type: Condition
codeFilter: { path:  code, valueSet: 'http://fakeurl.com/hospice-encounter' }
dateFilter: { path: onset }
*/

/* define "HospiceEncounterConditions":
  [Condition: "Hospice Encounter"] HospiceEncounterACond
    where IsActiveAndConfirmed(HospiceEncounterACond)
      and date from HospiceEncounterACond.onset in day of "Measurement Period"

define function "IsActiveAndConfirmed"(c FHIR.Condition):
  true */

/*
4.
DataRequirement
type: Claim
codeFilter: { path:  item.location, valueSet: 'com:testmi:valueset:xyz-33475' }
*/

/* define "LeadScreeningClaims":
  [Claim] C
    where exists ( C.item I
        where I.location in "HEDIS2020_Independent_Laboratory"
    ) */



/*
5. - Could potentially get a dateFilter too if "Measurement Period" were constant
DataRequirement
type: Claim
codeFilter: { path : item.careTeam }
dateFilter: { path : item.serviced.start }
*/

/* define "Has Nephrologist Claim Support":
  [Claim] C
    where IsNephrologistVisit(C)
    return NephrologistVisitSupport(C)

define function "IsNephrologistVisit"(C Claim):
  exists ( ( ClaimAndItemsForClaim(C)) T
      let c: T.c,
      ci: T.ci
      where HasValidNephrologistProvider(C, ci.careTeamLinkId)
        and date from ci.serviced."start" in day of "Measurement Period"
  )

define function "ClaimAndItemsForClaim"(C Claim):
  C.item CI
    let i: {
      careTeamLinkId: C.careTeam,
      serviced: CI.serviced
    }
    return {
      c: C,
      ci: i
    } */

// Hmm.. this would probably be some filter on the practitioner .

/* define function "HasValidNephrologistProvider"(c Claim, ct List<FHIR.Claim.CareTeam>):
  true

define function "NephrologistVisitSupport"(C Claim):
  true */

/*
6. - Actually, a simpler version of the last one..
DataRequirement
type: Claim
codeFilter: { path : item.careTeam }
dateFilter: { path : item.serviced.start }
*/

/* define "Has Nephrologist Claim":
  exists [Claim] C
    where IsNephrologistVisit(C) */

/*
7. Could potentially add a value to the date filter if "i" were constant
DataRequirement
type: Procedure
codeFilter: { path: code, url : http://fakeurl.com/ed }
dateFilter: { path: performed.start }

DataRequirement
type: Claim
codeFilter: { path: id}
*/

/* define function EDFractureProcedureVisitsDuring(i Interval<DateTime>):
  [Procedure: ED] Proc
    let ci: ClaimItem(Proc)
    with "All Fracture Dates" Dt
      such that Dt = date from start of ( Proc.performed as FHIR.Period )
    where ( date from Proc.performed."start" in day of i )
    return VisitDescriptor(Proc.performed, 'ed')

define function VisitDescriptor(P FHIR.Period, V String):
  {
    period: P,
    type: V
  }

define function "ClaimIdForReference"(ref String):
  Split(ref, '#')[0]

define function "Claim"(ref String):
  First([Claim] C
      where EndsWith(ClaimIdForReference(ref), C.id)
  )

define function "ClaimItem"(P Procedure):
  null

define "All Fracture Dates":
  { @2012-01-01 } */


/* None of these are query context */
/*
8a. - Could potentially get a dateFilter too if "Measurement Period" were constant
DataRequirement
Patient
dateFilter : { path : birthDate }
*/

/* define "Patient Age":
  AgeInYearsAt(
    end of "Measurement Period"
  ) */

/* define "In Age Range":
  "Patient Age" >= 67
    and "Patient Age" <= 85 */

/*
8b.
DataRequirement
Patient
codeFilter : { path : gender,  code: 'female'}
*/

/* define "Is Female":
  Patient.gender = 'female' */

/*
9: Support "include"
9a. Support include when the reference is through a "Last(Split())" in a with
9b. Support include when the reference is through a "Last(Split())" in a let
9c. Support include when the reference is through a "Last(Split())" in an exists
9d. Support include when the reference is through a "Last(Split())" with an "or"
9e. Support include when the reference is through a ".resolve()" in a with
9f. Support include when the reference is through a ".resolve()" in a let
9g. Support include when the reference is through a ".resolve()" in an exists
9h. Support include when the reference is through a ".resolve()" with an "or"
9i. Support include when the reference is through a ".reference()" in a with
9j. Support include when the reference is through a ".reference()" in a let
9k. Support include when the reference is through a ".reference()" in a exists
9l. Support include when the reference is through a ".reference()" with an "or"
9m. Support include when the reference is through a related-context retrieve in a with
9n. Support include when the reference is through a related-context retrieve in a let
9o. Support include when the reference is through a related-context retrieve in an exists
9p. Support include when the reference is through a related-context retrieve with an "or"
*/

