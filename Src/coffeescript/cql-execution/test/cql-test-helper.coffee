DT = require '../lib/cql-datatypes'

class DateTime
  @parse: (string) ->
    DateTime.fromDateTime(DT.DateTime.parse string)

  @fromDateTime: (d) ->
    new DateTime(d.year, d.month, d.day, d.hour, d.minute, d.second, d.millisecond)

  constructor: (year, month = 0, day = 1, hour = 0, minute = 0, second = 0, millisecond = 0) ->
    @full = new DT.DateTime(year, month, day, hour, minute, second, millisecond)
    @toYear = new DT.DateTime(year)
    @toMonth = new DT.DateTime(year, month)
    @toDay = new DT.DateTime(year, month, day)
    @toHour = new DT.DateTime(year, month, day, hour)
    @toMinute = new DT.DateTime(year, month, day, hour, minute)
    @toSecond = new DT.DateTime(year, month, day, hour, minute, second)
    @toMillisecond = new DT.DateTime(year, month, day, hour, minute, second, millisecond)


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
    @toMillisecond = new DT.Interval(thLow.toMillisecond, thHigh.toMillisecond)

module.exports.Interval = Interval
module.exports.DateTime = DateTime