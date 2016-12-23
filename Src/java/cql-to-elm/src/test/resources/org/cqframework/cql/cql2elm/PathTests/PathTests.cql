library PathTests version '1'

using FHIR version '1.8'

context Patient

define TestPathTraversal: Patient.name.given
define TestFirst: Patient.name.first()
define TestWhere: Patient.name.where(use.value = 'official')
define TestToListConversion: exists(Patient.birthDate)
define TestFromListConversion: Patient.name.given.value + ' ' + Patient.name.family.value

// Could run this by either demoting the left operand or promoting the right operand
// Need the algorithm to favor demotion...
// The instantiation favors promotion because it encounters the list type first
// But if during instantiation, we find that a type parameter has been bound to a list and the signature contains a list of the same type
// The type parameter should be demoted
define TestGenericFromListConversion: { 1 } in { 1, 2, 3, 4, 5 }
define TestListInListOfLists: { 1 } in { { 1 }, { 2 }, { 3 }, { 4 }, { 5 } }

