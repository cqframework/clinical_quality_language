should = require 'should'
setup = require '../../setup'
data = require './data'
{Code, Concept} = require '../../../lib/datatypes/clinical'
{DateTime} = require '../../../lib/datatypes/datetime'
{Interval} = require '../../../lib/datatypes/interval'
{Quantity} = require '../../../lib/elm/quantity'

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
    listParam.exec(@ctx.withParameters { ListParameter: ['a', 'b', 'c'] }).should.eql ['a', 'b', 'c']

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

  it 'should fail when provided value is wrong type', ->
    @foo.exec(@ctx.withParameters { FooP: 12 }).should.equal 12

describe 'BooleanParameterTypes', ->
  @beforeEach ->
    setup @, data

  it 'should execute to provided valid value', ->
    @foo.exec(@ctx.withParameters { FooP: true }).should.equal true

  it 'should throw when provided value is wrong type', ->
    try
      @foo.exec(@ctx.withParameters { FooP: 12 })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  it 'should execute to default value', ->
    @foo2.exec(@ctx).should.equal true

  it 'should execute to overriding valid value', ->
    @foo2.exec(@ctx.withParameters { FooDP: false }).should.equal false

  it 'should throw when overriding value is wrong type', ->
    try
      @foo2.exec(@ctx.withParameters { FooDP: 12 })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

describe 'DecimalParameterTypes', ->
  @beforeEach ->
    setup @, data

  it 'should execute to provided valid value', ->
    @foo.exec(@ctx.withParameters { FooP: 3.0 }).should.equal 3.0

  it 'should throw when provided value is wrong type', ->
    try
      @foo.exec(@ctx.withParameters { FooP: '3' })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  it 'should execute to default value', ->
    @foo2.exec(@ctx).should.equal 1.5

  it 'should execute to overriding valid value', ->
    @foo2.exec(@ctx.withParameters { FooDP: 3.0 }).should.equal 3.0

  it 'should throw when overriding value is wrong type', ->
    try
      @foo2.exec(@ctx.withParameters { FooDP: '3' })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

describe 'IntegerParameterTypes', ->
  @beforeEach ->
    setup @, data

  it 'should execute to provided valid value', ->
    @foo.exec(@ctx.withParameters { FooP: 3 }).should.equal 3

  it 'should throw when provided value is wrong type', ->
    try
      @foo.exec(@ctx.withParameters { FooP: 3.5 })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  it 'should execute to default value', ->
    @foo2.exec(@ctx).should.equal 2

  it 'should execute to overriding valid value', ->
    @foo2.exec(@ctx.withParameters { FooDP: 3 }).should.equal 3

  it 'should throw when overriding value is wrong type', ->
    try
      @foo2.exec(@ctx.withParameters { FooDP: 3.5 })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

describe 'StringParameterTypes', ->
  @beforeEach ->
    setup @, data

  it 'should execute to provided valid value', ->
    @foo.exec(@ctx.withParameters { FooP: 'Hello World' }).should.equal 'Hello World'

  it 'should throw when provided value is wrong type', ->
    try
      @foo.exec(@ctx.withParameters { FooP: 42 })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  it 'should execute to default value', ->
    @foo2.exec(@ctx).should.equal 'Hello'

  it 'should execute to overriding valid value', ->
    @foo2.exec(@ctx.withParameters { FooDP: 'Hello World' }).should.equal 'Hello World'

  it 'should throw when overriding value is wrong type', ->
    try
      @foo2.exec(@ctx.withParameters { FooDP: 42 })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

describe 'ConceptParameterTypes', ->
  @beforeEach ->
    setup @, data

  it 'should execute to provided valid value', ->
    c = new Concept([new Code("foo", "http://foo.org")], "Foo")
    @foo.exec(@ctx.withParameters { FooP: c }).should.equal c

  it 'should throw when provided value is wrong type', ->
    c = new Code("foo", "http://foo.org")
    try
      @foo.exec(@ctx.withParameters { FooP: c })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  it 'should execute to default value', ->
    @foo2.exec(@ctx).should.eql new Concept([new Code("FooTest", "http://footest.org")], "Foo Test")

  it 'should execute to overriding valid value', ->
    c = new Concept([new Code("foo", "http://foo.org")], "Foo")
    @foo2.exec(@ctx.withParameters { FooDP: c }).should.equal c

  it 'should throw when overriding value is wrong type', ->
    c = new Code("foo", "http://foo.org")
    try
      @foo2.exec(@ctx.withParameters { FooDP: c })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

describe 'DateTimeParameterTypes', ->
  @beforeEach ->
    setup @, data

  it 'should execute to provided valid value', ->
    d = DateTime.parse('2012-10-25T12:55:14.456+00')
    @foo.exec(@ctx.withParameters { FooP: d }).should.equal d

  it 'should throw when provided value is wrong type', ->
    d = "2012-10-25T12:55:14.456+00"
    try
      @foo.exec(@ctx.withParameters { FooP: d })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  it 'should execute to default value', ->
    @foo2.exec(@ctx).should.eql DateTime.parse('2012-04-01')

  it 'should execute to overriding valid value', ->
    d = DateTime.parse('2012-10-25T12:55:14.456+00')
    @foo2.exec(@ctx.withParameters { FooDP: d }).should.equal d

  it 'should throw when overriding value is wrong type', ->
    d = "2012-10-25T12:55:14.456+00"
    try
      @foo2.exec(@ctx.withParameters { FooDP: d })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

