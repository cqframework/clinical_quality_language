// @Test: ParameterDef
parameter MeasureYear default 2012
parameter IntParameter Integer
parameter ListParameter List<String>
parameter TupleParameter Tuple{a Integer, b String, c Boolean, d List<Integer>, e Tuple{ f String, g Boolean}}
define foo: "bar"

// @Test: ParameterRef
parameter FooP default 'Bar'
define Foo: FooP
