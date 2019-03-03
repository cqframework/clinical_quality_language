library ComparisonOperator version '1'

context Population

//Between
define BetweenIntTrue: 4 between 2 and 6

//Equal
define SimpleEqTrueTrue : true = true
define SimpleEqTrueFalse : true = false
define SimpleEqFalseFalse : false = false
define SimpleEqFalseTrue : false = true
//define SimpleEqNullNull : null = null
//define SimpleEqTrueNull : true = null
//define SimpleEqNullTrue : null = true
define SimpleEqInt1Int1 : 1 = 1
define SimpleEqInt1Int2 : 1 = 2
define SimpleEqStringAStringA : 'a' = 'a'
define SimpleEqStringAStringB : 'a' = 'b'
define SimpleEqFloat1Float1 : 1.0 = 1.0
define SimpleEqFloat1Float2 : 1.0 = 2.0
define SimpleEqFloat1Int1 : 1.0 = 1
define SimpleEqFloat1Int2 : 1.0 = 2
define QuantityEqCM1CM1 : 1'cm' = 1'cm'
define QuantityEqCM1M01 : 1'cm' = 0.01'm'
define TupleEqJohnJohn: Tuple { Id : 1, Name : 'John' } = Tuple { Id : 1, Name : 'John' }
define TupleEqJohnJane: Tuple { Id : 1, Name : 'John' } = Tuple { Id : 2, Name : 'Jane' }
define TupleEqJohn1John2: Tuple { Id : 1, Name : 'John' } = Tuple { Id : 2, Name : 'John' }
define TupleEqDateTimeTrue: Tuple { dateId: 1, Date: DateTime(2012, 10, 5, 0, 0, 0, 0) } = Tuple { dateId: 1, Date: DateTime(2012, 10, 5, 0, 0, 0, 0) }
define TupleEqDateTimeFalse: Tuple { dateId: 1, Date: DateTime(2012, 10, 5, 0, 0, 0, 0) } = Tuple { dateId: 1, Date: DateTime(2012, 10, 5, 5, 0, 0, 0) }
define TupleEqDateTimeNull: Tuple { dateId: 12, Date: DateTime(2012, 1, 1) } = Tuple { dateId: 12, Date: DateTime(2012, 1, 1) }
define TupleEqTimeTrue: Tuple { timeId: 55, TheTime: @T05:15:15.541 } = Tuple { timeId: 55, TheTime: @T05:15:15.541 }
define TupleEqTimeFalse: Tuple { timeId: 55, TheTime: @T05:15:15.541 } = Tuple { timeId: 55, TheTime: @T05:15:15.540 }
define ListEqEmptyEmpty : {} = {}
define ListEqABCABC : { 'a', 'b', 'c' } = { 'a', 'b', 'c' }
define ListEqABCAB : { 'a', 'b', 'c' } = { 'a', 'b' }
define ListEqABC123: { 'a', 'b', 'c' } = { 1, 2, 3 }
define ListEq123ABC: { 1, 2, 3 } = { 'a', 'b', 'c' }
define ListEq123String123:  { 1, 2, 3 } = { '1', '2', '3' }
define IntervalEq1To101To10: Interval[1, 10] = Interval[1, 10]
define IntervalEq1To101To5: Interval[1, 10] = Interval[1, 5]
define DateTimeEqTodayToday: Today() = Today()
define DateTimeEqTodayYesterday: Today() = Today() - 1 days
define DateTimeEqJanJan: DateTime(2014, 1, 5, 5, 0, 0, 0, 0) = DateTime(2014, 1, 5, 5, 0, 0, 0, 0)
define DateTimeEqJanJuly: DateTime(2014, 1, 5, 5, 0, 0, 0, 0) = DateTime(2014, 7, 5, 5, 0, 0, 0, 0)
define DateTimeEqMissingArg: DateTime(2015, 1, 5, 5, 0, 0) = DateTime(2015, 1, 5, 5, 0, 0)
define DateTimeEqNull: DateTime(null) = DateTime(null)
define DateTimeEqNull2: DateTime(2001, 1, 1, null) = DateTime(2001, 1, 1, null, null)
define DateTimeUTC: @2014-01-25T14:30:14.559+01:00 = @2014-01-25T14:30:14.559+01:00
define TimeEq10A10A: @T10:00:00.000 = @T10:00:00.000
define TimeEq10A10P: @T10:00:00.000 = @T22:00:00.000

