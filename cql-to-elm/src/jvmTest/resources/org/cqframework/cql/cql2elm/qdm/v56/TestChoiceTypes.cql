library TestChoiceTypes

using QDM version '5.6'

parameter "Measurement Period" Interval<DateTime>

context Patient

define TestIntegerChoice:
  ["Laboratory Test, Performed"] L
    where L.result = 1

define TestDecimalChoice:
  ["Laboratory Test, Performed"] L
    where L.result = 1.0

define TestQuantityChoice:
  ["Laboratory Test, Performed"] L
    where L.result = 1.0 'mm[Hg]'

define TestRatioChoice:
  ["Laboratory Test, Performed"] L
    where L.result = 1:1

define TestUnionChoices:
  (["Intervention, Performed"] union ["Encounter, Performed"]) U
    where U.relevantPeriod during "Measurement Period"