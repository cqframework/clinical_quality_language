package org.hl7.cql.ast.analysis

import org.hl7.cql.ast.Identifier
import org.hl7.cql.ast.Locator
import kotlin.collections.ArrayDeque

internal class ScopeTracker {

    private val scopes = ArrayDeque<MutableMap<String, Locator>>()

    fun reset() {
        scopes.clear()
    }

    fun push() {
        scopes.addLast(mutableMapOf())
    }

    fun pop() {
        check(scopes.isNotEmpty()) { "Cannot pop scope from empty stack." }
        scopes.removeLast()
    }

    fun declare(identifier: Identifier, locator: Locator): Locator? =
        declare(identifier.value, locator)

    fun declare(name: String, locator: Locator): Locator? {
        val previous = scopes.asReversed().firstNotNullOfOrNull { it[name] }
        check(scopes.isNotEmpty()) { "No active scope to declare identifier '$name'." }
        scopes.last()[name] = locator
        return previous
    }
}
