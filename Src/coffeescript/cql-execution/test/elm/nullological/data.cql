// @Test: Nil
define Nil = null

// @Test: IsNull
define Nil = null
define One = 1
define NullIsNull = IsNull(null)
define NullVarIsNull = IsNull(Nil)
define StringIsNull = IsNull('')
define NonNullVarIsNull = IsNull(One)

// @Test: IfNull
define NullAndA = IfNull(null, 'a')
define ZeroAndB = IfNull(0, 1)
define BothNull = IfNull(null, null)

// @Test: Coalesce
define NullNullHelloNullWorld = Coalesce(null, null, 'Hello', null, 'World')
define FooNullNullBar = Coalesce('Foo', null, null, 'Bar')
define AllNull = Coalesce(null, null, null)
