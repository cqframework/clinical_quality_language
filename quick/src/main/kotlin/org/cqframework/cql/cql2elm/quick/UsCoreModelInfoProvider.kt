package org.cqframework.cql.cql2elm.quick

import java.io.InputStream
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml

class UsCoreModelInfoProvider : ModelInfoProvider {
    private var namespaceManager: NamespaceManager? = null

    fun setNamespaceManager(namespaceManager: NamespaceManager?) {
        this.namespaceManager = namespaceManager
    }

    private fun isUSCoreModelIdentifier(modelIdentifier: ModelIdentifier): Boolean {
        if (namespaceManager != null && namespaceManager!!.hasNamespaces()) {
            return modelIdentifier.id == "USCore" &&
                (modelIdentifier.system == null ||
                    modelIdentifier.system.equals("http://hl7.org/fhir/us/core"))
        }

        return modelIdentifier.id == "USCore"
    }

    override fun load(modelIdentifier: ModelIdentifier): ModelInfo? {
        if (isUSCoreModelIdentifier(modelIdentifier)) {
            val localVersion = if (modelIdentifier.version == null) "" else modelIdentifier.version
            getResource(localVersion!!)?.use {
                return parseModelInfoXml(it.asSource().buffered())
            }
        }

        return null
    }

    private fun getResource(localVersion: String): InputStream? {
        return when (localVersion) {
            "3.1.0" ->
                QuickModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-3.1.0.xml")
            "3.1.1" ->
                QuickModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-3.1.1.xml")
            "6.1.0",
            "" ->
                QuickModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/uscore-modelinfo-6.1.0.xml")
            else -> null
        }
    }
}
