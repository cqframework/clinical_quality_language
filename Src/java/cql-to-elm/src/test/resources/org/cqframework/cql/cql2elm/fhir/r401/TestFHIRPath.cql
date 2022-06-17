library TestFHIRPath

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

context Patient

define TestToday: today()

define TestNow: now()

define TestTimeOfDay: timeOfDay()

define Encounters: [Encounter]

define TestTodayInWhere: Encounters.where(status = 'in-progress' and period.end <= today() - 72 hours)

define TestNowInWhere: Encounters.where(status = 'in-progress' and period.end <= now() - 72 hours)

