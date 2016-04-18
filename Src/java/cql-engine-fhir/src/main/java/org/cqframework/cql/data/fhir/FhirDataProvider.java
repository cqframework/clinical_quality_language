package org.cqframework.cql.data.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import org.cqframework.cql.data.DataProvider;
import org.cqframework.cql.runtime.Concept;
import org.cqframework.cql.runtime.Interval;
import org.joda.time.Partial;

/**
 * Created by Bryn on 4/16/2016.
 */
public class FhirDataProvider implements DataProvider {

    private String endpoint;
    public String getEndpoint() {
        return endpoint;
    }
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        fhirContext = FhirContext.forDstu3();
        fhirClient = fhirContext.newRestfulGenericClient(endpoint);
    }
    public FhirDataProvider withEndpoint(String endpoint) {
        setEndpoint(endpoint);
        return this;
    }

    private FhirContext fhirContext;
    private IGenericClient fhirClient;

    public Iterable<Object> retrieve(String context, String dataType, String templateId,
             String codePath, Iterable<Concept> codes, String datePath, String dateLowPath,
             String dateHighPath, Interval<Partial> dateRange) {

        // TODO: Apply filtering based on
            // profile (templateId)
            // codes
            // dateRange
        ca.uhn.fhir.model.api.Bundle results = fhirClient.search().forResource(dataType).execute();
        return new FhirBundleCursor(fhirClient, results);
    }
}
