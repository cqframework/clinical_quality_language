library NCQA_CQLBase version '1.0.0'

using FHIR version '4.0.1'

 /*
    Sort interval lists

    This is a technique to sort intervals (or any other quantity) according to a criteria.
    CQL natively supports sorting resources (e.g. [Condition: <valuesets>]), or tuples
    So to sort generic lists, create a tuple and project the sort criteria to a tuple value.
 */

define function "Sort Date Intervals"(intervals List<Interval<Date>>):
  if intervals is null then { } as List<Interval<Date>>
  else if Count(intervals)= 0 then { } as List<Interval<Date>>
  else ( ( intervals I
            return Tuple { 
                    interval: I,
                    startOfInterval: if start of I is null then minimum Date else start of I
                  } sort by startOfInterval asc 
         ) sortedIntervals
         return sortedIntervals.interval
       )

define function "Sort DateTime Intervals"(intervals List<Interval<DateTime>>):
  if intervals is null then { } as List<Interval<DateTime>>
  else if Count(intervals)= 0 then { } as List<Interval<DateTime>>
  else ( ( intervals I
            return Tuple {
                      interval: I,
                      startOfInterval: if start of I is null then minimum DateTime else start of I
                    } sort by startOfInterval asc 
         ) sortedIntervals
         return sortedIntervals.interval
       )


 /*
    Workaround for collapse function

    In the reference engine, the collapse function may not work correctly if 2 intervals have the same start, 
    and one interval has an inclusive end while the other has an exclusive end.
    
    Consider this example:
    collapse { Interval[Date(2019, 1, 1), Date(2019, 3, 1)), Interval[Date(2019, 1, 1), Date(2019, 6, 1)]} per day

    The result should be Interval[Date(2019, 1, 1), Date(2019, 6, 1)]
    However, the reference engine returns Interval[Date(2019, 1, 1), Date(2019, 6, 1))

    I suspect the reason for the bug is as follows:
      1. The engine sorts the intervals according to start date
      2. CQL sorts are indeterminate for equal values
      3. CQL sorts the end inclusion/exclusion incorrectly

    Note: If one of the intervals above had a different start date, then collapse would work correctly.

    This workaround operates on the following principle:
      If 2 intervals have the same start date, then one interval must be properly included in the other interval

    So the workaround removes all intervals that are properly contained in any other interval.  
    This operation does not affect the collapse function in general, and resolves the bug noted here.
 */

define function "Collapse Date Interval Workaround"(intervals List<Interval<Date>>):
  if intervals is null then { } as List<Interval<Date>>
  else if Count(intervals)= 0 then { } as List<Interval<Date>>
  else collapse ( intervals I where not AnyTrue( intervals J return I properly included in J ) ) per day

define function "Collapse DateTime Interval Workaround"(intervals List<Interval<DateTime>>):
  if intervals is null then { } as List<Interval<DateTime>>
  else if Count(intervals)= 0 then { } as List<Interval<DateTime>>
  else collapse ( intervals I where not AnyTrue(intervals J return I properly included in J ) )


 /*
    Interval Coverings

    Get a unique covering to a specified base interval by a list of covering intervals
    
    Note: Uses collapse bug workaround functions
 */
define function "Date Interval Covering Relative to Base Interval"(baseInterval Interval<Date>, coveringIntervals List<Interval<Date>>):
  "Collapse Date Interval Workaround"(("Sort Date Intervals"(coveringIntervals)) sortedInterval
                                        return baseInterval intersect sortedInterval)

define function "DateTime Interval Covering Relative to Base Interval"(baseInterval Interval<DateTime>, coveringIntervals List<Interval<DateTime>>):
  "Collapse DateTime Interval Workaround"(("Sort DateTime Intervals"(coveringIntervals))sortedInterval
                                            return baseInterval intersect sortedInterval)


 /*
    Interval Gaps

    Get a unique list of gaps between between the coveringIntervals over a base interval

    This works by getting the gap in front of each covering interval and adding the gap after the last interval.
 */

