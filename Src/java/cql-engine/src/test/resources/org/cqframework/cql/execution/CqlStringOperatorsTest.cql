library StringOperator version '1'

context Population

//Combine
define CombineEmpty : Combine()
define CombineNull : Combine(null)
define CombineEmptyList : Combine({})
define CombineABC : Combine({'a,'b','c'})
define CombineABCSepDash : Combine({'a,'b','c'}, '-')

//Concatenate
define ConcatenateEmpty : Concatenate()
define ConcatenateNull : Concatenate(null)
define ConcatenateANull : Concatenate('a', null)
define ConcatenateNullB : Concatenate(null, 'b')
define ConcatenateAB : Concatenate('a,'b')

//Indexer

//Length

//Lower

//PositionOf

//Split

//Substring

//Upper

