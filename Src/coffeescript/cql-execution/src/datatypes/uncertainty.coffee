{ ThreeValuedLogic } = require './logic'

module.exports.Uncertainty = class Uncertainty
  @from: (obj) ->
    if obj instanceof Uncertainty then obj else new Uncertainty(obj)

  constructor: (@low = null, @high) ->
    gt = (a, b) -> 
      if typeof a != typeof b
        # TODO: This should probably throw rather than return false.
        # Uncertainties with different types probably shouldn't be supported.
        return false
      if typeof a.after is 'function' then a.after b else a > b
    if typeof @high is 'undefined' then @high = @low
    if @low? and @high? and gt(@low, @high) then [@low, @high] = [@high, @low]

  isPoint: () ->
    # Note: Can't use normal equality, as that fails for Javascript dates
    # TODO: Fix after we don't need to support Javascript date uncertainties anymore
    lte = (a, b) -> 
      if typeof a != typeof b
        return false
      if typeof a.sameOrBefore is 'function' then a.sameOrBefore b else a <= b
    gte = (a, b) -> 
      if typeof a != typeof b
        return false
      if typeof a.sameOrBefore is 'function' then a.sameOrAfter b else a >= b
    @low? and @high? and lte(@low, @high) and gte(@low, @high)

  equals: (other) ->
    other = Uncertainty.from other
    ThreeValuedLogic.not ThreeValuedLogic.or(@lessThan(other), @greaterThan(other))

  lessThan: (other) ->
    lt = (a, b) -> 
      if typeof a != typeof b then return false
      if typeof a.before is 'function' then a.before b else a < b
    other = Uncertainty.from other
    bestCase = not @low? or not other.high? or lt(@low, other.high)
    worstCase = @high? and other.low? and lt(@high, other.low)
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
