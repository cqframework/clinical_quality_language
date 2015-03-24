define function NilaryFunction() { return true }
define function UnaryFunction(operand String) { return operand }
define function BinaryFunction(left Integer, right Integer) { return left + right }

define NilaryFunctionCall: NilaryFunction()
define UnaryFunctionCall: UnaryFunction('Operand')
define BinaryFunctionCal: BinaryFunction(1, 1)
