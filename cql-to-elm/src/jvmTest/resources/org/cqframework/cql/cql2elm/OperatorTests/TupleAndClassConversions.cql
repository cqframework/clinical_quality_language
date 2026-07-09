using QUICK

define CodeExpression: Code { code: '123', version: '2014' }
define ElementExpression: Element { id: '123' }
define TupleToClass: convert Tuple { id: '123', extension: List<Extension> { } } to Element
define ClassToTuple: convert TupleToClass to Tuple { id String, extension List<Extension> }
define ImplicitClassToTuple: TupleToClass = Tuple { id: '123', extension: null as List<Extension> }
define EmptyTuple: { : }


