package org.cqframework.cql.cql2elm.quick

import java.io.InputStream
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.cql.model.NamespaceAware
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml

/** Created by Bryn on 4/15/2016. */
class FhirModelInfoProvider : ModelInfoProvider, NamespaceAware {
    private var namespaceManager: NamespaceManager? = null

    override fun setNamespaceManager(namespaceManager: NamespaceManager) {
        this.namespaceManager = namespaceManager
    }

    private fun isFHIRModelIdentifier(modelIdentifier: ModelIdentifier): Boolean {
        if (namespaceManager != null && namespaceManager!!.hasNamespaces()) {
            return modelIdentifier.id == "FHIR" &&
                (modelIdentifier.system == null ||
                    modelIdentifier.system.equals("http://hl7.org/fhir"))
        }

        return modelIdentifier.id == "FHIR"
    }

    override fun load(modelIdentifier: ModelIdentifier): ModelInfo? {
        if (isFHIRModelIdentifier(modelIdentifier)) {
            val localVersion = if (modelIdentifier.version == null) "" else modelIdentifier.version
            val stream = getResource(localVersion!!)
            if (stream != null) {
                return parseModelInfoXml(stream.asSource().buffered())
            }
        }

        return null
    }

    private fun getResource(localVersion: String): InputStream? {
        return when (localVersion) {
            "1.0.2" ->
                FhirModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.0.2.xml")

            "1.4" ->
                FhirModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.4.xml")

            "1.6" ->
                FhirModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.6.xml")

            "1.8" ->
                FhirModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-1.8.xml")

            "3.0.0",
            "" ->
                FhirModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.0.0.xml")

            "3.0.1" ->
                FhirModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.0.1.xml")

            "3.2.0" ->
                FhirModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-3.2.0.xml")

            "4.0.0" ->
                FhirModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-4.0.0.xml")

            "4.0.1" ->
                FhirModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/fhir-modelinfo-4.0.1.xml")
            else -> null
        }
    }
}
