package org.cqframework.cql.elm.serializing

import kotlinx.serialization.Serializable
import org.hl7.elm.r1.Library

@Serializable data class LibraryWrapper(val library: Library)
