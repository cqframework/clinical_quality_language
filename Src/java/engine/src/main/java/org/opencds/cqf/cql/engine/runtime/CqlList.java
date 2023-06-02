package org.opencds.cqf.cql.engine.runtime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.cqframework.cql.elm.execution.Expression;
import org.opencds.cqf.cql.engine.elm.execution.EqualEvaluator;
import org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.exception.InvalidComparison;
import org.opencds.cqf.cql.engine.execution.Context;
import org.opencds.cqf.cql.engine.execution.Variable;

public class CqlList {
    private Context context;
    private String alias;
    private Expression expression;
    private String path;

    public CqlList() { }

    public CqlList(Context context, String alias, Expression expression) {
        this.context = context;
        this.alias = alias;
        this.expression = expression;
    }

    public CqlList(Context context, String path) {
        this.context = context;
        this.path = path;
    }

    public Comparator<Object> valueSort = this::compareTo;

    public Comparator<Object> expressionSort = new Comparator<Object>() {
        public int compare(Object left, Object right) {

            try {
                context.push(new Variable().withName(alias).withValue(left));
                left = expression.evaluate(context);
            }
            finally {
                context.pop();
            }

            try {
                context.push(new Variable().withName(alias).withValue(right));
                right = expression.evaluate(context);
            }
            finally {
                context.pop();
            }

            return compareTo(left, right);
        }
    };

    public Comparator<Object> columnSort = new Comparator<Object>() {
        public int compare(Object left, Object right) {
            Object leftCol = context.resolvePath(left, path);
            Object rightCol = context.resolvePath(right, path);

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

    public static Boolean equivalent(Iterable<?> left, Iterable<?> right, Context context) {
        Iterator<?> leftIterator = left.iterator();
        Iterator<?> rightIterator = right.iterator();

        while (leftIterator.hasNext()) {
            Object leftObject = leftIterator.next();
            if (rightIterator.hasNext()) {
                Object rightObject = rightIterator.next();
                Boolean elementEquivalent = EquivalentEvaluator.equivalent(leftObject, rightObject, context);
                if (!elementEquivalent) {
                    return false;
                }
            }
            else { return false; }
        }

        return !rightIterator.hasNext();
    }

    public static Boolean equal(Iterable<?> left, Iterable<?> right, Context context) {
        Iterator<?> leftIterator = left.iterator();
        Iterator<?> rightIterator = right.iterator();

        while (leftIterator.hasNext()) {
            Object leftObject = leftIterator.next();
            if (rightIterator.hasNext()) {
                Object rightObject = rightIterator.next();
                if (leftObject instanceof Iterable && rightObject instanceof Iterable) {
                    return equal((Iterable<?>) leftObject, (Iterable<?>) rightObject, context);
                }
                Boolean elementEquals = EqualEvaluator.equal(leftObject, rightObject, context);
                if (elementEquals == null || !elementEquals) {
                    return elementEquals;
                }
            }
            else if (leftObject == null) {
                return null;
            }
            else {
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
