package org.hl7.cql.model
class InvalidRedeclarationException(
    classType: ClassType,
    original: ClassTypeElement,
    redeclared: ClassTypeElement
) :
    IllegalArgumentException(
        """${classType.name}.${redeclared.name} cannot be redeclared
            with type ${redeclared.type} because it is not a subtype
            of the original element type ${original.type}"""
            .trimIndent()
    )
