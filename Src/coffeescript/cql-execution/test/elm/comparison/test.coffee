should = require 'should'
setup = require '../../setup'
data = require './data'

# TO Comparisons for Dates

describe 'Equal', ->
  @beforeEach ->
    setup @, data

  it 'should be false for 5 = 4', ->
    @aGtB_Int.exec(@ctx).should.be.false()

  it 'should be true for 5 = 5', ->
    @aEqB_Int.exec(@ctx).should.be.true()

  it 'should be false for 5 = 6', ->
    @aLtB_Int.exec(@ctx).should.be.false()

  it 'should identify equal/unequal tuples', ->
    @eqTuples.exec(@ctx).should.be.true()
    @uneqTuples.exec(@ctx).should.be.false()

  it 'should identify equal/unequal DateTimes in same timezone', ->
    @eqDateTimes.exec(@ctx).should.be.true()
    @uneqDateTimes.exec(@ctx).should.be.false()

  it 'should identify equal/unequal DateTimes in different timezones', ->
    @eqDateTimesTZ.exec(@ctx).should.be.true()
    @uneqDateTimesTZ.exec(@ctx).should.be.false()

  it 'should identify uncertain/unequal DateTimes when there is imprecision', ->
    should(@possiblyEqualDateTimes.exec(@ctx)).be.null
    @impossiblyEqualDateTimes.exec(@ctx).should.be.false()

describe 'NotEqual', ->
  @beforeEach ->
    setup @, data

  it 'should be true for 5 <> 4', ->
    @aGtB_Int.exec(@ctx).should.be.true()

  it 'should be false for 5 <> 5', ->
    @aEqB_Int.exec(@ctx).should.be.false()

  it 'should be true for 5 <> 6', ->
    @aLtB_Int.exec(@ctx).should.be.true()

  it 'should identify equal/unequal tuples', ->
    @eqTuples.exec(@ctx).should.be.false()
    @uneqTuples.exec(@ctx).should.be.true()

  it 'should identify equal/unequal DateTimes in same timezone', ->
    @eqDateTimes.exec(@ctx).should.be.false()
    @uneqDateTimes.exec(@ctx).should.be.true()

  it 'should identify equal/unequal DateTimes in different timezones', ->
    @eqDateTimesTZ.exec(@ctx).should.be.false()
    @uneqDateTimesTZ.exec(@ctx).should.be.true()

  it 'should identify uncertain/unequal DateTimes when there is imprecision', ->
    should(@possiblyEqualDateTimes.exec(@ctx)).be.null
    @impossiblyEqualDateTimes.exec(@ctx).should.be.true()

describe 'Less', ->
  @beforeEach ->
    setup @, data

  it 'should be false for 5 < 4', ->
    @aGtB_Int.exec(@ctx).should.be.false()

  it 'should be false for 5 < 5', ->
    @aEqB_Int.exec(@ctx).should.be.false()

  it 'should be true for 5 < 6', ->
    @aLtB_Int.exec(@ctx).should.be.true()

describe 'LessOrEqual', ->
  @beforeEach ->
    setup @, data

  it 'should be false for 5 <= 4', ->
    @aGtB_Int.exec(@ctx).should.be.false()

  it 'should be true for 5 <= 5', ->
    @aEqB_Int.exec(@ctx).should.be.true()

  it 'should be true for 5 <= 6', ->
    @aLtB_Int.exec(@ctx).should.be.true()

describe 'Greater', ->
  @beforeEach ->
    setup @, data

  it 'should be true for 5 > 4', ->
    @aGtB_Int.exec(@ctx).should.be.true()

  it 'should be false for 5 > 5', ->
    @aEqB_Int.exec(@ctx).should.be.false()

  it 'should be false for 5 > 6', ->
    @aLtB_Int.exec(@ctx).should.be.false()

describe 'GreaterOrEqual', ->
  @beforeEach ->
    setup @, data

  it 'should be true for 5 >= 4', ->
    @aGtB_Int.exec(@ctx).should.be.true()

  it 'should be true for 5 >= 5', ->
    @aEqB_Int.exec(@ctx).should.be.true()

  it 'should be false for 5 >= 6', ->
    @aLtB_Int.exec(@ctx).should.be.false()

