should = require 'should'
setup = require '../../setup'
data = require './data'

describe 'Literal', ->
  @beforeEach ->
    setup @, data

  it 'should convert true to boolean true', ->
    @boolTrue.value.should.be.true()

  it 'should execute true as true', ->
    @boolTrue.exec(@ctx).should.be.true()

  it 'should convert false to boolean false', ->
    @boolFalse.value.should.be.false()

  it 'should execute false as false', ->
    @boolFalse.exec(@ctx).should.be.false()

  it 'should convert 1 to int 1', ->
    @intOne.value.should.equal 1

  it 'should execute 1 as 1', ->
    @intOne.exec(@ctx).should.equal 1

  it 'should convert .1 to decimal .1', ->
    @decimalTenth.value.should.equal 0.1

  it 'should execute .1 as .1', ->
    @decimalTenth.exec(@ctx).should.equal 0.1

  it 'should convert \'true\' to string \'true\'', ->
    @stringTrue.value.should.equal 'true'

  it 'should execute \'true\' as \'true\'', ->
    @stringTrue.exec(@ctx).should.equal 'true'
