package org.cqframework.cql.elm.serializing

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.cqframework.cql.shared.QName
import org.cqframework.cql.shared.serializing.XmlNode
import org.hl7.cql_annotations.r1.Narrative
import org.hl7.cql_annotations.r1.fromJsonObject
import org.hl7.cql_annotations.r1.fromXmlElement
import org.hl7.cql_annotations.r1.toJsonObject
import org.hl7.cql_annotations.r1.toXmlElement

/**
 * Extracts the content of a `Narrative` instance from a mixed-content XML element.
 *
 * @param xmlElement The XML element representing the `Narrative`.
 * @param narrative The `Narrative` instance being built.
 * @param namespaces A map of namespace prefixes to URIs assigned in the current context.
 */
internal fun getNarrativeContentFromXml(
    xmlElement: XmlNode.Element,
    narrative: Narrative,
    namespaces: Map<String, String>
) {
    narrative.content.addAll(
        xmlElement.children.map {
            when (it) {
                is XmlNode.Element -> Narrative.fromXmlElement(it, namespaces)
                is XmlNode.Text -> it.text
            }
        }
    )
}

/**
 * Adds the content of a `Narrative` instance to a mixed-content XML element.
 *
 * @param narrative The `Narrative` instance being serialized.
 * @param children The children of the XML element being created.
 * @param namespaces A map of namespace prefixes to URIs assigned in the current context.
 * @param defaultNamespaces Prefixes to use for well-known namespaces.
 */
internal fun addNarrativeContentToXml(
    narrative: Narrative,
    children: MutableList<XmlNode>,
    namespaces: MutableMap<String, String>,
    defaultNamespaces: Map<String, String>
) {
    children.addAll(
        narrative.content.map {
            when (it) {
                is String -> XmlNode.Text(it)
                is Narrative ->
                    it.toXmlElement(
                        QName("urn:hl7-org:cql-annotations:r1", "s"),
                        false,
                        namespaces,
                        defaultNamespaces
                    )
                else -> error("Bad Narrative content")
            }
        }
    )
}

/**
 * Unwraps the inner value from a JSON object, if it exists and is a JSON object itself.
 *
 * @param jsonObject The JSON object to unwrap.
 * @return The unwrapped inner value or the original JSON object.
 */
private fun getInnerValueIfObject(jsonObject: JsonObject): JsonObject {
    return jsonObject["value"] as? JsonObject ?: jsonObject
}

/**
 * Extracts the content of a `Narrative` instance from a JSON object.
 *
 * @param jsonObject The JSON object representing the `Narrative`.
 * @param narrative The `Narrative` instance being built.
 */
internal fun getNarrativeContentFromJson(jsonObject: JsonObject, narrative: Narrative) {
    jsonObject["value"]?.let {
        (it as? JsonArray)?.let {
            narrative.content.addAll(
                it.map {
                    when (it) {
                        is JsonObject -> Narrative.fromJsonObject(getInnerValueIfObject(it))
                        is JsonPrimitive -> it.content
                        else -> error("Bad Narrative content")
                    }
                }
            )
        } ?: error("Bad Narrative content")
    }
        ?: jsonObject["s"]?.let {
            (it as? JsonArray)?.let {
                narrative.content.addAll(
                    it.map {
                        when (it) {
                            is JsonObject -> Narrative.fromJsonObject(getInnerValueIfObject(it))
                            is JsonPrimitive -> {
                                val innerNarrative = Narrative()
                                innerNarrative.content.add(it.content)
                                innerNarrative
                            }
                            else -> error("Bad Narrative content")
                        }
                    }
                )
            } ?: error("Bad Narrative content")
        }
}

/**
 * Adds the content of a `Narrative` instance to a JSON object being created.
 *
 * @param narrative The `Narrative` instance being serialized.
 * @param entries The entries of the JSON object being created.
 */
internal fun addNarrativeContentToJson(
    narrative: Narrative,
    entries: MutableMap<String, JsonElement>
) {
    if (narrative.content.any { it is String }) {
        entries["value"] =
            JsonArray(
                narrative.content.map {
                    when (it) {
                        is String -> JsonPrimitive(it)
                        is Narrative -> it.toJsonObject(false)
                        else -> error("Bad Narrative content")
                    }
                }
            )
    } else {
        entries["s"] =
            JsonArray(
                narrative.content.map {
                    (it as? Narrative)?.toJsonObject(false) ?: error("Bad Narrative content")
                }
            )
    }
}
