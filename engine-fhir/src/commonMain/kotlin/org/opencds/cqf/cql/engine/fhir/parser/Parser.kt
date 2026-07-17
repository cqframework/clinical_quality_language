package org.opencds.cqf.cql.engine.fhir.parser

import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import org.cqframework.cql.cql2elm.model.Model
import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.QName
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.NamedType
import org.hl7.cql.model.SimpleType
import org.opencds.cqf.cql.engine.fhir.fhirModelId
import org.opencds.cqf.cql.engine.fhir.fhirModelNamespaceUri
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlString
import org.opencds.cqf.cql.engine.util.offsetDateTimeParse
import org.opencds.cqf.cql.engine.util.zonedDateTimeNow

/** Parses a JSON [Source] representing a FHIR resource into a CQL [Value]. */
fun fhirResourceJsonToCqlValue(source: Source, model: Model): ClassInstance {
    val jsonElement = Json.parseToJsonElement(source.readString())
    require(jsonElement is JsonObject) {
        "Expected JSON object for FHIR resource, but got $jsonElement"
    }
    return fhirResourceJsonObjectToCqlClassInstance(jsonElement, model)
}

/** Converts a JSON object representing a FHIR resource into a CQL [ClassInstance]. */
private fun fhirResourceJsonObjectToCqlClassInstance(
    jsonObject: JsonObject,
    model: Model,
): ClassInstance {
    val resourceType =
        (jsonObject["resourceType"] as? JsonPrimitive)?.contentOrNull
            ?: throw IllegalArgumentException("Missing or non-string resourceType")

    val resourceClassType =
        model.resolveTypeName(resourceType) as? ClassType
            ?: throw IllegalArgumentException("Unsupported resource type $resourceType")

    return jsonObjectToCqlClassInstance(jsonObject, resourceClassType, model)
}

/**
 * Converts a JSON object representing a FHIR Resource, non-primitive type (complex datatype), or an
 * objectified primitive type into a CQL [ClassInstance].
 */
private fun jsonObjectToCqlClassInstance(
    jsonObject: JsonObject,
    dataType: ClassType,
    model: Model,
): ClassInstance {
    if (dataType.name == "FHIR.Resource") {
        return fhirResourceJsonObjectToCqlClassInstance(jsonObject, model)
    }

    return ClassInstance(
        QName(fhirModelNamespaceUri, dataType.name.removePrefix(FHIR_PREFIX), fhirModelId),
        dataType
            .getAllElements()
            .mapValues { (elementName, elementType) ->
                extractPropertyFromJsonObjectAsCqlValue(jsonObject, elementName, elementType, model)
            }
            .toMutableMap(),
    )
}

/**
 * Extracts a property of a JSON object representing a FHIR Resource, non-primitive type (complex
 * datatype), or an objectified primitive type as a CQL [Value].
 */
