should = require 'should'
TH = require './cql-test-helper'
DT = require '../lib/cql-datatypes'

setupIntervalsAndDateTimes = (test) ->
  test['sameAs'] = {
    #    |----------X----------|
    #    |----------Y----------|
    x: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2012-12-31T23:59:59')),
    y: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2012-12-31T23:59:59'))
  }
  test['before'] = {
    #    |----------X----------|
    #                                   |----------Y----------|
    x: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2012-04-01T00:00:00')),
    y: new TH.Interval(DT.DateTime.parse('2012-07-01T00:00:00'), DT.DateTime.parse('2012-12-31T23:59:59'))
  }
  test['meets'] = {
    #    |----------X----------|
    #                           |-----------Y----------|
    x: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2012-06-30T23:59:59')),
    y: new TH.Interval(DT.DateTime.parse('2012-07-01T00:00:00'), DT.DateTime.parse('2012-12-31T23:59:59'))
  }
  test['overlaps'] = {
    #    |----------X----------|
    #                  |----------Y----------|
    x: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2012-09-01T00:00:00')),
    y: new TH.Interval(DT.DateTime.parse('2012-06-01T00:00:00'), DT.DateTime.parse('2012-12-31T23:59:59'))
  }
  test['begins'] = {
    #    |-----X-----|
    #    |----------Y----------|
    x: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2012-07-01T00:00:00')),
    y: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2012-12-31T23:59:59'))
  }
  test['during'] = {
    #         |-----X-----|
    #    |----------Y----------|
    x: new TH.Interval(DT.DateTime.parse('2012-05-01T00:00:00'), DT.DateTime.parse('2012-07-01T00:00:00')),
    y: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2012-12-31T23:59:59'))
  }
  test['ends'] = {
    #              |-----X-----|
    #    |----------Y----------|
    x: new TH.Interval(DT.DateTime.parse('2012-07-01T00:00:00'), DT.DateTime.parse('2012-12-31T23:59:59')),
    y: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2012-12-31T23:59:59'))
  }
  test['all2012'] = new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2012-12-31T23:59:59'))
  test['bef2012'] = TH.DateTime.parse('2011-06-01T00:00:00')
  test['beg2012'] = TH.DateTime.parse('2012-01-01T00:00:00')
  test['mid2012'] = TH.DateTime.parse('2012-06-01T00:00:00')
  test['end2012'] = TH.DateTime.parse('2012-12-31T23:59:59')
  test['aft2012'] = TH.DateTime.parse('2013-06-01T00:00:00')

xy = (obj) -> [obj.x, obj.y]

describe 'Code', ->
  @beforeEach ->
    @code = new DT.Code('ABC', '5.4.3.2.1', '1')

  it 'should properly represent the code, system, and version', ->
    @code.code.should.equal 'ABC'
    @code.system.should.equal '5.4.3.2.1'
    @code.version.should.equal '1'

describe 'ValueSet', ->
  @beforeEach ->
    @valueSet = new DT.ValueSet('1.2.3.4.5', '1', [
        new DT.Code('ABC', '5.4.3.2.1', '1'),
        new DT.Code('DEF', '5.4.3.2.1', '2'),
        new DT.Code('GHI', '5.4.3.4.5', '3'),
      ])

  it 'should properly represent the OID, version and codes', ->
    @valueSet.oid.should.equal '1.2.3.4.5'
    @valueSet.version.should.equal '1'
    @valueSet.codes.length.should.equal 3
    @valueSet.codes[0].should.eql new DT.Code('ABC', '5.4.3.2.1', '1')
    @valueSet.codes[1].should.eql new DT.Code('DEF', '5.4.3.2.1', '2')
    @valueSet.codes[2].should.eql new DT.Code('GHI', '5.4.3.4.5', '3')

  it 'should find code by name', ->
    @valueSet.hasCode('DEF').should.be.true

  it 'should find code by name and system', ->
    @valueSet.hasCode('DEF', '5.4.3.2.1').should.be.true

  it 'should find code by name, system, and version', ->
    @valueSet.hasCode('DEF', '5.4.3.2.1', '2').should.be.true

  it 'should find code by Code object', ->
    @valueSet.hasCode(new DT.Code('DEF', '5.4.3.2.1', '2')).should.be.true

  it 'should not find code with wrong name', ->
    @valueSet.hasCode('XYZ').should.be.false

  it 'should not find code with wrong system', ->
    @valueSet.hasCode('DEF', '0.0.0.0.0').should.be.false

  it 'should not find code with wrong version', ->
    @valueSet.hasCode('DEF', '5.4.3.2.1', '3').should.be.false

  it 'should not find code with wrong Code object', ->
    @valueSet.hasCode(new DT.Code('DEF', '5.4.3.2.1', '3')).should.be.false

describe 'ThreeValuedLogic.and', ->

  it 'should return true when all is true', ->
    DT.ThreeValuedLogic.and(true, true, true, true, true).should.be.true

  it 'should return false when at least one is false', ->
    DT.ThreeValuedLogic.and(true, true, false, true, true).should.be.false
    DT.ThreeValuedLogic.and(null, null, false, null, null).should.be.false
    DT.ThreeValuedLogic.and(true, true, false, null, true).should.be.false
    DT.ThreeValuedLogic.and(false, false, false, false, false).should.be.false

  it 'should return null when there is at least one null with no falses', ->
    should.not.exist DT.ThreeValuedLogic.and(true, true, null, true, true)
    should.not.exist DT.ThreeValuedLogic.and(null, null, null, null, null)

describe 'ThreeValuedLogic.or', ->

  it 'should return true when at least one is true', ->
    DT.ThreeValuedLogic.or(false, false, true, false, false).should.be.true
    DT.ThreeValuedLogic.or(null, null, true, null, null).should.be.true
    DT.ThreeValuedLogic.or(false, false, true, null, false).should.be.true
    DT.ThreeValuedLogic.or(true, true, true, true, true).should.be.true

  it 'should return false when all is false', ->
    DT.ThreeValuedLogic.or(false, false, false, false, false).should.be.false

  it 'should return null when there is at least one null with no trues', ->
    should.not.exist DT.ThreeValuedLogic.or(false, false, null, false, false)
    should.not.exist DT.ThreeValuedLogic.or(null, null, null, null, null)

describe 'ThreeValuedLogic.not', ->

  it 'should return true when input is false', ->
    DT.ThreeValuedLogic.not(false).should.be.true

  it 'should return false when input is true', ->
    DT.ThreeValuedLogic.not(true).should.be.false

  it 'should return null when input is null', ->
    should.not.exist DT.ThreeValuedLogic.not(null)

