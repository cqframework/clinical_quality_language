package org.cqframework.cql.elm.utility;

import java.util.Objects;
import java.util.function.BiFunction;

import org.cqframework.cql.elm.tracking.Trackable;
import org.cqframework.cql.elm.visiting.ElmFunctionalVisitor;

public class Visitors {

    private Visitors() {
    }

    public static <C, T> ElmFunctionalVisitor<T, C> from(BiFunction<Trackable, C, T> defaultResult, BiFunction<T, T, T> aggregateResult) {
        Objects.requireNonNull(defaultResult, "defaultResult can not be null");
        Objects.requireNonNull(aggregateResult, "aggregateResult can not be null");
        return new ElmFunctionalVisitor<>(defaultResult, aggregateResult);
    }

    public static <C, T> ElmFunctionalVisitor<T, C> from(BiFunction<Trackable, C, T> defaultResult) {
        Objects.requireNonNull(defaultResult, "defaultResult can not be null");
        return from(defaultResult, (a, b) -> b);
    }
}
