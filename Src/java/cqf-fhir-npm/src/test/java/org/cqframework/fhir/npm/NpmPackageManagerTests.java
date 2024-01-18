package org.cqframework.fhir.npm;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import ca.uhn.fhir.context.FhirContext;
import java.io.InputStream;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_40_50;
import org.hl7.fhir.convertors.conv40_50.VersionConvertor_40_50;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r5.context.IWorkerContext;
import org.hl7.fhir.r5.model.ImplementationGuide;
import org.hl7.fhir.utilities.npm.NpmPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

public class NpmPackageManagerTests implements IWorkerContext.ILoggingService {

    private final Logger logger = LoggerFactory.getLogger(NpmPackageManagerTests.class);
    private final VersionConvertor_40_50 convertor = new VersionConvertor_40_50(new BaseAdvisor_40_50());

    @Test
    public void testSampleIGLocalNoDependencies() {
        Resource igResource = (Resource) FhirContext.forR4Cached()
                .newXmlParser()
                .parseResource(NpmPackageManagerTests.class.getResourceAsStream("myig.xml"));
        ImplementationGuide ig = (ImplementationGuide) convertor.convertResource(igResource);
        NpmPackageManager pm = new NpmPackageManager(ig);
        assertEquals(pm.getNpmList().size(), 1);
    }

    @Test
    public void testSampleContentIGLocalWithRecursiveDependencies() {
        Resource igResource = (Resource) FhirContext.forR4Cached()
                .newXmlParser()
                .parseResource(NpmPackageManagerTests.class.getResourceAsStream("mycontentig.xml"));
        ImplementationGuide ig = (ImplementationGuide) convertor.convertResource(igResource);
        NpmPackageManager pm = new NpmPackageManager(ig);
        assertTrue(pm.getNpmList().size() >= 3);
        boolean hasFHIR = false;
        boolean hasCommon = false;
        boolean hasCPG = false;
        for (NpmPackage p : pm.getNpmList()) {
            switch (p.canonical()) {
                case "http://hl7.org/fhir":
                    hasFHIR = true;
                    break;
                case "http://fhir.org/guides/cqf/common":
                    hasCommon = true;
                    break;
                case "http://hl7.org/fhir/uv/cpg":
                    hasCPG = true;
                    break;
            }
        }
        assertTrue(hasFHIR);
        assertTrue(hasCommon);
        assertTrue(hasCPG);
    }

    @Test
    public void testOpioidMMEIGLocalWithSingleFileDependency() {
        Resource igResource = (Resource) FhirContext.forR4Cached()
                .newXmlParser()
                .parseResource(NpmPackageManagerTests.class.getResourceAsStream("opioid-mme-r4.xml"));
        ImplementationGuide ig = (ImplementationGuide) convertor.convertResource(igResource);
        NpmPackageManager pm = new NpmPackageManager(ig);
        assertTrue(pm.getNpmList().size() >= 2);
        boolean hasFHIR = false;
        boolean hasCPG = false;
        for (NpmPackage p : pm.getNpmList()) {
            switch (p.canonical()) {
                case "http://hl7.org/fhir":
                    hasFHIR = true;
                    break;
                case "http://hl7.org/fhir/uv/cpg":
                    hasCPG = true;
                    break;
            }
        }
        assertTrue(hasFHIR);
        assertTrue(hasCPG);
    }

    @Test
    @Ignore("This test depends on the example.fhir.uv.myig package, which is not currently published")
    public void testLibrarySourceProviderLocal() {
        Resource igResource = (Resource) FhirContext.forR4Cached()
                .newXmlParser()
                .parseResource(NpmPackageManagerTests.class.getResourceAsStream("mycontentig.xml"));
        ImplementationGuide ig = (ImplementationGuide) convertor.convertResource(igResource);
        NpmPackageManager pm = new NpmPackageManager(ig);

        LibraryLoader reader = new LibraryLoader("4.0.1");
        NpmLibrarySourceProvider sp = new NpmLibrarySourceProvider(pm.getNpmList(), reader, this);
        InputStream is = sp.getLibrarySource(new VersionedIdentifier()
                .withSystem("http://somewhere.org/fhir/uv/myig")
                .withId("example"));
        // assertNotNull(is);
        is = sp.getLibrarySource(new VersionedIdentifier()
                .withSystem("http://somewhere.org/fhir/uv/myig")
                .withId("example")
                .withVersion("0.2.0"));
        assertNotNull(is);
    }

    @Test
    public void testModelInfoProviderLocal() {
        Resource igResource = (Resource) FhirContext.forR4Cached()
                .newXmlParser()
                .parseResource(NpmPackageManagerTests.class.getResourceAsStream("testig.xml"));
        ImplementationGuide ig = (ImplementationGuide) convertor.convertResource(igResource);
        NpmPackageManager pm = new NpmPackageManager(ig);
        assertTrue(pm.getNpmList().size() >= 1);

        LibraryLoader reader = new LibraryLoader("5.0");
        NpmModelInfoProvider mp = new NpmModelInfoProvider(pm.getNpmList(), reader, this);
        ModelInfo mi = mp.load(new ModelIdentifier()
                .withSystem("http://hl7.org/fhir/us/qicore")
                .withId("QICore"));
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
