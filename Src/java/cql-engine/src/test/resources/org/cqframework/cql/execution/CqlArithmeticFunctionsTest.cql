library ArithmeticFunctions version '1'

context Population

//Abs
define AbsEmpty: Abs()
define AbsNull: Abs(null)
define Abs0: Abs(0)
define AbsNeg1: Abs(-1)
define AbsNeg1Dec: Abs(-1.0)
define Abs0Dec: Abs(0.0)
define Abs1cm: Abs(-1.0'cm')

//Add
define AddEmpty: Add()
define AddNull: Add(null)
define Add11: 1 + 1
define Add1D1D: 1.0 + 1.0
define Add1Q1Q: 1'g/cm3' + 1'g/cm3'

//Ceiling
define CeilingEmpty: Ceiling()
define CeilingNull: Ceiling(null)
define Ceiling1: Ceiling(1)
define Ceiling1D: Ceiling(1.0)
define Ceiling1D1: Ceiling(1.1)
define CeilingNegD1: Ceiling(-0.1)
define CeilingNeg1: Ceiling(-1.0)
define CeilingNeg1D1: Ceiling(-1.1)

//Divide
define DivideEmpty: Divide()
define DivideNull: Divide(null)
define Divide10: 1 / 0
define Divide01: 0 / 1
define Divide11: 1 / 1
define Divide1d1d: 1.0 / 1.0
define Divide103: 10 / 3
define Divide1Q1: 1'g/cm3' / 1.0
define Divide1Q1Q: 1'g/cm3' / 1'g/cm3'

//Floor
define FloorEmpty: Floor()
define FloorNull: Floor(null)
define Floor1: Floor(1)
define Floor1D: Floor(1.0)
define Floor1D1: Floor(1.1)
define FloorNegD1: Floor(-0.1)
define FloorNeg1: Floor(-1.0)
define FloorNeg1D1: Floor(-1.1)

//Exp
define ExpEmpty  : Exp()
define ExpNull  : Exp(null)
define Exp0  : Exp(0)
define ExpNeg0  : Exp(-0)
define Exp1  : Exp(1)
define ExpNeg1  : Exp(-1)
define Exp1000  : Exp(1000)
define Exp1000D  : Exp(1000.0)

//Log
define LogEmpty  : Log()
define LogNullNull  : Log(null, null)
define Log1BaseNull  : Log(1, null)
define Log1Base1  : Log(1, 1)
define Log1Base2  : Log(1, 2)
define Log1Base100  : Log(1, 100)
define Log16Base2  : Log(16, 2)
define LogD125Base2  : Log(0.125, 2)


//Ln
define LnEmpty  : Ln()
define LnNull  : Ln(null)
define Ln0  : Ln(0)
define LnNeg0  : Ln(-0)
define Ln1  : Ln(1)
define LnNeg1  : Ln(-1)
define Ln1000  : Ln(1000)
define Ln1000D  : Ln(1000.0)

//Maximum

//Minimum

//Modulo
define ModuloEmpty  : Modulo()
define ModuloNull  : Modulo(null)
define Modulo0By0  : Modulo(0, 0)
define Modulo4By2  : Modulo(4, 2)
define Modulo4DBy2D  : Modulo(4.0, 2.0)
define Modulo10By3  : Modulo(10, 3)
define Modulo10DBy3D  : Modulo(10.0, 3.0)
define Modulo4CMBy2CM  : Modulo(4'cm', 2'cm')

//Multiply
define MultiplyEmpty: Multiply()
define MultiplyNull: Multiply(null)
define Multiply1By1 : 1 * 1

//Negate
define NegateEmpty: Negate()
define NegateNull: Negate(null)
define Negate0 : Negate(0)
define NegateNeg0 : Negate(-0)
define Negate1 : Negate(1)
define NegateNeg1 : Negate(-1)
define Negate0D : Negate(0.0)
define NegateNeg0D : Negate(-0.0)
define Negate1D : Negate(1.0)
define NegateNeg1D : Negate(-1.0)
define Negate1CM : Negate(1'cm')

//Predecessor
define PredecessorEmpty: Predecessor()
define PredecessorNull: Predecessor(null)
define PredecessorOf0: Predecessor(0)
define PredecessorOf1: Predecessor(1)
define PredecessorOf1D: Predecessor(1.0)
define PredecessorOf101D: Predecessor(1.01)
define PredecessorOfJan12000: Predecessor(DateTime(2000,1,1))
define PredecessorOfNoon: Predecessor(Time(12,0,0))

//Power

//Round

//Subtract
define SubtractEmpty: Subtract()
define SubtractNull: Subtract(null)
define Subtract1And1 : 1 - 1

//Successor

//Truncate

//Truncated Divide
