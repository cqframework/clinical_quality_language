should = require 'should'
{equals, equivalent} = require '../../lib/util/comparison'
{Code, Concept} = require '../../lib/datatypes/clinical'

describe 'equals', ->
  it 'should detect equality/inequality for numbers', ->
    equals(1, 1).should.be.true()
    equals(1, -1).should.be.false()
    equals(5, 2+3).should.be.true()
    equals(1.2345, 1.2345).should.be.true()
    equals(1.2345, 1.23456).should.be.false()

  it 'should detect equality/inequality for strings', ->
    equals('', '').should.be.true()
    equals('a', 'a').should.be.true()
    equals('a', 'A').should.be.false()
    equals('abc', 'ab' + 'c').should.be.true()
    equals('abc', 'abcd').should.be.false()

  it 'should detect equality/inequality for dates', ->
    equals(new Date(2012, 2, 5, 10, 55, 34, 235), new Date(2012, 2, 5, 10, 55, 34, 235)).should.be.true()
    equals(new Date(2012, 2, 5, 10, 55, 34, 235), new Date(2012, 2, 5, 10, 55, 34, 236)).should.be.false()
    equals(new Date('2012-03-05T10:55:34.235'), new Date('2012-03-05T10:55:34.235')).should.be.true()
    equals(new Date('2012-03-05T10:55:34.235-04:00'), new Date('2012-03-05T10:55:34.235-05:00')).should.be.false()
    equals(new Date('2012-03-05T10:55:34.235-04:00'), new Date('2012-03-05T09:55:34.235-05:00')).should.be.true()

  it 'should detect equality/inequality for regular expressions', ->
    equals(/\w+\s/g, /\w+\s/g).should.be.true()
    equals(/\w+\s/g, /\w+\s/gi).should.be.false()
    equals(/\w+\s*/g, /\w+\s/g).should.be.false()

  it 'should detect equality/inequality for objects', ->
    equals({}, {}).should.be.true()
    equals({a: 1, b:2, c:3}, {a: 1, b: 2, c: 3}).should.be.true()
    equals({a: 1, b:2, c:3}, {c: 3, b: 2, a: 1}).should.be.true()
    equals({a: 1, b:2, c:3}, {a: 1, b: 2}).should.be.false()
    equals({a: 1, b:2}, {a: 1, b: 2, c: 3}).should.be.false()
    equals({a: {b: {c: 'd'}, e: 'f'}, g: 'h'}, {a: {b: {c: 'd'}, e: 'f'}, g: 'h'}).should.be.true()
    equals({a: {b: {c: 'd'}, e: 'f'}, g: 'h'}, {a: {b: {c: 'dee'}, e: 'f'}, g: 'h'}).should.be.false()
    equals({a: new Date('2012-03-05T10:55:34.235')}, {a: new Date('2012-03-05T10:55:34.235')}).should.be.true()
    equals({a: [1, 2, 3], b: [4, 5, 6]}, {a: [1, 2, 3], b: [4, 5, 6]}).should.be.true()
    equals({a: [1, 2, 3], b: [4, 5, 6]}, {a: [3, 2, 1], b: [6, 5, 4]}).should.be.false()

  it 'should detect equality/inequality for classes', ->
    class Foo
      constructor: (@prop1, @prop2) ->

    class Bar extends Foo
      constructor: (@prop1, @prop2) ->
        super

    equals(new Foo('abc', [1,2,3]), new Foo('abc', [1,2,3])).should.be.true()
    equals(new Foo('abc', [1,2,3]), new Foo('abcd', [1,2,3])).should.be.false()
    equals(new Foo('abc', new Bar('xyz', [1,2,3])), new Foo('abc', new Bar('xyz', [1,2,3]))).should.be.true()
    equals(new Foo('abc', new Bar('xyz')), new Foo('abc', new Bar('xyz'))).should.be.false() # because Bar is missing components
    equals(new Foo('abc', new Bar('xyz')), new Foo('abc', new Bar('xyz',999))).should.be.false()
    equals(new Foo('abc', [1,2,3]), new Bar('abc', [1,2,3])).should.be.false()
    equals(new Bar('abc', [1,2,3]), new Foo('abc', [1,2,3])).should.be.false()

  it 'should delegate to equals method when available', ->
    class Int
      constructor: (@num) ->

    class StringFriendlyInt extends Int
      constructor: (@num) ->

      asInt: () ->
        switch typeof(@num)
          when 'number' then Math.floor(@num)
          when 'string' then parseInt(@num)
          else Number.NaN

      equals: (other) ->
        other instanceof StringFriendlyInt and @asInt() is other.asInt()

    equals(new Int(1), new Int('1')).should.be.false()
    equals(new StringFriendlyInt(1), new StringFriendlyInt('1')).should.be.true()

  it 'should detect equality/inequality for arrays', ->
    equals([], []).should.be.true()
    equals([1], [1]).should.be.true()
    equals([1, 2, 3], [1, 2, 3]).should.be.true()
    equals([1, 2, 3], [3, 2, 1]).should.be.false()
    equals([1, 2, 3], [1, 2]).should.be.false()
    equals([1, 2], [1, 2, 3]).should.be.false()
    equals([{a: 1}, {b: 2}], [{a: 1}, {b: 2}]).should.be.true()
    equals([['a','b','c'],[1,2,3]], [['a','b','c'],[1,2,3]]).should.be.true()
    equals([['a','b','c'],[1,2,3]], [['a','b','c'],[1,2,3,4]]).should.be.false()

  it 'should handle null values', ->
    should.not.exist(equals(null, null))
    should.not.exist(equals(null, 0))
    should.not.exist(equals(0, null))
    should.not.exist(equals(null, 'null'))
    should.not.exist(equals('null', null))
    should.not.exist(equals(null, {}))
    should.not.exist(equals({}, null))
    should.not.exist(equals(null, [null]))
    should.not.exist(equals([null], null))
    should.not.exist(equals(null, {}.undef))
    should.not.exist(equals({}.undef, null))

