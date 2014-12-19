should = require 'should'
setup = require '../../setup'
data = require './data'
vsets = require './valuesets'
{ p1 } = require './patients'

describe 'DateRangeOptimizedQuery', ->
  @beforeEach ->
    setup @, data, [ p1 ], vsets

  it 'should find encounters performed during the MP', ->
    e = @encountersDuringMP.exec(@ctx)
    e.should.have.length(1)
    e[0].id().should.equal 'http://cqframework.org/3/5'

  it 'should find ambulatory encounters performed during the MP', ->
    e = @ambulatoryEncountersDuringMP.exec(@ctx)
    e.should.have.length(1)
    e[0].id().should.equal 'http://cqframework.org/3/5'

  it 'should find ambulatory encounter performances included in the MP', ->
    e = @ambulatoryEncountersIncludedInMP.exec(@ctx)
    e.should.have.length(1)
    e[0].id().should.equal 'http://cqframework.org/3/5'

describe.skip 'IncludesQuery', ->
  @beforeEach ->
    setup @, data, [ p1 ], vsets

  it 'should find ambulatory encounter performances included in the MP', ->
    e = @mPIncludedAmbulatoryEncounters.exec(@ctx)
    e.should.have.length(1)
    e[0].id().should.equal 'http://cqframework.org/3/5'

describe 'MultiSourceQuery', ->
  @beforeEach ->
    setup @, data, [ p1 ], vsets

  it 'should find all Encounters performed and Conditions', ->
    e = @msQuery.exec(@ctx)
    e.should.have.length(6)

  it 'should find encounters performed during the MP and All conditions', ->
    e = @msQueryWhere.exec(@ctx)
    e.should.have.length(2)

  it 'should be able to filter items in the where clause', ->
    e = @msQueryWhere2.exec(@ctx)
    e.should.have.length(1)

describe 'QueryRelationship', ->
  @beforeEach ->
    setup @, data, [ p1 ]

  it 'should be able to filter items with a with clause', ->
    e = @withQuery.exec(@ctx)
    e.should.have.length(3)

  it 'with clause should filter out items not available', ->
    e = @withQuery2.exec(@ctx)
    e.should.have.length(0)

  it 'should be able to filter items with a without clause', ->
    e = @withOutQuery.exec(@ctx)
    e.should.have.length(3)

  it 'without clause should be able to filter items with a without clause', ->
    e = @withOutQuery2.exec(@ctx)
    e.should.have.length(0)

describe 'QueryDefine', ->
  @beforeEach ->
    setup @, data, [ p1 ]

  it 'should be able to define a variable in a query and use it', ->
    e = @query.exec(@ctx)
    e.should.have.length(3)
    e[0]["a"].should.equal  e[0]["E"]
    e[1]["a"].should.equal  e[1]["E"]
    e[2]["a"].should.equal  e[2]["E"]

describe 'Tuple', ->
  @beforeEach ->
    setup @, data, [ p1 ]

  it 'should be able to return tuple from a query', ->
    e = @query.exec(@ctx)
    e.should.have.length(3)

describe 'Sorting', ->
  @beforeEach ->
    setup @, data, [ p1 ]

  it 'should be able to sort by a single field asc' , ->
    e = @singleAsc.exec(@ctx)
    e.should.have.length(3)
    e[0].E.id().should.equal "http://cqframework.org/3/1"
    e[1].E.id().should.equal  "http://cqframework.org/3/3"
    e[2].E.id().should.equal  "http://cqframework.org/3/5"

  it 'should be able to sort by a single field desc', ->
    e = @singleDesc.exec(@ctx)
    e.should.have.length(3)
    e[2].E.id().should.equal "http://cqframework.org/3/1"
    e[1].E.id().should.equal  "http://cqframework.org/3/3"
    e[0].E.id().should.equal  "http://cqframework.org/3/5"
