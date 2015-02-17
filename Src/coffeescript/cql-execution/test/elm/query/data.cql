// @Test: DateRangeOptimizedQuery
valueset "Ambulatory/ED Visit" = '2.16.840.1.113883.3.464.1003.101.12.1061'
parameter MeasurementPeriod default interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))
define EncountersDuringMP = [Encounter] E where E.period during MeasurementPeriod
define AmbulatoryEncountersDuringMP = [Encounter: "Ambulatory/ED Visit"] E where E.period during MeasurementPeriod
define AmbulatoryEncountersIncludedInMP = [Encounter: "Ambulatory/ED Visit"] E where E.period included in MeasurementPeriod

// @Test: IncludesQuery
valueset "Ambulatory/ED Visit" = '2.16.840.1.113883.3.464.1003.101.12.1061'
parameter MeasurementPeriod default interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))
define MPIncludedAmbulatoryEncounters = [Encounter: "Ambulatory/ED Visit"] E where MeasurementPeriod includes E.period

// @Test: MultiSourceQuery
parameter MeasurementPeriod default interval[DateTime(2013, 1, 1), DateTime(2014, 1, 1))
define msQueryWhere = foreach [Encounter] E,
[Condition] C
where E.period included in MeasurementPeriod

define msQueryWhere2 = foreach [Encounter] E, [Condition] C
where  E.period  included in MeasurementPeriod and C.id = 'http://cqframework.org/3/2'

define msQuery = foreach [Encounter] E, [Condition] C return {E: E, C:C}

// @Test: QueryRelationship
define withQuery =  [Encounter] E
with [Condition] C such that C.id = 'http://cqframework.org/3/2'

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
define tupleAsc = [Encounter] E sort by id
define tupleReturnAsc = [Encounter] E return E sort by id
define tupleReturnTupleAsc = [Encounter] E return {E : E} sort by E.id
define tupleDesc = [Encounter] E sort by id desc
define tupleReturnDesc = [Encounter] E return E sort by id desc
define tupleReturnTupleDesc =  [Encounter] E return {E : E} sort by E.id desc
define numberAsc = ({8, 6, 7, 5, 3, 0, 9}) N sort asc
define numberReturnAsc = ({8, 6, 7, 5, 3, 0, 9}) N return N sort asc
define numberDesc = ({8, 6, 7, 5, 3, 0, 9}) N sort desc
define numberReturnDesc = ({8, 6, 7, 5, 3, 0, 9}) N return N sort desc
define stringAsc = ({'jenny', 'dont', 'change', 'your', 'number'}) S sort asc
define stringReturnAsc = ({'jenny', 'dont', 'change', 'your', 'number'}) S return S sort asc
define stringDesc = ({'jenny', 'dont', 'change', 'your', 'number'}) S sort desc
define stringReturnDesc = ({'jenny', 'dont', 'change', 'your', 'number'}) S return S sort desc

// @Test: Distinct
define defaultNumbers = ({1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 3, 3, 3, 2, 2, 1}) N return N
define defaultStrings = ({'foo', 'bar', 'baz', 'bar'}) S return S
define defaultTuples = ({tuple{a: 1, b:2}, tuple{a: 2, b: 3}, tuple{a: 1, b: 2}}) T return T
define distinctNumbers = ({1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 3, 3, 3, 2, 2, 1}) N return distinct N
define distinctStrings = ({'foo', 'bar', 'baz', 'bar'}) S return distinct S
define distinctTuples = ({tuple{a: 1, b:2}, tuple{a: 2, b: 3}, tuple{a: 1, b: 2}}) T return distinct T
define allNumbers = ({1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 3, 3, 3, 2, 2, 1}) N return all N
define allStrings = ({'foo', 'bar', 'baz', 'bar'}) S return all S
define allTuples = ({tuple{a: 1, b:2}, tuple{a: 2, b: 3}, tuple{a: 1, b: 2}}) T return all T
