package org.opencds.cqf.cql.engine.exception

class InvalidDateTime : CqlException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    companion object {
        private const val serialVersionUID = 1L
    }
}
