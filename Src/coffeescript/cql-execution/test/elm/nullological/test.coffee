should = require 'should'
setup = require '../../setup'
data = require './data'

describe 'Nil', ->
  @beforeEach ->
    setup @, data

  it 'should execute as null', ->
    should(@nil.exec(@ctx)).be.null
