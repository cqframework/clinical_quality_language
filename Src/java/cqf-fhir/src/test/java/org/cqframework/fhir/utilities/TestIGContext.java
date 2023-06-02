package org.cqframework.fhir.utilities;

import org.hl7.fhir.r5.context.IWorkerContext;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.testng.Assert.*;

public class TestIGContext implements IWorkerContext.ILoggingService {

    @Test
    public void TestTypesAndValuesIG() throws URISyntaxException {
        URL url = TestIGContext.class.getResource("types-and-values/ig.ini");
        assertNotNull(url);
        URI uri = Uris.parseOrNull(url.toURI().toString());
        assertNotNull(uri);
        String path = uri.getSchemeSpecificPart();
        IGContext igContext = new IGContext(this);
        igContext.initializeFromIni(path);
        assertEquals(igContext.getPackageId(), "fhir.cqf.typesandvalues");
        assertEquals(igContext.getCanonicalBase(), "http://fhir.org/guides/cqf/typesandvalues");
    }

    @Override
    public void logMessage(String s) {
        System.out.println(s);
    }

    @Override
    public void logDebugMessage(LogCategory logCategory, String s) {
        System.out.println(String.format("%s: %s", logCategory.toString(), s));
    }

    @Override
    public boolean isDebugLogging() {
        return true;
    }
}
