package org.opencds.cqf.cql.engine.runtime

interface CqlType {
    fun equivalent(other: Any?): Boolean?

    fun equal(other: Any?): Boolean?
}
