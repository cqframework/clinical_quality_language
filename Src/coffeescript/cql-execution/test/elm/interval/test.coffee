should = require 'should'
setup = require '../../setup'
data = require './data'
{ Interval } = require '../../../lib/datatypes/interval'

describe 'Interval', ->
  @beforeEach ->
    setup @, data

  it 'should properly represent an open interval', ->
    @open.lowClosed.should.be.false
    @open.highClosed.should.be.false
    @open.low.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    @open.high.exec(@ctx).toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

  it 'should properly represent a left-open interval', ->
    @leftOpen.lowClosed.should.be.false
    @leftOpen.highClosed.should.be.true
    @leftOpen.low.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    @leftOpen.high.exec(@ctx).toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

  it 'should properly represent a right-open interval', ->
    @rightOpen.lowClosed.should.be.true
    @rightOpen.highClosed.should.be.false
    @rightOpen.low.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    @rightOpen.high.exec(@ctx).toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

  it 'should properly represent a closed interval', ->
    @closed.lowClosed.should.be.true
    @closed.highClosed.should.be.true
    @closed.low.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    @closed.high.exec(@ctx).toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

  it 'should exec to native Interval datatype', ->
    ivl = @open.exec(@cql)
    ivl.should.be.instanceOf Interval
    ivl.lowClosed.should.equal @open.lowClosed
    ivl.highClosed.should.equal @open.highClosed
    ivl.low.toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    ivl.high.toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

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

describe 'Overlaps', ->
  @beforeEach ->
    setup @, data

  it 'should accept overlaps (integer)', ->
    @overlapsBeforeIntIvl.exec(@ctx).should.be.true
    @overlapsAfterIntIvl.exec(@ctx).should.be.true
    @overlapsBoundaryIntIvl.exec(@ctx).should.be.true
    @startOverlapsInt.exec(@ctx).should.be.true
    @endOverlapsInt.exec(@ctx).should.be.true

  it 'should accept overlaps (real)', ->
    @overlapsBeforeRealIvl.exec(@ctx).should.be.true
    @overlapsAfterRealIvl.exec(@ctx).should.be.true
    @overlapsBoundaryRealIvl.exec(@ctx).should.be.true
    @startOverlapsReal.exec(@ctx).should.be.true
    @endOverlapsReal.exec(@ctx).should.be.true

  it 'should reject non-overlaps (integer)', ->
    @noOverlapsIntIvl.exec(@ctx).should.be.false
    @noOverlapsInt.exec(@ctx).should.be.false

  it 'should reject non-overlaps (real)', ->
    @noOverlapsRealIvl.exec(@ctx).should.be.false
    @noOverlapsReal.exec(@ctx).should.be.false


describe.skip 'OverlapsDateTime', ->
  @beforeEach ->
    setup @, data

  it 'should accept overlaps', ->
    @overlapsBefore.exec(@ctx).should.be.true
    @overlapsAfter.exec(@ctx).should.be.true
    @overlapsContained.exec(@ctx).should.be.true
    @overlapsContains.exec(@ctx).should.be.true
    @overlapsDate.exec(@ctx).should.be.true
    @startOverlapsDate.exec(@ctx).should.be.true
    @endOverlapsDate.exec(@ctx).should.be.true

  it 'should accept imprecise overlaps', ->
    @impreciseOverlap.exec(@ctx).should.be.true

  it.skip 'should reject non-overlaps', ->
    @noOverlap.exec(@ctx).should.be.false
    @noOverlapsDate.exec(@ctx).should.be.false

  it 'should reject imprecise non-overlaps', ->
    @noImpreciseOverlap.exec(@ctx).should.be.false

  it 'should return null for imprecise overlaps that are unknown', ->
    should(@unknownOverlap.exec(@ctx)).be.null
    should(@unknownOverlapsDate.exec(@ctx)).be.null
    should(@overlapsUnknownDate.exec(@ctx)).be.null

describe.skip 'OverlapsAfter', ->
  @beforeEach ->
    setup @, data

  it 'should accept overlaps that are after (integer)', ->
    @overlapsAfterIntIvl.exec(@ctx).should.be.true
    @overlapsBoundaryIntIvl.exec(@ctx).should.be.true
    @startOverlapsInt.exec(@ctx).should.be.true

  it 'should accept overlaps that are after (real)', ->
    @overlapsAfterRealIvl.exec(@ctx).should.be.true
    @overlapsBoundaryRealIvl.exec(@ctx).should.be.true
    @startOverlapsReal.exec(@ctx).should.be.true

  it 'should reject overlaps that are before (integer)', ->
    @overlapsBeforeIntIvl.exec(@ctx).should.be.false
    @endOverlapsInt.exec(@ctx).should.be.false

  it 'should reject overlaps that are before (real)', ->
    @overlapsBeforeRealIvl.exec(@ctx).should.be.false
    @endOverlapsReal.exec(@ctx).should.be.false

  it 'should reject non-overlaps (integer)', ->
    @noOverlapsIntIvl.exec(@ctx).should.be.false
    @noOverlapsInt.exec(@ctx).should.be.false

  it 'should reject non-overlaps (real)', ->
    @noOverlapsRealIvl.exec(@ctx).should.be.false
    @noOverlapsReal.exec(@ctx).should.be.false

