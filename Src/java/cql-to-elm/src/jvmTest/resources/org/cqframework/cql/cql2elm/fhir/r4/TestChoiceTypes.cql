library TestChoiceTypes

using FHIR version '4.0.0'

include FHIRHelpers version '4.0.0' called FHIRHelpers

context Patient

define TestChoice:
  [QuestionnaireResponse] Q
    with Q.item i1
      such that (i1.answer[0].value) > 18

define TestDateChoice:
  [QuestionnaireResponse] Q
    with Q.item i1
      such that (i1.answer[0].value) > @2017-01-18

// When a Choice type results in multiple, equally tenable conversions to a target type, the
// translator will use a case expression to apply the conversion based on the run-time type of the choice
define TestStringChoice:
  [QuestionnaireResponse] Q
    with Q.item i1
      such that (i1.answer[0].value) > 'Blah-blah-blah'

