library TestHidden


// Should result in a "Hiding operand identifier"
define function TestOperandHiding(testOperand Integer):
  "TestList" testOperand
    where testOperand.id = 1