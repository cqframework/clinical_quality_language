package org.cqframework.cql.elm.visiting

import java.util.function.BiFunction
import org.cqframework.cql.elm.tracking.Trackable

/**
 * This is a base class for visitors that apply functions to all the visited ELM elements. Useful
 * for quick visitor implementations, such as counting all nodes, or finding a specific element
 * type.
 */
class FunctionalElmVisitor<T, C>(
    private val defaultResult: BiFunction<Trackable?, C, T>,
    private val aggregateResult: BiFunction<T, T, T>
) : BaseElmLibraryVisitor<T, C>() {

    public override fun defaultResult(elm: Trackable?, context: C): T {
        return defaultResult.apply(elm, context)
    }

    public override fun aggregateResult(aggregate: T, nextResult: T): T {
        return aggregateResult.apply(aggregate, nextResult)
    }
}
