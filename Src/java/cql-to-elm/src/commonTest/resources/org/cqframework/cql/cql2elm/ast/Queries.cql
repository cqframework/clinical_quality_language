library Queries version '1.0.0'
using System

context Patient

define IntList: { 1, 2, 3, 4, 5 }
define ShortList: { 1, 2, 3 }

// Simple list query with where
define SimpleQuery: IntList X where X > 3

// Query with return
define QueryWithReturn: ShortList X return X + 1

// Query with let
define QueryWithLet: ShortList X let Y: X + 1 return Y

// Query with sort ascending
define QueryWithSort: ShortList X sort asc

// Query with distinct return
define QueryDistinct: IntList X return distinct X
