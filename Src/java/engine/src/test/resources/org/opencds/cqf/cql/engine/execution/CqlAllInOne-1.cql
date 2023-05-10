library CqlAllInOne version '1'

define AbsNull: Abs(null as Integer)
define Add11: 1 + 1
define TestAfterNull: null as Integer after IntegerIntervalTest
define IntegerIntervalTest: Interval[1, 10]
define AllTrueAllTrue : AllTrue({true,true})
define TrueAndTrue : true and true
define AnyTrueAllTrue               : AnyTrue({true,true})
define AnyTrueAllFalse              : AnyTrue({false,false})

define AsQuantity: 45.5 'g' as Quantity
define CastAsQuantity: cast 45.5 'g' as Quantity
define AsDateTime: DateTime(2014, 01, 01) as DateTime

define CeilingNull: Ceiling(null as Decimal)
define Ceiling1D: Ceiling(1.0)
define Ceiling1D1: Ceiling(1.1)

define DivideNull: 1 / null
define Divide10: 1 / 0
define Divide01: 0 / 1
define Divide11: 1 / 1

define FloorNull: Floor(null as Decimal)
define Floor1: Floor(1)
define Floor1D: Floor(1.0)

define ExpNull  : Exp(null as Decimal)
define Exp0  : Exp(0)
define ExpNeg0  : Exp(-0)

define HighBoundaryDec : HighBoundary(1.587, 8)
define HighBoundaryDate :HighBoundary(@2014, 6)
define HighBoundaryDateTime : HighBoundary(@2014-01-01T08, 17)

define LogNullNull  : Log(null, null)
define Log1BaseNull  : Log(1, null)
define Log1Base1  : Log(1, 1)

define LowBoundaryDec : LowBoundary(1.587, 8)
define LowBoundaryDate :LowBoundary(@2014, 6)
define LowBoundaryDateTime : LowBoundary(@2014-01-01T08, 17)

define LnNull  : Ln(null)
//define Ln0  : Ln(0)
//define LnNeg0  : Ln(-0)
//define Ln1  : Ln(1)
