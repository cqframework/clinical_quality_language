package org.cqframework.cql.cql2elm.utils

open class Stack<T> {
    private val stack = mutableListOf<T>()

    val indices: IntRange
        get() = stack.indices

    fun push(item: T) {
        stack.add(item)
    }

    fun pop(): T {
        if (stack.isEmpty()) {
            throw IllegalStateException("Cannot pop from an empty stack")
        }
        return stack.removeAt(stack.size - 1)
    }

    fun peek(): T {
        if (stack.isEmpty()) {
            throw IllegalStateException("Cannot peek at an empty stack")
        }
        return stack[stack.size - 1]
    }

    fun isEmpty(): Boolean = stack.isEmpty()

    fun empty(): Boolean = stack.isEmpty()

    fun isNotEmpty(): Boolean = stack.isNotEmpty()

    fun size(): Int = stack.size

    fun elementAt(index: Int): T {
        return stack[index]
    }
}
