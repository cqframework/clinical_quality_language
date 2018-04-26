library TestIncludedIn

using QDM version '5.3'

valueset "Comfort Measures": 'urn:oid:1.2.3.4.5'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Interventions":
  ["Intervention, Performed": "Comfort Measures"] I
    where I.authorDatetime during "Measurement Period"


