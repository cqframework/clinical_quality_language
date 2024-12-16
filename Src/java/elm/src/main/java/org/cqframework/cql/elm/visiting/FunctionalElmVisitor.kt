package org.cqframework.cql.elm.visiting

import java.util.function.BiFunction

/**
 * This is a base class for visitors that apply functions to all the visited ELM elements. Useful
 * for quick visitor implementations, such as counting all nodes, or finding a specific element
 * type.
 */
class FunctionalElmVisitor<T, C>(
    private val defaultResult: BiFunction<org.hl7.elm.r1.Element, C, T>,
    private val aggregateResult: BiFunction<T, T, T>
) : BaseElmLibraryVisitor<T, C>() {

    public override fun defaultResult(elm: org.hl7.elm.r1.Element, context: C): T {
        return defaultResult.apply(elm, context)
    }

    public override fun aggregateResult(aggregate: T, nextResult: T): T {
        return aggregateResult.apply(aggregate, nextResult)
    }
}
