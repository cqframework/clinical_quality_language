package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Tuple;

import java.util.ArrayList;
import java.util.List;

public class DescendentsEvaluator {

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

}
