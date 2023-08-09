package org.opencds.cqf.cql.engine.elm.executing;

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.List;
import org.opencds.cqf.cql.engine.execution.State;

import java.util.ArrayList;

public class ListEvaluator {

    public static Object internalEvaluate(List list, State state, ElmLibraryVisitor<Object, State> visitor) {
        ArrayList<Object> result = new ArrayList<>();
        for (Expression element : list.getElement()) {
            Object obj = visitor.visitExpression(element, state);
            result.add(obj);
        }
        return result;
    }
}
