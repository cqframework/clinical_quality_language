library StringOperators version '1.0.0'
using System
context Patient

define StringAdd: 'John' + ' ' + 'Doe'
define StringConcatenateWithAmpersand: 'John' & ' ' & 'Doe'
define StringConcatenate: Concatenate('John', 'Doe')
define StringLength: Length('John')
define StringUpper: Upper('John')
define StringLower: Lower('John')
define StringStartsWith: StartsWith('John Doe', 'John')
define StringEndsWith: EndsWith('John Doe', 'Doe')
define StringMatches: Matches('Super', 'Sup')
define StringPositionOf: PositionOf('J', 'John')
define StringLastPositionOf: LastPositionOf('J', 'John')
define StringSubstring: Substring('JohnDoe', 5)
define StringSubstringWithLength: Substring('JohnDoe', 1, 4)
define StringReplaceMatches: ReplaceMatches('Super', 'Sup', 'Supp')
define StringCombine: Combine({ 'First', 'Second', 'Third', 'Fourth' }, ',')
define StringCombineNoSeparator: Combine({ 'abc', 'def', 'ghi', 'jkl' })
define StringSplit: Split('First,Second,Third,Fourth', ',')
define StringSplitOnMatches: SplitOnMatches('First,Second,Third,Fourth', ',')
define StringIndexer: 'John'[1]
