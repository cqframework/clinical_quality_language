should = require 'should'
setup = require '../../setup'
data = require './data'
{ Uncertainty } = require '../../../lib/datatypes/uncertainty'

describe 'DateTime', ->
  @beforeEach ->
    setup @, data
    @defaultOffset = (new Date()).getTimezoneOffset() / 60 * -1

  it 'should execute year precision correctly', ->
    d = @year.exec(@ctx)
    d.isTime().should.be.false()
    d.year.should.equal 2012
    d.timezoneOffset.should.equal @defaultOffset
    should.not.exist(d[field]) for field in [ 'month', 'day', 'hour', 'minute', 'second', 'millisecond' ]

  it 'should execute month precision correctly', ->
    d = @month.exec(@ctx)
    d.isTime().should.be.false()
    d.year.should.equal 2012
    d.month.should.equal 2
    d.timezoneOffset.should.equal @defaultOffset
    should.not.exist(d[field]) for field in [ 'day', 'hour', 'minute', 'second', 'millisecond' ]

  it 'should execute day precision correctly', ->
    d = @day.exec(@ctx)
    d.isTime().should.be.false()
    d.year.should.equal 2012
    d.month.should.equal 2
    d.day.should.equal 15
    d.timezoneOffset.should.equal @defaultOffset
    should.not.exist(d[field]) for field in [ 'hour', 'minute', 'second', 'millisecond' ]

  it 'should execute hour precision correctly', ->
    d = @hour.exec(@ctx)
    d.isTime().should.be.false()
    d.year.should.equal 2012
    d.month.should.equal 2
    d.day.should.equal 15
    d.hour.should.equal 12
    d.timezoneOffset.should.equal @defaultOffset
    should.not.exist(d[field]) for field in [ 'minute', 'second', 'millisecond' ]

  it 'should execute minute precision correctly', ->
    d = @minute.exec(@ctx)
    d.isTime().should.be.false()
    d.year.should.equal 2012
    d.month.should.equal 2
    d.day.should.equal 15
    d.hour.should.equal 12
    d.minute.should.equal 10
    d.timezoneOffset.should.equal @defaultOffset
    should.not.exist(d[field]) for field in [ 'second', 'millisecond' ]

  it 'should execute second precision correctly', ->
    d = @second.exec(@ctx)
    d.isTime().should.be.false()
    d.year.should.equal 2012
    d.month.should.equal 2
    d.day.should.equal 15
    d.hour.should.equal 12
    d.minute.should.equal 10
    d.second.should.equal 59
    d.timezoneOffset.should.equal @defaultOffset
    should.not.exist(d.millisecond)

  it 'should execute millisecond precision correctly', ->
    d = @millisecond.exec(@ctx)
    d.isTime().should.be.false()
    d.year.should.equal 2012
    d.month.should.equal 2
    d.day.should.equal 15
    d.hour.should.equal 12
    d.minute.should.equal 10
    d.second.should.equal 59
    d.millisecond.should.equal 456
    d.timezoneOffset.should.equal @defaultOffset

  it 'should execute timezone offsets correctly', ->
    d = @timezoneOffset.exec(@ctx)
    d.isTime().should.be.false()
    d.year.should.equal 2012
    d.month.should.equal 2
    d.day.should.equal 15
    d.hour.should.equal 12
    d.minute.should.equal 10
    d.second.should.equal 59
    d.millisecond.should.equal 456
    d.timezoneOffset.should.equal -8

