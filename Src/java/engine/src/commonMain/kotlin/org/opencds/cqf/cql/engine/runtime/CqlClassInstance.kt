package org.opencds.cqf.cql.engine.runtime

import org.cqframework.cql.shared.QName

/** Represents an instance of a named structured type (class). */
data class CqlClassInstance(val type: QName, val elements: MutableMap<String, Any?>) : CqlType
