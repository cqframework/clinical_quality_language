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

  properlyIncludes: (other) ->
    if not (other instanceof Interval) then throw new Error("Argument to properlyIncludes must be an interval")
    a = @toClosed()
    b = other.toClosed()
    ThreeValuedLogic.and(
      cmp.lessThan(a.low, b.low),
      cmp.greaterThan(a.high, b.high)
    )

  includes: (other) ->
    if not (other instanceof Interval) then throw new Error("Argument to includes must be an interval")
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

  areDateTimes = (x, y) ->
    [x, y].every (z) -> z instanceof DateTime

  areNumeric = (x, y) ->
    [x, y].every (z) -> typeof z is 'number' or (z instanceof Uncertainty and typeof z.low is 'number')

  lowestNumericUncertainty = (x, y) ->
    if not(x instanceof Uncertainty) then x = new Uncertainty(x)
    if not(y instanceof Uncertainty) then y = new Uncertainty(y)
    low = if x.low < y.low then x.low else y.low
    high = if x.high < y.high then x.high else y.high
    if low != high then return new Uncertainty(low, high) else return low

  highestNumericUncertainty = (x, y) ->
    if not(x instanceof Uncertainty) then x = new Uncertainty(x)
    if not(y instanceof Uncertainty) then y = new Uncertainty(y)
    low = if x.low > y.low then x.low else y.low
    high = if x.high > y.high then x.high else y.high
    if low != high then return new Uncertainty(low, high) else return low

  union: (other) ->
    if not (other instanceof Interval) then throw new Error("Argument to union must be an interval")
    # Note that interval union is only defined if the arguments overlap or meet.
    if @overlaps(other) or @meets(other)
      [a, b] = [@toClosed(), other.toClosed()]
      [l, lc] = switch
        when cmp.lessThanOrEquals(a.low, b.low) then [@low, @lowClosed]
        when cmp.greaterThanOrEquals(a.low, b.low) then [other.low, other.lowClosed]
        when areNumeric(a.low, b.low) then [lowestNumericUncertainty(a.low, b.low), true]
        when areDateTimes(a.low, b.low) and a.low.isMorePrecise(b.low) then [other.low, other.lowClosed]
        else [@low, @lowClosed]
      [h, hc] = switch
        when cmp.greaterThanOrEquals(a.high, b.high) then [@high, @highClosed]
        when cmp.lessThanOrEquals(a.high, b.high) then [other.high, other.highClosed]
        when areNumeric(a.low, b.low) then [highestNumericUncertainty(a.high, b.high), true]
        when areDateTimes(a.high, b.high) and a.high.isMorePrecise(b.high) then [other.high, other.highClosed]
        else [@high, @highClosed]
      new Interval(l, h, lc, hc)
    else
      null

  intersect: (other) ->
    if not (other instanceof Interval) then throw new Error("Argument to union must be an interval")
    # Note that interval union is only defined if the arguments overlap.
    if @overlaps(other)
      [a, b] = [@toClosed(), other.toClosed()]
      [l, lc] = switch
        when cmp.greaterThanOrEquals(a.low, b.low) then [@low, @lowClosed]
        when cmp.lessThanOrEquals(a.low, b.low) then [other.low, other.lowClosed]
        when areNumeric(a.low, b.low) then [highestNumericUncertainty(a.low, b.low), true]
        when areDateTimes(a.low, b.low) and b.low.isMorePrecise(a.low) then [other.low, other.lowClosed]
        else [@low, @lowClosed]
      [h, hc] = switch
        when cmp.lessThanOrEquals(a.high, b.high) then [@high, @highClosed]
        when cmp.greaterThanOrEquals(a.high, b.high) then [other.high, other.highClosed]
        when areNumeric(a.low, b.low) then [lowestNumericUncertainty(a.high, b.high), true]
        when areDateTimes(a.high, b.high) and b.high.isMorePrecise(a.high) then [other.high, other.highClosed]
        else [@high, @highClosed]
      new Interval(l, h, lc, hc)
    else
      null

  except: (other) ->
    if (other == null) then return null
    if not (other instanceof Interval) then throw new Error("Argument to except must be an interval")

    if @properlyIncludes(other) is false
      if @overlapsBefore(other)
        new Interval(@low, other.low, @lowClosed, not other.lowClosed)
      else if @overlapsAfter(other)
        new Interval(other.high, @high, not other.highClosed, @highClosed)
      else
        null
    else if @overlaps(other) is false
      null # todo: fix bug in spec
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