describe 'Time', ->
  @beforeEach ->
    setup @, data
    @defaultOffset = (new Date()).getTimezoneOffset() / 60 * -1

  it 'should execute hour precision correctly', ->
    d = @hour.exec(@ctx)
    d.isTime().should.be.true()
    d.year.should.equal 0
    d.month.should.equal 1
    d.day.should.equal 1
    d.hour.should.equal 12
    d.timezoneOffset.should.equal @defaultOffset
    should.not.exist(d[field]) for field in [ 'minute', 'second', 'millisecond' ]

  it 'should execute minute precision correctly', ->
    d = @minute.exec(@ctx)
    d.isTime().should.be.true()
    d.year.should.equal 0
    d.month.should.equal 1
    d.day.should.equal 1
    d.hour.should.equal 12
    d.minute.should.equal 10
    d.timezoneOffset.should.equal @defaultOffset
    should.not.exist(d[field]) for field in [ 'second', 'millisecond' ]

  it 'should execute second precision correctly', ->
    d = @second.exec(@ctx)
    d.isTime().should.be.true()
    d.year.should.equal 0
    d.month.should.equal 1
    d.day.should.equal 1
    d.hour.should.equal 12
    d.minute.should.equal 10
    d.second.should.equal 59
    d.timezoneOffset.should.equal @defaultOffset
    should.not.exist(d.millisecond)

  it 'should execute millisecond precision correctly', ->
    d = @millisecond.exec(@ctx)
    d.isTime().should.be.true()
    d.year.should.equal 0
    d.month.should.equal 1
    d.day.should.equal 1
    d.hour.should.equal 12
    d.minute.should.equal 10
    d.second.should.equal 59
    d.millisecond.should.equal 456
    d.timezoneOffset.should.equal @defaultOffset

  it 'should execute timezone offsets correctly', ->
    d = @timezoneOffset.exec(@ctx)
    d.isTime().should.be.true()
    d.year.should.equal 0
    d.month.should.equal 1
    d.day.should.equal 1
    d.hour.should.equal 12
    d.minute.should.equal 10
    d.second.should.equal 59
    d.millisecond.should.equal 456
    d.timezoneOffset.should.equal -8

describe 'Today', ->
  @beforeEach ->
    setup @, data

  it 'should return only day components and timezone of today', ->
    jsDate = new Date()
    today = @todayVar.exec @ctx
    today.isTime().should.be.false()
    today.year.should.equal jsDate.getFullYear()
    today.month.should.equal jsDate.getMonth() + 1
    today.day.should.equal jsDate.getDate()
    today.timezoneOffset.should.equal jsDate.getTimezoneOffset() / 60 * -1
    should.not.exist(today[field]) for field in [ 'hour', 'minute', 'second', 'millisecond' ]

describe 'Now', ->
  @beforeEach ->
    setup @, data

  it 'should return all date components representing now', ->
    jsDate = new Date()
    now = @nowVar.exec @ctx
    now.isTime().should.be.false()
    now.year.should.equal jsDate.getFullYear()
    now.month.should.equal jsDate.getMonth() + 1
    now.day.should.equal jsDate.getDate()
    now.hour.should.equal jsDate.getHours()
    now.minute.should.exist
    now.second.should.exist
    now.millisecond.should.exist
    now.timezoneOffset.should.equal jsDate.getTimezoneOffset() / 60 * -1

describe 'TimeOfDay', ->
  @beforeEach ->
    setup @, data

  it 'should return all date components representing now', ->
    jsDate = new Date()
    tod = @timeOfDayVar.exec @ctx
    tod.isTime().should.be.true()
    tod.year.should.equal 0
    tod.month.should.equal 1
    tod.day.should.equal 1
    tod.hour.should.equal jsDate.getHours()
    tod.minute.should.exist
    tod.second.should.exist
    tod.millisecond.should.exist
    tod.timezoneOffset.should.equal jsDate.getTimezoneOffset() / 60 * -1

describe 'DateTimeComponentFrom', ->
  @beforeEach ->
    setup @, data

  it 'should return the year from the date', ->
    @year.exec(@ctx).should.equal 2000

  it 'should return the month from the date', ->
    @month.exec(@ctx).should.equal 3

  it 'should return the day from the date', ->
    @day.exec(@ctx).should.equal 15

  it 'should return the hour from the date', ->
    @hour.exec(@ctx).should.equal 13

  it 'should return the minute from the date', ->
    @minute.exec(@ctx).should.equal 30

  it 'should return the second from the date', ->
    @second.exec(@ctx).should.equal 25

  it 'should return the millisecond from the date', ->
    @millisecond.exec(@ctx).should.equal 200

  it 'should return null for imprecise components', ->
    result = @impreciseComponentTuple.exec(@ctx)
    result.should.eql {
      Year: 2000,
      Month: 3,
      Day: 15,
      Hour: null,
      Minute: null,
      Second: null,
      Millisecond: null
    }

  it 'should return null for null date', ->
    should(@nullDate.exec(@ctx)).be.null

