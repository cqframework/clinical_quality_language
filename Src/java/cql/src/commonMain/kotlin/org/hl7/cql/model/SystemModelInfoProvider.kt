package org.hl7.cql.model

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml

@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
class SystemModelInfoProvider : ModelInfoProvider {
    private var namespaceManager: NamespaceManager? = null

    fun setNamespaceManager(namespaceManager: NamespaceManager) {
        this.namespaceManager = namespaceManager
    }

    private fun ModelIdentifier.isSystemModelIdentifier(): Boolean {
        if (namespaceManager?.hasNamespaces() == true) {
            return this.id == "System" &&
                (this.system == null || this.system == "urn:hl7-org:elm-types:r1")
        }

        return this.id == "System"
    }

    override fun load(modelIdentifier: ModelIdentifier): ModelInfo? {
        return if (modelIdentifier.isSystemModelIdentifier()) {
            return parseModelInfoXml(getSystemModelInfoXml())
        } else null
    }
}

expect fun getSystemModelInfoXml(): String
