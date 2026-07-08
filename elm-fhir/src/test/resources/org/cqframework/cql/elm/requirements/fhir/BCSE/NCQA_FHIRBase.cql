library NCQA_FHIRBase version '1.0.0'

using FHIR version '4.0.1'

include FHIRHelpers version '4.0.1'

context Patient

define function "Normalize Onset"(onset Choice<FHIR.dateTime, FHIR.Age, FHIR.Period, FHIR.Range, FHIR.string>):
  if onset is FHIR.dateTime then Interval[FHIRHelpers.ToDateTime ( onset as FHIR.dateTime ), FHIRHelpers.ToDateTime ( onset as FHIR.dateTime )]
    else if onset is FHIR.Period then Interval[onset.start.value, onset.
  end.value]
    else if onset is FHIR.string then Message(null as Interval<DateTime>, true, '1', 'Error', 'Cannot compute an interval from a String value')
    else if onset is FHIR.Age then Interval[FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( onset as FHIR.Age ), FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( onset as FHIR.Age ) + 1 year ) 
    else if onset is FHIR.Range then Interval[FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( ( onset as FHIR.Range ).low ), FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( ( onset as FHIR.Range ).high ) + 1 year ) 
    else null

define function "Normalize Abatement"(abatement Choice<FHIR.dateTime, FHIR.Age, FHIR.Period, FHIR.Range, FHIR.string>):
  if abatement is FHIR.dateTime then Interval[FHIRHelpers.ToDateTime ( abatement as FHIR.dateTime ), FHIRHelpers.ToDateTime ( abatement as FHIR.dateTime )]
    else if abatement is FHIR.Period then Interval[abatement.start.value, abatement.
  end.value]
    else if abatement is FHIR.string then Message(null as Interval<DateTime>, true, '1', 'Error', 'Cannot compute an interval from a String value')
    else if abatement is FHIR.Age then Interval[FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( abatement as FHIR.Age ), FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( abatement as FHIR.Age ) + 1 year ) 
    else if abatement is FHIR.Range then Interval[FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( ( abatement as FHIR.Range ).low ), FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( ( abatement as FHIR.Range ).high ) + 1 year ) 
    else null

define function "Prevalence Period"(condition Condition):
  Interval[start of "Normalize Onset"(condition.onset), 
  end of "Normalize Abatement"(condition.abatement)]

define function "Normalize Interval"(choice Choice<FHIR.dateTime, FHIR.date, FHIR.Period, FHIR.Timing, FHIR.instant, FHIR.string, FHIR.Age, FHIR.Range>):
  case
    when choice is FHIR.dateTime then Interval[FHIRHelpers.ToDateTime ( choice as FHIR.dateTime ), FHIRHelpers.ToDateTime ( choice as FHIR.dateTime )]
    when choice is FHIR.date then Interval[ToDateTime(choice as FHIR.date), ToDateTime(choice as FHIR.date)]
    when choice is FHIR.Period then Interval[( choice as FHIR.Period ).start.value, ( choice as FHIR.Period ).
    end.value]
    when choice is FHIR.instant then Interval[FHIRHelpers.ToDateTime ( choice as FHIR.instant ), FHIRHelpers.ToDateTime ( choice as FHIR.instant )]
    when choice is FHIR.Age then Interval[FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( choice as FHIR.Age ), FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( choice as FHIR.Age ) + 1 year )
    when choice is FHIR.Range then Interval[FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( ( choice as FHIR.Range ).low ), FHIRHelpers.ToDate ( Patient.birthDate ) + FHIRHelpers.ToQuantity ( ( choice as FHIR.Range ).high ) + 1 year )
    when choice is FHIR.Timing then Message(null as Interval<DateTime>, true, '1', 'Error', 'Cannot compute a single interval from a Timing type')
    when choice is FHIR.string then Message(null as Interval<DateTime>, true, '1', 'Error', 'Cannot compute an interval from a String value')
    else null as Interval<DateTime>
  end

