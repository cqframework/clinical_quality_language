library CqlConceptTest

codesystem system1: 'http://system-1.org' version '1'
codesystem systemA: 'http://system-a.org' version 'a'

code code123: '123-value' from system1 display '123-display'
code code234: '234-value' from system1 display '234-display'
code codeAbc: 'abc-value' from systemA display 'abc-display'

concept testConcept: { code123, code234, codeAbc } display 'test-concept-display'

define testConceptRef: testConcept