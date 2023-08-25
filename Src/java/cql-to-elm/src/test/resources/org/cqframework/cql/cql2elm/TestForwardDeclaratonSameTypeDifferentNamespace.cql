library TestForwardDeclarionSameTypeDifferentNamespace

using FHIR version '4.0.1'

define function toString(value FHIR.Code) : null

    /*
// define function toString(value List<System.Code>):
define function toString(value List<System.Code>):
    Combine(
        value C return 'Code: ' + C.display,
        ', '
    )

     */
// define function toString(value List<FHIR.Code>):
// define function toString(value List<FHIR.Code>) : null

// define function toString(value System.Code) : null
