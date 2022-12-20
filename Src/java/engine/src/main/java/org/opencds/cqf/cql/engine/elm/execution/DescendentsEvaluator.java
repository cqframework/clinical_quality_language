package org.opencds.cqf.cql.engine.elm.execution;

import java.util.ArrayList;
import java.util.List;

import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Tuple;

public class DescendentsEvaluator extends org.cqframework.cql.elm.execution.Descendents {

    private static List<Object> descendents = new ArrayList<>();

    public static Object descendents(Object source) {
        if (source == null) {
            return null;
        }

        return getDescendents(source);
    }

    public static Object getDescendents(Object source) {
        if (source instanceof Iterable) {
            for (Object element : (Iterable<?>) source) {
                descendents.add(getDescendents(element));
            }
        }

        else if (source instanceof Tuple) {
            for (Object element : ((Tuple) source).getElements().values()) {
                descendents.add(getDescendents(element));
            }
        }

        else if (source instanceof Interval) {
            descendents.add(getDescendents(((Interval) source).getStart()));
            descendents.add(getDescendents(((Interval) source).getEnd()));
        }

        else {
            descendents.add(source);
        }

        return descendents;
    }

    @Override
    protected Object internalEvaluate(Context context) {
        Object source = getSource().evaluate(context);

        return descendents(source);
    }
}
