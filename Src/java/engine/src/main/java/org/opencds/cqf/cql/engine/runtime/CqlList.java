package org.opencds.cqf.cql.engine.runtime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.Expression;
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.exception.InvalidComparison;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.execution.Variable;

public class CqlList {
    private State state;
    private String alias;
    private Expression expression;
    private ElmLibraryVisitor<Object, State> visitor;
    private String path;

    public CqlList() {}

    public CqlList(State state, ElmLibraryVisitor<Object, State> visitor, String alias, Expression expression) {
        this.state = state;
        this.visitor = visitor;
        this.alias = alias;
        this.expression = expression;
    }

    public CqlList(State state, String path) {
        this.state = state;
        this.path = path;
    }

    public Comparator<Object> valueSort = this::compareTo;

    public Comparator<Object> expressionSort = new Comparator<Object>() {
        public int compare(Object left, Object right) {

            try {
                state.push(new Variable().withName(alias).withValue(left));
                left = visitor.visitExpression(expression, state);
            } finally {
                state.pop();
            }

            try {
                state.push(new Variable().withName(alias).withValue(right));
                right = visitor.visitExpression(expression, state);
            } finally {
                state.pop();
            }

            return compareTo(left, right);
        }
    };

    public final Comparator<Object> columnSort = new Comparator<>() {
        public int compare(Object left, Object right) {
            Object leftCol = state.getEnvironment().resolvePath(left, path);
            Object rightCol = state.getEnvironment().resolvePath(right, path);

            return compareTo(leftCol, rightCol);
        }
    };

    @SuppressWarnings({"rawtypes", "unchecked"})
    public int compareTo(Object left, Object right) {
        if (left == null && right == null) return 0;
        else if (left == null) return -1;
        else if (right == null) return 1;

        try {
            return ((Comparable) left).compareTo(right);
        } catch (ClassCastException cce) {
            throw new InvalidComparison("Type " + left.getClass().getName() + " is not comparable");
        }
    }

    public static Boolean equivalent(Iterable<?> left, Iterable<?> right, State state) {
        Iterator<?> leftIterator = left.iterator();
        Iterator<?> rightIterator = right.iterator();

        while (leftIterator.hasNext()) {
            Object leftObject = leftIterator.next();
            if (rightIterator.hasNext()) {
                Object rightObject = rightIterator.next();
                Boolean elementEquivalent = EquivalentEvaluator.equivalent(leftObject, rightObject, state);
                if (!elementEquivalent) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return !rightIterator.hasNext();
    }

    public static Boolean equal(Iterable<?> left, Iterable<?> right, State state) {
        Iterator<?> leftIterator = left.iterator();
        Iterator<?> rightIterator = right.iterator();

        while (leftIterator.hasNext()) {
            Object leftObject = leftIterator.next();
            if (rightIterator.hasNext()) {
                Object rightObject = rightIterator.next();
                if (leftObject instanceof Iterable && rightObject instanceof Iterable) {
                    return equal((Iterable<?>) leftObject, (Iterable<?>) rightObject, state);
                }
                if (leftObject == null && rightObject == null) {
                    continue;
                }
                Boolean elementEquals = EqualEvaluator.equal(leftObject, rightObject, state);
                if (elementEquals == null || !elementEquals) {
                    return elementEquals;
                }
            } else if (leftObject == null) {
                return null;
            } else {
                return false;
            }
        }

        if (rightIterator.hasNext()) {
            return rightIterator.next() == null ? null : false;
        }

        return true;
    }

    public static <T> List<T> toList(Iterable<T> iterable, boolean includeNullElements) {
        List<T> ret = new ArrayList<>();
        for (T element : iterable) {
            if (element != null || includeNullElements) {
                ret.add(element);
            }
        }
        return ret;
    }
}
