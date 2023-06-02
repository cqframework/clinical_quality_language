package org.opencds.cqf.cql.engine.elm.execution;

import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Tuple;

public class PropertyEvaluator extends org.cqframework.cql.elm.execution.Property {

    @Override
    protected Object internalEvaluate(Context context) {
        Object target = null;

        if (this.getSource() != null) {
            target = getSource().evaluate(context);
            // Tuple element access
            if (target instanceof Tuple) {
              // NOTE: translator will throw error if Tuple does not contain the specified element -- no need for x.containsKey() check
              return ((Tuple)target).getElements().get(this.getPath());
            }
        }
        else if (this.getScope() != null) {
            target = context.resolveVariable(this.getScope(), true).getValue();
        }

        if (target == null) {
            return null;
        }

        if (target instanceof Iterable) {

        }

        return context.resolvePath(target, this.getPath());
    }
}
