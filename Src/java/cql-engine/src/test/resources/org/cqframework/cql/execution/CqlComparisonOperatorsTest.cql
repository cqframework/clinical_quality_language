library ComparisonOperator version '1'

context Population

//Between

//Equal
define SimpleEqTrueTrue : Equal(true, true)
define SimpleEqTrueFalse : Equal(true, false)
define SimpleEqFalseFalse : Equal(false, false)
define SimpleEqFalseTrue : Equal(false, true)
define SimpleEqNullNull : Equal(null, null)
define SimpleEqTrueNull : Equal(true, null)
define SimpleEqNullTrue : Equal(null, true)
define SimpleEqInt1Int1 : Equal(1, 1)
define SimpleEqInt1Int2 : Equal(1, 2)
define SimpleEqStringAStringA : Equal('a','a')
define SimpleEqStringAStringB : Equal('a','b')
define SimpleEqFloat1Float1 : Equal(1.0,1.0)
define SimpleEqFloat1Float2 : Equal(1.0,2.0)
define SimpleEqFloat1Int1 : Equal(1.0,1)
define SimpleEqFloat1Int2 : Equal(1.0,2)
define QuantityEqCM1CM1 : Equal(1'cm',1'cm')
define QuantityEqCM1M01 : Equal(1'cm',0.01'm')
define TupleEqJohnJohn: Equal(Tuple { Id : 1, Name : 'John' }, Tuple { Id : 1, Name : 'John' })
define TupleEqJohnJane: Equal(Tuple { Id : 1, Name : 'John' }, Tuple { Id : 2, Name : 'Jane' })
define TupleEqJohn1John2: Equal(Tuple { Id : 1, Name : 'John' }, Tuple { Id : 2, Name : 'John' })
define ListEqEmptyEmpty : Equal({}, {})
define ListEqABCABC : Equal({ 'a', 'b', 'c' }, { 'a', 'b', 'c' })
define ListEqABCAB : Equal({ 'a', 'b', 'c' }, { 'a', 'b' })
define ListEqABC123: Equal({ 'a', 'b', 'c' }, { 1, 2, 3 })
define ListEq123ABC: Equal({ 1, 2, 3 }, { 'a', 'b', 'c' })
define ListEq123String123:  Equal({ 1, 2, 3 },  { '1', '2', '3' })
define IntervalEq1To101To10: Equal(Interval[1, 10], Interval[1, 10])
define IntervalEq1To101To5: Equal(Interval[1, 10], Interval[1, 5])
define DateTimeEqTodayToday: Equal(Today(), Today())
define DateTimeEqTodayYesterday: Equal(Today(), Today() - 1 day)
define TimeEq10A10A: Equal(Time(10, 0, 0), Time(10, 0, 0))
define TimeEq10A10P: Equal(Time(10, 0, 0), Time(22, 0, 0))

//Greater
define GreaterZZ : Greater(0,0)
define GreaterZ1 : Greater(0,1)
define GreaterZNeg1 : Greater(0,-1)
define GreaterDecZZ : Greater(0.0, 0.0)
define GreaterDecZ1 : Greater(0.0, 1.0)
define GreaterDecZNeg1 : Greater(0.0, -1.0)
define GreaterCM0CM0 : Greater(0'cm',0'cm')
define GreaterCM0CM1 : Greater(0'cm',1'cm')
define GreaterCM0NegCM1 : Greater(0'cm',-1'cm')
define QuantityEqM1CM1 : Greater(1'm',1'cm')
define QuantityEqM1CM10 : Greater(1'm',10'cm')

//Greater Or Equal

//Less

//Less Or Equal

//Equivalent

//Not Equal
define SimpleNotEqTrueTrue : NotEqual(true, true)
define SimpleNotEqTrueFalse : NotEqual(true, false)
define SimpleNotEqFalseFalse : NotEqual(false, false)
define SimpleNotEqFalseTrue : NotEqual(false, true)
define SimpleNotEqNullNull : NotEqual(null, null)
define SimpleNotEqTrueNull : NotEqual(true, null)
define SimpleNotEqNullTrue : NotEqual(null, true)
define SimpleNotEqInt1Int1 : NotEqual(1, 1)
define SimpleNotEqInt1Int2 : NotEqual(1, 2)
define SimpleNotEqStringAStringA : NotEqual('a','a')
define SimpleNotEqStringAStringB : NotEqual('a','b')
define SimpleNotEqFloat1Float1 : NotEqual(1.0,1.0)
define SimpleNotEqFloat1Float2 : NotEqual(1.0,2.0)
define SimpleNotEqFloat1Int1 : NotEqual(1.0,1)
define SimpleNotEqFloat1Int2 : NotEqual(1.0,2)
define QuantityNotEqCM1CM1 : NotEqual(1'cm',1'cm')
define QuantityNotEqCM1M01 : NotEqual(1'cm',0.01'm')
define TupleNotEqJohnJohn: NotEqual(Tuple { Id : 1, Name : 'John' }, Tuple { Id : 1, Name : 'John' })
define TupleNotEqJohnJane: NotEqual(Tuple { Id : 1, Name : 'John' }, Tuple { Id : 2, Name : 'Jane' })
define TupleNotEqJohn1John2: NotEqual(Tuple { Id : 1, Name : 'John' }, Tuple { Id : 2, Name : 'John' })
define ListNotEqEmptyEmpty : NotEqual({}, {})
define ListNotEqABCABC : NotEqual({ 'a', 'b', 'c' }, { 'a', 'b', 'c' })
define ListNotEqABCAB : NotEqual({ 'a', 'b', 'c' }, { 'a', 'b' })
define ListNotEqABC123: NotEqual({ 'a', 'b', 'c' }, { 1, 2, 3 })
define ListNotEq123ABC: NotEqual({ 1, 2, 3 }, { 'a', 'b', 'c' })
define ListNotEq123String123:  NotEqual({ 1, 2, 3 },  { '1', '2', '3' })
define IntervalNotEq1To101To10: NotEqual(Interval[1, 10], Interval[1, 10])
define IntervalNotEq1To101To5: NotEqual(Interval[1, 10], Interval[1, 5])
define DateTimeNotEqTodayToday: NotEqual(Today(), Today())
define DateTimeNotEqTodayYesterday: NotEqual(Today(), Today() - 1 day)
define TimeNotEq10A10A: NotEqual(Time(10, 0, 0), Time(10, 0, 0))
define TimeNotEq10A10P: NotEqual(Time(10, 0, 0), Time(22, 0, 0))