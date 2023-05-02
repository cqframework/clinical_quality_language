package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.Is;
import org.opencds.cqf.cql.engine.execution.State;

/*
is<T>(argument Any) Boolean

The is operator allows the type of a result to be tested.
If the run-time type of the argument is of the type being tested, the result of the operator is true;
  otherwise, the result is false.
*/

public class IsEvaluator {
  private static Class<?> resolveType(Is is, State state) {
      if (is.getIsTypeSpecifier() != null) {
          return state.resolveType(is.getIsTypeSpecifier());
      }

      return state.resolveType(is.getIsType());
  }

  public static Object internalEvaluate(Is is, Object operand, State state) {
    Class<?> type = resolveType(is, state);

    return state.is(operand, type);
  }
}
