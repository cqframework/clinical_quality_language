package org.opencds.cqf.cql.engine.runtime

data class List(val value: Iterable<Value?>) : Value, Iterable<Value?> by value {
    override val typeAsString = "List"

    companion object {
        val EMPTY_LIST = List(emptyList())
    }
}

fun Iterable<Value?>.toCqlList() = List(this)
