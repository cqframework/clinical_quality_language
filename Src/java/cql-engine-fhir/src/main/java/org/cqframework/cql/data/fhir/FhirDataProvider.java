package org.cqframework.cql.data.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import org.cqframework.cql.data.DataProvider;
import org.cqframework.cql.runtime.Code;
import org.cqframework.cql.runtime.Interval;
import org.hl7.fhir.dstu3.model.Bundle;
import org.joda.time.Partial;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


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
                                     String codePath, Iterable<Code> codes, String valueSet, String datePath, String dateLowPath,
                                     String dateHighPath, Interval<Partial> dateRange) {

        // Apply filtering based on
        //  profile (templateId)
        //  codes
        //  dateRange
        IQuery<ca.uhn.fhir.model.api.Bundle> search = null; //fhirClient.search().forResource(dataType);

        // TODO: Would like to be able to use the criteria builders, but it looks like they don't have one for :in with a valueset?
        // So..... I'll just construct a search URL
        //        if (templateId != null && !templateId.equals("")) {
        //            search = search.withProfile(templateId);
        //        }
        //
        //        if (codePath != null && !codePath.equals("")) {
        //            search.where(Patient.ACTIVE.)
        //        }

        // TODO: It's unclear from the FHIR documentation whether we need to use a URLEncoder.encode call on the embedded system and valueset uris here...
        StringBuilder params = new StringBuilder();

        if (templateId != null && !templateId.equals("")) {
            params.append(String.format("_profile=%s", templateId));
        }

        if (codePath != null && !codePath.equals("")) {
            if (params.length() > 0) {
                params.append("&");
            }
            if (valueSet != null && !valueSet.equals("")) {
                params.append(String.format("%s:in=%s", convertPathToSearchParam(dataType, codePath), valueSet));
            }
            else if (codes != null) {
                StringBuilder codeList = new StringBuilder();
                for (Code code : codes) {
                    if (codeList.length() > 0) {
                        codeList.append(",");
                    }

                    if (code.getSystem() != null) {
                        codeList.append(code.getSystem());
                        codeList.append("|");
                    }

                    codeList.append(code.getCode());

                    if (code.getSystem() != null) {
                        codeList.append(String.format("%s|%s"));
                    }
                    else {
                        codeList.append(String.format("%s"));
                    }
                }
                params.append(String.format("%s=%s", convertPathToSearchParam(dataType, codePath), codeList.toString()));
            }
        }

        if (params.length() > 0) {
            search = fhirClient.search().byUrl(params.toString());
        }
        else {
            search = fhirClient.search().forResource(dataType);
        }

        Bundle results = search.returnBundle(Bundle.class).execute();
        return new FhirBundleCursor(fhirClient, results);
    }

    private String convertPathToSearchParam(String dataType, String codePath) {
        switch (dataType) {
            case "Patient": {
                switch (codePath) {
                    case "active": return "active";
                    case "address": return "address";
                    case "address.city": return "address-city";
                    case "address.country": return "address-country";
                    case "address.postalCode": return "address-postalcode";
                    case "address.state": return "address-state";
                    case "address.use": return "adddress-use";
                    case "animal.breed": return "animal-breed";
                    case "animal.species": return "animal-species";
                    case "birthDate": return "birthdate";
                    // TODO: the rest of the patient search parameters
                    case "gender": return "gender";
                }
            }
            default: throw new IllegalArgumentException(String.format("Search path map for code path %s.%s is not defined.", dataType, codePath));
        }
    }
}
