should = require 'should'
setup = require './interval-setup'
{ Interval } = require '../../lib/datatypes/interval'
{ DateTime } = require '../../lib/datatypes/datetime'
{ Uncertainty } = require '../../lib/datatypes/uncertainty'

xy = (obj) -> [obj.x, obj.y]

describe 'Interval', ->

  it 'should properly set all properties when constructed as DateTime interval', ->
    i = new Interval(DateTime.parse('2012-01-01'), DateTime.parse('2013-01-01'), true, false)
    i.low.should.eql DateTime.parse '2012-01-01'
    i.high.should.eql DateTime.parse '2013-01-01'
    i.lowClosed.should.be.true
    i.highClosed.should.be.false

  it 'should properly set all properties when constructed as integer interval', ->
    i = new Interval(12, 36, true, false)
    i.low.should.equal 12
    i.high.should.equal 36
    i.lowClosed.should.be.true
    i.highClosed.should.be.false

  it 'should default lowClosed/highClosed to true', ->
    i = new Interval(DateTime.parse('2012-01-01'), DateTime.parse('2013-01-01'))
    i.low.should.eql DateTime.parse '2012-01-01'
    i.high.should.eql DateTime.parse '2013-01-01'
    i.lowClosed.should.be.true
    i.highClosed.should.be.true

describe 'DateTimeInterval.contains', ->
  @beforeEach ->
    setup @

  it 'should properly calculate dates before it', ->
    @all2012.closed.contains(@bef2012.full).should.be.false

  it 'should properly calculate the left boundary date', ->
    @all2012.closed.contains(@beg2012.full).should.be.true
    @all2012.open.contains(@beg2012.full).should.be.false

  it 'should properly calculate dates in the middle of it', ->
    @all2012.closed.contains(@mid2012.full).should.be.true

  it 'should properly calculate the right boundary date', ->
    @all2012.closed.contains(@end2012.full).should.be.true
    @all2012.open.contains(@end2012.full).should.be.false

  it 'should properly calculate dates after it', ->
    @all2012.closed.contains(@aft2012.full).should.be.false

  it 'should properly handle null endpoints', ->
    date = DateTime.parse('2012-01-01T00:00:00.0')
    early = DateTime.parse('1900-01-01T00:00:00.0')
    late = DateTime.parse('2999-01-01T00:00:00.0')
    new Interval(null, date).contains(early).should.be.true
    new Interval(null, date).contains(late).should.be.false
    new Interval(null,date,false,true).contains(date).should.be.true
    should(new Interval(null,date,false,true).contains(early)).be.null
    new Interval(null,date,false,true).contains(late).should.be.false
    new Interval(date,null).contains(late).should.be.true
    new Interval(date,null).contains(early).should.be.false
    new Interval(date,null,true,false).contains(date).should.be.true
    should(new Interval(date,null,true,false).contains(late)).be.null
    new Interval(date,null,true,false).contains(early).should.be.false

  it 'should properly handle imprecision', ->
    @all2012.closed.contains(@bef2012.toMonth).should.be.false
    @all2012.closed.contains(@beg2012.toMonth).should.be.true
    @all2012.closed.contains(@mid2012.toMonth).should.be.true
    @all2012.closed.contains(@end2012.toMonth).should.be.true
    @all2012.closed.contains(@aft2012.toMonth).should.be.false

    @all2012.toMonth.contains(@bef2012.toMonth).should.be.false
    should.not.exist @all2012.toMonth.contains(@beg2012.toMonth)
    @all2012.toMonth.contains(@mid2012.toMonth).should.be.true
    should.not.exist @all2012.toMonth.contains(@end2012.toMonth)
    @all2012.toMonth.contains(@aft2012.toMonth).should.be.false

    @all2012.toMonth.contains(@bef2012.full).should.be.false
    should.not.exist @all2012.toMonth.contains(@beg2012.full)
    @all2012.toMonth.contains(@mid2012.full).should.be.true
    should.not.exist @all2012.toMonth.contains(@end2012.full)
    @all2012.toMonth.contains(@aft2012.full).should.be.false

    @all2012.closed.contains(@mid2012.toYear).should.be.true

  it 'should throw when the argument is an interval', ->
    try
      @all2012.closed.contains @all2012
      should.fail
    catch e
      (e?).should.be.true

describe 'DateTimeInterval.includes', ->
  @beforeEach ->
    setup @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @dIvl.sameAs
    x.closed.includes(y.closed).should.be.true
    x.closed.includes(y.open).should.be.true
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.true
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @dIvl.before
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.false
    y.closed.includes(x.open).should.be.false
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @dIvl.meets
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.false
    y.closed.includes(x.open).should.be.false
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @dIvl.overlaps
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.false
    y.closed.includes(x.open).should.be.false
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.false

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @dIvl.begins
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.true

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @dIvl.during
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.true
    y.open.includes(x.open).should.be.true

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @dIvl.ends
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.true

  it 'should properly handle imprecision', ->

    [x, y] = xy @dIvl.sameAs
    x.closed.includes(y.toMinute).should.be.true
    should.not.exist x.toHour.includes(y.toMinute)

    [x, y] = xy @dIvl.before
    x.toMonth.includes(y.toMonth).should.be.false
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @dIvl.meets
    x.toMonth.includes(y.toMonth).should.be.false
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @dIvl.overlaps
    x.toMonth.includes(y.toMonth).should.be.false
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @dIvl.begins
    x.toMinute.includes(y.toMinute).should.be.false
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @dIvl.during
    x.toMonth.includes(y.toMonth).should.be.false
    y.toMonth.includes(x.toMonth).should.be.true
    should.not.exist x.toYear.includes(y.closed)

    [x, y] = xy @dIvl.ends
    x.toMinute.includes(y.toMinute).should.be.false
    should.not.exist x.toYear.includes(y.closed)

  it 'should throw when the argument is a point', ->
    try
      @all2012.closed.includes @mid2012
      should.fail
    catch e
      (e?).should.be.true

