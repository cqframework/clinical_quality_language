should = require 'should'
setup = require '../../setup'
data = require './data'

describe 'FromString', ->
  @beforeEach ->
    setup @, data

  it "should convert 'true' to true", ->
    @boolTrue.exec(@ctx).should.equal true

  it "should convert 'false' to false", ->
    @boolFalse.exec(@ctx).should.equal false
    
  it "should convert 10.2 to Decimal", ->
    @decimalValid.exec(@ctx).should.equal 10.2

  it "should convert abc to Decimal NaN", ->
    isNaN(@decimalInvalid.exec(@ctx)).should.equal true

  it "should convert 10 to Integer", ->
    @integerValid.exec(@ctx).should.equal 10

  it "should convert 10.2 to Integer 10", ->
    @integerDropDecimal.exec(@ctx).should.equal 10

  it "should convert abc to Integer NaN", ->
    isNaN(@integerInvalid.exec(@ctx)).should.equal true
