should = require 'should'
setup = require '../../setup'
data = require './data'

describe 'ExpressionDef', ->
  @beforeEach ->
    setup @, data
    @def = @lib.expressions.Foo

  it 'should have a name', ->
    @def.name.should.equal 'Foo'

  it 'should have the correct context', ->
    @def.context.should.equal 'Patient'

  it 'should execute to its value', ->
    @def.exec(@ctx).should.equal 'Bar'

describe 'ExpressionRef', ->
  @beforeEach ->
    setup @, data

  it 'should have a name', ->
    @foo.name.should.equal 'Life'

  it 'should execute to expression value', ->
    @foo.exec(@ctx).should.equal 42

describe 'FunctionDefinitions', ->
  @beforeEach ->
    setup @, data

  it 'should be able to define and use a simple function' , ->
    e = @testValue.exec(@ctx)
    e.should.equal 3

describe.skip 'FunctionOverloads', ->
  @beforeEach ->
    setup @, data

  it 'should be able to use the function with Integer argument' , ->
    e = @testValue1.exec(@ctx)
    e.should.equal 2
    e = @testValue2.exec(@ctx)
    e.should.equal 'Hello World'