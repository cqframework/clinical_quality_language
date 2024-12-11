package org.hl7.cql.model

import java.util.*

class InvalidRedeclarationException : IllegalArgumentException {
    constructor() : super()

    constructor(s: String?) : super(s)

    constructor(
        classType: ClassType,
        original: ClassTypeElement,
        redeclared: ClassTypeElement
    ) : super(
        String.format(
            Locale.US,
            "%s.%s cannot be redeclared with type %s because it is not a subtype of the original element type %s",
            classType.name,
            redeclared.name,
            redeclared.type,
            original.type
        )
    )
}
