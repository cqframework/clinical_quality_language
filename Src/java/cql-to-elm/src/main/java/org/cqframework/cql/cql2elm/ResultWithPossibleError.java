package org.cqframework.cql.cql2elm;

/**
 * Indicate either a populated result or the presence of an error that prevented the result from being created.
 */
public class ResultWithPossibleError<T> {
    private final T underlyingThingOrNull;

    public static <T> ResultWithPossibleError<T> withError() {
        return new ResultWithPossibleError<>(null);
    }

    public static <T> ResultWithPossibleError<T> withTypeSpecifier(T underlyingThingOrNull) {
        return new ResultWithPossibleError<>(underlyingThingOrNull);
    }

    private ResultWithPossibleError(T namedTypeSpecifierOrNull) {
        this.underlyingThingOrNull = namedTypeSpecifierOrNull;
    }

    public boolean hasError() {
        return (underlyingThingOrNull == null);
    }

    public T getUnderlyingResultIfExists() {
        if (hasError()) {
            throw new IllegalArgumentException("Should have called hasError() first");
        }

        return underlyingThingOrNull;
    }
}
