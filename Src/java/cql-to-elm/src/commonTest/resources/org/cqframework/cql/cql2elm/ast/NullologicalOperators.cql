library NullologicalOperators version '1.0.0'
using System
context Patient

define IsNullExpression: 1 is null
define IsTrueExpression: true is true
define IsFalseExpression: false is false
define IsNotNullExpression: 1 is not null
define IsNotTrueExpression: true is not true
define IsNotFalseExpression: false is not false
define CoalesceTwoArgument: Coalesce(1, 2)
define CoalesceThreeArgument: Coalesce(1, 2, 3)
define CoalesceFourArgument: Coalesce(1, 2, 3, 4)
define CoalesceFiveArgument: Coalesce(1, 2, 3, 4, 5)
define CoalesceList: Coalesce({ 1, 2, 3, 4, 5 })
define IfExpression: if true then 1 else 2
