/*
CQL Translator Issue [#577](https://github.com/cqframework/clinical_quality_language/issues/577)
CQL Specification Issue [J#31392](https://jira.hl7.org/browse/FHIR-31392)
 */
library TestPluralTerminologyTarget

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

codesystem "CONDCATEGORY": 'http://terminology.hl7.org/CodeSystem/condition-category'
codesystem "US-CONDCATEGORY": 'http://hl7.org/fhir/us/core/CodeSystem/condition-category'

valueset "Condition Category Codes": 'TBD'

code "Condition Problem List Item code": 'problem-list-item' from "CONDCATEGORY" display 'Problem List Item'
code "Condition Encounter Diagnosis code": 'encounter-diagnosis' from "CONDCATEGORY" display 'Encounter Diagnosis'
code "Condition Health Concern code": 'health-concern' from "US-CONDCATEGORY" display 'health concern'

concept "Condition Problem List Item":
  { "Condition Problem List Item code" } display 'Problem List Item'
concept "Condition Encounter Diagnosis":
  { "Condition Encounter Diagnosis code" } display 'Encounter Diagnosis'
concept "Condition Health Concern":
  { "Condition Health Concern code" } display 'Health Concern'

context Patient

/*
Multi-cardinality terminology targets work with value sets and code systems
because the terminology membership operator is specifically overridden to
support List-valued arguments on the left-hand side of the operator.
*/
define ConditionCategoryCodes:
  [Condition: category in "Condition Category Codes"]

define ConditionCategoryCodesLongHand:
  [Condition] C
    where C.category in "Condition Category Codes"

/*
However, the analogous overloads don't exist for the singular case
(when the terminology expression is a direct-reference code or concept)
*/
define ProblemListItems:
  [Condition: category ~ "Condition Problem List Item"]

/*
The analogous long-hand here is "contains", and that would require the
introduction of a new terminology comparator in the retrieve.
As a result, this need to be raised as a CQL issue to add the
ability to use "contains" as a terminology comparator.
*/
define ProblemListItemsLongHand:
  [Condition] C
    where C.category contains "Condition Problem List Item"

define EncounterDiagnoses:
  [Condition: category ~ "Condition Encounter Diagnosis"]

define HealthConcerns:
  [Condition: category ~ "Condition Health Concern"]
