package org.opencds.cqf.cql.engine.elm.executing

/*

A standard case allows any number of conditions, each with a corresponding expression
 that will be the result of the case if the associated condition evaluates to true.
If none of the conditions evaluate to true, the else expression is the result:
case
 when X > Y then X
 when Y > X then Y
 else 0
end

A selected case specifies a comparand, and each case item specifies a possible value for the comparand.
If the comparand is equal to a case item, the corresponding expression is the result of the selected case.
If the comparand does not equal any of the case items, the else expression is the result:
case X
 when 1 then 12
 when 2 then 14
 else 15
end

*/

class CaseEvaluator