define function "GetId"(uri String):
  if ( PositionOf('/', uri)> 0 ) then Last(Split(uri, '/'))
    else uri

define function "VS Cast Function"(VSet List<System.Code>):
  ( ( cast { "VSet", 1 }[0]as Tuple {
      codes List<System.Code>,
      oid System.String,
      version System.String
    }
  ).codes ) VSetCodes
    return System.Code { code: VSetCodes.code, system: VSetCodes.system }

define function "First Dates per 31 Day Periods"(DateList List<Date>):
  ( Tuple {
    SortedDates: ( DateList d
      return d
      sort asc ) X
      where X is not null
  } ) SortedDates
    return ( Tuple {
      SortedList: SortedDates.SortedDates,
      AnchorIndex: 0
    } ) AnchorList
      return ( Tuple {
        NextDate: AnchorList.SortedList[AnchorList.AnchorIndex]as System.Date,
        NewList: DateList X
          where not ( X in Interval[( ( ( AnchorList.SortedList[AnchorList.AnchorIndex]) as System.Date ) + 1 day ), ( ( AnchorList.SortedList[AnchorList.AnchorIndex]) as System.Date ) + 30 days]),
        IndexofNewDate: AnchorList.AnchorIndex + 1
      } ) FirstList
        return if FirstList.NewList[FirstList.IndexofNewDate]is null then FirstList 
          else ( Tuple {
          NextDate: FirstList.NewList[FirstList.IndexofNewDate]as System.Date,
          NewList: FirstList.NewList X
            where not ( X in Interval[( ( ( FirstList.NewList[FirstList.IndexofNewDate]) as System.Date ) + 1 day ), ( ( FirstList.NewList[FirstList.IndexofNewDate]as System.Date ) + 30 days )]),
          IndexofNewDate: FirstList.IndexofNewDate + 1
        } ) SecondList
          return if SecondList.NewList[SecondList.IndexofNewDate]is null then SecondList 
            else ( Tuple {
            NextDate: SecondList.NewList[SecondList.IndexofNewDate]as System.Date,
            NewList: SecondList.NewList X
              where not ( X in Interval[( ( ( SecondList.NewList[SecondList.IndexofNewDate]) as System.Date ) + 1 day ), ( ( SecondList.NewList[SecondList.IndexofNewDate]as System.Date ) + 30 days )]),
            IndexofNewDate: SecondList.IndexofNewDate + 1
          } ) ThirdList
            return if ThirdList.NewList[ThirdList.IndexofNewDate]is null then ThirdList 
              else ( Tuple {
              NextDate: ThirdList.NewList[ThirdList.IndexofNewDate]as System.Date,
              NewList: ThirdList.NewList X
                where not ( X in Interval[( ( ( ThirdList.NewList[ThirdList.IndexofNewDate]) as System.Date ) + 1 day ), ( ( ThirdList.NewList[ThirdList.IndexofNewDate]as System.Date ) + 30 days )]),
              IndexofNewDate: ThirdList.IndexofNewDate + 1
            } ) FourthList
              return if FourthList.NewList[FourthList.IndexofNewDate]is null then FourthList 
                else ( Tuple {
                NextDate: FourthList.NewList[FourthList.IndexofNewDate]as System.Date,
                NewList: FourthList.NewList X
                  where not ( X in Interval[( ( ( FourthList.NewList[FourthList.IndexofNewDate]) as System.Date ) + 1 day ), ( ( FourthList.NewList[FourthList.IndexofNewDate]as System.Date ) + 30 days )]),
                IndexofNewDate: FourthList.IndexofNewDate + 1
              } ) FifthList
                return if FifthList.NewList[FifthList.IndexofNewDate]is null then FifthList 
                  else ( Tuple {
                  NextDate: FifthList.NewList[FifthList.IndexofNewDate]as System.Date,
                  NewList: FifthList.NewList X
                    where not ( X in Interval[( ( ( FifthList.NewList[FifthList.IndexofNewDate]) as System.Date ) + 1 day ), ( ( FifthList.NewList[FifthList.IndexofNewDate]as System.Date ) + 30 days )]),
                  IndexofNewDate: FifthList.IndexofNewDate + 1
                } ) SixthList
                  return if SixthList.NewList[SixthList.IndexofNewDate]is null then SixthList 
                    else ( Tuple {
                    NextDate: SixthList.NewList[SixthList.IndexofNewDate]as System.Date,
                    NewList: SixthList.NewList X
                      where not ( X in Interval[( ( ( SixthList.NewList[SixthList.IndexofNewDate]) as System.Date ) + 1 day ), ( ( SixthList.NewList[SixthList.IndexofNewDate]as System.Date ) + 30 days )]),
                    IndexofNewDate: SixthList.IndexofNewDate + 1
                  } ) SeventhList
                    return if SeventhList.NewList[SeventhList.IndexofNewDate]is null then SeventhList 
                      else ( Tuple {
                      NextDate: SeventhList.NewList[SeventhList.IndexofNewDate]as System.Date,
                      NewList: SeventhList.NewList X
                        where not ( X in Interval[( ( ( SeventhList.NewList[SeventhList.IndexofNewDate]) as System.Date ) + 1 day ), ( ( SeventhList.NewList[SeventhList.IndexofNewDate]as System.Date ) + 30 days )]),
                      IndexofNewDate: SeventhList.IndexofNewDate + 1
                    } ) EighthList
                      return if EighthList.NewList[EighthList.IndexofNewDate]is null then EighthList 
                        else ( Tuple {
                        NextDate: EighthList.NewList[EighthList.IndexofNewDate]as System.Date,
                        NewList: EighthList.NewList X
                          where not ( X in Interval[( ( ( EighthList.NewList[EighthList.IndexofNewDate]) as System.Date ) + 1 day ), ( ( EighthList.NewList[EighthList.IndexofNewDate]as System.Date ) + 30 days )]),
                        IndexofNewDate: EighthList.IndexofNewDate + 1
                      } ) NinethList
                        return if NinethList.NewList[NinethList.IndexofNewDate]is null then NinethList 
                          else ( Tuple {
                          NextDate: NinethList.NewList[NinethList.IndexofNewDate]as System.Date,
                          NewList: NinethList.NewList X
                            where not ( X in Interval[( ( ( NinethList.NewList[NinethList.IndexofNewDate]) as System.Date ) + 1 day ), ( ( NinethList.NewList[NinethList.IndexofNewDate]as System.Date ) + 30 days )]),
                          IndexofNewDate: NinethList.IndexofNewDate + 1
                        } ) TenthList
                          return if TenthList.NewList[TenthList.IndexofNewDate]is null then TenthList 
                            else ( Tuple {
                            NextDate: TenthList.NewList[TenthList.IndexofNewDate]as System.Date,
                            NewList: TenthList.NewList X
                              where not ( X in Interval[( ( ( TenthList.NewList[TenthList.IndexofNewDate]) as System.Date ) + 1 day ), ( ( TenthList.NewList[TenthList.IndexofNewDate]as System.Date ) + 30 days )]),
                            IndexofNewDate: TenthList.IndexofNewDate + 1
                          } ) EleventhList
                            return if EleventhList.NewList[EleventhList.IndexofNewDate]is null then EleventhList 
                              else ( Tuple {
                                NextDate: EleventhList.NewList[EleventhList.IndexofNewDate]as System.Date,
                                NewList: EleventhList.NewList X
                                  where not ( X in Interval[( ( ( EleventhList.NewList[EleventhList.IndexofNewDate]) as System.Date ) + 1 day ), ( ( EleventhList.NewList[EleventhList.IndexofNewDate]as System.Date ) + 30 days )]),
                                IndexofNewDate: EleventhList.IndexofNewDate + 1
                              }
                            )