//Greater
define GreaterZZ        : 0 > 0
define GreaterZ1        : 0 > 1
define GreaterZNeg1     : 0 > -1
define GreaterDecZZ     : 0.0 > 0.0
define GreaterDecZ1     : 0.0 > 1.0
define GreaterDecZNeg1  : 0.0 > -1.0
define GreaterCM0CM0    : 0'cm' > 0'cm'
define GreaterCM0CM1    : 0'cm' > 1'cm'
define GreaterCM0NegCM1 : 0'cm' > -1'cm'
define GreaterM1CM1     : 1'm' > 1'cm'
define GreaterM1CM10    : 1'm' > 10'cm'
define GreaterAA        : 'a' > 'a'
define GreaterAB        : 'a' > 'b'
define GreaterBA        : 'b' > 'a'
define GreaterAThanAA   : 'a' > 'aa'
define GreaterAAThanA   : 'aa' > 'a'
define GreaterJackJill   : 'Jack' > 'Jill'
define DateTimeGreaterTrue: DateTime(2012, 2, 12) > DateTime(2012, 2, 10)
define DateTimeGreaterFalse: DateTime(2012, 2, 12) > DateTime(2012, 2, 13)
define TimeGreaterTrue: @T10:00:00.001 > @T10:00:00.000
define TimeGreaterFalse: @T10:00:00.000 > @T10:00:00.001

//Greater Or Equal
define GreaterOrEqualZZ        : 0 >= 0
define GreaterOrEqualZ1        : 0 >= 1
define GreaterOrEqualZNeg1     : 0 >= -1
define GreaterOrEqualDecZZ     : 0.0 >= 0.0
define GreaterOrEqualDecZ1     : 0.0 >= 1.0
define GreaterOrEqualDecZNeg1  : 0.0 >= -1.0
define GreaterOrEqualCM0CM0    : 0'cm' >= 0'cm'
define GreaterOrEqualCM0CM1    : 0'cm' >= 1'cm'
define GreaterOrEqualCM0NegCM1 : 0'cm' >= -1'cm'
define GreaterOrEqualM1CM1     : 1'm' >= 1'cm'
define GreaterOrEqualM1CM10    : 1'm' >= 10'cm'
define GreaterOrEqualAA        : 'a' >= 'a'
define GreaterOrEqualAB        : 'a' >= 'b'
define GreaterOrEqualBA        : 'b' >= 'a'
define GreaterOrEqualAThanAA   : 'a' >= 'aa'
define GreaterOrEqualAAThanA   : 'aa' >= 'a'
define GreaterOrEqualJackJill  : 'Jack' >= 'Jill'
define DateTimeGreaterEqTrue: DateTime(2012, 2, 12, 0, 0, 0, 0) >= DateTime(2012, 2, 10, 0, 0, 0, 0)
define DateTimeGreaterEqTrue2: DateTime(2012, 2, 12, 0, 0, 0, 0) >= DateTime(2012, 2, 12, 0, 0, 0, 0)
define DateTimeGreaterEqFalse: DateTime(2012, 2, 12, 0, 0, 0, 0) >= DateTime(2012, 2, 13, 0, 0, 0, 0)
define TimeGreaterEqTrue: @T10:00:00.001 >= @T10:00:00.000
define TimeGreaterEqTrue2: @T10:00:00.000 >= @T10:00:00.000
define TimeGreaterEqFalse: @T10:00:00.000 >= @T10:00:00.001

