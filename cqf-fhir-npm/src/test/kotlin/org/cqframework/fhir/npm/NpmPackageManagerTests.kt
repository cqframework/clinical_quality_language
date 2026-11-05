package org.cqframework.fhir.npm

import ca.uhn.fhir.context.FhirContext
import org.hl7.cql.model.ModelIdentifier
import org.hl7.elm.r1.VersionedIdentifier
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_40_50
import org.hl7.fhir.convertors.conv40_50.VersionConvertor_40_50
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r5.context.ILoggingService
import org.hl7.fhir.r5.model.ImplementationGuide
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NpmPackageManagerTests : ILoggingService {
    private val logger: Logger = LoggerFactory.getLogger(NpmPackageManagerTests::class.java)
    private val convertor = VersionConvertor_40_50(BaseAdvisor_40_50())

    @Test
    fun sampleIGLocalNoDependencies() {
        val igResource =
            FhirContext.forR4Cached()
                .newXmlParser()
                .parseResource(NpmPackageManagerTests::class.java.getResourceAsStream("myig.xml"))
                as Resource
        val ig = convertor.convertResource(igResource) as ImplementationGuide
        val pm = NpmPackageManager(ig)
        Assertions.assertEquals(1, pm.npmList.size)
    }

    @Test
    fun sampleContentIGLocalWithRecursiveDependencies() {
        val igResource =
            FhirContext.forR4Cached()
                .newXmlParser()
                .parseResource(
                    NpmPackageManagerTests::class.java.getResourceAsStream("mycontentig.xml")
                ) as Resource
        val ig = convertor.convertResource(igResource) as ImplementationGuide
        val pm = NpmPackageManager(ig)
        Assertions.assertTrue(pm.npmList.size >= 3)
        var hasFHIR = false
        var hasCommon = false
        var hasCPG = false
        for (p in pm.npmList) {
            when (p.canonical()) {
                "http://hl7.org/fhir" -> hasFHIR = true
                "http://fhir.org/guides/cqf/common" -> hasCommon = true
                "http://hl7.org/fhir/uv/cpg" -> hasCPG = true
            }
        }
        Assertions.assertTrue(hasFHIR)
        Assertions.assertTrue(hasCommon)
        Assertions.assertTrue(hasCPG)
    }

    @Test
    fun opioidMmeIGLocalWithSingleFileDependency() {
        val igResource =
            FhirContext.forR4Cached()
                .newXmlParser()
                .parseResource(
                    NpmPackageManagerTests::class.java.getResourceAsStream("opioid-mme-r4.xml")
                ) as Resource
        val ig = convertor.convertResource(igResource) as ImplementationGuide
        val pm = NpmPackageManager(ig)
        Assertions.assertTrue(pm.npmList.size >= 2)
        var hasFHIR = false
        var hasCPG = false
        for (p in pm.npmList) {
            when (p.canonical()) {
                "http://hl7.org/fhir" -> hasFHIR = true
                "http://hl7.org/fhir/uv/cpg" -> hasCPG = true
            }
        }
        Assertions.assertTrue(hasFHIR)
        Assertions.assertTrue(hasCPG)
    }

    @Test
    @Disabled(
        "This test depends on the example.fhir.uv.myig package, which is not currently published"
    )
    fun librarySourceProviderLocal() {
        val igResource =
            FhirContext.forR4Cached()
                .newXmlParser()
                .parseResource(
                    NpmPackageManagerTests::class.java.getResourceAsStream("mycontentig.xml")
                ) as Resource
        val ig = convertor.convertResource(igResource) as ImplementationGuide
        val pm = NpmPackageManager(ig)

        val reader = LibraryLoader("4.0.1")
        val sp = NpmLibrarySourceProvider(pm.npmList, reader, this)
        val source =
            sp.getLibrarySource(
                VersionedIdentifier()
                    .withSystem("http://somewhere.org/fhir/uv/myig")
                    .withId("example")
                    .withVersion("0.2.0")
            )
        Assertions.assertNotNull(source)
    }

    @Test
    fun modelInfoProviderLocal() {
        val igResource =
            FhirContext.forR4Cached()
                .newXmlParser()
                .parseResource(NpmPackageManagerTests::class.java.getResourceAsStream("testig.xml"))
                as Resource
        val ig = convertor.convertResource(igResource) as ImplementationGuide
        val pm = NpmPackageManager(ig)
        Assertions.assertFalse(pm.npmList.isEmpty())

        val reader = LibraryLoader("5.0")
        val mp = NpmModelInfoProvider(pm.npmList, reader, this)
        val mi = mp.load(ModelIdentifier("QICore", "http://hl7.org/fhir/us/qicore", null))
        Assertions.assertNotNull(mi)
        Assertions.assertEquals("QICore", mi!!.name)
    }

    override fun logMessage(msg: String?) {
        logger.info(msg)
    }

    override fun logDebugMessage(category: ILoggingService.LogCategory?, msg: String?) {
        logMessage(msg)
    }

    @Deprecated("Deprecated in FHIR core")
    override fun isDebugLogging(): Boolean {
        return logger.isDebugEnabled
    }
}
