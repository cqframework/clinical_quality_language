package org.cqframework.cql.cql2elm.analysis

import org.hl7.cql.ast.FunctionDefinition
import org.hl7.cql.model.DataType

/**
 * Manages all mutable scope state for [TypeResolver] during type inference. Encapsulates five
 * concerns that were previously scattered across TypeResolver as independent mutable fields:
 * 1. **Query scope stack** — alias/let bindings visible during query expression resolution
 * 2. **Operand scope** — parameter types during function body resolution
 * 3. **Expression circularity guard** — prevents infinite recursion on forward-referenced defs
 * 4. **Function circularity guard** — prevents infinite recursion on recursive functions
 * 5. **Function result cache** — memoizes resolved function return types
 *
 * The `with*` methods use `inline` + try/finally to guarantee scope cleanup, replacing the manual
 * save/restore patterns that were previously duplicated across resolveExpressionDef and
 * resolveFunctionDef.
 */
internal class ScopeTracker {

    // ----- Query scope stack -----

    private val queryScopes = mutableListOf<Map<String, Resolution>>()

    /**
     * Execute [block] with [scope] pushed onto the query scope stack. The scope is always popped
     * after [block] completes, even on exception.
     */
    inline fun <T> withQueryScope(scope: Map<String, Resolution>, block: () -> T): T {
        queryScopes.add(scope)
        try {
            return block()
        } finally {
            queryScopes.removeAt(queryScopes.lastIndex)
        }
    }

    /**
     * Execute [block] with query scopes temporarily cleared. Restores the previous scopes after
     * [block] completes. Used by expression-def resolution: expression definitions are
     * library-level declarations that must not inherit the caller's query scope.
     */
    inline fun <T> withIsolatedScopes(block: () -> T): T {
        val saved = queryScopes.toList()
        queryScopes.clear()
        try {
            return block()
        } finally {
            queryScopes.clear()
            queryScopes.addAll(saved)
        }
    }

    /**
     * Look up a name in the query scope stack, searching from innermost to outermost. Returns the
     * first matching [Resolution], or null if not found.
     */
    fun resolveInQueryScopes(name: String): Resolution? {
        for (i in queryScopes.indices.reversed()) {
            queryScopes[i][name]?.let {
                return it
            }
        }
        return null
    }

    // ----- Operand scope -----

    private var operandScope: Map<String, DataType> = emptyMap()

    /**
     * Execute [block] with [scope] as the active operand scope. Restores the previous scope after
     * [block] completes, even on exception. Used during function body resolution.
     */
    inline fun <T> withOperandScope(scope: Map<String, DataType>, block: () -> T): T {
        val previous = operandScope
        operandScope = scope
        try {
            return block()
        } finally {
            operandScope = previous
        }
    }

    /** Look up a name in the operand scope. Returns the operand's type, or null if not found. */
    fun resolveOperand(name: String): DataType? = operandScope[name]

    // ----- Circularity guards -----

    private val inProgressExpressions = mutableSetOf<String>()
    private val inProgressFunctions = mutableSetOf<FunctionDefinition>()

    /** Returns true if [name] is already being resolved (circular reference). */
    fun isExpressionInProgress(name: String): Boolean = name in inProgressExpressions

    /** Mark [name] as in-progress. Call [releaseExpression] when done. */
    fun guardExpression(name: String) {
        inProgressExpressions.add(name)
    }

    /** Release [name] from the in-progress set. */
    fun releaseExpression(name: String) {
        inProgressExpressions.remove(name)
    }

    /** Returns true if [funcDef] is already being resolved (recursive call). */
    fun isFunctionInProgress(funcDef: FunctionDefinition): Boolean = funcDef in inProgressFunctions

    /** Mark [funcDef] as in-progress. Call [releaseFunction] when done. */
    fun guardFunction(funcDef: FunctionDefinition) {
        inProgressFunctions.add(funcDef)
    }

    /** Release [funcDef] from the in-progress set. */
    fun releaseFunction(funcDef: FunctionDefinition) {
        inProgressFunctions.remove(funcDef)
    }

    // ----- Function result cache -----

    private val functionResultTypes = HashMap<FunctionDefinition, DataType>()

    /** Return the cached result type for [funcDef], or null if not yet resolved. */
    fun getCachedFunctionResult(funcDef: FunctionDefinition): DataType? =
        functionResultTypes[funcDef]

    /** Cache the resolved result type for [funcDef]. */
    fun cacheFunctionResult(funcDef: FunctionDefinition, type: DataType) {
        functionResultTypes[funcDef] = type
    }
}
