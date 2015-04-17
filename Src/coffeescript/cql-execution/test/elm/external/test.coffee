should = require 'should'
setup = require '../../setup'
data = require './data'
vsets = require './valuesets'
{ p1 } = require './patients'

describe 'Retrieve', ->
  @beforeEach ->
    setup @, data, [ p1 ], vsets

  it 'should find conditions', ->
    c = @conditions.exec(@ctx)
    c.should.have.length(2)
    c[0].id().should.equal 'http://cqframework.org/3/2'
    c[1].id().should.equal 'http://cqframework.org/3/4'

  it 'should find encounter performances', ->
    e = @encounters.exec(@ctx)
    e.should.have.length(3)
    e[0].id().should.equal 'http://cqframework.org/3/1'
    e[1].id().should.equal 'http://cqframework.org/3/3'
    e[2].id().should.equal 'http://cqframework.org/3/5'

  it 'should find observations with a value set', ->
    p = @pharyngitisConditions.exec(@ctx)
    p.should.have.length(1)
    p[0].id().should.equal 'http://cqframework.org/3/2'

  it 'should find encounter performances with a value set', ->
    a = @ambulatoryEncounters.exec(@ctx)
    a.should.have.length(3)
    a[0].id().should.equal 'http://cqframework.org/3/1'
    a[1].id().should.equal 'http://cqframework.org/3/3'
    a[2].id().should.equal 'http://cqframework.org/3/5'

  it 'should find encounter performances by service type', ->
    e = @encountersByServiceType.exec(@ctx)
    e.should.have.length(3)
    e[0].id().should.equal 'http://cqframework.org/3/1'
    e[1].id().should.equal 'http://cqframework.org/3/3'
    e[2].id().should.equal 'http://cqframework.org/3/5'

  it 'should not find conditions with wrong valueset', ->
    e = @wrongValueSet.exec(@ctx)
    e.should.be.empty

  it 'should not find encounter performances using wrong codeProperty', ->
    e = @wrongCodeProperty.exec(@ctx)
    e.should.be.empty
