should = require 'should'
setup = require '../../setup'
data = require './data'

describe 'Add', ->
  @beforeEach ->
    setup @, data

  it 'should add two numbers', ->
    @onePlusTwo.exec(@ctx).should.equal 3

  it 'should add multiple numbers', ->
    @addMultiple.exec(@ctx).should.equal 55

  it 'should add variables', ->
    @addVariables.exec(@ctx).should.equal 21

describe 'Subtract', ->
  @beforeEach ->
    setup @, data

  it 'should subtract two numbers', ->
    @fiveMinusTwo.exec(@ctx).should.equal 3

  it 'should subtract multiple numbers', ->
    @subtractMultiple.exec(@ctx).should.equal 15

  it 'should subtract variables', ->
    @subtractVariables.exec(@ctx).should.equal 1

describe 'Multiply', ->
  @beforeEach ->
    setup @, data

  it 'should multiply two numbers', ->
    @fiveTimesTwo.exec(@ctx).should.equal 10

  it 'should multiply multiple numbers', ->
    @multiplyMultiple.exec(@ctx).should.equal 120

  it 'should multiply variables', ->
    @multiplyVariables.exec(@ctx).should.equal 110

describe 'Divide', ->
  @beforeEach ->
    setup @, data

  it 'should divide two numbers', ->
    @tenDividedByTwo.exec(@ctx).should.equal 5

  it 'should divide two numbers that don\'t evenly divide', ->
    @tenDividedByFour.exec(@ctx).should.equal 2.5

  it 'should divide multiple numbers', ->
    @divideMultiple.exec(@ctx).should.equal 5

  it 'should divide variables', ->
    @divideVariables.exec(@ctx).should.equal 25

describe 'Negate', ->
  @beforeEach ->
    setup @, data

  it 'should negate a number', ->
    @negativeOne.exec(@ctx).should.equal -1

describe 'MathPrecedence', ->
  @beforeEach ->
    setup @, data

  it 'should follow order of operations', ->
    @mixed.exec(@ctx).should.equal 46

  it 'should allow parentheses to override order of operations', ->
    @parenthetical.exec(@ctx).should.equal -10

describe  'Power', ->
  @beforeEach ->
    setup @, data

  it "should be able to calculate the power of a number" , ->
    @pow.exec(@ctx).should.equal 81

describe 'TruncatedDivide', ->
  @beforeEach ->
    setup @, data

  it "should be able to return just the integer portion of a division", ->
    @trunc.exec(@ctx).should.equal 3
    @even.exec(@ctx).should.equal 3

describe  'Truncate', ->
  @beforeEach ->
    setup @, data

  it "should be able to return the integer portion of a number", ->
    @trunc.exec(@ctx).should.equal 10
    @even.exec(@ctx).should.equal 10

describe  'Floor', ->
  @beforeEach ->
    setup @, data

  it "should be able to round down to the closest integer", ->
    @flr.exec(@ctx).should.equal 10
    @even.exec(@ctx).should.equal 10

describe 'Ceiling', ->
  @beforeEach ->
    setup @, data

    it "should be able to round up to the closest integer", ->
      @ceil.exec(@ctx).should.equal 11
      @even.exec(@ctx).should.equal 10

describe 'Ln', ->
  @beforeEach ->
    setup @, data

  it "should be able to return the natural log of a number", ->
    @ln.exec(@ctx).should.equal Math.log(4)

describe 'Log', ->
  @beforeEach ->
    setup @, data

    it "should be able to return the log of a number based on an arbitary base value", ->
      @log.exec(@ctx).should.equal 0.25

describe 'Modulo', ->
  @beforeEach ->
    setup @, data

    it "should be able to return the remainder of a division", ->
      @mod.exec(@ctx).should.equal 1

describe 'Abs', ->
  @beforeEach ->
    setup @, data

  it "should be able to return the absolute value of a positive number", ->
    @pos.exec(@ctx).should.equal 10
  it "should be able to return the absolute value of a negative number", ->
    @neg.exec(@ctx).should.equal 10
  it "should be able to return the absolute value of 0", ->
    @zero.exec(@ctx).should.equal 0

