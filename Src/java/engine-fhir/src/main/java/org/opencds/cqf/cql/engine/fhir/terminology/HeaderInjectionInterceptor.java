/*
*
*   This code is derived from work done by Barry Cassidy from Motive Medical Intelligence
*   Thank you!
*
* */

package org.opencds.cqf.cql.engine.fhir.terminology;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;

public class HeaderInjectionInterceptor implements IClientInterceptor {


    private HashMap<String, String> myHeaders;

    /**
     * Instantiates a new header injection interception.
     *
     * @param headerKey the header key
     * @param headerValue the header value
     */
    public HeaderInjectionInterceptor(String headerKey, String headerValue) {
        super();
        this.myHeaders = new HashMap<>();
        this.myHeaders.put(headerKey, headerValue);
    }

    /**
     * Instantiates a new header injection interception.
     *
     * @param headers the headers
     */
    public HeaderInjectionInterceptor(HashMap<String, String> headers) {
        super();
        this.myHeaders = headers;
    }

    @Override
    public void interceptRequest(IHttpRequest theRequest) {

        for(Map.Entry<String, String> entry : this.myHeaders.entrySet()) {
            theRequest.addHeader(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void interceptResponse(IHttpResponse theResponse) throws IOException {
        // nothing
    }
}
