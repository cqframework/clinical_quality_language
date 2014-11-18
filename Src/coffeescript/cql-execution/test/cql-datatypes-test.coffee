should = require 'should'
TH = require './cql-test-helper'
DT = require '../lib/cql-datatypes'

setupIntervalsAndDateTimes = (test) ->
  test['all2012'] = new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00.0'), DT.DateTime.parse('2012-12-31T23:59:59.999'))
  test['bef2012'] = TH.DateTime.parse('2011-06-01T00:00:00.0')
  test['beg2012'] = TH.DateTime.parse('2012-01-01T00:00:00.0')
  test['mid2012'] = TH.DateTime.parse('2012-06-01T00:00:00.0')
  test['end2012'] = TH.DateTime.parse('2012-12-31T23:59:59.999')
  test['aft2012'] = TH.DateTime.parse('2013-06-01T00:00:00.0')
  test['dIvl'] = {
    sameAs: {
      #    |----------X----------|
      #    |----------Y----------|
      x: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00.0'), DT.DateTime.parse('2012-12-31T23:59:59.999')),
      y: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00.0'), DT.DateTime.parse('2012-12-31T23:59:59.999'))
    },
    before: {
      #    |----------X----------|
      #                                   |----------Y----------|
      x: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00.0'), DT.DateTime.parse('2012-04-01T00:00:00.0')),
      y: new TH.Interval(DT.DateTime.parse('2012-07-01T00:00:00.0'), DT.DateTime.parse('2012-12-31T23:59:59.999'))
    },
    meets: {
      #    |----------X----------|
      #                           |-----------Y----------|
      x: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00.0'), DT.DateTime.parse('2012-06-30T23:59:59.999')),
      y: new TH.Interval(DT.DateTime.parse('2012-07-01T00:00:00.0'), DT.DateTime.parse('2012-12-31T23:59:59.999'))
    },
    overlaps: {
      #    |----------X----------|
      #                  |----------Y----------|
      x: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00.0'), DT.DateTime.parse('2012-09-01T00:00:00.0')),
      y: new TH.Interval(DT.DateTime.parse('2012-06-01T00:00:00.0'), DT.DateTime.parse('2012-12-31T23:59:59.999'))
    },
    begins: {
      #    |-----X-----|
      #    |----------Y----------|
      x: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00.0'), DT.DateTime.parse('2012-07-01T00:00:00.0')),
      y: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00.0'), DT.DateTime.parse('2012-12-31T23:59:59.999'))
    },
    during: {
      #         |-----X-----|
      #    |----------Y----------|
      x: new TH.Interval(DT.DateTime.parse('2012-05-01T00:00:00.0'), DT.DateTime.parse('2012-07-01T00:00:00.0')),
      y: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00.0'), DT.DateTime.parse('2012-12-31T23:59:59.999'))
    },
    ends: {
      #              |-----X-----|
      #    |----------Y----------|
      x: new TH.Interval(DT.DateTime.parse('2012-07-01T00:00:00.0'), DT.DateTime.parse('2012-12-31T23:59:59.999')),
      y: new TH.Interval(DT.DateTime.parse('2012-01-01T00:00:00.0'), DT.DateTime.parse('2012-12-31T23:59:59.999'))
    }
  }
  test['zeroToHundred'] = new TH.Interval(0, 100)
  test['iIvl'] = {
    sameAs: {
      #    |----------X----------|
      #    |----------Y----------|
      x: new TH.Interval(0, 100),
      y: new TH.Interval(0, 100)
    },
    before: {
      #    |----------X----------|
      #                                   |----------Y----------|
      x: new TH.Interval(0, 40),
      y: new TH.Interval(60, 100)
    },
    meets: {
      #    |----------X----------|
      #                           |-----------Y----------|
      x: new TH.Interval(0, 50),
      y: new TH.Interval(51, 100)
    },
    overlaps: {
      #    |----------X----------|
      #                  |----------Y----------|
      x: new TH.Interval(0, 60),
      y: new TH.Interval(40, 100)
    },
    begins: {
      #    |-----X-----|
      #    |----------Y----------|
      x: new TH.Interval(0, 60),
      y: new TH.Interval(0, 100)
    },
    during: {
      #         |-----X-----|
      #    |----------Y----------|
      x: new TH.Interval(30, 70),
      y: new TH.Interval(0, 100)
    },
    ends: {
      #              |-----X-----|
      #    |----------Y----------|
      x: new TH.Interval(40, 100),
      y: new TH.Interval(0, 100)
    }
  }

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
    should(DT.ThreeValuedLogic.or(false, false, null, false, false)).be.null
    should(DT.ThreeValuedLogic.or(null, null, null, null, null)).be.null

describe 'ThreeValuedLogic.xor', ->

  it 'should return true when exlusive', ->
    DT.ThreeValuedLogic.xor(false, true).should.be.true
    DT.ThreeValuedLogic.xor(false, true).should.be.true
    DT.ThreeValuedLogic.xor(true, false, false, false).should.be.true
    DT.ThreeValuedLogic.xor(false, true, false, false).should.be.true
    DT.ThreeValuedLogic.xor(true, true, true, false, false).should.be.true
    DT.ThreeValuedLogic.xor(false, false, true, false, false).should.be.true

  it 'should return false when not exlcusive', ->
    DT.ThreeValuedLogic.xor(true, true).should.be.false
    DT.ThreeValuedLogic.xor(false, false).should.be.false
    DT.ThreeValuedLogic.xor(true, false, true).should.be.false
    DT.ThreeValuedLogic.xor(false, true, true).should.be.false
    DT.ThreeValuedLogic.xor(true, true, false).should.be.false
    DT.ThreeValuedLogic.xor(false, false, false).should.be.false
    DT.ThreeValuedLogic.xor(false, false, true, false, true).should.be.false

  it 'should return null when there is at least one null', ->
    should(DT.ThreeValuedLogic.xor(true, null)).be.null
    should(DT.ThreeValuedLogic.xor(false, null)).be.null
    should(DT.ThreeValuedLogic.xor(true, false, null)).be.null
    should(DT.ThreeValuedLogic.xor(false, true, null)).be.null
    should(DT.ThreeValuedLogic.xor(false, false, true, null, false)).be.null

describe 'ThreeValuedLogic.not', ->

  it 'should return true when input is false', ->
    DT.ThreeValuedLogic.not(false).should.be.true

  it 'should return false when input is true', ->
    DT.ThreeValuedLogic.not(true).should.be.false

  it 'should return null when input is null', ->
    should.not.exist DT.ThreeValuedLogic.not(null)

