// @Test: Concat
define HelloWorld: 'Hello' + 'World'
define Sentence: 'The' + ' ' + 'quick' + ' ' + 'brown' + ' ' + 'fox' + ' ' + 'jumps' + ' '
                  + 'over' + ' ' + 'the' + ' ' + 'lazy' + ' ' + 'dog.'
define ConcatNull: 'Hello' + null
define Hello: 'Hello'
define World: 'World'
define HelloWorldVariables: Hello + World

// @Test: Combine
define NoSeparator: Combine({ 'abc', 'def', 'ghi', 'jkl' })
define Separator: Combine({ 'abc', 'def', 'ghi', 'jkl' }, ';')
define CombineNull: Combine(null, ';')
define CombineNullItem: Combine({ 'abc', 'def', null, 'jkl' }, ';')

// @Test: Split
define CommaSeparated: Split('a,b,c,,1,2,3', ',')
define SeparatorNotUsed: Split('a,b,c,,1,2,3', ';')
define SeparateNull: Split(null, ',')
define SeparateUsingNull: Split('a,b,c', null)

// @Test: Length
define ElevenLetters: Length('Hello World')
define NullString: Length(null)

// @Test: Upper
define LowerC: Upper('abcdefg123')
define UpperC: Upper('ABCDEFG123')
define CamelC: Upper('aBcDeFg123')
define NullString: Upper(null)

// @Test: Lower
define LowerC: Lower('abcdefg123')
define UpperC: Lower('ABCDEFG123')
define CamelC: Lower('aBcDeFg123')
define NullString: Lower(null)

// @Test: Indexer
define HelloWorldSix: 'HelloWorld'[6]
define HelloWorldZero: 'HelloWorld'[0]
define HelloWorldTwenty: 'HelloWorld'[20]
define NullString: null[4]
define NullIndex: 'HelloWorld'[null]

// @Test: PositionOf
define found: PositionOf('cde', 'abcdefg')
define notFound: PositionOf('fgh', 'abcdefg')
define nullPattern: PositionOf(null, 'abcdefg')
define nullString: PositionOf('cde', null)

// @Test: Substring
define World: Substring('HelloWorld', 6)
define Or: Substring('HelloWorld', 7, 2)
define ZeroLength: Substring('HelloWorld', 7, 0)
define StartTooLow: Substring('HelloWorld', 0)
define TooMuchLength: Substring('HelloWorld', 7, 25)
define NegativeLength: Substring('HelloWorld', 7, -1)
define NullString: Substring(null, 5)
define NullStart: Substring('HelloWorld', null)
