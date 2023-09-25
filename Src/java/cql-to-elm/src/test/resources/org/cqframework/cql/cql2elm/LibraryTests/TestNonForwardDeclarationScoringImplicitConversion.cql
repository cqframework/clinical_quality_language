library TestForwardDeclaration

define function calledFunc(value System.String): null
define function calledFunc(value System.Quantity): null

define function toString():
   calledFunc(System.Ratio{numerator: 1, denominator: 2})

// LUKETODO:  come up with better example
    /*
define function things():
   System.Ratio{numerator: 1, denominator: 2}
     */
