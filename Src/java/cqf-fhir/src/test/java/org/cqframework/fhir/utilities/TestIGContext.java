package org.cqframework.fhir.utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.hl7.fhir.r5.context.IWorkerContext;
import org.junit.jupiter.api.Test;

public class TestIGContext implements IWorkerContext.ILoggingService {

    @Test
    void typesAndValuesIG() throws URISyntaxException {
        URL url = TestIGContext.class.getResource("types-and-values/ig.ini");
        assertNotNull(url);
        URI uri = Uris.parseOrNull(url.toURI().toString());
        assertNotNull(uri);
        String path = uri.getSchemeSpecificPart();
        IGContext igContext = new IGContext(this);
        igContext.initializeFromIni(path);
        assertEquals("fhir.cqf.typesandvalues", igContext.getPackageId());
        assertEquals("http://fhir.org/guides/cqf/typesandvalues", igContext.getCanonicalBase());
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