describe 'DateTime', ->

  it 'should properly set all properties when constructed', ->
    d = new DT.DateTime(2000, 12, 1, 3, 25, 59)
    d.year.should.equal 2000
    d.month.should.equal 12
    d.day.should.equal 1
    d.hour.should.equal 3
    d.minute.should.equal 25
    d.second.should.equal 59

  it 'should leave unset properties as undefined', ->
    d = new DT.DateTime(2000)
    d.year.should.equal 2000
    should.not.exist d.month
    should.not.exist d.day
    should.not.exist d.hour
    should.not.exist d.minute
    should.not.exist d.second

  it 'should parse yyyy', ->
    d = DT.DateTime.parse '2012'
    d.should.eql new DT.DateTime(2012)

  it 'should parse yyyy-mm', ->
    d = DT.DateTime.parse '2012-10'
    d.should.eql new DT.DateTime(2012, 10)

  it 'should parse yyyy-mm-dd', ->
    d = DT.DateTime.parse '2012-10-25'
    d.should.eql new DT.DateTime(2012, 10, 25)

  it 'should parse yyyy-mm-ddThh', ->
    d = DT.DateTime.parse '2012-10-25T12'
    d.should.eql new DT.DateTime(2012, 10, 25, 12)

  it 'should parse yyyy-mm-ddThh:mm', ->
    d = DT.DateTime.parse '2012-10-25T12:55'
    d.should.eql new DT.DateTime(2012, 10, 25, 12, 55)

  it 'should parse yyyy-mm-ddThh:mm:ss', ->
    d = DT.DateTime.parse '2012-10-25T12:55:14'
    d.should.eql new DT.DateTime(2012, 10, 25, 12, 55, 14)

  it 'should not parse invalid strings', ->
    should.not.exist DT.DateTime.parse '20121025'

  it 'should construct from a javascript date', ->
    DT.DateTime.fromDate(new Date(1999, 1, 16, 13, 56, 24)).should.eql DT.DateTime.parse('1999-02-16T13:56:24')

  it 'should copy a fully define DateTime', ->
    original = DT.DateTime.parse('1999-02-16T13:56:24')
    copy = original.copy()
    copy.should.eql original
    copy.should.not.equal original

  it 'should copy an imprecise DateTime', ->
    original = DT.DateTime.parse('1999-02')
    copy = original.copy()
    copy.should.eql original
    copy.should.not.equal original

  it 'should know if it is precise', ->
    DT.DateTime.parse('2000-01-01T00:00:00').isPrecise().should.be.true
    DT.DateTime.parse('2000-01-01T00:00').isPrecise().should.be.false
    DT.DateTime.parse('2000-01-01T00').isPrecise().should.be.false
    DT.DateTime.parse('2000-01-01').isPrecise().should.be.false
    DT.DateTime.parse('2000-01').isPrecise().should.be.false
    DT.DateTime.parse('2000').isPrecise().should.be.false

  it 'should know if it is imprecise', ->
    DT.DateTime.parse('2000-01-01T00:00:00').isImprecise().should.be.false
    DT.DateTime.parse('2000-01-01T00:00').isImprecise().should.be.true
    DT.DateTime.parse('2000-01-01T00').isImprecise().should.be.true
    DT.DateTime.parse('2000-01-01').isImprecise().should.be.true
    DT.DateTime.parse('2000-01').isImprecise().should.be.true
    DT.DateTime.parse('2000').isImprecise().should.be.true

  it 'should be able to convert imprecise dates to earliest possible date', ->
    DT.DateTime.parse('2000-03-25T12:15:43').asLowest().should.eql DT.DateTime.parse('2000-03-25T12:15:43')
    DT.DateTime.parse('2000-03-25T12:15').asLowest().should.eql DT.DateTime.parse('2000-03-25T12:15:00')
    DT.DateTime.parse('2000-03-25T12').asLowest().should.eql DT.DateTime.parse('2000-03-25T12:00:00')
    DT.DateTime.parse('2000-03-25').asLowest().should.eql DT.DateTime.parse('2000-03-25T00:00:00')
    DT.DateTime.parse('2000-03').asLowest().should.eql DT.DateTime.parse('2000-03-01T00:00:00')
    DT.DateTime.parse('2000').asLowest().should.eql DT.DateTime.parse('2000-01-01T00:00:00')

  it 'should be able to convert imprecise dates to latest possible date', ->
    DT.DateTime.parse('2000-02-25T12:15:43').asHighest().should.eql DT.DateTime.parse('2000-02-25T12:15:43')
    DT.DateTime.parse('2000-02-25T12:15').asHighest().should.eql DT.DateTime.parse('2000-02-25T12:15:59')
    DT.DateTime.parse('2000-02-25T12').asHighest().should.eql DT.DateTime.parse('2000-02-25T12:59:59')
    DT.DateTime.parse('2000-02-25').asHighest().should.eql DT.DateTime.parse('2000-02-25T23:59:59')
    DT.DateTime.parse('2000-02').asHighest().should.eql DT.DateTime.parse('2000-02-29T23:59:59')
    DT.DateTime.parse('1999-02').asHighest().should.eql DT.DateTime.parse('1999-02-28T23:59:59')
    DT.DateTime.parse('2000').asHighest().should.eql DT.DateTime.parse('2000-12-31T23:59:59')

  it 'should convert to javascript Date', ->
    DT.DateTime.parse('2012-10-25T12:55:14').toJSDate().should.eql new Date(2012, 9, 25, 12, 55, 14)

  it 'should floor unknown values when it converts to javascript Date', ->
    DT.DateTime.parse('2012').toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)

