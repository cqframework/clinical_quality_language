should = require 'should'
setup = require '../../setup'
data = require './data'

describe 'ParameterDef', ->
  @beforeEach ->
    setup @, data
    @param = @lib.parameters.MeasureYear

  it 'should have a name', ->
    @param.name.should.equal 'MeasureYear'

  it 'should execute to default value', ->
    @param.exec(@ctx).should.equal 2012

  it 'should execute to provided value', ->
    @param.exec(@ctx.withParameters { MeasureYear: 2013 }).should.equal 2013

describe 'ParameterRef', ->
  @beforeEach ->
    setup @, data

  it 'should have a name', ->
    @foo.name.should.equal 'FooP'

  it 'should execute to default value', ->
    @foo.exec(@ctx).should.equal 'Bar'

  it 'should execute to provided value', ->
    @foo.exec(@ctx.withParameters { FooP: 'Bah' }).should.equal 'Bah'
