package org.opencds.cqf.cql.engine.elm.executing;

import org.hl7.elm.r1.ValueSetRef;
import org.opencds.cqf.cql.engine.execution.State;

public class AnyInValueSetEvaluator {

    public static Object internalEvaluate(Object codes, ValueSetRef valueSetRef, Object valueset, State state) {
        if (codes == null) {
            return false;
        }

        Object vs = null;
        if (valueSetRef != null) {
            vs = ValueSetRefEvaluator.toValueSet(state, valueSetRef);
        } else if (valueset != null) {
            vs = valueset;
        }

        if (vs == null) {
            return null;
        }

        if (codes instanceof Iterable) {
            Object result;
            for (Object code : (Iterable<?>) codes) {
                result = InValueSetEvaluator.inValueSet(code, vs, state);
                if (result instanceof Boolean && (Boolean) result) {
                    return true;
                }
            }
        }

        return false;
    }
}
