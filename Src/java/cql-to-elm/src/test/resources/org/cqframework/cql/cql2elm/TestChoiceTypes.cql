library TestChoiceTypes

using FHIR version '3.0.0'

include FHIRHelpers version '3.0.0' called FHIRHelpers

context Patient

define TestChoice:
  [QuestionnaireResponse] Q
    with Q.item i1
      such that (i1.answer[0].value) > 18

define TestDateChoice:
  [QuestionnaireResponse] Q
    with Q.item i1
      such that (i1.answer[0].value) > @2017-01-18
