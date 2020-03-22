define IntegerAbs: Abs(-1)
define LongAbs: Abs(-1L)
define DecimalAbs: Abs(-1.0)
define QuantityAbs: Abs(-1.0'cm')

define IntegerAdd: 1 + 1
define LongAdd: 1L + 1L
define RealAdd: 1.0 + 1.0
define QuantityAdd: 1'g/cm3' + 1'g/cm3'

define DecimalCeiling: Ceiling(1.0)
define IntegerCeiling: Ceiling(1)

define IntegerDivide: 1 / 1
define IntegerDivide10: 1 / 0
define RealDivide: 1.0 / 1.0
define QuantityRealDivide: 1'g/cm3' / 1.0
define QuantityDivide: 1'g/cm3' / 1'g/cm3'

define DecimalFloor: Floor(1.0)
define IntegerFloor: Floor(1)

define DecimalDecimalLog: Log(1000.0, 10.0)
define DecimalIntegerLog: Log(1000.0, 10)
define IntegerDecimalLog: Log(1000, 10.0)
define IntegerIntegerLog: Log(1000, 10)

define DecimalLn: Ln(1000.0)
define IntegerLn: Ln(1000)

define DecimalExp: Exp(1000.0)
define IntegerExp: Exp(1000)

define IntegerModulo: 100 mod 10
define LongModulo: 100L mod 10L
define RealModulo: 100.0 mod 10.0
//define QuantityIntegerModulo: 100'g/cm3' mod 10
//define QuantityRealModulo: 100'g/cm3' mod 10.0

define IntegerMultiply: 10 * 10
define LongMultiply: 10L * 10L
define RealMultiply: 10.0 * 10.0
define QuantityRealMultiply: 10'g/cm3' * 10.0
define RealQuantityMultiply: 10.0 * 10'g/cm3'
define QuantityMultiply: 10'cm' * 10'cm'

define IntegerNegate: -(1)
define LongNegate: -(1L)
define RealNegate: -(1.0)
define QuantityNegate: -(1'cm')

define IntegerPredecessor: predecessor of 1
define LongPredecessor: predecessor of 1L
define RealPredecessor: predecessor of 1.0
define DateTimePredecessor: predecessor of DateTime(2014, 1, 1)
define QuantityPredecessor: predecessor of 1'cm'

define IntegerPower: 10^3
define LongPower: 10L^3
define RealPower: 10.0^3.0

define DecimalRound: Round(10.55)
define DecimalRoundWithPrecision: Round(10.5555, 2)

define IntegerSubtract: 5 - 4
define LongSubtract: 5L - 4L
define RealSubtract: 5.0 - 4.0
define QuantitySubtract: 5'cm' - 4'cm'

define IntegerSuccessor: successor of 1
define LongSuccessor: successor of 1L
define RealSuccessor: successor of 1.0
define DateTimeSuccessor: successor of DateTime(2014, 1, 1)
define QuantitySuccessor: successor of 1'cm'

define DecimalTruncate: Truncate(5.5)
define IntegerTruncate: Truncate(5)

define IntegerDiv: 100 div 10
define LongDiv: 100L div 10L
define RealDiv: 100.0 div 10.0
//define QuantityIntegerDiv: 100'cm' div 10
//define QuantityRealDiv: 100'cm' div 10.0

define DecimalPrecision: Precision(1.58700)
define DatePrecision: Precision(@2014)
define DateTimePrecision: Precision(@2014-01-05T10:30:00.000)
define TimePrecision: Precision(@T10:30)

define DecimalLowBoundary: LowBoundary(1.587, 8) // 1.58700000
define DateLowBoundary: LowBoundary(@2014, 6) // @2014-01
define DateTimeLowBoundary: LowBoundary(@2014-01-01T08, 17) // @2014-01-01T08:00:00.000
define TimeLowBoundary: LowBoundary(@T10:30, 9) // @T10:30:00.000

define DecimalHighBoundary: HighBoundary(1.587, 8) // 1.58799999
define DateHighBoundary: HighBoundary(@2014, 6) // @2014-12
define DateTimeHighBoundary: HighBoundary(@2014-01-01T08, 17) // @2014-01-01T08:59:59.999
define TimeHighBoundary: HighBoundary(@T10:30, 9) // @T10:30:59.999

