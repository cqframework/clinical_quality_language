package org.opencds.cqf.cql.engine.exception

import org.opencds.cqf.cql.engine.util.javaClassName

class InvalidConversion : CqlException {
    constructor(message: String?) : super(message)

    constructor(
        from: Any,
        to: Any,
    ) : super("Cannot Convert a value of type ${from.javaClassName} as ${to.javaClassName}.")

    companion object {
        private const val serialVersionUID = 1L
    }
}
