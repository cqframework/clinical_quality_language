should = require 'should'
setup = require '../../setup'
data = require './data'
{ Uncertainty } = require '../../../lib/datatypes/uncertainty'

describe 'DateTimeFunctionRef', ->
  @beforeEach ->
    setup @, data

  it 'should execute year precision correctly', ->
    d = @year.exec(@ctx)
    d.year.should.equal 2012
    should.not.exist(d[field]) for field in [ 'month', 'day', 'hour', 'minute', 'second', 'millisecond', 'timeZoneOffset' ]

  it 'should execute month precision correctly', ->
    d = @month.exec(@ctx)
    d.year.should.equal 2012
    d.month.should.equal 4
    should.not.exist(d[field]) for field in [ 'day', 'hour', 'minute', 'second', 'millisecond', 'timeZoneOffset' ]

  it 'should execute day precision correctly', ->
    d = @day.exec(@ctx)
    d.year.should.equal 2012
    d.month.should.equal 4
    d.day.should.equal 15
    should.not.exist(d[field]) for field in [ 'hour', 'minute', 'second', 'millisecond', 'timeZoneOffset' ]

  it 'should execute hour precision correctly', ->
    d = @hour.exec(@ctx)
    d.year.should.equal 2012
    d.month.should.equal 4
    d.day.should.equal 15
    d.hour.should.equal 12
    should.not.exist(d[field]) for field in [ 'minute', 'second', 'millisecond', 'timeZoneOffset' ]

  it 'should execute minute precision correctly', ->
    d = @minute.exec(@ctx)
    d.year.should.equal 2012
    d.month.should.equal 4
    d.day.should.equal 15
    d.hour.should.equal 12
    d.minute.should.equal 10
    should.not.exist(d[field]) for field in [ 'second', 'millisecond', 'timeZoneOffset' ]

  it 'should execute second precision correctly', ->
    d = @second.exec(@ctx)
    d.year.should.equal 2012
    d.month.should.equal 4
    d.day.should.equal 15
    d.hour.should.equal 12
    d.minute.should.equal 10
    d.second.should.equal 59
    should.not.exist(d[field]) for field in [ 'millisecond', 'timeZoneOffset' ]

  it 'should execute millisecond precision correctly', ->
    d = @millisecond.exec(@ctx)
    d.year.should.equal 2012
    d.month.should.equal 4
    d.day.should.equal 15
    d.hour.should.equal 12
    d.minute.should.equal 10
    d.second.should.equal 59
    d.millisecond.should.equal 456
    should.not.exist(d.timeZoneOffset)

  it 'should execute timezone offsets correctly', ->
    d = @timeZoneOffset.exec(@ctx)
    d.year.should.equal 2012
    d.month.should.equal 4
    d.day.should.equal 15
    d.hour.should.equal 12
    d.minute.should.equal 10
    d.second.should.equal 59
    d.millisecond.should.equal 456
    d.timeZoneOffset.should.equal -5

describe 'DurationBetween', ->
  @beforeEach ->
    setup @, data

  it 'should properly execute years between', ->
    @yearsBetween.exec(@ctx).should.equal 1

  it 'should properly execute months between', ->
    @monthsBetween.exec(@ctx).should.equal 12

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

describe 'DurationBetween Comparisons', ->
  @beforeEach ->
    setup @, data

  it 'should calculate days between > x', ->
    @greaterThan25DaysAfter.exec(@ctx).should.be.true
    should(@greaterThan40DaysAfter.exec(@ctx)).be.null
    @greaterThan80DaysAfter.exec(@ctx).should.be.false

  it 'should calculate days between >= x', ->
    @greaterOrEqualTo25DaysAfter.exec(@ctx).should.be.true
    should(@greaterOrEqualTo40DaysAfter.exec(@ctx)).be.null
    @greaterOrEqualTo80DaysAfter.exec(@ctx).should.be.false

  it 'should calculate days between = x', ->
    @equalTo25DaysAfter.exec(@ctx).should.be.false
    should(@equalTo40DaysAfter.exec(@ctx)).be.null
    @equalTo80DaysAfter.exec(@ctx).should.be.false

  it 'should calculate days between <= x', ->
    @lessOrEqualTo25DaysAfter.exec(@ctx).should.be.false
    should(@lessOrEqualTo40DaysAfter.exec(@ctx)).be.null
    @lessOrEqualTo80DaysAfter.exec(@ctx).should.be.true

  it 'should calculate days between < x', ->
    @lessThan25DaysAfter.exec(@ctx).should.be.false
    should(@lessThan40DaysAfter.exec(@ctx)).be.null
    @lessThan80DaysAfter.exec(@ctx).should.be.true

  it 'should calculate other way too', ->
    @twentyFiveDaysLessThanDaysBetween.exec(@ctx).should.be.true
    should(@fortyDaysEqualToDaysBetween.exec(@ctx)).be.null
    @twentyFiveDaysGreaterThanDaysBetween.exec(@ctx).should.be.false
