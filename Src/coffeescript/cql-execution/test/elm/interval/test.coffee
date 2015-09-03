should = require 'should'
setup = require '../../setup'
data = require './data'
{ Interval } = require '../../../lib/datatypes/interval'
{ DateTime } = require '../../../lib/datatypes/datetime'

describe 'Interval', ->
  @beforeEach ->
    setup @, data

  it 'should properly represent an open interval', ->
    @open.lowClosed.should.be.false
    @open.highClosed.should.be.false
    @open.low.exec(@ctx).should.eql new DateTime(2012, 1, 1)
    @open.high.exec(@ctx).should.eql new DateTime(2013, 1, 1)

  it 'should properly represent a left-open interval', ->
    @leftOpen.lowClosed.should.be.false
    @leftOpen.highClosed.should.be.true
    @leftOpen.low.exec(@ctx).should.eql new DateTime(2012, 1, 1)
    @leftOpen.high.exec(@ctx).should.eql new DateTime(2013, 1, 1)

  it 'should properly represent a right-open interval', ->
    @rightOpen.lowClosed.should.be.true
    @rightOpen.highClosed.should.be.false
    @rightOpen.low.exec(@ctx).should.eql new DateTime(2012, 1, 1)
    @rightOpen.high.exec(@ctx).should.eql new DateTime(2013, 1, 1)

  it 'should properly represent a closed interval', ->
    @closed.lowClosed.should.be.true
    @closed.highClosed.should.be.true
    @closed.low.exec(@ctx).should.eql new DateTime(2012, 1, 1)
    @closed.high.exec(@ctx).should.eql new DateTime(2013, 1, 1)

  it 'should exec to native Interval datatype', ->
    ivl = @open.exec(@cql)
    ivl.should.be.instanceOf Interval
    ivl.lowClosed.should.equal @open.lowClosed
    ivl.highClosed.should.equal @open.highClosed
    ivl.low.should.eql new DateTime(2012, 1, 1)
    ivl.high.should.eql new DateTime(2013, 1, 1)

describe 'Equal', ->
  @beforeEach ->
    setup @, data

  it 'should determine equal integer intervals', ->
    @equalClosed.exec(@ctx).should.be.true
    @equalOpen.exec(@ctx).should.be.true
    @equalOpenClosed.exec(@ctx).should.be.true

  it 'should determine unequal integer intervals', ->
    @unequalClosed.exec(@ctx).should.be.false
    @unequalOpen.exec(@ctx).should.be.false
    @unequalClosedOpen.exec(@ctx).should.be.false

  it 'should determine equal datetime intervals', ->
    @equalDates.exec(@ctx).should.be.true
    @equalDatesOpenClosed.exec(@ctx).should.be.true

  it 'should operate correctly with imprecision', ->
    should(@sameDays.exec(@ctx)).be.null
    @differentDays.exec(@ctx).should.be.false

describe 'NotEqual', ->
  @beforeEach ->
    setup @, data

  it 'should determine equal integer intervals', ->
    @equalClosed.exec(@ctx).should.be.false
    @equalOpen.exec(@ctx).should.be.false
    @equalOpenClosed.exec(@ctx).should.be.false

  it 'should determine unequal integer intervals', ->
    @unequalClosed.exec(@ctx).should.be.true
    @unequalOpen.exec(@ctx).should.be.true
    @unequalClosedOpen.exec(@ctx).should.be.true

  it 'should determine equal datetime intervals', ->
    @equalDates.exec(@ctx).should.be.false
    @equalDatesOpenClosed.exec(@ctx).should.be.false

  it 'should operate correctly with imprecision', ->
    should(@sameDays.exec(@ctx)).be.null
    @differentDays.exec(@ctx).should.be.true

describe 'Contains', ->
  @beforeEach ->
    setup @, data

  it 'should accept contained items', ->
    @containsInt.exec(@ctx).should.be.true
    @containsReal.exec(@ctx).should.be.true
    @containsDate.exec(@ctx).should.be.true

  it 'should reject uncontained items', ->
    @notContainsInt.exec(@ctx).should.be.false
    @notContainsReal.exec(@ctx).should.be.false
    @notContainsDate.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (int)', ->
    @negInfBegContainsInt.exec(@ctx).should.be.true
    @negInfBegNotContainsInt.exec(@ctx).should.be.false
    @unknownBegContainsInt.exec(@ctx).should.be.true
    should(@unknownBegMayContainInt.exec(@ctx)).be.null
    @unknownBegNotContainsInt.exec(@ctx).should.be.false
    @posInfEndContainsInt.exec(@ctx).should.be.true
    @posInfEndNotContainsInt.exec(@ctx).should.be.false
    @unknownEndContainsInt.exec(@ctx).should.be.true
    should(@unknownEndMayContainInt.exec(@ctx)).be.null
    @unknownEndNotContainsInt.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (date)', ->
    @negInfBegContainsDate.exec(@ctx).should.be.true
    @negInfBegNotContainsDate.exec(@ctx).should.be.false
    @unknownBegContainsDate.exec(@ctx).should.be.true
    should(@unknownBegMayContainDate.exec(@ctx)).be.null
    @unknownBegNotContainsDate.exec(@ctx).should.be.false
    @posInfEndContainsDate.exec(@ctx).should.be.true
    @posInfEndNotContainsDate.exec(@ctx).should.be.false
    @unknownEndContainsDate.exec(@ctx).should.be.true
    should(@unknownEndMayContainDate.exec(@ctx)).be.null
    @unknownEndNotContainsDate.exec(@ctx).should.be.false

  it 'should correctly handle imprecision', ->
    @containsImpreciseDate.exec(@ctx).should.be.true
    @notContainsImpreciseDate.exec(@ctx).should.be.false
    should(@mayContainImpreciseDate.exec(@ctx)).be.null
    @impreciseContainsDate.exec(@ctx).should.be.true
    @impreciseNotContainsDate.exec(@ctx).should.be.false
    should(@impreciseMayContainDate.exec(@ctx)).be.null

