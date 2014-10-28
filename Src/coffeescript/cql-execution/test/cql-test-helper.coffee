DT = require '../lib/cql-datatypes'

adjustPrecision = (x, precisionField) ->
  switch precisionField
    when DT.DateTime.Unit.YEAR then new DT.DateTime(x.year)
    when DT.DateTime.Unit.MONTH then new DT.DateTime(x.year, x.month)
    when DT.DateTime.Unit.DAY then new DT.DateTime(x.year, x.month, x.day)
    when DT.DateTime.Unit.HOUR then new DT.DateTime(x.year, x.month, x.day, x.hour)
    when DT.DateTime.Unit.MINUTE then new DT.DateTime(x.year, x.month, x.day, x.hour, x.minute)
    else new DT.DateTime(x.year, x.month, x.day, x.hour, x.minute, x.second)

class Interval
  constructor: (begin, end) ->
    @closed = new DT.Interval(begin, end, false, false)
    @open = new DT.Interval(begin, end, true, true)
    @toYear = new DT.Interval(adjustPrecision(begin, DT.DateTime.Unit.YEAR), adjustPrecision(end, DT.DateTime.Unit.YEAR))
    @toMonth = new DT.Interval(adjustPrecision(begin, DT.DateTime.Unit.MONTH), adjustPrecision(end, DT.DateTime.Unit.MONTH))
    @toDay = new DT.Interval(adjustPrecision(begin, DT.DateTime.Unit.DAY), adjustPrecision(end, DT.DateTime.Unit.DAY))
    @toHour = new DT.Interval(adjustPrecision(begin, DT.DateTime.Unit.HOUR), adjustPrecision(end, DT.DateTime.Unit.HOUR))
    @toMinute = new DT.Interval(adjustPrecision(begin, DT.DateTime.Unit.MINUTE), adjustPrecision(end, DT.DateTime.Unit.MINUTE))
    @toSecond = new DT.Interval(adjustPrecision(begin, DT.DateTime.Unit.SECOND), adjustPrecision(end, DT.DateTime.Unit.SECOND))

module.exports.Interval = Interval