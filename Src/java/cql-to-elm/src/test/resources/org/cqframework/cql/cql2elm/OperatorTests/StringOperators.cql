define StringAdd: 'John' + ' ' + 'Doe'
define StringCombine: Combine({ 'First', 'Second', 'Third', 'Fourth' }, ',')
define StringCombineNoSeparator: Combine({ 'abc', 'def', 'ghi', 'jkl' })
define StringConcatenate: Concatenate('John', 'Doe')
define StringIndexer: 'John'[1]
define StringLength: Length('John')
define StringLower: Lower('John')
define StringPositionOf: PositionOf('J', 'John')
define StringSplit: Split('First,Second,Third,Fourth', ',')
define StringSubstring: Substring('JohnDoe', 5)
define StringSubstringWithLength: Substring('JohnDoe', 1, 4)
define StringUpper: Upper('John')
