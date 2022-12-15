library CqlExternalFunctionsTest version '2'

// MyMath2

define function MyTimes(x Integer, y Integer) returns Integer : external

define CallMyTimes: MyTimes(18,3)

define function MyDividedBy(x Integer, y Integer) returns Integer : external

define CallMyDividedBy: MyDividedBy(18,3)
