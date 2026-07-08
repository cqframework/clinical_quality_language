@file:Suppress("PackageNaming")

package org.hl7.elm_modelinfo.r1.serializing

import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.qdm.QdmModelInfoProvider
import org.cqframework.cql.cql2elm.quick.FhirModelInfoProvider
import org.cqframework.cql.cql2elm.quick.QICoreModelInfoProvider
import org.cqframework.cql.cql2elm.quick.QuickFhirModelInfoProvider
import org.cqframework.cql.cql2elm.quick.QuickModelInfoProvider
import org.hl7.cql.model.SystemModelInfoProvider
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.junit.jupiter.api.Test

internal class ModelInfoLoadingTests {
    private fun read(clazz: Class<*>, resource: String): ModelInfo {
        val stream = clazz.getResourceAsStream(resource)
        return parseModelInfoXml(stream!!.asSource().buffered())
    }

    @Test
    fun system() {
        read(SystemModelInfoProvider::class.java, "/org/hl7/elm/r1/system-modelinfo.xml")
    }

    @Test
    fun uSCore310() {
        read(QuickModelInfoProvider::class.java, "/org/hl7/fhir/uscore-modelinfo-3.1.0.xml")
    }

    @Test
    fun uSCore311() {
        read(QuickModelInfoProvider::class.java, "/org/hl7/fhir/uscore-modelinfo-3.1.1.xml")
    }

    @Test
    fun quickFhir301() {
        read(QuickFhirModelInfoProvider::class.java, "/org/hl7/fhir/quickfhir-modelinfo-3.0.1.xml")
    }

    @Test
    fun quick330() {
        read(QuickModelInfoProvider::class.java, "/org/hl7/fhir/quick-modelinfo-3.3.0.xml")
    }

    @Test
    fun quick300() {
        read(QuickModelInfoProvider::class.java, "/org/hl7/fhir/quick-modelinfo-3.0.0.xml")
    }

    @Test
    fun quick() {
        read(QuickModelInfoProvider::class.java, "/org/hl7/fhir/quick-modelinfo.xml")
    }

    @Test
    fun qICore400() {
        read(QICoreModelInfoProvider::class.java, "/org/hl7/fhir/qicore-modelinfo-4.0.0.xml")
    }

    @Test
    fun qICore410() {
        read(QICoreModelInfoProvider::class.java, "/org/hl7/fhir/qicore-modelinfo-4.1.0.xml")
    }

    @Test
    fun qICore411() {
        read(QICoreModelInfoProvider::class.java, "/org/hl7/fhir/qicore-modelinfo-4.1.1.xml")
    }

    @Test
    fun qdm() {
        read(QdmModelInfoProvider::class.java, "/gov/healthit/qdm/qdm-modelinfo.xml")
    }

    @Test
    fun qdm420() {
        read(QdmModelInfoProvider::class.java, "/gov/healthit/qdm/qdm-modelinfo-4.2.xml")
    }

    @Test
    fun qdm430() {
        read(QdmModelInfoProvider::class.java, "/gov/healthit/qdm/qdm-modelinfo-4.3.xml")
    }

    @Test
    fun qdm500() {
        read(QdmModelInfoProvider::class.java, "/gov/healthit/qdm/qdm-modelinfo-5.0.xml")
    }

    @Test
    fun qdm501() {
        read(QdmModelInfoProvider::class.java, "/gov/healthit/qdm/qdm-modelinfo-5.0.1.xml")
    }

    @Test
    fun qdm502() {
        read(QdmModelInfoProvider::class.java, "/gov/healthit/qdm/qdm-modelinfo-5.0.2.xml")
    }

    @Test
    fun qdm530() {
        read(QdmModelInfoProvider::class.java, "/gov/healthit/qdm/qdm-modelinfo-5.3.xml")
    }

    @Test
    fun qdm540() {
        read(QdmModelInfoProvider::class.java, "/gov/healthit/qdm/qdm-modelinfo-5.4.xml")
    }

    @Test
    fun qdm550() {
        read(QdmModelInfoProvider::class.java, "/gov/healthit/qdm/qdm-modelinfo-5.5.xml")
    }

    @Test
    fun qdm560() {
        read(QdmModelInfoProvider::class.java, "/gov/healthit/qdm/qdm-modelinfo-5.6.xml")
    }

    @Test
    fun fhirModelInfo102() {
        read(FhirModelInfoProvider::class.java, "/org/hl7/fhir/fhir-modelinfo-1.0.2.xml")
    }

    @Test
    fun fhirModelInfo140() {
        read(FhirModelInfoProvider::class.java, "/org/hl7/fhir/fhir-modelinfo-1.4.xml")
    }

    @Test
    fun fhirModelInfo160() {
        read(FhirModelInfoProvider::class.java, "/org/hl7/fhir/fhir-modelinfo-1.6.xml")
    }

    @Test
    fun fhirModelInfo180() {
        read(FhirModelInfoProvider::class.java, "/org/hl7/fhir/fhir-modelinfo-1.8.xml")
    }

    @Test
    fun fhirModelInfo300() {
        read(FhirModelInfoProvider::class.java, "/org/hl7/fhir/fhir-modelinfo-3.0.0.xml")
    }

    @Test
    fun fhirModelInfo301() {
        read(FhirModelInfoProvider::class.java, "/org/hl7/fhir/fhir-modelinfo-3.0.1.xml")
    }

    @Test
    fun fhirModelInfo320() {
        read(FhirModelInfoProvider::class.java, "/org/hl7/fhir/fhir-modelinfo-3.2.0.xml")
    }

    @Test
    fun fhirModelInfo400() {
        read(FhirModelInfoProvider::class.java, "/org/hl7/fhir/fhir-modelinfo-4.0.0.xml")
    }

    @Test
    fun fhirModelInfo401() {
        read(FhirModelInfoProvider::class.java, "/org/hl7/fhir/fhir-modelinfo-4.0.1.xml")
    }
}