describe 'DateTime.add', ->

  it 'should add units for simple cases', ->
    simple = DT.DateTime.parse('2000-06-15T10:20:30')
    simple.add(1, DT.DateTime.Unit.YEAR).should.eql DT.DateTime.parse('2001-06-15T10:20:30')
    simple.add(1, DT.DateTime.Unit.MONTH).should.eql DT.DateTime.parse('2000-07-15T10:20:30')
    simple.add(1, DT.DateTime.Unit.DAY).should.eql DT.DateTime.parse('2000-06-16T10:20:30')
    simple.add(1, DT.DateTime.Unit.HOUR).should.eql DT.DateTime.parse('2000-06-15T11:20:30')
    simple.add(1, DT.DateTime.Unit.MINUTE).should.eql DT.DateTime.parse('2000-06-15T10:21:30')
    simple.add(1, DT.DateTime.Unit.SECOND).should.eql DT.DateTime.parse('2000-06-15T10:20:31')

  it 'should subtract units for simple cases', ->
    simple = DT.DateTime.parse('2000-06-15T10:20:30')
    simple.add(-1, DT.DateTime.Unit.YEAR).should.eql DT.DateTime.parse('1999-06-15T10:20:30')
    simple.add(-1, DT.DateTime.Unit.MONTH).should.eql DT.DateTime.parse('2000-05-15T10:20:30')
    simple.add(-1, DT.DateTime.Unit.DAY).should.eql DT.DateTime.parse('2000-06-14T10:20:30')
    simple.add(-1, DT.DateTime.Unit.HOUR).should.eql DT.DateTime.parse('2000-06-15T09:20:30')
    simple.add(-1, DT.DateTime.Unit.MINUTE).should.eql DT.DateTime.parse('2000-06-15T10:19:30')
    simple.add(-1, DT.DateTime.Unit.SECOND).should.eql DT.DateTime.parse('2000-06-15T10:20:29')

  it 'should rollover when you add past a boundary', ->
    almostMidnight = DT.DateTime.parse('2000-12-31T23:59:59')
    almostMidnight.add(1, DT.DateTime.Unit.MONTH).should.eql DT.DateTime.parse('2001-01-31T23:59:59')
    almostMidnight.add(1, DT.DateTime.Unit.DAY).should.eql DT.DateTime.parse('2001-01-01T23:59:59')
    almostMidnight.add(1, DT.DateTime.Unit.HOUR).should.eql DT.DateTime.parse('2001-01-01T00:59:59')
    almostMidnight.add(1, DT.DateTime.Unit.MINUTE).should.eql DT.DateTime.parse('2001-01-01T00:00:59')
    almostMidnight.add(1, DT.DateTime.Unit.SECOND).should.eql DT.DateTime.parse('2001-01-01T00:00:00')

  it 'should rollover when you subtract past a boundary', ->
    midnight = DT.DateTime.parse('2001-01-01T00:00:00')
    midnight.add(-1, DT.DateTime.Unit.MONTH).should.eql DT.DateTime.parse('2000-12-01T00:00:00')
    midnight.add(-1, DT.DateTime.Unit.DAY).should.eql DT.DateTime.parse('2000-12-31T00:00:00')
    midnight.add(-1, DT.DateTime.Unit.HOUR).should.eql DT.DateTime.parse('2000-12-31T23:00:00')
    midnight.add(-1, DT.DateTime.Unit.MINUTE).should.eql DT.DateTime.parse('2000-12-31T23:59:00')
    midnight.add(-1, DT.DateTime.Unit.SECOND).should.eql DT.DateTime.parse('2000-12-31T23:59:59')

  it 'should still work for imprecise numbers, when adding to a defined field', ->
    DT.DateTime.parse('2000-06-15T10:20').add(50, DT.DateTime.Unit.MINUTE).should.eql DT.DateTime.parse('2000-06-15T11:10')
    DT.DateTime.parse('2000-06-15T10:20').add(3, DT.DateTime.Unit.DAY).should.eql DT.DateTime.parse('2000-06-18T10:20')
    DT.DateTime.parse('2000-06-15T10').add(14, DT.DateTime.Unit.HOUR).should.eql DT.DateTime.parse('2000-06-16T00')
    DT.DateTime.parse('2000-06-15').add(30, DT.DateTime.Unit.DAY).should.eql DT.DateTime.parse('2000-07-15')
    DT.DateTime.parse('2000-06').add(8, DT.DateTime.Unit.MONTH).should.eql DT.DateTime.parse('2001-02')
    DT.DateTime.parse('2000').add(5, DT.DateTime.Unit.YEAR).should.eql DT.DateTime.parse('2005')

  it 'should not add anything on undefined fields', ->
    DT.DateTime.parse('2000-06-15T10:20').add(100, DT.DateTime.Unit.SECOND).should.eql DT.DateTime.parse('2000-06-15T10:20')
    DT.DateTime.parse('2000-06-15T10').add(100, DT.DateTime.Unit.MINUTE).should.eql DT.DateTime.parse('2000-06-15T10')
    DT.DateTime.parse('2000-06-15').add(100, DT.DateTime.Unit.HOUR).should.eql DT.DateTime.parse('2000-06-15')
    DT.DateTime.parse('2000-06').add(100, DT.DateTime.Unit.DAY).should.eql DT.DateTime.parse('2000-06')
    DT.DateTime.parse('2000').add(100, DT.DateTime.Unit.MONTH).should.eql DT.DateTime.parse('2000')
    DT.DateTime.parse('2000').add(100, DT.DateTime.Unit.MINUTE).should.eql DT.DateTime.parse('2000')

  it 'should not mutate the original object', ->
    date1 = DT.DateTime.parse('2000-06-15T10:20:30')
    date2 = date1.add(6, DT.DateTime.Unit.MONTH)
    date1.should.eql DT.DateTime.parse('2000-06-15T10:20:30')
    date2.should.eql DT.DateTime.parse('2000-12-15T10:20:30')

  it 'should return a different object (copy)', ->
    date1 = DT.DateTime.parse('2000-06-15T10:20:30')
    date2 = date1.add(0, DT.DateTime.Unit.SECOND)
    date1.should.eql date2
    date1.should.not.equal date2