describe 'DateFrom', ->
  @beforeEach ->
    setup @, data

  it 'should return the date from a fully defined DateTime', ->
    date = @date.exec(@ctx)
    date.year.should.equal 2000
    date.month.should.equal 3
    date.day.should.equal 15
    date.timezoneOffset.should.equal 1
    should.not.exist date.hour
    should.not.exist date.minute
    should.not.exist date.second
    should.not.exist date.millisecond

  it 'should return the defined date components from an imprecise date', ->
    date = @impreciseDate.exec(@ctx)
    date.year.should.equal 2000
    should.not.exist date.month
    should.not.exist date.day
    should.not.exist date.hour
    should.not.exist date.minute
    should.not.exist date.second
    should.not.exist date.millisecond

  it 'should return null for null date', ->
    should(@nullDate.exec(@ctx)).be.null

describe 'TimeFrom', ->
  @beforeEach ->
    setup @, data

  it 'should return the time from a fully defined DateTime (and date should be lowest expressible date)', ->
    time = @time.exec(@ctx)
    time.year.should.equal 0
    time.month.should.equal 1
    time.day.should.equal 1
    time.hour.should.equal 13
    time.minute.should.equal 30
    time.second.should.equal 25
    time.millisecond.should.equal 200
    time.timezoneOffset.should.equal 1

  it 'should return the null time components from a date with no time', ->
    noTime = @noTime.exec(@ctx)
    noTime.year.should.equal 0
    noTime.month.should.equal 1
    noTime.day.should.equal 1
    should.not.exist noTime.hour
    should.not.exist noTime.minute
    should.not.exist noTime.second
    should.not.exist noTime.millisecond

  it 'should return null for null date', ->
    should(@nullDate.exec(@ctx)).be.null

describe 'TimezoneFrom', ->
  @beforeEach ->
    setup @, data

  it 'should return the timezone from a fully defined DateTime', ->
    @centralEuropean.exec(@ctx).should.equal 1
    @easternStandard.exec(@ctx).should.equal -5

  it 'should return the default timezone when not specified', ->
    @defaultTimezone.exec(@ctx).should.equal (new Date()).getTimezoneOffset() / 60 * -1

  it 'should return null for null date', ->
    should(@nullDate.exec(@ctx)).be.null

describe 'SameAs', ->
  @beforeEach ->
    setup @, data

  it 'should properly determine when year is the same', ->
    @sameYear.exec(@ctx).should.be.true()
    @notSameYear.exec(@ctx).should.be.false()

  it 'should properly determine when month is the same', ->
    @sameMonth.exec(@ctx).should.be.true()
    @notSameMonth.exec(@ctx).should.be.false()
    @sameMonthWrongYear.exec(@ctx).should.be.false()

  it 'should properly determine when day is the same', ->
    @sameDay.exec(@ctx).should.be.true()
    @notSameDay.exec(@ctx).should.be.false()
    @sameDayWrongMonth.exec(@ctx).should.be.false()

  it 'should properly determine when hour is the same', ->
    @sameHour.exec(@ctx).should.be.true()
    @notSameHour.exec(@ctx).should.be.false()
    @sameHourWrongDay.exec(@ctx).should.be.false()

  it 'should properly determine when minute is the same', ->
    @sameMinute.exec(@ctx).should.be.true()
    @notSameMinute.exec(@ctx).should.be.false()
    @sameMinuteWrongHour.exec(@ctx).should.be.false()

  it 'should properly determine when second is the same', ->
    @sameSecond.exec(@ctx).should.be.true()
    @notSameSecond.exec(@ctx).should.be.false()
    @sameSecondWrongMinute.exec(@ctx).should.be.false()

  it 'should properly determine when millisecond is the same', ->
    @sameMillisecond.exec(@ctx).should.be.true()
    @notSameMillisecond.exec(@ctx).should.be.false()
    @sameMillisecondWrongSecond.exec(@ctx).should.be.false()

  it 'should properly determine same as using milliseconds', ->
    @same.exec(@ctx).should.be.true()
    @notSame.exec(@ctx).should.be.false()

  it 'should normalize timezones when determining sameness', ->
    @sameNormalized.exec(@ctx).should.be.true()
    @sameHourWrongTimezone.exec(@ctx).should.be.false()

  it 'should handle imprecision', ->
    should(@impreciseHour.exec(@ctx)).be.null
    @impreciseHourWrongDay.exec(@ctx).should.be.false()

  it 'should return null when either argument is null', ->
    should(@nullLeft.exec(@ctx)).be.null
    should(@nullRight.exec(@ctx)).be.null
    should(@nullBoth.exec(@ctx)).be.null

