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

//Split

//Substring

//Upper
define UpperNull : Upper(null)
define UpperEmpty : Upper('')
define UpperA : Upper('a')
define UpperB : Upper('B')
define UpperAB : Upper('aB')
