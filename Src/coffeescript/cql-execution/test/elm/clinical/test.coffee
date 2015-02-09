should = require 'should'
setup = require '../../setup'
data = require './data'
vsets = require './valuesets'
{ p1, p2 } = require './patients'
{ PatientSource} = require '../../../lib/cql-patient'


describe 'ValueSetDef', ->
  @beforeEach ->
    setup @, data, [], vsets

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
    setup @, data

  it 'should have a name', ->
    @foo.name.should.equal 'Acute Pharyngitis'

  it 'should execute to the defined value set', ->
    @foo.exec(@ctx).oid.should.equal '2.16.840.1.113883.3.464.1003.101.12.1001'

describe 'InValueSet', ->
  @beforeEach ->
    setup @, data, [], vsets

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

describe 'Patient Property In ValueSet', ->
  @beforeEach ->
    setup @, data, [], vsets

  it 'should find that John is not female', ->
    @ctx = @ctx.withPatients new PatientSource([ p1 ])
    @isFemale.exec(@ctx).should.be.false

  it 'should find that Sally is female', ->
    @ctx = @ctx.withPatients new PatientSource([ p2 ])
    @isFemale.exec(@ctx).should.be.true

describe 'CalculateAge', ->
  @beforeEach ->
    setup @, data, [ p1 ]
    # Note, tests are inexact (otherwise test needs to repeat exact logic we're testing)
    @highYears = new Date().getFullYear() - 1980
    @lowYears = @highYears - 1

  it 'should execute age in years', ->
    @years.exec(@ctx).should.be.within @lowYears, @highYears

  it 'should execute age in months', ->
    @months.exec(@ctx).should.be.within @lowYears * 12, @highYears * 12

  it 'should execute age in days', ->
    @days.exec(@ctx).should.be.within @lowYears * 372, @highYears * 372

  it 'should execute age in hours', ->
    @hours.exec(@ctx).should.be.within @lowYears * 8928, @highYears * 8928

  it 'should execute age in minutes', ->
    @minutes.exec(@ctx).should.be.within @lowYears * 535680, @highYears * 535680

  it 'should execute age in seconds', ->
    @seconds.exec(@ctx).should.be.within @lowYears * 32140800, @highYears * 32140800

describe 'CalculateAgeAt', ->
  @beforeEach ->
    setup @, data, [ p1 ]

  it 'should execute age at 2012 as 31', ->
    @ageAt2012.exec(@ctx).should.equal 31

  it 'should execute age at 19810216 as 0', ->
    @ageAt19810216.exec(@ctx).should.equal 0

  it 'should execute age at 1975 as -5', ->
    @ageAt19810216.exec(@ctx).should.equal 0
