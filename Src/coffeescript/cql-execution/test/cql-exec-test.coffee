should = require 'should'
{ Library, Context, Results } =  require '../lib/cql-exec'
{ CodeService } = require '../lib/cql-code-service'
PAT = require '../lib/cql-patient'
DT = require '../lib/cql-datatypes'
D = require './data/cql-test-data'
P = require './data/cql-test-patients'

setup = (test, patients=[], parameters={}) ->
  test.lib = new Library(D[test.test.parent.title])
  psource = new PAT.PatientSource(patients)  
  test.ctx = new Context(test.lib, psource, parameters)
  for k,v of test.lib.expressions
    test[k[0].toLowerCase() + k[1..-1]] = v.expression

describe 'InAgeDemographic', ->
  @beforeEach ->
    setup @, P.P1AndP2
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

describe 'ValueSetDef', ->
  @beforeEach ->
    setup @
    @ctx.withCodeService new CodeService {
      "2.16.840.1.113883.3.464.1003.101.12.1061" : {
        "20140501" : [
          { "code": "185349003", "system": "2.16.840.1.113883.6.96", "version": "2013-09" },
          { "code": "270427003", "system": "2.16.840.1.113883.6.96", "version": "2013-09" },
          { "code": "406547006", "system": "2.16.840.1.113883.6.96", "version": "2013-09" }
        ]
      }
    }

  it 'should return a resolved value set when the codeService knows about it', ->
    vs = @known.exec(@ctx)
    vs.oid.should.equal '2.16.840.1.113883.3.464.1003.101.12.1061'
    vs.version.should.equal '20140501'
    vs.codes.length.should.equal 3
  
  it 'should execute one-arg to ValueSet with ID', ->
    vs = @['unknown One Arg'].exec(@ctx)
    vs.oid.should.equal '1.2.3.4.5.6.7.8.9'
    should.not.exist vs.version

  it 'should execute two-arg to ValueSet with ID and version', ->
    vs = @['unknown Two Arg'].exec(@ctx)
    vs.oid.should.equal '1.2.3.4.5.6.7.8.9'
    vs.version.should.equal '1'

describe 'ValueSetRef', ->
  @beforeEach ->
    setup @
    @ctx.withCodeService new CodeService {}

  it 'should have a name', ->
    @foo.name.should.equal 'Acute Pharyngitis'

  it 'should execute to the defined value set', ->
    @foo.exec(@ctx).oid.should.equal '2.16.840.1.113883.3.464.1003.101.12.1001'

describe 'And', ->
  @beforeEach ->
    setup @

  it 'should execute true and...', ->
    @tT.exec(@ctx).should.be.true
    @tF.exec(@ctx).should.be.false
    should(@tN.exec(@ctx)).be.null

  it 'should execute false and...', ->
    @fF.exec(@ctx).should.be.false
    @fT.exec(@ctx).should.be.false
    @fN.exec(@ctx).should.be.false

  it 'should execute null and...', ->
    should(@nN.exec(@ctx)).be.null
    should(@nT.exec(@ctx)).be.null
    @nF.exec(@ctx).should.be.false

describe 'Or', ->
  @beforeEach ->
    setup @

  it 'should execute true or...', ->
    @tT.exec(@ctx).should.be.true
    @tF.exec(@ctx).should.be.true
    @tN.exec(@ctx).should.be.true

  it 'should execute false or...', ->
    @fF.exec(@ctx).should.be.false
    @fT.exec(@ctx).should.be.true
    should(@fN.exec(@ctx)).be.null

  it 'should execute null or...', ->
    should(@nN.exec(@ctx)).be.null
    @nT.exec(@ctx).should.be.true
    should(@nF.exec(@ctx)).be.null

describe 'XOr', ->
  @beforeEach ->
    setup @

  it 'should execute true xor...', ->
    @tT.exec(@ctx).should.be.false
    @tF.exec(@ctx).should.be.true
    should(@tN.exec(@ctx)).be.null

  it 'should execute false xor...', ->
    @fF.exec(@ctx).should.be.false
    @fT.exec(@ctx).should.be.true
    should(@fN.exec(@ctx)).be.null

  it 'should execute null xor...', ->
    should(@nN.exec(@ctx)).be.null
    should(@nT.exec(@ctx)).be.null
    should(@nF.exec(@ctx)).be.null

