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

    private String URLEncode(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Iterable<Object> retrieve(String context, String dataType, String templateId,
                                     String codePath, Iterable<Code> codes, String valueSet, String datePath, String dateLowPath,
                                     String dateHighPath, Interval dateRange) {

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

        if (codePath == null && (codes != null || valueSet != null)) {
            throw new IllegalArgumentException("A code path must be provided when filtering on codes or a valueset.");
        }

        if (codePath != null && !codePath.equals("")) {
            if (params.length() > 0) {
                params.append("&");
            }
            if (valueSet != null && !valueSet.equals("")) {
                params.append(String.format("%s:in=%s", convertPathToSearchParam(dataType, codePath), URLEncode(valueSet)));
            }
            else if (codes != null) {
                StringBuilder codeList = new StringBuilder();
                for (Code code : codes) {
                    if (codeList.length() > 0) {
                        codeList.append(",");
                    }

                    if (code.getSystem() != null) {
                        codeList.append(URLEncode(code.getSystem()));
                        codeList.append("|");
                    }

                    codeList.append(URLEncode(code.getCode()));
                }
                params.append(String.format("%s=%s", convertPathToSearchParam(dataType, codePath), codeList.toString()));
            }
        }

        if (dateRange != null) {
            if (dateRange.getLow() != null) {
                String lowDatePath = convertPathToSearchParam(dataType, dateLowPath != null ? dateLowPath : datePath);
                if (lowDatePath == null || lowDatePath.equals("")) {
                    throw new IllegalArgumentException("A date path or low date path must be provided when filtering on a date range.");
                }

                params.append(String.format("%s=%s%s",
                        lowDatePath,
                        dateRange.getLowClosed() ? "ge" : "gt",
                        dateRange.getLow().toString()));
            }

            if (dateRange.getHigh() != null) {
                String highDatePath = convertPathToSearchParam(dataType, dateHighPath != null ? dateHighPath : datePath);
                if (highDatePath == null || highDatePath.equals("")) {
                    throw new IllegalArgumentException("A date path or high date path must be provided when filtering on a date range.");
                }

                params.append(String.format("%s=%s%s",
                        highDatePath,
                        dateRange.getHighClosed() ? "le" : "lt",
                        dateRange.getHigh().toString()));
            }
        }

        // TODO: Use compartment search for patient context?
        if (params.length() > 0) {
            search = fhirClient.search().byUrl(String.format("%s?%s", dataType, params.toString()));
        }
        else {
            search = fhirClient.search().forResource(dataType);
        }

        Bundle results = search.returnBundle(Bundle.class).execute();
        return new FhirBundleCursor(fhirClient, results);
    }

    private String convertPathToSearchParam(String dataType, String codePath) {
        return codePath.replace('.', '-');
        // TODO: The above won't work in all cases, but is a best guess for now
        // What we really need is something like the below, but that's a lot of specific mapping...
//        switch (dataType) {
//            case "Patient": {
//                switch (codePath) {
//                    case "active": return "active";
//                    case "address": return "address";
//                    case "address.city": return "address-city";
//                    case "address.country": return "address-country";
//                    case "address.postalCode": return "address-postalcode";
//                    case "address.state": return "address-state";
//                    case "address.use": return "adddress-use";
//                    case "animal.breed": return "animal-breed";
//                    case "animal.species": return "animal-species";
//                    case "birthDate": return "birthdate";
//                    // TODO: the rest of the patient search parameters
//                    case "gender": return "gender";
//                }
//            }
//            default: throw new IllegalArgumentException(String.format("Search path map for code path %s.%s is not defined.", dataType, codePath));
//        }
    }
}
