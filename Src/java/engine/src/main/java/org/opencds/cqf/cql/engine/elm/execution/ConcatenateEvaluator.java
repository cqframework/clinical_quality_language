package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.execution.Context;

/*
+(left String, right String) String

The concatenate (+) operator performs string concatenation of its arguments.
If either argument is null, the result is null.
*/

public class ConcatenateEvaluator extends org.cqframework.cql.elm.execution.Concatenate {

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

    @Override
    protected Object internalEvaluate(Context context) {
        Object left = getOperand().get(0).evaluate(context);
        Object right = getOperand().get(1).evaluate(context);

        return concatenate(left, right);
    }
}
