should = require 'should'
DT = require '../lib/cql-datatypes'

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

  it 'should convert to javascript Date', ->
    DT.DateTime.parse('2012-10-25T12:55:14').toJSDate().should.eql new Date(2012, 9, 25, 12, 55, 14)

  it 'should floor unknown values when it converts to javascript Date', ->
    DT.DateTime.parse('2012').toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)

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

  it 'should detect dates contained by it', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'))
    i.contains(DT.DateTime.parse '2012-06-01').should.be.true

  it 'should detect boundary dates contained by it when it is closed', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'), false, false)
    i.contains(DT.DateTime.parse '2012-01-01').should.be.true
    i.contains(DT.DateTime.parse '2013-01-01').should.be.true

  it 'should detect boundary dates not contained by it when it is open', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'), true, true)
    i.contains(DT.DateTime.parse '2012-01-01').should.be.false
    i.contains(DT.DateTime.parse '2013-01-01').should.be.false

  it 'should detect dates not contained by it', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'))
    i.contains(DT.DateTime.parse '2011-06-01').should.be.false

  it 'should detect intervals contained by it', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'))
    inner = new DT.Interval(DT.DateTime.parse('2012-02-01'), DT.DateTime.parse('2012-12-01'))
    i.contains(inner).should.be.true

  it 'should detect same closed interval contained by it when it is closed', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'), false, false)
    same = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'))
    i.contains(same).should.be.true

  it 'should detect same closed interval not contained by it when it is open', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'), true, true)
    same = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'))
    i.contains(same).should.be.false

  it 'should detect same open interval contained by it when it is open', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'), true, true)
    same = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'), true, true)
    i.contains(same).should.be.true

  it 'should detect outer interval not contained by it', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'))
    outer = new DT.Interval(DT.DateTime.parse('2011-01-01'), DT.DateTime.parse('2014-01-01'))
    i.contains(outer).should.be.false

  it 'should detect overlapping interval not contained by it', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'))
    overlap = new DT.Interval(DT.DateTime.parse('2011-06-01'), DT.DateTime.parse('2012-06-01'))
    i.contains(overlap).should.be.false

  it 'should detect outside intervals not contained by it', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'))
    before = new DT.Interval(DT.DateTime.parse('2010-01-01'), DT.DateTime.parse('2011-01-01'))
    after = new DT.Interval(DT.DateTime.parse('2014-01-01'), DT.DateTime.parse('2015-01-01'))
    i.contains(before).should.be.false
    i.contains(after).should.be.false

