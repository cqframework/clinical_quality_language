package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.QName

sealed interface NamedTypeValue : Value {
    override val typeAsString: kotlin.String
        get() = type.toString()

    val type: QName
}

const val systemModelNamespaceUri = "urn:hl7-org:elm-types:r1"

val anyTypeName = QName(systemModelNamespaceUri, "Any")
val booleanTypeName = QName(systemModelNamespaceUri, "Boolean")
val integerTypeName = QName(systemModelNamespaceUri, "Integer")
val longTypeName = QName(systemModelNamespaceUri, "Long")
val decimalTypeName = QName(systemModelNamespaceUri, "Decimal")
val stringTypeName = QName(systemModelNamespaceUri, "String")
val dateTypeName = QName(systemModelNamespaceUri, "Date")
val dateTimeTypeName = QName(systemModelNamespaceUri, "DateTime")
val timeTypeName = QName(systemModelNamespaceUri, "Time")
val quantityTypeName = QName(systemModelNamespaceUri, "Quantity")
val ratioTypeName = QName(systemModelNamespaceUri, "Ratio")
val codeTypeName = QName(systemModelNamespaceUri, "Code")
val conceptTypeName = QName(systemModelNamespaceUri, "Concept")
val codeSystemTypeName = QName(systemModelNamespaceUri, "CodeSystem")
val valueSetTypeName = QName(systemModelNamespaceUri, "ValueSet")
val vocabularyTypeName = QName(systemModelNamespaceUri, "Vocabulary")

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