describe 'DateTime.sameAs', ->
  it 'should always accept cases where a is same as b', ->
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45')).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.SECOND).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.YEAR).should.be.true

  it 'should properly calculate cases where the second is different', ->
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:46')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.YEAR).should.be.true

  it 'should properly calculate cases where the minute is different', ->
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:36:45')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:36:45'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:36:45'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:36:45'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:36:45'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:36:45'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:36:45'), DT.DateTime.Unit.YEAR).should.be.true

  it 'should properly calculate cases where the hour is different', ->
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T13:35:45')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T13:35:45'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T13:35:45'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T13:35:45'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T13:35:45'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T13:35:45'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T13:35:45'), DT.DateTime.Unit.YEAR).should.be.true

  it 'should properly calculate cases where the day is different', ->
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-16T12:35:45')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-16T12:35:45'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-16T12:35:45'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-16T12:35:45'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-16T12:35:45'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-16T12:35:45'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-16T12:35:45'), DT.DateTime.Unit.YEAR).should.be.true

  it 'should properly calculate cases where the month is different', ->
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-06-15T12:35:45')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-06-15T12:35:45'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-06-15T12:35:45'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-06-15T12:35:45'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-06-15T12:35:45'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-06-15T12:35:45'), DT.DateTime.Unit.MONTH).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-06-15T12:35:45'), DT.DateTime.Unit.YEAR).should.be.true

  it 'should properly calculate cases where the year is different', ->
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2001-05-15T12:35:45')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2001-05-15T12:35:45'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2001-05-15T12:35:45'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2001-05-15T12:35:45'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2001-05-15T12:35:45'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2001-05-15T12:35:45'), DT.DateTime.Unit.MONTH).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2001-05-15T12:35:45'), DT.DateTime.Unit.YEAR).should.be.false

  it 'should handle imprecision correctly with missing seconds', ->
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.SECOND)
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.SECOND)
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.SECOND)
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.YEAR).should.be.true

    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:36')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-05-15T12:36').sameAs(DT.DateTime.parse('2000-05-15T12:35:45')).should.be.false
    DT.DateTime.parse('2000-05-15T12:36').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:36').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:36').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:36').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:36').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:36').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:36')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.YEAR).should.be.true

it 'should handle imprecision correctly with missing minutes', ->
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.MINUTE)
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'))
    should.not.exist DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MINUTE)
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12'))
    should.not.exist DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.MINUTE)
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.YEAR).should.be.true

    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T13')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-05-15T13').sameAs(DT.DateTime.parse('2000-05-15T12:35:45')).should.be.false
    DT.DateTime.parse('2000-05-15T13').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T13').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T13').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T13').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T13').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T13').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T13')).should.be.false
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.YEAR).should.be.true

it 'should handle imprecision correctly with missing hours', ->
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.HOUR)
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'))
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.HOUR)
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15'))
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.HOUR)
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.YEAR).should.be.true

    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-16')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-05-16').sameAs(DT.DateTime.parse('2000-05-15T12:35:45')).should.be.false
    DT.DateTime.parse('2000-05-16').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-16').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-16').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-16').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05-16').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-16').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-16')).should.be.false
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.YEAR).should.be.true

it 'should handle imprecision correctly with missing days', ->
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.HOUR)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.DAY)
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'))
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.HOUR)
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.DAY)
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05'))
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.HOUR)
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.DAY)
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.YEAR).should.be.true

    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-06')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.MONTH).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-06').sameAs(DT.DateTime.parse('2000-05-15T12:35:45')).should.be.false
    DT.DateTime.parse('2000-06').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-06').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-06').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-06').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-06').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MONTH).should.be.false
    DT.DateTime.parse('2000-06').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-06')).should.be.false
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.MONTH).should.be.false
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.YEAR).should.be.true

it 'should handle imprecision correctly with missing months', ->
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.HOUR)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.DAY)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.MONTH)
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'))
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.HOUR)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.DAY)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MONTH)
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000'))
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.HOUR)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.DAY)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.MONTH)
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.YEAR).should.be.true

    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2001')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.MONTH).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.YEAR).should.be.false
    DT.DateTime.parse('2001').sameAs(DT.DateTime.parse('2000-05-15T12:35:45')).should.be.false
    DT.DateTime.parse('2001').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2001').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2001').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2001').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2001').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MONTH).should.be.false
    DT.DateTime.parse('2001').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.YEAR).should.be.false
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2001')).should.be.false
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.MONTH).should.be.false
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.YEAR).should.be.false

