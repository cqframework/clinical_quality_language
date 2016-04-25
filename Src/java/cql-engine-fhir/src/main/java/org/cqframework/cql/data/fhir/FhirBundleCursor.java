package org.cqframework.cql.data.fhir;

import org.hl7.fhir.dstu3.model.Bundle;
import ca.uhn.fhir.rest.client.IGenericClient;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Created by Bryn on 4/16/2016.
 */
public class FhirBundleCursor implements Iterable<Object> {
    public FhirBundleCursor(IGenericClient fhirClient, Bundle results) {
        this.fhirClient = fhirClient;
        this.results = results;
    }

    private IGenericClient fhirClient;
    private Bundle results;

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    public Iterator<Object> iterator() {
        return new FhirBundleIterator(fhirClient, results);
    }

    private class FhirBundleIterator implements Iterator<Object> {
        public FhirBundleIterator(IGenericClient fhirClient, Bundle results) {
            this.fhirClient = fhirClient;
            this.results = results;
            this.current = -1;
        }

        private IGenericClient fhirClient;
        private Bundle results;
        private int current;

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        public boolean hasNext() {
            return current < results.getEntry().size() - 1;
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        public Object next() {
            current++;
            if (current < results.getEntry().size()) {
                return results.getEntry().get(current).getResource();
            } else {
                results = fhirClient.loadPage().next(results).execute();
                current = 0;
                if (current < results.getEntry().size()) {
                    return results.getEntry().get(current).getResource();
                }
            }

            // TODO: It would be possible to get here if the next link was present, but the returned page had 0 entries...
            throw new NoSuchElementException();
        }
    }
}
