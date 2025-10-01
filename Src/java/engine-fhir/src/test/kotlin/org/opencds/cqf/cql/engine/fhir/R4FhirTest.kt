package org.opencds.cqf.cql.engine.fhir

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import ca.uhn.fhir.rest.client.api.IGenericClient
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import java.net.ServerSocket
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CapabilityStatement
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class R4FhirTest {
    var wireMockServer: WireMockServer? = null
    var wireMock: WireMock? = null

    @BeforeEach
    fun start() {
        wireMockServer = WireMockServer(httpPort)
        wireMockServer!!.start()
        WireMock.configureFor("localhost", httpPort)
        wireMock = WireMock("localhost", httpPort)

        mockFhirRead("/metadata", this.capabilityStatement)
    }

    @AfterEach
    fun stop() {
        wireMockServer!!.stop()
    }

    fun mockNotFound(resource: String?) {
        val outcome = OperationOutcome()
        outcome.getText().statusAsString = "generated"
        outcome.issueFirstRep
            .setSeverity(OperationOutcome.IssueSeverity.ERROR)
            .setCode(OperationOutcome.IssueType.PROCESSING)
            .setDiagnostics(resource)

        mockFhirRead(resource, outcome, 404)
    }

    fun mockFhirRead(resource: Resource) {
        val resourcePath = "/" + resource.fhirType() + "/" + resource.getId()
        mockFhirInteraction(resourcePath, resource)
    }

    @JvmOverloads
    fun mockFhirRead(path: String?, resource: Resource?, statusCode: Int = 200) {
        val builder = WireMock.get(WireMock.urlEqualTo(path))
        mockFhirInteraction(builder, resource, statusCode)
    }

    fun mockFhirSearch(path: String?, vararg resources: Resource) {
        val builder = WireMock.get(WireMock.urlEqualTo(path))
        mockFhirInteraction(builder, makeBundle(*resources))
    }

    fun mockFhirPost(path: String?, resource: Resource?) {
        mockFhirInteraction(WireMock.post(WireMock.urlEqualTo(path)), resource, 200)
    }

    fun mockFhirInteraction(path: String?, resource: Resource?) {
        mockFhirRead(path, resource, 200)
    }

    @JvmOverloads
    fun mockFhirInteraction(builder: MappingBuilder, resource: Resource?, statusCode: Int = 200) {
        var body: String? = null
        if (resource != null) {
            body = fhirParser.encodeResourceToString(resource)
        }

        WireMock.stubFor(
            builder.willReturn(
                WireMock.aResponse()
                    .withStatus(statusCode)
                    .withHeader("Content-Type", "application/json")
                    .withBody(body)
            )
        )
    }

    val capabilityStatement: CapabilityStatement
        get() {
            val metadata = CapabilityStatement()
            metadata.setFhirVersion(Enumerations.FHIRVersion._4_0_1)
            return metadata
        }

    fun makeBundle(resources: MutableList<out Resource>): Bundle {
        return makeBundle(*resources.toTypedArray<Resource>())
    }

    fun makeBundle(vararg resources: Resource): Bundle {
        val bundle = Bundle()
        bundle.setType(Bundle.BundleType.SEARCHSET)
        bundle.setTotal(resources.size)
        for (l in resources) {
            bundle.addEntry().setResource(l).setFullUrl("/" + l.fhirType() + "/" + l.getId())
        }
        return bundle
    }

    companion object {
        val fhirContext: FhirContext = FhirContext.forCached(FhirVersionEnum.R4)
        val fhirParser: IParser = fhirContext.newJsonParser().setPrettyPrint(true)
        private var HTTP_PORT = 0

        val httpPort: Int
            get() {
                if (HTTP_PORT == 0) {
                    try {
                        ServerSocket(0).use { socket -> HTTP_PORT = socket.getLocalPort() }
                    } catch (ex: Exception) {
                        throw RuntimeException(
                            "Failed to determine a port for the wiremock server",
                            ex,
                        )
                    }
                }
                return HTTP_PORT
            }

        fun newClient(): IGenericClient {
            val client: IGenericClient =
                fhirContext.newRestfulGenericClient(String.format("http://localhost:%d/", httpPort))

            val logger = LoggingInterceptor()
            logger.setLogRequestSummary(true)
            logger.setLogResponseBody(true)
            client.registerInterceptor(logger)

            return client
        }
    }
}
