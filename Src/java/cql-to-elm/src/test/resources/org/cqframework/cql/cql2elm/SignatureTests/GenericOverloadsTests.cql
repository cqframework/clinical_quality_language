library GenericOverloadsTests

using FHIR version '4.0.1'

define "Encounter":
   Encounter {
      id: FHIR.id {value: '123'}
   }

define function Stringify(value List<FHIR.Encounter>, value2 List<FHIR.DomainResource>):
   'Encounter / Domain'

define function Stringify(value List<FHIR.DomainResource>, value2 List<FHIR.Encounter>):
   'Domain / Encounter'

define function Stringify(value List<FHIR.Encounter>, value2 List<FHIR.Encounter>):
   'Encounter / Encounter'

define function Stringify(value List<FHIR.DomainResource>, value2 List<FHIR.DomainResource>):
   'Domain / Domain'

define "Test":
   Stringify({"Encounter"}, {("Encounter" as FHIR.DomainResource)})