describe 'DurationsGreaterThan', ->
  @beforeEach ->
    setup @, data

  it 'should correctly show greater than', ->
    @aGtB__Year_G_Year_G.exec(@ctx).should.be.true()
    @aGtB__Year_G_Year_J.exec(@ctx).should.be.true()
    @aGtB__Year_G_Mo_G.exec(@ctx).should.be.true()
    @aGtB__Year_G_Mo_J.exec(@ctx).should.be.true()
    @aGtB__Year_G_Week.exec(@ctx).should.be.true()
    @aGtB__Year_G_Day.exec(@ctx).should.be.true()
    @aGtB__Year_G_Hour.exec(@ctx).should.be.true()
    @aGtB__Year_G_Minute.exec(@ctx).should.be.true()
    @aGtB__Year_G_Second.exec(@ctx).should.be.true()
    @aGtB__Year_G_Millisecond.exec(@ctx).should.be.true()
    @aGtB__Year_J_Year_J.exec(@ctx).should.be.true()
    @aGtB__Year_J_Mo_G.exec(@ctx).should.be.true()
    @aGtB__Year_J_Mo_J.exec(@ctx).should.be.true()
    @aGtB__Year_J_Week.exec(@ctx).should.be.true()
    @aGtB__Year_J_Day.exec(@ctx).should.be.true()
    @aGtB__Year_J_Hour.exec(@ctx).should.be.true()
    @aGtB__Year_J_Minute.exec(@ctx).should.be.true()
    @aGtB__Year_J_Second.exec(@ctx).should.be.true()
    @aGtB__Year_J_Millisecond.exec(@ctx).should.be.true()
    @aEqB__Year_G_Year_G.exec(@ctx).should.be.false()
    @aEqB__Year_G_Year_J.exec(@ctx).should.be.false()
    @aEqB__Year_G_Mo_G.exec(@ctx).should.be.false()
    @aEqB__Year_G_Mo_J.exec(@ctx).should.be.false()
    @aEqB__Year_G_Week.exec(@ctx).should.be.false()
    @aEqB__Year_G_Day.exec(@ctx).should.be.false()
    @aEqB__Year_G_Hour.exec(@ctx).should.be.false()
    @aEqB__Year_G_Minute.exec(@ctx).should.be.false()
    @aEqB__Year_G_Second.exec(@ctx).should.be.false()
    @aEqB__Year_G_Millisecond.exec(@ctx).should.be.false()
    @aEqB__Year_J_Year_J.exec(@ctx).should.be.false()
    @aEqB__Year_J_Mo_G.exec(@ctx).should.be.false()
    @aEqB__Year_J_Mo_J.exec(@ctx).should.be.false()
    @aEqB__Year_J_Week.exec(@ctx).should.be.false()
    @aEqB__Year_J_Day.exec(@ctx).should.be.false()
    @aEqB__Year_J_Hour.exec(@ctx).should.be.false()
    @aEqB__Year_J_Minute.exec(@ctx).should.be.false()
    @aEqB__Year_J_Second.exec(@ctx).should.be.false()
    @aEqB__Year_J_Millisecond.exec(@ctx).should.be.false()
    @aLtB__Year_G_Year_G.exec(@ctx).should.be.false()
    @aLtB__Year_G_Year_J.exec(@ctx).should.be.false()
    @aLtB__Year_G_Mo_G.exec(@ctx).should.be.false()
    @aLtB__Year_G_Mo_J.exec(@ctx).should.be.false()
    @aLtB__Year_G_Week.exec(@ctx).should.be.false()
    @aLtB__Year_G_Day.exec(@ctx).should.be.false()
    @aLtB__Year_G_Hour.exec(@ctx).should.be.false()
    @aLtB__Year_G_Minute.exec(@ctx).should.be.false()
    @aLtB__Year_G_Second.exec(@ctx).should.be.false()
    @aLtB__Year_G_Millisecond.exec(@ctx).should.be.false()
    @aLtB__Year_J_Year_J.exec(@ctx).should.be.false()
    @aLtB__Year_J_Mo_G.exec(@ctx).should.be.false()
    @aLtB__Year_J_Mo_J.exec(@ctx).should.be.false()
    @aLtB__Year_J_Week.exec(@ctx).should.be.false()
    @aLtB__Year_J_Day.exec(@ctx).should.be.false()
    @aLtB__Year_J_Hour.exec(@ctx).should.be.false()
    @aLtB__Year_J_Minute.exec(@ctx).should.be.false()
    @aLtB__Year_J_Second.exec(@ctx).should.be.false()
    @aLtB__Year_J_Millisecond.exec(@ctx).should.be.false()

