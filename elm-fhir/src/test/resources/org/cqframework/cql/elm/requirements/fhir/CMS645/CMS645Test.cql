library CMS645Test

using QICore version '4.1.1'

include FHIRHelpers version '4.0.001'

valueset "Androgen deprivation therapy for Urology Care": 'tbd'

context Patient

define "test":
    [MedicationRequest: "Androgen deprivation therapy for Urology Care"] ADTActive
     where ADTActive.status in { 'active', 'completed' }
        and ADTActive.intent in { 'order', 'original-order', 'reflex-order', 'filler-order', 'instance-order' }
        and ADTActive.doNotPerform is not true
     return "Earliest"(
       "NormalizePeriod"(
         First(
            (ADTActive.dosageInstruction DoseInstruction
                let DoseTiming: DoseInstruction.timing,
                    FirstDoseEventInTiming: First(DoseTiming.event DoseEvent return DoseEvent sort ascending)
                return FirstDoseEventInTiming
              ) DoseTimingEvent
                return DoseTimingEvent
                sort ascending
            ),
         First(
            ( collapse (ADTActive.dosageInstruction.timing.repeat.bounds DoseTime
                return DoseTime.toInterval())
                ) DrugPeriod
                sort by start of $this
            )
         )
      )

define function "HasStart"(period Interval<DateTime> ):
  not ( start of period is null
      or start of period = minimum DateTime
  )

define function "Earliest"(choice Choice<DateTime, Quantity, Interval<DateTime>, Interval<Quantity>> ):
  (choice.toInterval()) period
    return
      if (HasStart(period)) then start of period
      else end of period

define function "NormalizePeriod"(pointInTime DateTime, dateTimeInterval Interval<DateTime>):
  if pointInTime is not null then Interval[pointInTime, pointInTime]
  else if dateTimeInterval is not null then dateTimeInterval
  else null as Interval<DateTime>

define fluent function toInterval(choice Choice<DateTime, Quantity, Interval<DateTime>, Interval<Quantity>>):
  case
	  when choice is DateTime then
    	Interval[choice as DateTime, choice as DateTime]
		when choice is Interval<DateTime> then
  		choice as Interval<DateTime>
		when choice is Quantity then
		  Interval[Patient.birthDate + (choice as Quantity),
			  Patient.birthDate + (choice as Quantity) + 1 year)
		when choice is Interval<Quantity> then
		  Interval[Patient.birthDate + (choice.low as Quantity),
			  Patient.birthDate + (choice.high as Quantity) + 1 year)
		else
			null as Interval<DateTime>
	end

