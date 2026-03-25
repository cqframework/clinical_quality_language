package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.BigDecimal
import org.cqframework.cql.shared.QName

/**
 * Returns the type as a `QName` for instances of named types. Returns null for intervals, lists,
 * and anonymous tuples.
 *
 * @param value The native CQL value to get the type for.
 */
fun getNamedTypeForCqlValue(value: Any?): QName? {
    if (value == null) {
        return QName("urn:hl7-org:elm-types:r1", "Any")
    }

    return when (value) {
        is Boolean -> QName("urn:hl7-org:elm-types:r1", "Boolean")
        is Int -> QName("urn:hl7-org:elm-types:r1", "Integer")
        is Long -> QName("urn:hl7-org:elm-types:r1", "Long")
        is BigDecimal -> QName("urn:hl7-org:elm-types:r1", "Decimal")
        is String -> QName("urn:hl7-org:elm-types:r1", "String")
        is Date -> QName("urn:hl7-org:elm-types:r1", "Date")
        is DateTime -> QName("urn:hl7-org:elm-types:r1", "DateTime")
        is Time -> QName("urn:hl7-org:elm-types:r1", "Time")
        is Quantity -> QName("urn:hl7-org:elm-types:r1", "Quantity")
        is Ratio -> QName("urn:hl7-org:elm-types:r1", "Ratio")
        is Code -> QName("urn:hl7-org:elm-types:r1", "Code")
        is Concept -> QName("urn:hl7-org:elm-types:r1", "Concept")
        is CodeSystem -> QName("urn:hl7-org:elm-types:r1", "CodeSystem")
        is ValueSet -> QName("urn:hl7-org:elm-types:r1", "ValueSet")
        is CqlClassInstance -> value.type
        else -> null
    }
}
