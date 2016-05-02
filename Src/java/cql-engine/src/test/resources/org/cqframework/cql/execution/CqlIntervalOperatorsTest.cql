library CqlIntervalOperatorsTest version '1'

context Population

//Interval
define IntegerIntervalTest: Interval[1, 10]
define DecimalIntervalTest: Interval[1.0, 10.0]
define QuantityIntervalTest: Interval[1.0 'g', 10.0 'g']
//define DateTimeIntervalTest: Interval[@2016-05-01T00:00:00Z, @2016-05-02T00:00:00Z)
//define TimeIntervalTest: Interval[@T00:00:00Z, @T23:59:59Z]

//MinValue
define IntegerMinValue: minimum Integer
define DecimalMinValue: minimum Decimal
define QuantityMinValue: minimum Quantity
//define DateTimeMinValue: minimum DateTime
//define TimeMinValue: minimum Time

//MaxValue
define IntegerMaxValue: maximum Integer
define DecimalMaxValue: maximum Decimal
define QuantityMaxValue: maximum Quantity
//define DateTimeMaxValue: maximum DateTime
//define TimeMaxValue: maximum Time

//Successor
define IntegerSuccessor: successor of 1
define DecimalSuccessor: successor of 1.0
define QuantitySuccessor: successor of 1.0 'g'
//define DateTimeSuccessor: successor of @2016-05-01T00:00:00Z
//define TimeSuccessor: successor of @T00:00:00Z

//Predecessor
define IntegerPredecessor: predecessor of 1
define DecimalPredecessor: predecessor of 1.0
define QuantityPredecessor: predecessor of 1.0 'g'
//define DateTimePredecessor: predecessor of @2016-05-01T00:00:00Z
//define TimePredecessor: predecessor of @T00:00:00Z

//Start
define IntegerIntervalStart: start of IntegerIntervalTest
define DecimalIntervalStart: start of DecimalIntervalTest
define QuantityIntervalStart: start of QuantityIntervalTest
//define DateTimeIntervalStart: start of DateTimeIntervalTest
//define TimeIntervalStart: start of TimeIntervalTest

//End
define IntegerIntervalEnd: end of IntegerIntervalTest
define DecimalIntervalEnd: end of DecimalIntervalTest
define QuantityIntervalEnd: end of QuantityIntervalTest
//define DateTimeIntervalEnd: end of DateTimeIntervalTest
//define TimeIntervalEnd: end of TimeIntervalTest
