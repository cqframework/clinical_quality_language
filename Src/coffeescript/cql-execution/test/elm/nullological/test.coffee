should = require 'should'
setup = require '../../setup'
data = require './data'

describe 'Nil', ->
  @beforeEach ->
    setup @, data

  it 'should execute as null', ->
    should(@nil.exec(@ctx)).be.null

describe 'IsNull', ->
  @beforeEach ->
    setup @, data

  it 'should detect that null is null', ->
    @nullIsNull.exec(@ctx).should.be.true

  it 'should detect that null variable is null', ->
    @nullVarIsNull.exec(@ctx).should.be.true

  it 'should detect that string is not null', ->
    @stringIsNull.exec(@ctx).should.be.false

  it 'should detect that non-null variable is not null', ->
    @nonNullVarIsNull.exec(@ctx).should.be.false

describe.skip 'Coalesce', ->
  @beforeEach ->
    setup @, data

  it 'should return first non-null when leading args are null', ->
    @nullNullHelloNullWorld.exec(@ctx).should.equal 'Hello'

  it 'should return first arg when it is non-null', ->
    @fooNullNullBar.exec(@ctx).should.equal 'Foo'

  it 'should return null when they are all null', ->
    should(@allNull.exec(@ctx)).be.null