describe 'QuantityParameterTypes', ->
  @beforeEach ->
    setup @, data

  it 'should execute to provided valid value', ->
    q = new Quantity({value: 5, unit: "mg"})
    @foo.exec(@ctx.withParameters { FooP: q }).should.equal q

  it 'should throw when provided value is wrong type', ->
    q = 5
    try
      @foo.exec(@ctx.withParameters { FooP: q })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  it 'should execute to default value', ->
    @foo2.exec(@ctx).should.eql new Quantity({value: 10, unit: "dL"})

  it 'should execute to overriding valid value', ->
    q = new Quantity({value: 5, unit: "mg"})
    @foo2.exec(@ctx.withParameters { FooDP: q }).should.equal q

  it 'should throw when overriding value is wrong type', ->
    q = 5
    try
      @foo2.exec(@ctx.withParameters { FooDP: q })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

describe 'TimeParameterTypes', ->
  @beforeEach ->
    setup @, data

  it 'should execute to provided valid value', ->
    t = DateTime.parse('2012-10-25T12:55:14.456+00').getTime()
    @foo.exec(@ctx.withParameters { FooP: t }).should.equal t

  it 'should throw when provided value is wrong type', ->
    t = DateTime.parse('2012-10-25T12:55:14.456+00')
    try
      @foo.exec(@ctx.withParameters { FooP: t })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  ### Currently Time literals and constructors don't work in execution engine
  it 'should execute to default value', ->
    @foo2.exec(@ctx).should.eql DateTime.parse('2012-10-25T12:00:00').getTime()
  ###

  it 'should execute to overriding valid value', ->
    t = DateTime.parse('2012-10-25T12:55:14.456+00').getTime()
    @foo2.exec(@ctx.withParameters { FooDP: t }).should.equal t

  it 'should throw when overriding value is wrong type', ->
    t = DateTime.parse('2012-10-25T12:55:14.456+00')
    try
      @foo2.exec(@ctx.withParameters { FooDP: t })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

describe 'ListParameterTypes', ->
  @beforeEach ->
    setup @, data

  it 'should execute to provided valid value', ->
    @foo.exec(@ctx.withParameters { FooP: ["Hello", "World"] }).should.eql ["Hello", "World"]

  it 'should throw when provided value is not a list', ->
    try
      @foo.exec(@ctx.withParameters { FooP: "Hello World" })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  it 'should throw when list contains a wrong type', ->
    try
      @foo.exec(@ctx.withParameters { FooP: ["Hello", 2468] })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  it 'should execute to default value', ->
    @foo2.exec(@ctx).should.eql ['a', 'b', 'c']

  it 'should execute to overriding valid value', ->
    @foo2.exec(@ctx.withParameters { FooDP: ["Hello", "World"] }).should.eql ["Hello", "World"]

  it 'should throw when overriding value is not a list', ->
    try
      @foo2.exec(@ctx.withParameters { FooDP: "Hello World" })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  it 'should throw when overriding list contains a wrong type', ->
    try
      @foo2.exec(@ctx.withParameters { FooDP: ["Hello", 2468] })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

describe 'IntervalParameterTypes', ->
  @beforeEach ->
    setup @, data

  it 'should execute to provided valid value', ->
    @foo.exec(@ctx.withParameters { FooP: new Interval(1, 5) }).should.eql new Interval(1, 5)

  it 'should throw when provided value is not an interval', ->
    try
      @foo.exec(@ctx.withParameters { FooP: [1, 5] })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  it 'should throw when interval contains a wrong point type', ->
    try
      @foo.exec(@ctx.withParameters { FooP: new Interval(1.5, 5.5) })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  it 'should execute to default value', ->
    @foo2.exec(@ctx).should.eql new Interval(2, 6)

  it 'should execute to overriding valid value', ->
    @foo2.exec(@ctx.withParameters { FooDP: new Interval(1, 5) }).should.eql new Interval(1, 5)

  it 'should throw when overriding value is not an interval', ->
    try
      @foo2.exec(@ctx.withParameters { FooDP: [1, 5] })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  it 'should throw when overriding interval contains a wrong point type', ->
    try
      @foo2.exec(@ctx.withParameters { FooDP: new Interval(1.5, 5.5) })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

describe 'TupleParameterTypes', ->
  @beforeEach ->
    setup @, data

  it 'should execute to provided valid value', ->
    t = { Hello: "World", MeaningOfLife: 42 }
    @foo.exec(@ctx.withParameters { FooP: t }).should.eql t

  it 'should allow missing tuple properties', ->
    t = { MeaningOfLife: 42 }
    @foo.exec(@ctx.withParameters { FooP: t }).should.eql t

  it 'should throw when provided value is not a tuple', ->
    try
      @foo.exec(@ctx.withParameters { FooP: "Hello World" })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  it 'should throw when tuple contains a wrong property type', ->
    try
      @foo.exec(@ctx.withParameters { FooP: { Hello: "World", MeaningOfLife: "Forty-Two" } })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  it 'should execute to default value', ->
    @foo2.exec(@ctx).should.eql { Hello: "Universe", MeaningOfLife: 24 }

  it 'should execute to overriding valid value', ->
    t = { Hello: "World", MeaningOfLife: 42 }
    @foo2.exec(@ctx.withParameters { FooDP: t }).should.eql t

  it 'should allow missing tuple properties in overriding tuple', ->
    t = { MeaningOfLife: 42 }
    @foo2.exec(@ctx.withParameters { FooDP: t }).should.eql t

  it 'should throw when overriding value is not a tuple', ->
    try
      @foo2.exec(@ctx.withParameters { FooDP: "Hello World" })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error

  it 'should throw when overriding tuple contains a wrong property type', ->
    try
      @foo2.exec(@ctx.withParameters { FooDP: { Hello: "World", MeaningOfLife: "Forty-Two" } })
      should.fail("Passing in wrong parameter type should throw an error")
    catch e
      e.should.be.instanceof Error