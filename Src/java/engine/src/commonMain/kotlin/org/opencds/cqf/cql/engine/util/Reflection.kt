package org.opencds.cqf.cql.engine.util

/**
 * Used in error messages. Note: Returns `java.lang.Integer` for integer values (including
 * non-nullable integers).
 */
expect val Any.javaClassName: String