describe.skip 'OverlapsAfterDateTime', ->
  @beforeEach ->
    setup @, data

  it 'should accept overlaps that are after', ->
    @overlapsAfter.exec(@ctx).should.be.true
    @overlapsContains.exec(@ctx).should.be.true
    @overlapsDate.exec(@ctx).should.be.true
    @startOverlapsDate.exec(@ctx).should.be.true

  it 'should accept imprecise overlaps that are after', ->
    @impreciseOverlapAfter.exec(@ctx).should.be.true

  it.skip 'should reject overlaps that are not before', ->
    @overlapsBefore.exec(@ctx).should.be.false
    @overlapsContained.exec(@ctx).should.be.false
    @endOverlapsDate.exec(@ctx).should.be.false

  it 'should reject imprecise overlaps that are not before', ->
    @impreciseOverlapBefore.exec(@ctx).should.be.false

  it.skip 'should reject non-overlaps', ->
    @noOverlap.exec(@ctx).should.be.false
    @noOverlapsDate.exec(@ctx).should.be.false

  it 'should reject imprecise non-overlaps', ->
    @noImpreciseOverlap.exec(@ctx).should.be.false

  it 'should return null for imprecise overlaps that are unknown', ->
    should(@unknownOverlap.exec(@ctx)).be.null
    should(@unknownOverlapsDate.exec(@ctx)).be.null
    should(@overlapsUnknownDate.exec(@ctx)).be.null

describe.skip 'OverlapsBefore', ->
  @beforeEach ->
    setup @, data

  it 'should accept overlaps that are before (integer)', ->
    @overlapsBeforeIntIvl.exec(@ctx).should.be.true
    @overlapsBoundaryIntIvl.exec(@ctx).should.be.true
    @endOverlapsInt.exec(@ctx).should.be.true

  it 'should accept overlaps that are before (real)', ->
    @overlapsBeforeRealIvl.exec(@ctx).should.be.true
    @overlapsBoundaryRealIvl.exec(@ctx).should.be.true
    @endOverlapsReal.exec(@ctx).should.be.true

  it 'should reject overlaps that are after (integer)', ->
    @overlapsAfterIntIvl.exec(@ctx).should.be.false
    @startOverlapsInt.exec(@ctx).should.be.false

  it 'should reject overlaps that are after (real)', ->
    @overlapsAfterRealIvl.exec(@ctx).should.be.false
    @startOverlapsReal.exec(@ctx).should.be.false

  it 'should reject non-overlaps (integer)', ->
    @noOverlapsIntIvl.exec(@ctx).should.be.false
    @noOverlapsInt.exec(@ctx).should.be.false

  it 'should reject non-overlaps (real)', ->
    @noOverlapsRealIvl.exec(@ctx).should.be.false
    @noOverlapsReal.exec(@ctx).should.be.false

describe.skip 'OverlapsBeforeDateTime', ->
  @beforeEach ->
    setup @, data

  it 'should accept overlaps that are before', ->
    @overlapsBefore.exec(@ctx).should.be.true
    @overlapsContains.exec(@ctx).should.be.true
    @overlapsDate.exec(@ctx).should.be.true
    @endOverlapsDate.exec(@ctx).should.be.true

  it 'should accept imprecise overlaps that are before', ->
    @impreciseOverlapBefore.exec(@ctx).should.be.true

  it.skip 'should reject overlaps that are not before', ->
    @overlapsAfter.exec(@ctx).should.be.false
    @overlapsContained.exec(@ctx).should.be.false
    @startOverlapsDate.exec(@ctx).should.be.false

  it 'should reject imprecise overlaps that are not before', ->
    @impreciseOverlapAfter.exec(@ctx).should.be.false

  it.skip 'should reject non-overlaps', ->
    @noOverlap.exec(@ctx).should.be.false
    @noOverlapsDate.exec(@ctx).should.be.false

  it 'should reject imprecise non-overlaps', ->
    @noImpreciseOverlap.exec(@ctx).should.be.false

  it 'should return null for imprecise overlaps that are unknown', ->
    should(@unknownOverlap.exec(@ctx)).be.null
    should(@unknownOverlapsDate.exec(@ctx)).be.null
    should(@overlapsUnknownDate.exec(@ctx)).be.null

describe 'Width', ->
  @beforeEach ->
    setup @, data

  it 'should calculate the width of integer intervals', ->
    @intWidth.exec(@ctx).should.equal 7
    @intOpenWidth.exec(@ctx).should.equal 5

  it 'should calculate the width of real intervals', ->
    @realWidth.exec(@ctx).should.equal 3.33
    @realOpenWidth.exec(@ctx).should.equal 3.32999998

  it 'should calculate the width of date intervals', ->
    # TODO: Confirm this, support uncertainties if necessary
    @dateTimeWidth.exec(@ctx).should.equal 691200000
    @dateTimeOpenWidth.exec(@ctx).should.equal 691199998

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
    @foo.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
