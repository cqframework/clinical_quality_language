library StringOperator version '1'

context Population

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

//Indexer
define IndexerNullNull : Indexer(null, null)
define IndexerANull : Indexer('a', null)
define IndexerNull1 : Indexer(null, 1)
define IndexerAB0 : Indexer('ab', 0)
define IndexerAB1 : Indexer('ab', 1)
define IndexerAB2 : Indexer('ab', 2)
define IndexerABNeg1 : Indexer('ab', -1)

//Length
define LengthNull : Length(null)
define LengthEmpty : Length('')
define LengthA : Length('a')
define LengthAB : Length('ab')

//Lower
define LowerNull : Lower(null)
define LowerEmpty : Lower('')
define LowerA : Lower('A')
define LowerB : Lower('b')
define LowerAB : Lower('Ab')

//PositionOf
define PositionOfNullNull : PositionOf(null, null)
define PositionOfANull : PositionOf('a', null)
define PositionOfNullA : PositionOf(null, 'a')
define PositionOfAInAB : PositionOf('a', 'ab')
define PositionOfBInAB : PositionOf('b', 'ab')
define PositionOfCInAB : PositionOf('c', 'ab')

//Split
define SplitNullNull : Split(null, null)
define SplitNullComma : Split(null, ',')
define SplitABNull : Split('a,b', null)
define SplitABDash : Split('a,b', '-')
define SplitABComma : Split('a,b', ',')

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
define UpperA : Upper('a')
define UpperB : Upper('B')
define UpperAB : Upper('aB')
