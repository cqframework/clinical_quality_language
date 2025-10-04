package org.opencds.cqf.cql.engine.exception

class InvalidConversion : CqlException {
    constructor(message: String?) : super(message)

    constructor(
        from: Any,
        to: Any,
    ) : super(
        String.format(
            "Cannot Convert a value of type %s as %s.",
            from.javaClass.getName(),
            to.javaClass.getName(),
        )
    )

    companion object {
        private const val serialVersionUID = 1L
    }
}