describe 'DateTime.before', ->

  it 'should accept cases where a is before b', ->
    DT.DateTime.parse('2000-12-31T23:59:58').before(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.true
    DT.DateTime.parse('2000-12-31T23:58:59').before(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.true
    DT.DateTime.parse('2000-12-31T22:59:59').before(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.true
    DT.DateTime.parse('2000-12-30T23:59:59').before(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.true
    DT.DateTime.parse('2000-11-31T23:59:59').before(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.true
    DT.DateTime.parse('1999-12-31T23:59:59').before(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.true

  it 'should reject cases where a is after b', ->
    DT.DateTime.parse('2000-01-01T00:00:01').before(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000-01-01T00:01:00').before(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000-01-01T01:00:00').before(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000-01-02T00:00:00').before(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000-02-01T00:00:00').before(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2001-01-01T00:00:00').before(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false

  it 'should reject cases where a is b', ->
    DT.DateTime.parse('2000-01-01T00:00:00').before(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false

  it 'should return null in cases where a is b but there are unknown values', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').before(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').before(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01').before(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01').before(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000').before(DT.DateTime.parse('2000'))

  it 'should return null in cases where a has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').before(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').before(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000-01-01').before(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000-01').before(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000').before(DT.DateTime.parse('2000-01-01T00:00:59'))

  it 'should return null in cases where b has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').before(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').before(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').before(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').before(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').before(DT.DateTime.parse('2000'))

  it 'should accept cases where a has unknown values but is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00').before(DT.DateTime.parse('2000-01-01T00:01:00')).should.be.true
    DT.DateTime.parse('2000-01-01T00').before(DT.DateTime.parse('2000-01-01T01:00:00')).should.be.true
    DT.DateTime.parse('2000-01-01').before(DT.DateTime.parse('2000-01-02T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01').before(DT.DateTime.parse('2000-02-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000').before(DT.DateTime.parse('2001-01-01T00:00:00')).should.be.true

  it 'should accept cases where b has unknown values but a is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00:00').before(DT.DateTime.parse('2000-01-01T00:01')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00').before(DT.DateTime.parse('2000-01-01T01')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00').before(DT.DateTime.parse('2000-01-02')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00').before(DT.DateTime.parse('2000-02')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00').before(DT.DateTime.parse('2001')).should.be.true

  it 'should reject cases where a has unknown values but is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:01').before(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000-01-01T01').before(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000-01-02').before(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000-02').before(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2001').before(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false

  it 'should reject cases where b has unknown values but a is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:01:00').before(DT.DateTime.parse('2000-01-01T00:00')).should.be.false
    DT.DateTime.parse('2000-01-01T01:00:00').before(DT.DateTime.parse('2000-01-01T00')).should.be.false
    DT.DateTime.parse('2000-01-02T00:00:00').before(DT.DateTime.parse('2000-01-01')).should.be.false
    DT.DateTime.parse('2000-02-01T00:00:00').before(DT.DateTime.parse('2000-01')).should.be.false
    DT.DateTime.parse('2001-01-01T00:00:00').before(DT.DateTime.parse('2000')).should.be.false

describe 'DateTime.beforeOrSameAs', ->

  it 'should accept cases where a is before b', ->
    DT.DateTime.parse('2000-12-31T23:59:58').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.true
    DT.DateTime.parse('2000-12-31T23:58:59').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.true
    DT.DateTime.parse('2000-12-31T22:59:59').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.true
    DT.DateTime.parse('2000-12-30T23:59:59').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.true
    DT.DateTime.parse('2000-11-31T23:59:59').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.true
    DT.DateTime.parse('1999-12-31T23:59:59').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.true

  it 'should reject cases where a is after b', ->
    DT.DateTime.parse('2000-01-01T00:00:01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000-01-01T00:01:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000-01-01T01:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000-01-02T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000-02-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2001-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false

  it 'should accept cases where a is b', ->
    DT.DateTime.parse('2000-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true

  it 'should return null in cases where a is b but there are unknown values in a and b', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01').beforeOrSameAs(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01').beforeOrSameAs(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000').beforeOrSameAs(DT.DateTime.parse('2000'))

  it 'should return null in cases where a has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:58'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:59:58'))
    should.not.exist DT.DateTime.parse('2000-01-01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T23:59:58'))
    should.not.exist DT.DateTime.parse('2000-01').beforeOrSameAs(DT.DateTime.parse('2000-01-31T23:59:58'))
    should.not.exist DT.DateTime.parse('2000').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:58'))

  it 'should return null in cases where b has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').beforeOrSameAs(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').beforeOrSameAs(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').beforeOrSameAs(DT.DateTime.parse('2000'))

  it 'should accept cases where a has unknown values but is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:01:00')).should.be.true
    DT.DateTime.parse('2000-01-01T00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T01:00:00')).should.be.true
    DT.DateTime.parse('2000-01-01').beforeOrSameAs(DT.DateTime.parse('2000-01-02T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01').beforeOrSameAs(DT.DateTime.parse('2000-02-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000').beforeOrSameAs(DT.DateTime.parse('2001-01-01T00:00:00')).should.be.true

  it 'should accept cases where a has unknown values but is still deterministicly before or same as b', ->
    DT.DateTime.parse('2000-01-01T00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59')).should.be.true
    DT.DateTime.parse('2000-01-01T00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:59:59')).should.be.true
    DT.DateTime.parse('2000-01-01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T23:59:59')).should.be.true
    DT.DateTime.parse('2000-01').beforeOrSameAs(DT.DateTime.parse('2000-01-31T23:59:59')).should.be.true
    DT.DateTime.parse('2000').beforeOrSameAs(DT.DateTime.parse('2001-12-31T23:59:59')).should.be.true

  it 'should accept cases where b has unknown values but a is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00:59').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:01')).should.be.true
    DT.DateTime.parse('2000-01-01T00:59:59').beforeOrSameAs(DT.DateTime.parse('2000-01-01T01')).should.be.true
    DT.DateTime.parse('2000-01-01T23:59:59').beforeOrSameAs(DT.DateTime.parse('2000-01-02')).should.be.true
    DT.DateTime.parse('2000-01-31T23:59:59').beforeOrSameAs(DT.DateTime.parse('2000-02')).should.be.true
    DT.DateTime.parse('2000-12-31T23:59:59').beforeOrSameAs(DT.DateTime.parse('2001')).should.be.true

  it 'should accept cases where b has unknown values but a is still deterministicly before or same as b', ->
    DT.DateTime.parse('2000-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000')).should.be.true

  it 'should reject cases where a has unknown values but is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59')).should.be.false
    DT.DateTime.parse('2000-01-01T01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:59:59')).should.be.false
    DT.DateTime.parse('2000-01-02').beforeOrSameAs(DT.DateTime.parse('2000-01-01T23:59:59')).should.be.false
    DT.DateTime.parse('2000-02').beforeOrSameAs(DT.DateTime.parse('2000-01-31T23:59:59')).should.be.false
    DT.DateTime.parse('2001').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.false

  it 'should reject cases where b has unknown values but a is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:01:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00')).should.be.false
    DT.DateTime.parse('2000-01-01T01:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00')).should.be.false
    DT.DateTime.parse('2000-01-02T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01')).should.be.false
    DT.DateTime.parse('2000-02-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01')).should.be.false
    DT.DateTime.parse('2001-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000')).should.be.false

