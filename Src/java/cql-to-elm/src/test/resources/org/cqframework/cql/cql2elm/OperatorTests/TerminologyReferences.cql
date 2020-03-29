library TerminologyReferences

using FHIR version '4.0.1'

codesystem LOINC: 'http://loinc.org'

valueset Test1: 'http://example.org/fhir/ValueSet/test1'
valueset Test2: 'http://example.org/fhir/ValueSet/test2'
valueset Test3: 'http://example.org/fhir/ValueSet/test3'
valueset Test4: 'http://example.org/fhir/ValueSet/test4'

define function EncountersInValueSet(Test valueset):
  [Encounter: Test]

define function EncountersInCodeSystem(Test codesystem):
  [Encounter: Test]

define Encounters:
  EncountersInValueSet(Test1)
    union EncountersInValueSet(Test2)
    union EncountersInValueSet(Test3)
    union EncountersInValueSet(Test4)

