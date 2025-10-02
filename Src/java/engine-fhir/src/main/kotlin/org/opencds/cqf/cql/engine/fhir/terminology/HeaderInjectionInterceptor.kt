/*
 *
 *   This code is derived from work done by Barry Cassidy from Motive Medical Intelligence
 *   Thank you!
 *
 * */
package org.opencds.cqf.cql.engine.fhir.terminology

import ca.uhn.fhir.rest.client.api.IClientInterceptor
import ca.uhn.fhir.rest.client.api.IHttpRequest
import ca.uhn.fhir.rest.client.api.IHttpResponse
import java.io.IOException

class HeaderInjectionInterceptor(private val headers: Map<String, String> = mapOf()) :
    IClientInterceptor {
    /**
     * Instantiates a new header injection interception.
     *
     * @param headerKey the header key
     * @param headerValue the header value
     */
    constructor(headerKey: String, headerValue: String) : this(mapOf(headerKey to headerValue))

    override fun interceptRequest(request: IHttpRequest) {
        for (entry in this.headers.entries) {
            request.addHeader(entry.key, entry.value)
        }
    }

    @Throws(IOException::class)
    override fun interceptResponse(response: IHttpResponse?) {
        // nothing
    }
}