describe 'SameOrAfter', ->
  @beforeEach ->
    setup @, data

  it 'should properly determine when year is same or after', ->
    @sameYear.exec(@ctx).should.be.true()
    @yearAfter.exec(@ctx).should.be.true()
    @yearBefore.exec(@ctx).should.be.false()

  it 'should properly determine when month is same or after', ->
    @sameMonth.exec(@ctx).should.be.true()
    @monthAfter.exec(@ctx).should.be.true()
    @monthBefore.exec(@ctx).should.be.false()

  it 'should properly determine when day is same or after', ->
    @sameDay.exec(@ctx).should.be.true()
    @dayAfter.exec(@ctx).should.be.true()
    @dayBefore.exec(@ctx).should.be.false()

  it 'should properly determine when hour is same or after', ->
    @sameHour.exec(@ctx).should.be.true()
    @hourAfter.exec(@ctx).should.be.true()
    @hourBefore.exec(@ctx).should.be.false()

  it 'should properly determine when minute is same or after', ->
    @sameMinute.exec(@ctx).should.be.true()
    @minuteAfter.exec(@ctx).should.be.true()
    @minuteBefore.exec(@ctx).should.be.false()

  it 'should properly determine when second is same or after', ->
    @sameSecond.exec(@ctx).should.be.true()
    @secondAfter.exec(@ctx).should.be.true()
    @secondBefore.exec(@ctx).should.be.false()

  it 'should properly determine when millisecond is same or after', ->
    @sameMillisecond.exec(@ctx).should.be.true()
    @millisecondAfter.exec(@ctx).should.be.true()
    @millisecondBefore.exec(@ctx).should.be.false()

  it 'should properly determine same or after using ms when no precision defined', ->
    @same.exec(@ctx).should.be.true()
    @after.exec(@ctx).should.be.true()
    @before.exec(@ctx).should.be.false()

  it 'should consider precision units above the specified unit', ->
    @sameDayMonthBefore.exec(@ctx).should.be.false()
    @dayAfterMonthBefore.exec(@ctx).should.be.false()
    @dayBeforeMonthAfter.exec(@ctx).should.be.true()

  it 'should handle imprecision', ->
    should(@impreciseDay.exec(@ctx)).be.null
    @impreciseDayMonthAfter.exec(@ctx).should.be.true()
    @impreciseDayMonthBefore.exec(@ctx).should.be.false()

  it 'should normalize timezones', ->
    @sameHourNormalizeZones.exec(@ctx).should.be.true()
    @hourAfterNormalizeZones.exec(@ctx).should.be.true()
    @hourBeforeNormalizeZones.exec(@ctx).should.be.false()

  it 'should return null when either argument is null', ->
    should(@nullLeft.exec(@ctx)).be.null
    should(@nullRight.exec(@ctx)).be.null
    should(@nullBoth.exec(@ctx)).be.null

describe 'SameOrBefore', ->
  @beforeEach ->
    setup @, data

  it 'should properly determine when year is same or after', ->
    @sameYear.exec(@ctx).should.be.true()
    @yearAfter.exec(@ctx).should.be.false()
    @yearBefore.exec(@ctx).should.be.true()

  it 'should properly determine when month is same or after', ->
    @sameMonth.exec(@ctx).should.be.true()
    @monthAfter.exec(@ctx).should.be.false()
    @monthBefore.exec(@ctx).should.be.true()

  it 'should properly determine when day is same or after', ->
    @sameDay.exec(@ctx).should.be.true()
    @dayAfter.exec(@ctx).should.be.false()
    @dayBefore.exec(@ctx).should.be.true()

  it 'should properly determine when hour is same or after', ->
    @sameHour.exec(@ctx).should.be.true()
    @hourAfter.exec(@ctx).should.be.false()
    @hourBefore.exec(@ctx).should.be.true()

  it 'should properly determine when minute is same or after', ->
    @sameMinute.exec(@ctx).should.be.true()
    @minuteAfter.exec(@ctx).should.be.false()
    @minuteBefore.exec(@ctx).should.be.true()

  it 'should properly determine when second is same or after', ->
    @sameSecond.exec(@ctx).should.be.true()
    @secondAfter.exec(@ctx).should.be.false()
    @secondBefore.exec(@ctx).should.be.true()

  it 'should properly determine when millisecond is same or after', ->
    @sameMillisecond.exec(@ctx).should.be.true()
    @millisecondAfter.exec(@ctx).should.be.false()
    @millisecondBefore.exec(@ctx).should.be.true()

  it 'should properly determine same or after using ms when no precision defined', ->
    @same.exec(@ctx).should.be.true()
    @after.exec(@ctx).should.be.false()
    @before.exec(@ctx).should.be.true()

  it 'should consider precision units above the specified unit', ->
    @sameDayMonthBefore.exec(@ctx).should.be.true()
    @dayAfterMonthBefore.exec(@ctx).should.be.true()
    @dayBeforeMonthAfter.exec(@ctx).should.be.false()

  it 'should handle imprecision', ->
    should(@impreciseDay.exec(@ctx)).be.null
    @impreciseDayMonthAfter.exec(@ctx).should.be.false()
    @impreciseDayMonthBefore.exec(@ctx).should.be.true()

  it 'should normalize timezones', ->
    @sameHourNormalizeZones.exec(@ctx).should.be.true()
    @hourAfterNormalizeZones.exec(@ctx).should.be.false()
    @hourBeforeNormalizeZones.exec(@ctx).should.be.true()

  it 'should return null when either argument is null', ->
    should(@nullLeft.exec(@ctx)).be.null
    should(@nullRight.exec(@ctx)).be.null
    should(@nullBoth.exec(@ctx)).be.null

