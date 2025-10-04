package org.opencds.cqf.cql.engine.exception

class InvalidTime(message: String?) : CqlException(message) {
    companion object {
        private const val serialVersionUID = 1L
    }
}
