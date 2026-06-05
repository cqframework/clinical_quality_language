@file:OptIn(ExperimentalJsExport::class)
@file:JsOnlyExport

package org.opencds.cqf.cql.engine.runtime

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName

sealed interface NamedTypeValue : Value {
    override val typeAsString: kotlin.String
        get() =
            if (type.getNamespaceURI() == systemModelNamespaceUri) type.getLocalPart()
            else "${type.getPrefix()}.${type.getLocalPart()}"

    val type: QName
}

const val systemModelNamespaceUri = "urn:hl7-org:elm-types:r1"
const val systemModelId = "System"

val anyTypeName = QName(systemModelNamespaceUri, "Any", systemModelId)
val booleanTypeName = QName(systemModelNamespaceUri, "Boolean", systemModelId)
val integerTypeName = QName(systemModelNamespaceUri, "Integer", systemModelId)
val longTypeName = QName(systemModelNamespaceUri, "Long", systemModelId)
val decimalTypeName = QName(systemModelNamespaceUri, "Decimal", systemModelId)
val stringTypeName = QName(systemModelNamespaceUri, "String", systemModelId)
val dateTypeName = QName(systemModelNamespaceUri, "Date", systemModelId)
val dateTimeTypeName = QName(systemModelNamespaceUri, "DateTime", systemModelId)
val timeTypeName = QName(systemModelNamespaceUri, "Time", systemModelId)
val quantityTypeName = QName(systemModelNamespaceUri, "Quantity", systemModelId)
val ratioTypeName = QName(systemModelNamespaceUri, "Ratio", systemModelId)
val codeTypeName = QName(systemModelNamespaceUri, "Code", systemModelId)
val conceptTypeName = QName(systemModelNamespaceUri, "Concept", systemModelId)
val codeSystemTypeName = QName(systemModelNamespaceUri, "CodeSystem", systemModelId)
val valueSetTypeName = QName(systemModelNamespaceUri, "ValueSet", systemModelId)
val vocabularyTypeName = QName(systemModelNamespaceUri, "Vocabulary", systemModelId)

/**
 * Returns the type as a `QName` for instances of named types. Returns null for intervals, lists,
 * and tuples.
 *
 * @param value The native CQL value to get the type for.
 */
fun getNamedTypeForCqlValue(value: Value?): QName? {
    if (value == null) {
        return anyTypeName
    }

    if (value is NamedTypeValue) {
        return value.type
    }

    return null
}
