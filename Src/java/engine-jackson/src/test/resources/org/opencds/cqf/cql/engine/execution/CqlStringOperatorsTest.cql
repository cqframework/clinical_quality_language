library StringOperator version '1'

codesystem "LOINC": 'http://loinc.org'

//Combine
define CombineNull : Combine(null)
define CombineEmptyList : Combine({})
define CombineABC : Combine({'a', 'b', 'c'})
define CombineABCSepDash : Combine({'a', 'b', 'c'}, '-')

//Concatenate
define ConcatenateNullNull : Concatenate(null, null)
define ConcatenateANull : Concatenate('a', null)
define ConcatenateNullB : Concatenate(null, 'b')
define ConcatenateAB : Concatenate('a', 'b')
define ConcatenateABWithAdd: 'a' + 'b'

//EndsWith
define EndsWithNull: EndsWith(null, null)
define EndsWithTrue: EndsWith('Chris Schuler is the man!!', 'n!!')
define EndsWithFalse: EndsWith('Chris Schuler is the man!!', 'n!')

//Indexer
define IndexerNullNull : Indexer(null as String, null) // cast is required due to ambiguity with Indexer(String) and Indexer(List<Any>)
define IndexerANull : Indexer('a', null)
define IndexerNull1String : Indexer(null as String, 1) // cast is required due to ambiguity with Indexer(String) and Indexer(List<Any>)
define IndexerAB0 : Indexer('ab', 0)
define IndexerAB1 : Indexer('ab', 1)
define IndexerAB2 : Indexer('ab', 2)
define IndexerABNeg1 : Indexer('ab', -1)

//LastPositionOf
define LastPositionOfNull: LastPositionOf(null, null)
define LastPositionOfNull1: LastPositionOf(null, 'hi')
define LastPositionOfNull2: LastPositionOf('hi', null)
define LastPositionOf1: LastPositionOf('hi', 'Ohio is the place to be!')
define LastPositionOf2: LastPositionOf('hi', 'Say hi to Ohio!')

//Length
define LengthNullString : Length(null as String) // cast is required due to ambiguity with Length(String) and Length(List<Any>)
define LengthEmptyString : Length('')
define LengthA : Length('a')
define LengthAB : Length('ab')

//Lower
define LowerNull : Lower(null)
define LowerEmpty : Lower('')
define LowerA : Lower('A')
define LowerB : Lower('b')
define LowerAB : Lower('Ab')

//Matches
define MatchesNull: Matches('Not all who wander are lost', null)
define MatchesNumberFalse: Matches('Not all who wander are lost', '.*\\d+')
define MatchesNumberTrue: Matches('Not all who wander are lost - circa 2017', '.*\\d+')
define MatchesAllTrue: Matches('Not all who wander are lost', '.*')
define MatchesWordsAndSpacesTrue: Matches('Not all who wander are lost', '[\\w|\\s]+')
define MatchesWordsAndSpacesFalse: Matches('Not all who wander are lost - circa 2017', '[\\w]+')
define MatchesNotWords: Matches('   ', '\\W+')
define MatchesWhiteSpace: Matches(' \n\t', '\\s+')

//PositionOf
define PositionOfNullNull : PositionOf(null, null)
define PositionOfANull : PositionOf('a', null)
define PositionOfNullA : PositionOf(null, 'a')
define PositionOfAInAB : PositionOf('a', 'ab')
define PositionOfBInAB : PositionOf('b', 'ab')
define PositionOfCInAB : PositionOf('c', 'ab')

//ReplaceMatches
define ReplaceMatchesNull: ReplaceMatches('Not all who wander are lost', null, 'But I am...')
define ReplaceMatchesAll: ReplaceMatches('Not all who wander are lost', 'Not all who wander are lost', 'But still waters run deep')
define ReplaceMatchesMany: ReplaceMatches('Who put the bop in the bop she bop she bop?', 'bop', 'bang')
define ReplaceMatchesSpaces: ReplaceMatches('All that glitters is not gold', '\\s', '\\$')

//Split
define SplitNullNull : Split(null, null)
define SplitNullComma : Split(null, ',')
define SplitABNull : Split('a,b', null)
define SplitABDash : Split('a,b', '-')
define SplitABSpace : Split('a b', ' ')
define SplitABComma : Split('a,b', ',')

//SplitOnMatches
define SplitMatchesNullNull : SplitOnMatches(null, null)
define SplitMatchesNullComma : SplitOnMatches(null, ',')
define SplitMatchesABNull : SplitOnMatches('a,b', null)
define SplitMatchesABSpaceRegex : SplitOnMatches('a b', '\\s')
define SplitMatchesABComma : SplitOnMatches('a,b', ',')

//StartsWith
define StartsWithNull: StartsWith(null, null)
define StartsWithNull1: StartsWith('hi', null)
define StartsWithNull2: StartsWith(null, 'hi')
define StartsWithTrue1: StartsWith('Breathe deep the gathering gloom', 'Bre')
define StartsWithFalse1: StartsWith('Breathe deep the gathering gloom', 'bre')

//Substring
define SubstringNullNull : Substring(null, null)
define SubstringANull : Substring('a', null)
define SubstringNull1 : Substring(null, 1)
define SubstringAB0 : Substring('ab', 0)
define SubstringAB1 : Substring('ab', 1)
define SubstringAB2 : Substring('ab', 2)
define SubstringABNeg1 : Substring('ab', -1)
define SubstringAB0To1 : Substring('ab', 0, 1)
define SubstringABC1To1 : Substring('abc', 1, 1)
define SubstringAB0To3 : Substring('ab', 0, 3)

//Upper
define UpperNull : Upper(null)
define UpperEmpty : Upper('')
define UpperSpace : Upper(' ')
define UpperA : Upper('a')
define UpperB : Upper('B')
define UpperAB : Upper('aB')

// toString tests
define QuantityToString: ToString(125 'cm')
define DateTimeToString1: ToString(DateTime(2000, 1, 1))
define DateTimeToString2: ToString(DateTime(2000, 1, 1, 15, 25, 25, 300))
define DateTimeToString3: ToString(DateTime(2000, 1, 1, 8, 25, 25, 300, -7))
define TimeToString1: ToString(@T09:30:01.003)
define TupleToString: Tuple { this: 'is', a: 123, test: 25 'g' }
define IntervalToString: Interval [ DateTime(1999, 12, 31), DateTime(2000, 1, 1) ]
define UncertaintyToString: difference in months between DateTime(2005) and DateTime(2006, 7)
define CodeToString: Code { code: '4225-6', system: 'http://loinc.org', version: '2.1', display: 'loinc' }
define ConceptToString: Concept { Code '66071002' from "LOINC", Code 'B18.1' from "LOINC"} display 'Type B viral hepatitis'