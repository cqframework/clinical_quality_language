package org.cqframework.cql.elm.visiting

import kotlin.jvm.JvmStatic
import org.hl7.elm.r1.Element

/**
 * This is a base class for visitors that apply functions to all the visited ELM elements. Useful
 * for quick visitor implementations, such as counting all nodes, or finding a specific element
 * type.
 */
class FunctionalElmVisitor<T, C>(
    private val defaultResultFunc: (Element, C) -> T,
    private val aggregateResultFunc: (T, T) -> T,
) : BaseElmLibraryVisitor<T, C>() {

    public override fun defaultResult(elm: Element, context: C): T {
        return defaultResultFunc(elm, context)
    }

    public override fun aggregateResult(aggregate: T, nextResult: T): T {
        return aggregateResultFunc(aggregate, nextResult)
    }

    companion object {
        @JvmStatic
        fun <C, T> from(
            defaultResult: (Element, C) -> T,
            aggregateResult: (T, T) -> T,
        ): FunctionalElmVisitor<T, C> {
            return FunctionalElmVisitor(defaultResult, aggregateResult)
        }

        @JvmStatic
        fun <C, T> from(defaultResult: (Element, C) -> T): FunctionalElmVisitor<T, C> {
            return from(defaultResult) { _: T, b: T -> b }
        }
    }
}
