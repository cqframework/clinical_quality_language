should = require 'should'
setup = require '../../setup'
data = require './data'
{Repository} = require './repository'

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


describe 'Using CommonLib', ->
  @beforeEach ->
    setup @, data, [ p1, p2 ], {}, {}, new Repository(data)
    

  it 'Should have included a library', ->
    @lib.includes.should.not.be.empty

  it "should be able to execute expression from included library", ->
    @results = @lib.exec(@ctx)
    @results.patientResults['1'].ID.should.equal false
    @results.patientResults['2'].ID.should.equal true