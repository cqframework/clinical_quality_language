package org.opencds.cqf.cql.engine.exception

class UndefinedResult(message: String?) : CqlException(message) {
    companion object {
        private const val serialVersionUID = 1L
    }
}
