define function NilaryFunction(): true
define function UnaryFunction(operand String): operand
define function BinaryFunction(left Integer, right Integer): left + right

define NilaryFunctionCall: NilaryFunction()
define UnaryFunctionCall: UnaryFunction('Operand')
define BinaryFunctionCal: BinaryFunction(1, 1)

define function MatchConcept (c Concept, s String):
  exists ((c.codes) C where C.code = s)

define function TypedFunction(left Integer, right Integer) returns Integer : left + right
define function ExternalFunction(left Integer, right Integer) returns Integer : external
