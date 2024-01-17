package org.opencds.cqf.cql.engine.fhir.retrieve;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.opencds.cqf.cql.engine.fhir.exception.UnknownElement;

public class FhirBundleCursor implements Iterable<Object> {

    public FhirBundleCursor(IGenericClient fhirClient, IBaseBundle results) {
        this(fhirClient, results, null, null);
    }

    public FhirBundleCursor(IGenericClient fhirClient, IBaseBundle results, String dataType) {
        this(fhirClient, results, dataType, null);
    }

    // This constructor filters the bundle based on dataType
    // If templateId is provided, this is a trusted cursor, meaning that it will only return results
    // for resources that declare they conform to the given profile
    public FhirBundleCursor(IGenericClient fhirClient, IBaseBundle results, String dataType, String templateId) {
        this.fhirClient = fhirClient;
        this.results = results;
        this.dataType = dataType;
        this.templateId = templateId;
    }

    private IGenericClient fhirClient;
    private IBaseBundle results;
    private String dataType;
    private String templateId;

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    public Iterator<Object> iterator() {
        return new FhirBundleIterator(fhirClient, results, dataType, templateId);
    }

    private class FhirBundleIterator implements Iterator<Object> {
        public FhirBundleIterator(IGenericClient fhirClient, IBaseBundle results, String dataType, String templateId) {
            this.fhirClient = fhirClient;
            this.results = results;
            this.current = -1;
            this.dataType = dataType;
            this.templateId = templateId;

            // Do not test templateId for base resource "profiles"
            if (this.templateId != null
                    && this.templateId.startsWith(
                            String.format("http://hl7.org/fhir/StructureDefinition/%s", dataType))) {
                this.templateId = null;
            }

            if (dataType != null) {
                this.dataTypeClass = this.fhirClient
                        .getFhirContext()
                        .getResourceDefinition(this.dataType)
                        .getImplementingClass();
            }

            this.currentEntry = this.getEntry();
        }

        private IGenericClient fhirClient;
        private IBaseBundle results;
        private int current;
        private String dataType;
        private String templateId;
        private Class<? extends IBaseResource> dataTypeClass;
        private List<? extends IBaseResource> currentEntry;

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        public boolean hasNext() {
            return current < this.currentEntry.size() - 1 || this.getLink() != null;
        }

        private List<? extends IBaseResource> getEntry() {
            if (this.dataTypeClass != null) {
                List<? extends IBaseResource> entries = BundleUtil.toListOfResourcesOfType(
                        this.fhirClient.getFhirContext(), this.results, this.dataTypeClass);
                if (templateId != null) {
                    return getTrustedEntries(entries, templateId);
                } else {
                    return entries;
                }
            } else {
                return BundleUtil.toListOfResources(this.fhirClient.getFhirContext(), this.results);
            }
        }

        private List<? extends IBaseResource> getTrustedEntries(
                List<? extends IBaseResource> entries, String templateId) {
            List<IBaseResource> trustedEntries = new ArrayList<IBaseResource>();
            for (IBaseResource entry : entries) {
                if (entry.getMeta() != null && entry.getMeta().getProfile() != null) {
                    for (IPrimitiveType<?> profile : entry.getMeta().getProfile()) {
                        if (profile.hasValue() && profile.getValueAsString().equals(templateId)) {
                            trustedEntries.add(entry);
                        }
                    }
                }
            }
            return trustedEntries;
        }

        private String getLink() {
            return BundleUtil.getLinkUrlOfType(this.fhirClient.getFhirContext(), this.results, IBaseBundle.LINK_NEXT);
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws UnknownElement if the iteration has no more elements
         */
        public Object next() {
            current++;
            if (current < this.currentEntry.size()) {
                return this.currentEntry.get(current);
            } else {
                this.results = fhirClient.loadPage().next(results).execute();
                this.currentEntry = getEntry();
                current = 0;
                if (current < this.currentEntry.size()) {
                    return this.currentEntry.get(current);
                }
            }

            // TODO: It would be possible to get here if the next link was present, but the returned page had 0
            // entries...
            // NOTE: This is especially true if the page has only data that is not conformant to the given profile
            throw new UnknownElement("The iteration has no more elements.");
        }
    }
}