describe 'Uncertainty', ->

  it 'should contruct uncertainties with correct properties', ->
    oneToFive = new DT.Uncertainty(1, 5)
    oneToFive.low.should.equal 1
    oneToFive.high.should.equal 5

    oneToPInf = new DT.Uncertainty(1, null)
    oneToPInf.low.should.equal 1
    should(oneToPInf.high).be.null

    nInfToFive = new DT.Uncertainty(null, 5)
    should(nInfToFive.low).be.null
    nInfToFive.high.should.equal 5

    two = new DT.Uncertainty(2)
    two.low.should.equal 2
    two.high.should.equal 2

    everything = new DT.Uncertainty()
    should(everything.low).be.null
    should(everything.high).be.null

  it 'should swap low and high when constructed in wrong order', ->
    fiveToOne = new DT.Uncertainty(5, 1)
    fiveToOne.low.should.equal 1
    fiveToOne.high.should.equal 5

  it 'should contruct uncertainties with correct properties', ->
    oneToFive = new DT.Uncertainty(1,5)
    oneToFive.low.should.equal 1
    oneToFive.high.should.equal 5

  it 'should detect zero-width intervals as points', ->
    new DT.Uncertainty(2).isPoint().should.be.true
    new DT.Uncertainty(2, 2).isPoint().should.be.true
    new DT.Uncertainty(null, null).isPoint().should.be.false
    new DT.Uncertainty(2, null).isPoint().should.be.false
    new DT.Uncertainty(null, 2).isPoint().should.be.false
    new DT.Uncertainty(1, 2).isPoint().should.be.false
    new DT.Uncertainty().isPoint().should.be.false

  it 'should properly calculate equality', ->

    # Equality
    new DT.Uncertainty(1, 1).equals(new DT.Uncertainty(1, 1)).should.be.true

    # <
    new DT.Uncertainty(null, 1).equals(new DT.Uncertainty(2, 2)).should.be.false
    new DT.Uncertainty(null, 1).equals(new DT.Uncertainty(2, 3)).should.be.false
    new DT.Uncertainty(null, 1).equals(new DT.Uncertainty(2, null)).should.be.false
    new DT.Uncertainty(0, 1).equals(new DT.Uncertainty(2, 2)).should.be.false
    new DT.Uncertainty(0, 1).equals(new DT.Uncertainty(2, 3)).should.be.false
    new DT.Uncertainty(0, 1).equals(new DT.Uncertainty(2, null)).should.be.false
    new DT.Uncertainty(1, 1).equals(new DT.Uncertainty(2, 2)).should.be.false
    new DT.Uncertainty(1, 1).equals(new DT.Uncertainty(2, 3)).should.be.false
    new DT.Uncertainty(1, 1).equals(new DT.Uncertainty(2, null)).should.be.false

    # <=
    should.not.exist new DT.Uncertainty(null, 1).equals(new DT.Uncertainty(1, 1))
    should.not.exist new DT.Uncertainty(null, 1).equals(new DT.Uncertainty(1, 2))
    should.not.exist new DT.Uncertainty(null, 1).equals(new DT.Uncertainty(1, null))
    should.not.exist new DT.Uncertainty(0, 1).equals(new DT.Uncertainty(1, 1))
    should.not.exist new DT.Uncertainty(0, 1).equals(new DT.Uncertainty(1, 2))
    should.not.exist new DT.Uncertainty(0, 1).equals(new DT.Uncertainty(1, null))
    should.not.exist new DT.Uncertainty(1, 1).equals(new DT.Uncertainty(1, 2))
    should.not.exist new DT.Uncertainty(1, 1).equals(new DT.Uncertainty(1, null))

    # overlaps
    should.not.exist new DT.Uncertainty(null, null).equals(new DT.Uncertainty(null, null))
    should.not.exist new DT.Uncertainty(null, 10).equals(new DT.Uncertainty(5, 5))
    should.not.exist new DT.Uncertainty(null, 10).equals(new DT.Uncertainty(5, 10))
    should.not.exist new DT.Uncertainty(null, 10).equals(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(null, 10).equals(new DT.Uncertainty(5, null))
    should.not.exist new DT.Uncertainty(null, 10).equals(new DT.Uncertainty(0, 5))
    should.not.exist new DT.Uncertainty(null, 10).equals(new DT.Uncertainty(null, 5))
    should.not.exist new DT.Uncertainty(0, 10).equals(new DT.Uncertainty(5, 5))
    should.not.exist new DT.Uncertainty(0, 10).equals(new DT.Uncertainty(5, 10))
    should.not.exist new DT.Uncertainty(0, 10).equals(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(0, 10).equals(new DT.Uncertainty(5, null))
    should.not.exist new DT.Uncertainty(0, 10).equals(new DT.Uncertainty(0, 5))
    should.not.exist new DT.Uncertainty(0, 10).equals(new DT.Uncertainty(null, 5))
    should.not.exist new DT.Uncertainty(10, 10).equals(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(10, 10).equals(new DT.Uncertainty(5, null))
    should.not.exist new DT.Uncertainty(10, null).equals(new DT.Uncertainty(5, 10))
    should.not.exist new DT.Uncertainty(10, null).equals(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(10, null).equals(new DT.Uncertainty(5, null))

    # >=
    should.not.exist new DT.Uncertainty(1, null).equals(new DT.Uncertainty(null, 1))
    should.not.exist new DT.Uncertainty(1, null).equals(new DT.Uncertainty(0, 1))
    should.not.exist new DT.Uncertainty(1, null).equals(new DT.Uncertainty(1, 1))
    should.not.exist new DT.Uncertainty(1, 2).equals(new DT.Uncertainty(null, 1))
    should.not.exist new DT.Uncertainty(1, 2).equals(new DT.Uncertainty(0, 1))
    should.not.exist new DT.Uncertainty(1, 2).equals(new DT.Uncertainty(1, 1))
    should.not.exist new DT.Uncertainty(1, 1).equals(new DT.Uncertainty(null, 1))
    should.not.exist new DT.Uncertainty(1, 1).equals(new DT.Uncertainty(0, 1))

    # >
    new DT.Uncertainty(2, 2).equals(new DT.Uncertainty(null, 1)).should.be.false
    new DT.Uncertainty(2, 3).equals(new DT.Uncertainty(null, 1)).should.be.false
    new DT.Uncertainty(2, null).equals(new DT.Uncertainty(null, 1)).should.be.false
    new DT.Uncertainty(2, 2).equals(new DT.Uncertainty(0, 1)).should.be.false
    new DT.Uncertainty(2, 3).equals(new DT.Uncertainty(0, 1)).should.be.false
    new DT.Uncertainty(2, null).equals(new DT.Uncertainty(0, 1)).should.be.false
    new DT.Uncertainty(2, 2).equals(new DT.Uncertainty(1, 1)).should.be.false
    new DT.Uncertainty(2, 3).equals(new DT.Uncertainty(1, 1)).should.be.false
    new DT.Uncertainty(2, null).equals(new DT.Uncertainty(1, 1)).should.be.false

  it 'should properly calculate "less than" inequality', ->

    # Equality
    new DT.Uncertainty(1, 1).lessThan(new DT.Uncertainty(1, 1)).should.be.false

    # <
    new DT.Uncertainty(null, 1).lessThan(new DT.Uncertainty(2, 2)).should.be.true
    new DT.Uncertainty(null, 1).lessThan(new DT.Uncertainty(2, 3)).should.be.true
    new DT.Uncertainty(null, 1).lessThan(new DT.Uncertainty(2, null)).should.be.true
    new DT.Uncertainty(0, 1).lessThan(new DT.Uncertainty(2, 2)).should.be.true
    new DT.Uncertainty(0, 1).lessThan(new DT.Uncertainty(2, 3)).should.be.true
    new DT.Uncertainty(0, 1).lessThan(new DT.Uncertainty(2, null)).should.be.true
    new DT.Uncertainty(1, 1).lessThan(new DT.Uncertainty(2, 2)).should.be.true
    new DT.Uncertainty(1, 1).lessThan(new DT.Uncertainty(2, 3)).should.be.true
    new DT.Uncertainty(1, 1).lessThan(new DT.Uncertainty(2, null)).should.be.true

    # <=
    should.not.exist new DT.Uncertainty(null, 1).lessThan(new DT.Uncertainty(1, 1))
    should.not.exist new DT.Uncertainty(null, 1).lessThan(new DT.Uncertainty(1, 2))
    should.not.exist new DT.Uncertainty(null, 1).lessThan(new DT.Uncertainty(1, null))
    should.not.exist new DT.Uncertainty(0, 1).lessThan(new DT.Uncertainty(1, 1))
    should.not.exist new DT.Uncertainty(0, 1).lessThan(new DT.Uncertainty(1, 2))
    should.not.exist new DT.Uncertainty(0, 1).lessThan(new DT.Uncertainty(1, null))
    should.not.exist new DT.Uncertainty(1, 1).lessThan(new DT.Uncertainty(1, 2))
    should.not.exist new DT.Uncertainty(1, 1).lessThan(new DT.Uncertainty(1, null))

    # overlaps
    should.not.exist new DT.Uncertainty(null, null).lessThan(new DT.Uncertainty(null, null))
    should.not.exist new DT.Uncertainty(null, 10).lessThan(new DT.Uncertainty(5, 5))
    should.not.exist new DT.Uncertainty(null, 10).lessThan(new DT.Uncertainty(5, 10))
    should.not.exist new DT.Uncertainty(null, 10).lessThan(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(null, 10).lessThan(new DT.Uncertainty(5, null))
    should.not.exist new DT.Uncertainty(null, 10).lessThan(new DT.Uncertainty(0, 5))
    should.not.exist new DT.Uncertainty(null, 10).lessThan(new DT.Uncertainty(null, 5))
    should.not.exist new DT.Uncertainty(0, 10).lessThan(new DT.Uncertainty(5, 5))
    should.not.exist new DT.Uncertainty(0, 10).lessThan(new DT.Uncertainty(5, 10))
    should.not.exist new DT.Uncertainty(0, 10).lessThan(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(0, 10).lessThan(new DT.Uncertainty(5, null))
    should.not.exist new DT.Uncertainty(0, 10).lessThan(new DT.Uncertainty(0, 5))
    should.not.exist new DT.Uncertainty(0, 10).lessThan(new DT.Uncertainty(null, 5))
    should.not.exist new DT.Uncertainty(10, 10).lessThan(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(10, 10).lessThan(new DT.Uncertainty(5, null))
    should.not.exist new DT.Uncertainty(10, null).lessThan(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(10, null).lessThan(new DT.Uncertainty(5, null))

    # >=
    new DT.Uncertainty(1, null).lessThan(new DT.Uncertainty(null, 1)).should.be.false
    new DT.Uncertainty(1, null).lessThan(new DT.Uncertainty(0, 1)).should.be.false
    new DT.Uncertainty(1, null).lessThan(new DT.Uncertainty(1, 1)).should.be.false
    new DT.Uncertainty(1, 2).lessThan(new DT.Uncertainty(null, 1)).should.be.false
    new DT.Uncertainty(1, 2).lessThan(new DT.Uncertainty(0, 1)).should.be.false
    new DT.Uncertainty(1, 2).lessThan(new DT.Uncertainty(1, 1)).should.be.false
    new DT.Uncertainty(1, 1).lessThan(new DT.Uncertainty(null, 1)).should.be.false
    new DT.Uncertainty(1, 1).lessThan(new DT.Uncertainty(0, 1)).should.be.false

    # >
    new DT.Uncertainty(2, 2).lessThan(new DT.Uncertainty(null, 1)).should.be.false
    new DT.Uncertainty(2, 3).lessThan(new DT.Uncertainty(null, 1)).should.be.false
    new DT.Uncertainty(2, null).lessThan(new DT.Uncertainty(null, 1)).should.be.false
    new DT.Uncertainty(2, 2).lessThan(new DT.Uncertainty(0, 1)).should.be.false
    new DT.Uncertainty(2, 3).lessThan(new DT.Uncertainty(0, 1)).should.be.false
    new DT.Uncertainty(2, null).lessThan(new DT.Uncertainty(0, 1)).should.be.false
    new DT.Uncertainty(2, 2).lessThan(new DT.Uncertainty(1, 1)).should.be.false
    new DT.Uncertainty(2, 3).lessThan(new DT.Uncertainty(1, 1)).should.be.false
    new DT.Uncertainty(2, null).lessThan(new DT.Uncertainty(1, 1)).should.be.false

  it 'should properly calculate "less than or equals" inequality', ->

    # Equality
    new DT.Uncertainty(1, 1).lessThanOrEquals(new DT.Uncertainty(1, 1)).should.be.true

    # <
    new DT.Uncertainty(null, 1).lessThanOrEquals(new DT.Uncertainty(2, 2)).should.be.true
    new DT.Uncertainty(null, 1).lessThanOrEquals(new DT.Uncertainty(2, 3)).should.be.true
    new DT.Uncertainty(null, 1).lessThanOrEquals(new DT.Uncertainty(2, null)).should.be.true
    new DT.Uncertainty(0, 1).lessThanOrEquals(new DT.Uncertainty(2, 2)).should.be.true
    new DT.Uncertainty(0, 1).lessThanOrEquals(new DT.Uncertainty(2, 3)).should.be.true
    new DT.Uncertainty(0, 1).lessThanOrEquals(new DT.Uncertainty(2, null)).should.be.true
    new DT.Uncertainty(1, 1).lessThanOrEquals(new DT.Uncertainty(2, 2)).should.be.true
    new DT.Uncertainty(1, 1).lessThanOrEquals(new DT.Uncertainty(2, 3)).should.be.true
    new DT.Uncertainty(1, 1).lessThanOrEquals(new DT.Uncertainty(2, null)).should.be.true

    # <=
    new DT.Uncertainty(null, 1).lessThanOrEquals(new DT.Uncertainty(1, 1)).should.be.true
    new DT.Uncertainty(null, 1).lessThanOrEquals(new DT.Uncertainty(1, 2)).should.be.true
    new DT.Uncertainty(null, 1).lessThanOrEquals(new DT.Uncertainty(1, null)).should.be.true
    new DT.Uncertainty(0, 1).lessThanOrEquals(new DT.Uncertainty(1, 1)).should.be.true
    new DT.Uncertainty(0, 1).lessThanOrEquals(new DT.Uncertainty(1, 2)).should.be.true
    new DT.Uncertainty(0, 1).lessThanOrEquals(new DT.Uncertainty(1, null)).should.be.true
    new DT.Uncertainty(1, 1).lessThanOrEquals(new DT.Uncertainty(1, 2)).should.be.true
    new DT.Uncertainty(1, 1).lessThanOrEquals(new DT.Uncertainty(1, null)).should.be.true

    # overlaps
    should.not.exist new DT.Uncertainty(null, null).lessThanOrEquals(new DT.Uncertainty(null, null))
    should.not.exist new DT.Uncertainty(null, 10).lessThanOrEquals(new DT.Uncertainty(5, 5))
    should.not.exist new DT.Uncertainty(null, 10).lessThanOrEquals(new DT.Uncertainty(5, 10))
    should.not.exist new DT.Uncertainty(null, 10).lessThanOrEquals(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(null, 10).lessThanOrEquals(new DT.Uncertainty(5, null))
    should.not.exist new DT.Uncertainty(null, 10).lessThanOrEquals(new DT.Uncertainty(0, 5))
    should.not.exist new DT.Uncertainty(null, 10).lessThanOrEquals(new DT.Uncertainty(null, 5))
    should.not.exist new DT.Uncertainty(0, 10).lessThanOrEquals(new DT.Uncertainty(5, 5))
    should.not.exist new DT.Uncertainty(0, 10).lessThanOrEquals(new DT.Uncertainty(5, 10))
    should.not.exist new DT.Uncertainty(0, 10).lessThanOrEquals(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(0, 10).lessThanOrEquals(new DT.Uncertainty(5, null))
    should.not.exist new DT.Uncertainty(0, 10).lessThanOrEquals(new DT.Uncertainty(0, 5))
    should.not.exist new DT.Uncertainty(0, 10).lessThanOrEquals(new DT.Uncertainty(null, 5))
    should.not.exist new DT.Uncertainty(10, 10).lessThanOrEquals(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(10, 10).lessThanOrEquals(new DT.Uncertainty(5, null))
    should.not.exist new DT.Uncertainty(10, null).lessThanOrEquals(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(10, null).lessThanOrEquals(new DT.Uncertainty(5, null))

    # >=
    should.not.exist new DT.Uncertainty(1, null).lessThanOrEquals(new DT.Uncertainty(null, 1))
    should.not.exist new DT.Uncertainty(1, null).lessThanOrEquals(new DT.Uncertainty(0, 1))
    should.not.exist new DT.Uncertainty(1, null).lessThanOrEquals(new DT.Uncertainty(1, 1))
    should.not.exist new DT.Uncertainty(1, 2).lessThanOrEquals(new DT.Uncertainty(null, 1))
    should.not.exist new DT.Uncertainty(1, 2).lessThanOrEquals(new DT.Uncertainty(0, 1))
    should.not.exist new DT.Uncertainty(1, 2).lessThanOrEquals(new DT.Uncertainty(1, 1))
    should.not.exist new DT.Uncertainty(1, 1).lessThanOrEquals(new DT.Uncertainty(null, 1))
    should.not.exist new DT.Uncertainty(1, 1).lessThanOrEquals(new DT.Uncertainty(0, 1))

    # >
    new DT.Uncertainty(2, 2).lessThanOrEquals(new DT.Uncertainty(null, 1)).should.be.false
    new DT.Uncertainty(2, 3).lessThanOrEquals(new DT.Uncertainty(null, 1)).should.be.false
    new DT.Uncertainty(2, null).lessThanOrEquals(new DT.Uncertainty(null, 1)).should.be.false
    new DT.Uncertainty(2, 2).lessThanOrEquals(new DT.Uncertainty(0, 1)).should.be.false
    new DT.Uncertainty(2, 3).lessThanOrEquals(new DT.Uncertainty(0, 1)).should.be.false
    new DT.Uncertainty(2, null).lessThanOrEquals(new DT.Uncertainty(0, 1)).should.be.false
    new DT.Uncertainty(2, 2).lessThanOrEquals(new DT.Uncertainty(1, 1)).should.be.false
    new DT.Uncertainty(2, 3).lessThanOrEquals(new DT.Uncertainty(1, 1)).should.be.false
    new DT.Uncertainty(2, null).lessThanOrEquals(new DT.Uncertainty(1, 1)).should.be.false

  it 'should properly calculate "greater than" inequality', ->

    # Equality
    new DT.Uncertainty(1, 1).greaterThan(new DT.Uncertainty(1, 1)).should.be.false

    # <
    new DT.Uncertainty(null, 1).greaterThan(new DT.Uncertainty(2, 2)).should.be.false
    new DT.Uncertainty(null, 1).greaterThan(new DT.Uncertainty(2, 3)).should.be.false
    new DT.Uncertainty(null, 1).greaterThan(new DT.Uncertainty(2, null)).should.be.false
    new DT.Uncertainty(0, 1).greaterThan(new DT.Uncertainty(2, 2)).should.be.false
    new DT.Uncertainty(0, 1).greaterThan(new DT.Uncertainty(2, 3)).should.be.false
    new DT.Uncertainty(0, 1).greaterThan(new DT.Uncertainty(2, null)).should.be.false
    new DT.Uncertainty(1, 1).greaterThan(new DT.Uncertainty(2, 2)).should.be.false
    new DT.Uncertainty(1, 1).greaterThan(new DT.Uncertainty(2, 3)).should.be.false
    new DT.Uncertainty(1, 1).greaterThan(new DT.Uncertainty(2, null)).should.be.false

    # <=
    new DT.Uncertainty(null, 1).greaterThan(new DT.Uncertainty(1, 1)).should.be.false
    new DT.Uncertainty(null, 1).greaterThan(new DT.Uncertainty(1, 2)).should.be.false
    new DT.Uncertainty(null, 1).greaterThan(new DT.Uncertainty(1, null)).should.be.false
    new DT.Uncertainty(0, 1).greaterThan(new DT.Uncertainty(1, 1)).should.be.false
    new DT.Uncertainty(0, 1).greaterThan(new DT.Uncertainty(1, 2)).should.be.false
    new DT.Uncertainty(0, 1).greaterThan(new DT.Uncertainty(1, null)).should.be.false
    new DT.Uncertainty(1, 1).greaterThan(new DT.Uncertainty(1, 2)).should.be.false
    new DT.Uncertainty(1, 1).greaterThan(new DT.Uncertainty(1, null)).should.be.false

    # overlaps
    should.not.exist new DT.Uncertainty(null, null).greaterThan(new DT.Uncertainty(null, null))
    should.not.exist new DT.Uncertainty(null, 10).greaterThan(new DT.Uncertainty(5, 5))
    should.not.exist new DT.Uncertainty(null, 10).greaterThan(new DT.Uncertainty(5, 10))
    should.not.exist new DT.Uncertainty(null, 10).greaterThan(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(null, 10).greaterThan(new DT.Uncertainty(5, null))
    should.not.exist new DT.Uncertainty(null, 10).greaterThan(new DT.Uncertainty(0, 5))
    should.not.exist new DT.Uncertainty(null, 10).greaterThan(new DT.Uncertainty(null, 5))
    should.not.exist new DT.Uncertainty(0, 10).greaterThan(new DT.Uncertainty(5, 5))
    should.not.exist new DT.Uncertainty(0, 10).greaterThan(new DT.Uncertainty(5, 10))
    should.not.exist new DT.Uncertainty(0, 10).greaterThan(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(0, 10).greaterThan(new DT.Uncertainty(5, null))
    should.not.exist new DT.Uncertainty(0, 10).greaterThan(new DT.Uncertainty(0, 5))
    should.not.exist new DT.Uncertainty(0, 10).greaterThan(new DT.Uncertainty(null, 5))
    should.not.exist new DT.Uncertainty(10, 10).greaterThan(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(10, 10).greaterThan(new DT.Uncertainty(5, null))
    should.not.exist new DT.Uncertainty(10, null).greaterThan(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(10, null).greaterThan(new DT.Uncertainty(5, null))

    # >=
    should.not.exist new DT.Uncertainty(1, null).greaterThan(new DT.Uncertainty(null, 1))
    should.not.exist new DT.Uncertainty(1, null).greaterThan(new DT.Uncertainty(0, 1))
    should.not.exist new DT.Uncertainty(1, null).greaterThan(new DT.Uncertainty(1, 1))
    should.not.exist new DT.Uncertainty(1, 2).greaterThan(new DT.Uncertainty(null, 1))
    should.not.exist new DT.Uncertainty(1, 2).greaterThan(new DT.Uncertainty(0, 1))
    should.not.exist new DT.Uncertainty(1, 2).greaterThan(new DT.Uncertainty(1, 1))
    should.not.exist new DT.Uncertainty(1, 1).greaterThan(new DT.Uncertainty(null, 1))
    should.not.exist new DT.Uncertainty(1, 1).greaterThan(new DT.Uncertainty(0, 1))

    # >
    new DT.Uncertainty(2, 2).greaterThan(new DT.Uncertainty(null, 1)).should.be.true
    new DT.Uncertainty(2, 3).greaterThan(new DT.Uncertainty(null, 1)).should.be.true
    new DT.Uncertainty(2, null).greaterThan(new DT.Uncertainty(null, 1)).should.be.true
    new DT.Uncertainty(2, 2).greaterThan(new DT.Uncertainty(0, 1)).should.be.true
    new DT.Uncertainty(2, 3).greaterThan(new DT.Uncertainty(0, 1)).should.be.true
    new DT.Uncertainty(2, null).greaterThan(new DT.Uncertainty(0, 1)).should.be.true
    new DT.Uncertainty(2, 2).greaterThan(new DT.Uncertainty(1, 1)).should.be.true
    new DT.Uncertainty(2, 3).greaterThan(new DT.Uncertainty(1, 1)).should.be.true
    new DT.Uncertainty(2, null).greaterThan(new DT.Uncertainty(1, 1)).should.be.true

  it 'should properly calculate "greater than or equals" inequality', ->

    # Equality
    new DT.Uncertainty(1, 1).greaterThanOrEquals(new DT.Uncertainty(1, 1)).should.be.true

    # <
    new DT.Uncertainty(null, 1).greaterThanOrEquals(new DT.Uncertainty(2, 2)).should.be.false
    new DT.Uncertainty(null, 1).greaterThanOrEquals(new DT.Uncertainty(2, 3)).should.be.false
    new DT.Uncertainty(null, 1).greaterThanOrEquals(new DT.Uncertainty(2, null)).should.be.false
    new DT.Uncertainty(0, 1).greaterThanOrEquals(new DT.Uncertainty(2, 2)).should.be.false
    new DT.Uncertainty(0, 1).greaterThanOrEquals(new DT.Uncertainty(2, 3)).should.be.false
    new DT.Uncertainty(0, 1).greaterThanOrEquals(new DT.Uncertainty(2, null)).should.be.false
    new DT.Uncertainty(1, 1).greaterThanOrEquals(new DT.Uncertainty(2, 2)).should.be.false
    new DT.Uncertainty(1, 1).greaterThanOrEquals(new DT.Uncertainty(2, 3)).should.be.false
    new DT.Uncertainty(1, 1).greaterThanOrEquals(new DT.Uncertainty(2, null)).should.be.false

    # <=
    should.not.exist new DT.Uncertainty(null, 1).greaterThanOrEquals(new DT.Uncertainty(1, 1))
    should.not.exist new DT.Uncertainty(null, 1).greaterThanOrEquals(new DT.Uncertainty(1, 2))
    should.not.exist new DT.Uncertainty(null, 1).greaterThanOrEquals(new DT.Uncertainty(1, null))
    should.not.exist new DT.Uncertainty(0, 1).greaterThanOrEquals(new DT.Uncertainty(1, 1))
    should.not.exist new DT.Uncertainty(0, 1).greaterThanOrEquals(new DT.Uncertainty(1, 2))
    should.not.exist new DT.Uncertainty(0, 1).greaterThanOrEquals(new DT.Uncertainty(1, null))
    should.not.exist new DT.Uncertainty(1, 1).greaterThanOrEquals(new DT.Uncertainty(1, 2))
    should.not.exist new DT.Uncertainty(1, 1).greaterThanOrEquals(new DT.Uncertainty(1, null))

    # overlaps
    should.not.exist new DT.Uncertainty(null, null).greaterThanOrEquals(new DT.Uncertainty(null, null))
    should.not.exist new DT.Uncertainty(null, 10).greaterThanOrEquals(new DT.Uncertainty(5, 5))
    should.not.exist new DT.Uncertainty(null, 10).greaterThanOrEquals(new DT.Uncertainty(5, 10))
    should.not.exist new DT.Uncertainty(null, 10).greaterThanOrEquals(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(null, 10).greaterThanOrEquals(new DT.Uncertainty(5, null))
    should.not.exist new DT.Uncertainty(null, 10).greaterThanOrEquals(new DT.Uncertainty(0, 5))
    should.not.exist new DT.Uncertainty(null, 10).greaterThanOrEquals(new DT.Uncertainty(null, 5))
    should.not.exist new DT.Uncertainty(0, 10).greaterThanOrEquals(new DT.Uncertainty(5, 5))
    should.not.exist new DT.Uncertainty(0, 10).greaterThanOrEquals(new DT.Uncertainty(5, 10))
    should.not.exist new DT.Uncertainty(0, 10).greaterThanOrEquals(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(0, 10).greaterThanOrEquals(new DT.Uncertainty(5, null))
    should.not.exist new DT.Uncertainty(0, 10).greaterThanOrEquals(new DT.Uncertainty(0, 5))
    should.not.exist new DT.Uncertainty(0, 10).greaterThanOrEquals(new DT.Uncertainty(null, 5))
    should.not.exist new DT.Uncertainty(10, 10).greaterThanOrEquals(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(10, 10).greaterThanOrEquals(new DT.Uncertainty(5, null))
    should.not.exist new DT.Uncertainty(10, null).greaterThanOrEquals(new DT.Uncertainty(5, 15))
    should.not.exist new DT.Uncertainty(10, null).greaterThanOrEquals(new DT.Uncertainty(5, null))

    # >=
    new DT.Uncertainty(1, null).greaterThanOrEquals(new DT.Uncertainty(null, 1)).should.be.true
    new DT.Uncertainty(1, null).greaterThanOrEquals(new DT.Uncertainty(0, 1)).should.be.true
    new DT.Uncertainty(1, null).greaterThanOrEquals(new DT.Uncertainty(1, 1)).should.be.true
    new DT.Uncertainty(1, 2).greaterThanOrEquals(new DT.Uncertainty(null, 1)).should.be.true
    new DT.Uncertainty(1, 2).greaterThanOrEquals(new DT.Uncertainty(0, 1)).should.be.true
    new DT.Uncertainty(1, 2).greaterThanOrEquals(new DT.Uncertainty(1, 1)).should.be.true
    new DT.Uncertainty(1, 1).greaterThanOrEquals(new DT.Uncertainty(null, 1)).should.be.true
    new DT.Uncertainty(1, 1).greaterThanOrEquals(new DT.Uncertainty(0, 1)).should.be.true

    # >
    new DT.Uncertainty(2, 2).greaterThanOrEquals(new DT.Uncertainty(null, 1)).should.be.true
    new DT.Uncertainty(2, 3).greaterThanOrEquals(new DT.Uncertainty(null, 1)).should.be.true
    new DT.Uncertainty(2, null).greaterThanOrEquals(new DT.Uncertainty(null, 1)).should.be.true
    new DT.Uncertainty(2, 2).greaterThanOrEquals(new DT.Uncertainty(0, 1)).should.be.true
    new DT.Uncertainty(2, 3).greaterThanOrEquals(new DT.Uncertainty(0, 1)).should.be.true
    new DT.Uncertainty(2, null).greaterThanOrEquals(new DT.Uncertainty(0, 1)).should.be.true
    new DT.Uncertainty(2, 2).greaterThanOrEquals(new DT.Uncertainty(1, 1)).should.be.true
    new DT.Uncertainty(2, 3).greaterThanOrEquals(new DT.Uncertainty(1, 1)).should.be.true
    new DT.Uncertainty(2, null).greaterThanOrEquals(new DT.Uncertainty(1, 1)).should.be.true

describe 'DateTime', ->

  it 'should properly set all properties when constructed', ->
    d = new DT.DateTime(2000, 12, 1, 3, 25, 59, 246, 5.5)
    d.year.should.equal 2000
    d.month.should.equal 12
    d.day.should.equal 1
    d.hour.should.equal 3
    d.minute.should.equal 25
    d.second.should.equal 59
    d.millisecond.should.equal 246
    d.timeZoneOffset.should.equal 5.5

  it 'should leave unset properties as undefined', ->
    d = new DT.DateTime(2000)
    d.year.should.equal 2000
    should.not.exist d.month
    should.not.exist d.day
    should.not.exist d.hour
    should.not.exist d.minute
    should.not.exist d.second
    should.not.exist d.millisecond
    should.not.exist d.timeZoneOffset

  it 'should parse yyyy', ->
    d = DT.DateTime.parse '2012'
    d.should.eql new DT.DateTime(2012)

  it 'should parse yyyy-mm', ->
    d = DT.DateTime.parse '2012-10'
    d.should.eql new DT.DateTime(2012, 10)

  it 'should parse yyyy-mm-dd', ->
    d = DT.DateTime.parse '2012-10-25'
    d.should.eql new DT.DateTime(2012, 10, 25)

  it 'should parse yyyy-mm-ddThh with and without timezone offset', ->
    d = DT.DateTime.parse '2012-10-25T12'
    d.should.eql new DT.DateTime(2012, 10, 25, 12)
    d = DT.DateTime.parse '2012-10-25T12-05'
    d.should.eql new DT.DateTime(2012, 10, 25, 12, null, null, null, -5)

  it 'should parse yyyy-mm-ddThh:mm with and without timezone offset', ->
    d = DT.DateTime.parse '2012-10-25T12:55'
    d.should.eql new DT.DateTime(2012, 10, 25, 12, 55)
    d = DT.DateTime.parse '2012-10-25T12:55+05:30'
    d.should.eql new DT.DateTime(2012, 10, 25, 12, 55, null, null, 5.5)

  it 'should parse yyyy-mm-ddThh:mm:ss with and without timezone offset', ->
    d = DT.DateTime.parse '2012-10-25T12:55:14'
    d.should.eql new DT.DateTime(2012, 10, 25, 12, 55, 14)
    d = DT.DateTime.parse '2012-10-25T12:55:14+01'
    d.should.eql new DT.DateTime(2012, 10, 25, 12, 55, 14, null, 1)

  it 'should parse yyyy-mm-ddThh:mm:ss.s with and without timezone offset', ->
    d = DT.DateTime.parse '2012-10-25T12:55:14.9'
    d.should.eql new DT.DateTime(2012, 10, 25, 12, 55, 14, 900)

    d = DT.DateTime.parse '2012-10-25T12:55:14.95'
    d.should.eql new DT.DateTime(2012, 10, 25, 12, 55, 14, 950)

    d = DT.DateTime.parse '2012-10-25T12:55:14.953'
    d.should.eql new DT.DateTime(2012, 10, 25, 12, 55, 14, 953)

    d = DT.DateTime.parse '2012-10-25T12:55:14.9641368'
    d.should.eql new DT.DateTime(2012, 10, 25, 12, 55, 14, 964)

    d = DT.DateTime.parse '2012-10-25T12:55:14.953-01'
    d.should.eql new DT.DateTime(2012, 10, 25, 12, 55, 14, 953, -1)

  it 'should not parse invalid strings', ->
    should.not.exist DT.DateTime.parse '20121025'

  it 'should construct from a javascript date', ->
    DT.DateTime.fromDate(new Date(1999, 1, 16, 13, 56, 24, 123)).should.eql DT.DateTime.parse('1999-02-16T13:56:24.123')

  it 'should construct from a javascript date into a target timezone', ->
    DT.DateTime.fromDate(new Date(Date.UTC(1999, 1, 16, 13, 56, 24, 123)), -5).should.eql DT.DateTime.parse('1999-02-16T08:56:24.123-05:00')
    DT.DateTime.fromDate(new Date(Date.UTC(1999, 1, 16, 13, 56, 24, 123)), +4.5).should.eql DT.DateTime.parse('1999-02-16T18:26:24.123+04:30')

  it 'should copy a fully define DateTime', ->
    original = DT.DateTime.parse('1999-02-16T13:56:24.123+04:30')
    copy = original.copy()
    copy.should.eql original
    copy.should.not.equal original

  it 'should copy an imprecise DateTime', ->
    original = DT.DateTime.parse('1999-02')
    copy = original.copy()
    copy.should.eql original
    copy.should.not.equal original

  it 'should convert to other timezone offsets', ->
    original = DT.DateTime.parse('1999-02-16T13:56:24.123+04:30')
    converted = original.convertToTimeZoneOffset(-5)
    converted.should.not.eql original
    converted.should.eql DT.DateTime.parse('1999-02-16T04:26:24.123-05:00')

  it 'should know if it is precise', ->
    DT.DateTime.parse('2000-01-01T00:00:00.0-05:00').isPrecise().should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00.0').isPrecise().should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00').isPrecise().should.be.false
    DT.DateTime.parse('2000-01-01T00:00').isPrecise().should.be.false
    DT.DateTime.parse('2000-01-01T00').isPrecise().should.be.false
    DT.DateTime.parse('2000-01-01').isPrecise().should.be.false
    DT.DateTime.parse('2000-01').isPrecise().should.be.false
    DT.DateTime.parse('2000').isPrecise().should.be.false

  it 'should know if it is imprecise', ->
    DT.DateTime.parse('2000-01-01T00:00:00.0-05:00').isImprecise().should.be.false
    DT.DateTime.parse('2000-01-01T00:00:00.0').isImprecise().should.be.false
    DT.DateTime.parse('2000-01-01T00:00:00').isImprecise().should.be.true
    DT.DateTime.parse('2000-01-01T00:00').isImprecise().should.be.true
    DT.DateTime.parse('2000-01-01T00').isImprecise().should.be.true
    DT.DateTime.parse('2000-01-01').isImprecise().should.be.true
    DT.DateTime.parse('2000-01').isImprecise().should.be.true
    DT.DateTime.parse('2000').isImprecise().should.be.true

  it 'should correctly convert to uncertainties with JavaScript dates', ->
    preciseUncertainty = DT.DateTime.parse('2000-02-25T12:15:43.123').toUncertainty()
    preciseUncertainty.isPoint().should.be.true
    preciseUncertainty.low.should.eql new Date(2000, 1, 25, 12, 15, 43, 123)
    preciseUncertainty.high.should.eql new Date(2000, 1, 25, 12, 15, 43, 123)

    toSecond = DT.DateTime.parse('2000-02-25T12:15:43').toUncertainty()
    toSecond.isPoint().should.be.false
    toSecond.low.should.eql new Date(2000, 1, 25, 12, 15, 43, 0)
    toSecond.high.should.eql new Date(2000, 1, 25, 12, 15, 43, 999)

    toMinute = DT.DateTime.parse('2000-02-25T12:15').toUncertainty()
    toMinute.isPoint().should.be.false
    toMinute.low.should.eql new Date(2000, 1, 25, 12, 15, 0, 0)
    toMinute.high.should.eql new Date(2000, 1, 25, 12, 15, 59, 999)

    toHour = DT.DateTime.parse('2000-02-25T12').toUncertainty()
    toHour.isPoint().should.be.false
    toHour.low.should.eql new Date(2000, 1, 25, 12, 0, 0, 0)
    toHour.high.should.eql new Date(2000, 1, 25, 12, 59, 59, 999)

    toDay = DT.DateTime.parse('2000-02-25').toUncertainty()
    toDay.isPoint().should.be.false
    toDay.low.should.eql new Date(2000, 1, 25, 0, 0, 0, 0)
    toDay.high.should.eql new Date(2000, 1, 25, 23, 59, 59, 999)

    toMonthLeapYear = DT.DateTime.parse('2000-02').toUncertainty()
    toMonthLeapYear.isPoint().should.be.false
    toMonthLeapYear.low.should.eql new Date(2000, 1, 1, 0, 0, 0, 0)
    toMonthLeapYear.high.should.eql new Date(2000, 1, 29, 23, 59, 59, 999)

    toMonthNonLeapYear = DT.DateTime.parse('1999-02').toUncertainty()
    toMonthNonLeapYear.isPoint().should.be.false
    toMonthNonLeapYear.low.should.eql new Date(1999, 1, 1, 0, 0, 0, 0)
    toMonthNonLeapYear.high.should.eql new Date(1999, 1, 28, 23, 59, 59, 999)

    toYear = DT.DateTime.parse('2000').toUncertainty()
    toYear.isPoint().should.be.false
    toYear.low.should.eql new Date(2000, 0, 1, 0, 0, 0, 0)
    toYear.high.should.eql new Date(2000, 11, 31, 23, 59, 59, 999)

  it 'should convert to javascript Date', ->
    DT.DateTime.parse('2012-10-25T12:55:14.456').toJSDate().should.eql new Date(2012, 9, 25, 12, 55, 14, 456)

  it 'should convert to javascript Date w/ time zone offsets', ->
    DT.DateTime.parse('2012-10-25T12:55:14.456+04:30').toJSDate().should.eql new Date('2012-10-25T12:55:14.456+04:30')
    DT.DateTime.parse('2012-10-25T12:55:14.456+00:00').toJSDate().should.eql new Date('2012-10-25T12:55:14.456Z')
    DT.DateTime.parse('2012-10-25T12:55:14.0-05').toJSDate().should.eql new Date('25 Oct 2012 12:55:14 EST')

  it 'should floor unknown values when it converts to javascript Date', ->
    DT.DateTime.parse('2012').toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0, 0)

describe 'DateTime.add', ->

  it 'should add units for simple cases', ->
    simple = DT.DateTime.parse('2000-06-15T10:20:30.555')
    simple.add(1, DT.DateTime.Unit.YEAR).should.eql DT.DateTime.parse('2001-06-15T10:20:30.555')
    simple.add(1, DT.DateTime.Unit.MONTH).should.eql DT.DateTime.parse('2000-07-15T10:20:30.555')
    simple.add(1, DT.DateTime.Unit.DAY).should.eql DT.DateTime.parse('2000-06-16T10:20:30.555')
    simple.add(1, DT.DateTime.Unit.HOUR).should.eql DT.DateTime.parse('2000-06-15T11:20:30.555')
    simple.add(1, DT.DateTime.Unit.MINUTE).should.eql DT.DateTime.parse('2000-06-15T10:21:30.555')
    simple.add(1, DT.DateTime.Unit.SECOND).should.eql DT.DateTime.parse('2000-06-15T10:20:31.555')
    simple.add(1, DT.DateTime.Unit.MILLISECOND).should.eql DT.DateTime.parse('2000-06-15T10:20:30.556')

  it 'should subtract units for simple cases', ->
    simple = DT.DateTime.parse('2000-06-15T10:20:30.555')
    simple.add(-1, DT.DateTime.Unit.YEAR).should.eql DT.DateTime.parse('1999-06-15T10:20:30.555')
    simple.add(-1, DT.DateTime.Unit.MONTH).should.eql DT.DateTime.parse('2000-05-15T10:20:30.555')
    simple.add(-1, DT.DateTime.Unit.DAY).should.eql DT.DateTime.parse('2000-06-14T10:20:30.555')
    simple.add(-1, DT.DateTime.Unit.HOUR).should.eql DT.DateTime.parse('2000-06-15T09:20:30.555')
    simple.add(-1, DT.DateTime.Unit.MINUTE).should.eql DT.DateTime.parse('2000-06-15T10:19:30.555')
    simple.add(-1, DT.DateTime.Unit.SECOND).should.eql DT.DateTime.parse('2000-06-15T10:20:29.555')
    simple.add(-1, DT.DateTime.Unit.MILLISECOND).should.eql DT.DateTime.parse('2000-06-15T10:20:30.554')

  it 'should rollover when you add past a boundary', ->
    almostMidnight = DT.DateTime.parse('2000-12-31T23:59:59.999')
    almostMidnight.add(1, DT.DateTime.Unit.MONTH).should.eql DT.DateTime.parse('2001-01-31T23:59:59.999')
    almostMidnight.add(1, DT.DateTime.Unit.DAY).should.eql DT.DateTime.parse('2001-01-01T23:59:59.999')
    almostMidnight.add(1, DT.DateTime.Unit.HOUR).should.eql DT.DateTime.parse('2001-01-01T00:59:59.999')
    almostMidnight.add(1, DT.DateTime.Unit.MINUTE).should.eql DT.DateTime.parse('2001-01-01T00:00:59.999')
    almostMidnight.add(1, DT.DateTime.Unit.SECOND).should.eql DT.DateTime.parse('2001-01-01T00:00:00.999')
    almostMidnight.add(1, DT.DateTime.Unit.MILLISECOND).should.eql DT.DateTime.parse('2001-01-01T00:00:00.0')

  it 'should rollover when you add past a boundary w/ timezone offsets', ->
    almostMidnight = DT.DateTime.parse('2000-12-31T23:59:59.999+00:00')
    almostMidnight.add(1, DT.DateTime.Unit.MONTH).should.eql DT.DateTime.parse('2001-01-31T23:59:59.999+00:00')
    almostMidnight.add(1, DT.DateTime.Unit.DAY).should.eql DT.DateTime.parse('2001-01-01T23:59:59.999+00:00')
    almostMidnight.add(1, DT.DateTime.Unit.HOUR).should.eql DT.DateTime.parse('2001-01-01T00:59:59.999+00:00')
    almostMidnight.add(1, DT.DateTime.Unit.MINUTE).should.eql DT.DateTime.parse('2001-01-01T00:00:59.999+00:00')
    almostMidnight.add(1, DT.DateTime.Unit.SECOND).should.eql DT.DateTime.parse('2001-01-01T00:00:00.999+00:00')
    almostMidnight.add(1, DT.DateTime.Unit.MILLISECOND).should.eql DT.DateTime.parse('2001-01-01T00:00:00.0+00:00')

  it 'should rollover when you subtract past a boundary', ->
    midnight = DT.DateTime.parse('2001-01-01T00:00:00.0')
    midnight.add(-1, DT.DateTime.Unit.MONTH).should.eql DT.DateTime.parse('2000-12-01T00:00:00.0')
    midnight.add(-1, DT.DateTime.Unit.DAY).should.eql DT.DateTime.parse('2000-12-31T00:00:00.0')
    midnight.add(-1, DT.DateTime.Unit.HOUR).should.eql DT.DateTime.parse('2000-12-31T23:00:00.0')
    midnight.add(-1, DT.DateTime.Unit.MINUTE).should.eql DT.DateTime.parse('2000-12-31T23:59:00.0')
    midnight.add(-1, DT.DateTime.Unit.SECOND).should.eql DT.DateTime.parse('2000-12-31T23:59:59.0')
    midnight.add(-1, DT.DateTime.Unit.MILLISECOND).should.eql DT.DateTime.parse('2000-12-31T23:59:59.999')

  it 'should rollover when you subtract past a boundary w/ timezone offsets', ->
    midnight = DT.DateTime.parse('2001-01-01T00:00:00.0+00:00')
    midnight.add(-1, DT.DateTime.Unit.MONTH).should.eql DT.DateTime.parse('2000-12-01T00:00:00.0+00:00')
    midnight.add(-1, DT.DateTime.Unit.DAY).should.eql DT.DateTime.parse('2000-12-31T00:00:00.0+00:00')
    midnight.add(-1, DT.DateTime.Unit.HOUR).should.eql DT.DateTime.parse('2000-12-31T23:00:00.0+00:00')
    midnight.add(-1, DT.DateTime.Unit.MINUTE).should.eql DT.DateTime.parse('2000-12-31T23:59:00.0+00:00')
    midnight.add(-1, DT.DateTime.Unit.SECOND).should.eql DT.DateTime.parse('2000-12-31T23:59:59.0+00:00')
    midnight.add(-1, DT.DateTime.Unit.MILLISECOND).should.eql DT.DateTime.parse('2000-12-31T23:59:59.999+00:00')

  it 'should still work for imprecise numbers, when adding to a defined field', ->
    DT.DateTime.parse('2000-06-15T10:20:40').add(30, DT.DateTime.Unit.SECOND).should.eql DT.DateTime.parse('2000-06-15T10:21:10')
    DT.DateTime.parse('2000-06-15T10:20').add(50, DT.DateTime.Unit.MINUTE).should.eql DT.DateTime.parse('2000-06-15T11:10')
    DT.DateTime.parse('2000-06-15T10').add(14, DT.DateTime.Unit.HOUR).should.eql DT.DateTime.parse('2000-06-16T00')
    DT.DateTime.parse('2000-06-15').add(30, DT.DateTime.Unit.DAY).should.eql DT.DateTime.parse('2000-07-15')
    DT.DateTime.parse('2000-06').add(8, DT.DateTime.Unit.MONTH).should.eql DT.DateTime.parse('2001-02')
    DT.DateTime.parse('2000').add(5, DT.DateTime.Unit.YEAR).should.eql DT.DateTime.parse('2005')

  it 'should not add anything on undefined fields', ->
    DT.DateTime.parse('2000-06-15T10:20:15').add(100, DT.DateTime.Unit.MILLISECOND).should.eql DT.DateTime.parse('2000-06-15T10:20:15')
    DT.DateTime.parse('2000-06-15T10:20').add(100, DT.DateTime.Unit.SECOND).should.eql DT.DateTime.parse('2000-06-15T10:20')
    DT.DateTime.parse('2000-06-15T10').add(100, DT.DateTime.Unit.MINUTE).should.eql DT.DateTime.parse('2000-06-15T10')
    DT.DateTime.parse('2000-06-15').add(100, DT.DateTime.Unit.HOUR).should.eql DT.DateTime.parse('2000-06-15')
    DT.DateTime.parse('2000-06').add(100, DT.DateTime.Unit.DAY).should.eql DT.DateTime.parse('2000-06')
    DT.DateTime.parse('2000').add(100, DT.DateTime.Unit.MONTH).should.eql DT.DateTime.parse('2000')
    DT.DateTime.parse('2000').add(100, DT.DateTime.Unit.MINUTE).should.eql DT.DateTime.parse('2000')

  it 'should not mutate the original object', ->
    date1 = DT.DateTime.parse('2000-06-15T10:20:30.0')
    date2 = date1.add(6, DT.DateTime.Unit.MONTH)
    date1.should.eql DT.DateTime.parse('2000-06-15T10:20:30.0')
    date2.should.eql DT.DateTime.parse('2000-12-15T10:20:30.0')

  it 'should return a different object (copy)', ->
    date1 = DT.DateTime.parse('2000-06-15T10:20:30.0')
    date2 = date1.add(0, DT.DateTime.Unit.SECOND)
    date1.should.eql date2
    date1.should.not.equal date2

describe 'DateTime.timeBetween', ->
  it 'should calculate time between two full specified dates', ->
    a = DT.DateTime.parse '2009-06-15T12:37:45.0'
    b = DT.DateTime.parse '2009-06-15T12:37:45.0'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(0)

    a = DT.DateTime.parse '2009-06-15T12:37:45.123'
    b = DT.DateTime.parse '2009-06-15T12:37:45.456'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(333)

    a = DT.DateTime.parse '2009-06-15T12:37:45.100'
    b = DT.DateTime.parse '2009-06-15T12:37:52.499'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(7)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(7399)

    a = DT.DateTime.parse '2009-06-15T12:37:45.750'
    b = DT.DateTime.parse '2009-06-15T12:56:17.875'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(19)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(1112)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(1112125)

    a = DT.DateTime.parse '2009-06-15T12:37:45.0'
    b = DT.DateTime.parse '2009-06-15T14:56:50.500'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(2)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(139)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(8345)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(8345500)

    a = DT.DateTime.parse '2009-06-15T12:37:45.0'
    b = DT.DateTime.parse '2009-06-20T17:56:50.500'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(5)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(125)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(7519)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(451145)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(451145500)

    a = DT.DateTime.parse '2009-06-15T12:37:45.0'
    b = DT.DateTime.parse '2009-07-04T12:56:50.500'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(1)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(19)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(456)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(27379)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(1642745)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(1642745500)

    a = DT.DateTime.parse '2000-06-15T12:37:45.0'
    b = DT.DateTime.parse '2009-07-04T12:56:50.500'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(9)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(109)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(3306)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(79344)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(4760659)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(285639545)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(285639545500)

    a = DT.DateTime.parse '2001-01-01T00:00:00.0'
    b = DT.DateTime.parse '2001-12-31T23:59:59.999'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(11)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(364)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(8759)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(525599)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(31535999)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(31535999999)

  it 'should handle leap year', ->
    a = DT.DateTime.parse '1999-02-01T00:00:00.00'
    b = DT.DateTime.parse '2000-02-01T00:00:00.00'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(1)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(12)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(365)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(8760)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(525600)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(31536000)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(31536000000)

    a = DT.DateTime.parse '2000-02-01T00:00:00.0'
    b = DT.DateTime.parse '2001-02-01T00:00:00.0'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(1)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(12)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(366)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(8784)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(527040)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(31622400)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(31622400000)

  it 'should handle different timezones', ->

    a = DT.DateTime.parse '2001-01-01T00:00:00.0+00:00'
    b = DT.DateTime.parse '2000-12-31T19:00:00.0-05:00'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(0)

    # TODO: When a and b are different timezones, which do we use to count boundaries?
    # 1) a's timezone
    # 2) b's timezone
    # 3) default timezone (right now, the environment's timezone)
    # 4) UTC

  it 'should handle imprecision', ->
    a = DT.DateTime.parse '2009-06-15T12:37:45.250'
    b = DT.DateTime.parse '2009-06-15T12:37:45'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(-250,749)

    a = DT.DateTime.parse '2009-06-15T12:37:45.250'
    b = DT.DateTime.parse '2009-06-15T12:37'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(-45,14)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(-45250,14749)

    a = DT.DateTime.parse '2009-06-15T12:37:45.250'
    b = DT.DateTime.parse '2009-06-15T14'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(2)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(83, 142)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(4935, 8534)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(4934750,8534749)

    a = DT.DateTime.parse '2000-06-15T12:37:45.250'
    b = DT.DateTime.parse '2009'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(9)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(103, 114)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(3122, 3486)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(74917, 83676)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(4494983, 5020582)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(269698935, 301234934)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(269698934750,301234934749)

    a = DT.DateTime.parse '2009-06-15T12:37:45'
    b = DT.DateTime.parse '2009-06-15T12:37:45'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(-999,999)

  it 'should return negative values for going backwards', ->
    a = DT.DateTime.parse '2009-07-04T12:56:50.150'
    b = DT.DateTime.parse '2000-06-15T12:37:45.350'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(-9)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(-109)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(-3306)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(-79344)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(-4760659)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(-285639545)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(-285639544800)

    a = DT.DateTime.parse '2009-06-15T12:37:45'
    b = DT.DateTime.parse '2009-06-15T12:37:44.123'
    a.timeBetween(b, DT.DateTime.Unit.YEAR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MONTH).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.DAY).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.HOUR).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.MINUTE).should.eql new DT.Uncertainty(0)
    a.timeBetween(b, DT.DateTime.Unit.SECOND).should.eql new DT.Uncertainty(-1)
    a.timeBetween(b, DT.DateTime.Unit.MILLISECOND).should.eql new DT.Uncertainty(-1876, -877)


describe 'DateTime.sameAs', ->
  it 'should always accept cases where a is same as b', ->
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123')).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MILLISECOND).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.SECOND).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.YEAR).should.be.true

  it 'should properly calculate cases where the millisecond is different', ->
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.124')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.124'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.124'), DT.DateTime.Unit.SECOND).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.124'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.124'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.124'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.124'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.124'), DT.DateTime.Unit.YEAR).should.be.true

  it 'should properly calculate cases where the second is different', ->
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:46.123')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:46.123'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:46.123'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:46.123'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:46.123'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:46.123'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:46.123'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:46.123'), DT.DateTime.Unit.YEAR).should.be.true

  it 'should properly calculate cases where the minute is different', ->
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:36:45.123')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:36:45.123'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:36:45.123'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:36:45.123'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:36:45.123'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:36:45.123'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:36:45.123'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:36:45.123'), DT.DateTime.Unit.YEAR).should.be.true

  it 'should properly calculate cases where the hour is different', ->
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T13:35:45.123')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T13:35:45.123'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T13:35:45.123'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T13:35:45.123'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T13:35:45.123'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T13:35:45.123'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T13:35:45.123'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T13:35:45.123'), DT.DateTime.Unit.YEAR).should.be.true

  it 'should properly calculate cases where the day is different', ->
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-16T12:35:45.123')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-16T12:35:45.123'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-16T12:35:45.123'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-16T12:35:45.123'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-16T12:35:45.123'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-16T12:35:45.123'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-16T12:35:45.123'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-16T12:35:45.123'), DT.DateTime.Unit.YEAR).should.be.true

  it 'should properly calculate cases where the month is different', ->
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-06-15T12:35:45.123')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-06-15T12:35:45.123'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-06-15T12:35:45.123'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-06-15T12:35:45.123'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-06-15T12:35:45.123'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-06-15T12:35:45.123'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-06-15T12:35:45.123'), DT.DateTime.Unit.MONTH).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-06-15T12:35:45.123'), DT.DateTime.Unit.YEAR).should.be.true

  it 'should properly calculate cases where the year is different', ->
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2001-05-15T12:35:45.123')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2001-05-15T12:35:45.123'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2001-05-15T12:35:45.123'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2001-05-15T12:35:45.123'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2001-05-15T12:35:45.123'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2001-05-15T12:35:45.123'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2001-05-15T12:35:45.123'), DT.DateTime.Unit.MONTH).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2001-05-15T12:35:45.123'), DT.DateTime.Unit.YEAR).should.be.false

