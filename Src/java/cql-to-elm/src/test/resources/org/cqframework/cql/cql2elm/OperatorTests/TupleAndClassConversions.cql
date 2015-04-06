using QUICK

define CodeExpression : Code { code: '123', version: '2014' }
define ElementExpression : Element { id: '123' as id_primitive }
define TupleToClass : cast Tuple { id : '123' as id_primitive } as Element
define ClassToTuple : cast TupleToClass as Tuple { id id_primitive, extension List<Extension> }
define ImplicitClassToTuple : TupleToClass = Tuple { id : '123' as id_primitive, extension : null as List<Extension> }



