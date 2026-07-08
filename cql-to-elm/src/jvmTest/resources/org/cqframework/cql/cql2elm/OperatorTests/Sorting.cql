define TestSimpleSort: ({ 1, 2, 3, 4, 5 }) X sort desc
define TestDescendingSort: ({ 1, 2, 3, 4, 5 }) X sort descending
define TestAscSort: ({ 5, 4, 3, 2, 1}) X sort asc
define TestAscendingSort: ({ 5, 4, 3, 2, 1 }) X sort ascending
define TestColumnSort: ({ { id: 1, name: 'Joe' } }) X sort by id desc
define TestExpressionSort: ({ 1, 2, 3, 4, 5 }) X sort by $this * $this
define TestAliasSort: ({ 1, 2, 3, 4, 5 }) X sort by X desc
