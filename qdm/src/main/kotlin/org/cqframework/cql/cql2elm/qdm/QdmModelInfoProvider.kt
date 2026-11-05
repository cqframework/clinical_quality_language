package org.cqframework.cql.cql2elm.qdm

import java.io.InputStream
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.cql.model.NamespaceAware
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml

/** Created by Bryn on 2/3/2016. */
class QdmModelInfoProvider : ModelInfoProvider, NamespaceAware {
    private var namespaceManager: NamespaceManager? = null

    override fun setNamespaceManager(namespaceManager: NamespaceManager) {
        this.namespaceManager = namespaceManager
    }

    private fun isQDMModelIdentifier(modelIdentifier: ModelIdentifier): Boolean {
        if (namespaceManager != null && namespaceManager!!.hasNamespaces()) {
            return modelIdentifier.id == "QDM" &&
                (modelIdentifier.system == null ||
                    modelIdentifier.system.equals("urn:healthit-gov"))
        }

        return modelIdentifier.id == "QDM"
    }

    override fun load(modelIdentifier: ModelIdentifier): ModelInfo? {
        if (isQDMModelIdentifier(modelIdentifier)) {
            val localVersion = if (modelIdentifier.version == null) "" else modelIdentifier.version
            getQdmResource(localVersion!!)?.use {
                return parseModelInfoXml(it.asSource().buffered())
            }
        }

        return null
    }

    private fun getQdmResource(localVersion: String): InputStream? {
        return when (localVersion) {
            "4.1.2" ->
                QdmModelInfoProvider::class
                    .java
                    .getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo.xml")
            "4.2" ->
                QdmModelInfoProvider::class
                    .java
                    .getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-4.2.xml")
            "4.3" ->
                QdmModelInfoProvider::class
                    .java
                    .getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-4.3.xml")
            "5.0" ->
                QdmModelInfoProvider::class
                    .java
                    .getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.xml")
            "5.0.1" ->
                QdmModelInfoProvider::class
                    .java
                    .getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.1.xml")
            "5.0.2" ->
                QdmModelInfoProvider::class
                    .java
                    .getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.0.2.xml")
            "5.3" ->
                QdmModelInfoProvider::class
                    .java
                    .getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.3.xml")
            "5.4" ->
                QdmModelInfoProvider::class
                    .java
                    .getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.4.xml")
            "5.5" ->
                QdmModelInfoProvider::class
                    .java
                    .getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.5.xml")
            "5.6",
            "" ->
                QdmModelInfoProvider::class
                    .java
                    .getResourceAsStream("/gov/healthit/qdm/qdm-modelinfo-5.6.xml")
            else -> null
        }
    }
}
