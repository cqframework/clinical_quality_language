package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.NamedTypeSpecifier;

public class GenericResult<T> {
    private final T underlyingThingOrNull;

    public static <T> GenericResult<T> withError() {
        return new GenericResult<>(null);
    }

    public static <T> GenericResult<T> withTypeSpecifier(T underlyingThingOrNull) {
        return new GenericResult<>(underlyingThingOrNull);
    }

    private GenericResult(T namedTypeSpecifierOrNull) {
        this.underlyingThingOrNull = namedTypeSpecifierOrNull;
    }

    public boolean hasError() {
        return (underlyingThingOrNull == null);
    }

    public T getUnderlyingThing() {
        if (hasError()) {
            throw new IllegalArgumentException("Should have called hasError() first");
        }

        return underlyingThingOrNull;
    }
}
