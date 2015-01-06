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

describe 'Equal', ->
  @beforeEach ->
    setup @, data

  it 'should identify equal lists of integers', ->
    @equalIntList.exec(@ctx).should.be.true

  it 'should identify unequal lists of integers', ->
    @unequalIntList.exec(@ctx).should.be.false

  it 'should identify re-ordered lists of integers as unequal', ->
    @reverseIntList.exec(@ctx).should.be.false

  it 'should identify equal lists of strings', ->
    @equalStringList.exec(@ctx).should.be.true

  it 'should identify unequal lists of strings', ->
    @unequalStringList.exec(@ctx).should.be.false

  it 'should identify equal lists of tuples', ->
    @equalTupleList.exec(@ctx).should.be.true

  it 'should identify unequal lists of integers', ->
    @unequalTupleList.exec(@ctx).should.be.false

describe 'NotEqual', ->
  @beforeEach ->
    setup @, data

  it 'should identify equal lists of integers', ->
    @equalIntList.exec(@ctx).should.be.false

  it 'should identify unequal lists of integers', ->
    @unequalIntList.exec(@ctx).should.be.true

  it 'should identify re-ordered lists of integers as unequal', ->
    @reverseIntList.exec(@ctx).should.be.true

  it 'should identify equal lists of strings', ->
    @equalStringList.exec(@ctx).should.be.false

  it 'should identify unequal lists of strings', ->
    @unequalStringList.exec(@ctx).should.be.true

  it 'should identify equal lists of tuples', ->
    @equalTupleList.exec(@ctx).should.be.false

  it 'should identify unequal lists of integers', ->
    @unequalTupleList.exec(@ctx).should.be.true

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

describe 'Expand', ->
  @beforeEach ->
    setup @, data

  it 'should expand a list of lists', ->
    @listOfLists.exec(@ctx).should.eql [1, 2, 3, 4, 5, 6, 7, 8, 9, 9, 8, 7, 6, 5, 4, 3, 2, 1]

  it 'should do nothing with a list of integers', ->
    @listOfInts.exec(@ctx).should.eql [1, 2, 3, 4, 5, 6, 7, 8, 9]

  it 'should do nothing with a mixed list', ->
    @mixedList.exec(@ctx).should.eql [1, 2, 3, [4, 5, 6], 7, 8, 9]

  it 'should return null for a null list', ->
    should(@nullValue.exec(@ctx)).be.null

describe 'Distinct', ->
  @beforeEach ->
    setup @, data

  it 'should remove duplicates', ->
    @lotsOfDups.exec(@ctx).should.eql [1, 2, 3, 4, 5]

  it 'should do nothing to an already distinct array', ->
    @noDups.exec(@ctx).should.eql [2, 4, 6, 8, 10]

describe 'First', ->
  @beforeEach ->
    setup @, data

  it 'should get first of a list of numbers', ->
    @numbers.exec(@ctx).should.equal 1

  it 'should get first of a list of letters', ->
    @letters.exec(@ctx).should.equal 'a'

  it 'should get first of a list of lists', ->
    @lists.exec(@ctx).should.eql ['a','b','c']

  it 'should get first of a list of tuples', ->
    @tuples.exec(@ctx).should.eql { a: 1, b: 2, c: 3 }

  it 'should get first of a list of unordered numbers', ->
    @unordered.exec(@ctx).should.equal 3

  it 'should return null for an empty list', ->
    should(@empty.exec(@ctx)).be.null

  it 'should return null for an empty list', ->
    should(@nullValue.exec(@ctx)).be.null

describe 'Last', ->
  @beforeEach ->
    setup @, data

  it 'should get last of a list of numbers', ->
    @numbers.exec(@ctx).should.equal 4

  it 'should get last of a list of letters', ->
    @letters.exec(@ctx).should.equal 'c'

  it 'should get last of a list of lists', ->
    @lists.exec(@ctx).should.eql [1,2,3]

  it 'should get last of a list of tuples', ->
    @tuples.exec(@ctx).should.eql { x: 24, y: 25, z: 26 }

  it 'should get last of a list of unordered numbers', ->
    @unordered.exec(@ctx).should.equal 2

  it 'should return null for an empty list', ->
    should(@empty.exec(@ctx)).be.null

  it 'should return null for an empty list', ->
    should(@nullValue.exec(@ctx)).be.null
