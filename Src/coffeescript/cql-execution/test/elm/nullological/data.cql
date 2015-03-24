// @Test: Nil
define Nil: null

// @Test: IsNull
define Nil: null
define One: 1
define NullIsNull: IsNull(null)
define NullVarIsNull: IsNull(Nil)
define StringIsNull: IsNull('')
define NonNullVarIsNull: IsNull(One)

// @Test: Coalesce
define NullNullHelloNullWorld: Coalesce(null as String, null as String, 'Hello', null as String, 'World')
define FooNullNullBar: Coalesce('Foo', null as String, null as String, 'Bar')
define AllNull: Coalesce(null as String, null as String, null as String)