describe 'Round', ->
  @beforeEach ->
    setup @, data

  it "should be able to round a number up or down to the closest integer value", ->
    @up.exec(@ctx).should.equal 5
    @down.exec(@ctx).should.equal 4
  it "should be able to round a number up or down to the closest decimal place ", ->
    @up_percent.exec(@ctx).should.equal 4.6
    @down_percent.exec(@ctx).should.equal 4.4

describe 'Successor', ->
  @beforeEach ->
    setup @, data

  it "should be able to get Integer Successor", ->
    @is.exec(@ctx).should.equal 3
  it "should be able to get Real Successor", ->
    @rs.exec(@ctx).should.equal ( 2.2  + Math.pow(10,-8) )

  it "should cause runtime error for Successor greater than Integer Max value" , ->
    a = false
    try
      @ofr.exec(@ctx)
    catch e
      e.constructor.name.should.equal "OverFlowException"
      a = true

    a.should.equal true

  it "should be able to get Date Successor for year", ->
    dp = @y_date.exec(@ctx)
    dp.year.should.equal 2016
    should.not.exist dp.month
    should.not.exist dp.day
    should.not.exist dp.hour
    should.not.exist dp.minute
    should.not.exist dp.second
    should.not.exist  dp.millisecond

  it "should be able to get Date Successor for year,month", ->
    dp = @ym_date.exec(@ctx)
    dp.year.should.equal 2015
    dp.month.should.equal 2
    should.not.exist dp.day
    should.not.exist dp.hour
    should.not.exist dp.minute
    should.not.exist dp.second
    should.not.exist  dp.millisecond

  it "should be able to get Date Successor for year,month,day", ->
    dp = @ymd_date.exec(@ctx)
    dp.year.should.equal 2015
    dp.month.should.equal 1
    dp.day.should.equal 2
    should.not.exist dp.hour
    should.not.exist dp.minute
    should.not.exist dp.second
    should.not.exist  dp.millisecond

  it "should be able to get Date Successor for year,month,day,hour", ->
    dp = @ymdh_date.exec(@ctx)
    dp.year.should.equal 2015
    dp.month.should.equal 1
    dp.day.should.equal 1
    dp.hour.should.equal 1
    should.not.exist dp.minute
    should.not.exist dp.second
    should.not.exist  dp.millisecond

  it "should be able to get Date Successor for year,month,day,hour,minute", ->
    dp = @ymdhm_date.exec(@ctx)
    dp.year.should.equal 2015
    dp.month.should.equal 1
    dp.day.should.equal 1
    dp.hour.should.equal 0
    dp.minute.should.equal 1
    should.not.exist dp.second
    should.not.exist  dp.millisecond

  it "should be able to get Date Successor for year,month,day,hour,minute,seconds", ->
    dp = @ymdhms_date.exec(@ctx)
    dp.year.should.equal 2015
    dp.month.should.equal 1
    dp.day.should.equal 1
    dp.hour.should.equal 0
    dp.minute.should.equal 0
    dp.second.should.equal 1
    should.not.exist  dp.millisecond

  it "should be able to get Date Successor for year,month,day,hour,minute,seconds,milliseconds", ->
    dp = @ymdhmsm_date.exec(@ctx)
    dp.year.should.equal 2015
    dp.month.should.equal 1
    dp.day.should.equal 1
    dp.hour.should.equal 0
    dp.minute.should.equal 0
    dp.second.should.equal 0
    dp.millisecond.should.equal 1

  it "should throw an exception when attempting to get the Successor of the maximum allowed date", ->
    a = false
    try
      @max_date.exec(@ctx)
    catch e
      e.constructor.name.should.equal "OverFlowException"
      a = true
     a.should.equal true