//Less
define LessZZ        : 0 < 0
define LessZ1        : 0 < 1
define LessZNeg1     : 0 < -1
define LessDecZZ     : 0.0 < 0.0
define LessDecZ1     : 0.0 < 1.0
define LessDecZNeg1  : 0.0 < -1.0
define LessCM0CM0    : 0'cm' < 0'cm'
define LessCM0CM1    : 0'cm' < 1'cm'
define LessCM0NegCM1 : 0'cm' < -1'cm'
define LessM1CM1     : 1'm' < 1'cm'
define LessM1CM10    : 1'm' < 10'cm'
define LessAA        : 'a' < 'a'
define LessAB        : 'a' < 'b'
define LessBA        : 'b' < 'a'
define LessAThanAA   : 'a' < 'aa'
define LessAAThanA   : 'aa' < 'a'
define LessJackJill  : 'Jack' < 'Jill'
define DateTimeLessTrue: DateTime(2012, 2, 9) < DateTime(2012, 2, 10)
define DateTimeLessFalse: DateTime(2012, 2, 14) < DateTime(2012, 2, 13)
define TimeLessTrue: @T10:00:00.001 < @T10:00:00.002
define TimeLessFalse: @T10:10:00.000 < @T10:00:00.001

//Less Or Equal
define LessOrEqualZZ        : 0 <= 0
define LessOrEqualZ1        : 0 <= 1
define LessOrEqualZNeg1     : 0 <= -1
define LessOrEqualDecZZ     : 0.0 <= 0.0
define LessOrEqualDecZ1     : 0.0 <= 1.0
define LessOrEqualDecZNeg1  : 0.0 <= -1.0
define LessOrEqualCM0CM0    : 0'cm' <= 0'cm'
define LessOrEqualCM0CM1    : 0'cm' <= 1'cm'
define LessOrEqualCM0NegCM1 : 0'cm' <= -1'cm'
define LessOrEqualM1CM1     : 1'm' <= 1'cm'
define LessOrEqualM1CM10    : 1'm' <= 10'cm'
define LessOrEqualAA        : 'a' <= 'a'
define LessOrEqualAB        : 'a' <= 'b'
define LessOrEqualBA        : 'b' <= 'a'
define LessOrEqualAThanAA   : 'a' <= 'aa'
define LessOrEqualAAThanA   : 'aa' <= 'a'
define LessOrEqualJackJill  : 'Jack' <= 'Jill'
define DateTimeLessEqTrue: DateTime(2012, 2, 9, 0, 0, 0, 0) <= DateTime(2012, 2, 10, 0, 0, 0, 0)
define DateTimeLessEqTrue2: DateTime(2012, 2, 12, 0, 0, 0, 0) <= DateTime(2012, 2, 12, 0, 0, 0, 0)
define DateTimeLessEqFalse: DateTime(2012, 2, 12, 1, 0, 0, 0) <= DateTime(2012, 2, 12, 0, 0, 0, 0)
define TimeLessEqTrue: @T10:00:00.001 <= @T10:00:00.002
define TimeLessEqTrue2: @T10:00:00.000 <= @T10:00:00.000
define TimeLessEqFalse: @T10:00:00.002 <= @T10:00:00.001

