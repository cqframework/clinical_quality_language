package org.hl7.cql.model;

public class InvalidRedeclarationException extends IllegalArgumentException {
    public InvalidRedeclarationException() {
        super();
    }

    public InvalidRedeclarationException(String s) {
        super(s);
    }

    public InvalidRedeclarationException(ClassType classType, ClassTypeElement original, ClassTypeElement redeclared) {
        super(String.format(
                "%s.%s cannot be redeclared with type %s because it is not a subtype of the original element type %s",
                classType.getName(), redeclared.getName(), redeclared.getType(), original.getType()));
    }
}
