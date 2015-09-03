should = require 'should'
setup = require '../../setup'
data = require './data'
{ DateTime } = require '../../../lib/datatypes/datetime'

describe 'Instance', ->
  @beforeEach ->
    setup @, data

  it 'should create generic json objects with the correct key values', ->
    @quantity.exec(@ctx).unit.should.eql 'a'
    @quantity.exec(@ctx).value.should.eql 12
    @med.exec(@ctx).isBrand.should.eql false
    @med.exec(@ctx).name.should.eql "Best Med Ever"
    @val.exec(@ctx).should.eql 12


