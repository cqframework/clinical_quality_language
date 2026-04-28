package org.opencds.cqf.cql.engine.exception

import org.opencds.cqf.cql.engine.runtime.Value

class InvalidConversion : CqlException {
    constructor(message: String?) : super(message)

    constructor(
        from: Value,
        to: Value,
    ) : super("Cannot Convert a value of type ${from.typeAsString} as ${to.typeAsString}.")

    companion object {
        private const val serialVersionUID = 1L
    }
}
