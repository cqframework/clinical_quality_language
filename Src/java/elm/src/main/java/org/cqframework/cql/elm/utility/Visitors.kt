package org.cqframework.cql.elm.utility

import java.util.function.BiFunction
import org.cqframework.cql.elm.tracking.Trackable
import org.cqframework.cql.elm.visiting.FunctionalElmVisitor

object Visitors {
    @JvmStatic
    fun <C, T> from(
        defaultResult: BiFunction<Trackable?, C, T>,
        aggregateResult: BiFunction<T, T, T>
    ): FunctionalElmVisitor<T, C> {
        return FunctionalElmVisitor(defaultResult, aggregateResult)
    }

    @JvmStatic
    fun <C, T> from(defaultResult: BiFunction<Trackable?, C, T>): FunctionalElmVisitor<T, C> {
        return from(defaultResult) { _: T, b: T -> b }
    }
}
