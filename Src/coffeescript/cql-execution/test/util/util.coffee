should = require 'should'
{typeIsArray} = require '../../lib/util/util'

describe 'typeIsArray', ->

  it 'should properly identify arrays', ->
    typeIsArray([1,2,3]).should.be.true()
    typeIsArray(['a', 'b', 'c']).should.be.true()
    typeIsArray([['a','b','c'],[1,2,3]]).should.be.true()
    typeIsArray([{a: 1, b:2, c:3}, {x: 24, y: 25, z: 26}])
    typeIsArray([]).should.be.true()

  it 'should properly reject non-arrays', ->
    typeIsArray(1).should.be.false()
    typeIsArray('a').should.be.false()
    typeIsArray('[]').should.be.false()
    typeIsArray({a: 1, b:2, c:3}).should.be.false()
    typeIsArray({a: []}).should.be.false()
    typeIsArray(null).should.be.false()
