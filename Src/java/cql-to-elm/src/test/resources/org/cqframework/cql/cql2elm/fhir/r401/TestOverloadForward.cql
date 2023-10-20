library TestOverloadForward

using FHIR version '4.0.1'

define "Encounter":
   Encounter {
      id: FHIR.id {value: '123'}
   }

define function Stringify(value FHIR.DomainResource):
   'Domain'

define "Test":
   Stringify(("Encounter" as FHIR.DomainResource))

define function Stringify(value FHIR.Encounter):
   'Encounter'

