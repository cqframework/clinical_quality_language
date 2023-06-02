library CqlExternalFunctionsTest version '1'

// MyMath

define function MyPlus(x Integer, y Integer) returns Integer : external

define CallMyPlus: MyPlus(4,6)

define function MyMinus(x Integer, y Integer) returns Integer : external

define CallMyMinus: MyMinus(4,6)