describe 'After', ->
  @beforeEach ->
    setup @, data

  it 'should properly determine when year is same or after', ->
    @sameYear.exec(@ctx).should.be.false()
    @yearAfter.exec(@ctx).should.be.true()
    @yearBefore.exec(@ctx).should.be.false()

  it 'should properly determine when month is same or after', ->
    @sameMonth.exec(@ctx).should.be.false()
    @monthAfter.exec(@ctx).should.be.true()
    @monthBefore.exec(@ctx).should.be.false()

  it 'should properly determine when day is same or after', ->
    @sameDay.exec(@ctx).should.be.false()
    @dayAfter.exec(@ctx).should.be.true()
    @dayBefore.exec(@ctx).should.be.false()

  it 'should properly determine when hour is same or after', ->
    @sameHour.exec(@ctx).should.be.false()
    @hourAfter.exec(@ctx).should.be.true()
    @hourBefore.exec(@ctx).should.be.false()

  it 'should properly determine when minute is same or after', ->
    @sameMinute.exec(@ctx).should.be.false()
    @minuteAfter.exec(@ctx).should.be.true()
    @minuteBefore.exec(@ctx).should.be.false()

  it 'should properly determine when second is same or after', ->
    @sameSecond.exec(@ctx).should.be.false()
    @secondAfter.exec(@ctx).should.be.true()
    @secondBefore.exec(@ctx).should.be.false()

  it 'should properly determine when millisecond is same or after', ->
    @sameMillisecond.exec(@ctx).should.be.false()
    @millisecondAfter.exec(@ctx).should.be.true()
    @millisecondBefore.exec(@ctx).should.be.false()

  it 'should properly determine same or after using ms when no precision defined', ->
    @same.exec(@ctx).should.be.false()
    @after.exec(@ctx).should.be.true()
    @before.exec(@ctx).should.be.false()

  it 'should handle imprecision', ->
    should(@impreciseDay.exec(@ctx)).be.null
    @impreciseDayMonthAfter.exec(@ctx).should.be.true()
    @impreciseDayMonthBefore.exec(@ctx).should.be.false()

  it 'should normalize timezones', ->
    @sameHourNormalizeZones.exec(@ctx).should.be.false()
    @hourAfterNormalizeZones.exec(@ctx).should.be.true()
    @hourBeforeNormalizeZones.exec(@ctx).should.be.false()

  it 'should return null when either argument is null', ->
    should(@nullLeft.exec(@ctx)).be.null
    should(@nullRight.exec(@ctx)).be.null
    should(@nullBoth.exec(@ctx)).be.null

