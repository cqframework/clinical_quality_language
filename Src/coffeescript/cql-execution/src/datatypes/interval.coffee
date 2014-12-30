{ DateTime } = require './datetime'
{ Uncertainty } = require './uncertainty'
{ ThreeValuedLogic } = require './logic'
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
    # TODO: Use successor and predecessor to properly convert
    low = @low
    high = @high
    if typeof(@low) is 'number'
      if (@low is parseInt @low)
        low = low + 1 unless @lowClosed
        high = high - 1 unless @highClosed
      else
        low = low + Math.pow(10,-8) unless @lowClosed
        high = high - Math.pow(10,-8) unless @highClosed
    else if @low instanceof DateTime
      # TODO: this is currently wrong...
      low = low.add(1, DateTime.Unit.MILLISECOND) unless @lowClosed
      high = high.add(-1, DateTime.Unit.MILLISECOND) unless @highClosed
    new Interval(low, high, true, true)

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
