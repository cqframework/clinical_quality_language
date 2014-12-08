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

class DateTime
  @Unit: { YEAR: 'year', MONTH: 'month', DAY: 'day', HOUR: 'hour', MINUTE: 'minute', SECOND: 'second', MILLISECOND: 'millisecond' }
  @FIELDS: [@Unit.YEAR, @Unit.MONTH, @Unit.DAY, @Unit.HOUR, @Unit.MINUTE, @Unit.SECOND, @Unit.MILLISECOND]

  @parse: (string) ->
    match = regex = /(\d{4})(-(\d{2}))?(-(\d{2}))?(T((\d{2})(\:(\d{2})(\:(\d{2})(\.(\d+))?)?)?)?(([+-])(\d{2})(\:?(\d{2}))?)?)?/.exec string
    
    if match[0] is string
      args = [match[1], match[3], match[5], match[8], match[10], match[12], match[14]]
      # fix up milliseconds by padding zeros and/or truncating (5 --> 500, 50 --> 500, 54321 --> 543, etc.)
      if args[6]? then args[6] = (args[6] + "00").substring(0, 3)
      # convert them all to integers
      args = ((if arg? then parseInt(arg,10)) for arg in args)
      # convert timezone offset to decimal and add it to arguments
      if match[17]?
        num = parseInt(match[17],10) + (if match[19]? then parseInt(match[19],10) / 60 else 0)
        args.push(if match[16] is '+' then num else num * -1)
      new DateTime(args...)
    else
      null

  @fromDate: (date, timeZoneOffset) ->
    if timeZoneOffset?
      date = new Date(date.getTime() + (timeZoneOffset * 60 * 60 * 1000))
      new DateTime(
        date.getUTCFullYear(),
        date.getUTCMonth() + 1,
        date.getUTCDate(),
        date.getUTCHours(),
        date.getUTCMinutes(),
        date.getUTCSeconds(),
        date.getUTCMilliseconds(),
        timeZoneOffset)
    else
      new DateTime(
        date.getFullYear(),
        date.getMonth() + 1,
        date.getDate(),
        date.getHours(),
        date.getMinutes(),
        date.getSeconds(),
        date.getMilliseconds())

  constructor: (@year=null, @month=null, @day=null, @hour=null, @minute=null, @second=null, @millisecond=null, @timeZoneOffset=null) ->

  copy: () ->
    new DateTime(@year, @month, @day, @hour, @minute, @second, @millisecond, @timeZoneOffset)

  convertToTimeZoneOffset: (timeZoneOffset = 0) ->
    DateTime.fromDate(@toJSDate(), timeZoneOffset)

  sameAs: (other, precision = DateTime.Unit.MILLISECOND) ->
    if not(other instanceof DateTime) then null

    if @timeZoneOffset isnt other.timeZoneOffset
      if @timeZoneOffset? then return @sameAs(other.convertToTimeZoneOffset(@timeZoneOffset), precision)
      else return @convertToTimeZoneOffset(other.timeZoneOffset).sameAs(other, precision)

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
      normalized = DateTime.fromDate(result.toJSDate(), @timeZoneOffset)
      for field in DateTime.FIELDS when result[field]?
        result[field] = normalized[field]

    result

  durationBetween: (other, unitField) ->
    if not(other instanceof DateTime) then return null

    a = @toUncertainty()
    b = other.toUncertainty()
    new Uncertainty(@_durationBetweenDates(a.high, b.low, unitField), @_durationBetweenDates(a.low, b.high, unitField))

  _durationBetweenDates: (a, b, unitField) ->
    # To count boundaries below month, we need to floor units at lower precisions
    [a, b] = [a, b].map (x) ->
      switch unitField
        when DateTime.Unit.DAY then new Date(x.getFullYear(), x.getMonth(), x.getDate())
        when DateTime.Unit.HOUR then new Date(x.getFullYear(), x.getMonth(), x.getDate(), x.getHours())
        when DateTime.Unit.MINUTE then new Date(x.getFullYear(), x.getMonth(), x.getDate(), x.getHours(), x.getMinutes())
        when DateTime.Unit.SECOND then new Date(x.getFullYear(), x.getMonth(), x.getDate(), x.getHours(), x.getMinutes(), x.getSeconds())
        when DateTime.Unit.MILLISECOND then new Date(x.getFullYear(), x.getMonth(), x.getDate(), x.getHours(), x.getMinutes(), x.getSeconds(), x.getMilliseconds())
        else x

    msDiff = b.getTime() - a.getTime()
    switch unitField
      when DateTime.Unit.YEAR then b.getFullYear() - a.getFullYear()
      when DateTime.Unit.MONTH then b.getMonth() - a.getMonth() + (12 * (b.getFullYear() - a.getFullYear()))
      when DateTime.Unit.DAY then Math.floor(msDiff / (24 * 60 * 60 * 1000))
      when DateTime.Unit.HOUR then Math.floor(msDiff / (60 * 60 * 1000))
      when DateTime.Unit.MINUTE then Math.floor(msDiff / (60 * 1000))
      when DateTime.Unit.SECOND then Math.floor(msDiff / 1000)
      when DateTime.Unit.MILLISECOND then msDiff
      else null

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
      @second ? 59,
      @millisecond ? 999,
      @timeZoneOffset)).toJSDate()
    new Uncertainty(low, high)

  toJSDate: () ->
    [y, mo, d, h, mi, s, ms] = [ @year, (if @month? then @month-1 else 0), @day ? 1, @hour ? 0, @minute ? 0, @second ? 0, @millisecond ? 0 ]
    if @timeZoneOffset?
      new Date(Date.UTC(y, mo, d, h, mi, s, ms) - (@timeZoneOffset * 60 * 60 * 1000))
    else
      new Date(y, mo, d, h, mi, s, ms)

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

class ThreeValuedLogic
  @and: (val...) ->
    if false in val then false
    else if null in val then null
    else true

  @or: (val...) ->
    if true in val then true
    else if null in val then null
    else false

  @xor: (val...) ->
    if null in val then null
    else val.reduce (a,b) -> (!a ^ !b) is 1

  @not: (val) ->
    if val? then return not val else return null

module.exports.Code = Code
module.exports.ValueSet = ValueSet
module.exports.Uncertainty = Uncertainty
module.exports.DateTime = DateTime
module.exports.Interval = Interval
module.exports.ThreeValuedLogic = ThreeValuedLogic