describe 'In', ->
  @beforeEach ->
    setup @, data

  it 'should accept contained items', ->
    @containsInt.exec(@ctx).should.be.true
    @containsReal.exec(@ctx).should.be.true
    @containsDate.exec(@ctx).should.be.true

  it 'should reject uncontained items', ->
    @notContainsInt.exec(@ctx).should.be.false
    @notContainsReal.exec(@ctx).should.be.false
    @notContainsDate.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (int)', ->
    @negInfBegContainsInt.exec(@ctx).should.be.true
    @negInfBegNotContainsInt.exec(@ctx).should.be.false
    @unknownBegContainsInt.exec(@ctx).should.be.true
    should(@unknownBegMayContainInt.exec(@ctx)).be.null
    @unknownBegNotContainsInt.exec(@ctx).should.be.false
    @posInfEndContainsInt.exec(@ctx).should.be.true
    @posInfEndNotContainsInt.exec(@ctx).should.be.false
    @unknownEndContainsInt.exec(@ctx).should.be.true
    should(@unknownEndMayContainInt.exec(@ctx)).be.null
    @unknownEndNotContainsInt.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (date)', ->
    @negInfBegContainsDate.exec(@ctx).should.be.true
    @negInfBegNotContainsDate.exec(@ctx).should.be.false
    @unknownBegContainsDate.exec(@ctx).should.be.true
    should(@unknownBegMayContainDate.exec(@ctx)).be.null
    @unknownBegNotContainsDate.exec(@ctx).should.be.false
    @posInfEndContainsDate.exec(@ctx).should.be.true
    @posInfEndNotContainsDate.exec(@ctx).should.be.false
    @unknownEndContainsDate.exec(@ctx).should.be.true
    should(@unknownEndMayContainDate.exec(@ctx)).be.null
    @unknownEndNotContainsDate.exec(@ctx).should.be.false

  it 'should correctly handle imprecision', ->
    @containsImpreciseDate.exec(@ctx).should.be.true
    @notContainsImpreciseDate.exec(@ctx).should.be.false
    should(@mayContainImpreciseDate.exec(@ctx)).be.null
    @impreciseContainsDate.exec(@ctx).should.be.true
    @impreciseNotContainsDate.exec(@ctx).should.be.false
    should(@impreciseMayContainDate.exec(@ctx)).be.null

describe 'Includes', ->
  @beforeEach ->
    setup @, data

  it 'should accept included items', ->
    @includesIntIvl.exec(@ctx).should.be.true
    @includesRealIvl.exec(@ctx).should.be.true
    @includesDateIvl.exec(@ctx).should.be.true

  it 'should reject unincluded items', ->
    @notIncludesIntIvl.exec(@ctx).should.be.false
    @notIncludesRealIvl.exec(@ctx).should.be.false
    @notIncludesDateIvl.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (int)', ->
    @negInfBegIncludesIntIvl.exec(@ctx).should.be.true
    @negInfBegNotIncludesIntIvl.exec(@ctx).should.be.false
    @unknownBegIncludesIntIvl.exec(@ctx).should.be.true
    should(@unknownBegMayIncludeIntIvl.exec(@ctx)).be.null
    @unknownBegNotIncludesIntIvl.exec(@ctx).should.be.false
    @posInfEndIncludesIntIvl.exec(@ctx).should.be.true
    @posInfEndNotIncludesIntIvl.exec(@ctx).should.be.false
    @unknownEndIncludesIntIvl.exec(@ctx).should.be.true
    should(@unknownEndMayIncludeIntIvl.exec(@ctx)).be.null
    @unknownEndNotIncludesIntIvl.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (date)', ->
    @negInfBegIncludesDateIvl.exec(@ctx).should.be.true
    @negInfBegNotIncludesDateIvl.exec(@ctx).should.be.false
    @unknownBegIncludesDateIvl.exec(@ctx).should.be.true
    should(@unknownBegMayIncludeDateIvl.exec(@ctx)).be.null
    @unknownBegNotIncludesDateIvl.exec(@ctx).should.be.false
    @posInfEndIncludesDateIvl.exec(@ctx).should.be.true
    @posInfEndNotIncludesDateIvl.exec(@ctx).should.be.false
    @unknownEndIncludesDateIvl.exec(@ctx).should.be.true
    should(@unknownEndMayIncludeDateIvl.exec(@ctx)).be.null
    @unknownEndNotIncludesDateIvl.exec(@ctx).should.be.false

  it 'should correctly handle imprecision', ->
    @includesImpreciseDateIvl.exec(@ctx).should.be.true
    @notIncludesImpreciseDateIvl.exec(@ctx).should.be.false
    should(@mayIncludeImpreciseDateIvl.exec(@ctx)).be.null
    @impreciseIncludesDateIvl.exec(@ctx).should.be.true
    @impreciseNotIncludesDateIvl.exec(@ctx).should.be.false
    should(@impreciseMayIncludeDateIvl.exec(@ctx)).be.null

describe 'ProperlyIncludes', ->
  @beforeEach ->
    setup @, data

  it 'should accept properly included intervals', ->
    @properlyIncludesIntIvl.exec(@ctx).should.be.true
    @properlyIncludesIntBeginsIvl.exec(@ctx).should.be.true
    @properlyIncludesIntEndsIvl.exec(@ctx).should.be.true
    @properlyIncludesRealIvl.exec(@ctx).should.be.true
    @properlyIncludesDateIvl.exec(@ctx).should.be.true

  it 'should reject intervals not properly included', ->
    @notProperlyIncludesIntIvl.exec(@ctx).should.be.false
    @notProperlyIncludesRealIvl.exec(@ctx).should.be.false
    @notProperlyIncludesDateIvl.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (int)', ->
    @posInfEndProperlyIncludesIntIvl.exec(@ctx).should.be.true
    @posInfEndNotProperlyIncludesIntIvl.exec(@ctx).should.be.false
    should(@unknownEndMayProperlyIncludeIntIvl.exec(@ctx)).be.null