describe 'equivalent', ->
  it 'should detect equivalent and non-equivalent codes', ->
    equivalent(new Code('12345', 'Code System', '2016', 'Display Name'), new Code('12345', 'Code System', undefined, undefined)).should.be.true()
    equivalent(new Code('1234', 'First Code System', '2016', 'Display Name'), new Code('1234', 'Different Code System', undefined, undefined)).should.be.false()
    equivalent(new Code('123', 'test', '2016'), new Code('12', 'test', '2016')).should.be.false()
    equivalent(new Code('123', undefined, undefined, undefined), new Code('123', undefined, undefined, undefined)).should.be.true()

  it 'should detect if second parameter is not a code and still result to true', ->
    equivalent(new Code('123', 'test', '2016'), '123').should.be.true()

  it 'should detect if parameters are not codes and return using equals', ->
    equivalent('123', '123').should.be.true()
    equivalent(123, 123).should.be.true()
    equivalent('123', new Code('123', 'test', '2016')).should.be.false()

  it 'should consider codes with different versions to be equivalent', ->
    equivalent(new Code('1234', 'System', '2016', 'Display Name'), new Code('1234', 'System', '2017', undefined)).should.be.true()

  it 'should detect equivalency between code and list of codes', ->
    equivalent(new Code('1234', 'System', 'version2017', 'Display Name'), [new Code('1234', 'System', 'version2017', undefined), new Code('1', '2', '3', undefined)]).should.be.true()
    equivalent(new Code('1234', 'System', 'version2017', 'Display Name'), [new Code('1111', 'System', 'version2017', undefined), new Code('1', '2', '3', undefined)]).should.be.false()
    equivalent(new Code('1234', 'System', 'version2016', 'Display Name'), [new Code('1234', 'System', 'version2017', undefined), new Code('1', '2', '3', undefined)]).should.be.true()
    equivalent(new Code('1234', 'System', 'version2016', 'Display Name'), [new Code('1111', 'System', 'version2017', undefined), new Code('1', '2', '3', undefined)]).should.be.false()

  it 'should detect equivalency between code and concept of codes', ->
    equivalent(new Code('1234', 'System', 'version2016', 'Display Name'), new Concept([new Code('1234', 'System', 'version2016', undefined), new Code('1','2','3', undefined)])).should.be.true()
    equivalent(new Code('1234', 'System', 'version2016', 'Display Name'), new Concept([new Code('1111', 'System', 'version2016', undefined), new Code('1','2','3', undefined)])).should.be.false()
    equivalent(new Code('1234', 'System', 'version2016', 'Display Name'), new Concept([new Code('1234', 'System', 'version2017', undefined), new Code('1','2','3', undefined)])).should.be.true()
    equivalent(new Code('1234', 'System', 'version2016', 'Display Name'), new Concept([new Code('1111', 'System', 'version2017', undefined), new Code('1','2','3', undefined)])).should.be.false()
