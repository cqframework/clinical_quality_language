{ DateTime } = require './datetime'
{ Uncertainty } = require './uncertainty'
{ ThreeValuedLogic } = require './logic'
{ successor, predecessor, maxValueForInstance, minValueForInstance } = require '../util/math'
cmp = require '../util/comparison'

module.exports.Interval = class Interval
  constructor: (@low, @high, @lowClosed = true, @highClosed = true) ->

  contains: (item) ->
    if item instanceof Interval then throw new Error("Argument to contains must be a point")
    closed = @toClosed()
    ThreeValuedLogic.and(
      cmp.lessThanOrEquals(closed.low, item),
      cmp.greaterThanOrEquals(closed.high, item)
    )

  includes: (other) ->
    if not (other instanceof Interval) then throw new Error("Argument to includedIn must be an interval")
    a = @toClosed()
    b = other.toClosed()
    ThreeValuedLogic.and(
      cmp.lessThanOrEquals(a.low, b.low),
      cmp.greaterThanOrEquals(a.high, b.high)
    )

  includedIn: (other) ->
    if not (other instanceof Interval) then throw new Error("Argument to includedIn must be an interval")
    other.includes @

  overlaps: (item) ->
    closed = @toClosed()
    [low, high] = if item instanceof Interval
      itemClosed = item.toClosed()
      [itemClosed.low, itemClosed.high]
    else
      [item, item]
    ThreeValuedLogic.and(
      cmp.lessThanOrEquals(closed.low, high),
      cmp.greaterThanOrEquals(closed.high, low)
    )

  overlapsAfter: (item) ->
    closed = @toClosed()
    high = if item instanceof Interval then item.toClosed().high else item
    ThreeValuedLogic.and(
      cmp.lessThanOrEquals(closed.low, high),
      cmp.greaterThan(closed.high, high)
    )

  overlapsBefore: (item) ->
    closed = @toClosed()
    low = if item instanceof Interval then item.toClosed().low else item
    ThreeValuedLogic.and(
      cmp.lessThan(closed.low, low),
      cmp.greaterThanOrEquals(closed.high, low)
    )
    
  areDateTimes = (a, b) ->
    a instanceof DateTime and b instanceof DateTime

  isNumber = (x) ->
    typeof x is 'number'

  isUncertainty = (x) ->
    x instanceof Uncertainty

  isNumberAndUncertainty = (a, b) ->
    ( (isUncertainty(a) and isNumber(b)) || (isUncertainty(b) and isNumber(a)) )
    
  union: (other) ->
    if not (other instanceof Interval) then throw new Error("Argument to union must be an interval")
    # Note that interval union is only defined if the arguments overlap or meet.
    # TODO - what is the resulting union of DateTime intervals, where the lower/higher bounds are the
    # same DateTime with different levels of precision? Which level of precision is used?
    if @overlaps(other) or @meets(other)
      [a, b] = [@toClosed(), other.toClosed()]
      [l, lc] = switch
        when cmp.equals(a.low, b.low) then [other.low, (other.lowClosed || @lowClosed)]
        when cmp.greaterThan(a.low, b.low) then [other.low, other.lowClosed]
        when (isNumberAndUncertainty(a.low, b.low) and cmp.lessThan(a.low, b.low)==false) then [other.low, other.lowClosed]
        when areDateTimes(a.low, b.low) and a.low.isMorePrecise(b.low) then [other.low, (@lowClosed || other.lowClosed)]
        else [@low, @lowClosed]
      [h, hc] = switch
        when cmp.equals(a.high, b.high) then [other.high, (other.highClosed || @highClosed)]
        when cmp.lessThan(a.high, b.high) then [other.high, other.highClosed]
        when (isNumberAndUncertainty(a.high, b.high) and cmp.greaterThan(a.high, b.high)==false) then [other.high, other.highClosed]
        when areDateTimes(a.high, b.high) and a.high.isMorePrecise(b.high) then [other.high, (@highClosed || other.highClosed)]
        else [@high, @highClosed]
      new Interval(l, h, lc, hc)
    else
      null

  equals: (other) ->
    if other instanceof Interval
      [a, b] = [@toClosed(), other.toClosed()]
      ThreeValuedLogic.and(
        cmp.equals(a.low, b.low),
        cmp.equals(a.high, b.high)
      )
    else
      false

  after: (other) ->
    closed = @toClosed()
    otherClosed = other.toClosed()
    # Meets spec, but not 100% correct (e.g., (null, 5] after [6, 10] --> null)
    # Simple way to fix it: and w/ not overlaps
    cmp.greaterThan closed.low, otherClosed.high

  before: (other) ->
    closed = @toClosed()
    otherClosed = other.toClosed()
    # Meets spec, but not 100% correct (e.g., (null, 5] after [6, 10] --> null)
    # Simple way to fix it: and w/ not overlaps
    cmp.lessThan closed.high, otherClosed.low

  meets: (other) ->
    ThreeValuedLogic.or(
      @meetsBefore(other),
      @meetsAfter(other)
    )

  meetsAfter: (other) ->
    try
      cmp.equals @toClosed().low, successor(other.toClosed().high)
    catch
      false

  meetsBefore: (other) ->
    try
      cmp.equals @toClosed().high, predecessor(other.toClosed().low)
    catch
      false

  width: () ->
    closed = @toClosed()
    if closed.low instanceof Uncertainty or closed.high instanceof Uncertainty
      null
    else if closed.low instanceof DateTime
      # TODO: Handle uncertainties
      Math.abs(closed.low.durationBetween(closed.high, DateTime.Unit.MILLISECOND).low)
    else
      # TODO: Fix precision to 8 decimals in other places that return numbers
      diff = Math.abs(closed.high - closed.low)
      Math.round(diff * Math.pow(10, 8)) / Math.pow(10, 8)


  toClosed: () ->
    point = @low ? @high
    if typeof(point) is 'number' or point instanceof DateTime
      low = switch
        when @lowClosed and not @low? then minValueForInstance point
        when not @lowClosed and @low? then successor @low
        else @low
      high = switch
        when @highClosed and not @high? then maxValueForInstance point
        when not @highClosed and @high? then predecessor @high
        else @high
      if not low? then low = new Uncertainty(minValueForInstance(point), high)
      if not high? then high = new Uncertainty(low, maxValueForInstance(point))
      new Interval(low, high, true, true)
    else
      new Interval(@low, @high, true, true)
