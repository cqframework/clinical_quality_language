using QUICK

define TupleToClass = cast tuple { id : "123" as id_primitive } as Element
define ClassToTuple = cast TupleToClass as tuple { id : id_primitive, extension : list<Extension> }
define ImplicitClassToTuple = TupleToClass = tuple { id : "123" as id_primitive, extension : null as list<Extension> }



