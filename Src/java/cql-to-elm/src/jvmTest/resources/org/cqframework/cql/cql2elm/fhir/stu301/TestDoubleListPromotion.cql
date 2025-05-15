library Test
using FHIR version '3.0.1'
codesystem test: 'test'
code T0: '0' from test

define Observations: [Observation: T0]