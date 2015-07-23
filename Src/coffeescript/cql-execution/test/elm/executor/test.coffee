should = require 'should'
setup = require '../../setup'
data = require './data'


{ p1, p2 } = require './patients'

describe 'Age', ->
  @beforeEach ->
    setup @, data, [ p1, p2 ]
    @results = @executor.withLibrary(@lib).exec(@patientSource)

  it 'should have correct patient results', ->
    @results.patientResults['1'].Age.should.equal 32
    @results.patientResults['2'].Age.should.equal 5

  it 'should have the correct population results', ->
    @results.populationResults.AgeSum.should.equal 37

  it 'should be able to reference other population context expressions', ->
    @results.populationResults.AgeSumRef.should.equal 37

        