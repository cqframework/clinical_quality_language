/* Context-independent Data Elements. (e.g., the Retrieves, to be used in more than one context.) */

library BSElements version '1.0.000'

using FHIR version '4.0.1'

include FHIRCommon version '4.0.1' called FHIRCommon
include FHIRHelpers version '4.0.1' called FHIRHelpers
include CommonConcepts version '1.0.000' called CommonCx
include CommonElements version '1.0.000' called CommonEl

include Ind2E31A37EB104A7D1 version '1.0.000' called Ind2E31A37EB104A7D1

context Patient

define "4. Biliopancreatic bypass with duodenal switch in patients ages greater than or equal to 18 years with BMI greater than or equal to 50 kg/(meter squared)":
  Ind2E31A37EB104A7D1."4. Biliopancreatic bypass with duodenal switch in patients ages greater than or equal to 18 years with BMI greater than or equal to 50 kg/(meter squared)"
