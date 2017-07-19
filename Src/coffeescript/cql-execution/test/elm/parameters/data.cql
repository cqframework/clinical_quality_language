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
parameter FooDP default true
define Foo: FooP
define Foo2: FooDP

// @Test: DecimalParameterTypes
parameter FooP Decimal
parameter FooDP default 1.5
define Foo: FooP
define Foo2: FooDP

// @Test: IntegerParameterTypes
parameter FooP Integer
parameter FooDP default 2
define Foo: FooP
define Foo2: FooDP

// @Test: StringParameterTypes
parameter FooP String
parameter FooDP default 'Hello'
define Foo: FooP
define Foo2: FooDP

// @Test: ConceptParameterTypes
codesystem "FOOTESTCS": 'http://footest.org'
parameter FooP Concept
parameter FooDP default Concept { Code 'FooTest' from "FOOTESTCS" } display 'Foo Test'
define Foo: FooP
define Foo2: FooDP

// @Test: DateTimeParameterTypes
parameter FooP DateTime
parameter FooDP default @2012-04-01
define Foo: FooP
define Foo2: FooDP

// @Test: QuantityParameterTypes
parameter FooP Quantity
parameter FooDP default 10 'dL'
define Foo: FooP
define Foo2: FooDP

// @Test: TimeParameterTypes
parameter FooP Time
parameter FooDP default @T12:00:00
define Foo: FooP
define Foo2: FooDP

// @Test: ListParameterTypes
parameter FooP List<String>
parameter FooDP default { 'a', 'b', 'c' }
define Foo: FooP
define Foo2: FooDP

// @Test: IntervalParameterTypes
parameter FooP Interval<Integer>
parameter FooDP default Interval[2,6]
define Foo: FooP
define Foo2: FooDP

// @Test: TupleParameterTypes
parameter FooP Tuple { Hello String, MeaningOfLife Integer }
parameter FooDP default Tuple { Hello: 'Universe', MeaningOfLife: 24 }
define Foo: FooP
define Foo2: FooDP

// @Test: DefaultAndNoDefault
parameter FooWithNoDefault Integer
parameter FooWithDefault default 5
define Foo: FooWithNoDefault
define Foo2: FooWithDefault