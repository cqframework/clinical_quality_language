library TestForwardDeclarionSameTypeDifferentNamespace

using FHIR version '4.0.1'

define function toString(value Concept):
  if value is null
    then 'null'
  else toString(ToQuantity(1))

define function toString(value System.Quantity) : ''
define function toString(value FHIR.Quantity) : ''