describe 'DateTimeInterval.includedIn', ->
  @beforeEach ->
    setup @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @dIvl.sameAs
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true

    y.closed.includedIn(x.closed).should.be.true
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.true
    y.open.includedIn(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @dIvl.before
    x.closed.includedIn(y.closed).should.be.false
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.false
    x.open.includedIn(y.open).should.be.false
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @dIvl.meets
    x.closed.includedIn(y.closed).should.be.false
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.false
    x.open.includedIn(y.open).should.be.false
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @dIvl.overlaps
    x.closed.includedIn(y.closed).should.be.false
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.false
    x.open.includedIn(y.open).should.be.false
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @dIvl.begins
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @dIvl.during
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.true
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @dIvl.ends
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly handle imprecision', ->
    [x, y] = xy @dIvl.sameAs
    should.not.exist x.closed.includedIn(y.toMinute)
    should.not.exist x.toHour.includedIn(y.toMinute)

    [x, y] = xy @dIvl.before
    x.toMonth.includedIn(y.toMonth).should.be.false
    should.not.exist x.toYear.includedIn(y.closed)

    [x, y] = xy @dIvl.meets
    x.toMonth.includedIn(y.toMonth).should.be.false
    should.not.exist x.toYear.includedIn(y.closed)

    [x, y] = xy @dIvl.overlaps
    x.toMonth.includedIn(y.toMonth).should.be.false
    should.not.exist x.toYear.includedIn(y.closed)

    [x, y] = xy @dIvl.begins
    should.not.exist x.toMinute.includedIn(y.toMinute)
    x.toYear.includedIn(y.closed).should.be.true

    [x, y] = xy @dIvl.during
    x.toMonth.includedIn(y.toMonth).should.be.true
    y.toMonth.includedIn(x.toMonth).should.be.false
    x.toYear.includedIn(y.closed).should.be.true

    [x, y] = xy @dIvl.ends
    should.not.exist x.toMinute.includedIn(y.toMinute)
    x.toYear.includedIn(y.closed).should.be.true

  it 'should throw when the argument is a point', ->
    try
      @all2012.closed.includedIn @mid2012
      should.fail
    catch e
      (e?).should.be.true

describe 'DateTimeInterval.overlaps(DateTimeInterval)', ->
  @beforeEach ->
    setup @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @dIvl.sameAs
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @dIvl.before
    x.closed.overlaps(y.closed).should.be.false
    x.closed.overlaps(y.open).should.be.false
    x.open.overlaps(y.closed).should.be.false
    x.open.overlaps(y.open).should.be.false
    y.closed.overlaps(x.closed).should.be.false
    y.closed.overlaps(x.open).should.be.false
    y.open.overlaps(x.closed).should.be.false
    y.open.overlaps(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @dIvl.meets
    x.closed.overlaps(y.closed).should.be.false
    x.closed.overlaps(y.open).should.be.false
    x.open.overlaps(y.closed).should.be.false
    x.open.overlaps(y.open).should.be.false
    y.closed.overlaps(x.closed).should.be.false
    y.closed.overlaps(x.open).should.be.false
    y.open.overlaps(x.closed).should.be.false
    y.open.overlaps(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @dIvl.overlaps
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @dIvl.begins
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @dIvl.during
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @dIvl.ends
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly handle imprecision', ->
    [x, y] = xy @dIvl.sameAs
    x.closed.overlaps(y.toMinute).should.be.true
    x.toHour.overlaps(y.toMinute).should.be.true

    [x, y] = xy @dIvl.before
    x.toMonth.overlaps(y.toMonth).should.be.false
    should.not.exist x.toYear.overlaps(y.closed)

    [x, y] = xy @dIvl.meets
    x.toMonth.overlaps(y.toMonth).should.be.false
    should.not.exist x.toYear.overlaps(y.closed)

    [x, y] = xy @dIvl.overlaps
    x.toMonth.overlaps(y.toMonth).should.be.true
    should.not.exist x.toYear.overlaps(y.closed)

    [x, y] = xy @dIvl.begins
    x.toMinute.overlaps(y.toMinute).should.be.true
    x.toYear.overlaps(y.closed).should.be.true

    [x, y] = xy @dIvl.during
    x.toMonth.overlaps(y.toMonth).should.be.true
    y.toMonth.overlaps(x.toMonth).should.be.true
    x.toYear.overlaps(y.closed).should.be.true

    [x, y] = xy @dIvl.ends
    x.toMinute.overlaps(y.toMinute).should.be.true
    x.toYear.overlaps(y.closed).should.be.true

describe 'DateTimeInterval.overlaps(DateTime)', ->
  @beforeEach ->
    setup @

  it 'should properly calculate dates before it', ->
    @all2012.closed.overlaps(@bef2012.full).should.be.false

  it 'should properly calculate the left boundary date', ->
    @all2012.closed.overlaps(@beg2012.full).should.be.true
    @all2012.open.overlaps(@beg2012.full).should.be.false

  it 'should properly calculate dates in the middle of it', ->
    @all2012.closed.overlaps(@mid2012.full).should.be.true

  it 'should properly calculate the right boundary date', ->
    @all2012.closed.overlaps(@end2012.full).should.be.true
    @all2012.open.overlaps(@end2012.full).should.be.false

  it 'should properly calculate dates after it', ->
    @all2012.closed.overlaps(@aft2012.full).should.be.false

  it 'should properly handle imprecision', ->
    @all2012.closed.overlaps(@bef2012.toMonth).should.be.false
    @all2012.closed.overlaps(@beg2012.toMonth).should.be.true
    @all2012.closed.overlaps(@mid2012.toMonth).should.be.true
    @all2012.closed.overlaps(@end2012.toMonth).should.be.true
    @all2012.closed.overlaps(@aft2012.toMonth).should.be.false

    @all2012.toMonth.overlaps(@bef2012.toMonth).should.be.false
    should.not.exist @all2012.toMonth.overlaps(@beg2012.toMonth)
    @all2012.toMonth.overlaps(@mid2012.toMonth).should.be.true
    should.not.exist @all2012.toMonth.overlaps(@end2012.toMonth)
    @all2012.toMonth.overlaps(@aft2012.toMonth).should.be.false

    @all2012.toMonth.overlaps(@bef2012.full).should.be.false
    should.not.exist @all2012.toMonth.overlaps(@beg2012.full)
    @all2012.toMonth.overlaps(@mid2012.full).should.be.true
    should.not.exist @all2012.toMonth.overlaps(@end2012.full)
    @all2012.toMonth.overlaps(@aft2012.full).should.be.false

    @all2012.closed.overlaps(@mid2012.toYear).should.be.true

describe 'DateTimeInterval.equals', ->
  @beforeEach ->
    setup @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @dIvl.sameAs
    x.closed.equals(y.closed).should.be.true
    x.closed.equals(y.open).should.be.false
    x.open.equals(y.closed).should.be.false
    x.open.equals(y.open).should.be.true
    y.closed.equals(x.closed).should.be.true
    y.closed.equals(x.open).should.be.false
    y.open.equals(x.closed).should.be.false
    y.open.equals(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @dIvl.before
    x.closed.equals(y.closed).should.be.false
    x.closed.equals(y.open).should.be.false
    x.open.equals(y.closed).should.be.false
    x.open.equals(y.open).should.be.false
    y.closed.equals(x.closed).should.be.false
    y.closed.equals(x.open).should.be.false
    y.open.equals(x.closed).should.be.false
    y.open.equals(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @dIvl.meets
    x.closed.equals(y.closed).should.be.false
    x.closed.equals(y.open).should.be.false
    x.open.equals(y.closed).should.be.false
    x.open.equals(y.open).should.be.false
    y.closed.equals(x.closed).should.be.false
    y.closed.equals(x.open).should.be.false
    y.open.equals(x.closed).should.be.false
    y.open.equals(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @dIvl.overlaps
    x.closed.equals(y.closed).should.be.false
    x.closed.equals(y.open).should.be.false
    x.open.equals(y.closed).should.be.false
    x.open.equals(y.open).should.be.false
    y.closed.equals(x.closed).should.be.false
    y.closed.equals(x.open).should.be.false
    y.open.equals(x.closed).should.be.false
    y.open.equals(x.open).should.be.false

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @dIvl.begins
    x.closed.equals(y.closed).should.be.false
    x.closed.equals(y.open).should.be.false
    x.open.equals(y.closed).should.be.false
    x.open.equals(y.open).should.be.false
    y.closed.equals(x.closed).should.be.false
    y.closed.equals(x.open).should.be.false
    y.open.equals(x.closed).should.be.false
    y.open.equals(x.open).should.be.false

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @dIvl.during
    x.closed.equals(y.closed).should.be.false
    x.closed.equals(y.open).should.be.false
    x.open.equals(y.closed).should.be.false
    x.open.equals(y.open).should.be.false
    y.closed.equals(x.closed).should.be.false
    y.closed.equals(x.open).should.be.false
    y.open.equals(x.closed).should.be.false
    y.open.equals(x.open).should.be.false

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @dIvl.ends
    x.closed.equals(y.closed).should.be.false
    x.closed.equals(y.open).should.be.false
    x.open.equals(y.closed).should.be.false
    x.open.equals(y.open).should.be.false
    y.closed.equals(x.closed).should.be.false
    y.closed.equals(x.open).should.be.false
    y.open.equals(x.closed).should.be.false
    y.open.equals(x.open).should.be.false

  it 'should properly calculate open vs. closed intervals', ->
    lowEdge2012 = DateTime.parse('2012-01-01T00:00:00.0+00')
    lowEdge2012Succ = DateTime.parse('2012-01-01T00:00:00.001+00')
    highEdge2012 = DateTime.parse('2012-12-31T23:59:59.999+00')
    highEdge2012Pred = DateTime.parse('2012-12-31T23:59:59.998+00')
    cc = new Interval(lowEdge2012, highEdge2012, true, true)
    oc = new Interval(lowEdge2012, highEdge2012, false, true)
    co = new Interval(lowEdge2012, highEdge2012, true, false)
    oo = new Interval(lowEdge2012, highEdge2012, false, false)
    cci = new Interval(lowEdge2012Succ, highEdge2012Pred, true, true)
    oci = new Interval(lowEdge2012Succ, highEdge2012Pred, false, true)
    coi = new Interval(lowEdge2012Succ, highEdge2012Pred, true, false)
    ooi = new Interval(lowEdge2012Succ, highEdge2012Pred, false, false)

    oo.equals(oo).should.be.true
    oo.equals(cc).should.be.false
    oo.equals(cci).should.be.true
    oo.equals(oci).should.be.false
    oo.equals(coi).should.be.false
    oo.equals(ooi).should.be.false
    cci.equals(cci).should.be.true
    cci.equals(oo).should.be.true
    cci.equals(co).should.be.false
    cci.equals(oc).should.be.false
    cci.equals(cc).should.be.false

  it 'should properly handle imprecision', ->
    [x, y] = xy @dIvl.sameAs
    should(x.closed.equals(y.toMinute)).be.null
    should(x.toHour.equals(y.toMinute)).be.null

    [x, y] = xy @dIvl.before
    x.toMonth.equals(y.toMonth).should.be.false
    should(x.toYear.equals(y.closed)).be.null

    [x, y] = xy @dIvl.meets
    x.toMonth.equals(y.toMonth).should.be.false
    should(x.toYear.equals(y.closed)).be.null

    [x, y] = xy @dIvl.overlaps
    x.toMonth.equals(y.toMonth).should.be.false
    should(x.toYear.equals(y.closed)).be.null

    [x, y] = xy @dIvl.begins
    x.toMinute.equals(y.toMinute).should.be.false
    should(x.toYear.equals(y.closed)).be.null

    [x, y] = xy @dIvl.during
    x.toMonth.equals(y.toMonth).should.be.false
    y.toMonth.equals(x.toMonth).should.be.false
    should(x.toYear.equals(y.closed)).be.null

    [x, y] = xy @dIvl.ends
    x.toMinute.equals(y.toMinute).should.be.false
    should(x.toYear.equals(y.closed)).be.null

  it 'should be false for equality with points', ->
    point = DateTime.parse('2012-01-01T00:00:00.0+00')
    ivl = new Interval(point, point, true, true)

    ivl.equals(point).should.be.false

describe 'DateTimeInterval.after', ->
  @beforeEach ->
    setup @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @dIvl.sameAs
    x.closed.after(y.closed).should.be.false
    x.closed.after(y.open).should.be.false
    x.open.after(y.closed).should.be.false
    x.open.after(y.open).should.be.false
    y.closed.after(x.closed).should.be.false
    y.closed.after(x.open).should.be.false
    y.open.after(x.closed).should.be.false
    y.open.after(x.open).should.be.false

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @dIvl.before
    x.closed.after(y.closed).should.be.false
    x.closed.after(y.open).should.be.false
    x.open.after(y.closed).should.be.false
    x.open.after(y.open).should.be.false
    y.closed.after(x.closed).should.be.true
    y.closed.after(x.open).should.be.true
    y.open.after(x.closed).should.be.true
    y.open.after(x.open).should.be.true

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @dIvl.meets
    x.closed.after(y.closed).should.be.false
    x.closed.after(y.open).should.be.false
    x.open.after(y.closed).should.be.false
    x.open.after(y.open).should.be.false
    y.closed.after(x.closed).should.be.true
    y.closed.after(x.open).should.be.true
    y.open.after(x.closed).should.be.true
    y.open.after(x.open).should.be.true

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @dIvl.overlaps
    x.closed.after(y.closed).should.be.false
    x.closed.after(y.open).should.be.false
    x.open.after(y.closed).should.be.false
    x.open.after(y.open).should.be.false
    y.closed.after(x.closed).should.be.false
    y.closed.after(x.open).should.be.false
    y.open.after(x.closed).should.be.false
    y.open.after(x.open).should.be.false

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @dIvl.begins
    x.closed.after(y.closed).should.be.false
    x.closed.after(y.open).should.be.false
    x.open.after(y.closed).should.be.false
    x.open.after(y.open).should.be.false
    y.closed.after(x.closed).should.be.false
    y.closed.after(x.open).should.be.false
    y.open.after(x.closed).should.be.false
    y.open.after(x.open).should.be.false

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @dIvl.during
    x.closed.after(y.closed).should.be.false
    x.closed.after(y.open).should.be.false
    x.open.after(y.closed).should.be.false
    x.open.after(y.open).should.be.false
    y.closed.after(x.closed).should.be.false
    y.closed.after(x.open).should.be.false
    y.open.after(x.closed).should.be.false
    y.open.after(x.open).should.be.false

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @dIvl.ends
    x.closed.after(y.closed).should.be.false
    x.closed.after(y.open).should.be.false
    x.open.after(y.closed).should.be.false
    x.open.after(y.open).should.be.false
    y.closed.after(x.closed).should.be.false
    y.closed.after(x.open).should.be.false
    y.open.after(x.closed).should.be.false
    y.open.after(x.open).should.be.false

  it 'should properly handle imprecision', ->
    [x, y] = xy @dIvl.sameAs
    x.closed.after(y.toMinute).should.be.false
    x.toHour.after(y.toMinute).should.be.false

    [x, y] = xy @dIvl.before
    x.toMonth.after(y.toMonth).should.be.false
    x.toYear.after(y.closed).should.be.false
    should.not.exist y.toYear.after(x.closed)

    [x, y] = xy @dIvl.meets
    x.toMonth.after(y.toMonth).should.be.false
    x.toYear.after(y.closed).should.be.false
    should.not.exist y.toYear.after(x.closed)

    [x, y] = xy @dIvl.overlaps
    x.toMonth.after(y.toMonth).should.be.false
    x.toYear.after(y.closed).should.be.false
    should.not.exist y.toYear.after(x.closed)

    [x, y] = xy @dIvl.begins
    x.toMinute.after(y.toMinute).should.be.false
    x.toYear.after(y.closed).should.be.false
    should.not.exist y.toYear.after(x.closed)

    [x, y] = xy @dIvl.during
    x.toMonth.after(y.toMonth).should.be.false
    y.toMonth.after(x.toMonth).should.be.false
    x.toYear.after(y.closed).should.be.false
    should.not.exist y.toYear.after(x.closed)

    [x, y] = xy @dIvl.ends
    x.toMinute.after(y.toMinute).should.be.false
    x.toYear.after(y.closed).should.be.false
    x.toYear.after(x.closed).should.be.false

describe 'DateTimeInterval.before', ->
  @beforeEach ->
    setup @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @dIvl.sameAs
    x.closed.before(y.closed).should.be.false
    x.closed.before(y.open).should.be.false
    x.open.before(y.closed).should.be.false
    x.open.before(y.open).should.be.false
    y.closed.before(x.closed).should.be.false
    y.closed.before(x.open).should.be.false
    y.open.before(x.closed).should.be.false
    y.open.before(x.open).should.be.false

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @dIvl.before
    x.closed.before(y.closed).should.be.true
    x.closed.before(y.open).should.be.true
    x.open.before(y.closed).should.be.true
    x.open.before(y.open).should.be.true
    y.closed.before(x.closed).should.be.false
    y.closed.before(x.open).should.be.false
    y.open.before(x.closed).should.be.false
    y.open.before(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @dIvl.meets
    x.closed.before(y.closed).should.be.true
    x.closed.before(y.open).should.be.true
    x.open.before(y.closed).should.be.true
    x.open.before(y.open).should.be.true
    y.closed.before(x.closed).should.be.false
    y.closed.before(x.open).should.be.false
    y.open.before(x.closed).should.be.false
    y.open.before(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @dIvl.overlaps
    x.closed.before(y.closed).should.be.false
    x.closed.before(y.open).should.be.false
    x.open.before(y.closed).should.be.false
    x.open.before(y.open).should.be.false
    y.closed.before(x.closed).should.be.false
    y.closed.before(x.open).should.be.false
    y.open.before(x.closed).should.be.false
    y.open.before(x.open).should.be.false

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @dIvl.begins
    x.closed.before(y.closed).should.be.false
    x.closed.before(y.open).should.be.false
    x.open.before(y.closed).should.be.false
    x.open.before(y.open).should.be.false
    y.closed.before(x.closed).should.be.false
    y.closed.before(x.open).should.be.false
    y.open.before(x.closed).should.be.false
    y.open.before(x.open).should.be.false

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @dIvl.during
    x.closed.before(y.closed).should.be.false
    x.closed.before(y.open).should.be.false
    x.open.before(y.closed).should.be.false
    x.open.before(y.open).should.be.false
    y.closed.before(x.closed).should.be.false
    y.closed.before(x.open).should.be.false
    y.open.before(x.closed).should.be.false
    y.open.before(x.open).should.be.false

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @dIvl.ends
    x.closed.before(y.closed).should.be.false
    x.closed.before(y.open).should.be.false
    x.open.before(y.closed).should.be.false
    x.open.before(y.open).should.be.false
    y.closed.before(x.closed).should.be.false
    y.closed.before(x.open).should.be.false
    y.open.before(x.closed).should.be.false
    y.open.before(x.open).should.be.false

  it 'should properly handle imprecision', ->
    [x, y] = xy @dIvl.sameAs
    x.closed.before(y.toMinute).should.be.false
    x.toHour.before(y.toMinute).should.be.false

    [x, y] = xy @dIvl.before
    x.toMonth.before(y.toMonth).should.be.true
    y.toYear.before(x.closed).should.be.false
    should.not.exist x.toYear.before(y.closed)

    [x, y] = xy @dIvl.meets
    x.toMonth.before(y.toMonth).should.be.true
    y.toYear.before(x.closed).should.be.false
    should.not.exist x.toYear.before(y.closed)

    [x, y] = xy @dIvl.overlaps
    x.toMonth.before(y.toMonth).should.be.false
    y.toYear.before(x.closed).should.be.false
    should.not.exist x.toYear.before(y.closed)

    [x, y] = xy @dIvl.begins
    x.toMinute.before(y.toMinute).should.be.false
    y.toYear.before(x.closed).should.be.false
    x.toYear.before(y.closed).should.be.false

    [x, y] = xy @dIvl.during
    x.toMonth.before(y.toMonth).should.be.false
    y.toMonth.before(x.toMonth).should.be.false
    should.not.exist y.toYear.before(x.closed)
    x.toYear.before(y.closed).should.be.false

    [x, y] = xy @dIvl.ends
    x.toMinute.before(y.toMinute).should.be.false
    should.not.exist y.toYear.before(x.closed)
    x.toYear.before(y.closed).should.be.false

describe 'IntegerInterval.contains', ->
  @beforeEach ->
    setup @

  it 'should properly calculate integers less than it', ->
    @zeroToHundred.closed.contains(-5).should.be.false

  it 'should properly calculate the left boundary integer', ->
    @zeroToHundred.closed.contains(0).should.be.true
    @zeroToHundred.open.contains(0).should.be.false

  it 'should properly calculate integers in the middle of it', ->
    @zeroToHundred.closed.contains(50).should.be.true

  it 'should properly calculate the right boundary integer', ->
    @zeroToHundred.closed.contains(100).should.be.true
    @zeroToHundred.open.contains(100).should.be.false

  it 'should properly calculate integers greater than it', ->
    @zeroToHundred.closed.contains(105).should.be.false

  it 'should properly handle null endpoints', ->
    new Interval(null, 0).contains(-123456789).should.be.true
    new Interval(null, 0).contains(1).should.be.false
    new Interval(null,0,false,true).contains(0).should.be.true
    should(new Interval(null,0,false,true).contains(-123456789)).be.null
    new Interval(null,0,false,true).contains(1).should.be.false
    new Interval(0,null).contains(123456789).should.be.true
    new Interval(0,null).contains(-1).should.be.false
    new Interval(0,null,true,false).contains(0).should.be.true
    should(new Interval(0,null,true,false).contains(123456789)).be.null
    new Interval(0,null,true,false).contains(-1).should.be.false

  it 'should properly handle imprecision', ->
    @zeroToHundred.closed.contains(new Uncertainty(-20,-10)).should.be.false
    should.not.exist @zeroToHundred.closed.contains(new Uncertainty(-20,20))
    @zeroToHundred.closed.contains(new Uncertainty(0,100)).should.be.true
    should.not.exist @zeroToHundred.closed.contains(new Uncertainty(80,120))
    @zeroToHundred.closed.contains(new Uncertainty(120,140)).should.be.false
    should.not.exist @zeroToHundred.closed.contains(new Uncertainty(-20,120))

    uIvl = new Interval(new Uncertainty(5,10), new Uncertainty(15, 20))

    uIvl.contains(0).should.be.false
    should.not.exist uIvl.contains(5)
    should.not.exist uIvl.contains(6)
    uIvl.contains(10).should.be.true
    uIvl.contains(12).should.be.true
    uIvl.contains(15).should.be.true
    should.not.exist uIvl.contains(16)
    should.not.exist uIvl.contains(20)
    uIvl.contains(25).should.be.false

    uIvl.contains(new Uncertainty(0,4)).should.be.false
    should.not.exist uIvl.contains(new Uncertainty(0,5))
    should.not.exist uIvl.contains(new Uncertainty(5,10))
    uIvl.contains(new Uncertainty(10,15)).should.be.true
    should.not.exist uIvl.contains(new Uncertainty(15,20))
    should.not.exist uIvl.contains(new Uncertainty(20,25))
    uIvl.contains(new Uncertainty(25,30)).should.be.false

  it 'should throw when the argument is an interval', ->
    try
      @zeroToHundred.closed.contains new Interval(5, 10)
      should.fail
    catch e
      (e?).should.be.true

describe 'IntegerInterval.includes', ->
  @beforeEach ->
    setup @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @iIvl.sameAs
    x.closed.includes(y.closed).should.be.true
    x.closed.includes(y.open).should.be.true
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.true
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @iIvl.before
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.false
    y.closed.includes(x.open).should.be.false
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @iIvl.meets
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.false
    y.closed.includes(x.open).should.be.false
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @iIvl.overlaps
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.false
    y.closed.includes(x.open).should.be.false
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.false

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @iIvl.begins
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.true

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @iIvl.during
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.true
    y.open.includes(x.open).should.be.true

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @iIvl.ends
    x.closed.includes(y.closed).should.be.false
    x.closed.includes(y.open).should.be.false
    x.open.includes(y.closed).should.be.false
    x.open.includes(y.open).should.be.false
    y.closed.includes(x.closed).should.be.true
    y.closed.includes(x.open).should.be.true
    y.open.includes(x.closed).should.be.false
    y.open.includes(x.open).should.be.true

  it 'should properly handle imprecision', ->
    uIvl = new Interval(new Uncertainty(5,10), new Uncertainty(15, 20))

    ivl = new Interval(0, 100)
    ivl.includes(uIvl).should.be.true
    uIvl.includes(ivl).should.be.false

    ivl = new Interval(-100, 0)
    ivl.includes(uIvl).should.be.false
    uIvl.includes(ivl).should.be.false

    ivl = new Interval(10, 15)
    should.not.exist ivl.includes(uIvl)
    uIvl.includes(ivl).should.be.true

    ivl = new Interval(5, 20)
    ivl.includes(uIvl).should.be.true
    should.not.exist uIvl.includes(ivl)

    should.not.exist uIvl.includes(uIvl)

  it 'should throw when the argument is a point', ->
    try
      @zeroToHundred.closed.includes 50
      should.fail
    catch e
      (e?).should.be.true

describe 'IntegerInterval.includedIn', ->
  @beforeEach ->
    setup @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @iIvl.sameAs
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true

    y.closed.includedIn(x.closed).should.be.true
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.true
    y.open.includedIn(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @iIvl.before
    x.closed.includedIn(y.closed).should.be.false
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.false
    x.open.includedIn(y.open).should.be.false
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @iIvl.meets
    x.closed.includedIn(y.closed).should.be.false
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.false
    x.open.includedIn(y.open).should.be.false
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @iIvl.overlaps
    x.closed.includedIn(y.closed).should.be.false
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.false
    x.open.includedIn(y.open).should.be.false
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @iIvl.begins
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @iIvl.during
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.true
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @iIvl.ends
    x.closed.includedIn(y.closed).should.be.true
    x.closed.includedIn(y.open).should.be.false
    x.open.includedIn(y.closed).should.be.true
    x.open.includedIn(y.open).should.be.true
    y.closed.includedIn(x.closed).should.be.false
    y.closed.includedIn(x.open).should.be.false
    y.open.includedIn(x.closed).should.be.false
    y.open.includedIn(x.open).should.be.false

  it 'should properly handle imprecision', ->
    uIvl = new Interval(new Uncertainty(5,10), new Uncertainty(15, 20))

    ivl = new Interval(0, 100)
    ivl.includedIn(uIvl).should.be.false
    uIvl.includedIn(ivl).should.be.true

    ivl = new Interval(-100, 0)
    ivl.includedIn(uIvl).should.be.false
    uIvl.includedIn(ivl).should.be.false

    ivl = new Interval(10, 15)
    ivl.includedIn(uIvl).should.be.true
    should.not.exist uIvl.includedIn(ivl)

    ivl = new Interval(5, 20)
    should.not.exist ivl.includedIn(uIvl)
    uIvl.includedIn(ivl).should.be.true

    should.not.exist uIvl.includedIn(uIvl)

  it 'should throw when the argument is a point', ->
    try
      @zeroToHundred.closed.includedIn 50
      should.fail
    catch e
      (e?).should.be.true

describe 'IntegerInterval.overlaps(IntegerInterval)', ->
  @beforeEach ->
    setup @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @iIvl.sameAs
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @iIvl.before
    x.closed.overlaps(y.closed).should.be.false
    x.closed.overlaps(y.open).should.be.false
    x.open.overlaps(y.closed).should.be.false
    x.open.overlaps(y.open).should.be.false
    y.closed.overlaps(x.closed).should.be.false
    y.closed.overlaps(x.open).should.be.false
    y.open.overlaps(x.closed).should.be.false
    y.open.overlaps(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @iIvl.meets
    x.closed.overlaps(y.closed).should.be.false
    x.closed.overlaps(y.open).should.be.false
    x.open.overlaps(y.closed).should.be.false
    x.open.overlaps(y.open).should.be.false
    y.closed.overlaps(x.closed).should.be.false
    y.closed.overlaps(x.open).should.be.false
    y.open.overlaps(x.closed).should.be.false
    y.open.overlaps(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @iIvl.overlaps
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @iIvl.begins
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @iIvl.during
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @iIvl.ends
    x.closed.overlaps(y.closed).should.be.true
    x.closed.overlaps(y.open).should.be.true
    x.open.overlaps(y.closed).should.be.true
    x.open.overlaps(y.open).should.be.true
    y.closed.overlaps(x.closed).should.be.true
    y.closed.overlaps(x.open).should.be.true
    y.open.overlaps(x.closed).should.be.true
    y.open.overlaps(x.open).should.be.true

  it 'should properly handle imprecision', ->
    uIvl = new Interval(new Uncertainty(5,10), new Uncertainty(15, 20))

    ivl = new Interval(0, 100)
    ivl.overlaps(uIvl).should.be.true
    uIvl.overlaps(ivl).should.be.true

    ivl = new Interval(-100, 0)
    ivl.overlaps(uIvl).should.be.false
    uIvl.overlaps(ivl).should.be.false

    ivl = new Interval(10, 15)
    ivl.overlaps(uIvl).should.be.true
    uIvl.overlaps(ivl).should.be.true

    ivl = new Interval(5, 20)
    ivl.overlaps(uIvl).should.be.true
    uIvl.overlaps(ivl).should.be.true

    uIvl.overlaps(uIvl).should.be.true

describe 'IntegerInterval.overlaps(Integer)', ->
  @beforeEach ->
    setup @

  it 'should properly calculate integers less than it', ->
    @zeroToHundred.closed.overlaps(-5).should.be.false

  it 'should properly calculate the left boundary integer', ->
    @zeroToHundred.closed.overlaps(0).should.be.true
    @zeroToHundred.open.overlaps(0).should.be.false

  it 'should properly calculate integers in the middle of it', ->
    @zeroToHundred.closed.overlaps(50).should.be.true

  it 'should properly calculate the right boundary integer', ->
    @zeroToHundred.closed.overlaps(100).should.be.true
    @zeroToHundred.open.overlaps(100).should.be.false

  it 'should properly calculate integers greater than it', ->
    @zeroToHundred.closed.overlaps(105).should.be.false

  it 'should properly handle imprecision', ->
    @zeroToHundred.closed.overlaps(new Uncertainty(-20,-10)).should.be.false
    should.not.exist @zeroToHundred.closed.overlaps(new Uncertainty(-20,20))
    @zeroToHundred.closed.overlaps(new Uncertainty(0,100)).should.be.true
    should.not.exist @zeroToHundred.closed.overlaps(new Uncertainty(80,120))
    @zeroToHundred.closed.overlaps(new Uncertainty(120,140)).should.be.false
    should.not.exist @zeroToHundred.closed.overlaps(new Uncertainty(-20,120))

    uIvl = new Interval(new Uncertainty(5,10), new Uncertainty(15, 20))

    uIvl.overlaps(0).should.be.false
    should.not.exist uIvl.overlaps(5)
    should.not.exist uIvl.overlaps(6)
    uIvl.overlaps(10).should.be.true
    uIvl.overlaps(12).should.be.true
    uIvl.overlaps(15).should.be.true
    should.not.exist uIvl.overlaps(16)
    should.not.exist uIvl.overlaps(20)
    uIvl.overlaps(25).should.be.false

    uIvl.overlaps(new Uncertainty(0,4)).should.be.false
    should.not.exist uIvl.overlaps(new Uncertainty(0,5))
    should.not.exist uIvl.overlaps(new Uncertainty(5,10))
    uIvl.overlaps(new Uncertainty(10,15)).should.be.true
    should.not.exist uIvl.overlaps(new Uncertainty(15,20))
    should.not.exist uIvl.overlaps(new Uncertainty(20,25))
    uIvl.overlaps(new Uncertainty(25,30)).should.be.false

describe 'IntegerInterval.equals', ->
  @beforeEach ->
    setup @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @iIvl.sameAs
    x.closed.equals(y.closed).should.be.true
    x.closed.equals(y.open).should.be.false
    x.open.equals(y.closed).should.be.false
    x.open.equals(y.open).should.be.true
    y.closed.equals(x.closed).should.be.true
    y.closed.equals(x.open).should.be.false
    y.open.equals(x.closed).should.be.false
    y.open.equals(x.open).should.be.true

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @iIvl.before
    x.closed.equals(y.closed).should.be.false
    x.closed.equals(y.open).should.be.false
    x.open.equals(y.closed).should.be.false
    x.open.equals(y.open).should.be.false
    y.closed.equals(x.closed).should.be.false
    y.closed.equals(x.open).should.be.false
    y.open.equals(x.closed).should.be.false
    y.open.equals(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @iIvl.meets
    x.closed.equals(y.closed).should.be.false
    x.closed.equals(y.open).should.be.false
    x.open.equals(y.closed).should.be.false
    x.open.equals(y.open).should.be.false
    y.closed.equals(x.closed).should.be.false
    y.closed.equals(x.open).should.be.false
    y.open.equals(x.closed).should.be.false
    y.open.equals(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @iIvl.overlaps
    x.closed.equals(y.closed).should.be.false
    x.closed.equals(y.open).should.be.false
    x.open.equals(y.closed).should.be.false
    x.open.equals(y.open).should.be.false
    y.closed.equals(x.closed).should.be.false
    y.closed.equals(x.open).should.be.false
    y.open.equals(x.closed).should.be.false
    y.open.equals(x.open).should.be.false

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @iIvl.begins
    x.closed.equals(y.closed).should.be.false
    x.closed.equals(y.open).should.be.false
    x.open.equals(y.closed).should.be.false
    x.open.equals(y.open).should.be.false
    y.closed.equals(x.closed).should.be.false
    y.closed.equals(x.open).should.be.false
    y.open.equals(x.closed).should.be.false
    y.open.equals(x.open).should.be.false

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @iIvl.during
    x.closed.equals(y.closed).should.be.false
    x.closed.equals(y.open).should.be.false
    x.open.equals(y.closed).should.be.false
    x.open.equals(y.open).should.be.false
    y.closed.equals(x.closed).should.be.false
    y.closed.equals(x.open).should.be.false
    y.open.equals(x.closed).should.be.false
    y.open.equals(x.open).should.be.false

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @iIvl.ends
    x.closed.equals(y.closed).should.be.false
    x.closed.equals(y.open).should.be.false
    x.open.equals(y.closed).should.be.false
    x.open.equals(y.open).should.be.false
    y.closed.equals(x.closed).should.be.false
    y.closed.equals(x.open).should.be.false
    y.open.equals(x.closed).should.be.false
    y.open.equals(x.open).should.be.false

  it 'should properly calculate open vs. closed intervals', ->
    c2c5 = new Interval(2, 5, true, true)
    o2c5 = new Interval(2, 5, false, true)
    c2o5 = new Interval(2, 5, true, false)
    o2o5 = new Interval(2, 5, false, false)
    c1c6 = new Interval(1, 6, true, true)
    o1c6 = new Interval(1, 6, false, true)
    c1o6 = new Interval(1, 6, true, false)
    o1o6 = new Interval(1, 6, false, false)

    c2c5.equals(o2o5).should.be.false
    c2c5.equals(c1c6).should.be.false
    c2c5.equals(o1c6).should.be.false
    c2c5.equals(c1o6).should.be.false
    c2c5.equals(o1o6).should.be.true
    o1o6.equals(c1c6).should.be.false
    o1o6.equals(c2c5).should.be.true
    o1o6.equals(o2c5).should.be.false
    o1o6.equals(c2o5).should.be.false
    o1o6.equals(o2o5).should.be.false

  it 'should properly handle imprecision', ->
    uIvl = new Interval(new Uncertainty(5,10), new Uncertainty(15, 20))

    ivl = new Interval(0, 100)
    ivl.equals(uIvl).should.be.false
    uIvl.equals(ivl).should.be.false

    ivl = new Interval(-100, 0)
    ivl.equals(uIvl).should.be.false
    uIvl.equals(ivl).should.be.false

    ivl = new Interval(10, 15)
    should(ivl.equals(uIvl)).be.null
    should(uIvl.equals(ivl)).be.null

    ivl = new Interval(5, 20)
    should(ivl.equals(uIvl)).be.null
    should(uIvl.equals(ivl)).be.null

    should(uIvl.equals(uIvl)).be.null

  it 'should be false for equality with points', ->
    point = 3
    ivl = new Interval(point, point, true, true)

    ivl.equals(point).should.be.false

describe 'IntegerInterval.after', ->
  @beforeEach ->
    setup @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @iIvl.sameAs
    x.closed.after(y.closed).should.be.false
    x.closed.after(y.open).should.be.false
    x.open.after(y.closed).should.be.false
    x.open.after(y.open).should.be.false
    y.closed.after(x.closed).should.be.false
    y.closed.after(x.open).should.be.false
    y.open.after(x.closed).should.be.false
    y.open.after(x.open).should.be.false

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @iIvl.before
    x.closed.after(y.closed).should.be.false
    x.closed.after(y.open).should.be.false
    x.open.after(y.closed).should.be.false
    x.open.after(y.open).should.be.false
    y.closed.after(x.closed).should.be.true
    y.closed.after(x.open).should.be.true
    y.open.after(x.closed).should.be.true
    y.open.after(x.open).should.be.true

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @iIvl.meets
    x.closed.after(y.closed).should.be.false
    x.closed.after(y.open).should.be.false
    x.open.after(y.closed).should.be.false
    x.open.after(y.open).should.be.false
    y.closed.after(x.closed).should.be.true
    y.closed.after(x.open).should.be.true
    y.open.after(x.closed).should.be.true
    y.open.after(x.open).should.be.true

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @iIvl.overlaps
    x.closed.after(y.closed).should.be.false
    x.closed.after(y.open).should.be.false
    x.open.after(y.closed).should.be.false
    x.open.after(y.open).should.be.false
    y.closed.after(x.closed).should.be.false
    y.closed.after(x.open).should.be.false
    y.open.after(x.closed).should.be.false
    y.open.after(x.open).should.be.false

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @iIvl.begins
    x.closed.after(y.closed).should.be.false
    x.closed.after(y.open).should.be.false
    x.open.after(y.closed).should.be.false
    x.open.after(y.open).should.be.false
    y.closed.after(x.closed).should.be.false
    y.closed.after(x.open).should.be.false
    y.open.after(x.closed).should.be.false
    y.open.after(x.open).should.be.false

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @iIvl.during
    x.closed.after(y.closed).should.be.false
    x.closed.after(y.open).should.be.false
    x.open.after(y.closed).should.be.false
    x.open.after(y.open).should.be.false
    y.closed.after(x.closed).should.be.false
    y.closed.after(x.open).should.be.false
    y.open.after(x.closed).should.be.false
    y.open.after(x.open).should.be.false

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @iIvl.ends
    x.closed.after(y.closed).should.be.false
    x.closed.after(y.open).should.be.false
    x.open.after(y.closed).should.be.false
    x.open.after(y.open).should.be.false
    y.closed.after(x.closed).should.be.false
    y.closed.after(x.open).should.be.false
    y.open.after(x.closed).should.be.false
    y.open.after(x.open).should.be.false

  it 'should properly handle imprecision', ->
    uIvl = new Interval(new Uncertainty(5,10), new Uncertainty(15, 20))

    ivl = new Interval(0, 100)
    ivl.after(uIvl).should.be.false
    uIvl.after(ivl).should.be.false

    ivl = new Interval(-100, 0)
    ivl.after(uIvl).should.be.false
    uIvl.after(ivl).should.be.true

    ivl = new Interval(10, 15)
    ivl.after(uIvl).should.be.false
    uIvl.after(ivl).should.be.false

    ivl = new Interval(15, 20)
    ivl.after(uIvl).should.be.false
    uIvl.after(ivl).should.be.false

    ivl = new Interval(20, 30)
    should.not.exist ivl.after(uIvl)
    uIvl.after(ivl).should.be.false

    ivl = new Interval(5, 20)
    ivl.after(uIvl).should.be.false
    uIvl.after(ivl).should.be.false

    uIvl.after(uIvl).should.be.false

describe 'IntegerInterval.before', ->
  @beforeEach ->
    setup @

  it 'should properly calculate sameAs intervals', ->
    [x, y] = xy @iIvl.sameAs
    x.closed.before(y.closed).should.be.false
    x.closed.before(y.open).should.be.false
    x.open.before(y.closed).should.be.false
    x.open.before(y.open).should.be.false
    y.closed.before(x.closed).should.be.false
    y.closed.before(x.open).should.be.false
    y.open.before(x.closed).should.be.false
    y.open.before(x.open).should.be.false

  it 'should properly calculate before/after intervals', ->
    [x, y] = xy @iIvl.before
    x.closed.before(y.closed).should.be.true
    x.closed.before(y.open).should.be.true
    x.open.before(y.closed).should.be.true
    x.open.before(y.open).should.be.true
    y.closed.before(x.closed).should.be.false
    y.closed.before(x.open).should.be.false
    y.open.before(x.closed).should.be.false
    y.open.before(x.open).should.be.false

  it 'should properly calculate meets intervals', ->
    [x, y] = xy @iIvl.meets
    x.closed.before(y.closed).should.be.true
    x.closed.before(y.open).should.be.true
    x.open.before(y.closed).should.be.true
    x.open.before(y.open).should.be.true
    y.closed.before(x.closed).should.be.false
    y.closed.before(x.open).should.be.false
    y.open.before(x.closed).should.be.false
    y.open.before(x.open).should.be.false

  it 'should properly calculate left/right overlapping intervals', ->
    [x, y] = xy @iIvl.overlaps
    x.closed.before(y.closed).should.be.false
    x.closed.before(y.open).should.be.false
    x.open.before(y.closed).should.be.false
    x.open.before(y.open).should.be.false
    y.closed.before(x.closed).should.be.false
    y.closed.before(x.open).should.be.false
    y.open.before(x.closed).should.be.false
    y.open.before(x.open).should.be.false

  it 'should properly calculate begins/begun by intervals', ->
    [x, y] = xy @iIvl.begins
    x.closed.before(y.closed).should.be.false
    x.closed.before(y.open).should.be.false
    x.open.before(y.closed).should.be.false
    x.open.before(y.open).should.be.false
    y.closed.before(x.closed).should.be.false
    y.closed.before(x.open).should.be.false
    y.open.before(x.closed).should.be.false
    y.open.before(x.open).should.be.false

  it 'should properly calculate includes/included by intervals', ->
    [x, y] = xy @iIvl.during
    x.closed.before(y.closed).should.be.false
    x.closed.before(y.open).should.be.false
    x.open.before(y.closed).should.be.false
    x.open.before(y.open).should.be.false
    y.closed.before(x.closed).should.be.false
    y.closed.before(x.open).should.be.false
    y.open.before(x.closed).should.be.false
    y.open.before(x.open).should.be.false

  it 'should properly calculate ends/ended by intervals', ->
    [x, y] = xy @iIvl.ends
    x.closed.before(y.closed).should.be.false
    x.closed.before(y.open).should.be.false
    x.open.before(y.closed).should.be.false
    x.open.before(y.open).should.be.false
    y.closed.before(x.closed).should.be.false
    y.closed.before(x.open).should.be.false
    y.open.before(x.closed).should.be.false
    y.open.before(x.open).should.be.false

  it 'should properly handle imprecision', ->
    uIvl = new Interval(new Uncertainty(5,10), new Uncertainty(15, 20))

    ivl = new Interval(0, 100)
    ivl.before(uIvl).should.be.false
    uIvl.before(ivl).should.be.false

    ivl = new Interval(-100, 0)
    ivl.before(uIvl).should.be.true
    uIvl.before(ivl).should.be.false

    ivl = new Interval(10, 15)
    ivl.before(uIvl).should.be.false
    uIvl.before(ivl).should.be.false

    ivl = new Interval(15, 20)
    ivl.before(uIvl).should.be.false
    uIvl.before(ivl).should.be.false

    ivl = new Interval(20, 30)
    should.not.exist uIvl.before(ivl)
    ivl.before(uIvl).should.be.false

    ivl = new Interval(5, 20)
    ivl.before(uIvl).should.be.false
    uIvl.before(ivl).should.be.false

    uIvl.before(uIvl).should.be.false

# TODO: Tests for real numbers (i.e., floats)
