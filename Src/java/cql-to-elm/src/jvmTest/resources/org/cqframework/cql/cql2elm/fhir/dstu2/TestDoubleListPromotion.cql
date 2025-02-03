library Test
using FHIR version '1.0.2'
codesystem test: 'test'
code T0: '0' from test

define Observations: [Observation: T0]