it 'should handle different time zones', ->
    DT.DateTime.parse('2000-12-31T19:35:45.123+00:00').sameAs(DT.DateTime.parse('2001-01-01T00:05:45.123+04:30')).should.be.true
    DT.DateTime.parse('2000-12-31T19:35:45.123+00:00').sameAs(DT.DateTime.parse('2001-01-01T00:05:45.123+04:30'), DT.DateTime.Unit.MILLISECOND).should.be.true
    DT.DateTime.parse('2000-12-31T19:35:45.123+00:00').sameAs(DT.DateTime.parse('2001-01-01T00:05:45.123+04:30'), DT.DateTime.Unit.SECOND).should.be.true
    DT.DateTime.parse('2000-12-31T19:35:45.123+00:00').sameAs(DT.DateTime.parse('2001-01-01T00:05:45.123+04:30'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-12-31T19:35:45.123+00:00').sameAs(DT.DateTime.parse('2001-01-01T00:05:45.123+04:30'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-12-31T19:35:45.123+00:00').sameAs(DT.DateTime.parse('2001-01-01T00:05:45.123+04:30'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-12-31T19:35:45.123+00:00').sameAs(DT.DateTime.parse('2001-01-01T00:05:45.123+04:30'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-12-31T19:35:45.123+00:00').sameAs(DT.DateTime.parse('2001-01-01T00:05:45.123+04:30'), DT.DateTime.Unit.YEAR).should.be.true

it 'should handle imprecision correctly with missing milliseconds', ->
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MILLISECOND)
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.SECOND).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MILLISECOND)
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.SECOND).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MILLISECOND)
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.SECOND).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:45'), DT.DateTime.Unit.YEAR).should.be.true

    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:46')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:46').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:46').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:46').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:46').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:46').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:46').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:46').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:46').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:46')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45').sameAs(DT.DateTime.parse('2000-05-15T12:35:46'), DT.DateTime.Unit.YEAR).should.be.true

  it 'should handle imprecision correctly with missing seconds', ->
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.MILLISECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.SECOND)
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MILLISECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.SECOND)
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.MILLISECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.SECOND)
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.MINUTE).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:35'), DT.DateTime.Unit.YEAR).should.be.true

    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:36')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-05-15T12:36').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123')).should.be.false
    DT.DateTime.parse('2000-05-15T12:36').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:36').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:36').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:36').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:36').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:36').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:36').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:36')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35').sameAs(DT.DateTime.parse('2000-05-15T12:36'), DT.DateTime.Unit.YEAR).should.be.true

