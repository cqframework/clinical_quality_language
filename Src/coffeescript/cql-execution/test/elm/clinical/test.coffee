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
    @ctx.patient =  new PatientSource([ p1 ]).currentPatient()
    @isFemale.exec(@ctx).should.be.false

  it 'should find that Sally is female', ->
    @ctx.patient =  new PatientSource([ p2 ]).currentPatient()
    @isFemale.exec(@ctx).should.be.true

describe 'CalculateAge', ->
  @beforeEach ->
    setup @, data, [ p1 ]
    # Note, tests are inexact (otherwise test needs to repeat exact logic we're testing)
    # p1 birth date is 1980-06-17
    @bday = new Date(1980, 5, 17)
    @today = new Date()
    # according to spec, dates without timezones are in *current* time offset, so need to adjust
    offsetDiff = @today.getTimezoneOffset() - @bday.getTimezoneOffset()
    @bday.setMinutes(@bday.getMinutes() + offsetDiff)

    # this is getting the possible number of months in years with the addtion of an offset
    # to get the correct number of months
    @full_months = ((@today.getFullYear() - 1980) * 12) + (@today.getMonth() - 5)
    @timediff = @today - @bday # diff in milliseconds

  it 'should execute age in years', ->
    @years.exec(@ctx).should.equal @full_months // 12

  it 'should execute age in months', ->
    # what is returned will depend on whether the day in the current month has
    # made it to the 17th day of the month as declared in the birthday
    [@full_months, @full_months-1].indexOf(@months.exec(@ctx)).should.not.equal -1

  it 'should execute age in days', ->
    @days.exec(@ctx).should.equal @timediff // 1000 // 60 // 60 // 24

  it 'should execute age in hours', ->
    # a little strange since the qicore data model specified birthDate as a date (no time)
    @hours.exec(@ctx).should.equal @timediff // 1000 // 60 // 60

  it 'should execute age in minutes', ->
    # a little strange since the qicore data model specified birthDate as a date (no time)
    @minutes.exec(@ctx).should.equal @timediff // 1000 // 60

  it 'should execute age in seconds', ->
    # a little strange since the qicore data model specified birthDate as a date (no time)
    @seconds.exec(@ctx).should.equal @timediff // 1000

describe 'CalculateAgeAt', ->
  @beforeEach ->
    setup @, data, [ p1 ]

  it 'should execute age at 2012 as 31', ->
    @ageAt2012.exec(@ctx).should.equal 31

  it 'should execute age at 19810216 as 0', ->
    @ageAt19810216.exec(@ctx).should.equal 0

  it 'should execute age at 1975 as -5', ->
    @ageAt19810216.exec(@ctx).should.equal 0
