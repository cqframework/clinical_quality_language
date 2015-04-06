define function NilaryFunction(): true
define function UnaryFunction(operand String): operand
define function BinaryFunction(left Integer, right Integer): left + right

define NilaryFunctionCall: NilaryFunction()
define UnaryFunctionCall: UnaryFunction('Operand')
define BinaryFunctionCal: BinaryFunction(1, 1)