describe 'DurationsLessThan', ->
  @beforeEach ->
    setup @, data

  it 'should correctly show less than', ->
    @aLtB__Year_G_Year_G.exec(@ctx).should.be.true()
    @aLtB__Year_G_Year_J.exec(@ctx).should.be.true()
    @aLtB__Year_G_Mo_G.exec(@ctx).should.be.true()
    @aLtB__Year_G_Mo_J.exec(@ctx).should.be.true()
    @aLtB__Year_G_Week.exec(@ctx).should.be.true()
    @aLtB__Year_G_Day.exec(@ctx).should.be.true()
    @aLtB__Year_G_Hour.exec(@ctx).should.be.true()
    @aLtB__Year_G_Minute.exec(@ctx).should.be.true()
    @aLtB__Year_G_Second.exec(@ctx).should.be.true()
    @aLtB__Year_G_Millisecond.exec(@ctx).should.be.true()
    @aLtB__Year_J_Year_J.exec(@ctx).should.be.true()
    @aLtB__Year_J_Mo_G.exec(@ctx).should.be.true()
    @aLtB__Year_J_Mo_J.exec(@ctx).should.be.true()
    @aLtB__Year_J_Week.exec(@ctx).should.be.true()
    @aLtB__Year_J_Day.exec(@ctx).should.be.true()
    @aLtB__Year_J_Hour.exec(@ctx).should.be.true()
    @aLtB__Year_J_Minute.exec(@ctx).should.be.true()
    @aLtB__Year_J_Second.exec(@ctx).should.be.true()
    @aLtB__Year_J_Millisecond.exec(@ctx).should.be.true()
    @aGtB__Year_G_Year_G.exec(@ctx).should.be.false()
    @aGtB__Year_G_Year_J.exec(@ctx).should.be.false()
    @aGtB__Year_G_Mo_G.exec(@ctx).should.be.false()
    @aGtB__Year_G_Mo_J.exec(@ctx).should.be.false()
    @aGtB__Year_G_Week.exec(@ctx).should.be.false()
    @aGtB__Year_G_Day.exec(@ctx).should.be.false()
    @aGtB__Year_G_Hour.exec(@ctx).should.be.false()
    @aGtB__Year_G_Minute.exec(@ctx).should.be.false()
    @aGtB__Year_G_Second.exec(@ctx).should.be.false()
    @aGtB__Year_G_Millisecond.exec(@ctx).should.be.false()
    @aGtB__Year_J_Year_J.exec(@ctx).should.be.false()
    @aGtB__Year_J_Mo_G.exec(@ctx).should.be.false()
    @aGtB__Year_J_Mo_J.exec(@ctx).should.be.false()
    @aGtB__Year_J_Week.exec(@ctx).should.be.false()
    @aGtB__Year_J_Day.exec(@ctx).should.be.false()
    @aGtB__Year_J_Hour.exec(@ctx).should.be.false()
    @aGtB__Year_J_Minute.exec(@ctx).should.be.false()
    @aGtB__Year_J_Second.exec(@ctx).should.be.false()
    @aGtB__Year_J_Millisecond.exec(@ctx).should.be.false()
    @aEqB__Year_G_Year_G.exec(@ctx).should.be.false()
    @aEqB__Year_G_Year_J.exec(@ctx).should.be.false()
    @aEqB__Year_G_Mo_G.exec(@ctx).should.be.false()
    @aEqB__Year_G_Mo_J.exec(@ctx).should.be.false()
    @aEqB__Year_G_Week.exec(@ctx).should.be.false()
    @aEqB__Year_G_Day.exec(@ctx).should.be.false()
    @aEqB__Year_G_Hour.exec(@ctx).should.be.false()
    @aEqB__Year_G_Minute.exec(@ctx).should.be.false()
    @aEqB__Year_G_Second.exec(@ctx).should.be.false()
    @aEqB__Year_G_Millisecond.exec(@ctx).should.be.false()
    @aEqB__Year_J_Year_J.exec(@ctx).should.be.false()
    @aEqB__Year_J_Mo_G.exec(@ctx).should.be.false()
    @aEqB__Year_J_Mo_J.exec(@ctx).should.be.false()
    @aEqB__Year_J_Week.exec(@ctx).should.be.false()
    @aEqB__Year_J_Day.exec(@ctx).should.be.false()
    @aEqB__Year_J_Hour.exec(@ctx).should.be.false()
    @aEqB__Year_J_Minute.exec(@ctx).should.be.false()
    @aEqB__Year_J_Second.exec(@ctx).should.be.false()
    @aEqB__Year_J_Millisecond.exec(@ctx).should.be.false()

