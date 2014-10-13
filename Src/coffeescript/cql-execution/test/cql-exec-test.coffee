should = require 'should'
{ Library, Context, Results } =  require '../lib/cql-exec'
D = require './cql-test-data'
P = require './cql-test-patients'

setup = (test, patients=[], parameters={}) ->
  test.lib = new Library(D[test.test.parent.title])
  test.ctx = new Context(test.lib, patients, parameters)
  for k,v of test.lib.expressions
    test[k[0].toLowerCase() + k[1..-1]] = v.expression

describe 'InAgeDemographic', ->
  @beforeEach ->
    setup @, P.InAgeDemographic
    @results = @lib.exec(@ctx)
  
  it 'should have correct patient results', ->
    @results.patientResults['1'].InDemographic.should.equal false
    @results.patientResults['2'].InDemographic.should.equal true

  it 'should have empty population results', ->  
    @results.populationResults.should.be.empty

describe 'ExpressionDef', ->
  @beforeEach ->
    setup @
    @def = @lib.expressions.Foo

  it 'should have a name', ->
    @def.name.should.equal 'Foo'

  it 'should have the correct context', ->
    @def.context.should.equal 'PATIENT'

  it 'should execute to its value', ->
    @def.exec(@ctx).should.equal 'Bar'

describe 'ExpressionRef', ->
  @beforeEach ->
    setup @

  it 'should have a name', ->
    @foo.name.should.equal 'Life'

  it 'should execute to expression value', ->
    @foo.exec(@ctx).should.equal 42

describe 'ParameterDef', ->
  @beforeEach ->
    setup @
    @param = @lib.parameters.MeasureYear

  it 'should have a name', ->
    @param.name.should.equal 'MeasureYear'

  it 'should execute to default value', ->
    @param.exec(@ctx).should.equal 2012

  it 'should execute to provided value', ->
    @param.exec(@ctx.withParameters { MeasureYear: 2013 }).should.equal 2013

describe 'ParameterRef', ->
  @beforeEach ->
    setup @

  it 'should have a name', ->
    @foo.name.should.equal 'FooP'

  it 'should execute to default value', ->
    @foo.exec(@ctx).should.equal 'Bar'

  it 'should execute to provided value', ->
    @foo.exec(@ctx.withParameters { FooP: 'Bah' }).should.equal 'Bah'

describe 'And', ->
  @beforeEach ->
    setup @

  it 'should have type: And', ->
    @allTrue.type.should.equal 'And'

  it 'should execute allTrue as true', ->
    @allTrue.exec(@ctx).should.be.true

  it 'should execute allFalse as false', ->
    @allFalse.exec(@ctx).should.be.false

  it 'should execute someTrue as false', ->
    @someTrue.exec(@ctx).should.be.false

describe 'AgeAtFunctionRef', ->
  @beforeEach ->
    setup @, [P.p1]

  it 'should have type: FunctionRef', ->
    @ageAt2012.type.should.equal 'FunctionRef'

  it 'should execute age at 2012 as 31', ->
    @ageAt2012.exec(@ctx).should.equal 31

  it 'should execute age at 19810216 as 0', ->
    @ageAt19810216.exec(@ctx).should.equal 0

  it 'should execute age at 1975 as -5', ->
    @ageAt19810216.exec(@ctx).should.equal 0

describe 'DateFunctionRef', ->
  @beforeEach ->
    setup @

  it 'should have type: FunctionRef', ->
    @year.type.should.equal 'FunctionRef'

  it 'should execute year precision correctly', ->
    d = @year.exec(@ctx)
    d.year.should.equal 2012
    should.not.exist(d[field]) for field in [ 'month', 'day', 'hour', 'minute', 'second' ]

  it 'should execute month precision correctly', ->
    d = @month.exec(@ctx)
    d.year.should.equal 2012
    d.month.should.equal 4
    should.not.exist(d[field]) for field in [ 'day', 'hour', 'minute', 'second' ]

  it 'should execute day precision correctly', ->
    d = @day.exec(@ctx)
    d.year.should.equal 2012
    d.month.should.equal 4
    d.day.should.equal 15
    should.not.exist(d[field]) for field in [ 'hour', 'minute', 'second' ]

  it 'should execute hour precision correctly', ->
    d = @hour.exec(@ctx)
    d.year.should.equal 2012
    d.month.should.equal 4
    d.day.should.equal 15
    d.hour.should.equal 12
    should.not.exist(d[field]) for field in [ 'minute', 'second' ]

  it 'should execute minute precision correctly', ->
    d = @minute.exec(@ctx)
    d.year.should.equal 2012
    d.month.should.equal 4
    d.day.should.equal 15
    d.hour.should.equal 12
    d.minute.should.equal 10
    should.not.exist(d.second)

  it 'should execute second precision correctly', ->
    d = @second.exec(@ctx)
    d.year.should.equal 2012
    d.month.should.equal 4
    d.day.should.equal 15
    d.hour.should.equal 12
    d.minute.should.equal 10
    d.second.should.equal 59