//Equivalent
define EquivTrueTrue                : true ~ true
define EquivTrueFalse               : true ~ false
define EquivFalseFalse              : false ~ false
define EquivFalseTrue               : false ~ true
//define EquivNullNull                : null ~ null
//define EquivTrueNull                : true ~ null
//define EquivNullTrue                : null ~ true
define EquivInt1Int1                : 1 ~ 1
define EquivInt1Int2                : 1 ~ 2
define EquivStringAStringA          : 'a' ~ 'a'
define EquivStringAStringB          : 'a' ~ 'b'
define EquivFloat1Float1            : 1.0 ~ 1.0
define EquivFloat1Float2            : 1.0 ~ 2.0
define EquivFloat1Int1              : 1.0 ~ 1
define EquivFloat1Int2              : 1.0 ~ 2
define EquivEqCM1CM1                : 1'cm' ~ 1'cm'
define EquivEqCM1M01                : 1'cm' ~ 0.01'm'
define EquivTupleJohnJohn           : Tuple { Id : 1, Name : 'John' } ~ Tuple { Id : 1, Name : 'John' }
define EquivTupleJohnJane           : Tuple { Id : 1, Name : 'John' } ~ Tuple { Id : 2, Name : 'Jane' }
define EquivTupleJohn1John2         : Tuple { Id : 1, Name : 'John' } ~ Tuple { Id : 2, Name : 'John' }
define EquivListEmptyEmpty          : {} ~ {}
define EquivListABCABC              : { 'a', 'b', 'c' } ~ { 'a', 'b', 'c' }
define EquivListABCAB               : { 'a', 'b', 'c' } ~ { 'a', 'b' }
define EquivListABC123              : { 'a', 'b', 'c' } ~ { 1, 2, 3 }
define EquivList123ABC              : { 1, 2, 3 } ~ { 'a', 'b', 'c' }
define EquivList123String123        : { 1, 2, 3 } ~  { '1', '2', '3' }
define EquivInterval1To101To10      : Interval[1, 10] ~ Interval[1, 10]
define EquivInterval1To101To5       : Interval[1, 10] ~ Interval[1, 5]
define EquivDateTimeTodayToday      : Today() ~ Today()
define EquivDateTimeTodayYesterday  : Today() ~ Today() - 1 days
define EquivTime10A10A              : @T10:00:00.000 ~ @T10:00:00.000
define EquivTime10A10P              : @T10:00:00.000 ~ @T22:00:00.000

//Not Equal
define SimpleNotEqTrueTrue : true != true
define SimpleNotEqTrueFalse : true != false
define SimpleNotEqFalseFalse : false != false
define SimpleNotEqFalseTrue : false != true
//define SimpleNotEqNullNull : null != null
// define SimpleNotEqTrueNull : true != null
//define SimpleNotEqNullTrue : null != true
define SimpleNotEqInt1Int1 : 1 != 1
define SimpleNotEqInt1Int2 : 1 != 2
define SimpleNotEqStringAStringA : 'a' != 'a'
define SimpleNotEqStringAStringB : 'a' != 'b'
define SimpleNotEqFloat1Float1 : 1.0 != 1.0
define SimpleNotEqFloat1Float2 : 1.0 != 2.0
define SimpleNotEqFloat1Int1 : 1.0 != 1
define SimpleNotEqFloat1Int2 : 1.0 != 2
define QuantityNotEqCM1CM1 : 1'cm' != 1'cm'
define QuantityNotEqCM1M01 : 1'cm' != 0.01'm'
define TupleNotEqJohnJohn: Tuple{ Id : 1, Name : 'John' } != Tuple{ Id : 1, Name : 'John' }
define TupleNotEqJohnJane: Tuple{ Id : 1, Name : 'John' } != Tuple{ Id : 2, Name : 'Jane' }
define TupleNotEqJohn1John2: Tuple{ Id : 1, Name : 'John' } != Tuple{ Id : 2, Name : 'John' }
define ListNotEqEmptyEmpty : {} != {}
define ListNotEqABCABC : { 'a', 'b', 'c' } != { 'a', 'b', 'c' }
define ListNotEqABCAB : { 'a', 'b', 'c' } != { 'a', 'b' }
define ListNotEqABC123: { 'a', 'b', 'c' } != { 1, 2, 3 }
define ListNotEq123ABC: { 1, 2, 3 } != { 'a', 'b', 'c' }
define ListNotEq123String123:  { 1, 2, 3 } != { '1', '2', '3' }
define IntervalNotEq1To101To10: Interval[1, 10] != Interval[1, 10]
define IntervalNotEq1To101To5: Interval[1, 10] != Interval[1, 5]
define DateTimeNotEqTodayToday: Today() != Today()
define DateTimeNotEqTodayYesterday: Today() != Today() - 1 days
define TimeNotEq10A10A: @T10:00:00.000 != @T10:00:00.000
define TimeNotEq10A10P: @T10:00:00.000 != @T22:00:00.000
