package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;

/*
+(left String, right String) String

The concatenate (+) operator performs string concatenation of its arguments.
If either argument is null, the result is null.
*/

public class ConcatenateEvaluator {

    public static Object concatenate(Object left, Object right) {
        if (left == null || right == null) {
            return null;
        }

        if(left instanceof String && right instanceof String){
            return ((String)left).concat((String)right);
        }

        throw new InvalidOperatorArgument(
                "Concatenate(String, String)",
                String.format("Concatenate(%s, %s)", left.getClass().getName(), right.getClass().getName())
        );
    }
}
