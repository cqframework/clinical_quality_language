library ComplexXResourceSearch version '1'

using FHIR

valueset "Diabetes": '2.16.840.1.113883.3.464.1003.103.12.1001'

context Patient

define XSearchUsingMultipleSources:
    from [Condition: "Diabetes"] C,
         [Observation: "Diabetes"] O
    where
        C.code.coding[0].code.value = '190390000' and O.code.coding[0].code.value = '190390000'

define getCondition:
    XSearchUsingMultipleSources X
        return X.C

define firstCondition:
    First(getCondition C)

define conditionCode:
    firstCondition F
        return F.code.coding[0].code.value

define getObservation:
    XSearchUsingMultipleSources X
            return X.O

define firstObservation:
    First(getObservation O)

define observationCode:
    firstObservation F
        return F.code.coding[0].code.value