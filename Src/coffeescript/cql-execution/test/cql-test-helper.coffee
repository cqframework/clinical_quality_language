DT = require '../lib/cql-datatypes'

adjustPrecision = (x, precisionField) ->
  switch precisionField
    when DT.DateTime.Unit.YEAR then new DT.DateTime(x.year)
    when DT.DateTime.Unit.MONTH then new DT.DateTime(x.year, x.month)
    when DT.DateTime.Unit.DAY then new DT.DateTime(x.year, x.month, x.day)
    when DT.DateTime.Unit.HOUR then new DT.DateTime(x.year, x.month, x.day, x.hour)
    when DT.DateTime.Unit.MINUTE then new DT.DateTime(x.year, x.month, x.day, x.hour, x.minute)
    else new DT.DateTime(x.year, x.month, x.day, x.hour, x.minute, x.second)

class DateTime
  @parse: (string) ->
    DateTime.fromDateTime(DT.DateTime.parse string)

  @fromDateTime: (d) ->
    new DateTime(d.year, d.month, d.day, d.hour, d.minute, d.second)

  constructor: (year, month = 0, day = 1, hour = 0, minute = 0, second = 0) ->
    @full = new DT.DateTime(year, month, day, hour, minute, second)
    @toYear = new DT.DateTime(year)
    @toMonth = new DT.DateTime(year, month)
    @toDay = new DT.DateTime(year, month, day)
    @toHour = new DT.DateTime(year, month, day, hour)
    @toMinute = new DT.DateTime(year, month, day, hour, minute)
    @toSecond = new DT.DateTime(year, month, day, hour, minute, second)


class Interval
  constructor: (low, high) ->
    [thLow, thHigh] = [DateTime.fromDateTime(low), DateTime.fromDateTime(high)]
    @closed = new DT.Interval(low, high, true, true)
    @open = new DT.Interval(low, high, false, false)
    @toYear = new DT.Interval(thLow.toYear, thHigh.toYear)
    @toMonth = new DT.Interval(thLow.toMonth, thHigh.toMonth)
    @toDay = new DT.Interval(thLow.toDay, thHigh.toDay)
    @toHour = new DT.Interval(thLow.toHour, thHigh.toHour)
    @toMinute = new DT.Interval(thLow.toMinute, thHigh.toMinute)
    @toSecond = new DT.Interval(thLow.toSecond, thHigh.toSecond)

module.exports.Interval = Interval
module.exports.DateTime = DateTime