package org.cqframework.cql.cql2elm.quick

import java.io.InputStream
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml

class QICoreModelInfoProvider : ModelInfoProvider {
    private var namespaceManager: NamespaceManager? = null

    fun setNamespaceManager(namespaceManager: NamespaceManager?) {
        this.namespaceManager = namespaceManager
    }

    private fun isQICoreModelIdentifier(modelIdentifier: ModelIdentifier): Boolean {
        if (namespaceManager != null && namespaceManager!!.hasNamespaces()) {
            return modelIdentifier.id == "QICore" &&
                (modelIdentifier.system == null ||
                    modelIdentifier.system.equals("http://hl7.org/fhir/us/qicore"))
        }

        return modelIdentifier.id == "QICore"
    }

    override fun load(modelIdentifier: ModelIdentifier): ModelInfo? {
        if (isQICoreModelIdentifier(modelIdentifier)) {
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
            "4.0.0" ->
                return QICoreModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.0.0.xml")
            "4.1.0" ->
                return QICoreModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.1.0.xml")
            "4.1.1" ->
                return QICoreModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-4.1.1.xml")
            "5.0.0" ->
                return QICoreModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-5.0.0.xml")
            "6.0.0",
            "" ->
                return QICoreModelInfoProvider::class
                    .java
                    .getResourceAsStream("/org/hl7/fhir/qicore-modelinfo-6.0.0.xml")
        }
        return null
    }
}
