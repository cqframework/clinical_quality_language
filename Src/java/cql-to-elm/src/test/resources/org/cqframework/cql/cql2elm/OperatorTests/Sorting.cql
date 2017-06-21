define TestSimpleSort: ({ 1, 2, 3, 4, 5 }) X sort desc
define TestColumnSort: ({ { id: 1, name: 'Joe' } }) X sort by id desc
define TestExpressionSort: ({ 1, 2, 3, 4, 5 }) X sort by $this * $this
define TestAliasSort: ({ 1, 2, 3, 4, 5 }) X sort by X desc