define function "Date Interval Gaps Relative to Base Interval"(baseInterval Interval<Date>, coveringIntervals List<Interval<Date>>):
  ( Tuple {
    sortedCoverings: "Date Interval Covering Relative to Base Interval"(baseInterval, coveringIntervals)
  } ) variableDeclarations
    return ( Tuple {
              frontgaps:  if Count(variableDeclarations.sortedCoverings)= 0 then { baseInterval } 
                          else ( variableDeclarations.sortedCoverings sortedCovering
                                  return  if IndexOf(variableDeclarations.sortedCoverings, sortedCovering) = 0 
                                          then ( Interval[start of baseInterval, start of sortedCovering] intersect baseInterval) except sortedCovering 
                                          else Interval( start of variableDeclarations.sortedCoverings[IndexOf(variableDeclarations.sortedCoverings, sortedCovering)- 1], end of sortedCovering )
                                                except variableDeclarations.sortedCoverings[IndexOf(variableDeclarations.sortedCoverings, sortedCovering)- 1]
                                                except sortedCovering),
              endgap: if Count(variableDeclarations.sortedCoverings)= 0 then { } as List<Interval<Date>>
                      else { ( Interval( start of Last(variableDeclarations.sortedCoverings), end of baseInterval]
                                except Last(variableDeclarations.sortedCoverings)) intersect baseInterval }
             } ) calculations
    return "Collapse Date Interval Workaround"( calculations.frontgaps union calculations.endgap )

define function "DateTime Interval Gaps Relative to Base Interval"(baseInterval Interval<DateTime>, coveringIntervals List<Interval<DateTime>>):
  ( Tuple {
    sortedCoverings: "DateTime Interval Covering Relative to Base Interval"(baseInterval, coveringIntervals)
  } ) variableDeclarations
    return ( Tuple {
              frontgaps: if Count(variableDeclarations.sortedCoverings)= 0 then { baseInterval } 
                         else ( variableDeclarations.sortedCoverings sortedCovering
                                return if IndexOf(variableDeclarations.sortedCoverings, sortedCovering) = 0 
                                       then ( Interval[start of baseInterval, start of sortedCovering] intersect baseInterval ) except sortedCovering 
                                       else Interval( start of variableDeclarations.sortedCoverings[IndexOf(variableDeclarations.sortedCoverings, sortedCovering)- 1], end of sortedCovering )
                                            except variableDeclarations.sortedCoverings[IndexOf(variableDeclarations.sortedCoverings, sortedCovering)- 1]
                                            except sortedCovering),
              endgap: if Count(variableDeclarations.sortedCoverings)= 0 then { } as List<Interval<DateTime>>
                      else { ( Interval( start of Last(variableDeclarations.sortedCoverings), end of baseInterval]
                                except Last(variableDeclarations.sortedCoverings)) intersect baseInterval }
             } ) calculations
    return "Collapse DateTime Interval Workaround"( calculations.frontgaps union calculations.endgap )


 /*
    Statistics Functions for Dates

    Return basic statistics for a list of intervals
 */

define function "Collapsed Date Interval Stats"(collapsedIntervals List<Interval<Date>>):
  Tuple {
    "Intervals": collapsedIntervals,
    "Interval Count": Count(collapsedIntervals),
    "Total Days In Intervals": if Count(collapsedIntervals)= 0 then 0 
                               else Sum(collapsedIntervals I
                                        return Max({ duration in days of I + 1, 0 })),
    "Longest Interval": if Count(collapsedIntervals)= 0 then null as Interval<Date>
                        else First(collapsedIntervals I
                                    return Tuple {
                                      "interval": I,
                                      "days": Max({ duration in days of I + 1, 0 })
                                    } sort by "days" desc).interval,
    "Total Days In Longest Interval": if Count(collapsedIntervals)= 0 then 0 
                                      else Max({ duration in days of First(collapsedIntervals I
                                                                              return Tuple {
                                                                                "interval": I,
                                                                                "days": Max({ duration in days of I + 1, 0 })
                                                                              }
                                                                              sort by "days" desc).interval + 1, 0 })
  }

define function "Date Interval Covering Relative to Base Interval Stats"(baseInterval Interval<Date>, coveringIntervals List<Interval<Date>>):
  ( Tuple {
    "Covering Intervals": "Date Interval Covering Relative to Base Interval"(baseInterval, coveringIntervals)
  } ) variableDeclarations
    return ( "Collapsed Date Interval Stats"(variableDeclarations."Covering Intervals"))

define function "Date Interval Gaps Relative to Base Interval Stats"(baseInterval Interval<Date>, coveringIntervals List<Interval<Date>>):
  ( Tuple {
    "Gap Intervals": "Date Interval Gaps Relative to Base Interval"(baseInterval, coveringIntervals)
  } ) variableDeclarations
    return ( "Collapsed Date Interval Stats"(variableDeclarations."Gap Intervals"))


 /*
    Statistics Functions for DateTimes
 */

 /*
    Bug Fix: duration in days of Interval<DateTime> doesn't work correctly if there are nulls in the datetimes
    This function corrects this issue by setting nulls to 0
 */