describe 'Not', ->
  @beforeEach ->
    setup @

  it 'should execute not true as false', ->
    @notTrue.exec(@ctx).should.be.false

  it 'should execute not false as true', ->
    @notFalse.exec(@ctx).should.be.true

  it 'should execute not null as null', ->
    should(@notNull.exec(@ctx)).be.null

describe 'AgeAtFunctionRef', ->
  @beforeEach ->
    setup @, [P.P1]

  it 'should execute age at 2012 as 31', ->
    @ageAt2012.exec(@ctx).should.equal 31

  it 'should execute age at 19810216 as 0', ->
    @ageAt19810216.exec(@ctx).should.equal 0

  it 'should execute age at 1975 as -5', ->
    @ageAt19810216.exec(@ctx).should.equal 0

describe 'DateFunctionRef', ->
  @beforeEach ->
    setup @

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

  it 'should be true for 5 > 4', ->
    @aGtB_Int.exec(@ctx).should.be.true

  it 'should be false for 5 > 5', ->
    @aEqB_Int.exec(@ctx).should.be.false

  it 'should be false for 5 > 6', ->
    @aLtB_Int.exec(@ctx).should.be.false

describe 'GreaterOrEqual', ->
  @beforeEach ->
    setup @

  it 'should be true for 5 >= 4', ->
    @aGtB_Int.exec(@ctx).should.be.true

  it 'should be true for 5 >= 5', ->
    @aEqB_Int.exec(@ctx).should.be.true

  it 'should be false for 5 >= 6', ->
    @aLtB_Int.exec(@ctx).should.be.false

describe 'Equal', ->
  @beforeEach ->
    setup @

  it 'should be false for 5 = 4', ->
    @aGtB_Int.exec(@ctx).should.be.false

  it 'should be true for 5 = 5', ->
    @aEqB_Int.exec(@ctx).should.be.true

  it 'should be false for 5 = 6', ->
    @aLtB_Int.exec(@ctx).should.be.false

describe 'LessOrEqual', ->
  @beforeEach ->
    setup @

  it 'should be false for 5 <= 4', ->
    @aGtB_Int.exec(@ctx).should.be.false

  it 'should be true for 5 <= 5', ->
    @aEqB_Int.exec(@ctx).should.be.true

  it 'should be true for 5 <= 6', ->
    @aLtB_Int.exec(@ctx).should.be.true

describe 'Less', ->
  @beforeEach ->
    setup @

  it 'should be false for 5 < 4', ->
    @aGtB_Int.exec(@ctx).should.be.false

  it 'should be false for 5 < 5', ->
    @aEqB_Int.exec(@ctx).should.be.false

  it 'should be true for 5 < 6', ->
    @aLtB_Int.exec(@ctx).should.be.true

describe 'List', ->
  @beforeEach ->
    setup @

  it 'should execute to an array (ints)', ->
    @intList.exec(@ctx).should.eql [9, 7, 8]

  it 'should execute to an array (strings)', ->
    @stringList.exec(@ctx).should.eql ['a', 'bee', 'see']

  it 'should execute to an array (mixed)', ->
    @mixedList.exec(@ctx).should.eql [1, 'two', 3]

  it 'should execute to an empty array', ->
    @emptyList.exec(@ctx).should.eql []

describe 'Exists', ->
  @beforeEach ->
    setup @

  it 'should return false for empty list', ->
    @emptyList.exec(@ctx).should.be.false

  it 'should return true for full list', ->
    @fullList.exec(@ctx).should.be.true

