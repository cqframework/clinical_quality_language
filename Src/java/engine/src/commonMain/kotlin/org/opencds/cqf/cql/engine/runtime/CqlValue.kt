package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.QName

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
 * and anonymous tuples.
 *
 * @param value The native CQL value to get the type for.
 */
fun getNamedTypeForCqlValue(value: Any?): QName? {
    if (value == null) {
        return anyTypeName
    }

    return when (value) {
        is Boolean -> booleanTypeName
        is Int -> integerTypeName
        is Long -> longTypeName
        is BigDecimal -> decimalTypeName
        is String -> stringTypeName
        is Date -> dateTypeName
        is DateTime -> dateTimeTypeName
        is Time -> timeTypeName
        is Quantity -> quantityTypeName
        is Ratio -> ratioTypeName
        is Code -> codeTypeName
        is Concept -> conceptTypeName
        is CodeSystem -> codeSystemTypeName
        is ValueSet -> valueSetTypeName
        is CqlClassInstance -> value.type
        else -> null
    }
}