describe 'Predecessor', ->
  @beforeEach ->
    setup @, data

  it "should be able to get Integer Predecessor", ->
    @is.exec(@ctx).should.equal 1
  it "should be able to get Real Predecessor", ->
    @rs.exec(@ctx).should.equal ( 2.2  - Math.pow(10,-8))
  it "should cause runtime error for Predecessor greater than Integer Max value" , ->
    a = false
    try
      @ufr.exec(@ctx)
    catch e
      e.constructor.name.should.equal "OverFlowException"
      a = true

    a.should.equal true

  it "should be able to get Date Predecessor for year", ->
    dp = @y_date.exec(@ctx)
    dp.year.should.equal 2014
    should.not.exist dp.month
    should.not.exist dp.day
    should.not.exist dp.hour
    should.not.exist dp.minute
    should.not.exist dp.second
    should.not.exist  dp.millisecond

  it "should be able to get Date Predecessor for year,month", ->
    dp = @ym_date.exec(@ctx)
    dp.year.should.equal 2014
    dp.month.should.equal 12
    should.not.exist dp.day
    should.not.exist dp.hour
    should.not.exist dp.minute
    should.not.exist dp.second
    should.not.exist  dp.millisecond

  it "should be able to get Date Predecessor for year,month,day", ->
    dp = @ymd_date.exec(@ctx)
    dp.year.should.equal 2014
    dp.month.should.equal 12
    dp.day.should.equal 31
    should.not.exist dp.hour
    should.not.exist dp.minute
    should.not.exist dp.second
    should.not.exist  dp.millisecond
  it "should be able to get Date Predecessor for year,month,day,hour", ->
    dp = @ymdh_date.exec(@ctx)
    dp.year.should.equal 2014
    dp.month.should.equal 12
    dp.day.should.equal 31
    dp.hour.should.equal 23
    should.not.exist dp.minute
    should.not.exist dp.second
    should.not.exist  dp.millisecond

  it "should be able to get Date Predecessor for year,month,day,hour,minute", ->
    dp = @ymdhm_date.exec(@ctx)
    dp.year.should.equal 2014
    dp.month.should.equal 12
    dp.day.should.equal 31
    dp.hour.should.equal 23
    dp.minute.should.equal 59
    should.not.exist dp.second
    should.not.exist  dp.millisecond

  it "should be able to get Date Predecessor for year,month,day,hour,minute,seconds", ->
    dp = @ymdhms_date.exec(@ctx)
    dp.year.should.equal 2014
    dp.month.should.equal 12
    dp.day.should.equal 31
    dp.hour.should.equal 23
    dp.minute.should.equal 59
    dp.second.should.equal 59
    should.not.exist  dp.millisecond

  it "should be able to get Date Predecessor for year,month,day,hour,minute,seconds,milliseconds", ->
    dp = @ymdhmsm_date.exec(@ctx)
    dp.year.should.equal 2014
    dp.month.should.equal 12
    dp.day.should.equal 31
    dp.hour.should.equal 23
    dp.minute.should.equal 59
    dp.millisecond.should.equal 999

  it "should throw an exception when attempting to get the Predecessor of the minimum allowed date", ->
    a = false
    try
      @min_date.exec(@ctx)
    catch e
      e.constructor.name.should.equal "OverFlowException"
      a = true
     a.should.equal true

describe 'Quantity', ->
  @beforeEach ->
    setup @, data

  it "should be able to perform Quantity Addition", -> 
    aqq = @add_q_q.exec(@ctx)
    aqq.value.should.equal 20
    aqq.unit.should.equal 'days'
    adq = @add_d_q.exec(@ctx)
    adq.constructor.name.should.equal "DateTime"
    adq.year.should.equal 2000
    adq.month.should.equal 1
    adq.day.should.equal 11


  it "should be able to perform Quantity Subtraction", -> 
    sqq = @sub_q_q.exec(@ctx)
    sqq.value.should.equal 0
    sqq.unit.should.equal 'days'
    sdq = @sub_d_q.exec(@ctx)
    sdq.constructor.name.should.equal "DateTime"
    sdq.year.should.equal 1999
    sdq.month.should.equal 12
    sdq.day.should.equal 22
 
  it "should be able to perform Quantity Division", -> 

  it "should be able to perform Quantity Multiplication", -> 

  it "should be able to perform Quantity Absolution", -> 
    q = @abs.exec(@ctx)
    q.value.should.equal 10
    q.unit.should.equal 'days'

  it "should be able to perform Quantity Negation", -> 
    q = @neg.exec(@ctx)
    q.value.should.equal -10
    q.unit.should.equal 'days'