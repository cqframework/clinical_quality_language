package org.opencds.cqf.cql.engine.elm.executing;

import org.hl7.elm.r1.As;
import org.opencds.cqf.cql.engine.execution.State;

/*
as<T>(argument Any) T
cast as<T>(argument Any) T

The as operator allows the result of an expression to be cast as a given target type.
  This allows expressions to be written that are statically typed against the expected run-time type of the argument.
If the argument is not of the specified type at run-time the result is null.
The cast prefix indicates that if the argument is not of the specified type at run-time then an exception is thrown.
Example:
The following examples illustrate the use of the as operator.
define AllProcedures: [Procedure]
define ImagingProcedures:
  AllProcedures P
    where P is ImagingProcedure
    return P as ImagingProcedure
define RuntimeError:
  ImagingProcedures P
    return cast P as Observation
*/

public class AsEvaluator {

    private static Class<?> resolveType(As as, State state) {
        if (as.getAsTypeSpecifier() != null) {
            return state.getEnvironment().resolveType(as.getAsTypeSpecifier());
        }

        return state.getEnvironment().resolveType(as.getAsType());
    }

    public static Object internalEvaluate(Object operand, As as, boolean isStrict, State state) {
        Class<?> clazz = resolveType(as, state);
        return state.getEnvironment().as(operand, clazz, isStrict);
    }
}