describe 'IncludedIn', ->
  @beforeEach ->
    setup @, data

  it 'should accept included items', ->
    @includesIntIvl.exec(@ctx).should.be.true
    @includesRealIvl.exec(@ctx).should.be.true
    @includesDateIvl.exec(@ctx).should.be.true

  it 'should reject unincluded items', ->
    @notIncludesIntIvl.exec(@ctx).should.be.false
    @notIncludesRealIvl.exec(@ctx).should.be.false
    @notIncludesDateIvl.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (int)', ->
    @negInfBegIncludedInIntIvl.exec(@ctx).should.be.true
    @negInfBegNotIncludedInIntIvl.exec(@ctx).should.be.false
    @unknownBegIncludedInIntIvl.exec(@ctx).should.be.true
    should(@unknownBegMayBeIncludedInIntIvl.exec(@ctx)).be.null
    @unknownBegNotIncludedInIntIvl.exec(@ctx).should.be.false
    @posInfEndIncludedInIntIvl.exec(@ctx).should.be.true
    @posInfEndNotIncludedInIntIvl.exec(@ctx).should.be.false
    @unknownEndIncludedInIntIvl.exec(@ctx).should.be.true
    should(@unknownEndMayBeIncludedInIntIvl.exec(@ctx)).be.null
    @unknownEndNotIncludedInIntIvl.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (date)', ->
    @negInfBegIncludedInDateIvl.exec(@ctx).should.be.true
    @negInfBegNotIncludedInDateIvl.exec(@ctx).should.be.false
    @unknownBegIncludedInDateIvl.exec(@ctx).should.be.true
    should(@unknownBegMayBeIncludedInDateIvl.exec(@ctx)).be.null
    @unknownBegNotIncludedInDateIvl.exec(@ctx).should.be.false
    @posInfEndIncludedInDateIvl.exec(@ctx).should.be.true
    @posInfEndNotIncludedInDateIvl.exec(@ctx).should.be.false
    @unknownEndIncludedInDateIvl.exec(@ctx).should.be.true
    should(@unknownEndMayBeIncludedInDateIvl.exec(@ctx)).be.null
    @unknownEndNotIncludedInDateIvl.exec(@ctx).should.be.false

  it 'should correctly handle imprecision', ->
    @includesImpreciseDateIvl.exec(@ctx).should.be.true
    @notIncludesImpreciseDateIvl.exec(@ctx).should.be.false
    should(@mayIncludeImpreciseDateIvl.exec(@ctx)).be.null
    @impreciseIncludesDateIvl.exec(@ctx).should.be.true
    @impreciseNotIncludesDateIvl.exec(@ctx).should.be.false
    should(@impreciseMayIncludeDateIvl.exec(@ctx)).be.null

describe 'ProperlyIncludedIn', ->
  @beforeEach ->
    setup @, data

  it 'should accept properly included intervals', ->
    @properlyIncludesIntIvl.exec(@ctx).should.be.true
    @properlyIncludesIntBeginsIvl.exec(@ctx).should.be.true
    @properlyIncludesIntEndsIvl.exec(@ctx).should.be.true
    @properlyIncludesRealIvl.exec(@ctx).should.be.true
    @properlyIncludesDateIvl.exec(@ctx).should.be.true

  it 'should reject intervals not properly included', ->
    @notProperlyIncludesIntIvl.exec(@ctx).should.be.false
    @notProperlyIncludesRealIvl.exec(@ctx).should.be.false
    @notProperlyIncludesDateIvl.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (int)', ->
    @posInfEndProperlyIncludedInDateIvl.exec(@ctx).should.be.true
    @posInfEndNotProperlyIncludedInDateIvl.exec(@ctx).should.be.false
    should(@unknownEndMayBeProperlyIncludedInDateIvl.exec(@ctx)).be.null

describe 'After', ->
  @beforeEach ->
    setup @, data

  it 'should accept intervals before it', ->
    @afterIntIvl.exec(@ctx).should.be.true
    @afterRealIvl.exec(@ctx).should.be.true
    @afterDateIvl.exec(@ctx).should.be.true

  it 'should reject intervals on or after it', ->
    @notAfterIntIvl.exec(@ctx).should.be.false
    @notAfterRealIvl.exec(@ctx).should.be.false
    @notAfterDateIvl.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (int)', ->
    @negInfBegNotAfterIntIvl.exec(@ctx).should.be.false
    should(@unknownBegMayBeAfterIntIvl.exec(@ctx)).be.null
    @unknownBegNotAfterIntIvl.exec(@ctx).should.be.false
    @posInfEndAfterIntIvl.exec(@ctx).should.be.true
    @posInfEndNotAfterIntIvl.exec(@ctx).should.be.false
    @unknownEndAfterIntIvl.exec(@ctx).should.be.true
    @unknownEndNotAfterIntIvl.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (date)', ->
    @negInfBegNotAfterDateIvl.exec(@ctx).should.be.false
    should(@unknownBegMayBeAfterDateIvl.exec(@ctx)).be.null
    @unknownBegNotAfterDateIvl.exec(@ctx).should.be.false
    @posInfEndAfterDateIvl.exec(@ctx).should.be.true
    @posInfEndNotAfterDateIvl.exec(@ctx).should.be.false
    @unknownEndAfterDateIvl.exec(@ctx).should.be.true
    @unknownEndNotAfterDateIvl.exec(@ctx).should.be.false

  it 'should correctly handle imprecision', ->
    @afterImpreciseDateIvl.exec(@ctx).should.be.true
    @notAfterImpreciseDateIvl.exec(@ctx).should.be.false
    should(@mayBeAfterImpreciseDateIvl.exec(@ctx)).be.null
    @impreciseAfterDateIvl.exec(@ctx).should.be.true
    @impreciseNotAfterDateIvl.exec(@ctx).should.be.false
    should(@impreciseMayBeAfterDateIvl.exec(@ctx)).be.null

