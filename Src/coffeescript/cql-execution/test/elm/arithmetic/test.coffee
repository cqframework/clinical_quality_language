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
