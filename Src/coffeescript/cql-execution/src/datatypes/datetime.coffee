{ Uncertainty } = require './uncertainty'

module.exports.DateTime = class DateTime
  @Unit: { YEAR: 'year', MONTH: 'month', DAY: 'day', HOUR: 'hour', MINUTE: 'minute', SECOND: 'second', MILLISECOND: 'millisecond' }
  @FIELDS: [@Unit.YEAR, @Unit.MONTH, @Unit.DAY, @Unit.HOUR, @Unit.MINUTE, @Unit.SECOND, @Unit.MILLISECOND]

  @parse: (string) ->
    match = regex = /(\d{4})(-(\d{2}))?(-(\d{2}))?(T((\d{2})(\:(\d{2})(\:(\d{2})(\.(\d+))?)?)?)?(([+-])(\d{2})(\:?(\d{2}))?)?)?/.exec string

    if match?[0] is string
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

  @fromDate: (date, timezoneOffset) ->
    if timezoneOffset?
      date = new Date(date.getTime() + (timezoneOffset * 60 * 60 * 1000))
      new DateTime(
        date.getUTCFullYear(),
        date.getUTCMonth() + 1,
        date.getUTCDate(),
        date.getUTCHours(),
        date.getUTCMinutes(),
        date.getUTCSeconds(),
        date.getUTCMilliseconds(),
        timezoneOffset)
    else
      new DateTime(
        date.getFullYear(),
        date.getMonth() + 1,
        date.getDate(),
        date.getHours(),
        date.getMinutes(),
        date.getSeconds(),
        date.getMilliseconds())

  constructor: (@year=null, @month=null, @day=null, @hour=null, @minute=null, @second=null, @millisecond=null, @timezoneOffset) ->
    if not @timezoneOffset?
      @timezoneOffset = (new Date()).getTimezoneOffset() / 60 * -1


  copy: () ->
    new DateTime(@year, @month, @day, @hour, @minute, @second, @millisecond, @timezoneOffset)

  successor: () ->
    if @millisecond?
      @add(1,DateTime.Unit.MILLISECOND)
    else if @second?
      @add(1,DateTime.Unit.SECOND)
    else if @minute?
      @add(1,DateTime.Unit.MINUTE)
    else if @hour?
      @add(1,DateTime.Unit.HOUR)
    else if @day?
      @add(1,DateTime.Unit.DAY)
    else if @month?
      @add(1,DateTime.Unit.MONTH)
    else if @year?
      @add(1,DateTime.Unit.YEAR)


  predecessor: () ->
    if @millisecond?
      @add(-1,DateTime.Unit.MILLISECOND)
    else if @second?
      @add(-1,DateTime.Unit.SECOND)
    else if @minute?
      @add(-1,DateTime.Unit.MINUTE)
    else if @hour?
      @add(-1,DateTime.Unit.HOUR)
    else if @day?
      @add(-1,DateTime.Unit.DAY)
    else if @month?
      @add(-1,DateTime.Unit.MONTH)
    else if @year?
      @add(-1,DateTime.Unit.YEAR)

  convertToTimezoneOffset: (timezoneOffset = 0) ->
    DateTime.fromDate(@toJSDate(), timezoneOffset)

  sameAs: (other, precision = DateTime.Unit.MILLISECOND) ->
    if not(other instanceof DateTime) then null

    diff = @durationBetween(other, precision)
    switch
      when (diff.low == 0 and diff.high == 0) then true
      when (diff.low <= 0 and diff.high >= 0) then null
      else false

  equals: (other) ->
    @sameAs(other, DateTime.Unit.MILLISECOND)

  sameOrBefore: (other, precision = DateTime.Unit.MILLISECOND) ->
    if not(other instanceof DateTime) then return false

    diff = @durationBetween(other, precision)
    switch
      when (diff.low >= 0 and diff.high >= 0) then true
      when (diff.low < 0 and diff.high < 0) then false
      else null

  sameOrAfter: (other, precision = DateTime.Unit.MILLISECOND) ->
    if not(other instanceof DateTime) then return false

    diff = @durationBetween(other, precision)
    switch
      when (diff.low <= 0 and diff.high <= 0) then true
      when (diff.low > 0 and diff.high > 0) then false
      else null

  before: (other, precision = DateTime.Unit.MILLISECOND) ->
    if not(other instanceof DateTime) then return false

    diff = @durationBetween(other, precision)
    switch
      when (diff.low > 0 and diff.high > 0) then true
      when (diff.low <= 0 and diff.high <= 0) then false
      else null

  after: (other, precision = DateTime.Unit.MILLISECOND) ->
    if not(other instanceof DateTime) then return false

    diff = @durationBetween(other, precision)
    switch
      when (diff.low < 0 and diff.high < 0) then true
      when (diff.low >= 0 and diff.high >= 0) then false
      else null

  add: (offset, field) ->
    # TODO: According to spec, 2/29/2000 + 1 year is 2/28/2001
    # Currently, it evaluates to 3/1/2001.  Doh.
    result = @copy()
    if result[field]?
      # Increment the field, then round-trip to JS date and back for calendar math
      result[field] = result[field] + offset
      normalized = DateTime.fromDate(result.toJSDate(), @timezoneOffset)
      for field in DateTime.FIELDS when result[field]?
        result[field] = normalized[field]

    result

  durationBetween: (other, unitField) ->
    if not(other instanceof DateTime) then return null

    if @timezoneOffset isnt other.timezoneOffset
      other = other.convertToTimezoneOffset(@timezoneOffset)

    a = @toUncertainty(true)
    b = other.toUncertainty(true)
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

  isMorePrecise: (other) ->
    for field in DateTime.FIELDS
      if (other[field]? and not @[field]?) then return false
    not @isSamePrecision(other)

  isLessPrecise: (other) ->
    not @isSamePrecision(other) and not @isMorePrecise(other)

  isSamePrecision: (other) ->
    for field in DateTime.FIELDS
      if (@[field]? and not other[field]?) then return false
      if (not @[field]? and other[field]?) then return false
    true

  getPrecision: () ->
    result = null
    if @year? then result = DateTime.Unit.YEAR else return result
    if @month? then result = DateTime.Unit.MONTH else return result
    if @day? then result = DateTime.Unit.DAY else return result
    if @hour? then result = DateTime.Unit.HOUR else return result
    if @minute? then result = DateTime.Unit.MINUTE else return result
    if @second? then result = DateTime.Unit.SECOND else return result
    if @millisecond? then result = DateTime.Unit.MILLISECOND else return result

  toUncertainty: (ignoreTimezone = false) ->
    low = @toJSDate(ignoreTimezone)
    high = (new DateTime(
      @year,
      @month ? 12,
      # see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date/setDate
      @day ? (new Date(@year, @month ? 12, 0)).getDate(),
      @hour ? 23,
      @minute ? 59,
      @second ? 59,
      @millisecond ? 999,
      @timezoneOffset)).toJSDate(ignoreTimezone)
    new Uncertainty(low, high)

  toJSDate: (ignoreTimezone = false) ->
    [y, mo, d, h, mi, s, ms] = [ @year, (if @month? then @month-1 else 0), @day ? 1, @hour ? 0, @minute ? 0, @second ? 0, @millisecond ? 0 ]
    if @timezoneOffset? and not ignoreTimezone
      new Date(Date.UTC(y, mo, d, h, mi, s, ms) - (@timezoneOffset * 60 * 60 * 1000))
    else
      new Date(y, mo, d, h, mi, s, ms)

  _pad: (num) ->
    String("0" + num).slice(-2);

  # TODO: Needs unit tests!
  toString: () ->
    str = ''
    if @year?
      str += @year
      if @month?
        str += '-' + @_pad(@month)
        if @day?
          str += '-' + @_pad(@day)
          if @hour?
            str += 'T' + @_pad(@hour)
            if @minute?
              str += ':' + @_pad(@minute)
              if @second?
                str += ':' + @_pad(@second)
                if @millisecond?
                  str += '.' + @_pad(@millisecond)

    if str.indexOf('T') != -1 and @timezoneOffset?
      str += if @timezoneOffset < 0 then '-' else '+'
      offsetHours = Math.floor(Math.abs(@timezoneOffset))
      str += @_pad(offsetHours)
      offsetMin = (Math.abs(@timezoneOffset) - offsetHours) * 60
      str += @_pad(offsetMin)

    str

  getDate: () ->
    @reducedPrecision DateTime.Unit.DAY

  getTime: () ->
    new DateTime(1900, 1, 1, @hour, @minute, @second, @millisecond, @timezoneOffset)

  reducedPrecision: (unitField = DateTime.Unit.MILLISECOND) ->
    reduced = @copy()
    if unitField isnt DateTime.Unit.MILLISECOND
      fieldIndex = DateTime.FIELDS.indexOf unitField
      fieldsToRemove = DateTime.FIELDS.slice(fieldIndex + 1)
      reduced[field] = null for field in fieldsToRemove
    reduced
