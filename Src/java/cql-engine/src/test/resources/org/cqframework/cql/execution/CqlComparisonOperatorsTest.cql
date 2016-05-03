library ComparisonOperator version '1'

context Population

//Between

//Equal
define SimpleTrueTrue : Equal(true, true)
define SimpleTrueFalse : Equal(true, false)
define SimpleFalseFalse : Equal(false, false)
define SimpleFalseTrue : Equal(false, true)
define SimpleNullNull : Equal(null, null)
define SimpleTrueNull : Equal(true, null)
define SimpleNullTrue : Equal(null, true)
define SimpleInt1Int1 : Equal(1, 1)
define SimpleInt1Int2 : Equal(1, 2)
define SimpleStringAStringA : Equal("a","a")
define SimpleStringAStringB : Equal("a","b")
define SimpleCharACharA : Equal('a','a')
define SimpleCharACharB : Equal('a','b')
define SimpleFloat1Float1 : Equal(1.0,1.0)
define SimpleFloat1Float2 : Equal(1.0,2.0)
define SimpleFloat1Int1 : Equal(1.0,1)
define SimpleFloat1Int2 : Equal(1.0,2)
define QuantityCM1CM1 : Equal(1'cm',1'cm')
define QuantityCM1M01 : Equal(1'cm',0.01'm')
define TupleJohnJohn: Tuple { Id : 1, Name : 'John' } = Tuple { Id : 1, Name : 'John' }
define TupleJohnJane: Tuple { Id : 1, Name : 'John' } = Tuple { Id : 2, Name : 'Jane' }
define TupleJohn1John2: Tuple { Id : 1, Name : 'John' } = Tuple { Id : 2, Name : 'John' }
define ListEmptyEmpty : Equal({}, {})
define ListABCABC : Equal({ "a", "b", "c" }, { "a", "b", "c" })
define ListABCAB : Equal({ "a", "b", "c" }, { "a", "b" })
define ListABC123: Equal({ "a", "b", "c" }, { 1, 2, 3 })
define List123ABC: Equal({ 1, 2, 3 }, { "a", "b", "c" })
define ListABCabc:  Equal({ "a", "b", "c" },  { 'a', 'b', 'c' })
define List123String123:  Equal({ 1, 2, 3 },  { "1", "2", "3"})
define IntervalEqual: Interval[1, 10] = Interval[1, 10]
define DateTimeEqual: DateTime(2014, 1, 1) = DateTime(2014, 1, 1)
define TimeEqual: Time(10, 0, 0) = Time(10, 0, 0)

//Greater

//Greater Or Equal

//Less

//Less Or Equal

//Equivalent

//Not Equal