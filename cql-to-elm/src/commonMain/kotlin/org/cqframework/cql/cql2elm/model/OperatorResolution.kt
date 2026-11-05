package org.cqframework.cql.cql2elm.model

import org.hl7.elm.r1.VersionedIdentifier

data class OperatorResolution(var operator: Operator, val conversions: List<Conversion?>) {
    constructor(
        operator: Operator,
        conversions: Array<Conversion?>?,
    ) : this(operator, conversions?.toList() ?: emptyList())

    var score: Int = 0
    var allowFluent: Boolean = false

    fun hasConversions(): Boolean = conversions.filterNotNull().isNotEmpty()

    /*
    The versioned identifier
    (fully qualified, versioned, library identifier of the library
    in which the resolved operator is defined.)
    This is set by the library resolution to allow the calling context
    to understand the defined location
    of the resolved operator.
     */
    var libraryIdentifier: VersionedIdentifier? = null

    /*
    The local alias for the resolved library. This is set by the libraryBuilder to allow the invocation
    to set the library alias if necessary.
     */
    var libraryName: String? = null
    var operatorHasOverloads: Boolean = false
}
