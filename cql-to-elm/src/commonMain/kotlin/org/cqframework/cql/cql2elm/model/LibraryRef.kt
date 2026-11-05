package org.cqframework.cql.cql2elm.model

import org.hl7.elm.r1.Expression

// Note: This class is only used as a place-holder during resolution in a translator (or
// compiler...)
class LibraryRef(localId: String, val libraryName: String?) : Expression() {
    init {
        this.localId = localId
    }
}
