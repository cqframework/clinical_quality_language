library IncludedParameterTest

include IncludedParameterTestCommon called P

parameter "Measurement Period" Integer


define "Included Parameter":
  "P"."Measurement Period"

define "Local Parameter":
  "Measurement Period"