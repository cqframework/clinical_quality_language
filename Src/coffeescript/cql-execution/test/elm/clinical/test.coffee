should = require 'should'
setup = require '../../setup'
data = require './data'
vsets = require './valuesets'
{ Uncertainty } = require '../../../lib/datatypes/uncertainty'
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
    @string.exec(@ctx).should.be.true()

  it 'should find string code in versioned value set', ->
    @stringInVersionedValueSet.exec(@ctx).should.be.true()

  it 'should find short code in value set', ->
    @shortCode.exec(@ctx).should.be.true()

  it 'should find medium code in value set', ->
    @mediumCode.exec(@ctx).should.be.true()

  it 'should find long code in value set', ->
    @longCode.exec(@ctx).should.be.true()

  it 'should not find string code in value set', ->
    @wrongString.exec(@ctx).should.be.false()

  it 'should not find string code in versioned value set', ->
    @wrongStringInVersionedValueSet.exec(@ctx).should.be.false()

  it 'should not find short code in value set', ->
    @wrongShortCode.exec(@ctx).should.be.false()

  it 'should not find medium code in value set', ->
    @wrongMediumCode.exec(@ctx).should.be.false()

  it 'should find long code with different version in value set', ->
    @longCodeDifferentVersion.exec(@ctx).should.be.true()

describe 'Patient Property In ValueSet', ->
  @beforeEach ->
    setup @, data, [], vsets

  it 'should find that John is not female', ->
    @ctx.patient =  new PatientSource([ p1 ]).currentPatient()
    @isFemale.exec(@ctx).should.be.false()

  it 'should find that Sally is female', ->
    @ctx.patient =  new PatientSource([ p2 ]).currentPatient()
    @isFemale.exec(@ctx).should.be.true()

describe 'CodeDef', ->
  @beforeEach ->
    setup @, data, []

  it 'should return a CodeDef', ->
    codeDef = @lib.getCode('Tobacco smoking status code')
    codeDef.constructor.name.should.equal 'CodeDef'
    codeDef.name.should.equal 'Tobacco smoking status code'

  it 'should execute to a Code datatype', ->
    codeDef = @lib.getCode('Tobacco smoking status code')
    code = codeDef.exec(@ctx)
    code.code.should.equal('72166-2')
    code.system.should.equal('http://loinc.org')
    should.not.exist(code.version)
    code.display.should.equal('Tobacco smoking status')

describe 'CodeRef', ->
  @beforeEach ->
    setup @, data

  it 'should have a name', ->
    @foo.name.should.equal 'Tobacco smoking status code'

  it 'should execute to the defined code', ->
    code = @foo.exec(@ctx)
    code.code.should.equal('72166-2')
    code.system.should.equal('http://loinc.org')
    should.not.exist(code.version)
    code.display.should.equal('Tobacco smoking status')

describe 'ConceptDef', ->
  @beforeEach ->
    setup @, data, []

  it 'should return a ConceptDef', ->
    conceptDef = @lib.getConcept('Tobacco smoking status')
    conceptDef.constructor.name.should.equal 'ConceptDef'
    conceptDef.name.should.equal 'Tobacco smoking status'

  it 'should execute to a Concept datatype', ->
    conceptDef = @lib.getConcept('Tobacco smoking status')
    concept = conceptDef.exec(@ctx)
    concept.text.should.equal('Tobacco smoking status')
    concept.codes.should.have.length(1)
    concept.codes[0].code.should.equal('72166-2')
    concept.codes[0].system.should.equal('http://loinc.org')
    should.not.exist(concept.codes[0].version)
    concept.codes[0].display.should.equal('Tobacco smoking status')

describe 'ConceptRef', ->
  @beforeEach ->
    setup @, data

  it 'should have a name', ->
    @foo.name.should.equal 'Tobacco smoking status'

  it 'should execute to the defined concept', ->
    concept = @foo.exec(@ctx)
    concept.text.should.equal('Tobacco smoking status')
    concept.codes.should.have.length(1)
    concept.codes[0].code.should.equal('72166-2')
    concept.codes[0].system.should.equal('http://loinc.org')
    should.not.exist(concept.codes[0].version)
    concept.codes[0].display.should.equal('Tobacco smoking status')

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
    @full_months = ((@today.getFullYear() - 1980) * 12) + (@today.getMonth() - 6)
    @timediff = @today - @bday # diff in milliseconds

  it 'should execute age in years', ->
    @years.exec(@ctx).should.equal @full_months // 12

  it 'should execute age in months', ->
    # what is returned will depend on whether the day in the current month has
    # made it to the 17th day of the month as declared in the birthday
    dayOfMonth = @today
    for i in [1 .. 28]
      dayOfMonth.setDate(i)
      month_offset = if dayOfMonth.getMonth() == 5 && dayOfMonth.getDate() < 17 then 6 else 5
      full_months = ((dayOfMonth.getFullYear() - 1980) * 12) + (dayOfMonth.getMonth() - month_offset)
      [full_months, full_months+1].indexOf(@months.exec(@ctx)).should.not.equal -1

  # Skipping because cql-to-elm in this branch does not properly translate AgeInWeeks
  it.skip 'should execute age in weeks', ->
    # this is an uncertainty since birthdate is only specfied to days
    @weeks.exec(@ctx).should.eql Math.floor(@timediff // 1000 // 60 // 60 // 24 // 7)

  it 'should execute age in days', ->
    # this is an uncertainty since birthdate is only specfied to days
    days = @timediff // 1000 // 60 // 60 // 24
    @days.exec(@ctx).should.eql new Uncertainty(days-1, days)

  it 'should execute age in hours', ->
    # this is an uncertainty since birthdate is only specfied to days
    hours = @timediff // 1000 // 60 // 60
    @hours.exec(@ctx).should.eql new Uncertainty(hours-24, hours)

  it 'should execute age in minutes', ->
    # this is an uncertainty since birthdate is only specfied to days
    minutes = @timediff // 1000 // 60
    @minutes.exec(@ctx).should.eql new Uncertainty(minutes-(24*60), minutes)

  it 'should execute age in seconds', ->
    # this is an uncertainty since birthdate is only specfied to days
    seconds = @timediff // 1000
    @seconds.exec(@ctx).should.eql new Uncertainty(seconds-(24*60*60), seconds)

describe 'CalculateAgeAt', ->
  @beforeEach ->
    setup @, data, [ p1 ]

  it 'should execute age at 2012 as 31 - 32 (since 2012 is not precise to days)', ->
    @ageAt2012.exec(@ctx).should.eql new Uncertainty(31, 32)

  it 'should execute age at 19810216 as 0', ->
    @ageAt19810216.exec(@ctx).should.equal 0

  it 'should execute age at 1975 as -5 to -4 (since 1975 is not precise to days)', ->
    @ageAt1975.exec(@ctx).should.eql new Uncertainty(-5, -4)
