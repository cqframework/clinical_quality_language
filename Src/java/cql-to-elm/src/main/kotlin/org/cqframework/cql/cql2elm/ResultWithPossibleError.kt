package org.cqframework.cql.cql2elm

/**
 * Indicate either a populated result or the presence of an error that prevented the result from
 * being created.
 */
class ResultWithPossibleError<T>(private val underlyingThingOrNull: T) {

    fun hasError(): Boolean {
        return (underlyingThingOrNull == null)
    }

    val underlyingResultIfExists: T
        get() {
            require(!hasError()) { "Should have called hasError() first" }
            return underlyingThingOrNull
        }

    companion object {
        fun <T> withError(): ResultWithPossibleError<T?> {
            return ResultWithPossibleError(null)
        }

        fun <T> withTypeSpecifier(underlyingThingOrNull: T): ResultWithPossibleError<T> {
            return ResultWithPossibleError(underlyingThingOrNull)
        }
    }
}
