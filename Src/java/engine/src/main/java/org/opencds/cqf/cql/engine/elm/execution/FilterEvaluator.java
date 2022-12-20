package org.opencds.cqf.cql.engine.elm.execution;

import java.util.ArrayList;
import java.util.List;

import org.cqframework.cql.elm.execution.Filter;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.execution.Variable;

public class FilterEvaluator extends Filter {

    @Override
    protected Object internalEvaluate(Context context) {
        Object source = this.getSource().evaluate(context);

        List<Object> ret = new ArrayList<>();

        if (source == null) {
            ret = null;
        }

        if (source instanceof Iterable) {

            for (Object obj : (List<?>) source) {
                try {
                    // Hmmm... This is hard without the alias.
                    // TODO: verify this works for all cases -> will scope always be present?
                    if (this.scope != null) {
                        context.push(new Variable().withName(this.getScope()).withValue(obj));
                    }

                    Object condition = this.getCondition().evaluate(context);
                    if (condition instanceof Boolean && (Boolean) condition) {
                        ret.add(obj);
                    }
                }
                finally {
                    context.pop();
                }
            }
        }

        return ret;
    }
}
