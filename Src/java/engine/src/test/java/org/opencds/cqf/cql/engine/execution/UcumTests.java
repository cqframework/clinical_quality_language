package org.opencds.cqf.cql.engine.execution;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.time.ZonedDateTime;

import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;
import org.opencds.cqf.cql.engine.data.SystemDataProvider;
import org.testng.annotations.Test;

public class UcumTests {

    @Test
    public void contextCreatesSharedUcumService() {
        Context context = new Context(new Library().withIdentifier(new VersionedIdentifier().withId("Test")));
        assertNotNull(context.getUcumService());
    }

    @Test
    public void contextUsesUcumService() throws UcumException {
        UcumService ucumService = new UcumEssenceService(UcumEssenceService.class.getResourceAsStream("/ucum-essence.xml"));
        Context context = new Context(new Library().withIdentifier(new VersionedIdentifier().withId("Test")), ZonedDateTime.now(), new SystemDataProvider(), ucumService);
        assertEquals(context.getUcumService(), ucumService);
    }
}
