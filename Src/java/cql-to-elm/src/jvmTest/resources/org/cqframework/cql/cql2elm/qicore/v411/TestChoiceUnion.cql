library TestChoiceUnion

using QICore version '4.1.1'

include FHIRHelpers version '4.0.1'

context Patient

define "Union of Different Types":
  ([Procedure] union [ServiceRequest]) R
    return {
      performed: R.performed,
      authoredOn: R.authoredOn
    }