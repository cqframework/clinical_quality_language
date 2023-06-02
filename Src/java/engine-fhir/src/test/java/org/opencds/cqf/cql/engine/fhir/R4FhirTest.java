package org.opencds.cqf.cql.engine.fhir;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.net.ServerSocket;
import java.util.List;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.Resource;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;

public abstract class R4FhirTest {
    private static FhirContext FHIR_CONTEXT = FhirContext.forCached(FhirVersionEnum.R4);
    private static IParser FHIR_PARSER = FHIR_CONTEXT.newJsonParser().setPrettyPrint(true);
    private static int HTTP_PORT = 0;

    // emulate wiremock's junit.WireMockRule with testng features
    WireMockServer wireMockServer;
    WireMock wireMock;

    @BeforeMethod()
    public void start() {
        wireMockServer = new WireMockServer(getHttpPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", getHttpPort());
        wireMock = new WireMock("localhost", getHttpPort());

        mockFhirRead( "/metadata", getCapabilityStatement() );
    }

    @AfterMethod
    public void stop() {
        wireMockServer.stop();
    }

    public FhirContext getFhirContext() {
        return FHIR_CONTEXT;
    }

    public IParser getFhirParser() {
        return FHIR_PARSER;
    }

    public int getHttpPort() {
        if( HTTP_PORT == 0 ) {
            try(ServerSocket socket = new ServerSocket(0)) {
                HTTP_PORT = socket.getLocalPort();
            } catch( Exception ex ) {
                throw new RuntimeException("Failed to determine a port for the wiremock server", ex);
            }
        }
        return HTTP_PORT;
    }

    public IGenericClient newClient() {
        IGenericClient client = getFhirContext().newRestfulGenericClient(String.format("http://localhost:%d/", getHttpPort()));

        LoggingInterceptor logger = new LoggingInterceptor();
        logger.setLogRequestSummary(true);
        logger.setLogResponseBody(true);
        client.registerInterceptor(logger);

        return client;
    }

    public void mockNotFound(String resource) {
        OperationOutcome outcome = new OperationOutcome();
        outcome.getText().setStatusAsString("generated");
        outcome.getIssueFirstRep().setSeverity(IssueSeverity.ERROR).setCode(OperationOutcome.IssueType.PROCESSING).setDiagnostics(resource);

        mockFhirRead( resource, outcome, 404 );
    }

    public void mockFhirRead( Resource resource ) {
        String resourcePath = "/" + resource.fhirType() + "/" + resource.getId();
        mockFhirInteraction( resourcePath, resource );
    }

    public void mockFhirRead( String path, Resource resource ) {
        mockFhirRead( path, resource, 200 );
    }

    public void mockFhirRead( String path, Resource resource, int statusCode ) {
        MappingBuilder builder = get(urlEqualTo(path));
        mockFhirInteraction( builder, resource, statusCode );
    }

    public void mockFhirSearch( String path, Resource... resources ) {
        MappingBuilder builder = get(urlEqualTo(path));
        mockFhirInteraction( builder,  makeBundle( resources ) );
    }

    public void mockFhirPost( String path, Resource resource ) {
        mockFhirInteraction( post(urlEqualTo(path)), resource, 200 );
    }

    public void mockFhirInteraction( String path, Resource resource ) {
        mockFhirRead( path, resource, 200 );
    }

    public void mockFhirInteraction( MappingBuilder builder, Resource resource ) {
        mockFhirInteraction( builder, resource, 200 );
    }

    public void mockFhirInteraction( MappingBuilder builder, Resource resource, int statusCode ) {
        String body = null;
        if( resource != null ) {
            body = getFhirParser().encodeResourceToString(resource);
        }

        stubFor(builder.willReturn(aResponse().withStatus(statusCode).withHeader("Content-Type", "application/json").withBody(body)));
    }

    public CapabilityStatement getCapabilityStatement() {
        CapabilityStatement metadata = new CapabilityStatement();
        metadata.setFhirVersion(Enumerations.FHIRVersion._4_0_1);
        return metadata;
    }

    public Bundle makeBundle(List<? extends Resource> resources) {
        return makeBundle( resources.toArray(new Resource[resources.size()]));
    }

    public Bundle makeBundle(Resource... resources) {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.SEARCHSET);
        bundle.setTotal(resources != null ? resources.length : 0);
        if( resources != null ) {
            for (Resource l : resources) {
                bundle.addEntry().setResource(l).setFullUrl("/" + l.fhirType() + "/" + l.getId());
            }
        }
        return bundle;
    }
}
