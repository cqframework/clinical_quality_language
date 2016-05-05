package org.cqframework.cql.runtime;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Bryn on 5/2/2016.
 */
public class Value {
    public static Boolean equals(Object left, Object right) {
        if ((left == null) || (right == null)) {
            return null;
        }

        // list equal
        if (left instanceof Iterable) {
            Iterable<Object> leftList = (Iterable<Object>)left;
            Iterable<Object> rightList = (Iterable<Object>)right;
            Iterator<Object> leftIterator = leftList.iterator();
            Iterator<Object> rightIterator = rightList.iterator();

            while (leftIterator.hasNext()) {
                Object leftObject = leftIterator.next();
                if (rightIterator.hasNext()) {
                    Object rightObject = rightIterator.next();
                    Boolean elementEquals = equals(leftObject, rightObject);
                    if (elementEquals == null || elementEquals == false) {
                        return elementEquals;
                    }
                }
                else {
                    return false;
                }
            }

            return true;
        }

        return left.equals(right);
    }

    public static Boolean equivalent(Object left, Object right) {
        if ((left == null) && (right == null)) {
            return true;
        }

        if ((left == null) || (right == null)) {
            return false;
        }

        // list equal
        if (left instanceof Iterable) {
            Iterable<Object> leftList = (Iterable<Object>)left;
            Iterable<Object> rightList = (Iterable<Object>)right;
            Iterator<Object> leftIterator = leftList.iterator();
            Iterator<Object> rightIterator = rightList.iterator();

            while (leftIterator.hasNext()) {
                Object leftObject = leftIterator.next();
                if (rightIterator.hasNext()) {
                    Object rightObject = rightIterator.next();
                    Boolean elementEquivalent = equivalent(leftObject, rightObject);
                    if (elementEquivalent == null || elementEquivalent == false) {
                        return elementEquivalent;
                    }
                }
                else {
                    return false;
                }
            }

            return true;
        }

        return left.equals(right);
    }

    public static Iterable<Object> ensureIterable(Object source) {
        if (source instanceof Iterable) {
            return (Iterable<Object>)source;
        }
        else {
            ArrayList sourceList = new ArrayList();
            if (source != null)
                sourceList.add(source);
            return sourceList;
        }
    }
}
