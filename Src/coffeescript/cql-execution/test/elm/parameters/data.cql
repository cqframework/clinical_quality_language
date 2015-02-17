// @Test: ParameterDef
parameter MeasureYear default 2012
parameter IntParameter : Integer
parameter ListParameter : list<String>
parameter TupleParameter : tuple{a : Integer, b : String, c : Boolean, d : list<Integer>, e : tuple{ f : String, g : Boolean}}

// @Test: ParameterRef
parameter FooP default 'Bar'
define Foo = FooP
