package org.opencds.cqf.cql.engine.elm.executing;

import org.hl7.elm.r1.CodeSystemRef;
import org.opencds.cqf.cql.engine.execution.State;

public class AnyInCodeSystemEvaluator {
    public static Object internalEvaluate(Object codes, CodeSystemRef codeSystemRef, Object codeSystem, State state) {
        Object cs = null;
        if (codeSystemRef != null) {
            cs = CodeSystemRefEvaluator.toCodeSystem(codeSystemRef, state);
        } else if (codeSystem != null) {
            cs = codeSystem;
        }

        if (codes == null || cs == null) return null;

        if (codes instanceof Iterable) {
            Object result;
            for (Object code : (Iterable<?>) codes) {
                result = InCodeSystemEvaluator.inCodeSystem(code, cs, state);
                if (result instanceof Boolean && (Boolean) result) {
                    return true;
                }
            }
        }

        return false;
    }
}