describe 'Before', ->
  @beforeEach ->
    setup @, data

  it 'should accept intervals before it', ->
    @beforeIntIvl.exec(@ctx).should.be.true
    @beforeRealIvl.exec(@ctx).should.be.true
    @beforeDateIvl.exec(@ctx).should.be.true

  it 'should reject intervals on or after it', ->
    @notBeforeIntIvl.exec(@ctx).should.be.false
    @notBeforeRealIvl.exec(@ctx).should.be.false
    @notBeforeDateIvl.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (int)', ->
    @negInfBegBeforeIntIvl.exec(@ctx).should.be.true
    @negInfBegNotBeforeIntIvl.exec(@ctx).should.be.false
    @unknownBegBeforeIntIvl.exec(@ctx).should.be.true
    @unknownBegNotBeforeIntIvl.exec(@ctx).should.be.false
    @posInfEndNotBeforeIntIvl.exec(@ctx).should.be.false
    should(@unknownEndMayBeBeforeIntIvl.exec(@ctx)).be.null
    @unknownEndNotBeforeIntIvl.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (date)', ->
    @negInfBegBeforeDateIvl.exec(@ctx).should.be.true
    @negInfBegNotBeforeDateIvl.exec(@ctx).should.be.false
    @unknownBegBeforeDateIvl.exec(@ctx).should.be.true
    @unknownBegNotBeforeDateIvl.exec(@ctx).should.be.false
    @posInfEndNotBeforeDateIvl.exec(@ctx).should.be.false
    should(@unknownEndMayBeBeforeDateIvl.exec(@ctx)).be.null
    @unknownEndNotBeforeDateIvl.exec(@ctx).should.be.false

  it 'should correctly handle imprecision', ->
    @beforeImpreciseDateIvl.exec(@ctx).should.be.true
    @notBeforeImpreciseDateIvl.exec(@ctx).should.be.false
    should(@mayBeBeforeImpreciseDateIvl.exec(@ctx)).be.null
    @impreciseBeforeDateIvl.exec(@ctx).should.be.true
    @impreciseNotBeforeDateIvl.exec(@ctx).should.be.false
    should(@impreciseMayBeBeforeDateIvl.exec(@ctx)).be.null

describe 'Meets', ->
  @beforeEach ->
    setup @, data

  it 'should accept intervals meeting after it', ->
    @meetsBeforeIntIvl.exec(@ctx).should.be.true
    @meetsBeforeRealIvl.exec(@ctx).should.be.true
    @meetsBeforeDateIvl.exec(@ctx).should.be.true

  it 'should accept intervals meeting before it', ->
    @meetsAfterIntIvl.exec(@ctx).should.be.true
    @meetsAfterRealIvl.exec(@ctx).should.be.true
    @meetsAfterDateIvl.exec(@ctx).should.be.true

  it 'should reject intervals not meeting it', ->
    @notMeetsIntIvl.exec(@ctx).should.be.false
    @notMeetsRealIvl.exec(@ctx).should.be.false
    @notMeetsDateIvl.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (int)', ->
    @negInfBegMeetsBeforeIntIvl.exec(@ctx).should.be.true
    @negInfBegNotMeetsIntIvl.exec(@ctx).should.be.false
    @intIvlNotMeetsNegInfBeg.exec(@ctx).should.be.false
    @unknownBegMeetsBeforeIntIvl.exec(@ctx).should.be.true
    should(@unknownBegMayMeetAfterIntIvl.exec(@ctx)).be.null
    @unknownBegNotMeetsIntIvl.exec(@ctx).should.be.false
    should(@intIvlMayMeetBeforeUnknownBeg.exec(@ctx)).be.null
    @posInfEndMeetsAfterIntIvl.exec(@ctx).should.be.true
    @posInfEndNotMeetsIntIvl.exec(@ctx).should.be.false
    @intIvlNotMeetsPosInfEnd.exec(@ctx).should.be.false
    @unknownEndMeetsAfterIntIvl.exec(@ctx).should.be.true
    should(@unknownEndMayMeetBeforeIntIvl.exec(@ctx)).be.null
    @unknownEndNotMeetsIntIvl.exec(@ctx).should.be.false
    should(@intIvlMayMeetAfterUnknownEnd.exec(@ctx)).be.null

  it 'should correctly handle null endpoints (date)', ->
    @negInfBegMeetsBeforeDateIvl.exec(@ctx).should.be.true
    @negInfBegNotMeetsDateIvl.exec(@ctx).should.be.false
    @dateIvlNotMeetsNegInfBeg.exec(@ctx).should.be.false
    @unknownBegMeetsBeforeDateIvl.exec(@ctx).should.be.true
    should(@unknownBegMayMeetAfterDateIvl.exec(@ctx)).be.null
    @unknownBegNotMeetsDateIvl.exec(@ctx).should.be.false
    should(@dateIvlMayMeetBeforeUnknownBeg.exec(@ctx)).be.null
    @posInfEndMeetsAfterDateIvl.exec(@ctx).should.be.true
    @posInfEndNotMeetsDateIvl.exec(@ctx).should.be.false
    @dateIvlNotMeetsPosInfEnd.exec(@ctx).should.be.false
    @unknownEndMeetsAfterDateIvl.exec(@ctx).should.be.true
    should(@unknownEndMayMeetBeforeDateIvl.exec(@ctx)).be.null
    @unknownEndNotMeetsDateIvl.exec(@ctx).should.be.false
    should(@dateIvlMayMeetAfterUnknownEnd.exec(@ctx)).be.null

  it 'should correctly handle imprecision', ->
    should(@mayMeetAfterImpreciseDateIvl.exec(@ctx)).be.null
    should(@mayMeetBeforeImpreciseDateIvl.exec(@ctx)).be.null
    @notMeetsImpreciseDateIvl.exec(@ctx).should.be.false
    should(@impreciseMayMeetAfterDateIvl.exec(@ctx)).be.null
    should(@impreciseMayMeetBeforeDateIvl.exec(@ctx)).be.null
    @impreciseNotMeetsDateIvl.exec(@ctx).should.be.false

