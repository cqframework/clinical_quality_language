// Not a real code system, made up for this test
codesystem "Booleans": 'boolean'

define TrueCode: Code { code: 'T', system: 'boolean' }
define FalseCode: Code { code: 'F', system: 'boolean' }
define ListOfCodes: { TrueCode, FalseCode }

define InListOfCodes: ListOfCodes in "Booleans"

define InCode: TrueCode in "Booleans"

define InCodeSystemInvocation: InCodeSystem(TrueCode, "Booleans")
define AnyInCodeSystemInvocation: AnyInCodeSystem(ListOfCodes, "Booleans")