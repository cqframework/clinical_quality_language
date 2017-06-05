// @Test: ParameterDef
parameter MeasureYear default 2012
parameter IntParameter Integer
parameter ListParameter List<String>
parameter TupleParameter Tuple{a Integer, b String, c Boolean, d List<Integer>, e Tuple{ f String, g Boolean}}
define foo: 'bar'

// @Test: ParameterRef
parameter FooP default 'Bar'
define Foo: FooP

// @Test: BooleanParameterTypes
parameter FooP Boolean
define Foo: FooP

// @Test: DecimalParameterTypes
parameter FooP Decimal
define Foo: FooP

// @Test: IntegerParameterTypes
parameter FooP Integer
define Foo: FooP

// @Test: StringParameterTypes
parameter FooP String
define Foo: FooP

// @Test: ConceptParameterTypes
parameter FooP Concept
define Foo: FooP

// @Test: DateTimeParameterTypes
parameter FooP DateTime
define Foo: FooP

// @Test: QuantityParameterTypes
parameter FooP Quantity
define Foo: FooP

// @Test: TimeParameterTypes
parameter FooP Time
define Foo: FooP

// @Test: ListParameterTypes
parameter FooP List<String>
define Foo: FooP

// @Test: IntervalParameterTypes
parameter FooP Interval<Integer>
define Foo: FooP

// @Test: TupleParameterTypes
parameter FooP Tuple { Hello String, MeaningOfLife Integer }
define Foo: FooP