describe 'DateTime.after', ->

  it 'should accept cases where a is after b', ->
    DT.DateTime.parse('2000-01-01T00:00:01').after(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T00:01:00').after(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T01:00:00').after(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01-02T00:00:00').after(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-02-01T00:00:00').after(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2001-01-01T00:00:00').after(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true

  it 'should reject cases where a is before b', ->
    DT.DateTime.parse('2000-12-31T23:59:58').after(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.false
    DT.DateTime.parse('2000-12-31T23:58:59').after(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.false
    DT.DateTime.parse('2000-12-31T22:59:59').after(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.false
    DT.DateTime.parse('2000-12-30T23:59:59').after(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.false
    DT.DateTime.parse('2000-11-31T23:59:59').after(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.false
    DT.DateTime.parse('1999-12-31T23:59:59').after(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.false

  it 'should reject cases where a is b', ->
    DT.DateTime.parse('2000-01-01T00:00:00').after(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false

  it 'should return null in cases where a is b but there are unknown values', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').after(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').after(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01').after(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01').after(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000').after(DT.DateTime.parse('2000'))

  it 'should return null in cases where a has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').after(DT.DateTime.parse('2000-01-01T00:00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').after(DT.DateTime.parse('2000-01-01T00:00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01').after(DT.DateTime.parse('2000-01-01T00:00:00'))
    should.not.exist DT.DateTime.parse('2000-01').after(DT.DateTime.parse('2000-01-01T00:00:00'))
    should.not.exist DT.DateTime.parse('2000').after(DT.DateTime.parse('2000-01-01T00:00:00'))

  it 'should return null in cases where b has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').after(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').after(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').after(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').after(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').after(DT.DateTime.parse('2000'))

  it 'should accept cases where a has unknown values but is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:01').after(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T01').after(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01-02').after(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-02').after(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2001').after(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true

  it 'should accept cases where b has unknown values but a is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:01:00').after(DT.DateTime.parse('2000-01-01T00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T01:00:00').after(DT.DateTime.parse('2000-01-01T00')).should.be.true
    DT.DateTime.parse('2000-01-02T00:00:00').after(DT.DateTime.parse('2000-01-01')).should.be.true
    DT.DateTime.parse('2000-02-01T00:00:00').after(DT.DateTime.parse('2000-01')).should.be.true
    DT.DateTime.parse('2001-01-01T00:00:00').after(DT.DateTime.parse('2000')).should.be.true

  it 'should reject cases where a has unknown values but is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00').after(DT.DateTime.parse('2000-01-01T00:01:00')).should.be.false
    DT.DateTime.parse('2000-01-01T00').after(DT.DateTime.parse('2000-01-01T01:00:00')).should.be.false
    DT.DateTime.parse('2000-01-01').after(DT.DateTime.parse('2000-01-02T00:00:00')).should.be.false
    DT.DateTime.parse('2000-01').after(DT.DateTime.parse('2000-02-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000').after(DT.DateTime.parse('2001-01-01T00:00:00')).should.be.false

  it 'should reject cases where b has unknown values but a is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00:00').after(DT.DateTime.parse('2000-01-01T00:01')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00:00').after(DT.DateTime.parse('2000-01-01T01')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00:00').after(DT.DateTime.parse('2000-01-02')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00:00').after(DT.DateTime.parse('2000-02')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00:00').after(DT.DateTime.parse('2001')).should.be.false

describe 'DateTime.afterOrSameAs', ->

  it 'should accept cases where a is after b', ->
    DT.DateTime.parse('2000-01-01T00:00:01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T00:01:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T01:00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01-02T00:00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-02-01T00:00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2001-01-01T00:00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true

  it 'should reject cases where a is before b', ->
    DT.DateTime.parse('2000-12-31T23:59:58').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.false
    DT.DateTime.parse('2000-12-31T23:58:59').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.false
    DT.DateTime.parse('2000-12-31T22:59:59').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.false
    DT.DateTime.parse('2000-12-30T23:59:59').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.false
    DT.DateTime.parse('2000-11-31T23:59:59').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.false
    DT.DateTime.parse('1999-12-31T23:59:59').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.false

  it 'should accept cases where a is b', ->
    DT.DateTime.parse('2000-01-01T00:00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true

  it 'should return null in cases where a is b but there and b have unknown values', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01').afterOrSameAs(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01').afterOrSameAs(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000').afterOrSameAs(DT.DateTime.parse('2000'))

  it 'should return null in cases where a has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:59:59'))
    should.not.exist DT.DateTime.parse('2000-01-01').afterOrSameAs(DT.DateTime.parse('2000-01-01T23:59:59'))
    should.not.exist DT.DateTime.parse('2000-01').afterOrSameAs(DT.DateTime.parse('2000-01-31T23:59:59'))
    should.not.exist DT.DateTime.parse('2000').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59'))

  it 'should return null in cases where b has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').afterOrSameAs(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').afterOrSameAs(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').afterOrSameAs(DT.DateTime.parse('2000'))

  it 'should accept cases where a has unknown values but is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59')).should.be.true
    DT.DateTime.parse('2000-01-01T01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:59:59')).should.be.true
    DT.DateTime.parse('2000-01-02').afterOrSameAs(DT.DateTime.parse('2000-01-01T23:59:59')).should.be.true
    DT.DateTime.parse('2000-02').afterOrSameAs(DT.DateTime.parse('2000-01-31T23:59:59')).should.be.true
    DT.DateTime.parse('2001').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59')).should.be.true

  it 'should accept cases where a has unknown values but is still deterministicly after or same as b', ->
    DT.DateTime.parse('2000-01-01T00:01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:01:00')).should.be.true
    DT.DateTime.parse('2000-01-01T01').afterOrSameAs(DT.DateTime.parse('2000-01-01T01:00:00')).should.be.true
    DT.DateTime.parse('2000-01-01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true

  it 'should accept cases where b has unknown values but a is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:01:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T01:00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00')).should.be.true
    DT.DateTime.parse('2000-01-02T00:00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01')).should.be.true
    DT.DateTime.parse('2000-02-01T00:00:00').afterOrSameAs(DT.DateTime.parse('2000-01')).should.be.true
    DT.DateTime.parse('2001-01-01T00:00:00').afterOrSameAs(DT.DateTime.parse('2000')).should.be.true

  it 'should accept cases where b has unknown values but a is still deterministicly same as or after b', ->
    DT.DateTime.parse('2000-01-01T00:00:59').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T00:59:59').afterOrSameAs(DT.DateTime.parse('2000-01-01T00')).should.be.true
    DT.DateTime.parse('2000-01-01T23:59:59').afterOrSameAs(DT.DateTime.parse('2000-01-01')).should.be.true
    DT.DateTime.parse('2000-01-31T23:59:59').afterOrSameAs(DT.DateTime.parse('2000-01')).should.be.true
    DT.DateTime.parse('2000-12-31T23:59:59').afterOrSameAs(DT.DateTime.parse('2000')).should.be.true

  it 'should reject cases where a has unknown values but is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:01:00')).should.be.false
    DT.DateTime.parse('2000-01-01T00').afterOrSameAs(DT.DateTime.parse('2000-01-01T01:00:00')).should.be.false
    DT.DateTime.parse('2000-01-01').afterOrSameAs(DT.DateTime.parse('2000-01-02T00:00:00')).should.be.false
    DT.DateTime.parse('2000-01').afterOrSameAs(DT.DateTime.parse('2000-02-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000').afterOrSameAs(DT.DateTime.parse('2001-01-01T00:00:00')).should.be.false

  it 'should reject cases where b has unknown values but a is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00:59').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:01')).should.be.false
    DT.DateTime.parse('2000-01-01T00:59:59').afterOrSameAs(DT.DateTime.parse('2000-01-01T01')).should.be.false
    DT.DateTime.parse('2000-01-01T23:59:59').afterOrSameAs(DT.DateTime.parse('2000-01-02')).should.be.false
    DT.DateTime.parse('2000-01-31T23:59:59').afterOrSameAs(DT.DateTime.parse('2000-02')).should.be.false
    DT.DateTime.parse('2000-12-31T23:59:59').afterOrSameAs(DT.DateTime.parse('2001')).should.be.false

describe 'Interval', ->

  it 'should properly set all properties when constructed', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'), true, false)
    i.low.should.eql DT.DateTime.parse '2012-01-01'
    i.high.should.eql DT.DateTime.parse '2013-01-01'
    i.lowClosed.should.be.true
    i.highClosed.should.be.false

  it 'should default lowClosed/highClosed to true', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'))
    i.low.should.eql DT.DateTime.parse '2012-01-01'
    i.high.should.eql DT.DateTime.parse '2013-01-01'
    i.lowClosed.should.be.true
    i.highClosed.should.be.true

