/*
 *
 *   This code is derived from work done by Barry Cassidy from Motive Medical Intelligence
 *   Thank you!
 *
 * */

package org.opencds.cqf.cql.engine.fhir.terminology;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HeaderInjectionInterceptor implements IClientInterceptor {

    private HashMap<String, String> headers;

    /**
     * Instantiates a new header injection interception.
     *
     * @param headerKey the header key
     * @param headerValue the header value
     */
    public HeaderInjectionInterceptor(String headerKey, String headerValue) {
        super();
        this.headers = new HashMap<>();
        this.headers.put(headerKey, headerValue);
    }

    /**
     * Instantiates a new header injection interception.
     *
     * @param headers the headers
     */
    public HeaderInjectionInterceptor(HashMap<String, String> headers) {
        super();
        this.headers = headers;
    }

    @Override
    public void interceptRequest(IHttpRequest request) {

        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            request.addHeader(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void interceptResponse(IHttpResponse response) throws IOException {
        // nothing
    }
}
