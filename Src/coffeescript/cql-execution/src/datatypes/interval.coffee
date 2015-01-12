{ DateTime } = require './datetime'
{ Uncertainty } = require './uncertainty'
{ ThreeValuedLogic } = require './logic'
{ successor, predecessor, maxValueForInstance, minValueForInstance } = require '../util/math'
cmp = require '../util/comparison'

module.exports.Interval = class Interval
  # WARNING: If point is an uncertainty or imprecise date, the interval can be
  # interpreted as having width when it shouldn't.  Use with caution!
  @_fromPoint: (point) -> new Interval(point, point, true, true)

  constructor: (@low, @high, @lowClosed = true, @highClosed = true) ->

  includes: (item) ->
    a = @toClosed()
    b = if item instanceof Interval then item.toClosed() else Interval._fromPoint(item)
    ThreeValuedLogic.and(
      cmp.lessThanOrEquals(a.low, b.low),
      cmp.greaterThanOrEquals(a.high, b.high)
    )

  includedIn: (item) ->
    if item instanceof Interval
      item.includes @
    else
      closed = @toClosed()
      ThreeValuedLogic.and(
        cmp.equals(closed.low, closed.high),
        cmp.equals(closed.low, item)
      )

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

  equals: (item) ->
    if item instanceof Interval
      [a, b] = [@toClosed(), item.toClosed()]
      ThreeValuedLogic.and(
        cmp.equals(a.low, b.low),
        cmp.equals(a.high, b.high)
      )
    else
      false

  toClosed: () ->
    if typeof(@low) is 'number' or @low instanceof DateTime
      low = switch
        when @lowClosed and @low is null then minValueForInstance @high
        when not @lowClosed then successor @low
        else @low
      high = switch
        when @highClosed and @high is null then maxValueForInstance @low
        when not @highClosed then predecessor @high
        else @high
      new Interval(low, high, true, true)
    else
      new Interval(@low, @high, true, true)
