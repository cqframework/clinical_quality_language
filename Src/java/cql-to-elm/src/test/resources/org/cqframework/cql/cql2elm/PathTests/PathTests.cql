library PathTests version '1'

using FHIR version '1.8'

include FHIRHelpers version '1.8' called FHIRHelpers

context Patient

define TestPathTraversal: Patient.name.given
define TestFirst: Patient.name.first()
define TestWhere: Patient.name.where(use.value = 'official')
define TestWhereImplicit: Patient.name.where(use = 'official')
define TestToListConversion: exists(Patient.birthDate)
define TestFromListConversion: Patient.name.given.value + ' ' + Patient.name.family.value
define TestFromListConversionImplicit: Patient.name.given + ' ' + Patient.name.family

// Could run this by either demoting the left operand or promoting the right operand
// Need the algorithm to favor demotion...
// The instantiation favors promotion because it encounters the list type first
// But if during instantiation, we find that a type parameter has been bound to a list and the signature contains a list of the same type
// The type parameter should be demoted
define TestGenericFromListConversion: { 1 } in { 1, 2, 3, 4, 5 }
define TestListInListOfLists: { 1 } in { { 1 }, { 2 }, { 3 }, { 4 }, { 5 } }

define TestEmpty: Patient.name.given.empty()
define TestNot: Patient.deceased.value.not()
define TestNotImplicit: Patient.deceased.not()
define TestExists: Patient.name.given.exists()
define TestExistsWithCriteria: Patient.name.exists(use.value = 'official')
define TestExistsWithCriteriaImplicit: Patient.name.exists(use = 'official')
define TestAll: Patient.name.all(use.value = 'official')
define TestAllImplicit: Patient.name.all(use = 'official')
define TestAllTrue: Patient.deceased.value.allTrue()
define TestAllTrueImplicit: Patient.deceased.allTrue()
define TestAnyTrue: Patient.deceased.value.anyTrue()
define TestAnyTrueImplicit: Patient.deceased.anyTrue()
//TODO: define TestAllFalse: Patient.deceased.value.allFalse()
//TODO: define TestAnyFalse: Patient.deceased.value.anyFalse()
define TestSubsetOf: Patient.name.where(use.value = 'official').subsetOf(Patient.name)
define TestSubsetOfImplicit: Patient.name.where(use = 'official').subsetOf(Patient.name)
define TestSupersetOf: Patient.name.supersetOf(Patient.name.where(use.value = 'official'))
define TestSupersetOfImplicit: Patient.name.supersetOf(Patient.name.where(use = 'official'))
//TODO: define TestIsDistinct: Patient.name.given.isDistinct()
define TestDistinct: Patient.name.given.distinct()
define TestCount: Patient.name.given.count()
define TestSelect: Patient.name.select(given.value + ' ' + family.value)
define TestSelectImplicit: Patient.name.select(given + ' ' + family)
//TODO: define TestRepeat: Questionnaire.repeat(group | question).question
//TODO: define TestOfType: Bundle.entry.resource.ofType(Patient)
define TestContains: Patient.name.given.contains('abc')
define TestIif: Patient.deceased.iif('is deceased', 'is not deceased')
define TestIndexOf: Patient.name.given.indexOf('abc')