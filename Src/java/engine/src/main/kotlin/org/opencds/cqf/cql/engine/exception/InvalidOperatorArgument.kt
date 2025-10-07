package org.opencds.cqf.cql.engine.exception

class InvalidOperatorArgument : CqlException {
    constructor(message: String?) : super(message)

    constructor(expected: String?, found: String?) : super("Expected ${expected}, Found ${found}")

    companion object {
        private const val serialVersionUID = 1L
    }
}
