package org.cqframework.cql.cql2elm.quick

import java.io.InputStream
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml

/** Created by Bryn on 4/15/2016. */
class QuickFhirModelInfoProvider : ModelInfoProvider {
    private var namespaceManager: NamespaceManager? = null

    fun setNamespaceManager(namespaceManager: NamespaceManager?) {
        this.namespaceManager = namespaceManager
    }

    private fun isQuickFhirModelIdentifier(modelIdentifier: ModelIdentifier): Boolean {
        if (namespaceManager != null && namespaceManager!!.hasNamespaces()) {
            return modelIdentifier.id == "QUICKFHIR" &&
                (modelIdentifier.system == null ||
                    modelIdentifier.system.equals("http://hl7.org/fhir"))
        }

        return modelIdentifier.id == "QUICKFHIR"
    }

    override fun load(modelIdentifier: ModelIdentifier): ModelInfo? {
        if (isQuickFhirModelIdentifier(modelIdentifier)) {
            val localVersion = if (modelIdentifier.version == null) "" else modelIdentifier.version
            val stream = getResource(localVersion!!)
            if (stream != null) {
                return parseModelInfoXml(stream.asSource().buffered())
            }
        }

        return null
    }

    private fun getResource(localVersion: String): InputStream? {
        when (localVersion) {
            "3.0.1",
            "" ->
                return QuickFhirModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/quickfhir-modelinfo-3.0.1.xml")
        }

        return null
    }
}
