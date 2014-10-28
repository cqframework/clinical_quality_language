should = require 'should'
TH = require './cql-test-helper'
DT = require '../lib/cql-datatypes'

setupIntervals = (test) ->
  test['sameAs'] = {
    #    |----------X----------|
    #    |----------Y----------|
    x: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2012-12-31T23:59:59')),
    y: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2012-12-31T23:59:59'))
  }
  test['before'] = {
    #    |----------X----------|
    #                               |----------Y----------|
    x: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2012-04-01T00:00:00')),
    y: new TH.Interval(DT.DateTime.parse('2012-07-01T00:00:00'), DT.DateTime.parse('2012-12-31T23:59:59'))
  }
  test['meets'] = {
    #    |----------X----------|
    #                          |-----------Y----------|
    x: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2012-07-01T00:00:00')),
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

xy = (obj) -> [obj.x, obj.y]

open = (ivl) ->
  new DT.Interval(ivl.begin, ivl.end, true, true)

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

  it 'should return null in cases where a is b but there are unknown values', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01').beforeOrSameAs(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01').beforeOrSameAs(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000').beforeOrSameAs(DT.DateTime.parse('2000'))

  it 'should return null in cases where a has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000-01-01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000-01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59'))

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

  it 'should accept cases where b has unknown values but a is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:01')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T01')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-02')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-02')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2001')).should.be.true

  it 'should reject cases where a has unknown values but is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000-01-01T01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000-01-02').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000-02').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2001').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false

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
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').after(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').after(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000-01-01').after(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000-01').after(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000').after(DT.DateTime.parse('2000-01-01T00:00:59'))

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

  it 'should return null in cases where a is b but there are unknown values', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01').afterOrSameAs(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01').afterOrSameAs(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000').afterOrSameAs(DT.DateTime.parse('2000'))

  it 'should return null in cases where a has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000-01-01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000-01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59'))
    should.not.exist DT.DateTime.parse('2000').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59'))

  it 'should return null in cases where b has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').afterOrSameAs(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').afterOrSameAs(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:01').afterOrSameAs(DT.DateTime.parse('2000'))

  it 'should accept cases where a has unknown values but is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01-02').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-02').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2001').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true

  it 'should accept cases where b has unknown values but a is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:01:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T01:00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00')).should.be.true
    DT.DateTime.parse('2000-01-02T00:00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01')).should.be.true
    DT.DateTime.parse('2000-02-01T00:00:00').afterOrSameAs(DT.DateTime.parse('2000-01')).should.be.true
    DT.DateTime.parse('2001-01-01T00:00:00').afterOrSameAs(DT.DateTime.parse('2000')).should.be.true

  it 'should reject cases where a has unknown values but is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:01:00')).should.be.false
    DT.DateTime.parse('2000-01-01T00').afterOrSameAs(DT.DateTime.parse('2000-01-01T01:00:00')).should.be.false
    DT.DateTime.parse('2000-01-01').afterOrSameAs(DT.DateTime.parse('2000-01-02T00:00:00')).should.be.false
    DT.DateTime.parse('2000-01').afterOrSameAs(DT.DateTime.parse('2000-02-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000').afterOrSameAs(DT.DateTime.parse('2001-01-01T00:00:00')).should.be.false

  it 'should reject cases where b has unknown values but a is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:01')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T01')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00:00').afterOrSameAs(DT.DateTime.parse('2000-01-02')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00:00').afterOrSameAs(DT.DateTime.parse('2000-02')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00:00').afterOrSameAs(DT.DateTime.parse('2001')).should.be.false

describe 'Interval', ->

  it 'should properly set all properties when constructed', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'), false, true)
    i.begin.should.eql DT.DateTime.parse '2012-01-01'
    i.end.should.eql DT.DateTime.parse '2013-01-01'
    i.beginOpen.should.be.false
    i.endOpen.should.be.true

  it 'should default beginOpen/endOpen to false', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'))
    i.begin.should.eql DT.DateTime.parse '2012-01-01'
    i.end.should.eql DT.DateTime.parse '2013-01-01'
    i.beginOpen.should.be.false
    i.endOpen.should.be.false

describe 'Interval.includes(DateTime)', ->

  it 'should detect dates contained by it', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2013-01-01T00:00:00'))
    i.includes(DT.DateTime.parse '2012-06-01T00:00:00').should.be.true

  it 'should detect imprecise dates contained by it', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01'), DT.DateTime.parse('2013-01'))
    i.includes(DT.DateTime.parse '2012-06').should.be.true

  it.skip 'should detect imprecise dates contained by it (edge case)', ->
    # Should this be supported?
    i = new DT.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2013-01-01T00:00:00'), false, true)
    i.includes(DT.DateTime.parse '2012').should.be.true

  it 'should detect boundary dates contained by it when it is closed', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2012-12-31T23:59:59'), false, false)
    i.includes(DT.DateTime.parse '2012-01-01T00:00:00').should.be.true
    i.includes(DT.DateTime.parse '2012-12-31T23:59:59').should.be.true

  it 'should detect boundary dates not contained by it when it is open', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2012-12-31T23:59:59'), true, true)
    i.includes(DT.DateTime.parse '2012-01-01T00:00:00').should.be.false
    i.includes(DT.DateTime.parse '2012-12-31T23:59:59').should.be.false

  it 'should return null for imprecise boundary dates possibly contained by it', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2012-12-31'), false, false)
    should.not.exist i.includes(DT.DateTime.parse '2012-01-01')
    should.not.exist i.includes(DT.DateTime.parse '2012-12-31')

    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2012-12-31'), true, true)
    should.not.exist i.includes(DT.DateTime.parse '2012-01-01')
    should.not.exist i.includes(DT.DateTime.parse '2012-12-31')

  it 'should detect dates not contained by it', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01T00:00:00'), DT.DateTime.parse('2013-01-01T00:00:00'))
    i.includes(DT.DateTime.parse '2011-06-01T00:00:00').should.be.false

  it 'should detect imprecise dates not contained by it', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01'), DT.DateTime.parse('2013-01'))
    i.includes(DT.DateTime.parse '2011-06').should.be.false

describe 'Interval.includes(Interval)', ->
  @beforeEach ->
    setupIntervals @

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
    should.not.exist x.closed.includes(y.toMinute)
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
    should.not.exist x.toMinute.includes(y.toMinute)
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @during
    x.toMonth.includes(y.toMonth).should.be.false
    y.toMonth.includes(x.toMonth).should.be.true
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @begins
    should.not.exist x.toMinute.includes(y.toMinute)
    should.not.exist x.toYear.includes(y.closed)