describe 'MeetsAfter', ->
  @beforeEach ->
    setup @, data

  it 'should accept intervals meeting before it', ->
    @meetsAfterIntIvl.exec(@ctx).should.be.true
    @meetsAfterRealIvl.exec(@ctx).should.be.true
    @meetsAfterDateIvl.exec(@ctx).should.be.true

  it 'should reject intervals meeting after it', ->
    @meetsBeforeIntIvl.exec(@ctx).should.be.false
    @meetsBeforeRealIvl.exec(@ctx).should.be.false
    @meetsBeforeDateIvl.exec(@ctx).should.be.false

  it 'should reject intervals not meeting it', ->
    @notMeetsIntIvl.exec(@ctx).should.be.false
    @notMeetsRealIvl.exec(@ctx).should.be.false
    @notMeetsDateIvl.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (int)', ->
    @negInfBegMeetsBeforeIntIvl.exec(@ctx).should.be.false
    @negInfBegNotMeetsIntIvl.exec(@ctx).should.be.false
    @intIvlNotMeetsNegInfBeg.exec(@ctx).should.be.false
    @unknownBegMeetsBeforeIntIvl.exec(@ctx).should.be.false
    should(@unknownBegMayMeetAfterIntIvl.exec(@ctx)).be.null
    @unknownBegNotMeetsIntIvl.exec(@ctx).should.be.false
    @intIvlMayMeetBeforeUnknownBeg.exec(@ctx).should.be.false
    @posInfEndMeetsAfterIntIvl.exec(@ctx).should.be.true
    @posInfEndNotMeetsIntIvl.exec(@ctx).should.be.false
    @intIvlNotMeetsPosInfEnd.exec(@ctx).should.be.false
    @unknownEndMeetsAfterIntIvl.exec(@ctx).should.be.true
    @unknownEndMayMeetBeforeIntIvl.exec(@ctx).should.be.false
    @unknownEndNotMeetsIntIvl.exec(@ctx).should.be.false
    should(@intIvlMayMeetAfterUnknownEnd.exec(@ctx)).be.null

  it 'should correctly handle null endpoints (date)', ->
    @negInfBegMeetsBeforeDateIvl.exec(@ctx).should.be.false
    @negInfBegNotMeetsDateIvl.exec(@ctx).should.be.false
    @dateIvlNotMeetsNegInfBeg.exec(@ctx).should.be.false
    @unknownBegMeetsBeforeDateIvl.exec(@ctx).should.be.false
    should(@unknownBegMayMeetAfterDateIvl.exec(@ctx)).be.null
    @unknownBegNotMeetsDateIvl.exec(@ctx).should.be.false
    @dateIvlMayMeetBeforeUnknownBeg.exec(@ctx).should.be.false
    @posInfEndMeetsAfterDateIvl.exec(@ctx).should.be.true
    @posInfEndNotMeetsDateIvl.exec(@ctx).should.be.false
    @dateIvlNotMeetsPosInfEnd.exec(@ctx).should.be.false
    @unknownEndMeetsAfterDateIvl.exec(@ctx).should.be.true
    @unknownEndMayMeetBeforeDateIvl.exec(@ctx).should.be.false
    @unknownEndNotMeetsDateIvl.exec(@ctx).should.be.false
    should(@dateIvlMayMeetAfterUnknownEnd.exec(@ctx)).be.null

  it 'should correctly handle imprecision', ->
    should(@mayMeetAfterImpreciseDateIvl.exec(@ctx)).be.null
    @mayMeetBeforeImpreciseDateIvl.exec(@ctx).should.be.false
    @notMeetsImpreciseDateIvl.exec(@ctx).should.be.false
    should(@impreciseMayMeetAfterDateIvl.exec(@ctx)).be.null
    @impreciseMayMeetBeforeDateIvl.exec(@ctx).should.be.false
    @impreciseNotMeetsDateIvl.exec(@ctx).should.be.false

