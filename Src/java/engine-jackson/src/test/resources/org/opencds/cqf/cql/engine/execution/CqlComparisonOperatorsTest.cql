library ComparisonOperator version '1'

//Between
define BetweenIntTrue: 4 between 2 and 6

//Equal
define SimpleEqTrueTrue : true = true
define SimpleEqTrueFalse : true = false
define SimpleEqFalseFalse : false = false
define SimpleEqFalseTrue : false = true
define SimpleEqNullNull : null as String = null
define SimpleEqTrueNull : true = null
define SimpleEqNullTrue : null = true
define SimpleEqInt1Int1 : 1 = 1
define SimpleEqInt1Int2 : 1 = 2
define SimpleEqInt1Int2Long : 10L = 20L
define SimpleEqStringAStringA : 'a' = 'a'
define SimpleEqStringAStringB : 'a' = 'b'
define SimpleEqFloat1Float1 : 1.0 = 1.0
define SimpleEqFloat1Float2 : 1.0 = 2.0
define SimpleEqFloat1Float1WithZ : 1.0 = 1.00
define SimpleEqFloat1Float1WithPrecisionAndZ : 1.50 = 1.55
define SimpleEqFloat1Int1 : 1.0 = 1
define SimpleEqFloat1Int2 : 1.0 = 2
define QuantityEqCM1CM1 : 1'cm' = 1'cm'
define QuantityEqCM1M01 : 1'cm' = 0.01'm'
define QuantityEqDiffPrecision: 2.0'cm' = 2.00'cm'
define RatioEqual: 1'cm':2'cm' = 1'cm':2'cm'
define RatioNotEqual: 1'cm':2'cm' = 1.1'cm':2'cm'
define TupleEqJohnJohn: Tuple { Id : 1, Name : 'John' } = Tuple { Id : 1, Name : 'John' }
define TupleEqJohnJohnFalse: Tuple { Id : 1, Name : 'John', Position: 'Shift Manager' } = Tuple { Id : 1, Name : 'John' }
define TupleEqJohnJohnFalse2: Tuple { Id : 1, Name : 'John' } = Tuple { Id : 1, Name : 'John', Position: 'Shift Manager' }
define TupleEqJohnJane: Tuple { Id : 1, Name : 'John' } = Tuple { Id : 2, Name : 'Jane' }
define TupleEqJohn1John2: Tuple { Id : 1, Name : 'John' } = Tuple { Id : 2, Name : 'John' }
define TupleEqDateTimeTrue: Tuple { dateId: 1, Date: DateTime(2012, 10, 5, 0, 0, 0, 0) } = Tuple { dateId: 1, Date: DateTime(2012, 10, 5, 0, 0, 0, 0) }
define TupleEqDateTimeFalse: Tuple { dateId: 1, Date: DateTime(2012, 10, 5, 0, 0, 0, 0) } = Tuple { dateId: 1, Date: DateTime(2012, 10, 5, 5, 0, 0, 0) }
define TupleEqTimeTrue: Tuple { timeId: 55, TheTime: @T05:15:15.541 } = Tuple { timeId: 55, TheTime: @T05:15:15.541 }
define TupleEqTimeFalse: Tuple { timeId: 55, TheTime: @T05:15:15.541 } = Tuple { timeId: 55, TheTime: @T05:15:15.540 }
define DateTimeEqTodayToday: Today() = Today()
define DateTimeEqTodayYesterday: Today() = Today() - 1 days
define DateTimeEqJanJan: DateTime(2014, 1, 5, 5, 0, 0, 0, 0) = DateTime(2014, 1, 5, 5, 0, 0, 0, 0)
define DateTimeEqJanJuly: DateTime(2014, 1, 5, 5, 0, 0, 0, 0) = DateTime(2014, 7, 5, 5, 0, 0, 0, 0)
define DateTimeEqNull: DateTime(null) = DateTime(null)
define DateTimeUTC: @2014-01-25T14:30:14.559+01:00 = @2014-01-25T14:30:14.559+01:00
define TimeEq10A10A: @T10:00:00.000 = @T10:00:00.000
define TimeEq10A10P: @T10:00:00.000 = @T22:00:00.000

//Greater
define GreaterZZ        : 0 > 0
define GreaterZ1        : 0 > 1
define GreaterLong      : 00L > 10L
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
define UncertaintyGreaterNull: DateTime(2014) > DateTime(2014, 2, 15)
define UncertaintyGreaterTrue: DateTime(2015) > DateTime(2014, 2, 15)
define UncertaintyGreaterFalse: DateTime(2013) > DateTime(2014, 2, 15)

//Greater Or Equal
define GreaterOrEqualZZ        : 0 >= 0
define GreaterOrEqualZ1        : 0 >= 1
define GreaterOrEqualZ1Long    : 00L >= 10L
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
define UncertaintyGreaterEqualNull: DateTime(2014) >= DateTime(2014, 2, 15)
define UncertaintyGreaterEqualTrue: DateTime(2015) >= DateTime(2014, 2, 15)
define UncertaintyGreaterEqualFalse: DateTime(2013) >= DateTime(2014, 2, 15)

