library Issue1225

using USCore version '3.1.1'

include FHIRHelpers version '4.0.1'

context Patient

define "Address":
  Patient.address.line

define "Address Line 1":
  Patient.address.line[0]