private fun extractPropertyFromJsonObjectAsCqlValue(
    jsonObject: JsonObject,
    propertyKey: kotlin.String,
    dataType: DataType,
    model: Model,
): Value? {
    if (dataType is ChoiceType) {
        // Elements that have a choice of datatype cannot repeat
        for (type in dataType.types) {
            require(type is NamedType) { "Expected named type $type but got $dataType" }
            val fhirType = type.name.removePrefix(FHIR_PREFIX)
            val propertyKeyWithType =
                "$propertyKey${fhirType.replaceFirstChar { it.uppercaseChar() }}"
            if (
                jsonObject.containsKey(propertyKeyWithType) ||
                    (type is ClassType &&
                        isFhirPrimitiveType(type) &&
                        jsonObject.containsKey("_$propertyKeyWithType"))
            ) {
                return extractPropertyFromJsonObjectAsCqlValue(
                    jsonObject,
                    propertyKeyWithType,
                    type,
                    model,
                )
            }
        }
        return null
    }

    if (dataType is SimpleType) {
        val jsonPrimitive = jsonObject[propertyKey] as? JsonPrimitive ?: return null

        return when (dataType.name) {
            "System.Boolean" -> jsonPrimitive.booleanOrNull?.toCqlBoolean()
            "System.Integer" -> jsonPrimitive.intOrNull?.toCqlInteger()
            "System.Decimal" -> jsonPrimitive.contentOrNull?.let { BigDecimal(it) }?.toCqlDecimal()
            "System.String" -> jsonPrimitive.takeIf { it.isString }?.content?.toCqlString()
            "System.Date" -> jsonPrimitive.takeIf { it.isString }?.content?.let { Date(it) }
            "System.DateTime" ->
                jsonPrimitive
                    .takeIf { it.isString }
                    ?.content
                    ?.let {
                        val zoneOffset =
                            try {
                                offsetDateTimeParse(it).getOffset()
                            } catch (_: Exception) {
                                zonedDateTimeNow().getOffset()
                            }

                        DateTime(it, zoneOffset)
                    }
            "System.Time" -> jsonPrimitive.takeIf { it.isString }?.content?.let { Time(it) }
            else -> throw IllegalArgumentException("Unsupported simple type $dataType")
        }
    }

    if (dataType is ListType) {
        val elementType = dataType.elementType
        require(elementType is ClassType) { "Unsupported element type $elementType" }

        if (isFhirPrimitiveType(elementType)) {
            val primitiveValues = jsonObject[propertyKey] as? JsonArray
            val extraPropertiesOfPrimitiveValues = jsonObject["_$propertyKey"] as? JsonArray
            val len = maxOf(primitiveValues?.size ?: 0, extraPropertiesOfPrimitiveValues?.size ?: 0)

            if (len == 0) return null

            return List(len) { i ->
                    primitiveValueAndExtraPropertiesToCqlClassInstance(
                        primitiveValues?.getOrNull(i) ?: JsonNull,
                        extraPropertiesOfPrimitiveValues?.getOrNull(i) ?: JsonNull,
                        elementType,
                        model,
                    )
                }
                .toCqlList()
        }

        return (jsonObject[propertyKey] as? JsonArray)
            ?.map { element ->
                if (element is JsonObject) jsonObjectToCqlClassInstance(element, elementType, model)
                else null
            }
            ?.toCqlList()
    }

    if (dataType is ClassType) {
        if (isFhirPrimitiveType(dataType)) {
            return primitiveValueAndExtraPropertiesToCqlClassInstance(
                jsonObject[propertyKey] ?: JsonNull,
                jsonObject["_$propertyKey"] ?: JsonNull,
                dataType,
                model,
            )
        }

        val value = jsonObject[propertyKey]
        return if (value is JsonObject) jsonObjectToCqlClassInstance(value, dataType, model)
        else null
    }

    throw IllegalArgumentException("Unsupported data type $dataType")
}

/**
 * Converts the JSON elements representing the FHIR primitive value and its extra properties (`id`,
 * `extension`) into a CQL [ClassInstance] of the given FHIR primitive type.
 */
private fun primitiveValueAndExtraPropertiesToCqlClassInstance(
    primitiveValue: JsonElement,
    extraProperties: JsonElement,
    dataType: ClassType,
    model: Model,
): ClassInstance? {
    if (primitiveValue is JsonNull && extraProperties is JsonNull) {
        return null
    }
    val jsonObject =
        JsonObject(
            mapOf("value" to primitiveValue).let {
                when (extraProperties) {
                    is JsonNull -> it
                    is JsonObject -> it + extraProperties
                    else ->
                        throw IllegalArgumentException(
                            "Expected JSON object or null but got $extraProperties"
                        )
                }
            }
        )
    return jsonObjectToCqlClassInstance(jsonObject, dataType, model)
}

private fun isFhirPrimitiveType(dataType: ClassType): kotlin.Boolean {
    val typeName = dataType.name.removePrefix(FHIR_PREFIX)
    val firstChar = typeName.firstOrNull() ?: return false
    if (firstChar.lowercaseChar() == firstChar) {
        return true
    }
    val baseType = dataType.baseType
    if (baseType is ClassType && baseType.name == "FHIR.Element") {
        val element = dataType.elements.singleOrNull() ?: return false
        val elementType = element.type
        if (
            element.name == "value" &&
                elementType is SimpleType &&
                elementType.name == "System.String"
        ) {
            return true
        }
    }

    return false
}

/** Collects all elements of a class type, including inherited elements. */
private fun ClassType.getAllElements(): Map<kotlin.String, DataType> {
    return (if (baseType is ClassType) (baseType as ClassType).getAllElements() else emptyMap()) +
        elements.associate { it.name to it.type }
}

private const val FHIR_PREFIX = "FHIR."
