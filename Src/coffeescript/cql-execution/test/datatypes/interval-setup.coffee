{ Interval } = require '../../lib/datatypes/interval'
{ DateTime } = require '../../lib/datatypes/datetime'

class TestDateTime
  @parse: (string) ->
    TestDateTime.fromDateTime(DateTime.parse string)

  @fromDateTime: (d) ->
    new TestDateTime(d.year, d.month, d.day, d.hour, d.minute, d.second, d.millisecond)

  constructor: (year, month = 0, day = 1, hour = 0, minute = 0, second = 0, millisecond = 0) ->
    @full = new DateTime(year, month, day, hour, minute, second, millisecond)
    @toYear = new DateTime(year)
    @toMonth = new DateTime(year, month)
    @toDay = new DateTime(year, month, day)
    @toHour = new DateTime(year, month, day, hour)
    @toMinute = new DateTime(year, month, day, hour, minute)
    @toSecond = new DateTime(year, month, day, hour, minute, second)
    @toMillisecond = new DateTime(year, month, day, hour, minute, second, millisecond)


class TestInterval
  constructor: (low, high) ->
    [thLow, thHigh] = [TestDateTime.fromDateTime(low), TestDateTime.fromDateTime(high)]
    @closed = new Interval(low, high, true, true)
    @open = new Interval(low, high, false, false)
    @closedOpen = new Interval(low, high, true, false)
    @openClosed = new Interval(low, high, false, true)
    @toYear = new Interval(thLow.toYear, thHigh.toYear)
    @toMonth = new Interval(thLow.toMonth, thHigh.toMonth)
    @toDay = new Interval(thLow.toDay, thHigh.toDay)
    @toHour = new Interval(thLow.toHour, thHigh.toHour)
    @toMinute = new Interval(thLow.toMinute, thHigh.toMinute)
    @toSecond = new Interval(thLow.toSecond, thHigh.toSecond)
    @toMillisecond = new Interval(thLow.toMillisecond, thHigh.toMillisecond)

module.exports = (test) ->
  test['all2012'] = new TestInterval(DateTime.parse('2012-01-01T00:00:00.0'), DateTime.parse('2012-12-31T23:59:59.999'))
  test['janjune'] = new TestInterval(DateTime.parse('2012-01-01T00:00:00.0'), DateTime.parse('2012-06-01T00:00:00.0'))
  test['septdec'] = new TestInterval(DateTime.parse('2012-09-01T00:00:00.0'), DateTime.parse('2012-12-31T23:59:59.999'))
  test['julysept'] = new TestInterval(DateTime.parse('2012-06-01T00:00:00.0'), DateTime.parse('2012-09-01T00:00:00.0'))
  test['julydec'] = new TestInterval(DateTime.parse('2012-07-01T00:00:00.0'), DateTime.parse('2012-12-31T23:59:59.999'))
  test['janjuly'] = new TestInterval(DateTime.parse('2012-01-01T00:00:00.0'), DateTime.parse('2012-07-01T00:00:00.0'))
  test['bef2012'] = TestDateTime.parse('2011-06-01T00:00:00.0')
  test['beg2012'] = TestDateTime.parse('2012-01-01T00:00:00.0')
  test['mid2012'] = TestDateTime.parse('2012-06-01T00:00:00.0')
  test['end2012'] = TestDateTime.parse('2012-12-31T23:59:59.999')
  test['aft2012'] = TestDateTime.parse('2013-06-01T00:00:00.0')
  test['dIvl'] = {
    sameAs: {
      #    |----------X----------|
      #    |----------Y----------|
      x: new TestInterval(DateTime.parse('2012-01-01T00:00:00.0'), DateTime.parse('2012-12-31T23:59:59.999')),
      y: new TestInterval(DateTime.parse('2012-01-01T00:00:00.0'), DateTime.parse('2012-12-31T23:59:59.999'))
    },
    before: {
      #    |----------X----------|
      #                                   |----------Y----------|
      x: new TestInterval(DateTime.parse('2012-01-01T00:00:00.0'), DateTime.parse('2012-04-01T00:00:00.0')),
      y: new TestInterval(DateTime.parse('2012-07-01T00:00:00.0'), DateTime.parse('2012-12-31T23:59:59.999'))
    },
    meets: {
      #    |----------X----------|
      #                           |-----------Y----------|
      x: new TestInterval(DateTime.parse('2012-01-01T00:00:00.0'), DateTime.parse('2012-06-30T23:59:59.999')),
      y: new TestInterval(DateTime.parse('2012-07-01T00:00:00.0'), DateTime.parse('2012-12-31T23:59:59.999'))
    },
    overlaps: {
      #    |----------X----------|
      #                  |----------Y----------|
      x: new TestInterval(DateTime.parse('2012-01-01T00:00:00.0'), DateTime.parse('2012-09-01T00:00:00.0')),
      y: new TestInterval(DateTime.parse('2012-06-01T00:00:00.0'), DateTime.parse('2012-12-31T23:59:59.999'))
    },
    begins: {
      #    |-----X-----|
      #    |----------Y----------|
      x: new TestInterval(DateTime.parse('2012-01-01T00:00:00.0'), DateTime.parse('2012-07-01T00:00:00.0')),
      y: new TestInterval(DateTime.parse('2012-01-01T00:00:00.0'), DateTime.parse('2012-12-31T23:59:59.999'))
    },
    during: {
      #         |-----X-----|
      #    |----------Y----------|
      x: new TestInterval(DateTime.parse('2012-05-01T00:00:00.0'), DateTime.parse('2012-07-01T00:00:00.0')),
      y: new TestInterval(DateTime.parse('2012-01-01T00:00:00.0'), DateTime.parse('2012-12-31T23:59:59.999'))
    },
    ends: {
      #              |-----X-----|
      #    |----------Y----------|
      x: new TestInterval(DateTime.parse('2012-07-01T00:00:00.0'), DateTime.parse('2012-12-31T23:59:59.999')),
      y: new TestInterval(DateTime.parse('2012-01-01T00:00:00.0'), DateTime.parse('2012-12-31T23:59:59.999'))
    }
  }
  test['zeroToHundred'] = new TestInterval(0, 100)
  test['zeroToForty'] = new TestInterval(0, 40)
  test['fortyToSixty'] = new TestInterval(40, 60)
  test['sixtyToHundred'] = new TestInterval(60, 100)
  test['iIvl'] = {
    sameAs: {
      #    |----------X----------|
      #    |----------Y----------|
      x: new TestInterval(0, 100),
      y: new TestInterval(0, 100)
    },
    before: {
      #    |----------X----------|
      #                                   |----------Y----------|
      x: new TestInterval(0, 40),
      y: new TestInterval(60, 100)
    },
    meets: {
      #    |----------X----------|
      #                           |-----------Y----------|
      x: new TestInterval(0, 50),
      y: new TestInterval(51, 100)
    },
    overlaps: {
      #    |----------X----------|
      #                  |----------Y----------|
      x: new TestInterval(0, 60),
      y: new TestInterval(40, 100)
    },
    begins: {
      #    |-----X-----|
      #    |----------Y----------|
      x: new TestInterval(0, 60),
      y: new TestInterval(0, 100)
    },
    during: {
      #         |-----X-----|
      #    |----------Y----------|
      x: new TestInterval(30, 70),
      y: new TestInterval(0, 100)
    },
    ends: {
      #              |-----X-----|
      #    |----------Y----------|
      x: new TestInterval(40, 100),
      y: new TestInterval(0, 100)
    }
  }
