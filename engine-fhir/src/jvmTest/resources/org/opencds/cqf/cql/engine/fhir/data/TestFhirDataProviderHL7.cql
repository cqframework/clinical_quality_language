library TestFhirDataProviderHL7 version '1.0'

using FHIR version '1.0.2'

define testString:
    [Encounter] E where E.status.value = 'arrived'

define testCode:
    [Procedure] P where P.code.coding[0].code.value = '93000'

define testDate:
    [ProcessRequest] A where A.created.value before year of DateTime(2014, 10, 10)

define testDecimal:
    [MedicationDispense] MD
        where MD.quantity.value.value >= 15.0

define testIBaseDataElement:
    [MedicationOrder] MO
        where MO.dosageInstruction.timing is not null