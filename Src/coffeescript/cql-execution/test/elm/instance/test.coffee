should = require 'should'
setup = require '../../setup'
data = require './data'
{ DateTime } = require '../../../lib/datatypes/datetime'
{ Code, Concept } = require '../../../lib/datatypes/clinical'
{ Quantity } = require '../../../lib/elm/quantity'

describe 'Instance', ->
  @beforeEach ->
    setup @, data

  it 'should be able to construct a Quantity', ->
    q = @quantityA.exec(@ctx)
    q.should.be.instanceof Quantity
    q.unit.should.eql 'a'
    q.value.should.eql 12
    q.toString().should.equal '12 \'a\''
    @val.exec(@ctx).should.eql 12

  it 'should be able to construct a Code', ->
    c = @codeA.exec(@ctx)
    c.should.be.instanceof Code
    c.code.should.equal '12345'
    c.system.should.equal 'http://loinc.org'
    c.version.should.equal '1'
    c.display.should.equal 'Test Code'

  it 'should be able to construct a Concept', ->
    c = @conceptA.exec(@ctx)
    c.should.be.instanceof Concept
    c.codes.should.have.length 1
    c.codes[0].code.should.equal '12345'
    c.codes[0].system.should.equal 'http://loinc.org'
    c.codes[0].version.should.equal '1'
    c.codes[0].display.should.equal 'Test Code'
    c.display.should.equal 'Test Concept'

  it 'should create generic json objects with the correct key values', ->
    @med.exec(@ctx).isBrand.should.eql false
    @med.exec(@ctx).name.should.eql "Best Med Ever"
