library Functions version '2'

define function NilaryFunction(): true
define function UnaryFunction(operand String): operand
define function BinaryFunction(left Integer, right Integer): left + right

define NilaryFunctionCall: NilaryFunction()
define UnaryFunctionCall: UnaryFunction('Operand')
define BinaryFunctionCal: BinaryFunction(1, 1)

define function MatchConcept (c Concept, s String):
  exists ((c.codes) C where C.code = s)