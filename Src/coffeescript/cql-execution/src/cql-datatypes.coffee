class Code
  constructor: (@code, @system, @version) ->

class ValueSet
  constructor: (@oid, @version, @codes = []) ->

  hasCode: (code, system, version) ->
    if code instanceof Object then [ code, system, version ] = [ code.code, code.system, code.version ]
    matches = (c for c in @codes when c.code is code)
    if system? then matches = (c for c in matches when c.system is system)
    if version? then matches = (c for c in matches when c.version is version)
    return matches.length > 0

class Uncertainty
  constructor: (@low = null, @high) ->
    if typeof high is 'undefined' then @high = @low
    if @low? and @high? and @low > @high then [@low, @high] = [@high, @low]

  isPoint: () ->
    # Note: Can't use normal equality, as that fails for dates
    @low? and @high? and @low <= @high and @low >= @high

  equals: (other) ->
    other = @_convert other
    ThreeValuedLogic.not ThreeValuedLogic.or(@lessThan(other), @greaterThan(other))

  lessThan: (other) ->
    other = @_convert other
    bestCase = not @low? or not other.high? or @low < other.high
    worstCase = @high? and other.low? and @high < other.low
    if bestCase is worstCase then return bestCase else return null

  greaterThan: (other) ->
    other = @_convert other
    other.lessThan @

  lessThanOrEquals: (other) ->
    other = @_convert other
    ThreeValuedLogic.not @greaterThan(other)

  greaterThanOrEquals: (other) ->
    other = @_convert other
    ThreeValuedLogic.not @lessThan(other)

  toUncertainty: () ->
    @

  _convert: (other) ->
    if other.toUncertainty? then other.toUncertainty() else new Uncertainty(other)

class DateTime
  @Unit: { YEAR: 'year', MONTH: 'month', DAY: 'day', HOUR: 'hour', MINUTE: 'minute', SECOND: 'second' }
  @FIELDS: [@Unit.YEAR, @Unit.MONTH, @Unit.DAY, @Unit.HOUR, @Unit.MINUTE, @Unit.SECOND]

  @parse: (string) ->
    match = /(\d{4})(-(\d{2})(-(\d{2})(T(\d{2})(\:(\d{2})(\:(\d{2})([+-](\d{2})(\:(\d{2}))?)?)?)?)?)?)?/.exec string
    # arguments to DateTime are at odd indexes (1, 3, 5...)
    if match[0] is string then new DateTime(((if arg? then parseInt(arg)) for arg in match[1..] by 2)...) else null 

  @fromDate: (date) ->
    new DateTime(date.getFullYear(), date.getMonth() + 1, date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds())

  constructor: (@year, @month, @day, @hour, @minute, @second) ->

  copy: (other) ->
    new DateTime(@year, @month, @day, @hour, @minute, @second)

  sameAs: (other, precision = DateTime.Unit.SECOND) ->
    if not(other instanceof DateTime) then null

    for field in DateTime.FIELDS
      [a, b] = [@[field], other[field]]
      same = if (a? and b?) then a is b else null
      if not same or field is precision then return same

    true

  before: (other) ->
    if not(other instanceof DateTime) then return false

    @toUncertainty().lessThan(other.toUncertainty())

  beforeOrSameAs: (other) ->
    if not(other instanceof DateTime) then return false

    @toUncertainty().lessThanOrEquals(other.toUncertainty())

  after: (other) ->
    if not(other instanceof DateTime) then return false

    @toUncertainty().greaterThan(other.toUncertainty())

  afterOrSameAs: (other) ->
    if not(other instanceof DateTime) then return false

    @toUncertainty().greaterThanOrEquals(other.toUncertainty())

  add: (offset, field) ->
    result = @copy()
    if result[field]?
      # Increment the field, then round-trip to JS date and back for calendar math
      result[field] = result[field] + offset
      normalized = DateTime.fromDate(result.toJSDate())
      for field in DateTime.FIELDS when result[field]?
        result[field] = normalized[field]

    result

  isPrecise: () ->
    DateTime.FIELDS.every (field) => @[field]?

  isImprecise: () ->
    not @isPrecise()

  toUncertainty: () ->
    low = @toJSDate()
    high = (new DateTime(
      @year,
      @month ? 12,
      # see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date/setDate
      @day ? (new Date(@year, @month ? 12, 0)).getDate(),
      @hour ? 23,
      @minute ? 59,
      @second ? 59)).toJSDate()
    new Uncertainty(low, high)

  toJSDate: () ->
    jsMonth = if @month? then @month-1 else 0
    new Date(@year, jsMonth, @day ? 1, @hour ? 0, @minute ? 0, @second ? 0, 0)

class Interval
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
      when @low instanceof DateTime then @low.add(1, DateTime.Unit.SECOND)
      else @low + 1

    high = switch
      when @highClosed then @high
      when @high instanceof DateTime then @high.add(-1, DateTime.Unit.SECOND)
      else @high - 1

    [
      if low.toUncertainty? then low.toUncertainty() else new Uncertainty(low),
      if high.toUncertainty? then high.toUncertainty() else new Uncertainty(high)
    ]

class ThreeValuedLogic
  @and: (val...) ->
    if false in val then false
    else if null in val then null
    else true

  @or: (val...) ->
    if true in val then true
    else if null in val then null
    else false

  @not: (val) ->
    if val? then return not val else return null

module.exports.Code = Code
module.exports.ValueSet = ValueSet
module.exports.Uncertainty = Uncertainty
module.exports.DateTime = DateTime
module.exports.Interval = Interval
module.exports.ThreeValuedLogic = ThreeValuedLogic