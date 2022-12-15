package org.opencds.cqf.cql.engine.runtime.iterators;

import java.util.AbstractMap;
import java.util.Iterator;


/**
 * Created by Bryn on 8/11/2019.
 */
public class TimesIterator implements Iterator<Object> {

    private Iterator<Object> left;
    private ResetIterator<Object> right;
    private boolean leftNeeded = true;
    private Object leftElement;

    public TimesIterator(Iterator<Object> left, Iterator<Object> right) {
        this.left = left;
        this.right = new ResetIterator<Object>(right);
    }

    @Override
    public boolean hasNext() {
        if (leftNeeded) {
            return left.hasNext() && right.hasNext();
        }

        if (!right.hasNext()) {
            if (left.hasNext()) {
                right.reset();
                leftNeeded = true;
            }
        }

        return right.hasNext();
    }

    @Override
    public Object next() {
        if (leftNeeded) {
            leftElement = left.next();
            leftNeeded = false;
        }

        return new AbstractMap.SimpleEntry<Object, Object>(leftElement, right.next());
    }
}

