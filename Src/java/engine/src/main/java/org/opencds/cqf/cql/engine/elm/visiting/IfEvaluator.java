package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.If;
import org.opencds.cqf.cql.engine.execution.CqlEngineVisitor;
import org.opencds.cqf.cql.engine.execution.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IfEvaluator {
    private static Logger logger = LoggerFactory.getLogger(IfEvaluator.class);

    public static Object internalEvaluate(If elm, State state, CqlEngineVisitor visitor) {
        logger.info("Evaluate IfEvaluator");

        Object condition = visitor.visitExpression(elm.getCondition(), state);

        if (condition == null) {
            condition = false;
        }

        return (Boolean) condition ? visitor.visitExpression(elm.getThen(), state) :
                visitor.visitExpression(elm.getElse(), state);
    }
}
