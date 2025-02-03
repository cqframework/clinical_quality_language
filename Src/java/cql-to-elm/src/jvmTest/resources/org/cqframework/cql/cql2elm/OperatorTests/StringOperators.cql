define StringAdd: 'John' + ' ' + 'Doe'
define StringCombine: Combine({ 'First', 'Second', 'Third', 'Fourth' }, ',')
define StringCombineNoSeparator: Combine({ 'abc', 'def', 'ghi', 'jkl' })
define StringConcatenate: Concatenate('John', 'Doe')
define StringConcatenateWithAmpersand: 'John' & ' ' & 'Doe'
define StringEndsWith: EndsWith('John Doe', 'Doe')
define StringIndexer: 'John'[1]
define StringLength: Length('John')
define StringLower: Lower('John')
define StringMatches: Matches('Super', 'Sup')
define StringPositionOf: PositionOf('J', 'John')
define StringLastPositionOf: LastPositionOf('J', 'John')
define StringReplaceMatches: ReplaceMatches('Super', 'Sup', 'Supp')
define StringSplit: Split('First,Second,Third,Fourth', ',')
define StringSplitOnMatches: SplitOnMatches('First,Second,Third,Fourth', ',')
define StringStartsWith: StartsWith('John Doe', 'John')
define StringSubstring: Substring('JohnDoe', 5)
define StringSubstringWithLength: Substring('JohnDoe', 1, 4)
define StringUpper: Upper('John')
