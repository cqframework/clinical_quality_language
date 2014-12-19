should = require 'should'
setup = require '../../setup'
data = require './data'

describe 'And', ->
  @beforeEach ->
    setup @, data

  it 'should execute true and...', ->
    @tT.exec(@ctx).should.be.true
    @tF.exec(@ctx).should.be.false
    should(@tN.exec(@ctx)).be.null

  it 'should execute false and...', ->
    @fF.exec(@ctx).should.be.false
    @fT.exec(@ctx).should.be.false
    @fN.exec(@ctx).should.be.false

  it 'should execute null and...', ->
    should(@nN.exec(@ctx)).be.null
    should(@nT.exec(@ctx)).be.null
    @nF.exec(@ctx).should.be.false

describe 'Or', ->
  @beforeEach ->
    setup @, data

  it 'should execute true or...', ->
    @tT.exec(@ctx).should.be.true
    @tF.exec(@ctx).should.be.true
    @tN.exec(@ctx).should.be.true

  it 'should execute false or...', ->
    @fF.exec(@ctx).should.be.false
    @fT.exec(@ctx).should.be.true
    should(@fN.exec(@ctx)).be.null

  it 'should execute null or...', ->
    should(@nN.exec(@ctx)).be.null
    @nT.exec(@ctx).should.be.true
    should(@nF.exec(@ctx)).be.null

describe 'Not', ->
  @beforeEach ->
    setup @, data

  it 'should execute not true as false', ->
    @notTrue.exec(@ctx).should.be.false

  it 'should execute not false as true', ->
    @notFalse.exec(@ctx).should.be.true

  it 'should execute not null as null', ->
    should(@notNull.exec(@ctx)).be.null

describe 'XOr', ->
  @beforeEach ->
    setup @, data

  it 'should execute true xor...', ->
    @tT.exec(@ctx).should.be.false
    @tF.exec(@ctx).should.be.true
    should(@tN.exec(@ctx)).be.null

  it 'should execute false xor...', ->
    @fF.exec(@ctx).should.be.false
    @fT.exec(@ctx).should.be.true
    should(@fN.exec(@ctx)).be.null

  it 'should execute null xor...', ->
    should(@nN.exec(@ctx)).be.null
    should(@nT.exec(@ctx)).be.null
    should(@nF.exec(@ctx)).be.null