//Less
define LessZZ        : 0 < 0
define LessZ1        : 0 < 1
define LessLong      : 00L < 10L
define LessLongNeg   : -30L < -20L
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
define UncertaintyLessNull: DateTime(2014) < DateTime(2014, 2, 15)
define UncertaintyLessTrue: DateTime(2013) < DateTime(2014, 2, 15)
define UncertaintyLessFalse: DateTime(2015) < DateTime(2014, 2, 15)

//Less Or Equal
define LessOrEqualZZ        : 0 <= 0
define LessOrEqualZ1        : 0 <= 1
define LessOrEqualZ1Long    : 00L <= 10L
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
define UncertaintyLessEqualNull: DateTime(2014) <= DateTime(2014, 2, 15)
define UncertaintyLessEqualTrue: DateTime(2013) <= DateTime(2014, 2, 15)
define UncertaintyLessEqualFalse: DateTime(2015) <= DateTime(2014, 2, 15)

//Equivalent
define EquivTrueTrue                      : true ~ true
define EquivTrueFalse                     : true ~ false
define EquivFalseFalse                    : false ~ false
define EquivFalseTrue                     : false ~ true
define EquivNullNull                      : null as String ~ null
define EquivTrueNull                      : true ~ null
define EquivNullTrue                      : null ~ true
define EquivInt1Int1                      : 1 ~ 1
define EquivInt1Int2                      : 1 ~ 2
define EquivStringAStringA                : 'a' ~ 'a'
define EquivStringAStringB                : 'a' ~ 'b'
define EquivStringIgnoreCase              : 'Abel' ~ 'abel'
define EquivFloat1Float1                  : 1.0 ~ 1.0
define EquivFloat1Float2                  : 1.0 ~ 2.0
define EquivFloat1Float1WithZ             : 1.0 ~ 1.00
define EquivFloat1Float1WithPrecision     : 1.5 ~ 1.55
define EquivFloat1Float1WithPrecisionAndZ : 1.50 ~ 1.55
define EquivFloatTrailingZero             : 1.001 ~ 1.000
define EquivFloat1Int1                    : 1.0 ~ 1
define EquivFloat1Int2                    : 1.0 ~ 2
define EquivEqCM1CM1                      : 1'cm' ~ 1'cm'
define EquivEqCM1M01                      : 1'cm' ~ 0.01'm'
define RatioEquivalent                    : 1'cm':2'cm' ~ 1'cm':2'cm'
define RatioNotEquivalent                 : 1'cm':2'cm' ~ 1'cm':3'cm'
define EquivTupleJohnJohn                 : Tuple { Id : 1, Name : 'John' } ~ Tuple { Id : 1, Name : 'John' }
define EquivTupleJohnJohnWithNulls        : Tuple { Id : 1, Name : 'John', Position: null } ~ Tuple { Id : 1, Name : 'John', Position: null }
define EquivTupleJohnJohnFalse            : Tuple { Id : 1, Name : 'John', Position: 'Shift Manager' } ~ Tuple { Id : 1, Name : 'John' }
define EquivTupleJohnJohnFalse2           : Tuple { Id : 1, Name : 'John' } ~ Tuple { Id : 1, Name : 'John', Position: 'Shift Manager' }
define EquivTupleJohnJane                 : Tuple { Id : 1, Name : 'John' } ~ Tuple { Id : 2, Name : 'Jane' }
define EquivTupleJohn1John2               : Tuple { Id : 1, Name : 'John' } ~ Tuple { Id : 2, Name : 'John' }
define EquivDateTimeTodayToday            : Today() ~ Today()
define EquivDateTimeTodayYesterday        : Today() ~ Today() - 1 days
define EquivTime10A10A                    : @T10:00:00.000 ~ @T10:00:00.000
define EquivTime10A10P                    : @T10:00:00.000 ~ @T22:00:00.000

//Not Equal
define SimpleNotEqTrueTrue : true != true
define SimpleNotEqTrueFalse : true != false
define SimpleNotEqFalseFalse : false != false
define SimpleNotEqFalseTrue : false != true
define SimpleNotEqNullNull : null as String != null
define SimpleNotEqTrueNull : true != null
define SimpleNotEqNullTrue : null != true
define SimpleNotEqInt1Int1 : 1 != 1
define SimpleNotEqInt1Int2 : 1 != 2
define SimpleNotEqInt1Int2Long : 10L != 20L
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
define DateTimeNotEqTodayToday: Today() != Today()
define DateTimeNotEqTodayYesterday: Today() != Today() - 1 days
define TimeNotEq10A10A: @T10:00:00.000 != @T10:00:00.000
define TimeNotEq10A10P: @T10:00:00.000 != @T22:00:00.000
