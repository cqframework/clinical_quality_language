package org.opencds.cqf.cql.engine.elm.executing;

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.If;
import org.opencds.cqf.cql.engine.execution.State;

public class IfEvaluator {
    public static Object internalEvaluate(If elm, State state, ElmLibraryVisitor<Object,State> visitor) {

        Object condition = visitor.visitExpression(elm.getCondition(), state);

        if (condition == null) {
            condition = false;
        }

        return (Boolean) condition ? visitor.visitExpression(elm.getThen(), state) :
                visitor.visitExpression(elm.getElse(), state);
    }
}
