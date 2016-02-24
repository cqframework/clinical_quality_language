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

  it "should convert '10A' to Quantity", ->
    quantity = @quantityStr.exec(@ctx)
    quantity.value.should.equal 10
    quantity.unit.should.equal 'A'

  it "should convert '10.0mA' to Quantity", ->
    quantity = @quantityStrDecimal.exec(@ctx)
    quantity.value.should.equal 10.0
    quantity.unit.should.equal 'mA'

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

  it "should convert 10A to '10A'", ->
    @quantityStr.exec(@ctx).should.equal "10A"