describe 'Interval.includes(Interval)', ->
  @beforeEach ->
    setupIntervalsAndDateTimes @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @sameAs
    x.closed.includes(y.closed).should.be.true
    x.closed.includes(y.open).should.be.true
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.true
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @before
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.false
    y.closed.includes(x.open).should.be.false
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @meets
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.false
    y.closed.includes(x.open).should.be.false
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @overlaps
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.false
    y.closed.includes(x.open).should.be.false
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.false

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @begins
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.true

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @during
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.true
    y.open.includes(x.open).should.be.true

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @ends
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.true

  it 'should properly handle imprecision', ->

    [x, y] = xy @sameAs
    x.closed.includes(y.toMinute).should.be.true
    should.not.exist x.toHour.includes(y.toMinute)

    [x, y] = xy @before
    x.toMonth.includes(y.toMonth).should.be.false
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @meets
    x.toMonth.includes(y.toMonth).should.be.false
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @overlaps
    x.toMonth.includes(y.toMonth).should.be.false
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @begins
    x.toMinute.includes(y.toMinute).should.be.false
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @during
    x.toMonth.includes(y.toMonth).should.be.false
    y.toMonth.includes(x.toMonth).should.be.true
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @ends
    x.toMinute.includes(y.toMinute).should.be.false
    should.not.exist x.toYear.includes(y.closed)

describe 'Interval.includes(DateTime)', ->
  @beforeEach ->
    setupIntervalsAndDateTimes @

  it 'should properly calculate dates before it', ->
    @all2012.closed.includes(@bef2012.full).should.be.false

  it 'should properly calculate the left boundary date', ->
    @all2012.closed.includes(@beg2012.full).should.be.true
    @all2012.open.includes(@beg2012.full).should.be.false

  it 'should properly calculate dates in the middle of it', ->
    @all2012.closed.includes(@mid2012.full).should.be.true

  it 'should properly calculate the right boundary date', ->
    @all2012.closed.includes(@end2012.full).should.be.true
    @all2012.open.includes(@end2012.full).should.be.false

  it 'should properly calculate dates after it', ->
    @all2012.closed.includes(@aft2012.full).should.be.false

  it 'should properly handle imprecision', ->
    @all2012.closed.includes(@bef2012.toMonth).should.be.false
    @all2012.closed.includes(@beg2012.toMonth).should.be.true
    @all2012.closed.includes(@mid2012.toMonth).should.be.true
    @all2012.closed.includes(@end2012.toMonth).should.be.true
    @all2012.closed.includes(@aft2012.toMonth).should.be.false

    @all2012.toMonth.includes(@bef2012.toMonth).should.be.false
    should.not.exist @all2012.toMonth.includes(@beg2012.toMonth)
    @all2012.toMonth.includes(@mid2012.toMonth).should.be.true
    should.not.exist @all2012.toMonth.includes(@end2012.toMonth)
    @all2012.toMonth.includes(@aft2012.toMonth).should.be.false

    @all2012.toMonth.includes(@bef2012.full).should.be.false
    should.not.exist @all2012.toMonth.includes(@beg2012.full)
    @all2012.toMonth.includes(@mid2012.full).should.be.true
    should.not.exist @all2012.toMonth.includes(@end2012.full)
    @all2012.toMonth.includes(@aft2012.full).should.be.false

    @all2012.closed.includes(@mid2012.toYear).should.be.true

