package org.opencds.cqf.cql.engine.exception

/*
 * This class is meant to be thrown by implementations of the DataProvider interface
 * whenever they encounter an Exception
 */
open class DataProviderException(message: String?) : CqlException(message) {
    companion object {
        private const val serialVersionUID = 1L
    }
}
