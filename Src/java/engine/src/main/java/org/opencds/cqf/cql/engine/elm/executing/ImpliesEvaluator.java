package org.opencds.cqf.cql.engine.elm.executing;

/*

implies (left Boolean, right Boolean) Boolean

The implies operator returns the logical implication of its arguments.
    This means that if the left operand evaluates to true, this operator returns
    the boolean evaluation of the right operand. If the left operand evaluates to false,
    this operator returns true. Otherwise, this operator returns true if the right operand
    evaluates to true, and null otherwise.
The following table defines the truth table for this operator:
        | TRUE	FALSE	NULL
-----------------------------
TRUE    | TRUE	FALSE	NULL
FALSE	| TRUE	TRUE	TRUE
NULL	| TRUE	NULL	NULL

*/

public class ImpliesEvaluator {

    public static Object implies(Boolean left, Boolean right) {
        if (left == null) {
            return right == null || !right ? null : true;
        }

        if (left) {
            return right;
        }

        return true;
    }
}
