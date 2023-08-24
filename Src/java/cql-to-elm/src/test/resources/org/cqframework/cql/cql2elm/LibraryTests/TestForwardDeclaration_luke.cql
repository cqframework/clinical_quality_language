library TestForwardDeclaration

// This is to test that forward declartions for overloaded functions works as expected.

//    define "testMe" : 2 + 2

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

    /*
// define function thing(value Concept, aString String):
define function toString(value Concept, aString String):
    Combine(
        value C return 'Code: ' + C.display,
        ', '
    )
     */

// value List<System.Code>, aString String)
define function toString(value List<System.Code>, aString String):
    Combine(
        value C return 'Code: ' + C.display,
        ', '
    )

/*
// define function thing(value List<String>):
define function toString(value List<System.String>):
    null


// TODO:  test this from a test that already imports FHIR
define function toString(value List<FHIR.Code>):
null

// define function thing(value Date):
define function toString(value Date):
''
 */

// TODO:  add a bunch of other overloaded declarations