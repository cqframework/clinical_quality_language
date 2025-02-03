library TestHiddenIdentifierArgumentToAlias

//// Should result in a "Hiding operand identifier"
define function TestOperandHiding(testOperand Integer):
    ({1, 2, 3}) testOperand
        return testOperand
