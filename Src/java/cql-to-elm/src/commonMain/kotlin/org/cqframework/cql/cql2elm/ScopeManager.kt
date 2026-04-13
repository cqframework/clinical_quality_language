package org.cqframework.cql.cql2elm

import org.cqframework.cql.cql2elm.model.QueryContext
import org.cqframework.cql.cql2elm.utils.Stack
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.FunctionDef

/**
 * Owns the stacks that model lexical and semantic scope during CQL-to-ELM translation.
 *
 * Each push/pop pair is also exposed as a lambda-scoped helper (`withXxx { ... }`) so callers can't
 * forget to pop. Prefer the lambda forms over raw push/pop in new code.
 */
@Suppress("TooManyFunctions")
class ScopeManager {
    class Scope {
        val targets: Stack<Expression> = Stack()
        val queries: Stack<QueryContext> = Stack()
    }

    class ExpressionDefinitionContext(val identifier: String) {
        val scope: Scope = Scope()
        var rootCause: Exception? = null
    }

    private class ExpressionDefinitionContextStack : Stack<ExpressionDefinitionContext?>() {
        operator fun contains(identifier: String): Boolean {
            for (i in 0 until size()) {
                if (this.elementAt(i)?.identifier == identifier) return true
            }
            return false
        }
    }

    private val expressionContext = Stack<String>()
    private val expressionDefinitions = ExpressionDefinitionContextStack()
    private val _functionDefs = Stack<FunctionDef>()
    private val _globalIdentifiers = ArrayDeque<IdentifierContext>()
    private val _localIdentifierStack = Stack<ArrayDeque<IdentifierContext>>()
    private var literalContext = 0
    private var typeSpecifierContext = 0

    val functionDefs: Stack<FunctionDef>
        get() = _functionDefs

    val globalIdentifiers: ArrayDeque<IdentifierContext>
        get() = _globalIdentifiers

    val localIdentifierStack: Stack<ArrayDeque<IdentifierContext>>
        get() = _localIdentifierStack

    fun hasScope(): Boolean = !expressionDefinitions.empty()

    val currentScope: Scope
        get() = expressionDefinitions.peek()?.scope!!

    // Query context -----------------------------------------------------------

    fun inQueryContext(): Boolean = hasScope() && currentScope.queries.isNotEmpty()

    fun pushQueryContext(context: QueryContext) {
        currentScope.queries.push(context)
    }

    fun popQueryContext(): QueryContext = currentScope.queries.pop()

    fun peekQueryContext(): QueryContext = currentScope.queries.peek()

    inline fun <R> withQueryContext(context: QueryContext, block: () -> R): R {
        pushQueryContext(context)
        try {
            return block()
        } finally {
            popQueryContext()
        }
    }

    // Expression context ------------------------------------------------------

    fun pushExpressionContext(context: String?) {
        requireNotNull(context) { "Expression context cannot be null" }
        expressionContext.push(context)
    }

    fun popExpressionContext() {
        check(!expressionContext.empty()) { "Expression context stack is empty." }
        expressionContext.pop()
    }

    fun currentExpressionContext(): String {
        check(!expressionContext.empty()) { "Expression context stack is empty." }
        return expressionContext.peek()
    }

    inline fun <R> withExpressionContext(context: String, block: () -> R): R {
        pushExpressionContext(context)
        try {
            return block()
        } finally {
            popExpressionContext()
        }
    }

    // Expression definitions (circular-reference tracking) --------------------

    fun containsExpressionDefinition(identifier: String): Boolean =
        expressionDefinitions.contains(identifier)

    fun pushExpressionDefinition(identifier: String) {
        require(!expressionDefinitions.contains(identifier)) {
            "Cannot resolve reference to expression or function $identifier because it results in a circular reference."
        }
        expressionDefinitions.push(ExpressionDefinitionContext(identifier))
    }

    fun popExpressionDefinition() {
        expressionDefinitions.pop()
    }

    inline fun <R> withExpressionDefinition(identifier: String, block: () -> R): R {
        pushExpressionDefinition(identifier)
        try {
            return block()
        } finally {
            popExpressionDefinition()
        }
    }

    fun determineRootCause(): Exception? {
        if (expressionDefinitions.isNotEmpty()) {
            return expressionDefinitions.peek()?.rootCause
        }
        return null
    }

    fun setRootCause(rootCause: Exception?) {
        if (expressionDefinitions.isNotEmpty()) {
            expressionDefinitions.peek()?.rootCause = rootCause
        }
    }

    // Expression target -------------------------------------------------------

    fun pushExpressionTarget(target: Expression) {
        currentScope.targets.push(target)
    }

    fun popExpressionTarget(): Expression = currentScope.targets.pop()

    fun hasExpressionTarget(): Boolean = hasScope() && currentScope.targets.isNotEmpty()

    inline fun <R> withExpressionTarget(target: Expression, block: () -> R): R {
        pushExpressionTarget(target)
        try {
            return block()
        } finally {
            popExpressionTarget()
        }
    }

    // Function def ------------------------------------------------------------

    fun beginFunctionDef(functionDef: FunctionDef) {
        _functionDefs.push(functionDef)
    }

    fun endFunctionDef() {
        _functionDefs.pop()
    }

    inline fun <R> withFunctionDef(functionDef: FunctionDef, block: () -> R): R {
        beginFunctionDef(functionDef)
        try {
            return block()
        } finally {
            endFunctionDef()
        }
    }

    // Literal context ---------------------------------------------------------

    fun pushLiteralContext() {
        literalContext++
    }

    fun popLiteralContext() {
        check(inLiteralContext()) { "Not in literal context" }
        literalContext--
    }

    fun inLiteralContext(): Boolean = literalContext > 0

    inline fun <R> withLiteralContext(block: () -> R): R {
        pushLiteralContext()
        try {
            return block()
        } finally {
            popLiteralContext()
        }
    }

    // Type specifier context --------------------------------------------------

    fun pushTypeSpecifierContext() {
        typeSpecifierContext++
    }

    fun popTypeSpecifierContext() {
        check(inTypeSpecifierContext()) { "Not in type specifier context" }
        typeSpecifierContext--
    }

    fun inTypeSpecifierContext(): Boolean = typeSpecifierContext > 0

    inline fun <R> withTypeSpecifierContext(block: () -> R): R {
        pushTypeSpecifierContext()
        try {
            return block()
        } finally {
            popTypeSpecifierContext()
        }
    }

    // Identifier scope --------------------------------------------------------

    fun pushIdentifierScope() {
        _localIdentifierStack.push(ArrayDeque())
    }

    fun popIdentifierScope() {
        _localIdentifierStack.pop()
    }

    inline fun <R> withIdentifierScope(block: () -> R): R {
        pushIdentifierScope()
        try {
            return block()
        } finally {
            popIdentifierScope()
        }
    }
}
