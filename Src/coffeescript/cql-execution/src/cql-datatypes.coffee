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

  sameXAs: (field, other) ->
    if not(other instanceof DateTime) then return null

    [a, b] = [@[field], other[field]]
    if (a? and b?) then a is b else null

  before: (other) ->
    if not(other instanceof DateTime) then null

    for field in DateTime.FIELDS
      same = @sameXAs(field, other)
      if same then continue
      else if not same? then return null
      else return @[field] < other[field]

    false

  beforeOrSameAs: (other) ->
    before = @before(other)
    sameAs = @sameAs(other)
    if before or sameAs then return true
    else if before? and sameAs? then return false
    else return null

  after: (other) ->
    if not(other instanceof DateTime) then null

    other.before(@)

  afterOrSameAs: (other) ->
    after = @after(other)
    sameAs = @sameAs(other)
    if after or sameAs then return true
    else if after? and sameAs? then return false
    else return null

  sameAs: (other) ->
    if not(other instanceof DateTime) then null

    for field in DateTime.FIELDS
      same = @sameXAs(field, other)
      if not same? or not same then return same

    true

  add: (offset, field) ->
    result = @copy()
    if result[field]?
      # Increment the field, then round-trip to JS date and back for calendar math
      result[field] = result[field] + offset
      normalized = DateTime.fromDate(result.toJSDate())
      for field in DateTime.FIELDS when result[field]?
        result[field] = normalized[field]

    result

  toJSDate: () ->
    jsMonth = if @month? then @month-1 else 0
    new Date(@year, jsMonth, @day ? 1, @hour ? 0, @minute ? 0, @second ? 0, 0)

class Interval
  constructor: (@begin, @end, @beginOpen = false, @endOpen = false) ->

  includes: (item) ->
    if item instanceof DateTime then item = new Interval(item, item, false, false)

    if item instanceof Interval
      if ([@begin, @end, item.begin, item.end].every (x) -> x instanceof DateTime)
        [begin, end] = @getAdjustedEndpoints()
        [itmBegin, itmEnd] = item.getAdjustedEndpoints()
        return begin.beforeOrSameAs(itmBegin) and end.afterOrSameAs(itmEnd)
    else false

  # Adjusted endpoints are useful for timing calculations with open endpoints
  getAdjustedEndpoints: () ->
    [
      if @beginOpen then @begin.add(1, DateTime.Unit.SECOND) else @begin,
      if @endOpen then @end.add(-1, DateTime.Unit.SECOND) else @end
    ]

module.exports.Code = Code
module.exports.ValueSet = ValueSet
module.exports.DateTime = DateTime
module.exports.Interval = Interval