describe 'Before', ->
  @beforeEach ->
    setup @, data

  it 'should properly determine when year is same or after', ->
    @sameYear.exec(@ctx).should.be.false()
    @yearAfter.exec(@ctx).should.be.false()
    @yearBefore.exec(@ctx).should.be.true()

  it 'should properly determine when month is same or after', ->
    @sameMonth.exec(@ctx).should.be.false()
    @monthAfter.exec(@ctx).should.be.false()
    @monthBefore.exec(@ctx).should.be.true()

  it 'should properly determine when day is same or after', ->
    @sameDay.exec(@ctx).should.be.false()
    @dayAfter.exec(@ctx).should.be.false()
    @dayBefore.exec(@ctx).should.be.true()

  it 'should properly determine when hour is same or after', ->
    @sameHour.exec(@ctx).should.be.false()
    @hourAfter.exec(@ctx).should.be.false()
    @hourBefore.exec(@ctx).should.be.true()

  it 'should properly determine when minute is same or after', ->
    @sameMinute.exec(@ctx).should.be.false()
    @minuteAfter.exec(@ctx).should.be.false()
    @minuteBefore.exec(@ctx).should.be.true()

  it 'should properly determine when second is same or after', ->
    @sameSecond.exec(@ctx).should.be.false()
    @secondAfter.exec(@ctx).should.be.false()
    @secondBefore.exec(@ctx).should.be.true()

  it 'should properly determine when millisecond is same or after', ->
    @sameMillisecond.exec(@ctx).should.be.false()
    @millisecondAfter.exec(@ctx).should.be.false()
    @millisecondBefore.exec(@ctx).should.be.true()

  it 'should properly determine same or after using ms when no precision defined', ->
    @same.exec(@ctx).should.be.false()
    @after.exec(@ctx).should.be.false()
    @before.exec(@ctx).should.be.true()

  it 'should handle imprecision', ->
    should(@impreciseDay.exec(@ctx)).be.null
    @impreciseDayMonthAfter.exec(@ctx).should.be.false()
    @impreciseDayMonthBefore.exec(@ctx).should.be.true()

  it 'should normalize timezones', ->
    @sameHourNormalizeZones.exec(@ctx).should.be.false()
    @hourAfterNormalizeZones.exec(@ctx).should.be.false()
    @hourBeforeNormalizeZones.exec(@ctx).should.be.true()

  it 'should return null when either argument is null', ->
    should(@nullLeft.exec(@ctx)).be.null
    should(@nullRight.exec(@ctx)).be.null
    should(@nullBoth.exec(@ctx)).be.null

describe 'DifferenceBetween', ->
  @beforeEach ->
    setup @, data

  it 'should properly execute years between', ->
    @yearsBetween.exec(@ctx).should.equal 1

  it 'should properly execute months between', ->
    @monthsBetween.exec(@ctx).should.equal 12

  it 'should properly execute weeks between', ->
    @weeksBetween.exec(@ctx).should.equal 52

  it 'should properly execute days between', ->
    @daysBetween.exec(@ctx).should.equal 365

  it 'should properly execute hours between', ->
    @hoursBetween.exec(@ctx).should.equal 24 * 365

  it 'should properly execute minutes between', ->
    @minutesBetween.exec(@ctx).should.equal 60 * 24 * 365

  it 'should properly execute seconds between', ->
    @secondsBetween.exec(@ctx).should.equal 60 * 60 * 24 * 365

  it 'should properly execute milliseconds between', ->
    @millisecondsBetween.exec(@ctx).should.equal 1000 * 60 * 60 * 24 * 365

  it 'should properly execute milliseconds between when date 1 is after date 2', ->
    @millisecondsBetweenReversed.exec(@ctx).should.equal -1 * 1000 * 60 * 60 * 24 * 365

  it 'should properly execute years between with an uncertainty', ->
    @yearsBetweenUncertainty.exec(@ctx).should.equal 0

  it 'should properly execute months between with an uncertainty', ->
    @monthsBetweenUncertainty.exec(@ctx).should.equal 0

  it 'should properly execute weeks between with an uncertainty', ->
    @weeksBetweenUncertainty.exec(@ctx).should.eql new Uncertainty(0, 4)

  it 'should properly execute days between with an uncertainty', ->
    @daysBetweenUncertainty.exec(@ctx).should.eql new Uncertainty(0, 30)

  it 'should properly execute hours between with an uncertainty', ->
    @hoursBetweenUncertainty.exec(@ctx).should.eql new Uncertainty(0, 743)

  it 'should properly execute minutes between with an uncertainty', ->
    @minutesBetweenUncertainty.exec(@ctx).should.eql new Uncertainty(0, 44639)

  it 'should properly execute seconds between with an uncertainty', ->
    @secondsBetweenUncertainty.exec(@ctx).should.eql new Uncertainty(0, 2678399)

  it 'should properly execute milliseconds between with an uncertainty', ->
    @millisecondsBetweenUncertainty.exec(@ctx).should.eql new Uncertainty(0, 2678399999)

  it 'should properly execute seconds between when date 1 is after date 2 with an uncertainty', ->
    @millisecondsBetweenReversedUncertainty.exec(@ctx).should.eql new Uncertainty(-2678399999, 0)

  it 'should properly execute hours between when springing ahead for DST', ->
    @hoursBetween1and3CrossingSpringDST.exec(@ctx).should.equal 1

  it 'should properly execute hours between when falling back for DST', ->
    @hoursBetween1and3CrossingFallDST.exec(@ctx).should.equal 3

