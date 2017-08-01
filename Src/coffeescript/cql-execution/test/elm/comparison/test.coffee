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

  it 'should be true for 5 m < 4 m', ->
    @aGtB_Quantity.exec(@ctx).should.be.false()

  it 'should be false for 5 m < 5 m', ->
    @aEqB_Quantity.exec(@ctx).should.be.false()

  it 'should be false for 5 m < 6 m', ->
    @aLtB_Quantity.exec(@ctx).should.be.true()

  it 'should be true for 5 m < 5 cm', ->
    @aGtB_Quantity_diff.exec(@ctx).should.be.false()

  it 'should be false for 5 m < 50 cm ', ->
    @aEqB_Quantity_diff.exec(@ctx).should.be.false()

  it 'should be false for 5 m < 5 km', ->
    @aLtB_Quantity_diff.exec(@ctx).should.be.true()


describe 'LessOrEqual', ->
  @beforeEach ->
    setup @, data

  it 'should be false for 5 <= 4', ->
    @aGtB_Int.exec(@ctx).should.be.false()

  it 'should be true for 5 <= 5', ->
    @aEqB_Int.exec(@ctx).should.be.true()

  it 'should be true for 5 <= 6', ->
    @aLtB_Int.exec(@ctx).should.be.true()

  it 'should be true for 5 m <= 4 m', ->
    @aGtB_Quantity.exec(@ctx).should.be.false()

  it 'should be false for 5 m <= 5 m', ->
    @aEqB_Quantity.exec(@ctx).should.be.true()

  it 'should be false for 5 m <= 6 m', ->
    @aLtB_Quantity.exec(@ctx).should.be.true()

  it 'should be true for 5 m <= 5 cm', ->
    @aGtB_Quantity_diff.exec(@ctx).should.be.false()

  it 'should be false for 5 m <= 500 cm ', ->
    @aEqB_Quantity_diff.exec(@ctx).should.be.true()

  it 'should be false for 5 m <= 5 km', ->
    @aLtB_Quantity_diff.exec(@ctx).should.be.true()


describe 'Greater', ->
  @beforeEach ->
    setup @, data

  it 'should be true for 5 > 4', ->
    @aGtB_Int.exec(@ctx).should.be.true()

  it 'should be false for 5 > 5', ->
    @aEqB_Int.exec(@ctx).should.be.false()

  it 'should be false for 5 > 6', ->
    @aLtB_Int.exec(@ctx).should.be.false()

  it 'should be true for 5 m > 4 m', ->
    @aGtB_Quantity.exec(@ctx).should.be.true()

  it 'should be false for 5 m > 5 m', ->
    @aEqB_Quantity.exec(@ctx).should.be.false()

  it 'should be false for 5 m > 6 m', ->
    @aLtB_Quantity.exec(@ctx).should.be.false()

  it 'should be true for 5 m > 5 cm', ->
    @aGtB_Quantity_diff.exec(@ctx).should.be.true()

  it 'should be false for 5 m > 50 cm ', ->
    @aEqB_Quantity_diff.exec(@ctx).should.be.false()

  it 'should be false for 5 m > 5 km', ->
    @aLtB_Quantity_diff.exec(@ctx).should.be.false()


describe 'GreaterOrEqual', ->
  @beforeEach ->
    setup @, data

  it 'should be true for 5 >= 4', ->
    @aGtB_Int.exec(@ctx).should.be.true()

  it 'should be true for 5 >= 5', ->
    @aEqB_Int.exec(@ctx).should.be.true()

  it 'should be false for 5 >= 6', ->
    @aLtB_Int.exec(@ctx).should.be.false()

  it 'should be true for 5 m >= 4 m', ->
    @aGtB_Quantity.exec(@ctx).should.be.true()

  it 'should be false for 5 m  >= 5 m', ->
    @aEqB_Quantity.exec(@ctx).should.be.true()

  it 'should be false for 5 m >= 6 m', ->
    @aLtB_Quantity.exec(@ctx).should.be.false()

  it 'should be true for 5 m >= 5 cm', ->
    @aGtB_Quantity_diff.exec(@ctx).should.be.true()

  it 'should be false for 5 m  >= 50 cm ', ->
    @aEqB_Quantity_diff.exec(@ctx).should.be.true()

  it 'should be false for 5 m  >=5 km', ->
    @aLtB_Quantity_diff.exec(@ctx).should.be.false()

  it 'should be true for 100 mg / 2 [lb_av]  > 49 mg/[lb_av]', ->
    @divideUcum.exec(@ctx).should.be.true()