it 'should handle imprecision correctly with missing minutes', ->
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.MILLISECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.MINUTE)
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'))
    should.not.exist DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MILLISECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MINUTE)
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12'))
    should.not.exist DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.MILLISECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.MINUTE)
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.HOUR).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T12'), DT.DateTime.Unit.YEAR).should.be.true

    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T13')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-05-15T13').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123')).should.be.false
    DT.DateTime.parse('2000-05-15T13').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T13').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T13').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T13').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T13').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T13').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T13').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T13')).should.be.false
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12').sameAs(DT.DateTime.parse('2000-05-15T13'), DT.DateTime.Unit.YEAR).should.be.true

it 'should handle imprecision correctly with missing hours', ->
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.MILLISECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.HOUR)
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'))
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MILLISECOND)
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.HOUR)
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15'))
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.MILLISECOND)
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.HOUR)
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.DAY).should.be.true
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-15'), DT.DateTime.Unit.YEAR).should.be.true

    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-16')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-05-16').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123')).should.be.false
    DT.DateTime.parse('2000-05-16').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-16').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-16').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-16').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-16').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05-16').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-16').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-16')).should.be.false
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15').sameAs(DT.DateTime.parse('2000-05-16'), DT.DateTime.Unit.YEAR).should.be.true

