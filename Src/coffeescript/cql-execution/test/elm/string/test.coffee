should = require 'should'
setup = require '../../setup'
data = require './data'
str = require '../../../lib/elm/string'

describe 'Concat', ->
  @beforeEach ->
    setup @, data

  it.skip 'should be a Concat', ->
    @helloWorld.should.be.an.instanceOf(str.Concat)
    @helloWorldVariables.should.be.an.instanceOf(str.Concat)

  it 'should concat two strings', ->
    @helloWorld.exec(@ctx).should.equal 'HelloWorld'

  it 'should concat multiple strings', ->
    @sentence.exec(@ctx).should.equal 'The quick brown fox jumps over the lazy dog.'

  it 'should return null when an arg is null', ->
    should(@concatNull.exec(@ctx)).be.null

  it 'should concat variables', ->
    @helloWorldVariables.exec(@ctx).should.equal 'HelloWorld'

describe 'Combine', ->
  @beforeEach ->
    setup @, data

  it.skip 'should be a Combine', ->
    @separator.should.be.an.instanceOf(str.Combine)

  it 'should combine strings with no separator', ->
    @noSeparator.exec(@ctx).should.equal 'abcdefghijkl'

  it 'should combine strings with a separator', ->
    @separator.exec(@ctx).should.equal 'abc;def;ghi;jkl'

  it 'should return null when the list is null', ->
    should(@combineNull.exec(@ctx)).be.null

  it 'should return null when an item in the list is null', ->
    should(@combineNullItem.exec(@ctx)).be.null

describe 'Split', ->
  @beforeEach ->
    setup @, data

  it.skip 'should be a Split', ->
    @commaSeparated.should.be.an.instanceOf(str.Split)

  it 'should split strings on comma', ->
    @commaSeparated.exec(@ctx).should.eql ['a','b','c','','1','2','3']

  it 'should return single-item array when separator is not used', ->
    @separatorNotUsed.exec(@ctx).should.eql ['a,b,c,,1,2,3']

  it 'should return null when separating null', ->
    should(@separateNull.exec(@ctx)).be.null

  # TODO: Verify this assumption
  it 'should return null when the separator is null', ->
    should(@separateUsingNull.exec(@ctx)).be.null

describe 'Length', ->
  @beforeEach ->
    setup @, data

  it.skip 'should be a Length', ->
    @elevenLetters.should.be.an.instanceOf(str.Length)

  it 'should count letters in string', ->
    @elevenLetters.exec(@ctx).should.equal 11

  it 'should return null when string is null', ->
    should(@nullString.exec(@ctx)).be.null

describe 'Upper', ->
  @beforeEach ->
    setup @, data

  it.skip 'should be an Upper', ->
    @upperC.should.be.an.instanceOf(str.Upper)

  it 'should convert lower to upper', ->
    @lowerC.exec(@ctx).should.equal 'ABCDEFG123'

  it 'should leave upper as upper', ->
    @upperC.exec(@ctx).should.equal 'ABCDEFG123'

  it 'should convert camel to upper', ->
    @camelC.exec(@ctx).should.equal 'ABCDEFG123'

  it 'should return null when uppering null', ->
    should(@nullString.exec(@ctx)).be.null

describe 'Lower', ->
  @beforeEach ->
    setup @, data

  it.skip 'should be a Lower', ->
    @lowerC.should.be.an.instanceOf(str.Lower)

  it 'should leave lower as lower', ->
    @lowerC.exec(@ctx).should.equal 'abcdefg123'

  it 'should convert upper to lower', ->
    @upperC.exec(@ctx).should.equal 'abcdefg123'

  it 'should convert camel to lower', ->
    @camelC.exec(@ctx).should.equal 'abcdefg123'

  it 'should return null when lowering null', ->
    should(@nullString.exec(@ctx)).be.null

# TODO: Verify behavior since its different than JS
describe 'Indexer', ->
  @beforeEach ->
    setup @, data

  it 'should be an Indexer', ->
    @helloWorldSix.should.be.an.instanceOf(str.Indexer)

  it 'should get letter at index', ->
    @helloWorldSix.exec(@ctx).should.equal 'W'

  it 'should error on index 0 (out of bounds)', ->
    try
      @helloWorldZero.exec(@ctx)
      should.fail()
    catch e
      # Good!

  it 'should error on index 20 (out of bounds)', ->
    try
      @helloWorldTwenty.exec(@ctx)
      should.fail()
    catch e
      # Good!

  it 'should return null when string is null', ->
    should(@nullString.exec(@ctx)).be.null

  it 'should return null when index is null', ->
    should(@nullIndex.exec(@ctx)).be.null

describe 'Pos', ->
  @beforeEach ->
    setup @, data

  it.skip 'should be a Pos', ->
    @found.should.be.an.instanceOf(str.Pos)

  it 'should return 1-based position', ->
    @found.exec(@ctx).should.equal 3

  it 'should return 0 when not found', ->
    @notFound.exec(@ctx).should.equal 0

  it 'should return null when pattern is null', ->
    should(@nullPattern.exec(@ctx)).be.null

  it 'should return null when string is null', ->
    should(@nullString.exec(@ctx)).be.null

describe 'Substring', ->
  @beforeEach ->
    setup @, data

  it.skip 'should be a Substring', ->
    @world.should.be.an.instanceOf(str.Substring)

  it 'should get substring to end', ->
    @world.exec(@ctx).should.equal 'World'

  it 'should get substring with length', ->
    @or.exec(@ctx).should.equal 'or'

  it 'should get substring with zero length', ->
    @zeroLength.exec(@ctx).should.equal ''

  it 'should error on index 0 (out of bounds)', ->
    try
      @startTooLow.exec(@ctx)
      should.fail()
    catch e
      # Good!

  it 'should error on too much length (out of bounds)', ->
    try
      @tooMuchLength.exec(@ctx)
      should.fail()
    catch e
      # Good!

  it 'should error on negative length', ->
    try
      @negativeLength.exec(@ctx)
      should.fail()
    catch e
      # Good!

  it 'should return null when string is null', ->
    should(@nullString.exec(@ctx)).be.null

  it 'should return null when start is null', ->
    should(@nullStart.exec(@ctx)).be.null
