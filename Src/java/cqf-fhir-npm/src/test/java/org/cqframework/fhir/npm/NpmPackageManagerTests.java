package org.cqframework.fhir.npm;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.InputStream;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_40_50;
import org.hl7.fhir.convertors.conv40_50.VersionConvertor_40_50;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r5.context.IWorkerContext;
import org.hl7.fhir.r5.model.ImplementationGuide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class NpmPackageManagerTests implements IWorkerContext.ILoggingService {

    private final Logger logger = LoggerFactory.getLogger(NpmPackageManagerTests.class);
    private final VersionConvertor_40_50 convertor = new VersionConvertor_40_50(new BaseAdvisor_40_50());

    @Test
    public void TestSampleIGLocal() {
        Resource igResource = (Resource) FhirContext.forR4Cached().newXmlParser().parseResource(
                NpmPackageManagerTests.class.getResourceAsStream("myig.xml"));
        ImplementationGuide ig = (ImplementationGuide) convertor.convertResource(igResource);
        NpmPackageManager pm = new NpmPackageManager(ig);
        assertEquals(pm.getNpmList().size(), 1);
    }

    @Test
    public void TestSampleContentIGLocal() {
        Resource igResource = (Resource) FhirContext.forR4Cached().newXmlParser().parseResource(
                NpmPackageManagerTests.class.getResourceAsStream("mycontentig.xml"));
        ImplementationGuide ig = (ImplementationGuide) convertor.convertResource(igResource);
        NpmPackageManager pm = new NpmPackageManager(ig);
        assertEquals(pm.getNpmList().size(), 3);
    }

    @Test
    public void TestOpioidMMEIGLocal() {
        Resource igResource = (Resource) FhirContext.forR4Cached().newXmlParser().parseResource(
                NpmPackageManagerTests.class.getResourceAsStream("opioid-mme-r4.xml"));
        ImplementationGuide ig = (ImplementationGuide) convertor.convertResource(igResource);
        NpmPackageManager pm = new NpmPackageManager(ig);
        assertEquals(pm.getNpmList().size(), 2);
    }

    @Test
    public void TestLibrarySourceProviderLocal() {
        Resource igResource = (Resource) FhirContext.forR4Cached().newXmlParser().parseResource(
                NpmPackageManagerTests.class.getResourceAsStream("mycontentig.xml"));
        ImplementationGuide ig = (ImplementationGuide) convertor.convertResource(igResource);
        NpmPackageManager pm = new NpmPackageManager(ig);

        LibraryLoader reader = new LibraryLoader("4.0.1");
        NpmLibrarySourceProvider sp = new NpmLibrarySourceProvider(pm.getNpmList(), reader, this);
        InputStream is = sp.getLibrarySource(new VersionedIdentifier().withSystem("http://somewhere.org/fhir/uv/myig").withId("example"));
        assertNotNull(is);
        is = sp.getLibrarySource(new VersionedIdentifier().withSystem("http://somewhere.org/fhir/uv/myig").withId("example").withVersion("0.2.0"));
        assertNotNull(is);
    }

    @Test
    public void TestModelInfoProviderLocal() {
        Resource igResource = (Resource) FhirContext.forR4Cached().newXmlParser().parseResource(
                NpmPackageManagerTests.class.getResourceAsStream("testig.xml"));
        ImplementationGuide ig = (ImplementationGuide) convertor.convertResource(igResource);
        NpmPackageManager pm = new NpmPackageManager(ig);
        assertTrue(pm.getNpmList().size() >= 1);

        LibraryLoader reader = new LibraryLoader("5.0");
        NpmModelInfoProvider mp = new NpmModelInfoProvider(pm.getNpmList(), reader, this);
        ModelInfo mi = mp.load(new ModelIdentifier().withSystem("http://hl7.org/fhir/us/qicore").withId("QICore"));
        assertNotNull(mi);
        assertEquals(mi.getName(), "QICore");
    }

    @Override
    public void logMessage(String msg) {
        logger.info(msg);
    }

    @Override
    public void logDebugMessage(IWorkerContext.ILoggingService.LogCategory category, String msg) {
        logMessage(msg);
    }

    @Override
    public boolean isDebugLogging() {
        return logger.isDebugEnabled();
    }

}