describe 'DifferenceBetween Comparisons', ->
  @beforeEach ->
    setup @, data

  it 'should calculate days between > x', ->
    @greaterThan25DaysAfter.exec(@ctx).should.be.true()
    should(@greaterThan40DaysAfter.exec(@ctx)).be.null
    @greaterThan80DaysAfter.exec(@ctx).should.be.false()

  it 'should calculate days between >= x', ->
    @greaterOrEqualTo25DaysAfter.exec(@ctx).should.be.true()
    should(@greaterOrEqualTo40DaysAfter.exec(@ctx)).be.null
    @greaterOrEqualTo80DaysAfter.exec(@ctx).should.be.false()

  it 'should calculate days between = x', ->
    @equalTo25DaysAfter.exec(@ctx).should.be.false()
    should(@equalTo40DaysAfter.exec(@ctx)).be.null
    @equalTo80DaysAfter.exec(@ctx).should.be.false()

  it 'should calculate days between <= x', ->
    @lessOrEqualTo25DaysAfter.exec(@ctx).should.be.false()
    should(@lessOrEqualTo40DaysAfter.exec(@ctx)).be.null
    @lessOrEqualTo80DaysAfter.exec(@ctx).should.be.true()

  it 'should calculate days between < x', ->
    @lessThan25DaysAfter.exec(@ctx).should.be.false()
    should(@lessThan40DaysAfter.exec(@ctx)).be.null
    @lessThan80DaysAfter.exec(@ctx).should.be.true()

  it 'should calculate other way too', ->
    @twentyFiveDaysLessThanDaysBetween.exec(@ctx).should.be.true()
    should(@fortyDaysEqualToDaysBetween.exec(@ctx)).be.null
    @twentyFiveDaysGreaterThanDaysBetween.exec(@ctx).should.be.false()

  it 'should properly determine that Sep to Dec is NOT <= 2 months', ->
    @bonnieTestCase.exec(@ctx).should.be.false()

  it 'should properly determine that Sep to Dec is NOT <= 2 months with 0 timezone offset (Zulu)', ->
    # THIS for some reason is BROKEN!
    @bonnieTestCaseZulu.exec(@ctx).should.be.false()

describe 'DurationBetween', ->
  @beforeEach ->
    setup @, data

  it 'should properly execute years between', ->
    @yearsBetween.exec(@ctx).should.equal 1

  it 'should properly execute months between', ->
    @monthsBetween.exec(@ctx).should.equal 12

  it 'should properly execute days between', ->
    @daysBetween.exec(@ctx).should.equal 365 + 21

  it 'should properly execute weeks between', ->
    @weeksBetween.exec(@ctx).should.equal 55

  it 'should properly execute hours between', ->
    @hoursBetween.exec(@ctx).should.equal 24 * (365 + 21) + 11

  it 'should properly execute minutes between', ->
    @minutesBetween.exec(@ctx).should.equal 60 * (24 * (365 + 21) + 11) + 29

  it 'should properly execute seconds between', ->
    @secondsBetween.exec(@ctx).should.equal 60 * (60 * (24 * (365 + 21) + 11) + 29) + 29

  it 'should properly execute milliseconds between', ->
    @millisecondsBetween.exec(@ctx).should.equal 1000 * (60 * (60 * (24 * (365 + 21) + 11) + 29) + 29) + 500

  it 'should properly execute milliseconds between when date 1 is after date 2', ->
    @millisecondsBetweenReversed.exec(@ctx).should.equal -1 * 1000 * (60 * (60 * (24 * (365 + 21) + 11) + 29) + 29) - 500

  it 'should properly execute years between with an uncertainty', ->
    @yearsBetweenUncertainty.exec(@ctx).should.equal 0

  it 'should properly execute months between with an uncertainty', ->
    @monthsBetweenUncertainty.exec(@ctx).should.equal 0

  it 'should properly execute weeks between with an uncertainty', ->
    @weeksBetweenUncertainty.exec(@ctx).should.eql new Uncertainty(0, 4)

  it 'should properly execute days between with an uncertainty', ->
    @daysBetweenUncertainty.exec(@ctx).should.eql new Uncertainty(0, 30)

  it 'should properly execute hours between with an uncertainty', ->
    @hoursBetweenUncertainty.exec(@ctx).should.eql new Uncertainty(0, 743)

  it 'should properly execute minutes between with an uncertainty', ->
    @minutesBetweenUncertainty.exec(@ctx).should.eql new Uncertainty(0, 44639)

  it 'should properly execute seconds between with an uncertainty', ->
    @secondsBetweenUncertainty.exec(@ctx).should.eql new Uncertainty(0, 2678399)

  it 'should properly execute milliseconds between with an uncertainty', ->
    @millisecondsBetweenUncertainty.exec(@ctx).should.eql new Uncertainty(0, 2678399999)

  it 'should properly execute seconds between when date 1 is after date 2 with an uncertainty', ->
    @millisecondsBetweenReversedUncertainty.exec(@ctx).should.eql new Uncertainty(-2678399999, 0)

  it 'should properly execute hours between when falling back for DST', ->
    @hoursBetween1and3CrossingSpringDST.exec(@ctx).should.equal 1

  it 'should properly execute hours between when springing ahead for DST', ->
    @hoursBetween1and3CrossingFallDST.exec(@ctx).should.equal 3

