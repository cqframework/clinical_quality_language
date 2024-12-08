package org.hl7.cql.model

import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlAttribute
import jakarta.xml.bind.annotation.XmlType

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ModelIdentifier", namespace = "urn:hl7-org:model")
data class ModelIdentifier(
    @XmlAttribute(name = "id") var id: String,
    @XmlAttribute(name = "system") var system: String? = null,
    @XmlAttribute(name = "version") var version: String? = null
) {
    init {
        require(id.isNotEmpty()) { "id is required" }
    }
}
