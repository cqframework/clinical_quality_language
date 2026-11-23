package org.opencds.cqf.cql.engine.util

/** A hash code consistent with reference equality (`===`) that does not inspect [value]. */
expect fun identityHashCode(value: Any): Int
