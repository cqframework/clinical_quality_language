should = require 'should'
setup = require '../../setup'
data = require './data'
{ Interval } = require '../../../lib/datatypes/interval'

describe 'Interval', ->
  @beforeEach ->
    setup @, data

  it 'should properly represent an open interval', ->
    @open.lowClosed.should.be.false
    @open.highClosed.should.be.false
    @open.low.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    @open.high.exec(@ctx).toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

  it 'should properly represent a left-open interval', ->
    @leftOpen.lowClosed.should.be.false
    @leftOpen.highClosed.should.be.true
    @leftOpen.low.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    @leftOpen.high.exec(@ctx).toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

  it 'should properly represent a right-open interval', ->
    @rightOpen.lowClosed.should.be.true
    @rightOpen.highClosed.should.be.false
    @rightOpen.low.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    @rightOpen.high.exec(@ctx).toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

  it 'should properly represent a closed interval', ->
    @closed.lowClosed.should.be.true
    @closed.highClosed.should.be.true
    @closed.low.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    @closed.high.exec(@ctx).toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

  it 'should exec to native Interval datatype', ->
    ivl = @open.exec(@cql)
    ivl.should.be.instanceOf Interval
    ivl.lowClosed.should.equal @open.lowClosed
    ivl.highClosed.should.equal @open.highClosed
    ivl.low.toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
    ivl.high.toJSDate().should.eql new Date(2013, 0, 1, 0, 0, 0)

describe 'Start', ->
  @beforeEach ->
    setup @, data

  it 'should execute as the start of the interval', ->
    @foo.exec(@ctx).toJSDate().should.eql new Date(2012, 0, 1, 0, 0, 0)