describe 'Interval', ->
  @beforeEach ->
    setup @

  it 'should properly represent an open interval', ->
    @open.lowClosed.should.be.false
    @open.highClosed.should.be.false
    @open.low.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    @open.high.exec(@ctx).toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

  it 'should properly represent a left-open interval', ->
    @leftOpen.lowClosed.should.be.false
    @leftOpen.highClosed.should.be.true
    @leftOpen.low.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    @leftOpen.high.exec(@ctx).toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

  it 'should properly represent a right-open interval', ->
    @rightOpen.lowClosed.should.be.true
    @rightOpen.highClosed.should.be.false
    @rightOpen.low.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    @rightOpen.high.exec(@ctx).toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

  it 'should properly represent a closed interval', ->
    @closed.lowClosed.should.be.true
    @closed.highClosed.should.be.true
    @closed.low.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    @closed.high.exec(@ctx).toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

  it 'should exec to native Interval datatype', ->
    ivl = @open.exec(@cql)
    ivl.should.be.instanceOf DT.Interval
    ivl.lowClosed.should.equal @open.lowClosed
    ivl.highClosed.should.equal @open.highClosed
    ivl.low.toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    ivl.high.toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

describe 'Start', ->
  @beforeEach ->
    setup @

  it 'should execute as the start of the interval', ->
    @foo.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)

describe 'InList', ->
  @beforeEach ->
    setup @

  it 'should execute to true when item is in list', ->
    @isIn.exec(@ctx).should.be.true

  it 'should execute to false when item is not in list', ->
    @isNotIn.exec(@ctx).should.be.false