# TO Comparisons for Dates

describe 'Greater', ->
  @beforeEach ->
    setup @

  it 'should have type: Greater', ->
    @aGtB_Int.type.should.equal 'Greater'

  it 'should be true for 5 > 4', ->
    @aGtB_Int.exec(@ctx).should.be.true

  it 'should be false for 5 > 5', ->
    @aEqB_Int.exec(@ctx).should.be.false

  it 'should be false for 5 > 6', ->
    @aLtB_Int.exec(@ctx).should.be.false

describe 'GreaterOrEqual', ->
  @beforeEach ->
    setup @

  it 'should have type: GreaterOrEqual', ->
    @aGtB_Int.type.should.equal 'GreaterOrEqual'

  it 'should be true for 5 >= 4', ->
    @aGtB_Int.exec(@ctx).should.be.true

  it 'should be true for 5 >= 5', ->
    @aEqB_Int.exec(@ctx).should.be.true

  it 'should be false for 5 >= 6', ->
    @aLtB_Int.exec(@ctx).should.be.false

describe 'Equal', ->
  @beforeEach ->
    setup @

  it 'should have type: Equal', ->
    @aGtB_Int.type.should.equal 'Equal'

  it 'should be false for 5 = 4', ->
    @aGtB_Int.exec(@ctx).should.be.false

  it 'should be true for 5 = 5', ->
    @aEqB_Int.exec(@ctx).should.be.true

  it 'should be false for 5 = 6', ->
    @aLtB_Int.exec(@ctx).should.be.false

describe 'LessOrEqual', ->
  @beforeEach ->
    setup @

  it 'should have type: LessOrEqual', ->
    @aGtB_Int.type.should.equal 'LessOrEqual'

  it 'should be false for 5 <= 4', ->
    @aGtB_Int.exec(@ctx).should.be.false

  it 'should be true for 5 <= 5', ->
    @aEqB_Int.exec(@ctx).should.be.true

  it 'should be true for 5 <= 6', ->
    @aLtB_Int.exec(@ctx).should.be.true

describe 'Less', ->
  @beforeEach ->
    setup @

  it 'should have type: Less', ->
    @aGtB_Int.type.should.equal 'Less'

  it 'should be false for 5 < 4', ->
    @aGtB_Int.exec(@ctx).should.be.false

  it 'should be false for 5 < 5', ->
    @aEqB_Int.exec(@ctx).should.be.false

  it 'should be true for 5 < 6', ->
    @aLtB_Int.exec(@ctx).should.be.true

describe 'Interval', ->
  @beforeEach ->
    setup @

  it 'should have type: Interval', ->
    @open.type.should.equal 'Interval'

  it 'should properly represent an open interval', ->
    @open.beginOpen.should.be.true
    @open.endOpen.should.be.true
    @open.begin.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    @open.end.exec(@ctx).toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

  it 'should properly represent a left-open interval', ->
    @leftOpen.beginOpen.should.be.true
    @leftOpen.endOpen.should.be.false
    @leftOpen.begin.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    @leftOpen.end.exec(@ctx).toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

  it 'should properly represent a right-open interval', ->
    @rightOpen.beginOpen.should.be.false
    @rightOpen.endOpen.should.be.true
    @rightOpen.begin.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    @rightOpen.end.exec(@ctx).toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

  it 'should properly represent a closed interval', ->
    @closed.beginOpen.should.be.false
    @closed.endOpen.should.be.false
    @closed.begin.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    @closed.end.exec(@ctx).toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

  it 'should exec as itself', ->
    @open.exec(@cql).should.equal @open

describe 'Begin', ->
  @beforeEach ->
    setup @

  it 'should have type: Begin', ->
    @foo.type.should.equal 'Begin'

  it 'should execute as the beginning of the interval', ->
    @foo.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)

describe 'Literal', ->
  @beforeEach ->
    setup @

  it 'should have type: Literal', ->
    @boolTrue.type.should.equal 'Literal'
    @boolFalse.type.should.equal 'Literal'
    @intOne.type.should.equal 'Literal'
    @stringTrue.type.should.equal 'Literal'

  it 'should convert true to boolean true', ->
    @boolTrue.value.should.be.true

  it 'should execute true as true', ->
    @boolTrue.exec(@ctx).should.be.true

  it 'should convert false to boolean false', ->
    @boolFalse.value.should.be.false

  it 'should execute false as false', ->
    @boolFalse.exec(@ctx).should.be.false

  it 'should convert 1 to int 1', ->
    @intOne.value.should.equal 1

  it 'should execute 1 as 1', ->
    @intOne.exec(@ctx).should.equal 1

  it 'should convert \'true\' to string \'true\'', ->
    @stringTrue.value.should.equal 'true'

  it 'should execute \'true\' as \'true\'', ->
    @stringTrue.exec(@ctx).should.equal 'true'
