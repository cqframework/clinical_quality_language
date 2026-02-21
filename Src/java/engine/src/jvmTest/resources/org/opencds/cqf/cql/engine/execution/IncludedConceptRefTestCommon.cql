library IncludedConceptRefTestCommon

codesystem testSystem: 'http://system.org' version '1'

code testCode: 'code-value' from testSystem display 'code-display'

concept testConcept: { testCode } display 'concept-display'