it 'should handle imprecision correctly with missing days', ->
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.MILLISECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.HOUR)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.DAY)
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'))
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MILLISECOND)
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.HOUR)
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.DAY)
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05'))
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.MILLISECOND)
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.HOUR)
    should.not.exist DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.DAY)
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.MONTH).should.be.true
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-05'), DT.DateTime.Unit.YEAR).should.be.true

    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-06')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.MONTH).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-06').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123')).should.be.false
    DT.DateTime.parse('2000-06').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-06').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-06').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-06').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-06').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-06').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MONTH).should.be.false
    DT.DateTime.parse('2000-06').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.YEAR).should.be.true
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-06')).should.be.false
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.MONTH).should.be.false
    DT.DateTime.parse('2000-05').sameAs(DT.DateTime.parse('2000-06'), DT.DateTime.Unit.YEAR).should.be.true

it 'should handle imprecision correctly with missing months', ->
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000'))
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.MILLISECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.HOUR)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.DAY)
    should.not.exist DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.MONTH)
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'))
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MILLISECOND)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.HOUR)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.DAY)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MONTH)
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.YEAR).should.be.true
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000'))
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.MILLISECOND)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.SECOND)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.MINUTE)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.HOUR)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.DAY)
    should.not.exist DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.MONTH)
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2000'), DT.DateTime.Unit.YEAR).should.be.true

    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2001')).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.MONTH).should.be.false
    DT.DateTime.parse('2000-05-15T12:35:45.123').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.YEAR).should.be.false
    DT.DateTime.parse('2001').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123')).should.be.false
    DT.DateTime.parse('2001').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2001').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2001').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2001').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2001').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2001').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.MONTH).should.be.false
    DT.DateTime.parse('2001').sameAs(DT.DateTime.parse('2000-05-15T12:35:45.123'), DT.DateTime.Unit.YEAR).should.be.false
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2001')).should.be.false
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.MILLISECOND).should.be.false
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.SECOND).should.be.false
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.MINUTE).should.be.false
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.HOUR).should.be.false
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.DAY).should.be.false
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.MONTH).should.be.false
    DT.DateTime.parse('2000').sameAs(DT.DateTime.parse('2001'), DT.DateTime.Unit.YEAR).should.be.false