describe 'DurationsEqual', ->
  @beforeEach ->
    setup @, data

  it 'should correctly show equal', ->
    @aEqB__Year_G_Year_G.exec(@ctx).should.be.true()
    @aEqB__Year_G_Year_J.exec(@ctx).should.be.true()
    @aEqB__Year_G_Mo_G.exec(@ctx).should.be.true()
    @aEqB__Year_G_Mo_J.exec(@ctx).should.be.true()
    @aEqB__Year_G_Week.exec(@ctx).should.be.true()
    @aEqB__Year_G_Day.exec(@ctx).should.be.true()
    @aEqB__Year_G_Hour.exec(@ctx).should.be.true()
    @aEqB__Year_G_Minute.exec(@ctx).should.be.true()
    @aEqB__Year_G_Second.exec(@ctx).should.be.true()
    @aEqB__Year_G_Millisecond.exec(@ctx).should.be.true()
    @aEqB__Year_J_Year_J.exec(@ctx).should.be.true()
    @aEqB__Year_J_Mo_G.exec(@ctx).should.be.true()
    @aEqB__Year_J_Mo_J.exec(@ctx).should.be.true()
    @aEqB__Year_J_Week.exec(@ctx).should.be.true()
    @aEqB__Year_J_Day.exec(@ctx).should.be.true()
    @aEqB__Year_J_Hour.exec(@ctx).should.be.true()
    @aEqB__Year_J_Minute.exec(@ctx).should.be.true()
    @aEqB__Year_J_Second.exec(@ctx).should.be.true()
    @aEqB__Year_J_Millisecond.exec(@ctx).should.be.true()
    @aLtB__Year_G_Year_G.exec(@ctx).should.be.false()
    @aLtB__Year_G_Year_J.exec(@ctx).should.be.false()
    @aLtB__Year_G_Mo_G.exec(@ctx).should.be.false()
    @aLtB__Year_G_Mo_J.exec(@ctx).should.be.false()
    @aLtB__Year_G_Week.exec(@ctx).should.be.false()
    @aLtB__Year_G_Day.exec(@ctx).should.be.false()
    @aLtB__Year_G_Hour.exec(@ctx).should.be.false()
    @aLtB__Year_G_Minute.exec(@ctx).should.be.false()
    @aLtB__Year_G_Second.exec(@ctx).should.be.false()
    @aLtB__Year_G_Millisecond.exec(@ctx).should.be.false()
    @aLtB__Year_J_Year_J.exec(@ctx).should.be.false()
    @aLtB__Year_J_Mo_G.exec(@ctx).should.be.false()
    @aLtB__Year_J_Mo_J.exec(@ctx).should.be.false()
    @aLtB__Year_J_Week.exec(@ctx).should.be.false()
    @aLtB__Year_J_Day.exec(@ctx).should.be.false()
    @aLtB__Year_J_Hour.exec(@ctx).should.be.false()
    @aLtB__Year_J_Minute.exec(@ctx).should.be.false()
    @aLtB__Year_J_Second.exec(@ctx).should.be.false()
    @aLtB__Year_J_Millisecond.exec(@ctx).should.be.false()
    @aGtB__Year_G_Year_G.exec(@ctx).should.be.false()
    @aGtB__Year_G_Year_J.exec(@ctx).should.be.false()
    @aGtB__Year_G_Mo_G.exec(@ctx).should.be.false()
    @aGtB__Year_G_Mo_J.exec(@ctx).should.be.false()
    @aGtB__Year_G_Week.exec(@ctx).should.be.false()
    @aGtB__Year_G_Day.exec(@ctx).should.be.false()
    @aGtB__Year_G_Hour.exec(@ctx).should.be.false()
    @aGtB__Year_G_Minute.exec(@ctx).should.be.false()
    @aGtB__Year_G_Second.exec(@ctx).should.be.false()
    @aGtB__Year_G_Millisecond.exec(@ctx).should.be.false()
    @aGtB__Year_J_Year_J.exec(@ctx).should.be.false()
    @aGtB__Year_J_Mo_G.exec(@ctx).should.be.false()
    @aGtB__Year_J_Mo_J.exec(@ctx).should.be.false()
    @aGtB__Year_J_Week.exec(@ctx).should.be.false()
    @aGtB__Year_J_Day.exec(@ctx).should.be.false()
    @aGtB__Year_J_Hour.exec(@ctx).should.be.false()
    @aGtB__Year_J_Minute.exec(@ctx).should.be.false()
    @aGtB__Year_J_Second.exec(@ctx).should.be.false()
    @aGtB__Year_J_Millisecond.exec(@ctx).should.be.false()