define function "DateTime Interval Set Nulls to Zero"(interval Interval<DateTime>):
  ( Tuple {
    StartYear: year from start of interval,
    StartMonth:       if month from start of interval is null then 0 
                      else month from start of interval,
    StartDay:         if day from start of interval is null then 0 
                      else day from start of interval,
    StartHour:        if hour from start of interval is null then 0 
                      else hour from start of interval,
    StartMinute:      if minute from start of interval is null then 0 
                      else minute from start of interval,
    StartSecond:      if second from start of interval is null then 0 
                      else second from start of interval,
    StartMillisecond: if millisecond from start of interval is null then 0 
                      else millisecond from start of interval,
    EndYear: year from end of interval,
    EndMonth:         if month from end of interval is null then 0 
                      else month from end of interval,
    EndDay:           if day from end of interval is null then 0 
                      else day from end of interval,
    EndHour:          if hour from end of interval is null then 0 
                      else hour from end of interval,
    EndMinute:        if minute from end of interval is null then 0 
                      else minute from end of interval,
    EndSecond:        if second from end of interval is null then 0 
                      else second from end of interval,
    EndMillisecond:   if millisecond from end of interval is null then 0 
                      else millisecond from end of interval
  } ) i
    return Interval[DateTime(i.StartYear, i.StartMonth, i.StartDay, i.StartHour, i.StartMinute, i.StartSecond, i.StartMillisecond), DateTime(i.EndYear, i.EndMonth, i.EndDay, i.EndHour, i.EndMinute, i.EndSecond, i.EndMillisecond)]

define function "Collapsed DateTime Interval Stats"(collapsedIntervals List<Interval<DateTime>>):
  Tuple {
    "Intervals": collapsedIntervals,
    "Interval Count": Count(collapsedIntervals),
    "Total Days In Intervals": if Count(collapsedIntervals)= 0 then 0 
                               else Sum(collapsedIntervals I
                                        return Max({ duration in days of "DateTime Interval Set Nulls to Zero"(I)+ 1, 0 })),
    "Longest Interval": if Count(collapsedIntervals)= 0 then null as Interval<DateTime>
                        else First(collapsedIntervals I
                                    return Tuple {
                                      "interval": I,
                                      "days": Max({ duration in days of "DateTime Interval Set Nulls to Zero"(I)+ 1, 0 })
                                    } sort by "days" desc).interval,
    "Total Days In Longest Interval": if Count(collapsedIntervals)= 0 then 0 
                                      else Max({ duration in days of "DateTime Interval Set Nulls to Zero"(First(collapsedIntervals I
                                                                                                                  return Tuple {
                                                                                                                    "interval": I,
                                                                                                                    "days": Max({ duration in days of "DateTime Interval Set Nulls to Zero"(I) + 1, 0 })
                                                                                                                  }
                                                                                                                  sort by "days" desc).interval) + 1, 0 })
  }

define function "DateTime Interval Covering Relative to Base Interval Stats"(baseInterval Interval<DateTime>, coveringIntervals List<Interval<DateTime>>):
  ( Tuple {
    "Covering Intervals": "DateTime Interval Covering Relative to Base Interval"(baseInterval, coveringIntervals)
  } ) variableDeclarations
    return ( "Collapsed DateTime Interval Stats"(variableDeclarations."Covering Intervals"))

define function "DateTime Interval Gaps Relative to Base Interval Stats"(baseInterval Interval<DateTime>, coveringIntervals List<Interval<DateTime>>):
  ( Tuple {
    "Gap Intervals": "DateTime Interval Gaps Relative to Base Interval"(baseInterval, coveringIntervals)
  } ) variableDeclarations
    return ( "Collapsed DateTime Interval Stats"(variableDeclarations."Gap Intervals"))

define function "Convert To UTC DateTime"(d Date):
  ( Tuple {
    StartYear: year from d,
    StartMonth:       if month from d is null then 0 
                      else month from d,
    StartDay:         if day from d is null then 0 
                      else day from d
  } ) i
return DateTime(i.StartYear, i.StartMonth, i.StartDay, 0, 0, 0, 0, 0)

define function "Convert Interval Date to UTC Interval DateTime"(interval Interval<Date>):
      Interval["Convert To UTC DateTime"(start of interval), "Convert To UTC DateTime"(end of interval)]