describe 'Interval.includedIn(Interval)', ->
  @beforeEach ->
    setupIntervalsAndDateTimes @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @sameAs
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true

    y.closed.includedIn(x.closed).should.be.true
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.true
    y.open.includedIn(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @before
    x.closed.includedIn(y.closed).should.be.false
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.false
    x.open.includedIn(y.open).should.be.false
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @meets
    x.closed.includedIn(y.closed).should.be.false
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.false
    x.open.includedIn(y.open).should.be.false
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @overlaps
    x.closed.includedIn(y.closed).should.be.false
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.false
    x.open.includedIn(y.open).should.be.false
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @begins
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @during
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.true
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @ends
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly handle imprecision', ->
    [x, y] = xy @sameAs
    should.not.exist x.closed.includedIn(y.toMinute)
    should.not.exist x.toHour.includedIn(y.toMinute)

    [x, y] = xy @before
    x.toMonth.includedIn(y.toMonth).should.be.false
    should.not.exist x.toYear.includedIn(y.closed)

    [x, y] = xy @meets
    x.toMonth.includedIn(y.toMonth).should.be.false
    should.not.exist x.toYear.includedIn(y.closed)

    [x, y] = xy @overlaps
    x.toMonth.includedIn(y.toMonth).should.be.false
    should.not.exist x.toYear.includedIn(y.closed)

    [x, y] = xy @begins
    should.not.exist x.toMinute.includedIn(y.toMinute)
    x.toYear.includedIn(y.closed).should.be.true

    [x, y] = xy @during
    x.toMonth.includedIn(y.toMonth).should.be.true
    y.toMonth.includedIn(x.toMonth).should.be.false
    x.toYear.includedIn(y.closed).should.be.true

    [x, y] = xy @ends
    should.not.exist x.toMinute.includedIn(y.toMinute)
    x.toYear.includedIn(y.closed).should.be.true

describe 'Interval.includedIn(DateTime)', ->
  @beforeEach ->
    setupIntervalsAndDateTimes @

  # Admittedly, most of this doesn't really make sense, but let's be sure the code reflects that!

  it 'should properly calculate dates before it', ->
    @all2012.closed.includedIn(@bef2012.full).should.be.false

  it 'should properly calculate the left boundary date', ->
    @all2012.closed.includedIn(@beg2012.full).should.be.false
    @all2012.open.includedIn(@beg2012.full).should.be.false

  it 'should properly calculate dates in the middle of it', ->
    @all2012.closed.includedIn(@mid2012.full).should.be.false

  it 'should properly calculate the right boundary date', ->
    @all2012.closed.includedIn(@end2012.full).should.be.false
    @all2012.open.includedIn(@end2012.full).should.be.false

  it 'should properly calculate dates after it', ->
    @all2012.closed.includedIn(@aft2012.full).should.be.false

  it 'should properly handle imprecision', ->
    @all2012.closed.includedIn(@bef2012.toMonth).should.be.false
    @all2012.closed.includedIn(@beg2012.toMonth).should.be.false
    @all2012.closed.includedIn(@mid2012.toMonth).should.be.false
    @all2012.closed.includedIn(@end2012.toMonth).should.be.false
    @all2012.closed.includedIn(@aft2012.toMonth).should.be.false
    @all2012.closed.includedIn(@bef2012.toYear).should.be.false
    @all2012.closed.includedIn(@beg2012.toYear).should.be.false
    @all2012.closed.includedIn(@mid2012.toYear).should.be.false
    @all2012.closed.includedIn(@end2012.toYear).should.be.false
    @all2012.closed.includedIn(@aft2012.toYear).should.be.false

    @all2012.toMonth.includedIn(@bef2012.toMonth).should.be.false
    @all2012.toMonth.includedIn(@beg2012.toMonth).should.be.false
    @all2012.toMonth.includedIn(@mid2012.toMonth).should.be.false
    @all2012.toMonth.includedIn(@end2012.toMonth).should.be.false
    @all2012.toMonth.includedIn(@aft2012.toMonth).should.be.false
    @all2012.toYear.includedIn(@bef2012.toYear).should.be.false
    should.not.exist @all2012.toYear.includedIn(@beg2012.toYear)
    should.not.exist @all2012.toYear.includedIn(@mid2012.toYear)
    should.not.exist @all2012.toYear.includedIn(@end2012.toYear)
    @all2012.toYear.includedIn(@aft2012.toYear).should.be.false

    @all2012.toMonth.includedIn(@bef2012.full).should.be.false
    @all2012.toMonth.includedIn(@beg2012.full).should.be.false
    @all2012.toMonth.includedIn(@mid2012.full).should.be.false
    @all2012.toMonth.includedIn(@end2012.full).should.be.false
    @all2012.toMonth.includedIn(@aft2012.full).should.be.false
    @all2012.toYear.includedIn(@bef2012.full).should.be.false
    should.not.exist @all2012.toYear.includedIn(@beg2012.full)
    should.not.exist @all2012.toYear.includedIn(@mid2012.full)
    should.not.exist @all2012.toYear.includedIn(@end2012.full)
    @all2012.toYear.includedIn(@aft2012.full).should.be.false

describe 'Interval.overlaps(Interval)', ->
  @beforeEach ->
    setupIntervalsAndDateTimes @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @sameAs
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @before
    x.closed.overlaps(y.closed).should.be.false
    x.closed.overlaps(y.open).should.be.false
    x.open.overlaps(y.closed).should.be.false
    x.open.overlaps(y.open).should.be.false
    y.closed.overlaps(x.closed).should.be.false
    y.closed.overlaps(x.open).should.be.false
    y.open.overlaps(x.closed).should.be.false
    y.open.overlaps(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @meets
    x.closed.overlaps(y.closed).should.be.false
    x.closed.overlaps(y.open).should.be.false
    x.open.overlaps(y.closed).should.be.false
    x.open.overlaps(y.open).should.be.false
    y.closed.overlaps(x.closed).should.be.false
    y.closed.overlaps(x.open).should.be.false
    y.open.overlaps(x.closed).should.be.false
    y.open.overlaps(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @overlaps
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @begins
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @during
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @ends
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly handle imprecision', ->
    [x, y] = xy @sameAs
    x.closed.overlaps(y.toMinute).should.be.true
    x.toHour.overlaps(y.toMinute).should.be.true

    [x, y] = xy @before
    x.toMonth.overlaps(y.toMonth).should.be.false
    should.not.exist x.toYear.overlaps(y.closed)

    [x, y] = xy @meets
    x.toMonth.overlaps(y.toMonth).should.be.false
    should.not.exist x.toYear.overlaps(y.closed)

    [x, y] = xy @overlaps
    x.toMonth.overlaps(y.toMonth).should.be.true
    should.not.exist x.toYear.overlaps(y.closed)

    [x, y] = xy @begins
    x.toMinute.overlaps(y.toMinute).should.be.true
    x.toYear.overlaps(y.closed).should.be.true

    [x, y] = xy @during
    x.toMonth.overlaps(y.toMonth).should.be.true
    y.toMonth.overlaps(x.toMonth).should.be.true
    x.toYear.overlaps(y.closed).should.be.true

    [x, y] = xy @ends
    x.toMinute.overlaps(y.toMinute).should.be.true
    x.toYear.overlaps(y.closed).should.be.true

describe 'Interval.overlaps(DateTime)', ->
  @beforeEach ->
    setupIntervalsAndDateTimes @

  it 'should properly calculate dates before it', ->
    @all2012.closed.overlaps(@bef2012.full).should.be.false

  it 'should properly calculate the left boundary date', ->
    @all2012.closed.overlaps(@beg2012.full).should.be.true
    @all2012.open.overlaps(@beg2012.full).should.be.false

  it 'should properly calculate dates in the middle of it', ->
    @all2012.closed.overlaps(@mid2012.full).should.be.true

  it 'should properly calculate the right boundary date', ->
    @all2012.closed.overlaps(@end2012.full).should.be.true
    @all2012.open.overlaps(@end2012.full).should.be.false

  it 'should properly calculate dates after it', ->
    @all2012.closed.overlaps(@aft2012.full).should.be.false

  it 'should properly handle imprecision', ->
    @all2012.closed.overlaps(@bef2012.toMonth).should.be.false
    @all2012.closed.overlaps(@beg2012.toMonth).should.be.true
    @all2012.closed.overlaps(@mid2012.toMonth).should.be.true
    @all2012.closed.overlaps(@end2012.toMonth).should.be.true
    @all2012.closed.overlaps(@aft2012.toMonth).should.be.false

    @all2012.toMonth.overlaps(@bef2012.toMonth).should.be.false
    should.not.exist @all2012.toMonth.overlaps(@beg2012.toMonth)
    @all2012.toMonth.overlaps(@mid2012.toMonth).should.be.true
    should.not.exist @all2012.toMonth.overlaps(@end2012.toMonth)
    @all2012.toMonth.overlaps(@aft2012.toMonth).should.be.false

    @all2012.toMonth.overlaps(@bef2012.full).should.be.false
    should.not.exist @all2012.toMonth.overlaps(@beg2012.full)
    @all2012.toMonth.overlaps(@mid2012.full).should.be.true
    should.not.exist @all2012.toMonth.overlaps(@end2012.full)
    @all2012.toMonth.overlaps(@aft2012.full).should.be.false

    @all2012.closed.overlaps(@mid2012.toYear).should.be.true
