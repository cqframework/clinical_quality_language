should = require 'should'
setup = require '../../setup'
data = require './data'
{ p1, p2 } = require './patients'

describe 'In Age Demographic', ->
  @beforeEach ->
    setup @, data, [ p1, p2 ]
    @results = @lib.exec(@ctx)

  it 'should have correct patient results', ->
    @results.patientResults['1'].InDemographic.should.equal false
    @results.patientResults['2'].InDemographic.should.equal true

  it 'should have empty population results', ->
    @results.populationResults.should.be.empty
