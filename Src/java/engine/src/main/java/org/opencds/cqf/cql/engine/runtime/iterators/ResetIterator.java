package org.opencds.cqf.cql.engine.runtime.iterators;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Bryn on 8/11/2019.
 */
public class ResetIterator<E> implements Iterator<E> {
    private Iterator<E> source;
    private ArrayList<E> data = new ArrayList<E>();
    int dataIndex = -1;
    private boolean dataCached = false;

    public ResetIterator(Iterator<E> source) {
        this.source = source;
        data = new ArrayList<E>();
    }

    @Override
    public boolean hasNext() {
        if (!dataCached) {
            return source.hasNext();
        }

        return dataIndex < data.size() - 1 && data.size() > 0;
    }

    @Override
    public E next() {
        if (!dataCached) {
            E element = source.next();
            data.add(element);
            return element;
        }

        dataIndex++;
        return data.get(dataIndex);
    }

    public void reset() {
        // Fill any remaining data
        while (source.hasNext()) {
            data.add(source.next());
        }
        dataCached = true;
        dataIndex = -1;
    }
}