describe 'MeetsBefore', ->
  @beforeEach ->
    setup @, data

  it 'should accept intervals meeting after it', ->
    @meetsBeforeIntIvl.exec(@ctx).should.be.true
    @meetsBeforeRealIvl.exec(@ctx).should.be.true
    @meetsBeforeDateIvl.exec(@ctx).should.be.true

  it 'should reject intervals meeting before it', ->
    @meetsAfterIntIvl.exec(@ctx).should.be.false
    @meetsAfterRealIvl.exec(@ctx).should.be.false
    @meetsAfterDateIvl.exec(@ctx).should.be.false

  it 'should reject intervals not meeting it', ->
    @notMeetsIntIvl.exec(@ctx).should.be.false
    @notMeetsRealIvl.exec(@ctx).should.be.false
    @notMeetsDateIvl.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (int)', ->
    @negInfBegMeetsBeforeIntIvl.exec(@ctx).should.be.true
    @negInfBegNotMeetsIntIvl.exec(@ctx).should.be.false
    @intIvlNotMeetsNegInfBeg.exec(@ctx).should.be.false
    @unknownBegMeetsBeforeIntIvl.exec(@ctx).should.be.true
    @unknownBegMayMeetAfterIntIvl.exec(@ctx).should.be.false
    @unknownBegNotMeetsIntIvl.exec(@ctx).should.be.false
    should(@intIvlMayMeetBeforeUnknownBeg.exec(@ctx)).be.null
    @posInfEndMeetsAfterIntIvl.exec(@ctx).should.be.false
    @posInfEndNotMeetsIntIvl.exec(@ctx).should.be.false
    @intIvlNotMeetsPosInfEnd.exec(@ctx).should.be.false
    @unknownEndMeetsAfterIntIvl.exec(@ctx).should.be.false
    should(@unknownEndMayMeetBeforeIntIvl.exec(@ctx)).be.null
    @unknownEndNotMeetsIntIvl.exec(@ctx).should.be.false
    @intIvlMayMeetAfterUnknownEnd.exec(@ctx).should.be.false

  it 'should correctly handle null endpoints (date)', ->
    @negInfBegMeetsBeforeDateIvl.exec(@ctx).should.be.true
    @negInfBegNotMeetsDateIvl.exec(@ctx).should.be.false
    @dateIvlNotMeetsNegInfBeg.exec(@ctx).should.be.false
    @unknownBegMeetsBeforeDateIvl.exec(@ctx).should.be.true
    @unknownBegMayMeetAfterDateIvl.exec(@ctx).should.be.false
    @unknownBegNotMeetsDateIvl.exec(@ctx).should.be.false
    should(@dateIvlMayMeetBeforeUnknownBeg.exec(@ctx)).be.null
    @posInfEndMeetsAfterDateIvl.exec(@ctx).should.be.false
    @posInfEndNotMeetsDateIvl.exec(@ctx).should.be.false
    @dateIvlNotMeetsPosInfEnd.exec(@ctx).should.be.false
    @unknownEndMeetsAfterDateIvl.exec(@ctx).should.be.false
    should(@unknownEndMayMeetBeforeDateIvl.exec(@ctx)).be.null
    @unknownEndNotMeetsDateIvl.exec(@ctx).should.be.false
    @dateIvlMayMeetAfterUnknownEnd.exec(@ctx).should.be.false

  it 'should correctly handle imprecision', ->
    @mayMeetAfterImpreciseDateIvl.exec(@ctx).should.be.false
    should(@mayMeetBeforeImpreciseDateIvl.exec(@ctx)).be.null
    @notMeetsImpreciseDateIvl.exec(@ctx).should.be.false
    @impreciseMayMeetAfterDateIvl.exec(@ctx).should.be.false
    should(@impreciseMayMeetBeforeDateIvl.exec(@ctx)).be.null
    @impreciseNotMeetsDateIvl.exec(@ctx).should.be.false

describe 'Overlaps', ->
  @beforeEach ->
    setup @, data

  it 'should accept overlaps (integer)', ->
    @overlapsBeforeIntIvl.exec(@ctx).should.be.true
    @overlapsAfterIntIvl.exec(@ctx).should.be.true
    @overlapsBoundaryIntIvl.exec(@ctx).should.be.true

  it 'should accept overlaps (real)', ->
    @overlapsBeforeRealIvl.exec(@ctx).should.be.true
    @overlapsAfterRealIvl.exec(@ctx).should.be.true
    @overlapsBoundaryRealIvl.exec(@ctx).should.be.true

  it 'should reject non-overlaps (integer)', ->
    @noOverlapsIntIvl.exec(@ctx).should.be.false

  it 'should reject non-overlaps (real)', ->
    @noOverlapsRealIvl.exec(@ctx).should.be.false

describe 'OverlapsDateTime', ->
  @beforeEach ->
    setup @, data

  it 'should accept overlaps', ->
    @overlapsBefore.exec(@ctx).should.be.true
    @overlapsAfter.exec(@ctx).should.be.true
    @overlapsContained.exec(@ctx).should.be.true
    @overlapsContains.exec(@ctx).should.be.true

  it 'should accept imprecise overlaps', ->
    @impreciseOverlap.exec(@ctx).should.be.true

  it 'should reject non-overlaps', ->
    @noOverlap.exec(@ctx).should.be.false

  it 'should reject imprecise non-overlaps', ->
    @noImpreciseOverlap.exec(@ctx).should.be.false

  it 'should return null for imprecise overlaps that are unknown', ->
    should(@unknownOverlap.exec(@ctx)).be.null

describe 'OverlapsAfter', ->
  @beforeEach ->
    setup @, data

  it 'should accept overlaps that are after (integer)', ->
    @overlapsAfterIntIvl.exec(@ctx).should.be.true
    @overlapsBoundaryIntIvl.exec(@ctx).should.be.true

  it 'should accept overlaps that are after (real)', ->
    @overlapsAfterRealIvl.exec(@ctx).should.be.true
    @overlapsBoundaryRealIvl.exec(@ctx).should.be.true

  it 'should reject overlaps that are before (integer)', ->
    @overlapsBeforeIntIvl.exec(@ctx).should.be.false

  it 'should reject overlaps that are before (real)', ->
    @overlapsBeforeRealIvl.exec(@ctx).should.be.false

  it 'should reject non-overlaps (integer)', ->
    @noOverlapsIntIvl.exec(@ctx).should.be.false

  it 'should reject non-overlaps (real)', ->
    @noOverlapsRealIvl.exec(@ctx).should.be.false

describe 'OverlapsAfterDateTime', ->
  @beforeEach ->
    setup @, data

  it 'should accept overlaps that are after', ->
    @overlapsAfter.exec(@ctx).should.be.true
    @overlapsContains.exec(@ctx).should.be.true

  it 'should accept imprecise overlaps that are after', ->
    @impreciseOverlapAfter.exec(@ctx).should.be.true

  it 'should reject overlaps that are not before', ->
    @overlapsBefore.exec(@ctx).should.be.false
    @overlapsContained.exec(@ctx).should.be.false

  it 'should reject imprecise overlaps that are not before', ->
    @impreciseOverlapBefore.exec(@ctx).should.be.false

  it 'should reject non-overlaps', ->
    @noOverlap.exec(@ctx).should.be.false

  it 'should reject imprecise non-overlaps', ->
    @noImpreciseOverlap.exec(@ctx).should.be.false

  it 'should return null for imprecise overlaps that are unknown', ->
    should(@unknownOverlap.exec(@ctx)).be.null

