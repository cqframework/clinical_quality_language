using QUICK

define CodeExpression : Code { code: '123', version: '2014' }
define ElementExpression : Element { id: '123' }
define TupleToClass : cast Tuple { id : '123' } as Element
define ClassToTuple : cast TupleToClass as Tuple { id String, extension List<Extension> }
define ImplicitClassToTuple : TupleToClass = Tuple { id : '123', extension : null as List<Extension> }



