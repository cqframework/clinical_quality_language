// @Test: DateRangeOptimizedQuery
parameter MeasurementPeriod default interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))
valueset "Ambulatory/ED Visit" = '2.16.840.1.113883.3.464.1003.101.12.1061'
define EncountersDuringMP = [Encounter] E where E.period during MeasurementPeriod
define AmbulatoryEncountersDuringMP = [Encounter: "Ambulatory/ED Visit"] E where E.period during MeasurementPeriod
define AmbulatoryEncountersIncludedInMP = [Encounter: "Ambulatory/ED Visit"] E where E.period included in MeasurementPeriod

// @Test: IncludesQuery
parameter MeasurementPeriod default interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))
valueset "Ambulatory/ED Visit" = '2.16.840.1.113883.3.464.1003.101.12.1061'
define MPIncludedAmbulatoryEncounters = [Encounter: "Ambulatory/ED Visit"] E where MeasurementPeriod includes E.period

// @Test: MultiSourceQuery
parameter MeasurementPeriod default interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))
define msQueryWhere = foreach [Encounter] E,
[Condition] C
where E.period included in MeasurementPeriod

define msQueryWhere2 = foreach [Encounter] E, [Condition] C
where  E.period  included in MeasurementPeriod and  C.id = 'http://cqframework.org/3/2'

define msQuery = foreach [Encounter] E, [Condition] C return {E: E, C:C}

// @Test: QueryRelationship
define withQuery =  [Encounter] E
with [Condition] C such that C.id= 'http://cqframework.org/3/2'

define withQuery2 =  [Encounter] E
with [Condition] C such that C.id = 'http://cqframework.org/3'

define withOutQuery =  [Encounter] E
without [Condition] C such that C.id = 'http://cqframework.org/3/'

define withOutQuery2 =  [Encounter] E
without [Condition] C such that C.id = 'http://cqframework.org/3/2'

// @Test: QueryDefine
define query =  [Encounter] E
define a = E
return {E: E, a:a}

// @Test: Tuple
define query =  [Encounter] E return {id: E.id, thing: E.status}

// @Test: Sorting
define singleAsc =  [Encounter] E  return {E : E} sort by E.id
define singleDesc =  [Encounter] E return {E : E} sort by E.id desc
