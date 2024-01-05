package org.cqframework.cql.elm.visiting;

import java.util.Objects;
import java.util.function.BiFunction;
import org.cqframework.cql.elm.tracking.Trackable;

/**
 * The is a base class for visitors that apply functions to all the visited ELM elements.
 * Useful for quick visitor implementations, such as counting all nodes, or finding a specific element
 * type.
 */
public class FunctionalElmVisitor<T, C> extends BaseElmLibraryVisitor<T, C> {

    private final BiFunction<Trackable, C, T> defaultResult;
    private final BiFunction<T, T, T> aggregateResult;

    /**
     * Constructor that takes a default visit function and an aggregate result function.
     * @param defaultResult the function for processing a visited element
     * @param aggregateResult the function for aggregating results
     */
    public FunctionalElmVisitor(BiFunction<Trackable, C, T> defaultResult, BiFunction<T, T, T> aggregateResult) {
        this.defaultResult = Objects.requireNonNull(defaultResult);
        this.aggregateResult = Objects.requireNonNull(aggregateResult);
    }

    @Override
    public T defaultResult(Trackable elm, C context) {
        return this.defaultResult.apply(elm, context);
    }

    @Override
    public T aggregateResult(T aggregate, T nextResult) {
        return this.aggregateResult.apply(aggregate, nextResult);
    }

    public static <C, T> FunctionalElmVisitor<T, C> from(
            BiFunction<Trackable, C, T> defaultResult, BiFunction<T, T, T> aggregateResult) {
        return new FunctionalElmVisitor<>(defaultResult, aggregateResult);
    }
}
