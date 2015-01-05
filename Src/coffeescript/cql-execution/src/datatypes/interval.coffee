{ DateTime } = require './datetime'
{ Uncertainty } = require './uncertainty'
{ ThreeValuedLogic } = require './logic'
{ successor, predecessor } = require '../util/math'
{ equals } = require '../util/util'

module.exports.Interval = class Interval
  constructor: (@low, @high, @lowClosed = true, @highClosed = true) ->

  includes: (item) ->
    [uLow, uHigh] = @_getEndpointsAsUncertainties()
    if item instanceof Interval
      [uItmLow, uItmHigh] = item._getEndpointsAsUncertainties()
      ThreeValuedLogic.and uLow.lessThanOrEquals(uItmLow), uHigh.greaterThanOrEquals(uItmHigh)
    else
      uItem = if item.toUncertainty? then item.toUncertainty() else new Uncertainty(item)
      ThreeValuedLogic.and uLow.lessThanOrEquals(uItem), uHigh.greaterThanOrEquals(uItem)

  includedIn: (item) ->
    if item instanceof Interval
      item.includes @
    else
      [uLow, uHigh] = @_getEndpointsAsUncertainties()
      uItem = if item.toUncertainty? then item.toUncertainty() else new Uncertainty(item)
      ThreeValuedLogic.and @lowClosed, @highClosed, uLow.equals(uHigh), uLow.equals(uItem), uHigh.equals(uItem)

  overlaps: (item) ->
    if item instanceof Interval
      [uLow, uHigh] = @_getEndpointsAsUncertainties()
      [uItmLow, uItmHigh] = item._getEndpointsAsUncertainties()
      uLow.lessThanOrEquals(uItmHigh) and uHigh.greaterThanOrEquals(uItmLow)
    else
      @includes item

  equals: (item) ->
    if item instanceof Interval
      [a, b] = [@toClosed(), item.toClosed()]
      ThreeValuedLogic.and equals(a.low, b.low), equals(a.high, b.high)

  toClosed: () ->
    low = @low
    high = @high
    if typeof(@low) is 'number' or @low instanceof DateTime
      low = successor(low) unless @lowClosed
      high = predecessor(high) unless @highClosed
    new Interval(low, high, true, true)

  _getEndpointsAsUncertainties: () ->
    # Since uncertainties are always closed, adjust open endpoints
    ivl = @toClosed()

    [
      if ivl.low.toUncertainty? then ivl.low.toUncertainty() else new Uncertainty(ivl.low),
      if ivl.high.toUncertainty? then ivl.high.toUncertainty() else new Uncertainty(ivl.high)
    ]
