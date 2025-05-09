package org.opencds.cqf.cql.engine.elm.executing;

import java.util.ArrayList;
import java.util.List;
import org.hl7.elm.r1.Filter;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.execution.Variable;

public class FilterEvaluator {

    public static Object filter(Filter elm, Object source, Object condition, State state) {

        List<Object> ret = new ArrayList<>();

        if (source == null) {
            ret = null;
        }

        if (source instanceof Iterable) {

            for (Object obj : (List<?>) source) {
                try {
                    // Hmmm... This is hard without the alias.
                    // TODO: verify this works for all cases -> will scope always be present?
                    if (elm.getScope() != null) {
                        state.push(new Variable(elm.getScope()).withValue(obj));
                    }

                    if (condition instanceof Boolean && (Boolean) condition) {
                        ret.add(obj);
                    }
                } finally {
                    state.pop();
                }
            }
        }

        return ret;
    }
}
