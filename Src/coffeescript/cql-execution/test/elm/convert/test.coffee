should = require 'should'
setup = require '../../setup'
data = require './data'
{isNull} = require '../../../lib/util/util'

describe 'FromString', ->
  @beforeEach ->
    setup @, data

  it "should convert 'str' to 'str'", ->
    @stringStr.exec(@ctx).should.equal "str"
    
  it "should convert null to null", ->
    isNull(@stringNull.exec(@ctx)).should.equal true

  it "should convert 'true' to true", ->
    @boolTrue.exec(@ctx).should.equal true

  it "should convert 'false' to false", ->
    @boolFalse.exec(@ctx).should.equal false
    
  it "should convert '10.2' to Decimal", ->
    @decimalValid.exec(@ctx).should.equal 10.2

  it "should convert 'abc' to Decimal NaN", ->
    isNaN(@decimalInvalid.exec(@ctx)).should.equal true

  it "should convert '10' to Integer", ->
    @integerValid.exec(@ctx).should.equal 10

  it "should convert '10.2' to Integer 10", ->
    @integerDropDecimal.exec(@ctx).should.equal 10

  it "should convert 'abc' to Integer NaN", ->
    isNaN(@integerInvalid.exec(@ctx)).should.equal true

  it "should convert \"10 'A'\" to Quantity", ->
    quantity = @quantityStr.exec(@ctx)
    quantity.value.should.equal 10
    quantity.unit.should.equal "A"

  it "should convert \"+10 'A'\" to Quantity", ->
    quantity = @posQuantityStr.exec(@ctx)
    quantity.value.should.equal 10
    quantity.unit.should.equal "A"

  it "should convert \"-10 'A'\" to Quantity", ->
    quantity = @negQuantityStr.exec(@ctx)
    quantity.value.should.equal -10
    quantity.unit.should.equal "A"

  it "should convert \"10.0'mA'\" to Quantity", ->
    quantity = @quantityStrDecimal.exec(@ctx)
    quantity.value.should.equal 10.0
    quantity.unit.should.equal "mA"
    
  it "should convert '2015-01-02' to DateTime", ->
    date = @dateStr.exec(@ctx)
    date.year.should.equal 2015
    date.month.should.equal 1
    date.day.should.equal 2

describe 'FromInteger', ->
  @beforeEach ->
    setup @, data

  it "should convert 10 to '10'", ->
    @string10.exec(@ctx).should.equal "10"

  it "should convert 10 to 10.0", ->
    @decimal10.exec(@ctx).should.equal 10.0
    
  it "should convert null to null", ->
    isNull(@intNull.exec(@ctx)).should.equal true

  it "should convert 10 to 10", ->
    @intInt.exec(@ctx).should.equal 10

describe 'FromQuantity', ->
  @beforeEach ->
    setup @, data

  it "should convert \"10 'A'\" to \"10 'A'\"", ->
    @quantityStr.exec(@ctx).should.equal "10 'A'"

  it "should convert \"+10 'A'\" to \"10 'A'\"", ->
    @posQuantityStr.exec(@ctx).should.equal "10 'A'"

  it "should convert \"-10 'A'\" to \"10 'A'\"", ->
    @negQuantityStr.exec(@ctx).should.equal "-10 'A'"

  it "should convert \"10 'A'\" to \"10 'A'\"", ->
    quantity = @quantityQuantity.exec(@ctx)
    quantity.value.should.equal 10
    quantity.unit.should.equal 'A'

describe 'FromBoolean', ->
  @beforeEach ->
    setup @, data

  it "should convert true to 'true'", ->
    @booleanTrueStr.exec(@ctx).should.equal "true"

  it "should convert false to 'false'", ->
    @booleanFalseStr.exec(@ctx).should.equal "false"

  it "should convert true to true", ->
    @booleanTrueBool.exec(@ctx).should.equal true

  it "should convert false to false", ->
    @booleanFalseBool.exec(@ctx).should.equal false

describe 'FromDateTime', ->
  @beforeEach ->
    setup @, data

  it "should convert @2015-01-02 to '2015-01-02'", ->
    @dateStr.exec(@ctx).should.equal "2015-01-02"

  it "should convert @2015-01-02 to @2015-01-02", ->
    date = @dateDate.exec(@ctx)
    date.year.should.equal 2015
    date.month.should.equal 1
    date.day.should.equal 2
    
describe 'FromTime', ->
  @beforeEach ->
    setup @, data

  it.skip "should convert @T11:57 to '11:57'", ->
    @timeStr.exec(@ctx).should.equal "11:57"

  it.skip "should convert @T11:57 to @11:57", ->
    time = @timeTime.exec(@ctx)
    time.hour.should.equal 11
    time.minute.should.equal 57
    
describe 'FromCode', ->
  @beforeEach ->
    setup @, data
    
  it.skip "should convert hepB to a concept", ->
    concept = @codeConcept.exec(@ctx)
    
  it.skip "should convert hepB to a code", ->
    code = @codeCode.exec(@ctx)