describe 'OverlapsBefore', ->
  @beforeEach ->
    setup @, data

  it 'should accept overlaps that are before (integer)', ->
    @overlapsBeforeIntIvl.exec(@ctx).should.be.true
    @overlapsBoundaryIntIvl.exec(@ctx).should.be.true

  it 'should accept overlaps that are before (real)', ->
    @overlapsBeforeRealIvl.exec(@ctx).should.be.true
    @overlapsBoundaryRealIvl.exec(@ctx).should.be.true

  it 'should reject overlaps that are after (integer)', ->
    @overlapsAfterIntIvl.exec(@ctx).should.be.false

  it 'should reject overlaps that are after (real)', ->
    @overlapsAfterRealIvl.exec(@ctx).should.be.false

  it 'should reject non-overlaps (integer)', ->
    @noOverlapsIntIvl.exec(@ctx).should.be.false

  it 'should reject non-overlaps (real)', ->
    @noOverlapsRealIvl.exec(@ctx).should.be.false

describe 'OverlapsBeforeDateTime', ->
  @beforeEach ->
    setup @, data

  it 'should accept overlaps that are before', ->
    @overlapsBefore.exec(@ctx).should.be.true
    @overlapsContains.exec(@ctx).should.be.true

  it 'should accept imprecise overlaps that are before', ->
    @impreciseOverlapBefore.exec(@ctx).should.be.true

  it 'should reject overlaps that are not before', ->
    @overlapsAfter.exec(@ctx).should.be.false
    @overlapsContained.exec(@ctx).should.be.false

  it 'should reject imprecise overlaps that are not before', ->
    @impreciseOverlapAfter.exec(@ctx).should.be.false

  it 'should reject non-overlaps', ->
    @noOverlap.exec(@ctx).should.be.false

  it 'should reject imprecise non-overlaps', ->
    @noImpreciseOverlap.exec(@ctx).should.be.false

  it 'should return null for imprecise overlaps that are unknown', ->
    should(@unknownOverlap.exec(@ctx)).be.null

describe 'Width', ->
  @beforeEach ->
    setup @, data

  it 'should calculate the width of integer intervals', ->
    @intWidth.exec(@ctx).should.equal 7
    @intOpenWidth.exec(@ctx).should.equal 5

  it 'should calculate the width of real intervals', ->
    @realWidth.exec(@ctx).should.equal 3.33
    @realOpenWidth.exec(@ctx).should.equal 3.32999998

  it 'should calculate the width of infinite intervals', ->
    @intWidthThreeToMax.exec(@ctx).should.equal Math.pow(2,31)-4
    @intWidthMinToThree.exec(@ctx).should.equal Math.pow(2,31)+3

  it 'should calculate the width of infinite intervals', ->
    should(@intWidthThreeToUnknown.exec(@ctx)).be.null
    should(@intWidthUnknownToThree.exec(@ctx)).be.null

describe 'Start', ->
  @beforeEach ->
    setup @, data

  it 'should execute as the start of the interval', ->
    @foo.exec(@ctx).should.eql new DateTime(2012, 1, 1)

describe 'End', ->
  @beforeEach ->
    setup @, data

  it 'should execute as the end of the interval', ->
    @foo.exec(@ctx).should.eql new DateTime(2013, 1, 1)

describe 'IntegerIntervalUnion', ->
  @beforeEach ->
    setup @, data

  it 'should properly calculate open and closed unions', ->
    x = @intFullInterval.exec(@ctx)
    y = @intClosedUnionClosed.exec(@ctx)
    y.equals(x).should.be.true

    y = @intClosedUnionOpen.exec(@ctx)
    y.contains(0).should.be.true
    y.contains(10).should.be.false

    y = @intOpenUnionOpen.exec(@ctx)
    y.contains(0).should.be.false
    y.contains(10).should.be.false

    y = @intOpenUnionClosed.exec(@ctx)
    y.contains(0).should.be.false
    y.contains(10).should.be.true

  it 'should properly calculate sameAs unions', ->
    x = @intFullInterval.exec(@ctx)
    y = @intSameAsUnion.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate before/after unions', ->
    should(@intBeforeUnion.exec(@ctx)).be.null

  it 'should properly calculate meets unions', ->
    x = @intFullInterval.exec(@ctx)
    y = @intMeetsUnion.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate left/right overlapping unions', ->
    x = @intFullInterval.exec(@ctx)
    y = @intOverlapsUnion.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate begins/begun by unions', ->
    x = @intFullInterval.exec(@ctx)
    y = @intBeginsUnion.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate includes/included by unions', ->
    x = @intFullInterval.exec(@ctx)
    y = @intDuringUnion.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate ends/ended by unions', ->
    x = @intFullInterval.exec(@ctx)
    y = @intEndsUnion.exec(@ctx)
    y.equals(x).should.be.true

# TODO
# it 'should properly handle imprecision', ->