describe 'DateMath', ->
  @beforeEach ->
    setup @, data

  it 'should properly add and subtract years', ->
    d = @plusThreeYears.exec(@ctx)
    dateCheck(d, 2016, 6, 15, 0, 0, 0, 0)
    d = @minusThreeYears.exec(@ctx)
    dateCheck(d, 2010, 6, 15, 0, 0, 0, 0)

  it 'should properly add and subtract months', ->
    d = @plusEightMonths.exec(@ctx)
    dateCheck(d, 2014, 2, 15, 0, 0, 0, 0)
    d = @minusEightMonths.exec(@ctx)
    dateCheck(d, 2012, 10, 15, 0, 0, 0, 0)

  it 'should properly add and subtract weeks', ->
    d = @plusThreeWeeks.exec(@ctx)
    dateCheck(d, 2013, 7, 6, 0, 0, 0, 0)
    d = @minusThreeWeeks.exec(@ctx)
    dateCheck(d, 2013, 5, 25, 0, 0, 0, 0)

  it 'should properly add and subtract days', ->
    d = @plusTwentyDays.exec(@ctx)
    dateCheck(d, 2013, 7, 5, 0, 0, 0, 0)
    d = @minusTwentyDays.exec(@ctx)
    dateCheck(d, 2013, 5, 26, 0, 0, 0, 0)

  it 'should properly add and subtract hours', ->
    d = @plusThreeHours.exec(@ctx)
    dateCheck(d, 2013, 6, 15, 3, 0, 0, 0)
    d = @minusThreeHours.exec(@ctx)
    dateCheck(d, 2013, 6, 14, 21, 0, 0, 0)

  it 'should properly add and subtract minutes', ->
    d = @plusThreeMinutes.exec(@ctx)
    dateCheck(d, 2013, 6, 15, 0, 3, 0, 0)
    d = @minusThreeMinutes.exec(@ctx)
    dateCheck(d, 2013, 6, 14, 23, 57, 0, 0)

  it 'should properly add and subtract seconds', ->
    d = @plusThreeSeconds.exec(@ctx)
    dateCheck(d, 2013, 6, 15, 0, 0, 3, 0)
    d = @minusThreeSeconds.exec(@ctx)
    dateCheck(d, 2013, 6, 14, 23, 59, 57, 0)

  it 'should properly add and subtract milliseconds', ->
    d = @plusThreeMilliseconds.exec(@ctx)
    dateCheck(d, 2013, 6, 15, 0, 0, 0, 3)
    d = @minusThreeMilliseconds.exec(@ctx)
    dateCheck(d, 2013, 6, 14, 23, 59, 59, 997)

dateCheck = (date, year, month, day, hour, minute, second, millisecond) ->
  date.year.should.equal year
  date.month.should.equal month
  date.day.should.equal day
  date.hour.should.equal hour
  date.minute.should.equal minute
  date.second.should.equal second
  date.millisecond.should.equal millisecond
