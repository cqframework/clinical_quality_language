package org.cqframework.cql.cql2elm.quick

import java.io.InputStream
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml

class QuickModelInfoProvider : ModelInfoProvider {
    private var namespaceManager: NamespaceManager? = null

    fun setNamespaceManager(namespaceManager: NamespaceManager?) {
        this.namespaceManager = namespaceManager
    }

    private fun isQuickModelIdentifier(modelIdentifier: ModelIdentifier): Boolean {
        if (namespaceManager != null && namespaceManager!!.hasNamespaces()) {
            return modelIdentifier.id == "QUICK" &&
                (modelIdentifier.system == null ||
                    modelIdentifier.system.equals("http://hl7.org/fhir/us/qicore"))
        }

        return modelIdentifier.id == "QUICK"
    }

    override fun load(modelIdentifier: ModelIdentifier): ModelInfo? {
        if (isQuickModelIdentifier(modelIdentifier)) {
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
            "3.3.0" ->
                QuickModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/quick-modelinfo-3.3.0.xml")
            "3.0.0" ->
                QuickModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/quick-modelinfo-3.0.0.xml")
            else ->
                QuickModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/quick-modelinfo.xml")
        }
    }
}
