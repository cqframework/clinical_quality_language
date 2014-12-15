{ ThreeValuedLogic } = require './logic'

module.exports.Uncertainty = class Uncertainty
  @from: (obj) ->
    if obj.toUncertainty? then obj.toUncertainty() else new Uncertainty(obj)

  constructor: (@low = null, @high) ->
    if typeof high is 'undefined' then @high = @low
    if @low? and @high? and @low > @high then [@low, @high] = [@high, @low]

  isPoint: () ->
    # Note: Can't use normal equality, as that fails for dates
    @low? and @high? and @low <= @high and @low >= @high

  equals: (other) ->
    other = Uncertainty.from other
    ThreeValuedLogic.not ThreeValuedLogic.or(@lessThan(other), @greaterThan(other))

  lessThan: (other) ->
    other = Uncertainty.from other
    bestCase = not @low? or not other.high? or @low < other.high
    worstCase = @high? and other.low? and @high < other.low
    if bestCase is worstCase then return bestCase else return null

  greaterThan: (other) ->
    other = Uncertainty.from other
    other.lessThan @

  lessThanOrEquals: (other) ->
    other = Uncertainty.from other
    ThreeValuedLogic.not @greaterThan(other)

  greaterThanOrEquals: (other) ->
    other = Uncertainty.from other
    ThreeValuedLogic.not @lessThan(other)

  toUncertainty: () ->
    @
