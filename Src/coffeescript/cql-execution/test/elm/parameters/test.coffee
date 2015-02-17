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

  it 'should work with typed int parameters', ->
    intParam = @lib.parameters.IntParameter
    intParam.exec(@ctx.withParameters { IntParameter: 17 }).should.equal 17

  it 'should work with typed list parameters', ->
    listParam = @lib.parameters.ListParameter
    listParam.exec(@ctx.withParameters { ListParameter: {'a', 'b', 'c'} }).should.eql {'a', 'b', 'c'}

  it 'should work with typed tuple parameters', ->
    tupleParam = @lib.parameters.TupleParameter
    v = { a : 1, b : 'bee', c : true, d : [10, 9, 8], e : { f : 'eff', g : false}}
    tupleParam.exec(@ctx.withParameters { TupleParameter: v }).should.eql v

describe 'ParameterRef', ->
  @beforeEach ->
    setup @, data

  it 'should have a name', ->
    @foo.name.should.equal 'FooP'

  it 'should execute to default value', ->
    @foo.exec(@ctx).should.equal 'Bar'

  it 'should execute to provided value', ->
    @foo.exec(@ctx.withParameters { FooP: 'Bah' }).should.equal 'Bah'