describe 'DateTime.before', ->

  it 'should accept cases where a is before b', ->
    DT.DateTime.parse('2000-12-31T23:59:59.998').before(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.true
    DT.DateTime.parse('2000-12-31T23:59:58.999').before(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.true
    DT.DateTime.parse('2000-12-31T23:58:59.999').before(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.true
    DT.DateTime.parse('2000-12-31T22:59:59.999').before(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.true
    DT.DateTime.parse('2000-12-30T23:59:59.999').before(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.true
    DT.DateTime.parse('2000-11-31T23:59:59.999').before(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.true
    DT.DateTime.parse('1999-12-31T23:59:59.999').before(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.true

  it 'should reject cases where a is after b', ->
    DT.DateTime.parse('2000-01-01T00:00:00.001').before(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00:01.0').before(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000-01-01T00:01:00.0').before(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000-01-01T01:00:00.0').before(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000-01-02T00:00:00.0').before(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000-02-01T00:00:00.0').before(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2001-01-01T00:00:00.0').before(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false

  it 'should reject cases where a is b', ->
    DT.DateTime.parse('2000-01-01T00:00:00.0').before(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false

  it 'should work with different timezone offsets', ->
    DT.DateTime.parse('2000-01-01T12:00:00.0+01:00').before(DT.DateTime.parse('2000-01-01T07:00:00.0-05:00')).should.be.true
    DT.DateTime.parse('2000-01-01T12:00:00.0+01:00').before(DT.DateTime.parse('2000-01-01T06:00:00.0-05:00')).should.be.false
    DT.DateTime.parse('2000-01-01T07:00:00.0-05:00').before(DT.DateTime.parse('2000-01-01T12:00:00.0+01:00')).should.be.false

  it 'should return null in cases where a is b but there are unknown values', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00').before(DT.DateTime.parse('2000-01-01T00:00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').before(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').before(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01').before(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01').before(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000').before(DT.DateTime.parse('2000'))

  it 'should return null in cases where a has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00').before(DT.DateTime.parse('2000-01-01T00:00:00.999'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').before(DT.DateTime.parse('2000-01-01T00:00:00.999'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').before(DT.DateTime.parse('2000-01-01T00:00:00.999'))
    should.not.exist DT.DateTime.parse('2000-01-01').before(DT.DateTime.parse('2000-01-01T00:00:00.999'))
    should.not.exist DT.DateTime.parse('2000-01').before(DT.DateTime.parse('2000-01-01T00:00:00.999'))
    should.not.exist DT.DateTime.parse('2000').before(DT.DateTime.parse('2000-01-01T00:00:00.999'))

  it 'should return null in cases where b has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').before(DT.DateTime.parse('2000-01-01T00:00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').before(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').before(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').before(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').before(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').before(DT.DateTime.parse('2000'))

  it 'should accept cases where a has unknown values but is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00:00').before(DT.DateTime.parse('2000-01-01T00:00:01.0')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00').before(DT.DateTime.parse('2000-01-01T00:01:00.0')).should.be.true
    DT.DateTime.parse('2000-01-01T00').before(DT.DateTime.parse('2000-01-01T01:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01-01').before(DT.DateTime.parse('2000-01-02T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01').before(DT.DateTime.parse('2000-02-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000').before(DT.DateTime.parse('2001-01-01T00:00:00.0')).should.be.true

  it 'should accept cases where b has unknown values but a is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00:00.0').before(DT.DateTime.parse('2000-01-01T00:00:01')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00.0').before(DT.DateTime.parse('2000-01-01T00:01')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00.0').before(DT.DateTime.parse('2000-01-01T01')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00.0').before(DT.DateTime.parse('2000-01-02')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00.0').before(DT.DateTime.parse('2000-02')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00.0').before(DT.DateTime.parse('2001')).should.be.true

  it 'should reject cases where a has unknown values but is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:00:01').before(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000-01-01T00:01').before(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000-01-01T01').before(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000-01-02').before(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000-02').before(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2001').before(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false

  it 'should reject cases where b has unknown values but a is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:01:00.0').before(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000-01-01T00:01:00.0').before(DT.DateTime.parse('2000-01-01T00:00')).should.be.false
    DT.DateTime.parse('2000-01-01T01:00:00.0').before(DT.DateTime.parse('2000-01-01T00')).should.be.false
    DT.DateTime.parse('2000-01-02T00:00:00.0').before(DT.DateTime.parse('2000-01-01')).should.be.false
    DT.DateTime.parse('2000-02-01T00:00:00.0').before(DT.DateTime.parse('2000-01')).should.be.false
    DT.DateTime.parse('2001-01-01T00:00:00.0').before(DT.DateTime.parse('2000')).should.be.false

describe 'DateTime.beforeOrSameAs', ->

  it 'should accept cases where a is before b', ->
    DT.DateTime.parse('2000-12-31T23:59:59.998').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.true
    DT.DateTime.parse('2000-12-31T23:59:58.999').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.true
    DT.DateTime.parse('2000-12-31T23:58:59.999').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.true
    DT.DateTime.parse('2000-12-31T22:59:59.999').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.true
    DT.DateTime.parse('2000-12-30T23:59:59.999').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.true
    DT.DateTime.parse('2000-11-31T23:59:59.999').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.true
    DT.DateTime.parse('1999-12-31T23:59:59.999').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.true

  it 'should reject cases where a is after b', ->
    DT.DateTime.parse('2000-01-01T00:00:00.001').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00:01.0').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000-01-01T00:01:00.0').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000-01-01T01:00:00.0').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000-01-02T00:00:00.0').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000-02-01T00:00:00.0').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2001-01-01T00:00:00.0').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false

  it 'should accept cases where a is b', ->
    DT.DateTime.parse('2000-01-01T00:00:00.0').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true

  it 'should work with different timezone offsets', ->
    DT.DateTime.parse('2000-01-01T12:00:00.0+01:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T07:00:00.0-05:00')).should.be.true
    DT.DateTime.parse('2000-01-01T12:00:00.0+01:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T06:00:00.0-05:00')).should.be.true
    DT.DateTime.parse('2000-01-01T07:00:00.0-05:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T12:00:00.0+01:00')).should.be.false

  it 'should return null in cases where a is b but there are unknown values in a and b', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01').beforeOrSameAs(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01').beforeOrSameAs(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000').beforeOrSameAs(DT.DateTime.parse('2000'))

  it 'should return null in cases where a has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.998'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59.998'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:59:59.998'))
    should.not.exist DT.DateTime.parse('2000-01-01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T23:59:59.998'))
    should.not.exist DT.DateTime.parse('2000-01').beforeOrSameAs(DT.DateTime.parse('2000-01-31T23:59:59.998'))
    should.not.exist DT.DateTime.parse('2000').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.998'))

  it 'should return null in cases where b has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').beforeOrSameAs(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').beforeOrSameAs(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').beforeOrSameAs(DT.DateTime.parse('2000'))

  it 'should accept cases where a has unknown values but is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:01.0')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:01:00.0')).should.be.true
    DT.DateTime.parse('2000-01-01T00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T01:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01-01').beforeOrSameAs(DT.DateTime.parse('2000-01-02T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01').beforeOrSameAs(DT.DateTime.parse('2000-02-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000').beforeOrSameAs(DT.DateTime.parse('2001-01-01T00:00:00.0')).should.be.true

  it 'should accept cases where a has unknown values but is still deterministicly before or same as b', ->
    DT.DateTime.parse('2000-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.999')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59.999')).should.be.true
    DT.DateTime.parse('2000-01-01T00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:59:59.999')).should.be.true
    DT.DateTime.parse('2000-01-01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T23:59:59.999')).should.be.true
    DT.DateTime.parse('2000-01').beforeOrSameAs(DT.DateTime.parse('2000-01-31T23:59:59.999')).should.be.true
    DT.DateTime.parse('2000').beforeOrSameAs(DT.DateTime.parse('2001-12-31T23:59:59.999')).should.be.true

  it 'should accept cases where b has unknown values but a is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00:00.999').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:01')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:59.999').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:01')).should.be.true
    DT.DateTime.parse('2000-01-01T00:59:59.999').beforeOrSameAs(DT.DateTime.parse('2000-01-01T01')).should.be.true
    DT.DateTime.parse('2000-01-01T23:59:59.999').beforeOrSameAs(DT.DateTime.parse('2000-01-02')).should.be.true
    DT.DateTime.parse('2000-01-31T23:59:59.999').beforeOrSameAs(DT.DateTime.parse('2000-02')).should.be.true
    DT.DateTime.parse('2000-12-31T23:59:59.999').beforeOrSameAs(DT.DateTime.parse('2001')).should.be.true

  it 'should accept cases where b has unknown values but a is still deterministicly before or same as b', ->
    DT.DateTime.parse('2000-01-01T00:00:00.0').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00.0').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00.0').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00.0').beforeOrSameAs(DT.DateTime.parse('2000-01-01')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00.0').beforeOrSameAs(DT.DateTime.parse('2000-01')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:00.0').beforeOrSameAs(DT.DateTime.parse('2000')).should.be.true

  it 'should reject cases where a has unknown values but is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:00:01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.999')).should.be.false
    DT.DateTime.parse('2000-01-01T00:01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59.999')).should.be.false
    DT.DateTime.parse('2000-01-01T01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:59:59.999')).should.be.false
    DT.DateTime.parse('2000-01-02').beforeOrSameAs(DT.DateTime.parse('2000-01-01T23:59:59.999')).should.be.false
    DT.DateTime.parse('2000-02').beforeOrSameAs(DT.DateTime.parse('2000-01-31T23:59:59.999')).should.be.false
    DT.DateTime.parse('2001').beforeOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.false

  it 'should reject cases where b has unknown values but a is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:00:01').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.false
    DT.DateTime.parse('2000-01-01T00:01:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00:00')).should.be.false
    DT.DateTime.parse('2000-01-01T01:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01T00')).should.be.false
    DT.DateTime.parse('2000-01-02T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01-01')).should.be.false
    DT.DateTime.parse('2000-02-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000-01')).should.be.false
    DT.DateTime.parse('2001-01-01T00:00:00').beforeOrSameAs(DT.DateTime.parse('2000')).should.be.false

