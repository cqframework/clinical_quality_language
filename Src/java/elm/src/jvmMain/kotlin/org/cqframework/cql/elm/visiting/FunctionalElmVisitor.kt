package org.cqframework.cql.elm.visiting

import java.util.function.BiFunction
import org.hl7.elm.r1.Element

/**
 * This is a base class for visitors that apply functions to all the visited ELM elements. Useful
 * for quick visitor implementations, such as counting all nodes, or finding a specific element
 * type.
 */
class FunctionalElmVisitor<T, C>(
    private val defaultResult: BiFunction<Element, C, T>,
    private val aggregateResult: BiFunction<T, T, T>
) : BaseElmLibraryVisitor<T, C>() {

    public override fun defaultResult(elm: Element, context: C): T {
        return defaultResult.apply(elm, context)
    }

    public override fun aggregateResult(aggregate: T, nextResult: T): T {
        return aggregateResult.apply(aggregate, nextResult)
    }

    companion object {
        fun <C, T> from(
            defaultResult: BiFunction<Element, C, T>,
            aggregateResult: BiFunction<T, T, T>
        ): FunctionalElmVisitor<T, C> {
            return FunctionalElmVisitor(defaultResult, aggregateResult)
        }

        fun <C, T> from(defaultResult: BiFunction<Element, C, T>): FunctionalElmVisitor<T, C> {
            return from(defaultResult) { _: T, b: T -> b }
        }
    }
}
