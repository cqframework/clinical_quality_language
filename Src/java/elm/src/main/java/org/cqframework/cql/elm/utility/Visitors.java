package org.cqframework.cql.elm.utility;

import java.util.Objects;
import java.util.function.BiFunction;
import org.cqframework.cql.elm.tracking.Trackable;
import org.cqframework.cql.elm.visiting.FunctionalElmVisitor;

public class Visitors {

    private Visitors() {}

    public static <C, T> FunctionalElmVisitor<T, C> from(
            BiFunction<Trackable, C, T> defaultResult, BiFunction<T, T, T> aggregateResult) {
        Objects.requireNonNull(defaultResult, "defaultResult required");
        Objects.requireNonNull(aggregateResult, "aggregateResult required");
        return new FunctionalElmVisitor<>(defaultResult, aggregateResult);
    }

    public static <C, T> FunctionalElmVisitor<T, C> from(BiFunction<Trackable, C, T> defaultResult) {
        Objects.requireNonNull(defaultResult, "defaultResult required");
        return from(defaultResult, (a, b) -> b);
    }
}
