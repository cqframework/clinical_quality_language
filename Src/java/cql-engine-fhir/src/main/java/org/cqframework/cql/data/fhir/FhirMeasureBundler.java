package org.cqframework.cql.data.fhir;

import org.cqframework.cql.execution.Context;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Resource;


/**
 * Created by Bryn on 5/7/2016.
 */
public class FhirMeasureBundler {
    // Adds the resources returned from the given expressions to a bundle
    public Bundle Bundle(Context context, String... expressionNames) {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.COLLECTION);
        for (String expressionName : expressionNames) {
            Object result = context.resolveExpressionRef((String)null, expressionName).evaluate(context);
            for (Object element : (Iterable)result) {
                Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
                entry.setResource((Resource)element);
                entry.setFullUrl(((Resource)element).getId());
                bundle.getEntry().add(entry);
            }
        }

        return bundle;
    }
}
