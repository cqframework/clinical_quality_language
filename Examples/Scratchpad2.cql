//* CQL Updates
// Difference Between/Duration Between

define DurationBetweenExpression: days between DateTime(2014, 1, 1) and DateTime(2014, 12, 31)
define DifferenceBetweenExpression: difference in days between DateTime(2014, 1, 1) and DateTime(2014, 12, 31)

// Translation of Timing Phrases
// Removed IfNull operator in favor of Coalesce

Coalesce({ 1, 2, 3, 4, 5, 6, 7, 8 })

// Renamed Pos to PositionOf

// Point/Interval overloads for Before/After

define PointExpression: PointA before PointB
define IntervalExpression: IntervalA before IntervalB
define PointIntervalExpression: PointA before IntervalB

// occurs Keyword

define OccursExpression: [Encounter] E where E.startDate occurs before MeasurementPeriod

// Point/Interval overload of within

define WithinExpression: PointA occurs within 3 days of IntervalB

// from instead of foreach in queries

define QueryExpression:
    from [Encounter] E, [Diagnosis] D
    where E.period overlaps before D.period
    return E.id

// Type Specifier/Assignment operator

define function Add(A : Integer, B : Integer)
define function Add(A Integer, B Integer)

define TupleExpression: tuple { id: 1, name: 'Joe' }

// codesystems

// private/public Keywords

//* Outstanding Discussion Items
// Possible “new” syntax

define CodeExpression: Code { code: 'XYZ', system: 'http://uri' }
define CodeExpression: Code('XYZ', 'http://uri')

// tuple vs row keyword

tuple - row

// list / interval vs List/Interval

list<Integer>

list<Integer> { 1, 2, 3, 4, 5 }

tuple { integerList list<Integer> }


// any/all quantifiers


define AnyCode: any X in "Y" === AnyTrue(X X where X in "Y")
define AllCode: all X in "Y" === AllTrue(X X where X in "Y")

