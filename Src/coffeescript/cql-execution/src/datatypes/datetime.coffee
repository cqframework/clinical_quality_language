{ Uncertainty } = require './uncertainty'

module.exports.DateTime = class DateTime
  @Unit: { YEAR: 'year', MONTH: 'month', WEEK: 'week', DAY: 'day', HOUR: 'hour', MINUTE: 'minute', SECOND: 'second', MILLISECOND: 'millisecond' }
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
    # from the spec: If no timezone is specified, the timezone of the evaluation request timestamp is used.
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
    d = DateTime.fromDate(@toJSDate(), timezoneOffset)
    d.reducedPrecision(@getPrecision())

  sameAs: (other, precision = DateTime.Unit.MILLISECOND) ->
    if not(other instanceof DateTime) then null

    diff = @differenceBetween(other, precision)
    switch
      when (diff.low == 0 and diff.high == 0) then true
      when (diff.low <= 0 and diff.high >= 0) then null
      else false

  equals: (other) ->
    @sameAs(other, DateTime.Unit.MILLISECOND)

  sameOrBefore: (other, precision = DateTime.Unit.MILLISECOND) ->
    if not(other instanceof DateTime) then return false

    diff = @differenceBetween(other, precision)
    switch
      when (diff.low >= 0 and diff.high >= 0) then true
      when (diff.low < 0 and diff.high < 0) then false
      else null

  sameOrAfter: (other, precision = DateTime.Unit.MILLISECOND) ->
    if not(other instanceof DateTime) then return false

    diff = @differenceBetween(other, precision)
    switch
      when (diff.low <= 0 and diff.high <= 0) then true
      when (diff.low > 0 and diff.high > 0) then false
      else null

  before: (other, precision = DateTime.Unit.MILLISECOND) ->
    if not(other instanceof DateTime) then return false

    diff = @differenceBetween(other, precision)
    switch
      when (diff.low > 0 and diff.high > 0) then true
      when (diff.low <= 0 and diff.high <= 0) then false
      else null

  after: (other, precision = DateTime.Unit.MILLISECOND) ->
    if not(other instanceof DateTime) then return false

    diff = @differenceBetween(other, precision)
    switch
      when (diff.low < 0 and diff.high < 0) then true
      when (diff.low >= 0 and diff.high >= 0) then false
      else null

  add: (offset, field) ->
    # TODO: According to spec, 2/29/2000 + 1 year is 2/28/2001
    # Currently, it evaluates to 3/1/2001.  Doh.
    result = @copy()

    # If weeks, convert to days
    if field == DateTime.Unit.WEEK
      offset = offset * 7
      field = DateTime.Unit.DAY

    if result[field]?
      # Increment the field, then round-trip to JS date and back for calendar math
      result[field] = result[field] + offset
      normalized = DateTime.fromDate(result.toJSDate(), @timezoneOffset)
      for field in DateTime.FIELDS when result[field]?
        result[field] = normalized[field]

    result

  differenceBetween: (other, unitField) ->
    if not(other instanceof DateTime) then return null

    # According to CQL spec, to calculate difference, you can just floor lesser precisions and do a duration
    # Make copies since we'll be flooring values and mucking with timezones
    a = @copy()
    b = other.copy()
    # The dates need to agree on where the boundaries are, so we must normalize to the same time zone
    if a.timezoneOffset isnt b.timezoneOffset
      b = b.convertToTimezoneOffset(a.timezoneOffset)

    # JS always represents dates in the current locale, but in locales with DST, we want to account for the
    # potential difference in offset from one date to the other.  We try to simulate them being in the same
    # timezone, because we don't want midnight to roll back to 11:00pm since that will mess up day boundaries.
    aJS = a.toJSDate(true)
    bJS = b.toJSDate(true)
    tzDiff = aJS.getTimezoneOffset() - bJS.getTimezoneOffset()
    if (tzDiff != 0)
      # Since we'll be doing duration later, account for timezone offset by adding to the time (if possible)
      if b.year? and b.month? and b.day? and b.hour? and b.minute? then b = b.add(tzDiff, DateTime.Unit.MINUTE)
      else if b.year? and b.month? and b.day? and b.hour? then b = b.add(tzDiff/60, DateTime.Unit.HOUR)
      else b.timezoneOffset = b.timezoneOffset + (tzDiff/60)

    # Now floor lesser precisions before we go on to calculate duration
    if unitField == DateTime.Unit.YEAR
      a = new DateTime(a.year, 1, 1, 12, 0, 0, 0, a.timezoneOffset)
      b = new DateTime(b.year, 1, 1, 12, 0, 0, 0, b.timezoneOffset)
    else if unitField == DateTime.Unit.MONTH
      a = new DateTime(a.year, a.month, 1, 12, 0, 0, 0, a.timezoneOffset)
      b = new DateTime(b.year, b.month, 1, 12, 0, 0, 0, b.timezoneOffset)
    else if unitField == DateTime.Unit.WEEK
      a = @_floorWeek(a)
      b = @_floorWeek(b)
    else if unitField == DateTime.Unit.DAY
      a = new DateTime(a.year, a.month, a.day, 12, 0, 0, 0, a.timezoneOffset)
      b = new DateTime(b.year, b.month, b.day, 12, 0, 0, 0, b.timezoneOffset)
    else if unitField == DateTime.Unit.HOUR
      a = new DateTime(a.year, a.month, a.day, a.hour, 30, 0, 0, a.timezoneOffset)
      b = new DateTime(b.year, b.month, b.day, b.hour, 30, 0, 0, b.timezoneOffset)
    else if unitField == DateTime.Unit.MINUTE
      a = new DateTime(a.year, a.month, a.day, a.hour, a.minute, 0, 0, a.timezoneOffset)
      b = new DateTime(b.year, b.month, b.day, b.hour, b.minute, 0, 0, b.timezoneOffset)
    else if unitField == DateTime.Unit.SECOND
      a = new DateTime(a.year, a.month, a.day, a.hour, a.minute, a.second, 0, a.timezoneOffset)
      b = new DateTime(b.year, b.month, b.day, b.hour, b.minute, b.second, 0, b.timezoneOffset)

    a.durationBetween(b, unitField)

  _floorWeek: (d) ->
    # To "floor" a week, we need to go back to the last Sunday (that's when getDay() == 0 in javascript)
    # But if we don't know the day, then just return it as-is
    if (not d.day?) then return d
    floored = new Date(d.year, d.month-1, d.day)
    floored.setDate(floored.getDate() - 1) while floored.getDay() > 0
    new DateTime(floored.getFullYear(), floored.getMonth()+1, floored.getDate(), 12, 0, 0, 0, d.timezoneOffset)

  durationBetween: (other, unitField) ->
    if not(other instanceof DateTime) then return null
    a = @toUncertainty()
    b = other.toUncertainty()
    new Uncertainty(@_durationBetweenDates(a.high, b.low, unitField), @_durationBetweenDates(a.low, b.high, unitField))

  # NOTE: a and b are real JS dates -- not DateTimes
  _durationBetweenDates: (a, b, unitField) ->
    # DurationBetween is different than DifferenceBetween in that DurationBetween counts whole elapsed time periods, but
    # DifferenceBetween counts boundaries.  For example:
    # difference in days between @2012-01-01T23:59:59.999 and @2012-01-02T00:00:00.0 calculates to 1 (since it crosses day boundary)
    # days between @2012-01-01T23:59:59.999 and @2012-01-02T00:00:00.0 calculates to 0 (since there are no full days between them)
    msDiff = b.getTime() - a.getTime()

    if msDiff == 0 then return 0
    # If it's a negative delta, we need to use ceiling instead of floor when truncating
    truncFunc = if msDiff > 0 then Math.floor else Math.ceil
    # For ms, s, min, hr, day, and week this is trivial
    if unitField == DateTime.Unit.MILLISECOND then msDiff
    else if unitField == DateTime.Unit.SECOND then truncFunc(msDiff / 1000)
    else if unitField == DateTime.Unit.MINUTE then truncFunc(msDiff / (60 * 1000))
    else if unitField == DateTime.Unit.HOUR then truncFunc(msDiff / (60 * 60 * 1000))
    else if unitField == DateTime.Unit.DAY
      truncFunc(msDiff / (24 * 60 * 60 * 1000))
    else if unitField == DateTime.Unit.WEEK
      truncFunc(msDiff / (7 * 24 * 60 * 60 * 1000))
    # Months and years are trickier since months are variable length
    else if unitField == DateTime.Unit.MONTH or unitField == DateTime.Unit.YEAR
      # First get the rough months, essentially counting month "boundaries"
      months = (b.getFullYear() - a.getFullYear()) * 12 + (b.getMonth() - a.getMonth())
      # Now we need to look at the smaller units to see how they compare.  Since we only care about comparing
      # days and below at this point, it's much easier to bring a up to b so it's in the same month, then
      # we can compare on just the remaining units.
      aInMonth = new Date(a.getTime())
      # Remember the original timezone offset because if it changes when we bring it up a month, we need to fix it
      aInMonthOriginalOffset = aInMonth.getTimezoneOffset()
      aInMonth.setMonth(a.getMonth() + months)
      if aInMonthOriginalOffset != aInMonth.getTimezoneOffset()
        aInMonth.setMinutes(aInMonth.getMinutes() + (aInMonthOriginalOffset - aInMonth.getTimezoneOffset()))
      # When a is before b, then if a's smaller units are greater than b's, a whole month hasn't elapsed, so adjust
      if msDiff > 0 and aInMonth > b then months = months - 1
      # When b is before a, then if a's smaller units are less than b's, a whole month hasn't elaspsed backwards, so adjust
      else if msDiff < 0 and aInMonth < b then months = months + 1
      # If this is months, just return them, but if it's years, we need to convert
      if unitField == DateTime.Unit.MONTH
        months
      else
        truncFunc(months/12)
    else
      null


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
    if @millisecond? then result = DateTime.Unit.MILLISECOND
    result

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

  toJSON: () ->
    @toString()

  _pad: (num) ->
    String("0" + num).slice(-2)

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
    new DateTime(0, 1, 1, @hour, @minute, @second, @millisecond, @timezoneOffset)

  isTime: () ->
    @year == 0 && @month == 1 && @day == 1

  reducedPrecision: (unitField = DateTime.Unit.MILLISECOND) ->
    reduced = @copy()
    if unitField isnt DateTime.Unit.MILLISECOND
      fieldIndex = DateTime.FIELDS.indexOf unitField
      fieldsToRemove = DateTime.FIELDS.slice(fieldIndex + 1)
      reduced[field] = null for field in fieldsToRemove
    reduced
