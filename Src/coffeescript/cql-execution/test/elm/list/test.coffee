should = require 'should'
setup = require '../../setup'
data = require './data'

describe 'List', ->
  @beforeEach ->
    setup @, data

  it 'should execute to an array (ints)', ->
    @intList.exec(@ctx).should.eql [9, 7, 8]

  it 'should execute to an array (strings)', ->
    @stringList.exec(@ctx).should.eql ['a', 'bee', 'see']

  it 'should execute to an array (mixed)', ->
    @mixedList.exec(@ctx).should.eql [1, 'two', 3]

  it 'should execute to an empty array', ->
    @emptyList.exec(@ctx).should.eql []

describe 'Exists', ->
  @beforeEach ->
    setup @, data

  it 'should return false for empty list', ->
    @emptyList.exec(@ctx).should.be.false

  it 'should return true for full list', ->
    @fullList.exec(@ctx).should.be.true

describe 'Union', ->
  @beforeEach ->
    setup @, data

  it 'should union two lists to a single list', ->
    @oneToTen.exec(@ctx).should.eql [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

  it 'should maintain duplicate elements (according to CQL spec)', ->
    @oneToFiveOverlapped.exec(@ctx).should.eql [1, 2, 3, 4, 3, 4, 5]

  it 'should not fill in values in a disjoint union', ->
    @disjoint.exec(@ctx).should.eql [1, 2, 4, 5]

  it 'should return one list for multiple nested unions', ->
    @nestedToFifteen.exec(@ctx).should.eql [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]

describe 'Intersect', ->
  @beforeEach ->
    setup @, data

  it 'should intersect two disjoint lists  to an empty list', ->
    @noIntersection.exec(@ctx).should.eql []

  it 'should intersect two lists with a single common element', ->
    @intersectOnFive.exec(@ctx).should.eql [5]

  it 'should intersect two lists with several common elements', ->
    @intersectOnEvens.exec(@ctx).should.eql [2, 4, 6, 8, 10]

  it 'should intersect two identical lists to the same list', ->
    @intersectOnAll.exec(@ctx).should.eql [1, 2, 3, 4, 5]

  it 'should intersect multiple lists to only those elements common across all', ->
    @nestedIntersects.exec(@ctx).should.eql [4, 5]

describe 'InList', ->
  @beforeEach ->
    setup @, data

  it 'should execute to true when item is in list', ->
    @isIn.exec(@ctx).should.be.true

  it 'should execute to false when item is not in list', ->
    @isNotIn.exec(@ctx).should.be.false

describe 'Distinct', ->
  @beforeEach ->
    setup @, data

  it 'should remove duplicates', ->
    @lotsOfDups.exec(@ctx).should.eql [1, 2, 3, 4, 5]

  it 'should do nothing to an already distinct array', ->
    @noDups.exec(@ctx).should.eql [2, 4, 6, 8, 10]
