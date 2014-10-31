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

    if (@isPrecise() and other.isPrecise())
      return @toJSDate() < other.toJSDate()
    else
      bestCase = @asLowest().before other.asHighest()
      worstCase = @asHighest().before other.asLowest()
      if bestCase is worstCase then return bestCase else return null

    false

  beforeOrSameAs: (other) ->
    if not(other instanceof DateTime) then return false

    ThreeValuedLogic.not @after(other)

  after: (other) ->
    if not(other instanceof DateTime) then return false

    other.before(@)

  afterOrSameAs: (other) ->
    if not(other instanceof DateTime) then return false

    ThreeValuedLogic.not @before(other)

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
    self = @
    DateTime.FIELDS.every (field) -> self[field]?

  isImprecise: () ->
    not @isPrecise()

  asLowest: () ->
    result = @copy()

    if @isImprecise()
      result.year ?= 0
      result.month ?= 1
      result.day ?= 1
      result.hour ?= 0
      result.minute ?= 0
      result.second ?= 0

    result

  asHighest: () ->
    result = @copy()

    if @isImprecise()
      result.year ?= 10000
      result.month ?= 12
      # see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date/setDate
      result.day ?= (new Date(result.year, result.month, 0)).getDate()
      result.hour ?= 23
      result.minute ?= 59
      result.second ?= 59

    result

  toJSDate: () ->
    jsMonth = if @month? then @month-1 else 0
    new Date(@year, jsMonth, @day ? 1, @hour ? 0, @minute ? 0, @second ? 0, 0)

class Interval
  constructor: (@low, @high, @lowClosed = true, @highClosed = true) ->

  includes: (item) ->
    if @isDateTimeInterval()
      [low, high] = @_getAdjustedEndpoints()
      if item instanceof DateTime
        return ThreeValuedLogic.and low.beforeOrSameAs(item), high.afterOrSameAs(item)

      if item instanceof Interval and item.isDateTimeInterval()
        [itmLow, itmHigh] = item._getAdjustedEndpoints()
        return ThreeValuedLogic.and low.beforeOrSameAs(itmLow), high.afterOrSameAs(itmHigh)

    false

  includedIn: (item) ->
    if item instanceof DateTime and @isDateTimeInterval()
      return ThreeValuedLogic.and @lowClosed, @highClosed, @low.sameAs(@high), item.sameAs(@low)
    else if item instanceof Interval
      return item.includes @

    false

  overlaps: (item) ->
    if item instanceof DateTime then return @includes item

    if item instanceof Interval and @isDateTimeInterval()
      [low, high] = @_getAdjustedEndpoints()
      [itmLow, itmHigh] = item._getAdjustedEndpoints()
      disjoint = ThreeValuedLogic.or high.before(itmLow), itmHigh.before(low)
      return ThreeValuedLogic.not disjoint

    false

  isDateTimeInterval: () ->
    @low instanceof DateTime and @high instanceof DateTime

  # Adjusted endpoints are useful for timing calculations with open endpoints
  _getAdjustedEndpoints: () ->
    [
      if @lowClosed then @low else @low.add(1, DateTime.Unit.SECOND),
      if @highClosed then @high else @high.add(-1, DateTime.Unit.SECOND)
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
module.exports.DateTime = DateTime
module.exports.Interval = Interval
module.exports.ThreeValuedLogic = ThreeValuedLogic