describe 'DateTimeIntervalUnion', ->
  @beforeEach ->
    setup @, data

  it 'should properly calculate open and closed unions', ->
    x = @dateTimeFullInterval.exec(@ctx)
    y = @dateTimeClosedUnionClosed.exec(@ctx)
    y.equals(x).should.be.true

    a = new DateTime(2012, 1, 1, 0, 0, 0, 0)
    b = new DateTime(2013, 1, 1, 0, 0, 0, 0)

    y = @dateTimeClosedUnionOpen.exec(@ctx)
    y.contains(a).should.be.true
    y.contains(b).should.be.false

    y = @dateTimeOpenUnionOpen.exec(@ctx)
    y.contains(a).should.be.false
    y.contains(b).should.be.false

    y = @dateTimeOpenUnionClosed.exec(@ctx)
    y.contains(a).should.be.false
    y.contains(b).should.be.true

  it 'should properly calculate sameAs unions', ->
    x = @dateTimeFullInterval.exec(@ctx)
    y = @dateTimeSameAsUnion.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate before/after unions', ->
    should(@dateTimeBeforeUnion.exec(@ctx)).be.null

  it 'should properly calculate meets unions', ->
    x = @dateTimeFullInterval.exec(@ctx)
    y = @dateTimeMeetsUnion.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate left/right overlapping unions', ->
    x = @dateTimeFullInterval.exec(@ctx)
    y = @dateTimeOverlapsUnion.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate begins/begun by unions', ->
    x = @dateTimeFullInterval.exec(@ctx)
    y = @dateTimeBeginsUnion.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate includes/included by unions', ->
    x = @dateTimeFullInterval.exec(@ctx)
    y = @dateTimeDuringUnion.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate ends/ended by unions', ->
    x = @dateTimeFullInterval.exec(@ctx)
    y = @dateTimeEndsUnion.exec(@ctx)
    y.equals(x).should.be.true

# TODO
# it 'should properly handle imprecision', ->

describe 'IntegerIntervalExcept', ->
  @beforeEach ->
    setup @, data

   it 'should properly calculate sameAs except', ->
    should(@intSameAsExcept.exec(@ctx)).be.null

  it 'should properly calculate before/after except', ->
    @intBeforeExcept.exec(@ctx).should.eql new Interval(0,4)

  it 'should properly calculate meets except', ->
    x = @intHalfInterval.exec(@ctx)
    y = @intMeetsExcept.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate left/right overlapping except', ->
    x = @intHalfInterval.exec(@ctx)
    y = @intOverlapsExcept.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate begins/begun by except', ->
    should(@intBeginsExcept.exec(@ctx)).be.null

  it 'should properly calculate includes/included by except', ->
    should(@intDuringExcept.exec(@ctx)).be.null

  it 'should properly calculate ends/ended by except', ->
    should(@intEndsExcept.exec(@ctx)).be.null

# TODO
# it 'should properly handle imprecision', ->

describe 'DateTimeIntervalExcept', ->
  @beforeEach ->
    setup @, data

  it 'should properly calculate sameAs except', ->
    should(@dateTimeSameAsExcept.exec(@ctx)).be.null

  it 'should properly calculate before/after except', ->
    @dateTimeBeforeExcept.exec(@ctx).should.eql new Interval(new DateTime(2012, 1, 1, 0, 0, 0, 0), new DateTime(2012, 4, 1, 0, 0, 0, 0))

  it 'should properly calculate meets except', ->
    x = @dateTimeHalfInterval.exec(@ctx)
    y = @dateTimeMeetsExcept.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate left/right overlapping except', ->
    x = @dateTimeHalfInterval.exec(@ctx)
    y = @dateTimeOverlapsExcept.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate begins/begun by except', ->
    should(@dateTimeBeginsExcept.exec(@ctx)).be.null

  it 'should properly calculate includes/included by except', ->
    should(@dateTimeDuringExcept.exec(@ctx)).be.null

  it 'should properly calculate ends/ended by except', ->
    should(@dateTimeEndsExcept.exec(@ctx)).be.null

# TODO
# it 'should properly handle imprecision', ->

describe 'IntegerIntervalIntersect', ->
  @beforeEach ->
    setup @, data

   it 'should properly calculate sameAs intersect', ->
    x = @intSameAsIntersect.exec(@ctx)
    y = @intFullInterval.exec(@ctx)
    x.equals(y).should.be.true

  it 'should properly calculate before/after intersect', ->
    should(@intBeforeIntersect.exec(@ctx)).be.null

  it 'should properly calculate meets intersect', ->
    x = @intMeetsInterval.exec(@ctx)
    y = @intMeetsIntersect.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate left/right overlapping intersect', ->
    x = @intOverlapsInterval.exec(@ctx)
    y = @intOverlapsIntersect.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate begins/begun by intersect', ->
    x = @intBeginsInterval.exec(@ctx)
    y = @intBeginsIntersect.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate includes/included by intersect', ->
    x = @intDuringInterval.exec(@ctx)
    y = @intDuringIntersect.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate ends/ended by intersect', ->
    x = @intEndsInterval.exec(@ctx)
    y = @intEndsIntersect.exec(@ctx)
    y.equals(x).should.be.true

describe 'DateTimeIntervalIntersect', ->
  @beforeEach ->
    setup @, data

   it 'should properly calculate sameAs intersect', ->
    x = @dateTimeSameAsIntersect.exec(@ctx)
    y = @dateTimeFullInterval.exec(@ctx)
    x.equals(y).should.be.true

  it 'should properly calculate before/after intersect', ->
    should(@dateTimeBeforeIntersect.exec(@ctx)).be.null

  it 'should properly calculate meets intersect', ->
    x = @dateTimeMeetsInterval.exec(@ctx)
    y = @dateTimeMeetsIntersect.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate left/right overlapping intersect', ->
    x = @dateTimeOverlapsInterval.exec(@ctx)
    y = @dateTimeOverlapsIntersect.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate begins/begun by intersect', ->
    x = @dateTimeBeginsInterval.exec(@ctx)
    y = @dateTimeBeginsIntersect.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate includes/included by intersect', ->
    x = @dateTimeDuringInterval.exec(@ctx)
    y = @dateTimeDuringIntersect.exec(@ctx)
    y.equals(x).should.be.true

  it 'should properly calculate ends/ended by intersect', ->
    x = @dateTimeEndsInterval.exec(@ctx)
    y = @dateTimeEndsIntersect.exec(@ctx)
    y.equals(x).should.be.true
