should = require 'should'
setup = require '../../setup'
data = require './data'
{ DateTime } = require '../../../lib/datatypes/datetime'
{ Quantity } = require('../../../lib/elm/quantity')

describe 'Instance', ->
  @beforeEach ->
    setup @, data

  it 'should create generic json objects with the correct key values', ->
    q = @quantity.exec(@ctx)
    q.should.be.instanceof Quantity
    q.unit.should.eql 'a'
    q.value.should.eql 12
    q.toString().should.equal '12 \'a\''
    @med.exec(@ctx).isBrand.should.eql false
    @med.exec(@ctx).name.should.eql "Best Med Ever"
    @val.exec(@ctx).should.eql 12


