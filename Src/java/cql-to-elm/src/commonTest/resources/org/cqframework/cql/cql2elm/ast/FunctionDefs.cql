library FunctionDefs version '1.0.0'
using System
context Patient

define function NilaryFunction(): true
define function UnaryFunction(operand String): operand
define function BinaryFunction(left Integer, right Integer): left + right

define NilaryFunctionCall: NilaryFunction()
define UnaryFunctionCall: UnaryFunction('Operand')
define BinaryFunctionCall: BinaryFunction(1, 1)
