package org.hl7.cql.model

import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.XmlModelInfoReader

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
            val stream =
                this::class.java.getResourceAsStream("/org/hl7/elm/r1/system-modelinfo.xml")
            checkNotNull(stream) { "Could not find system model info" }
            XmlModelInfoReader.read(stream.asSource().buffered())
        } else null
    }
}
