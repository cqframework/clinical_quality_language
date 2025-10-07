package org.opencds.cqf.cql.engine.exception

class InvalidConversion : CqlException {
    constructor(message: String?) : super(message)

    constructor(
        from: Any,
        to: Any,
    ) : super(
        "Cannot Convert a value of type ${from.javaClass.getName()} as ${to.javaClass.getName()}."
    )

    companion object {
        private const val serialVersionUID = 1L
    }
}
