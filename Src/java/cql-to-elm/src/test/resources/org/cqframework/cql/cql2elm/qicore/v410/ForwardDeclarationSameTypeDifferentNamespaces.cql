library TestForwardDeclaration
// This is to test that forward declartions for overloaded functions works as expected.

define function toString(value Concept):
  if value is null
    then 'null'
  // The reference here to toString(value List<System.Code>) should _not_ result in a circular reference error
  // The function signature is distinct, therefore is should be considered a different function.
  else 'Concept { ' + toString(value.codes) + ' display: ' + value.display + ' }'

// define function toString(value List<System.Code>):
define function toString(value List<System.Code>):
    Combine(
        value C return 'Code: ' + C.display,
        ', '
    )

// define function toString(value List<FHIR.Code>):
define function toString(value List<FHIR.Code>) : null

