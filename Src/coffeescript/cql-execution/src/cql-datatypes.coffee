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
  @parse: (string) ->
    match = /(\d{4})(-(\d{2})(-(\d{2})(T(\d{2})(\:(\d{2})(\:(\d{2})([+-](\d{2})(\:(\d{2}))?)?)?)?)?)?)?/.exec string
    # arguments to DateTime are at odd indexes (1, 3, 5...)
    if match[0] is string then new DateTime((arg for arg in match[1..] by 2)...) else null 

  constructor: (@year, @month, @day, @hour, @minute, @second) ->

  toJSDate: () ->
    jsMonth = if @month? then @month-1 else 0
    new Date(@year, jsMonth, @day ? 1, @hour ? 0, @minute ? 0, @second ? 0, 0)

class Interval
  constructor: (@begin, @end, @beginOpen = false, @endOpen = false) ->

  contains: (item) ->
    if item instanceof DateTime then item = new Interval(item, item, false, false)

    if item instanceof Interval
      # TODO: Expand to work w/ more than date
      [ivlBeg, ivlEnd, itmBeg, itmEnd] = [@begin.toJSDate(), @end.toJSDate(), item.begin.toJSDate(), item.end.toJSDate()]
      itmBegOnOrAfterIvlBeg = itmBeg > ivlBeg or (itmBeg >= ivlBeg and (item.beginOpen or not @beginOpen))
      itmEndOnOrBeforeIvlEnd = itmEnd < ivlEnd or (itmEnd <= ivlEnd and (item.endOpen or not @endOpen))
      itmBegOnOrAfterIvlBeg and itmEndOnOrBeforeIvlEnd
    else false

module.exports.Code = Code
module.exports.ValueSet = ValueSet
module.exports.DateTime = DateTime
module.exports.Interval = Interval