describe 'DateTime.after', ->

  it 'should accept cases where a is after b', ->
    DT.DateTime.parse('2000-01-01T00:00:00.001').after(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:01.0').after(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01-01T00:01:00.0').after(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01-01T01:00:00.0').after(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01-02T00:00:00.0').after(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-02-01T00:00:00.0').after(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2001-01-01T00:00:00.0').after(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true

  it 'should reject cases where a is before b', ->
    DT.DateTime.parse('2000-12-31T23:59:59.998').after(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.false
    DT.DateTime.parse('2000-12-31T23:59:58.999').after(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.false
    DT.DateTime.parse('2000-12-31T23:58:59.999').after(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.false
    DT.DateTime.parse('2000-12-31T22:59:59.999').after(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.false
    DT.DateTime.parse('2000-12-30T23:59:59.999').after(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.false
    DT.DateTime.parse('2000-11-31T23:59:59.999').after(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.false
    DT.DateTime.parse('1999-12-31T23:59:59.999').after(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.false

  it 'should reject cases where a is b', ->
    DT.DateTime.parse('2000-01-01T00:00:00.0').after(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.false

  it 'should work with different timezone offsets', ->
    DT.DateTime.parse('2000-01-01T07:00:00.0-05:00').after(DT.DateTime.parse('2000-01-01T12:00:00.0+01:00')).should.be.true
    DT.DateTime.parse('2000-01-01T12:00:00.0+01:00').after(DT.DateTime.parse('2000-01-01T06:00:00.0-05:00')).should.be.false
    DT.DateTime.parse('2000-01-01T12:00:00.0+01:00').after(DT.DateTime.parse('2000-01-01T07:00:00.0-05:00')).should.be.false

  it 'should return null in cases where a is b but there are unknown values', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00').after(DT.DateTime.parse('2000-01-01T00:00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').after(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').after(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01').after(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01').after(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000').after(DT.DateTime.parse('2000'))

  it 'should return null in cases where a has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00').after(DT.DateTime.parse('2000-01-01T00:00:00.0'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').after(DT.DateTime.parse('2000-01-01T00:00:00.0'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').after(DT.DateTime.parse('2000-01-01T00:00:00.0'))
    should.not.exist DT.DateTime.parse('2000-01-01').after(DT.DateTime.parse('2000-01-01T00:00:00.0'))
    should.not.exist DT.DateTime.parse('2000-01').after(DT.DateTime.parse('2000-01-01T00:00:00.0'))
    should.not.exist DT.DateTime.parse('2000').after(DT.DateTime.parse('2000-01-01T00:00:00.0'))

  it 'should return null in cases where b has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').after(DT.DateTime.parse('2000-01-01T00:00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').after(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').after(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').after(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').after(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').after(DT.DateTime.parse('2000'))

  it 'should accept cases where a has unknown values but is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:00:01').after(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01-01T00:01').after(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01-01T01').after(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01-02').after(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-02').after(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2001').after(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true

  it 'should accept cases where b has unknown values but a is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:00:01.0').after(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T00:01:00.0').after(DT.DateTime.parse('2000-01-01T00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T01:00:00.0').after(DT.DateTime.parse('2000-01-01T00')).should.be.true
    DT.DateTime.parse('2000-01-02T00:00:00.0').after(DT.DateTime.parse('2000-01-01')).should.be.true
    DT.DateTime.parse('2000-02-01T00:00:00.0').after(DT.DateTime.parse('2000-01')).should.be.true
    DT.DateTime.parse('2001-01-01T00:00:00.0').after(DT.DateTime.parse('2000')).should.be.true

  it 'should reject cases where a has unknown values but is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00:00').after(DT.DateTime.parse('2000-01-01T00:00:01.0')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00').after(DT.DateTime.parse('2000-01-01T00:01:00.0')).should.be.false
    DT.DateTime.parse('2000-01-01T00').after(DT.DateTime.parse('2000-01-01T01:00:00.0')).should.be.false
    DT.DateTime.parse('2000-01-01').after(DT.DateTime.parse('2000-01-02T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000-01').after(DT.DateTime.parse('2000-02-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000').after(DT.DateTime.parse('2001-01-01T00:00:00.0')).should.be.false

  it 'should reject cases where b has unknown values but a is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00:00.0').after(DT.DateTime.parse('2000-01-01T00:00:01')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00:00.0').after(DT.DateTime.parse('2000-01-01T00:01')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00:00.0').after(DT.DateTime.parse('2000-01-01T01')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00:00.0').after(DT.DateTime.parse('2000-01-02')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00:00.0').after(DT.DateTime.parse('2000-02')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00:00.0').after(DT.DateTime.parse('2001')).should.be.false

describe 'DateTime.afterOrSameAs', ->

  it 'should accept cases where a is after b', ->
    DT.DateTime.parse('2000-01-01T00:00:00.001').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:01.0').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01-01T00:01:00.0').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01-01T01:00:00.0').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01-02T00:00:00.0').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-02-01T00:00:00.0').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2001-01-01T00:00:00.0').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true

  it 'should reject cases where a is before b', ->
    DT.DateTime.parse('2000-12-31T23:59:59.998').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.false
    DT.DateTime.parse('2000-12-31T23:59:58.999').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.false
    DT.DateTime.parse('2000-12-31T23:58:59.999').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.false
    DT.DateTime.parse('2000-12-31T22:59:59.999').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.false
    DT.DateTime.parse('2000-12-30T23:59:59.999').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.false
    DT.DateTime.parse('2000-11-31T23:59:59.999').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.false
    DT.DateTime.parse('1999-12-31T23:59:59.999').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.false

  it 'should accept cases where a is b', ->
    DT.DateTime.parse('2000-01-01T00:00:00.0').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true

  it 'should work with different timezone offsets', ->
    DT.DateTime.parse('2000-01-01T07:00:00.0-05:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T12:00:00.0+01:00')).should.be.true
    DT.DateTime.parse('2000-01-01T12:00:00.0+01:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T06:00:00.0-05:00')).should.be.true
    DT.DateTime.parse('2000-01-01T12:00:00.0+01:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T07:00:00.0-05:00')).should.be.false

  it 'should return null in cases where a is b but there and b have unknown values', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01').afterOrSameAs(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01').afterOrSameAs(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000').afterOrSameAs(DT.DateTime.parse('2000'))

  it 'should return null in cases where a has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.999'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59.999'))
    should.not.exist DT.DateTime.parse('2000-01-01T00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:59:59.999'))
    should.not.exist DT.DateTime.parse('2000-01-01').afterOrSameAs(DT.DateTime.parse('2000-01-01T23:59:59.999'))
    should.not.exist DT.DateTime.parse('2000-01').afterOrSameAs(DT.DateTime.parse('2000-01-31T23:59:59.999'))
    should.not.exist DT.DateTime.parse('2000').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999'))

  it 'should return null in cases where b has unknown values that prevent deterministic result', ->
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').afterOrSameAs(DT.DateTime.parse('2000-01-01T00'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').afterOrSameAs(DT.DateTime.parse('2000-01-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').afterOrSameAs(DT.DateTime.parse('2000-01'))
    should.not.exist DT.DateTime.parse('2000-01-01T00:00:00.001').afterOrSameAs(DT.DateTime.parse('2000'))

  it 'should accept cases where a has unknown values but is still deterministicly after b', ->
    DT.DateTime.parse('2000-01-01T00:00:01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.999')).should.be.true
    DT.DateTime.parse('2000-01-01T00:01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:59.999')).should.be.true
    DT.DateTime.parse('2000-01-01T01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:59:59.999')).should.be.true
    DT.DateTime.parse('2000-01-02').afterOrSameAs(DT.DateTime.parse('2000-01-01T23:59:59.999')).should.be.true
    DT.DateTime.parse('2000-02').afterOrSameAs(DT.DateTime.parse('2000-01-31T23:59:59.999')).should.be.true
    DT.DateTime.parse('2001').afterOrSameAs(DT.DateTime.parse('2000-12-31T23:59:59.999')).should.be.true

  it 'should accept cases where a has unknown values but is still deterministicly after or same as b', ->
    DT.DateTime.parse('2000-01-01T00:00:01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:01.0')).should.be.true
    DT.DateTime.parse('2000-01-01T00:01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:01:00.0')).should.be.true
    DT.DateTime.parse('2000-01-01T01').afterOrSameAs(DT.DateTime.parse('2000-01-01T01:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01-01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000-01').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true
    DT.DateTime.parse('2000').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00.0')).should.be.true

  it 'should accept cases where b has unknown values but a is still deterministicly after or same as b', ->
    DT.DateTime.parse('2000-01-01T00:00:01.0').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T00:01:00.0').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T01:00:00.0').afterOrSameAs(DT.DateTime.parse('2000-01-01T00')).should.be.true
    DT.DateTime.parse('2000-01-02T00:00:00.0').afterOrSameAs(DT.DateTime.parse('2000-01-01')).should.be.true
    DT.DateTime.parse('2000-02-01T00:00:00.0').afterOrSameAs(DT.DateTime.parse('2000-01')).should.be.true
    DT.DateTime.parse('2001-01-01T00:00:00.0').afterOrSameAs(DT.DateTime.parse('2000')).should.be.true

  it 'should accept cases where b has unknown values but a is still deterministicly same as or after b', ->
    DT.DateTime.parse('2000-01-01T00:00:00.999').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T00:00:59.999').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00')).should.be.true
    DT.DateTime.parse('2000-01-01T00:59:59.999').afterOrSameAs(DT.DateTime.parse('2000-01-01T00')).should.be.true
    DT.DateTime.parse('2000-01-01T23:59:59.999').afterOrSameAs(DT.DateTime.parse('2000-01-01')).should.be.true
    DT.DateTime.parse('2000-01-31T23:59:59.999').afterOrSameAs(DT.DateTime.parse('2000-01')).should.be.true
    DT.DateTime.parse('2000-12-31T23:59:59.999').afterOrSameAs(DT.DateTime.parse('2000')).should.be.true

  it 'should reject cases where a has unknown values but is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:01.0')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:01:00.0')).should.be.false
    DT.DateTime.parse('2000-01-01T00').afterOrSameAs(DT.DateTime.parse('2000-01-01T01:00:00.0')).should.be.false
    DT.DateTime.parse('2000-01-01').afterOrSameAs(DT.DateTime.parse('2000-01-02T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000-01').afterOrSameAs(DT.DateTime.parse('2000-02-01T00:00:00.0')).should.be.false
    DT.DateTime.parse('2000').afterOrSameAs(DT.DateTime.parse('2001-01-01T00:00:00.0')).should.be.false

  it 'should reject cases where b has unknown values but a is still deterministicly before b', ->
    DT.DateTime.parse('2000-01-01T00:00:00.999').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:00:01')).should.be.false
    DT.DateTime.parse('2000-01-01T00:00:59.999').afterOrSameAs(DT.DateTime.parse('2000-01-01T00:01')).should.be.false
    DT.DateTime.parse('2000-01-01T00:59:59.999').afterOrSameAs(DT.DateTime.parse('2000-01-01T01')).should.be.false
    DT.DateTime.parse('2000-01-01T23:59:59.999').afterOrSameAs(DT.DateTime.parse('2000-01-02')).should.be.false
    DT.DateTime.parse('2000-01-31T23:59:59.999').afterOrSameAs(DT.DateTime.parse('2000-02')).should.be.false
    DT.DateTime.parse('2000-12-31T23:59:59.999').afterOrSameAs(DT.DateTime.parse('2001')).should.be.false

describe 'Interval', ->

  it 'should properly set all properties when constructed as DateTime interval', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'), true, false)
    i.low.should.eql DT.DateTime.parse '2012-01-01'
    i.high.should.eql DT.DateTime.parse '2013-01-01'
    i.lowClosed.should.be.true
    i.highClosed.should.be.false

  it 'should properly set all properties when constructed as integer interval', ->
    i = new DT.Interval(12, 36, true, false)
    i.low.should.equal 12
    i.high.should.equal 36
    i.lowClosed.should.be.true
    i.highClosed.should.be.false

  it 'should default lowClosed/highClosed to true', ->
    i = new DT.Interval(DT.DateTime.parse('2012-01-01'), DT.DateTime.parse('2013-01-01'))
    i.low.should.eql DT.DateTime.parse '2012-01-01'
    i.high.should.eql DT.DateTime.parse '2013-01-01'
    i.lowClosed.should.be.true
    i.highClosed.should.be.true

describe 'DateTimeInterval.includes(DateTimeInterval)', ->
  @beforeEach ->
    setupIntervalsAndDateTimes @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @dIvl.sameAs
    x.closed.includes(y.closed).should.be.true
    x.closed.includes(y.open).should.be.true
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.true
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @dIvl.before
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.false
    y.closed.includes(x.open).should.be.false
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @dIvl.meets
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.false
    y.closed.includes(x.open).should.be.false
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @dIvl.overlaps
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.false
    y.closed.includes(x.open).should.be.false
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.false

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @dIvl.begins
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.true

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @dIvl.during
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.true
    y.open.includes(x.open).should.be.true

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @dIvl.ends
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.true

  it 'should properly handle imprecision', ->

    [x, y] = xy @dIvl.sameAs
    x.closed.includes(y.toMinute).should.be.true
    should.not.exist x.toHour.includes(y.toMinute)

    [x, y] = xy @dIvl.before
    x.toMonth.includes(y.toMonth).should.be.false
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @dIvl.meets
    x.toMonth.includes(y.toMonth).should.be.false
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @dIvl.overlaps
    x.toMonth.includes(y.toMonth).should.be.false
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @dIvl.begins
    x.toMinute.includes(y.toMinute).should.be.false
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @dIvl.during
    x.toMonth.includes(y.toMonth).should.be.false
    y.toMonth.includes(x.toMonth).should.be.true
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @dIvl.ends
    x.toMinute.includes(y.toMinute).should.be.false
    should.not.exist x.toYear.includes(y.closed)

describe 'DateTimeInterval.includes(DateTime)', ->
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

describe 'DateTimeInterval.includedIn(DateTimeInterval)', ->
  @beforeEach ->
    setupIntervalsAndDateTimes @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @dIvl.sameAs
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true

    y.closed.includedIn(x.closed).should.be.true
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.true
    y.open.includedIn(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @dIvl.before
    x.closed.includedIn(y.closed).should.be.false
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.false
    x.open.includedIn(y.open).should.be.false
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @dIvl.meets
    x.closed.includedIn(y.closed).should.be.false
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.false
    x.open.includedIn(y.open).should.be.false
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @dIvl.overlaps
    x.closed.includedIn(y.closed).should.be.false
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.false
    x.open.includedIn(y.open).should.be.false
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @dIvl.begins
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @dIvl.during
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.true
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @dIvl.ends
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly handle imprecision', ->
    [x, y] = xy @dIvl.sameAs
    should.not.exist x.closed.includedIn(y.toMinute)
    should.not.exist x.toHour.includedIn(y.toMinute)

    [x, y] = xy @dIvl.before
    x.toMonth.includedIn(y.toMonth).should.be.false
    should.not.exist x.toYear.includedIn(y.closed)

    [x, y] = xy @dIvl.meets
    x.toMonth.includedIn(y.toMonth).should.be.false
    should.not.exist x.toYear.includedIn(y.closed)

    [x, y] = xy @dIvl.overlaps
    x.toMonth.includedIn(y.toMonth).should.be.false
    should.not.exist x.toYear.includedIn(y.closed)

    [x, y] = xy @dIvl.begins
    should.not.exist x.toMinute.includedIn(y.toMinute)
    x.toYear.includedIn(y.closed).should.be.true

    [x, y] = xy @dIvl.during
    x.toMonth.includedIn(y.toMonth).should.be.true
    y.toMonth.includedIn(x.toMonth).should.be.false
    x.toYear.includedIn(y.closed).should.be.true

    [x, y] = xy @dIvl.ends
    should.not.exist x.toMinute.includedIn(y.toMinute)
    x.toYear.includedIn(y.closed).should.be.true

describe 'DateTimeInterval.includedIn(DateTime)', ->
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

  it 'should properly calculate zero-width intervals', ->
    ivl = new DT.Interval(@mid2012.full, @mid2012.full)
    ivl.includedIn(@beg2012.full).should.be.false
    ivl.includedIn(@mid2012.full).should.be.true
    ivl.includedIn(@end2012.full).should.be.false

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

describe 'DateTimeInterval.overlaps(DateTimeInterval)', ->
  @beforeEach ->
    setupIntervalsAndDateTimes @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @dIvl.sameAs
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @dIvl.before
    x.closed.overlaps(y.closed).should.be.false
    x.closed.overlaps(y.open).should.be.false
    x.open.overlaps(y.closed).should.be.false
    x.open.overlaps(y.open).should.be.false
    y.closed.overlaps(x.closed).should.be.false
    y.closed.overlaps(x.open).should.be.false
    y.open.overlaps(x.closed).should.be.false
    y.open.overlaps(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @dIvl.meets
    x.closed.overlaps(y.closed).should.be.false
    x.closed.overlaps(y.open).should.be.false
    x.open.overlaps(y.closed).should.be.false
    x.open.overlaps(y.open).should.be.false
    y.closed.overlaps(x.closed).should.be.false
    y.closed.overlaps(x.open).should.be.false
    y.open.overlaps(x.closed).should.be.false
    y.open.overlaps(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @dIvl.overlaps
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @dIvl.begins
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @dIvl.during
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @dIvl.ends
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly handle imprecision', ->
    [x, y] = xy @dIvl.sameAs
    x.closed.overlaps(y.toMinute).should.be.true
    x.toHour.overlaps(y.toMinute).should.be.true

    [x, y] = xy @dIvl.before
    x.toMonth.overlaps(y.toMonth).should.be.false
    should.not.exist x.toYear.overlaps(y.closed)

    [x, y] = xy @dIvl.meets
    x.toMonth.overlaps(y.toMonth).should.be.false
    should.not.exist x.toYear.overlaps(y.closed)

    [x, y] = xy @dIvl.overlaps
    x.toMonth.overlaps(y.toMonth).should.be.true
    should.not.exist x.toYear.overlaps(y.closed)

    [x, y] = xy @dIvl.begins
    x.toMinute.overlaps(y.toMinute).should.be.true
    x.toYear.overlaps(y.closed).should.be.true

    [x, y] = xy @dIvl.during
    x.toMonth.overlaps(y.toMonth).should.be.true
    y.toMonth.overlaps(x.toMonth).should.be.true
    x.toYear.overlaps(y.closed).should.be.true

    [x, y] = xy @dIvl.ends
    x.toMinute.overlaps(y.toMinute).should.be.true
    x.toYear.overlaps(y.closed).should.be.true

describe 'DateTimeInterval.overlaps(DateTime)', ->
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

describe 'IntegerInterval.includes(IntegerInterval)', ->
  @beforeEach ->
    setupIntervalsAndDateTimes @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @iIvl.sameAs
    x.closed.includes(y.closed).should.be.true
    x.closed.includes(y.open).should.be.true
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.true
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @iIvl.before
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.false
    y.closed.includes(x.open).should.be.false
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @iIvl.meets
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.false
    y.closed.includes(x.open).should.be.false
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @iIvl.overlaps
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.false
    y.closed.includes(x.open).should.be.false
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.false

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @iIvl.begins
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.true

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @iIvl.during
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.true
    y.open.includes(x.open).should.be.true

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @iIvl.ends
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.true

  it 'should properly handle imprecision', ->
    uIvl = new DT.Interval(new DT.Uncertainty(5,10), new DT.Uncertainty(15, 20))

    ivl = new DT.Interval(0, 100)
    ivl.includes(uIvl).should.be.true
    uIvl.includes(ivl).should.be.false

    ivl = new DT.Interval(-100, 0)
    ivl.includes(uIvl).should.be.false
    uIvl.includes(ivl).should.be.false

    ivl = new DT.Interval(10, 15)
    should.not.exist ivl.includes(uIvl)
    uIvl.includes(ivl).should.be.true

    ivl = new DT.Interval(5, 20)
    ivl.includes(uIvl).should.be.true
    should.not.exist uIvl.includes(ivl)

    should.not.exist uIvl.includes(uIvl)

describe 'IntegerInterval.includes(Integer)', ->
  @beforeEach ->
    setupIntervalsAndDateTimes @

  it 'should properly calculate integers less than it', ->
    @zeroToHundred.closed.includes(-5).should.be.false

  it 'should properly calculate the left boundary integer', ->
    @zeroToHundred.closed.includes(0).should.be.true
    @zeroToHundred.open.includes(0).should.be.false

  it 'should properly calculate integers in the middle of it', ->
    @zeroToHundred.closed.includes(50).should.be.true

  it 'should properly calculate the right boundary integer', ->
    @zeroToHundred.closed.includes(100).should.be.true
    @zeroToHundred.open.includes(100).should.be.false

  it 'should properly calculate integers greater than it', ->
    @zeroToHundred.closed.includes(105).should.be.false

  it 'should properly handle imprecision', ->
    @zeroToHundred.closed.includes(new DT.Uncertainty(-20,-10)).should.be.false
    should.not.exist @zeroToHundred.closed.includes(new DT.Uncertainty(-20,20))
    @zeroToHundred.closed.includes(new DT.Uncertainty(0,100)).should.be.true
    should.not.exist @zeroToHundred.closed.includes(new DT.Uncertainty(80,120))
    @zeroToHundred.closed.includes(new DT.Uncertainty(120,140)).should.be.false
    should.not.exist @zeroToHundred.closed.includes(new DT.Uncertainty(-20,120))

    uIvl = new DT.Interval(new DT.Uncertainty(5,10), new DT.Uncertainty(15, 20))

    uIvl.includes(0).should.be.false
    should.not.exist uIvl.includes(5)
    should.not.exist uIvl.includes(6)
    uIvl.includes(10).should.be.true
    uIvl.includes(12).should.be.true
    uIvl.includes(15).should.be.true
    should.not.exist uIvl.includes(16)
    should.not.exist uIvl.includes(20)
    uIvl.includes(25).should.be.false

    uIvl.includes(new DT.Uncertainty(0,4)).should.be.false
    should.not.exist uIvl.includes(new DT.Uncertainty(0,5))
    should.not.exist uIvl.includes(new DT.Uncertainty(5,10))
    uIvl.includes(new DT.Uncertainty(10,15)).should.be.true
    should.not.exist uIvl.includes(new DT.Uncertainty(15,20))
    should.not.exist uIvl.includes(new DT.Uncertainty(20,25))
    uIvl.includes(new DT.Uncertainty(25,30)).should.be.false

describe 'IntegerInterval.includedIn(IntegerInterval)', ->
  @beforeEach ->
    setupIntervalsAndDateTimes @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @iIvl.sameAs
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true

    y.closed.includedIn(x.closed).should.be.true
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.true
    y.open.includedIn(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @iIvl.before
    x.closed.includedIn(y.closed).should.be.false
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.false
    x.open.includedIn(y.open).should.be.false
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @iIvl.meets
    x.closed.includedIn(y.closed).should.be.false
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.false
    x.open.includedIn(y.open).should.be.false
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @iIvl.overlaps
    x.closed.includedIn(y.closed).should.be.false
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.false
    x.open.includedIn(y.open).should.be.false
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @iIvl.begins
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @iIvl.during
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.true
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @iIvl.ends
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly handle imprecision', ->
    uIvl = new DT.Interval(new DT.Uncertainty(5,10), new DT.Uncertainty(15, 20))

    ivl = new DT.Interval(0, 100)
    ivl.includedIn(uIvl).should.be.false
    uIvl.includedIn(ivl).should.be.true

    ivl = new DT.Interval(-100, 0)
    ivl.includedIn(uIvl).should.be.false
    uIvl.includedIn(ivl).should.be.false

    ivl = new DT.Interval(10, 15)
    ivl.includedIn(uIvl).should.be.true
    should.not.exist uIvl.includedIn(ivl)

    ivl = new DT.Interval(5, 20)
    should.not.exist ivl.includedIn(uIvl)
    uIvl.includedIn(ivl).should.be.true

    should.not.exist uIvl.includedIn(uIvl)

describe 'IntegerInterval.includedIn(Integer)', ->
  @beforeEach ->
    setupIntervalsAndDateTimes @

  # Admittedly, most of this doesn't really make sense, but let's be sure the code reflects that!

  it 'should properly calculate integers less than it', ->
    @zeroToHundred.closed.includedIn(-5).should.be.false

  it 'should properly calculate the left boundary integer', ->
    @zeroToHundred.closed.includedIn(0).should.be.false
    @zeroToHundred.open.includedIn(0).should.be.false

  it 'should properly calculate integers in the middle of it', ->
    @zeroToHundred.closed.includedIn(50).should.be.false

  it 'should properly calculate the right boundary integer', ->
    @zeroToHundred.closed.includedIn(100).should.be.false
    @zeroToHundred.open.includedIn(100).should.be.false

  it 'should properly calculate integers greater than it', ->
    @zeroToHundred.closed.includedIn(105).should.be.false

  it 'should properly calculate for zero-width intervals', ->
    ivl = new DT.Interval(50, 50)
    ivl.includedIn(25).should.be.false
    ivl.includedIn(50).should.be.true
    ivl.includedIn(75).should.be.false

  it 'should properly handle imprecision', ->
    @zeroToHundred.closed.includedIn(new DT.Uncertainty(-20,-10)).should.be.false
    @zeroToHundred.closed.includedIn(new DT.Uncertainty(-20,20)).should.be.false
    @zeroToHundred.closed.includedIn(new DT.Uncertainty(0,100)).should.be.false
    @zeroToHundred.closed.includedIn(new DT.Uncertainty(80,120)).should.be.false
    @zeroToHundred.closed.includedIn(new DT.Uncertainty(120,140)).should.be.false
    @zeroToHundred.closed.includedIn(new DT.Uncertainty(-20,120)).should.be.false

    uIvl = new DT.Interval(new DT.Uncertainty(5,10), new DT.Uncertainty(15, 20))

    uIvl.includedIn(0).should.be.false
    uIvl.includedIn(12).should.be.false
    uIvl.includedIn(25).should.be.false

    uIvl.includedIn(new DT.Uncertainty(0,4)).should.be.false
    uIvl.includedIn(new DT.Uncertainty(0,5)).should.be.false
    uIvl.includedIn(new DT.Uncertainty(10,15)).should.be.false
    uIvl.includedIn(new DT.Uncertainty(20,25)).should.be.false
    uIvl.includedIn(new DT.Uncertainty(25,30)).should.be.false

    ivl = new DT.Interval(5, 5)
    ivl.includedIn(new DT.Uncertainty(0,4)).should.be.false
    ivl.includedIn(new DT.Uncertainty(5,5)).should.be.true
    should.not.exist ivl.includedIn(new DT.Uncertainty(0,10))
    ivl.includedIn(new DT.Uncertainty(6,10)).should.be.false

describe 'IntegerInterval.overlaps(IntegerInterval)', ->
  @beforeEach ->
    setupIntervalsAndDateTimes @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @iIvl.sameAs
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @iIvl.before
    x.closed.overlaps(y.closed).should.be.false
    x.closed.overlaps(y.open).should.be.false
    x.open.overlaps(y.closed).should.be.false
    x.open.overlaps(y.open).should.be.false
    y.closed.overlaps(x.closed).should.be.false
    y.closed.overlaps(x.open).should.be.false
    y.open.overlaps(x.closed).should.be.false
    y.open.overlaps(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @iIvl.meets
    x.closed.overlaps(y.closed).should.be.false
    x.closed.overlaps(y.open).should.be.false
    x.open.overlaps(y.closed).should.be.false
    x.open.overlaps(y.open).should.be.false
    y.closed.overlaps(x.closed).should.be.false
    y.closed.overlaps(x.open).should.be.false
    y.open.overlaps(x.closed).should.be.false
    y.open.overlaps(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @iIvl.overlaps
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @iIvl.begins
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @iIvl.during
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @iIvl.ends
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly handle imprecision', ->
    uIvl = new DT.Interval(new DT.Uncertainty(5,10), new DT.Uncertainty(15, 20))

    ivl = new DT.Interval(0, 100)
    ivl.overlaps(uIvl).should.be.true
    uIvl.overlaps(ivl).should.be.true

    ivl = new DT.Interval(-100, 0)
    ivl.overlaps(uIvl).should.be.false
    uIvl.overlaps(ivl).should.be.false

    ivl = new DT.Interval(10, 15)
    ivl.overlaps(uIvl).should.be.true
    uIvl.overlaps(ivl).should.be.true

    ivl = new DT.Interval(5, 20)
    ivl.overlaps(uIvl).should.be.true
    uIvl.overlaps(ivl).should.be.true

    uIvl.overlaps(uIvl).should.be.true

describe 'IntegerInterval.overlaps(Integer)', ->
  @beforeEach ->
    setupIntervalsAndDateTimes @

  it 'should properly calculate integers less than it', ->
    @zeroToHundred.closed.overlaps(-5).should.be.false

  it 'should properly calculate the left boundary integer', ->
    @zeroToHundred.closed.overlaps(0).should.be.true
    @zeroToHundred.open.overlaps(0).should.be.false

  it 'should properly calculate integers in the middle of it', ->
    @zeroToHundred.closed.overlaps(50).should.be.true

  it 'should properly calculate the right boundary integer', ->
    @zeroToHundred.closed.overlaps(100).should.be.true
    @zeroToHundred.open.overlaps(100).should.be.false

  it 'should properly calculate integers greater than it', ->
    @zeroToHundred.closed.overlaps(105).should.be.false

  it 'should properly handle imprecision', ->
    @zeroToHundred.closed.overlaps(new DT.Uncertainty(-20,-10)).should.be.false
    should.not.exist @zeroToHundred.closed.overlaps(new DT.Uncertainty(-20,20))
    @zeroToHundred.closed.overlaps(new DT.Uncertainty(0,100)).should.be.true
    should.not.exist @zeroToHundred.closed.overlaps(new DT.Uncertainty(80,120))
    @zeroToHundred.closed.overlaps(new DT.Uncertainty(120,140)).should.be.false
    should.not.exist @zeroToHundred.closed.overlaps(new DT.Uncertainty(-20,120))

    uIvl = new DT.Interval(new DT.Uncertainty(5,10), new DT.Uncertainty(15, 20))

    uIvl.overlaps(0).should.be.false
    should.not.exist uIvl.overlaps(5)
    should.not.exist uIvl.overlaps(6)
    uIvl.overlaps(10).should.be.true
    uIvl.overlaps(12).should.be.true
    uIvl.overlaps(15).should.be.true
    should.not.exist uIvl.overlaps(16)
    should.not.exist uIvl.overlaps(20)
    uIvl.overlaps(25).should.be.false

    uIvl.overlaps(new DT.Uncertainty(0,4)).should.be.false
    should.not.exist uIvl.overlaps(new DT.Uncertainty(0,5))
    should.not.exist uIvl.overlaps(new DT.Uncertainty(5,10))
    uIvl.overlaps(new DT.Uncertainty(10,15)).should.be.true
    should.not.exist uIvl.overlaps(new DT.Uncertainty(15,20))
    should.not.exist uIvl.overlaps(new DT.Uncertainty(20,25))
    uIvl.overlaps(new DT.Uncertainty(25,30)).should.be.false