describe 'Union', ->
  @beforeEach ->
    setup @

  it 'should union two lists to a single list', ->
    @oneToTen.exec(@ctx).should.eql [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

  it 'should maintain duplicate elements (according to CQL spec)', ->
    @oneToFiveOverlapped.exec(@ctx).should.eql [1, 2, 3, 4, 3, 4, 5]

  it 'should not fill in values in a disjoint union', ->
    @disjoint.exec(@ctx).should.eql [1, 2, 4, 5]

  it 'should return one list for multiple nested unions', ->
    @nestedToFifteen.exec(@ctx).should.eql [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]

describe 'Intersect', ->
  @beforeEach ->
    setup @

  it 'should intersect two disjoint lists  to an empty list', ->
    @noIntersection.exec(@ctx).should.eql []

  it 'should intersect two lists with a single common element', ->
    @intersectOnFive.exec(@ctx).should.eql [5]

  it 'should intersect two lists with several common elements', ->
    @intersectOnEvens.exec(@ctx).should.eql [2, 4, 6, 8, 10]

  it 'should intersect two identical lists to the same list', ->
    @intersectOnAll.exec(@ctx).should.eql [1, 2, 3, 4, 5]

  it 'should intersect multiple lists to only those elements common across all', ->
    @nestedIntersects.exec(@ctx).should.eql [4, 5]

describe 'Distinct', ->
  @beforeEach ->
    setup @

  it 'should remove duplicates', ->
    @lotsOfDups.exec(@ctx).should.eql [1, 2, 3, 4, 5]

  it 'should do nothing to an already distinct array', ->
    @noDups.exec(@ctx).should.eql [2, 4, 6, 8, 10]

describe 'InValueSet', ->
  @beforeEach ->
    setup @
    @ctx.withCodeService new CodeService {
      "2.16.840.1.113883.3.560.100.2" : {
        "20121025" : [
          { "code": "F", "system": "2.16.840.1.113883.18.2", "version": "HL7V2.5" }
        ]
      }
    }

  it 'should find string code in value set', ->
    @string.exec(@ctx).should.be.true

  it 'should find string code in versioned value set', ->
    @stringInVersionedValueSet.exec(@ctx).should.be.true

  it 'should find short code in value set', ->
    @shortCode.exec(@ctx).should.be.true

  it 'should find medium code in value set', ->
    @mediumCode.exec(@ctx).should.be.true

  it 'should find long code in value set', ->
    @longCode.exec(@ctx).should.be.true

  it 'should not find string code in value set', ->
    @wrongString.exec(@ctx).should.be.false

  it 'should not find string code in versioned value set', ->
    @wrongStringInVersionedValueSet.exec(@ctx).should.be.false

  it 'should not find short code in value set', ->
    @wrongShortCode.exec(@ctx).should.be.false

  it 'should not find medium code in value set', ->
    @wrongMediumCode.exec(@ctx).should.be.false

  it 'should not find long code in value set', ->
    @wrongLongCode.exec(@ctx).should.be.false

describe 'InValueSetFunction', ->
  @beforeEach ->
    setup @
    @ctx.withCodeService new CodeService {
      "2.16.840.1.113883.3.560.100.2" : {
        "20121025" : [
          { "code": "F", "system": "2.16.840.1.113883.18.2", "version": "HL7V2.5" }
        ]
      }
    }

  it 'should find string code in value set', ->
    @string.exec(@ctx).should.be.true

  it 'should find string code in versioned value set', ->
    @stringInVersionedValueSet.exec(@ctx).should.be.true

  it 'should find short code in value set', ->
    @shortCode.exec(@ctx)#.should.be.true

  it 'should find medium code in value set', ->
    @mediumCode.exec(@ctx).should.be.true

  it 'should find long code in value set', ->
    @longCode.exec(@ctx).should.be.true

  it 'should not find string code in value set', ->
    @wrongString.exec(@ctx).should.be.false

  it 'should not find string code in versioned value set', ->
    @wrongStringInVersionedValueSet.exec(@ctx).should.be.false

  it 'should not find short code in value set', ->
    @wrongShortCode.exec(@ctx).should.be.false

  it 'should not find medium code in value set', ->
    @wrongMediumCode.exec(@ctx).should.be.false

  it 'should not find long code in value set', ->
    @wrongLongCode.exec(@ctx).should.be.false

describe.skip 'PatientPropertyInValueSet', ->
  @beforeEach ->
    setup @
    @ctx.withCodeService new CodeService {
      "2.16.840.1.113883.3.560.100.2" : {
        "20121025" : [
          { "code": "F", "system": "2.16.840.1.113883.18.2", "version": "HL7V2.5" }
        ]
      }
    }

  it 'should find that John is not female', ->
    @ctx = @ctx.withPatients new PAT.PatientSource([ P.P1 ])
    @isFemale.exec(@ctx).should.be.false

  it 'should find that Sally is female', ->
    @ctx = @ctx.withPatients new PAT.PatientSource([ P.P2 ])
    @isFemale.exec(@ctx).should.be.true

describe 'Add', ->
  @beforeEach ->
    setup @

  it 'should add two numbers', ->
    @onePlusTwo.exec(@ctx).should.equal 3

  it 'should add multiple numbers', ->
    @addMultiple.exec(@ctx).should.equal 55

  it 'should add variables', ->
    @addVariables.exec(@ctx).should.equal 21

describe 'Subtract', ->
  @beforeEach ->
    setup @

  it 'should subtract two numbers', ->
    @fiveMinusTwo.exec(@ctx).should.equal 3

  it 'should subtract multiple numbers', ->
    @subtractMultiple.exec(@ctx).should.equal 15

  it 'should subtract variables', ->
    @subtractVariables.exec(@ctx).should.equal 1

describe 'Multiply', ->
  @beforeEach ->
    setup @

  it 'should multiply two numbers', ->
    @fiveTimesTwo.exec(@ctx).should.equal 10

  it 'should multiply multiple numbers', ->
    @multiplyMultiple.exec(@ctx).should.equal 120

  it 'should multiply variables', ->
    @multiplyVariables.exec(@ctx).should.equal 110

describe 'Divide', ->
  @beforeEach ->
    setup @

  it 'should divide two numbers', ->
    @tenDividedByTwo.exec(@ctx).should.equal 5

  it 'should divide two numbers that don\'t evenly divide', ->
    @tenDividedByFour.exec(@ctx).should.equal 2.5

  it 'should divide multiple numbers', ->
    @divideMultiple.exec(@ctx).should.equal 5

  it 'should divide variables', ->
    @divideVariables.exec(@ctx).should.equal 25

describe 'MathPrecedence', ->
  @beforeEach ->
    setup @

  it 'should follow order of operations', ->
    @mixed.exec(@ctx).should.equal 46

  it 'should allow parentheses to override order of operations', ->
    @parenthetical.exec(@ctx).should.equal -10

describe 'Literal', ->
  @beforeEach ->
    setup @

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

  it 'should convert .1 to decimal .1', ->
    @decimalTenth.value.should.equal 0.1

  it 'should execute .1 as .1', ->
    @decimalTenth.exec(@ctx).should.equal 0.1

  it 'should convert \'true\' to string \'true\'', ->
    @stringTrue.value.should.equal 'true'

  it 'should execute \'true\' as \'true\'', ->
    @stringTrue.exec(@ctx).should.equal 'true'

describe 'Nil', ->
  @beforeEach ->
    setup @

  it 'should execute as null', ->
    should(@nil.exec(@ctx)).be.null

describe 'Retrieve', ->
  @beforeEach ->
    setup @
    @ctx.withPatients new PAT.PatientSource([P.P3])
    @ctx.withCodeService new CodeService {
      "2.16.840.1.113883.3.464.1003.102.12.1011" : {
        "20140501" : [
          { "code": "034.0", "system": "2.16.840.1.113883.6.103", "version": "2013" },
          { "code": "1532007", "system": "2.16.840.1.113883.6.96", "version": "2013-09" },
          { "code": "43878008", "system": "2.16.840.1.113883.6.96", "version": "2013-09" }
        ]
      },
      "2.16.840.1.113883.3.464.1003.101.12.1061" : {
        "20140501" : [
          { "code": "185349003", "system": "2.16.840.1.113883.6.96", "version": "2013-09" },
          { "code": "270427003", "system": "2.16.840.1.113883.6.96", "version": "2013-09" },
          { "code": "406547006", "system": "2.16.840.1.113883.6.96", "version": "2013-09" }
        ]
      },
      "2.16.840.1.113883.3.526.3.1010" : {
        "20140501" : [
          { "code": "109264009", "system": "2.16.840.1.113883.6.96", "version": "2013-09" },
          { "code": "109383000", "system": "2.16.840.1.113883.6.96", "version": "2013-09" },
          { "code": "109962001", "system": "2.16.840.1.113883.6.96", "version": "2013-09" }
        ]
      },
      "2.16.840.1.113883.3.526.3.1240" : {
        "20140501" : [
          { "code": "G0438", "system": "2.16.840.1.113883.6.285", "version": "2014" },
          { "code": "G0439", "system": "2.16.840.1.113883.6.285", "version": "2014" }
        ]
      }
    }

  it 'should find observations', ->
    c = @conditions.exec(@ctx)
    c.should.have.length(2)
    c[0].get('identifier').id.should.equal 'http://cqframework.org/3/2'
    c[1].get('identifier').id.should.equal 'http://cqframework.org/3/4'

  it 'should find encounter performances', ->
    e = @encounters.exec(@ctx)
    e.should.have.length(3)
    e[0].get('identifier').id.should.equal 'http://cqframework.org/3/1'
    e[1].get('identifier').id.should.equal 'http://cqframework.org/3/3'
    e[2].get('identifier').id.should.equal 'http://cqframework.org/3/5'

  it 'should find observations with a value set', ->
    p = @pharyngitisConditions.exec(@ctx)
    p.should.have.length(1)
    p[0].get('identifier').id.should.equal 'http://cqframework.org/3/2'

  it 'should find encounter performances with a value set', ->
    a = @ambulatoryEncounters.exec(@ctx)
    a.should.have.length(3)
    a[0].get('identifier').id.should.equal 'http://cqframework.org/3/1'
    a[1].get('identifier').id.should.equal 'http://cqframework.org/3/3'
    a[2].get('identifier').id.should.equal 'http://cqframework.org/3/5'

  it 'should find encounter performances by service type', ->
    e = @encountersByServiceType.exec(@ctx)
    e.should.have.length(2)
    e[0].get('identifier').id.should.equal 'http://cqframework.org/3/1'
    e[1].get('identifier').id.should.equal 'http://cqframework.org/3/5'

  it 'should not find encounter proposals when they don\'t exist', ->
    e = @wrongDataType.exec(@ctx)
    e.should.be.empty

  it 'should not find conditions with wrong valueset', ->
    e = @wrongValueSet.exec(@ctx)
    e.should.be.empty

  it 'should not find encounter performances using wrong codeProperty', ->
    e = @wrongCodeProperty.exec(@ctx)
    e.should.be.empty

describe 'DateRangeOptimizedQuery', ->
  @beforeEach ->
    setup @
    @ctx.withPatients new PAT.PatientSource([P.P3])
    @ctx.withCodeService new CodeService {
      "2.16.840.1.113883.3.464.1003.101.12.1061" : {
        "20140501" : [
          { "code": "185349003", "system": "2.16.840.1.113883.6.96", "version": "2013-09" },
          { "code": "270427003", "system": "2.16.840.1.113883.6.96", "version": "2013-09" },
          { "code": "406547006", "system": "2.16.840.1.113883.6.96", "version": "2013-09" }
        ]
      }
    }

  it 'should find encounters performed during the MP', ->
    e = @encountersDuringMP.exec(@ctx)
    e.should.have.length(1)
    e[0].get('identifier').id.should.equal 'http://cqframework.org/3/5'

  it 'should find ambulatory encounters performed during the MP', ->
    e = @ambulatoryEncountersDuringMP.exec(@ctx)
    e.should.have.length(1)
    e[0].get('identifier').id.should.equal 'http://cqframework.org/3/5'

  it 'should find ambulatory encounter performances included in the MP', ->
    e = @ambulatoryEncountersIncludedInMP.exec(@ctx)
    e.should.have.length(1)
    e[0].get('identifier').id.should.equal 'http://cqframework.org/3/5'

describe 'MultiSourceQuery', ->
  @beforeEach ->
    setup @
    @ctx.withPatients new PAT.PatientSource([P.P3])
    @ctx.withCodeService new CodeService {
      "2.16.840.1.113883.3.464.1003.101.12.1061" : {
        "20140501" : [
          { "code": "185349003", "system": "2.16.840.1.113883.6.96", "version": "2013-09" },
          { "code": "270427003", "system": "2.16.840.1.113883.6.96", "version": "2013-09" },
          { "code": "406547006", "system": "2.16.840.1.113883.6.96", "version": "2013-09" }
        ]
      }
    }

  it 'should find all Encounters performed and Conditions', ->
    e = @msQuery.exec(@ctx)
    e.should.have.length(6)
    # e["E"].should.have.length(3)
    # e["C"].should.have.length(2)
  
  it 'should find encounters performed during the MP and All conditions', ->
    e = @msQueryWhere.exec(@ctx)
    e.should.have.length(2)
  
describe 'QueryDefine', ->
  @beforeEach ->
    setup @
    @ctx.withPatients new PAT.PatientSource([P.P3])

  it 'should be able to define a variable in a query and use it', ->
    e = @query.exec(@ctx)
    e.should.have.length(3)
    e[0]["a"].should.equal  e[0]["E"]
    e[1]["a"].should.equal  e[1]["E"]
    e[2]["a"].should.equal  e[2]["E"]

describe 'Tuple', ->
  @beforeEach ->
    setup @
    @ctx.withPatients new PAT.PatientSource([P.P3])
    
  it 'should be able to define a tuple', ->
    e = @tup.exec(@ctx)
    e["a"].should.equal 1
    e["b"].should.equal 2
  
  it 'should be able to return tuple from a query', ->
    e = @query.exec(@ctx)
    e.should.have.length(3)


describe.skip 'IncludesQuery', ->
  @beforeEach ->
    setup @
    @ctx.withPatients new PAT.PatientSource([P.P3])
    @ctx.withCodeService new CodeService {
      "2.16.840.1.113883.3.464.1003.101.12.1061" : {
        "20140501" : [
          { "code": "185349003", "system": "2.16.840.1.113883.6.96", "version": "2013-09" },
          { "code": "270427003", "system": "2.16.840.1.113883.6.96", "version": "2013-09" },
          { "code": "406547006", "system": "2.16.840.1.113883.6.96", "version": "2013-09" }
        ]
      }
    }

  it 'should find ambulatory encounter performances included in the MP', ->
    e = @mPIncludedAmbulatoryEncounters.exec(@ctx)
    e.should.have.length(1)
    e[0].get('identifier').id.should.equal 'http://cqframework.org/3/5'

###
describe.only 'ScratchPad', ->
  it 'is a quick scratchpad for simple testing during development', ->
    setup @
    console.log JSON.stringify(@foo, undefined, 2)
###
