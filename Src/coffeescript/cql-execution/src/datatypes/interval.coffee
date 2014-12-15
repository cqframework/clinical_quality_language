{ DateTime } = require './datetime'
{ Uncertainty } = require './uncertainty'
{ ThreeValuedLogic } = require './logic'

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

  _getEndpointsAsUncertainties: () ->
    # Since uncertainties are always closed, adjust open endpoints
    low = switch
      when @lowClosed then @low
      when @low instanceof DateTime then @low.add(1, DateTime.Unit.MILLISECOND)
      else @low + 1

    high = switch
      when @highClosed then @high
      when @high instanceof DateTime then @high.add(-1, DateTime.Unit.MILLISECOND)
      else @high - 1

    [
      if low.toUncertainty? then low.toUncertainty() else new Uncertainty(low),
      if high.toUncertainty? then high.toUncertainty() else new Uncertainty(high)
    ]
